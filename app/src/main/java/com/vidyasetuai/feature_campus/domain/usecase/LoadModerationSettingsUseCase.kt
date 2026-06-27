package com.vidyasetuai.feature_campus.domain.usecase

import com.vidyasetuai.feature_campus.domain.model.ModerationSettings
import com.vidyasetuai.feature_campus.domain.repository.CampusRepository

class LoadModerationSettingsUseCase(private val repository: CampusRepository) {
    suspend operator fun invoke(): Result<ModerationSettings> {
        return repository.loadModerationSettings()
    }
}
