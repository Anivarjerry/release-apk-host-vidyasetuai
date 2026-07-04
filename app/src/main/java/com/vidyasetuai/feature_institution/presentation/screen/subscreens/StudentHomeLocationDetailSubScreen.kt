package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.vidyasetuai.core.auth.PermissionManager
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StudentHomeLocationDetailSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    userId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Register back press handler
    BackHandler(onBack = onBack)

    // Check if role is Guardian
    val isGuardian = state.activeWorkspace?.role == "Guardian"
    val isStudent = state.activeWorkspace?.role == "Student"
    
    // Selected student ID tracker (for Guardians)
    var selectedChildId by remember { mutableStateOf("") }
    
    // Form fields input state
    var latitudeInput by remember { mutableStateOf("") }
    var longitudeInput by remember { mutableStateOf("") }
    var isFetchingGps by remember { mutableStateOf(false) }
    
    // Cooldown timer state (5 seconds)
    var cooldownSeconds by remember { mutableStateOf(0) }
    
    // GPS Status dialog alert state
    var showGpsDisabledAlert by remember { mutableStateOf(false) }

    // Automatically load details for first child (if Guardian)
    LaunchedEffect(state.guardianStudents, isGuardian) {
        if (isGuardian && state.guardianStudents.isNotEmpty()) {
            val firstChildId = state.guardianStudents.first().id
            selectedChildId = firstChildId
            viewModel.onEvent(InstitutionEvent.LoadStudentProfileDetails(firstChildId))
        }
    }

    // Update lat/lng inputs when student details or location changes
    LaunchedEffect(state.selectedStudentDetail, state.selectedStudentHomeLocation) {
        state.selectedStudentHomeLocation?.let { loc ->
            latitudeInput = loc.latitude.toString()
            longitudeInput = loc.longitude.toString()
        } ?: run {
            state.selectedStudentDetail?.let { student ->
                if (student.homeLatitude != null && student.homeLongitude != null) {
                    latitudeInput = student.homeLatitude.toString()
                    longitudeInput = student.homeLongitude.toString()
                } else {
                    latitudeInput = ""
                    longitudeInput = ""
                }
            }
        }
    }

    // Toast error / success display helper
    var lastErrorShown by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null && state.errorMessage != lastErrorShown) {
            Toast.makeText(context, state.errorMessage, Toast.LENGTH_LONG).show()
            lastErrorShown = state.errorMessage
        } else if (state.errorMessage == null) {
            lastErrorShown = null
        }
    }

    var wasSaving by remember { mutableStateOf(false) }
    LaunchedEffect(state.isSavingHomeLocation) {
        if (wasSaving && !state.isSavingHomeLocation) {
            if (state.errorMessage == null) {
                Toast.makeText(
                    context,
                    if (isHindi) "घर का स्थान सफलतापूर्वक सहेज लिया गया है!" else "Home location saved successfully!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        wasSaving = state.isSavingHomeLocation
    }

    // Cooldown timer countdown loop
    LaunchedEffect(cooldownSeconds) {
        if (cooldownSeconds > 0) {
            delay(1000L)
            cooldownSeconds -= 1
        }
    }

    // GPS location provider fetching function
    val fetchLocationGps = {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsOn = PermissionManager.isLocationServicesEnabled(context)
        
        if (!isGpsOn) {
            showGpsDisabledAlert = true
        } else {
            isFetchingGps = true
            try {
                // Instantly try last known location as a quick backup
                val lastKnown = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                } else null
                
                if (lastKnown != null) {
                    latitudeInput = lastKnown.latitude.toString()
                    longitudeInput = lastKnown.longitude.toString()
                }

                // Request fresh coordinate updates
                val provider = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    LocationManager.GPS_PROVIDER
                } else {
                    LocationManager.NETWORK_PROVIDER
                }

                locationManager.requestLocationUpdates(
                    provider,
                    0L,
                    0f,
                    object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            latitudeInput = location.latitude.toString()
                            longitudeInput = location.longitude.toString()
                            isFetchingGps = false
                            locationManager.removeUpdates(this)
                        }
                        override fun onStatusChanged(p: String?, s: Int, e: Bundle?) {}
                        override fun onProviderEnabled(p: String) {}
                        override fun onProviderDisabled(p: String) {}
                    },
                    Looper.getMainLooper()
                )
                
                // Fallback timeout to stop loader if GPS takes too long
                scope.launch {
                    delay(8000L)
                    if (isFetchingGps) {
                        isFetchingGps = false
                        Toast.makeText(context, 
                            if (isHindi) "GPS समय सीमा समाप्त! पुराना स्थान उपयोग किया गया।" else "GPS Timeout! Using last known location.", 
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: SecurityException) {
                isFetchingGps = false
                Toast.makeText(context, 
                    if (isHindi) "अनुमति एरर!" else "Location permission missing!", 
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                isFetchingGps = false
                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Permission launcher configuration
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                          permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                fetchLocationGps()
            } else {
                Toast.makeText(context, 
                    if (isHindi) "स्थान सेवा अनुमति की आवश्यकता है!" else "Location permission is required!", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    // Execute location permissions check and fetch GPS
    val startLocationRetrieval = {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        
        if (hasFine || hasCoarse) {
            fetchLocationGps()
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // Edge-to-edge root screen layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) AppColors.NearBlack else Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ── Minimal Action Bar Header ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Lucide.ArrowLeft,
                        contentDescription = "Back",
                        tint = if (isDark) Color.White else Color.Black
                    )
                }
                Text(
                    text = if (isHindi) "घर का स्थान सेट करें" else "Set Home Location",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Spacer for visual breathing room
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // ── Guardians Child Selection list ──
                if (isGuardian && state.guardianStudents.isNotEmpty()) {
                    item {
                        Text(
                            text = if (isHindi) "बच्चे का चयन करें" else "Select Child",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.LightGray else Color.Gray
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.guardianStudents) { child ->
                                val isSelected = child.id == selectedChildId
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            if (isSelected) AppColors.EmeraldGreen.copy(alpha = 0.15f)
                                            else (if (isDark) Color(0xFF222222) else Color(0xFFF5F5F5))
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) AppColors.EmeraldGreen else Color.Transparent,
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                        .clickable {
                                            selectedChildId = child.id
                                            viewModel.onEvent(InstitutionEvent.LoadStudentProfileDetails(child.id))
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = child.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSelected) AppColors.EmeraldGreen else (if (isDark) Color.White else Color.Black)
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Student Details profile Card ──
                item {
                    state.selectedStudentDetail?.let { student ->
                        val locationIsSet = student.homeLatitude != null && student.homeLongitude != null
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF1E1E1E) else Color(0xFFFAFAFA))
                                .border(1.dp, if (isDark) Color(0xFF333333) else Color(0xFFEFEFEF), RoundedCornerShape(16.dp))
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AppColors.EmeraldGreen.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.User,
                                        contentDescription = "Student Profile",
                                        tint = AppColors.EmeraldGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column(modifier = Modifier.padding(start = 12.dp)) {
                                    Text(
                                        text = student.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else Color.Black
                                    )
                                    Text(
                                        text = if (isHindi) "कक्षा: ${student.className ?: "N/A"}" else "Class: ${student.className ?: "N/A"}",
                                        fontSize = 13.sp,
                                        color = if (isDark) Color.LightGray else Color.Gray
                                    )
                                }
                            }
                            
                            Divider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE)
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (locationIsSet) Lucide.MapPin else Lucide.CircleAlert,
                                    contentDescription = "Location Status",
                                    tint = if (locationIsSet) AppColors.EmeraldGreen else Color.LightGray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (locationIsSet) {
                                        if (isHindi) "घर का स्थान सेट है" else "Home location is set"
                                    } else {
                                        if (isHindi) "घर का स्थान अभी सेट नहीं है" else "Home location is not set yet"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (locationIsSet) AppColors.EmeraldGreen else (if (isDark) Color.LightGray else Color.Gray),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }

                // ── Location Fetch and Input Section ──
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isHindi) "स्थान विवरण" else "Location Details",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color.LightGray else Color.Gray
                            )
                            
                            // GPS Fetching Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppColors.EmeraldGreen.copy(alpha = 0.1f))
                                    .clickable(enabled = !isFetchingGps) { startLocationRetrieval() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (isFetchingGps) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = AppColors.EmeraldGreen,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Lucide.Compass,
                                            contentDescription = "GPS",
                                            tint = AppColors.EmeraldGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = if (isFetchingGps) {
                                            if (isHindi) "प्राप्त कर रहे हैं..." else "Fetching..."
                                        } else {
                                            if (isHindi) "जीपीएस स्थान लें" else "Get GPS Location"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.EmeraldGreen
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        // Latitude Input
                        OutlinedTextField(
                            value = latitudeInput,
                            onValueChange = { latitudeInput = it },
                            label = { Text(if (isHindi) "अक्षांश (Latitude)" else "Latitude") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.EmeraldGreen,
                                focusedLabelColor = AppColors.EmeraldGreen,
                                cursorColor = AppColors.EmeraldGreen
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        // Longitude Input
                        OutlinedTextField(
                            value = longitudeInput,
                            onValueChange = { longitudeInput = it },
                            label = { Text(if (isHindi) "देशांतर (Longitude)" else "Longitude") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.EmeraldGreen,
                                focusedLabelColor = AppColors.EmeraldGreen,
                                cursorColor = AppColors.EmeraldGreen
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // ── Save Location Button ──
                item {
                    val lat = latitudeInput.toDoubleOrNull()
                    val lng = longitudeInput.toDoubleOrNull()
                    val canSave = lat != null && lng != null && cooldownSeconds == 0 && !state.isSavingHomeLocation
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        Button(
                            onClick = {
                                if (canSave && state.selectedStudentDetail != null) {
                                    cooldownSeconds = 5
                                    viewModel.onEvent(
                                        InstitutionEvent.SaveStudentHomeCoordinates(
                                            organizationId = state.selectedStudentDetail.organizationId,
                                            studentId = state.selectedStudentDetail.id,
                                            latitude = lat!!,
                                            longitude = lng!!,
                                            userId = userId
                                        )
                                    )
                                }
                            },
                            enabled = canSave,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.EmeraldGreen,
                                disabledContainerColor = if (cooldownSeconds > 0) AppColors.EmeraldGreen.copy(alpha = 0.5f) else Color.LightGray
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (state.isSavingHomeLocation) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                val buttonText = when {
                                    cooldownSeconds > 0 -> {
                                        if (isHindi) "सहेजें (${cooldownSeconds}s)" else "Save Location (${cooldownSeconds}s)"
                                    }
                                    else -> {
                                        if (isHindi) "स्थान सहेजें" else "Save Location"
                                    }
                                }
                                Text(
                                    text = buttonText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // GPS Disabled alert settings warning dialog
    if (showGpsDisabledAlert) {
        AlertDialog(
            onDismissRequest = { showGpsDisabledAlert = false },
            title = {
                Text(
                    text = if (isHindi) "जीपीएस बंद है" else "GPS is Disabled",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isHindi) 
                        "वर्तमान स्थान प्राप्त करने के लिए कृपया जीपीएस/लोकेशन सेवाएं सक्षम करें।"
                    else 
                        "Please enable GPS / Location services in order to obtain the current coordinates."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showGpsDisabledAlert = false
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                ) {
                    Text(
                        text = if (isHindi) "सेटिंग्स खोलें" else "Open Settings",
                        color = AppColors.EmeraldGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showGpsDisabledAlert = false }) {
                    Text(
                        text = if (isHindi) "रद्द करें" else "Cancel",
                        color = Color.Gray
                    )
                }
            }
        )
    }
}
