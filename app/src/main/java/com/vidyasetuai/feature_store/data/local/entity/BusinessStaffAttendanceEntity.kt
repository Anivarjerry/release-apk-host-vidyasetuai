package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "business_staff_attendance")
data class BusinessStaffAttendanceEntity(
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

    @SerialName("attendance_date")
    @ColumnInfo(name = "attendance_date")
    val attendanceDate: String, // YYYY-MM-DD

    @SerialName("status")
    @ColumnInfo(name = "status")
    val status: String = "PRESENT", // PRESENT, ABSENT, HALF_DAY, PAID_LEAVE, HOLIDAY

    @SerialName("check_in_time")
    @ColumnInfo(name = "check_in_time")
    val checkInTime: String? = null,

    @SerialName("check_out_time")
    @ColumnInfo(name = "check_out_time")
    val checkOutTime: String? = null,

    @SerialName("remarks")
    @ColumnInfo(name = "remarks")
    val remarks: String? = null,

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
