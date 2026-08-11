package com.vidyasetuai.feature_case_study.data.remote.datasource

import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_case_study.data.remote.dto.QuickDto
import com.vidyasetuai.feature_case_study.data.remote.dto.QuickHelpfulDto
import com.vidyasetuai.feature_case_study.data.remote.dto.QuickViewDto
import com.vidyasetuai.feature_profile.data.remote.dto.UserProfileDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

class QuickRemoteDataSource {

    suspend fun getQuicks(): List<QuickDto> {
        return SupabaseClient.client.from("quicks")
            .select(columns = Columns.raw("*")) {
                order("created_at", order = Order.DESCENDING)
            }.decodeList()
    }

    suspend fun getAuthorProfiles(userIds: List<String>): List<UserProfileDto> {
        if (userIds.isEmpty()) return emptyList()
        return SupabaseClient.client.from("user_profiles")
            .select(columns = Columns.raw("user_id, username, first_name, last_name, full_name, profile_picture_url, cover_photo_url, bio, preferred_language, is_verified")) {
                filter {
                    isIn("user_id", userIds)
                }
            }.decodeList()
    }

    suspend fun getUserHelpfuls(userId: String): List<QuickHelpfulDto> {
        return SupabaseClient.client.from("quick_helpfuls")
            .select(columns = Columns.raw("id, quick_id, user_id, created_at")) {
                filter {
                    eq("user_id", userId)
                }
            }.decodeList()
    }

    suspend fun addHelpful(quickId: String, userId: String) {
        SupabaseClient.client.from("quick_helpfuls").insert(
            mapOf(
                "quick_id" to quickId,
                "user_id" to userId
            )
        )
    }

    suspend fun removeHelpful(quickId: String, userId: String) {
        SupabaseClient.client.from("quick_helpfuls").delete {
            filter {
                eq("quick_id", quickId)
                eq("user_id", userId)
            }
        }
    }

    suspend fun createQuick(title: String, description: String, coverImageUrl: String?, authorUserId: String) {
        SupabaseClient.client.from("quicks").insert(
            mapOf(
                "title" to title,
                "description" to description,
                "cover_image_url" to coverImageUrl,
                "author_user_id" to authorUserId,
                "status" to "published"
            )
        )
    }

    suspend fun incrementQuickViews(quickId: String, userId: String) {
        try {
            SupabaseClient.client.from("quick_views").insert(
                mapOf(
                    "quick_id" to quickId,
                    "user_id" to userId
                )
            )
        } catch (e: Exception) {
            // Ignore if already viewed to prevent duplicate views count constraint issues
        }
    }

    suspend fun getQuickViewers(quickId: String): List<UserProfileDto> {
        val views = SupabaseClient.client.from("quick_views")
            .select(columns = Columns.raw("id, quick_id, user_id, created_at")) {
                filter {
                    eq("quick_id", quickId)
                }
            }.decodeList<QuickViewDto>()
        val userIds = views.map { it.user_id }.distinct()
        return getAuthorProfiles(userIds)
    }

    suspend fun getQuickHelpfulUsers(quickId: String): List<UserProfileDto> {
        val helpfuls = SupabaseClient.client.from("quick_helpfuls")
            .select(columns = Columns.raw("id, quick_id, user_id, created_at")) {
                filter {
                    eq("quick_id", quickId)
                }
            }.decodeList<QuickHelpfulDto>()
        val userIds = helpfuls.map { it.user_id }.distinct()
        return getAuthorProfiles(userIds)
    }

    suspend fun deleteQuick(quickId: String) {
        SupabaseClient.client.from("quicks").delete {
            filter {
                eq("id", quickId)
            }
        }
    }
}
