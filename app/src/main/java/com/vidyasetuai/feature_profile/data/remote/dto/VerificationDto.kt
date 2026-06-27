package com.vidyasetuai.feature_profile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerificationDto(
    val id: String,
    val contributor_type: String,
    val user_id: String,
    val status: String,
    val applicant_note: String? = null,
    val rejection_reason: String? = null
)
