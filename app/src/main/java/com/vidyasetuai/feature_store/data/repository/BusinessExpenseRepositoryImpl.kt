package com.vidyasetuai.feature_store.data.repository

import android.content.Context
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.entity.BusinessExpenseEntity
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import com.vidyasetuai.feature_store.domain.model.BusinessExpenseUiModel
import com.vidyasetuai.feature_store.domain.model.ExpenseCategoryUiModel
import com.vidyasetuai.feature_store.domain.model.RecordExpensePayload
import com.vidyasetuai.feature_store.domain.repository.BusinessExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID

class BusinessExpenseRepositoryImpl(
    context: Context,
    private val database: StoreDatabase = StoreDatabase.getDatabase(context),
    private val remoteDataSource: StoreRemoteDataSource = StoreRemoteDataSource()
) : BusinessExpenseRepository {

    private val expenseDao = database.businessExpenseDao()

    override fun getExpensesFlow(businessId: String): Flow<List<BusinessExpenseUiModel>> {
        val expensesFlow = expenseDao.observeExpenses(businessId)
        val typesFlow = expenseDao.observeExpenseTypes(businessId)

        return combine(expensesFlow, typesFlow) { expenses, types ->
            val typeMap = types.associateBy { it.id }

            expenses.map { entity ->
                val type = typeMap[entity.expenseTypeId]
                BusinessExpenseUiModel(
                    id = entity.id,
                    businessId = entity.businessId,
                    branchId = entity.branchId,
                    expenseTypeId = entity.expenseTypeId,
                    categoryName = type?.name ?: "General Expense",
                    categoryIcon = type?.icon ?: "Receipt",
                    categoryColor = type?.color ?: "#10B981",
                    voucherNumber = entity.voucherNumber,
                    title = entity.title,
                    amount = entity.amount,
                    paymentMode = entity.paymentMode,
                    expenseDate = entity.expenseDate,
                    referenceType = entity.referenceType,
                    referenceId = entity.referenceId,
                    paidTo = entity.paidTo,
                    notes = entity.notes,
                    createdAt = entity.createdAt
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getExpenseCategoriesFlow(businessId: String): Flow<List<ExpenseCategoryUiModel>> {
        return expenseDao.observeExpenseTypes(businessId).map { types ->
            types.map { type ->
                ExpenseCategoryUiModel(
                    id = type.id,
                    name = type.name,
                    nameHi = type.nameHi,
                    code = type.code,
                    referenceType = type.referenceType,
                    icon = type.icon,
                    color = type.color,
                    isSystemDefault = type.isSystemDefault
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun recordExpense(
        businessId: String,
        branchId: String?,
        payload: RecordExpensePayload
    ): Result<String> = withContext(Dispatchers.IO) {
        val tempId = UUID.randomUUID().toString()
        val nowStr = Instant.now().toString()

        // 1. Generate temp local voucher number
        val year = java.time.LocalDate.now().year % 100
        val tempVoucher = "EXP-$year/${System.currentTimeMillis() % 10000}"

        // 2. Optimistic local Room DB insert (0ms immediate UI update)
        val localEntity = BusinessExpenseEntity(
            id = tempId,
            businessId = businessId,
            branchId = branchId,
            expenseTypeId = payload.expenseTypeId,
            voucherNumber = tempVoucher,
            title = payload.title,
            amount = payload.amount,
            paymentMode = payload.paymentMode,
            expenseDate = payload.expenseDate,
            referenceType = payload.referenceType,
            referenceId = payload.referenceId,
            paidTo = payload.paidTo,
            notes = payload.notes,
            isActive = true,
            isDeleted = false,
            createdAt = nowStr,
            updatedAt = nowStr,
            syncStatus = "PENDING"
        )
        expenseDao.insertExpense(localEntity)

        // 3. Remote RPC Invocation via SafeSupabaseInvoker
        try {
            val remoteResult = remoteDataSource.recordExpense(
                businessId = businessId,
                branchId = branchId,
                expenseTypeId = payload.expenseTypeId,
                title = payload.title,
                amount = payload.amount,
                paymentMode = payload.paymentMode,
                expenseDate = payload.expenseDate,
                referenceType = payload.referenceType,
                referenceId = payload.referenceId,
                paidTo = payload.paidTo,
                notes = payload.notes
            )

            remoteResult.onSuccess { actualVoucher ->
                // Update local voucher with authoritative server voucher number
                val syncedEntity = localEntity.copy(
                    voucherNumber = actualVoucher,
                    syncStatus = "SYNCED"
                )
                expenseDao.insertExpense(syncedEntity)
            }
            remoteResult
        } catch (e: Exception) {
            Result.success(tempVoucher) // Offline fallback success
        }
    }

    override suspend fun deleteExpense(expenseId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val nowStr = Instant.now().toString()
        expenseDao.softDeleteExpense(expenseId, nowStr)
        remoteDataSource.deleteExpense(expenseId)
        Result.success(true)
    }
}
