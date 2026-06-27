package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.domain.model.Remark
import com.vidyasetuai.feature_institution.domain.model.RemarkTarget
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemarkShowScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onNavigateToAddRemark: () -> Unit,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedPriority by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "Academic", "Behaviour", "Attendance", "Achievement",
        "Fee", "Medical", "Transport", "Discipline", "Counseling", "General"
    )
    val priorities = listOf("Low", "Medium", "High", "Critical")

    val role = state.activeWorkspace?.role ?: ""
    val isGuardian = role.equals("Guardian", ignoreCase = true)
    val isDriver = role.equals("Driver", ignoreCase = true)
    val isStudent = role.equals("Student", ignoreCase = true)

    val filteredRemarks = state.remarks.filter { remark ->
        val matchCategory = selectedCategory == null || remark.category == selectedCategory
        val matchPriority = selectedPriority == null || remark.priority == selectedPriority
        if (!matchCategory || !matchPriority) return@filter false

        val targets = state.remarkTargetsMap[remark.id] ?: emptyList()
        when {
            isGuardian -> {
                val hasGuardianAudience = remark.visibilityAudience.any { it.equals("Guardian", ignoreCase = true) }
                if (!hasGuardianAudience) return@filter false

                val childIds = state.guardianStudents.map { it.id }
                targets.any { target ->
                    target.targetStudentId in childIds ||
                    target.targetUserId == state.userId ||
                    remark.creatorUserId == state.userId
                }
            }
            isDriver -> {
                val assignedStudentIds = state.assignedStudents.map { it.id }
                targets.any { target ->
                    target.targetStudentId in assignedStudentIds ||
                    target.targetUserId == state.userId ||
                    remark.creatorUserId == state.userId
                }
            }
            isStudent -> {
                val hasStudentAudience = remark.visibilityAudience.any { it.equals("Student", ignoreCase = true) }
                if (!hasStudentAudience) return@filter false

                targets.any { target ->
                    target.targetStudentId == state.activeWorkspace?.childOrgId ||
                    target.targetUserId == state.userId
                }
            }
            else -> {
                true
            }
        }
    }

    LaunchedEffect(state.activeSessionId, state.activeWorkspace?.parentOrgId) {
        val parentOrgId = state.activeWorkspace?.parentOrgId
        if (parentOrgId != null) {
            viewModel.onEvent(
                InstitutionEvent.LoadRemarks(
                    sessionId = state.activeSessionId,
                    parentOrgId = parentOrgId
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) MaterialTheme.colorScheme.background else Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Padding to compensate for missing top bar
            Spacer(modifier = Modifier.height(16.dp))

            // Filtering Row (Left Category Dropdown, Right Priority Dropdown)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category Filter Dropdown
                var categoryMenuExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { categoryMenuExpanded = true },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isDark) Color(0xFF1C1C1E) else Color.White,
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val catText = if (selectedCategory == null) {
                                if (isHindi) "सभी श्रेणियां" else "All Categories"
                            } else {
                                if (isHindi) {
                                    when (selectedCategory) {
                                        "Academic" -> "शैक्षणिक"
                                        "Behaviour" -> "व्यवहार"
                                        "Attendance" -> "उपस्थिति"
                                        "Achievement" -> "उपलब्धि"
                                        "Fee" -> "शुल्क"
                                        "Medical" -> "चिकित्सा"
                                        "Transport" -> "परिवहन"
                                        "Discipline" -> "अनुशासन"
                                        "Counseling" -> "परामर्श"
                                        else -> "सामान्य"
                                    }
                                } else selectedCategory!!
                            }
                            Text(text = catText, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
                            Icon(
                                imageVector = Lucide.ChevronDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = categoryMenuExpanded,
                        onDismissRequest = { categoryMenuExpanded = false },
                        modifier = Modifier.background(if (isDark) Color(0xFF1C1C1E) else Color.White)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = selectedCategory == null,
                                        onCheckedChange = null,
                                        colors = CheckboxDefaults.colors(checkedColor = AppColors.EmeraldGreen)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = if (isHindi) "सभी" else "All", fontSize = 13.sp)
                                }
                            },
                            onClick = {
                                selectedCategory = null
                                categoryMenuExpanded = false
                            }
                        )
                        categories.forEach { category ->
                            val catDisplay = if (isHindi) {
                                when (category) {
                                    "Academic" -> "शैक्षणिक"
                                    "Behaviour" -> "व्यवहार"
                                    "Attendance" -> "उपस्थिति"
                                    "Achievement" -> "उपलब्धि"
                                    "Fee" -> "शुल्क"
                                    "Medical" -> "चिकित्सा"
                                    "Transport" -> "परिवहन"
                                    "Discipline" -> "अनुशासन"
                                    "Counseling" -> "परामर्श"
                                    else -> "सामान्य"
                                }
                            } else category

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = selectedCategory == category,
                                            onCheckedChange = null,
                                            colors = CheckboxDefaults.colors(checkedColor = AppColors.EmeraldGreen)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = catDisplay, fontSize = 13.sp)
                                    }
                                },
                                onClick = {
                                    selectedCategory = category
                                    categoryMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Priority Filter Dropdown
                var priorityMenuExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { priorityMenuExpanded = true },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isDark) Color(0xFF1C1C1E) else Color.White,
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val prioText = if (selectedPriority == null) {
                                if (isHindi) "सभी प्राथमिकता" else "All Priorities"
                            } else {
                                if (isHindi) {
                                    when (selectedPriority) {
                                        "Low" -> "निम्न"
                                        "Medium" -> "मध्यम"
                                        "High" -> "उच्च"
                                        "Critical" -> "गंभीर"
                                        else -> selectedPriority!!
                                    }
                                } else selectedPriority!!
                            }
                            Text(text = prioText, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
                            Icon(
                                imageVector = Lucide.ChevronDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = priorityMenuExpanded,
                        onDismissRequest = { priorityMenuExpanded = false },
                        modifier = Modifier.background(if (isDark) Color(0xFF1C1C1E) else Color.White)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = selectedPriority == null,
                                        onCheckedChange = null,
                                        colors = CheckboxDefaults.colors(checkedColor = AppColors.EmeraldGreen)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = if (isHindi) "सभी" else "All", fontSize = 13.sp)
                                }
                            },
                            onClick = {
                                selectedPriority = null
                                priorityMenuExpanded = false
                            }
                        )
                        priorities.forEach { priority ->
                            val prioDisplay = if (isHindi) {
                                when (priority) {
                                    "Low" -> "निम्न"
                                    "Medium" -> "मध्यम"
                                    "High" -> "उच्च"
                                    "Critical" -> "गंभीर"
                                    else -> priority
                                }
                            } else priority

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = selectedPriority == priority,
                                            onCheckedChange = null,
                                            colors = CheckboxDefaults.colors(checkedColor = AppColors.EmeraldGreen)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = prioDisplay, fontSize = 13.sp)
                                    }
                                },
                                onClick = {
                                    selectedPriority = priority
                                    priorityMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Sync Status/Button Row below filtering dropdowns (Cloud Icon)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.isSyncingLogs) {
                        if (isHindi) "सिंक हो रहा है..." else "Syncing..."
                    } else {
                        if (isHindi) "क्लाउड से सिंक करें" else "Sync with Cloud"
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = { viewModel.onEvent(InstitutionEvent.SyncRemarks) },
                    enabled = !state.isSyncingLogs,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Cloud,
                        contentDescription = "Sync",
                        tint = if (state.isSyncingLogs) MaterialTheme.colorScheme.onSurfaceVariant else AppColors.EmeraldGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredRemarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Lucide.FileText,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isHindi) "कोई रिमार्क नहीं मिला" else "No remarks found.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredRemarks) { remark ->
                        val priorityColor = when (remark.priority) {
                            "Critical" -> Color(0xFFEF4444)
                            "High" -> Color(0xFFF97316)
                            "Medium" -> Color(0xFF3B82F6)
                            else -> Color(0xFF10B981)
                        }

                        val categoryIcon = when (remark.category) {
                            "Academic" -> Lucide.BookOpen
                            "Behaviour" -> Lucide.Smile
                            "Attendance" -> Lucide.Check
                            "Achievement" -> Lucide.Trophy
                            "Fee" -> Lucide.CreditCard
                            "Medical" -> Lucide.Heart
                            "Transport" -> Lucide.Bus
                            "Discipline" -> Lucide.ShieldAlert
                            "Counseling" -> Lucide.UserCheck
                            else -> Lucide.MessageSquare
                        }

                        val targets = state.remarkTargetsMap[remark.id] ?: emptyList()
                        val targetText = if (targets.isNotEmpty()) {
                            targets.joinToString(", ") { target ->
                                when {
                                    target.targetStudentId != null -> {
                                        val studentName = state.offlineStudents.find { it.id == target.targetStudentId }?.name
                                            ?: state.guardianStudents.find { it.id == target.targetStudentId }?.name
                                            ?: state.assignedStudents.find { it.id == target.targetStudentId }?.name
                                            ?: (if (isHindi) "छात्र" else "Student")
                                        studentName
                                    }
                                    target.targetGuardianId != null -> if (isHindi) "अभिभावक" else "Guardian"
                                    target.targetStaffId != null -> {
                                        val staffName = state.offlineStaff.find { it.id == target.targetStaffId }?.name
                                            ?: (if (isHindi) "स्टाफ" else "Staff")
                                        staffName
                                    }
                                    else -> if (isHindi) "स्वयं" else "Self"
                                }
                            }
                        } else {
                            if (isHindi) "अनिर्दिष्ट" else "Unspecified"
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDark) Color(0xFF1C1C1E) else Color.White,
                            border = BorderStroke(1.dp, if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(priorityColor.copy(alpha = 0.08f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = categoryIcon,
                                                contentDescription = null,
                                                tint = priorityColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = remark.category,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            Text(
                                                text = "${if (isHindi) "लक्षित: " else "Target: "} $targetText",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }
                                    }

                                    Text(
                                        text = when (remark.priority) {
                                            "Critical" -> if (isHindi) "गंभीर" else "Critical"
                                            "High" -> if (isHindi) "उच्च" else "High"
                                            "Medium" -> if (isHindi) "मध्यम" else "Medium"
                                            else -> if (isHindi) "निम्न" else "Low"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = priorityColor
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = remark.content,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val dateStr = remark.createdAt.substringBefore("T")
                                    Text(
                                        text = dateStr,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (remark.syncStatus == "Offline_Pending") {
                                            Icon(
                                                imageVector = Lucide.CloudOff,
                                                contentDescription = "Offline Pending",
                                                tint = Color(0xFFF59E0B),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                        }

                                        val workspace = state.activeWorkspace
                                        val canDelete = workspace != null && (
                                            workspace.role in listOf("Admin", "System Administrator", "School Administrator", "Org Admin", "Principal") ||
                                            remark.creatorUserId == workspace.id
                                        )

                                        if (canDelete) {
                                            Icon(
                                                imageVector = Lucide.Trash,
                                                contentDescription = "Delete",
                                                tint = Color(0xFFEF4444).copy(alpha = 0.7f),
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clickable { viewModel.onEvent(InstitutionEvent.DeleteRemark(remark.id)) }
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

        // Add Remark Floating Action Button (Pure White luxury style with 1.dp thin outline)
        val canAdd = role != "Student"
        if (canAdd) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .clickable { onNavigateToAddRemark() },
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF1C1C1E) else Color.White,
                border = BorderStroke(1.dp, if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)),
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Plus,
                        contentDescription = "Add Remark",
                        tint = if (isDark) Color.White else Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
