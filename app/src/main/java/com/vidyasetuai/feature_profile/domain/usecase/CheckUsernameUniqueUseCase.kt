package com.vidyasetuai.feature_profile.domain.usecase

import com.vidyasetuai.feature_profile.domain.repository.ProfileRepository

class CheckUsernameUniqueUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(username: String, currentUserId: String): Result<Boolean> {
        if (username.isBlank()) return Result.success(false)
        return repository.checkUsernameUnique(username.trim(), currentUserId)
    }
}
