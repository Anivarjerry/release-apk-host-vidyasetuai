package com.vidyasetuai.feature_store.presentation.screen.role_staff.invoices

import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessEntity
import com.vidyasetuai.feature_store.domain.model.InvoiceWithDetailsUiModel

data class SalesInvoicesUiState(
    val businessId: String = "",
    val activeBusiness: BusinessEntity? = null,
    val activeBranch: BusinessBranchEntity? = null,
    val invoices: List<InvoiceWithDetailsUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,

    // Search & Filters
    val searchQuery: String = "",
    val statusFilter: InvoiceStatusFilter = InvoiceStatusFilter.ALL,

    // Modal / Sheet States
    val selectedInvoiceForDetails: InvoiceWithDetailsUiModel? = null,
    val isDetailsSheetOpen: Boolean = false,
    val selectedInvoiceForEway: InvoiceWithDetailsUiModel? = null,
    val isEwaySheetOpen: Boolean = false,
    val selectedInvoiceForCancel: InvoiceWithDetailsUiModel? = null,
    val isCancelDialogOpen: Boolean = false,

    // Print Options
    val printMode: PrintFormat = PrintFormat.THERMAL,

    // Feedback Messages
    val successMessage: String? = null,
    val errorMessage: String? = null
) {
    // Non-cancelled valid invoices for accurate accounting
    val activeNonCancelledInvoices: List<InvoiceWithDetailsUiModel>
        get() = invoices.filter { !it.isCancelled }

    // Summary KPI Totals (1:1 Web Parity)
    val totalSalesRevenue: Double
        get() = activeNonCancelledInvoices.sumOf { it.invoice.grandTotal }

    val totalPaidRevenue: Double
        get() = activeNonCancelledInvoices.sumOf { it.invoice.paidAmount }

    val totalCreditDue: Double
        get() = activeNonCancelledInvoices.sumOf { it.invoice.dueAmount }

    val totalInvoicesCount: Int
        get() = activeNonCancelledInvoices.size

    // Filtered invoices for list view
    val filteredInvoices: List<InvoiceWithDetailsUiModel>
        get() = invoices.filter { invWithDetails ->
            val inv = invWithDetails.invoice
            val matchesStatus = when (statusFilter) {
                InvoiceStatusFilter.ALL -> true
                InvoiceStatusFilter.PAID -> invWithDetails.isPaid
                InvoiceStatusFilter.CREDIT_KHATA -> invWithDetails.isCreditKhata
                InvoiceStatusFilter.UNPAID -> invWithDetails.isUnpaid
                InvoiceStatusFilter.CANCELLED -> invWithDetails.isCancelled
            }

            val q = searchQuery.trim().lowercase()
            val matchesQuery = if (q.isBlank()) true else {
                inv.invoiceNumber.lowercase().contains(q) ||
                        (invWithDetails.customerName?.lowercase()?.contains(q) == true) ||
                        (invWithDetails.customerPhone?.contains(q) == true) ||
                        inv.paymentStatus.lowercase().contains(q)
            }

            matchesStatus && matchesQuery
        }
}

enum class InvoiceStatusFilter {
    ALL,
    PAID,
    CREDIT_KHATA,
    UNPAID,
    CANCELLED
}

enum class PrintFormat {
    THERMAL,
    A4
}
