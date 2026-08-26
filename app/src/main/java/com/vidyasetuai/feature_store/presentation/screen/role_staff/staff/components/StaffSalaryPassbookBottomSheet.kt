package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffSalaryPaymentEntity
import com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.StaffSalaryRowUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSalaryPassbookBottomSheet(
    isHindi: Boolean = false,
    staffRow: StaffSalaryRowUiModel,
    payments: List<BusinessStaffSalaryPaymentEntity>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isHindi) "सैलरी पासबुक व स्टेटमेंट" else "Salary Passbook & Statement",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${staffRow.staff.name} • ${staffRow.staff.role}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Summary Financial Status Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isHindi) "कुल देय (Net Payable)" else "Net Payable",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${staffRow.netPayable}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isHindi) "भुगतान किया (Paid)" else "Paid",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${staffRow.paidAmount}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isHindi) "बकाया (Balance)" else "Balance Due",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${staffRow.balanceDue}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (staffRow.balanceDue > 0) Color(0xFFEF4444) else Color(0xFF10B981)
                        )
                    }
                }
            }

            // 1-Tap WhatsApp Share Slip Action
            Button(
                onClick = {
                    val slipMessage = buildString {
                        appendLine("🧾 *SALARY SLIP / वेतन पर्ची*")
                        appendLine("━━━━━━━━━━━━━━━━━━━")
                        appendLine("👤 *Employee:* ${staffRow.staff.name} (${staffRow.staff.role})")
                        appendLine("📅 *Present Days:* ${staffRow.presentDays} | *Half Days:* ${staffRow.halfDays} | *Leaves:* ${staffRow.paidLeaves}")
                        appendLine("💰 *Base Salary:* ₹${staffRow.baseSalary}")
                        appendLine("💵 *Net Payable:* ₹${staffRow.netPayable}")
                        appendLine("✅ *Amount Paid:* ₹${staffRow.paidAmount}")
                        if (staffRow.unsettledAdvance > 0) {
                            appendLine("⚠️ *Advances Deducted:* ₹${staffRow.unsettledAdvance}")
                        }
                        appendLine("🔴 *Balance Due:* ₹${staffRow.balanceDue}")
                        appendLine("━━━━━━━━━━━━━━━━━━━")
                        appendLine("Generated securely via VidyaSetu AI Store Management")
                    }

                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, slipMessage)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share Salary Slip")
                    context.startActivity(shareIntent)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Lucide.Share2, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isHindi) "व्हाट्सएप पर सैलरी स्लिप शेयर करें" else "Share Slip on WhatsApp",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = if (isHindi) "लेनदेन इतिहास (Payment History):" else "Payment History Ledger:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Passbook Transactions List
            if (payments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isHindi) "कोई भुगतान इतिहास नहीं मिला।" else "No payment records found.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(payments, key = { it.id }) { item ->
                        val isAdvance = item.paymentType == "ADVANCE"
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isAdvance) Color(0xFFF59E0B).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = if (isAdvance) "ADVANCE" else "SALARY",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isAdvance) Color(0xFFB45309) else Color(0xFF047857),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(
                                            text = item.paymentDate,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "${item.paymentMode}${if (!item.transactionRef.isNullOrBlank()) " • Ref: ${item.transactionRef}" else ""}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (!item.remarks.isNullOrBlank()) {
                                        Text(
                                            text = "Note: ${item.remarks}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = "₹${item.amountPaid}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isAdvance) Color(0xFFF59E0B) else Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
