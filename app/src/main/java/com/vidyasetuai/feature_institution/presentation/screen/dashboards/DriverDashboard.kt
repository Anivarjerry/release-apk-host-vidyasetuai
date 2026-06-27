package com.vidyasetuai.feature_feed.presentation.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.data.local.service.LocationTrackingService
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@Composable
fun DriverDashboard(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    userId: String,
    onNavigateToLeave: () -> Unit,
    onNavigateToSalary: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToDriverStudentAttendance: () -> Unit,
    onNavigateToRemarks: () -> Unit
) {
    val context = LocalContext.current
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    var showPermissionDialog by remember { mutableStateOf(false) }

    fun checkLocation() = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                           ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                           
    fun checkBgLocation() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
    
    fun checkGpsEnabled() = com.vidyasetuai.core.auth.PermissionManager.isLocationServicesEnabled(context)
    
    fun checkBattery() = (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(context.packageName)

    if (showPermissionDialog) {
        DriverPermissionsDialog(
            isHindi = isHindi,
            isDark = isDark,
            onDismiss = { showPermissionDialog = false },
            onCheckPermissions = {
                if (checkLocation() && checkBgLocation() && checkGpsEnabled() && checkBattery()) {
                    showPermissionDialog = false
                    // Automatically trigger starting trip after permissions are granted
                    val details = state.driverBusDetails
                    if (details != null && details.busId.isNotEmpty()) {
                        val intent = Intent(context, LocationTrackingService::class.java).apply {
                            putExtra("BUS_ID", details.busId)
                            putExtra("PARENT_ORG_ID", details.parentOrgId)
                            putExtra("SESSION_ID", details.activeSessionId)
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent)
                        } else {
                            context.startService(intent)
                        }
                    }
                }
            }
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isHindi) "ड्राइवर डैशबोर्ड" else "Driver Dashboard",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.EmeraldGreen
        )

        // 1. Assigned Bus Details Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
            color = cardBg
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isHindi) "संबद्ध वाहन विवरण" else "Assigned Vehicle",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val busNum = state.driverBusDetails?.busNumber
                val routeName = state.driverBusDetails?.routeName
                
                if (busNum.isNullOrEmpty()) {
                    Text(
                        text = if (isHindi) "कोई वाहन संबद्ध नहीं है" else "No Vehicle Assigned",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = if (isHindi) "बस नंबर: $busNum" else "Bus Number: $busNum",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!routeName.isNullOrEmpty()) {
                        Text(
                            text = if (isHindi) "रूट नाम: $routeName" else "Route Name: $routeName",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Live trip Switch Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
            color = cardBg
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) "बस ट्रिप शुरू करें" else "Start Bus Trip",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (state.isTripActive) {
                            if (isHindi) "लाइव ट्रैकिंग सक्रिय है (जीपीएस)" else "Live tracking active (GPS)"
                        } else {
                            if (isHindi) "लोकेशन ट्रैकिंग बंद है" else "Location tracking offline"
                        },
                        fontSize = 11.sp,
                        color = if (state.isTripActive) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = state.isTripActive,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            // Verify all permissions first
                            val hasLoc = checkLocation()
                            val hasBgLoc = checkBgLocation()
                            val hasGps = checkGpsEnabled()
                            val hasBatt = checkBattery()
                            
                            if (hasLoc && hasBgLoc && hasGps && hasBatt) {
                                val details = state.driverBusDetails
                                if (details != null && details.busId.isNotEmpty()) {
                                    val intent = Intent(context, LocationTrackingService::class.java).apply {
                                        putExtra("BUS_ID", details.busId)
                                        putExtra("PARENT_ORG_ID", details.parentOrgId)
                                        putExtra("SESSION_ID", details.activeSessionId)
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        context.startForegroundService(intent)
                                    } else {
                                        context.startService(intent)
                                    }
                                } else {
                                    Toast.makeText(context, if (isHindi) "कोई वाहन संबद्ध नहीं है!" else "No vehicle assigned!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                showPermissionDialog = true
                            }
                        } else {
                            // Stop service
                            val intent = Intent(context, LocationTrackingService::class.java).apply {
                                action = LocationTrackingService.ACTION_STOP_TRIP
                            }
                            context.startService(intent)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppColors.EmeraldGreen,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
                    )
                )
            }
        }

        // 3. Running Notification Widget Mockup
        if (state.isTripActive) {
            val durationMin = state.tripDurationSeconds / 60
            val durationSec = state.tripDurationSeconds % 60
            val timeString = String.format(java.util.Locale.US, "%02d:%02d", durationMin, durationSec)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF0F2C24) else Color(0xFFECFDF5),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, AppColors.EmeraldGreen.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Lucide.Bus,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isHindi) "ट्रिप एक्टिव है" else "Trip is Active",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = if (isHindi) "अवधि: $timeString" else "Duration: $timeString",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val intent = Intent(context, LocationTrackingService::class.java).apply {
                                action = LocationTrackingService.ACTION_STOP_TRIP
                            }
                            context.startService(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(text = if (isHindi) "रोकें" else "Stop", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 4. Student Attendance Card (Navigate to DriverStudentAttendanceScreen)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToDriverStudentAttendance() },
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
            color = cardBg
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(AppColors.EmeraldGreen.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.ScanLine,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "विद्यार्थी बस उपस्थिति" else "Student Bus Attendance",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isHindi) "पिकअप/ड्रॉप के लिए क्यूआर कोड स्कैन करें" else "Scan student QR codes for boarding/drop logs",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Lucide.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 5. Remarks Card
        RemarksDashboardCard(
            state = state,
            isHindi = isHindi,
            isDark = isDark,
            borderVal = borderVal,
            cardBg = cardBg,
            onClick = onNavigateToRemarks
        )

        // 6. Salary Card
        StaffSalaryCard(
            monthlySalary = state.monthlySalary,
            totalPaid = state.totalSalaryPaid,
            isHindi = isHindi,
            isDark = isDark,
            borderVal = borderVal,
            cardBg = cardBg,
            onClick = onNavigateToSalary
        )

        // 7. Apply for Leave Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToLeave() },
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
            color = cardBg
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(AppColors.EmeraldGreen.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Calendar,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "अवकाश के लिए आवेदन करें" else "Apply for Leave",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isHindi) "नई छुट्टी के लिए अनुरोध भेजें और इतिहास देखें" else "Submit new requests and view history",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Lucide.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 8. Notice & Gallery Card
        NoticeGalleryFeedCard(
            isHindi = isHindi,
            borderVal = borderVal,
            cardBg = cardBg,
            onClick = onNavigateToFeed
        )
    }
}

