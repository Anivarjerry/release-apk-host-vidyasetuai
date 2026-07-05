package com.vidyasetuai.feature_institution.presentation.screen.subscreens

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_institution.data.local.entity.LocalParentStaffEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity
import com.vidyasetuai.feature_institution.domain.model.InstitutionStudent
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRemarksFabSubScreen(
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
    val borderColor = if (isDark) Color(0xFF2C2C2E) else Color.White
    val borderStrokeColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val subtitleColor = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    val workspace = state.activeWorkspace
    val role = workspace?.role ?: ""
    
    val isGuardian = role.equals("Guardian", ignoreCase = true)
    val isStudent = role.equals("Student", ignoreCase = true)
    val isAdmin = role in listOf("Admin", "System Administrator", "School Administrator", "Org Admin", "Principal", "Director", "Owner")

    // Determine allowed target types
    val allowedTargetTypes = remember(role, isAdmin, isGuardian, isStudent) {
        when {
            isGuardian -> listOf("Student")
            isStudent -> listOf("Self")
            isAdmin -> listOf("Student", "Staff", "Self")
            else -> listOf("Student", "Self") // normal staff/teachers
        }
    }

    var selectedTargetType by remember { mutableStateOf(allowedTargetTypes.first()) }

    // ── Target Selections ──
    var selectedChild by remember { mutableStateOf(state.guardianStudents.firstOrNull()) }
    var selectedStudent by remember { mutableStateOf<LocalStudentEntity?>(null) }
    var selectedStaff by remember { mutableStateOf<LocalParentStaffEntity?>(null) }

    // ── Autocomplete / Search States ──
    var studentSearchQuery by remember { mutableStateOf("") }
    var studentDropdownExpanded by remember { mutableStateOf(false) }

    var staffSearchQuery by remember { mutableStateOf("") }
    var staffDropdownExpanded by remember { mutableStateOf(false) }

    var childDropdownExpanded by remember { mutableStateOf(false) }

    // ── Dropdown Choices ──
    var category by remember { mutableStateOf("General") }
    var categoryExpanded by remember { mutableStateOf(false) }

    var priority by remember { mutableStateOf("Medium") }
    var priorityExpanded by remember { mutableStateOf(false) }

    var visibilityType by remember { mutableStateOf("Public") }
    var visibilityExpanded by remember { mutableStateOf(false) }
    val selectedAudience = remember { mutableStateListOf<String>() }

    var content by remember { mutableStateOf("") }

    // Autocomplete filter logic
    val filteredStudentsList = remember(studentSearchQuery, state.offlineStudents) {
        if (studentSearchQuery.isBlank()) {
            emptyList()
        } else {
            state.offlineStudents.filter {
                it.name.contains(studentSearchQuery, ignoreCase = true) ||
                (it.srNumber ?: "").contains(studentSearchQuery, ignoreCase = true)
            }.take(5)
        }
    }

    val filteredStaffList = remember(staffSearchQuery, state.offlineStaff) {
        if (staffSearchQuery.isBlank()) {
            emptyList()
        } else {
            state.offlineStaff.filter {
                it.name.contains(staffSearchQuery, ignoreCase = true) ||
                (it.roleName ?: "").contains(staffSearchQuery, ignoreCase = true)
            }.take(5)
        }
    }

    // Validation
    val isFormValid = remember(selectedTargetType, selectedChild, selectedStudent, selectedStaff, content) {
        val targetOk = when (selectedTargetType) {
            "Student" -> if (isGuardian) selectedChild != null else selectedStudent != null
            "Staff" -> selectedStaff != null
            "Self" -> true
            else -> false
        }
        targetOk && content.trim().isNotEmpty()
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
                        text = if (isHindi) "टिप्पणी जोड़ें" else "Add Remarks",
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
            // 1. Target Type Segmented Control (if multiple allowed)
            if (allowedTargetTypes.size > 1) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, borderStrokeColor, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isHindi) "टिप्पणी का लक्ष्य (Remark Target)" else "Remark Target Type",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            allowedTargetTypes.forEach { type ->
                                FilterChip(
                                    selected = selectedTargetType == type,
                                    onClick = {
                                        selectedTargetType = type
                                        // Reset other selections
                                        selectedStudent = null
                                        selectedStaff = null
                                    },
                                    label = {
                                        Text(
                                            when (type) {
                                                "Student" -> if (isHindi) "छात्र (Student)" else "Student"
                                                "Staff" -> if (isHindi) "स्टाफ (Staff)" else "Staff"
                                                else -> if (isHindi) "स्वयं (Self)" else "Self"
                                            }
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Specific Target Selection Details Card
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, borderStrokeColor, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "लक्ष्य विवरण (Target Details)" else "Target Selection",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    when (selectedTargetType) {
                        "Student" -> {
                            if (isGuardian) {
                                // Guardian selects from child list dropdown
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
                                            Icon(imageVector = Lucide.ChevronDown, contentDescription = null, tint = textColor)
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
                                // Search student autocomplete
                                if (selectedStudent == null) {
                                    OutlinedTextField(
                                        value = studentSearchQuery,
                                        onValueChange = {
                                            studentSearchQuery = it
                                            studentDropdownExpanded = it.isNotEmpty()
                                        },
                                        label = { Text(if (isHindi) "छात्र का नाम या SR नंबर खोजें" else "Search student name or SR...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            Icon(imageVector = Lucide.Search, contentDescription = null, tint = subtitleColor)
                                        }
                                    )

                                    if (studentDropdownExpanded && filteredStudentsList.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().border(0.5.dp, borderStrokeColor, RoundedCornerShape(8.dp))
                                        ) {
                                            Column {
                                                filteredStudentsList.forEach { student ->
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                selectedStudent = student
                                                                studentSearchQuery = ""
                                                                studentDropdownExpanded = false
                                                            }
                                                            .padding(12.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(imageVector = Lucide.User, contentDescription = null, tint = subtitleColor)
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Column {
                                                            Text(student.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor)
                                                            Text("Class: ${student.className ?: ""} - SR: ${student.srNumber}", fontSize = 12.sp, color = subtitleColor)
                                                        }
                                                    }
                                                    Divider(color = borderStrokeColor, thickness = 0.5.dp)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Selected Student Display Card
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Lucide.User, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(selectedStudent!!.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textColor)
                                                Text("Class: ${selectedStudent!!.className ?: ""} - Roll No: ${selectedStudent!!.rollNumber ?: "N/A"}", fontSize = 12.sp, color = subtitleColor)
                                            }
                                        }
                                        IconButton(onClick = { selectedStudent = null }) {
                                            Icon(imageVector = Lucide.X, contentDescription = "Clear selection", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                        "Staff" -> {
                            // Search staff autocomplete
                            if (selectedStaff == null) {
                                OutlinedTextField(
                                    value = staffSearchQuery,
                                    onValueChange = {
                                        staffSearchQuery = it
                                        staffDropdownExpanded = it.isNotEmpty()
                                    },
                                    label = { Text(if (isHindi) "स्टाफ सदस्य का नाम खोजें" else "Search staff name...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        Icon(imageVector = Lucide.Search, contentDescription = null, tint = subtitleColor)
                                    }
                                )

                                if (staffDropdownExpanded && filteredStaffList.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().border(0.5.dp, borderStrokeColor, RoundedCornerShape(8.dp))
                                    ) {
                                        Column {
                                            filteredStaffList.forEach { staff ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedStaff = staff
                                                            staffSearchQuery = ""
                                                            staffDropdownExpanded = false
                                                        }
                                                        .padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(imageVector = Lucide.UserCheck, contentDescription = null, tint = subtitleColor)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text(staff.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor)
                                                        Text("Role: ${staff.roleName ?: "N/A"}", fontSize = 12.sp, color = subtitleColor)
                                                    }
                                                }
                                                Divider(color = borderStrokeColor, thickness = 0.5.dp)
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Selected Staff Display Card
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Lucide.UserCheck, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(selectedStaff!!.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textColor)
                                            Text("Role: ${selectedStaff!!.roleName ?: "N/A"}", fontSize = 12.sp, color = subtitleColor)
                                        }
                                    }
                                    IconButton(onClick = { selectedStaff = null }) {
                                        Icon(imageVector = Lucide.X, contentDescription = "Clear selection", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                        "Self" -> {
                            // Showing self details read only
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Lucide.User, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isHindi) "टिप्पणी स्वयं पर लगाई जाएगी" else "Remark will be added for yourself",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColor
                                    )
                                    Text(
                                        text = "Role: $role",
                                        fontSize = 13.sp,
                                        color = subtitleColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Remark Attributes Card (Category, Priority, Visibility)
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, borderStrokeColor, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = if (isHindi) "श्रेणी और प्राथमिकता (Category & Priority)" else "Remark Settings",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // A. Category Dropdown
                    Column {
                        Text(text = if (isHindi) "श्रेणी (Category)" else "Category", fontSize = 12.sp, color = subtitleColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = when (category) {
                                    "Academic" -> if (isHindi) "शैक्षणिक (Academic)" else "Academic"
                                    "Discipline" -> if (isHindi) "अनुशासन (Discipline)" else "Discipline"
                                    "Attendance" -> if (isHindi) "उपस्थिति (Attendance)" else "Attendance"
                                    else -> if (isHindi) "सामान्य (General)" else "General"
                                },
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().clickable { categoryExpanded = true },
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = textColor,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                trailingIcon = {
                                    Icon(imageVector = Lucide.ChevronDown, contentDescription = null, tint = textColor)
                                }
                            )
                            DropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                listOf("Academic", "Discipline", "Attendance", "General").forEach { cat ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                when (cat) {
                                                    "Academic" -> if (isHindi) "शैक्षणिक (Academic)" else "Academic"
                                                    "Discipline" -> if (isHindi) "अनुशासन (Discipline)" else "Discipline"
                                                    "Attendance" -> if (isHindi) "उपस्थिति (Attendance)" else "Attendance"
                                                    else -> if (isHindi) "सामान्य (General)" else "General"
                                                }
                                            )
                                        },
                                        onClick = {
                                            category = cat
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // B. Priority Dropdown
                    Column {
                        Text(text = if (isHindi) "प्राथमिकता (Priority)" else "Priority", fontSize = 12.sp, color = subtitleColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = when (priority) {
                                    "Low" -> if (isHindi) "कम (Low)" else "Low"
                                    "High" -> if (isHindi) "उच्च (High)" else "High"
                                    "Critical" -> if (isHindi) "महत्वपूर्ण (Critical)" else "Critical"
                                    else -> if (isHindi) "मध्यम (Medium)" else "Medium"
                                },
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().clickable { priorityExpanded = true },
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = textColor,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                trailingIcon = {
                                    Icon(imageVector = Lucide.ChevronDown, contentDescription = null, tint = textColor)
                                }
                            )
                            DropdownMenu(
                                expanded = priorityExpanded,
                                onDismissRequest = { priorityExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                listOf("Low", "Medium", "High", "Critical").forEach { prio ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                when (prio) {
                                                    "Low" -> if (isHindi) "कम (Low)" else "Low"
                                                    "High" -> if (isHindi) "उच्च (High)" else "High"
                                                    "Critical" -> if (isHindi) "महत्वपूर्ण (Critical)" else "Critical"
                                                    else -> if (isHindi) "मध्यम (Medium)" else "Medium"
                                                }
                                            )
                                        },
                                        onClick = {
                                            priority = prio
                                            priorityExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // C. Visibility Dropdown
                    Column {
                        Text(text = if (isHindi) "दृश्यता (Visibility)" else "Visibility", fontSize = 12.sp, color = subtitleColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = when (visibilityType) {
                                    "Private" -> if (isHindi) "निजी (Private)" else "Private"
                                    "Internal" -> if (isHindi) "आंतरिक (Internal)" else "Internal"
                                    else -> if (isHindi) "सार्वजनिक (Public)" else "Public"
                                },
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().clickable { visibilityExpanded = true },
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = textColor,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                trailingIcon = {
                                    Icon(imageVector = Lucide.ChevronDown, contentDescription = null, tint = textColor)
                                }
                            )
                            DropdownMenu(
                                expanded = visibilityExpanded,
                                onDismissRequest = { visibilityExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                listOf("Public", "Private", "Internal").forEach { vis ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                when (vis) {
                                                    "Private" -> if (isHindi) "निजी (Private)" else "Private"
                                                    "Internal" -> if (isHindi) "आंतरिक (Internal)" else "Internal"
                                                    else -> if (isHindi) "सार्वजनिक (Public)" else "Public"
                                                }
                                            )
                                        },
                                        onClick = {
                                            visibilityType = vis
                                            visibilityExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (visibilityType == "Private" || visibilityType == "Internal") {
                        Column {
                            Text(
                                text = if (isHindi) "दर्शक समूह (Visibility Audience)" else "Visibility Audience",
                                fontSize = 12.sp,
                                color = subtitleColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val rolesOptions = listOf(
                                Pair("Teacher", if (isHindi) "शिक्षक (Teacher)" else "Teacher"),
                                Pair("Guardian", if (isHindi) "अभिभावक (Guardian)" else "Guardian"),
                                Pair("Principal", if (isHindi) "प्रधानाचार्य (Principal)" else "Principal"),
                                Pair("Admin", if (isHindi) "व्यवस्थापक (Admin)" else "Admin")
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rolesOptions.forEach { (roleKey, roleLabel) ->
                                    val isSelected = selectedAudience.contains(roleKey)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            if (isSelected) {
                                                selectedAudience.remove(roleKey)
                                            } else {
                                                selectedAudience.add(roleKey)
                                            }
                                        },
                                        label = { Text(text = roleLabel, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Content Text Input Card
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, borderStrokeColor, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "टिप्पणी विवरण (Remark Details)" else "Remark Description",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        placeholder = {
                            Text(
                                text = if (isHindi) "टिप्पणी का विवरण यहाँ लिखें..." else "Write remark details here...",
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
                    val targetStudentId = when (selectedTargetType) {
                        "Student" -> if (isGuardian) selectedChild?.id else selectedStudent?.id
                        else -> null
                    }
                    val targetGuardianId = when (selectedTargetType) {
                        "Student" -> if (isGuardian) workspace?.guardianId else selectedStudent?.guardianId
                        else -> null
                    }
                    val targetStaffId = when (selectedTargetType) {
                        "Staff" -> selectedStaff?.id
                        else -> null
                    }

                    viewModel.onEvent(
                        InstitutionEvent.SubmitRemark(
                            content = content.trim(),
                            category = category,
                            priority = priority,
                            visibilityType = visibilityType,
                            visibilityAudience = selectedAudience.toList(),
                            targetType = selectedTargetType,
                            targetStudentId = targetStudentId,
                            targetGuardianId = targetGuardianId,
                            targetStaffId = targetStaffId
                        )
                    )
                    Toast.makeText(context, if (isHindi) "टिप्पणी सफलतापूर्वक दर्ज की गई।" else "Remark submitted successfully.", Toast.LENGTH_LONG).show()
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
                    text = if (isHindi) "टिप्पणी सबमिट करें" else "Submit Remark",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
