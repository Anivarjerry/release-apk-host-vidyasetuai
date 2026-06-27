package com.vidyasetuai.feature_profile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val user_id: String,
    val username: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val full_name: String? = null,
    val profile_picture_url: String? = null,
    val cover_photo_url: String? = null,
    val bio: String? = null,
    val preferred_language: String? = null,
    val is_verified: Boolean = false,
    val gender: String? = null,
    val date_of_birth: String? = null
)
