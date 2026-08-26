package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.BusinessSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessSettingsDao {
    @Query("SELECT * FROM business_settings WHERE business_id = :businessId AND is_deleted = 0 LIMIT 1")
    fun getSettingsFlow(businessId: String): Flow<BusinessSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: BusinessSettingsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(settingsList: List<BusinessSettingsEntity>)

    @Query("DELETE FROM business_settings WHERE business_id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
