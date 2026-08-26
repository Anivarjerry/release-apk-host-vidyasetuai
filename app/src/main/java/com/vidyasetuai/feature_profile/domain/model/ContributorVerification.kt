package com.vidyasetuai.feature_profile.domain.model

/**
 * Domain model representing a Contributor Verification application.
 */
data class ContributorVerification(
    val id: String,
    val userId: String,
    val contributorType: String,
    val status: String,
    val applicantNote: String? = null,
    val rejectionReason: String? = null,
    val createdAt: String = ""
)
