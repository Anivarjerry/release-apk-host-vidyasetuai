package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ============================================================
// ALL GLOBAL_* TABLE ENTITIES
// Read-only reference data synced once from Supabase
// ============================================================

// Supabase table: global_attendance_status
@Entity(tableName = "local_global_attendance_status")
data class LocalGlobalAttendanceStatusEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: global_blood_groups
@Entity(tableName = "local_global_blood_groups")
data class LocalGlobalBloodGroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: global_boards
@Entity(tableName = "local_global_boards")
data class LocalGlobalBoardEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val description: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: global_classes
@Entity(tableName = "local_global_classes")
data class LocalGlobalClassEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val levelOrder: Int,
    val educationLevel: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: global_exam_subject_rules
@Entity(tableName = "local_global_exam_subject_rules")
data class LocalGlobalExamSubjectRuleEntity(
    @PrimaryKey val id: String,
    val classId: String,
    val subjectId: String,
    val examTypeId: String,
    val defaultMaxMarks: Double,
    val defaultPassingMarks: Double,
    val defaultGradingSystem: String,   // JSONB stored as JSON string
    val isActive: Boolean
)

// Supabase table: global_exam_types
@Entity(tableName = "local_global_exam_types")
data class LocalGlobalExamTypeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String?,
    val description: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: global_expense_types
@Entity(tableName = "local_global_expense_types")
data class LocalGlobalExpenseTypeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String?,
    val isActive: Boolean,
    val referenceType: String?,         // USER-DEFINED enum stored as string
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: global_facilities
@Entity(tableName = "local_global_facilities")
data class LocalGlobalFacilityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val description: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: global_languages
@Entity(tableName = "local_global_languages")
data class LocalGlobalLanguageEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: global_mediums
@Entity(tableName = "local_global_mediums")
data class LocalGlobalMediumEntity(
    @PrimaryKey val id: String,
    val boardId: String,
    val name: String,
    val code: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

// Supabase table: global_organization_parent_types
@Entity(tableName = "local_global_org_parent_types")
data class LocalGlobalOrgParentTypeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val description: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: global_organization_types
@Entity(tableName = "local_global_org_types")
data class LocalGlobalOrgTypeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val description: String?,
    val isActive: Boolean,
    val isOwn: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: global_relationship_types
@Entity(tableName = "local_global_relationship_types")
data class LocalGlobalRelationshipTypeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: global_result_status
@Entity(tableName = "local_global_result_status")
data class LocalGlobalResultStatusEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: global_student_categories
@Entity(tableName = "local_global_student_categories")
data class LocalGlobalStudentCategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val description: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: global_student_status
@Entity(tableName = "local_global_student_status")
data class LocalGlobalStudentStatusEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: global_subjects
@Entity(tableName = "local_global_subjects")
data class LocalGlobalSubjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String?,
    val subjectType: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)
