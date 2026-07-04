package com.vidyasetuai.feature_institution.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class StudentHomeLocation(
    val id: String? = null,
    @SerialName("organization_id")
    val organizationId: String,
    @SerialName("student_id")
    val studentId: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("is_notification_sent")
    val isNotificationSent: Boolean = false,
    @SerialName("created_by")
    val createdBy: String? = null,
    @SerialName("updated_by")
    val updatedBy: String? = null
)
