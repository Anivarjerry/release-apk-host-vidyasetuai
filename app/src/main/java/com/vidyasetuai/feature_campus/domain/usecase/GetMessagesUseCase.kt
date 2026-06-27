package com.vidyasetuai.feature_campus.domain.usecase

import com.vidyasetuai.feature_campus.domain.model.CampusMessage
import com.vidyasetuai.feature_campus.domain.repository.CampusRepository
import kotlinx.coroutines.flow.Flow

class GetMessagesUseCase(private val repository: CampusRepository) {
    operator fun invoke(roomId: String): Flow<List<CampusMessage>> {
        return repository.getMessages(roomId)
    }

    suspend fun sync(roomId: String): Result<Unit> {
        return repository.syncMessages(roomId)
    }

    fun observeRealtime(roomId: String): Flow<CampusMessage> {
        return repository.observeRealtimeMessages(roomId)
    }

    fun observePresence(roomId: String, userId: String): Flow<Int> {
        return repository.observePresenceCount(roomId, userId)
    }
}
