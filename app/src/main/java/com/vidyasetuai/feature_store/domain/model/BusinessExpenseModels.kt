package com.vidyasetuai.feature_store.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BusinessExpenseUiModel(
    val id: String,
    val businessId: String,
    val branchId: String? = null,
    val expenseTypeId: String,
    val categoryName: String = "General Expense",
    val categoryIcon: String = "Receipt",
    val categoryColor: String = "#10B981",
    val voucherNumber: String,
    val title: String,
    val amount: Double,
    val paymentMode: String = "CASH", // 'CASH', 'UPI', 'BANK_TRANSFER', 'CHEQUE'
    val expenseDate: String, // 'YYYY-MM-DD'
    val referenceType: String = "NONE", // 'NONE', 'BRANCH', 'STAFF', 'SUPPLIER', 'ITEM'
    val referenceId: String? = null,
    val referenceName: String? = null,
    val paidTo: String? = null,
    val notes: String? = null,
    val createdAt: String
)

@Serializable
data class ExpenseCategoryUiModel(
    val id: String,
    val name: String,
    val nameHi: String? = null,
    val code: String,
    val referenceType: String = "NONE",
    val icon: String = "Receipt",
    val color: String = "#10B981",
    val isSystemDefault: Boolean = false
)

@Serializable
data class RecordExpensePayload(
    val expenseTypeId: String,
    val title: String,
    val amount: Double,
    val paymentMode: String = "CASH",
    val expenseDate: String,
    val referenceType: String = "NONE",
    val referenceId: String? = null,
    val paidTo: String? = null,
    val notes: String? = null
)