@Composable
fun DriverPermissionsDialog(
    isHindi: Boolean,
    isDark: Boolean,
    onDismiss: () -> Unit,
    onCheckPermissions: () -> Unit
) {
    val context = LocalContext.current
    
    var hasLocation by remember { mutableStateOf(false) }
    var hasBgLocation by remember { mutableStateOf(false) }
    var isGpsEnabled by remember { mutableStateOf(false) }
    var isBatteryIgnored by remember { mutableStateOf(false) }

    fun refreshStates() {
        hasLocation = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                      ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        
        hasBgLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        
        isGpsEnabled = com.vidyasetuai.core.auth.PermissionManager.isLocationServicesEnabled(context)
        isBatteryIgnored = (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(context.packageName)
    }

    LaunchedEffect(Unit) {
        refreshStates()
    }

    // Refresh states when returning back from system settings
    androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle.addObserver(remember {
        androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                refreshStates()
                onCheckPermissions()
            }
        }
    })

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        refreshStates()
        onCheckPermissions()
    }

    val bgLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        refreshStates()
        onCheckPermissions()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isDark) Color(0xFF1C1C1E) else Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isHindi) "आवश्यक अनुमतियां" else "Permissions Required",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = if (isHindi) {
                        "स्थान ट्रैकिंग सेवा को ठीक से काम करने के लिए कृपया निम्नलिखित अनुमतियां प्रदान करें।"
                    } else {
                        "To ensure location tracking works reliably, please grant the following settings."
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Divider(color = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA))

                PermissionCheckRow(
                    title = if (isHindi) "लोकेशन अनुमति (Location)" else "Location Permission",
                    desc = if (isHindi) "सटीक बस स्थान साझा करने के लिए।" else "To capture precise coordinate updates.",
                    isGranted = hasLocation,
                    isDark = isDark
                )

                PermissionCheckRow(
                    title = if (isHindi) "हमेशा चालू रखें (Background Location)" else "Background Location",
                    desc = if (isHindi) "स्क्रीन बंद होने या ऐप बैकग्राउंड में होने पर भी लोकेशन ट्रैक करने के लिए।" else "To keep transmitting even when screen is locked or app is closed.",
                    isGranted = hasBgLocation,
                    isDark = isDark
                )

                PermissionCheckRow(
                    title = if (isHindi) "जीपीएस सक्षम करें (GPS)" else "GPS Services",
                    desc = if (isHindi) "डिवाइस स्थान सेवाओं को सक्रिय करें।" else "To receive accurate hardware coordinates.",
                    isGranted = isGpsEnabled,
                    isDark = isDark
                )

                PermissionCheckRow(
                    title = if (isHindi) "बैटरी ऑप्टिमाइज़ेशन अक्षम करें (Battery Optimization)" else "Battery Optimization",
                    desc = if (isHindi) "ट्रैकिंग को बीच में बंद होने से रोकने के लिए।" else "Ignoring battery optimization is required to allow continuous background updates.",
                    isGranted = isBatteryIgnored,
                    isDark = isDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = if (isHindi) "रद्द करें" else "Cancel",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    if (!hasLocation) {
                        Button(
                            onClick = {
                                val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                launcher.launch(permissions)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isHindi) "अनुमति दें" else "Grant",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    } else if (!hasBgLocation) {
                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    bgLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isHindi) "अनुमति दें" else "Grant",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    } else if (!isGpsEnabled) {
                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isHindi) "जीपीएस चालू करें" else "Enable GPS",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    } else if (!isBatteryIgnored) {
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                        data = Uri.parse("package:${context.packageName}")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Log.e("DriverPermissions", "Failed to request battery optimization ignore", e)
                                    val intent = Intent(Settings.ACTION_SETTINGS)
                                    context.startActivity(intent)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isHindi) "सेटिंग्स खोलें" else "On settings",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionCheckRow(
    title: String,
    desc: String,
    isGranted: Boolean,
    isDark: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = if (isGranted) Lucide.Check else Lucide.X,
            contentDescription = null,
            tint = if (isGranted) AppColors.EmeraldGreen else Color(0xFFEF4444),
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
