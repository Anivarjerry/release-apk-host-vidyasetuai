package com.vidyasetuai.feature_institution.domain.usecase

import com.vidyasetuai.feature_institution.domain.model.Workspace
import com.vidyasetuai.feature_institution.domain.repository.InstitutionRepository

class GetWorkspacesUseCase(private val repository: InstitutionRepository) {
    suspend operator fun invoke(userId: String): Result<List<Workspace>> {
        return repository.getWorkspaces(userId)
    }
}
