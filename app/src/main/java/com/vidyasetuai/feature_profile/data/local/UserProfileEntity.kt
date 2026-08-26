package com.vidyasetuai.feature_profile.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_profile.domain.model.UserProfile

/**
 * 0ms Offline-First Room DB Entity for Cached User Profiles.
 */
@Entity(tableName = "user_profile_cache")
data class UserProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "first_name")
    val firstName: String? = null,

    @ColumnInfo(name = "last_name")
    val lastName: String? = null,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "profile_picture_url")
    val profilePictureUrl: String? = null,

    @ColumnInfo(name = "profile_picture_local_path")
    val profilePictureLocalPath: String? = null,

    @ColumnInfo(name = "cover_photo_url")
    val coverPhotoUrl: String? = null,

    @ColumnInfo(name = "cover_photo_local_path")
    val coverPhotoLocalPath: String? = null,

    @ColumnInfo(name = "gender")
    val gender: String? = null,

    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String? = null,

    @ColumnInfo(name = "bio")
    val bio: String? = null,

    @ColumnInfo(name = "preferred_language")
    val preferredLanguage: String? = null,

    @ColumnInfo(name = "is_private")
    val isPrivate: Boolean = false,

    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean = false,

    @ColumnInfo(name = "total_inspiring_count")
    val totalInspiringCount: Int = 0,

    @ColumnInfo(name = "total_inspired_count")
    val totalInspiredCount: Int = 0,

    @ColumnInfo(name = "total_case_studies_count")
    val totalCaseStudiesCount: Int = 0,

    @ColumnInfo(name = "is_me")
    val isMe: Boolean = false,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: String
) {
    fun toDomain(): UserProfile {
        return UserProfile(
            userId = userId,
            username = username,
            firstName = firstName,
            lastName = lastName,
            fullName = fullName,
            profilePictureUrl = profilePictureUrl,
            profilePictureLocalPath = profilePictureLocalPath,
            coverPhotoUrl = coverPhotoUrl,
            coverPhotoLocalPath = coverPhotoLocalPath,
            gender = gender,
            dateOfBirth = dateOfBirth,
            bio = bio,
            preferredLanguage = preferredLanguage,
            isPrivate = isPrivate,
            isVerified = isVerified,
            totalInspiringCount = totalInspiringCount,
            totalInspiredCount = totalInspiredCount,
            totalCaseStudiesCount = totalCaseStudiesCount,
            isMe = isMe,
            lastSyncedAt = lastSyncedAt
        )
    }
}
