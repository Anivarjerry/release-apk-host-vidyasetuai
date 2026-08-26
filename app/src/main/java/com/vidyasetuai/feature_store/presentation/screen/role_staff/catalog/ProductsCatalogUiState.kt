package com.vidyasetuai.feature_store.presentation.screen.role_staff.catalog

import com.vidyasetuai.feature_store.data.local.entity.ItemCategoryEntity
import com.vidyasetuai.feature_store.domain.model.ItemWithStockUiModel

data class ProductsCatalogUiState(
    val businessId: String = "",
    val branchId: String? = null,
    val items: List<ItemWithStockUiModel> = emptyList(),
    val categories: List<ItemCategoryEntity> = emptyList(),
    val selectedCategoryId: String = "ALL",
    val selectedFoodType: String = "ALL", // "ALL", "VEG", "NON_VEG", "EGG"
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isAddEditOpen: Boolean = false,
    val editingItem: ItemWithStockUiModel? = null,
    val isCategoryModalOpen: Boolean = false,
    val branches: List<com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity> = emptyList(),
    val isStockTransferOpen: Boolean = false,
    val isTransferringStock: Boolean = false,
    val branchStockMap: Map<String, Double> = emptyMap(),
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val filteredItems: List<ItemWithStockUiModel>
        get() {
            return items.filter { itemWithStock ->
                val item = itemWithStock.item
                // 1. Category Filter
                val matchesCategory = if (selectedCategoryId == "ALL") true else item.categoryId == selectedCategoryId
                
                // 2. Food Type Filter
                val matchesFoodType = when (selectedFoodType) {
                    "ALL" -> true
                    "VEG" -> item.foodType?.equals("VEG", ignoreCase = true) == true
                    "NON_VEG" -> item.foodType?.equals("NON_VEG", ignoreCase = true) == true
                    "EGG" -> item.foodType?.equals("EGG", ignoreCase = true) == true
                    else -> true
                }

                // 3. Search Query (Name or Barcode or SKU)
                val matchesSearch = if (searchQuery.isBlank()) true else {
                    val query = searchQuery.trim().lowercase()
                    item.name.lowercase().contains(query) ||
                            (item.barcode?.lowercase()?.contains(query) == true) ||
                            (item.sku?.lowercase()?.contains(query) == true)
                }

                matchesCategory && matchesFoodType && matchesSearch
            }
        }

    val totalItemsCount: Int
        get() = items.size

    val lowStockCount: Int
        get() = items.count { it.isLowStock }

    val outOfStockCount: Int
        get() = items.count { it.isOutOfStock }
}
