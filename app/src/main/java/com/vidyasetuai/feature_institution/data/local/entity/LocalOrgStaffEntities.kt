package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ============================================================
// ORGANIZATION STAFF RELATED ENTITIES
// ============================================================

// Supabase table: organization_parent_staff
@Entity(tableName = "local_parent_staff")
data class LocalParentStaffEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val name: String,
    val email: String?,
    val mobileNumber: String?,
    val roleId: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: organization_parent_staff_attendance
@Entity(tableName = "local_parent_staff_attendance")
data class LocalParentStaffAttendanceEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val staffId: String,
    val attendanceDate: String,
    val status: String,
    val remarks: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: organization_parent_staff_bus_enrollments
@Entity(tableName = "local_parent_staff_bus_enrollments")
data class LocalParentStaffBusEnrollmentEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val staffId: String,
    val busId: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_parent_staff_bus_fares
@Entity(tableName = "local_parent_staff_bus_fares")
data class LocalParentStaffBusFareEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val staffId: String,
    val busId: String,
    val fareAmount: Double,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_parent_staff_leave_quotas
@Entity(tableName = "local_parent_staff_leave_quotas")
data class LocalParentStaffLeaveQuotaEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val staffId: String,
    val leaveType: String,
    val totalLeaves: Double,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_parent_staff_salary_payouts
@Entity(tableName = "local_parent_staff_salary_payouts")
data class LocalParentStaffSalaryPayoutEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val staffId: String,
    val month: String,
    val year: Int,
    val baseSalary: Double,
    val daysPresent: Int?,
    val daysAbsent: Int?,
    val deductionAmount: Double,
    val netPayable: Double,
    val isPaid: Boolean,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_parent_staff_user_links
@Entity(tableName = "local_parent_staff_user_links")
data class LocalParentStaffUserLinkEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val staffId: String,
    val userId: String,
    val isApproved: Boolean,
    val approvedBy: String?,
    val approvedAt: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_parent_bus_staff_assignments
@Entity(tableName = "local_parent_bus_staff_assignments")
data class LocalParentBusStaffAssignmentEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val busId: String,
    val staffId: String,
    val role: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)
