package com.vidyasetuai.feature_institution.domain.model

data class BusRouteStop(
    val id: String,
    val parentOrgId: String,
    val activeSessionId: String,
    val busId: String,
    val stopName: String,
    val stopOrder: Int,
    val latitude: Double?,
    val longitude: Double?,
    val scheduledTime: String?,
    val isActive: Boolean
)
