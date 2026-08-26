package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "business_expense_types",
    indices = [
        Index(value = ["business_id"])
    ]
)
data class BusinessExpenseTypeEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("business_id")
    @ColumnInfo(name = "business_id")
    val businessId: String? = null,

    @SerialName("name")
    @ColumnInfo(name = "name")
    val name: String,

    @SerialName("name_hi")
    @ColumnInfo(name = "name_hi")
    val nameHi: String? = null,

    @SerialName("code")
    @ColumnInfo(name = "code")
    val code: String,

    @SerialName("reference_type")
    @ColumnInfo(name = "reference_type")
    val referenceType: String = "NONE",

    @SerialName("icon")
    @ColumnInfo(name = "icon")
    val icon: String = "Receipt",

    @SerialName("color")
    @ColumnInfo(name = "color")
    val color: String = "#10B981",

    @SerialName("is_system_default")
    @ColumnInfo(name = "is_system_default")
    val isSystemDefault: Boolean = false,

    @SerialName("is_active")
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @SerialName("is_deleted")
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @SerialName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",

    @SerialName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String = "",

    @SerialName("sync_version")
    @ColumnInfo(name = "sync_version")
    val syncVersion: Long = 1,

    @SerialName("sync_status")
    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "SYNCED"
)
