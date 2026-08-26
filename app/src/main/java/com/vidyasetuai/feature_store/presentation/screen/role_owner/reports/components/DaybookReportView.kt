package com.vidyasetuai.feature_store.presentation.screen.role_owner.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.domain.model.DaybookUiModel

@Composable
fun DaybookReportView(
    isHindi: Boolean = false,
    daybook: DaybookUiModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Primary Highlight Banner: Net Cash in Hand (Cash Drawer)
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Wallet,
                                contentDescription = "Cash",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (isHindi) "दुकान का गल्ला (Net Cash in Hand)" else "NET CASH IN HAND (दुकान का गल्ला)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = Lucide.Check, contentDescription = "Reconciled", tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                            Text(
                                text = "Reconciled",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                Text(
                    text = "₹${"%,.2f".format(daybook.netCashDrawer)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF10B981)
                )

                Text(
                    text = "Cash Sales (₹${"%.2f".format(daybook.cashSales)}) + Khata Collection (₹${"%.2f".format(daybook.paymentsReceivedCash)})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. Collection Breakdown Grid (3 Cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Card 1: POS Cash Sales
            DaybookCollectionCard(
                title = "POS Cash Sales",
                amount = daybook.cashSales,
                subtitle = "Direct Cash Collection at Counter",
                icon = Lucide.Banknote,
                color = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )

            // Card 2: UPI QR Collections
            DaybookCollectionCard(
                title = "UPI QR Collections",
                amount = daybook.upiSales,
                subtitle = "Bank Direct Settlement via UPI",
                icon = Lucide.QrCode,
                color = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f)
            )

            // Card 3: Khata Credit Sales
            DaybookCollectionCard(
                title = "Khata Credit Sales",
                amount = daybook.khataSales,
                subtitle = "Customer Udhar Bills",
                icon = Lucide.BookOpen,
                color = Color(0xFFF43F5E),
                modifier = Modifier.weight(1f)
            )
        }

        // 3. Inflow & Outflow Detail Table
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "DAYBOOK TRANSACTION INFLOW & OUTFLOW SUMMARY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // 1. Khata Cash Received
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Lucide.ArrowUpRight, contentDescription = "Cash Inflow", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                        Text(
                            text = if (isHindi) "उधारी वसूली नकद (Khata Cash Payments Received)" else "Khata Cash Payments Received (उधारी वसूली नकद)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "+₹${"%,.2f".format(daybook.paymentsReceivedCash)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF10B981)
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                // 2. Khata UPI Received
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Lucide.ArrowUpRight, contentDescription = "UPI Inflow", tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                        Text(
                            text = if (isHindi) "उधारी वसूली UPI (Khata UPI Payments Received)" else "Khata UPI Payments Received (उधारी वसूली UPI)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "+₹${"%,.2f".format(daybook.paymentsReceivedUpi)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF3B82F6)
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                // 3. Supplier Cash Paid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Lucide.ArrowDownRight, contentDescription = "Outflow", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        Text(
                            text = if (isHindi) "सप्लायर भुगतान (Supplier Cash Payments)" else "Supplier Cash Payments (सप्लायर भुगतान)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "-₹${"%,.2f".format(daybook.supplierPaidCash)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFEF4444)
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                // 4. Store Cash Expenses
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Lucide.ArrowDownRight, contentDescription = "Expense Outflow", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        Text(
                            text = if (isHindi) "दुकान नकद खर्चे (Store Petty Cash Expenses)" else "Store Cash Expenses (दुकान नकद खर्चे)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "-₹${"%,.2f".format(daybook.expensesPaidCash)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFEF4444)
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                // 5. Staff Cash Salary / Advances
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Lucide.ArrowDownRight, contentDescription = "Salary Outflow", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        Text(
                            text = if (isHindi) "स्टाफ वेतन भुगतान (Staff Salary Paid in Cash)" else "Staff Cash Salary (स्टाफ वेतन भुगतान)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "-₹${"%,.2f".format(daybook.salaryPaidCash)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}

@Composable
private fun DaybookCollectionCard(
    title: String,
    amount: Double,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(12.dp))
                }
            }

            Text(
                text = "₹${"%,.2f".format(amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = if (color == Color(0xFFF43F5E)) color else MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 10.sp
            )
        }
    }
}
