package com.vidyasetuai.feature_store.presentation.screen.role_staff.inventory

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
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.domain.model.ItemWithStockUiModel
import com.vidyasetuai.feature_store.presentation.screen.role_staff.inventory.components.StockAdjustmentBottomSheet
import com.vidyasetuai.feature_store.presentation.screen.role_staff.inventory.components.StockTimelineBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryStockControlScreen(
    isHindi: Boolean = false,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: InventoryStockViewModel = remember(context) {
        InventoryStockViewModel(context.applicationContext)
    }
    val uiState by viewModel.uiState.collectAsState()

    // Native Back Handler (Pillar 5 Resilience)
    BackHandler {
        if (uiState.isAdjustmentModalOpen) {
            viewModel.closeAdjustmentModal()
        } else if (uiState.isTimelineModalOpen) {
            viewModel.closeTimeline()
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
                            text = if (isHindi) "इन्वेंटरी व स्टॉक कंट्रोल" else "Inventory & Stock Control",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "${uiState.filteredItems.size} ट्रैक किए गए उत्पाद" else "${uiState.filteredItems.size} Tracked Products",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- 1. KPI SUMMARY METRIC CARDS (4-Pill Grid) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = if (isHindi) "स्टॉक वैल्यू" else "Stock Value",
                    value = "₹${"%.0f".format(uiState.totalInventoryValue)}",
                    color = AppColors.EmeraldGreen,
                    modifier = Modifier.weight(1.3f)
                )
                MetricCard(
                    title = if (isHindi) "कुल उत्पाद" else "Tracked",
                    value = uiState.totalTrackedItemsCount.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(0.9f)
                )
                MetricCard(
                    title = if (isHindi) "कम स्टॉक" else "Low Stock",
                    value = uiState.lowStockCount.toString(),
                    color = Color(0xFFD97706),
                    modifier = Modifier.weight(0.9f)
                )
                MetricCard(
                    title = if (isHindi) "स्टॉक खत्म" else "Out Stock",
                    value = uiState.outOfStockCount.toString(),
                    color = Color(0xFFDC2626),
                    modifier = Modifier.weight(0.9f)
                )
            }

            // --- 2. SEARCH BAR ---
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text(if (isHindi) "नाम, बारकोड या SKU से खोजें..." else "Search by name, barcode, SKU...") },
                leadingIcon = {
                    Icon(
                        imageVector = Lucide.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // --- 3. CATEGORY CHIPS CAROUSEL ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedCategoryId.isBlank(),
                    onClick = { viewModel.setSelectedCategoryId("") },
                    label = { Text(if (isHindi) "सभी श्रेणियां" else "All Categories") },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppColors.EmeraldGreen.copy(alpha = 0.15f),
                        selectedLabelColor = AppColors.EmeraldGreen
                    )
                )

                uiState.categories.forEach { cat ->
                    val isSelected = uiState.selectedCategoryId == cat.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedCategoryId(cat.id) },
                        label = { Text(cat.name) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppColors.EmeraldGreen.copy(alpha = 0.15f),
                            selectedLabelColor = AppColors.EmeraldGreen
                        )
                    )
                }
            }

            // --- 4. STOCK STATUS FILTER PILLS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val statusFilters = listOf(
                    Pair(StockStatusFilter.ALL, if (isHindi) "सभी" else "All"),
                    Pair(StockStatusFilter.IN_STOCK, if (isHindi) "🟢 उपलब्ध (In Stock)" else "🟢 In Stock"),
                    Pair(StockStatusFilter.LOW_STOCK, if (isHindi) "🟠 कम स्टॉक (Low)" else "🟠 Low Stock"),
                    Pair(StockStatusFilter.OUT_OF_STOCK, if (isHindi) "🔴 खत्म (Out)" else "🔴 Out of Stock")
                )

                statusFilters.forEach { (filter, label) ->
                    val isSelected = uiState.stockStatusFilter == filter
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.clickable { viewModel.setStockStatusFilter(filter) }
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // --- 5. INVENTORY PRODUCT CARDS LIST ---
            if (uiState.filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Boxes,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = if (isHindi) "कोई उत्पाद नहीं मिला" else "No inventory items found",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
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
                        InventoryProductCard(
                            itemWithStock = itemWithStock,
                            isHindi = isHindi,
                            onAdjustStock = { viewModel.openAdjustmentModal(itemWithStock) },
                            onOpenTimeline = { viewModel.openTimeline(itemWithStock) }
                        )
                    }
                }
            }
        }
    }

    // Modal 1: Stock Adjustment Bottom Sheet
    if (uiState.isAdjustmentModalOpen && uiState.selectedAdjustmentItem != null) {
        StockAdjustmentBottomSheet(
            itemWithStock = uiState.selectedAdjustmentItem!!,
            isHindi = isHindi,
            isSubmitting = uiState.isSubmittingAdjustment,
            onDismiss = { viewModel.closeAdjustmentModal() },
            onSubmit = { itemId, newStock, reason, notes ->
                viewModel.submitStockAdjustment(itemId, newStock, reason, notes)
            }
        )
    }

    // Modal 2: Stock Timeline Passbook Bottom Sheet
    if (uiState.isTimelineModalOpen && uiState.selectedTimelineItem != null) {
        StockTimelineBottomSheet(
            itemWithStock = uiState.selectedTimelineItem!!,
            history = uiState.itemTimelineHistory,
            isLoading = uiState.isTimelineLoading,
            isHindi = isHindi,
            onDismiss = { viewModel.closeTimeline() }
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun InventoryProductCard(
    itemWithStock: ItemWithStockUiModel,
    isHindi: Boolean = false,
    onAdjustStock: () -> Unit,
    onOpenTimeline: () -> Unit
) {
    val item = itemWithStock.item
    val valuation = itemWithStock.currentStock * (if (item.purchasePrice > 0) item.purchasePrice else item.salePrice)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Name, Category Pill, SKU
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
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
                    }
                }

                // Live Stock Badge
                val badgeColor = when {
                    itemWithStock.isOutOfStock -> Color(0xFFDC2626)
                    itemWithStock.isLowStock -> Color(0xFFD97706)
                    else -> Color(0xFF16A34A)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = badgeColor.copy(alpha = 0.12f)
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
                                .background(badgeColor)
                        )
                        Text(
                            text = "${"%.1f".format(itemWithStock.currentStock)} ${item.unit}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

            // Row 2: Valuation Details + 2 Action CTAs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Asset Valuation
                Column {
                    Text(
                        text = "Valuation: ₹${"%.2f".format(valuation)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (item.purchasePrice > 0) {
                        Text(
                            text = "Cost: ₹${"%.2f".format(item.purchasePrice)} / ${item.unit}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onOpenTimeline,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.History,
                            contentDescription = "Passbook",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isHindi) "पासबुक" else "Passbook", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onAdjustStock,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.SlidersHorizontal,
                            contentDescription = "Adjust",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isHindi) "एडजस्ट" else "Adjust", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
