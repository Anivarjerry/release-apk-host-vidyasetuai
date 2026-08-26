package com.vidyasetuai.feature_profile.presentation.viewmodel

import java.io.File

/**
 * UI Events for the Profile Screen and Edit Sheet.
 */
sealed interface ProfileEvent {
    data class LoadProfile(val targetUserId: String? = null) : ProfileEvent
    object Refresh : ProfileEvent
    data class SelectTab(val index: Int) : ProfileEvent
    data class ToggleEditSheet(val isOpen: Boolean) : ProfileEvent
    data class ToggleImageLightbox(val isOpen: Boolean, val imageUrl: String? = null) : ProfileEvent
    data class UpdateProfileInfo(
        val username: String? = null,
        val firstName: String?,
        val lastName: String?,
        val bio: String?,
        val gender: String?,
        val dateOfBirth: String?,
        val preferredLanguage: String?,
        val isPrivate: Boolean?
    ) : ProfileEvent
    data class UploadAvatar(val imageFile: File) : ProfileEvent
    data class UploadCover(val imageFile: File) : ProfileEvent
    object ClearMessages : ProfileEvent
}
