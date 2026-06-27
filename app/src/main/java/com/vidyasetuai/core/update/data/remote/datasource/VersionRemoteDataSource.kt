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
            val list = SupabaseClient.client.from("app_versions")
                .select(columns = Columns.raw("*")) {
                    filter {
                        eq("platform", "android")
                    }
                    order("build_number", order = Order.DESCENDING)
                    limit(5)
                }.decodeList<VersionDto>()
            
            list.firstOrNull()
        } catch (e: Exception) {
            Log.e(tag, "Error fetching latest app version from Supabase: ${e.message}", e)
            null
        }
    }
}
