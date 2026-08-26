package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "item_variants")
data class ItemVariantEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("item_id")
    @ColumnInfo(name = "item_id")
    val itemId: String,

    @SerialName("variant_name")
    @ColumnInfo(name = "variant_name")
    val variantName: String,

    @SerialName("sale_price")
    @ColumnInfo(name = "sale_price")
    val salePrice: Double,

    @SerialName("purchase_price")
    @ColumnInfo(name = "purchase_price")
    val purchasePrice: Double = 0.0,

    @SerialName("barcode")
    @ColumnInfo(name = "barcode")
    val barcode: String? = null,

    @SerialName("sku")
    @ColumnInfo(name = "sku")
    val sku: String? = null,

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
