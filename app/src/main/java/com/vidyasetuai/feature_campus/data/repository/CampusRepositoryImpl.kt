package com.vidyasetuai.feature_campus.data.repository

import com.vidyasetuai.feature_campus.data.local.dao.CampusDao
import com.vidyasetuai.feature_campus.data.local.entity.MessageEntity
import com.vidyasetuai.feature_campus.data.local.entity.RoomEntity
import com.vidyasetuai.feature_campus.data.mapper.CampusMapper.toDomain
import com.vidyasetuai.feature_campus.data.mapper.CampusMapper.toEntity
import com.vidyasetuai.feature_campus.data.remote.datasource.CampusRemoteDataSource
import com.vidyasetuai.feature_campus.domain.model.CampusMessage
import com.vidyasetuai.feature_campus.domain.model.CampusRoom
import com.vidyasetuai.feature_campus.domain.model.ModerationSettings
import com.vidyasetuai.feature_campus.domain.repository.CampusRepository
import com.vidyasetuai.feature_profile.data.local.dao.UserProfileDao
import com.vidyasetuai.feature_profile.data.remote.datasource.ProfileRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

class CampusRepositoryImpl(
    private val campusDao: CampusDao,
    private val remoteDataSource: CampusRemoteDataSource,
    private val userProfileDao: UserProfileDao,
    private val profileRemoteDS: ProfileRemoteDataSource
) : CampusRepository {

    private val blockedKeywords = CopyOnWriteArraySet<String>()
    private val usernameCache = ConcurrentHashMap<String, String>()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val tickerFlow = flow {
        while (true) {
            emit(Unit)
            delay(15000L) // Emit every 15 seconds to keep messages updated in real-time
        }
    }

    private fun parseCreatedAt(createdAt: String): Instant {
        if (createdAt.isEmpty()) return Instant.now()
        val trimmed = createdAt.trim()
        
        // 1. Try ISO-8601 parsing directly (Instant.parse)
        try {
            return Instant.parse(trimmed)
        } catch (e: Exception) {
            // Ignore
        }
        
        // 2. Try normalized ISO-8601 parsing (space replaced by 'T')
        val normalized = trimmed.replace(' ', 'T')
        try {
            return Instant.parse(normalized)
        } catch (e: Exception) {
            // Ignore
        }

        // 3. Try parsing with timezone offset normalization (e.g. +00 to +00:00)
        try {
            if (normalized.matches(Regex(".*[+-]\\d{2}"))) {
                return Instant.parse(normalized + ":00")
            }
        } catch (e: Exception) {
            // Ignore
        }

        // 4. Try parsing as LocalDateTime (assuming UTC if no timezone is provided)
        try {
            val dtString = if (normalized.contains('.')) normalized.substringBefore('.') else normalized
            val localDateTime = java.time.LocalDateTime.parse(dtString)
            return localDateTime.toInstant(java.time.ZoneOffset.UTC)
        } catch (e: Exception) {
            // Ignore
        }

        // 5. Try OffsetDateTime parsing
        try {
            return java.time.OffsetDateTime.parse(normalized).toInstant()
        } catch (e: Exception) {
            // Ignore
        }

        return Instant.now()
    }

    override fun getRooms(): Flow<List<CampusRoom>> {
        return campusDao.getRoomsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncRooms(): Result<Unit> {
        return runCatching {
            val remoteRooms = remoteDataSource.getRooms()
            campusDao.insertRooms(remoteRooms.map { it.toEntity() })
        }
    }

    override fun getMessages(roomId: String): Flow<List<CampusMessage>> {
        return combine(
            campusDao.getMessagesFlow(roomId),
            tickerFlow
        ) { entities, _ ->
            val cutoff = Instant.now().minus(15, ChronoUnit.MINUTES)
            val filtered = entities.filter {
                try {
                    val msgTime = parseCreatedAt(it.createdAt)
                    msgTime.isAfter(cutoff)
                } catch (e: Exception) {
                    true
                }
            }

            filtered.map { entity ->
                val cached = usernameCache[entity.userId]
                if (cached == null) {
                    usernameCache[entity.userId] = "User"
                    repositoryScope.launch {
                        fetchAndCacheUsername(entity.userId)
                    }
                }
                entity.toDomain(usernameCache[entity.userId] ?: "User")
            }
        }
    }

    override suspend fun syncMessages(roomId: String): Result<Unit> {
        return runCatching {
            // 1. Sync messages from server
            val remoteMessages = remoteDataSource.getMessages(roomId)
            campusDao.insertMessages(remoteMessages.map { it.toEntity(isSynced = true) })

            // 2. Local cleanup of messages older than 15 minutes
            val expiryTime = Instant.now().minus(15, ChronoUnit.MINUTES).toString()
            campusDao.deleteMessagesOlderThan(expiryTime)

            // 3. Try to sync any unsynced offline messages
            syncUnsyncedMessages()
        }
    }

    private suspend fun syncUnsyncedMessages() {
        val unsynced = campusDao.getUnsyncedMessages()
        val cutoff = Instant.now().minus(15, ChronoUnit.MINUTES)
        for (msg in unsynced) {
            val msgTime = try {
                Instant.parse(msg.createdAt)
            } catch (e: Exception) {
                Instant.now()
            }
            if (msgTime.isBefore(cutoff)) {
                // Expired: delete from local DB
                campusDao.deleteMessageById(msg.id)
                continue
            }
            try {
                val response = remoteDataSource.sendMessage(msg.roomId, msg.userId, msg.content)
                campusDao.deleteMessageById(msg.id)
                campusDao.insertMessage(response.toEntity(isSynced = true))
            } catch (e: Exception) {
                val errorMsg = e.message ?: ""
                if (errorMsg.contains("Spam Protection")) {
                    campusDao.insertMessage(msg.copy(isFailed = true))
                }
            }
        }
    }

    override suspend fun sendMessage(
        roomId: String,
        userId: String,
        content: String
    ): Result<CampusMessage> {
        if (!isContentAppropriate(content)) {
            return Result.failure(Exception("Appropriate language violation: Blocked keywords detected."))
        }

        val tempId = java.util.UUID.randomUUID().toString()
        val timestamp = Instant.now().toString()
        val tempEntity = MessageEntity(
            id = tempId,
            roomId = roomId,
            userId = userId,
            content = content,
            isHidden = false,
            isDeleted = false,
            createdAt = timestamp,
            updatedAt = timestamp,
            isSynced = false,
            isFailed = false
        )
        campusDao.insertMessage(tempEntity)

        return try {
            val remoteMessage = remoteDataSource.sendMessage(roomId, userId, content)
            campusDao.deleteMessageById(tempId)
            val syncedEntity = remoteMessage.toEntity(isSynced = true)
            campusDao.insertMessage(syncedEntity)
            Result.success(syncedEntity.toDomain(usernameCache[userId] ?: "User"))
        } catch (e: Exception) {
            val errorMsg = e.message ?: ""
            if (errorMsg.contains("Spam Protection")) {
                campusDao.insertMessage(tempEntity.copy(isFailed = true))
                Result.failure(e)
            } else {
                // Network failure: keep local message unsynced so syncMessages can sync it later
                Result.success(tempEntity.toDomain(usernameCache[userId] ?: "User"))
            }
        }
    }

    override suspend fun loadModerationSettings(): Result<ModerationSettings> {
        return runCatching {
            // First load from local DB if available
            val localSettings = campusDao.getModerationSettings()
            if (localSettings != null) {
                blockedKeywords.clear()
                blockedKeywords.addAll(localSettings.blockedKeywords)
            }

            // Fetch from remote and update cache
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
    ): Result<Unit> {
        return runCatching {
            remoteDataSource.reportMessage(messageId, reporterUserId, reason)
        }
    }

    override fun observeRealtimeMessages(roomId: String): Flow<CampusMessage> {
        return remoteDataSource.subscribeToMessages(roomId).map { dto ->
            val entity = dto.toEntity(isSynced = true)
            campusDao.insertMessage(entity)
            entity.toDomain(usernameCache[dto.userId] ?: "User")
        }
    }

    override fun observePresenceCount(roomId: String, userId: String): Flow<Int> {
        return remoteDataSource.subscribeToPresenceCount(roomId, userId)
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

        // 1. Check DB blocked keywords
        val isBlockedInDb = blockedKeywords.any { checkMatch(it) }
        if (isBlockedInDb) return false

        // 2. Check Hindi/Indian fallback list for extra safety
        val isBlockedInFallback = HINDI_ABUSE_FALLBACK.any { checkMatch(it) }
        return !isBlockedInFallback
    }

    companion object {
        private val HINDI_ABUSE_FALLBACK = listOf(
            // Hinglish / English slurs
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
            // Devanagari Hindi slurs
            "चूत", "चूतिया", "गांड", "गांडू", "लौड़ा", "लौड़े", "लोड़ा", "लोडू", "मादरचोद", 
            "बहनचोद", "भोसड़ीके", "भोसड़ी", "भोसड़ा", "रंडी", "भड़वा", "साला", "साले", "साली", 
            "हरामी", "कमीना", "कमीने", "कुत्ता", "कुत्ते", "कुतिया", "हरामजादा", "हरामजादे", 
            "चोदू", "चोदना", "चुदवाना", "गांडू", "गांडमस्ती", "गांडफाड़", "गांडमरा", "गांडमराओ"
        )
    }

    private suspend fun fetchAndCacheUsername(userId: String) {
        val localProfile = userProfileDao.getProfile(userId)
        if (localProfile != null) {
            usernameCache[userId] = localProfile.username ?: localProfile.fullName ?: "User"
            return
        }
        try {
            val remoteProfile = profileRemoteDS.getProfile(userId)
            val name = remoteProfile.username ?: remoteProfile.full_name ?: "User"
            usernameCache[userId] = name
            userProfileDao.insertProfile(
                com.vidyasetuai.feature_profile.data.local.entity.UserProfileEntity(
                    userId = remoteProfile.user_id,
                    username = remoteProfile.username,
                    firstName = remoteProfile.first_name,
                    lastName = remoteProfile.last_name,
                    fullName = remoteProfile.full_name,
                    profilePictureUrl = remoteProfile.profile_picture_url,
                    coverPhotoUrl = remoteProfile.cover_photo_url,
                    bio = remoteProfile.bio,
                    preferredLanguage = remoteProfile.preferred_language,
                    isVerified = remoteProfile.is_verified,
                    gender = remoteProfile.gender,
                    dateOfBirth = remoteProfile.date_of_birth
                )
            )
        } catch (e: Exception) {
            // Keep "User"
        }
    }
}