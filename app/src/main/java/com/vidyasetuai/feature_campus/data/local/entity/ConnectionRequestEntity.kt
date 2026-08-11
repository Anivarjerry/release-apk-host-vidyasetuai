package com.vidyasetuai.feature_campus.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "connection_requests")
data class ConnectionRequestEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "email")
    val email: String = "",

    @ColumnInfo(name = "username")
    val username: String? = null,

    @ColumnInfo(name = "first_name")
    val firstName: String? = null,

    @ColumnInfo(name = "last_name")
    val lastName: String? = null,

    @ColumnInfo(name = "full_name")
    val fullName: String? = null,

    @ColumnInfo(name = "profile_picture_url")
    val profilePictureUrl: String? = null,

    @ColumnInfo(name = "bio")
    val bio: String? = null,

    @ColumnInfo(name = "request_direction")
    val requestDirection: String = "INCOMING", // "INCOMING" or "OUTGOING"

    @ColumnInfo(name = "created_at")
    val createdAt: String = "",

    @ColumnInfo(name = "updated_at")
    val updatedAt: String = ""
)
