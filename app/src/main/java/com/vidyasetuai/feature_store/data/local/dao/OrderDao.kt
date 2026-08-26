package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE business_id = :businessId AND branch_id = :branchId AND order_status IN ('PLACED', 'ACCEPTED', 'PREPARING', 'READY') AND is_deleted = 0 ORDER BY created_at DESC")
    fun getLiveKdsOrdersFlow(businessId: String, branchId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE business_id = :businessId AND branch_id = :branchId AND is_deleted = 0 ORDER BY created_at DESC")
    fun getBranchOrdersFlow(businessId: String, branchId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE (business_id = :businessId OR :businessId = '') AND order_status IN ('NEW', 'PLACED', 'ACCEPTED', 'PREPARING', 'READY') AND is_deleted = 0 ORDER BY created_at DESC")
    fun getAllLiveKdsOrdersFlow(businessId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE (business_id = :businessId OR :businessId = '') AND order_status IN ('DELIVERED', 'COMPLETED', 'OUT_FOR_DELIVERY', 'CANCELLED') AND is_deleted = 0 ORDER BY created_at DESC")
    fun getServedOrdersFlow(businessId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE is_deleted = 0 ORDER BY created_at DESC")
    fun getAllOrdersFlow(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(orders: List<OrderEntity>)

    @Query("DELETE FROM orders WHERE business_id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
