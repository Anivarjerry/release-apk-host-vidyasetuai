package com.vidyasetuai.feature_campus.data.mapper

import com.vidyasetuai.feature_campus.data.local.entity.ModerationSettingsEntity
import com.vidyasetuai.feature_campus.data.local.entity.PrivateMessageEntity
import com.vidyasetuai.feature_campus.data.local.entity.PrivateRoomEntity
import com.vidyasetuai.feature_campus.data.remote.dto.ModerationSettingsDto
import com.vidyasetuai.feature_campus.domain.model.MessageSyncStatus
import com.vidyasetuai.feature_campus.domain.model.ModerationSettings
import com.vidyasetuai.feature_campus.domain.model.PrivateMessage
import com.vidyasetuai.feature_campus.domain.model.PrivateMessageDto
import com.vidyasetuai.feature_campus.domain.model.PrivateRoom
import com.vidyasetuai.feature_campus.domain.model.PrivateRoomDto

object CampusMapper {

    fun PrivateRoomDto.toEntity(): PrivateRoomEntity = PrivateRoomEntity(
        id = id,
        user1Id = user1Id,
        user2Id = user2Id,
        createdAt = createdAt
    )

    fun PrivateRoomEntity.toDomain(): PrivateRoom = PrivateRoom(
        id = id,
        user1Id = user1Id,
        user2Id = user2Id,
        createdAt = createdAt
    )

    fun PrivateMessageDto.toEntity(syncStatus: String = "SENT"): PrivateMessageEntity = PrivateMessageEntity(
        localId = id,
        serverId = id,
        roomId = roomId,
        senderId = senderId,
        messageText = messageText,
        mediaUrl = mediaUrl,
        isSaved = isSaved,
        syncStatus = syncStatus,
        createdAt = createdAt,
        updatedAt = createdAt
    )

    fun PrivateMessageEntity.toDomain(): PrivateMessage = PrivateMessage(
        id = localId,
        localId = localId,
        serverId = serverId,
        roomId = roomId,
        senderId = senderId,
        messageText = messageText,
        mediaUrl = mediaUrl,
        mediaLocalPath = mediaLocalPath,
        isSaved = isSaved,
        createdAt = createdAt,
        syncStatus = when (syncStatus) {
            "PENDING" -> MessageSyncStatus.PENDING
            "SENT" -> MessageSyncStatus.SENT
            "DELIVERED" -> MessageSyncStatus.DELIVERED
            "READ" -> MessageSyncStatus.READ
            "FAILED" -> MessageSyncStatus.FAILED
            else -> MessageSyncStatus.SENT
        }
    )

    fun ModerationSettingsDto.toEntity(): ModerationSettingsEntity = ModerationSettingsEntity(
        id = id,
        blockedKeywords = blockedKeywords,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    fun ModerationSettingsEntity.toDomain(): ModerationSettings = ModerationSettings(
        id = id,
        blockedKeywords = blockedKeywords,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}