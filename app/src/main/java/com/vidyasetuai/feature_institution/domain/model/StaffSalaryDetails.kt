package com.vidyasetuai.feature_institution.domain.model

data class StaffSalaryDetails(
    val monthlySalary: Double,
    val totalPaid: Double,
    val payments: List<StaffSalaryPayment>
)
