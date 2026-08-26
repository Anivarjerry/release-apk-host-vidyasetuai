package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "inventory_stocks")
data class InventoryStockEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("item_id")
    @ColumnInfo(name = "item_id")
    val itemId: String,

    @SerialName("variant_id")
    @ColumnInfo(name = "variant_id")
    val variantId: String? = null,

    @SerialName("branch_id")
    @ColumnInfo(name = "branch_id")
    val branchId: String,

    @SerialName("current_stock")
    @ColumnInfo(name = "current_stock")
    val currentStock: Double = 0.0,

    @SerialName("low_stock_threshold")
    @ColumnInfo(name = "low_stock_threshold")
    val lowStockThreshold: Double = 5.0,

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
