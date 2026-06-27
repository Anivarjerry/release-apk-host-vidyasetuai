package com.vidyasetuai.feature_feed.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeAttendanceSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    userId: String,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    var showDatePicker by remember { mutableStateOf(false) }
    var showOrgDropdown by remember { mutableStateOf(false) }
    var showClassDropdown by remember { mutableStateOf(false) }
    var showSectionDropdown by remember { mutableStateOf(false) }

    val selectedOrg = state.childOrganizations.find { it.id == state.selectedChildOrg }
    val selectedClass = state.activeClasses.find { it.id == state.selectedClass }
    val selectedSection = state.activeSections.find { it.id == state.selectedSection }

    // Date Picker Dialog
    if (showDatePicker) {
        val initialEpoch = try {
            java.time.LocalDate.parse(state.attendanceDate)
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
                        viewModel.onEvent(InstitutionEvent.ChangeAttendanceDate(dateStr))
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
                ),
                dateValidator = { utcTimeMillis ->
                    val date = java.time.Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(java.time.ZoneOffset.UTC).toLocalDate()
                    val today = java.time.LocalDate.now()
                    
                    val start = try { java.time.LocalDate.parse(state.sessionStartDate) } catch (e: Exception) { today.minusYears(1) }
                    val end = try { java.time.LocalDate.parse(state.sessionEndDate) } catch (e: Exception) { today.plusYears(1) }
                    
                    !date.isAfter(today) && !date.isBefore(start) && !date.isAfter(end)
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Success Notification Banner
            if (state.attendanceSubmittedSuccess) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) Color(0xFF0F2C24) else Color(0xFFECFDF5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Lucide.Check, contentDescription = null, tint = AppColors.EmeraldGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "उपस्थिति सफलतापूर्वक दर्ज की गई!" else "Attendance submitted successfully!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.EmeraldGreen
                        )
                    }
                }
            }

            // Compact Selectors Panel (Grid structure to reduce height)
            if (!state.isAttendanceAlreadyMarked || state.isAttendanceEditEnabled) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, borderVal),
                    color = cardBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Date & Child Org Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Date picker trigger field
                            Box(modifier = Modifier.weight(1.1f).clickable { showDatePicker = true }) {
                                OutlinedTextField(
                                    value = state.attendanceDate,
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = false,
                                    label = { Text(text = if (isHindi) "दिनांक (Date)" else "Date", fontSize = 10.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onBackground,
                                        disabledBorderColor = borderVal,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    trailingIcon = {
                                        Icon(imageVector = Lucide.Calendar, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                )
                            }

                            // Child Organization Selector
                            Box(modifier = Modifier.weight(1.5f)) {
                                OutlinedTextField(
                                    value = selectedOrg?.name ?: (if (isHindi) "संस्थान चुनें" else "Select Org"),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(text = if (isHindi) "संस्थान" else "Organization", fontSize = 10.sp) },
                                    modifier = Modifier.fillMaxWidth().clickable { showOrgDropdown = true },
                                    enabled = false,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onBackground,
                                        disabledBorderColor = borderVal,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    trailingIcon = {
                                        IconButton(onClick = { showOrgDropdown = true }) {
                                            Icon(imageVector = Lucide.ChevronDown, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                )
                                DropdownMenu(
                                    expanded = showOrgDropdown,
                                    onDismissRequest = { showOrgDropdown = false },
                                    modifier = Modifier.fillMaxWidth(0.6f)
                                ) {
                                    state.childOrganizations.forEach { org ->
                                        DropdownMenuItem(
                                            text = { Text(org.name, fontSize = 12.sp) },
                                            onClick = {
                                                viewModel.onEvent(InstitutionEvent.SelectChildOrg(org.id))
                                                showOrgDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Class & Section Row (side-by-side to save vertical space)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Class Selector
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = selectedClass?.name ?: (if (isHindi) "कक्षा चुनें" else "Class"),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(text = if (isHindi) "कक्षा" else "Class", fontSize = 10.sp) },
                                    modifier = Modifier.fillMaxWidth().clickable { showClassDropdown = true },
                                    enabled = false,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onBackground,
                                        disabledBorderColor = borderVal,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    trailingIcon = {
                                        IconButton(onClick = { showClassDropdown = true }) {
                                            Icon(imageVector = Lucide.ChevronDown, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                )
                                DropdownMenu(
                                    expanded = showClassDropdown,
                                    onDismissRequest = { showClassDropdown = false }
                                ) {
                                    state.activeClasses.forEach { cls ->
                                        DropdownMenuItem(
                                            text = { Text(cls.name, fontSize = 12.sp) },
                                            onClick = {
                                                viewModel.onEvent(InstitutionEvent.SelectClass(cls.id))
                                                showClassDropdown = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Section Selector
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = selectedSection?.name ?: (if (isHindi) "सेक्शन चुनें" else "Section"),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(text = if (isHindi) "सेक्शन" else "Section", fontSize = 10.sp) },
                                    modifier = Modifier.fillMaxWidth().clickable { showSectionDropdown = true },
                                    enabled = false,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onBackground,
                                        disabledBorderColor = borderVal,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    trailingIcon = {
                                        IconButton(onClick = { showSectionDropdown = true }) {
                                            Icon(imageVector = Lucide.ChevronDown, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                )
                                DropdownMenu(
                                    expanded = showSectionDropdown,
                                    onDismissRequest = { showSectionDropdown = false }
                                ) {
                                    state.activeSections.forEach { sect ->
                                        DropdownMenuItem(
                                            text = { Text(sect.name, fontSize = 12.sp) },
                                            onClick = {
                                                viewModel.onEvent(InstitutionEvent.SelectSection(sect.id))
                                                showSectionDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Search Button Row
                        val isSearchEnabled = !state.selectedChildOrg.isNullOrBlank() && 
                                              !state.selectedClass.isNullOrBlank() && 
                                              !state.selectedSection.isNullOrBlank()
                        
                        if (isSearchEnabled) {
                            Button(
                                onClick = {
                                    viewModel.onEvent(
                                        InstitutionEvent.LoadStudentsForAttendance(
                                            orgId = state.selectedChildOrg ?: "",
                                            classId = state.selectedClass ?: "",
                                            sectionId = state.selectedSection ?: "",
                                            date = state.attendanceDate
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppColors.EmeraldGreen,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .height(36.dp)
                            ) {
                                Icon(imageVector = Lucide.Search, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "खोजें" else "Search",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Already marked summary and edit button
            if (state.isAttendanceAlreadyMarked && !state.isAttendanceEditEnabled) {
                val presentCount = state.studentsForAttendance.count { it.status == "Present" }
                val absentCount = state.studentsForAttendance.count { it.status == "Absent" }
                val leaveCount = state.studentsForAttendance.count { it.status == "On Leave" }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                    color = cardBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(AppColors.EmeraldGreen.copy(alpha = 0.1f), CircleShape),
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
                                    text = if (isHindi) "उपस्थिति सबमिट की जा चुकी है!" else "Attendance Submitted!",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.EmeraldGreen
                                )
                                Text(
                                    text = "${selectedOrg?.name ?: ""} • ${selectedClass?.name ?: ""} • ${selectedSection?.name ?: ""}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Divider(color = borderVal, thickness = 0.5.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Lucide.Calendar, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = state.attendanceDate, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, AppColors.EmeraldGreen)
                            ) {
                                Text(
                                    text = if (isHindi) "सफलतापूर्वक दर्ज" else "Submitted",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.EmeraldGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text(text = if (isHindi) "उपस्थित" else "Present", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "$presentCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                            }
                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(borderVal))
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text(text = if (isHindi) "अनुपस्थित" else "Absent", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "$absentCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                            }
                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(borderVal))
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text(text = if (isHindi) "अवकाश" else "On Leave", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "$leaveCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                viewModel.onEvent(InstitutionEvent.SetAttendanceEditEnabled(true))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            Text(
                                text = if (isHindi) "उपस्थिति में बदलाव करें (Edit)" else "Edit Attendance",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Warning banner if edit mode is active
            if (state.isAttendanceAlreadyMarked && state.isAttendanceEditEnabled) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) Color(0xFF2C2514) else Color(0xFFFFFBEB),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(imageVector = Lucide.CircleAlert, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "सबमिट की गई उपस्थिति में बदलाव किया जा रहा है..." else "Editing marked attendance...",
                                fontSize = 12.sp,
                                color = if (isDark) Color(0xFFF59E0B) else Color(0xFFB45309)
                            )
                        }
                    }
                }
            }

            // Student Checklist Header
            if (state.studentsForAttendance.isNotEmpty() && (!state.isAttendanceAlreadyMarked || state.isAttendanceEditEnabled)) {
                Text(
                    text = if (isHindi) "उपस्थिति सूची (Checklist):" else "Attendance Checklist:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // List of students
            if (!state.isAttendanceAlreadyMarked || state.isAttendanceEditEnabled) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (state.studentsForAttendance.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                val selectionComplete = !state.selectedChildOrg.isNullOrBlank() && 
                                                        !state.selectedClass.isNullOrBlank() && 
                                                        !state.selectedSection.isNullOrBlank()
                                Text(
                                    text = if (selectionComplete) {
                                        if (isHindi) "छात्रों को लोड करने के लिए 'खोजें' पर क्लिक करें।" else "Click 'Search' to load students."
                                    } else {
                                        if (isHindi) "कृपया संस्थान, कक्षा और सेक्शन चुनें।" else "Please select Org, Class and Section."
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(state.studentsForAttendance) { student ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, borderVal),
                                color = cardBg,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = student.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            Text(
                                                text = "SR: ${student.srNumber}",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        // Leave Alert Statuses
                                        if (student.isLeaveApproved) {
                                            Surface(
                                                shape = RoundedCornerShape(100.dp),
                                                color = AppColors.EmeraldGreen.copy(alpha = 0.12f)
                                            ) {
                                                Text(
                                                    text = if (isHindi) "स्वीकृत अवकाश" else "Approved Leave",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AppColors.EmeraldGreen,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        } else if (student.isLeavePending) {
                                            Surface(
                                                shape = RoundedCornerShape(100.dp),
                                                color = Color(0xFFF59E0B).copy(alpha = 0.12f)
                                            ) {
                                                Text(
                                                    text = if (isHindi) "लंबित अवकाश" else "Pending Leave",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFF59E0B),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Present, Absent, On Leave Pills
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        val interactionEnabled = !student.isLeaveApproved && (!state.isAttendanceAlreadyMarked || state.isAttendanceEditEnabled)

                                        listOf("Present", "Absent", "On Leave").forEach { status ->
                                            val selected = student.status == status

                                            val btnColor = when (status) {
                                                "Present" -> AppColors.EmeraldGreen
                                                "Absent" -> Color(0xFFEF4444)
                                                else -> Color(0xFFF59E0B)
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (selected) btnColor.copy(alpha = 0.12f) else (if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    0.5.dp,
                                                    if (selected) btnColor else Color.Transparent
                                                ),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable(enabled = interactionEnabled) {
                                                        viewModel.onEvent(
                                                            InstitutionEvent.UpdateStudentAttendanceStatus(
                                                                student.studentId,
                                                                status
                                                            )
                                                        )
                                                    }
                                            ) {
                                                Text(
                                                    text = if (isHindi) {
                                                        when (status) {
                                                            "Present" -> "उपस्थित"
                                                            "Absent" -> "अनुपस्थित"
                                                            else -> "अवकाश"
                                                        }
                                                    } else status,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (selected) btnColor else (if (interactionEnabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)),
                                                    modifier = Modifier.padding(vertical = 6.dp),
                                                    textAlign = TextAlign.Center
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

            // Submit Button (Visible only if editing is active or attendance has not been marked yet)
            if (state.studentsForAttendance.isNotEmpty() && (!state.isAttendanceAlreadyMarked || state.isAttendanceEditEnabled)) {
                Button(
                    onClick = {
                        val orgId = state.selectedChildOrg
                        val date = state.attendanceDate
                        if (!orgId.isNullOrBlank()) {
                            viewModel.onEvent(
                                InstitutionEvent.SubmitStudentsAttendance(
                                    orgId = orgId,
                                    date = date,
                                    staffUserId = userId,
                                    parentOrgId = state.activeWorkspace?.parentOrgId ?: ""
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.EmeraldGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(21.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .height(42.dp)
                ) {
                    Text(
                        text = if (isHindi) "उपस्थिति दर्ज करें (Submit)" else "Submit Attendance",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
