package com.vidyasetuai.feature_store.presentation.screen.role_owner.purchases.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.data.local.entity.ItemEntity
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity
import com.vidyasetuai.feature_store.domain.model.PurchaseItemUiModel
import com.vidyasetuai.feature_store.domain.model.RecordPurchaseBillPayload
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPurchaseBillBottomSheet(
    isHindi: Boolean = false,
    suppliers: List<PartyEntity>,
    catalogItems: List<ItemEntity>,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (RecordPurchaseBillPayload) -> Unit
) {
    BackHandler(enabled = true) { onDismiss() }

    var selectedSupplierId by remember { mutableStateOf(suppliers.firstOrNull()?.id ?: "") }
    var supplierInvoiceNo by remember { mutableStateOf("") }
    var billDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var paymentMode by remember { mutableStateOf("CASH") }
    var paidAmountStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var itemRows by remember {
        mutableStateOf(
            listOf(
                PurchaseItemUiModel(
                    itemId = null,
                    itemName = "",
                    hsnSacCode = "",
                    quantity = 1.0,
                    unit = "PCS",
                    unitRate = 0.0,
                    taxRate = 5.0
                )
            )
        )
    }

    // Calculated Rows
    val calculatedRows = itemRows.map { row ->
        val taxableValue = row.quantity * row.unitRate
        val taxAmt = taxableValue * (row.taxRate / 100.0)
        val cgst = taxAmt / 2.0
        val sgst = taxAmt / 2.0
        val total = taxableValue + taxAmt
        row.copy(
            taxableValue = taxableValue,
            cgstAmount = cgst,
            sgstAmount = sgst,
            totalAmount = total
        )
    }

    val totalTaxable = calculatedRows.sumOf { it.taxableValue }
    val totalCgst = calculatedRows.sumOf { it.cgstAmount }
    val totalSgst = calculatedRows.sumOf { it.sgstAmount }
    val grandTotal = calculatedRows.sumOf { it.totalAmount }
    val paidAmount = paidAmountStr.toDoubleOrNull() ?: 0.0
    val dueAmount = maxOf(0.0, grandTotal - paidAmount)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
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
                            .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Lucide.ShoppingBag, contentDescription = "Purchase", tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(
                            text = if (isHindi) "नया सप्लायर खरीद बिल दर्ज करें" else "Record Supplier Purchase Bill",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Auto-Stock In (+Qty) & ITC GST Credit",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Supplier Selector
                item {
                    Text(
                        text = if (isHindi) "सप्लायर चुनें (Select Supplier) *" else "Select Supplier *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (suppliers.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "कोई सप्लायर नहीं मिला। कृपया पहले 'Parties & Khata' में सप्लायर जोड़ें।",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else {
                        var expanded by remember { mutableStateOf(false) }
                        val currentSupplier = suppliers.find { it.id == selectedSupplierId } ?: suppliers.first()

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = currentSupplier.name,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                suppliers.forEach { sup ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(text = sup.name, fontWeight = FontWeight.Bold)
                                                if (!sup.phone.isNullOrBlank()) {
                                                    Text(text = sup.phone, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        },
                                        onClick = {
                                            selectedSupplierId = sup.id
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Bill Number & Bill Date
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = supplierInvoiceNo,
                            onValueChange = { supplierInvoiceNo = it },
                            label = { Text("सप्लायर बिल नंबर *", fontSize = 11.sp) },
                            placeholder = { Text("INV-9021", fontSize = 11.sp) },
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = billDate,
                            onValueChange = { billDate = it },
                            label = { Text("बिल तारीख (YYYY-MM-DD)", fontSize = 11.sp) },
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 3. Purchase Items Header + Add Button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isHindi) "खरीदे गए सामान की सूची (Purchase Items)" else "Purchase Items & Stock In",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        TextButton(
                            onClick = {
                                itemRows = itemRows + PurchaseItemUiModel(
                                    itemId = null,
                                    itemName = "",
                                    quantity = 1.0,
                                    unit = "PCS",
                                    unitRate = 0.0,
                                    taxRate = 5.0
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(imageVector = Lucide.Plus, contentDescription = "Add", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ सामान जोड़ें", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 4. Line Items Rows
                itemsIndexed(itemRows) { index, row ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Item name with Catalog Picker
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                var catalogExpanded by remember { mutableStateOf(false) }

                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = row.itemName,
                                        onValueChange = { newName ->
                                            itemRows = itemRows.toMutableList().also {
                                                it[index] = it[index].copy(itemName = newName, itemId = null)
                                            }
                                        },
                                        label = { Text("सामान का नाम *", fontSize = 10.sp) },
                                        placeholder = { Text("या कैटलॉग से चुनें", fontSize = 10.sp) },
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                if (catalogItems.isNotEmpty()) {
                                    var showDropdown by remember { mutableStateOf(false) }
                                    Box {
                                        IconButton(
                                            onClick = { showDropdown = true },
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Icon(imageVector = Lucide.List, contentDescription = "Catalog", modifier = Modifier.size(16.dp))
                                        }
                                        DropdownMenu(
                                            expanded = showDropdown,
                                            onDismissRequest = { showDropdown = false }
                                        ) {
                                            catalogItems.forEach { catItem ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Column {
                                                            Text(text = catItem.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                            Text(text = "Rate: ₹${catItem.purchasePrice ?: catItem.salePrice} • ${catItem.unit ?: "PCS"}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        }
                                                    },
                                                    onClick = {
                                                        itemRows = itemRows.toMutableList().also {
                                                            it[index] = it[index].copy(
                                                                itemId = catItem.id,
                                                                itemName = catItem.name,
                                                                unit = catItem.unit ?: "PCS",
                                                                hsnSacCode = catItem.hsnSacCode ?: "",
                                                                unitRate = catItem.purchasePrice.takeIf { it > 0 } ?: catItem.salePrice,
                                                                taxRate = catItem.taxRate.takeIf { it > 0 } ?: 5.0
                                                            )
                                                        }
                                                        showDropdown = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                if (itemRows.size > 1) {
                                    IconButton(
                                        onClick = {
                                            itemRows = itemRows.toMutableList().also { it.removeAt(index) }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Lucide.Trash2, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            // Qty, Unit, Rate, GST %
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = if (row.quantity == 0.0) "" else row.quantity.toString(),
                                    onValueChange = { v ->
                                        val q = v.toDoubleOrNull() ?: 0.0
                                        itemRows = itemRows.toMutableList().also {
                                            it[index] = it[index].copy(quantity = q)
                                        }
                                    },
                                    label = { Text("Qty", fontSize = 10.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = if (row.unitRate == 0.0) "" else row.unitRate.toString(),
                                    onValueChange = { v ->
                                        val r = v.toDoubleOrNull() ?: 0.0
                                        itemRows = itemRows.toMutableList().also {
                                            it[index] = it[index].copy(unitRate = r)
                                        }
                                    },
                                    label = { Text("Rate (₹)", fontSize = 10.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    modifier = Modifier.weight(1.2f)
                                )

                                OutlinedTextField(
                                    value = if (row.taxRate == 0.0) "" else row.taxRate.toString(),
                                    onValueChange = { v ->
                                        val t = v.toDoubleOrNull() ?: 0.0
                                        itemRows = itemRows.toMutableList().also {
                                            it[index] = it[index].copy(taxRate = t)
                                        }
                                    },
                                    label = { Text("GST %", fontSize = 10.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    modifier = Modifier.weight(0.9f)
                                )
                            }

                            // Line Total preview
                            val lineTaxable = row.quantity * row.unitRate
                            val lineTax = lineTaxable * (row.taxRate / 100.0)
                            val lineTot = lineTaxable + lineTax
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Taxable: ₹${"%.2f".format(lineTaxable)} • Tax: ₹${"%.2f".format(lineTax)}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Line Total: ₹${"%,.2f".format(lineTot)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // 5. Payment & Due Details
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "भुगतान विवरण (Payment & Settlement)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("CASH", "UPI", "BANK_TRANSFER", "CREDIT").forEach { mode ->
                                    val isSelected = paymentMode == mode
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                paymentMode = mode
                                                if (mode == "CREDIT") paidAmountStr = "0"
                                            }
                                    ) {
                                        Text(
                                            text = mode.replace("_", " "),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = paidAmountStr,
                                    onValueChange = { paidAmountStr = it },
                                    label = { Text("अदा की गई राशि (Paid ₹)", fontSize = 10.sp) },
                                    placeholder = { Text("₹0.00", fontSize = 10.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (dueAmount > 0) Color(0xFFEF4444).copy(alpha = 0.1f) else Color(0xFF10B981).copy(alpha = 0.1f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "बकाया (Pending Due)", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = "₹${"%,.2f".format(dueAmount)}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            color = if (dueAmount > 0) Color(0xFFEF4444) else Color(0xFF10B981)
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("अतिरिक्त टिप्पणी (Optional Notes)", fontSize = 10.sp) },
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // 6. Grand Calculation Summary Box
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "कुल कर योग्य मूल्य (Taxable Amount):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "₹${"%,.2f".format(totalTaxable)}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "कुल इनपुट GST (CGST + SGST):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "₹${"%,.2f".format(totalCgst + totalSgst)}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                            }
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "कुल खरीद बिल राशि (Grand Total):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = "₹${"%,.2f".format(grandTotal)}", fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Color(0xFF10B981))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Submit Button
            Button(
                onClick = {
                    val validItems = calculatedRows.filter { it.itemName.isNotBlank() && it.quantity > 0 }
                    onSubmit(
                        RecordPurchaseBillPayload(
                            supplierId = selectedSupplierId,
                            supplierInvoiceNumber = supplierInvoiceNo.trim(),
                            billDate = billDate.trim(),
                            purchaseItems = validItems,
                            taxableAmount = totalTaxable,
                            cgstAmount = totalCgst,
                            sgstAmount = totalSgst,
                            igstAmount = 0.0,
                            grandTotal = grandTotal,
                            paidAmount = paidAmount,
                            paymentMode = paymentMode,
                            notes = notes.ifBlank { null }
                        )
                    )
                },
                enabled = !isSubmitting && selectedSupplierId.isNotBlank() && supplierInvoiceNo.isNotBlank() && calculatedRows.any { it.itemName.isNotBlank() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("खरीद बिल दर्ज हो रहा है...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Lucide.Check, contentDescription = "Submit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("✓ खरीद बिल दर्ज करें और स्टॉक जोड़ें (+Qty)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
