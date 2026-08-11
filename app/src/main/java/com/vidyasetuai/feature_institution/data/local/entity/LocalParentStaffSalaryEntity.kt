package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_parent_staff_salaries")
data class LocalParentStaffSalaryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,

    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,

    @ColumnInfo(name = "staff_id")
    val staffId: String,

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

    @ColumnInfo(name = "is_active")
    val isActive: Boolean,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "sync_state")
    val syncState: String = "SYNCED"
)
