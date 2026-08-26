package com.vidyasetuai.feature_store.presentation.screen.role_owner.settings.components

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
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.data.local.entity.BusinessSettingsEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBillingPrinterBottomSheet(
    isHindi: Boolean = false,
    initialSettings: BusinessSettingsEntity?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (
        invoicePrefix: String,
        thermalPrinterSize: String,
        enableGstBilling: Boolean,
        packingCharge: Double,
        deliveryCharge: Double,
        freeDeliveryAbove: Double?
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var invoicePrefix by remember { mutableStateOf(initialSettings?.invoicePrefix ?: "INV-24/") }
    var thermalPrinterSize by remember { mutableStateOf(initialSettings?.thermalPrinterSize ?: "3_INCH") }
    var enableGstBilling by remember { mutableStateOf(initialSettings?.enableGstBilling ?: false) }
    var packingChargeStr by remember { mutableStateOf(initialSettings?.packingChargeDefault?.toString() ?: "0.0") }
    var deliveryChargeStr by remember { mutableStateOf(initialSettings?.deliveryChargeDefault?.toString() ?: "0.0") }
    var freeDeliveryAboveStr by remember { mutableStateOf(initialSettings?.freeDeliveryAbove?.toString() ?: "") }

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
                .fillMaxHeight(0.85f)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Receipt,
                            contentDescription = "Billing",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isHindi) "इनवॉइसिंग व प्रिंटर सेटिंग्स" else "Invoicing & Printer Settings",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "बिल नंबरिंग, प्रिंटर साइज़ व GST नियम" else "Series numbering, paper format & GST rules",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: GST Toggle Switch Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (enableGstBilling) Color(0xFF10B981).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (enableGstBilling) Color(0xFF10B981).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "GST इनवॉइस बिलिंग सक्षम करें" else "Enable GST Invoicing & Tax Slabs",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (enableGstBilling) "बिल पर CGST/SGST टैक्स स्लैब्स लागू होंगे" else "सामान्य नॉन-जीएसटी बिलिंग मोड सक्रिय है",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = enableGstBilling,
                            onCheckedChange = { enableGstBilling = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981)
                            )
                        )
                    }
                }

                // Section 2: Bill Numbering Prefix
                OutlinedTextField(
                    value = invoicePrefix,
                    onValueChange = { invoicePrefix = it },
                    label = { Text(if (isHindi) "बिल नंबरिंग प्रीफिक्स (Prefix) *" else "Bill Numbering Prefix *") },
                    supportingText = { Text("Sample: ${invoicePrefix}0001") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Section 3: Thermal Printer Roll Size Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isHindi) "थर्मल प्रिंटर पेपर साइज़ (Printer Width)" else "Thermal Printer Roll Size",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("2_INCH", "2 INCH", "58mm POS"),
                            Triple("3_INCH", "3 INCH", "80mm POS"),
                            Triple("A4_REGULAR", "A4 FULL", "Standard Sheet")
                        ).forEach { (key, title, subtitle) ->
                            val isSelected = thermalPrinterSize == key
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF10B981).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { thermalPrinterSize = key }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = subtitle,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 4: Extra Charges Default
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = packingChargeStr,
                        onValueChange = { packingChargeStr = it },
                        label = { Text(if (isHindi) "पैकिंग शुल्क (₹)" else "Packing Fee (₹)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = deliveryChargeStr,
                        onValueChange = { deliveryChargeStr = it },
                        label = { Text(if (isHindi) "डिलीवरी शुल्क (₹)" else "Delivery Fee (₹)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = freeDeliveryAboveStr,
                    onValueChange = { freeDeliveryAboveStr = it },
                    label = { Text(if (isHindi) "मुफ़्त डिलीवरी थ्रेशोल्ड (₹ - वैकल्पिक)" else "Free Delivery Above (₹ Optional)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Save Button
                Button(
                    onClick = {
                        val pack = packingChargeStr.toDoubleOrNull() ?: 0.0
                        val del = deliveryChargeStr.toDoubleOrNull() ?: 0.0
                        val free = freeDeliveryAboveStr.toDoubleOrNull()
                        onSave(invoicePrefix, thermalPrinterSize, enableGstBilling, pack, del, free)
                    },
                    enabled = !isSaving && invoicePrefix.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = if (isHindi) "सेटिंग्स सुरक्षित करें (Save Settings)" else "Save Settings",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
