package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_institution.domain.model.FeePayment

@Entity(tableName = "local_fee_payments")
data class LocalFeePaymentEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val receiptNumber: String,
    val paymentMode: String,
    val paymentDate: String,
    val amountPaid: Double,
    val discountAmount: Double,
    val fineAmount: Double,
    val discountReason: String?,
    val remarks: String?
) {
    fun toDomain(): FeePayment = FeePayment(
        id = id,
        studentId = studentId,
        receiptNumber = receiptNumber,
        paymentMode = paymentMode,
        paymentDate = paymentDate,
        amountPaid = amountPaid,
        discountAmount = discountAmount,
        fineAmount = fineAmount,
        discountReason = discountReason,
        remarks = remarks
    )

    companion object {
        fun fromDomain(domain: FeePayment): LocalFeePaymentEntity = LocalFeePaymentEntity(
            id = domain.id,
            studentId = domain.studentId,
            receiptNumber = domain.receiptNumber,
            paymentMode = domain.paymentMode,
            paymentDate = domain.paymentDate,
            amountPaid = domain.amountPaid,
            discountAmount = domain.discountAmount,
            fineAmount = domain.fineAmount,
            discountReason = domain.discountReason,
            remarks = domain.remarks
        )
    }
}
