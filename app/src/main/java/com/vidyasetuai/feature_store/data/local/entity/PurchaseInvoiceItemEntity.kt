package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "purchase_invoice_items",
    indices = [
        Index(value = ["purchase_id"]),
        Index(value = ["item_id"])
    ]
)
data class PurchaseInvoiceItemEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("purchase_id")
    @ColumnInfo(name = "purchase_id")
    val purchaseId: String,

    @SerialName("item_id")
    @ColumnInfo(name = "item_id")
    val itemId: String? = null,

    @SerialName("item_name")
    @ColumnInfo(name = "item_name")
    val itemName: String,

    @SerialName("hsn_sac_code")
    @ColumnInfo(name = "hsn_sac_code")
    val hsnSacCode: String? = null,

    @SerialName("quantity")
    @ColumnInfo(name = "quantity")
    val quantity: Double = 1.0,

    @SerialName("unit")
    @ColumnInfo(name = "unit")
    val unit: String = "PCS",

    @SerialName("unit_rate")
    @ColumnInfo(name = "unit_rate")
    val unitRate: Double = 0.0,

    @SerialName("tax_rate")
    @ColumnInfo(name = "tax_rate")
    val taxRate: Double = 0.0,

    @SerialName("taxable_value")
    @ColumnInfo(name = "taxable_value")
    val taxableValue: Double = 0.0,

    @SerialName("cgst_amount")
    @ColumnInfo(name = "cgst_amount")
    val cgstAmount: Double = 0.0,

    @SerialName("sgst_amount")
    @ColumnInfo(name = "sgst_amount")
    val sgstAmount: Double = 0.0,

    @SerialName("igst_amount")
    @ColumnInfo(name = "igst_amount")
    val igstAmount: Double = 0.0,

    @SerialName("total_amount")
    @ColumnInfo(name = "total_amount")
    val totalAmount: Double = 0.0,

    @SerialName("is_active")
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @SerialName("is_deleted")
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @SerialName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @SerialName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String,

    @SerialName("sync_version")
    @ColumnInfo(name = "sync_version")
    val syncVersion: Long = 1,

    @SerialName("sync_status")
    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "SYNCED"
)
