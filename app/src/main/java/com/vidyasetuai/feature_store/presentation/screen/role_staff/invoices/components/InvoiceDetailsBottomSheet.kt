package com.vidyasetuai.feature_store.presentation.screen.role_staff.invoices.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessEntity
import com.vidyasetuai.feature_store.domain.model.InvoiceWithDetailsUiModel
import com.vidyasetuai.feature_store.presentation.screen.role_staff.invoices.PrintFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailsBottomSheet(
    invoiceWithDetails: InvoiceWithDetailsUiModel,
    business: BusinessEntity?,
    branch: BusinessBranchEntity?,
    isHindi: Boolean = false,
    printMode: PrintFormat = PrintFormat.THERMAL,
    onPrintModeChange: (PrintFormat) -> Unit,
    onCancelInvoicePrompt: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val invoice = invoiceWithDetails.invoice
    val items = invoiceWithDetails.items

    val isCancelled = invoiceWithDetails.isCancelled

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // --- 1. HEADER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.FileText,
                                contentDescription = "Invoice",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Invoice #${invoice.invoiceNumber}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = invoice.invoiceDate.take(19).replace("T", " • "),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

            // --- 2. SCROLLABLE BODY ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Status Banner
                val badgeColor = when {
                    isCancelled -> Color(0xFF9CA3AF)
                    invoiceWithDetails.isPaid -> Color(0xFF16A34A)
                    invoiceWithDetails.isCreditKhata -> Color(0xFFD97706)
                    else -> Color(0xFFDC2626)
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = badgeColor.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(badgeColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCancelled) Lucide.Ban else if (invoiceWithDetails.isPaid) Lucide.Check else Lucide.Clock,
                                    contentDescription = null,
                                    tint = badgeColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isCancelled) (if (isHindi) "इनवॉइस रद्द (Cancelled)" else "Invoice Cancelled")
                                    else "${if (isHindi) "भुगतान माध्यम" else "Payment"}: ${invoice.paymentStatus}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor
                                )
                                Text(
                                    text = "${invoiceWithDetails.displayCustomerName} ${invoiceWithDetails.customerPhone?.let { "($it)" } ?: ""}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "₹${"%.2f".format(invoice.grandTotal)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Customer Details Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isHindi) "ग्राहक विवरण (Customer Details)" else "CUSTOMER DETAILS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = invoiceWithDetails.displayCustomerName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            invoiceWithDetails.customerPhone?.let { phone ->
                                Text(
                                    text = phone,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        invoiceWithDetails.partyGstin?.let { gstin ->
                            Text(
                                text = "GSTIN: $gstin",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Itemized Items Table
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isHindi) "आइटम" else "ITEM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(2f)
                            )
                            Text(
                                text = if (isHindi) "मात्रा" else "QTY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(0.8f)
                            )
                            Text(
                                text = if (isHindi) "दर" else "RATE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (isHindi) "कुल" else "TOTAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1.2f)
                            )
                        }

                        if (items.isEmpty()) {
                            Text(
                                text = if (isHindi) "कोई आइटम नहीं मिला" else "No items listed",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(14.dp)
                            )
                        } else {
                            items.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(2f)) {
                                        Text(
                                            text = item.itemName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (!item.hsnSacCode.isNullOrBlank()) {
                                            Text(
                                                text = "HSN: ${item.hsnSacCode}",
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${item.quantity} ${item.unit}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(0.8f)
                                    )
                                    Text(
                                        text = "₹${"%.2f".format(item.unitRate)}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "₹${"%.2f".format(item.quantity * item.unitRate)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1.2f)
                                    )
                                }
                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                            }
                        }
                    }
                }

                // Financial Summary Breakdown Table
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SummaryRow(label = if (isHindi) "टैक्सेबल राशि (Taxable Amount):" else "Taxable Amount:", value = "₹${"%.2f".format(invoice.taxableAmount)}")
                    if (invoice.cgstAmount > 0) {
                        SummaryRow(label = "CGST:", value = "₹${"%.2f".format(invoice.cgstAmount)}")
                    }
                    if (invoice.sgstAmount > 0) {
                        SummaryRow(label = "SGST:", value = "₹${"%.2f".format(invoice.sgstAmount)}")
                    }
                    if (invoice.igstAmount > 0) {
                        SummaryRow(label = "IGST:", value = "₹${"%.2f".format(invoice.igstAmount)}")
                    }
                    if (invoice.deliveryCharge > 0) {
                        SummaryRow(label = if (isHindi) "डिलीवरी शुल्क:" else "Delivery Charge:", value = "₹${"%.2f".format(invoice.deliveryCharge)}")
                    }
                    if (invoice.packingCharge > 0) {
                        SummaryRow(label = if (isHindi) "पैकिंग शुल्क:" else "Packing Charge:", value = "₹${"%.2f".format(invoice.packingCharge)}")
                    }
                    if (invoice.discountTotal > 0) {
                        SummaryRow(label = if (isHindi) "कुल छूट (Discount):" else "Discount Total:", value = "-₹${"%.2f".format(invoice.discountTotal)}", color = Color(0xFF16A34A))
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "कुल देय राशि (Grand Total):" else "Grand Total:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "₹${"%.2f".format(invoice.grandTotal)}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = AppColors.EmeraldGreen
                        )
                    }

                    if (invoice.dueAmount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isHindi) "बाकी / उधार (Due Amount):" else "Due Amount:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD97706)
                            )
                            Text(
                                text = "₹${"%.2f".format(invoice.dueAmount)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                }

                // Dual Print Format Switcher (1:1 Web Parity)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = printMode == PrintFormat.THERMAL,
                        onClick = { onPrintModeChange(PrintFormat.THERMAL) },
                        label = { Text(if (isHindi) "🖨️ थर्मल स्लिप (2\"/3\")" else "🖨️ Thermal Slip (2\"/3\")") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    FilterChip(
                        selected = printMode == PrintFormat.A4,
                        onClick = { onPrintModeChange(PrintFormat.A4) },
                        label = { Text(if (isHindi) "📄 A4 GST इनवॉइस" else "📄 A4 GST Invoice") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

            // --- 3. BOTTOM ACTIONS BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!isCancelled) {
                    OutlinedButton(
                        onClick = onCancelInvoicePrompt,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Ban,
                            contentDescription = "Cancel",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "बिल रद्द करें" else "Cancel Bill",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = {
                        val shareText = buildReceiptShareText(invoiceWithDetails, business, branch)
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share Invoice #${invoice.invoiceNumber}")
                        context.startActivity(shareIntent)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(46.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Printer,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "शेयर व प्रिंट रसीद" else "Share & Print Receipt",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace, color = color)
    }
}

private fun buildReceiptShareText(
    inv: InvoiceWithDetailsUiModel,
    biz: BusinessEntity?,
    branch: BusinessBranchEntity?
): String {
    val b = StringBuilder()
    b.appendLine("🧾 *${biz?.tradeName ?: "VidyaSetu Commerce"}*")
    branch?.branchName?.let { b.appendLine("📍 $it") }
    biz?.gstin?.let { b.appendLine("GSTIN: $it") }
    b.appendLine("--------------------------------")
    b.appendLine("Invoice: #${inv.invoice.invoiceNumber}")
    b.appendLine("Date: ${inv.invoice.invoiceDate.take(16).replace("T", " ")}")
    b.appendLine("Customer: ${inv.displayCustomerName}")
    b.appendLine("Payment: ${inv.invoice.paymentStatus}")
    b.appendLine("--------------------------------")
    inv.items.forEach { item ->
        b.appendLine("${item.itemName} x${item.quantity} = ₹${"%.2f".format(item.quantity * item.unitRate)}")
    }
    b.appendLine("--------------------------------")
    b.appendLine("*Grand Total: ₹${"%.2f".format(inv.invoice.grandTotal)}*")
    if (inv.invoice.dueAmount > 0) {
        b.appendLine("Due / Khata: ₹${"%.2f".format(inv.invoice.dueAmount)}")
    }
    b.appendLine("Thank you for your business! 🙏")
    return b.toString()
}
