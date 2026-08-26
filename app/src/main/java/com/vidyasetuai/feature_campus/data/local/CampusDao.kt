package com.vidyasetuai.feature_campus.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * 0ms Offline-First Room DAO for Campus Social & E2EE Direct Messaging.
 * Feeds reactive Flows directly into Jetpack Compose LazyColumn layouts on Dispatchers.IO.
 */
@Dao
interface CampusDao {

    // ========================================================================
    // 1. CONNECTIONS & PEER PROFILE QUERIES (0ms Single-Query Read)
    // ========================================================================

    @Query("""
        SELECT * FROM campus_connections 
        WHERE user_id = :userId AND is_deleted = 0 
        ORDER BY updated_at DESC
    """)
    fun getAllConnectionsFlow(userId: String): Flow<List<CampusConnectionEntity>>

    @Query("""
        SELECT * FROM campus_connections 
        WHERE user_id = :userId AND is_mutual = 1 AND is_deleted = 0 
        ORDER BY updated_at DESC
    """)
    fun getMutualConnectionsFlow(userId: String): Flow<List<CampusConnectionEntity>>

    @Query("""
        SELECT * FROM campus_connections 
        WHERE user_id = :userId AND target_user_id = :targetUserId AND is_deleted = 0 
        LIMIT 1
    """)
    suspend fun getConnectionByTargetUser(userId: String, targetUserId: String): CampusConnectionEntity?

