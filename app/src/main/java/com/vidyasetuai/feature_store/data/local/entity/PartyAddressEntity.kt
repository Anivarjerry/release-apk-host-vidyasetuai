package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "party_addresses")
data class PartyAddressEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("party_id")
    @ColumnInfo(name = "party_id")
    val partyId: String,

    @SerialName("user_id")
    @ColumnInfo(name = "user_id")
    val userId: String? = null,

    @SerialName("address_type")
    @ColumnInfo(name = "address_type")
    val addressType: String = "HOME",

    @SerialName("full_address")
    @ColumnInfo(name = "full_address")
    val fullAddress: String,

    @SerialName("landmark")
    @ColumnInfo(name = "landmark")
    val landmark: String? = null,

    @SerialName("city")
    @ColumnInfo(name = "city")
    val city: String,

    @SerialName("state")
    @ColumnInfo(name = "state")
    val state: String,

    @SerialName("pincode")
    @ColumnInfo(name = "pincode")
    val pincode: String,

    @SerialName("lat")
    @ColumnInfo(name = "lat")
    val lat: Double? = null,

    @SerialName("lng")
    @ColumnInfo(name = "lng")
    val lng: Double? = null,

    @SerialName("is_default")
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = true,

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
