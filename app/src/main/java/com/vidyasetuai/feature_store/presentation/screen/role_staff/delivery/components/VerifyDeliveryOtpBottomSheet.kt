package com.vidyasetuai.feature_store.presentation.screen.role_staff.delivery.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.domain.model.DriverDeliveryOrderUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyDeliveryOtpBottomSheet(
    order: DriverDeliveryOrderUiModel,
    isHindi: Boolean = false,
    isVerifying: Boolean = false,
    onDismiss: () -> Unit,
    onVerifyOtp: (orderId: String, otpCode: String) -> Unit
) {
    var otpCode by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isCod = order.paymentMethod in listOf("CASH_ON_DELIVERY", "COD", "CASH")
    var isCashCollectedConfirmed by remember { mutableStateOf(!isCod) }

    // Pillar 5: Layered Back Navigation
    BackHandler(enabled = true) {
        onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isHindi) "डिलीवरी OTP वेरिफिकेशन" else "Verify Delivery OTP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Order #${order.orderNumber} • ${order.customerName}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AppColors.EmeraldGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "₹${"%.2f".format(order.grandTotal)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // COD Cash Collection Alert Card
            if (isCod) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF59E0B).copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Coins,
                            contentDescription = "COD Cash",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(22.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "कैश ऑन डिलीवरी (COD)" else "Cash on Delivery (COD)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                            Text(
                                text = if (isHindi) 
                                    "कृपया ग्राहक से ₹${"%.2f".format(order.grandTotal)} नकद प्राप्त करने के बाद ही OTP दर्ज करें।" 
                                else 
                                    "Please collect ₹${"%.2f".format(order.grandTotal)} cash from customer before verifying OTP.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // OTP PIN Input Field
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isHindi) "ग्राहक से 4-अंकों का डिलीवरी कोड पूछें:" else "Ask Customer for 4-Digit Delivery OTP:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) otpCode = it },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(64.dp),
                    shape = RoundedCornerShape(14.dp),
                    placeholder = {
                        Text(
                            text = "• • • •",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 6.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.EmeraldGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            // Confirmation Checkbox if COD
            if (isCod) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = isCashCollectedConfirmed,
                        onCheckedChange = { isCashCollectedConfirmed = it },
                        colors = CheckboxDefaults.colors(checkedColor = AppColors.EmeraldGreen)
                    )
                    Text(
                        text = if (isHindi) "मैंने ₹${"%.2f".format(order.grandTotal)} नकद प्राप्त कर लिया है" else "I have collected ₹${"%.2f".format(order.grandTotal)} cash in hand",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isVerifying
                ) {
                    Text(text = if (isHindi) "रद्द करें" else "Cancel", fontSize = 14.sp)
                }

                Button(
                    onClick = { onVerifyOtp(order.id, otpCode) },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                    enabled = !isVerifying && otpCode.length == 4 && isCashCollectedConfirmed
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(imageVector = Lucide.Check, contentDescription = "Verify", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "वेरिफ़ाई व डिलीवर" else "Verify & Complete",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
