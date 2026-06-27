package com.vidyasetuai.core.update.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VersionDto(
    val id: String? = null,
    val platform: String,
    val version_number: String,
    val build_number: Int,
    val file_url: String,
    val force_update: Boolean = false,
    val changelog: String? = "",
    val is_latest: Boolean = false,
    val created_at: String? = null
)
