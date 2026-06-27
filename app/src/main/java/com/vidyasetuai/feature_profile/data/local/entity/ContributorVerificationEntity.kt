package com.vidyasetuai.feature_profile.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contributor_verifications")
data class ContributorVerificationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "contributor_type")
    val contributorType: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "status")
    val status: String,
    
    @ColumnInfo(name = "applicant_note")
    val applicantNote: String?,
    
    @ColumnInfo(name = "rejection_reason")
    val rejectionReason: String?
)
