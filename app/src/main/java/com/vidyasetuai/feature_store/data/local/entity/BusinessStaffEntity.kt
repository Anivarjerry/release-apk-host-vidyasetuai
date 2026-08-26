package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "business_staff_members")
data class BusinessStaffEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("business_id")
    @ColumnInfo(name = "business_id")
    val businessId: String,

    @SerialName("branch_id")
    @ColumnInfo(name = "branch_id")
    val branchId: String? = null,

    @SerialName("user_id")
    @ColumnInfo(name = "user_id")
    val userId: String? = null,

    @SerialName("username")
    @ColumnInfo(name = "username")
    val username: String? = null,

    @SerialName("name")
    @ColumnInfo(name = "name")
    val name: String,

    @SerialName("phone")
    @ColumnInfo(name = "phone")
    val phone: String,

    @SerialName("email")
    @ColumnInfo(name = "email")
    val email: String? = null,

    @SerialName("role")
    @ColumnInfo(name = "role")
    val role: String = "CASHIER",

    @Serializable(with = com.vidyasetuai.feature_store.data.remote.serializer.FlexibleJsonAsStringSerializer::class)
    @SerialName("permissions_json")
    @ColumnInfo(name = "permissions_json")
    val permissionsJson: String? = "{}",

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
