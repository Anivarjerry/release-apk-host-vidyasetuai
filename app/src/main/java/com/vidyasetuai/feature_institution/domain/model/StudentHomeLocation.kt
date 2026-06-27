package com.vidyasetuai.feature_institution.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StudentHomeLocation(
    val id: String? = null,
    val organizationId: String,
    val studentId: String,
    val latitude: Double,
    val longitude: Double,
    val isNotificationSent: Boolean = false,
    val createdBy: String? = null,
    val updatedBy: String? = null
)
