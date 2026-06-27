package com.vidyasetuai.feature_journey.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_journey.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneyDao {

    // --- Templates Insert / Upsert ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalTemplates(templates: List<GlobalJourneyTemplateEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrgTemplates(templates: List<OrganizationJourneyTemplateEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentOrgTemplates(templates: List<OrganizationParentJourneyTemplateEntity>)

    // --- Tasks Insert / Upsert ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalTasks(tasks: List<GlobalJourneyTaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrgTasks(tasks: List<OrganizationJourneyTaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentOrgTasks(tasks: List<OrganizationParentJourneyTaskEntity>)

    // --- MCQs Insert / Upsert ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalMcqs(mcqs: List<GlobalJourneyMcqEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrgMcqs(mcqs: List<OrganizationJourneyMcqEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentOrgMcqs(mcqs: List<OrganizationParentJourneyMcqEntity>)

    // --- Template Query ---
    @Query("SELECT * FROM global_journey_templates WHERE isActive = 1 AND isDeleted = 0")
    fun getGlobalTemplates(): Flow<List<GlobalJourneyTemplateEntity>>

    @Query("SELECT * FROM organization_journey_templates WHERE organizationId = :orgId AND isActive = 1 AND isDeleted = 0")
    fun getOrgTemplates(orgId: String): Flow<List<OrganizationJourneyTemplateEntity>>

    @Query("SELECT * FROM organization_parent_journey_templates WHERE parentOrganizationId = :parentOrgId AND isActive = 1 AND isDeleted = 0")
    fun getParentOrgTemplates(parentOrgId: String): Flow<List<OrganizationParentJourneyTemplateEntity>>

    // --- Tasks Query ---
    @Query("SELECT * FROM global_journey_tasks WHERE templateId = :templateId AND dayNumber = :dayNumber AND isActive = 1 AND isDeleted = 0 ORDER BY sortOrder ASC")
    suspend fun getGlobalTasksForDay(templateId: String, dayNumber: Int): List<GlobalJourneyTaskEntity>

    @Query("SELECT * FROM organization_journey_tasks WHERE templateId = :templateId AND dayNumber = :dayNumber AND isActive = 1 AND isDeleted = 0 ORDER BY sortOrder ASC")
    suspend fun getOrgTasksForDay(templateId: String, dayNumber: Int): List<OrganizationJourneyTaskEntity>

    @Query("SELECT * FROM organization_parent_journey_tasks WHERE templateId = :templateId AND dayNumber = :dayNumber AND isActive = 1 AND isDeleted = 0 ORDER BY sortOrder ASC")
    suspend fun getParentOrgTasksForDay(templateId: String, dayNumber: Int): List<OrganizationParentJourneyTaskEntity>

    // --- MCQs Query ---
    @Query("SELECT * FROM global_journey_mcqs WHERE templateId = :templateId AND dayNumber = :dayNumber AND isActive = 1 AND isDeleted = 0")
    suspend fun getGlobalMcqsForDay(templateId: String, dayNumber: Int): List<GlobalJourneyMcqEntity>

    @Query("SELECT * FROM organization_journey_mcqs WHERE templateId = :templateId AND dayNumber = :dayNumber AND isActive = 1 AND isDeleted = 0")
    suspend fun getOrgMcqsForDay(templateId: String, dayNumber: Int): List<OrganizationJourneyMcqEntity>

    @Query("SELECT * FROM organization_parent_journey_mcqs WHERE templateId = :templateId AND dayNumber = :dayNumber AND isActive = 1 AND isDeleted = 0")
    suspend fun getParentOrgMcqsForDay(templateId: String, dayNumber: Int): List<OrganizationParentJourneyMcqEntity>

    // --- User Journeys ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserJourney(journey: UserJourneyEntity)

    @Query("SELECT * FROM user_journeys WHERE userId = :userId AND status = 'active' AND isDeleted = 0 LIMIT 1")
    fun getActiveUserJourney(userId: String): Flow<UserJourneyEntity?>

    @Query("SELECT * FROM user_journeys WHERE userId = :userId AND isDeleted = 0")
    fun getAllUserJourneys(userId: String): Flow<List<UserJourneyEntity>>

    @Query("UPDATE user_journeys SET currentDay = :dayNumber WHERE id = :journeyId")
    suspend fun updateUserJourneyDay(journeyId: String, dayNumber: Int)

    @Query("UPDATE user_journeys SET totalScore = :score, currentStreak = :streak, bestStreak = :bestStreak WHERE id = :journeyId")
    suspend fun updateUserJourneyScoreAndStreak(journeyId: String, score: Int, streak: Int, bestStreak: Int)

    @Query("UPDATE user_journeys SET status = :status WHERE id = :journeyId")
    suspend fun updateUserJourneyStatus(journeyId: String, status: String)

    // --- Task Progress ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskProgress(progress: UserJourneyTaskProgressEntity)

    @Query("SELECT * FROM user_journey_task_progress WHERE userJourneyId = :journeyId AND dayNumber = :dayNumber AND isDeleted = 0")
    suspend fun getTaskProgressForDay(journeyId: String, dayNumber: Int): List<UserJourneyTaskProgressEntity>

    @Query("SELECT * FROM user_journey_task_progress WHERE isSynced = 0")
    suspend fun getUnsyncedTaskProgress(): List<UserJourneyTaskProgressEntity>

    @Query("UPDATE user_journey_task_progress SET isSynced = 1 WHERE id = :progressId")
    suspend fun markTaskProgressSynced(progressId: String)

    // --- MCQ Progress ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMcqProgress(progress: UserJourneyMcqProgressEntity)

    @Query("SELECT * FROM user_journey_mcq_progress WHERE userJourneyId = :journeyId AND dayNumber = :dayNumber AND isDeleted = 0")
    suspend fun getMcqProgressForDay(journeyId: String, dayNumber: Int): List<UserJourneyMcqProgressEntity>

    @Query("SELECT * FROM user_journey_mcq_progress WHERE isSynced = 0")
    suspend fun getUnsyncedMcqProgress(): List<UserJourneyMcqProgressEntity>

    @Query("UPDATE user_journey_mcq_progress SET isSynced = 1 WHERE id = :progressId")
    suspend fun markMcqProgressSynced(progressId: String)

    @Query("SELECT COUNT(*) FROM user_journey_mcq_progress WHERE userJourneyId = :journeyId AND isCorrect = 1 AND isDeleted = 0")
    suspend fun getCorrectMcqCount(journeyId: String): Int
}