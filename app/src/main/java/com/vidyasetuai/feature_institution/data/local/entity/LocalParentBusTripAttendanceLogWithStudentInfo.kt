package com.vidyasetuai.feature_institution.data.local.entity

data class LocalParentBusTripAttendanceLogWithStudentInfo(
    val studentId: String = "",
    val studentName: String? = null,
    val studentClassName: String? = null,
    val studentSectionName: String? = null,
    val studentRollNumber: Int? = null,
    val scannedAt: String = "",
    val status: String = ""
)
