package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@Composable
fun SimpleDropdownField(
    label: String,
    selectedValueText: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) { onExpandChange(true) },
                shape = RoundedCornerShape(8.dp),
                color = if (isDark) Color(0xFF1C1C1E) else if (enabled) Color.White else Color(0xFFF2F2F7),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedValueText,
                        fontSize = 13.sp,
                        color = if (enabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Icon(
                        imageVector = Lucide.ChevronDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandChange(false) },
                modifier = Modifier
                    .heightIn(max = 280.dp)
                    .width(280.dp)
                    .background(if (isDark) Color(0xFF1C1C1E) else Color.White),
                content = content
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemarkAddScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("General") }
    var selectedPriority by remember { mutableStateOf("Medium") }
    var targetType by remember { mutableStateOf("Self") }
    var selectedStudentId by remember { mutableStateOf<String?>(null) }
    var selectedStaffId by remember { mutableStateOf<String?>(null) }

    // Visibility audiences
    val audiences = listOf("Only Me", "Student", "Guardian", "Teacher", "Principal", "Administrator")
    var selectedAudiences by remember { mutableStateOf(setOf("Teacher", "Principal")) }

    val categories = listOf(
        "Academic", "Behaviour", "Attendance", "Achievement",
        "Fee", "Medical", "Transport", "Discipline", "Counseling", "General"
    )
    val priorities = listOf("Low", "Medium", "High", "Critical")

    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val currentRole = state.activeWorkspace?.role ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) MaterialTheme.colorScheme.background else Color.White)
    ) {
        // App Bar / Top Header (Minimalist Cancel Arrow Only)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Content Input Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isHindi) "रिमार्क का विवरण" else "Remark Details",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    placeholder = {
                        Text(
                            text = if (isHindi) "यहाँ टिप्पणी लिखें..." else "Write observation details here...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppColors.EmeraldGreen,
                        unfocusedBorderColor = borderVal,
                        containerColor = if (isDark) Color(0xFF1C1C1E) else Color.White
                    )
                )
            }

            // Category & Priority Dropdowns in a single Row (Half-Half)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                var categoryExpanded by remember { mutableStateOf(false) }
                SimpleDropdownField(
                    label = if (isHindi) "श्रेणी (Category)" else "Category",
                    selectedValueText = selectedCategory,
                    expanded = categoryExpanded,
                    onExpandChange = { categoryExpanded = it },
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat, fontSize = 13.sp) },
                            onClick = {
                                selectedCategory = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }

                var priorityExpanded by remember { mutableStateOf(false) }
                SimpleDropdownField(
                    label = if (isHindi) "प्राथमिकता (Priority)" else "Priority",
                    selectedValueText = selectedPriority,
                    expanded = priorityExpanded,
                    onExpandChange = { priorityExpanded = it },
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                ) {
                    priorities.forEach { prio ->
                        DropdownMenuItem(
                            text = { Text(prio, fontSize = 13.sp) },
                            onClick = {
                                selectedPriority = prio
                                priorityExpanded = false
                            }
                        )
                    }
                }
            }

            // Target Type & Target Selector Dropdowns in a single Row (Half-Half)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                var targetTypeExpanded by remember { mutableStateOf(false) }
                val showStaffTarget = currentRole in listOf("Admin", "System Administrator", "School Administrator", "Org Admin", "Principal", "Teacher")
                val targetText = when (targetType) {
                    "Self" -> if (isHindi) "स्वयं" else "Self"
                    "Student" -> if (isHindi) "छात्र" else "Student"
                    "Staff" -> if (isHindi) "स्टाफ" else "Staff"
                    else -> targetType
                }

                SimpleDropdownField(
                    label = if (isHindi) "किसके लिए (Target Type)" else "Target Type",
                    selectedValueText = targetText,
                    expanded = targetTypeExpanded,
                    onExpandChange = { targetTypeExpanded = it },
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                ) {
                    DropdownMenuItem(
                        text = { Text(if (isHindi) "स्वयं" else "Self", fontSize = 13.sp) },
                        onClick = {
                            targetType = "Self"
                            selectedStudentId = null
                            selectedStaffId = null
                            targetTypeExpanded = false
                        }
                    )
                    if (currentRole != "Student") {
                        DropdownMenuItem(
                            text = { Text(if (isHindi) "छात्र" else "Student", fontSize = 13.sp) },
                            onClick = {
                                targetType = "Student"
                                selectedStudentId = null
                                selectedStaffId = null
                                targetTypeExpanded = false
                            }
                        )
                    }
                    if (showStaffTarget) {
                        DropdownMenuItem(
                            text = { Text(if (isHindi) "स्टाफ" else "Staff", fontSize = 13.sp) },
                            onClick = {
                                targetType = "Staff"
                                selectedStudentId = null
                                selectedStaffId = null
                                targetTypeExpanded = false
                            }
                        )
                    }
                }

                // Student / Staff Selector Dropdown (Right Selector)
                val students = remember(currentRole, state.assignedStudents, state.guardianStudents, state.offlineStudents) {
                    when {
                        currentRole == "Driver" -> state.assignedStudents
                        currentRole == "Guardian" -> state.guardianStudents.map {
                            LocalStudentEntity(
                                id = it.id,
                                name = it.name,
                                srNumber = it.id,
                                rollNumber = null,
                                className = it.className,
                                sectionName = null,
                                guardianName = null,
                                guardianMobile = null,
                                imageUrl = null
                            )
                        }
                        currentRole == "Student" -> {
                            val studentId = state.activeWorkspace?.childOrgId ?: ""
                            val studentName = state.activeWorkspace?.childOrgName ?: "Self"
                            if (studentId.isNotEmpty()) {
                                listOf(
                                    LocalStudentEntity(
                                        id = studentId,
                                        name = studentName,
                                        srNumber = studentId,
                                        rollNumber = null,
                                        className = null,
                                        sectionName = null,
                                        guardianName = null,
                                        guardianMobile = null,
                                        imageUrl = null
                                    )
                                )
                            } else {
                                state.offlineStudents
                            }
                        }
                        else -> state.offlineStudents
                    }
                }

                when (targetType) {
                    "Student" -> {
                        var studentExpanded by remember { mutableStateOf(false) }
                        var studentSearchQuery by remember { mutableStateOf("") }
                        
                        val selectedStudentName = students.find { it.id == selectedStudentId }?.name
                            ?: (if (isHindi) "छात्र चुनें" else "Select Student")
                            
                        val filteredStudents = remember(students, studentSearchQuery) {
                            students.filter { it.name.contains(studentSearchQuery, ignoreCase = true) }
                        }
                        
                        SimpleDropdownField(
                            label = if (isHindi) "लक्षित छात्र" else "Target Student",
                            selectedValueText = selectedStudentName,
                            expanded = studentExpanded,
                            onExpandChange = { studentExpanded = it },
                            isDark = isDark,
                            enabled = students.isNotEmpty(),
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = studentSearchQuery,
                                onValueChange = { studentSearchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                placeholder = { Text(if (isHindi) "खोजें..." else "Search...", fontSize = 12.sp) },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                                shape = RoundedCornerShape(8.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = AppColors.EmeraldGreen,
                                    unfocusedBorderColor = borderVal,
                                    containerColor = if (isDark) Color(0xFF1C1C1E) else Color.White
                                )
                            )
                            
                            if (filteredStudents.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text(if (isHindi) "कोई परिणाम नहीं" else "No results", fontSize = 13.sp) },
                                    onClick = {}
                                )
                            } else {
                                filteredStudents.forEach { std ->
                                    DropdownMenuItem(
                                        text = { Text(std.name, fontSize = 13.sp) },
                                        onClick = {
                                            selectedStudentId = std.id
                                            studentExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    "Staff" -> {
                        var staffExpanded by remember { mutableStateOf(false) }
                        var staffSearchQuery by remember { mutableStateOf("") }
                        
                        val selectedStaffName = state.offlineStaff.find { it.id == selectedStaffId }?.name
                            ?: (if (isHindi) "स्टाफ चुनें" else "Select Staff")
                            
                        val filteredStaff = remember(state.offlineStaff, staffSearchQuery) {
                            state.offlineStaff.filter { it.name.contains(staffSearchQuery, ignoreCase = true) }
                        }
                        
                        SimpleDropdownField(
                            label = if (isHindi) "लक्षित स्टाफ" else "Target Staff",
                            selectedValueText = selectedStaffName,
                            expanded = staffExpanded,
                            onExpandChange = { staffExpanded = it },
                            isDark = isDark,
                            enabled = state.offlineStaff.isNotEmpty(),
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = staffSearchQuery,
                                onValueChange = { staffSearchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                placeholder = { Text(if (isHindi) "खोजें..." else "Search...", fontSize = 12.sp) },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                                shape = RoundedCornerShape(8.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = AppColors.EmeraldGreen,
                                    unfocusedBorderColor = borderVal,
                                    containerColor = if (isDark) Color(0xFF1C1C1E) else Color.White
                                )
                            )
                            
                            if (filteredStaff.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text(if (isHindi) "कोई परिणाम नहीं" else "No results", fontSize = 13.sp) },
                                    onClick = {}
                                )
                            } else {
                                filteredStaff.forEach { st ->
                                    DropdownMenuItem(
                                        text = { Text(st.name, fontSize = 13.sp) },
                                        onClick = {
                                            selectedStaffId = st.id
                                            staffExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        // Self or other: show disabled N/A
                        SimpleDropdownField(
                            label = if (isHindi) "लक्ष्य (Target)" else "Target",
                            selectedValueText = "N/A",
                            expanded = false,
                            onExpandChange = {},
                            isDark = isDark,
                            enabled = false,
                            modifier = Modifier.weight(1f)
                        ) {}
                    }
                }
            }

            // Visibility Audience Selector (Checkboxes inside Dropdown to save screen space)
            var visibilityExpanded by remember { mutableStateOf(false) }
            val visibilityText = if (selectedAudiences.isEmpty()) {
                if (isHindi) "कोई नहीं" else "None"
            } else if (selectedAudiences.contains("Only Me")) {
                if (isHindi) "केवल मैं" else "Only Me"
            } else {
                selectedAudiences.joinToString(", ") { audience ->
                    if (isHindi) {
                        when (audience) {
                            "Only Me" -> "केवल मैं"
                            "Student" -> "छात्र"
                            "Guardian" -> "अभिभावक"
                            "Teacher" -> "शिक्षक"
                            "Principal" -> "प्रधानाचार्य"
                            "Administrator" -> "प्रशासक"
                            else -> audience
                        }
                    } else audience
                }
            }

            SimpleDropdownField(
                label = if (isHindi) "कौन देख सकता है (Visibility)" else "Who can view this",
                selectedValueText = visibilityText,
                expanded = visibilityExpanded,
                onExpandChange = { visibilityExpanded = it },
                isDark = isDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                audiences.forEach { audience ->
                    val isChecked = selectedAudiences.contains(audience)
                    val display = if (isHindi) {
                        when (audience) {
                            "Only Me" -> "केवल मैं"
                            "Student" -> "छात्र"
                            "Guardian" -> "अभिभावक"
                            "Teacher" -> "शिक्षक"
                            "Principal" -> "प्रधानाचार्य"
                            "Administrator" -> "प्रशासक"
                            else -> audience
                        }
                    } else audience

                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        selectedAudiences = if (checked == true) {
                                            if (audience == "Only Me") {
                                                setOf("Only Me")
                                            } else {
                                                selectedAudiences - "Only Me" + audience
                                            }
                                        } else {
                                            selectedAudiences - audience
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = AppColors.EmeraldGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = display, fontSize = 13.sp)
                            }
                        },
                        onClick = {
                            selectedAudiences = if (isChecked) {
                                selectedAudiences - audience
                            } else {
                                if (audience == "Only Me") {
                                    setOf("Only Me")
                                } else {
                                    selectedAudiences - "Only Me" + audience
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button (Compact Primary Action Button centered)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val canSubmit = content.isNotBlank() && (
                    targetType == "Self" || 
                    (targetType == "Student" && selectedStudentId != null) ||
                    (targetType == "Staff" && selectedStaffId != null)
                )

                Button(
                    onClick = {
                        val isPrivate = selectedAudiences.contains("Only Me")
                        viewModel.onEvent(
                            InstitutionEvent.SubmitRemark(
                                content = content,
                                category = selectedCategory,
                                priority = selectedPriority,
                                visibilityType = if (isPrivate) "Private" else "Public",
                                visibilityAudience = selectedAudiences.toList(),
                                targetType = targetType,
                                targetStudentId = if (targetType == "Student") selectedStudentId else null,
                                targetGuardianId = null,
                                targetStaffId = if (targetType == "Staff") selectedStaffId else null
                            )
                        )
                        onBack()
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(42.dp),
                    enabled = canSubmit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.EmeraldGreen,
                        contentColor = Color.White,
                        disabledContainerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7),
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(21.dp)
                ) {
                    Text(
                        text = if (isHindi) "रिमार्क सुरक्षित करें" else "Save Remark",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
