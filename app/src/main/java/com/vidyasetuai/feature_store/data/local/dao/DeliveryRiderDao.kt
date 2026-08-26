package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.DeliveryRiderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryRiderDao {
    @Query("SELECT * FROM delivery_riders WHERE business_id = :businessId AND is_deleted = 0")
    fun getRidersFlow(businessId: String): Flow<List<DeliveryRiderEntity>>

    @Query("SELECT * FROM delivery_riders WHERE is_deleted = 0")
    fun getAllRidersFlow(): Flow<List<DeliveryRiderEntity>>

    @Query("UPDATE delivery_riders SET is_online = :isOnline, updated_at = :updatedAt WHERE id = :riderId")
    suspend fun updateRiderDutyStatus(riderId: String, isOnline: Boolean, updatedAt: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rider: DeliveryRiderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(riders: List<DeliveryRiderEntity>)

    @Query("DELETE FROM delivery_riders WHERE business_id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
