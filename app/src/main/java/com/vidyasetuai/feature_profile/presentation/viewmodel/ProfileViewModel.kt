package com.vidyasetuai.feature_profile.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_profile.ProfileModuleFacade
import com.vidyasetuai.feature_profile.data.repository.ProfileRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/**
 * Enterprise ViewModel for Profile Module.
 * Connects 0ms Room DB reactive flows to Compose UI.
 */
class ProfileViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: ProfileRepository = ProfileModuleFacade.getRepository(application)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var activeTargetUserId: String? = null
    private var profileFlowJob: Job? = null
    private var inspirationsJob: Job? = null

    init {
        loadProfile(null)
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> loadProfile(event.targetUserId)
            is ProfileEvent.Refresh -> refresh()
            is ProfileEvent.SelectTab -> _uiState.update { it.copy(selectedTab = event.index) }
            is ProfileEvent.ToggleEditSheet -> _uiState.update { it.copy(isEditSheetOpen = event.isOpen) }
            is ProfileEvent.ToggleImageLightbox -> _uiState.update {
                it.copy(
                    isImageLightboxOpen = event.isOpen,
                    lightboxImageUrl = event.imageUrl
                )
            }
            is ProfileEvent.UpdateProfileInfo -> updateProfileInfo(event)
            is ProfileEvent.UploadAvatar -> uploadAvatar(event.imageFile)
            is ProfileEvent.UploadCover -> uploadCover(event.imageFile)
            is ProfileEvent.ClearMessages -> _uiState.update { it.copy(errorMessage = null, successMessage = null) }
        }
    }

    private fun loadProfile(targetUserId: String?) {
        activeTargetUserId = targetUserId
        profileFlowJob?.cancel()
        inspirationsJob?.cancel()

        // 1. 0ms Reactive Read from Room DB Flow
        profileFlowJob = viewModelScope.launch {
            val flow = if (targetUserId == null) {
                repository.getMyProfileFlow()
            } else {
                repository.getProfileByIdFlow(targetUserId)
            }

            flow.collect { profile ->
                _uiState.update {
                    it.copy(
                        profile = profile,
                        isMe = (targetUserId == null || profile?.isMe == true)
                    )
                }

                val resolvedUserId = profile?.userId ?: targetUserId ?: repository.getCurrentUserId()
                if (resolvedUserId != null && (inspirationsJob == null || !inspirationsJob!!.isActive)) {
                    startInspirationsObserver(resolvedUserId)
                }
            }
        }

        val initialUserId = targetUserId ?: repository.getCurrentUserId()
        if (initialUserId != null) {
            startInspirationsObserver(initialUserId)
        }

        // 3. Trigger background sync without blocking UI
        refresh()
    }

    private fun startInspirationsObserver(userId: String) {
        inspirationsJob?.cancel()
        inspirationsJob = viewModelScope.launch {
            launch {
                repository.getInspirationsFlow(userId, "FOLLOWING").collect { following ->
                    _uiState.update { it.copy(followingList = following) }
                }
            }
            launch {
                repository.getInspirationsFlow(userId, "FOLLOWER").collect { followers ->
                    _uiState.update { it.copy(followersList = followers) }
                }
            }
            launch {
                repository.getVerificationFlow(userId).collect { verification ->
                    _uiState.update { it.copy(verification = verification) }
                }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            val result = repository.syncProfileData(activeTargetUserId)
            _uiState.update {
                it.copy(
                    isSyncing = false,
                    errorMessage = if (result.isFailure) "Unable to sync profile." else null
                )
            }
        }
    }

    suspend fun checkUsernameAvailability(username: String): Boolean? {
        val result = repository.checkUsernameAvailability(username)
        return result.getOrNull()
    }

    private fun updateProfileInfo(event: ProfileEvent.UpdateProfileInfo) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.updateProfile(
                username = event.username,
                firstName = event.firstName,
                lastName = event.lastName,
                bio = event.bio,
                gender = event.gender,
                dateOfBirth = event.dateOfBirth,
                preferredLanguage = event.preferredLanguage,
                isPrivate = event.isPrivate
            )
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isEditSheetOpen = false,
                    successMessage = if (result.isSuccess) "Profile updated successfully" else null,
                    errorMessage = if (result.isFailure) "Failed to update profile. Please try again." else null
                )
            }
        }
    }

    private fun uploadAvatar(imageFile: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingAvatar = true) }
            val result = repository.uploadAvatar(imageFile)
            _uiState.update {
                it.copy(
                    isUploadingAvatar = false,
                    successMessage = if (result.isSuccess) "Profile picture updated" else null,
                    errorMessage = if (result.isFailure) "Failed to upload profile picture" else null
                )
            }
        }
    }

    private fun uploadCover(imageFile: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingCover = true) }
            val result = repository.uploadCoverPhoto(imageFile)
            _uiState.update {
                it.copy(
                    isUploadingCover = false,
                    successMessage = if (result.isSuccess) "Cover photo updated" else null,
                    errorMessage = if (result.isFailure) "Failed to upload cover photo" else null
                )
            }
        }
    }
}