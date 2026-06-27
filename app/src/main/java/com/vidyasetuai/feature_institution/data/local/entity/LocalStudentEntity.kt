package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Supabase table: organization_students
@Entity(tableName = "local_students")
data class LocalStudentEntity(
    @PrimaryKey val id: String,
    val organizationId: String = "",
    val activeSessionId: String = "",
    val name: String,
    val imageUrl: String?,
    val localImagePath: String? = null,   // WhatsApp style — downloaded image local path
    val isActive: Boolean = true,
    val isDeleted: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = "",
    val createdBy: String? = null,
    val updatedBy: String? = null,
    // Denormalized from organization_student_enrollments (join at sync time)
    val srNumber: String?,
    val rollNumber: Int?,
    val orgClassId: String? = null,       // FK to organization_classes
    val orgSectionId: String? = null,     // FK to organization_sections
    val className: String?,        // class name for display/search
    val sectionName: String?,      // section name for display/search
    // Denormalized from organization_guardians (join at sync time)
    val guardianName: String?,
    val guardianMobile: String?
)
