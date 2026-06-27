package com.vidyasetuai.feature_journey.domain.usecase

import com.vidyasetuai.feature_journey.domain.model.UserJourney
import com.vidyasetuai.feature_journey.domain.repository.JourneyRepository
import kotlinx.coroutines.flow.Flow

class GetAllUserJourneysUseCase(private val repository: JourneyRepository) {
    operator fun invoke(userId: String): Flow<List<UserJourney>> {
        return repository.getAllUserJourneys(userId)
    }
}
