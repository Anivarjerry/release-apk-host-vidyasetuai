package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "business_branches")
data class BusinessBranchEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("business_id")
    @ColumnInfo(name = "business_id")
    val businessId: String,

    @SerialName("branch_name")
    @ColumnInfo(name = "branch_name")
    val branchName: String,

    @SerialName("address_line")
    @ColumnInfo(name = "address_line")
    val addressLine: String,

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

    @SerialName("is_main_branch")
    @ColumnInfo(name = "is_main_branch")
    val isMainBranch: Boolean = true,

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
