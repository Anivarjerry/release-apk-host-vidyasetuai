package com.vidyasetuai.feature_campus.domain.repository

import com.vidyasetuai.feature_campus.domain.model.ModerationSettings
import com.vidyasetuai.feature_campus.domain.model.PrivateMessage
import com.vidyasetuai.feature_campus.domain.model.PrivateRoom
import com.vidyasetuai.feature_profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface CampusRepository {
    // ── Moderation & Profile Operations ────────────────────────────────────────
    suspend fun loadModerationSettings(): Result<ModerationSettings>
    suspend fun reportMessage(messageId: String, reporterUserId: String, reason: String): Result<Unit>
    suspend fun isContentAppropriate(content: String): Boolean
    fun getMutualInspirationsFlow(userId: String): Flow<List<UserProfile>>
    suspend fun getMutualInspirations(userId: String): Result<List<UserProfile>>
    suspend fun checkIsMutualConnection(userA: String, userB: String): Boolean

    // ── Offline-First & E2EE Private Campus Operations ────────────────────────
    fun getPrivateRoomsFlow(): Flow<List<PrivateRoom>>
    fun getPrivateMessagesFlow(roomId: String): Flow<List<PrivateMessage>>
    suspend fun syncPrivateRooms(): Result<Unit>
    suspend fun syncPrivateMessages(roomId: String, peerUserId: String? = null): Result<Unit>
    suspend fun syncUnsyncedPrivateMessages(): Result<Unit>
    suspend fun markRoomAsRead(roomId: String, currentUserId: String): Result<Unit>
    
    suspend fun getOrCreatePrivateRoom(userA: String, userB: String): Result<PrivateRoom>
    suspend fun sendPrivateMessage(
        roomId: String,
        senderId: String,
        text: String?,
        mediaUrl: String?,
        peerUserId: String? = null
    ): Result<PrivateMessage>
    
    suspend fun toggleSavePrivateMessage(messageId: String, isSaved: Boolean): Result<Unit>
    fun observePrivateMessages(roomId: String, peerUserId: String? = null): Flow<PrivateMessage>
    suspend fun initializeUserKeys(userId: String): Result<Unit>
}