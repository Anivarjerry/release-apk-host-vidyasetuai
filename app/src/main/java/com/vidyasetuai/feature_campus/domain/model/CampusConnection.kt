package com.vidyasetuai.feature_campus.domain.model

/**
 * Domain model representing a Campus connection / friend chat summary.
 */
data class CampusConnection(
    val id: String,
    val userId: String,
    val targetUserId: String,
    val status: String, // 'FOLLOWING', 'BLOCKED'
    val isMutual: Boolean,
    val peerName: String,
    val peerUsername: String,
    val peerAvatarUrl: String?,
    val peerAvatarLocalPath: String?,
    val peerBio: String?,
    val unreadCount: Int = 0,
    val lastMessageText: String? = null,
    val lastMessageTime: String? = null,
    val lastMessageStatus: String? = null // 'PENDING', 'SENT', 'DELIVERED', 'READ', 'FAILED'
)
