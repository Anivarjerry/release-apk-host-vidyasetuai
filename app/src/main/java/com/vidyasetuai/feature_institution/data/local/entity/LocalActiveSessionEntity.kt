package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_active_session")
data class LocalActiveSessionEntity(
    @PrimaryKey val id: String,
    val name: String
)
