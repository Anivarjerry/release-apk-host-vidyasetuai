package com.vidyasetuai.feature_auth.data.repository

import com.vidyasetuai.core.auth.SessionManager
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_auth.domain.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(val id: String)

@Serializable
data class UserSessionDto(
    val user_id: String,
    val device_id: String,
    val device_name: String,
    val fcm_token: String?,
    val platform: String,
    val is_active: Boolean
)

@Serializable
data class UserSessionResponseDto(
    val id: String,
    val is_active: Boolean
)

class AuthRepositoryImpl(
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun signUp(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Authenticate with Supabase Auth
            SupabaseClient.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val authSession = SupabaseClient.client.auth.currentSessionOrNull() ?: throw Exception("Auth session not found")
            val authId = authSession.user?.id ?: throw Exception("Auth user not found")

            // 2. Fetch the corresponding public user ID from users table
            val userResponse = SupabaseClient.client.from("users")
                .select(columns = Columns.raw("id")) {
                    filter {
                        eq("auth_id", authId)
                    }
                }.decodeSingleOrNull<UserDto>() ?: throw Exception("Public user ID mapping not found")
            
            val userId = userResponse.id

            // 3. Save session details locally
            sessionManager.saveSession(
                userId = userId,
                email = email,
                accessToken = authSession.accessToken,
                refreshToken = authSession.refreshToken ?: ""
            )

            // 4. Implement Single Device Login: Deactivate other active sessions on the server
            val deviceId = sessionManager.getDeviceId()
            SupabaseClient.client.from("user_sessions")
                .update(mapOf("is_active" to false)) {
                    filter {
                        eq("user_id", userId)
                        eq("is_active", true)
                        neq("device_id", deviceId)
                    }
                }

            // 5. Register/Update current session in user_sessions table
            val fcmToken = sessionManager.getFcmToken()
            val sessionDto = UserSessionDto(
                user_id = userId,
                device_id = deviceId,
                device_name = android.os.Build.MODEL,
                fcm_token = fcmToken,
                platform = "android",
                is_active = true
            )
            
            SupabaseClient.client.from("user_sessions").upsert(sessionDto) {
                   onConflict = "user_id,device_id"
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = sessionManager.getUserId()
            val deviceId = sessionManager.getDeviceId()
            
            if (!userId.isNullOrEmpty()) {
                // Deactivate this session on the server
                try {
                    SupabaseClient.client.from("user_sessions")
                        .update(mapOf("is_active" to false)) {
                            filter {
                                eq("user_id", userId)
                                eq("device_id", deviceId)
                            }
                        }
                } catch (e: Exception) {
                    // Ignore network failure during sign out
                }
            }

            SupabaseClient.client.auth.signOut()
            sessionManager.clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun restoreSession(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (sessionManager.hasActiveSession()) {
                val refreshToken = sessionManager.getRefreshToken()
                if (!refreshToken.isNullOrEmpty()) {
                    // Attempt to restore session using refresh token
                    val session = UserSession(
                        accessToken = sessionManager.getAccessToken() ?: "",
                        refreshToken = refreshToken,
                        expiresIn = 3600L,
                        tokenType = "Bearer",
                        user = null
                    )
                    SupabaseClient.client.auth.importSession(session)
                    try {
                        SupabaseClient.client.auth.refreshCurrentSession()
                    } catch (e: Exception) {
                        android.util.Log.e("VidyaSetu_Auth", "Failed to refresh session on restoreSession", e)
                    }
                    
                    val currentSession = SupabaseClient.client.auth.currentSessionOrNull()
                    if (currentSession != null) {
                        sessionManager.updateTokens(
                            accessToken = currentSession.accessToken,
                            refreshToken = currentSession.refreshToken ?: ""
                        )
                        return@withContext Result.success(true)
                    }
                }
            }
            Result.success(false)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validateSessionOnline(): Boolean = withContext(Dispatchers.IO) {
        val userId = sessionManager.getUserId() ?: return@withContext false
        val deviceId = sessionManager.getDeviceId()

        try {
            val activeSession = SupabaseClient.client.from("user_sessions")
                .select(columns = Columns.raw("id, is_active")) {
                    filter {
                        eq("user_id", userId)
                        eq("device_id", deviceId)
                        eq("is_active", true)
                    }
                }.decodeSingleOrNull<UserSessionResponseDto>()

            activeSession != null
        } catch (e: Exception) {
            // Offline-first decision: if offline, assume session remains valid to support offline work
            true
        }
    }
}