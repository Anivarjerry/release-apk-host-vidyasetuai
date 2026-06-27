package com.vidyasetuai.feature_campus.domain.repository

import com.vidyasetuai.feature_campus.domain.model.CampusMessage
import com.vidyasetuai.feature_campus.domain.model.CampusRoom
import com.vidyasetuai.feature_campus.domain.model.ModerationSettings
import kotlinx.coroutines.flow.Flow

interface CampusRepository {
    fun getRooms(): Flow<List<CampusRoom>>
    suspend fun syncRooms(): Result<Unit>
    fun getMessages(roomId: String): Flow<List<CampusMessage>>
    suspend fun syncMessages(roomId: String): Result<Unit>
    suspend fun sendMessage(roomId: String, userId: String, content: String): Result<CampusMessage>
    suspend fun loadModerationSettings(): Result<ModerationSettings>
    suspend fun reportMessage(messageId: String, reporterUserId: String, reason: String): Result<Unit>
    fun observeRealtimeMessages(roomId: String): Flow<CampusMessage>
    fun observePresenceCount(roomId: String, userId: String): Flow<Int>
    suspend fun isContentAppropriate(content: String): Boolean
}