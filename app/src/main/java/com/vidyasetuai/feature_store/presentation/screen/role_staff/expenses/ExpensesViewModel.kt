package com.vidyasetuai.feature_store.presentation.screen.role_staff.expenses

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffEntity
import com.vidyasetuai.feature_store.data.local.entity.ItemEntity
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity
import com.vidyasetuai.feature_store.data.repository.BusinessExpenseRepositoryImpl
import com.vidyasetuai.feature_store.domain.model.BusinessExpenseUiModel
import com.vidyasetuai.feature_store.domain.model.ExpenseCategoryUiModel
import com.vidyasetuai.feature_store.domain.model.RecordExpensePayload
import com.vidyasetuai.feature_store.domain.repository.BusinessExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ExpensesUiState(
    val expenses: List<BusinessExpenseUiModel> = emptyList(),
    val categories: List<ExpenseCategoryUiModel> = emptyList(),
    val staffList: List<BusinessStaffEntity> = emptyList(),
    val branchesList: List<BusinessBranchEntity> = emptyList(),
    val partiesList: List<PartyEntity> = emptyList(),
    val itemsList: List<ItemEntity> = emptyList(),
    val selectedCategoryFilter: String? = null,
    val selectedPaymentModeFilter: String? = null,
    val searchQuery: String = "",
    val totalExpenseMonth: Double = 0.0,
    val todayCashExpense: Double = 0.0,
    val todayOnlineExpense: Double = 0.0,
    val totalVouchersCount: Int = 0,
    val isAddSheetOpen: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null
)

class ExpensesViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: BusinessExpenseRepository = BusinessExpenseRepositoryImpl(application),
    private val database: StoreDatabase = StoreDatabase.getDatabase(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState: StateFlow<ExpensesUiState> = _uiState.asStateFlow()

    private var currentBusinessId: String = ""
    private var currentBranchId: String? = null

    fun initialize(businessId: String, branchId: String?) {
        currentBusinessId = businessId
        currentBranchId = branchId

        // 1. Observe Expenses Flow (0ms local Room reads)
        viewModelScope.launch {
            repository.getExpensesFlow(businessId).collect { rawExpenses ->
                val todayStr = LocalDate.now().toString()
                val currentMonthStr = todayStr.substring(0, 7) // 'YYYY-MM'

                var totalMonth = 0.0
                var todayCash = 0.0
                var todayOnline = 0.0

                rawExpenses.forEach { exp ->
                    if (exp.expenseDate.startsWith(currentMonthStr)) {
                        totalMonth += exp.amount
                    }
                    if (exp.expenseDate == todayStr) {
                        if (exp.paymentMode == "CASH") {
                            todayCash += exp.amount
                        } else {
                            todayOnline += exp.amount
                        }
                    }
                }

                _uiState.update {
                    it.copy(
                        expenses = rawExpenses,
                        totalExpenseMonth = totalMonth,
                        todayCashExpense = todayCash,
                        todayOnlineExpense = todayOnline,
                        totalVouchersCount = rawExpenses.size
                    )
                }
            }
        }

        // 2. Observe Categories Flow
        viewModelScope.launch {
            repository.getExpenseCategoriesFlow(businessId).collect { rawCats ->
                _uiState.update { it.copy(categories = rawCats) }
            }
        }

        // 3. Observe Staff List Flow (for STAFF reference type)
        viewModelScope.launch {
            database.businessStaffDao().getStaffFlow(businessId).collect { staff ->
                _uiState.update { it.copy(staffList = staff) }
            }
        }

        // 4. Observe Branches List Flow (for BRANCH reference type)
        viewModelScope.launch {
            database.businessBranchDao().getBranchesFlow(businessId).collect { branches ->
                _uiState.update { it.copy(branchesList = branches) }
            }
        }

        // 5. Observe Parties List Flow (for SUPPLIER reference type)
        viewModelScope.launch {
            database.partyDao().getAllPartiesFlow(businessId).collect { parties ->
                _uiState.update { it.copy(partiesList = parties) }
            }
        }

        // 6. Observe Items List Flow (for ITEM wastage reference type)
        viewModelScope.launch {
            database.itemDao().getStoreItemsFlow(businessId).collect { items ->
                _uiState.update { it.copy(itemsList = items) }
            }
        }
    }

    fun setCategoryFilter(categoryId: String?) {
        _uiState.update { it.copy(selectedCategoryFilter = categoryId) }
    }

    fun setPaymentModeFilter(mode: String?) {
        _uiState.update { it.copy(selectedPaymentModeFilter = mode) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openAddSheet() {
        _uiState.update { it.copy(isAddSheetOpen = true) }
    }

    fun closeAddSheet() {
        _uiState.update { it.copy(isAddSheetOpen = false) }
    }

    fun recordExpense(
        expenseTypeId: String,
        branchId: String? = null,
        title: String,
        amount: Double,
        paymentMode: String,
        expenseDate: String,
        referenceType: String = "NONE",
        referenceId: String? = null,
        paidTo: String? = null,
        notes: String? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            val payload = RecordExpensePayload(
                expenseTypeId = expenseTypeId,
                title = title,
                amount = amount,
                paymentMode = paymentMode,
                expenseDate = expenseDate,
                referenceType = referenceType,
                referenceId = referenceId,
                paidTo = paidTo,
                notes = notes
            )
            val effectiveBranch = branchId ?: currentBranchId
            val result = repository.recordExpense(currentBusinessId, effectiveBranch, payload)
            result.onSuccess {
                _uiState.update { it.copy(isSubmitting = false, isAddSheetOpen = false) }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { it.copy(isSubmitting = false, error = err.message ?: "Failed to record expense") }
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            repository.deleteExpense(expenseId)
        }
    }
}
