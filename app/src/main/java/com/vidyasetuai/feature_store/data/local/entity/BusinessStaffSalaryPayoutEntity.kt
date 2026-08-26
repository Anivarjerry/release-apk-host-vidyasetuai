package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "business_staff_salary_payouts")
data class BusinessStaffSalaryPayoutEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("business_id")
    @ColumnInfo(name = "business_id")
    val businessId: String,

    @SerialName("branch_id")
    @ColumnInfo(name = "branch_id")
    val branchId: String? = null,

    @SerialName("staff_id")
    @ColumnInfo(name = "staff_id")
    val staffId: String,

    @SerialName("payout_month")
    @ColumnInfo(name = "payout_month")
    val payoutMonth: Int, // 1 to 12

    @SerialName("payout_year")
    @ColumnInfo(name = "payout_year")
    val payoutYear: Int,

    @SerialName("base_salary")
    @ColumnInfo(name = "base_salary")
    val baseSalary: Double,

    @SerialName("total_working_days")
    @ColumnInfo(name = "total_working_days")
    val totalWorkingDays: Int = 30,

    @SerialName("present_days")
    @ColumnInfo(name = "present_days")
    val presentDays: Double = 30.0,

    @SerialName("absent_days")
    @ColumnInfo(name = "absent_days")
    val absentDays: Double = 0.0,

    @SerialName("half_days")
    @ColumnInfo(name = "half_days")
    val halfDays: Double = 0.0,

    @SerialName("paid_leaves")
    @ColumnInfo(name = "paid_leaves")
    val paidLeaves: Double = 0.0,

    @SerialName("gross_salary")
    @ColumnInfo(name = "gross_salary")
    val grossSalary: Double,

    @SerialName("attendance_deduction")
    @ColumnInfo(name = "attendance_deduction")
    val attendanceDeduction: Double = 0.0,

    @SerialName("advance_deduction")
    @ColumnInfo(name = "advance_deduction")
    val advanceDeduction: Double = 0.0,

    @SerialName("bonus_amount")
    @ColumnInfo(name = "bonus_amount")
    val bonusAmount: Double = 0.0,

    @SerialName("net_payable_amount")
    @ColumnInfo(name = "net_payable_amount")
    val netPayableAmount: Double,

    @SerialName("paid_amount")
    @ColumnInfo(name = "paid_amount")
    val paidAmount: Double = 0.0,

    @SerialName("payment_status")
    @ColumnInfo(name = "payment_status")
    val paymentStatus: String = "UNPAID", // UNPAID, PARTIALLY_PAID, PAID

    @SerialName("is_locked")
    @ColumnInfo(name = "is_locked")
    val isLocked: Boolean = false,

    @SerialName("locked_at")
    @ColumnInfo(name = "locked_at")
    val lockedAt: String? = null,

    @SerialName("locked_by")
    @ColumnInfo(name = "locked_by")
    val lockedBy: String? = null,

    @SerialName("is_active")
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @SerialName("is_deleted")
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @SerialName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @SerialName("created_by")
    @ColumnInfo(name = "created_by")
    val createdBy: String? = null,

    @SerialName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String,

    @SerialName("updated_by")
    @ColumnInfo(name = "updated_by")
    val updatedBy: String? = null,

    @SerialName("sync_version")
    @ColumnInfo(name = "sync_version")
    val syncVersion: Long = 1,

    @SerialName("sync_status")
    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "SYNCED"
)
