package com.vidyasetuai.feature_campus.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "private_rooms")
data class PrivateRoomEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user1_id")
    val user1Id: String,

    @ColumnInfo(name = "user2_id")
    val user2Id: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "last_message_text")
    val lastMessageText: String? = null,

    @ColumnInfo(name = "last_message_time")
    val lastMessageTime: String? = null,

    @ColumnInfo(name = "unread_count")
    val unreadCount: Int = 0,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String = ""
)
