package com.vidyasetuai.feature_store.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomerOrderItemDto(
    @SerialName("id") val id: String? = null,
    @SerialName("item_name") val itemName: String = "Item",
    @SerialName("quantity") val quantity: Double = 1.0,
    @SerialName("unit_price") val unitPrice: Double = 0.0,
    @SerialName("total_price") val totalPrice: Double = 0.0
)

@Serializable
data class CustomerOrderDto(
    @SerialName("order_id") val orderId: String,
    @SerialName("order_number") val orderNumber: String,
    @SerialName("order_status") val orderStatus: String = "NEW", // NEW, PREPARING, READY, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    @SerialName("order_type") val orderType: String = "DELIVERY", // DINE_IN, TAKEAWAY, DELIVERY
    @SerialName("table_or_token_no") val tableOrTokenNo: String? = null,
    @SerialName("delivery_otp") val deliveryOtp: String = "0000",
    @SerialName("sub_total") val subTotal: Double = 0.0,
    @SerialName("grand_total") val grandTotal: Double = 0.0,
    @SerialName("delivery_charge") val deliveryCharge: Double = 0.0,
    @SerialName("customer_name") val customerName: String? = null,
    @SerialName("customer_phone") val customerPhone: String? = null,
    @SerialName("payment_status") val paymentStatus: String? = "UNPAID",
    @SerialName("payment_method") val paymentMethod: String? = "CASH_ON_DELIVERY",
    @SerialName("order_notes") val orderNotes: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("merchant_name") val merchantName: String? = "VidyaSetu Store",
    @SerialName("merchant_logo") val merchantLogo: String? = null,
    @SerialName("items") val items: List<CustomerOrderItemDto> = emptyList()
)
