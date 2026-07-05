package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyLeaveFabSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    userId: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBgColor = if (isDark) Color(0xFF1E1E20) else Color.White
    val borderColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val subtitleColor = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    val workspace = state.activeWorkspace
    val role = workspace?.role ?: ""
    val isGuardian = role.equals("Guardian", ignoreCase = true)
    val isStudent = role.equals("Student", ignoreCase = true)

    // Resolved Applicant Name (For read-only views)
    val applicantName = remember(role, state.offlineStudents, state.offlineStaff, workspace) {
        when {
            isStudent -> {
                state.offlineStudents.find { it.id == workspace?.studentId }?.name
                    ?: (if (isHindi) "छात्र" else "Student")
            }
            isGuardian -> {
                "" // Guardians select children from dropdown
            }
            else -> {
                state.offlineStaff.find { it.id == workspace?.id }?.name
                    ?: (if (isHindi) "स्टाफ सदस्य" else "Staff Member")
            }
        }
    }

    // ── Form States ──
    var selectedChild by remember { mutableStateOf(state.guardianStudents.firstOrNull()) }
    var leaveType by remember { mutableStateOf("Sick Leave") }
    var leaveTypeExpanded by remember { mutableStateOf(false) }
    var childDropdownExpanded by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val displaySdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    var startDateRaw by remember { mutableStateOf(sdf.format(calendar.time)) }
    var endDateRaw by remember { mutableStateOf(sdf.format(calendar.time)) }
    var isHalfDay by remember { mutableStateOf(false) }
    var halfDayPeriod by remember { mutableStateOf("First Half") }
    var reason by remember { mutableStateOf("") }

    val formattedStartDate = remember(startDateRaw) {
        try {
            val date = sdf.parse(startDateRaw)
            if (date != null) displaySdf.format(date) else startDateRaw
        } catch (e: Exception) {
            startDateRaw
        }
    }

    val formattedEndDate = remember(endDateRaw) {
        try {
            val date = sdf.parse(endDateRaw)
            if (date != null) displaySdf.format(date) else endDateRaw
        } catch (e: Exception) {
            endDateRaw
        }
    }

    // Validation
    val isFormValid = remember(role, selectedChild, reason) {
        if (isGuardian && selectedChild == null) {
            false
        } else {
            reason.trim().isNotEmpty()
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
                        text = if (isHindi) "छुट्टी के लिए आवेदन" else "Apply Leave",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Applicant Section
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, borderColor, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "आवेदक विवरण (Applicant Details)" else "Applicant Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (isGuardian) {
                        // Child Dropdown Selection
                        Text(
                            text = if (isHindi) "बच्चे का चयन करें" else "Select Child",
                            fontSize = 12.sp,
                            color = subtitleColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedChild?.name ?: (if (isHindi) "बच्चा चुनें" else "Select Child"),
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().clickable { childDropdownExpanded = true },
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = textColor,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Lucide.ChevronDown,
                                        contentDescription = null,
                                        tint = textColor
                                    )
                                }
                            )
                            DropdownMenu(
                                expanded = childDropdownExpanded,
                                onDismissRequest = { childDropdownExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                state.guardianStudents.forEach { child ->
                                    DropdownMenuItem(
                                        text = { Text(child.name) },
                                        onClick = {
                                            selectedChild = child
                                            childDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // Read only user name display
                        Text(
                            text = if (isHindi) "आवेदक का नाम" else "Applicant Name",
                            fontSize = 12.sp,
                            color = subtitleColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = applicantName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isHindi) "भूमिका (Role): $role" else "Role: $role",
                            fontSize = 14.sp,
                            color = subtitleColor
                        )
                    }
                }
            }

            // 2. Leave Config Section
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, borderColor, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "अवकाश विवरण (Leave Details)" else "Leave Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Leave Type Dropdown
                    Text(
                        text = if (isHindi) "अवकाश प्रकार" else "Leave Type",
                        fontSize = 12.sp,
                        color = subtitleColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = when (leaveType) {
                                "Sick Leave" -> if (isHindi) "बीमारी की छुट्टी (Sick Leave)" else "Sick Leave"
                                "Casual Leave" -> if (isHindi) "सामान्य अवकाश (Casual Leave)" else "Casual Leave"
                                "Short Leave" -> if (isHindi) "छोटी छुट्टी (Short Leave)" else "Short Leave"
                                else -> if (isHindi) "अन्य (Other)" else "Other"
                            },
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().clickable { leaveTypeExpanded = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = textColor,
                                disabledBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            trailingIcon = {
                                Icon(
                                    imageVector = Lucide.ChevronDown,
                                    contentDescription = null,
                                    tint = textColor
                                )
                            }
                        )
                        DropdownMenu(
                            expanded = leaveTypeExpanded,
                            onDismissRequest = { leaveTypeExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            listOf("Sick Leave", "Casual Leave", "Short Leave", "Other").forEach { type ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (type) {
                                                "Sick Leave" -> if (isHindi) "बीमारी की छुट्टी (Sick Leave)" else "Sick Leave"
                                                "Casual Leave" -> if (isHindi) "सामान्य अवकाश (Casual Leave)" else "Casual Leave"
                                                "Short Leave" -> if (isHindi) "छोटी छुट्टी (Short Leave)" else "Short Leave"
                                                else -> if (isHindi) "अन्य (Other)" else "Other"
                                            }
                                        )
                                    },
                                    onClick = {
                                        leaveType = type
                                        leaveTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date Selectors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "प्रारंभ तिथि" else "Start Date",
                                fontSize = 12.sp,
                                color = subtitleColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                                    .clickable {
                                        val cur = sdf.parse(startDateRaw) ?: Date()
                                        calendar.time = cur
                                        DatePickerDialog(
                                            context,
                                            { _, y, m, d ->
                                                val c = Calendar.getInstance()
                                                c.set(y, m, d)
                                                startDateRaw = sdf.format(c.time)
                                                // Automatically adjust end date if needed
                                                if (c.time.after(sdf.parse(endDateRaw))) {
                                                    endDateRaw = startDateRaw
                                                }
                                            },
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH),
                                            calendar.get(Calendar.DAY_OF_MONTH)
                                        ).show()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.Calendar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = formattedStartDate, color = textColor, fontSize = 14.sp)
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "समाप्ति तिथि" else "End Date",
                                fontSize = 12.sp,
                                color = subtitleColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                                    .clickable {
                                        val cur = sdf.parse(endDateRaw) ?: Date()
                                        calendar.time = cur
                                        DatePickerDialog(
                                            context,
                                            { _, y, m, d ->
                                                val c = Calendar.getInstance()
                                                c.set(y, m, d)
                                                // Prevent setting end date before start date
                                                if (c.time.after(sdf.parse(startDateRaw)) || sdf.format(c.time) == startDateRaw) {
                                                    endDateRaw = sdf.format(c.time)
                                                } else {
                                                    Toast.makeText(context, if (isHindi) "समाप्ति तिथि प्रारंभ तिथि से पहले नहीं हो सकती" else "End date cannot be before start date", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH),
                                            calendar.get(Calendar.DAY_OF_MONTH)
                                        ).show()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.Calendar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = formattedEndDate, color = textColor, fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Half Day Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isHindi) "हाफ डे (Half Day)" else "Half Day",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor
                            )
                            Text(
                                text = if (isHindi) "आधे दिन की छुट्टी के लिए" else "For half-day request",
                                fontSize = 12.sp,
                                color = subtitleColor
                            )
                        }
                        Switch(
                            checked = isHalfDay,
                            onCheckedChange = { isHalfDay = it }
                        )
                    }

                    if (isHalfDay) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isHindi) "अवधि चुनें" else "Select Period",
                            fontSize = 12.sp,
                            color = subtitleColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = halfDayPeriod == "First Half",
                                onClick = { halfDayPeriod = "First Half" },
                                label = { Text(if (isHindi) "प्रथम अर्धभाग (First Half)" else "First Half") }
                            )
                            FilterChip(
                                selected = halfDayPeriod == "Second Half",
                                onClick = { halfDayPeriod = "Second Half" },
                                label = { Text(if (isHindi) "द्वितीय अर्धभाग (Second Half)" else "Second Half") }
                            )
                        }
                    }
                }
            }

            // 3. Reason Section
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, borderColor, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "छुट्टी का कारण (Reason for Leave)" else "Reason for Leave",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        placeholder = {
                            Text(
                                text = if (isHindi) "कृपया छुट्टी लेने का ठोस कारण लिखें..." else "Enter detailed reason here...",
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Submit Button
            Button(
                onClick = {
                    val activeSession = state.activeSessionId
                    val applicantType = if (isStudent || isGuardian) "student" else "staff"
                    val staffId = if (!isStudent && !isGuardian) workspace?.id else null
                    val studentId = if (isGuardian) selectedChild?.id else if (isStudent) workspace?.studentId else null
                    val childOrg = if (isGuardian) selectedChild?.classId else workspace?.childOrgId

                    viewModel.onEvent(
                        InstitutionEvent.SubmitLeaveRequest(
                            parentOrgId = workspace?.parentOrgId ?: "",
                            orgId = childOrg,
                            sessionId = activeSession,
                            applicantType = applicantType,
                            staffId = staffId,
                            studentId = studentId,
                            leaveType = leaveType,
                            startDate = startDateRaw,
                            endDate = endDateRaw,
                            isHalfDay = isHalfDay,
                            halfDayPeriod = if (isHalfDay) halfDayPeriod else null,
                            reason = reason.trim(),
                            createdBy = userId
                        )
                    )
                    Toast.makeText(context, if (isHindi) "छुट्टी का आवेदन सफलतापूर्वक भेजा गया।" else "Leave application submitted successfully.", Toast.LENGTH_LONG).show()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = isFormValid
            ) {
                Text(
                    text = if (isHindi) "आवेदन सबमिट करें" else "Submit Application",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
