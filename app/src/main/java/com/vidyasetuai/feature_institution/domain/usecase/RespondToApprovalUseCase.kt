package com.vidyasetuai.feature_institution.domain.usecase

import com.vidyasetuai.feature_institution.domain.repository.InstitutionRepository

class RespondToApprovalUseCase(private val repository: InstitutionRepository) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return repository.approveConnection(userId)
    }
}
