package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_parent_staff")
data class LocalParentStaffEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_parent_staff.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "mobile_number")
    val mobileNumber: String,
    
    @ColumnInfo(name = "email")
    val email: String?,
    
    @ColumnInfo(name = "gender")
    val gender: String?,
    
    @ColumnInfo(name = "date_of_joining")
    val dateOfJoining: String,
    
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String?,
    
    @ColumnInfo(name = "pan_number")
    val panNumber: String?,
    
    @ColumnInfo(name = "aadhaar_number")
    val aadhaarNumber: String?,
    
    @ColumnInfo(name = "license_number")
    val licenseNumber: String?,
    
    @ColumnInfo(name = "license_expiry_date")
    val licenseExpiryDate: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Resolved Global Details
    @ColumnInfo(name = "role_id")
    val roleId: String?,
    @ColumnInfo(name = "role_name")
    val roleName: String?,
    @ColumnInfo(name = "subject_id")
    val subjectId: String?,
    @ColumnInfo(name = "subject_name")
    val subjectName: String?,
    @ColumnInfo(name = "address_area_id")
    val addressAreaId: String?,
    @ColumnInfo(name = "address_area_name")
    val addressAreaName: String?,

    // Leave Quota Details
    @ColumnInfo(name = "leave_quota_id")
    val leaveQuotaId: String?,
    @ColumnInfo(name = "total_leaves")
    val totalLeaves: Double,

    // Salary & Bank Details
    @ColumnInfo(name = "salary_id")
    val salaryId: String?,
    @ColumnInfo(name = "monthly_salary")
    val monthlySalary: Double,
    @ColumnInfo(name = "bank_name")
    val bankName: String?,
    @ColumnInfo(name = "bank_account_number")
    val bankAccountNumber: String?,
    @ColumnInfo(name = "ifsc_code")
    val ifscCode: String?,
    @ColumnInfo(name = "upi_id")
    val upiId: String?,

    // Bus Assignment Details
    @ColumnInfo(name = "bus_assignment_id")
    val busAssignmentId: String?,
    @ColumnInfo(name = "bus_id")
    val busId: String?,
    @ColumnInfo(name = "bus_number")
    val busNumber: String?,
    @ColumnInfo(name = "bus_name")
    val busName: String?,
    @ColumnInfo(name = "route_name")
    val routeName: String?,
    @ColumnInfo(name = "role_in_bus")
    val roleInBus: String?,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String
)
