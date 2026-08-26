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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.data.local.entity.BusinessEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessSettingsEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GstEwayBillConfigBottomSheet(
    isHindi: Boolean = false,
    initialBusiness: BusinessEntity?,
    initialSettings: BusinessSettingsEntity?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (
        gstin: String?,
        apiUsername: String?,
        apiPassword: String?,
        gspProvider: String,
        enableAutoEway: Boolean
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var gstin by remember { mutableStateOf(initialBusiness?.gstin ?: "") }
    var apiUsername by remember { mutableStateOf(initialSettings?.gstApiUsername ?: "") }
    var apiPassword by remember { mutableStateOf(initialSettings?.gstApiPassword ?: "") }
    var gspProvider by remember { mutableStateOf(initialSettings?.gspProviderName ?: "CLEARTAX") }
    var enableAutoEway by remember { mutableStateOf(initialSettings?.enableAutoEwayBill ?: false) }

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
                            imageVector = Lucide.ShieldCheck,
                            contentDescription = "GST",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isHindi) "GSTIN व E-Way Bill NIC API" else "Indian GST & E-Way Bill Config",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "ई-वे बिल ऑटो-जनरेशन व टैक्स फाइलिंग सेटअप" else "Direct NIC portal integration & automatic E-Way Bills",
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // GSTIN
                OutlinedTextField(
                    value = gstin,
                    onValueChange = { gstin = it.uppercase().trim() },
                    label = { Text(if (isHindi) "व्यापार GSTIN नंबर *" else "Business GSTIN Number *") },
                    placeholder = { Text("08AAAAA0000A1Z5") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // GSP Provider Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isHindi) "GSP API प्रोवाइडर (GSP Provider)" else "GSP API Provider",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("CLEARTAX", "MASTERS_INDIA", "EWAY_NIC").forEach { provider ->
                            val isSelected = gspProvider == provider
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF10B981).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { gspProvider = provider }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = provider.replace("_", " "),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // API Credentials
                OutlinedTextField(
                    value = apiUsername,
                    onValueChange = { apiUsername = it.trim() },
                    label = { Text(if (isHindi) "GST API यूजरनेम" else "GST API Username") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = apiPassword,
                    onValueChange = { apiPassword = it },
                    label = { Text(if (isHindi) "GST API पासवर्ड" else "GST API Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Auto E-Way Bill Switch
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (enableAutoEway) Color(0xFF10B981).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (enableAutoEway) Color(0xFF10B981).copy(alpha = 0.35f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
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
                                text = if (isHindi) "ऑटो ई-वे बिल जनरेशन (₹50,000+ पर)" else "Auto E-Way Bill Generation",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isHindi) "बिक्री इनवॉइस बनते ही सीधे NIC पोर्टल से EWB नंबर प्राप्त करें" else "Auto-fetch E-Way bill number directly on qualifying B2B sales",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = enableAutoEway,
                            onCheckedChange = { enableAutoEway = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onSave(
                            gstin.ifBlank { null },
                            apiUsername.ifBlank { null },
                            apiPassword.ifBlank { null },
                            gspProvider,
                            enableAutoEway
                        )
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
                            text = if (isHindi) "GST सेटिंग्स सुरक्षित करें" else "Save GST & E-Way Settings",
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
