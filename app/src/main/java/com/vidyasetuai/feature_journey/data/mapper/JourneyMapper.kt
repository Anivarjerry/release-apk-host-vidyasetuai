package com.vidyasetuai.feature_journey.data.mapper

import com.vidyasetuai.feature_journey.data.local.entity.*
import com.vidyasetuai.feature_journey.data.remote.dto.*
import com.vidyasetuai.feature_journey.domain.model.*

// --- Templates DTO -> Entity ---

fun GlobalJourneyTemplateDto.toEntity() = GlobalJourneyTemplateEntity(
    id = id, title = title, description = description, durationDays = duration_days,
    category = category, version = version, isActive = is_active, isDeleted = is_deleted,
    createdAt = created_at, updatedAt = updated_at, createdBy = created_by, updatedBy = updated_by
)

fun OrganizationJourneyTemplateDto.toEntity() = OrganizationJourneyTemplateEntity(
    id = id, organizationId = organization_id, title = title, description = description, durationDays = duration_days,
    category = category, version = version, isActive = is_active, isDeleted = is_deleted,
    createdAt = created_at, updatedAt = updated_at, createdBy = created_by, updatedBy = updated_by
)

fun OrganizationParentJourneyTemplateDto.toEntity() = OrganizationParentJourneyTemplateEntity(
    id = id, parentOrganizationId = parent_organization_id, title = title, description = description, durationDays = duration_days,
    category = category, version = version, isActive = is_active, isDeleted = is_deleted,
    createdAt = created_at, updatedAt = updated_at, createdBy = created_by, updatedBy = updated_by
)

// --- Templates Entity -> Domain ---

fun GlobalJourneyTemplateEntity.toDomain() = JourneyTemplate(
    id = id, title = title, description = description, durationDays = durationDays,
    category = category, version = version, ownerType = JourneyOwnerType.GLOBAL, ownerId = null
)

fun OrganizationJourneyTemplateEntity.toDomain() = JourneyTemplate(
    id = id, title = title, description = description, durationDays = durationDays,
    category = category, version = version, ownerType = JourneyOwnerType.ORGANIZATION, ownerId = organizationId
)

fun OrganizationParentJourneyTemplateEntity.toDomain() = JourneyTemplate(
    id = id, title = title, description = description, durationDays = durationDays,
    category = category, version = version, ownerType = JourneyOwnerType.PARENT_ORGANIZATION, ownerId = parentOrganizationId
)

// --- Tasks DTO -> Entity ---

fun GlobalJourneyTaskDto.toEntity() = GlobalJourneyTaskEntity(
    id = id, templateId = template_id, dayNumber = day_number, taskTitle = task_title,
    taskDescription = task_description, taskType = task_type, sortOrder = sort_order,
    isActive = is_active, isDeleted = is_deleted, createdAt = created_at, updatedAt = updated_at,
    createdBy = created_by, updatedBy = updated_by
)

fun OrganizationJourneyTaskDto.toEntity() = OrganizationJourneyTaskEntity(
    id = id, templateId = template_id, dayNumber = day_number, taskTitle = task_title,
    taskDescription = task_description, taskType = task_type, sortOrder = sort_order,
    isActive = is_active, isDeleted = is_deleted, createdAt = created_at, updatedAt = updated_at,
    createdBy = created_by, updatedBy = updated_by
)

fun OrganizationParentJourneyTaskDto.toEntity() = OrganizationParentJourneyTaskEntity(
    id = id, templateId = template_id, dayNumber = day_number, taskTitle = task_title,
    taskDescription = task_description, taskType = task_type, sortOrder = sort_order,
    isActive = is_active, isDeleted = is_deleted, createdAt = created_at, updatedAt = updated_at,
    createdBy = created_by, updatedBy = updated_by
)

// --- Tasks Entity -> Domain ---

fun GlobalJourneyTaskEntity.toDomain() = JourneyTask(
    id = id, templateId = templateId, dayNumber = dayNumber, taskTitle = taskTitle,
    taskDescription = taskDescription, taskType = taskType, sortOrder = sortOrder
)

