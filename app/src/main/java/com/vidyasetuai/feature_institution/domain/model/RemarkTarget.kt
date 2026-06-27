package com.vidyasetuai.feature_institution.domain.model

data class RemarkTarget(
    val id: String,
    val parentOrgId: String,
    val organizationId: String?,
    val activeSessionId: String,
    val remarkId: String,
    val targetType: String, // Student, Guardian, Staff, Self
    val targetStudentId: String?,
    val targetGuardianId: String?,
    val targetStaffId: String?,
    val targetUserId: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?,
    val syncStatus: String = "Synced"
)
