package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {
    @Query("SELECT * FROM order_items WHERE order_id = :orderId AND is_deleted = 0")
    fun getOrderItemsFlow(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE is_deleted = 0")
    fun getAllOrderItemsFlow(): Flow<List<OrderItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(orderItem: OrderItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(orderItems: List<OrderItemEntity>)

    @Query("DELETE FROM order_items WHERE order_id IN (SELECT id FROM orders WHERE business_id = :businessId)")
    suspend fun purgeByBusiness(businessId: String)
}
