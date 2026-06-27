package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_institution.domain.model.Remark
import com.vidyasetuai.feature_institution.domain.model.RemarkTarget
import com.vidyasetuai.feature_institution.domain.model.GlobalStaffRole

@Entity(tableName = "local_remarks")
data class LocalRemarkEntity(
    @PrimaryKey val id: String,
    val parentOrgId: String,
    val organizationId: String?,
    val activeSessionId: String,
    val content: String,
    val category: String,
    val priority: String,
    val creatorUserId: String,
    val creatorWorkspaceRoleId: String?,
    val visibilityType: String,
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
    val syncStatus: String
) {
    fun toDomain(): Remark = Remark(
        id = id, parentOrgId = parentOrgId, organizationId = organizationId, activeSessionId = activeSessionId,
        content = content, category = category, priority = priority, creatorUserId = creatorUserId,
        creatorWorkspaceRoleId = creatorWorkspaceRoleId, visibilityType = visibilityType, visibilityAudience = visibilityAudience,
        isPinned = isPinned, pinExpiresAt = pinExpiresAt, expiresAt = expiresAt, isActive = isActive, isDeleted = isDeleted,
        createdAt = createdAt, updatedAt = updatedAt, createdBy = createdBy, updatedBy = updatedBy, syncStatus = syncStatus
    )

    companion object {
        fun fromDomain(d: Remark): LocalRemarkEntity = LocalRemarkEntity(
            id = d.id, parentOrgId = d.parentOrgId, organizationId = d.organizationId, activeSessionId = d.activeSessionId,
            content = d.content, category = d.category, priority = d.priority, creatorUserId = d.creatorUserId,
            creatorWorkspaceRoleId = d.creatorWorkspaceRoleId, visibilityType = d.visibilityType, visibilityAudience = d.visibilityAudience,
            isPinned = d.isPinned, pinExpiresAt = d.pinExpiresAt, expiresAt = d.expiresAt, isActive = d.isActive, isDeleted = d.isDeleted,
            createdAt = d.createdAt, updatedAt = d.updatedAt, createdBy = d.createdBy, updatedBy = d.updatedBy, syncStatus = d.syncStatus
        )
    }
}

@Entity(tableName = "local_remark_targets")
data class LocalRemarkTargetEntity(
    @PrimaryKey val id: String,
    val parentOrgId: String,
    val organizationId: String?,
    val activeSessionId: String,
    val remarkId: String,
    val targetType: String,
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
    val syncStatus: String
) {
    fun toDomain(): RemarkTarget = RemarkTarget(
        id = id, parentOrgId = parentOrgId, organizationId = organizationId, activeSessionId = activeSessionId,
        remarkId = remarkId, targetType = targetType, targetStudentId = targetStudentId, targetGuardianId = targetGuardianId,
        targetStaffId = targetStaffId, targetUserId = targetUserId, isActive = isActive, isDeleted = isDeleted,
        createdAt = createdAt, updatedAt = updatedAt, createdBy = createdBy, updatedBy = updatedBy, syncStatus = syncStatus
    )

    companion object {
        fun fromDomain(d: RemarkTarget): LocalRemarkTargetEntity = LocalRemarkTargetEntity(
            id = d.id, parentOrgId = d.parentOrgId, organizationId = d.organizationId, activeSessionId = d.activeSessionId,
            remarkId = d.remarkId, targetType = d.targetType, targetStudentId = d.targetStudentId, targetGuardianId = d.targetGuardianId,
            targetStaffId = d.targetStaffId, targetUserId = d.targetUserId, isActive = d.isActive, isDeleted = d.isDeleted,
            createdAt = d.createdAt, updatedAt = d.updatedAt, createdBy = d.createdBy, updatedBy = d.updatedBy, syncStatus = d.syncStatus
        )
    }
}

@Entity(tableName = "local_global_staff_roles")
data class LocalGlobalStaffRoleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val description: String?,
    val isActive: Boolean,
    val isDeleted: Boolean
) {
    fun toDomain(): GlobalStaffRole = GlobalStaffRole(
        id = id, name = name, code = code, description = description, isActive = isActive, isDeleted = isDeleted
    )

    companion object {
        fun fromDomain(d: GlobalStaffRole): LocalGlobalStaffRoleEntity = LocalGlobalStaffRoleEntity(
            id = d.id, name = d.name, code = d.code, description = d.description, isActive = d.isActive, isDeleted = d.isDeleted
        )
    }
}
