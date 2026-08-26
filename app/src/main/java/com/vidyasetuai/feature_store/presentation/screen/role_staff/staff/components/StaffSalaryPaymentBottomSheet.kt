package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.components

import androidx.compose.foundation.background
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
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.StaffSalaryRowUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSalaryPaymentBottomSheet(
    isHindi: Boolean = false,
    staffRow: StaffSalaryRowUiModel,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSavePayment: (
        paymentType: String,
        amount: Double,
        mode: String,
        ref: String?,
        remarks: String?
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var paymentType by remember { mutableStateOf("SALARY_PAYMENT") } // SALARY_PAYMENT vs ADVANCE
    var amountText by remember { mutableStateOf(if (staffRow.balanceDue > 0) staffRow.balanceDue.toString() else "") }
    var paymentMode by remember { mutableStateOf("CASH") } // CASH, UPI, BANK_TRANSFER
    var transactionRef by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

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
                        text = if (isHindi) "सैलरी / एडवांस भुगतान" else "Record Salary / Advance",
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

            // Summary Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.EmeraldGreen.copy(alpha = 0.08f)
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
                            text = if (isHindi) "देय वेतन (Balance Due)" else "Balance Due",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${staffRow.balanceDue}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AppColors.EmeraldGreen
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isHindi) "चल रहा एडवांस" else "Running Advance",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${staffRow.unsettledAdvance}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B)
                        )
                    }
                }
            }

            // 1. Payment Type Selector
            Text(
                text = if (isHindi) "भुगतान का प्रकार (Payment Type)" else "Payment Type",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "SALARY_PAYMENT" to if (isHindi) "सैलरी भुगतान (Salary)" else "Salary Payout",
                    "ADVANCE" to if (isHindi) "अग्रिम / एडवांस (Advance)" else "Advance Cash"
                ).forEach { (type, label) ->
                    val isSelected = paymentType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            paymentType = type
                            if (type == "SALARY_PAYMENT" && staffRow.balanceDue > 0) {
                                amountText = staffRow.balanceDue.toString()
                            }
                        },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
            }

            // 2. Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text(if (isHindi) "भुगतान राशि (Amount ₹) *" else "Amount Paid (₹) *") },
                placeholder = { Text("e.g. 5000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Payment Mode Chips
            Text(
                text = if (isHindi) "भुगतान का माध्यम (Payment Mode)" else "Payment Mode",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "CASH" to "💵 Cash (नकद)",
                    "UPI" to "📱 UPI / QR",
                    "BANK_TRANSFER" to "🏦 Bank Transfer"
                ).forEach { (mode, label) ->
                    val isSelected = paymentMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { paymentMode = mode },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
            }

            // 4. Transaction Reference / UTR
            if (paymentMode != "CASH") {
                OutlinedTextField(
                    value = transactionRef,
                    onValueChange = { transactionRef = it },
                    label = { Text(if (isHindi) "UTR / ट्रांजैक्शन रेफरेंस (वैकल्पिक)" else "UTR / Reference No (Optional)") },
                    placeholder = { Text("UPI/Bank Txn ID") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 5. Remarks / Note
            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text(if (isHindi) "रिमार्क / नोट (वैकल्पिक)" else "Remarks / Notes (Optional)") },
                placeholder = { Text(if (isHindi) "उदा. अगस्त माह का एडवांस" else "e.g. Festival advance") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Save CTA Button
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    onSavePayment(paymentType, amount, paymentMode, transactionRef, remarks)
                },
                enabled = !isSaving && (amountText.toDoubleOrNull() ?: 0.0) > 0.0,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = if (isHindi) "भुगतान दर्ज करें (Record Payment)" else "Record Payment",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
