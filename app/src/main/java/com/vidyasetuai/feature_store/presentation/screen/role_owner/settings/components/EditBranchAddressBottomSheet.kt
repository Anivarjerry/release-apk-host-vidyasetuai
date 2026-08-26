package com.vidyasetuai.feature_store.presentation.screen.role_owner.settings.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBranchAddressBottomSheet(
    isHindi: Boolean = false,
    initialBranch: BusinessBranchEntity?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (
        branchName: String,
        addressLine: String,
        city: String,
        state: String,
        pincode: String,
        lat: Double?,
        lng: Double?
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var branchName by remember { mutableStateOf(initialBranch?.branchName ?: "Main Counter") }
    var addressLine by remember { mutableStateOf(initialBranch?.addressLine ?: "") }
    var city by remember { mutableStateOf(initialBranch?.city ?: "Jaipur") }
    var state by remember { mutableStateOf(initialBranch?.state ?: "Rajasthan") }
    var pincode by remember { mutableStateOf(initialBranch?.pincode ?: "302001") }
    var lat by remember { mutableStateOf(initialBranch?.lat) }
    var lng by remember { mutableStateOf(initialBranch?.lng) }
    var isDetectingGps by remember { mutableStateOf(false) }

    val locationManager = remember { context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager }

    val onLocationReceived: (Double, Double) -> Unit = { latitude, longitude ->
        lat = latitude
        lng = longitude
        isDetectingGps = false

        // Reverse Geocode via Android Geocoder
        scope.launch(Dispatchers.IO) {
            try {
                if (Geocoder.isPresent()) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val addr = addresses[0]
                        withContext(Dispatchers.Main) {
                            if (addressLine.isBlank()) {
                                addressLine = addr.getAddressLine(0)
                                    ?: listOfNotNull(addr.subThoroughfare, addr.thoroughfare, addr.subLocality).joinToString(", ")
                            }
                            if (city.isBlank() || city == "Jaipur") {
                                addr.locality?.let { city = it }
                            }
                            if (state.isBlank() || state == "Rajasthan") {
                                addr.adminArea?.let { state = it }
                            }
                            if (pincode.isBlank() || pincode == "302001") {
                                addr.postalCode?.let { pincode = it }
                            }
                            Toast.makeText(
                                context,
                                if (isHindi) "📍 GPS स्थान व पता दर्ज हुआ!" else "📍 GPS Location & Address auto-detected!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val startLocationFetch = {
        val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
        val isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

        if (!isGpsEnabled && !isNetworkEnabled) {
            Toast.makeText(
                context,
                if (isHindi) "कृपया डिवाइस का GPS / Location चालू करें" else "Please turn on Device Location / GPS",
                Toast.LENGTH_LONG
            ).show()
            try {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            isDetectingGps = true
            try {
                val lastLoc = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                if (lastLoc != null) {
                    onLocationReceived(lastLoc.latitude, lastLoc.longitude)
                }

                val listener = object : LocationListener {
                    override fun onLocationChanged(loc: Location) {
                        onLocationReceived(loc.latitude, loc.longitude)
                        locationManager?.removeUpdates(this)
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                if (isGpsEnabled) {
                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0L,
                        0f,
                        listener,
                        Looper.getMainLooper()
                    )
                } else if (isNetworkEnabled) {
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0L,
                        0f,
                        listener,
                        Looper.getMainLooper()
                    )
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                isDetectingGps = false
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            startLocationFetch()
        }
    }

    val detectLocation = {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            startLocationFetch()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            // Fixed Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Building2,
                            contentDescription = "Branch",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isHindi) "दुकान व शाखा का पता" else "Store & Branch Address",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "बिल व रसीदों के ऊपर प्रिंट होने वाला पता" else "Printed at the top of customer bills & receipts",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Form Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Branch Name
                OutlinedTextField(
                    value = branchName,
                    onValueChange = { branchName = it },
                    label = { Text(if (isHindi) "काउंटर / शाखा का नाम *" else "Counter / Branch Name *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Full Street Address
                OutlinedTextField(
                    value = addressLine,
                    onValueChange = { addressLine = it },
                    label = { Text(if (isHindi) "पूरा दुकान का पता (Street Address) *" else "Full Street Address *") },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // City, State, Pincode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text(if (isHindi) "शहर" else "City") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text(if (isHindi) "राज्य" else "State") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = pincode,
                        onValueChange = { pincode = it },
                        label = { Text(if (isHindi) "पिनकोड" else "Pincode") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                // GPS Location Detection Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (lat != null) Color(0xFF10B981).copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.MapPin,
                                contentDescription = "GPS",
                                tint = if (lat != null) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = if (lat != null) "GPS: ${String.format("%.4f", lat)}, ${String.format("%.4f", lng)}" else "GPS Location (Optional)",
                                    fontSize = 12.sp,
                                    fontWeight = if (lat != null) FontWeight.Bold else FontWeight.Normal,
                                    color = if (lat != null) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (lat != null) "सटीक लोकेशन पिन है" else "दुकान की लोकेशन डिटेक्ट करें",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = { detectLocation() },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            if (isDetectingGps) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 1.5.dp)
                            } else {
                                Text(
                                    text = if (lat != null) "📍 Re-Detect" else "📍 Auto-Detect GPS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Save Button
                Button(
                    onClick = {
                        onSave(branchName, addressLine, city, state, pincode, lat, lng)
                    },
                    enabled = !isSaving && branchName.isNotBlank() && addressLine.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = if (isHindi) "पता सुरक्षित करें (Save Address)" else "Save Address",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
