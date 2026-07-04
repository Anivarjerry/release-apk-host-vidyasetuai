package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "local_student_attendance",
    indices = [
        Index(value = ["student_id", "attendance_date"], unique = true)
    ]
)
data class LocalStudentAttendanceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_student_attendance.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "student_id")
    val studentId: String,
    
    @ColumnInfo(name = "student_name")
    val studentName: String?,
    
    @ColumnInfo(name = "roll_number")
    val rollNumber: Int?,
    
    @ColumnInfo(name = "class_id")
    val classId: String?,
    
    @ColumnInfo(name = "class_name")
    val className: String?,
    
    @ColumnInfo(name = "section_id")
    val sectionId: String?,
    
    @ColumnInfo(name = "section_name")
    val sectionName: String?,
    
    @ColumnInfo(name = "attendance_date")
    val attendanceDate: String, // YYYY-MM-DD
    
    @ColumnInfo(name = "status")
    val status: String, // Present, Absent, Late, Half Day, On Leave
    
    @ColumnInfo(name = "remarks")
    val remarks: String?,
    
    @ColumnInfo(name = "marked_by_staff_id")
    val markedByStaffId: String?,
    
    @ColumnInfo(name = "marked_by_staff_name")
    val markedByStaffName: String?,
    
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
