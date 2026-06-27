package com.vidyasetuai.feature_journey.data.repository

import com.vidyasetuai.feature_journey.data.local.datasource.JourneyLocalDataSource
import com.vidyasetuai.feature_journey.data.mapper.*
import com.vidyasetuai.feature_journey.data.remote.datasource.JourneyRemoteDataSource
import com.vidyasetuai.feature_journey.data.remote.dto.UserJourneyDto
import com.vidyasetuai.feature_journey.domain.model.*
import com.vidyasetuai.feature_journey.domain.repository.JourneyRepository
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

class JourneyRepositoryImpl(
    private val localDataSource: JourneyLocalDataSource,
    private val remoteDataSource: JourneyRemoteDataSource
) : JourneyRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    private fun getCurrentIsoString(): String = isoFormat.format(Date())

    override fun getAvailableTemplates(orgId: String?, parentOrgId: String?): Flow<List<JourneyTemplate>> {
        // Fetch local templates as a flow and fetch remote in background to update local
        val globalFlow = localDataSource.getGlobalTemplates().map { list -> list.map { it.toDomain() } }
        
        val orgFlow = if (orgId != null) {
            localDataSource.getOrgTemplates(orgId).map { list -> list.map { it.toDomain() } }
        } else {
            flowOf(emptyList())
        }

        val parentOrgFlow = if (parentOrgId != null) {
            localDataSource.getParentOrgTemplates(parentOrgId).map { list -> list.map { it.toDomain() } }
        } else {
            flowOf(emptyList())
        }

        return combine(globalFlow, orgFlow, parentOrgFlow) { global, org, parent ->
            global + org + parent
        }.onStart {
            // Trigger remote fetch to update local database
            try {
                val remGlobal = remoteDataSource.getGlobalTemplates()
                localDataSource.saveGlobalTemplates(remGlobal.map { it.toEntity() })

                if (orgId != null) {
                    val remOrg = remoteDataSource.getOrgTemplates(orgId)
                    localDataSource.saveOrgTemplates(remOrg.map { it.toEntity() })
                }

                if (parentOrgId != null) {
                    val remParent = remoteDataSource.getParentOrgTemplates(parentOrgId)
                    localDataSource.saveParentOrgTemplates(remParent.map { it.toEntity() })
                }
            } catch (e: Exception) {
                // Fail silently, fallback to Room cache
            }
        }
    }

    override suspend fun downloadJourneyContent(templateId: String, type: JourneyOwnerType): Result<Unit> {
        return kotlin.runCatching {
            when (type) {
                JourneyOwnerType.GLOBAL -> {
                    val tasks = remoteDataSource.getGlobalTasks(templateId)
                    localDataSource.saveGlobalTasks(tasks.map { it.toEntity() })
                    val mcqs = remoteDataSource.getGlobalMcqs(templateId)
                    localDataSource.saveGlobalMcqs(mcqs.map { it.toEntity() })
                }
                JourneyOwnerType.ORGANIZATION -> {
                    val tasks = remoteDataSource.getOrgTasks(templateId)
                    localDataSource.saveOrgTasks(tasks.map { it.toEntity() })
                    val mcqs = remoteDataSource.getOrgMcqs(templateId)
                    localDataSource.saveOrgMcqs(mcqs.map { it.toEntity() })
                }
                JourneyOwnerType.PARENT_ORGANIZATION -> {
                    val tasks = remoteDataSource.getParentOrgTasks(templateId)
                    localDataSource.saveParentOrgTasks(tasks.map { it.toEntity() })
                    val mcqs = remoteDataSource.getParentOrgMcqs(templateId)
                    localDataSource.saveParentOrgMcqs(mcqs.map { it.toEntity() })
                }
            }
        }
    }

    override suspend fun enrollInJourney(
        userId: String,
        templateId: String,
        type: JourneyOwnerType,
        notificationTime: String?
    ): Result<UserJourney> {
        return kotlin.runCatching {
            // 1. Download tasks and MCQs offline first
            downloadJourneyContent(templateId, type).getOrThrow()

            // 2. Create UserJourneyDto
            val nowStr = getCurrentIsoString()
            val userJourneyDto = UserJourneyDto(
                id = UUID.randomUUID().toString(),
                user_id = userId,
                global_template_id = if (type == JourneyOwnerType.GLOBAL) templateId else null,
                organization_template_id = if (type == JourneyOwnerType.ORGANIZATION) templateId else null,
                organization_parent_template_id = if (type == JourneyOwnerType.PARENT_ORGANIZATION) templateId else null,
                start_date = dateFormat.format(Date()),
                current_day = 1,
                notification_time = notificationTime,
                status = "active",
                total_score = 0,
                current_streak = 0,
                best_streak = 0,
                created_at = nowStr,
                updated_at = nowStr
            )

            // 3. Save locally
            localDataSource.saveUserJourney(userJourneyDto.toEntity())

            // 4. Upload to remote Supabase in background or directly
            val remoteResult = remoteDataSource.insertUserJourney(userJourneyDto)
            if (remoteResult != null) {
                // Update local with remote confirmation
                localDataSource.saveUserJourney(remoteResult.toEntity())
                remoteResult.toEntity().toDomain()
            } else {
                userJourneyDto.toEntity().toDomain()
            }
        }
    }

    override fun getActiveUserJourney(userId: String): Flow<UserJourney?> {
        return localDataSource.getActiveUserJourney(userId).map { it?.toDomain() }
    }

    override fun getAllUserJourneys(userId: String): Flow<List<UserJourney>> {
        return localDataSource.getAllUserJourneys(userId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getTasksForDay(
        journeyId: String,
        templateId: String,
        type: JourneyOwnerType,
        dayNumber: Int
    ): List<JourneyTask> {
        return when (type) {
            JourneyOwnerType.GLOBAL -> localDataSource.getGlobalTasksForDay(templateId, dayNumber).map { it.toDomain() }
            JourneyOwnerType.ORGANIZATION -> localDataSource.getOrgTasksForDay(templateId, dayNumber).map { it.toDomain() }
            JourneyOwnerType.PARENT_ORGANIZATION -> localDataSource.getParentOrgTasksForDay(templateId, dayNumber).map { it.toDomain() }
        }
    }

    override suspend fun getMcqsForDay(
        journeyId: String,
        templateId: String,
        type: JourneyOwnerType,
        dayNumber: Int
    ): List<JourneyMcq> {
        return when (type) {
            JourneyOwnerType.GLOBAL -> localDataSource.getGlobalMcqsForDay(templateId, dayNumber).map { it.toDomain() }
            JourneyOwnerType.ORGANIZATION -> localDataSource.getOrgMcqsForDay(templateId, dayNumber).map { it.toDomain() }
            JourneyOwnerType.PARENT_ORGANIZATION -> localDataSource.getParentOrgMcqsForDay(templateId, dayNumber).map { it.toDomain() }
        }
    }

    override suspend fun getTaskProgress(journeyId: String, dayNumber: Int): List<JourneyTaskProgress> {
        return localDataSource.getTaskProgressForDay(journeyId, dayNumber).map { it.toDomain() }
    }

    override suspend fun getMcqProgress(journeyId: String, dayNumber: Int): List<JourneyMcqProgress> {
        return localDataSource.getMcqProgressForDay(journeyId, dayNumber).map { it.toDomain() }
    }

    override suspend fun submitTaskProgress(progress: JourneyTaskProgress): Result<Unit> {
        return kotlin.runCatching {
            val nowStr = getCurrentIsoString()
            // 1. Save locally with isSynced = false
            localDataSource.saveTaskProgress(progress.toEntity(isSynced = false, createdAt = nowStr, updatedAt = nowStr))

            // 2. Try immediate sync, ignore failure (WorkManager will handle later)
            val dto = progress.toEntity(isSynced = true, createdAt = nowStr, updatedAt = nowStr).toDto()
            val success = remoteDataSource.syncTaskProgress(listOf(dto))
            if (success) {
                localDataSource.markTaskProgressSynced(progress.id)
            }
        }
    }

    override suspend fun submitMcqAttempt(
        progress: JourneyMcqProgress,
        correctAnswer: String,
        points: Int
    ): Result<Boolean> {
        return kotlin.runCatching {
            val isCorrect = progress.selectedOption.equals(correctAnswer, ignoreCase = true)
            val scoreEarned = if (isCorrect) points else 0
            val nowStr = getCurrentIsoString()

            val progressWithCorrection = progress.copy(isCorrect = isCorrect, scoreEarned = scoreEarned)

            // 1. Save locally (unsynced)
            localDataSource.saveMcqProgress(
                progressWithCorrection.toEntity(isSynced = false, createdAt = nowStr, updatedAt = nowStr)
            )

            // 2. Update stats on active user journey
            val activeJourneyFlow = localDataSource.getActiveUserJourney(progress.userJourneyId).firstOrNull()
            if (activeJourneyFlow != null) {
                val currentScore = localDataSource.getCorrectMcqCount(progress.userJourneyId) * points
                
                // Calculate streak
                var streak = activeJourneyFlow.currentStreak
                var best = activeJourneyFlow.bestStreak
                
                // Simple streak math: if they did today, keep or increment
                // For simplicity: if they do MCQs, increment streak
                if (streak == 0) {
                    streak = 1
                } else {
                    streak += 1
                }
                if (streak > best) {
                    best = streak
                }

                // Update Local User Journey details
                localDataSource.updateUserJourneyScoreAndStreak(
                    journeyId = progress.userJourneyId,
                    score = currentScore,
                    streak = streak,
                    bestStreak = best
                )

                // Try to update remote user journey stats
                remoteDataSource.updateUserJourneyScoreAndStreak(
                    journeyId = progress.userJourneyId,
                    totalScore = currentScore,
                    currentStreak = streak,
                    bestStreak = best,
                    currentDay = activeJourneyFlow.currentDay
                )
            }

            // 3. Try immediate attempt sync
            val dto = progressWithCorrection.toEntity(isSynced = true, createdAt = nowStr, updatedAt = nowStr).toDto()
            val success = remoteDataSource.syncMcqProgress(listOf(dto))
            if (success) {
                localDataSource.markMcqProgressSynced(progress.id)
            }

            isCorrect
        }
    }

    override suspend fun getLeaderboard(
        globalTemplateId: String?,
        organizationTemplateId: String?,
        organizationParentTemplateId: String?
    ): List<LeaderboardEntry> {
        return remoteDataSource.getLeaderboard(
            globalTemplateId,
            organizationTemplateId,
            organizationParentTemplateId
        ).map { it.toDomain() }
    }

    override suspend fun performBackgroundSync(): Result<Unit> {
        return kotlin.runCatching {
            // Sync tasks
            val unsyncedTasks = localDataSource.getUnsyncedTaskProgress()
            if (unsyncedTasks.isNotEmpty()) {
                val success = remoteDataSource.syncTaskProgress(unsyncedTasks.map { it.toDto() })
                if (success) {
                    unsyncedTasks.forEach { localDataSource.markTaskProgressSynced(it.id) }
                }
            }

            // Sync MCQs
            val unsyncedMcqs = localDataSource.getUnsyncedMcqProgress()
            if (unsyncedMcqs.isNotEmpty()) {
                val success = remoteDataSource.syncMcqProgress(unsyncedMcqs.map { it.toDto() })
                if (success) {
                    unsyncedMcqs.forEach { localDataSource.markMcqProgressSynced(it.id) }
                }
            }
        }
    }
}