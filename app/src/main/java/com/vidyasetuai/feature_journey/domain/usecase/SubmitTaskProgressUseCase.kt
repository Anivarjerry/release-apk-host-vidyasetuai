package com.vidyasetuai.feature_journey.domain.usecase

import com.vidyasetuai.feature_journey.domain.model.JourneyTaskProgress
import com.vidyasetuai.feature_journey.domain.repository.JourneyRepository

class SubmitTaskProgressUseCase(private val repository: JourneyRepository) {
    suspend operator fun invoke(progress: JourneyTaskProgress): Result<Unit> {
        return repository.submitTaskProgress(progress)
    }
}
