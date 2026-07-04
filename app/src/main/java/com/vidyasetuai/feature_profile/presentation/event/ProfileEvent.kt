package com.vidyasetuai.feature_profile.presentation.event

sealed class ProfileEvent {
    data class LoadProfile(val userId: String) : ProfileEvent()
    data class CheckUsername(val username: String, val currentUserId: String) : ProfileEvent()
    data class UpdateProfile(
        val firstName: String,
        val lastName: String,
        val bio: String,
        val username: String,
        val gender: String?,
        val dateOfBirth: String?,
        val profilePictureUrl: String?,
        val coverPhotoUrl: String?,
        val preferredLanguage: String?
    ) : ProfileEvent()
    data class ApplyVerification(val note: String) : ProfileEvent()
    object DismissSuccess : ProfileEvent()
    object ResetUsernameCheck : ProfileEvent()
    data class UpdateUserCaseStudies(val list: List<com.vidyasetuai.feature_case_study.domain.model.CaseStudy>) : ProfileEvent()
    data class UpdateUserExperiences(val list: List<com.vidyasetuai.feature_feed.domain.model.Experience>) : ProfileEvent()
    data class SetUserUploadedLoading(val isLoading: Boolean) : ProfileEvent()
}