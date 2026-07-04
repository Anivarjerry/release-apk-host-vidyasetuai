package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_exam_subject_settings")
data class LocalExamSubjectSettingEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_exam_subject_settings.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "exam_id")
    val examId: String, // links to local_organization_exams.id
    
    @ColumnInfo(name = "class_id")
    val classId: String,
    
    @ColumnInfo(name = "subject_id")
    val subjectId: String,
    
    @ColumnInfo(name = "class_name")
    val className: String?, // resolved class name like "Class 10"
    
    @ColumnInfo(name = "subject_name")
    val subjectName: String?, // resolved subject name like "Mathematics"
    
    @ColumnInfo(name = "max_marks")
    val maxMarks: Double?,
    
    @ColumnInfo(name = "minimum_passing_marks")
    val minimumPassingMarks: Double?,
    
    @ColumnInfo(name = "grading_system")
    val gradingSystem: String?, // JSON string of grading rules (A, B, C etc.)
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED
)
