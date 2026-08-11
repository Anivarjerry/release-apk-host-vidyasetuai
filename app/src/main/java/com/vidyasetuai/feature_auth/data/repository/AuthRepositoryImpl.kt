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
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlin.coroutines.resume

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
            val cleanEmail = email.trim()
            val cleanPassword = password.trim()
            android.util.Log.d("VidyaSetu_AuthRepo", "signIn called for email='$cleanEmail', passLength=${cleanPassword.length}")

            // 1. Authenticate with Supabase Auth
            SupabaseClient.client.auth.signInWith(Email) {
                this.email = cleanEmail
                this.password = cleanPassword
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
                email = cleanEmail,
                accessToken = authSession.accessToken,
                refreshToken = authSession.refreshToken ?: ""
            )
            sessionManager.saveTrustedAccount(
                email = cleanEmail,
                userId = userId,
                password = cleanPassword,
                refreshToken = authSession.refreshToken ?: ""
            )

            // 4. Implement Single Device Login: Deactivate other active mobile sessions on the server
            val deviceId = sessionManager.getDeviceId()
            SupabaseClient.client.from("user_sessions")
                .update(mapOf("is_active" to false)) {
                    filter {
                        eq("user_id", userId)
                        eq("is_active", true)
                        isIn("platform", listOf("android", "ios"))
                        neq("device_id", deviceId)
                    }
                }

            // 5. Register/Update current session in user_sessions table
            var fcmToken = sessionManager.getFcmToken()
            if (fcmToken.isNullOrEmpty()) {
                fcmToken = getFirebaseTokenSafely()
                if (!fcmToken.isNullOrEmpty()) {
                    sessionManager.saveFcmToken(fcmToken)
                }
            }
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
                    return@withContext com.vidyasetuai.core.auth.AuthManager.tokenRefreshMutex.withLock {
                        val currentSessionBefore = SupabaseClient.client.auth.currentSessionOrNull()
                        if (currentSessionBefore != null && !currentSessionBefore.accessToken.isNullOrEmpty()) {
                            try {
                                SupabaseClient.client.auth.refreshCurrentSession()
                                val refreshedSession = SupabaseClient.client.auth.currentSessionOrNull()
                                if (refreshedSession != null && !refreshedSession.accessToken.isNullOrEmpty()) {
                                    sessionManager.updateTokens(
                                        accessToken = refreshedSession.accessToken,
                                        refreshToken = refreshedSession.refreshToken ?: ""
                                    )
                                }
                            } catch (_: Exception) {}
                            return@withLock Result.success(true)
                        }

                        // Attempt to restore session using refresh token
                        val session = UserSession(
                            accessToken = sessionManager.getAccessToken() ?: "",
                            refreshToken = refreshToken,
                            expiresIn = 3600L,
                            tokenType = "Bearer",
                            user = null
                        )
                        try {
                            SupabaseClient.client.auth.importSession(session)
                            SupabaseClient.client.auth.refreshCurrentSession()
                        } catch (e: Exception) {
                            android.util.Log.e("VidyaSetu_Auth", "Failed to refresh session on restoreSession: ${e.message}")
                        }
                        
                        val currentSession = SupabaseClient.client.auth.currentSessionOrNull()
                        if (currentSession != null && !currentSession.accessToken.isNullOrEmpty()) {
                            sessionManager.updateTokens(
                                accessToken = currentSession.accessToken,
                                refreshToken = currentSession.refreshToken ?: ""
                            )
                            Result.success(true)
                        } else {
                            // Offline/Transient Glitch Resiliency: Keep session active if local token exists
                            if (!sessionManager.getAccessToken().isNullOrEmpty()) {
                                Result.success(true)
                            } else {
                                Result.success(false)
                            }
                        }
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

        // Ensure Supabase Auth has an active session carrying a JWT token before querying RLS table
        val currentSupabaseSession = SupabaseClient.client.auth.currentSessionOrNull()
        if (currentSupabaseSession == null || currentSupabaseSession.accessToken.isNullOrEmpty()) {
            // If Supabase client session is not loaded yet or offline, assume session remains valid to prevent false logout
            return@withContext true
        }

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

    override suspend fun sendPasswordResetOtp(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim()
            SupabaseClient.client.auth.resetPasswordForEmail(cleanEmail)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyRecoveryOtp(email: String, otp: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim()
            val cleanOtp = otp.trim()
            try {
                SupabaseClient.client.auth.verifyEmailOtp(
                    type = io.github.jan.supabase.auth.OtpType.Email.EMAIL,
                    email = cleanEmail,
                    token = cleanOtp
                )
            } catch (ex: Exception) {
                SupabaseClient.client.auth.verifyEmailOtp(
                    type = io.github.jan.supabase.auth.OtpType.Email.RECOVERY,
                    email = cleanEmail,
                    token = cleanOtp
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePassword(newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cleanPassword = newPassword.trim()
            SupabaseClient.client.auth.updateUser {
                password = cleanPassword
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendLoginOtp(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim()
            SupabaseClient.client.auth.signInWith(io.github.jan.supabase.auth.providers.builtin.OTP) {
                this.email = cleanEmail
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyLoginOtp(email: String, otp: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim()
            val cleanOtp = otp.trim()
            SupabaseClient.client.auth.verifyEmailOtp(
                type = io.github.jan.supabase.auth.OtpType.Email.EMAIL,
                email = cleanEmail,
                token = cleanOtp
            )
            val currentSession = SupabaseClient.client.auth.currentSessionOrNull()
                ?: throw Exception("Auth session not found after OTP verification")
            val authId = currentSession.user?.id
                ?: throw Exception("Auth user ID not found")

            // 1. Fetch public user_id mapped from auth_id
            val userResponse = SupabaseClient.client.from("users")
                .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("id")) {
                    filter {
                        eq("auth_id", authId)
                    }
                }.decodeSingleOrNull<UserDto>() ?: throw Exception("Public user ID mapping not found")

            val userId = userResponse.id

            // 2. Save session locally
            sessionManager.saveSession(
                userId = userId,
                email = cleanEmail,
                accessToken = currentSession.accessToken,
                refreshToken = currentSession.refreshToken
            )

            // 3. Save trusted account entry for quick biometric login
            sessionManager.saveTrustedAccount(
                email = cleanEmail,
                userId = userId,
                password = "",
                refreshToken = currentSession.refreshToken
            )

            // 4. Single Device Login: Deactivate other active sessions on the server
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
            var fcmToken = sessionManager.getFcmToken()
            if (fcmToken.isNullOrEmpty()) {
                fcmToken = getFirebaseTokenSafely()
                if (!fcmToken.isNullOrEmpty()) {
                    sessionManager.saveFcmToken(fcmToken)
                }
            }
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

    private suspend fun getFirebaseTokenSafely(): String? = kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (continuation.isActive) {
                    if (task.isSuccessful) {
                        continuation.resume(task.result)
                    } else {
                        continuation.resume(null)
                    }
                }
            }
        } catch (e: Exception) {
            if (continuation.isActive) {
                continuation.resume(null)
            }
        }
    }
}