fun OrganizationJourneyTaskEntity.toDomain() = JourneyTask(
    id = id, templateId = templateId, dayNumber = dayNumber, taskTitle = taskTitle,
    taskDescription = taskDescription, taskType = taskType, sortOrder = sortOrder
)

fun OrganizationParentJourneyTaskEntity.toDomain() = JourneyTask(
    id = id, templateId = templateId, dayNumber = dayNumber, taskTitle = taskTitle,
    taskDescription = taskDescription, taskType = taskType, sortOrder = sortOrder
)

// --- MCQs DTO -> Entity ---

fun GlobalJourneyMcqDto.toEntity() = GlobalJourneyMcqEntity(
    id = id, templateId = template_id, dayNumber = day_number, questionText = question_text,
    optionA = option_a, optionB = option_b, optionC = option_c, optionD = option_d,
    correctOption = correct_option, explanation = explanation, difficultyLevel = difficulty_level,
    points = points, isActive = is_active, isDeleted = is_deleted, createdAt = created_at,
    updatedAt = updated_at, createdBy = created_by, updatedBy = updated_by
)

fun OrganizationJourneyMcqDto.toEntity() = OrganizationJourneyMcqEntity(
    id = id, templateId = template_id, dayNumber = day_number, questionText = question_text,
    optionA = option_a, optionB = option_b, optionC = option_c, optionD = option_d,
    correctOption = correct_option, explanation = explanation, difficultyLevel = difficulty_level,
    points = points, isActive = is_active, isDeleted = is_deleted, createdAt = created_at,
    updatedAt = updated_at, createdBy = created_by, updatedBy = updated_by
)

fun OrganizationParentJourneyMcqDto.toEntity() = OrganizationParentJourneyMcqEntity(
    id = id, templateId = template_id, dayNumber = day_number, questionText = question_text,
    optionA = option_a, optionB = option_b, optionC = option_c, optionD = option_d,
    correctOption = correct_option, explanation = explanation, difficultyLevel = difficulty_level,
    points = points, isActive = is_active, isDeleted = is_deleted, createdAt = created_at,
    updatedAt = updated_at, createdBy = created_by, updatedBy = updated_by
)

// --- MCQs Entity -> Domain ---

fun GlobalJourneyMcqEntity.toDomain() = JourneyMcq(
    id = id, templateId = templateId, dayNumber = dayNumber, questionText = questionText,
    optionA = optionA, optionB = optionB, optionC = optionC, optionD = optionD,
    correctOption = correctOption, explanation = explanation, difficultyLevel = difficultyLevel, points = points
)

fun OrganizationJourneyMcqEntity.toDomain() = JourneyMcq(
    id = id, templateId = templateId, dayNumber = dayNumber, questionText = questionText,
    optionA = optionA, optionB = optionB, optionC = optionC, optionD = optionD,
    correctOption = correctOption, explanation = explanation, difficultyLevel = difficultyLevel, points = points
)

fun OrganizationParentJourneyMcqEntity.toDomain() = JourneyMcq(
    id = id, templateId = templateId, dayNumber = dayNumber, questionText = questionText,
    optionA = optionA, optionB = optionB, optionC = optionC, optionD = optionD,
    correctOption = correctOption, explanation = explanation, difficultyLevel = difficultyLevel, points = points
)

// --- User Journey Mappers ---

fun UserJourneyDto.toEntity() = UserJourneyEntity(
    id = id, userId = user_id, globalTemplateId = global_template_id,
    organizationTemplateId = organization_template_id, organizationParentTemplateId = organization_parent_template_id,
    startDate = start_date, currentDay = current_day, notificationTime = notification_time,
    status = status, totalScore = total_score, currentStreak = current_streak, bestStreak = best_streak,
    isActive = is_active, isDeleted = is_deleted, createdAt = created_at, updatedAt = updated_at
)

