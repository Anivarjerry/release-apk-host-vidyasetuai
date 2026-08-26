package com.vidyasetuai.feature_profile.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_profile.domain.model.ContributorVerification

/**
 * 0ms Offline-First Room DB Entity for Contributor Verification Applications.
 */
@Entity(tableName = "profile_verifications")
data class ProfileVerificationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "contributor_type")
    val contributorType: String,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "applicant_note")
    val applicantNote: String? = null,

    @ColumnInfo(name = "rejection_reason")
    val rejectionReason: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String
) {
    fun toDomain(): ContributorVerification {
        return ContributorVerification(
            id = id,
            userId = userId,
            contributorType = contributorType,
            status = status,
            applicantNote = applicantNote,
            rejectionReason = rejectionReason,
            createdAt = createdAt
        )
    }
}
