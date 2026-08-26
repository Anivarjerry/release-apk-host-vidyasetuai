package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("business_id")
    @ColumnInfo(name = "business_id")
    val businessId: String,

    @SerialName("order_id")
    @ColumnInfo(name = "order_id")
    val orderId: String? = null,

    @SerialName("invoice_id")
    @ColumnInfo(name = "invoice_id")
    val invoiceId: String? = null,

    @SerialName("party_id")
    @ColumnInfo(name = "party_id")
    val partyId: String? = null,

    @SerialName("user_id")
    @ColumnInfo(name = "user_id")
    val userId: String? = null,

    @SerialName("amount")
    @ColumnInfo(name = "amount")
    val amount: Double,

    @SerialName("payment_mode")
    @ColumnInfo(name = "payment_mode")
    val paymentMode: String,

    @SerialName("payment_status")
    @ColumnInfo(name = "payment_status")
    val paymentStatus: String = "PENDING",

    @SerialName("collected_by_type")
    @ColumnInfo(name = "collected_by_type")
    val collectedByType: String = "CASHIER",

    @SerialName("collected_by_user_id")
    @ColumnInfo(name = "collected_by_user_id")
    val collectedByUserId: String? = null,

    @SerialName("gateway_provider")
    @ColumnInfo(name = "gateway_provider")
    val gatewayProvider: String? = null,

    @SerialName("gateway_order_id")
    @ColumnInfo(name = "gateway_order_id")
    val gatewayOrderId: String? = null,

    @SerialName("gateway_payment_id")
    @ColumnInfo(name = "gateway_payment_id")
    val gatewayPaymentId: String? = null,

    @SerialName("gateway_signature")
    @ColumnInfo(name = "gateway_signature")
    val gatewaySignature: String? = null,

    @Serializable(with = com.vidyasetuai.feature_store.data.remote.serializer.FlexibleJsonAsStringSerializer::class)
    @SerialName("gateway_response_json")
    @ColumnInfo(name = "gateway_response_json")
    val gatewayResponseJson: String? = null,

    @SerialName("is_active")
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @SerialName("is_deleted")
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @SerialName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @SerialName("created_by")
    @ColumnInfo(name = "created_by")
    val createdBy: String? = null,

    @SerialName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String,

    @SerialName("updated_by")
    @ColumnInfo(name = "updated_by")
    val updatedBy: String? = null,

    @SerialName("sync_version")
    @ColumnInfo(name = "sync_version")
    val syncVersion: Long = 1,

    @SerialName("sync_status")
    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "SYNCED"
)
