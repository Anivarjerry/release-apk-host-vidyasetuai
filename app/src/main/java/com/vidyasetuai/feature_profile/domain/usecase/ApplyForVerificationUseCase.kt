package com.vidyasetuai.feature_profile.domain.usecase

import com.vidyasetuai.feature_profile.domain.model.ContributorVerification
import com.vidyasetuai.feature_profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class ApplyForVerificationUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: String, applicantNote: String): Result<Unit> {
        return repository.applyForVerification(userId, applicantNote.trim())
    }

    fun getVerificationFlow(userId: String): Flow<ContributorVerification?> {
        return repository.getVerificationFlow(userId)
    }

    suspend fun sync(userId: String): Result<Unit> {
        return repository.syncVerification(userId)
    }
}
