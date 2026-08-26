package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "delivery_riders")
data class DeliveryRiderEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("business_id")
    @ColumnInfo(name = "business_id")
    val businessId: String,

    @SerialName("user_id")
    @ColumnInfo(name = "user_id")
    val userId: String,

    @SerialName("name")
    @ColumnInfo(name = "name")
    val name: String,

    @SerialName("phone")
    @ColumnInfo(name = "phone")
    val phone: String,

    @SerialName("vehicle_number")
    @ColumnInfo(name = "vehicle_number")
    val vehicleNumber: String? = null,

    @SerialName("vehicle_type")
    @ColumnInfo(name = "vehicle_type")
    val vehicleType: String = "BIKE",

    @SerialName("is_online")
    @ColumnInfo(name = "is_online")
    val isOnline: Boolean = false,

    @SerialName("current_lat")
    @ColumnInfo(name = "current_lat")
    val currentLat: Double? = null,

    @SerialName("current_lng")
    @ColumnInfo(name = "current_lng")
    val currentLng: Double? = null,

    @SerialName("heading")
    @ColumnInfo(name = "heading")
    val heading: Double? = null,

    @SerialName("speed")
    @ColumnInfo(name = "speed")
    val speed: Double? = null,

    @SerialName("last_ping_at")
    @ColumnInfo(name = "last_ping_at")
    val lastPingAt: String? = null,

    @SerialName("active_order_id")
    @ColumnInfo(name = "active_order_id")
    val activeOrderId: String? = null,

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
