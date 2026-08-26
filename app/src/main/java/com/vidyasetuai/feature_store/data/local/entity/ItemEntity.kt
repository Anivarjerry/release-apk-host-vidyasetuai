package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("business_id")
    @ColumnInfo(name = "business_id")
    val businessId: String,

    @SerialName("category_id")
    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,

    @SerialName("item_type")
    @ColumnInfo(name = "item_type")
    val itemType: String = "PRODUCT",

    @SerialName("name")
    @ColumnInfo(name = "name")
    val name: String,

    @SerialName("description")
    @ColumnInfo(name = "description")
    val description: String? = null,

    @SerialName("sku")
    @ColumnInfo(name = "sku")
    val sku: String? = null,

    @SerialName("barcode")
    @ColumnInfo(name = "barcode")
    val barcode: String? = null,

    @SerialName("hsn_sac_code")
    @ColumnInfo(name = "hsn_sac_code")
    val hsnSacCode: String? = null,

    @SerialName("tax_rate")
    @ColumnInfo(name = "tax_rate")
    val taxRate: Double = 0.0,

    @SerialName("is_tax_inclusive")
    @ColumnInfo(name = "is_tax_inclusive")
    val isTaxInclusive: Boolean = true,

    @SerialName("purchase_price")
    @ColumnInfo(name = "purchase_price")
    val purchasePrice: Double = 0.0,

    @SerialName("sale_price")
    @ColumnInfo(name = "sale_price")
    val salePrice: Double,

    @SerialName("mrp")
    @ColumnInfo(name = "mrp")
    val mrp: Double? = null,

    @SerialName("unit")
    @ColumnInfo(name = "unit")
    val unit: String = "PCS",

    @SerialName("food_type")
    @ColumnInfo(name = "food_type")
    val foodType: String? = "NONE",

    @SerialName("is_available_online")
    @ColumnInfo(name = "is_available_online")
    val isAvailableOnline: Boolean = true,

    @SerialName("image_url")
    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    @Serializable(with = com.vidyasetuai.feature_store.data.remote.serializer.FlexibleJsonAsStringSerializer::class)
    @SerialName("extra_images")
    @ColumnInfo(name = "extra_images")
    val extraImages: String? = "[]",

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