fun UserJourneyEntity.toDomain(): UserJourney {
    val templateId = globalTemplateId ?: organizationTemplateId ?: organizationParentTemplateId ?: ""
    val ownerType = when {
        globalTemplateId != null -> JourneyOwnerType.GLOBAL
        organizationTemplateId != null -> JourneyOwnerType.ORGANIZATION
        else -> JourneyOwnerType.PARENT_ORGANIZATION
    }
    return UserJourney(
        id = id, userId = userId, templateId = templateId, templateType = ownerType,
        startDate = startDate, currentDay = currentDay, notificationTime = notificationTime,
        status = status, totalScore = totalScore, currentStreak = currentStreak, bestStreak = bestStreak
    )
}

fun UserJourney.toDto(createdAt: String, updatedAt: String): UserJourneyDto {
    return UserJourneyDto(
        id = id, user_id = userId,
        global_template_id = if (templateType == JourneyOwnerType.GLOBAL) templateId else null,
        organization_template_id = if (templateType == JourneyOwnerType.ORGANIZATION) templateId else null,
        organization_parent_template_id = if (templateType == JourneyOwnerType.PARENT_ORGANIZATION) templateId else null,
        start_date = startDate, current_day = currentDay, notification_time = notificationTime,
        status = status, total_score = totalScore, current_streak = currentStreak, best_streak = bestStreak,
        is_active = true, is_deleted = false, created_at = createdAt, updated_at = updatedAt
    )
}

// --- Progress Mappers ---

fun UserJourneyTaskProgressEntity.toDomain() = JourneyTaskProgress(
    id = id, userJourneyId = userJourneyId, dayNumber = dayNumber, taskId = taskId,
    isCompleted = isCompleted, completedAt = completedAt
)

fun JourneyTaskProgress.toEntity(isSynced: Boolean, createdAt: String, updatedAt: String) = UserJourneyTaskProgressEntity(
    id = id, userJourneyId = userJourneyId, dayNumber = dayNumber, taskId = taskId,
    isCompleted = isCompleted, completedAt = completedAt, isSynced = isSynced, isDeleted = false,
    createdAt = createdAt, updatedAt = updatedAt
)

fun UserJourneyTaskProgressEntity.toDto() = UserJourneyTaskProgressDto(
    id = id, user_journey_id = userJourneyId, day_number = dayNumber, task_id = taskId,
    is_completed = isCompleted, completed_at = completedAt, is_deleted = isDeleted,
    created_at = createdAt, updated_at = updatedAt
)

fun UserJourneyMcqProgressEntity.toDomain() = JourneyMcqProgress(
    id = id, userJourneyId = userJourneyId, dayNumber = dayNumber, mcqId = mcqId,
    selectedOption = selectedOption, isCorrect = isCorrect, scoreEarned = scoreEarned, answeredAt = answeredAt
)

fun JourneyMcqProgress.toEntity(isSynced: Boolean, createdAt: String, updatedAt: String) = UserJourneyMcqProgressEntity(
    id = id, userJourneyId = userJourneyId, dayNumber = dayNumber, mcqId = mcqId,
    selectedOption = selectedOption, isCorrect = isCorrect, scoreEarned = scoreEarned, answeredAt = answeredAt,
    isSynced = isSynced, isDeleted = false, createdAt = createdAt, updatedAt = updatedAt
)

fun UserJourneyMcqProgressEntity.toDto() = UserJourneyMcqProgressDto(
    id = id, user_journey_id = userJourneyId, day_number = dayNumber, mcq_id = mcqId,
    selected_option = selectedOption, is_correct = isCorrect, score_earned = scoreEarned,
    answered_at = answeredAt, is_deleted = isDeleted, created_at = createdAt, updated_at = updatedAt
)

// --- Leaderboard Mapper ---

fun LeaderboardEntryDto.toDomain() = LeaderboardEntry(
    userId = user_id, username = username, fullName = full_name,
    profilePictureUrl = profile_picture_url, totalScore = total_score, currentStreak = current_streak
)