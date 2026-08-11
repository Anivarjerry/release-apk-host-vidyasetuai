package com.vidyasetuai.feature_campus.domain.model

data class ModerationSettings(
    val id: Int,
    val blockedKeywords: List<String>,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
