package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

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
    val scrollState = rememberScrollState()
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val bgColor = if (isDark) Color(0xFF121212) else Color(0xFFF2F2F7)
    val textColor = if (isDark) Color.White else Color.Black
    val subTextColor = if (isDark) Color(0xFF8E8E93) else Color(0xFF8E8E93)

    val student = state.selectedStudentDetail
    val bus = state.selectedStudentBusAssignment
    val homeLoc = state.selectedStudentHomeLocation

    var latStr by remember { mutableStateOf("") }
    var lonStr by remember { mutableStateOf("") }
    var initialLoadDone by remember { mutableStateOf(false) }

    LaunchedEffect(homeLoc) {
        if (homeLoc != null) {
            latStr = homeLoc.latitude.toString()
            lonStr = homeLoc.longitude.toString()
            initialLoadDone = true
        } else if (!initialLoadDone) {
            latStr = ""
            lonStr = ""
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val locationManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
                val providers = locationManager.getProviders(true)
                var bestLocation: android.location.Location? = null
                for (provider in providers) {
                    val l = locationManager.getLastKnownLocation(provider) ?: continue
                    if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                        bestLocation = l
                    }
                }
                if (bestLocation != null) {
                    latStr = bestLocation.latitude.toString()
                    lonStr = bestLocation.longitude.toString()
                    Toast.makeText(context, if (isHindi) "स्थान प्राप्त किया गया!" else "Location detected!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, if (isHindi) "लोकेशन नहीं मिल सकी" else "Unable to find location", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                // Ignore
            }
        } else {
            Toast.makeText(context, if (isHindi) "अनुमति अस्वीकार कर दी गई" else "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun detectLocation() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            try {
                val locationManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
                val providers = locationManager.getProviders(true)
                var bestLocation: android.location.Location? = null
                for (provider in providers) {
                    val l = locationManager.getLastKnownLocation(provider) ?: continue
                    if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                        bestLocation = l
                    }
                }
                if (bestLocation != null) {
                    latStr = bestLocation.latitude.toString()
                    lonStr = bestLocation.longitude.toString()
                    Toast.makeText(context, if (isHindi) "स्थान प्राप्त किया गया!" else "Location detected!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, if (isHindi) "GPS सक्रिय करें और पुनः प्रयास करें" else "Enable GPS and try again", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                // Ignore
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = bgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (student == null) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppColors.EmeraldGreen)
                }
            } else {
                // 1. Profile details card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                    color = cardBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(AppColors.EmeraldGreen.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.User,
                                    contentDescription = null,
                                    tint = AppColors.EmeraldGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = student.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${if (isHindi) "कक्षा: " else "Class: "} ${student.className ?: "N/A"} | ${if (isHindi) "रोल नं: " else "Roll: "} ${student.rollNumber ?: "N/A"}",
                                    fontSize = 12.sp,
                                    color = subTextColor
                                )
                            }
                        }
                    }
                }

                // 2. Bus assignments details
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                    color = cardBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isHindi) "असाइन की गई बस की जानकारी" else "Assigned School Bus",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (bus != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(AppColors.EmeraldGreen.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.Bus,
                                        contentDescription = null,
                                        tint = AppColors.EmeraldGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "${bus.busNumber} - ${bus.busName ?: ""}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = textColor
                                    )
                                    Text(
                                        text = "${if (isHindi) "रूट: " else "Route: "} ${bus.routeName ?: "N/A"} | ${if (isHindi) "स्टॉप: " else "Stop: "} ${bus.pickupStop ?: "N/A"}",
                                        fontSize = 11.sp,
                                        color = subTextColor
                                    )
                                }
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Lucide.Info,
                                    contentDescription = null,
                                    tint = subTextColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "कोई बस असाइन नहीं है।" else "No bus is assigned to this student yet.",
                                    fontSize = 12.sp,
                                    color = subTextColor
                                )
                            }
                        }
                    }
                }

                // 3. Home location coordinates form
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                    color = cardBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isHindi) "घर की लोकेशन सेट करें (Home Coordinates)" else "Home Location Coordinates",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isHindi) {
                                "बस जब इस सेट की गई होम लोकेशन के 1 किलोमीटर के दायरे में आएगी, तब आपको नियरबाय अलर्ट जाएगा।"
                            } else {
                                "Nearby alerts will trigger automatically when the school bus is within 1 km of this location."
                            },
                            fontSize = 11.sp,
                            color = subTextColor,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Latitude input
                        OutlinedTextField(
                            value = latStr,
                            onValueChange = { latStr = it },
                            label = { Text(if (isHindi) "अक्षांश (Latitude)" else "Latitude") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = AppColors.EmeraldGreen,
                                unfocusedBorderColor = borderVal,
                                focusedLabelColor = AppColors.EmeraldGreen,
                                unfocusedLabelColor = subTextColor
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Longitude input
                        OutlinedTextField(
                            value = lonStr,
                            onValueChange = { lonStr = it },
                            label = { Text(if (isHindi) "देशांतर (Longitude)" else "Longitude") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = AppColors.EmeraldGreen,
                                unfocusedBorderColor = borderVal,
                                focusedLabelColor = AppColors.EmeraldGreen,
                                unfocusedLabelColor = subTextColor
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Detect current location button
                        Button(
                            onClick = { detectLocation() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) Color(0xFF2C2C2E) else MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = if (isDark) Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Lucide.MapPin,
                                contentDescription = null,
                                tint = if (isDark) Color.White else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "मेरी वर्तमान लोकेशन भरें" else "Detect My Current Location",
                                color = if (isDark) Color.White else MaterialTheme.colorScheme.onSecondaryContainer,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Save button
                        Button(
                            onClick = {
                                val latVal = latStr.toDoubleOrNull()
                                val lonVal = lonStr.toDoubleOrNull()
                                if (latVal != null && lonVal != null) {
                                    viewModel.onEvent(
                                        InstitutionEvent.SaveStudentHomeCoordinates(
                                            organizationId = student.organizationId,
                                            studentId = student.id,
                                            latitude = latVal,
                                            longitude = lonVal,
                                            userId = userId
                                        )
                                    )
                                    Toast.makeText(context, if (isHindi) "लोकेशन सेव की जा रही है..." else "Saving location...", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, if (isHindi) "कृपया सही अक्षांश और देशांतर भरें" else "Please enter valid coordinates", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                            enabled = !state.isSavingHomeLocation,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (state.isSavingHomeLocation) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                            } else {
                                Icon(
                                    imageVector = Lucide.Save,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "होम लोकेशन सेव करें" else "Save Home Location",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
