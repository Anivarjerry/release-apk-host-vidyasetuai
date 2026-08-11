package com.vidyasetuai.feature_campus.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "private_messages",
    indices = [
        Index(value = ["room_id"]),
        Index(value = ["sync_status"])
    ]
)
data class PrivateMessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "local_id")
    val localId: String,

    @ColumnInfo(name = "server_id")
    val serverId: String? = null,

    @ColumnInfo(name = "room_id")
    val roomId: String,

    @ColumnInfo(name = "sender_id")
    val senderId: String,

    @ColumnInfo(name = "message_text")
    val messageText: String? = null,

    @ColumnInfo(name = "media_url")
    val mediaUrl: String? = null,

    @ColumnInfo(name = "media_local_path")
    val mediaLocalPath: String? = null,

    @ColumnInfo(name = "is_saved")
    val isSaved: Boolean = false,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "PENDING", // PENDING, SENT, DELIVERED, READ, FAILED

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String = ""
)
