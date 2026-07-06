package com.vidyasetuai.feature_campus.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrivateRoomDto(
    val id: String,
    @SerialName("user1_id") val user1Id: String,
    @SerialName("user2_id") val user2Id: String,
    @SerialName("created_at") val createdAt: String
)

data class PrivateRoom(
    val id: String,
    val user1Id: String,
    val user2Id: String,
    val createdAt: String
)

@Serializable
data class PrivateMessageDto(
    val id: String,
    @SerialName("room_id") val roomId: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("message_text") val messageText: String? = null,
    @SerialName("media_url") val mediaUrl: String? = null,
    @SerialName("is_saved") val isSaved: Boolean = false,
    @SerialName("created_at") val createdAt: String
)

data class PrivateMessage(
    val id: String,
    val roomId: String,
    val senderId: String,
    val messageText: String?,
    val mediaUrl: String?,
    val isSaved: Boolean,
    val createdAt: String,
    val isSynced: Boolean = true,
    val isFailed: Boolean = false
)

fun PrivateRoomDto.toDomain() = PrivateRoom(
    id = id,
    user1Id = user1Id,
    user2Id = user2Id,
    createdAt = createdAt
)

fun PrivateMessageDto.toDomain() = PrivateMessage(
    id = id,
    roomId = roomId,
    senderId = senderId,
    messageText = messageText,
    mediaUrl = mediaUrl,
    isSaved = isSaved,
    createdAt = createdAt
)
