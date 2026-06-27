package com.vidyasetuai.feature_institution.domain.model

data class BusLiveLocation(
    val busId: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Double,
    val updatedAt: String,
    val isLive: Boolean
)
