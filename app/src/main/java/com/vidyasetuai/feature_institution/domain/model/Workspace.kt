package com.vidyasetuai.feature_institution.domain.model

data class Workspace(
    val id: String,
    val parentOrgId: String,
    val parentOrgName: String,
    val childOrgId: String?,
    val childOrgName: String?,
    val role: String, // "Student", "Guardian", "Teacher", "Driver", "Admin"
    val isActive: Boolean
)
