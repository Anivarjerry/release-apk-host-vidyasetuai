package com.vidyasetuai.feature_profile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserInspirationDto(
    val id: String? = null,
    val inspired_user_id: String? = null,
    val inspiring_user_id: String? = null,
    val is_deleted: Boolean = false,
    val created_at: String? = null,
    val updated_at: String? = null
)
