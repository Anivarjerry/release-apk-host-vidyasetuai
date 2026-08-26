package com.vidyasetuai.feature_profile.domain.model

/**
 * Domain model representing a User Profile.
 */
data class UserProfile(
    val userId: String,
    val username: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val profilePictureLocalPath: String? = null,
    val coverPhotoUrl: String? = null,
    val coverPhotoLocalPath: String? = null,
    val gender: String? = null,
    val dateOfBirth: String? = null,
    val bio: String? = null,
    val preferredLanguage: String? = null,
    val isPrivate: Boolean = false,
    val isVerified: Boolean = false,
    val totalInspiringCount: Int = 0,
    val totalInspiredCount: Int = 0,
    val totalCaseStudiesCount: Int = 0,
    val isMe: Boolean = false,
    val lastSyncedAt: String = ""
)