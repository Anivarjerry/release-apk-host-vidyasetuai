package com.vidyasetuai.feature_feed.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExperienceDto(
    val id: String,
    val title: String,
    val cover_image_url: String? = null,
    val description: String,
    val author_user_id: String,
    val inspired_count: Int = 0,
    val status: String = "published",
    val created_at: String
)

@Serializable
data class ExperienceInspirationDto(
    val id: String,
    val experience_id: String,
    val user_id: String,
    val created_at: String
)
