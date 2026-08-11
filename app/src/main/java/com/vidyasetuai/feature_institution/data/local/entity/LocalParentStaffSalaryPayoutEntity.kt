package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_parent_staff_salary_payouts")
data class LocalParentStaffSalaryPayoutEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,

    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,

    @ColumnInfo(name = "staff_id")
    val staffId: String,

    @ColumnInfo(name = "payout_month")
    val payoutMonth: Int,

    @ColumnInfo(name = "payout_year")
    val payoutYear: Int,

    @ColumnInfo(name = "salary_amount")
    val salaryAmount: Double,

    @ColumnInfo(name = "bonus")
    val bonus: Double,

    @ColumnInfo(name = "deduction")
    val deduction: Double,

    @ColumnInfo(name = "is_locked")
    val isLocked: Boolean,

    @ColumnInfo(name = "locked_at")
    val lockedAt: String?,

    @ColumnInfo(name = "locked_by")
    val lockedBy: String?,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "sync_state")
    val syncState: String = "SYNCED"
)
