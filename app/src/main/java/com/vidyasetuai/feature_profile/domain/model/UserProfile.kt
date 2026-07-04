package com.vidyasetuai.feature_profile.domain.model

data class UserProfile(
    val userId: String,
    val email: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val username: String?,
    val firstName: String?,
    val lastName: String?,
    val fullName: String?,
    val profilePictureUrl: String?,
    val coverPhotoUrl: String?,
    val bio: String?,
    val preferredLanguage: String?,
    val isVerified: Boolean,
    val gender: String?,
    val dateOfBirth: String?,
    val totalInspiringCount: Int = 0,
    val totalInspiredCount: Int = 0
)