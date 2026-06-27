package com.vidyasetuai.feature_journey.domain.usecase

import com.vidyasetuai.feature_journey.domain.model.JourneyMcq
import com.vidyasetuai.feature_journey.domain.model.JourneyOwnerType
import com.vidyasetuai.feature_journey.domain.repository.JourneyRepository

class GetDailyMCQsUseCase(private val repository: JourneyRepository) {
    suspend operator fun invoke(
        journeyId: String,
        templateId: String,
        type: JourneyOwnerType,
        dayNumber: Int
    ): List<JourneyMcq> {
        return repository.getMcqsForDay(journeyId, templateId, type, dayNumber)
    }
}
