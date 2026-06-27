package com.vidyasetuai.feature_institution.domain.model

data class FeePayment(
    val id: String,
    val studentId: String,
    val receiptNumber: String,
    val paymentMode: String, // "Cash", "Cheque", "UPI", "Online"
    val paymentDate: String,
    val amountPaid: Double,
    val discountAmount: Double,
    val fineAmount: Double,
    val discountReason: String?,
    val remarks: String?
)
