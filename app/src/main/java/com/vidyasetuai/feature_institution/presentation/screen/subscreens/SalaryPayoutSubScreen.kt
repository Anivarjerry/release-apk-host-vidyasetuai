package com.vidyasetuai.feature_feed.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState

@Composable
fun SalaryPayoutSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    onPaymentClick: (com.vidyasetuai.feature_institution.domain.model.StaffSalaryPayment) -> Unit,
    onBack: () -> Unit
) {
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Salary overview card
            Surface(
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, borderVal),
                color = cardBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = if (isHindi) "मासिक वेतन" else "Monthly Salary", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "₹${state.monthlySalary}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(borderVal))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = if (isHindi) "कुल भुगतान (सत्र)" else "Total Paid (Session)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "₹${state.totalSalaryPaid}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                    }
                }
            }

            Text(
                text = if (isHindi) "भुगतान इतिहास (Transactions):" else "Payout Transactions:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Payments List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.salaryPayments.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isHindi) "अभी तक कोई वेतन भुगतान दर्ज नहीं हुआ है।" else "No salary payments recorded yet.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(state.salaryPayments) { payment ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, borderVal),
                            color = cardBg,
                            modifier = Modifier.fillMaxWidth().clickable { onPaymentClick(payment) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isHindi) "वेतन भुगतान" else "Salary Payment",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = payment.paymentDate,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹${payment.amountPaid}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = AppColors.EmeraldGreen
                                        )
                                        Text(
                                            text = payment.paymentMode,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(imageVector = Lucide.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SalaryReceiptDialog(
    payment: com.vidyasetuai.feature_institution.domain.model.StaffSalaryPayment,
    isHindi: Boolean,
    isDark: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isDark) Color(0xFF1C1C1E) else Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)),
            modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                // Header (Receipt title)
                Icon(
                    imageVector = Lucide.Check,
                    contentDescription = null,
                    tint = AppColors.EmeraldGreen,
                    modifier = Modifier.size(48.dp).background(AppColors.EmeraldGreen.copy(alpha = 0.1f), CircleShape).padding(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isHindi) "वेतन भुगतान रसीद" else "Salary Payment Receipt",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Details Grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReceiptDetailRow(
                        label = if (isHindi) "भुगतान तिथि" else "Payment Date",
                        value = payment.paymentDate,
                        isDark = isDark
                    )
                    ReceiptDetailRow(
                        label = if (isHindi) "भुगतान राशि" else "Amount Paid",
                        value = "₹${payment.amountPaid}",
                        valueColor = AppColors.EmeraldGreen,
                        isDark = isDark
                    )
                    ReceiptDetailRow(
                        label = if (isHindi) "भुगतान माध्यम" else "Payment Mode",
                        value = payment.paymentMode,
                        isDark = isDark
                    )
                    if (!payment.transactionId.isNullOrBlank()) {
                        ReceiptDetailRow(
                            label = if (isHindi) "ट्रांजैक्शन आईडी" else "Transaction ID",
                            value = payment.transactionId,
                            isDark = isDark
                        )
                    }
                    if (!payment.paymentApp.isNullOrBlank()) {
                        ReceiptDetailRow(
                            label = if (isHindi) "भुगतान ऐप" else "Payment App",
                            value = payment.paymentApp,
                            isDark = isDark
                        )
                    }
                    if (!payment.remarks.isNullOrBlank()) {
                        ReceiptDetailRow(
                            label = if (isHindi) "विवरण / रिमार्क्स" else "Remarks",
                            value = payment.remarks,
                            isDark = isDark
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.EmeraldGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth().height(38.dp)
                ) {
                    Text(text = if (isHindi) "बंद करें" else "Close", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun ReceiptDetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified,
    isDark: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (valueColor != Color.Unspecified) valueColor else MaterialTheme.colorScheme.onBackground
        )
    }
}
