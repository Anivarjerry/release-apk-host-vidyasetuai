package com.vidyasetuai.feature_institution.domain.model

data class StaffSalaryPayment(
    val id: String,
    val paymentDate: String,
    val amountPaid: Double,
    val paymentMode: String,
    val transactionId: String?,
    val paymentApp: String?,
    val remarks: String?
)
