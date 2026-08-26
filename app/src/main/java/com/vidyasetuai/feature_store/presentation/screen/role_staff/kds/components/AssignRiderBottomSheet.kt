package com.vidyasetuai.feature_store.presentation.screen.role_staff.kds.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.data.local.entity.DeliveryRiderEntity
import com.vidyasetuai.feature_store.domain.model.KitchenKdsUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignRiderBottomSheet(
    isHindi: Boolean = false,
    ticket: KitchenKdsUiModel,
    availableRiders: List<DeliveryRiderEntity>,
    isSubmitting: Boolean = false,
    onDismiss: () -> Unit,
    onConfirmDispatch: (orderId: String, newStatus: String, riderId: String?, otpCode: String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedRiderId by remember { mutableStateOf(availableRiders.firstOrNull()?.id ?: "") }
    var otpCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Pillar 5: Layered BackHandler
    BackHandler(enabled = true) {
        onDismiss()
    }

    val isOutForDelivery = ticket.isOutForDelivery

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isOutForDelivery) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF3B82F6).copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isOutForDelivery) Lucide.Key else Lucide.Bike,
                            contentDescription = "Rider",
                            tint = if (isOutForDelivery) Color(0xFF10B981) else Color(0xFF3B82F6),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isOutForDelivery) {
                                if (isHindi) "स्टेप २: ग्राहक डिलीवरी OTP सत्यापन" else "Step 2: Customer Delivery OTP"
                            } else {
                                if (isHindi) "स्टेप १: राइडर को डिस्पैच करें" else "Step 1: Dispatch to Delivery Rider"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Order ${ticket.formattedOrderNumber}",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Customer Info Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ticket.order.customerName.ifBlank { if (isHindi) "ग्राहक" else "Customer" },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "₹${ticket.order.grandTotal.toInt()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF10B981)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Lucide.Phone, contentDescription = "Phone", modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = ticket.order.customerPhone.ifBlank { "N/A" },
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!ticket.order.deliveryAddressJson.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Lucide.MapPin, contentDescription = "Address", modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = ticket.order.deliveryAddressJson ?: "",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            // Error Message
            errorMessage?.let { err ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEF4444).copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Lucide.Info, contentDescription = "Error", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        Text(text = err, fontSize = 12.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (isOutForDelivery) {
                // Step 2: Doorstep OTP Input
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isHindi) "४-अंकीय ग्राहक डिलीवरी OTP दर्ज करें" else "Enter 4-Digit Customer Delivery OTP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { if (it.length <= 6) otpCode = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("उदा. 4821", textAlign = TextAlign.Center) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(imageVector = Lucide.Key, contentDescription = "OTP", tint = Color(0xFF10B981))
                        }
                    )

                    Text(
                        text = if (isHindi) "यह OTP ग्राहक के पास SMS या ऑनलाइन ऑर्डर ट्रैकिंग स्क्रीन पर उपलब्ध है।" else "OTP is available on customer's order tracking screen.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            if (otpCode.isBlank()) {
                                errorMessage = if (isHindi) "कृपया सही OTP दर्ज करें" else "Please enter delivery OTP"
                                return@Button
                            }
                            errorMessage = null
                            onConfirmDispatch(ticket.order.id, "DELIVERED", null, otpCode.trim())
                        },
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(imageVector = Lucide.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "डिलीवरी पूरी करें (Mark Delivered)" else "COMPLETE DOORSTEP DELIVERY",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            } else {
                // Step 1: Rider Selection
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isHindi) "उपलब्ध डिलीवरी राइडर चुनें" else "Select Delivery Rider to Assign",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (availableRiders.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "कोई राइडर दर्ज नहीं है" else "No Delivery Riders Registered",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isHindi) "आप सीधे भी 'Out For Delivery' मार्क कर सकते हैं।" else "You can dispatch directly without assigning a specific rider.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            availableRiders.forEach { rider ->
                                val isSelected = selectedRiderId == rider.id
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) Color(0xFF3B82F6).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) Color(0xFF3B82F6) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedRiderId = rider.id }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { selectedRiderId = rider.id },
                                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF3B82F6))
                                            )
                                            Column {
                                                Text(
                                                    text = rider.name,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "Ph: ${rider.phone} ${if (!rider.vehicleNumber.isNullOrBlank()) "• ${rider.vehicleNumber}" else ""}",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (rider.isOnline) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = if (rider.isOnline) "ON DUTY" else "OFFLINE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (rider.isOnline) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onConfirmDispatch(
                                ticket.order.id,
                                "OUT_FOR_DELIVERY",
                                selectedRiderId.ifBlank { null },
                                null
                            )
                        },
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(imageVector = Lucide.Bike, contentDescription = "Dispatch", tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "🛵 राइडर को डिस्पैच करें (Out For Delivery)" else "🛵 DISPATCH TO RIDER (OUT FOR DELIVERY)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
