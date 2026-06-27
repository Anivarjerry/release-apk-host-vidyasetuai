package com.vidyasetuai.feature_journey.domain.usecase

import com.vidyasetuai.feature_journey.domain.model.JourneyMcqProgress
import com.vidyasetuai.feature_journey.domain.repository.JourneyRepository

class SubmitMCQAttemptUseCase(private val repository: JourneyRepository) {
    suspend operator fun invoke(
        progress: JourneyMcqProgress,
        correctAnswer: String,
        points: Int
    ): Result<Boolean> {
        return repository.submitMcqAttempt(progress, correctAnswer, points)
    }
}
