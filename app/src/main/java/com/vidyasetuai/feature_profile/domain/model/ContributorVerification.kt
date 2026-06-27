package com.vidyasetuai.feature_profile.domain.model

data class ContributorVerification(
    val id: String,
    val contributorType: String,
    val userId: String,
    val status: String,
    val applicantNote: String?,
    val rejectionReason: String?
)
