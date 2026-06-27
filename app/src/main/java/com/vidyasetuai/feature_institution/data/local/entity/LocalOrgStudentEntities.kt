package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ============================================================
// ORGANIZATION STUDENT RELATED ENTITIES
// ============================================================

// Supabase table: organization_student_enrollments
@Entity(tableName = "local_student_enrollments")
data class LocalStudentEnrollmentEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val studentId: String,
    val organizationClassId: String,
    val organizationSectionId: String?,
    val rollNumber: Int?,
    val srNumber: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: organization_guardians
@Entity(tableName = "local_guardians")
data class LocalGuardianEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val name: String,
    val mobileNumber: String,
    val relationshipTypeId: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: organization_student_additional_details
@Entity(tableName = "local_student_additional_details")
data class LocalStudentAdditionalDetailsEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val studentId: String,
    val bloodGroupId: String?,
    val categoryId: String?,
    val aadharNumber: String?,
    val dateOfBirth: String?,
    val gender: String?,
    val address: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_student_additional_fees
@Entity(tableName = "local_student_additional_fees")
data class LocalStudentAdditionalFeeEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val sessionId: String,
    val studentId: String,
    val feeHeadId: String,
    val amount: Double,
    val reason: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_student_exam_marks
@Entity(tableName = "local_student_exam_marks")
data class LocalStudentExamMarkEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val examId: String,
    val studentId: String,
    val subjectId: String,
    val marksObtained: Double?,
    val isAbsent: Boolean,
    val remarks: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_student_homeworks
@Entity(tableName = "local_student_homeworks")
data class LocalStudentHomeworkEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val studentId: String,
    val subjectId: String?,
    val title: String,
    val description: String?,
    val dueDate: String?,
    val isCompleted: Boolean,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_student_marks
@Entity(tableName = "local_student_marks")
data class LocalStudentMarkEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val studentId: String,
    val subjectId: String,
    val marksObtained: Double?,
    val maxMarks: Double?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_student_subjects
@Entity(tableName = "local_student_subjects")
data class LocalStudentSubjectEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val studentId: String,
    val subjectId: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_student_user_links
@Entity(tableName = "local_student_user_links")
data class LocalStudentUserLinkEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val studentId: String,
    val userId: String,
    val isApproved: Boolean,
    val approvedBy: String?,
    val approvedAt: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)
