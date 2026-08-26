package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "invoice_items")
data class InvoiceItemEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("invoice_id")
    @ColumnInfo(name = "invoice_id")
    val invoiceId: String,

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
    val quantity: Double,

    @SerialName("unit")
    @ColumnInfo(name = "unit")
    val unit: String = "PCS",

    @SerialName("unit_rate")
    @ColumnInfo(name = "unit_rate")
    val unitRate: Double,

    @SerialName("discount_amount")
    @ColumnInfo(name = "discount_amount")
    val discountAmount: Double = 0.0,

    @SerialName("taxable_value")
    @ColumnInfo(name = "taxable_value")
    val taxableValue: Double,

    @SerialName("gst_rate")
    @ColumnInfo(name = "gst_rate")
    val gstRate: Double = 0.0,

    @SerialName("cgst_rate")
    @ColumnInfo(name = "cgst_rate")
    val cgstRate: Double = 0.0,

    @SerialName("cgst_amount")
    @ColumnInfo(name = "cgst_amount")
    val cgstAmount: Double = 0.0,

    @SerialName("sgst_rate")
    @ColumnInfo(name = "sgst_rate")
    val sgstRate: Double = 0.0,

    @SerialName("sgst_amount")
    @ColumnInfo(name = "sgst_amount")
    val sgstAmount: Double = 0.0,

    @SerialName("igst_rate")
    @ColumnInfo(name = "igst_rate")
    val igstRate: Double = 0.0,

    @SerialName("igst_amount")
    @ColumnInfo(name = "igst_amount")
    val igstAmount: Double = 0.0,

    @SerialName("total_amount")
    @ColumnInfo(name = "total_amount")
    val totalAmount: Double,

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
