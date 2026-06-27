package com.vidyasetuai.feature_journey.domain.usecase

import com.vidyasetuai.feature_journey.domain.model.JourneyOwnerType
import com.vidyasetuai.feature_journey.domain.model.JourneyTask
import com.vidyasetuai.feature_journey.domain.repository.JourneyRepository

class GetDailyTasksUseCase(private val repository: JourneyRepository) {
    suspend operator fun invoke(
        journeyId: String,
        templateId: String,
        type: JourneyOwnerType,
        dayNumber: Int
    ): List<JourneyTask> {
        return repository.getTasksForDay(journeyId, templateId, type, dayNumber)
    }
}
