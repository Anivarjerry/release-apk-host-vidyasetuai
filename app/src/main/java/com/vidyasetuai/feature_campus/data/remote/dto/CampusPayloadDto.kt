package com.vidyasetuai.feature_campus.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CampusWorkspacePayloadDto(
    @SerialName("success") val success: Boolean = true,
    @SerialName("connections") val connections: List<CampusConnectionDto> = emptyList(),
    @SerialName("incoming_requests") val incomingRequests: List<CampusIncomingRequestDto> = emptyList(),
    @SerialName("messages") val messages: List<CampusMessageDto> = emptyList(),
    @SerialName("server_timestamp") val serverTimestamp: String? = null
)

@Serializable
data class CampusConnectionDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("target_user_id") val targetUserId: String,
    @SerialName("status") val status: String = "FOLLOWING",
    @SerialName("is_mutual") val isMutual: Boolean = false,
    @SerialName("peer_name") val peerName: String? = null,
    @SerialName("peer_username") val peerUsername: String? = null,
    @SerialName("peer_avatar_url") val peerAvatarUrl: String? = null,
    @SerialName("peer_bio") val peerBio: String? = null,
    @SerialName("sync_version") val syncVersion: Long = 1L,
    @SerialName("is_deleted") val isDeleted: Boolean = false,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class CampusIncomingRequestDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("target_user_id") val targetUserId: String,
    @SerialName("status") val status: String = "FOLLOWING",
    @SerialName("is_mutual") val isMutual: Boolean = false,
    @SerialName("peer_name") val peerName: String? = null,
    @SerialName("peer_username") val peerUsername: String? = null,
    @SerialName("peer_avatar_url") val peerAvatarUrl: String? = null,
    @SerialName("peer_bio") val peerBio: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class CampusMessageDto(
    @SerialName("id") val id: String,
    @SerialName("conversation_id") val conversationId: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("recipient_id") val recipientId: String,
    @SerialName("encrypted_payload") val encryptedPayload: String,
    @SerialName("media_url") val mediaUrl: String? = null,
    @SerialName("media_type") val mediaType: String = "TEXT",
    @SerialName("status") val status: String = "SENT",
    @SerialName("is_saved") val isSaved: Boolean = false,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("sync_version") val syncVersion: Long = 1L,
    @SerialName("is_deleted") val isDeleted: Boolean = false,
    @SerialName("created_at") val createdAt: String,
    @SerialName("delivered_at") val deliveredAt: String? = null,
    @SerialName("read_at") val readAt: String? = null
)

@Serializable
data class CampusSendMessageResponseDto(
    @SerialName("success") val success: Boolean = false,
    @SerialName("message_id") val messageId: String? = null,
    @SerialName("conversation_id") val conversationId: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("error") val error: String? = null,
    @SerialName("message") val message: String? = null
)

@Serializable
data class CampusUserSearchDto(
    @SerialName("id") val id: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("connection_status") val connectionStatus: String = "NONE" // "NONE", "INSPIRED", "INSPIRES_YOU", "MUTUAL"
)

@Serializable
data class CampusUserSearchResponseDto(
    @SerialName("success") val success: Boolean = true,
    @SerialName("users") val users: List<CampusUserSearchDto> = emptyList(),
    @SerialName("error") val error: String? = null
)
