package com.vidyasetuai.feature_store.domain.repository

import com.vidyasetuai.feature_store.data.local.entity.InventoryTransactionEntity
import com.vidyasetuai.feature_store.data.local.entity.ItemCategoryEntity
import com.vidyasetuai.feature_store.data.local.entity.ItemEntity
import com.vidyasetuai.feature_store.domain.model.ItemWithStockUiModel
import kotlinx.coroutines.flow.Flow

interface StoreCatalogRepository {
    fun getItemsWithStockFlow(businessId: String): Flow<List<ItemWithStockUiModel>>
    fun getCategoriesFlow(businessId: String): Flow<List<ItemCategoryEntity>>
    fun getItemTimelineFlow(itemId: String): Flow<List<InventoryTransactionEntity>>

    suspend fun createItemWithStock(
        businessId: String,
        branchId: String?,
        item: ItemEntity,
        initialStock: Double,
        lowStockThreshold: Double
    ): Result<ItemEntity>

    suspend fun updateItem(item: ItemEntity): Result<Unit>
    suspend fun deleteItem(itemId: String): Result<Unit>
    suspend fun createCategory(businessId: String, name: String, displayOrder: Int = 0): Result<ItemCategoryEntity>
    suspend fun deleteCategory(categoryId: String): Result<Unit>

    suspend fun adjustStock(
        businessId: String,
        branchId: String?,
        itemId: String,
        adjustedStock: Double,
        reason: String,
        notes: String?
    ): Result<Boolean>

    suspend fun syncItemTimeline(itemId: String): Result<Unit>
}
