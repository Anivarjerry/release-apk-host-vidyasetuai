package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_student_qr_identities")
data class LocalStudentQrIdentityEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val studentId: String,
    val qrTokenHash: String,
    val version: Int,
    val status: String,
    val expiryDate: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

@Entity(tableName = "local_student_id_cards")
data class LocalStudentIdCardEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val studentId: String,
    val qrIdentityId: String,
    val cardNumber: String,
    val status: String,
    val reasonForReissue: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

@Entity(tableName = "local_parent_bus_trips")
data class LocalParentBusTripEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val busId: String,
    val driverId: String,
    val tripType: String,
    val status: String, // "Scheduled", "Ongoing", "Completed", "Cancelled"
    val startTime: String?,
    val endTime: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

@Entity(tableName = "local_parent_bus_trip_attendance_logs")
data class LocalParentBusTripAttendanceLogEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val organizationId: String,
    val activeSessionId: String,
    val tripId: String,
    val studentId: String,
    val status: String, // "Boarded", "Dropped", "Absent"
    val scanLatitude: Double?,
    val scanLongitude: Double?,
    val scannedAt: String,
    val scannedByStaffId: String,
    val syncStatus: String, // "Synced", "Offline_Pending"
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Helper projection class for joining logs with student details
data class LocalParentBusTripAttendanceLogWithStudentInfo(
    val id: String,
    val parentOrganizationId: String,
    val organizationId: String,
    val activeSessionId: String,
    val tripId: String,
    val studentId: String,
    val status: String,
    val scanLatitude: Double?,
    val scanLongitude: Double?,
    val scannedAt: String,
    val scannedByStaffId: String,
    val syncStatus: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?,
    val studentName: String?,
    val studentClassName: String?,
    val studentSectionName: String?,
    val studentRollNumber: Int?
)
