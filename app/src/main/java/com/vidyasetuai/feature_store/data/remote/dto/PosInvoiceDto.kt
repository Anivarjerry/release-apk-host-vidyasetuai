package com.vidyasetuai.feature_store.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosCartItemDto(
    @SerialName("item_id") val itemId: String,
    @SerialName("item_name") val itemName: String,
    @SerialName("barcode") val barcode: String = "",
    @SerialName("hsn_sac_code") val hsnSacCode: String = "",
    @SerialName("unit") val unit: String = "PCS",
    @SerialName("quantity") val quantity: Double = 1.0,
    @SerialName("sale_price") val salePrice: Double = 0.0,
    @SerialName("tax_rate") val taxRate: Double = 0.0,
    @SerialName("discount") val discount: Double = 0.0
)

@Serializable
data class PosInvoiceResponseDto(
    @SerialName("success") val success: Boolean = false,
    @SerialName("invoice_id") val invoiceId: String? = null,
    @SerialName("invoice_number") val invoiceNumber: String? = null,
    @SerialName("order_id") val orderId: String? = null,
    @SerialName("order_number") val orderNumber: String? = null,
    @SerialName("grand_total") val grandTotal: Double = 0.0,
    @SerialName("tax_total") val taxTotal: Double = 0.0,
    @SerialName("discount_total") val discountTotal: Double = 0.0,
    @SerialName("payment_method") val paymentMethod: String? = "CASH_ON_COUNTER",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class PosCustomerPartyDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("current_balance") val currentBalance: Double = 0.0,
    @SerialName("credit_limit") val creditLimit: Double = 0.0,
    @SerialName("party_type") val partyType: String = "CUSTOMER"
)
