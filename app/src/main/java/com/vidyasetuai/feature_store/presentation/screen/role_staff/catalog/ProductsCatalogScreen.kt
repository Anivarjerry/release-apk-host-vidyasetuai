package com.vidyasetuai.feature_store.presentation.screen.role_staff.catalog

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.domain.model.ItemWithStockUiModel
import com.vidyasetuai.feature_store.presentation.screen.role_staff.catalog.components.AddEditProductBottomSheet
import com.vidyasetuai.feature_store.presentation.screen.role_staff.catalog.components.ManageCategoriesDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsCatalogScreen(
    isHindi: Boolean = false,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ProductsCatalogViewModel = remember(context) {
        ProductsCatalogViewModel(context.applicationContext)
    }
    val uiState by viewModel.uiState.collectAsState()

    var productToDelete by remember { mutableStateOf<ItemWithStockUiModel?>(null) }

    // Native Back Handler (Pillar 5 Resilience)
    BackHandler(enabled = true) {
        if (uiState.isAddEditOpen) {
            viewModel.closeAddEdit()
        } else if (uiState.isCategoryModalOpen) {
            viewModel.closeCategoryModal()
        } else if (uiState.isStockTransferOpen) {
            viewModel.closeStockTransfer()
        } else if (productToDelete != null) {
            productToDelete = null
        } else {
            onBack()
        }
    }

    // Toast Notifications
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isHindi) "सामान व मेनू कैटलॉग" else "Products & Menu Catalog",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isHindi) "${uiState.filteredItems.size} सामान उपलब्ध" else "${uiState.filteredItems.size} Products Listed",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Stock Transfer Button (Always accessible with guidance)
                    IconButton(onClick = {
                        if (uiState.branches.size > 1) {
                            viewModel.openStockTransfer()
                        } else {
                            Toast.makeText(
                                context,
                                if (isHindi) "स्टॉक ट्रांसफर के लिए कम से कम 2 शाखाएं (Branches) होनी चाहिए।" else "Stock transfer requires at least 2 branches.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }) {
                        Icon(
                            imageVector = Lucide.ArrowRightLeft,
                            contentDescription = "Transfer Stock",
                            tint = AppColors.EmeraldGreen
                        )
                    }

                    // Manage Categories Button
                    IconButton(onClick = { viewModel.openCategoryModal() }) {
                        Icon(
                            imageVector = Lucide.Layers,
                            contentDescription = "Categories",
                            tint = AppColors.EmeraldGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddProduct() },
                containerColor = AppColors.EmeraldGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = {
                    Icon(
                        imageVector = Lucide.Plus,
                        contentDescription = "Add Product"
                    )
                },
                text = {
                    Text(
                        text = if (isHindi) "नया सामान जोड़ें" else "Add Product",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. Search Bar & Metric Badges
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search TextField
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text(if (isHindi) "नाम या बारकोड से खोजें..." else "Search by name, barcode...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Lucide.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Lucide.X, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Metric Summary Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBadge(
                        label = if (isHindi) "कुल सामान" else "Total",
                        count = uiState.totalItemsCount,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBadge(
                        label = if (isHindi) "कम स्टॉक" else "Low Stock",
                        count = uiState.lowStockCount,
                        color = Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                    MetricBadge(
                        label = if (isHindi) "आउट ऑफ स्टॉक" else "Out of Stock",
                        count = uiState.outOfStockCount,
                        color = Color(0xFFDC2626),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 2. Category Filter Pills & Stock Transfer Quick Action (Horizontal Scroll)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Stock Transfer Quick Action Chip
                AssistChip(
                    onClick = {
                        if (uiState.branches.size > 1) {
                            viewModel.openStockTransfer()
                        } else {
                            Toast.makeText(
                                context,
                                if (isHindi) "स्टॉक ट्रांसफर के लिए कम से कम 2 शाखाएं (Branches) होनी चाहिए।" else "Stock transfer requires at least 2 branches.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Lucide.ArrowRightLeft,
                            contentDescription = "Stock Transfer",
                            modifier = Modifier.size(15.dp),
                            tint = AppColors.EmeraldGreen
                        )
                    },
                    label = {
                        Text(
                            text = if (isHindi) "स्टॉक ट्रांसफर" else "Stock Transfer",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = AppColors.EmeraldGreen
                        )
                    },
                    border = AssistChipDefaults.assistChipBorder(borderColor = AppColors.EmeraldGreen.copy(alpha = 0.4f))
                )

                // "All" Category Pill
                FilterChip(
                    selected = uiState.selectedCategoryId == "ALL",
                    onClick = { viewModel.selectCategory("ALL") },
                    label = { Text(if (isHindi) "सभी श्रेणियां" else "All Categories") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppColors.EmeraldGreen.copy(alpha = 0.15f),
                        selectedLabelColor = AppColors.EmeraldGreen
                    )
                )

                uiState.categories.forEach { cat ->
                    FilterChip(
                        selected = uiState.selectedCategoryId == cat.id,
                        onClick = { viewModel.selectCategory(cat.id) },
                        label = { Text(cat.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppColors.EmeraldGreen.copy(alpha = 0.15f),
                            selectedLabelColor = AppColors.EmeraldGreen
                        )
                    )
                }
            }

            // 3. Food Type Quick Filters (Veg / Non-Veg / Egg)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val foodFilters = listOf(
                    Pair("ALL", if (isHindi) "सभी" else "All"),
                    Pair("VEG", if (isHindi) "🟢 शाकाहारी" else "🟢 Veg"),
                    Pair("NON_VEG", if (isHindi) "🔴 मांसाहारी" else "🔴 Non-Veg"),
                    Pair("EGG", if (isHindi) "🟡 अंडा" else "🟡 Egg")
                )

                foodFilters.forEach { (type, label) ->
                    val isSelected = uiState.selectedFoodType == type
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .clickable { viewModel.selectFoodType(type) }
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 4. Products LazyColumn
            if (uiState.filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.PackageOpen,
                            contentDescription = "Empty",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = if (isHindi) "कोई सामान नहीं मिला" else "No products found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isHindi) "नया सामान जोड़ने के लिए नीचे '+' बटन दबाएं" else "Tap '+' below to add your first product",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.filteredItems, key = { it.item.id }) { itemWithStock ->
                        ProductItemCard(
                            itemWithStock = itemWithStock,
                            isHindi = isHindi,
                            onEdit = { viewModel.openEditProduct(itemWithStock) },
                            onDelete = { productToDelete = itemWithStock }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Add / Edit Product Bottom Sheet
    if (uiState.isAddEditOpen) {
        AddEditProductBottomSheet(
            isHindi = isHindi,
            editingItem = uiState.editingItem,
            categories = uiState.categories,
            isSaving = uiState.isSaving,
            onDismiss = { viewModel.closeAddEdit() },
            onOpenCategoryModal = { viewModel.openCategoryModal() },
            onGenerateSku = { viewModel.generateSku(it) },
            onSave = { name, catId, itemType, desc, sku, barcode, hsn, sPrice, mrp, pPrice, taxRate, isTaxInc, unit, fType, isOnline, imgUrl, extraImgs, initStock, lowStock ->
                viewModel.saveProduct(
                    name = name,
                    categoryId = catId,
                    itemType = itemType,
                    description = desc,
                    sku = sku,
                    barcode = barcode,
                    hsnSacCode = hsn,
                    salePrice = sPrice,
                    mrp = mrp,
                    purchasePrice = pPrice,
                    taxRate = taxRate,
                    isTaxInclusive = isTaxInc,
                    unit = unit,
                    foodType = fType,
                    isAvailableOnline = isOnline,
                    imageUrl = imgUrl,
                    extraImages = extraImgs,
                    initialStock = initStock,
                    lowStockThreshold = lowStock
                )
            }
        )
    }

    // Manage Categories Modal Dialog
    if (uiState.isCategoryModalOpen) {
        ManageCategoriesDialog(
            isHindi = isHindi,
            categories = uiState.categories,
            onDismiss = { viewModel.closeCategoryModal() },
            onCreateCategory = { viewModel.createCategory(it) },
            onDeleteCategory = { viewModel.deleteCategory(it) }
        )
    }

    // Multi-Branch Stock Transfer Bottom Sheet
    if (uiState.isStockTransferOpen) {
        com.vidyasetuai.feature_store.presentation.screen.role_staff.catalog.components.BranchStockTransferBottomSheet(
            isHindi = isHindi,
            branches = uiState.branches,
            items = uiState.items.map { it.item },
            sourceStocks = uiState.branchStockMap,
            isTransferring = uiState.isTransferringStock,
            onDismiss = { viewModel.closeStockTransfer() },
            onExecuteTransfer = { fromBranchId, toBranchId, itemId, quantity, notes ->
                viewModel.executeStockTransfer(fromBranchId, toBranchId, itemId, quantity, notes)
            }
        )
    }

    // Delete Product Confirmation Dialog
    productToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text(if (isHindi) "सामान हटाएं?" else "Delete Product?") },
            text = {
                Text(
                    if (isHindi) {
                        "क्या आप '${target.item.name}' को हटाना चाहते हैं? यह POS बिलिंग में नहीं दिखेगा।"
                    } else {
                        "Are you sure you want to delete '${target.item.name}'? It will no longer appear in POS billing."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProduct(target.item.id)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (isHindi) "हटाएं" else "Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun MetricBadge(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ProductItemCard(
    itemWithStock: ItemWithStockUiModel,
    isHindi: Boolean = false,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val item = itemWithStock.item
    val isService = item.itemType == "SERVICE"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Food Type Badge + Name + Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Veg / Non-Veg Indicator Symbol
                    FoodTypeIndicator(foodType = item.foodType)

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (isService) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Text(
                                        text = "SERVICE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            itemWithStock.category?.name?.let { catName ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = catName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            if (!item.sku.isNullOrBlank()) {
                                Text(
                                    text = "SKU: ${item.sku}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (!item.barcode.isNullOrBlank()) {
                                Text(
                                    text = "• ${item.barcode}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Edit & Delete Actions
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Pencil,
                            contentDescription = "Edit",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Trash2,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

            // Row 2: Price Details & Live Stock Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pricing
                Column {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "₹${"%.2f".format(item.salePrice)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.EmeraldGreen
                        )
                        Text(
                            text = "/ ${item.unit}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (item.mrp != null && item.mrp > item.salePrice) {
                            Text(
                                text = "MRP ₹${"%.2f".format(item.mrp)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                            )
                        }
                    }
                    if (item.purchasePrice > 0) {
                        Text(
                            text = "Cost: ₹${"%.2f".format(item.purchasePrice)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Live Stock Status Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when {
                        isService -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        itemWithStock.isOutOfStock -> Color(0xFFDC2626).copy(alpha = 0.12f)
                        itemWithStock.isLowStock -> Color(0xFFD97706).copy(alpha = 0.12f)
                        else -> Color(0xFF16A34A).copy(alpha = 0.12f)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isService -> Color(0xFF6B7280)
                                        itemWithStock.isOutOfStock -> Color(0xFFDC2626)
                                        itemWithStock.isLowStock -> Color(0xFFD97706)
                                        else -> Color(0xFF16A34A)
                                    }
                                )
                        )
                        Text(
                            text = when {
                                isService -> if (isHindi) "सर्विस" else "Service"
                                itemWithStock.isOutOfStock -> if (isHindi) "आउट ऑफ स्टॉक" else "Out of Stock"
                                itemWithStock.isLowStock -> if (isHindi) "${itemWithStock.currentStock.toInt()} ${item.unit} (कम)" else "${itemWithStock.currentStock.toInt()} ${item.unit} (Low)"
                                else -> "${itemWithStock.currentStock.toInt()} ${item.unit}"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isService -> MaterialTheme.colorScheme.onSurfaceVariant
                                itemWithStock.isOutOfStock -> Color(0xFFDC2626)
                                itemWithStock.isLowStock -> Color(0xFFD97706)
                                else -> Color(0xFF16A34A)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodTypeIndicator(foodType: String?) {
    val color = when (foodType?.uppercase()) {
        "VEG" -> Color(0xFF16A34A)
        "NON_VEG" -> Color(0xFFDC2626)
        "EGG" -> Color(0xFFEAB308)
        else -> Color(0xFF9CA3AF)
    }

    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color.Transparent)
            .padding(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(3.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(color)),
            color = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}
