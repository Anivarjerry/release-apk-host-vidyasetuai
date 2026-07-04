package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "local_student_exam_marks",
    indices = [
        Index(value = ["exam_id", "class_id", "subject_id", "student_id"], unique = true)
    ]
)
data class LocalStudentExamMarkEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_student_exam_marks.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "exam_id")
    val examId: String,
    
    @ColumnInfo(name = "class_id")
    val classId: String,
    
    @ColumnInfo(name = "subject_id")
    val subjectId: String,
    
    @ColumnInfo(name = "student_id")
    val studentId: String,
    
    @ColumnInfo(name = "obtained_marks")
    val obtainedMarks: Double?,
    
    @ColumnInfo(name = "is_absent")
    val isAbsent: Boolean,
    
    @ColumnInfo(name = "is_medical_leave")
    val isMedicalLeave: Boolean,
    
    @ColumnInfo(name = "teacher_remarks")
    val teacherRemarks: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,
    
    // Denormalized Fields for fast offline rendering
    @ColumnInfo(name = "student_name")
    val studentName: String?,
    
    @ColumnInfo(name = "roll_number")
    val rollNumber: Int?,
    
    @ColumnInfo(name = "subject_name")
    val subjectName: String?,
    
    @ColumnInfo(name = "exam_name")
    val examName: String?,
    
    @ColumnInfo(name = "max_marks")
    val maxMarks: Double?, // cached from settings to compute offline percentages easily
    
    @ColumnInfo(name = "minimum_passing_marks")
    val minimumPassingMarks: Double?, // cached to display pass/fail status immediately
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_INSERT, PENDING_UPDATE
)
