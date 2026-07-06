package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeAttendanceFabSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    userId: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = androidx.compose.ui.platform.LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val subtitleColor = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    val cardBgColor = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF9FAFB)
    val borderStrokeColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    // Trigger LoadDropdowns once parentOrgId is available
    val activeWs = state.activeWorkspace
    val parentOrgId = activeWs?.parentOrgId ?: ""
    LaunchedEffect(parentOrgId) {
        if (parentOrgId.isNotEmpty()) {
            viewModel.onEvent(InstitutionEvent.LoadAttendanceDropdowns(parentOrgId))
            
            // Auto-load class teacher assignment if applicable
            viewModel.onEvent(InstitutionEvent.LoadClassTeacherAssignment(userId, parentOrgId, autoLoadIfMarked = true))
        }
    }

    // Trigger Toast on successful submission
    LaunchedEffect(state.attendanceSubmittedSuccess) {
        if (state.attendanceSubmittedSuccess) {
            android.widget.Toast.makeText(
                context,
                if (isHindi) "उपस्थिति सफलतापूर्वक सुरक्षित की गई!" else "Attendance saved successfully!",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        val formattedDate = sdf.format(Date(selectedMillis))
                        viewModel.onEvent(InstitutionEvent.ChangeAttendanceDate(formattedDate))
                    }
                    showDatePicker = false
                }) {
                    Text(if (isHindi) "ठीक है" else "OK", color = AppColors.EmeraldGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
                        text = if (isHindi) "उपस्थिति दर्ज करें" else "Take Attendance",
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
        bottomBar = {
            val canSave = state.studentsForAttendance.isNotEmpty() && (!state.isAttendanceAlreadyMarked || state.isAttendanceEditEnabled)
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = backgroundColor,
                tonalElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, dividerColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    Button(
                        onClick = {
                            val activeWs = state.activeWorkspace
                            if (activeWs != null && state.selectedChildOrg != null) {
                                viewModel.onEvent(
                                    InstitutionEvent.SubmitStudentsAttendance(
                                        orgId = state.selectedChildOrg,
                                        date = state.attendanceDate,
                                        staffUserId = userId,
                                        parentOrgId = activeWs.parentOrgId
                                    )
                                )
                            }
                        },
                        enabled = canSave && !state.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.EmeraldGreen,
                            disabledContainerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isHindi) "उपस्थिति सुरक्षित करें" else "Save Attendance",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (canSave) Color.White else subtitleColor
                            )
                        }
                    }
                }
            }
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        var isFiltersExpanded by remember { mutableStateOf(true) }

        // Automatically collapse filters when student list loads
        LaunchedEffect(state.studentsForAttendance) {
            if (state.studentsForAttendance.isNotEmpty()) {
                isFiltersExpanded = false
            }
        }

        val selectedClassName = state.activeClasses.find { it.id == state.selectedClass }?.name
        val selectedSectionName = state.activeSections.find { it.id == state.selectedSection }?.name

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            // Filters Section (Fixed at top)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isFiltersExpanded && state.studentsForAttendance.isNotEmpty()) {
                    // Collapsed Filter Summary Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isFiltersExpanded = true }
                            .border(0.5.dp, borderStrokeColor, RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Lucide.Calendar,
                                    contentDescription = null,
                                    tint = AppColors.EmeraldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${state.attendanceDate} • ${selectedClassName ?: ""} - ${selectedSectionName ?: ""}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isHindi) "बदलें" else "Change",
                                    fontSize = 12.sp,
                                    color = AppColors.EmeraldGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Lucide.ChevronDown,
                                    contentDescription = "Expand",
                                    tint = AppColors.EmeraldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Full Expanded Filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "फ़िल्टर विकल्प" else "Filter Selection",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = subtitleColor
                        )
                        if (state.studentsForAttendance.isNotEmpty()) {
                            Row(
                                modifier = Modifier.clickable { isFiltersExpanded = false },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isHindi) "समेटें" else "Collapse",
                                    fontSize = 11.sp,
                                    color = subtitleColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Lucide.ChevronUp,
                                    contentDescription = "Collapse",
                                    tint = subtitleColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Date Selector Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, borderStrokeColor, RoundedCornerShape(8.dp))
                            .background(cardBgColor)
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Lucide.Calendar,
                                    contentDescription = null,
                                    tint = AppColors.EmeraldGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "${if (isHindi) "दिनांक: " else "Date: "} ${state.attendanceDate}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                            Icon(
                                imageVector = Lucide.ChevronDown,
                                contentDescription = null,
                                tint = subtitleColor,
                                modifier = Modifier.size(16.dp)
                              )
                        }
                    }

                    // Child Org Selection
                    val childOrgOptions = state.childOrganizations.map { it.id to it.name }
                    val selectedOrgName = state.childOrganizations.find { it.id == state.selectedChildOrg }?.name
                    FilterDropdown(
                        label = if (isHindi) "शाखा / उप-संस्था (Child Organization)" else "Child Organization",
                        selectedValue = selectedOrgName,
                        placeholder = if (isHindi) "शाखा चुनें" else "Select branch organization",
                        options = childOrgOptions,
                        isDark = isDark,
                        onSelect = { id ->
                            viewModel.onEvent(InstitutionEvent.SelectChildOrg(id))
                        }
                    )

                    // Row for Class and Section side by side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val classOptions = state.activeClasses.map { it.id to it.name }
                        Box(modifier = Modifier.weight(1f)) {
                            FilterDropdown(
                                label = if (isHindi) "कक्षा (Class)" else "Class",
                                selectedValue = selectedClassName,
                                placeholder = if (isHindi) "चुनें" else "Select",
                                options = classOptions,
                                isDark = isDark,
                                onSelect = { id ->
                                    viewModel.onEvent(InstitutionEvent.SelectClass(id))
                                }
                            )
                        }

                        val sectionOptions = state.activeSections.map { it.id to it.name }
                        Box(modifier = Modifier.weight(1f)) {
                            FilterDropdown(
                                label = if (isHindi) "सेक्शन (Section)" else "Section",
                                selectedValue = selectedSectionName,
                                placeholder = if (isHindi) "चुनें" else "Select",
                                options = sectionOptions,
                                isDark = isDark,
                                onSelect = { id ->
                                    viewModel.onEvent(InstitutionEvent.SelectSection(id))
                                }
                            )
                        }
                    }

                    // Search Button
                    val isSearchEnabled = state.selectedChildOrg != null && state.selectedClass != null && state.selectedSection != null
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
                        enabled = isSearchEnabled && !state.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.EmeraldGreen.copy(alpha = 0.85f),
                            disabledContainerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Search,
                            contentDescription = null,
                            tint = if (isSearchEnabled) Color.White else subtitleColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "खोजें" else "Search",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSearchEnabled) Color.White else subtitleColor
                        )
                    }
                }
            }

            Divider(color = dividerColor, thickness = 0.5.dp)

            // Results Section
            if (state.studentsForAttendance.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.User,
                            contentDescription = null,
                            tint = subtitleColor.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isHindi) "कक्षा, सेक्शन चुनकर खोजें" else "Select class and section to load students",
                            fontSize = 14.sp,
                            color = subtitleColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    // Already Marked Alert Banner
                    if (state.isAttendanceAlreadyMarked) {
                        val isEditable = state.isAttendanceEditEnabled
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isEditable) AppColors.EmeraldGreen.copy(alpha = 0.08f) else Color(0xFFFF9500).copy(alpha = 0.08f))
                                .border(width = 0.5.dp, color = if (isEditable) AppColors.EmeraldGreen.copy(alpha = 0.2f) else Color(0xFFFF9500).copy(alpha = 0.2f))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isEditable) Lucide.Check else Lucide.Info,
                                        contentDescription = null,
                                        tint = if (isEditable) AppColors.EmeraldGreen else Color(0xFFFF9500),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (isHindi) "उपस्थिति पहले से दर्ज है" else "Attendance already marked",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                        Text(
                                            text = if (isEditable) {
                                                if (isHindi) "आप अभी बदलाव कर सकते हैं" else "Editing mode active"
                                            } else {
                                                if (isHindi) "रिकॉर्ड्स केवल पढ़ने के लिए हैं" else "Records are read-only"
                                            },
                                            fontSize = 11.sp,
                                            color = subtitleColor
                                        )
                                    }
                                }
                                if (!isEditable) {
                                    TextButton(
                                        onClick = { viewModel.onEvent(InstitutionEvent.SetAttendanceEditEnabled(true)) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.textButtonColors(contentColor = AppColors.EmeraldGreen)
                                    ) {
                                        Text(
                                            text = if (isHindi) "सुधारें (Edit)" else "Edit",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Student List Scroll
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.studentsForAttendance, key = { it.studentId }) { student ->
                            StudentAttendanceCard(
                                student = student,
                                isDark = isDark,
                                cardBgColor = cardBgColor,
                                borderStrokeColor = borderStrokeColor,
                                textColor = textColor,
                                subtitleColor = subtitleColor,
                                isHindi = isHindi,
                                enabled = !state.isAttendanceAlreadyMarked || state.isAttendanceEditEnabled,
                                onStatusChange = { newStatus ->
                                    viewModel.onEvent(
                                        InstitutionEvent.UpdateStudentAttendanceStatus(
                                            student.studentId,
                                            newStatus
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterDropdown(
    label: String,
    selectedValue: String?,
    placeholder: String,
    options: List<Pair<String, String>>, // id to display name
    isDark: Boolean,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color(0xFFA3A3A3) else Color(0xFF525252),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA),
                    shape = RoundedCornerShape(8.dp)
                )
                .background(if (isDark) Color(0xFF1E1E20) else Color(0xFFF9FAFB))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedValue ?: placeholder,
                    color = if (selectedValue != null) {
                        if (isDark) Color.White else Color.Black
                    } else {
                        if (isDark) Color(0xFF737373) else Color(0xFFA3A3A3)
                    },
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Lucide.ChevronDown,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFF737373) else Color(0xFFA3A3A3),
                    modifier = Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(if (isDark) Color(0xFF1E1E20) else Color.White)
            ) {
                options.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(name, fontSize = 14.sp) },
                        onClick = {
                            onSelect(id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StudentAttendanceCard(
    student: com.vidyasetuai.feature_institution.domain.model.StudentAttendanceInfo,
    isDark: Boolean,
    cardBgColor: Color,
    borderStrokeColor: Color,
    textColor: Color,
    subtitleColor: Color,
    isHindi: Boolean,
    enabled: Boolean,
    onStatusChange: (String) -> Unit
) {
    // Force default to On Leave if approved leave is present
    LaunchedEffect(student.isLeaveApproved) {
        if (student.isLeaveApproved && student.status != "On Leave") {
            onStatusChange("On Leave")
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, borderStrokeColor, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Student Info Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SR: ${student.srNumber ?: "N/A"}",
                        fontSize = 12.sp,
                        color = subtitleColor
                    )
                }

                // Leave Badge Indicators
                if (student.isLeaveApproved) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AppColors.EmeraldGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isHindi) "स्वीकृत छुट्टी" else "Leave Approved",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.EmeraldGreen
                        )
                    }
                } else if (student.isLeavePending) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFF9500).copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isHindi) "अवकाश लंबित" else "Leave Pending",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9500)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Selector Chip Group (P, A, L)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AttendanceSelectorChip(
                    text = if (isHindi) "उपस्थित" else "Present",
                    shortText = "P",
                    selected = student.status == "Present",
                    activeColor = AppColors.EmeraldGreen,
                    isDark = isDark,
                    enabled = enabled && !student.isLeaveApproved,
                    onClick = { onStatusChange("Present") }
                )
                AttendanceSelectorChip(
                    text = if (isHindi) "अनुपस्थित" else "Absent",
                    shortText = "A",
                    selected = student.status == "Absent",
                    activeColor = Color(0xFFEF4444),
                    isDark = isDark,
                    enabled = enabled && !student.isLeaveApproved,
                    onClick = { onStatusChange("Absent") }
                )
                AttendanceSelectorChip(
                    text = if (isHindi) "छुट्टी" else "Leave",
                    shortText = "L",
                    selected = student.status == "On Leave" || student.isLeaveApproved,
                    activeColor = Color(0xFFFF9500),
                    isDark = isDark,
                    enabled = enabled,
                    onClick = { onStatusChange("On Leave") }
                )
            }
        }
    }
}

@Composable
fun AttendanceSelectorChip(
    text: String,
    shortText: String,
    selected: Boolean,
    activeColor: Color,
    isDark: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (selected) {
        activeColor.copy(alpha = 0.2f)
    } else {
        if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA).copy(alpha = 0.5f)
    }

    val contentColor = if (selected) {
        activeColor
    } else {
        if (isDark) Color(0xFF8E8E93) else Color(0xFF737373)
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(enabled = enabled) { onClick() }
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = if (selected) activeColor else Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = shortText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}
