package com.vidyasetuai.core.auth

import android.content.Context
import android.util.Log
import com.vidyasetuai.core.database.AppDatabase
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_auth.data.repository.AuthRepositoryImpl
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object AuthManager {
    private const val tag = "VidyaSetu_AuthManager"

    // Thread-safe mutex to prevent concurrent token refresh race conditions
    val tokenRefreshMutex = Mutex()

    private val _isSessionValid = MutableStateFlow(true)
    val isSessionValid: StateFlow<Boolean> = _isSessionValid.asStateFlow()

    /**
     * Set the session validation state.
     */
    fun setSessionValid(isValid: Boolean) {
        _isSessionValid.value = isValid
    }

    /**
     * Resets the session state flow to valid. Call this when logging in.
     */
    fun resetSessionState() {
        _isSessionValid.value = true
    }

    /**
     * Automatically log out the user, clear local session credentials,
     * and clear the entire Room Database cache.
     */
    fun logoutAndClearData(context: Context, sessionManager: SessionManager) {
        LogoutManager.logoutAndClearData(context, sessionManager)
    }

    /**
     * Checks if the active session is valid online.
     * If invalid, triggers logout automatically.
     */
    suspend fun checkSessionOnline(context: Context, sessionManager: SessionManager, authRepository: AuthRepositoryImpl) {
        if (!sessionManager.hasActiveSession()) return

        // 1. Refresh token & check auth status
        val sessionRestored = authRepository.restoreSession().getOrDefault(false)
        if (!sessionRestored && !sessionManager.hasActiveSession()) {
            Log.w(tag, "Session restoration failed with no active local session, forcing logout")
            logoutAndClearData(context, sessionManager)
            return
        }

        // 2. Validate online status in user_sessions table (Single Device Login Check)
        val isOnlineValid = authRepository.validateSessionOnline()
        if (!isOnlineValid) {
            Log.w(tag, "Online session validation failed (deactivated on another device), forcing logout")
            logoutAndClearData(context, sessionManager)
            return
        }

        // 3. Sync FCM token in the background
        syncFcmTokenIfNeeded(sessionManager)

        // 4. Start proactive token refresh loop
        startProactiveTokenRefresh(sessionManager)
    }

    private var isProactiveRefreshRunning = false

    /**
     * Starts a background coroutine to proactively refresh the session token
     * every 45 minutes (15 minutes before the 1-hour JWT expiration).
     * Thread-safe with Mutex to prevent clashes with foreground checks.
     */
    fun startProactiveTokenRefresh(sessionManager: SessionManager) {
        if (isProactiveRefreshRunning) return
        isProactiveRefreshRunning = true

        CoroutineScope(Dispatchers.IO).launch {
            Log.d(tag, "Proactive token refresh loop started")
            while (sessionManager.hasActiveSession()) {
                kotlinx.coroutines.delay(45 * 60 * 1000L) // Wait 45 minutes
                if (!sessionManager.hasActiveSession()) break

                var refreshedSuccessfully = false
                var attempts = 0
                val maxAttempts = 5

                while (!refreshedSuccessfully && attempts < maxAttempts && sessionManager.hasActiveSession()) {
                    attempts++
                    try {
                        Log.d(tag, "Attempting proactive token refresh (Attempt $attempts)...")
                        tokenRefreshMutex.withLock {
                            SupabaseClient.client.auth.refreshCurrentSession()
                            val currentSession = SupabaseClient.client.auth.currentSessionOrNull()
                            val newAccess = currentSession?.accessToken
                            val newRefresh = currentSession?.refreshToken
                            if (!newAccess.isNullOrEmpty()) {
                                sessionManager.updateTokens(newAccess, newRefresh ?: "")
                                refreshedSuccessfully = true
                                Log.d(tag, "Proactive token refresh succeeded!")
                            } else {
                                Log.w(tag, "Proactive token refresh returned empty token")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "Proactive token refresh failed (Attempt $attempts): ${e.message}")
                    }

                    if (!refreshedSuccessfully) {
                        // Exponential Backoff Retry: Wait 2 minutes before retrying
                        kotlinx.coroutines.delay(2 * 60 * 1000L)
                    }
                }
            }
            isProactiveRefreshRunning = false
            Log.d(tag, "Proactive token refresh loop stopped")
        }
    }

    /**
     * Public helper to trigger FCM token sync manually or from services.
     */
    fun syncFcmToken(context: Context, sessionManager: SessionManager) {
        CoroutineScope(Dispatchers.IO).launch {
            syncFcmTokenIfNeeded(sessionManager)
        }
    }

    /**
     * Syncs the FCM token with Supabase user_sessions table if it has changed.
     */
    private suspend fun syncFcmTokenIfNeeded(sessionManager: SessionManager) = withContext(Dispatchers.IO) {
        val userId = sessionManager.getUserId() ?: return@withContext
        val currentFcmToken = sessionManager.getFcmToken() ?: return@withContext

        try {
            SupabaseClient.client.from("user_sessions")
                .update(buildJsonObject {
                    put("fcm_token", currentFcmToken)
                    put("is_active", true)
                }) {
                    filter {
                        eq("user_id", userId)
                    }
                }
            Log.d(tag, "FCM token synced successfully with server for user $userId")
        } catch (e: Exception) {
            Log.e(tag, "Failed to sync FCM token with server: ${e.message}")
        }
    }
}