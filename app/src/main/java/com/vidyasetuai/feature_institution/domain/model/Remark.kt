package com.vidyasetuai.feature_institution.domain.model

data class Remark(
    val id: String,
    val parentOrgId: String,
    val organizationId: String?,
    val activeSessionId: String,
    val content: String,
    val category: String,
    val priority: String, // Low, Medium, High, Critical
    val creatorUserId: String,
    val creatorWorkspaceRoleId: String?,
    val visibilityType: String, // Public, Private, Custom
    val visibilityAudience: List<String>,
    val isPinned: Boolean,
    val pinExpiresAt: String?,
    val expiresAt: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?,
    val syncStatus: String = "Synced" // Synced, Offline_Pending
)
