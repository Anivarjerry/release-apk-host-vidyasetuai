package com.vidyasetuai.feature_institution.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState

@Composable
fun DashboardSalaryOverviewCard(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    onViewSalaryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDark) AppColors.CharcoalGray else AppColors.PureWhite
    val borderClr = if (isDark) Color(0xFF262626) else Color(0xFFE5E5E5)
    val textColor = if (isDark) AppColors.PureWhite else Color(0xFF171717)
    val subtitleColor = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)
    val primaryColor = AppColors.EmeraldGreen

    val currentStaffId = state.activeWorkspace?.staffId ?: ""
    val userId = state.userId

    val myOverview = remember(state.salaryOverviews, currentStaffId, userId) {
        state.salaryOverviews.firstOrNull { 
            (currentStaffId.isNotEmpty() && it.staff.id == currentStaffId) ||
            (userId.isNotEmpty() && it.staff.id == userId)
        } ?: state.salaryOverviews.firstOrNull()
    }

    val baseSalary = myOverview?.baseMonthlySalary ?: state.monthlySalary
    val totalPaid = myOverview?.totalAmountPaid ?: state.totalSalaryPaid
    val dueBalance = myOverview?.dueBalance ?: (baseSalary - totalPaid).let { if (it < 0) 0.0 else it }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderClr, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Coins,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isHindi) "सैलरी सारांश 💰" else "Salary Overview 💰",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = if (isHindi) "सभी सत्र (All Sessions)" else "All Sessions",
                            fontSize = 11.sp,
                            color = subtitleColor
                        )
                    }
                }

                Row(
                    modifier = Modifier.clickable { onViewSalaryClick() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isHindi) "विवरण >" else "View Details >",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
            }

            Divider(color = borderClr, thickness = 0.5.dp)

            // Metrics Grid (Base Salary | Total Paid | Pending Due)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Base Salary
                Surface(
                    modifier = Modifier.weight(1f),
                    color = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = if (isHindi) "मूल वेतन" else "Base Salary",
                            fontSize = 10.sp,
                            color = subtitleColor,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "₹${baseSalary.toInt()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }

                // Total Paid
                Surface(
                    modifier = Modifier.weight(1f),
                    color = primaryColor.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = if (isHindi) "कुल प्राप्त" else "Total Paid",
                            fontSize = 10.sp,
                            color = subtitleColor,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "₹${totalPaid.toInt()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }

                // Pending Balance
                Surface(
                    modifier = Modifier.weight(1f),
                    color = if (dueBalance > 0) Color(0xFFFEF2F2) else Color(0xFFECFDF5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = if (isHindi) "शेष बकाया" else "Pending Due",
                            fontSize = 10.sp,
                            color = subtitleColor,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "₹${dueBalance.toInt()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (dueBalance > 0) Color(0xFFDC2626) else primaryColor
                        )
                    }
                }
            }

            // Recent Payment Snippet
            val lastPayment = myOverview?.payments?.firstOrNull()
            if (lastPayment != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Lucide.Check, contentDescription = null, tint = primaryColor, modifier = Modifier.size(14.dp))
                        Text(
                            text = "${if (isHindi) "हालिया भुगतान: " else "Recent Payout: "}₹${lastPayment.amountPaid.toInt()} (${lastPayment.paymentMode})",
                            fontSize = 11.sp,
                            color = textColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = lastPayment.paymentDate,
                        fontSize = 10.sp,
                        color = subtitleColor
                    )
                }
            }
        }
    }
}
