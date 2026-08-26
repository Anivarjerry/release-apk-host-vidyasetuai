package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.ItemVariantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemVariantDao {
    @Query("SELECT * FROM item_variants WHERE item_id = :itemId AND is_deleted = 0")
    fun getItemVariantsFlow(itemId: String): Flow<List<ItemVariantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(variant: ItemVariantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(variants: List<ItemVariantEntity>)

    @Query("DELETE FROM item_variants WHERE item_id IN (SELECT id FROM items WHERE business_id = :businessId)")
    suspend fun purgeByBusiness(businessId: String)
}
