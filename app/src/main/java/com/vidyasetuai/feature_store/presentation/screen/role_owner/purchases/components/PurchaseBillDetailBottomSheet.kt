package com.vidyasetuai.feature_store.presentation.screen.role_owner.purchases.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.vidyasetuai.feature_store.domain.model.PurchaseBillUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseBillDetailBottomSheet(
    isHindi: Boolean = false,
    purchase: PurchaseBillUiModel,
    onDismiss: () -> Unit
) {
    BackHandler(enabled = true) { onDismiss() }

    val statusColor = when {
        purchase.isPaid -> Color(0xFF10B981)
        purchase.isPartial -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Lucide.FileText, contentDescription = "Bill", modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(
                            text = "Purchase Bill #${purchase.supplierInvoiceNumber}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Date: ${purchase.invoiceDate.take(10)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Supplier Contact Card
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "सप्लायर विवरण (Supplier Details)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = purchase.supplierName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                if (!purchase.supplierPhone.isNullOrBlank()) {
                                    Text(text = purchase.supplierPhone, fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                // 2. Financial Breakdown Box
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "वित्तीय हिसाब (Financial Breakdown)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "कर योग्य मूल्य (Taxable Amount):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "₹${"%,.2f".format(purchase.taxableAmount)}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "CGST Input Tax Credit (ITC):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "₹${"%,.2f".format(purchase.cgstAmount)}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF10B981))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "SGST Input Tax Credit (ITC):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "₹${"%,.2f".format(purchase.sgstAmount)}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF10B981))
                            }

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "कुल खरीद बिल (Grand Total):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = "₹${"%,.2f".format(purchase.grandTotal)}", fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "अदा की गई राशि (Paid):", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                Text(text = "₹${"%,.2f".format(purchase.paidAmount)}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                            }

                            if (purchase.dueAmount > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "सप्लायर बकाया (Due Amount):", fontSize = 11.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                                    Text(text = "₹${"%,.2f".format(purchase.dueAmount)}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 3. Notes if available
                if (!purchase.notes.isNullOrBlank()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "टिप्पणी (Notes):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = purchase.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text("बंद करें (Close)", fontWeight = FontWeight.Bold)
            }
        }
    }
}
