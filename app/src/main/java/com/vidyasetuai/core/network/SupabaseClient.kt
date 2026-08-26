package com.vidyasetuai.core.network

import com.vidyasetuai.BuildConfig
import com.vidyasetuai.VidyaSetuApplication
import com.vidyasetuai.core.auth.EncryptedSupabaseSessionManager
import com.vidyasetuai.core.auth.SessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {
    val client by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth) {
                alwaysAutoRefresh = true
                autoLoadFromStorage = true
                sessionManager = EncryptedSupabaseSessionManager(
                    SessionManager(VidyaSetuApplication.appContext)
                )
            }
            install(Postgrest)
            install(Realtime)
        }
    }
}
