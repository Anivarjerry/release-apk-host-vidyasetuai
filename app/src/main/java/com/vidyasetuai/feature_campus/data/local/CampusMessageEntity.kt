package com.vidyasetuai.feature_campus.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 0ms Offline-First Entity for Campus E2EE Chat Messages, Delivery Ticks & Media.
 * Status progression: PENDING (🕒) -> SENT (✓) -> DELIVERED (✓✓) -> READ (✓✓ Blue) -> FAILED (⚠️).
 */
@Entity(
    tableName = "campus_messages",
    indices = [
        Index(value = ["conversation_id", "created_at"]),
        Index(value = ["recipient_id", "status"]),
        Index(value = ["sender_id", "status"])
    ]
)
data class CampusMessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "conversation_id")
    val conversationId: String,

    @ColumnInfo(name = "sender_id")
    val senderId: String,

    @ColumnInfo(name = "recipient_id")
    val recipientId: String,

    @ColumnInfo(name = "text")
    val text: String, // 0ms Instant Plaintext Read

    @ColumnInfo(name = "encrypted_payload")
    val encryptedPayload: String? = null, // Remote Cloud Sync Payload (Zero Plaintext on Server)

    @ColumnInfo(name = "media_url")
    val mediaUrl: String? = null,

    @ColumnInfo(name = "media_local_path")
    val mediaLocalPath: String? = null, // Local disk path for downloaded media

    @ColumnInfo(name = "media_type")
    val mediaType: String = "TEXT", // 'TEXT', 'IMAGE', 'VOICE', 'FILE'

    @ColumnInfo(name = "status")
    val status: String = "SENT", // 'PENDING', 'SENT', 'DELIVERED', 'READ', 'FAILED'

    @ColumnInfo(name = "is_saved")
    val isSaved: Boolean = false, // Starred/Saved messages never vanish

    @ColumnInfo(name = "expires_at")
    val expiresAt: String? = null,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "SYNCED", // 'PENDING_OUTBOX', 'SYNCED', 'FAILED'

    @ColumnInfo(name = "sync_version")
    val syncVersion: Long = 1L,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "delivered_at")
    val deliveredAt: String? = null,

    @ColumnInfo(name = "read_at")
    val readAt: String? = null
)
