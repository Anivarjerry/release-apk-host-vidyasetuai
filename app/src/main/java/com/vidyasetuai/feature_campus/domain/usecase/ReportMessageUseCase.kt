package com.vidyasetuai.feature_campus.domain.usecase

import com.vidyasetuai.feature_campus.domain.repository.CampusRepository

class ReportMessageUseCase(private val repository: CampusRepository) {
    suspend operator fun invoke(messageId: String, reporterUserId: String, reason: String): Result<Unit> {
        return repository.reportMessage(messageId, reporterUserId, reason)
    }
}
