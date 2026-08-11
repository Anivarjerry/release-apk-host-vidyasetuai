package com.vidyasetuai.feature_institution.domain.model

import com.vidyasetuai.feature_institution.data.local.entity.LocalParentStaffEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalParentStaffSalaryEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalParentStaffSalaryPayoutEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalParentStaffSalaryPaymentEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalParentStaffBusEnrollmentEntity

data class StaffSalaryOverview(
    val staff: LocalParentStaffEntity,
    val salaryConfig: LocalParentStaffSalaryEntity?,
    val busEnrollment: LocalParentStaffBusEnrollmentEntity?,
    val currentPayout: LocalParentStaffSalaryPayoutEntity?,
    val payments: List<LocalParentStaffSalaryPaymentEntity>,
    val baseMonthlySalary: Double,
    val busDeductionAmount: Double,
    val netSalaryPayable: Double,
    val totalAmountPaid: Double,
    val dueBalance: Double,
    val hasPayoutGenerated: Boolean,
    val paymentStatus: String // "PAID", "PARTIAL", "PENDING", "NOT_GENERATED"
)
