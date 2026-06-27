package com.vidyasetuai.feature_campus.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String, // Temporary UUID for local-only, server-generated UUID after sync
    @ColumnInfo(name = "room_id")
    val roomId: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    val content: String,
    @ColumnInfo(name = "is_hidden")
    val isHidden: Boolean,
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: String, // ISO timestamp
    @ColumnInfo(name = "updated_at")
    val updatedAt: String,
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = true,
    @ColumnInfo(name = "is_failed")
    val isFailed: Boolean = false
)
