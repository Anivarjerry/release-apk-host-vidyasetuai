package com.vidyasetuai.feature_profile.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    
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
    
    @ColumnInfo(name = "cover_photo_url")
    val coverPhotoUrl: String?,
    
    @ColumnInfo(name = "bio")
    val bio: String?,
    
    @ColumnInfo(name = "preferred_language")
    val preferredLanguage: String?,
    
    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean,
    
    @ColumnInfo(name = "gender")
    val gender: String?,
    
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String?
)
