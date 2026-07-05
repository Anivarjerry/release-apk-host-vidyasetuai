package com.vidyasetuai.feature_case_study.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonObject

@Serializable
data class CaseStudyDto(
    val id: String,
    val title: String,
    val slug: String = "",
    val cover_image_url: String? = null,
    val short_description: String,
    val language: String = "hindi",
    val tags: List<String> = emptyList(),
    val read_time_minutes: Int? = null,
    val author_type: String = "user",
    val author_user_id: String? = null,
    @SerialName("views_count")
    val view_count: Int = 0,
    val published_at: String? = null,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class CaseStudyDetailDto(
    val id: String,
    val title: String,
    val slug: String = "",
    val cover_image_url: String? = null,
    val short_description: String,
    val language: String = "hindi",
    val tags: List<String> = emptyList(),
    val read_time_minutes: Int? = null,
    @SerialName("content")
    val content: JsonObject,
    val additional_image_urls: List<String> = emptyList(),
    val author_type: String = "user",
    val author_user_id: String? = null,
    @SerialName("views_count")
    val view_count: Int = 0,
    val published_at: String? = null,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class ReactionDto(
    val id: String,
    val case_study_id: String,
    val user_id: String,
    val reaction_type: String = "helpful"
)

@Serializable
data class BookmarkDto(
    val id: String,
    val case_study_id: String,
    val user_id: String
)
