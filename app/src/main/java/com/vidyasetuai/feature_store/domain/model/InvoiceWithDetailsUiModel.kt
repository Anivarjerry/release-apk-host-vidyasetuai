package com.vidyasetuai.feature_store.domain.model

import com.vidyasetuai.feature_store.data.local.entity.InvoiceEntity
import com.vidyasetuai.feature_store.data.local.entity.InvoiceItemEntity

data class InvoiceWithDetailsUiModel(
    val invoice: InvoiceEntity,
    val items: List<InvoiceItemEntity> = emptyList(),
    val customerName: String? = null,
    val customerPhone: String? = null,
    val orderNumber: String? = null,
    val partyGstin: String? = null
) {
    val displayCustomerName: String
        get() = customerName?.takeIf { it.isNotBlank() } ?: "Walk-in Customer"

    val isCancelled: Boolean
        get() = invoice.paymentStatus.equals("CANCELLED", ignoreCase = true)

    val isPaid: Boolean
        get() = invoice.paymentStatus.equals("PAID", ignoreCase = true)

    val isCreditKhata: Boolean
        get() = invoice.paymentStatus.equals("CREDIT_KHATA", ignoreCase = true)

    val isUnpaid: Boolean
        get() = invoice.paymentStatus.equals("UNPAID", ignoreCase = true)

    val hasEwayBill: Boolean
        get() = !invoice.ewayBillNumber.isNullOrBlank()
}
