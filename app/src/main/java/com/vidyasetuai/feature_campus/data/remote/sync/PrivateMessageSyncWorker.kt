package com.vidyasetuai.feature_campus.data.remote.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vidyasetuai.core.database.AppDatabase
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_campus.data.local.dao.CampusDao
import com.vidyasetuai.feature_campus.data.local.entity.PrivateMessageEntity
import com.vidyasetuai.feature_campus.data.remote.datasource.CampusRemoteDataSource
import com.vidyasetuai.feature_profile.data.remote.datasource.ProfileRemoteDataSource
import io.github.jan.supabase.postgrest.postgrest

/**
 * Background WorkManager Worker to sync pending offline private messages
 * whenever active internet connectivity is restored.
 */
class PrivateMessageSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val campusDao = database.campusDao()
            val remoteDS = CampusRemoteDataSource()
            val profileRemoteDS = ProfileRemoteDataSource()

            val unsyncedMessages = campusDao.getUnsyncedPrivateMessages()
            if (unsyncedMessages.isEmpty()) {
                return Result.success()
            }

            for (msg in unsyncedMessages) {
                try {
                    val peerPublicKey = fetchPeerPublicKey(msg.roomId, msg.senderId, campusDao, profileRemoteDS)
                    val encryptedPayload = if (msg.messageText != null && peerPublicKey != null) {
                        com.vidyasetuai.core.security.CryptoManager.encryptMessage(msg.messageText, peerPublicKey)
                    } else {
                        msg.messageText
                    }

                    val sentDto = remoteDS.sendPrivateMessage(
                        roomId = msg.roomId,
                        senderId = msg.senderId,
                        text = encryptedPayload,
                        mediaUrl = msg.mediaUrl
                    )

                    campusDao.updatePrivateMessageStatus(
                        localId = msg.localId,
                        serverId = sentDto.id,
                        status = "SENT"
                    )
                } catch (e: Exception) {
                    android.util.Log.e("PrivateMessageSyncWorker", "Failed to sync message ${msg.localId}", e)
                }
            }

            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("PrivateMessageSyncWorker", "Error executing sync worker", e)
            Result.retry()
        }
    }

    private suspend fun fetchPeerPublicKey(
        roomId: String,
        senderId: String,
        campusDao: CampusDao,
        profileRemoteDS: ProfileRemoteDataSource
    ): String? {
        return try {
            val room = campusDao.getPrivateRoomById(roomId) ?: return null
            val peerId = if (room.user1Id == senderId) room.user2Id else room.user1Id
            val peerProfile = profileRemoteDS.getProfile(peerId)
            peerProfile.public_key
        } catch (e: Exception) {
            null
        }
    }
}
