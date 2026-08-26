package com.vidyasetuai.feature_profile.presentation.viewmodel

import com.vidyasetuai.feature_profile.domain.model.ContributorVerification
import com.vidyasetuai.feature_profile.domain.model.ProfileInspiration
import com.vidyasetuai.feature_profile.domain.model.UserProfile

/**
 * Immutable UI State for the Profile Screen.
 */
data class ProfileUiState(
    val profile: UserProfile? = null,
    val isMe: Boolean = true,
    val followingList: List<ProfileInspiration> = emptyList(),
    val followersList: List<ProfileInspiration> = emptyList(),
    val verification: ContributorVerification? = null,
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val isUploadingAvatar: Boolean = false,
    val isUploadingCover: Boolean = false,
    val isEditSheetOpen: Boolean = false,
    val isImageLightboxOpen: Boolean = false,
    val lightboxImageUrl: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
