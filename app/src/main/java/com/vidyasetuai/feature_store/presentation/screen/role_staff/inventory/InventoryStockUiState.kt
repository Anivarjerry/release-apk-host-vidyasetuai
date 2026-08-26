package com.vidyasetuai.feature_store.presentation.screen.role_staff.inventory

import com.vidyasetuai.feature_store.data.local.entity.InventoryTransactionEntity
import com.vidyasetuai.feature_store.data.local.entity.ItemCategoryEntity
import com.vidyasetuai.feature_store.domain.model.ItemWithStockUiModel

data class InventoryStockUiState(
    val businessId: String = "",
    val branchId: String? = null,
    val items: List<ItemWithStockUiModel> = emptyList(),
    val categories: List<ItemCategoryEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isSubmittingAdjustment: Boolean = false,
    val isTimelineLoading: Boolean = false,

    // Filters
    val searchQuery: String = "",
    val selectedCategoryId: String = "",
    val stockStatusFilter: StockStatusFilter = StockStatusFilter.ALL,

    // Modal States
    val isAdjustmentModalOpen: Boolean = false,
    val selectedAdjustmentItem: ItemWithStockUiModel? = null,
    val isTimelineModalOpen: Boolean = false,
    val selectedTimelineItem: ItemWithStockUiModel? = null,
    val itemTimelineHistory: List<InventoryTransactionEntity> = emptyList(),

    // Feedback Messages
    val successMessage: String? = null,
    val errorMessage: String? = null
) {
    // Calculated Summary Metrics (1:1 Web Parity)
    val totalInventoryValue: Double
        get() = items.filter { it.item.itemType == "PRODUCT" }
            .sumOf { it.currentStock * (if (it.item.purchasePrice > 0) it.item.purchasePrice else it.item.salePrice) }

    val totalTrackedItemsCount: Int
        get() = items.count { it.item.itemType == "PRODUCT" }

    val lowStockCount: Int
        get() = items.count { it.item.itemType == "PRODUCT" && it.isLowStock && !it.isOutOfStock }

    val outOfStockCount: Int
        get() = items.count { it.item.itemType == "PRODUCT" && it.isOutOfStock }

    // Filtered Items for List Rendering
    val filteredItems: List<ItemWithStockUiModel>
        get() = items.filter { itemWithStock ->
            // Only track inventory for physical products (Service items are excluded from stock table)
            val isProduct = itemWithStock.item.itemType == "PRODUCT"

            val matchesSearch = if (searchQuery.isBlank()) true else {
                val q = searchQuery.trim().lowercase()
                itemWithStock.item.name.lowercase().contains(q) ||
                        itemWithStock.item.barcode?.lowercase()?.contains(q) == true ||
                        itemWithStock.item.sku?.lowercase()?.contains(q) == true
            }

            val matchesCategory = if (selectedCategoryId.isBlank()) true else {
                itemWithStock.item.categoryId == selectedCategoryId
            }

            val matchesStatus = when (stockStatusFilter) {
                StockStatusFilter.ALL -> true
                StockStatusFilter.IN_STOCK -> !itemWithStock.isLowStock && !itemWithStock.isOutOfStock
                StockStatusFilter.LOW_STOCK -> itemWithStock.isLowStock && !itemWithStock.isOutOfStock
                StockStatusFilter.OUT_OF_STOCK -> itemWithStock.isOutOfStock
            }

            isProduct && matchesSearch && matchesCategory && matchesStatus
        }
}

enum class StockStatusFilter {
    ALL,
    IN_STOCK,
    LOW_STOCK,
    OUT_OF_STOCK
}
