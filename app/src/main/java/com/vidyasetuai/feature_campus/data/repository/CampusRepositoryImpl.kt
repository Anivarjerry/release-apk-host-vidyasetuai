package com.vidyasetuai.feature_campus.data.repository

import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.core.security.CryptoManager
import com.vidyasetuai.feature_campus.data.local.dao.CampusDao
import com.vidyasetuai.feature_campus.data.local.entity.PrivateMessageEntity
import com.vidyasetuai.feature_campus.data.local.entity.PrivateRoomEntity
import com.vidyasetuai.feature_campus.data.mapper.CampusMapper.toDomain
import com.vidyasetuai.feature_campus.data.mapper.CampusMapper.toEntity
import com.vidyasetuai.feature_campus.data.remote.datasource.CampusRemoteDataSource
import com.vidyasetuai.feature_campus.domain.model.MessageSyncStatus
import com.vidyasetuai.feature_campus.domain.model.ModerationSettings
import com.vidyasetuai.feature_campus.domain.model.PrivateMessage
import com.vidyasetuai.feature_campus.domain.model.PrivateRoom
import com.vidyasetuai.feature_campus.domain.repository.CampusRepository
import com.vidyasetuai.feature_profile.data.local.dao.UserProfileDao
import com.vidyasetuai.feature_profile.data.remote.datasource.ProfileRemoteDataSource
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

