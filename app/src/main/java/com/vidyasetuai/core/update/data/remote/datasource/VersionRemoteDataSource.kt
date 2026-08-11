package com.vidyasetuai.core.update.data.remote.datasource

import android.util.Log
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.core.update.data.remote.dto.VersionDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

class VersionRemoteDataSource {
    private val tag = "VidyaSetu_VersionRemote"

    suspend fun getLatestVersion(): VersionDto? {
        return try {
            // 1. First attempt to query version where platform is android and is_latest is explicitly true
            val list = SupabaseClient.client.from("app_versions")
                .select(columns = Columns.raw("*")) {
                    filter {
                        eq("platform", "android")
                        eq("is_latest", true)
                    }
                    order("build_number", order = Order.DESCENDING)
                    limit(1)
                }.decodeList<VersionDto>()
            
            if (list.isNotEmpty()) {
                list.first()
            } else {
                // 2. Fallback: Query highest build_number for android platform
                val fallbackList = SupabaseClient.client.from("app_versions")
                    .select(columns = Columns.raw("*")) {
                        filter {
                            eq("platform", "android")
                        }
                        order("build_number", order = Order.DESCENDING)
                        limit(1)
                    }.decodeList<VersionDto>()
                fallbackList.firstOrNull()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error fetching latest app version from Supabase: ${e.message}", e)
            null
        }
    }
}
