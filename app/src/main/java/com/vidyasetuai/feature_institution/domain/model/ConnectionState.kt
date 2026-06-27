package com.vidyasetuai.feature_institution.domain.model

sealed class ConnectionState {
    object NOT_CONNECTED : ConnectionState()
    object CONNECTED : ConnectionState()
    
    data class PENDING(
        val role: String,
        val targetName: String,
        val personName: String,
        val logoUrl: String? = null,
        val websiteUrl: String? = null,
        val email: String? = null,
        val mobileNumber: String? = null,
        val address: String? = null
    ) : ConnectionState()
}
