package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "workspaces",
    primaryKeys = ["workspace_id", "workspace_role"]
)
data class WorkspaceEntity(
    @ColumnInfo(name = "workspace_id")
    val workspaceId: String,
    
    @ColumnInfo(name = "workspace_role")
    val workspaceRole: String, // STAFF, STUDENT, GUARDIAN
    
    @ColumnInfo(name = "workspace_sub_role")
    val workspaceSubRole: String, // TEACHER, ADMIN, DRIVER, etc.
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "parent_organization_name")
    val parentOrganizationName: String?,
    
    @ColumnInfo(name = "parent_org_logo_url")
    val parentOrgLogoUrl: String?,
    
    @ColumnInfo(name = "parent_org_logo_local_path")
    val parentOrgLogoLocalPath: String?,
    
    @ColumnInfo(name = "parent_org_email")
    val parentOrgEmail: String?,
    
    @ColumnInfo(name = "parent_org_mobile")
    val parentOrgMobile: String?,
    
    @ColumnInfo(name = "parent_org_active_session_id")
    val parentOrgActiveSessionId: String,
    
    @ColumnInfo(name = "parent_org_active_session_name")
    val parentOrgActiveSessionName: String?,
    
    @ColumnInfo(name = "parent_org_website_url")
    val parentOrgWebsiteUrl: String?,
    
    @ColumnInfo(name = "child_organization_id")
    val childOrganizationId: String?,
    
    @ColumnInfo(name = "child_organization_name")
    val childOrganizationName: String?,
    
    @ColumnInfo(name = "child_org_logo_url")
    val childOrgLogoUrl: String?,
    
    @ColumnInfo(name = "child_org_logo_local_path")
    val childOrgLogoLocalPath: String?,
    
    @ColumnInfo(name = "child_org_email")
    val childOrgEmail: String?,
    
    @ColumnInfo(name = "child_org_mobile_number")
    val childOrgMobileNumber: String?,
    
    @ColumnInfo(name = "child_org_alternate_mobile")
    val childOrgAlternateMobile: String?,
    
    @ColumnInfo(name = "child_org_active_session_id")
    val childOrgActiveSessionId: String?,
    
    @ColumnInfo(name = "child_org_active_session_name")
    val childOrgActiveSessionName: String?,
    
    @ColumnInfo(name = "child_org_address_line1")
    val childOrgAddressLine1: String?,
    
    @ColumnInfo(name = "child_org_address_line2")
    val childOrgAddressLine2: String?,
    
    @ColumnInfo(name = "child_org_city")
    val childOrgCity: String?,
    
    @ColumnInfo(name = "child_org_state")
    val childOrgState: String? = "Rajasthan",
    
    @ColumnInfo(name = "child_org_pincode")
    val childOrgPincode: String?,
    
    @ColumnInfo(name = "staff_id")
    val staffId: String?,
    
    @ColumnInfo(name = "student_id")
    val studentId: String?,
    
    @ColumnInfo(name = "guardian_id")
    val guardianId: String?,
    
    @ColumnInfo(name = "role_display_name")
    val roleDisplayName: String?,
    
    @ColumnInfo(name = "role_mobile_number")
    val roleMobileNumber: String?,
    
    @ColumnInfo(name = "role_image_url")
    val roleImageUrl: String?,
    
    @ColumnInfo(name = "role_image_local_path")
    val roleImageLocalPath: String?,
    
    @ColumnInfo(name = "is_approved")
    val isApproved: Boolean,
    
    @ColumnInfo(name = "approved_by")
    val approvedBy: String?,
    
    @ColumnInfo(name = "approved_at")
    val approvedAt: String?,
    
    @ColumnInfo(name = "is_active_now")
    val isActiveNow: Boolean,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long
)
