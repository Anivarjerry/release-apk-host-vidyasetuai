package com.vidyasetuai.feature_institution.domain.model

data class Leave(
    val id: String,
    val parentOrgId: String,
    val organizationId: String?,
    val applicantType: String, // "staff", "student"
    val staffId: String?,
    val studentId: String?,
    val leaveType: String,
    val startDate: String,
    val endDate: String,
    val isHalfDay: Boolean,
    val halfDayPeriod: String?,
    val reason: String?,
    val status: String // "Pending", "Approved", "Rejected"
)
