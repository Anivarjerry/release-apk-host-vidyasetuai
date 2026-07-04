package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_student_fee_payments")
data class LocalStudentFeePaymentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_student_fee_payments.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "student_id")
    val studentId: String,
    
    @ColumnInfo(name = "receipt_number")
    val receiptNumber: String,
    
    @ColumnInfo(name = "payment_mode")
    val paymentMode: String,
    
    @ColumnInfo(name = "payment_date")
    val paymentDate: String,
    
    @ColumnInfo(name = "amount_paid")
    val amountPaid: Double,
    
    @ColumnInfo(name = "discount_amount")
    val discountAmount: Double,
    
    @ColumnInfo(name = "fine_amount")
    val fineAmount: Double,
    
    @ColumnInfo(name = "discount_reason")
    val discountReason: String?,
    
    @ColumnInfo(name = "cash_received_by_user_id")
    val cashReceivedByUserId: String?,
    
    @ColumnInfo(name = "cash_received_by_user_name")
    val cashReceivedByUserName: String?,
    
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
    
    @ColumnInfo(name = "status")
    val status: String,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String
)
