package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_institution.domain.model.StaffSalaryPayment

@Entity(tableName = "local_staff_salary_details", primaryKeys = ["userId", "parentOrgId"])
data class LocalStaffSalaryDetailsEntity(
    val userId: String,
    val parentOrgId: String,
    val monthlySalary: Double,
    val totalPaid: Double
)

@Entity(tableName = "local_staff_salary_payments")
data class LocalStaffSalaryPaymentEntity(
    @PrimaryKey val id: String,
    val staffUserId: String,
    val parentOrgId: String,
    val paymentDate: String,
    val amountPaid: Double,
    val paymentMode: String,
    val transactionId: String?,
    val paymentApp: String?,
    val remarks: String?
) {
    fun toDomain(): StaffSalaryPayment = StaffSalaryPayment(
        id = id,
        paymentDate = paymentDate,
        amountPaid = amountPaid,
        paymentMode = paymentMode,
        transactionId = transactionId,
        paymentApp = paymentApp,
        remarks = remarks
    )

    companion object {
        fun fromDomain(domain: StaffSalaryPayment, userId: String, parentOrgId: String): LocalStaffSalaryPaymentEntity = LocalStaffSalaryPaymentEntity(
            id = domain.id,
            staffUserId = userId,
            parentOrgId = parentOrgId,
            paymentDate = domain.paymentDate,
            amountPaid = domain.amountPaid,
            paymentMode = domain.paymentMode,
            transactionId = domain.transactionId,
            paymentApp = domain.paymentApp,
            remarks = domain.remarks
        )
    }
}
