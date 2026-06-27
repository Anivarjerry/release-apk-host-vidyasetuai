package com.vidyasetuai.feature_campus.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_campus.data.local.entity.MessageEntity
import com.vidyasetuai.feature_campus.data.local.entity.ModerationSettingsEntity
import com.vidyasetuai.feature_campus.data.local.entity.RoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusDao {
    @Query("SELECT * FROM rooms WHERE is_active = 1 AND is_deleted = 0")
    fun getRoomsFlow(): Flow<List<RoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<RoomEntity>)

    @Query("SELECT * FROM rooms WHERE id = :roomId")
    suspend fun getRoomById(roomId: String): RoomEntity?

    @Query("SELECT * FROM messages WHERE room_id = :roomId AND is_deleted = 0 AND is_hidden = 0 ORDER BY created_at ASC")
    fun getMessagesFlow(roomId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE is_synced = 0")
    suspend fun getUnsyncedMessages(): List<MessageEntity>

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: String)

    @Query("DELETE FROM messages WHERE created_at < :timestamp")
    suspend fun deleteMessagesOlderThan(timestamp: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModerationSettings(settings: ModerationSettingsEntity)

    @Query("SELECT * FROM moderation_settings WHERE id = 1")
    suspend fun getModerationSettings(): ModerationSettingsEntity?
}