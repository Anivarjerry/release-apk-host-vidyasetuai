package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverStudentAttendanceScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onNavigateToHistory: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            Toast.makeText(context, if (isHindi) "कैमरा अनुमति आवश्यक है!" else "Camera permission required!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(onClick = onNavigateToHistory) {
                Icon(
                    imageVector = Lucide.History,
                    contentDescription = "History",
                    tint = AppColors.EmeraldGreen
                )
            }
        }

        if (state.activeBusTrip == null) {
            // Configuration Setup Screen: Select trip type & start session
            TripSetupView(
                state = state,
                isHindi = isHindi,
                isDark = isDark,
                viewModel = viewModel,
                borderVal = borderVal,
                cardBg = cardBg
            )
        } else {
            // Active Redesigned Boarding & Check Screen
            ActiveScanningView(
                state = state,
                isHindi = isHindi,
                isDark = isDark,
                viewModel = viewModel,
                borderVal = borderVal,
                cardBg = cardBg,
                hasCameraPermission = hasCameraPermission,
                onRequestCameraPermission = { launcher.launch(Manifest.permission.CAMERA) }
            )
        }
    }
}

@Composable
fun TripSetupView(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    borderVal: Color,
    cardBg: Color
) {
    val details = state.driverBusDetails
    val busNo = details?.busNumber ?: "N/A"
    val route = details?.routeName ?: (if (isHindi) "अनिर्धारित मार्ग" else "Unassigned Route")
    
    val tripTypes = listOf(
        Triple(
            "Morning_Pickup", 
            if (isHindi) "सुबह पिकअप" else "Morning Pickup", 
            Lucide.Sun
        ),
        Triple(
            "Evening_Drop", 
            if (isHindi) "शाम ड्रॉप" else "Evening Drop", 
            Lucide.Sunset
        ),
        Triple(
            "Special", 
            if (isHindi) "विशेष यात्रा" else "Special Trip", 
            Lucide.Bus
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Upper Graphic
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(AppColors.EmeraldGreen.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.Bus,
                contentDescription = null,
                tint = AppColors.EmeraldGreen,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (isHindi) "उपस्थिति सत्र शुरू करें" else "Start Attendance Session",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = if (isHindi) {
                "बस नं: $busNo | मार्ग: $route"
            } else {
                "Bus No: $busNo | Route: $route"
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )
        
        Text(
            text = if (isHindi) "उपस्थिति शुरू करने के लिए एक यात्रा प्रकार चुनें:" else "Choose a trip type run to begin capture:",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Trip run quick cards
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            tripTypes.forEach { trip ->
                val (type, name, icon) = trip
                
                Surface(
                    onClick = {
                        val parentOrgId = details?.parentOrgId ?: "fa000000-0000-0000-0000-000000000001"
                        val sessId = state.activeSessionId.ifEmpty { "fa000000-0000-0000-0000-000000000001" }
                        val busId = details?.busId ?: "fa000000-0000-0000-0000-000000000001"
                        val staffId = details?.staffId ?: "fa000000-0000-0000-0000-000000000001"
                        viewModel.onEvent(
                            InstitutionEvent.StartBusTrip(
                                parentOrgId = parentOrgId,
                                sessionId = sessId,
                                busId = busId,
                                driverId = staffId,
                                tripType = type
                            )
                        )
                    },
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(AppColors.EmeraldGreen.copy(alpha = 0.08f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = AppColors.EmeraldGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        
                        Icon(
                            imageVector = Lucide.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveScanningView(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    borderVal: Color,
    cardBg: Color,
    hasCameraPermission: Boolean,
    onRequestCameraPermission: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var lastScanTime by remember { mutableStateOf(0L) }
    
    // Status Bar Info
    val tripTypeHi = when (state.activeBusTrip?.tripType) {
        "Morning_Pickup" -> "सुबह पिकअप"
        "Evening_Drop" -> "शाम ड्रॉप"
        else -> "विशेष यात्रा"
    }
    val tripTypeDisplay = if (isHindi) tripTypeHi else state.activeBusTrip?.tripType?.replace("_", " ") ?: ""
    val staffId = state.driverBusDetails?.staffId ?: "fa000000-0000-0000-0000-000000000001"

    // Filter assigned students list by search query
    val filteredStudents = remember(state.assignedStudents, state.searchQuery) {
        if (state.searchQuery.isBlank()) {
            state.assignedStudents
        } else {
            state.assignedStudents.filter { student ->
                student.name.contains(state.searchQuery, ignoreCase = true) ||
                (student.className ?: "").contains(state.searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Summary Active Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isDark) Color(0xFF13241F) else Color(0xFFECFDF5),
            border = androidx.compose.foundation.BorderStroke(0.5.dp, AppColors.EmeraldGreen.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isHindi) "सक्रिय यात्रा: $tripTypeDisplay" else "Active Trip: $tripTypeDisplay",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isHindi) {
                            "बोर्डेड छात्र: ${state.busTripAttendanceLogs.size} / ${state.assignedStudents.size}"
                        } else {
                            "Boarded Students: ${state.busTripAttendanceLogs.size} / ${state.assignedStudents.size}"
                        },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Button(
                    onClick = {
                        state.activeBusTrip?.id?.let {
                            viewModel.onEvent(InstitutionEvent.EndBusTrip(it))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(text = if (isHindi) "यात्रा समाप्त" else "End Trip", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar with Camera Scan Toggle Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onEvent(InstitutionEvent.UpdateSearchQuery(it)) },
                placeholder = {
                    Text(
                        text = if (isHindi) "छात्र का नाम / कक्षा खोजें..." else "Search name or class...",
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Lucide.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onEvent(InstitutionEvent.UpdateSearchQuery("")) }) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Clear",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.EmeraldGreen,
                    unfocusedBorderColor = borderVal
                )
            )
            
            // Toggle Camera Scanner button
            IconButton(
                onClick = { 
                    if (!hasCameraPermission) {
                        onRequestCameraPermission()
                    } else {
                        viewModel.onEvent(InstitutionEvent.ToggleCameraScanner(!state.isCameraScannerOpen))
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (state.isCameraScannerOpen) AppColors.EmeraldGreen else AppColors.EmeraldGreen.copy(alpha = 0.08f),
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.dp,
                        if (state.isCameraScannerOpen) Color.Transparent else borderVal,
                        RoundedCornerShape(10.dp)
                    )
            ) {
                Icon(
                    imageVector = if (state.isCameraScannerOpen) Lucide.CameraOff else Lucide.Camera,
                    contentDescription = "Toggle Scan",
                    tint = if (state.isCameraScannerOpen) Color.White else AppColors.EmeraldGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Expanded Inline CameraX Scanner Area
        AnimatedVisibility(
            visible = state.isCameraScannerOpen,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(2.dp, AppColors.EmeraldGreen, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasCameraPermission) {
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx).apply {
                                    scaleType = PreviewView.ScaleType.FILL_CENTER
                                }
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().apply {
                                        setSurfaceProvider(previewView.surfaceProvider)
                                    }
                                    
                                    val barcodeScanner = BarcodeScanning.getClient()
                                    val analysisUseCase = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .build()
                                    
                                    analysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                        val mediaImage = imageProxy.image
                                        if (mediaImage != null) {
                                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                            barcodeScanner.process(image)
                                                .addOnSuccessListener { barcodes ->
                                                    val barcode = barcodes.firstOrNull()
                                                    val rawValue = barcode?.rawValue
                                                    if (!rawValue.isNullOrBlank()) {
                                                        val now = System.currentTimeMillis()
                                                        if (now - lastScanTime > 2500) {
                                                            lastScanTime = now
                                                            viewModel.onEvent(
                                                                InstitutionEvent.ScanStudentQrCode(
                                                                    qrToken = rawValue,
                                                                    latitude = null,
                                                                    longitude = null,
                                                                    staffId = staffId
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                                .addOnCompleteListener {
                                                    imageProxy.close()
                                                }
                                        } else {
                                            imageProxy.close()
                                        }
                                    }
                                    
                                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            analysisUseCase
                                        )
                                    } catch (e: Exception) {
                                        Log.e("DriverAttendance", "Use case binding failed", e)
                                    }
                                }, ContextCompat.getMainExecutor(ctx))
                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        // Scanner overlay HUD
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .border(1.5.dp, AppColors.EmeraldGreen, RoundedCornerShape(12.dp))
                            )
                        }
                    } else {
                        Text(
                            text = if (isHindi) "उपस्थिति के लिए कैमरा अनुमति दें" else "Grant camera permission to scan QR",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pre-populated Assigned Students list
        Text(
            text = if (isHindi) "आवंटित विद्यार्थी सूची" else "Assigned Student Directory Checklist",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (filteredStudents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (state.assignedStudents.isEmpty()) {
                                if (isHindi) "इस बस में कोई छात्र आवंटित नहीं है।" else "No students assigned to this bus route yet."
                            } else {
                                if (isHindi) "कोई मेल खाते विद्यार्थी नहीं मिले।" else "No matching students found."
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredStudents) { student ->
                    val isBoarded = state.busTripAttendanceLogs.any { it.studentId == student.id }
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, borderVal),
                        color = cardBg
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Student image / Initials circle
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!student.imageUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = student.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        val initials = student.name.take(2).uppercase()
                                        val colorHash = student.name.hashCode()
                                        val initialColor = when (Math.abs(colorHash) % 4) {
                                            0 -> AppColors.EmeraldGreen
                                            1 -> Color(0xFF34C759)
                                            2 -> Color(0xFF007AFF)
                                            else -> Color(0xFF5856D6)
                                        }
                                        Text(
                                            text = initials,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = initialColor
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = student.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    
                                    val cls = student.className ?: ""
                                    val sec = student.sectionName ?: ""
                                    val classDisplay = if (cls.isNotEmpty()) "$cls-$sec" else "N/A"
                                    
                                    Text(
                                        text = if (isHindi) {
                                            "कक्षा: $classDisplay | पिता: ${student.guardianName ?: "—"}"
                                        } else {
                                            "Class: $classDisplay | Father: ${student.guardianName ?: "—"}"
                                        },
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            
                            // Boarding Action Status
                            Box(modifier = Modifier.padding(start = 8.dp)) {
                                if (isBoarded) {
                                    // Green Boarded Badge
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier
                                            .background(AppColors.EmeraldGreen.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                            .border(0.5.dp, AppColors.EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Lucide.Check,
                                            contentDescription = null,
                                            tint = AppColors.EmeraldGreen,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = if (isHindi) "बोर्डेड" else "Boarded",
                                            fontSize = 10.sp,
                                            color = AppColors.EmeraldGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    // Submit boarding button
                                    Button(
                                        onClick = {
                                            viewModel.onEvent(
                                                InstitutionEvent.MarkStudentBoarded(
                                                    studentId = student.id,
                                                    latitude = null,
                                                    longitude = null,
                                                    staffId = staffId
                                                )
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(
                                            text = if (isHindi) "चढ़ाएं" else "Board", 
                                            color = Color.White, 
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
