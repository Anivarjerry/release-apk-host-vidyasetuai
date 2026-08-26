package com.vidyasetuai.feature_store.presentation.screen.role_owner.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.data.local.entity.BusinessSettingsEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureSwitchesBottomSheet(
    isHindi: Boolean = false,
    initialSettings: BusinessSettingsEntity?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (
        enableKds: Boolean,
        enableDeliveryTracking: Boolean,
        enableInventoryTracking: Boolean,
        enableCustomerKhata: Boolean,
        allowOnlineOrders: Boolean
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var enableKds by remember { mutableStateOf(initialSettings?.enableKds ?: false) }
    var enableDeliveryTracking by remember { mutableStateOf(initialSettings?.enableDeliveryTracking ?: false) }
    var enableInventoryTracking by remember { mutableStateOf(initialSettings?.enableInventoryTracking ?: true) }
    var enableCustomerKhata by remember { mutableStateOf(initialSettings?.enableCustomerKhata ?: true) }
    var allowOnlineOrders by remember { mutableStateOf(initialSettings?.allowOnlineOrders ?: true) }

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
                            imageVector = Lucide.SlidersHorizontal,
                            contentDescription = "Features",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isHindi) "सिस्टम फीचर्स व मॉड्यूल्स" else "Active System Features",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "इस वर्कस्पेस के लिए सक्रिय टूल्स चुनें" else "Feature switches enabled for this workspace",
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Feature 1: Online Orders
                FeatureToggleRow(
                    title = if (isHindi) "पब्लिक कस्टमर ऑनलाइन ऑर्डर्स" else "Public Customer Online Store Orders",
                    subtitle = if (isHindi) "वेब मेनू लिंक से ग्राहक सीधे ऑर्डर कर सकते हैं" else "Allow customer storefront ordering from digital web menu",
                    checked = allowOnlineOrders,
                    onCheckedChange = { allowOnlineOrders = it }
                )

                // Feature 2: Customer Khata
                FeatureToggleRow(
                    title = if (isHindi) "ग्राहक उधार व पार्टी खाता" else "Customer Udhar Khata Ledger",
                    subtitle = if (isHindi) "POS काउंटर पर उधारी और पासबुक ट्रैकिंग सक्षम करें" else "Track customer credit/debit balances and passbook reminders",
                    checked = enableCustomerKhata,
                    onCheckedChange = { enableCustomerKhata = it }
                )

                // Feature 3: Inventory
                FeatureToggleRow(
                    title = if (isHindi) "इन्वेंटरी व स्टॉक कंट्रोल" else "Multi-Branch Inventory & Stock Control",
                    subtitle = if (isHindi) "बिलिंग पर अपने आप स्टॉक कटना व लो-स्टॉक अलर्ट्स" else "Automatic stock deduction on sales and low stock alerts",
                    checked = enableInventoryTracking,
                    onCheckedChange = { enableInventoryTracking = it }
                )

                // Feature 4: Delivery Rider
                FeatureToggleRow(
                    title = if (isHindi) "डिलीवरी राइडर व GPS ट्रैकिंग" else "Delivery Rider Fleet & Tracking",
                    subtitle = if (isHindi) "राइडर ड्यूटी टॉगल व COD कैश कलेक्शन सेटलमेंट" else "Fleet management, on-duty toggles and COD cash settlement",
                    checked = enableDeliveryTracking,
                    onCheckedChange = { enableDeliveryTracking = it }
                )

                // Feature 5: KDS
                FeatureToggleRow(
                    title = if (isHindi) "किचन डिस्प्ले सिस्टम (KDS)" else "Kitchen Display System (KDS)",
                    subtitle = if (isHindi) "शेफ व कुक के लिए लाइव ऑर्डर KOT स्क्रीन" else "Live kitchen order ticket screen for kitchen staff",
                    checked = enableKds,
                    onCheckedChange = { enableKds = it }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onSave(enableKds, enableDeliveryTracking, enableInventoryTracking, enableCustomerKhata, allowOnlineOrders)
                    },
                    enabled = !isSaving,
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
                            text = if (isHindi) "फीचर्स सुरक्षित करें (Save Features)" else "Save Features",
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

@Composable
private fun FeatureToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (checked) Color(0xFF10B981).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (checked) Color(0xFF10B981).copy(alpha = 0.35f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF10B981)
                )
            )
        }
    }
}
