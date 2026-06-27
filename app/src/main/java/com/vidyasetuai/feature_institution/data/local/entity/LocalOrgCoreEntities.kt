package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ============================================================
// ORGANIZATION CORE & PARENT ORG ENTITIES
// ============================================================

// Supabase table: organization_attendance_status
@Entity(tableName = "local_org_attendance_status")
data class LocalOrgAttendanceStatusEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val attendanceStatusId: String,
    val salaryPayoutPercentage: Double,
    val isActive: Boolean,
    val createdAt: String
)

// Supabase table: organization_boards
@Entity(tableName = "local_org_boards")
data class LocalOrgBoardEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val boardId: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: organization_bus_child_assignments
@Entity(tableName = "local_bus_child_assignments")
data class LocalBusChildAssignmentEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val organizationId: String,
    val activeSessionId: String,
    val busId: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: organization_exams
@Entity(tableName = "local_org_exams")
data class LocalOrgExamEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val examTypeId: String,
    val name: String,
    val startDate: String?,
    val endDate: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String
)

// Supabase table: organization_fee_assignments
@Entity(tableName = "local_fee_assignments")
data class LocalFeeAssignmentEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val sessionId: String,
    val organizationClassId: String,
    val feeHeadId: String,
    val amount: Double,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: organization_guardian_user_links
@Entity(tableName = "local_guardian_user_links")
data class LocalGuardianUserLinkEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val guardianId: String,
    val userId: String,
    val isApproved: Boolean,
    val approvedBy: String?,
    val approvedAt: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_holidays
@Entity(tableName = "local_holidays")
data class LocalHolidayEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val organizationId: String,
    val activeSessionId: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val description: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: organization_languages
@Entity(tableName = "local_org_languages")
data class LocalOrgLanguageEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val languageId: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: organization_mediums
@Entity(tableName = "local_org_mediums")
data class LocalOrgMediumEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val mediumId: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: organization_parent_buses (full table — replaces partial LocalDriverBusDetailsEntity)
@Entity(tableName = "local_parent_buses")
data class LocalParentBusEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val busNumber: String,
    val busName: String?,
    val routeName: String?,
    val capacity: Int?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: organization_parent_expenses
@Entity(tableName = "local_parent_expenses")
data class LocalParentExpenseEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val expenseTypeId: String,
    val amount: Double,
    val expenseDate: String,
    val description: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: organization_parents
@Entity(tableName = "local_parent_organizations")
data class LocalParentOrganizationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val organizationTypeId: String?,
    val boardId: String?,
    val mediumId: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: organization_parents_profiles
@Entity(tableName = "local_parent_org_profiles")
data class LocalParentOrgProfileEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val logoUrl: String?,
    val websiteUrl: String?,
    val email: String?,
    val mobileNumber: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val city: String?,
    val state: String?,
    val pincode: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_parents_users
@Entity(tableName = "local_parent_org_users")
data class LocalParentOrgUserEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val userId: String,
    val role: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_periods
@Entity(tableName = "local_org_periods")
data class LocalOrgPeriodEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val name: String,
    val startTime: String,
    val endTime: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_profiles
@Entity(tableName = "local_org_profiles")
data class LocalOrgProfileEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val logoUrl: String?,
    val websiteUrl: String?,
    val email: String?,
    val mobileNumber: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val city: String?,
    val state: String?,
    val pincode: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_users
@Entity(tableName = "local_org_users")
data class LocalOrgUserEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val userId: String,
    val role: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)