class CampusRepositoryImpl(
    private val campusDao: CampusDao,
    private val remoteDataSource: CampusRemoteDataSource,
    private val userProfileDao: UserProfileDao,
    private val profileRemoteDS: ProfileRemoteDataSource,
    private val context: android.content.Context? = null
) : CampusRepository {

    private val blockedKeywords = CopyOnWriteArraySet<String>()
    private val peerPublicKeyCache = ConcurrentHashMap<String, String>()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    override suspend fun initializeUserKeys(userId: String): Result<Unit> = runCatching {
        CryptoManager.ensureKeyPairExists()
        val pubKeyBase64 = CryptoManager.getMyPublicKeyBase64()
        if (!pubKeyBase64.isNullOrEmpty()) {
            try {
                SupabaseClient.client.postgrest["user_profiles"].update(
                    mapOf("public_key" to pubKeyBase64)
                ) {
                    filter {
                        eq("user_id", userId)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("CampusRepo", "Failed to upload public key to server", e)
            }
        }
    }

    override fun getPrivateRoomsFlow(): Flow<List<PrivateRoom>> {
        return campusDao.getPrivateRoomsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun parseCreatedAt(dateStr: String): java.time.Instant {
        return try {
            java.time.Instant.parse(dateStr)
        } catch (e: Exception) {
            try {
                val zdt = java.time.ZonedDateTime.parse(dateStr)
                zdt.toInstant()
            } catch (ex: Exception) {
                java.time.Instant.EPOCH
            }
        }
    }

    override fun getPrivateMessagesFlow(roomId: String): Flow<List<PrivateMessage>> {
        return campusDao.getPrivateMessagesFlow(roomId).map { entities ->
            val cutoff = java.time.Instant.now().minus(24, java.time.temporal.ChronoUnit.HOURS)
            val distinctEntities = entities
                .filter { entity ->
                    if (entity.isSaved) return@filter true
                    try {
                        val msgTime = parseCreatedAt(entity.createdAt)
                        msgTime.isAfter(cutoff)
                    } catch (e: Exception) {
                        true
                    }
                }
                .distinctBy { if (!it.serverId.isNullOrEmpty()) it.serverId else it.localId }

            distinctEntities.map { it.toDomain() }
        }
    }

    override suspend fun syncPrivateRooms(): Result<Unit> = runCatching {
        // Fetch or update rooms from remote if needed
    }

    override suspend fun syncPrivateMessages(roomId: String, peerUserId: String?): Result<Unit> = runCatching {
        val cutoffIso = java.time.Instant.now().minus(24, java.time.temporal.ChronoUnit.HOURS).toString()
        campusDao.deleteExpiredUnsavedPrivateMessages(cutoffIso)

        val remoteDtos = remoteDataSource.getPrivateMessages(roomId)
        val peerPubKey = if (!peerUserId.isNullOrEmpty()) fetchPeerPublicKey(peerUserId) else null

        val entities = remoteDtos.map { dto ->
            val plainText = if (dto.messageText != null && peerPubKey != null) {
                CryptoManager.decryptMessage(dto.messageText, peerPubKey)
            } else {
                dto.messageText
            }

            val existingLocal = campusDao.getPrivateMessageByServerId(dto.id)
            val targetLocalId = existingLocal?.localId ?: dto.id

            PrivateMessageEntity(
                localId = targetLocalId,
                serverId = dto.id,
                roomId = dto.roomId,
                senderId = dto.senderId,
                messageText = plainText,
                mediaUrl = dto.mediaUrl,
                isSaved = dto.isSaved,
                syncStatus = when (dto.status?.lowercase()) {
                    "delivered" -> "DELIVERED"
                    "read" -> "READ"
                    "pending" -> "PENDING"
                    "failed" -> "FAILED"
                    else -> "SENT"
                },
                createdAt = dto.createdAt,
                updatedAt = dto.createdAt
            )
        }

        campusDao.insertPrivateMessages(entities)
        syncUnsyncedPrivateMessages()
    }

    override suspend fun syncUnsyncedPrivateMessages(): Result<Unit> = runCatching {
        val unsynced = campusDao.getUnsyncedPrivateMessages()
        for (msg in unsynced) {
            try {
                val room = campusDao.getPrivateRoomById(msg.roomId)
                val peerId = if (room != null) {
                    if (room.user1Id == msg.senderId) room.user2Id else room.user1Id
                } else null

                val peerPubKey = if (peerId != null) fetchPeerPublicKey(peerId) else null
                val encryptedText = if (msg.messageText != null && peerPubKey != null) {
                    CryptoManager.encryptMessage(msg.messageText, peerPubKey)
                } else {
                    msg.messageText
                }

                val response = remoteDataSource.sendPrivateMessage(
                    roomId = msg.roomId,
                    senderId = msg.senderId,
                    text = encryptedText,
                    mediaUrl = msg.mediaUrl
                )

                campusDao.updatePrivateMessageStatus(
                    localId = msg.localId,
                    serverId = response.id,
                    status = "SENT"
                )
            } catch (e: Exception) {
                android.util.Log.e("CampusRepo", "Failed to sync pending message ${msg.localId}", e)
            }
        }
    }

    override suspend fun getOrCreatePrivateRoom(userA: String, userB: String): Result<PrivateRoom> = runCatching {
        val localRoom = campusDao.getPrivateRoomForUsers(userA, userB)
        if (localRoom != null) {
            val isMutual = checkIsMutualConnection(userA, userB)
            if (isMutual) {
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    runCatching {
                        val dto = remoteDataSource.getOrCreatePrivateRoom(userA, userB)
                        campusDao.insertPrivateRoom(dto.toEntity())
                    }
                }
            }
            return@runCatching localRoom.toDomain()
        }

        val isMutual = checkIsMutualConnection(userA, userB)
        if (!isMutual) {
            val userListSorted = listOf(userA, userB).sorted()
            val tempRoomId = "temp_${userListSorted[0]}_${userListSorted[1]}"
            val tempEntity = com.vidyasetuai.feature_campus.data.local.entity.PrivateRoomEntity(
                id = tempRoomId,
                user1Id = userListSorted[0],
                user2Id = userListSorted[1],
                createdAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault()).apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }.format(java.util.Date()),
                lastMessageText = null,
                lastMessageTime = null,
                unreadCount = 0
            )
            campusDao.insertPrivateRoom(tempEntity)
            return@runCatching tempEntity.toDomain()
        }

        val dto = remoteDataSource.getOrCreatePrivateRoom(userA, userB)
        val entity = dto.toEntity()
        campusDao.insertPrivateRoom(entity)
        entity.toDomain()
    }

    override suspend fun sendPrivateMessage(
        roomId: String,
        senderId: String,
        text: String?,
        mediaUrl: String?,
        peerUserId: String?
    ): Result<PrivateMessage> {
        if (text != null && !isContentAppropriate(text)) {
            return Result.failure(Exception("Appropriate language violation: Blocked keywords detected."))
        }

        val localId = UUID.randomUUID().toString()
        val timestamp = Instant.now().toString()

        // 1. Instant local Room DB insertion (Optimistic UI Render - 0 ms)
        val tempEntity = PrivateMessageEntity(
            localId = localId,
            serverId = null,
            roomId = roomId,
            senderId = senderId,
            messageText = text,
            mediaUrl = mediaUrl,
            mediaLocalPath = null,
            isSaved = false,
            syncStatus = "PENDING",
            createdAt = timestamp,
            updatedAt = timestamp
        )
        campusDao.insertPrivateMessage(tempEntity)
        campusDao.updateRoomLastMessage(roomId, text ?: "📷 Photo", timestamp)

        // 2. Network transmission (with E2EE payload encryption if peer key is present)
        return try {
            val peerPubKey = if (peerUserId != null) fetchPeerPublicKey(peerUserId) else null
            val encryptedText = if (text != null && peerPubKey != null) {
                CryptoManager.encryptMessage(text, peerPubKey)
            } else {
                text
            }

            val remoteDto = remoteDataSource.sendPrivateMessage(roomId, senderId, encryptedText, mediaUrl)
            
            // Update local entity with serverId and status = SENT
            campusDao.updatePrivateMessageStatus(
                localId = localId,
                serverId = remoteDto.id,
                status = "SENT"
            )

            Result.success(tempEntity.copy(serverId = remoteDto.id, syncStatus = "SENT").toDomain())
        } catch (e: Exception) {
            // Network failure: keep local message in Room DB with status PENDING for background sync
            Result.success(tempEntity.toDomain())
        }
    }

    override suspend fun markRoomAsRead(roomId: String, currentUserId: String): Result<Unit> = runCatching {
        campusDao.markRoomAsRead(roomId)
        campusDao.markMessagesAsRead(roomId, currentUserId)
        try {
            remoteDataSource.markRoomMessagesAsReadRemote(roomId, currentUserId)
        } catch (e: Exception) {
            // Silent catch for background network read status update
        }
    }

    override suspend fun toggleSavePrivateMessage(
        messageId: String,
        isSaved: Boolean
    ): Result<Unit> = runCatching {
        campusDao.updatePrivateMessageSavedStatus(messageId, messageId, isSaved)
        try {
            remoteDataSource.toggleSavePrivateMessage(messageId, isSaved)
        } catch (e: Exception) {
            // Saved locally
        }
    }

    override fun observePrivateMessages(roomId: String, peerUserId: String?): Flow<PrivateMessage> {
        return remoteDataSource.subscribeToPrivateMessages(roomId).map { dto ->
            val peerPubKey = if (peerUserId != null) fetchPeerPublicKey(peerUserId) else null
            val plainText = if (dto.messageText != null && peerPubKey != null) {
                CryptoManager.decryptMessage(dto.messageText, peerPubKey)
            } else {
                dto.messageText
            }

            val existingLocal = campusDao.getPrivateMessageByServerId(dto.id)
                ?: campusDao.getPrivateMessageByLocalId(dto.id)
            val targetLocalId = existingLocal?.localId ?: dto.id
            val targetSaved = existingLocal?.isSaved ?: dto.isSaved
            val finalMessageText = if (!plainText.isNullOrEmpty()) plainText else (existingLocal?.messageText ?: "")

            val statusStr = when (dto.status?.lowercase()) {
                "delivered" -> "DELIVERED"
                "read" -> "READ"
                "pending" -> "PENDING"
                "failed" -> "FAILED"
                else -> "SENT"
            }

            val entity = PrivateMessageEntity(
                localId = targetLocalId,
                serverId = dto.id,
                roomId = dto.roomId,
                senderId = dto.senderId,
                messageText = finalMessageText,
                mediaUrl = dto.mediaUrl,
                isSaved = targetSaved,
                syncStatus = statusStr,
                createdAt = dto.createdAt,
                updatedAt = dto.createdAt
            )
            campusDao.insertPrivateMessage(entity)
            campusDao.updateRoomLastMessage(dto.roomId, finalMessageText.ifEmpty { "📷 Photo" }, dto.createdAt)

            if (context != null && com.vidyasetuai.core.notification.handler.ChatNotificationHandler.activeChatRoomId != dto.roomId) {
                val senderName = if (peerUserId != null) {
                    val profile = userProfileDao.getProfile(peerUserId)
                    profile?.fullName ?: profile?.firstName ?: "New Message"
                } else {
                    "New Message"
                }
                com.vidyasetuai.core.notification.handler.ChatNotificationHandler.showChatMessageNotification(
                    context = context,
                    senderName = senderName,
                    messageSnippet = finalMessageText.ifEmpty { "Sent a photo" },
                    roomId = dto.roomId,
                    senderUserId = dto.senderId
                )
            }

            entity.toDomain()
        }
    }

    private suspend fun fetchPeerPublicKey(peerUserId: String): String? {
        val cached = peerPublicKeyCache[peerUserId]
        if (!cached.isNullOrEmpty()) return cached

        return try {
            val profile = profileRemoteDS.getProfile(peerUserId)
            val pubKey = profile.public_key
            if (!pubKey.isNullOrEmpty()) {
                peerPublicKeyCache[peerUserId] = pubKey
            }
            pubKey
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun loadModerationSettings(): Result<ModerationSettings> {
        return runCatching {
            val localSettings = campusDao.getModerationSettings()
            if (localSettings != null) {
                blockedKeywords.clear()
                blockedKeywords.addAll(localSettings.blockedKeywords)
            }
            val remoteSettings = remoteDataSource.getModerationSettings()
            campusDao.insertModerationSettings(remoteSettings.toEntity())
            blockedKeywords.clear()
            blockedKeywords.addAll(remoteSettings.blockedKeywords)
            remoteSettings.toEntity().toDomain()
        }
    }

    override suspend fun reportMessage(
        messageId: String,
        reporterUserId: String,
        reason: String
    ): Result<Unit> = runCatching {
        remoteDataSource.reportMessage(messageId, reporterUserId, reason)
    }

    override suspend fun isContentAppropriate(content: String): Boolean {
        if (blockedKeywords.isEmpty()) {
            loadModerationSettings()
        }
        val lowerContent = content.lowercase(Locale.ROOT)
        val normalizedContent = lowerContent.filter { it.isLetterOrDigit() }

        fun checkMatch(keyword: String): Boolean {
            val kw = keyword.lowercase(Locale.ROOT)
            val kwNormalized = kw.filter { it.isLetterOrDigit() }

            if (lowerContent.contains(kw)) return true
            if (kwNormalized.isNotEmpty() && normalizedContent.contains(kwNormalized)) return true
            return false
        }

        val isBlockedInDb = blockedKeywords.any { checkMatch(it) }
        if (isBlockedInDb) return false

        val isBlockedInFallback = HINDI_ABUSE_FALLBACK.any { checkMatch(it) }
        return !isBlockedInFallback
    }

    override fun getMutualInspirationsFlow(userId: String): Flow<List<com.vidyasetuai.feature_profile.domain.model.UserProfile>> {
        return userProfileDao.getOtherProfilesFlow(userId).map { entities ->
            entities.map { entity ->
                com.vidyasetuai.feature_profile.domain.model.UserProfile(
                    userId = entity.userId,
                    email = entity.email,
                    isActive = entity.isActive,
                    isDeleted = entity.isDeleted,
                    username = entity.username,
                    firstName = entity.firstName,
                    lastName = entity.lastName,
                    fullName = entity.fullName,
                    profilePictureUrl = entity.profilePictureUrl,
                    coverPhotoUrl = entity.coverPhotoUrl,
                    bio = entity.bio,
                    preferredLanguage = entity.preferredLanguage,
                    isVerified = entity.isVerified,
                    gender = entity.gender,
                    dateOfBirth = entity.dateOfBirth ?: "",
                    totalInspiringCount = entity.totalInspiringCount,
                    totalInspiredCount = entity.totalInspiredCount
                )
            }
        }
    }

    override suspend fun checkIsMutualConnection(userA: String, userB: String): Boolean {
        if (userA.isEmpty() || userB.isEmpty()) return false
        if (userProfileDao.getProfile(userB) != null || campusDao.getPrivateRoomForUsers(userA, userB) != null) {
            return true
        }
        return try {
            val inspired = profileRemoteDS.getInspiredUsers(userA)
            val inspiring = profileRemoteDS.getInspiringUsers(userA)
            inspired.any { it.user_id == userB } && inspiring.any { it.user_id == userB }
        } catch (e: Exception) {
            userProfileDao.getProfile(userB) != null
        }
    }

    override suspend fun getMutualInspirations(userId: String): Result<List<com.vidyasetuai.feature_profile.domain.model.UserProfile>> = runCatching {
        try {
            val inspired = profileRemoteDS.getInspiredUsers(userId)
            val inspiring = profileRemoteDS.getInspiringUsers(userId)
            val mutualDtos = inspired.filter { u -> inspiring.any { it.user_id == u.user_id } }
            
            val entities = mutualDtos.map { dto ->
                com.vidyasetuai.feature_profile.data.local.entity.UserProfileEntity(
                    userId = dto.user_id,
                    email = dto.email,
                    isActive = dto.is_active,
                    isDeleted = dto.is_deleted,
                    username = dto.username,
                    firstName = dto.first_name,
                    lastName = dto.last_name,
                    fullName = dto.full_name,
                    profilePictureUrl = dto.profile_picture_url,
                    coverPhotoUrl = dto.cover_photo_url,
                    bio = dto.bio,
                    preferredLanguage = dto.preferred_language,
                    isVerified = dto.is_verified,
                    gender = dto.gender,
                    dateOfBirth = dto.date_of_birth,
                    totalInspiringCount = dto.total_inspiring_count,
                    totalInspiredCount = dto.total_inspired_count
                )
            }
            if (entities.isNotEmpty()) {
                userProfileDao.insertProfiles(entities)
            }

            entities.map { entity ->
                com.vidyasetuai.feature_profile.domain.model.UserProfile(
                    userId = entity.userId,
                    email = entity.email,
                    isActive = entity.isActive,
                    isDeleted = entity.isDeleted,
                    username = entity.username,
                    firstName = entity.firstName,
                    lastName = entity.lastName,
                    fullName = entity.fullName,
                    profilePictureUrl = entity.profilePictureUrl,
                    coverPhotoUrl = entity.coverPhotoUrl,
                    bio = entity.bio,
                    preferredLanguage = entity.preferredLanguage,
                    isVerified = entity.isVerified,
                    gender = entity.gender,
                    dateOfBirth = entity.dateOfBirth ?: "",
                    totalInspiringCount = entity.totalInspiringCount,
                    totalInspiredCount = entity.totalInspiredCount
                )
            }
        } catch (e: Exception) {
            // Offline fallback: load cached profiles from local Room DB
            val cachedEntities = userProfileDao.getOtherProfiles(userId)
            cachedEntities.map { entity ->
                com.vidyasetuai.feature_profile.domain.model.UserProfile(
                    userId = entity.userId,
                    email = entity.email,
                    isActive = entity.isActive,
                    isDeleted = entity.isDeleted,
                    username = entity.username,
                    firstName = entity.firstName,
                    lastName = entity.lastName,
                    fullName = entity.fullName,
                    profilePictureUrl = entity.profilePictureUrl,
                    coverPhotoUrl = entity.coverPhotoUrl,
                    bio = entity.bio,
                    preferredLanguage = entity.preferredLanguage,
                    isVerified = entity.isVerified,
                    gender = entity.gender,
                    dateOfBirth = entity.dateOfBirth ?: "",
                    totalInspiringCount = entity.totalInspiringCount,
                    totalInspiredCount = entity.totalInspiredCount
                )
            }
        }
    }

    companion object {
        private val HINDI_ABUSE_FALLBACK = listOf(
            "chut", "chutya", "chutiya", "gand", "gaand", "gandu", "gaandu", "lauda", "laude", 
            "lowda", "lowde", "loda", "lodu", "lode", "madarchod", "behenchod", "bhenchod", 
            "behanchod", "bhosdike", "bhosadike", "bhosda", "bhosdi", "randi", "rndi", "bhadwa", 
            "bhadwe", "harami", "saala", "sala", "choot", "choote", "ma ki chut", "maa ki chut", 
            "maki chut", "makichut", "behen ki chut", "saale", "chutye", "chutiyo", "gandi", 
            "bhadua", "bhadue", "katuya", "katua", "katuwa", "saali", "sali", "gandya", "gandia", 
            "chudail", "chudaail", "kutta", "kutte", "kutto", "kutti", "kuttiya", "harampana", 
            "haramzada", "haramzade", "haramzadi", "chutiyaap", "chutiyaapa", "chodu", "chodo", 
            "chodna", "chudna", "chudana", "gaandmaru", "gaandmasti", "gaandfaad", "gaandfaadna", 
            "lawda", "lode", "lowde", "chuchi", "chuchiya", "chuchiyan", "chuchiyo", "bitch", 
            "bastard", "fuck", "asshole", "dick", "pussy", "vagina", "boobs", "breast", "cunt", 
            "whore", "slut", "rape", "blowjob", "anal", "cum", "nude", "naked", "sex", "xxx", "porn",
            "चूत", "चूतिया", "गांड", "गांडू", "लौड़ा", "लौड़े", "लोड़ा", "लोडू", "मादरचोद", 
            "बहनचोद", "भोसड़ीके", "भोसड़ी", "भोसड़ा", "रंडी", "भड़वा", "साला", "साले", "साली", 
            "हरामी", "कमीना", "कमीने", "कुत्ता", "कुत्ते", "कुतिया", "हरामजादा", "हरामजादे", 
            "चोदू", "चोदना", "चुदवाना", "गांडू", "गांडमस्ती", "गांडफाड़", "गांडमरा", "गांडमराओ"
        )
    }
}