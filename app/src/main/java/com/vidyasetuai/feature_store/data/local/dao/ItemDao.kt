package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE business_id = :businessId AND is_deleted = 0 ORDER BY name ASC")
    fun getStoreItemsFlow(businessId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE is_deleted = 0 ORDER BY name ASC")
    fun getAllItemsFlow(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE business_id = :businessId AND category_id = :categoryId AND is_deleted = 0")
    fun getItemsByCategoryFlow(businessId: String, categoryId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE business_id = :businessId AND barcode = :barcode AND is_deleted = 0 LIMIT 1")
    suspend fun findByBarcode(businessId: String, barcode: String): ItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ItemEntity>)

    @Query("UPDATE items SET is_deleted = 1, is_active = 0 WHERE id = :itemId")
    suspend fun softDelete(itemId: String)

    @Query("DELETE FROM items WHERE business_id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
