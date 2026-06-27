package com.vidyasetuai.feature_institution.domain.model

data class ParentBusTrip(
    val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val busId: String,
    val driverId: String,
    val tripType: String, // "Morning_Pickup", "Evening_Drop", "Special"
    val status: String, // "Scheduled", "Ongoing", "Completed", "Cancelled"
    val startTime: String?,
    val endTime: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)
