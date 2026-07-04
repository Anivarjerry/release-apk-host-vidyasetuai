package com.vidyasetuai.feature_institution.domain.model

data class StaffAttendance(
    val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val staffId: String,
    val staffName: String?,
    val staffRole: String?,
    val attendanceDate: String,
    val statusId: String,
    val statusCode: String,
    val statusName: String,
    val isPaidLeave: Boolean,
    val checkInTime: String?,
    val checkOutTime: String?,
    val remarks: String?
)
