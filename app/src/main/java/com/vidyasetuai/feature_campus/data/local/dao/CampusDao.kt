package com.vidyasetuai.feature_campus.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_campus.data.local.entity.ConnectionRequestEntity
import com.vidyasetuai.feature_campus.data.local.entity.ModerationSettingsEntity
import com.vidyasetuai.feature_campus.data.local.entity.PrivateMessageEntity
import com.vidyasetuai.feature_campus.data.local.entity.PrivateRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusDao {
    // ── Private Rooms ─────────────────────────────────────────────────────────
    @Query("SELECT * FROM private_rooms ORDER BY updated_at DESC")
    fun getPrivateRoomsFlow(): Flow<List<PrivateRoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrivateRooms(rooms: List<PrivateRoomEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrivateRoom(room: PrivateRoomEntity)

    @Query("SELECT * FROM private_rooms WHERE id = :roomId")
    suspend fun getPrivateRoomById(roomId: String): PrivateRoomEntity?

    @Query("SELECT * FROM private_rooms WHERE (user1_id = :userA AND user2_id = :userB) OR (user1_id = :userB AND user2_id = :userA) LIMIT 1")
    suspend fun getPrivateRoomForUsers(userA: String, userB: String): PrivateRoomEntity?

    @Query("UPDATE private_rooms SET unread_count = 0 WHERE id = :roomId")
    suspend fun markRoomAsRead(roomId: String)

    @Query("UPDATE private_rooms SET last_message_text = :text, last_message_time = :time, updated_at = :time WHERE id = :roomId")
    suspend fun updateRoomLastMessage(roomId: String, text: String, time: String)

    @Query("UPDATE private_rooms SET unread_count = unread_count + 1 WHERE id = :roomId")
    suspend fun incrementRoomUnreadCount(roomId: String)

    // ── Private Messages ──────────────────────────────────────────────────────
    @Query("SELECT * FROM private_messages WHERE room_id = :roomId ORDER BY created_at ASC")
    fun getPrivateMessagesFlow(roomId: String): Flow<List<PrivateMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrivateMessages(messages: List<PrivateMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrivateMessage(message: PrivateMessageEntity)

    @Query("SELECT * FROM private_messages WHERE local_id = :localId")
    suspend fun getPrivateMessageByLocalId(localId: String): PrivateMessageEntity?

    @Query("SELECT * FROM private_messages WHERE server_id = :serverId LIMIT 1")
    suspend fun getPrivateMessageByServerId(serverId: String): PrivateMessageEntity?

    @Query("SELECT * FROM private_messages WHERE sync_status = 'PENDING'")
    suspend fun getUnsyncedPrivateMessages(): List<PrivateMessageEntity>

    @Query("UPDATE private_messages SET server_id = :serverId, sync_status = :status WHERE local_id = :localId")
    suspend fun updatePrivateMessageStatus(localId: String, serverId: String?, status: String)

    @Query("UPDATE private_messages SET sync_status = 'READ' WHERE room_id = :roomId AND sender_id != :currentUserId")
    suspend fun markMessagesAsRead(roomId: String, currentUserId: String)

    @Query("UPDATE private_messages SET is_saved = :isSaved WHERE local_id = :localId OR server_id = :serverId")
    suspend fun updatePrivateMessageSavedStatus(localId: String, serverId: String?, isSaved: Boolean)

    @Query("DELETE FROM private_messages WHERE local_id = :localId")
    suspend fun deletePrivateMessageByLocalId(localId: String)

    @Query("DELETE FROM private_messages WHERE is_saved = 0 AND created_at < :cutoffTime")
    suspend fun deleteExpiredUnsavedPrivateMessages(cutoffTime: String)

    // ── Connection Requests ──────────────────────────────────────────────────
    @Query("SELECT * FROM connection_requests ORDER BY created_at DESC")
    fun getConnectionRequestsFlow(): Flow<List<ConnectionRequestEntity>>

    @Query("SELECT * FROM connection_requests ORDER BY created_at DESC")
    suspend fun getConnectionRequests(): List<ConnectionRequestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnectionRequests(requests: List<ConnectionRequestEntity>)

    @Query("DELETE FROM connection_requests WHERE user_id = :userId")
    suspend fun deleteConnectionRequest(userId: String)

    @Query("DELETE FROM connection_requests")
    suspend fun clearConnectionRequests()

    // ── Moderation Settings ──────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModerationSettings(settings: ModerationSettingsEntity)

    @Query("SELECT * FROM moderation_settings WHERE id = 1")
    suspend fun getModerationSettings(): ModerationSettingsEntity?
}