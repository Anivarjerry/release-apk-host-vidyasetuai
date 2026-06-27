package com.vidyasetuai.feature_institution.domain.model

data class GlobalStaffRole(
    val id: String,
    val name: String,
    val code: String,
    val description: String?,
    val isActive: Boolean,
    val isDeleted: Boolean
)
