package com.vidyasetuai.feature_profile.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_profile.domain.model.ProfileInspiration

/**
 * 0ms Offline-First Room DB Entity for Inspirations (Following/Followers).
 */
@Entity(tableName = "profile_inspirations")
data class ProfileInspirationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "target_user_id")
    val targetUserId: String,

    @ColumnInfo(name = "peer_name")
    val peerName: String,

    @ColumnInfo(name = "peer_username")
    val peerUsername: String,

    @ColumnInfo(name = "peer_avatar_url")
    val peerAvatarUrl: String? = null,

    @ColumnInfo(name = "peer_avatar_local_path")
    val peerAvatarLocalPath: String? = null,

    @ColumnInfo(name = "peer_bio")
    val peerBio: String? = null,

    @ColumnInfo(name = "relation_type")
    val relationType: String, // "FOLLOWING" or "FOLLOWER"

    @ColumnInfo(name = "is_mutual")
    val isMutual: Boolean = false,

    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean = false,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
) {
    fun toDomain(): ProfileInspiration {
        return ProfileInspiration(
            id = id,
            userId = userId,
            targetUserId = targetUserId,
            peerName = peerName,
            peerUsername = peerUsername,
            peerAvatarUrl = peerAvatarUrl,
            peerAvatarLocalPath = peerAvatarLocalPath,
            peerBio = peerBio,
            relationType = relationType,
            isMutual = isMutual,
            isVerified = isVerified,
            updatedAt = updatedAt
        )
    }
}
