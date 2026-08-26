package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.ItemCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemCategoryDao {
    @Query("SELECT * FROM item_categories WHERE business_id = :businessId AND is_deleted = 0 ORDER BY display_order ASC")
    fun getCategoriesFlow(businessId: String): Flow<List<ItemCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: ItemCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(categories: List<ItemCategoryEntity>)

    @Query("UPDATE item_categories SET is_deleted = 1, is_active = 0 WHERE id = :categoryId")
    suspend fun softDelete(categoryId: String)

    @Query("DELETE FROM item_categories WHERE business_id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
