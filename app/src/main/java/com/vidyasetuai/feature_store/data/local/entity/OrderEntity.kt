package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("business_id")
    @ColumnInfo(name = "business_id")
    val businessId: String,

    @SerialName("branch_id")
    @ColumnInfo(name = "branch_id")
    val branchId: String,

    @SerialName("order_number")
    @ColumnInfo(name = "order_number")
    val orderNumber: String,

    @SerialName("party_id")
    @ColumnInfo(name = "party_id")
    val partyId: String? = null,

    @SerialName("user_id")
    @ColumnInfo(name = "user_id")
    val userId: String? = null,

    @SerialName("customer_name")
    @ColumnInfo(name = "customer_name")
    val customerName: String,

    @SerialName("customer_phone")
    @ColumnInfo(name = "customer_phone")
    val customerPhone: String,

    @SerialName("order_type")
    @ColumnInfo(name = "order_type")
    val orderType: String = "COUNTER_POS",

    @SerialName("table_or_token_no")
    @ColumnInfo(name = "table_or_token_no")
    val tableOrTokenNo: String? = null,

    @SerialName("order_status")
    @ColumnInfo(name = "order_status")
    val orderStatus: String = "PLACED",

    @SerialName("delivery_otp")
    @ColumnInfo(name = "delivery_otp")
    val deliveryOtp: String,

    @SerialName("sub_total")
    @ColumnInfo(name = "sub_total")
    val subTotal: Double = 0.0,

    @SerialName("tax_total")
    @ColumnInfo(name = "tax_total")
    val taxTotal: Double = 0.0,

    @SerialName("delivery_charge")
    @ColumnInfo(name = "delivery_charge")
    val deliveryCharge: Double = 0.0,

    @SerialName("packing_charge")
    @ColumnInfo(name = "packing_charge")
    val packingCharge: Double = 0.0,

    @SerialName("discount_amount")
    @ColumnInfo(name = "discount_amount")
    val discountAmount: Double = 0.0,

    @SerialName("grand_total")
    @ColumnInfo(name = "grand_total")
    val grandTotal: Double = 0.0,

    @SerialName("payment_status")
    @ColumnInfo(name = "payment_status")
    val paymentStatus: String = "UNPAID",

    @SerialName("payment_method")
    @ColumnInfo(name = "payment_method")
    val paymentMethod: String = "CASH_ON_DELIVERY",

    @Serializable(with = com.vidyasetuai.feature_store.data.remote.serializer.FlexibleJsonAsStringSerializer::class)
    @SerialName("delivery_address_json")
    @ColumnInfo(name = "delivery_address_json")
    val deliveryAddressJson: String? = null,

    @SerialName("order_notes")
    @ColumnInfo(name = "order_notes")
    val orderNotes: String? = null,

    @SerialName("cancelled_reason")
    @ColumnInfo(name = "cancelled_reason")
    val cancelledReason: String? = null,

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
