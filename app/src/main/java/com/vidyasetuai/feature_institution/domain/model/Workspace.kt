package com.vidyasetuai.feature_institution.domain.model

data class Workspace(
    val id: String,
    val parentOrgId: String,
    val parentOrgName: String,
    val childOrgId: String?,
    val childOrgName: String?,
    val role: String, // "Student", "Guardian", "Teacher", "Driver", "Admin"
    val isActive: Boolean,
    val roleDisplayName: String? = null,
    val workspaceSubRole: String? = null,
    val roleImageUrl: String? = null,
    val roleImageLocalPath: String? = null,
    val studentId: String? = null,
    val guardianId: String? = null,
    val staffId: String? = null
)
