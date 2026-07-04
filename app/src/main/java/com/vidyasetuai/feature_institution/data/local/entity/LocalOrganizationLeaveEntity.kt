package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_organization_leaves")
data class LocalOrganizationLeaveEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_leaves.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String?,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "applicant_type")
    val applicantType: String, // staff, student
    
    @ColumnInfo(name = "staff_id")
    val staffId: String?,
    
    @ColumnInfo(name = "staff_name")
    val staffName: String?,
    
    @ColumnInfo(name = "staff_role_name")
    val staffRoleName: String?,
    
    @ColumnInfo(name = "student_id")
    val studentId: String?,
    
    @ColumnInfo(name = "student_name")
    val studentName: String?,
    
    @ColumnInfo(name = "class_name")
    val className: String?,
    
    @ColumnInfo(name = "section_name")
    val sectionName: String?,
    
    @ColumnInfo(name = "leave_type")
    val leaveType: String,
    
    @ColumnInfo(name = "start_date")
    val startDate: String, // YYYY-MM-DD
    
    @ColumnInfo(name = "end_date")
    val endDate: String, // YYYY-MM-DD
    
    @ColumnInfo(name = "is_half_day")
    val isHalfDay: Boolean,
    
    @ColumnInfo(name = "half_day_period")
    val halfDayPeriod: String?, // First Half, Second Half
    
    @ColumnInfo(name = "reason")
    val reason: String?,
    
    @ColumnInfo(name = "status")
    val status: String, // Pending, Approved, Rejected
    
    @ColumnInfo(name = "action_remarks")
    val actionRemarks: String?,
    
    @ColumnInfo(name = "action_by")
    val actionBy: String?,
    
    @ColumnInfo(name = "action_by_name")
    val actionByName: String?,
    
    @ColumnInfo(name = "action_at")
    val actionAt: String?,
    
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
