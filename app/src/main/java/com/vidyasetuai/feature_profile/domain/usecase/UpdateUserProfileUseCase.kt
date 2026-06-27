package com.vidyasetuai.feature_profile.domain.usecase

import com.vidyasetuai.feature_profile.domain.model.UserProfile
import com.vidyasetuai.feature_profile.domain.repository.ProfileRepository

class UpdateUserProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(profile: UserProfile): Result<Unit> {
        return repository.updateProfile(profile)
    }
}
