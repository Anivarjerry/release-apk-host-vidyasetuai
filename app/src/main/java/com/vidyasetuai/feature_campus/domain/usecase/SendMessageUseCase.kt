package com.vidyasetuai.feature_campus.domain.usecase

import com.vidyasetuai.feature_campus.domain.model.CampusMessage
import com.vidyasetuai.feature_campus.domain.repository.CampusRepository

class SendMessageUseCase(private val repository: CampusRepository) {
    suspend operator fun invoke(roomId: String, userId: String, content: String): Result<CampusMessage> {
        return repository.sendMessage(roomId, userId, content)
    }
}
