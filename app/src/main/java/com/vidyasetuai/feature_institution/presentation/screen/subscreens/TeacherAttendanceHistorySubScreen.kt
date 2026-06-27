package com.vidyasetuai.feature_feed.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun TeacherAttendanceHistorySubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onBack: () -> Unit
) {
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    var showDatePicker by remember { mutableStateOf(false) }
    var showOrgDropdown by remember { mutableStateOf(false) }
    var showClassDropdown by remember { mutableStateOf(false) }
    var showSectionDropdown by remember { mutableStateOf(false) }

    val selectedOrg = state.childOrganizations.find { it.id == state.selectedChildOrg }
    val selectedClass = state.activeClasses.find { it.id == state.selectedClass }
    val selectedSection = state.activeSections.find { it.id == state.selectedSection }

    // Date Picker Dialog for history
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

    // Compute stats
    val presentCount = state.studentsForAttendance.count { it.status == "Present" }
    val absentCount = state.studentsForAttendance.count { it.status == "Absent" }
    val leaveCount = state.studentsForAttendance.count { it.status == "On Leave" }

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
            // Compact Selectors Panel
            Surface(
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, borderVal),
                color = cardBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Date picker
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

                        // Org Selector
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

            // If selection is complete and a query was performed (students list is not empty)
            if (state.studentsForAttendance.isNotEmpty()) {
                if (state.isAttendanceAlreadyMarked) {
                    // Summary Stats Card (Present/Absent/On Leave counts)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, borderVal),
                        color = cardBg,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
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
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Read-Only List Header
                    Text(
                        text = if (isHindi) "दर्ज उपस्थिति रिकॉर्ड (Marked Record):" else "Attendance Log:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Read-Only List
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.studentsForAttendance) { student ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, borderVal),
                                color = cardBg,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
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

                                    val statusColor = when (student.status) {
                                        "Present" -> AppColors.EmeraldGreen
                                        "Absent" -> Color(0xFFEF4444)
                                        else -> Color(0xFFF59E0B)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = statusColor.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, statusColor)
                                    ) {
                                        Text(
                                            text = if (isHindi) {
                                                when (student.status) {
                                                    "Present" -> "उपस्थित"
                                                    "Absent" -> "अनुपस्थित"
                                                    else -> "अवकाश"
                                                }
                                            } else student.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = statusColor,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Attendance is not marked for this date
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDark) Color(0xFF2C2514) else Color(0xFFFFFBEB),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.CircleAlert,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (isHindi) "इस तिथि के लिए उपस्थिति दर्ज नहीं की गई है।" else "Attendance has not been marked for this date.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDark) Color(0xFFF59E0B) else Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }
            } else {
                // Not queried yet or selection incomplete
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val selectionComplete = !state.selectedChildOrg.isNullOrBlank() && 
                                            !state.selectedClass.isNullOrBlank() && 
                                            !state.selectedSection.isNullOrBlank()
                    Text(
                        text = if (selectionComplete) {
                            if (isHindi) "उपस्थिति इतिहास देखने के लिए 'खोजें' पर क्लिक करें।" else "Click 'Search' to view attendance history."
                        } else {
                            if (isHindi) "कृपया संस्थान, कक्षा और सेक्शन चुनें।" else "Please select Org, Class and Section."
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
