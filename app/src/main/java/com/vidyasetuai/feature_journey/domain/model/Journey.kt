package com.vidyasetuai.feature_journey.domain.model

data class JourneyTemplate(
    val id: String,
    val title: String,
    val description: String?,
    val durationDays: Int,
    val category: String?,
    val version: Int,
    val ownerType: JourneyOwnerType,
    val ownerId: String?
)

enum class JourneyOwnerType {
    GLOBAL,
    ORGANIZATION,
    PARENT_ORGANIZATION
}

data class JourneyTask(
    val id: String,
    val templateId: String,
    val dayNumber: Int,
    val taskTitle: String,
    val taskDescription: String?,
    val taskType: String,
    val sortOrder: Int
)

data class JourneyMcq(
    val id: String,
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
    val points: Int
)

data class UserJourney(
    val id: String,
    val userId: String,
    val templateId: String,
    val templateType: JourneyOwnerType,
    val startDate: String,
    val currentDay: Int,
    val notificationTime: String?,
    val status: String,
    val totalScore: Int,
    val currentStreak: Int,
    val bestStreak: Int
)

data class JourneyTaskProgress(
    val id: String,
    val userJourneyId: String,
    val dayNumber: Int,
    val taskId: String,
    val isCompleted: Boolean,
    val completedAt: String?
)

data class JourneyMcqProgress(
    val id: String,
    val userJourneyId: String,
    val dayNumber: Int,
    val mcqId: String,
    val selectedOption: String,
    val isCorrect: Boolean,
    val scoreEarned: Int,
    val answeredAt: String
)

data class LeaderboardEntry(
    val userId: String,
    val username: String?,
    val fullName: String?,
    val profilePictureUrl: String?,
    val totalScore: Int,
    val currentStreak: Int
)