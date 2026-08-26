package com.vidyasetuai.feature_store.domain.repository

import com.vidyasetuai.feature_store.data.local.entity.DeliveryRiderEntity
import com.vidyasetuai.feature_store.domain.model.KitchenKdsUiModel
import kotlinx.coroutines.flow.Flow

/**
 * Domain Repository Interface for Kitchen Display System (KDS).
 */
interface KitchenKdsRepository {

    /**
     * 0ms Reactive Room Flow returning active KDS orders combined with their order items.
     */
    fun getActiveKdsTicketsFlow(businessId: String): Flow<List<KitchenKdsUiModel>>

    /**
     * 0ms Reactive Room Flow returning past served/completed orders for today.
     */
    fun getServedTicketsFlow(businessId: String): Flow<List<KitchenKdsUiModel>>

    /**
     * 0ms Reactive Room Flow returning available delivery riders.
     */
    fun getDeliveryRidersFlow(businessId: String): Flow<List<DeliveryRiderEntity>>

    /**
     * Updates order status in local Room DB immediately and syncs with Supabase RPC.
     */
    suspend fun updateOrderStatus(
        businessId: String,
        orderId: String,
        newStatus: String,
        riderId: String? = null,
        otpCode: String? = null
    ): Result<Unit>
}
