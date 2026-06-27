package com.vidyasetuai.feature_case_study.domain.model

/**
 * Preview model — Home Screen cards के लिए।
 * content_blocks यहाँ नहीं है — only list data।
 */
data class CaseStudy(
    val id: String,
    val title: String,
    val slug: String,
    val coverImageUrl: String,
    val shortDescription: String,
    val language: String,
    val tags: List<String>,
    val readTimeMinutes: Int?,
    val authorType: String,
    val authorUserId: String?,
    val authorName: String,
    val authorUsername: String,
    val authorProfilePicUrl: String?,
    val isAuthorVerified: Boolean = false,
    val viewCount: Int,
    val publishedAt: String?,
    val createdAt: String,
    val updatedAt: String,
    // User interaction state (local)
    val isReacted: Boolean = false,
    val reactionCount: Int = 0,
    val isBookmarked: Boolean = false,
    val bookmarkCount: Int = 0
)
