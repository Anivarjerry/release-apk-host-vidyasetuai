package com.vidyasetuai.feature_journey.domain.usecase

import com.vidyasetuai.feature_journey.domain.model.JourneyOwnerType
import com.vidyasetuai.feature_journey.domain.model.UserJourney
import com.vidyasetuai.feature_journey.domain.repository.JourneyRepository

class EnrollInJourneyUseCase(private val repository: JourneyRepository) {
    suspend operator fun invoke(
        userId: String,
        templateId: String,
        type: JourneyOwnerType,
        notificationTime: String?
    ): Result<UserJourney> {
        return repository.enrollInJourney(userId, templateId, type, notificationTime)
    }
}
