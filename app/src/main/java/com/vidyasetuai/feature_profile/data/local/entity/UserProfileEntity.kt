package com.vidyasetuai.feature_profile.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "email")
    val email: String = "",
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,
    
    @ColumnInfo(name = "username")
    val username: String?,
    
    @ColumnInfo(name = "first_name")
    val firstName: String?,
    
    @ColumnInfo(name = "last_name")
    val lastName: String?,
    
    @ColumnInfo(name = "full_name")
    val fullName: String?,
    
    @ColumnInfo(name = "profile_picture_url")
    val profilePictureUrl: String?,
    
    @ColumnInfo(name = "profile_picture_local_path")
    val profilePictureLocalPath: String? = null,
    
    @ColumnInfo(name = "cover_photo_url")
    val coverPhotoUrl: String?,
    
    @ColumnInfo(name = "cover_photo_local_path")
    val coverPhotoLocalPath: String? = null,
    
    @ColumnInfo(name = "gender")
    val gender: String?,
    
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String?,
    
    @ColumnInfo(name = "bio")
    val bio: String?,
    
    @ColumnInfo(name = "preferred_language")
    val preferredLanguage: String?,
    
    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean = false,
    
    @ColumnInfo(name = "is_verified_status")
    val isVerifiedStatus: String? = null,
    
    @ColumnInfo(name = "active_workspace_id")
    val activeWorkspaceId: String? = null,
    
    @ColumnInfo(name = "active_journey_id")
    val activeJourneyId: String? = null,

    @ColumnInfo(name = "total_inspiring_count")
    val totalInspiringCount: Int = 0,

    @ColumnInfo(name = "total_inspired_count")
    val totalInspiredCount: Int = 0,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long = 0L,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String = "SYNCED"
)

