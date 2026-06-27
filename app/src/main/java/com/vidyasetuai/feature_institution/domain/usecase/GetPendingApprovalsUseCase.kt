package com.vidyasetuai.feature_institution.domain.usecase

import com.vidyasetuai.feature_institution.domain.model.ConnectionState
import com.vidyasetuai.feature_institution.domain.repository.InstitutionRepository

class GetPendingApprovalsUseCase(private val repository: InstitutionRepository) {
    suspend operator fun invoke(userId: String): Result<ConnectionState> {
        return repository.checkConnectionStatus(userId)
    }
}
