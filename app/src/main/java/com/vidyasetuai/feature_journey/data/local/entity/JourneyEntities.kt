package com.vidyasetuai.feature_journey.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "global_journey_templates")
data class GlobalJourneyTemplateEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val durationDays: Int,
    val category: String?,
    val version: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

@Entity(tableName = "organization_journey_templates")
data class OrganizationJourneyTemplateEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val title: String,
    val description: String?,
    val durationDays: Int,
    val category: String?,
    val version: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

@Entity(tableName = "organization_parent_journey_templates")
data class OrganizationParentJourneyTemplateEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val title: String,
    val description: String?,
    val durationDays: Int,
    val category: String?,
    val version: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

@Entity(tableName = "global_journey_tasks")
data class GlobalJourneyTaskEntity(
    @PrimaryKey val id: String,
    val templateId: String,
    val dayNumber: Int,
    val taskTitle: String,
    val taskDescription: String?,
    val taskType: String,
    val sortOrder: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

@Entity(tableName = "organization_journey_tasks")
data class OrganizationJourneyTaskEntity(
    @PrimaryKey val id: String,
    val templateId: String,
    val dayNumber: Int,
    val taskTitle: String,
    val taskDescription: String?,
    val taskType: String,
    val sortOrder: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

@Entity(tableName = "organization_parent_journey_tasks")
data class OrganizationParentJourneyTaskEntity(
    @PrimaryKey val id: String,
    val templateId: String,
    val dayNumber: Int,
    val taskTitle: String,
    val taskDescription: String?,
    val taskType: String,
    val sortOrder: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

@Entity(tableName = "global_journey_mcqs")
data class GlobalJourneyMcqEntity(
    @PrimaryKey val id: String,
    val templateId: String,
    val dayNumber: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String,
    val explanation: String?,
    val difficultyLevel: String,
    val points: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

@Entity(tableName = "organization_journey_mcqs")
data class OrganizationJourneyMcqEntity(
    @PrimaryKey val id: String,
    val templateId: String,
    val dayNumber: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String,
    val explanation: String?,
    val difficultyLevel: String,
    val points: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

@Entity(tableName = "organization_parent_journey_mcqs")
data class OrganizationParentJourneyMcqEntity(
    @PrimaryKey val id: String,
    val templateId: String,
    val dayNumber: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String,
    val explanation: String?,
    val difficultyLevel: String,
    val points: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)

@Entity(tableName = "user_journeys")
data class UserJourneyEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val globalTemplateId: String?,
    val organizationTemplateId: String?,
    val organizationParentTemplateId: String?,
    val startDate: String,
    val currentDay: Int,
    val notificationTime: String?,
    val status: String,
    val totalScore: Int,
    val currentStreak: Int,
    val bestStreak: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "user_journey_task_progress")
data class UserJourneyTaskProgressEntity(
    @PrimaryKey val id: String,
    val userJourneyId: String,
    val dayNumber: Int,
    val taskId: String,
    val isCompleted: Boolean,
    val completedAt: String?,
    val isSynced: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "user_journey_mcq_progress")
data class UserJourneyMcqProgressEntity(
    @PrimaryKey val id: String,
    val userJourneyId: String,
    val dayNumber: Int,
    val mcqId: String,
    val selectedOption: String,
    val isCorrect: Boolean,
    val scoreEarned: Int,
    val answeredAt: String,
    val isSynced: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)
