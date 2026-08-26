package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("order_id")
    @ColumnInfo(name = "order_id")
    val orderId: String,

    @SerialName("item_id")
    @ColumnInfo(name = "item_id")
    val itemId: String? = null,

    @SerialName("variant_id")
    @ColumnInfo(name = "variant_id")
    val variantId: String? = null,

    @SerialName("item_name")
    @ColumnInfo(name = "item_name")
    val itemName: String,

    @SerialName("variant_name")
    @ColumnInfo(name = "variant_name")
    val variantName: String? = null,

    @SerialName("quantity")
    @ColumnInfo(name = "quantity")
    val quantity: Double,

    @SerialName("unit_price")
    @ColumnInfo(name = "unit_price")
    val unitPrice: Double,

    @SerialName("tax_rate")
    @ColumnInfo(name = "tax_rate")
    val taxRate: Double = 0.0,

    @SerialName("tax_amount")
    @ColumnInfo(name = "tax_amount")
    val taxAmount: Double = 0.0,

    @SerialName("discount_amount")
    @ColumnInfo(name = "discount_amount")
    val discountAmount: Double = 0.0,

    @SerialName("total_price")
    @ColumnInfo(name = "total_price")
    val totalPrice: Double,

    @SerialName("item_notes")
    @ColumnInfo(name = "item_notes")
    val itemNotes: String? = null,

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
