package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_institution.domain.model.Workspace

@Entity(tableName = "workspaces")
data class WorkspaceEntity(
    @PrimaryKey val id: String,
    val parentOrgId: String,
    val parentOrgName: String,
    val childOrgId: String?,
    val childOrgName: String?,
    val role: String,
    val isActive: Boolean
) {
    fun toDomain(): Workspace = Workspace(
        id = id,
        parentOrgId = parentOrgId,
        parentOrgName = parentOrgName,
        childOrgId = childOrgId,
        childOrgName = childOrgName,
        role = role,
        isActive = isActive
    )

    companion object {
        fun fromDomain(workspace: Workspace): WorkspaceEntity = WorkspaceEntity(
            id = workspace.id,
            parentOrgId = workspace.parentOrgId,
            parentOrgName = workspace.parentOrgName,
            childOrgId = workspace.childOrgId,
            childOrgName = workspace.childOrgName,
            role = workspace.role,
            isActive = workspace.isActive
        )
    }
}