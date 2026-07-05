package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StartTripFabSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBgColor = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val subtitleColor = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    // Active workspace credentials
    val activeWs = state.activeWorkspace
    val assignedBus = state.allBuses.find { it.driverId == activeWs?.staffId }
    val busId = assignedBus?.id ?: state.driverBusDetails?.busId ?: ""
    val parentOrgId = assignedBus?.parentOrganizationId ?: state.driverBusDetails?.parentOrgId ?: ""
    val activeSessionId = assignedBus?.activeSessionId ?: state.driverBusDetails?.activeSessionId ?: ""
    val driverId = activeWs?.staffId ?: ""

    // Dropdown options
    var dropdownExpanded by remember { mutableStateOf(false) }
    val tripTypes = listOf("Morning_Pickup", "Evening_Drop", "Special")
    var selectedTripType by remember { mutableStateOf("Morning_Pickup") }

    val activeTrip = state.activeBusTrip

    // Load logs and student lists when the trip loads
    LaunchedEffect(activeTrip?.id) {
        activeTrip?.id?.let { tripId ->
            viewModel.onEvent(InstitutionEvent.LoadTripAttendanceLogs(tripId))
            if (busId.isNotEmpty()) {
                viewModel.onEvent(InstitutionEvent.LoadAssignedStudents(busId))
            }
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(backgroundColor)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = textColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isHindi) "ट्रिप अटेंडेंस" else "Trip Attendance",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(dividerColor)
                )
            }
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        if (busId.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isHindi) "आपको कोई बस आवंटित नहीं की गई है।" else "No bus is assigned to you.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = subtitleColor,
                    textAlign = TextAlign.Center
                )
            }
            return@Scaffold
        }

        if (activeTrip == null) {
            // ── TRIP INITIALIZATION SCREEN ───────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .background(backgroundColor),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Lucide.Bus,
                    contentDescription = null,
                    tint = AppColors.EmeraldGreen,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isHindi) "नई यात्रा शुरू करें" else "Start New Trip",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = if (isHindi) "बस: ${assignedBus?.busNumber ?: ""}" else "Bus: ${assignedBus?.busNumber ?: ""}",
                    fontSize = 14.sp,
                    color = subtitleColor
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Trip Type Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { dropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val displayText = when (selectedTripType) {
                                "Morning_Pickup" -> if (isHindi) "सुबह बच्चों को लाना (Morning Pickup)" else "Morning Pickup"
                                "Evening_Drop" -> if (isHindi) "शाम को बच्चों को छोड़ना (Evening Drop)" else "Evening Drop"
                                else -> if (isHindi) "विशेष यात्रा (Special)" else "Special"
                            }
                            Text(text = displayText, color = textColor)
                            Icon(imageVector = Lucide.ChevronDown, contentDescription = null, tint = textColor)
                        }
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        tripTypes.forEach { type ->
                            val itemText = when (type) {
                                "Morning_Pickup" -> if (isHindi) "सुबह बच्चों को लाना (Morning Pickup)" else "Morning Pickup"
                                "Evening_Drop" -> if (isHindi) "शाम को बच्चों को छोड़ना (Evening Drop)" else "Evening Drop"
                                else -> if (isHindi) "विशेष यात्रा (Special)" else "Special"
                            }
                            DropdownMenuItem(
                                text = { Text(text = itemText) },
                                onClick = {
                                    selectedTripType = type
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.onEvent(
                            InstitutionEvent.StartBusTrip(
                                parentOrgId = parentOrgId,
                                sessionId = activeSessionId,
                                busId = busId,
                                driverId = driverId,
                                tripType = selectedTripType
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Lucide.Play, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "यात्रा शुरू करें" else "Start Trip",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        } else {
            // ── ACTIVE TRIP ATTENDANCE CHECKLIST ─────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor)
            ) {
                // Header Trip Details Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val tripText = when (activeTrip.tripType) {
                                "Morning_Pickup" -> if (isHindi) "सुबह बच्चों को लाना" else "Morning Pickup"
                                "Evening_Drop" -> if (isHindi) "शाम को बच्चों को छोड़ना" else "Evening Drop"
                                else -> if (isHindi) "विशेष यात्रा" else "Special Trip"
                            }
                            Text(
                                text = tripText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Text(
                                text = if (isHindi) "स्थिति: जारी है (Ongoing)" else "Status: Ongoing",
                                fontSize = 13.sp,
                                color = AppColors.EmeraldGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // End Trip Button
                        Button(
                            onClick = {
                                viewModel.onEvent(InstitutionEvent.EndBusTrip(activeTrip.id))
                                Toast.makeText(context, if (isHindi) "यात्रा समाप्त हो गई है" else "Trip completed successfully", Toast.LENGTH_SHORT).show()
                                onBack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Lucide.Square, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isHindi) "यात्रा समाप्त" else "End Trip", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onEvent(InstitutionEvent.UpdateSearchQuery(it)) },
                    placeholder = { Text(text = if (isHindi) "विद्यार्थी का नाम या रोल नंबर..." else "Search name or roll...") },
                    leadingIcon = { Icon(imageVector = Lucide.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Students List
                val filteredStudents = remember(state.assignedStudents, state.searchQuery) {
                    state.assignedStudents.filter { student ->
                        state.searchQuery.isEmpty() ||
                        (student.name ?: "").contains(state.searchQuery, ignoreCase = true) ||
                        (student.rollNumber?.toString() ?: "").contains(state.searchQuery)
                    }
                }

                if (filteredStudents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isHindi) "कोई विद्यार्थी नहीं मिला" else "No students found",
                            fontSize = 14.sp,
                            color = subtitleColor
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredStudents, key = { it.id }) { student ->
                            val matchedLog = state.busTripAttendanceLogs.find { it.studentId == student.id }
                            val currentStatus = matchedLog?.status ?: ""

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Avatar circle
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = (student.name ?: "S").take(1).uppercase(),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textColor
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        // Details
                                        Column {
                                            Text(
                                                text = student.name ?: "Student",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textColor
                                            )
                                            Text(
                                                text = "${student.className ?: ""} ${student.sectionName ?: ""} • Roll: ${student.rollNumber ?: ""}",
                                                fontSize = 12.sp,
                                                color = subtitleColor
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Attendance Action Selector Buttons Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // 1. Boarded Button
                                        val isBoarded = currentStatus == "Boarded"
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.onEvent(
                                                    InstitutionEvent.MarkStudentTripAttendance(
                                                        studentId = student.id,
                                                        status = "Boarded",
                                                        latitude = null,
                                                        longitude = null,
                                                        staffId = driverId
                                                    )
                                                )
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = if (isBoarded) Color(0xFF2563EB) else Color.Transparent
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isBoarded) Color(0xFF2563EB) else borderColor
                                            ),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                        ) {
                                            if (isBoarded) {
                                                Icon(
                                                    imageVector = Lucide.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = if (isHindi) "चढ़ा" else "Boarded",
                                                fontSize = 11.sp,
                                                color = if (isBoarded) Color.White else textColor
                                            )
                                        }

                                        // 2. Dropped Button
                                        val isDropped = currentStatus == "Dropped"
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.onEvent(
                                                    InstitutionEvent.MarkStudentTripAttendance(
                                                        studentId = student.id,
                                                        status = "Dropped",
                                                        latitude = null,
                                                        longitude = null,
                                                        staffId = driverId
                                                    )
                                                )
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = if (isDropped) AppColors.EmeraldGreen else Color.Transparent
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isDropped) AppColors.EmeraldGreen else borderColor
                                            ),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                        ) {
                                            if (isDropped) {
                                                Icon(
                                                    imageVector = Lucide.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = if (isHindi) "उतरा" else "Dropped",
                                                fontSize = 11.sp,
                                                color = if (isDropped) Color.White else textColor
                                            )
                                        }

                                        // 3. Absent Button
                                        val isAbsent = currentStatus == "Absent"
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.onEvent(
                                                    InstitutionEvent.MarkStudentTripAttendance(
                                                        studentId = student.id,
                                                        status = "Absent",
                                                        latitude = null,
                                                        longitude = null,
                                                        staffId = driverId
                                                    )
                                                )
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = if (isAbsent) Color(0xFFEF4444) else Color.Transparent
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isAbsent) Color(0xFFEF4444) else borderColor
                                            ),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                        ) {
                                            if (isAbsent) {
                                                Icon(
                                                    imageVector = Lucide.X,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = if (isHindi) "अनुपस्थित" else "Absent",
                                                fontSize = 11.sp,
                                                color = if (isAbsent) Color.White else textColor
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
}
