package com.vidyasetuai.feature_journey.domain.repository

import com.vidyasetuai.feature_journey.domain.model.*
import kotlinx.coroutines.flow.Flow

interface JourneyRepository {

    // --- Template Operations ---
    fun getAvailableTemplates(orgId: String?, parentOrgId: String?): Flow<List<JourneyTemplate>>
    suspend fun downloadJourneyContent(templateId: String, type: JourneyOwnerType): Result<Unit>

    // --- Enrollment ---
    suspend fun enrollInJourney(
        userId: String,
        templateId: String,
        type: JourneyOwnerType,
        notificationTime: String?
    ): Result<UserJourney>

    fun getActiveUserJourney(userId: String): Flow<UserJourney?>
    fun getAllUserJourneys(userId: String): Flow<List<UserJourney>>

    // --- Progress & Offline Execution ---
    suspend fun getTasksForDay(journeyId: String, templateId: String, type: JourneyOwnerType, dayNumber: Int): List<JourneyTask>
    suspend fun getMcqsForDay(journeyId: String, templateId: String, type: JourneyOwnerType, dayNumber: Int): List<JourneyMcq>

    suspend fun getTaskProgress(journeyId: String, dayNumber: Int): List<JourneyTaskProgress>
    suspend fun getMcqProgress(journeyId: String, dayNumber: Int): List<JourneyMcqProgress>

    suspend fun submitTaskProgress(progress: JourneyTaskProgress): Result<Unit>
    suspend fun submitMcqAttempt(progress: JourneyMcqProgress, correctAnswer: String, points: Int): Result<Boolean>

    // --- Leaderboard & Sync ---
    suspend fun getLeaderboard(
        globalTemplateId: String?,
        organizationTemplateId: String?,
        organizationParentTemplateId: String?
    ): List<LeaderboardEntry>

    suspend fun performBackgroundSync(): Result<Unit>
}