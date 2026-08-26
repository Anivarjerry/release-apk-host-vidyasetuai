package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "business_expenses",
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["branch_id"]),
        Index(value = ["expense_type_id"]),
        Index(value = ["expense_date"])
    ]
)
data class BusinessExpenseEntity(
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

    @SerialName("expense_type_id")
    @ColumnInfo(name = "expense_type_id")
    val expenseTypeId: String,

    @SerialName("voucher_number")
    @ColumnInfo(name = "voucher_number")
    val voucherNumber: String,

    @SerialName("title")
    @ColumnInfo(name = "title")
    val title: String,

    @SerialName("amount")
    @ColumnInfo(name = "amount")
    val amount: Double = 0.0,

    @SerialName("payment_mode")
    @ColumnInfo(name = "payment_mode")
    val paymentMode: String = "CASH", // 'CASH', 'UPI', 'BANK_TRANSFER', 'CHEQUE'

    @SerialName("expense_date")
    @ColumnInfo(name = "expense_date")
    val expenseDate: String, // 'YYYY-MM-DD'

    @SerialName("reference_type")
    @ColumnInfo(name = "reference_type")
    val referenceType: String = "NONE", // 'NONE', 'BRANCH', 'STAFF', 'SUPPLIER', 'ITEM'

    @SerialName("reference_id")
    @ColumnInfo(name = "reference_id")
    val referenceId: String? = null,

    @SerialName("paid_to")
    @ColumnInfo(name = "paid_to")
    val paidTo: String? = null,

    @SerialName("notes")
    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @SerialName("created_by")
    @ColumnInfo(name = "created_by")
    val createdBy: String? = null,

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
