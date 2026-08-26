package com.vidyasetuai.feature_campus.data.remote

import android.util.Log
import com.vidyasetuai.core.network.SafeSupabaseInvoker
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_campus.data.remote.dto.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Enterprise Remote Data Source for Campus Social & Direct E2EE Chat.
 * All remote calls are Mutex-guarded and resilient via SafeSupabaseInvoker.
 */
class CampusRemoteDataSource {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    /**
     * 1-Roundtrip 0ms Payload fetch from Supabase RPC `fn_fetch_campus_workspace_payload`.
     */
    suspend fun fetchWorkspacePayload(): CampusWorkspacePayloadDto? {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val responseText = SupabaseClient.client.postgrest.rpc(
                "fn_fetch_campus_workspace_payload"
            ).data

            Log.d("CampusRemote", "Workspace Payload response length: ${responseText.length}")
            json.decodeFromString<CampusWorkspacePayloadDto>(responseText)
        }
        return result.getOrNull()
    }

    /**
     * Sends an E2EE message via atomic Supabase RPC `fn_send_campus_message`.
     */
    suspend fun sendMessage(
        conversationId: String,
        recipientId: String,
        encryptedPayload: String,
        mediaUrl: String? = null,
        mediaType: String = "TEXT"
    ): CampusSendMessageResponseDto? {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val params = buildJsonObject {
                put("p_conversation_id", conversationId)
                put("p_recipient_id", recipientId)
                put("p_encrypted_payload", encryptedPayload)
                if (mediaUrl != null) put("p_media_url", mediaUrl)
                put("p_media_type", mediaType)
            }

            val responseText = SupabaseClient.client.postgrest.rpc(
                "fn_send_campus_message",
                params
            ).data

            Log.d("CampusRemote", "SendMessage RPC response: $responseText")
            json.decodeFromString<CampusSendMessageResponseDto>(responseText)
        }
        return result.getOrNull()
    }

    /**
     * Marks chat as read via Supabase RPC `fn_mark_campus_chat_read`.
     */
    suspend fun markChatRead(conversationId: String): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val params = buildJsonObject {
                put("p_conversation_id", conversationId)
            }
            SupabaseClient.client.postgrest.rpc("fn_mark_campus_chat_read", params)
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Follows or connects with a user by upserting `campus_connections`.
     */
    suspend fun followUser(userId: String, targetUserId: String): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                put("user_id", userId)
                put("target_user_id", targetUserId)
                put("status", "FOLLOWING")
                put("is_deleted", false)
            }

            SupabaseClient.client.postgrest["campus_connections"].upsert(payload) {
                onConflict = "user_id,target_user_id"
            }
            true
        }
        return result.getOrDefault(false)
    }


    /**
     * Unfollows a user by setting `is_deleted = true`.
     */
    suspend fun unfollowUser(userId: String, targetUserId: String): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                put("is_deleted", true)
                put("is_mutual", false)
            }

            SupabaseClient.client.postgrest["campus_connections"].update(payload) {
                filter {
                    eq("user_id", userId)
                    eq("target_user_id", targetUserId)
                }
            }
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Blocks a user by updating status to 'BLOCKED'.
     */
    suspend fun blockUser(userId: String, targetUserId: String): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                put("user_id", userId)
                put("target_user_id", targetUserId)
                put("status", "BLOCKED")
                put("is_mutual", false)
                put("is_deleted", false)
            }

            SupabaseClient.client.postgrest["campus_connections"].upsert(payload) {
                onConflict = "user_id,target_user_id"
            }
            true
        }
        return result.getOrDefault(false)
    }



    /**
     * Searches campus users with bidirectional connection statuses via RPC `fn_search_campus_users`.
     */
    suspend fun searchUsers(query: String, limit: Int = 20): List<CampusUserSearchDto> {
        val trimmed = query.trim()
        if (trimmed.length < 2) return emptyList()

        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val params = buildJsonObject {
                put("p_query", trimmed)
                put("p_limit", limit)
            }

            val responseText = SupabaseClient.client.postgrest.rpc(
                "fn_search_campus_users",
                params
            ).data

            Log.d("CampusRemote", "Search users response: $responseText")
            val payload = json.decodeFromString<CampusUserSearchResponseDto>(responseText)
            payload.users
        }
        return result.getOrDefault(emptyList())
    }
}

