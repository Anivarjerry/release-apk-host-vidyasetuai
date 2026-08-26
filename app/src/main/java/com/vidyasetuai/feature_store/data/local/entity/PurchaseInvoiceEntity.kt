package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "purchase_invoices")
data class PurchaseInvoiceEntity(
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

    @SerialName("supplier_id")
    @ColumnInfo(name = "supplier_id")
    val supplierId: String,

    @SerialName("supplier_invoice_number")
    @ColumnInfo(name = "supplier_invoice_number")
    val supplierInvoiceNumber: String,

    @SerialName("invoice_date")
    @ColumnInfo(name = "invoice_date")
    val invoiceDate: String,

    @SerialName("taxable_amount")
    @ColumnInfo(name = "taxable_amount")
    val taxableAmount: Double = 0.0,

    @SerialName("cgst_amount")
    @ColumnInfo(name = "cgst_amount")
    val cgstAmount: Double = 0.0,

    @SerialName("sgst_amount")
    @ColumnInfo(name = "sgst_amount")
    val sgstAmount: Double = 0.0,

    @SerialName("igst_amount")
    @ColumnInfo(name = "igst_amount")
    val igstAmount: Double = 0.0,

    @SerialName("grand_total")
    @ColumnInfo(name = "grand_total")
    val grandTotal: Double = 0.0,

    @SerialName("paid_amount")
    @ColumnInfo(name = "paid_amount")
    val paidAmount: Double = 0.0,

    @SerialName("due_amount")
    @ColumnInfo(name = "due_amount")
    val dueAmount: Double = 0.0,

    @SerialName("payment_status")
    @ColumnInfo(name = "payment_status")
    val paymentStatus: String = "UNPAID",

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
