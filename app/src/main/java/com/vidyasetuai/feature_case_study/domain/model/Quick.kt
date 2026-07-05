package com.vidyasetuai.feature_case_study.domain.model

data class Quick(
    val id: String,
    val authorUserId: String,
    val title: String,
    val description: String,
    val coverImageUrl: String?,
    val viewsCount: Int,
    val helpfulCount: Int,
    val status: String,
    val createdAt: String,
    val expiresAt: String,
    val authorName: String,
    val authorUsername: String,
    val authorProfilePicUrl: String?,
    val isAuthorVerified: Boolean,
    val isHelpful: Boolean = false
)
