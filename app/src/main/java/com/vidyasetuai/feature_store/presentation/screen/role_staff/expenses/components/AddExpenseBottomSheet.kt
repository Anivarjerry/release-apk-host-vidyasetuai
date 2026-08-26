package com.vidyasetuai.feature_store.presentation.screen.role_staff.expenses.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffEntity
import com.vidyasetuai.feature_store.data.local.entity.ItemEntity
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity
import com.vidyasetuai.feature_store.domain.model.ExpenseCategoryUiModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseBottomSheet(
    isHindi: Boolean = false,
    categories: List<ExpenseCategoryUiModel>,
    branchesList: List<BusinessBranchEntity> = emptyList(),
    staffList: List<BusinessStaffEntity> = emptyList(),
    partiesList: List<PartyEntity> = emptyList(),
    itemsList: List<ItemEntity> = emptyList(),
    isSubmitting: Boolean,
    onSaveExpense: (
        expenseTypeId: String,
        branchId: String?,
        title: String,
        amount: Double,
        paymentMode: String,
        expenseDate: String,
        referenceType: String,
        referenceId: String?,
        paidTo: String?,
        notes: String?
    ) -> Unit,
    onDismiss: () -> Unit
) {
    // Mandatory Layered BackHandler: dismiss sheet on back press
    BackHandler(enabled = true) {
        onDismiss()
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Form States
    var selectedCategoryId by remember(categories) {
        mutableStateOf(categories.firstOrNull()?.id ?: "")
    }
    var selectedBranchId by remember(branchesList) {
        mutableStateOf(branchesList.firstOrNull { it.isMainBranch }?.id ?: branchesList.firstOrNull()?.id ?: "")
    }
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("CASH") }
    var expenseDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var referenceId by remember { mutableStateOf<String?>(null) }
    var paidTo by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Dropdown Expanded States
    var isCategoryDropdownOpen by remember { mutableStateOf(false) }
    var isBranchDropdownOpen by remember { mutableStateOf(false) }
    var isStaffDropdownOpen by remember { mutableStateOf(false) }
    var isPartyDropdownOpen by remember { mutableStateOf(false) }
    var isItemDropdownOpen by remember { mutableStateOf(false) }

    val selectedCategory = categories.find { it.id == selectedCategoryId }
    val refType = selectedCategory?.referenceType ?: "NONE"

    // Auto-update default title when category changes
    LaunchedEffect(selectedCategoryId) {
        if (selectedCategory != null) {
            val label = if (isHindi && !selectedCategory.nameHi.isNullOrBlank()) selectedCategory.nameHi else selectedCategory.name
            title = label
            if (refType == "BRANCH") {
                referenceId = selectedBranchId.ifBlank { null }
            } else {
                referenceId = null
            }
        }
    }

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
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isHindi) "नया खर्च दर्ज करें (Voucher)" else "Record New Store Expense",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isHindi) "दुकान संचालन, किराया, स्टाफ भत्ता या फुटकर पेट्टी कैश खर्च" else "Operational store expense, rent, utilities, staff travel or petty cash",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. Branch Selector (If multi-branch exists)
            if (branchesList.size > 1) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (isHindi) "दुकान शाखा (Store Branch):" else "Store Branch:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ExposedDropdownMenuBox(
                        expanded = isBranchDropdownOpen,
                        onExpandedChange = { isBranchDropdownOpen = it }
                    ) {
                        val currentBranch = branchesList.find { it.id == selectedBranchId }
                        val branchLabel = currentBranch?.branchName ?: (if (isHindi) "शाखा चुनें" else "Select Branch")

                        OutlinedTextField(
                            value = branchLabel,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isBranchDropdownOpen) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = isBranchDropdownOpen,
                            onDismissRequest = { isBranchDropdownOpen = false }
                        ) {
                            branchesList.forEach { branch ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${branch.branchName} ${if (branch.isMainBranch) if (isHindi) "★ (मुख्य)" else "★ (Main)" else ""}",
                                            fontWeight = if (branch.id == selectedBranchId) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        selectedBranchId = branch.id
                                        if (refType == "BRANCH") {
                                            referenceId = branch.id
                                        }
                                        isBranchDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 3. Category Selector Dropdown
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isHindi) "खर्च की श्रेणी (Expense Category)*:" else "Expense Category*:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ExposedDropdownMenuBox(
                    expanded = isCategoryDropdownOpen,
                    onExpandedChange = { isCategoryDropdownOpen = it }
                ) {
                    val catLabel = if (selectedCategory != null) {
                        if (isHindi && !selectedCategory.nameHi.isNullOrBlank()) selectedCategory.nameHi else selectedCategory.name
                    } else {
                        if (isHindi) "श्रेणी चुनें" else "Select Category"
                    }

                    OutlinedTextField(
                        value = catLabel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownOpen) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = null,
                                tint = Color(0xFF10B981)
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = isCategoryDropdownOpen,
                        onDismissRequest = { isCategoryDropdownOpen = false }
                    ) {
                        categories.forEach { cat ->
                            val label = if (isHindi && !cat.nameHi.isNullOrBlank()) cat.nameHi else cat.name
                            val isSelected = cat.id == selectedCategoryId

                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (cat.referenceType != "NONE") {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFF10B981).copy(alpha = 0.12f)
                                            ) {
                                                Text(
                                                    text = cat.referenceType,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF10B981),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                },
                                onClick = {
                                    selectedCategoryId = cat.id
                                    isCategoryDropdownOpen = false
                                }
                            )
                        }
                    }
                }
            }

            // 4. Dynamic Reference Card based on Category's reference_type
            when (refType) {
                "STAFF" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.08f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Text(
                                text = if (isHindi) "संबंधित कर्मचारी / राइडर चुनें*" else "Select Staff Member / Rider*",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        ExposedDropdownMenuBox(
                            expanded = isStaffDropdownOpen,
                            onExpandedChange = { isStaffDropdownOpen = it }
                        ) {
                            val currentStaff = staffList.find { it.id == referenceId }
                            val staffLabel = currentStaff?.let { "${it.name} (${it.role})" } ?: (if (isHindi) "-- कर्मचारी चुनें --" else "-- Select Staff --")

                            OutlinedTextField(
                                value = staffLabel,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStaffDropdownOpen) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF10B981),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = isStaffDropdownOpen,
                                onDismissRequest = { isStaffDropdownOpen = false }
                            ) {
                                staffList.forEach { staff ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${staff.name} (${staff.role}) ${if (staff.phone.isNotBlank()) "• ${staff.phone}" else ""}",
                                                fontSize = 12.sp
                                            )
                                        },
                                        onClick = {
                                            referenceId = staff.id
                                            if (paidTo.isBlank()) {
                                                paidTo = staff.name
                                            }
                                            isStaffDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                "SUPPLIER" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF59E0B).copy(alpha = 0.08f))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                            Text(
                                text = if (isHindi) "संबंधित सप्लायर / वेंडर चुनें*" else "Select Supplier / Vendor*",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        ExposedDropdownMenuBox(
                            expanded = isPartyDropdownOpen,
                            onExpandedChange = { isPartyDropdownOpen = it }
                        ) {
                            val currentParty = partiesList.find { it.id == referenceId }
                            val partyLabel = currentParty?.let { "${it.name} ${if (!it.phone.isNullOrBlank()) "(${it.phone})" else ""}" } ?: (if (isHindi) "-- सप्लायर चुनें --" else "-- Select Supplier --")

                            OutlinedTextField(
                                value = partyLabel,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPartyDropdownOpen) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFF59E0B),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = isPartyDropdownOpen,
                                onDismissRequest = { isPartyDropdownOpen = false }
                            ) {
                                partiesList.forEach { party ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${party.name} ${if (!party.phone.isNullOrBlank()) "• ${party.phone}" else ""}",
                                                fontSize = 12.sp
                                            )
                                        },
                                        onClick = {
                                            referenceId = party.id
                                            if (paidTo.isBlank()) {
                                                paidTo = party.name
                                            }
                                            isPartyDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                "ITEM" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.08f))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            Text(
                                text = if (isHindi) "संबंधित प्रोडक्ट / सामान चुनें (Wastage)*" else "Select Product / Catalog Item (Wastage)*",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        ExposedDropdownMenuBox(
                            expanded = isItemDropdownOpen,
                            onExpandedChange = { isItemDropdownOpen = it }
                        ) {
                            val currentItem = itemsList.find { it.id == referenceId }
                            val itemLabel = currentItem?.name ?: (if (isHindi) "-- प्रोडक्ट चुनें --" else "-- Select Product --")

                            OutlinedTextField(
                                value = itemLabel,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isItemDropdownOpen) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFEF4444),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = isItemDropdownOpen,
                                onDismissRequest = { isItemDropdownOpen = false }
                            ) {
                                itemsList.forEach { item ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${item.name} (₹${item.salePrice})",
                                                fontSize = 12.sp
                                            )
                                        },
                                        onClick = {
                                            referenceId = item.id
                                            isItemDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                "BRANCH" -> {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF6366F1).copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(18.dp))
                            Text(
                                text = if (isHindi) "यह खर्च सीधे चयनित दुकान शाखा के नाम पर दर्ज होगा।" else "This expense is directly booked against selected store branch.",
                                fontSize = 11.sp,
                                color = Color(0xFF6366F1),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 5. Title & Amount Inputs
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(if (isHindi) "खर्च का विवरण (Title)*" else "Expense Description / Title*") },
                placeholder = { Text(if (isHindi) "उदा. दैनिक स्टाफ चाय, पेट्रोल रिफिल" else "e.g. Daily Staff Tea, Petrol Refill") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) {
                            amountText = it
                        }
                    },
                    label = { Text(if (isHindi) "राशि (Amount)*" else "Amount (₹)*") },
                    placeholder = { Text("0.00") },
                    prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = expenseDate,
                    onValueChange = { expenseDate = it },
                    label = { Text(if (isHindi) "तारीख (YYYY-MM-DD)" else "Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // 6. Payment Mode Selector
            Text(
                text = if (isHindi) "भुगतान का माध्यम (Payment Mode):" else "Payment Mode (Drawer / Bank):",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "CASH" to (if (isHindi) "🪙 गल्ला (Cash)" else "🪙 Cash Drawer"),
                    "UPI" to "📱 UPI / QR",
                    "BANK_TRANSFER" to (if (isHindi) "🏦 बैंक" else "🏦 Bank Transfer"),
                    "CHEQUE" to (if (isHindi) "📑 चेक" else "📑 Cheque")
                ).forEach { (mode, label) ->
                    val isSelected = paymentMode == mode
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { paymentMode = mode },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // 7. Paid To & Notes
            OutlinedTextField(
                value = paidTo,
                onValueChange = { paidTo = it },
                label = { Text(if (isHindi) "भुगतान किसे दिया (Paid To)" else "Paid To (Person/Shop)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(if (isHindi) "टिप्पणी / रिमार्क्स (Notes)" else "Remarks / Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Validation Error if any
            if (validationError != null) {
                Text(
                    text = validationError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 8. Action Button: Save Expense
            Button(
                onClick = {
                    val amountVal = amountText.toDoubleOrNull()
                    if (amountVal == null || amountVal <= 0) {
                        validationError = if (isHindi) "कृपया सही राशि दर्ज करें" else "Please enter a valid expense amount"
                        return@Button
                    }
                    if (title.isBlank()) {
                        validationError = if (isHindi) "कृपया खर्च का विवरण (Title) दर्ज करें" else "Please enter an expense title"
                        return@Button
                    }
                    if (refType != "NONE" && refType != "BRANCH" && referenceId.isNullOrBlank()) {
                        validationError = when (refType) {
                            "STAFF" -> if (isHindi) "कृपया कर्मचारी चुनें" else "Please select a staff member"
                            "SUPPLIER" -> if (isHindi) "कृपया सप्लायर चुनें" else "Please select a supplier"
                            "ITEM" -> if (isHindi) "कृपया प्रोडक्ट चुनें" else "Please select a product"
                            else -> null
                        }
                        if (validationError != null) return@Button
                    }

                    validationError = null
                    onSaveExpense(
                        selectedCategoryId,
                        selectedBranchId.ifBlank { null },
                        title.trim(),
                        amountVal,
                        paymentMode,
                        expenseDate,
                        refType,
                        referenceId,
                        paidTo.trim().ifBlank { null },
                        notes.trim().ifBlank { null }
                    )
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = if (isHindi) "खर्च दर्ज करें (Save Voucher)" else "Record Expense (Save Voucher)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

