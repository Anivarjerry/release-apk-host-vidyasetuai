package com.vidyasetuai.feature_profile.domain.model

/**
 * Domain model representing an Inspiration peer relationship (Following/Followers).
 */
data class ProfileInspiration(
    val id: String,
    val userId: String,
    val targetUserId: String,
    val peerName: String,
    val peerUsername: String,
    val peerAvatarUrl: String? = null,
    val peerAvatarLocalPath: String? = null,
    val peerBio: String? = null,
    val relationType: String, // "FOLLOWING" or "FOLLOWER"
    val isMutual: Boolean = false,
    val isVerified: Boolean = false,
    val updatedAt: String = ""
)
