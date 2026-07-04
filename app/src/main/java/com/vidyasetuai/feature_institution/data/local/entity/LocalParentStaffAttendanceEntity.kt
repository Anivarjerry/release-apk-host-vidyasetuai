package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "local_parent_staff_attendance",
    indices = [
        Index(value = ["staff_id", "attendance_date"], unique = true)
    ]
)
data class LocalParentStaffAttendanceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_parent_staff_attendance.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "staff_id")
    val staffId: String,
    
    @ColumnInfo(name = "staff_name")
    val staffName: String?, // resolved from organization_parent_staff.name
    
    @ColumnInfo(name = "staff_role")
    val staffRole: String?, // resolved from global_staff_roles.name
    
    @ColumnInfo(name = "attendance_date")
    val attendanceDate: String, // YYYY-MM-DD
    
    @ColumnInfo(name = "status_id")
    val statusId: String,
    
    @ColumnInfo(name = "status_code")
    val statusCode: String, // PR, AB, HD, LV
    
    @ColumnInfo(name = "status_name")
    val statusName: String, // Present, Absent, Half Day
    
    @ColumnInfo(name = "is_paid_leave")
    val isPaidLeave: Boolean,
    
    @ColumnInfo(name = "check_in_time")
    val checkInTime: String?, // HH:MM:SS
    
    @ColumnInfo(name = "check_out_time")
    val checkOutTime: String?, // HH:MM:SS
    
    @ColumnInfo(name = "remarks")
    val remarks: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_INSERT, PENDING_UPDATE
)
