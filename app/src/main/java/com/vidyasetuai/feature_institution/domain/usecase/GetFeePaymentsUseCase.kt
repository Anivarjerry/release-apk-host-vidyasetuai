package com.vidyasetuai.feature_institution.domain.usecase

import com.vidyasetuai.feature_institution.domain.model.FeePayment
import com.vidyasetuai.feature_institution.domain.repository.InstitutionRepository

class GetFeePaymentsUseCase(private val repository: InstitutionRepository) {
    suspend operator fun invoke(studentId: String): Result<List<FeePayment>> {
        return repository.getFeePayments(listOf(studentId))
    }
}
