package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "business_staff_salary_payments")
data class BusinessStaffSalaryPaymentEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("business_id")
    @ColumnInfo(name = "business_id")
    val businessId: String,

    @SerialName("staff_id")
    @ColumnInfo(name = "staff_id")
    val staffId: String,

    @SerialName("payout_id")
    @ColumnInfo(name = "payout_id")
    val payoutId: String? = null,

    @SerialName("payment_type")
    @ColumnInfo(name = "payment_type")
    val paymentType: String = "SALARY_PAYMENT", // SALARY_PAYMENT, ADVANCE, BONUS

    @SerialName("amount_paid")
    @ColumnInfo(name = "amount_paid")
    val amountPaid: Double,

    @SerialName("payment_mode")
    @ColumnInfo(name = "payment_mode")
    val paymentMode: String = "CASH", // CASH, UPI, BANK_TRANSFER, CHEQUE

    @SerialName("payment_date")
    @ColumnInfo(name = "payment_date")
    val paymentDate: String, // YYYY-MM-DD

    @SerialName("transaction_ref")
    @ColumnInfo(name = "transaction_ref")
    val transactionRef: String? = null,

    @SerialName("remarks")
    @ColumnInfo(name = "remarks")
    val remarks: String? = null,

    @SerialName("is_advance_settled")
    @ColumnInfo(name = "is_advance_settled")
    val isAdvanceSettled: Boolean = false,

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
