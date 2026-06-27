package com.vidyasetuai.feature_journey.domain.usecase

import com.vidyasetuai.feature_journey.domain.model.JourneyOwnerType
import com.vidyasetuai.feature_journey.domain.repository.JourneyRepository

class DownloadJourneyContentUseCase(private val repository: JourneyRepository) {
    suspend operator fun invoke(templateId: String, type: JourneyOwnerType): Result<Unit> {
        return repository.downloadJourneyContent(templateId, type)
    }
}
