package com.vidyasetuai.feature_campus.domain.model

data class CampusRoom(
    val id: String,
    val name: String,
    val category: String?,
    val description: String?,
    val messageCooldownSeconds: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class CampusMessage(
    val id: String,
    val roomId: String,
    val userId: String,
    val content: String,
    val isHidden: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = true,
    val isFailed: Boolean = false,
    val senderUsername: String? = null // For UI display
)

data class ModerationSettings(
    val id: Int,
    val blockedKeywords: List<String>,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)