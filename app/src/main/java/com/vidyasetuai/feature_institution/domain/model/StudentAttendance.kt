package com.vidyasetuai.feature_institution.domain.model

data class StudentAttendance(
    val id: String,
    val studentId: String,
    val attendanceDate: String,
    val status: String,
    val remarks: String?
)
