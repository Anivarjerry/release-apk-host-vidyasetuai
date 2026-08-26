package com.vidyasetuai.feature_profile.data.remote

import android.content.Context
import android.util.Log
import com.vidyasetuai.core.network.SafeSupabaseInvoker
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.core.network.SupabaseStorageHelper
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.io.File
import java.util.*

/**
 * Traffic Police-Guarded Remote Data Source for Profile Module (Pillars 1 & 2 AGENTS.md).
 */
class ProfileRemoteDataSource(private val context: Context? = null) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    suspend fun fetchProfilePayload(targetUserId: String? = null): Result<JsonObject> = withContext(Dispatchers.IO) {
        SafeSupabaseInvoker.safeSupabaseCall {
            val params = buildJsonObject {
                if (!targetUserId.isNullOrBlank()) {
                    put("p_target_user_id", targetUserId)
                }
            }

            val response = SupabaseClient.client.postgrest.rpc(
                function = "fn_fetch_profile_v2_payload",
                parameters = params
            )

            val rawJson = response.data
            Log.d("ProfileRemote", "Profile payload received, length: ${rawJson.length}")
            json.parseToJsonElement(rawJson).jsonObject
        }
    }

    suspend fun checkUsernameAvailability(username: String): Result<Boolean> = withContext(Dispatchers.IO) {
        SafeSupabaseInvoker.safeSupabaseCall {
            val params = buildJsonObject {
                put("p_username", username.trim().lowercase())
            }

            val response = SupabaseClient.client.postgrest.rpc(
                function = "fn_check_username_available",
                parameters = params
            )

            val rawJson = response.data
            val jsonObject = json.parseToJsonElement(rawJson).jsonObject
            jsonObject["available"]?.jsonPrimitive?.booleanOrNull ?: false
        }
    }

    suspend fun updateProfile(
        username: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        bio: String? = null,
        gender: String? = null,
        dateOfBirth: String? = null,
        preferredLanguage: String? = null,
        profilePictureUrl: String? = null,
        coverPhotoUrl: String? = null,
        isPrivate: Boolean? = null
    ): Result<JsonObject> = withContext(Dispatchers.IO) {
        SafeSupabaseInvoker.safeSupabaseCall {
            val params = buildJsonObject {
                if (username != null) put("p_username", username.trim().lowercase())
                if (firstName != null) put("p_first_name", firstName)
                if (lastName != null) put("p_last_name", lastName)
                if (bio != null) put("p_bio", bio)
                if (gender != null) put("p_gender", gender)
                if (dateOfBirth != null) put("p_date_of_birth", dateOfBirth)
                if (preferredLanguage != null) put("p_preferred_language", preferredLanguage)
                if (profilePictureUrl != null) put("p_profile_picture_url", profilePictureUrl)
                if (coverPhotoUrl != null) put("p_cover_photo_url", coverPhotoUrl)
                if (isPrivate != null) put("p_is_private", isPrivate)
            }

            val response = SupabaseClient.client.postgrest.rpc(
                function = "fn_update_user_profile_v2",
                parameters = params
            )

            val rawJson = response.data
            json.parseToJsonElement(rawJson).jsonObject
        }
    }

    suspend fun uploadProfileImage(
        bucketName: String,
        imageFile: File,
        userId: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileExt = imageFile.extension.ifBlank { "jpg" }
            val fileName = "${userId}_${UUID.randomUUID().toString().take(8)}.$fileExt"
            val bytes = imageFile.readBytes()

            val publicUrl = SupabaseStorageHelper.uploadImage(bucketName, fileName, bytes)
            Log.d("ProfileRemote", "Image uploaded successfully: $publicUrl")
            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e("ProfileRemote", "Image upload failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
