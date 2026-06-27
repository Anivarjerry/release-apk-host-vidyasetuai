package com.vidyasetuai.feature_institution.data.local.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.vidyasetuai.MainActivity
import com.vidyasetuai.core.database.AppDatabase
import com.vidyasetuai.feature_institution.data.repository.InstitutionRepositoryImpl
import com.vidyasetuai.feature_institution.domain.repository.InstitutionRepository
import com.vidyasetuai.core.auth.PermissionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LocationTrackingService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var locationManager: LocationManager
    private lateinit var repository: InstitutionRepository

    private var busId: String? = null
    private var parentOrgId: String? = null
    private var sessionId: String? = null

    private var currentBestLocation: Location? = null
    private var updateJob: Job? = null
    private var hasUploadedAtLeastOnce = false

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentBestLocation = location
            if (!hasUploadedAtLeastOnce) {
                uploadLocation(location)
            }
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val db = AppDatabase.getDatabase(applicationContext)
        repository = InstitutionRepositoryImpl(db.institutionDao())
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_TRIP) {
            stopTracking()
            return START_NOT_STICKY
        }

        busId = intent?.getStringExtra("BUS_ID")
        parentOrgId = intent?.getStringExtra("PARENT_ORG_ID")
        sessionId = intent?.getStringExtra("SESSION_ID")

        if (busId.isNullOrEmpty() || parentOrgId.isNullOrEmpty()) {
            stopSelf()
            return START_NOT_STICKY
        }

        startTracking()
        // Returning START_STICKY ensures the system will recreate the service if it's killed.
        return START_STICKY
    }

    private fun getLastKnownLocation(): Location? {
        try {
            val gpsLoc = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            } else null
            val netLoc = if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            } else null
            
            if (gpsLoc != null && netLoc != null) {
                return if (gpsLoc.time > netLoc.time) gpsLoc else netLoc
            }
            return gpsLoc ?: netLoc
        } catch (e: SecurityException) {
            Log.e("LocationTrackingService", "Permission missing for last known location", e)
        } catch (e: Exception) {
            Log.e("LocationTrackingService", "Error getting last known location", e)
        }
        return null
    }

    private fun startTracking() {
        isTracking.value = true
        if (trackingStartTime.value == null) {
            trackingStartTime.value = System.currentTimeMillis()
        }

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Try to obtain the last known location and upload it immediately
        currentBestLocation = getLastKnownLocation()
        currentBestLocation?.let {
            uploadLocation(it)
        }

        // Start periodic coroutine to upload location every 20 seconds
        updateJob?.cancel()
        updateJob = serviceScope.launch {
            while (true) {
                // 1. Audit location requirements (Fine Location, Background Location, GPS Enabled, Battery Optimization)
                val hasLoc = PermissionManager.isFineLocationPermissionGranted(applicationContext)
                val hasBgLoc = PermissionManager.isBackgroundLocationPermissionGranted(applicationContext)
                val hasGps = PermissionManager.isLocationServicesEnabled(applicationContext)
                val hasBattery = PermissionManager.isBatteryOptimizationExemptionGranted(applicationContext)

                if (!hasLoc || !hasBgLoc || !hasGps || !hasBattery) {
                    Log.e("LocationTrackingService", "Mandatory tracking permission lost. Stopping trip. hasLoc=$hasLoc, hasBgLoc=$hasBgLoc, hasGps=$hasGps, hasBattery=$hasBattery")
                    showPermissionLostNotification()
                    stopTracking()
                    break
                }

                delay(20000L)
                currentBestLocation?.let {
                    uploadLocation(it)
                }
            }
        }

        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000L, // Request GPS location every 5 seconds
                    5f,    // Or if moved by 5 meters
                    locationListener
                )
            } else {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000L,
                    5f,
                    locationListener
                )
            }
        } catch (e: SecurityException) {
            Log.e("LocationTrackingService", "Permissions missing for requesting location updates", e)
            stopTracking()
        }
    }

    private fun uploadLocation(location: Location) {
        val bus = busId ?: return
        val parentOrg = parentOrgId ?: return
        
        serviceScope.launch {
            val speedKmh = location.speed * 3.6
            Log.d("LocationTrackingService", "Uploading location: lat=${location.latitude}, lng=${location.longitude}, speed=$speedKmh")
            repository.updateBusLiveLocation(
                busId = bus,
                parentOrgId = parentOrg,
                latitude = location.latitude,
                longitude = location.longitude,
                speed = speedKmh,
                sessionId = sessionId
            ).onSuccess {
                hasUploadedAtLeastOnce = true
                Log.d("LocationTrackingService", "Successfully updated location in Supabase")
            }.onFailure { e ->
                Log.e("LocationTrackingService", "Failed to upload location to Supabase", e)
            }
        }
    }

    private fun stopTracking() {
        try {
            locationManager.removeUpdates(locationListener)
        } catch (e: Exception) {
            Log.e("LocationTrackingService", "Error removing location updates", e)
        }
        updateJob?.cancel()
        updateJob = null
        hasUploadedAtLeastOnce = false
        currentBestLocation = null
        isTracking.value = false
        trackingStartTime.value = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            locationManager.removeUpdates(locationListener)
        } catch (e: Exception) {
            Log.e("LocationTrackingService", "Error removing location updates on destroy", e)
        }
        updateJob?.cancel()
        updateJob = null
        isTracking.value = false
        trackingStartTime.value = null
        serviceJob.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Bus Trip Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows active bus trip location tracking status"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, LocationTrackingService::class.java).apply {
            action = ACTION_STOP_TRIP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("NAVIGATE_TO", "driver_trip")
        }
        val mainPendingIntent = PendingIntent.getActivity(
            this,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Determine language settings for notification strings
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val isHindi = prefs.getString("language", "en") == "hi"

        val titleText = if (isHindi) "बस ट्रिप सक्रिय है" else "Bus Trip is Active"
        val descText = if (isHindi) "विद्यार्थियों और अभिभावकों के साथ लाइव लोकेशन साझा की जा रही है..." else "Live location is being shared with students & parents..."
        val stopText = if (isHindi) "ट्रिप समाप्त करें" else "Stop Trip"

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(com.vidyasetuai.R.drawable.ic_launcher_foreground)
            .setContentTitle(titleText)
            .setContentText(descText)
            .setOngoing(true)
            .setContentIntent(mainPendingIntent)
            .setUsesChronometer(true)
            .setShowWhen(true)
            .setWhen(trackingStartTime.value ?: System.currentTimeMillis())
            .addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_menu_close_clear_cancel,
                    stopText,
                    stopPendingIntent
                ).build()
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(Notification.CATEGORY_SERVICE)

        return builder.build()
    }

    private fun showPermissionLostNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val mainPendingIntent = PendingIntent.getActivity(
            this,
            9,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val isHindi = prefs.getString("language", "en") == "hi"
        
        val titleText = if (isHindi) "चेतावनी: ट्रिप समाप्त हो गई है" else "Alert: Trip Ended"
        val descText = if (isHindi) 
            "लोकेशन या जीपीएस परमिशन बंद होने के कारण ट्रिप समाप्त कर दी गई।" 
            else "Trip ended because location or battery permission was disabled."

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(com.vidyasetuai.R.drawable.ic_launcher_foreground)
            .setContentTitle(titleText)
            .setContentText(descText)
            .setAutoCancel(true)
            .setContentIntent(mainPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_ERROR)
            .build()
            
        manager.notify(5432, notification)
    }

    companion object {
        const val CHANNEL_ID = "bus_trip_tracking_channel"
        const val NOTIFICATION_ID = 4521
        const val ACTION_STOP_TRIP = "ACTION_STOP_TRIP"

        val isTracking = MutableStateFlow(false)
        val trackingStartTime = MutableStateFlow<Long?>(null)
    }
}
