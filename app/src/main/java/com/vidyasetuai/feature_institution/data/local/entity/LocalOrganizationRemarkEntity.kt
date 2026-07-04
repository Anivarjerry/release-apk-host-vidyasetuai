package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_organization_remarks")
data class LocalOrganizationRemarkEntity(
    @PrimaryKey
    @ColumnInfo(name = "target_id")
    val targetId: String, // from organization_remark_targets.id
    
    @ColumnInfo(name = "id")
    val id: String, // from organization_remarks.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String?,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "content")
    val content: String,
    
    @ColumnInfo(name = "category")
    val category: String,
    
    @ColumnInfo(name = "priority")
    val priority: String, // Low, Medium, High, Critical
    
    @ColumnInfo(name = "creator_user_id")
    val creatorUserId: String,
    
    @ColumnInfo(name = "creator_user_name")
    val creatorUserName: String?,
    
    @ColumnInfo(name = "creator_role_name")
    val creatorRoleName: String?,
    
    @ColumnInfo(name = "visibility_type")
    val visibilityType: String, // Public, Private, Internal
    
    @ColumnInfo(name = "visibility_audience_json")
    val visibilityAudienceJson: String, // JSON Array of roles
    
    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean,
    
    @ColumnInfo(name = "pin_expires_at")
    val pinExpiresAt: String?,
    
    @ColumnInfo(name = "expires_at")
    val expiresAt: String?,
    
    // Target details
    @ColumnInfo(name = "target_type")
    val targetType: String, // student, guardian, staff, user
    
    @ColumnInfo(name = "target_student_id")
    val targetStudentId: String?,
    
    @ColumnInfo(name = "target_student_name")
    val targetStudentName: String?,
    
    @ColumnInfo(name = "target_class_name")
    val targetClassName: String?,
    
    @ColumnInfo(name = "target_section_name")
    val targetSectionName: String?,
    
    @ColumnInfo(name = "target_guardian_id")
    val targetGuardianId: String?,
    
    @ColumnInfo(name = "target_guardian_name")
    val targetGuardianName: String?,
    
    @ColumnInfo(name = "target_staff_id")
    val targetStaffId: String?,
    
    @ColumnInfo(name = "target_staff_name")
    val targetStaffName: String?,
    
    @ColumnInfo(name = "target_user_id")
    val targetUserId: String?,
    
    // Attachments
    @ColumnInfo(name = "attachments_json")
    val attachmentsJson: String, // JSON Array of attachments with remote/local paths
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_INSERT, PENDING_UPDATE
)
