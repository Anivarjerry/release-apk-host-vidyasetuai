package com.vidyasetuai.feature_journey.data.remote.datasource

import android.util.Log
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_journey.data.remote.dto.*
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable

class JourneyRemoteDataSource {

    private val tag = "VidyaSetu_JourneyRemote"

    suspend fun getGlobalTemplates(): List<GlobalJourneyTemplateDto> {
        return try {
            SupabaseClient.client.from("global_journey_templates")
                .select(columns = Columns.raw("*")) {
                    filter {
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList()
        } catch (e: Exception) {
            Log.e(tag, "Error fetching global templates: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getOrgTemplates(orgId: String): List<OrganizationJourneyTemplateDto> {
        return try {
            SupabaseClient.client.from("organization_journey_templates")
                .select(columns = Columns.raw("*")) {
                    filter {
                        eq("organization_id", orgId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList()
        } catch (e: Exception) {
            Log.e(tag, "Error fetching org templates for $orgId: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getParentOrgTemplates(parentOrgId: String): List<OrganizationParentJourneyTemplateDto> {
        return try {
            SupabaseClient.client.from("organization_parent_journey_templates")
                .select(columns = Columns.raw("*")) {
                    filter {
                        eq("parent_organization_id", parentOrgId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList()
        } catch (e: Exception) {
            Log.e(tag, "Error fetching parent org templates for $parentOrgId: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getGlobalTasks(templateId: String): List<GlobalJourneyTaskDto> {
        return try {
            SupabaseClient.client.from("global_journey_tasks")
                .select(columns = Columns.raw("*")) {
                    filter {
                        eq("template_id", templateId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList()
        } catch (e: Exception) {
            Log.e(tag, "Error fetching global tasks for template $templateId: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getOrgTasks(templateId: String): List<OrganizationJourneyTaskDto> {
        return try {
            SupabaseClient.client.from("organization_journey_tasks")
                .select(columns = Columns.raw("*")) {
                    filter {
                        eq("template_id", templateId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList()
        } catch (e: Exception) {
            Log.e(tag, "Error fetching org tasks for template $templateId: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getParentOrgTasks(templateId: String): List<OrganizationParentJourneyTaskDto> {
        return try {
            SupabaseClient.client.from("organization_parent_journey_tasks")
                .select(columns = Columns.raw("*")) {
                    filter {
                        eq("template_id", templateId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList()
        } catch (e: Exception) {
            Log.e(tag, "Error fetching parent org tasks for template $templateId: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getGlobalMcqs(templateId: String): List<GlobalJourneyMcqDto> {
        return try {
            SupabaseClient.client.from("global_journey_mcqs")
                .select(columns = Columns.raw("*")) {
                    filter {
                        eq("template_id", templateId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList()
        } catch (e: Exception) {
            Log.e(tag, "Error fetching global mcqs for template $templateId: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getOrgMcqs(templateId: String): List<OrganizationJourneyMcqDto> {
        return try {
            SupabaseClient.client.from("organization_journey_mcqs")
                .select(columns = Columns.raw("*")) {
                    filter {
                        eq("template_id", templateId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList()
        } catch (e: Exception) {
            Log.e(tag, "Error fetching org mcqs for template $templateId: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getParentOrgMcqs(templateId: String): List<OrganizationParentJourneyMcqDto> {
        return try {
            SupabaseClient.client.from("organization_parent_journey_mcqs")
                .select(columns = Columns.raw("*")) {
                    filter {
                        eq("template_id", templateId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList()
        } catch (e: Exception) {
            Log.e(tag, "Error fetching parent org mcqs for template $templateId: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getActiveUserJourney(userId: String): UserJourneyDto? {
        return try {
            val list = SupabaseClient.client.from("user_journeys")
                .select(columns = Columns.raw("*")) {
                    filter {
                        eq("user_id", userId)
                        eq("status", "active")
                        eq("is_deleted", false)
                    }
                }.decodeList<UserJourneyDto>()
            list.firstOrNull()
        } catch (e: Exception) {
            Log.e(tag, "Error fetching active user journey for $userId: ${e.message}", e)
            null
        }
    }

    suspend fun insertUserJourney(journey: UserJourneyDto): UserJourneyDto? {
        return try {
            val inserted = SupabaseClient.client.from("user_journeys")
                .insert(journey) {
                    select()
                }.decodeSingle<UserJourneyDto>()
            inserted
        } catch (e: Exception) {
            Log.e(tag, "Error inserting user journey: ${e.message}", e)
            null
        }
    }

    suspend fun syncTaskProgress(progressList: List<UserJourneyTaskProgressDto>): Boolean {
        if (progressList.isEmpty()) return true
        return try {
            SupabaseClient.client.from("user_journey_task_progress").upsert(progressList)
            true
        } catch (e: Exception) {
            Log.e(tag, "Error syncing task progress list: ${e.message}", e)
            false
        }
    }

    suspend fun syncMcqProgress(progressList: List<UserJourneyMcqProgressDto>): Boolean {
        if (progressList.isEmpty()) return true
        return try {
            SupabaseClient.client.from("user_journey_mcq_progress").upsert(progressList)
            true
        } catch (e: Exception) {
            Log.e(tag, "Error syncing mcq progress list: ${e.message}", e)
            false
        }
    }

    suspend fun updateUserJourneyScoreAndStreak(
        journeyId: String,
        totalScore: Int,
        currentStreak: Int,
        bestStreak: Int,
        currentDay: Int
    ): Boolean {
        return try {
            SupabaseClient.client.from("user_journeys").update({
                set("total_score", totalScore)
                set("current_streak", currentStreak)
                set("best_streak", bestStreak)
                set("current_day", currentDay)
                set("updated_at", java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(java.util.Date()))
            }) {
                filter {
                    eq("id", journeyId)
                }
            }
            true
        } catch (e: Exception) {
            Log.e(tag, "Error updating user journey progress stats: ${e.message}", e)
            false
        }
    }

    @Serializable
    private data class LeaderboardUserInfoDto(
        val username: String? = null,
        val full_name: String? = null,
        val profile_picture_url: String? = null
    )

    @Serializable
    private data class LeaderboardResponseDto(
        val user_id: String,
        val total_score: Int,
        val current_streak: Int,
        val user_profiles: LeaderboardUserInfoDto? = null
    )

    suspend fun getLeaderboard(
        globalTemplateId: String?,
        organizationTemplateId: String?,
        organizationParentTemplateId: String?
    ): List<LeaderboardEntryDto> {
        return try {
            val list = SupabaseClient.client.from("user_journeys")
                .select(columns = Columns.raw("user_id, total_score, current_streak, user_profiles(username, full_name, profile_picture_url)")) {
                    filter {
                        if (globalTemplateId != null) eq("global_template_id", globalTemplateId)
                        if (organizationTemplateId != null) eq("organization_template_id", organizationTemplateId)
                        if (organizationParentTemplateId != null) eq("organization_parent_template_id", organizationParentTemplateId)
                        eq("is_deleted", false)
                    }
                }.decodeList<LeaderboardResponseDto>()
            
            list.map {
                LeaderboardEntryDto(
                    user_id = it.user_id,
                    username = it.user_profiles?.username,
                    full_name = it.user_profiles?.full_name,
                    profile_picture_url = it.user_profiles?.profile_picture_url,
                    total_score = it.total_score,
                    current_streak = it.current_streak
                )
            }.sortedByDescending { it.total_score }
        } catch (e: Exception) {
            Log.e(tag, "Error fetching leaderboard: ${e.message}", e)
            emptyList()
        }
    }
}