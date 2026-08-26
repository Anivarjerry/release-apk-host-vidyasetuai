package com.vidyasetuai.feature_store.presentation.screen.role_staff.inventory.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.domain.model.ItemWithStockUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAdjustmentBottomSheet(
    itemWithStock: ItemWithStockUiModel,
    isHindi: Boolean = false,
    isSubmitting: Boolean = false,
    onDismiss: () -> Unit,
    onSubmit: (itemId: String, adjustedStock: Double, reason: String, notes: String?) -> Unit
) {
    val item = itemWithStock.item
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var adjustedStockText by remember(itemWithStock) {
        mutableStateOf(itemWithStock.currentStock.toString())
    }
    var selectedReason by remember { mutableStateOf("CORRECTION") }
    var notesText by remember { mutableStateOf("") }

    val currentStock = itemWithStock.currentStock
    val parsedNewStock = adjustedStockText.toDoubleOrNull() ?: currentStock
    val diff = parsedNewStock - currentStock

    val reasons = listOf(
        Pair("DAMAGE", if (isHindi) "💔 क्षतिग्रस्त सामान (Damaged)" else "💔 Damaged Item"),
        Pair("EXPIRED", if (isHindi) "⏳ एक्सपायर स्टॉक (Expired)" else "⏳ Expired Stock"),
        Pair("LOST", if (isHindi) "🔍 खोया / चोरी (Lost/Stolen)" else "🔍 Lost / Stolen"),
        Pair("CORRECTION", if (isHindi) "✏️ इन्वेंटरी सुधार (Correction)" else "✏️ Physical Audit Correction")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.SlidersHorizontal,
                                contentDescription = "Adjust",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = if (isHindi) "स्टॉक एडजस्टमेंट" else "Adjust Stock Level",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${item.name} (${item.unit})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
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

            // --- 1. CURRENT SYSTEM STOCK CARD ---
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "वर्तमान सिस्टम स्टॉक:" else "Current System Stock:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${"%.3f".format(currentStock)} ${item.unit}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // --- 2. NEW ACTUAL PHYSICAL STOCK INPUT ---
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isHindi) "नया वास्तविक भौतिक स्टॉक (New Stock) *" else "New Actual Physical Stock *",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = adjustedStockText,
                    onValueChange = { adjustedStockText = it },
                    placeholder = { Text("0.000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Text(
                            text = item.unit,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                )

                // Real-time Difference Live Pill
                if (diff != 0.0) {
                    val diffColor = if (diff > 0) Color(0xFF16A34A) else Color(0xFFDC2626)
                    val sign = if (diff > 0) "+" else ""
                    Text(
                        text = if (isHindi) {
                            "स्टॉक बदलाव: $sign${"%.3f".format(diff)} ${item.unit}"
                        } else {
                            "Stock Change: $sign${"%.3f".format(diff)} ${item.unit}"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = diffColor,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }
            }

            // --- 3. REASON CHIPS (2x2 Grid) ---
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isHindi) "एडजस्टमेंट का कारण (Reason) *" else "Adjustment Reason *",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    reasons.chunked(2).forEach { rowReasons ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowReasons.forEach { (key, label) ->
                                val isSelected = selectedReason == key
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) AppColors.EmeraldGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    border = if (isSelected) ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(AppColors.EmeraldGreen)
                                    ) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedReason = key }
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 4. AUDIT NOTES / REMARKS ---
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text(if (isHindi) "ऑडिट टिप्पणी / नोट्स (वैकल्पिक)" else "Audit Notes / Remarks (Optional)") },
                placeholder = { Text(if (isHindi) "उदा. भौतिक गिनती में बॉक्स टूटा मिला" else "e.g. Broken box found during audit") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // --- 5. ACTION BUTTONS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                ) {
                    Text(if (isHindi) "रद्द करें" else "Cancel")
                }

                Button(
                    onClick = {
                        val validNewStock = adjustedStockText.toDoubleOrNull() ?: 0.0
                        if (validNewStock >= 0.0) {
                            onSubmit(
                                item.id,
                                validNewStock,
                                selectedReason,
                                notesText.ifBlank { null }
                            )
                        }
                    },
                    enabled = (adjustedStockText.toDoubleOrNull() ?: -1.0) >= 0.0 && !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(46.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "सेव हो रहा है..." else "Saving...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = if (isHindi) "स्टॉक सेव करें" else "Save Adjustment",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
