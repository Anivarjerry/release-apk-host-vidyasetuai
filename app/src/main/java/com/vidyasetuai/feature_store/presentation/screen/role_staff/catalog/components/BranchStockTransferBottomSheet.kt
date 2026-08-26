package com.vidyasetuai.feature_store.presentation.screen.role_staff.catalog.components

import androidx.compose.foundation.background
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
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.ItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchStockTransferBottomSheet(
    isHindi: Boolean = false,
    branches: List<BusinessBranchEntity>,
    items: List<ItemEntity>,
    sourceStocks: Map<String, Double>, // itemId -> available stock at source branch
    isTransferring: Boolean,
    onDismiss: () -> Unit,
    onExecuteTransfer: (
        fromBranchId: String,
        toBranchId: String,
        itemId: String,
        quantity: Double,
        notes: String?
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var fromBranchId by remember(branches) {
        mutableStateOf(branches.firstOrNull { it.isMainBranch }?.id ?: branches.firstOrNull()?.id ?: "")
    }
    var toBranchId by remember(branches, fromBranchId) {
        mutableStateOf(branches.firstOrNull { it.id != fromBranchId }?.id ?: "")
    }

    var selectedItemId by remember(items) {
        mutableStateOf(items.firstOrNull { it.itemType == "PRODUCT" }?.id ?: "")
    }

    var quantityText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }

    var isFromDropdownOpen by remember { mutableStateOf(false) }
    var isToDropdownOpen by remember { mutableStateOf(false) }
    var isItemDropdownOpen by remember { mutableStateOf(false) }

    val selectedItem = items.find { it.id == selectedItemId }
    val availableAtSource = sourceStocks[selectedItemId] ?: 0.0
    val transferQty = quantityText.toDoubleOrNull() ?: 0.0
    val isQtyValid = transferQty > 0.0 && transferQty <= availableAtSource
    val isBranchValid = fromBranchId.isNotBlank() && toBranchId.isNotBlank() && fromBranchId != toBranchId

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
                        text = if (isHindi) "मल्टी-ब्रांच स्टॉक ट्रांसफर" else "Multi-Branch Stock Transfer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isHindi) "एक ब्रांच से दूसरी ब्रांच में माल ट्रांसफर करें" else "Transfer inventory between store outlets",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // 1. Source Branch Selector (From)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isHindi) "कहाँ से भेजें (Source Branch / Warehouse) *" else "From Branch / Warehouse *",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ExposedDropdownMenuBox(
                    expanded = isFromDropdownOpen,
                    onExpandedChange = { isFromDropdownOpen = !isFromDropdownOpen }
                ) {
                    val fromName = branches.find { it.id == fromBranchId }?.branchName ?: "Select Source..."
                    OutlinedTextField(
                        value = fromName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFromDropdownOpen) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isFromDropdownOpen,
                        onDismissRequest = { isFromDropdownOpen = false }
                    ) {
                        branches.forEach { branch ->
                            DropdownMenuItem(
                                text = { Text("${branch.branchName}${if (branch.isMainBranch) " (Main)" else ""}") },
                                onClick = {
                                    fromBranchId = branch.id
                                    if (toBranchId == branch.id) {
                                        toBranchId = branches.firstOrNull { it.id != branch.id }?.id ?: ""
                                    }
                                    isFromDropdownOpen = false
                                }
                            )
                        }
                    }
                }
            }

            // 2. Destination Branch Selector (To)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isHindi) "कहाँ प्राप्त करें (Destination Branch) *" else "To Branch (Destination) *",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ExposedDropdownMenuBox(
                    expanded = isToDropdownOpen,
                    onExpandedChange = { isToDropdownOpen = !isToDropdownOpen }
                ) {
                    val toName = branches.find { it.id == toBranchId }?.branchName ?: "Select Destination..."
                    OutlinedTextField(
                        value = toName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isToDropdownOpen) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isToDropdownOpen,
                        onDismissRequest = { isToDropdownOpen = false }
                    ) {
                        branches.filter { it.id != fromBranchId }.forEach { branch ->
                            DropdownMenuItem(
                                text = { Text(branch.branchName) },
                                onClick = {
                                    toBranchId = branch.id
                                    isToDropdownOpen = false
                                }
                            )
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // 3. Product Selector
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isHindi) "सामान / उत्पाद चुनें (Select Item) *" else "Select Product *",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ExposedDropdownMenuBox(
                    expanded = isItemDropdownOpen,
                    onExpandedChange = { isItemDropdownOpen = !isItemDropdownOpen }
                ) {
                    val itemName = selectedItem?.name ?: "Select Product..."
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isItemDropdownOpen) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isItemDropdownOpen,
                        onDismissRequest = { isItemDropdownOpen = false }
                    ) {
                        items.filter { it.itemType == "PRODUCT" }.forEach { item ->
                            val s = sourceStocks[item.id] ?: 0.0
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(item.name)
                                        Text("($s ${item.unit})", fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                                    }
                                },
                                onClick = {
                                    selectedItemId = item.id
                                    isItemDropdownOpen = false
                                }
                            )
                        }
                    }
                }
            }

            // Available Source Stock Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (availableAtSource > 0) AppColors.EmeraldGreen.copy(alpha = 0.1f) else Color(0xFFEF4444).copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "स्रोत शाखा पर उपलब्ध स्टॉक:" else "Stock Available at Source:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$availableAtSource ${selectedItem?.unit ?: "PCS"}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (availableAtSource > 0) AppColors.EmeraldGreen else Color(0xFFEF4444)
                    )
                }
            }

            // 4. Quantity Input
            OutlinedTextField(
                value = quantityText,
                onValueChange = { quantityText = it },
                label = { Text(if (isHindi) "ट्रांसफर मात्रा (Quantity) *" else "Transfer Quantity *") },
                placeholder = { Text("Max: $availableAtSource") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = transferQty > availableAtSource,
                supportingText = {
                    if (transferQty > availableAtSource) {
                        Text(if (isHindi) "उपलब्ध स्टॉक से अधिक मात्रा नहीं हो सकती।" else "Cannot exceed available stock ($availableAtSource)", color = MaterialTheme.colorScheme.error)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // 5. Transfer Reason / Notes
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text(if (isHindi) "ट्रांसफर विवरण / नोट (वैकल्पिक)" else "Transfer Reason / Notes (Optional)") },
                placeholder = { Text(if (isHindi) "उदा. आउटलेट की मांग के अनुसार" else "e.g. Replenishment for weekend rush") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Execute Transfer CTA Button
            Button(
                onClick = {
                    onExecuteTransfer(fromBranchId, toBranchId, selectedItemId, transferQty, notesText.ifBlank { null })
                },
                enabled = !isTransferring && isQtyValid && isBranchValid && selectedItemId.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isTransferring) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Lucide.ArrowRightLeft, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "स्टॉक ट्रांसफर निष्पादित करें" else "Execute Stock Transfer",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
