package com.vidyasetuai.core.auth

import io.github.jan.supabase.auth.SessionManager as SupabaseSessionManager
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Enterprise Native Supabase Session Manager backed by Android EncryptedSharedPreferences (AES-256 GCM).
 * 
 * 1. Single Source of Truth: Directly bridges Supabase SDK's background token rotation with local secure storage.
 * 2. Zero Desync: Whenever Supabase rotates a refresh token, it atomically saves it to encrypted disk.
 * 3. Zero Polling / Zero Server Load: Disk reads on app startup, disk writes on 1-hour token rotation.
 */
class EncryptedSupabaseSessionManager(
    private val sessionManager: SessionManager
) : SupabaseSessionManager {

    override suspend fun saveSession(session: UserSession) = withContext(Dispatchers.IO) {
        val refreshToken = session.refreshToken ?: ""
        val accessToken = session.accessToken
        if (accessToken.isNotEmpty()) {
            sessionManager.updateTokens(accessToken, refreshToken)
        }
    }

    override suspend fun loadSession(): UserSession? = withContext(Dispatchers.IO) {
        val accessToken = sessionManager.getAccessToken() ?: return@withContext null
        val refreshToken = sessionManager.getRefreshToken() ?: return@withContext null
        if (accessToken.isEmpty() || refreshToken.isEmpty()) return@withContext null

        UserSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = 3600L,
            tokenType = "Bearer",
            user = null
        )
    }

    override suspend fun deleteSession() = withContext(Dispatchers.IO) {
        sessionManager.updateTokens("", "")
    }
}
