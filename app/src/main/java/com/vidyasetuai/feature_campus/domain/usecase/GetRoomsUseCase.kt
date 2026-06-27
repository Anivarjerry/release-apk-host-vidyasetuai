package com.vidyasetuai.feature_campus.domain.usecase

import com.vidyasetuai.feature_campus.domain.model.CampusRoom
import com.vidyasetuai.feature_campus.domain.repository.CampusRepository
import kotlinx.coroutines.flow.Flow

class GetRoomsUseCase(private val repository: CampusRepository) {
    operator fun invoke(): Flow<List<CampusRoom>> {
        return repository.getRooms()
    }

    suspend fun sync(): Result<Unit> {
        return repository.syncRooms()
    }
}
