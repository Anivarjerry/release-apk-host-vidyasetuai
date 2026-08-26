package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "inventory_transactions")
data class InventoryTransactionEntity(
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

    @SerialName("item_id")
    @ColumnInfo(name = "item_id")
    val itemId: String,

    @SerialName("variant_id")
    @ColumnInfo(name = "variant_id")
    val variantId: String? = null,

    @SerialName("txn_type")
    @ColumnInfo(name = "txn_type")
    val txnType: String,

    @SerialName("quantity")
    @ColumnInfo(name = "quantity")
    val quantity: Double,

    @SerialName("balance_after")
    @ColumnInfo(name = "balance_after")
    val balanceAfter: Double,

    @SerialName("reference_id")
    @ColumnInfo(name = "reference_id")
    val referenceId: String? = null,

    @SerialName("notes")
    @ColumnInfo(name = "notes")
    val notes: String? = null,

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
