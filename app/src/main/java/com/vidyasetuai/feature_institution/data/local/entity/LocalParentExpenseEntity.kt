package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_parent_expenses")
data class LocalParentExpenseEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_parent_expenses.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "expense_type_id")
    val expenseTypeId: String,
    
    @ColumnInfo(name = "expense_type_name")
    val expenseTypeName: String?,
    
    @ColumnInfo(name = "expense_type_code")
    val expenseTypeCode: String?,
    
    @ColumnInfo(name = "payment_method")
    val paymentMethod: String,
    
    @ColumnInfo(name = "amount")
    val amount: Double,
    
    @ColumnInfo(name = "receipt_uuid")
    val receiptUuid: String?,
    
    @ColumnInfo(name = "receipt_local_path")
    val receiptLocalPath: String?,
    
    @ColumnInfo(name = "admin_note")
    val adminNote: String?,
    
    @ColumnInfo(name = "expense_date")
    val expenseDate: String,
    
    // Poly-reference details
    @ColumnInfo(name = "reference_id")
    val referenceId: String?,
    @ColumnInfo(name = "reference_type")
    val referenceType: String?,
    @ColumnInfo(name = "reference_name")
    val referenceName: String?,
    
    @ColumnInfo(name = "cash_paid_to")
    val cashPaidTo: String?,
    
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
    
    @ColumnInfo(name = "vendor_name")
    val vendorName: String?,
    
    @ColumnInfo(name = "bill_number")
    val billNumber: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "created_by")
    val createdBy: String?,
    @ColumnInfo(name = "created_by_name")
    val createdByName: String?,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String
)
