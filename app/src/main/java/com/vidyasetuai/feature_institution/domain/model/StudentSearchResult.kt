package com.vidyasetuai.feature_institution.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StudentSearchResult(
    val id: String,
    val name: String,
    val sr_number: String? = null,
    val roll_number: Int? = null,
    val class_name: String? = null,
    val section_name: String? = null,
    val guardian_name: String? = null,
    val guardian_mobile: String? = null,
    val image_url: String? = null
)
