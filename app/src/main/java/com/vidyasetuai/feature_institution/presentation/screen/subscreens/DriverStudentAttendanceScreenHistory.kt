package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.domain.model.ParentBusTrip
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverStudentAttendanceScreenHistory(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White

    val driverId = state.driverBusDetails?.staffId ?: ""
    
    // Load trips on launch
    LaunchedEffect(driverId) {
        if (driverId.isNotEmpty()) {
            viewModel.onEvent(InstitutionEvent.LoadDriverTrips(driverId))
        }
    }

    var selectedTripId by remember { mutableStateOf<String?>(null) }
    var showTripDropdown by remember { mutableStateOf(false) }
    
    var filterDate by remember { mutableStateOf<String?>(null) }
    var filterTripType by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    var anchorWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val tripTypes = listOf(
        "Morning_Pickup" to (if (isHindi) "सुबह पिकअप" else "Morning Pickup"),
        "Evening_Drop" to (if (isHindi) "शाम ड्रॉप" else "Evening Drop"),
        "Special" to (if (isHindi) "विशेष यात्रा" else "Special Trip")
    )
    
    val filteredTrips = remember(state.driverBusTrips, filterDate, filterTripType) {
        state.driverBusTrips.filter { trip ->
            val dateMatch = if (filterDate != null) {
                trip.createdAt.substringBefore("T") == filterDate
            } else {
                true
            }
            val typeMatch = if (filterTripType != null) {
                trip.tripType == filterTripType
            } else {
                true
            }
            dateMatch && typeMatch
        }
    }

    // Date Picker Dialog for history
    if (showDatePicker) {
        val initialEpoch = try {
            val dateToParse = filterDate ?: java.time.LocalDate.now().toString()
            java.time.LocalDate.parse(dateToParse)
                .atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
        } catch(e: Exception) {
            java.time.Instant.now().toEpochMilli()
        }
        
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialEpoch
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val dateStr = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneOffset.UTC).toLocalDate().toString()
                        filterDate = dateStr
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = AppColors.EmeraldGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = AppColors.EmeraldGreen,
                    selectedDayContentColor = Color.White,
                    todayContentColor = AppColors.EmeraldGreen,
                    todayDateBorderColor = AppColors.EmeraldGreen
                )
            )
        }
    }

    // Auto-select most recent trip matching filters if none selected
    LaunchedEffect(filteredTrips) {
        if (selectedTripId == null || filteredTrips.none { it.id == selectedTripId }) {
            selectedTripId = filteredTrips.firstOrNull()?.id
        }
    }

    // Load logs when selected trip changes
    LaunchedEffect(selectedTripId) {
        selectedTripId?.let {
            viewModel.onEvent(InstitutionEvent.LoadTripAttendanceLogs(it))
        }
    }

    // Handle sync success toast notification
    LaunchedEffect(state.syncSuccessCount) {
        if (state.syncSuccessCount > 0) {
            val message = if (isHindi) {
                "${state.syncSuccessCount} ऑफलाइन उपस्थिति रिकॉर्ड सिंक हो गए हैं!"
            } else {
                "Synced ${state.syncSuccessCount} offline attendance logs successfully!"
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    val selectedTrip = state.driverBusTrips.find { it.id == selectedTripId }
    val totalScans = if (selectedTripId == null) 0 else state.busTripAttendanceLogs.size
    val syncedScans = if (selectedTripId == null) 0 else state.busTripAttendanceLogs.count { it.syncStatus == "Synced" }
    val pendingScans = if (selectedTripId == null) 0 else state.busTripAttendanceLogs.count { it.syncStatus == "Offline_Pending" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Stats Summary Dashboard
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Synced Stats
            StatsBlock(
                title = if (isHindi) "सिंक किया हुआ" else "Synced",
                count = syncedScans,
                color = AppColors.EmeraldGreen,
                isDark = isDark,
                modifier = Modifier.weight(1f)
            )

            // Pending Sync Stats
            StatsBlock(
                title = if (isHindi) "ऑफलाइन लंबित" else "Offline Pending",
                count = pendingScans,
                color = Color(0xFFFF9500),
                isDark = isDark,
                modifier = Modifier.weight(1f)
            )
        }

        // Manual Sync trigger card
        if (pendingScans > 0) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                color = if (isDark) Color(0xFF2C2419) else Color(0xFFFFF9E6),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFFF9500).copy(alpha = 0.4f))
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
                        Icon(
                            imageVector = Lucide.CloudLightning,
                            contentDescription = null,
                            tint = Color(0xFFFF9500),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) {
                                "$pendingScans ऑफलाइन डेटा सिंक के लिए लंबित है"
                            } else {
                                "$pendingScans records pending local sync"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    val rotation = remember { Animatable(0f) }
                    LaunchedEffect(state.isSyncingLogs) {
                        if (state.isSyncingLogs) {
                            rotation.animateTo(
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1200, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                )
                            )
                        } else {
                            rotation.stop()
                            rotation.snapTo(0f)
                        }
                    }

                    Button(
                        onClick = {
                            if (!state.isSyncingLogs) {
                                viewModel.onEvent(InstitutionEvent.SyncOfflineLogs)
                            }
                        },
                        enabled = !state.isSyncingLogs,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9500)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.RefreshCw,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(12.dp)
                                    .rotate(rotation.value)
                            )
                            Text(
                                text = if (isHindi) "सिंक करें" else "Sync Now",
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Filters Section Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
            color = cardBg
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Header Row (Filters Title + Clear button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Lucide.SlidersHorizontal,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "यात्रा फ़िल्टर" else "Trip Filters",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    if (filterDate != null || filterTripType != null) {
                        Row(
                            modifier = Modifier.clickable {
                                filterDate = null
                                filterTripType = null
                            },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Clear Filters",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHindi) "फ़िल्टर हटाएं" else "Clear",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Date picker trigger button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clickable { showDatePicker = true },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, borderVal),
                    color = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Lucide.Calendar,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val dateLabel = if (filterDate != null) {
                                try {
                                    val parsed = java.time.LocalDate.parse(filterDate)
                                    parsed.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"))
                                } catch (e: Exception) {
                                    filterDate!!
                                }
                            } else {
                                if (isHindi) "सभी तिथियां (दिनांक चुनें)" else "All Dates (Select Date)"
                            }
                            Text(
                                text = dateLabel,
                                fontSize = 11.sp,
                                color = if (filterDate != null) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (filterDate != null) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        Icon(
                            imageVector = Lucide.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Trip Type Selector Chips Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tripTypes.forEach { (typeKey, typeLabel) ->
                        val isSelected = filterTripType == typeKey
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .clickable {
                                    filterTripType = if (isSelected) null else typeKey
                                },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) AppColors.EmeraldGreen.copy(alpha = 0.12f) else (if (isDark) Color(0xFF1C1C1E) else Color.White),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) AppColors.EmeraldGreen else borderVal
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = typeLabel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Trip Selector Dropdown Card
        Text(
            text = if (isHindi) "सत्र यात्रा चुनें" else "Select Bus Trip Run",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .onGloballyPositioned { coordinates ->
                    anchorWidth = with(density) { coordinates.size.width.toDp() }
                }
                .clickable { showTripDropdown = true },
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
            color = cardBg
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.Bus,
                        contentDescription = null,
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        if (selectedTrip != null) {
                            val rawType = selectedTrip.tripType
                            val tripHi = when (rawType) {
                                "Morning_Pickup" -> "सुबह पिकअप"
                                "Evening_Drop" -> "शाम ड्रॉप"
                                else -> "विशेष यात्रा"
                            }
                            val tripName = if (isHindi) tripHi else rawType.replace("_", " ")
                            
                            val timeText = try {
                                val parsed = java.time.ZonedDateTime.parse(selectedTrip.createdAt)
                                parsed.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM, hh:mm a"))
                            } catch (e: Exception) {
                                selectedTrip.createdAt.substringBefore("T")
                            }

                            Text(
                                text = "$tripName ($timeText)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = if (isHindi) {
                                    "स्थिति: ${if (selectedTrip.status == "Ongoing") "चालू" else "पूर्ण"}"
                                } else {
                                    "Status: ${selectedTrip.status}"
                                },
                                fontSize = 10.sp,
                                color = if (selectedTrip.status == "Ongoing") AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = if (isHindi) "कोई यात्रा उपलब्ध नहीं है" else "No trips available",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                Icon(
                    imageVector = Lucide.ChevronDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded = showTripDropdown,
                onDismissRequest = { showTripDropdown = false },
                modifier = Modifier.width(anchorWidth)
            ) {
                if (filteredTrips.isEmpty()) {
                    DropdownMenuItem(
                        text = { 
                            Text(
                                text = if (isHindi) "कोई यात्रा उपलब्ध नहीं है" else "No trips available",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ) 
                        },
                        onClick = { showTripDropdown = false }
                    )
                } else {
                    filteredTrips.forEach { trip ->
                        val rawType = trip.tripType
                        val tripHi = when (rawType) {
                            "Morning_Pickup" -> "सुबह पिकअप"
                            "Evening_Drop" -> "शाम ड्रॉप"
                            else -> "विशेष यात्रा"
                        }
                        val tripName = if (isHindi) tripHi else rawType.replace("_", " ")
                        val dateStr = try {
                            val parsed = java.time.ZonedDateTime.parse(trip.createdAt)
                            parsed.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
                        } catch (e: Exception) {
                            trip.createdAt.substringBefore("T")
                        }

                        DropdownMenuItem(
                            text = { Text("$tripName ($dateStr)", fontSize = 12.sp) },
                            onClick = {
                                selectedTripId = trip.id
                                showTripDropdown = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // History logs header
        Text(
            text = if (isHindi) "विद्यार्थी सूची रिकॉर्ड" else "Student Scan Log Entries",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        // Logs Container List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            val logsToShow = if (selectedTripId == null) emptyList() else state.busTripAttendanceLogs
            if (logsToShow.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Lucide.Users,
                            contentDescription = null,
                            tint = borderVal,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isHindi) "कोई छात्र लॉग उपलब्ध नहीं है।" else "No attendance logs recorded.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(logsToShow) { log ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(AppColors.EmeraldGreen.copy(alpha = 0.08f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.Check,
                                        contentDescription = null,
                                        tint = AppColors.EmeraldGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = log.studentName ?: (if (isHindi) "अज्ञात छात्र" else "Unknown Student"),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    val cls = log.studentClassName ?: ""
                                    val sec = log.studentSectionName ?: ""
                                    val classDisplay = if (cls.isNotEmpty()) "$cls-$sec" else "N/A"
                                    
                                    Text(
                                        text = if (isHindi) {
                                            "कक्षा: $classDisplay | रोल: ${log.studentRollNumber ?: "—"}"
                                        } else {
                                            "Class: $classDisplay | Roll: ${log.studentRollNumber ?: "—"}"
                                        },
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            
                            Column(horizontalAlignment = Alignment.End) {
                                val time = try {
                                    val parsed = java.time.ZonedDateTime.parse(log.scannedAt)
                                    parsed.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))
                                } catch (e: Exception) {
                                    log.scannedAt.substringAfter("T").substringBefore(".")
                                }
                                
                                Text(
                                    text = time,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                
                                val isSynced = log.syncStatus == "Synced"
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(
                                                if (isSynced) AppColors.EmeraldGreen else Color(0xFFFF9500),
                                                CircleShape
                                            )
                                    )
                                    Text(
                                        text = if (isSynced) {
                                            if (isHindi) "सिंक्रोनाइज्ड" else "Synced"
                                        } else {
                                            if (isHindi) "लंबित" else "Offline"
                                        },
                                        fontSize = 9.sp,
                                        color = if (isSynced) AppColors.EmeraldGreen else Color(0xFFFF9500),
                                        fontWeight = FontWeight.Black
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

@Composable
fun StatsBlock(
    title: String,
    count: Int,
    color: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = if (isDark) Color(0xFF1C1C1E) else Color.White,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}
