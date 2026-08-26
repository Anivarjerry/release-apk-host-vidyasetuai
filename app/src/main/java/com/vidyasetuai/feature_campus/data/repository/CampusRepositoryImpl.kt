package com.vidyasetuai.feature_campus.data.repository

import android.content.Context
import android.util.Log
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_campus.data.local.CampusConnectionEntity
import com.vidyasetuai.feature_campus.data.local.CampusDao
import com.vidyasetuai.feature_campus.data.local.CampusMessageEntity
import com.vidyasetuai.feature_campus.data.remote.CampusRemoteDataSource
import com.vidyasetuai.feature_campus.domain.crypto.CampusCryptoEngine
import com.vidyasetuai.feature_campus.domain.model.CampusConnection
import com.vidyasetuai.feature_campus.domain.model.CampusConnectionStatus
import com.vidyasetuai.feature_campus.domain.model.CampusMessage
import com.vidyasetuai.feature_campus.domain.model.CampusSearchResult
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.broadcast
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import com.vidyasetuai.feature_campus.domain.util.CampusDateTimeUtils
import java.util.*

@Serializable
data class CampusTypingPayload(
    @SerialName("user_id") val userId: String = "",
    @SerialName("is_typing") val isTyping: Boolean = false
)

class CampusRepositoryImpl(
    private val campusDao: CampusDao,
    private val remoteDataSource: CampusRemoteDataSource = CampusRemoteDataSource(),
    private val context: Context? = null
) : CampusRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var activeRealtimeChannel: RealtimeChannel? = null
    private var realtimeSubscriptionJob: Job? = null

    override fun getCurrentUserId(): String? {
        val sessionUserId = context?.let { 
            try {
                com.vidyasetuai.core.auth.SessionManager(it).getUserId()
            } catch (e: Exception) {
                null
            }
        }
        if (!sessionUserId.isNullOrBlank()) return sessionUserId
        return SupabaseClient.client.auth.currentSessionOrNull()?.user?.id
    }

    private val _peerPresence = MutableStateFlow(PeerPresence())
    override val peerPresenceFlow: Flow<PeerPresence> = _peerPresence.asStateFlow()

    // 0ms WhatsApp-grade Single SQL Query Read (Zero N+1 Queries, Zero Runtime Decryption)
    override fun getConnectionsFlow(): Flow<List<CampusConnection>> {
        val currentUserId = getCurrentUserId() ?: return flowOf(emptyList())
        return campusDao.getAllConnectionsFlow(currentUserId).map { entities ->
            entities.map { entity ->
                CampusConnection(
                    id = entity.id,
                    userId = entity.userId,
                    targetUserId = entity.targetUserId,
                    status = entity.status,
                    isMutual = entity.isMutual,
                    peerName = entity.peerName ?: "Campus User",
                    peerUsername = entity.peerUsername ?: "",
                    peerAvatarUrl = entity.peerAvatarUrl,
                    peerAvatarLocalPath = entity.peerAvatarLocalPath,
                    peerBio = entity.peerBio,
                    unreadCount = entity.unreadCount,
                    lastMessageText = entity.lastMessageText,
                    lastMessageTime = entity.lastMessageTime,
                    lastMessageStatus = entity.lastMessageStatus
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getMutualConnectionsFlow(): Flow<List<CampusConnection>> {
        val currentUserId = getCurrentUserId() ?: return flowOf(emptyList())
        return campusDao.getMutualConnectionsFlow(currentUserId).map { entities ->
            entities.map { entity ->
                CampusConnection(
                    id = entity.id,
                    userId = entity.userId,
                    targetUserId = entity.targetUserId,
                    status = entity.status,
                    isMutual = entity.isMutual,
                    peerName = entity.peerName ?: "Campus User",
                    peerUsername = entity.peerUsername ?: "",
                    peerAvatarUrl = entity.peerAvatarUrl,
                    peerAvatarLocalPath = entity.peerAvatarLocalPath,
                    peerBio = entity.peerBio,
                    unreadCount = entity.unreadCount,
                    lastMessageText = entity.lastMessageText,
                    lastMessageTime = entity.lastMessageTime,
                    lastMessageStatus = entity.lastMessageStatus
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    // 0ms WhatsApp-grade Instant Plaintext Message Stream (Zero Runtime Decryption)
    override fun getMessagesFlow(conversationId: String, limit: Int): Flow<List<CampusMessage>> {
        val currentUserId = getCurrentUserId() ?: ""
        return campusDao.getMessagesForConversationFlow(conversationId, limit)
            .map { entities ->
                entities.map { entity ->
                    CampusMessage(
                        id = entity.id,
                        conversationId = entity.conversationId,
                        senderId = entity.senderId,
                        recipientId = entity.recipientId,
                        text = entity.text,
                        mediaUrl = entity.mediaUrl,
                        mediaLocalPath = entity.mediaLocalPath,
                        mediaType = entity.mediaType,
                        status = entity.status,
                        isSaved = entity.isSaved,
                        isOutgoing = (entity.senderId == currentUserId),
                        createdAt = entity.createdAt,
                        createdAtFormatted = CampusDateTimeUtils.formatDisplayTime(entity.createdAt),
                        deliveredAt = entity.deliveredAt,
                        readAt = entity.readAt
                    )
                }
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getTotalUnreadCountFlow(): Flow<Int> {
        val currentUserId = getCurrentUserId() ?: return flowOf(0)
        return campusDao.getTotalUnreadCountFlow(currentUserId)
    }

    private val avatarCacheManager by lazy {
        context?.let { com.vidyasetuai.feature_campus.data.local.CampusAvatarCacheManager(it, campusDao) }
    }

    override fun getUnreadCountForConversationFlow(conversationId: String): Flow<Int> {
        val currentUserId = getCurrentUserId() ?: return flowOf(0)
        return campusDao.getUnreadCountForConversationFlow(conversationId, currentUserId)
    }

    override suspend fun getConnectionByPeerUserId(peerUserId: String): CampusConnection? = withContext(Dispatchers.IO) {
        val currentUserId = getCurrentUserId() ?: return@withContext null
        val entity = campusDao.getConnectionByTargetUser(currentUserId, peerUserId)
        if (entity != null) {
            CampusConnection(
                id = entity.id,
                userId = entity.userId,
                targetUserId = entity.targetUserId,
                status = entity.status,
                isMutual = entity.isMutual,
                peerName = entity.peerName ?: "Campus User",
                peerUsername = entity.peerUsername ?: "",
                peerAvatarUrl = entity.peerAvatarUrl,
                peerAvatarLocalPath = entity.peerAvatarLocalPath,
                peerBio = entity.peerBio,
                unreadCount = entity.unreadCount,
                lastMessageText = entity.lastMessageText,
                lastMessageTime = entity.lastMessageTime,
                lastMessageStatus = entity.lastMessageStatus
            )
        } else {
            // Synthesize optimistic connection model for instant 0ms deep-link chat opening
            CampusConnection(
                id = peerUserId,
                userId = currentUserId,
                targetUserId = peerUserId,
                status = "FOLLOWING",
                isMutual = true,
                peerName = "Campus Friend",
                peerUsername = "",
                peerAvatarUrl = null,
                peerAvatarLocalPath = null,
                peerBio = null
            )
        }
    }

    override suspend fun syncCampusData(): Result<Unit> = withContext(Dispatchers.IO) {
        val currentUserId = getCurrentUserId() ?: return@withContext Result.failure(Exception("Unauthorized"))

        try {
            val payload = remoteDataSource.fetchWorkspacePayload()
            if (payload != null) {
                // 1. Decrypt ONCE at sync-time and store pre-decrypted plaintext
                val messageEntities = payload.messages.map { dto ->
                    val decrypted = CampusCryptoEngine.decrypt(dto.encryptedPayload, dto.conversationId)
                    CampusMessageEntity(
                        id = dto.id,
                        conversationId = dto.conversationId,
                        senderId = dto.senderId,
                        recipientId = dto.recipientId,
                        text = decrypted,
                        encryptedPayload = dto.encryptedPayload,
                        mediaUrl = dto.mediaUrl,
                        mediaLocalPath = null,
                        mediaType = dto.mediaType,
                        status = dto.status,
                        isSaved = dto.isSaved,
                        expiresAt = dto.expiresAt,
                        syncStatus = "SYNCED",
                        syncVersion = dto.syncVersion,
                        isDeleted = dto.isDeleted,
                        createdAt = dto.createdAt,
                        deliveredAt = dto.deliveredAt,
                        readAt = dto.readAt
                    )
                }

                // 2. Cache Connections with pre-calculated latest message and unread count (Zero null flicker)
                val avatarsDir = context?.let { java.io.File(it.filesDir, "campus_avatars") }
                val connectionEntities = payload.connections.map { dto ->
                    val cachedFile = avatarsDir?.let { java.io.File(it, "avatar_${dto.targetUserId}.jpg") }
                    val localPath = if (cachedFile?.exists() == true && cachedFile.length() > 0) cachedFile.absolutePath else null

                    val convId = CampusCryptoEngine.generateConversationId(currentUserId, dto.targetUserId)
                    val latestMsg = messageEntities
                        .filter { it.conversationId == convId }
                        .maxByOrNull { it.createdAt }

                    val unread = messageEntities.count {
                        it.conversationId == convId &&
                        it.recipientId == currentUserId &&
                        it.status != "READ"
                    }

                    CampusConnectionEntity(
                        id = dto.id,
                        userId = dto.userId,
                        targetUserId = dto.targetUserId,
                        status = dto.status,
                        isMutual = dto.isMutual,
                        peerName = dto.peerName,
                        peerUsername = dto.peerUsername,
                        peerAvatarUrl = dto.peerAvatarUrl,
                        peerAvatarLocalPath = localPath,
                        peerBio = dto.peerBio,
                        lastMessageText = latestMsg?.text,
                        lastMessageTime = latestMsg?.let { CampusDateTimeUtils.formatDisplayTime(it.createdAt) },
                        lastMessageStatus = latestMsg?.status,
                        unreadCount = unread,
                        syncStatus = "SYNCED",
                        syncVersion = dto.syncVersion,
                        isDeleted = dto.isDeleted,
                        updatedAt = latestMsg?.createdAt ?: dto.updatedAt ?: CampusDateTimeUtils.nowIso()
                    )
                }

                // 3. Single-Flight Atomic SQLite Write (Zero Intermediate null emissions to Compose)
                campusDao.syncWorkspacePayloadAtomic(connectionEntities, messageEntities)

                // 4. Process Pending Outbox messages
                processPendingOutboxMessages(currentUserId)

                // 5. Background Offline Avatar Image Download & Local Path Sync
                avatarCacheManager?.let { cacheMgr ->
                    repositoryScope.launch {
                        cacheMgr.downloadAndCacheAvatars(connectionEntities)
                    }
                }

                Log.d("CampusRepo", "Synced ${connectionEntities.size} connections and ${messageEntities.size} messages.")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch campus workspace payload"))
            }
        } catch (e: Exception) {
            Log.e("CampusRepo", "Sync campus data failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun sendMessage(
        recipientId: String,
        text: String,
        mediaUrl: String?,
        mediaType: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val currentUserId = getCurrentUserId() ?: return@withContext Result.failure(Exception("Unauthorized"))
        val conversationId = CampusCryptoEngine.generateConversationId(currentUserId, recipientId)
        val messageId = UUID.randomUUID().toString()
        val encryptedPayload = CampusCryptoEngine.encrypt(text, conversationId)
        val now = CampusDateTimeUtils.nowIso()
        val displayTime = CampusDateTimeUtils.formatDisplayTime(now)

        // 1. WhatsApp-grade Atomic Local Write (Message + Connection denormalization in 0ms)
        val localMessage = CampusMessageEntity(
            id = messageId,
            conversationId = conversationId,
            senderId = currentUserId,
            recipientId = recipientId,
            text = text, // Plaintext for 0ms instant display
            encryptedPayload = encryptedPayload,
            mediaUrl = mediaUrl,
            mediaLocalPath = null,
            mediaType = mediaType,
            status = "PENDING",
            isSaved = false,
            expiresAt = null,
            syncStatus = "PENDING_OUTBOX",
            isDeleted = false,
            createdAt = now
        )
        campusDao.recordOutgoingMessageAtomic(
            message = localMessage,
            currentUserId = currentUserId,
            targetUserId = recipientId,
            plaintext = text,
            displayTime = displayTime,
            isoTimestamp = now
        )

        // 2. Dispatch network call asynchronously via Traffic Police
        repositoryScope.launch {
            try {
                val response = remoteDataSource.sendMessage(
                    conversationId = conversationId,
                    recipientId = recipientId,
                    encryptedPayload = encryptedPayload,
                    mediaUrl = mediaUrl,
                    mediaType = mediaType
                )

                if (response != null && response.success) {
                    val serverMsgId = response.messageId ?: messageId
                    val serverCreatedAt = response.createdAt ?: now
                    val serverDisplayTime = CampusDateTimeUtils.formatDisplayTime(serverCreatedAt)
                    // Zero Duplicate Swapping: Reconcile local temp UUID with server generated ID
                    campusDao.reconcileSentMessage(
                        oldId = messageId,
                        newId = serverMsgId,
                        status = "SENT",
                        syncStatus = "SYNCED",
                        createdAt = serverCreatedAt,
                        currentUserId = currentUserId,
                        targetUserId = recipientId,
                        displayTime = serverDisplayTime,
                        isoTimestamp = serverCreatedAt
                    )
                } else if (response?.error == "BLOCKED_BY_RECIPIENT") {
                    campusDao.updateMessageStatus(messageId, "FAILED", "FAILED")
                } else {
                    campusDao.updateMessageStatus(messageId, "PENDING", "PENDING_OUTBOX")
                }
            } catch (e: Exception) {
                Log.w("CampusRepo", "Message background send deferred: ${e.message}")
            }
        }

        Result.success(Unit)
    }

    override suspend fun sendTypingIndicator(isTyping: Boolean): Unit = withContext(Dispatchers.IO) {
        try {
            val myUserId = getCurrentUserId() ?: return@withContext
            activeRealtimeChannel?.broadcast(
                event = "typing",
                message = CampusTypingPayload(
                    userId = myUserId,
                    isTyping = isTyping
                )
            )
        } catch (e: Exception) {
            Log.w("CampusRepo", "Typing broadcast failed: ${e.message}")
        }
    }

    override suspend fun markChatRead(conversationId: String) = withContext(Dispatchers.IO) {
        val currentUserId = getCurrentUserId() ?: return@withContext
        val now = nowIso()

        // Extract peer target id from conversationId
        val parts = conversationId.removePrefix("conv_").split("_")
        val peerId = parts.firstOrNull { it != currentUserId } ?: ""

        // 1. 0ms Atomic Local Read & Unread Badge Clear
        campusDao.markChatReadAtomic(conversationId, currentUserId, peerId, now)

        // 2. Remote Read Receipt RPC
        repositoryScope.launch {
            remoteDataSource.markChatRead(conversationId)
        }
    }

    override suspend fun followUser(
        targetUserId: String,
        peerName: String?,
        peerUsername: String?,
        peerAvatarUrl: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val currentUserId = getCurrentUserId() ?: return@withContext Result.failure(Exception("Unauthorized"))
        val connectionId = UUID.randomUUID().toString()
        val now = nowIso()

        // 1. Optimistic Local Insert
        val localConnection = CampusConnectionEntity(
            id = connectionId,
            userId = currentUserId,
            targetUserId = targetUserId,
            status = "FOLLOWING",
            isMutual = false,
            peerName = peerName,
            peerUsername = peerUsername,
            peerAvatarUrl = peerAvatarUrl,
            peerAvatarLocalPath = null,
            peerBio = null,
            lastMessageText = null,
            lastMessageTime = null,
            lastMessageStatus = null,
            unreadCount = 0,
            syncStatus = "PENDING_SYNC",
            updatedAt = now
        )
        campusDao.upsertConnection(localConnection)

        // 2. Remote follow via Traffic Police
        val success = remoteDataSource.followUser(currentUserId, targetUserId)
        if (success) {
            syncCampusData()
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to follow user"))
        }
    }

    override suspend fun unfollowUser(targetUserId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val currentUserId = getCurrentUserId() ?: return@withContext Result.failure(Exception("Unauthorized"))
        val now = nowIso()

        // 1. 0ms Instant Room DB soft-delete
        campusDao.softDeleteConnection(currentUserId, targetUserId, now)

        // 2. Remote Supabase Call via Traffic Police
        val success = remoteDataSource.unfollowUser(currentUserId, targetUserId)
        if (success) {
            syncCampusData()
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to unfollow user"))
        }
    }

    override suspend fun blockUser(targetUserId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val currentUserId = getCurrentUserId() ?: return@withContext Result.failure(Exception("Unauthorized"))
        val now = nowIso()

        // 1. 0ms Instant Room DB update
        campusDao.updateConnectionStatus(currentUserId, targetUserId, "BLOCKED", now)

        // 2. Remote Supabase Call via Traffic Police
        val success = remoteDataSource.blockUser(currentUserId, targetUserId)
        if (success) {
            syncCampusData()
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to block user"))
        }
    }

    override suspend fun searchUsers(query: String): List<CampusSearchResult> = withContext(Dispatchers.IO) {
        val dtos = remoteDataSource.searchUsers(query)
        dtos.map { dto ->
            val status = when (dto.connectionStatus) {
                "MUTUAL" -> CampusConnectionStatus.MUTUAL
                "INSPIRED" -> CampusConnectionStatus.INSPIRED
                "INSPIRES_YOU" -> CampusConnectionStatus.INSPIRES_YOU
                else -> CampusConnectionStatus.NONE
            }

            CampusSearchResult(
                userId = dto.id,
                fullName = dto.fullName ?: "Campus Member",
                username = dto.username ?: "",
                avatarUrl = dto.avatarUrl,
                bio = dto.bio,
                isVerified = dto.isVerified,
                status = status
            )
        }
    }

    override suspend fun toggleInspireUser(
        targetUserId: String,
        currentStatus: CampusConnectionStatus,
        peerName: String?,
        peerUsername: String?,
        peerAvatarUrl: String?
    ): Result<CampusConnectionStatus> = withContext(Dispatchers.IO) {
        val currentUserId = getCurrentUserId() ?: return@withContext Result.failure(Exception("Unauthorized"))

        if (currentStatus == CampusConnectionStatus.INSPIRED || currentStatus == CampusConnectionStatus.MUTUAL) {
            // Un-inspire (Unfollow)
            val result = unfollowUser(targetUserId)
            if (result.isSuccess) {
                val newStatus = if (currentStatus == CampusConnectionStatus.MUTUAL) CampusConnectionStatus.INSPIRES_YOU else CampusConnectionStatus.NONE
                Result.success(newStatus)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Failed to un-inspire"))
            }
        } else {
            // Inspire (Follow)
            val result = followUser(targetUserId, peerName, peerUsername, peerAvatarUrl)
            if (result.isSuccess) {
                val newStatus = if (currentStatus == CampusConnectionStatus.INSPIRES_YOU) CampusConnectionStatus.MUTUAL else CampusConnectionStatus.INSPIRED
                Result.success(newStatus)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Failed to inspire user"))
            }
        }
    }

    override suspend fun toggleSaveMessage(messageId: String, isSaved: Boolean) = withContext(Dispatchers.IO) {
        campusDao.toggleSaveMessage(messageId, isSaved)
    }

    override suspend fun startRealtimeChatListener(conversationId: String) = withContext(Dispatchers.IO) {
        stopRealtimeChatListener()
        try {
            val myUserId = getCurrentUserId() ?: return@withContext
            val parts = conversationId.removePrefix("conv_").split("_")
            val peerUserId = parts.firstOrNull { it != myUserId } ?: ""

            val channelName = "campus_chat_$conversationId"
            val channel = SupabaseClient.client.realtime.channel(channelName)
            activeRealtimeChannel = channel

            // 1. Postgres Changes: INSERT (New Incoming Messages)
            val insertFlow = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                table = "campus_messages"
                filter(FilterOperation("conversation_id", FilterOperator.EQ, conversationId))
            }

            // 2. Postgres Changes: UPDATE (Live Read Receipts & Double Blue Ticks)
            val updateFlow = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
                table = "campus_messages"
                filter(FilterOperation("conversation_id", FilterOperator.EQ, conversationId))
            }

            // 3. Ephemeral Broadcast Flow: Live Typing Indicators (WhatsApp Standard, 0% DB Load)
            val typingFlow = channel.broadcastFlow<CampusTypingPayload>(event = "typing")

            channel.subscribe(blockUntilSubscribed = false)

            // Collector 1: Incoming Message Insertion (Decrypted once in 0ms)
            realtimeSubscriptionJob = repositoryScope.launch {
                launch {
                    try {
                        insertFlow.collect { action ->
                            try {
                                val record = action.record
                                val msgId = record["id"]?.jsonPrimitive?.content ?: return@collect
                                val senderId = record["sender_id"]?.jsonPrimitive?.content ?: ""
                                val recipientId = record["recipient_id"]?.jsonPrimitive?.content ?: ""
                                val encryptedPayload = record["encrypted_payload"]?.jsonPrimitive?.content ?: ""
                                val mediaUrl = record["media_url"]?.jsonPrimitive?.contentOrNull
                                val mediaType = record["media_type"]?.jsonPrimitive?.content ?: "TEXT"
                                val status = record["status"]?.jsonPrimitive?.content ?: "SENT"
                                val isSaved = record["is_saved"]?.jsonPrimitive?.booleanOrNull ?: false
                                val createdAt = record["created_at"]?.jsonPrimitive?.content ?: nowIso()

                                if (senderId == myUserId) {
                                    // Outgoing echo from server: update status without creating duplicate
                                    campusDao.updateMessageStatus(msgId, "SENT", "SYNCED")
                                    return@collect
                                }

                                // Decrypt ONCE at receive time
                                val decryptedText = CampusCryptoEngine.decrypt(encryptedPayload, conversationId)

                                val entity = CampusMessageEntity(
                                    id = msgId,
                                    conversationId = conversationId,
                                    senderId = senderId,
                                    recipientId = recipientId,
                                    text = decryptedText,
                                    encryptedPayload = encryptedPayload,
                                    mediaUrl = mediaUrl,
                                    mediaLocalPath = null,
                                    mediaType = mediaType,
                                    status = "DELIVERED",
                                    isSaved = isSaved,
                                    expiresAt = null,
                                    syncStatus = "SYNCED",
                                    isDeleted = false,
                                    createdAt = createdAt
                                )

                                val displayTime = CampusDateTimeUtils.formatDisplayTime(createdAt)
                                // Atomic Room write (Message + Connection denormalization)
                                campusDao.recordIncomingMessageAtomic(
                                    message = entity,
                                    currentUserId = myUserId,
                                    peerUserId = senderId,
                                    decryptedText = decryptedText,
                                    displayTime = displayTime,
                                    isoTimestamp = createdAt
                                )
                                Log.d("CampusRepo", "Realtime message received, decrypted & saved: $msgId")
                            } catch (e: Exception) {
                                Log.e("CampusRepo", "Error decoding realtime msg payload: ${e.message}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.w("CampusRepo", "Realtime insert flow stopped: ${e.message}")
                    }
                }

                // Collector 2: Live Message Delivery & Read Receipts (Double Blue Ticks)
                launch {
                    try {
                        updateFlow.collect { action ->
                            try {
                                val record = action.record
                                val msgId = record["id"]?.jsonPrimitive?.content ?: return@collect
                                val status = record["status"]?.jsonPrimitive?.content ?: "SENT"
                                val deliveredAt = record["delivered_at"]?.jsonPrimitive?.contentOrNull
                                val readAt = record["read_at"]?.jsonPrimitive?.contentOrNull

                                campusDao.updateMessageDeliveryStatus(
                                    messageId = msgId,
                                    status = status,
                                    deliveredAt = deliveredAt,
                                    readAt = readAt
                                )
                                campusDao.updateConnectionLastMessageStatus(myUserId, peerUserId, status)
                                Log.d("CampusRepo", "Realtime message status updated: $msgId -> $status")
                            } catch (e: Exception) {
                                Log.e("CampusRepo", "Error processing message update: ${e.message}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.w("CampusRepo", "Realtime update flow stopped: ${e.message}")
                    }
                }

                // Collector 3: Live Peer Typing Broadcast with 3s Debounce Auto-Reset
                launch {
                    var typingResetJob: Job? = null
                    try {
                        typingFlow.collect { payload ->
                            if (payload.userId == peerUserId) {
                                typingResetJob?.cancel()
                                _peerPresence.value = PeerPresence(
                                    isOnline = true,
                                    isTyping = payload.isTyping
                                )
                                if (payload.isTyping) {
                                    typingResetJob = launch {
                                        kotlinx.coroutines.delay(3000)
                                        _peerPresence.value = PeerPresence(
                                            isOnline = true,
                                            isTyping = false
                                        )
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.w("CampusRepo", "Realtime typing broadcast flow stopped: ${e.message}")
                    }
                }
            }
            Log.d("CampusRepo", "Realtime channel subscribed for conversation: $conversationId")
        } catch (e: Exception) {
            Log.w("CampusRepo", "Failed to start realtime chat channel: ${e.message}")
        }
        Unit
    }

    override suspend fun stopRealtimeChatListener(): Unit = withContext(Dispatchers.IO) {
        try {
            _peerPresence.value = PeerPresence(isOnline = false, isTyping = false)
            realtimeSubscriptionJob?.cancel()
            realtimeSubscriptionJob = null
            activeRealtimeChannel?.let {
                SupabaseClient.client.realtime.removeChannel(it)
            }
            activeRealtimeChannel = null
            Log.d("CampusRepo", "Realtime chat channel cleanly unsubscribed and removed.")
        } catch (e: Exception) {
            Log.w("CampusRepo", "Error stopping realtime channel: ${e.message}")
        }
        Unit
    }

    override suspend fun clearAllLocalData() = withContext(Dispatchers.IO) {
        stopRealtimeChatListener()
        campusDao.clearAllConnections()
        campusDao.clearAllMessages()
    }

    private suspend fun processPendingOutboxMessages(myUserId: String) {
        val pending = campusDao.getPendingOutboxMessages(myUserId)
        for (msg in pending) {
            val payloadToSend = msg.encryptedPayload ?: CampusCryptoEngine.encrypt(msg.text, msg.conversationId)
            val response = remoteDataSource.sendMessage(
                conversationId = msg.conversationId,
                recipientId = msg.recipientId,
                encryptedPayload = payloadToSend,
                mediaUrl = msg.mediaUrl,
                mediaType = msg.mediaType
            )
            if (response != null && response.success) {
                campusDao.updateMessageStatus(msg.id, "SENT", "SYNCED")
            } else if (response?.error == "BLOCKED_BY_RECIPIENT") {
                campusDao.updateMessageStatus(msg.id, "FAILED", "FAILED")
            }
        }
    }

    private fun nowIso(): String {
        return CampusDateTimeUtils.nowIso()
    }

    private fun formatDisplayTime(dateString: String): String {
        return CampusDateTimeUtils.formatDisplayTime(dateString)
    }
}
