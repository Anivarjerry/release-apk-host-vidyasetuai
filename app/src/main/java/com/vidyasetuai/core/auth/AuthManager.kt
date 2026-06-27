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
import kotlinx.coroutines.withContext

object AuthManager {
    private const val tag = "VidyaSetu_AuthManager"

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
        if (!sessionRestored) {
            Log.w(tag, "Session restoration failed, forcing logout")
            logoutAndClearData(context, sessionManager)
            return
        }

        // 2. Validate online status in user_sessions table
        val isOnlineValid = authRepository.validateSessionOnline()
        if (!isOnlineValid) {
            Log.w(tag, "Online session validation failed (deactivated), forcing logout")
            logoutAndClearData(context, sessionManager)
            return
        }

        // 3. Sync FCM token in the background
        syncFcmTokenIfNeeded(sessionManager)
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
        val deviceId = sessionManager.getDeviceId()
        val currentFcmToken = sessionManager.getFcmToken() ?: return@withContext

        try {
            // Update FCM token on the server for this device session
            SupabaseClient.client.from("user_sessions")
                .update(mapOf("fcm_token" to currentFcmToken)) {
                    filter {
                        eq("user_id", userId)
                        eq("device_id", deviceId)
                        eq("is_active", true)
                    }
                }
            Log.d(tag, "FCM token synced successfully with server")
        } catch (e: Exception) {
            Log.e(tag, "Failed to sync FCM token with server: ${e.message}")
        }
    }
}