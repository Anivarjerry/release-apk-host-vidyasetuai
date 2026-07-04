package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_student_additional_fees")
data class LocalStudentAdditionalFeeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_student_additional_fees.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "student_id")
    val studentId: String,
    
    @ColumnInfo(name = "global_fee_head_id")
    val globalFeeHeadId: String,
    
    @ColumnInfo(name = "global_fee_head_name")
    val globalFeeHeadName: String?,
    
    @ColumnInfo(name = "global_fee_head_code")
    val globalFeeHeadCode: String?,
    
    @ColumnInfo(name = "amount")
    val amount: Double,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String
)
