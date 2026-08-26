package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "order_deliveries")
data class OrderDeliveryEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("order_id")
    @ColumnInfo(name = "order_id")
    val orderId: String,

    @SerialName("rider_id")
    @ColumnInfo(name = "rider_id")
    val riderId: String,

    @SerialName("delivery_status")
    @ColumnInfo(name = "delivery_status")
    val deliveryStatus: String = "ASSIGNED",

    @SerialName("assigned_at")
    @ColumnInfo(name = "assigned_at")
    val assignedAt: String,

    @SerialName("picked_up_at")
    @ColumnInfo(name = "picked_up_at")
    val pickedUpAt: String? = null,

    @SerialName("delivered_at")
    @ColumnInfo(name = "delivered_at")
    val deliveredAt: String? = null,

    @SerialName("otp_entered")
    @ColumnInfo(name = "otp_entered")
    val otpEntered: String? = null,

    @SerialName("cash_collected_by_rider")
    @ColumnInfo(name = "cash_collected_by_rider")
    val cashCollectedByRider: Double = 0.0,

    @SerialName("cash_submitted_to_shop")
    @ColumnInfo(name = "cash_submitted_to_shop")
    val cashSubmittedToShop: Boolean = false,

    @SerialName("delivery_notes")
    @ColumnInfo(name = "delivery_notes")
    val deliveryNotes: String? = null,

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
