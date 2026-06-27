package com.vidyasetuai.feature_campus.data.mapper

import com.vidyasetuai.feature_campus.data.local.entity.MessageEntity
import com.vidyasetuai.feature_campus.data.local.entity.ModerationSettingsEntity
import com.vidyasetuai.feature_campus.data.local.entity.RoomEntity
import com.vidyasetuai.feature_campus.data.remote.dto.MessageDto
import com.vidyasetuai.feature_campus.data.remote.dto.ModerationSettingsDto
import com.vidyasetuai.feature_campus.data.remote.dto.RoomDto
import com.vidyasetuai.feature_campus.domain.model.CampusMessage
import com.vidyasetuai.feature_campus.domain.model.CampusRoom
import com.vidyasetuai.feature_campus.domain.model.ModerationSettings

object CampusMapper {

    fun RoomDto.toEntity(): RoomEntity = RoomEntity(
        id = id,
        name = name,
        category = category,
        description = description,
        messageCooldownSeconds = messageCooldownSeconds,
        isActive = isActive,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    fun RoomEntity.toDomain(): CampusRoom = CampusRoom(
        id = id,
        name = name,
        category = category,
        description = description,
        messageCooldownSeconds = messageCooldownSeconds,
        isActive = isActive,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    fun MessageDto.toEntity(isSynced: Boolean = true): MessageEntity = MessageEntity(
        id = id,
        roomId = roomId,
        userId = userId,
        content = content,
        isHidden = isHidden,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = isSynced,
        isFailed = false
    )

    fun MessageEntity.toDomain(senderUsername: String? = null): CampusMessage = CampusMessage(
        id = id,
        roomId = roomId,
        userId = userId,
        content = content,
        isHidden = isHidden,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = isSynced,
        isFailed = isFailed,
        senderUsername = senderUsername
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