package com.vidyasetuai.core.network

import android.util.Log
import com.vidyasetuai.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object SupabaseStorageHelper {
    private val client = OkHttpClient()

    suspend fun uploadImage(bucket: String, fileName: String, bytes: ByteArray): String = withContext(Dispatchers.IO) {
        val cleanUrl = BuildConfig.SUPABASE_URL.trimEnd('/')
        val url = "$cleanUrl/storage/v1/object/$bucket/$fileName"
        
        Log.d("SupabaseStorage", "Uploading image to url: '$url'")
        val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .put(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            val responseCode = response.code
            val responseBody = response.body?.string() ?: ""
            Log.d("SupabaseStorage", "Upload response: $responseCode - $responseBody")

            if (!response.isSuccessful) {
                // Supabase returns 400/409 if the file already exists or has duplicate keys
                if (responseCode != 400 && responseCode != 409) {
                    throw IOException("Upload failed with code $responseCode: $responseBody")
                }
            }
            
            // Return public URL
            "$cleanUrl/storage/v1/object/public/$bucket/$fileName"
        }
    }
}
