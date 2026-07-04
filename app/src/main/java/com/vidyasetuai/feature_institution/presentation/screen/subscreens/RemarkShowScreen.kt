package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@Composable
fun RemarkShowScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onNavigateToAddRemark: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val activeWorkspace = state.activeWorkspace
    val role = activeWorkspace?.role ?: ""

    // Trigger LoadRemarks when screen opens
    LaunchedEffect(activeWorkspace?.parentOrgId) {
        val active = state.activeWorkspace
        if (active != null) {
            viewModel.onEvent(InstitutionEvent.LoadRemarks(
                sessionId = state.activeSessionId,
                parentOrgId = active.parentOrgId
            ))
        }
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBg = if (isDark) Color(0xFF1E1E20) else Color.White
    val cardBorderColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val subtitleColor = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    // ── Filter Remarks ──
    val filteredRemarks = when (role) {
        "Guardian" -> {
            val childIds = state.guardianStudents.map { it.id }
            val guardianId = activeWorkspace?.guardianId ?: ""
            state.remarks.filter { remark ->
                val targets = state.remarkTargetsMap[remark.id] ?: emptyList()
                targets.any { target ->
                    (target.targetStudentId != null && target.targetStudentId in childIds) ||
                    (target.targetGuardianId != null && target.targetGuardianId == guardianId)
                }
            }
        }
        "Student" -> {
            val studentId = activeWorkspace?.studentId ?: ""
            state.remarks.filter { remark ->
                val targets = state.remarkTargetsMap[remark.id] ?: emptyList()
                targets.any { target ->
                    target.targetStudentId != null && target.targetStudentId == studentId
                }
            }
        }
        else -> {
            // Staff / Principal / Teacher: own remarks, targeted staff remarks, or public ones
            val staffId = activeWorkspace?.id ?: ""
            state.remarks.filter { remark ->
                val targets = state.remarkTargetsMap[remark.id] ?: emptyList()
                remark.creatorUserId == state.userId ||
                remark.visibilityType == "Public" ||
                targets.any { target ->
                    target.targetStaffId != null && target.targetStaffId == staffId
                }
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
                        text = if (isHindi) "टिप्पणियाँ" else "Remarks",
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
        floatingActionButton = {
            // Only Staff can write new remarks
            val isStaff = role != "Guardian" && role != "Student"
            if (isStaff) {
                FloatingActionButton(
                    onClick = onNavigateToAddRemark,
                    containerColor = AppColors.EmeraldGreen,
                    contentColor = Color.White
                ) {
                    Icon(imageVector = Lucide.Plus, contentDescription = "Add Remark")
                }
            }
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            if (filteredRemarks.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Icon(
                        imageVector = Lucide.MessageSquareOff,
                        contentDescription = null,
                        tint = subtitleColor,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isHindi) "कोई टिप्पणी नहीं मिली" else "No Remarks Found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHindi) 
                            "वर्तमान में कोई विशेष टिप्पणी या समीक्षा उपलब्ध नहीं है।" 
                        else 
                            "Currently there are no special remarks or reviews posted.",
                        fontSize = 13.sp,
                        color = subtitleColor,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredRemarks) { remark ->
                        // Priority Badge colors
                        val (priorityText, priorityBg, priorityTextCol) = when (remark.priority) {
                            "Critical" -> Triple(
                                if (isHindi) "महत्वपूर्ण!" else "Critical",
                                if (isDark) Color(0xFF3E1F1F) else Color(0xFFFFEBEE),
                                if (isDark) Color(0xFFE57373) else Color(0xFFC62828)
                            )
                            "High" -> Triple(
                                if (isHindi) "उच्च" else "High",
                                if (isDark) Color(0xFF3E2D1F) else Color(0xFFFFF3E0),
                                if (isDark) Color(0xFFFFB74D) else Color(0xFFE65100)
                            )
                            "Medium" -> Triple(
                                if (isHindi) "मध्यम" else "Medium",
                                if (isDark) Color(0xFF1E2D3E) else Color(0xFFE3F2FD),
                                if (isDark) Color(0xFF64B5F6) else Color(0xFF1565C0)
                            )
                            else -> Triple(
                                if (isHindi) "सामान्य" else "Low",
                                if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7),
                                if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)
                            )
                        }

                        // Targets child name for Guardian
                        val childNames = if (role == "Guardian") {
                            val targets = state.remarkTargetsMap[remark.id] ?: emptyList()
                            val childIds = state.guardianStudents.map { it.id }
                            targets.filter { it.targetStudentId in childIds }
                                .mapNotNull { t -> state.guardianStudents.find { it.id == t.targetStudentId }?.name }
                                .joinToString(", ")
                        } else null

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = cardBorderColor.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = remark.category,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = priorityBg
                                    ) {
                                        Text(
                                            text = priorityText,
                                            color = priorityTextCol,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                
                                Text(
                                    text = remark.content,
                                    fontSize = 14.sp,
                                    color = textColor,
                                    lineHeight = 20.sp
                                )

                                if (!childNames.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${if (isHindi) "बच्चा: " else "Child: "} $childNames",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.EmeraldGreen
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = cardBorderColor.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${if (isHindi) "द्वारा: " else "By: "} ${remark.createdBy ?: (if (isHindi) "संस्थान" else "Institution")}",
                                        fontSize = 11.sp,
                                        color = subtitleColor
                                    )
                                    Text(
                                        text = remark.createdAt.split("T").firstOrNull() ?: "",
                                        fontSize = 11.sp,
                                        color = subtitleColor
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
