package com.vidyasetuai.feature_journey.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GlobalJourneyTemplateDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val duration_days: Int,
    val category: String? = null,
    val version: Int = 1,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String,
    val updated_at: String,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
data class OrganizationJourneyTemplateDto(
    val id: String,
    val organization_id: String,
    val title: String,
    val description: String? = null,
    val duration_days: Int,
    val category: String? = null,
    val version: Int = 1,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String,
    val updated_at: String,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
data class OrganizationParentJourneyTemplateDto(
    val id: String,
    val parent_organization_id: String,
    val title: String,
    val description: String? = null,
    val duration_days: Int,
    val category: String? = null,
    val version: Int = 1,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String,
    val updated_at: String,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
data class GlobalJourneyTaskDto(
    val id: String,
    val template_id: String,
    val day_number: Int,
    val task_title: String,
    val task_description: String? = null,
    val task_type: String = "read",
    val sort_order: Int = 1,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String,
    val updated_at: String,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
data class OrganizationJourneyTaskDto(
    val id: String,
    val template_id: String,
    val day_number: Int,
    val task_title: String,
    val task_description: String? = null,
    val task_type: String = "read",
    val sort_order: Int = 1,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String,
    val updated_at: String,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
data class OrganizationParentJourneyTaskDto(
    val id: String,
    val template_id: String,
    val day_number: Int,
    val task_title: String,
    val task_description: String? = null,
    val task_type: String = "read",
    val sort_order: Int = 1,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String,
    val updated_at: String,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
data class GlobalJourneyMcqDto(
    val id: String,
    val template_id: String,
    val day_number: Int,
    val question_text: String,
    val option_a: String,
    val option_b: String,
    val option_c: String,
    val option_d: String,
    val correct_option: String,
    val explanation: String? = null,
    val difficulty_level: String = "medium",
    val points: Int = 1,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String,
    val updated_at: String,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
data class OrganizationJourneyMcqDto(
    val id: String,
    val template_id: String,
    val day_number: Int,
    val question_text: String,
    val option_a: String,
    val option_b: String,
    val option_c: String,
    val option_d: String,
    val correct_option: String,
    val explanation: String? = null,
    val difficulty_level: String = "medium",
    val points: Int = 1,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String,
    val updated_at: String,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
data class OrganizationParentJourneyMcqDto(
    val id: String,
    val template_id: String,
    val day_number: Int,
    val question_text: String,
    val option_a: String,
    val option_b: String,
    val option_c: String,
    val option_d: String,
    val correct_option: String,
    val explanation: String? = null,
    val difficulty_level: String = "medium",
    val points: Int = 1,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String,
    val updated_at: String,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
data class UserJourneyDto(
    val id: String,
    val user_id: String,
    val global_template_id: String? = null,
    val organization_template_id: String? = null,
    val organization_parent_template_id: String? = null,
    val start_date: String,
    val current_day: Int = 1,
    val notification_time: String? = null,
    val status: String = "active",
    val total_score: Int = 0,
    val current_streak: Int = 0,
    val best_streak: Int = 0,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class UserJourneyTaskProgressDto(
    val id: String,
    val user_journey_id: String,
    val day_number: Int,
    val task_id: String,
    val is_completed: Boolean = false,
    val completed_at: String? = null,
    val is_deleted: Boolean = false,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class UserJourneyMcqProgressDto(
    val id: String,
    val user_journey_id: String,
    val day_number: Int,
    val mcq_id: String,
    val selected_option: String,
    val is_correct: Boolean,
    val score_earned: Int = 0,
    val answered_at: String,
    val is_deleted: Boolean = false,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class LeaderboardEntryDto(
    val user_id: String,
    val username: String? = null,
    val full_name: String? = null,
    val profile_picture_url: String? = null,
    val total_score: Int,
    val current_streak: Int
)