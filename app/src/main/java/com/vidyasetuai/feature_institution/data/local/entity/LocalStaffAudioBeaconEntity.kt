package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_staff_audio_beacons")
data class LocalStaffAudioBeaconEntity(
    @PrimaryKey
    @ColumnInfo(name = "staff_id")
    val staffId: String,

    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,

    @ColumnInfo(name = "audio_code")
    val audioCode: String,

    @ColumnInfo(name = "secret_salt")
    val secretSalt: String,

    @ColumnInfo(name = "valid_until")
    val validUntil: String,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long
)
