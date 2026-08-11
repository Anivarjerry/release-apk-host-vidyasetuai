package com.vidyasetuai.feature_profile.data.mapper

import com.vidyasetuai.feature_profile.data.local.entity.ContributorVerificationEntity
import com.vidyasetuai.feature_profile.data.local.entity.UserProfileEntity
import com.vidyasetuai.feature_profile.data.remote.dto.VerificationDto
import com.vidyasetuai.feature_profile.data.remote.dto.UserProfileDto
import com.vidyasetuai.feature_profile.domain.model.ContributorVerification
import com.vidyasetuai.feature_profile.domain.model.UserProfile

fun UserProfileDto.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        userId = this.user_id,
        email = this.email,
        isActive = this.is_active,
        isDeleted = this.is_deleted,
        username = this.username,
        firstName = this.first_name,
        lastName = this.last_name,
        fullName = this.full_name,
        profilePictureUrl = this.profile_picture_url,
        coverPhotoUrl = this.cover_photo_url,
        bio = this.bio,
        preferredLanguage = this.preferred_language,
        isVerified = this.is_verified,
        gender = this.gender,
        dateOfBirth = this.date_of_birth,
        totalInspiringCount = this.total_inspiring_count,
        totalInspiredCount = this.total_inspired_count
    )
}

fun UserProfileDto.toDomain(): UserProfile {
    return UserProfile(
        userId = this.user_id,
        email = this.email,
        isActive = this.is_active,
        isDeleted = this.is_deleted,
        username = this.username,
        firstName = this.first_name,
        lastName = this.last_name,
        fullName = this.full_name,
        profilePictureUrl = this.profile_picture_url,
        coverPhotoUrl = this.cover_photo_url,
        bio = this.bio,
        preferredLanguage = this.preferred_language,
        isVerified = this.is_verified,
        gender = this.gender,
        dateOfBirth = this.date_of_birth,
        totalInspiringCount = this.total_inspiring_count,
        totalInspiredCount = this.total_inspired_count
    )
}

fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        userId = this.userId,
        email = this.email,
        isActive = this.isActive,
        isDeleted = this.isDeleted,
        username = this.username,
        firstName = this.firstName,
        lastName = this.lastName,
        fullName = this.fullName,
        profilePictureUrl = this.profilePictureUrl,
        profilePictureLocalPath = this.profilePictureLocalPath,
        coverPhotoUrl = this.coverPhotoUrl,
        coverPhotoLocalPath = this.coverPhotoLocalPath,
        bio = this.bio,
        preferredLanguage = this.preferredLanguage,
        isVerified = this.isVerified,
        gender = this.gender,
        dateOfBirth = this.dateOfBirth,
        totalInspiringCount = this.totalInspiringCount,
        totalInspiredCount = this.totalInspiredCount
    )
}

fun UserProfile.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        userId = this.userId,
        email = this.email,
        isActive = this.isActive,
        isDeleted = this.isDeleted,
        username = this.username,
        firstName = this.firstName,
        lastName = this.lastName,
        fullName = this.fullName,
        profilePictureUrl = this.profilePictureUrl,
        profilePictureLocalPath = this.profilePictureLocalPath,
        coverPhotoUrl = this.coverPhotoUrl,
        coverPhotoLocalPath = this.coverPhotoLocalPath,
        bio = this.bio,
        preferredLanguage = this.preferredLanguage,
        isVerified = this.isVerified,
        gender = this.gender,
        dateOfBirth = this.dateOfBirth,
        totalInspiringCount = this.totalInspiringCount,
        totalInspiredCount = this.totalInspiredCount
    )
}

fun UserProfile.toDto(): UserProfileDto {
    return UserProfileDto(
        user_id = this.userId,
        email = this.email,
        is_active = this.isActive,
        is_deleted = this.isDeleted,
        username = this.username,
        first_name = this.firstName,
        last_name = this.lastName,
        full_name = this.fullName,
        profile_picture_url = this.profilePictureUrl,
        cover_photo_url = this.coverPhotoUrl,
        bio = this.bio,
        preferred_language = this.preferredLanguage,
        is_verified = this.isVerified,
        gender = this.gender,
        date_of_birth = this.dateOfBirth,
        total_inspiring_count = this.totalInspiringCount,
        total_inspired_count = this.totalInspiredCount
    )
}

fun VerificationDto.toEntity(): ContributorVerificationEntity {
    return ContributorVerificationEntity(
        id = this.id,
        contributorType = this.contributor_type,
        userId = this.user_id,
        status = this.status,
        applicantNote = this.applicant_note,
        rejectionReason = this.rejection_reason
    )
}

fun ContributorVerificationEntity.toDomain(): ContributorVerification {
    return ContributorVerification(
        id = this.id,
        contributorType = this.contributorType,
        userId = this.userId,
        status = this.status,
        applicantNote = this.applicantNote,
        rejectionReason = this.rejectionReason
    )
}