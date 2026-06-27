package com.vidyasetuai.feature_journey.data.local.datasource

import com.vidyasetuai.feature_journey.data.local.dao.JourneyDao
import com.vidyasetuai.feature_journey.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class JourneyLocalDataSource(private val journeyDao: JourneyDao) {

    // --- Save content locally ---
    suspend fun saveGlobalTemplates(templates: List<GlobalJourneyTemplateEntity>) =
        journeyDao.insertGlobalTemplates(templates)

    suspend fun saveOrgTemplates(templates: List<OrganizationJourneyTemplateEntity>) =
        journeyDao.insertOrgTemplates(templates)

    suspend fun saveParentOrgTemplates(templates: List<OrganizationParentJourneyTemplateEntity>) =
        journeyDao.insertParentOrgTemplates(templates)

    suspend fun saveGlobalTasks(tasks: List<GlobalJourneyTaskEntity>) =
        journeyDao.insertGlobalTasks(tasks)

    suspend fun saveOrgTasks(tasks: List<OrganizationJourneyTaskEntity>) =
        journeyDao.insertOrgTasks(tasks)

    suspend fun saveParentOrgTasks(tasks: List<OrganizationParentJourneyTaskEntity>) =
        journeyDao.insertParentOrgTasks(tasks)

    suspend fun saveGlobalMcqs(mcqs: List<GlobalJourneyMcqEntity>) =
        journeyDao.insertGlobalMcqs(mcqs)

    suspend fun saveOrgMcqs(mcqs: List<OrganizationJourneyMcqEntity>) =
        journeyDao.insertOrgMcqs(mcqs)

    suspend fun saveParentOrgMcqs(mcqs: List<OrganizationParentJourneyMcqEntity>) =
        journeyDao.insertParentOrgMcqs(mcqs)

    // --- Read templates locally ---
    fun getGlobalTemplates(): Flow<List<GlobalJourneyTemplateEntity>> =
        journeyDao.getGlobalTemplates()

    fun getOrgTemplates(orgId: String): Flow<List<OrganizationJourneyTemplateEntity>> =
        journeyDao.getOrgTemplates(orgId)

    fun getParentOrgTemplates(parentOrgId: String): Flow<List<OrganizationParentJourneyTemplateEntity>> =
        journeyDao.getParentOrgTemplates(parentOrgId)

    // --- Read Tasks & MCQs locally ---
    suspend fun getGlobalTasksForDay(templateId: String, dayNumber: Int) =
        journeyDao.getGlobalTasksForDay(templateId, dayNumber)

    suspend fun getOrgTasksForDay(templateId: String, dayNumber: Int) =
        journeyDao.getOrgTasksForDay(templateId, dayNumber)

    suspend fun getParentOrgTasksForDay(templateId: String, dayNumber: Int) =
        journeyDao.getParentOrgTasksForDay(templateId, dayNumber)

    suspend fun getGlobalMcqsForDay(templateId: String, dayNumber: Int) =
        journeyDao.getGlobalMcqsForDay(templateId, dayNumber)

    suspend fun getOrgMcqsForDay(templateId: String, dayNumber: Int) =
        journeyDao.getOrgMcqsForDay(templateId, dayNumber)

    suspend fun getParentOrgMcqsForDay(templateId: String, dayNumber: Int) =
        journeyDao.getParentOrgMcqsForDay(templateId, dayNumber)

    // --- User Journey management ---
    suspend fun saveUserJourney(journey: UserJourneyEntity) =
        journeyDao.insertUserJourney(journey)

    fun getActiveUserJourney(userId: String): Flow<UserJourneyEntity?> =
        journeyDao.getActiveUserJourney(userId)

    fun getAllUserJourneys(userId: String): Flow<List<UserJourneyEntity>> =
        journeyDao.getAllUserJourneys(userId)

    suspend fun updateUserJourneyDay(journeyId: String, dayNumber: Int) =
        journeyDao.updateUserJourneyDay(journeyId, dayNumber)

    suspend fun updateUserJourneyScoreAndStreak(journeyId: String, score: Int, streak: Int, bestStreak: Int) =
        journeyDao.updateUserJourneyScoreAndStreak(journeyId, score, streak, bestStreak)

    suspend fun updateUserJourneyStatus(journeyId: String, status: String) =
        journeyDao.updateUserJourneyStatus(journeyId, status)

    // --- Progress Tracking ---
    suspend fun saveTaskProgress(progress: UserJourneyTaskProgressEntity) =
        journeyDao.insertTaskProgress(progress)

    suspend fun getTaskProgressForDay(journeyId: String, dayNumber: Int) =
        journeyDao.getTaskProgressForDay(journeyId, dayNumber)

    suspend fun getUnsyncedTaskProgress() =
        journeyDao.getUnsyncedTaskProgress()

    suspend fun markTaskProgressSynced(progressId: String) =
        journeyDao.markTaskProgressSynced(progressId)

    suspend fun saveMcqProgress(progress: UserJourneyMcqProgressEntity) =
        journeyDao.insertMcqProgress(progress)

    suspend fun getMcqProgressForDay(journeyId: String, dayNumber: Int) =
        journeyDao.getMcqProgressForDay(journeyId, dayNumber)

    suspend fun getUnsyncedMcqProgress() =
        journeyDao.getUnsyncedMcqProgress()

    suspend fun markMcqProgressSynced(progressId: String) =
        journeyDao.markMcqProgressSynced(progressId)

    suspend fun getCorrectMcqCount(journeyId: String) =
        journeyDao.getCorrectMcqCount(journeyId)
}