package com.vidyasetuai.feature_store.domain.repository

import com.vidyasetuai.feature_store.domain.model.BusinessExpenseUiModel
import com.vidyasetuai.feature_store.domain.model.ExpenseCategoryUiModel
import com.vidyasetuai.feature_store.domain.model.RecordExpensePayload
import kotlinx.coroutines.flow.Flow

interface BusinessExpenseRepository {

    /**
     * 0ms Reactive Room Flow returning all expenses with mapped category names and icons.
     */
    fun getExpensesFlow(businessId: String): Flow<List<BusinessExpenseUiModel>>

    /**
     * Flow of all available expense types/categories.
     */
    fun getExpenseCategoriesFlow(businessId: String): Flow<List<ExpenseCategoryUiModel>>

    /**
     * Records a new store expense, updates local Room DB immediately, and executes atomic RPC on Supabase.
     */
    suspend fun recordExpense(
        businessId: String,
        branchId: String?,
        payload: RecordExpensePayload
    ): Result<String> // Returns generated voucher number (e.g. 'EXP-26/0001')

    /**
     * Soft deletes an expense locally and remotely.
     */
    suspend fun deleteExpense(expenseId: String): Result<Boolean>
}
