package com.vidyasetuai.feature_case_study.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "case_study_details_cache")
data class CaseStudyDetailEntity(
    @PrimaryKey val id: String,
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
    val isAuthorVerified: Boolean,
    val viewCount: Int,
    val publishedAt: String?,
    val createdAt: String,
    val updatedAt: String,
    
    // Full Content
    val contentBlocksJson: String,
    val additionalImageUrls: List<String>,

    // User interaction (cached)
    val isReacted: Boolean,
    val reactionCount: Int,
    val isBookmarked: Boolean,
    val bookmarkCount: Int
)
