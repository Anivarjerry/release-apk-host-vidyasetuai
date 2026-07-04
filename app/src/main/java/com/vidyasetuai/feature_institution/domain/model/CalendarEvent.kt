package com.vidyasetuai.feature_institution.domain.model

data class CalendarEvent(
    val id: String,
    val parentOrganizationId: String,
    val parentOrganizationName: String?,
    val organizationId: String,
    val organizationName: String?,
    val activeSessionId: String,
    val name: String,
    val description: String?,
    val startDate: String,
    val endDate: String,
    val eventType: String,
    val isSchoolClosed: Boolean
)
