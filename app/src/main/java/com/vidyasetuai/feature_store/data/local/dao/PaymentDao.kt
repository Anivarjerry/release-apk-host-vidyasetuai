package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE business_id = :businessId AND is_deleted = 0 ORDER BY created_at DESC")
    fun getPaymentsFlow(businessId: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE is_deleted = 0 ORDER BY created_at DESC")
    fun getAllPaymentsFlow(): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(payment: PaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(payments: List<PaymentEntity>)

    @Query("DELETE FROM payments WHERE business_id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
