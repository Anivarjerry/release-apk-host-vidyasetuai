package com.vidyasetuai.feature_feed.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
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
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState

@Composable
fun AdminDashboard(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    onNavigateToStudentDirectory: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToRemarks: () -> Unit
) {
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val activeWorkspace = state.activeWorkspace

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isHindi) "प्रशासक डैशबोर्ड" else "Admin Dashboard",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.EmeraldGreen
        )

        // 1. Parent/Child Organization Context Overview
        activeWorkspace?.let { active ->
            val isParentOrg = active.childOrgId.isNullOrEmpty()

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                color = cardBg
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = AppColors.EmeraldGreen.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isParentOrg) Lucide.Network else Lucide.Building2,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        if (isParentOrg) {
                            Text(
                                text = if (isHindi) "संबद्ध चाइल्ड शाखाएं" else "Linked Branches (Child Orgs)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (isHindi) "${state.childOrganizations.size} शाखाएं" else "${state.childOrganizations.size} Child Schools",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        } else {
                            Text(
                                text = if (isHindi) "संबद्ध मुख्य संगठन (Parent Org)" else "Parent Organization Name",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = active.parentOrgName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        // 2. Finance Stats Overview
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
            color = cardBg
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isHindi) "शुल्क वित्तीय अवलोकन" else "Fee Collections Overview",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = if (isHindi) "कुल संग्रह" else "Total Collected", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = formatAmount(state.adminTotalCollected, isHindi), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                    }
                    Column {
                        Text(text = if (isHindi) "पेंडिंग संग्रह" else "Pending Collection", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = formatAmount(state.adminTotalPending, isHindi), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                    }
                }
            }
        }

        // 3. Remarks Card
        RemarksDashboardCard(
            state = state,
            isHindi = isHindi,
            isDark = isDark,
            borderVal = borderVal,
            cardBg = cardBg,
            onClick = onNavigateToRemarks
        )

        // 4. Student Directory Universal Search Button Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToStudentDirectory() },
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
            color = cardBg
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = AppColors.EmeraldGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Users,
                        contentDescription = null,
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) "छात्र निर्देशिका (सर्च)" else "Student Directory Search",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isHindi) "नाम, मोबाइल, रोल नंबर, फ़ोटो आदि से खोजें" else "Search students by Name, Mobile, Roll, or Face",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Icon(
                    imageVector = Lucide.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // 5. Notice Gallery Feed Card
        NoticeGalleryFeedCard(
            isHindi = isHindi,
            borderVal = borderVal,
            cardBg = cardBg,
            onClick = onNavigateToFeed
        )
    }
}

private fun formatAmount(amount: Double, isHindi: Boolean): String {
    return if (amount >= 100000) {
        val lakhs = amount / 100000.0
        if (isHindi) {
            String.format("₹%.1f लाख", lakhs)
        } else {
            String.format("₹%.1f Lakhs", lakhs)
        }
    } else {
        String.format("₹%,.0f", amount)
    }
}