    @Query("""
        SELECT * FROM campus_connections 
        WHERE user_id = :userId AND target_user_id = :targetUserId AND is_deleted = 0 
        LIMIT 1
    """)
    fun getConnectionByTargetUserFlow(userId: String, targetUserId: String): Flow<CampusConnectionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnections(connections: List<CampusConnectionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConnection(connection: CampusConnectionEntity)

    @Query("""
        UPDATE campus_connections 
        SET peer_avatar_local_path = :localPath, updated_at = :updatedAt 
        WHERE target_user_id = :targetUserId
    """)
    suspend fun updatePeerAvatarLocalPath(targetUserId: String, localPath: String, updatedAt: String)

    @Query("""
        UPDATE campus_connections 
        SET is_deleted = 1, is_mutual = 0, sync_status = 'PENDING_SYNC', updated_at = :updatedAt 
        WHERE user_id = :userId AND target_user_id = :targetUserId
    """)
    suspend fun softDeleteConnection(userId: String, targetUserId: String, updatedAt: String)

    @Query("""
        UPDATE campus_connections 
        SET status = :status, is_mutual = 0, sync_status = 'PENDING_SYNC', updated_at = :updatedAt 
        WHERE user_id = :userId AND target_user_id = :targetUserId
    """)
    suspend fun updateConnectionStatus(userId: String, targetUserId: String, status: String, updatedAt: String)

    @Query("""
        UPDATE campus_connections 
        SET last_message_text = :lastMessageText,
            last_message_time = :lastMessageTime,
            last_message_status = :lastMessageStatus,
            unread_count = unread_count + :unreadIncrement,
            updated_at = :updatedAt
        WHERE user_id = :userId AND target_user_id = :targetUserId
    """)
    suspend fun updateConnectionLastMessage(
        userId: String,
        targetUserId: String,
        lastMessageText: String,
        lastMessageTime: String,
        lastMessageStatus: String,
        unreadIncrement: Int,
        updatedAt: String
    )

    @Query("""
        UPDATE campus_connections 
        SET unread_count = 0 
        WHERE user_id = :userId AND target_user_id = :targetUserId
    """)
    suspend fun clearConnectionUnreadCount(userId: String, targetUserId: String)


    // ========================================================================
    // 2. E2EE CHAT MESSAGES & REALTIME DELIVERY QUERIES
    // ========================================================================

    @Query("""
        SELECT * FROM (
            SELECT * FROM campus_messages 
            WHERE conversation_id = :conversationId AND is_deleted = 0 
            ORDER BY created_at DESC 
            LIMIT :limit
        ) ORDER BY created_at ASC
    """)
    fun getMessagesForConversationFlow(conversationId: String, limit: Int = 40): Flow<List<CampusMessageEntity>>

    @Query("""
        SELECT * FROM campus_messages 
        WHERE conversation_id = :conversationId AND is_deleted = 0 
        ORDER BY created_at DESC 
        LIMIT 1
    """)
    suspend fun getLatestMessageForConversation(conversationId: String): CampusMessageEntity?

    @Query("""
        SELECT COUNT(*) FROM campus_messages 
        WHERE conversation_id = :conversationId 
          AND recipient_id = :myId 
          AND status != 'READ' 
          AND is_deleted = 0
    """)
    fun getUnreadCountForConversationFlow(conversationId: String, myId: String): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM campus_messages 
        WHERE recipient_id = :myId 
          AND status != 'READ' 
          AND is_deleted = 0
    """)
    fun getTotalUnreadCountFlow(myId: String): Flow<Int>

    @Query("""
        SELECT * FROM campus_messages 
        WHERE sender_id = :myId 
          AND sync_status = 'PENDING_OUTBOX' 
          AND is_deleted = 0 
        ORDER BY created_at ASC
    """)
    suspend fun getPendingOutboxMessages(myId: String): List<CampusMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<CampusMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMessage(message: CampusMessageEntity)

    @Query("SELECT * FROM campus_messages WHERE id = :id LIMIT 1")
    suspend fun getMessageById(id: String): CampusMessageEntity?

    @Query("DELETE FROM campus_messages WHERE id = :id")
    suspend fun deleteMessageById(id: String)

    @Query("""
        UPDATE campus_messages 
        SET status = :status, 
            delivered_at = COALESCE(:deliveredAt, delivered_at), 
            read_at = COALESCE(:readAt, read_at) 
        WHERE id = :messageId
    """)
    suspend fun updateMessageDeliveryStatus(messageId: String, status: String, deliveredAt: String? = null, readAt: String? = null)

    @Query("""
        UPDATE campus_messages 
        SET status = :status, sync_status = :syncStatus 
        WHERE id = :messageId
    """)
    suspend fun updateMessageStatus(messageId: String, status: String, syncStatus: String)

    @Query("""
        UPDATE campus_connections 
        SET last_message_status = :status 
        WHERE (user_id = :userId AND target_user_id = :targetUserId)
           OR (user_id = :targetUserId AND target_user_id = :userId)
    """)
    suspend fun updateConnectionLastMessageStatus(userId: String, targetUserId: String, status: String)

    @Query("""
        UPDATE campus_connections 
        SET last_message_status = :status,
            last_message_time = COALESCE(:displayTime, last_message_time),
            updated_at = COALESCE(:isoTimestamp, updated_at)
        WHERE (user_id = :userId AND target_user_id = :targetUserId)
           OR (user_id = :targetUserId AND target_user_id = :userId)
    """)
    suspend fun updateConnectionLastMessageState(
        userId: String,
        targetUserId: String,
        status: String,
        displayTime: String? = null,
        isoTimestamp: String? = null
    )

    @Transaction
    suspend fun reconcileSentMessage(
        oldId: String,
        newId: String,
        status: String,
        syncStatus: String,
        createdAt: String,
        currentUserId: String? = null,
        targetUserId: String? = null,
        displayTime: String? = null,
        isoTimestamp: String? = null
    ) {
        if (oldId != newId) {
            val oldMsg = getMessageById(oldId)
            if (oldMsg != null) {
                deleteMessageById(oldId)
                upsertMessage(oldMsg.copy(id = newId, status = status, syncStatus = syncStatus, createdAt = createdAt))
            } else {
                updateMessageStatus(newId, status, syncStatus)
            }
        } else {
            updateMessageStatus(oldId, status, syncStatus)
        }
        if (!currentUserId.isNullOrBlank() && !targetUserId.isNullOrBlank()) {
            updateConnectionLastMessageState(
                userId = currentUserId,
                targetUserId = targetUserId,
                status = status,
                displayTime = displayTime,
                isoTimestamp = isoTimestamp ?: createdAt
            )
        }
    }

    @Query("""
        UPDATE campus_messages 
        SET status = 'READ', read_at = :readTimestamp 
        WHERE conversation_id = :conversationId 
          AND recipient_id = :myId 
          AND status != 'READ'
    """)
    suspend fun markConversationReadLocally(conversationId: String, myId: String, readTimestamp: String)

    @Query("""
        UPDATE campus_messages 
        SET is_saved = :isSaved 
        WHERE id = :messageId
    """)
    suspend fun toggleSaveMessage(messageId: String, isSaved: Boolean)

    @Transaction
    suspend fun recordIncomingMessageAtomic(
        message: CampusMessageEntity,
        currentUserId: String,
        peerUserId: String,
        decryptedText: String,
        displayTime: String,
        isoTimestamp: String
    ) {
        upsertMessage(message)
        updateConnectionLastMessage(
            userId = currentUserId,
            targetUserId = peerUserId,
            lastMessageText = decryptedText,
            lastMessageTime = displayTime,
            lastMessageStatus = message.status,
            unreadIncrement = 1,
            updatedAt = isoTimestamp
        )
    }

    @Transaction
    suspend fun recordOutgoingMessageAtomic(
        message: CampusMessageEntity,
        currentUserId: String,
        targetUserId: String,
        plaintext: String,
        displayTime: String,
        isoTimestamp: String
    ) {
        upsertMessage(message)
        updateConnectionLastMessage(
            userId = currentUserId,
            targetUserId = targetUserId,
            lastMessageText = plaintext,
            lastMessageTime = displayTime,
            lastMessageStatus = message.status,
            unreadIncrement = 0,
            updatedAt = isoTimestamp
        )
    }

    @Transaction
    suspend fun markChatReadAtomic(
        conversationId: String,
        currentUserId: String,
        peerUserId: String,
        readTimestamp: String
    ) {
        markConversationReadLocally(conversationId, currentUserId, readTimestamp)
        clearConnectionUnreadCount(currentUserId, peerUserId)
    }

    @Transaction
    suspend fun syncWorkspacePayloadAtomic(
        connections: List<CampusConnectionEntity>,
        messages: List<CampusMessageEntity>
    ) {
        insertMessages(messages)
        insertConnections(connections)
    }

    @Query("DELETE FROM campus_connections")
    suspend fun clearAllConnections()

    @Query("DELETE FROM campus_messages")
    suspend fun clearAllMessages()
}
