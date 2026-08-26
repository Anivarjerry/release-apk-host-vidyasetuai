package com.vidyasetuai.feature_store.domain.model

import com.vidyasetuai.feature_store.data.local.entity.OrderEntity
import com.vidyasetuai.feature_store.data.local.entity.OrderItemEntity

/**
 * Filter tab options for Kitchen Display Board.
 */
enum class KdsFilterTab {
    ALL_ACTIVE,
    NEW,
    PREPARING,
    READY,
    SERVED_TODAY
}

/**
 * Complete KOT Ticket UI Model for Kitchen Display Screen.
 */
data class KitchenKdsUiModel(
    val order: OrderEntity,
    val items: List<OrderItemEntity> = emptyList(),
    val elapsedMinutes: Int = 0,
    val isUrgent: Boolean = false
) {
    val isNew: Boolean
        get() = order.orderStatus.equals("NEW", ignoreCase = true) || order.orderStatus.equals("PLACED", ignoreCase = true)

    val isPreparing: Boolean
        get() = order.orderStatus.equals("PREPARING", ignoreCase = true) || order.orderStatus.equals("ACCEPTED", ignoreCase = true)

    val isReady: Boolean
        get() = order.orderStatus.equals("READY", ignoreCase = true)

    val isOutForDelivery: Boolean
        get() = order.orderStatus.equals("OUT_FOR_DELIVERY", ignoreCase = true)

    val isServed: Boolean
        get() = order.orderStatus.equals("DELIVERED", ignoreCase = true) || order.orderStatus.equals("COMPLETED", ignoreCase = true)

    val isDeliveryOrder: Boolean
        get() = order.orderType.contains("DELIVERY", ignoreCase = true) || order.orderType.contains("ONLINE", ignoreCase = true)

    val formattedOrderNumber: String
        get() = "#${order.orderNumber.removePrefix("#")}"
}
