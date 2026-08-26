package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.BusinessEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessDao {
    @Query("SELECT * FROM businesses WHERE id = :businessId AND is_deleted = 0 LIMIT 1")
    fun getBusinessFlow(businessId: String): Flow<BusinessEntity?>

    @Query("SELECT * FROM businesses WHERE is_deleted = 0 LIMIT 1")
    fun getAnyActiveBusinessFlow(): Flow<BusinessEntity?>

    @Query("SELECT * FROM businesses WHERE user_id = :userId AND is_deleted = 0")
    fun getOwnerBusinessesFlow(userId: String): Flow<List<BusinessEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(business: BusinessEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(businesses: List<BusinessEntity>)

    @Query("DELETE FROM businesses WHERE id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
