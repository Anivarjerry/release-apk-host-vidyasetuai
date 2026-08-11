package com.vidyasetuai.feature_campus.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrivateRoomDto(
    val id: String,
    @SerialName("user1_id") val user1Id: String,
    @SerialName("user2_id") val user2Id: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("last_message_text") val lastMessageText: String? = null,
    @SerialName("last_message_time") val lastMessageTime: String? = null,
    @SerialName("unread_count") val unreadCount: Int = 0
)

data class PrivateRoom(
    val id: String,
    val user1Id: String,
    val user2Id: String,
    val createdAt: String,
    val lastMessageText: String? = null,
    val lastMessageTime: String? = null,
    val unreadCount: Int = 0
)

@Serializable
data class PrivateMessageDto(
    val id: String,
    @SerialName("room_id") val roomId: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("message_text") val messageText: String? = null,
    @SerialName("media_url") val mediaUrl: String? = null,
    @SerialName("is_saved") val isSaved: Boolean = false,
    @SerialName("created_at") val createdAt: String,
    @SerialName("status") val status: String? = "sent"
)

enum class MessageSyncStatus {
    PENDING,   // Offline / Sending (🕒)
    SENT,      // Server received (✔️)
    DELIVERED, // Recipient received (✔️✔️)
    READ,      // Recipient read (🟢✔️✔️)
    FAILED     // Moderation error / failed (❌)
}

data class PrivateMessage(
    val id: String, // localId or serverId
    val localId: String = id,
    val serverId: String? = null,
    val roomId: String,
    val senderId: String,
    val messageText: String?,
    val mediaUrl: String?,
    val mediaLocalPath: String? = null,
    val isSaved: Boolean,
    val createdAt: String,
    val syncStatus: MessageSyncStatus = MessageSyncStatus.SENT
)

fun PrivateRoomDto.toDomain() = PrivateRoom(
    id = id,
    user1Id = user1Id,
    user2Id = user2Id,
    createdAt = createdAt,
    lastMessageText = lastMessageText,
    lastMessageTime = lastMessageTime,
    unreadCount = unreadCount
)

fun PrivateMessageDto.toDomain() = PrivateMessage(
    id = id,
    localId = id,
    serverId = id,
    roomId = roomId,
    senderId = senderId,
    messageText = messageText,
    mediaUrl = mediaUrl,
    isSaved = isSaved,
    createdAt = createdAt,
    syncStatus = when (status?.lowercase()) {
        "delivered" -> MessageSyncStatus.DELIVERED
        "read" -> MessageSyncStatus.READ
        "pending" -> MessageSyncStatus.PENDING
        "failed" -> MessageSyncStatus.FAILED
        else -> MessageSyncStatus.SENT
    }
)
