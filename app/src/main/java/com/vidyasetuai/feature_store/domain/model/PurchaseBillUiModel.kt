package com.vidyasetuai.feature_store.domain.model

enum class PurchaseFilterStatus {
    ALL,
    PAID,
    PARTIAL,
    UNPAID
}

data class PurchaseItemUiModel(
    val itemId: String? = null,
    val itemName: String = "",
    val hsnSacCode: String? = null,
    val quantity: Double = 1.0,
    val unit: String = "PCS",
    val unitRate: Double = 0.0,
    val taxRate: Double = 5.0,
    val taxableValue: Double = 0.0,
    val cgstAmount: Double = 0.0,
    val sgstAmount: Double = 0.0,
    val totalAmount: Double = 0.0
)

data class PurchaseBillUiModel(
    val id: String,
    val businessId: String,
    val branchId: String = "",
    val supplierId: String,
    val supplierName: String = "Supplier",
    val supplierPhone: String? = null,
    val supplierInvoiceNumber: String,
    val invoiceDate: String,
    val taxableAmount: Double = 0.0,
    val cgstAmount: Double = 0.0,
    val sgstAmount: Double = 0.0,
    val igstAmount: Double = 0.0,
    val grandTotal: Double = 0.0,
    val paidAmount: Double = 0.0,
    val dueAmount: Double = 0.0,
    val paymentStatus: String = "UNPAID",
    val notes: String? = null,
    val createdAt: String = "",
    val items: List<PurchaseItemUiModel> = emptyList()
) {
    val isPaid: Boolean get() = paymentStatus.equals("PAID", ignoreCase = true) || dueAmount <= 0
    val isPartial: Boolean get() = paymentStatus.equals("PARTIAL", ignoreCase = true)
    val isUnpaid: Boolean get() = paymentStatus.equals("UNPAID", ignoreCase = true) && !isPaid
}

data class PurchaseSummaryUiModel(
    val totalSpend: Double = 0.0,
    val totalPaid: Double = 0.0,
    val totalDue: Double = 0.0,
    val supplierCount: Int = 0
)

data class RecordPurchaseBillPayload(
    val supplierId: String,
    val supplierInvoiceNumber: String,
    val billDate: String,
    val purchaseItems: List<PurchaseItemUiModel>,
    val taxableAmount: Double,
    val cgstAmount: Double,
    val sgstAmount: Double,
    val igstAmount: Double,
    val grandTotal: Double,
    val paidAmount: Double,
    val paymentMode: String = "CASH",
    val notes: String? = null
)
