package com.vidyasetuai.feature_campus.domain.model

/**
 * Domain model representing a single 1-on-1 E2EE Chat Message.
 */
data class CampusMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val recipientId: String,
    val text: String,
    val mediaUrl: String? = null,
    val mediaLocalPath: String? = null,
    val mediaType: String = "TEXT", // 'TEXT', 'IMAGE', 'VOICE', 'FILE'
    val status: String = "SENT", // 'PENDING', 'SENT', 'DELIVERED', 'READ', 'FAILED'
    val isSaved: Boolean = false,
    val isOutgoing: Boolean = false,
    val createdAt: String,
    val createdAtFormatted: String,
    val deliveredAt: String? = null,
    val readAt: String? = null
)
