package com.vidyasetuai.feature_store.data.repository

import android.content.Context
import android.util.Log
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.dao.*
import com.vidyasetuai.feature_store.data.local.entity.*
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import com.vidyasetuai.feature_store.domain.model.ItemWithStockUiModel
import com.vidyasetuai.feature_store.domain.repository.StoreCatalogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class StoreCatalogRepositoryImpl(
    private val itemCategoryDao: ItemCategoryDao,
    private val itemDao: ItemDao,
    private val itemVariantDao: ItemVariantDao,
    private val inventoryStockDao: InventoryStockDao,
    private val inventoryTransactionDao: InventoryTransactionDao,
    private val remoteDataSource: StoreRemoteDataSource = StoreRemoteDataSource()
) : StoreCatalogRepository {

    constructor(context: Context) : this(
        StoreDatabase.getDatabase(context).itemCategoryDao(),
        StoreDatabase.getDatabase(context).itemDao(),
        StoreDatabase.getDatabase(context).itemVariantDao(),
        StoreDatabase.getDatabase(context).inventoryStockDao(),
        StoreDatabase.getDatabase(context).inventoryTransactionDao()
    )

    override fun getItemsWithStockFlow(businessId: String): Flow<List<ItemWithStockUiModel>> {
        return combine(
            itemDao.getStoreItemsFlow(businessId),
            itemCategoryDao.getCategoriesFlow(businessId),
            inventoryStockDao.getAllStocksFlow()
        ) { items, categories, stocks ->
            val catMap = categories.associateBy { it.id }
            val stockMap = stocks.associateBy { it.itemId }

            items.map { item ->
                val stockRecord = stockMap[item.id]
                ItemWithStockUiModel(
                    item = item,
                    category = item.categoryId?.let { catMap[it] },
                    currentStock = stockRecord?.currentStock ?: 0.0,
                    lowStockThreshold = stockRecord?.lowStockThreshold ?: 5.0
                )
            }
        }
    }

    override fun getCategoriesFlow(businessId: String): Flow<List<ItemCategoryEntity>> {
        return itemCategoryDao.getCategoriesFlow(businessId)
    }

    override suspend fun createItemWithStock(
        businessId: String,
        branchId: String?,
        item: ItemEntity,
        initialStock: Double,
        lowStockThreshold: Double
    ): Result<ItemEntity> = withContext(Dispatchers.IO) {
        try {
            // 1. Save directly to Supabase via single-flight safe invoker
            val insertedItem = remoteDataSource.createItemWithStock(
                item = item,
                branchId = branchId,
                initialStock = initialStock,
                lowStockThreshold = lowStockThreshold
            )

            val effectiveItem = insertedItem ?: item

            // 2. Immediately update local Room DB for instant UI reaction
            itemDao.upsert(effectiveItem)
            if (effectiveItem.itemType == "PRODUCT" && !branchId.isNullOrBlank()) {
                val stockEntity = InventoryStockEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    itemId = effectiveItem.id,
                    branchId = branchId,
                    currentStock = initialStock,
                    lowStockThreshold = lowStockThreshold,
                    isActive = true,
                    isDeleted = false,
                    createdAt = effectiveItem.createdAt,
                    updatedAt = effectiveItem.updatedAt
                )
                inventoryStockDao.upsert(stockEntity)
            }

            Result.success(effectiveItem)
        } catch (e: Exception) {
            Log.e("CatalogRepo", "Error creating item with stock: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun updateItem(item: ItemEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.updateItem(item)
            itemDao.upsert(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CatalogRepo", "Error updating item: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteItem(itemId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.deleteItem(itemId)
            itemDao.softDelete(itemId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CatalogRepo", "Error deleting item: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun createCategory(
        businessId: String,
        name: String,
        displayOrder: Int
    ): Result<ItemCategoryEntity> = withContext(Dispatchers.IO) {
        try {
            val categoryEntity = ItemCategoryEntity(
                id = java.util.UUID.randomUUID().toString(),
                businessId = businessId,
                name = name.trim(),
                displayOrder = displayOrder,
                isActive = true,
                isDeleted = false,
                createdAt = java.time.Instant.now().toString(),
                updatedAt = java.time.Instant.now().toString()
            )

            val insertedCategory = remoteDataSource.createCategory(categoryEntity)
            val effectiveCategory = insertedCategory ?: categoryEntity

            itemCategoryDao.upsert(effectiveCategory)
            Result.success(effectiveCategory)
        } catch (e: Exception) {
            Log.e("CatalogRepo", "Error creating category: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.deleteCategory(categoryId)
            itemCategoryDao.softDelete(categoryId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CatalogRepo", "Error deleting category: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun getItemTimelineFlow(itemId: String): Flow<List<InventoryTransactionEntity>> {
        return inventoryTransactionDao.getItemTransactionsFlow(itemId)
    }

    override suspend fun adjustStock(
        businessId: String,
        branchId: String?,
        itemId: String,
        adjustedStock: Double,
        reason: String,
        notes: String?
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // 1. Call atomic Supabase RPC
            val success = remoteDataSource.adjustStock(
                businessId = businessId,
                branchId = branchId,
                itemId = itemId,
                adjustedStock = adjustedStock,
                reason = reason,
                notes = notes
            )

            val nowIso = java.time.Instant.now().toString()

            // 2. Immediately update local Room DB inventory_stocks
            val resolvedBranch = branchId ?: ""
            if (resolvedBranch.isNotBlank()) {
                val existingStock = inventoryStockDao.getStockFlow(itemId, resolvedBranch).firstOrNull()
                val updatedStock = existingStock?.copy(
                    currentStock = adjustedStock,
                    updatedAt = nowIso
                ) ?: InventoryStockEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    itemId = itemId,
                    branchId = resolvedBranch,
                    currentStock = adjustedStock,
                    lowStockThreshold = 5.0,
                    isActive = true,
                    isDeleted = false,
                    createdAt = nowIso,
                    updatedAt = nowIso
                )
                inventoryStockDao.upsert(updatedStock)

                // 3. Immediately insert local audit transaction in Room DB
                val txnEntity = InventoryTransactionEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    businessId = businessId,
                    branchId = resolvedBranch,
                    itemId = itemId,
                    txnType = reason.uppercase(),
                    quantity = kotlin.math.abs(adjustedStock - (existingStock?.currentStock ?: 0.0)),
                    balanceAfter = adjustedStock,
                    notes = notes ?: "Manual Stock Adjustment ($reason)",
                    createdAt = nowIso,
                    updatedAt = nowIso
                )
                inventoryTransactionDao.upsert(txnEntity)
            }

            Result.success(success)
        } catch (e: Exception) {
            Log.e("CatalogRepo", "Error adjusting stock: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun syncItemTimeline(itemId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val remoteTransactions = remoteDataSource.fetchItemStockTimeline(itemId)
            if (remoteTransactions.isNotEmpty()) {
                inventoryTransactionDao.upsertAll(remoteTransactions)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CatalogRepo", "Error syncing stock timeline: ${e.message}", e)
            Result.failure(e)
        }
    }
}
