package com.vidyasetuai.feature_institution.domain.usecase

import com.vidyasetuai.feature_institution.domain.repository.InstitutionRepository

class UpdateBusLiveLocationUseCase(private val repository: InstitutionRepository) {
    suspend operator fun invoke(
        busId: String,
        parentOrgId: String,
        latitude: Double,
        longitude: Double,
        speed: Double,
        sessionId: String?
    ): Result<Unit> {
        return repository.updateBusLiveLocation(
            busId = busId,
            parentOrgId = parentOrgId,
            latitude = latitude,
            longitude = longitude,
            speed = speed,
            sessionId = sessionId
        )
    }
}
