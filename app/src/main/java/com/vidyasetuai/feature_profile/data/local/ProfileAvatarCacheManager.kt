package com.vidyasetuai.feature_profile.data.local

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Isolated background image cache engine for the Profile module.
 * Caches avatars and cover photos to local disk storage for instant 0ms offline rendering.
 */
class ProfileAvatarCacheManager(
    private val context: Context,
    private val profileDao: ProfileDao
) {
    private val mediaDir: File by lazy {
        File(context.filesDir, "profile_media").apply {
            if (!exists()) mkdirs()
        }
    }

    suspend fun cacheAvatar(userId: String, remoteUrl: String?): String? = withContext(Dispatchers.IO) {
        if (remoteUrl.isNullOrBlank()) return@withContext null

        try {
            val destinationFile = File(mediaDir, "avatar_${userId.replace("-", "_")}.jpg")
            
            if (destinationFile.exists() && destinationFile.length() > 0) {
                profileDao.updateAvatarLocalPath(userId, destinationFile.absolutePath)
                profileDao.updateInspirationAvatarLocalPath(userId, destinationFile.absolutePath)
                return@withContext destinationFile.absolutePath
            }

            val downloaded = downloadFile(remoteUrl, destinationFile)
            if (downloaded) {
                val absolutePath = destinationFile.absolutePath
                profileDao.updateAvatarLocalPath(userId, absolutePath)
                profileDao.updateInspirationAvatarLocalPath(userId, absolutePath)
                return@withContext absolutePath
            }
        } catch (e: Exception) {
            Log.w("ProfileAvatarCache", "Failed to cache avatar for $userId: ${e.message}")
        }
        null
    }

    suspend fun cacheCoverPhoto(userId: String, remoteUrl: String?): String? = withContext(Dispatchers.IO) {
        if (remoteUrl.isNullOrBlank()) return@withContext null

        try {
            val destinationFile = File(mediaDir, "cover_${userId.replace("-", "_")}.jpg")
            
            if (destinationFile.exists() && destinationFile.length() > 0) {
                profileDao.updateCoverLocalPath(userId, destinationFile.absolutePath)
                return@withContext destinationFile.absolutePath
            }

            val downloaded = downloadFile(remoteUrl, destinationFile)
            if (downloaded) {
                val absolutePath = destinationFile.absolutePath
                profileDao.updateCoverLocalPath(userId, absolutePath)
                return@withContext absolutePath
            }
        } catch (e: Exception) {
            Log.w("ProfileAvatarCache", "Failed to cache cover photo for $userId: ${e.message}")
        }
        null
    }

    fun clearMediaCache() {
        try {
            if (mediaDir.exists()) {
                mediaDir.deleteRecursively()
            }
        } catch (e: Exception) {
            Log.e("ProfileAvatarCache", "Failed to clear media cache", e)
        }
    }

    private fun downloadFile(remoteUrl: String, destinationFile: File): Boolean {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(remoteUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            connection.instanceFollowRedirects = true
            connection.connect()

            if (connection.responseCode in 200..299) {
                val tempFile = File(destinationFile.parentFile, "${destinationFile.name}.tmp")
                connection.inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                if (tempFile.exists() && tempFile.length() > 0) {
                    if (destinationFile.exists()) destinationFile.delete()
                    tempFile.renameTo(destinationFile)
                    true
                } else {
                    tempFile.delete()
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            Log.w("ProfileAvatarCache", "HTTP download error: ${e.message}")
            false
        } finally {
            connection?.disconnect()
        }
    }
}
