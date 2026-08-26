package com.vidyasetuai.feature_store.presentation.screen.role_staff.parties.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ArrowDownLeft
import com.composables.icons.lucide.ArrowUpRight
import com.composables.icons.lucide.Banknote
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.QrCode
import com.composables.icons.lucide.X
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentBottomSheet(
    isHindi: Boolean = false,
    party: PartyEntity,
    isSubmitting: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onRecord: (
        partyId: String,
        txnType: String,
        amount: Double,
        paymentMode: String,
        refNo: String?,
        notes: String?
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Default TxnType: If Customer -> PAYMENT_IN (Got money), If Supplier -> PAYMENT_OUT (Paid money)
    var txnType by remember(party) {
        mutableStateOf(if (party.partyType == "SUPPLIER") "PAYMENT_OUT" else "PAYMENT_IN")
    }

    var amountStr by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("CASH") }
    var refNo by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val currentBal = party.currentBalance

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
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isHindi) "भुगतान प्रविष्टि (Record Payment)" else "Record Payment Settlement",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${party.name} • ${party.phone}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Current Balance Badge Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = when {
                    currentBal > 0 -> Color(0xFFFEE2E2)
                    currentBal < 0 -> Color(0xFFDCFCE7)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when {
                        currentBal > 0 -> Color(0xFFEF4444).copy(alpha = 0.4f)
                        currentBal < 0 -> Color(0xFF10B981).copy(alpha = 0.4f)
                        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            currentBal > 0 -> if (isHindi) "वर्तमान बकाया (Pending Due):" else "Current Pending Balance:"
                            currentBal < 0 -> if (isHindi) "एडवांस जमा (Advance Paid):" else "Advance Balance:"
                            else -> if (isHindi) "खाता चुकता (Settled):" else "Balance Settled:"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "₹${String.format("%.2f", Math.abs(currentBal))}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = when {
                            currentBal > 0 -> Color(0xFFDC2626)
                            currentBal < 0 -> Color(0xFF166534)
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Error Banner if any
            if (!errorMessage.isNullOrBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEE2E2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                ) {
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = Color(0xFFB91C1C),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 1. Transaction Type Toggle: PAYMENT_IN vs PAYMENT_OUT
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Payment In (Got Money)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { txnType = "PAYMENT_IN" },
                    shape = RoundedCornerShape(10.dp),
                    color = if (txnType == "PAYMENT_IN") Color(0xFF10B981) else Color.Transparent,
                    shadowElevation = if (txnType == "PAYMENT_IN") 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowDownLeft,
                            contentDescription = "In",
                            tint = if (txnType == "PAYMENT_IN") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "रुपया मिला (Payment In)" else "Payment In",
                            fontSize = 13.sp,
                            fontWeight = if (txnType == "PAYMENT_IN") FontWeight.Bold else FontWeight.Medium,
                            color = if (txnType == "PAYMENT_IN") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Payment Out (Gave Money)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { txnType = "PAYMENT_OUT" },
                    shape = RoundedCornerShape(10.dp),
                    color = if (txnType == "PAYMENT_OUT") Color(0xFFEF4444) else Color.Transparent,
                    shadowElevation = if (txnType == "PAYMENT_OUT") 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowUpRight,
                            contentDescription = "Out",
                            tint = if (txnType == "PAYMENT_OUT") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "भुगतान दिया (Payment Out)" else "Payment Out",
                            fontSize = 13.sp,
                            fontWeight = if (txnType == "PAYMENT_OUT") FontWeight.Bold else FontWeight.Medium,
                            color = if (txnType == "PAYMENT_OUT") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Amount Input
            OutlinedTextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = { Text(if (isHindi) "राशि दर्ज करें (Amount ₹) *" else "Enter Amount (₹) *") },
                placeholder = { Text("₹0.00") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Amount Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(500.0, 1000.0, 2000.0, 5000.0).forEach { chipAmt ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { amountStr = chipAmt.toInt().toString() },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "₹${chipAmt.toInt()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                // Full balance quick chip if pending balance > 0
                if (currentBal > 0) {
                    Surface(
                        modifier = Modifier
                            .weight(1.2f)
                            .clickable { amountStr = currentBal.toString() },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFDCFCE7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = if (isHindi) "पूरा हिसाब" else "Full",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534),
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Payment Mode Selector
            Text(
                text = if (isHindi) "भुगतान का माध्यम (Payment Mode)" else "Payment Mode",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("CASH", if (isHindi) "नकद" else "Cash", Lucide.Banknote),
                    Triple("UPI", "UPI / QR", Lucide.QrCode),
                    Triple("BANK_TRANSFER", if (isHindi) "बैंक" else "Bank", Lucide.Landmark),
                    Triple("CHEQUE", if (isHindi) "चेक" else "Cheque", Lucide.Landmark)
                ).forEach { (modeKey, label, icon) ->
                    val isSelected = paymentMode == modeKey
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { paymentMode = modeKey },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isSelected) Color(0xFF10B981) else Color.Transparent
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Reference Number & Notes
            OutlinedTextField(
                value = refNo,
                onValueChange = { refNo = it },
                label = { Text(if (isHindi) "रेफरेंस / UPI UTR नंबर" else "Ref / UTR Number") },
                placeholder = { Text("optional") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(if (isHindi) "टिप्पणी / विवरण" else "Notes / Description") },
                placeholder = { Text("optional payment notes") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Submit Button
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    onRecord(
                        party.id,
                        txnType,
                        amt,
                        paymentMode,
                        refNo,
                        notes
                    )
                },
                enabled = !isSubmitting && (amountStr.toDoubleOrNull() ?: 0.0) > 0.0,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (txnType == "PAYMENT_IN") Color(0xFF10B981) else Color(0xFFEF4444)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (txnType == "PAYMENT_IN") {
                            if (isHindi) "भुगतान जमा करें (Record Received)" else "Record Payment In"
                        } else {
                            if (isHindi) "भुगतान दर्ज करें (Record Paid)" else "Record Payment Out"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
