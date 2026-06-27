package com.vidyasetuai.feature_feed.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors

import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState

@Composable
fun StaffLeaveSummaryCard(
    leaveQuota: Double,
    remainingLeaves: Double,
    isHindi: Boolean,
    isDark: Boolean,
    borderVal: Color,
    cardBg: Color,
    onClick: (() -> Unit)? = null
) {
    val taken = maxOf(0.0, leaveQuota - remainingLeaves)
    val modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Surface(
        modifier = Modifier.fillMaxWidth().then(modifier),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
        color = cardBg
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isHindi) "अवकाश विवरण (Leave Summary)" else "Leave Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Column 1: Total
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) "कुल आवंटित" else "Allocated",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$leaveQuota",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                // Divider
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(borderVal).align(Alignment.CenterVertically))
                
                // Column 2: Taken
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) "लिया गया" else "Taken",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$taken",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
                    )
                }

                // Divider
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(borderVal).align(Alignment.CenterVertically))

                // Column 3: Remaining
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) "शेष बकाया" else "Remaining",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$remainingLeaves",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen
                    )
                }
            }
        }
    }
}

@Composable
fun StaffSalaryCard(
    monthlySalary: Double,
    totalPaid: Double,
    isHindi: Boolean,
    isDark: Boolean,
    borderVal: Color,
    cardBg: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
        color = cardBg
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(AppColors.EmeraldGreen.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Check, // Fallback check icon
                        contentDescription = null,
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isHindi) "वेतन विवरण (Salary Dashboard)" else "Salary Dashboard",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isHindi) "मासिक: ₹$monthlySalary • कुल भुगतान: ₹$totalPaid" else "Monthly: ₹$monthlySalary • Total Paid: ₹$totalPaid",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Lucide.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun NoticeGalleryFeedCard(
    isHindi: Boolean,
    borderVal: Color,
    cardBg: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
        color = cardBg
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(AppColors.EmeraldGreen.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Bell,
                        contentDescription = null,
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isHindi) "सूचना और गैलरी" else "Notice & Gallery Feed",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isHindi) "स्कूल के नोटिस और गैलरी फोटो देखें" else "View school notices and gallery photos",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Lucide.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun RemarksDashboardCard(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    borderVal: Color,
    cardBg: Color,
    onClick: () -> Unit
) {
    val role = state.activeWorkspace?.role ?: ""
    val isGuardian = role.equals("Guardian", ignoreCase = true)
    val isDriver = role.equals("Driver", ignoreCase = true)
    val isStudent = role.equals("Student", ignoreCase = true)

    val allowedRemarks = state.remarks.filter { remark ->
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

    val latestAlert = allowedRemarks
        .filter { it.priority in listOf("Critical", "High") }
        .maxByOrNull { it.createdAt } ?: allowedRemarks.maxByOrNull { it.createdAt }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
        color = cardBg
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
                            .size(40.dp)
                            .background(AppColors.EmeraldGreen.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.MessageSquare,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "रिमार्क और अवलोकन" else "Remarks & Observations",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isHindi) "संस्थान द्वारा जारी रिमार्क और फीडबैक" else "Remarks and feedback issued by institution",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Lucide.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            latestAlert?.let { remark ->
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = borderVal)
                Spacer(modifier = Modifier.height(12.dp))
                
                val priorityColor = when (remark.priority) {
                    "Critical" -> Color(0xFFEF4444)
                    "High" -> Color(0xFFF97316)
                    "Medium" -> Color(0xFF3B82F6)
                    else -> Color(0xFF10B981)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = priorityColor.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, priorityColor.copy(alpha = 0.3f))
                        ) {
                            val priorityDisplay = if (isHindi) {
                                when (remark.priority) {
                                    "Critical" -> "गंभीर"
                                    "High" -> "उच्च"
                                    "Medium" -> "मध्यम"
                                    else -> "निम्न"
                                }
                            } else remark.priority
                            Text(
                                text = priorityDisplay,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = priorityColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        val categoryDisplay = if (isHindi) {
                            when (remark.category) {
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
                        } else remark.category
                        Text(
                            text = categoryDisplay,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    val dateStr = remark.createdAt.substringBefore("T")
                    Text(
                        text = dateStr,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = remark.content,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
