package com.vidyasetuai.feature_store.presentation.screen.role_staff.invoices

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.repository.SalesOrderRepositoryImpl
import com.vidyasetuai.feature_store.domain.model.InvoiceWithDetailsUiModel
import com.vidyasetuai.feature_store.domain.repository.SalesOrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SalesInvoicesViewModel(
    context: Context,
    private val repository: SalesOrderRepository = SalesOrderRepositoryImpl(context)
) : ViewModel() {

    private val storeDb = StoreDatabase.getDatabase(context)

    private val _uiState = MutableStateFlow(SalesInvoicesUiState())
    val uiState: StateFlow<SalesInvoicesUiState> = _uiState.asStateFlow()

    private var invoicesJob: Job? = null

    init {
        observeActiveBusiness()
    }

    private fun observeActiveBusiness() {
        viewModelScope.launch {
            storeDb.businessDao().getAnyActiveBusinessFlow().collect { activeBusiness ->
                val businessId = activeBusiness?.id ?: ""
                _uiState.update {
                    it.copy(
                        businessId = businessId,
                        activeBusiness = activeBusiness
                    )
                }

                if (businessId.isNotEmpty()) {
                    observeBranch(businessId)
                    observeInvoices(businessId)
                }
            }
        }
    }

    private fun observeBranch(businessId: String) {
        viewModelScope.launch {
            storeDb.businessBranchDao().getBranchesFlow(businessId).collect { branches ->
                val branch = branches.firstOrNull()
                _uiState.update { it.copy(activeBranch = branch) }
            }
        }
    }

    private fun observeInvoices(businessId: String) {
        invoicesJob?.cancel()
        invoicesJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getInvoicesWithDetailsFlow(businessId).collect { invoicesList ->
                _uiState.update {
                    it.copy(
                        invoices = invoicesList,
                        isLoading = false
                    )
                }
            }
        }

        // Background Remote Sync from Supabase
        viewModelScope.launch {
            repository.syncInvoices(businessId)
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setStatusFilter(filter: InvoiceStatusFilter) {
        _uiState.update { it.copy(statusFilter = filter) }
    }

    fun setPrintMode(mode: PrintFormat) {
        _uiState.update { it.copy(printMode = mode) }
    }

    fun openDetails(invoice: InvoiceWithDetailsUiModel) {
        _uiState.update {
            it.copy(
                selectedInvoiceForDetails = invoice,
                isDetailsSheetOpen = true
            )
        }
    }

    fun closeDetails() {
        _uiState.update {
            it.copy(
                selectedInvoiceForDetails = null,
                isDetailsSheetOpen = false
            )
        }
    }

    fun openEway(invoice: InvoiceWithDetailsUiModel) {
        _uiState.update {
            it.copy(
                selectedInvoiceForEway = invoice,
                isEwaySheetOpen = true
            )
        }
    }

    fun closeEway() {
        _uiState.update {
            it.copy(
                selectedInvoiceForEway = null,
                isEwaySheetOpen = false
            )
        }
    }

    fun promptCancel(invoice: InvoiceWithDetailsUiModel) {
        _uiState.update {
            it.copy(
                selectedInvoiceForCancel = invoice,
                isCancelDialogOpen = true
            )
        }
    }

    fun dismissCancelDialog() {
        _uiState.update {
            it.copy(
                selectedInvoiceForCancel = null,
                isCancelDialogOpen = false
            )
        }
    }

    fun confirmCancelInvoice() {
        val invoice = _uiState.value.selectedInvoiceForCancel ?: return
        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            val result = repository.cancelInvoice(invoice.invoice.id)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            isCancelDialogOpen = false,
                            selectedInvoiceForCancel = null,
                            isDetailsSheetOpen = false,
                            selectedInvoiceForDetails = null,
                            successMessage = "Invoice #${invoice.invoice.invoiceNumber} marked as CANCELLED."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "Failed to cancel invoice."
                        )
                    }
                }
            )
        }
    }

    fun updateEwayBill(
        invoiceId: String,
        ewayBillNo: String?,
        vehicleNo: String?,
        transportName: String?,
        distanceKm: Int,
        ewayStatus: String
    ) {
        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            val result = repository.updateEwayBill(
                invoiceId = invoiceId,
                ewayBillNo = ewayBillNo,
                vehicleNo = vehicleNo,
                transportName = transportName,
                distanceKm = distanceKm,
                ewayStatus = ewayStatus
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            isEwaySheetOpen = false,
                            selectedInvoiceForEway = null,
                            successMessage = "E-Way Bill details updated successfully."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "Failed to update E-Way Bill."
                        )
                    }
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
