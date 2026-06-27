package com.vidyasetuai.feature_case_study.data.remote.datasource

import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_case_study.data.remote.dto.BookmarkDto
import com.vidyasetuai.feature_case_study.data.remote.dto.CaseStudyDetailDto
import com.vidyasetuai.feature_case_study.data.remote.dto.CaseStudyDto
import com.vidyasetuai.feature_case_study.data.remote.dto.ReactionDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import com.vidyasetuai.feature_profile.data.remote.dto.UserProfileDto

class CaseStudyRemoteDataSource {

    suspend fun getCaseStudies(): List<CaseStudyDto> {
        return SupabaseClient.client.from("case_studies_preview")
            .select(columns = Columns.raw("*"))
            .decodeList()
    }

    suspend fun getCaseStudyDetail(id: String): CaseStudyDetailDto {
        return SupabaseClient.client.from("case_studies")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("id", id)
                }
            }.decodeSingle()
    }

    suspend fun getUserReactions(userId: String): List<ReactionDto> {
        return SupabaseClient.client.from("case_study_reactions")
            .select(columns = Columns.raw("id, case_study_id, user_id, reaction_type")) {
                filter {
                    eq("user_id", userId)
                }
            }.decodeList()
    }

    suspend fun getUserBookmarks(userId: String): List<BookmarkDto> {
        return SupabaseClient.client.from("case_study_bookmarks")
            .select(columns = Columns.raw("id, case_study_id, user_id")) {
                filter {
                    eq("user_id", userId)
                }
            }.decodeList()
    }

    suspend fun getAllReactions(): List<ReactionDto> {
        return SupabaseClient.client.from("case_study_reactions")
            .select(columns = Columns.raw("id, case_study_id, user_id, reaction_type"))
            .decodeList()
    }

    suspend fun getAllBookmarks(): List<BookmarkDto> {
        return SupabaseClient.client.from("case_study_bookmarks")
            .select(columns = Columns.raw("id, case_study_id, user_id"))
            .decodeList()
    }

    suspend fun getReactionsForCaseStudy(caseStudyId: String): List<ReactionDto> {
        return SupabaseClient.client.from("case_study_reactions")
            .select(columns = Columns.raw("id, case_study_id, user_id, reaction_type")) {
                filter {
                    eq("case_study_id", caseStudyId)
                }
            }.decodeList()
    }

    suspend fun getBookmarksForCaseStudy(caseStudyId: String): List<BookmarkDto> {
        return SupabaseClient.client.from("case_study_bookmarks")
            .select(columns = Columns.raw("id, case_study_id, user_id")) {
                filter {
                    eq("case_study_id", caseStudyId)
                }
            }.decodeList()
    }

    suspend fun addReaction(caseStudyId: String, userId: String, reactionType: String) {
        SupabaseClient.client.from("case_study_reactions").insert(
            mapOf(
                "case_study_id" to caseStudyId,
                "user_id" to userId,
                "reaction_type" to reactionType
            )
        )
    }

    suspend fun removeReaction(caseStudyId: String, userId: String) {
        SupabaseClient.client.from("case_study_reactions").delete {
            filter {
                eq("case_study_id", caseStudyId)
                eq("user_id", userId)
            }
        }
    }

    suspend fun addBookmark(caseStudyId: String, userId: String) {
        SupabaseClient.client.from("case_study_bookmarks").insert(
            mapOf(
                "case_study_id" to caseStudyId,
                "user_id" to userId
            )
        )
    }

    suspend fun removeBookmark(caseStudyId: String, userId: String) {
        SupabaseClient.client.from("case_study_bookmarks").delete {
            filter {
                eq("case_study_id", caseStudyId)
                eq("user_id", userId)
            }
        }
    }

    suspend fun getCaseStudiesByUser(authorUserId: String): List<CaseStudyDto> {
        return SupabaseClient.client.from("case_studies")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("author_user_id", authorUserId)
                }
                order("created_at", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            }.decodeList()
    }

    suspend fun createCaseStudy(
        title: String,
        shortDescription: String,
        coverImageUrl: String,
        language: String,
        tags: List<String>,
        readTimeMinutes: Int?,
        detailedContent: String,
        additionalImageUrls: List<String>,
        authorUserId: String
    ) {
        val slug = title.lowercase()
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .replace(Regex("\\s+"), "-")
            .trim('-') + "-" + java.util.UUID.randomUUID().toString().take(6)
            
        val contentBlocks = kotlinx.serialization.json.buildJsonObject {
            put("schema_version", kotlinx.serialization.json.JsonPrimitive(1))
            put("blocks", kotlinx.serialization.json.buildJsonArray {
                add(kotlinx.serialization.json.buildJsonObject {
                    put("type", kotlinx.serialization.json.JsonPrimitive("paragraph"))
                    put("text", kotlinx.serialization.json.JsonPrimitive(detailedContent))
                })
            })
        }

        SupabaseClient.client.from("case_studies").insert(
            mapOf(
                "title" to title,
                "short_description" to shortDescription,
                "cover_image_url" to coverImageUrl,
                "slug" to slug,
                "author_user_id" to authorUserId,
                "author_type" to "user",
                "status" to "under_review",
                "language" to language.lowercase(),
                "tags" to tags,
                "read_time_minutes" to readTimeMinutes,
                "additional_image_urls" to additionalImageUrls,
                "content_blocks" to contentBlocks
            )
        )
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
}
