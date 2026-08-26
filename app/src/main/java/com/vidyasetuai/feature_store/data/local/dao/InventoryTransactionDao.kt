package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.InventoryTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryTransactionDao {
    @Query("SELECT * FROM inventory_transactions WHERE business_id = :businessId AND branch_id = :branchId AND is_deleted = 0 ORDER BY created_at DESC")
    fun getBranchTransactionsFlow(businessId: String, branchId: String): Flow<List<InventoryTransactionEntity>>

    @Query("SELECT * FROM inventory_transactions WHERE item_id = :itemId AND is_deleted = 0 ORDER BY created_at DESC")
    fun getItemTransactionsFlow(itemId: String): Flow<List<InventoryTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(transaction: InventoryTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(transactions: List<InventoryTransactionEntity>)

    @Query("DELETE FROM inventory_transactions WHERE business_id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
