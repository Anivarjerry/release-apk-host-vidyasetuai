package com.vidyasetuai.feature_profile.data.remote.datasource

import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_profile.data.remote.dto.VerificationDto
import com.vidyasetuai.feature_profile.data.remote.dto.UserProfileDto
import com.vidyasetuai.feature_profile.data.remote.dto.UserInspirationDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class ProfileRemoteDataSource {

    suspend fun getProfile(userId: String): UserProfileDto {
        return SupabaseClient.client.from("user_profiles")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("user_id", userId)
                }
            }.decodeSingle()
    }

    suspend fun updateProfile(profile: UserProfileDto) {
        // Update user_profiles table. Filter by user_id
        SupabaseClient.client.from("user_profiles").update(
            mapOf(
                "first_name" to profile.first_name,
                "last_name" to profile.last_name,
                "full_name" to profile.full_name,
                "bio" to profile.bio,
                "username" to profile.username,
                "gender" to profile.gender,
                "date_of_birth" to profile.date_of_birth,
                "profile_picture_url" to profile.profile_picture_url,
                "cover_photo_url" to profile.cover_photo_url,
                "preferred_language" to profile.preferred_language
            )
        ) {
            filter {
                eq("user_id", profile.user_id)
            }
        }
    }

    suspend fun checkUsernameUnique(username: String, currentUserId: String): Boolean {
        val list = SupabaseClient.client.from("user_profiles")
            .select(columns = Columns.raw("user_id")) {
                filter {
                    eq("username", username)
                    neq("user_id", currentUserId)
                }
            }.decodeList<UserProfileDto>()
        return list.isEmpty()
    }

    suspend fun getVerification(userId: String): VerificationDto? {
        val list = SupabaseClient.client.from("content_contributor_verifications")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("user_id", userId)
                }
            }.decodeList<VerificationDto>()
        return list.firstOrNull()
    }

    suspend fun applyForVerification(userId: String, applicantNote: String) {
        SupabaseClient.client.from("content_contributor_verifications").upsert(
            mapOf(
                "contributor_type" to "user",
                "user_id" to userId,
                "applicant_note" to applicantNote,
                "status" to "pending"
            )
        )
    }

    suspend fun getInspirationRelation(inspiredUserId: String, inspiringUserId: String): UserInspirationDto? {
        val list = SupabaseClient.client.from("user_inspirations")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("inspired_user_id", inspiredUserId)
                    eq("inspiring_user_id", inspiringUserId)
                }
            }.decodeList<UserInspirationDto>()
        return list.firstOrNull()
    }

    suspend fun toggleUserInspiration(inspiredUserId: String, inspiringUserId: String): Boolean {
        val existing = getInspirationRelation(inspiredUserId, inspiringUserId)
        return if (existing != null) {
            val nextState = !existing.is_deleted
            SupabaseClient.client.from("user_inspirations").update(
                mapOf(
                    "is_deleted" to nextState,
                    "updated_at" to java.time.Instant.now().toString()
                )
            ) {
                filter {
                    eq("id", existing.id!!)
                }
            }
            !nextState
        } else {
            SupabaseClient.client.from("user_inspirations").insert(
                UserInspirationDto(
                    inspired_user_id = inspiredUserId,
                    inspiring_user_id = inspiringUserId,
                    is_deleted = false
                )
            )
            true
        }
    }

    suspend fun getInspiredCount(userId: String): Int {
        val list = SupabaseClient.client.from("user_inspirations")
            .select(columns = Columns.raw("id")) {
                filter {
                    eq("inspiring_user_id", userId)
                    eq("is_deleted", false)
                }
            }.decodeList<UserInspirationDto>()
        return list.size
    }

    suspend fun getInspiringCount(userId: String): Int {
        val list = SupabaseClient.client.from("user_inspirations")
            .select(columns = Columns.raw("id")) {
                filter {
                    eq("inspired_user_id", userId)
                    eq("is_deleted", false)
                }
            }.decodeList<UserInspirationDto>()
        return list.size
    }

    suspend fun isInspiredBy(inspiredUserId: String, inspiringUserId: String): Boolean {
        val relation = getInspirationRelation(inspiredUserId, inspiringUserId)
        return relation?.is_deleted == false
    }

    suspend fun getInspiredUsers(userId: String): List<UserProfileDto> {
        val connections = SupabaseClient.client.from("user_inspirations")
            .select(columns = Columns.raw("inspired_user_id")) {
                filter {
                    eq("inspiring_user_id", userId)
                    eq("is_deleted", false)
                }
            }.decodeList<UserInspirationDto>()
        if (connections.isEmpty()) return emptyList()
        val userIds = connections.mapNotNull { it.inspired_user_id }
        return SupabaseClient.client.from("user_profiles")
            .select(columns = Columns.raw("*")) {
                filter {
                    isIn("user_id", userIds)
                }
            }.decodeList()
    }

    suspend fun getInspiringUsers(userId: String): List<UserProfileDto> {
        val connections = SupabaseClient.client.from("user_inspirations")
            .select(columns = Columns.raw("inspiring_user_id")) {
                filter {
                    eq("inspired_user_id", userId)
                    eq("is_deleted", false)
                }
            }.decodeList<UserInspirationDto>()
        if (connections.isEmpty()) return emptyList()
        val userIds = connections.mapNotNull { it.inspiring_user_id }
        return SupabaseClient.client.from("user_profiles")
            .select(columns = Columns.raw("*")) {
                filter {
                    isIn("user_id", userIds)
                }
            }.decodeList()
    }
}