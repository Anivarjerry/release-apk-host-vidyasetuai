package com.vidyasetuai.feature_journey.domain.usecase

import com.vidyasetuai.feature_journey.domain.model.LeaderboardEntry
import com.vidyasetuai.feature_journey.domain.repository.JourneyRepository

class GetJourneyLeaderboardUseCase(private val repository: JourneyRepository) {
    suspend operator fun invoke(
        globalTemplateId: String?,
        organizationTemplateId: String?,
        organizationParentTemplateId: String?
    ): List<LeaderboardEntry> {
        return repository.getLeaderboard(
            globalTemplateId,
            organizationTemplateId,
            organizationParentTemplateId
        )
    }
}
