package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_parent_staff_salary_payments")
data class LocalParentStaffSalaryPaymentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,

    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,

    @ColumnInfo(name = "staff_id")
    val staffId: String,

    @ColumnInfo(name = "payment_date")
    val paymentDate: String,

    @ColumnInfo(name = "amount_paid")
    val amountPaid: Double,

    @ColumnInfo(name = "payment_mode")
    val paymentMode: String, // 'Cash', 'Cheque', 'UPI', 'Online'

    @ColumnInfo(name = "cash_paid_by_user_id")
    val cashPaidByUserId: String?,

    @ColumnInfo(name = "cheque_number")
    val chequeNumber: String?,

    @ColumnInfo(name = "cheque_date")
    val chequeDate: String?,

    @ColumnInfo(name = "cheque_bank_name")
    val chequeBankName: String?,

    @ColumnInfo(name = "online_transaction_id")
    val onlineTransactionId: String?,

    @ColumnInfo(name = "online_payment_app")
    val onlinePaymentApp: String?,

    @ColumnInfo(name = "remarks")
    val remarks: String?,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "sync_state")
    val syncState: String = "SYNCED"
)
