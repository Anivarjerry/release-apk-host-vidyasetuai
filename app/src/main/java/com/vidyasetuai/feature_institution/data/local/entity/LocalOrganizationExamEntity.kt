package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_organization_exams")
data class LocalOrganizationExamEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_exams.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "exam_type_id")
    val examTypeId: String,
    
    @ColumnInfo(name = "exam_type_name")
    val examTypeName: String?, // resolved from global_exam_types.name
    
    @ColumnInfo(name = "exam_type_code")
    val examTypeCode: String?, // resolved from global_exam_types.code
    
    @ColumnInfo(name = "name")
    val name: String, // exam name like "Term 1 Exam"
    
    @ColumnInfo(name = "start_date")
    val startDate: String?, // YYYY-MM-DD
    
    @ColumnInfo(name = "end_date")
    val endDate: String?, // YYYY-MM-DD
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_UPDATE
)
