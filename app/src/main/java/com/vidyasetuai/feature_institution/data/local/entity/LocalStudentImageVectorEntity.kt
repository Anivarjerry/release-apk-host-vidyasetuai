package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_student_image_vectors")
data class LocalStudentImageVectorEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val studentId: String,
    val personType: String,
    val faceVector: List<Float>,
    val imageUrl: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)
