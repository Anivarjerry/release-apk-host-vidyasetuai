package com.vidyasetuai.feature_profile.domain.usecase

import com.vidyasetuai.feature_profile.domain.model.UserProfile
import com.vidyasetuai.feature_profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase(private val repository: ProfileRepository) {
    operator fun invoke(userId: String): Flow<UserProfile?> {
        return repository.getProfileFlow(userId)
    }

    suspend fun sync(userId: String): Result<Unit> {
        return repository.syncProfile(userId)
    }
}