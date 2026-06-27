package com.vidyasetuai.feature_campus.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moderation_settings")
data class ModerationSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    @ColumnInfo(name = "blocked_keywords")
    val blockedKeywords: List<String>,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
