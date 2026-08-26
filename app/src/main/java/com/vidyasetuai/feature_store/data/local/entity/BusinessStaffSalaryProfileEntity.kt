package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "business_staff_salary_profiles")
data class BusinessStaffSalaryProfileEntity(
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

    @SerialName("salary_type")
    @ColumnInfo(name = "salary_type")
    val salaryType: String = "MONTHLY", // MONTHLY, DAILY, PER_ORDER

    @SerialName("base_salary")
    @ColumnInfo(name = "base_salary")
    val baseSalary: Double = 0.0,

    @SerialName("working_days_per_month")
    @ColumnInfo(name = "working_days_per_month")
    val workingDaysPerMonth: Int = 30,

    @SerialName("bank_name")
    @ColumnInfo(name = "bank_name")
    val bankName: String? = null,

    @SerialName("account_number")
    @ColumnInfo(name = "account_number")
    val accountNumber: String? = null,

    @SerialName("ifsc_code")
    @ColumnInfo(name = "ifsc_code")
    val ifscCode: String? = null,

    @SerialName("account_holder_name")
    @ColumnInfo(name = "account_holder_name")
    val accountHolderName: String? = null,

    @SerialName("upi_id")
    @ColumnInfo(name = "upi_id")
    val upiId: String? = null,

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
