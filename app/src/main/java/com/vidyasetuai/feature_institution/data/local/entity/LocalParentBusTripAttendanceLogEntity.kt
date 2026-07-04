package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_parent_bus_trip_attendance_logs")
data class LocalParentBusTripAttendanceLogEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_parent_bus_trip_attendance_logs.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "trip_id")
    val tripId: String,
    
    @ColumnInfo(name = "student_id")
    val studentId: String,
    
    @ColumnInfo(name = "student_name")
    val studentName: String?,
    
    @ColumnInfo(name = "roll_number")
    val rollNumber: Int?,
    
    @ColumnInfo(name = "class_name")
    val className: String?,
    
    @ColumnInfo(name = "section_name")
    val sectionName: String?,
    
    @ColumnInfo(name = "status")
    val status: String, // Boarded, Dropped, Absent
    
    @ColumnInfo(name = "scan_latitude")
    val scanLatitude: Double?,
    
    @ColumnInfo(name = "scan_longitude")
    val scanLongitude: Double?,
    
    @ColumnInfo(name = "scanned_at")
    val scannedAt: String, // ISO timestamp
    
    @ColumnInfo(name = "scanned_by_staff_id")
    val scannedByStaffId: String,
    
    @ColumnInfo(name = "scanned_by_staff_name")
    val scannedByStaffName: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_INSERT
)
