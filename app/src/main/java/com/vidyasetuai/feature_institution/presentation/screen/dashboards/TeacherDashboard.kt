package com.vidyasetuai.feature_feed.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@Composable
fun TeacherDashboard(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    userId: String,
    onNavigateToLeave: () -> Unit,
    onNavigateToTakeAttendance: () -> Unit,
    onNavigateToAttendanceHistory: () -> Unit,
    onNavigateToSalary: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToRemarks: () -> Unit
) {
    val scrollState = rememberScrollState()
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isHindi) "शिक्षक डैशबोर्ड" else "Teacher Dashboard",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.EmeraldGreen
        )

        // 1. Remarks & Observations Card
        RemarksDashboardCard(
            state = state,
            isHindi = isHindi,
            isDark = isDark,
            borderVal = borderVal,
            cardBg = cardBg,
            onClick = onNavigateToRemarks
        )

        // 2. Take Attendance Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.onEvent(InstitutionEvent.ClearAttendanceFilters)
                    viewModel.onEvent(InstitutionEvent.LoadAttendanceDropdowns(state.activeWorkspace?.parentOrgId ?: ""))
                    viewModel.onEvent(InstitutionEvent.LoadClassTeacherAssignment(userId, state.activeWorkspace?.parentOrgId ?: "", autoLoadIfMarked = true))
                    onNavigateToTakeAttendance()
                },
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
                            imageVector = Lucide.Check,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "छात्र उपस्थिति दर्ज करें" else "Take Student Attendance",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isHindi) "दैनिक छात्र उपस्थिति और रिपोर्ट सबमिट करें" else "Submit daily student attendance reports",
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

        // 3. Attendance History Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.onEvent(InstitutionEvent.ClearAttendanceFilters)
                    viewModel.onEvent(InstitutionEvent.LoadAttendanceDropdowns(state.activeWorkspace?.parentOrgId ?: ""))
                    viewModel.onEvent(InstitutionEvent.LoadClassTeacherAssignment(userId, state.activeWorkspace?.parentOrgId ?: "", autoLoadIfMarked = false))
                    onNavigateToAttendanceHistory()
                },
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
                            imageVector = Lucide.Calendar,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "उपस्थिति इतिहास देखें" else "View Attendance History",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isHindi) "कक्षावार और तिथि अनुसार दर्ज उपस्थिति रिपोर्ट" else "Class-wise and date-wise marked attendance logs",
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

        // 4. Apply for Leave Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToLeave() },
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
                            imageVector = Lucide.Calendar,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "अवकाश के लिए आवेदन करें" else "Apply for Leave",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isHindi) "नई छुट्टी के लिए अनुरोध भेजें और इतिहास देखें" else "Submit new requests and view history",
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

        // 5. Salary Card
        StaffSalaryCard(
            monthlySalary = state.monthlySalary,
            totalPaid = state.totalSalaryPaid,
            isHindi = isHindi,
            isDark = isDark,
            borderVal = borderVal,
            cardBg = cardBg,
            onClick = onNavigateToSalary
        )

        // 6. Notice & Gallery Card
        NoticeGalleryFeedCard(
            isHindi = isHindi,
            borderVal = borderVal,
            cardBg = cardBg,
            onClick = onNavigateToFeed
        )
    }
}
