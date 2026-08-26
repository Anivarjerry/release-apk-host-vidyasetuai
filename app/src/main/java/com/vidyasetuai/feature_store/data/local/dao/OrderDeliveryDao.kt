package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.OrderDeliveryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDeliveryDao {
    @Query("SELECT * FROM order_deliveries WHERE order_id = :orderId AND is_deleted = 0 LIMIT 1")
    fun getDeliveryFlow(orderId: String): Flow<OrderDeliveryEntity?>

    @Query("SELECT * FROM order_deliveries WHERE is_deleted = 0 AND delivery_status = 'DELIVERED' AND cash_submitted_to_shop = 0")
    fun getUnsettledDeliveriesFlow(): Flow<List<OrderDeliveryEntity>>

    @Query("UPDATE order_deliveries SET cash_submitted_to_shop = 1, updated_at = :updatedAt WHERE rider_id = :riderId AND delivery_status = 'DELIVERED' AND cash_submitted_to_shop = 0")
    suspend fun settleRiderDeliveries(riderId: String, updatedAt: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(delivery: OrderDeliveryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(deliveries: List<OrderDeliveryEntity>)

    @Query("DELETE FROM order_deliveries WHERE order_id IN (SELECT id FROM orders WHERE business_id = :businessId)")
    suspend fun purgeByBusiness(businessId: String)
}
