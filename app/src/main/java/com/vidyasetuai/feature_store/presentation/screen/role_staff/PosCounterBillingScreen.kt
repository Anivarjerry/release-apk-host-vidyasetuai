package com.vidyasetuai.feature_store.presentation.screen.role_staff

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.data.local.entity.ItemEntity
import com.vidyasetuai.feature_store.data.remote.dto.PosCustomerPartyDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosCounterBillingScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: PosCounterViewModel = remember(context) {
        PosCounterViewModel(context.applicationContext)
    }
    val isDark = isSystemInDarkTheme()
    val uiState by viewModel.uiState.collectAsState()

    var isBranchSheetOpen by remember { mutableStateOf(false) }
    var isCheckoutSheetOpen by remember { mutableStateOf(false) }
    var isPartySearchOpen by remember { mutableStateOf(false) }
    var partySearchQuery by remember { mutableStateOf("") }

    // Device Hardware & Gesture Back Button Handler
    BackHandler {
        when {
            uiState.checkoutSuccessInvoice != null -> {
                viewModel.dismissSuccessInvoice()
            }
            isPartySearchOpen -> {
                isPartySearchOpen = false
            }
            isCheckoutSheetOpen -> {
                isCheckoutSheetOpen = false
            }
            isBranchSheetOpen -> {
                isBranchSheetOpen = false
            }
            else -> {
                onNavigateBack()
            }
        }
    }

    // Color Palette
    val bgColor = if (isDark) Color(0xFF000000) else Color(0xFFFAFAFA)
    val cardBg = if (isDark) Color(0xFF141414) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF242424) else Color(0xFFE5E7EB)
    val textColor = if (isDark) Color(0xFFF3F4F6) else Color(0xFF111827)
    val textSecondary = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    // Filter items by search and category
    val filteredItems = remember(uiState.items, uiState.searchQuery, uiState.selectedCategoryId) {
        uiState.items.filter { item ->
            val matchesCategory = (uiState.selectedCategoryId == "ALL" || item.categoryId == uiState.selectedCategoryId)
            val matchesSearch = uiState.searchQuery.isBlank() ||
                    item.name.contains(uiState.searchQuery, ignoreCase = true) ||
                    (item.barcode?.contains(uiState.searchQuery, ignoreCase = true) == true) ||
                    (item.sku?.contains(uiState.searchQuery, ignoreCase = true) == true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            // =========================================================================
            // 📌 1. TOP APP BAR & ACTIVE BRANCH CONTEXT SELECTOR
            // =========================================================================
            Surface(
                color = cardBg,
                shadowElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = cardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.ArrowLeft,
                                    contentDescription = "Back",
                                    tint = textColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "POS Billing Counter",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )

                                // Branch Pill Selector Button
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                                    modifier = Modifier.clickable { isBranchSheetOpen = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Lucide.MapPin,
                                            contentDescription = "Branch",
                                            tint = AppColors.EmeraldGreen,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = uiState.activeBranchName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AppColors.EmeraldGreen
                                        )
                                        Icon(
                                            imageVector = Lucide.ChevronDown,
                                            contentDescription = "Switch",
                                            tint = AppColors.EmeraldGreen,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Cart Floating Pill Badge in Top Bar
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (uiState.totalItemsCount > 0) AppColors.EmeraldGreen else cardBorder,
                            modifier = Modifier.clickable {
                                if (uiState.totalItemsCount > 0) isCheckoutSheetOpen = true
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = if (uiState.totalItemsCount > 0) Color.White else textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${uiState.totalItemsCount} • ₹${String.format("%.2f", uiState.itemsSubtotal)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.totalItemsCount > 0) Color.White else textSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // =========================================================================
            // 🛒 4. BOTTOM FLOATING CART CHECKOUT BAR
            // =========================================================================
            AnimatedVisibility(
                visible = uiState.totalItemsCount > 0,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Surface(
                    color = cardBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .border(width = 1.dp, color = cardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${uiState.totalItemsCount} ITEMS IN CART",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary
                            )
                            Text(
                                text = "₹${String.format("%.2f", uiState.grandTotal)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textColor
                            )
                        }

                        Button(
                            onClick = { isCheckoutSheetOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Checkout (F8)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = Lucide.ArrowRight,
                                    contentDescription = "Checkout",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgColor)
        ) {
            // =========================================================================
            // 🔍 2. SEARCH & BARCODE INPUT ROW
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text("Search items", fontSize = 13.sp, color = textSecondary)
                    },
                    leadingIcon = {
                        Icon(imageVector = Lucide.Search, contentDescription = "Search", tint = textSecondary, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(imageVector = Lucide.X, contentDescription = "Clear", tint = textSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardBg,
                        unfocusedContainerColor = cardBg,
                        focusedBorderColor = AppColors.EmeraldGreen,
                        unfocusedBorderColor = cardBorder
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (uiState.searchQuery.isNotBlank()) {
                            viewModel.handleBarcodeScan(uiState.searchQuery)
                        }
                    }),
                    modifier = Modifier.weight(1f)
                )

                // Quick Barcode Action Trigger
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = cardBg,
                    modifier = Modifier
                        .size(48.dp)
                        .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            Toast.makeText(context, "Barcode Scanner Active. Type or scan code.", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Lucide.Search,
                            contentDescription = "Scan Barcode",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // =========================================================================
            // 🏷️ 3. CATEGORY FILTER PILL TABS
            // =========================================================================
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "ALL" Category Chip
                item {
                    val isSelected = (uiState.selectedCategoryId == "ALL")
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) AppColors.EmeraldGreen else cardBg,
                        modifier = Modifier
                            .border(1.dp, if (isSelected) AppColors.EmeraldGreen else cardBorder, RoundedCornerShape(20.dp))
                            .clickable { viewModel.selectCategory("ALL") }
                    ) {
                        Text(
                            text = "All Items (${uiState.items.size})",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else textColor,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                // Dynamic Category Chips
                items(uiState.categories) { category ->
                    val isSelected = (uiState.selectedCategoryId == category.id)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) AppColors.EmeraldGreen else cardBg,
                        modifier = Modifier
                            .border(1.dp, if (isSelected) AppColors.EmeraldGreen else cardBorder, RoundedCornerShape(20.dp))
                            .clickable { viewModel.selectCategory(category.id) }
                    ) {
                        Text(
                            text = category.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else textColor,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // =========================================================================
            // 📦 4. PRODUCTS TOUCH GRID
            // =========================================================================
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Lucide.Package, contentDescription = "Empty", tint = textSecondary, modifier = Modifier.size(48.dp))
                        Text("No products found", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Text("Try searching another name or category.", fontSize = 12.sp, color = textSecondary)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = if (uiState.totalItemsCount > 0) 80.dp else 16.dp)
                ) {
                    items(filteredItems) { item ->
                        val cartLine = uiState.cart[item.id]
                        val qty = cartLine?.quantity ?: 0.0
                        val stock = uiState.stockMap[item.id] ?: 0.0
                        val threshold = uiState.lowStockThresholdMap[item.id] ?: 5.0

                        PosProductCard(
                            item = item,
                            quantityInCart = qty,
                            availableStock = stock,
                            lowStockThreshold = threshold,
                            allowNegativeStock = uiState.allowNegativeStock,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            textColor = textColor,
                            textSecondary = textSecondary,
                            onAddToCart = { viewModel.addToCart(item) },
                            onDecrease = { viewModel.decreaseQuantity(item) }
                        )
                    }
                }
            }
        }
    }

    // =========================================================================
    // 🏢 BOTTOM SHEET: Branch Switcher Modal
    // =========================================================================
    if (isBranchSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isBranchSheetOpen = false },
            containerColor = cardBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select Active Branch / Outlet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                uiState.branches.forEach { branch ->
                    val isSelected = (branch.id == uiState.activeBranchId)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AppColors.EmeraldGreen.copy(alpha = 0.12f) else bgColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (isSelected) AppColors.EmeraldGreen else cardBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.selectBranch(branch.id)
                                isBranchSheetOpen = false
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = branch.branchName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) AppColors.EmeraldGreen else textColor
                                )
                                Text(
                                    text = "${branch.addressLine}, ${branch.city}",
                                    fontSize = 12.sp,
                                    color = textSecondary
                                )
                            }
                            if (isSelected) {
                                Icon(imageVector = Lucide.Check, contentDescription = "Selected", tint = AppColors.EmeraldGreen, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // =========================================================================
    // 💳 BOTTOM SHEET: Checkout & Billing Modal
    // =========================================================================
    val checkoutSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (isCheckoutSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isCheckoutSheetOpen = false },
            sheetState = checkoutSheetState,
            containerColor = cardBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "POS Billing & Checkout",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    IconButton(onClick = { isCheckoutSheetOpen = false }) {
                        Icon(imageVector = Lucide.X, contentDescription = "Close", tint = textSecondary)
                    }
                }

                // =====================================================================
                // 📦 1. ORDER ITEMS BREAKDOWN (DIRECT TOP SECTION)
                // =====================================================================
                Text("Order Items (${uiState.totalItemsCount})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.cartItemsList.forEach { line ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, cardBorder, RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(line.item.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                                Text("₹${line.item.salePrice} x ${line.quantity.toInt()} ${line.item.unit}", fontSize = 11.sp, color = textSecondary)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = bgColor,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable { viewModel.decreaseQuantity(line.item) }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Lucide.Minus, contentDescription = "Minus", tint = textColor, modifier = Modifier.size(12.dp))
                                    }
                                }

                                Text("${line.quantity.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)

                                Surface(
                                    shape = CircleShape,
                                    color = bgColor,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable { viewModel.addToCart(line.item) }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Lucide.Plus, contentDescription = "Plus", tint = textColor, modifier = Modifier.size(12.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.width(4.dp))
                                Text("₹${String.format("%.2f", line.subtotal)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                            }
                        }
                    }
                }

                // =====================================================================
                // 💳 2. PAYMENT METHOD SEGMENTED SELECTOR
                // =====================================================================
                Text("Payment Method", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val modes = listOf(
                        Triple("CASH_ON_COUNTER", "Cash", Lucide.CreditCard),
                        Triple("UPI_QR", "UPI QR", Lucide.Smartphone),
                        Triple("CREDIT_KHATA", "Credit Khata", Lucide.BookOpen)
                    )

                    modes.forEach { (modeKey, modeTitle, modeIcon) ->
                        val isSelected = (uiState.paymentMethod == modeKey)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) AppColors.EmeraldGreen else bgColor,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, if (isSelected) AppColors.EmeraldGreen else cardBorder, RoundedCornerShape(10.dp))
                                .clickable { viewModel.setPaymentMethod(modeKey) }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = modeIcon, contentDescription = modeTitle, tint = if (isSelected) Color.White else textSecondary, modifier = Modifier.size(18.dp))
                                Text(modeTitle, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else textColor)
                            }
                        }
                    }
                }

                // =====================================================================
                // 💵 3A. CONTEXTUAL VIEW 1: CASH TENDER CALCULATOR
                // =====================================================================
                if (uiState.paymentMethod == "CASH_ON_COUNTER") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = bgColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Custom Cash Received Input Field + Exact Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = if (uiState.tenderedCash == 0.0) "" else if (uiState.tenderedCash % 1.0 == 0.0) uiState.tenderedCash.toInt().toString() else String.format(java.util.Locale.US, "%.2f", uiState.tenderedCash),
                                    onValueChange = { str ->
                                        val cleaned = str.filter { it.isDigit() || it == '.' }
                                        val num = cleaned.toDoubleOrNull() ?: 0.0
                                        viewModel.setTenderedCash(num)
                                    },
                                    label = { Text("Enter Cash Received (₹)", fontSize = 11.sp) },
                                    placeholder = { Text("e.g. ${uiState.grandTotal.toInt()}", fontSize = 12.sp) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (uiState.tenderedCash == uiState.grandTotal) AppColors.EmeraldGreen else AppColors.EmeraldGreen.copy(alpha = 0.12f),
                                    modifier = Modifier
                                        .border(1.dp, AppColors.EmeraldGreen, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setTenderedCash(uiState.grandTotal) }
                                        .padding(horizontal = 14.dp, vertical = 14.dp)
                                ) {
                                    Text(
                                        text = "Exact",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.tenderedCash == uiState.grandTotal) Color.White else AppColors.EmeraldGreen
                                    )
                                }
                            }

                            // Quick Rupay Note Buttons (+100, +200, +500, +2000)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(100.0, 200.0, 500.0, 2000.0).forEach { note ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = cardBg,
                                        modifier = Modifier
                                            .weight(1f)
                                            .border(1.dp, cardBorder, RoundedCornerShape(6.dp))
                                            .clickable { viewModel.setTenderedCash(note) }
                                    ) {
                                        Text(
                                            text = "₹${note.toInt()}",
                                            textAlign = TextAlign.Center,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            // Smart Status Badge: Change Due or Shortage Alert
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when {
                                    uiState.isCashShort -> Color(0xFFFEE2E2)
                                    uiState.tenderedCash > 0 -> AppColors.EmeraldGreen.copy(alpha = 0.12f)
                                    else -> cardBg
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        when {
                                            uiState.isCashShort -> Color(0xFFEF4444)
                                            uiState.tenderedCash > 0 -> AppColors.EmeraldGreen
                                            else -> cardBorder
                                        },
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = when {
                                            uiState.isCashShort -> "⚠️ Cash Short (पैसे कम हैं):"
                                            uiState.tenderedCash > 0 -> "✓ Return Change to Customer:"
                                            else -> "Status:"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = when {
                                            uiState.isCashShort -> Color(0xFFDC2626)
                                            uiState.tenderedCash > 0 -> AppColors.EmeraldGreen
                                            else -> textSecondary
                                        }
                                    )
                                    Text(
                                        text = when {
                                            uiState.isCashShort -> "-₹${String.format(java.util.Locale.US, "%.2f", uiState.cashShortageAmount)}"
                                            uiState.tenderedCash > 0 -> "₹${String.format(java.util.Locale.US, "%.2f", uiState.changeDue)}"
                                            else -> "Exact Cash"
                                        },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = when {
                                            uiState.isCashShort -> Color(0xFFDC2626)
                                            uiState.tenderedCash > 0 -> AppColors.EmeraldGreen
                                            else -> textColor
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // =====================================================================
                // 📱 3B. CONTEXTUAL VIEW 2: DYNAMIC UPI QR SCAN & PAY
                // =====================================================================
                if (uiState.paymentMethod == "UPI_QR") {
                    val upiQrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=" +
                            java.net.URLEncoder.encode("upi://pay?pa=${uiState.upiId}&pn=VidyaSetuStore&am=${String.format(java.util.Locale.US, "%.2f", uiState.grandTotal)}&cu=INR", "UTF-8")

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = bgColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                shadowElevation = 1.dp,
                                modifier = Modifier.size(140.dp)
                            ) {
                                AsyncImage(
                                    model = upiQrUrl,
                                    contentDescription = "UPI QR",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                )
                            }

                            Text(
                                text = "Scan & Pay ₹${String.format("%.2f", uiState.grandTotal)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Text(
                                text = "Accepts Google Pay, PhonePe, Paytm, BHIM UPI",
                                fontSize = 11.sp,
                                color = textSecondary
                            )
                        }
                    }
                }

                // =====================================================================
                // 📖 3C. CONTEXTUAL VIEW 3: CREDIT KHATA CUSTOMER PARTY SELECTOR
                // =====================================================================
                if (uiState.paymentMethod == "CREDIT_KHATA") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = bgColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppColors.EmeraldGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Khata Customer (उधारी खाता):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
                                if (uiState.selectedParty != null) {
                                    Text(
                                        text = "Clear",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF4444),
                                        modifier = Modifier.clickable { viewModel.selectParty(null) }
                                    )
                                }
                            }

                            if (uiState.selectedParty != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = cardBg,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, AppColors.EmeraldGreen, RoundedCornerShape(8.dp))
                                        .clickable { isPartySearchOpen = true }
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(uiState.selectedParty!!.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                                            Text("📞 ${uiState.selectedParty!!.phone ?: "No Phone"}", fontSize = 11.sp, color = textSecondary)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Previous Due", fontSize = 10.sp, color = textSecondary)
                                            Text("₹${uiState.selectedParty!!.currentBalance}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                        }
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { isPartySearchOpen = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen.copy(alpha = 0.12f)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(imageVector = Lucide.Search, contentDescription = "Search", tint = AppColors.EmeraldGreen, modifier = Modifier.size(14.dp))
                                        Text("Select Customer from Khata Directory", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                                    }
                                }
                            }
                        }
                    }
                }

                // =====================================================================
                // 🧾 4. BILL CALCULATIONS SUMMARY
                // =====================================================================
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = bgColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Items Subtotal", fontSize = 12.sp, color = textSecondary)
                            Text("₹${String.format("%.2f", uiState.itemsSubtotal)}", fontSize = 12.sp, color = textColor)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tax & GST (CGST+SGST)", fontSize = 12.sp, color = textSecondary)
                            Text("₹${String.format("%.2f", uiState.totalTaxAmount)}", fontSize = 12.sp, color = textColor)
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp), color = cardBorder)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Grand Total", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                            Text("₹${String.format("%.2f", uiState.grandTotal)}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.EmeraldGreen)
                        }
                    }
                }

                // =====================================================================
                // 🍽️ 5. KITCHEN KDS DISPATCH TOGGLE (DEFAULT: OFF)
                // =====================================================================
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = bgColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, cardBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("🍽️ Send Order to Kitchen KDS (KOT)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
                            Text(
                                text = if (uiState.sendToKitchen) "Alerts Kitchen Screen with audio beep (Status: NEW)" else "Direct Counter Handover (Status: DELIVERED)",
                                fontSize = 10.sp,
                                color = textSecondary
                            )
                        }
                        Switch(
                            checked = uiState.sendToKitchen,
                            onCheckedChange = { viewModel.setSendToKitchen(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AppColors.EmeraldGreen,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = textSecondary.copy(alpha = 0.4f)
                            )
                        )
                    }
                }

                // Error Message Banner (if any)
                if (uiState.errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEE2E2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Complete Bill CTA Button
                Button(
                    onClick = {
                        viewModel.executeCheckout { invoice ->
                            isCheckoutSheetOpen = false
                        }
                    },
                    enabled = !uiState.isCheckingOut,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (uiState.isCheckingOut) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(
                            text = "⚡ Complete & Print Bill (₹${String.format("%.2f", uiState.grandTotal)})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // =========================================================================
    // 👥 MODAL: Search & Select Customer Party
    // =========================================================================
    if (isPartySearchOpen) {
        Dialog(onDismissRequest = { isPartySearchOpen = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cardBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Khata Customer", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)

                    OutlinedTextField(
                        value = partySearchQuery,
                        onValueChange = { partySearchQuery = it },
                        placeholder = { Text("Search party by name or phone...", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    val filteredParties = uiState.customerParties.filter {
                        partySearchQuery.isBlank() || it.name.contains(partySearchQuery, ignoreCase = true) || (it.phone?.contains(partySearchQuery) == true)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        filteredParties.forEach { party ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = bgColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectParty(party)
                                        isPartySearchOpen = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(party.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                                        Text("📞 ${party.phone ?: "No Phone"}", fontSize = 11.sp, color = textSecondary)
                                    }
                                    Text("Bal: ₹${party.currentBalance}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // 🧾 DIALOG: Successful Invoice Bill Generated
    // =========================================================================
    if (uiState.checkoutSuccessInvoice != null) {
        val invoice = uiState.checkoutSuccessInvoice!!
        Dialog(onDismissRequest = { viewModel.dismissSuccessInvoice() }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = AppColors.EmeraldGreen.copy(alpha = 0.15f),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Lucide.Check, contentDescription = "Success", tint = AppColors.EmeraldGreen, modifier = Modifier.size(32.dp))
                        }
                    }

                    Text(
                        text = "Bill Generated Successfully!",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = bgColor,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Invoice: ${invoice.invoiceNumber ?: "INV-26/0001"}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
                            Text("Order Number: ${invoice.orderNumber ?: "#ORD-0001"}", fontSize = 12.sp, color = textSecondary)
                            Text("Amount: ₹${String.format("%.2f", invoice.grandTotal)}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.EmeraldGreen)
                            Text("Payment: ${invoice.paymentMethod ?: "CASH"}", fontSize = 12.sp, color = textSecondary)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Thermal Receipt Sent to Bluetooth Printer", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Lucide.FileText, contentDescription = "Print", modifier = Modifier.size(14.dp))
                                Text("Print 2\"", fontSize = 12.sp)
                            }
                        }

                        Button(
                            onClick = { viewModel.dismissSuccessInvoice() },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+ Next Bill", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PosProductCard(
    item: ItemEntity,
    quantityInCart: Double,
    availableStock: Double = 0.0,
    lowStockThreshold: Double = 5.0,
    allowNegativeStock: Boolean = true,
    cardBg: Color,
    cardBorder: Color,
    textColor: Color,
    textSecondary: Color,
    onAddToCart: () -> Unit,
    onDecrease: () -> Unit
) {
    val isService = item.itemType == "SERVICE"
    val isOutOfStock = !isService && availableStock <= 0.0
    val isLowStock = !isService && availableStock > 0.0 && availableStock <= lowStockThreshold
    val isBlocked = isOutOfStock && !allowNegativeStock

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isBlocked) cardBg.copy(alpha = 0.7f) else cardBg,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isOutOfStock) Color(0xFFEF4444).copy(alpha = 0.5f) else cardBorder,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Top Image / Placeholder + Stock Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = cardBorder.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (!item.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Lucide.Package, contentDescription = "Item", tint = textSecondary, modifier = Modifier.size(28.dp))
                        }
                    }
                }

                // Top Stock Badge Chip
                Surface(
                    shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    color = when {
                        isService -> Color(0xFF3B82F6) // Blue for Services
                        isOutOfStock -> Color(0xFFEF4444) // Red for Out of Stock
                        isLowStock -> Color(0xFFF59E0B) // Amber for Low Stock
                        else -> Color(0xFF10B981) // Emerald for In Stock
                    },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = when {
                            isService -> "🍽️ Service"
                            isOutOfStock -> "🔴 Out of Stock"
                            isLowStock -> "⚠️ ${availableStock.toInt()} ${item.unit}"
                            else -> "📦 ${availableStock.toInt()} ${item.unit}"
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Item Name & Category
            Text(
                text = item.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isBlocked) textSecondary else textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Price & Tax
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "₹${item.salePrice}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isBlocked) textSecondary else AppColors.EmeraldGreen
                )

                if (item.taxRate > 0) {
                    Text(
                        text = "${item.taxRate.toInt()}% GST",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary
                    )
                }
            }

            // Fast Add Stepper / Button
            if (quantityInCart > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AppColors.EmeraldGreen.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Icon(
                            imageVector = Lucide.Minus,
                            contentDescription = "Minus",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onDecrease() }
                        )
                        Text(
                            text = "${quantityInCart.toInt()}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.EmeraldGreen
                        )
                        Icon(
                            imageVector = Lucide.Plus,
                            contentDescription = "Plus",
                            tint = if (isBlocked) textSecondary else AppColors.EmeraldGreen,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable(enabled = !isBlocked) { onAddToCart() }
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isBlocked) Color(0xFFE5E7EB) else AppColors.EmeraldGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isBlocked) { onAddToCart() }
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBlocked) "OUT OF STOCK" else "+ ADD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBlocked) Color(0xFF9CA3AF) else Color.White
                        )
                    }
                }
            }
        }
    }
}
