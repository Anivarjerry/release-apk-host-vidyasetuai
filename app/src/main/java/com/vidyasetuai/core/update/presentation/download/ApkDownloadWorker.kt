package com.vidyasetuai.core.update.presentation.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.TimeUnit

class ApkDownloadWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val tag = "VidyaSetu_ApkDownload"
    private val channelId = "vidyasetu_app_updates"
    private val notificationId = 9999

    override suspend fun doWork(): Result {
        val apkUrl = inputData.getString("apk_url") ?: return Result.failure()
        val destinationPath = inputData.getString("destination_path") ?: return Result.failure()

        val destinationFile = File(destinationPath)
        val parentDir = destinationFile.parentFile
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs()
        }

        // Initialize progress
        setProgress(workDataOf(
            "status" to "STARTING",
            "progress" to 0,
            "downloaded_bytes" to 0L,
            "total_bytes" to 0L
        ))

        // Trigger foreground notification
        try {
            setForeground(getForegroundInfo())
        } catch (e: Exception) {
            Log.e(tag, "Failed to set foreground service: ${e.message}")
        }

        return try {
            downloadFile(apkUrl, destinationFile)
            Result.success(workDataOf("file_path" to destinationFile.absolutePath))
        } catch (e: Exception) {
            Log.e(tag, "Download failed: ${e.message}", e)
            Result.failure(workDataOf("error_message" to e.message))
        }
    }

    private suspend fun downloadFile(url: String, file: File) {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val checkRequest = Request.Builder().url(url).build()
        val checkResponse = client.newCall(checkRequest).execute()
        if (!checkResponse.isSuccessful) {
            checkResponse.close()
            throw Exception("Server returned HTTP: ${checkResponse.code}")
        }
        val remoteLength = checkResponse.body?.contentLength() ?: -1L
        checkResponse.close()

        val existingLength = if (file.exists()) file.length() else 0L
        Log.d(tag, "Remote length: $remoteLength, Local length: $existingLength")

        if (remoteLength > 0 && existingLength == remoteLength) {
            Log.d(tag, "File is already fully downloaded. Skipping.")
            setProgress(workDataOf(
                "status" to "DOWNLOADING",
                "progress" to 100,
                "downloaded_bytes" to existingLength,
                "total_bytes" to remoteLength
            ))
            return
        }

        val startBytes = if (existingLength < remoteLength && existingLength > 0) {
            existingLength
        } else {
            if (file.exists()) {
                file.delete()
            }
            0L
        }

        val requestBuilder = Request.Builder().url(url)
        if (startBytes > 0) {
            requestBuilder.header("Range", "bytes=$startBytes-")
            Log.d(tag, "Resuming download from byte: $startBytes")
        }

        val request = requestBuilder.build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            response.close()
            throw Exception("Server returned HTTP: ${response.code}")
        }

        val responseBody = response.body ?: throw Exception("Response body is null")
        val isPartial = response.code == 206
        val totalLength = if (isPartial) {
            startBytes + responseBody.contentLength()
        } else {
            responseBody.contentLength()
        }

        Log.d(tag, "Total bytes to download: $totalLength (Is partial resume: $isPartial)")

        val fileMode = if (isPartial) "rw" else "rwd"
        val randomAccessFile = RandomAccessFile(file, fileMode)
        if (isPartial) {
            randomAccessFile.seek(startBytes)
        } else {
            randomAccessFile.setLength(0) // Truncate existing file if server starts from scratch
        }

        val inputStream = responseBody.byteStream()
        val buffer = ByteArray(8192)
        var bytesRead: Int
        var downloadedBytes = if (isPartial) startBytes else 0L
        var lastProgressUpdateTime = 0L

        try {
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                if (isStopped) {
                    Log.d(tag, "Download stopped by worker cancellation")
                    val progress = if (totalLength > 0) ((downloadedBytes * 100) / totalLength).toInt() else 0
                    setProgress(workDataOf(
                        "status" to "PAUSED",
                        "progress" to progress,
                        "downloaded_bytes" to downloadedBytes,
                        "total_bytes" to totalLength
                    ))
                    break
                }

                randomAccessFile.write(buffer, 0, bytesRead)
                downloadedBytes += bytesRead

                val currentTime = System.currentTimeMillis()
                if (currentTime - lastProgressUpdateTime > 500 || downloadedBytes == totalLength) {
                    val progress = if (totalLength > 0) ((downloadedBytes * 100) / totalLength).toInt() else 0
                    setProgress(workDataOf(
                        "status" to "DOWNLOADING",
                        "progress" to progress,
                        "downloaded_bytes" to downloadedBytes,
                        "total_bytes" to totalLength
                    ))
                    updateNotification(progress, downloadedBytes, totalLength)
                    lastProgressUpdateTime = currentTime
                }
            }
        } finally {
            inputStream.close()
            responseBody.close()
            randomAccessFile.close()
        }
    }

    private fun updateNotification(progress: Int, downloaded: Long, total: Long) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = createNotification(progress, downloaded, total)
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotification(progress: Int, downloaded: Long, total: Long): android.app.Notification {
        val downloadedMb = String.format("%.2f", downloaded / (1024.0 * 1024.0))
        val totalMb = String.format("%.2f", total / (1024.0 * 1024.0))

        val text = if (total > 0) {
            "$progress% ($downloadedMb MB / $totalMb MB)"
        } else {
            "Downloading..."
        }

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("Downloading App Update")
            .setContentText(text)
            .setSmallIcon(com.vidyasetuai.R.mipmap.ic_launcher)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, progress, false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        createNotificationChannel()
        val notification = createNotification(0, 0, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "App Updates",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Shows progress for downloading application updates"
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
