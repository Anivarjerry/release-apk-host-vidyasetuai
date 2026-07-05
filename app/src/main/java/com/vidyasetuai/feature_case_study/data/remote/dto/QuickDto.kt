package com.vidyasetuai.feature_case_study.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuickDto(
    val id: String,
    val author_user_id: String,
    val title: String,
    val description: String,
    val cover_image_url: String? = null,
    val views_count: Int = 0,
    val helpful_count: Int = 0,
    val status: String = "published",
    val created_at: String,
    val expires_at: String
)

@Serializable
data class QuickHelpfulDto(
    val id: String,
    val quick_id: String,
    val user_id: String,
    val created_at: String
)
