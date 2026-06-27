package com.vidyasetuai.feature_institution.domain.model

data class StudentAttendanceInfo(
    val studentId: String,
    val name: String,
    val srNumber: String,
    val status: String, // "Present", "Absent", "On Leave"
    val isLeaveApproved: Boolean = false,
    val isLeavePending: Boolean = false
)
