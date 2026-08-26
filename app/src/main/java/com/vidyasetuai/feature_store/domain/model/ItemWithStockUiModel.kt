package com.vidyasetuai.feature_store.domain.model

import com.vidyasetuai.feature_store.data.local.entity.ItemCategoryEntity
import com.vidyasetuai.feature_store.data.local.entity.ItemEntity

data class ItemWithStockUiModel(
    val item: ItemEntity,
    val category: ItemCategoryEntity? = null,
    val currentStock: Double = 0.0,
    val lowStockThreshold: Double = 5.0
) {
    val isLowStock: Boolean
        get() = currentStock <= lowStockThreshold && currentStock > 0

    val isOutOfStock: Boolean
        get() = currentStock <= 0
}
