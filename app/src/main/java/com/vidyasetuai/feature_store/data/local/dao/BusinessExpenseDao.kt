package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vidyasetuai.feature_store.data.local.entity.BusinessExpenseEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessExpenseTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessExpenseDao {

    // 1. Observe Expenses Reactively on Dispatchers.IO
    @Query("SELECT * FROM business_expenses WHERE business_id = :businessId AND is_deleted = 0 ORDER BY expense_date DESC, created_at DESC")
    fun observeExpenses(businessId: String): Flow<List<BusinessExpenseEntity>>

    @Query("SELECT * FROM business_expenses WHERE business_id = :businessId AND is_deleted = 0 AND expense_date BETWEEN :startDate AND :endDate ORDER BY expense_date DESC, created_at DESC")
    fun observeExpensesByDateRange(businessId: String, startDate: String, endDate: String): Flow<List<BusinessExpenseEntity>>

    @Query("SELECT * FROM business_expenses WHERE id = :expenseId LIMIT 1")
    fun observeExpenseById(expenseId: String): Flow<BusinessExpenseEntity?>

    // 2. Observe Expense Categories / Types
    @Query("SELECT * FROM business_expense_types WHERE (business_id = :businessId OR is_system_default = 1) AND is_deleted = 0 ORDER BY is_system_default DESC, name ASC")
    fun observeExpenseTypes(businessId: String): Flow<List<BusinessExpenseTypeEntity>>

    // 3. One-Shot Queries
    @Query("SELECT * FROM business_expenses WHERE business_id = :businessId AND is_deleted = 0")
    suspend fun getExpenses(businessId: String): List<BusinessExpenseEntity>

    @Query("SELECT * FROM business_expense_types WHERE (business_id = :businessId OR is_system_default = 1) AND is_deleted = 0")
    suspend fun getExpenseTypes(businessId: String): List<BusinessExpenseTypeEntity>

    // 4. Upsert Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: BusinessExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExpenses(expenses: List<BusinessExpenseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseType(type: BusinessExpenseTypeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExpenseTypes(types: List<BusinessExpenseTypeEntity>)

    // 5. Soft Delete
    @Query("UPDATE business_expenses SET is_deleted = 1, updated_at = :updatedAt WHERE id = :expenseId")
    suspend fun softDeleteExpense(expenseId: String, updatedAt: String)

    @Query("DELETE FROM business_expenses WHERE business_id = :businessId")
    suspend fun clearExpenses(businessId: String)
}
