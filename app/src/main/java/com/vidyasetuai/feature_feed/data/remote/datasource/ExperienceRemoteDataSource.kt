package com.vidyasetuai.feature_feed.data.remote.datasource

import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_feed.data.remote.dto.ExperienceDto
import com.vidyasetuai.feature_feed.data.remote.dto.ExperienceInspirationDto
import com.vidyasetuai.feature_profile.data.remote.dto.UserProfileDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

class ExperienceRemoteDataSource {

    suspend fun getExperiences(): List<ExperienceDto> {
        return SupabaseClient.client.from("experiences")
            .select(columns = Columns.raw("*")) {
                order("created_at", order = Order.DESCENDING)
            }.decodeList()
    }
    
    suspend fun getExperiencesByUser(authorUserId: String): List<ExperienceDto> {
        return SupabaseClient.client.from("experiences")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("author_user_id", authorUserId)
                }
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

    suspend fun getUserInspirations(userId: String): List<ExperienceInspirationDto> {
        return SupabaseClient.client.from("experience_inspirations")
            .select(columns = Columns.raw("id, experience_id, user_id, created_at")) {
                filter {
                    eq("user_id", userId)
                }
            }.decodeList()
    }

    suspend fun getInspirationsForExperience(experienceId: String): List<ExperienceInspirationDto> {
        return SupabaseClient.client.from("experience_inspirations")
            .select(columns = Columns.raw("id, experience_id, user_id, created_at")) {
                filter {
                    eq("experience_id", experienceId)
                }
            }.decodeList()
    }

    suspend fun addInspiration(experienceId: String, userId: String) {
        SupabaseClient.client.from("experience_inspirations").insert(
            mapOf(
                "experience_id" to experienceId,
                "user_id" to userId
            )
        )
        val count = getInspirationsForExperience(experienceId).size
        SupabaseClient.client.from("experiences").update(
            mapOf("inspired_count" to count)
        ) {
            filter {
                eq("id", experienceId)
            }
        }
    }

    suspend fun removeInspiration(experienceId: String, userId: String) {
        SupabaseClient.client.from("experience_inspirations").delete {
            filter {
                eq("experience_id", experienceId)
                eq("user_id", userId)
            }
        }
        val count = getInspirationsForExperience(experienceId).size
        SupabaseClient.client.from("experiences").update(
            mapOf("inspired_count" to count)
        ) {
            filter {
                eq("id", experienceId)
            }
        }
    }

    suspend fun createExperience(title: String, description: String, coverImageUrl: String?, authorUserId: String) {
        SupabaseClient.client.from("experiences").insert(
            mapOf(
                "title" to title,
                "description" to description,
                "cover_image_url" to coverImageUrl,
                "author_user_id" to authorUserId,
                "inspired_count" to 0,
                "status" to "published"
            )
        )
    }
}
