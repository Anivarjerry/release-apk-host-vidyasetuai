package com.vidyasetuai.feature_journey.domain.usecase

import com.vidyasetuai.feature_journey.domain.model.JourneyTemplate
import com.vidyasetuai.feature_journey.domain.repository.JourneyRepository
import kotlinx.coroutines.flow.Flow

class GetAvailableTemplatesUseCase(private val repository: JourneyRepository) {
    operator fun invoke(orgId: String?, parentOrgId: String?): Flow<List<JourneyTemplate>> {
        return repository.getAvailableTemplates(orgId, parentOrgId)
    }
}
