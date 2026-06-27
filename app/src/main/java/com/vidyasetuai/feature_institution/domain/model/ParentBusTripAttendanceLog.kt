package com.vidyasetuai.feature_institution.domain.model

data class ParentBusTripAttendanceLog(
    val id: String,
    val parentOrganizationId: String,
    val organizationId: String,
    val activeSessionId: String,
    val tripId: String,
    val studentId: String,
    val status: String, // "Boarded", "Dropped", "Absent"
    val scanLatitude: Double?,
    val scanLongitude: Double?,
    val scannedAt: String,
    val scannedByStaffId: String,
    val syncStatus: String, // "Synced", "Offline_Pending"
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)
