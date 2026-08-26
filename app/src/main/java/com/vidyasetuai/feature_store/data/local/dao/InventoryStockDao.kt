package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.InventoryStockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryStockDao {
    @Query("SELECT * FROM inventory_stocks WHERE item_id = :itemId AND branch_id = :branchId AND is_deleted = 0 LIMIT 1")
    fun getStockFlow(itemId: String, branchId: String): Flow<InventoryStockEntity?>

    @Query("SELECT * FROM inventory_stocks WHERE item_id = :itemId AND branch_id = :branchId AND is_deleted = 0 LIMIT 1")
    suspend fun getStockDirect(itemId: String, branchId: String): InventoryStockEntity?

    @Query("SELECT * FROM inventory_stocks WHERE branch_id = :branchId AND is_deleted = 0")
    fun getStocksForBranchFlow(branchId: String): Flow<List<InventoryStockEntity>>

    @Query("SELECT * FROM inventory_stocks WHERE is_deleted = 0")
    fun getAllStocksFlow(): Flow<List<InventoryStockEntity>>

    @Query("SELECT * FROM inventory_stocks WHERE branch_id = :branchId AND current_stock <= low_stock_threshold AND is_deleted = 0")
    fun getLowStockAlertsFlow(branchId: String): Flow<List<InventoryStockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stock: InventoryStockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(stocks: List<InventoryStockEntity>)

    @Query("DELETE FROM inventory_stocks WHERE branch_id IN (SELECT id FROM business_branches WHERE business_id = :businessId)")
    suspend fun purgeByBusiness(businessId: String)
}
