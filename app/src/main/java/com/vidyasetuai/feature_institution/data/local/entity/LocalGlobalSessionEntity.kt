package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_global_sessions")
data class LocalGlobalSessionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val startingDate: String?,
    val endingDate: String?,
    val updateBeforeEndingDays: Int?
)
