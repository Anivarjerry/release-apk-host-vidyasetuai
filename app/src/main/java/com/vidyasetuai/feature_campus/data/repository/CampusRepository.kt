package com.vidyasetuai.feature_campus.data.repository

import com.vidyasetuai.feature_campus.domain.model.CampusConnection
import com.vidyasetuai.feature_campus.domain.model.CampusConnectionStatus
import com.vidyasetuai.feature_campus.domain.model.CampusMessage
import com.vidyasetuai.feature_campus.domain.model.CampusSearchResult
import kotlinx.coroutines.flow.Flow

data class PeerPresence(
    val isOnline: Boolean = false,
    val isTyping: Boolean = false,
    val lastSeenFormatted: String? = null
)

interface CampusRepository {
    fun getConnectionsFlow(): Flow<List<CampusConnection>>
    fun getMutualConnectionsFlow(): Flow<List<CampusConnection>>
    fun getMessagesFlow(conversationId: String, limit: Int = 40): Flow<List<CampusMessage>>
    val peerPresenceFlow: Flow<PeerPresence>

    fun getTotalUnreadCountFlow(): Flow<Int>
    fun getUnreadCountForConversationFlow(conversationId: String): Flow<Int>
    suspend fun syncCampusData(): Result<Unit>
    suspend fun searchUsers(query: String): List<CampusSearchResult>
    suspend fun toggleInspireUser(
        targetUserId: String,
        currentStatus: CampusConnectionStatus,
        peerName: String? = null,
        peerUsername: String? = null,
        peerAvatarUrl: String? = null
    ): Result<CampusConnectionStatus>
    suspend fun sendMessage(recipientId: String, text: String, mediaUrl: String? = null, mediaType: String = "TEXT"): Result<Unit>
    suspend fun sendTypingIndicator(isTyping: Boolean)
    suspend fun markChatRead(conversationId: String)
    suspend fun followUser(targetUserId: String, peerName: String? = null, peerUsername: String? = null, peerAvatarUrl: String? = null): Result<Unit>
    suspend fun unfollowUser(targetUserId: String): Result<Unit>
    suspend fun blockUser(targetUserId: String): Result<Unit>

    suspend fun startRealtimeChatListener(conversationId: String)
    suspend fun stopRealtimeChatListener()
    suspend fun toggleSaveMessage(messageId: String, isSaved: Boolean)
    suspend fun clearAllLocalData()
    suspend fun getConnectionByPeerUserId(peerUserId: String): CampusConnection?
    fun getCurrentUserId(): String?
}

