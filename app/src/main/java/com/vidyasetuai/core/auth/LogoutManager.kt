package com.vidyasetuai.core.auth

import android.content.Context
import android.util.Log
import com.vidyasetuai.core.database.AppDatabase
import com.vidyasetuai.core.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object LogoutManager {
    private const val tag = "VidyaSetu_LogoutManager"

    /**
     * Wipes all user credentials, room cached databases, deactivates the session on Supabase
     * remotely, and invalidates the session flow.
     */
    fun logoutAndClearData(context: Context, sessionManager: SessionManager) {
        // 1. Immediately invalidate session flow to trigger UI navigation redirect to Login screen
        AuthManager.setSessionValid(false)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userId = sessionManager.getUserId()
                val deviceId = sessionManager.getDeviceId()

                // 2. Deactivate session row in Supabase
                if (!userId.isNullOrEmpty()) {
                    try {
                        SupabaseClient.client.from("user_sessions")
                            .update(mapOf("is_active" to false)) {
                                filter {
                                    eq("user_id", userId)
                                    eq("device_id", deviceId)
                                }
                            }
                        Log.d(tag, "Successfully deactivated session remotely on Supabase")
                    } catch (e: Exception) {
                        Log.e(tag, "Failed to deactivate session remotely: ${e.message}")
                    }
                }

                // 3. Clear Supabase client cache/session
                try {
                    SupabaseClient.client.auth.signOut()
                    Log.d(tag, "Successfully signed out of Supabase Auth")
                } catch (e: Exception) {
                    Log.e(tag, "Supabase signOut failed: ${e.message}")
                }

                // 4. Clear all local Room Database tables
                try {
                    val database = AppDatabase.getDatabase(context)
                    database.clearAllTables()
                    Log.d(tag, "Room Database tables cleared successfully")
                } catch (e: Exception) {
                    Log.e(tag, "Failed to clear Room Database tables: ${e.message}")
                }

                // 5. Wipes out SharedPreferences credentials
                try {
                    sessionManager.clearSession()
                    Log.d(tag, "SharedPreferences session wiped successfully")
                } catch (e: Exception) {
                    Log.e(tag, "Failed to clear Session SharedPreferences: ${e.message}")
                }

            } catch (e: Exception) {
                Log.e(tag, "Unexpected error in LogoutManager: ${e.message}", e)
            }
        }
    }
}
