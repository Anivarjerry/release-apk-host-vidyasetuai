package com.vidyasetuai.feature_store.presentation.screen.role_owner.purchases

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_store.data.repository.PurchaseBillsRepositoryImpl
import com.vidyasetuai.feature_store.domain.model.PurchaseBillUiModel
import com.vidyasetuai.feature_store.domain.model.PurchaseFilterStatus
import com.vidyasetuai.feature_store.domain.model.PurchaseSummaryUiModel
import com.vidyasetuai.feature_store.domain.model.RecordPurchaseBillPayload
import com.vidyasetuai.feature_store.domain.repository.PurchaseBillsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PurchaseBillsViewModel(
    context: Context,
    private val repository: PurchaseBillsRepository = PurchaseBillsRepositoryImpl(context)
) : ViewModel() {

    private val _uiState = MutableStateFlow(PurchaseBillsUiState())
    val uiState: StateFlow<PurchaseBillsUiState> = _uiState.asStateFlow()

    fun loadData(businessId: String) {
        viewModelScope.launch {
            repository.getSuppliersFlow(businessId).collect { suppliers ->
                _uiState.update { it.copy(suppliers = suppliers) }
            }
        }

        viewModelScope.launch {
            repository.getCatalogItemsFlow(businessId).collect { items ->
                _uiState.update { it.copy(catalogItems = items) }
            }
        }

        viewModelScope.launch {
            repository.getPurchaseBillsFlow(businessId).collect { purchases ->
                val totalSpend = purchases.sumOf { it.grandTotal }
                val totalPaid = purchases.sumOf { it.paidAmount }
                val totalDue = purchases.sumOf { it.dueAmount }
                val uniqueSuppliers = purchases.map { it.supplierId }.distinct().size

                val summary = PurchaseSummaryUiModel(
                    totalSpend = totalSpend,
                    totalPaid = totalPaid,
                    totalDue = totalDue,
                    supplierCount = uniqueSuppliers
                )

                _uiState.update {
                    it.copy(
                        purchases = purchases,
                        summary = summary,
                        isLoading = false
                    )
                }
                applyFilterAndSearch()
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilterAndSearch()
    }

    fun setStatusFilter(filter: PurchaseFilterStatus) {
        _uiState.update { it.copy(selectedStatusFilter = filter) }
        applyFilterAndSearch()
    }

    private fun applyFilterAndSearch() {
        val all = _uiState.value.purchases
        val query = _uiState.value.searchQuery.trim().lowercase()
        val filter = _uiState.value.selectedStatusFilter

        val filtered = all.filter { pur ->
            val matchesQuery = query.isBlank() ||
                    pur.supplierInvoiceNumber.lowercase().contains(query) ||
                    pur.supplierName.lowercase().contains(query) ||
                    (pur.supplierPhone?.contains(query) == true)

            val matchesFilter = when (filter) {
                PurchaseFilterStatus.ALL -> true
                PurchaseFilterStatus.PAID -> pur.isPaid
                PurchaseFilterStatus.PARTIAL -> pur.isPartial
                PurchaseFilterStatus.UNPAID -> pur.isUnpaid
            }

            matchesQuery && matchesFilter
        }

        _uiState.update { it.copy(filteredPurchases = filtered) }
    }

    fun openAddDrawer() {
        _uiState.update { it.copy(isAddDrawerOpen = true) }
    }

    fun closeAddDrawer() {
        _uiState.update { it.copy(isAddDrawerOpen = false) }
    }

    fun openPurchaseDetail(purchase: PurchaseBillUiModel) {
        _uiState.update { it.copy(selectedPurchaseForDetail = purchase) }
    }

    fun closePurchaseDetail() {
        _uiState.update { it.copy(selectedPurchaseForDetail = null) }
    }

    fun recordPurchaseBill(
        businessId: String,
        branchId: String,
        payload: RecordPurchaseBillPayload
    ) {
        if (payload.supplierId.isBlank() || payload.supplierInvoiceNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "कृपया सप्लायर और बिल नंबर भरें।") }
            return
        }

        if (payload.purchaseItems.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "कृपया कम से कम एक सामान जोड़ें।") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val result = repository.recordPurchaseBill(businessId, branchId, payload)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isAddDrawerOpen = false,
                        successMessage = "खरीद बिल #${payload.supplierInvoiceNumber} सफलतापूर्वक दर्ज हो गया और स्टॉक बढ़ गया! (+Qty)"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = "खरीद बिल दर्ज करने में त्रुटि: ${err.message}"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
