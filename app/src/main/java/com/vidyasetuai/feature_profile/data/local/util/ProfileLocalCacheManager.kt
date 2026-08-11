package com.vidyasetuai.feature_profile.data.local.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

object ProfileLocalCacheManager {

    suspend fun cacheImageFromUrl(context: Context, imageUrl: String?, filename: String): String? {
        if (imageUrl.isNullOrBlank()) return null
        return withContext(Dispatchers.IO) {
            try {
                val mediaDir = File(context.filesDir, "profile_media")
                if (!mediaDir.exists()) {
                    mediaDir.mkdirs()
                }
                val destFile = File(mediaDir, filename)
                
                val url = URL(imageUrl)
                val connection = url.openConnection()
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.getInputStream().use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }
                destFile.absolutePath
            } catch (e: Exception) {
                android.util.Log.e("ProfileLocalCacheManager", "Error caching image from $imageUrl", e)
                null
            }
        }
    }
}
