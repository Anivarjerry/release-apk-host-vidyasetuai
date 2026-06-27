package com.vidyasetuai.feature_feed.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.vidyasetuai.feature_institution.domain.model.InstitutionStudent
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@Composable
fun LeaveSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    userId: String,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    val activeRole = when (state.activeWorkspace?.role) {
        "Student" -> "student"
        "Guardian" -> "student"
        else -> "staff"
    }
    val isGuardian = state.activeWorkspace?.role == "Guardian"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (activeRole == "staff") {
                StaffLeaveSummaryCard(
                    leaveQuota = state.leaveQuota,
                    remainingLeaves = state.remainingLeaves,
                    isHindi = isHindi,
                    isDark = isDark,
                    borderVal = borderVal,
                    cardBg = cardBg
                )
            }

            // Leave Request Form Box
            LeaveRequestBox(
                isHindi = isHindi,
                isDark = isDark,
                userId = userId,
                activeRole = activeRole,
                viewModel = viewModel,
                parentOrgId = state.activeWorkspace?.parentOrgId ?: "",
                orgId = state.activeWorkspace?.childOrgId,
                students = state.guardianStudents,
                isGuardian = isGuardian
            )

            // Past Leaves History
            Text(
                text = if (isHindi) "अवकाश इतिहास (History)" else "Leave History",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (state.leaves.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                    color = cardBg
                ) {
                    Text(
                        text = if (isHindi) "कोई पुराना अवकाश इतिहास उपलब्ध नहीं है" else "No leave history available.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                state.leaves.forEach { leave ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, borderVal),
                        color = cardBg
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = leave.leaveType,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    val dateText = if (leave.startDate == leave.endDate) {
                                        leave.startDate
                                    } else {
                                        "${leave.startDate} to ${leave.endDate}"
                                    }
                                    Text(
                                        text = dateText,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (leave.isHalfDay) {
                                        Text(
                                            text = "Half Day (${leave.halfDayPeriod ?: ""})",
                                            fontSize = 11.sp,
                                            color = AppColors.EmeraldGreen,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                
                                val badgeColor = when (leave.status) {
                                    "Approved" -> AppColors.EmeraldGreen
                                    "Rejected" -> Color(0xFFEF4444)
                                    else -> Color(0xFFF59E0B) // Pending -> Amber
                                }
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = badgeColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = leave.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            if (isGuardian && !leave.studentId.isNullOrEmpty()) {
                                val childName = state.guardianStudents.find { it.id == leave.studentId }?.name ?: "Student"
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isHindi) "बच्चा: $childName" else "Child: $childName",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (!leave.reason.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isHindi) "कारण: ${leave.reason}" else "Reason: ${leave.reason}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaveRequestBox(
    isHindi: Boolean,
    isDark: Boolean,
    userId: String,
    activeRole: String,
    viewModel: InstitutionViewModel,
    parentOrgId: String,
    orgId: String?,
    students: List<InstitutionStudent> = emptyList(),
    isGuardian: Boolean = false
) {
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    var leaveType by remember { mutableStateOf("Sick") }
    var reason by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("2026-06-14") }
    var endDate by remember { mutableStateOf("2026-06-14") }
    var isHalfDay by remember { mutableStateOf(false) }
    var halfDayPeriod by remember { mutableStateOf("First Half") }
    var selectedStudentId by remember(students) { mutableStateOf(students.firstOrNull()?.id ?: "") }
    var leavesSubmittedSuccess by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
        color = cardBg
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isHindi) "अवकाश के लिए आवेदन करें" else "Apply for Leave",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (leavesSubmittedSuccess) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) Color(0xFF0F2C24) else Color(0xFFECFDF5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Lucide.Check, contentDescription = null, tint = AppColors.EmeraldGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "अवकाश आवेदन सफलतापूर्वक भेजा गया!" else "Leave request submitted successfully!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.EmeraldGreen
                        )
                    }
                }
            } else {
                if (isGuardian && students.isNotEmpty()) {
                    val selectedStudent = students.find { it.id == selectedStudentId }
                    val selectedStudentName = selectedStudent?.name ?: (if (isHindi) "बच्चे का चयन करें" else "Select Child")

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedStudentName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = if (isHindi) "बच्चे का चयन करें" else "Select Child", fontSize = 11.sp) },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (dropdownExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.EmeraldGreen,
                                unfocusedBorderColor = borderVal
                            )
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { dropdownExpanded = !dropdownExpanded }
                        )
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier
                                .background(cardBg)
                        ) {
                            students.forEach { child ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = child.name,
                                            fontSize = 13.sp,
                                            fontWeight = if (child.id == selectedStudentId) FontWeight.Bold else FontWeight.Normal,
                                            color = if (child.id == selectedStudentId) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground
                                        )
                                    },
                                    onClick = {
                                        selectedStudentId = child.id
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Input forms (Mock selection / reason field)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Sick", "Casual", "Personal").forEach { type ->
                        val selected = leaveType == type
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (selected) AppColors.EmeraldGreen.copy(alpha = 0.12f) else (if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)),
                            border = androidx.compose.foundation.BorderStroke(
                                0.5.dp,
                                if (selected) AppColors.EmeraldGreen else Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { leaveType = type }
                        ) {
                            Text(
                                text = type,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Date Fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text(text = if (isHindi) "शुरू तिथि" else "Start Date", fontSize = 11.sp) },
                        placeholder = { Text("YYYY-MM-DD", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.EmeraldGreen,
                            unfocusedBorderColor = borderVal
                        )
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text(text = if (isHindi) "अंत तिथि" else "End Date", fontSize = 11.sp) },
                        placeholder = { Text("YYYY-MM-DD", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.EmeraldGreen,
                            unfocusedBorderColor = borderVal
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Half Day Options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isHalfDay,
                        onCheckedChange = { isHalfDay = it },
                        colors = CheckboxDefaults.colors(checkedColor = AppColors.EmeraldGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "हाफ डे (Half Day)" else "Half Day",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (isHalfDay) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("First Half", "Second Half").forEach { period ->
                            val selected = halfDayPeriod == period
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (selected) AppColors.EmeraldGreen.copy(alpha = 0.12f) else (if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)),
                                border = androidx.compose.foundation.BorderStroke(
                                    0.5.dp,
                                    if (selected) AppColors.EmeraldGreen else Color.Transparent
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { halfDayPeriod = period }
                            ) {
                                Text(
                                    text = if (isHindi) {
                                        if (period == "First Half") "प्रथम भाग" else "द्वितीय भाग"
                                    } else period,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Simple TextField for reason
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text(text = if (isHindi) "अवकाश का कारण लिखें" else "Reason for Leave", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.EmeraldGreen,
                        unfocusedBorderColor = borderVal
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        viewModel.onEvent(
                            InstitutionEvent.SubmitLeaveRequest(
                                parentOrgId = parentOrgId,
                                orgId = orgId,
                                sessionId = viewModel.uiState.value.driverBusDetails?.activeSessionId ?: "session_mock_id_1",
                                applicantType = activeRole,
                                staffId = if (activeRole == "staff") (viewModel.uiState.value.driverBusDetails?.staffId ?: "staff_mock_id_1") else null,
                                studentId = if (activeRole == "student") {
                                    if (isGuardian) selectedStudentId else "student_mock_id_1"
                                } else null,
                                leaveType = leaveType,
                                startDate = startDate,
                                endDate = endDate,
                                isHalfDay = isHalfDay,
                                halfDayPeriod = if (isHalfDay) halfDayPeriod else null,
                                reason = reason,
                                createdBy = userId
                            )
                        )
                        leavesSubmittedSuccess = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.EmeraldGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(21.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                ) {
                    Text(text = if (isHindi) "आवेदन जमा करें" else "Submit Application", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
