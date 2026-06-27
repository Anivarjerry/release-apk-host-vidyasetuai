package com.vidyasetuai.feature_institution.domain.model

data class DriverBusDetails(
    val busId: String,
    val busNumber: String,
    val routeName: String?,
    val staffId: String,
    val parentOrgId: String,
    val activeSessionId: String
)
