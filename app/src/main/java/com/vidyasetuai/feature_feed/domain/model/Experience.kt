package com.vidyasetuai.feature_feed.domain.model

data class Experience(
    val id: String,
    val title: String,
    val coverImageUrl: String?,
    val description: String,
    val authorUserId: String,
    val inspiredCount: Int,
    val isInspired: Boolean,
    val createdAt: String,
    val authorName: String,
    val authorUsername: String,
    val authorProfilePicUrl: String?,
    val isAuthorVerified: Boolean = false
)
