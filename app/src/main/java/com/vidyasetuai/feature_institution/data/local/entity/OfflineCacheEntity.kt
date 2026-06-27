package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_cache")
data class OfflineCacheEntity(
    @PrimaryKey val cacheKey: String,
    val responseJson: String,
    val timestamp: Long
)
