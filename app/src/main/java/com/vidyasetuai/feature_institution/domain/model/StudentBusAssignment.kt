package com.vidyasetuai.feature_institution.domain.model

data class StudentBusAssignment(
    val studentId: String,
    val studentName: String,
    val busId: String,
    val busNumber: String,
    val busName: String?,
    val routeName: String?,
    val pickupStop: String?
)
