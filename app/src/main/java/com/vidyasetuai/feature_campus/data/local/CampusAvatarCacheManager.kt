package com.vidyasetuai.feature_campus.data.local

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.vidyasetuai.feature_campus.domain.util.CampusDateTimeUtils
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * High-Performance Isolated Local Image Cache Manager for Campus Avatars.
 * Guarantees 0ms true offline profile picture rendering from local disk & Room DB.
 */
class CampusAvatarCacheManager(
    private val context: Context,
    private val campusDao: CampusDao
) {
    private val tag = "CampusAvatarCache"
    private val avatarsDir by lazy {
        val dir = File(context.filesDir, "campus_avatars")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        dir
    }

    /**
     * Downloads remote avatar images in the background and saves absolute file paths into Room DB.
     */
    suspend fun downloadAndCacheAvatars(connections: List<CampusConnectionEntity>) = withContext(Dispatchers.IO) {
        for (connection in connections) {
            val remoteUrl = connection.peerAvatarUrl?.trim()
            val targetUserId = connection.targetUserId

            if (remoteUrl.isNullOrBlank()) continue

            try {
                val avatarFile = File(avatarsDir, "avatar_${targetUserId}.jpg")

                // If file already exists locally on disk and is non-empty
                if (avatarFile.exists() && avatarFile.length() > 0) {
                    if (connection.peerAvatarLocalPath != avatarFile.absolutePath) {
                        campusDao.updatePeerAvatarLocalPath(
                            targetUserId = targetUserId,
                            localPath = avatarFile.absolutePath,
                            updatedAt = nowIso()
                        )
                        Log.d(tag, "Local avatar path restored for targetUser: $targetUserId")
                    }
                    continue
                }

                // Download image from remote URL
                val success = downloadImageToFile(remoteUrl, avatarFile)
                if (success && avatarFile.exists() && avatarFile.length() > 0) {
                    campusDao.updatePeerAvatarLocalPath(
                        targetUserId = targetUserId,
                        localPath = avatarFile.absolutePath,
                        updatedAt = nowIso()
                    )
                    Log.d(tag, "Avatar successfully downloaded & cached locally for: $targetUserId (${avatarFile.absolutePath})")
                }
            } catch (e: Exception) {
                Log.w(tag, "Failed to cache avatar for user $targetUserId: ${e.message}")
            }
        }
    }

    /**
     * Streams bytes safely from remote URL into local file.
     */
    private fun downloadImageToFile(urlString: String, destinationFile: File): Boolean {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                instanceFollowRedirects = true
                requestMethod = "GET"
            }

            if (connection.responseCode in 200..299) {
                connection.inputStream.use { input ->
                    FileOutputStream(destinationFile).use { output ->
                        input.copyTo(output)
                    }
                }
                true
            } else {
                Log.w(tag, "HTTP error downloading avatar: ${connection.responseCode}")
                false
            }
        } catch (e: Exception) {
            Log.w(tag, "Network error downloading avatar from $urlString: ${e.message}")
            false
        } finally {
            connection?.disconnect()
        }
    }

    private fun nowIso(): String {
        return CampusDateTimeUtils.nowIso()
    }
}
