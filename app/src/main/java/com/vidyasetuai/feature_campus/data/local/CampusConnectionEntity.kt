package com.vidyasetuai.feature_campus.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 0ms Offline-First Entity for Campus Connections & Peer Profile Disk Cache.
 * Stores follower/following relationship, mutual state, and downloaded avatar path.
 */
@Entity(
    tableName = "campus_connections",
    indices = [
        Index(value = ["target_user_id"]),
        Index(value = ["user_id", "is_mutual"]),
        Index(value = ["user_id", "updated_at"])
    ]
)
data class CampusConnectionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "target_user_id")
    val targetUserId: String,

    @ColumnInfo(name = "status")
    val status: String = "FOLLOWING", // 'FOLLOWING', 'BLOCKED'

    @ColumnInfo(name = "is_mutual")
    val isMutual: Boolean = false,

    @ColumnInfo(name = "peer_name")
    val peerName: String? = null,

    @ColumnInfo(name = "peer_username")
    val peerUsername: String? = null,

    @ColumnInfo(name = "peer_avatar_url")
    val peerAvatarUrl: String? = null,

    @ColumnInfo(name = "peer_avatar_local_path")
    val peerAvatarLocalPath: String? = null, // Disk cache path (0ms DP load in offline/airplane mode)

    @ColumnInfo(name = "peer_bio")
    val peerBio: String? = null,

    // WhatsApp-grade 0ms Denormalized Fields for Chat List
    @ColumnInfo(name = "last_message_text")
    val lastMessageText: String? = null,

    @ColumnInfo(name = "last_message_time")
    val lastMessageTime: String? = null,

    @ColumnInfo(name = "last_message_status")
    val lastMessageStatus: String? = null,

    @ColumnInfo(name = "unread_count")
    val unreadCount: Int = 0,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "SYNCED", // 'PENDING_SYNC', 'SYNCED'

    @ColumnInfo(name = "sync_version")
    val syncVersion: Long = 1L,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
