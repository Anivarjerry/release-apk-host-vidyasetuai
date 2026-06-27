package com.vidyasetuai.feature_feed.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@Composable
fun GuardianDashboard(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    userId: String,
    onNavigateToLeave: () -> Unit,
    onNavigateToFees: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToTransport: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToRemarks: () -> Unit,
    onNavigateToChildProfiles: () -> Unit
) {
    val scrollState = rememberScrollState()
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    val students = state.guardianStudents
    val totalStudents = students.size

    val combinedPaid = students.sumOf { it.paidFee }
    val combinedPending = students.sumOf { it.pendingFee }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Child selection / Summary Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isHindi) "अभिभावक डैशबोर्ड" else "Guardian Dashboard",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.EmeraldGreen
            )
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = AppColors.EmeraldGreen.copy(alpha = 0.1f),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = if (isHindi) "कुल बच्चे: $totalStudents" else "Total Children: $totalStudents",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.EmeraldGreen,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // 1. Combined Fees Card (Clickable to navigate to fees detail sub-screen)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToFees() },
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
            color = cardBg
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "कुल शुल्क सारांश (Combined Fees)" else "Combined Fees Summary",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(
                        imageVector = Lucide.ChevronRight,
                        contentDescription = "Details",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = if (isHindi) "पेंडिंग फीस" else "Pending Fees", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "₹$combinedPending", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                    }
                    Column {
                        Text(text = if (isHindi) "जमा की गई फीस" else "Paid Fees", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "₹$combinedPaid", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                    }
                }
            }
        }

        // 2. Remarks Dashboard Card
        RemarksDashboardCard(
            state = state,
            isHindi = isHindi,
            isDark = isDark,
            borderVal = borderVal,
            cardBg = cardBg,
            onClick = onNavigateToRemarks
        )

        // 3. School Bus Tracker Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToTransport() },
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
                            imageVector = Lucide.Bus,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "स्कूल बस ट्रैकर (Transport)" else "School Bus Tracker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isHindi) "लाइव बस लोकेशन और रूट विवरण" else "Live bus tracking and route details",
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

        // 4. Attendance Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToAttendance() },
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
                            text = if (isHindi) "उपस्थिति (Attendance)" else "Attendance Calendar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isHindi) "दैनिक उपस्थिति रिपोर्ट और मासिक इतिहास" else "Daily attendance report and monthly history",
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

        // 5. Apply for Leave Card (Clickable to navigate to leave sub-screen)
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

        // 6. Child Profiles Card (Home location config)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToChildProfiles() },
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
                            imageVector = Lucide.User,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "बच्चों की प्रोफ़ाइल एवं होम लोकेशन" else "Child Profiles & Home Location",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isHindi) "बच्चों की जानकारी और बस अलर्ट होम लोकेशन सेट करें" else "Manage profiles and setup home location alerts",
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

        NoticeGalleryFeedCard(
            isHindi = isHindi,
            borderVal = borderVal,
            cardBg = cardBg,
            onClick = onNavigateToFeed
        )
    }
}
