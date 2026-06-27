package com.vidyasetuai.feature_profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_profile.domain.model.UserProfile
import com.vidyasetuai.feature_profile.domain.usecase.ApplyForVerificationUseCase
import com.vidyasetuai.feature_profile.domain.usecase.CheckUsernameUniqueUseCase
import com.vidyasetuai.feature_profile.domain.usecase.GetUserProfileUseCase
import com.vidyasetuai.feature_profile.domain.usecase.UpdateUserProfileUseCase
import com.vidyasetuai.feature_profile.presentation.event.ProfileEvent
import com.vidyasetuai.feature_profile.presentation.state.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val checkUsernameUniqueUseCase: CheckUsernameUniqueUseCase,
    private val applyForVerificationUseCase: ApplyForVerificationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    fun loadProfile(userId: String) {
        if (userId.isEmpty() || userId == currentUserId) return
        currentUserId = userId
        
        _uiState.update { it.copy(isLoading = true) }

        // Profile flow collection
        viewModelScope.launch {
            getUserProfileUseCase(userId)
                .catch { e ->
                    _uiState.update { it.copy(error = e.localizedMessage) }
                }
                .collect { profile ->
                    _uiState.update { it.copy(profile = profile, isLoading = false) }
                }
        }

        // Verification status flow collection
        viewModelScope.launch {
            applyForVerificationUseCase.getVerificationFlow(userId)
                .catch { e ->
                    _uiState.update { it.copy(error = e.localizedMessage) }
                }
                .collect { verification ->
                    _uiState.update { it.copy(verification = verification) }
                }
        }

        // Sync from remote in background
        sync(userId)
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> {
                loadProfile(event.userId)
            }
            is ProfileEvent.CheckUsername -> {
                checkUsername(event.username, event.currentUserId)
            }
            is ProfileEvent.UpdateProfile -> {
                updateProfile(
                    firstName = event.firstName,
                    lastName = event.lastName,
                    bio = event.bio,
                    username = event.username,
                    gender = event.gender,
                    dateOfBirth = event.dateOfBirth,
                    profilePictureUrl = event.profilePictureUrl,
                    coverPhotoUrl = event.coverPhotoUrl,
                    preferredLanguage = event.preferredLanguage
                )
            }
            is ProfileEvent.ApplyVerification -> {
                applyVerification(event.note)
            }
            is ProfileEvent.DismissSuccess -> {
                _uiState.update { it.copy(updateSuccess = false, applySuccess = false) }
            }
            is ProfileEvent.ResetUsernameCheck -> {
                _uiState.update { it.copy(usernameUnique = null) }
            }
        }
    }

    private fun checkUsername(username: String, userId: String) {
        if (username.isBlank()) {
            _uiState.update { it.copy(usernameUnique = null) }
            return
        }
        
        _uiState.update { it.copy(usernameChecking = true) }
        viewModelScope.launch {
            checkUsernameUniqueUseCase(username, userId)
                .onSuccess { isUnique ->
                    _uiState.update { it.copy(usernameUnique = isUnique, usernameChecking = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(usernameUnique = null, usernameChecking = false, error = e.localizedMessage) }
                }
        }
    }

    private fun updateProfile(
        firstName: String,
        lastName: String,
        bio: String,
        username: String,
        gender: String?,
        dateOfBirth: String?,
        profilePictureUrl: String?,
        coverPhotoUrl: String?,
        preferredLanguage: String?
    ) {
        val currentProfile = _uiState.value.profile ?: return
        val updatedProfile = currentProfile.copy(
            firstName = firstName,
            lastName = lastName,
            fullName = "${firstName.trim()} ${lastName.trim()}".trim(),
            bio = bio,
            username = username.trim(),
            gender = gender,
            dateOfBirth = dateOfBirth,
            profilePictureUrl = profilePictureUrl,
            coverPhotoUrl = coverPhotoUrl,
            preferredLanguage = preferredLanguage
        )

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            updateUserProfileUseCase(updatedProfile)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, updateSuccess = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                }
        }
    }

    private fun applyVerification(note: String) {
        if (currentUserId.isEmpty()) return
        
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            applyForVerificationUseCase(currentUserId, note)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, applySuccess = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                }
        }
    }

    private fun sync(userId: String) {
        viewModelScope.launch {
            getUserProfileUseCase.sync(userId)
        }
        viewModelScope.launch {
            applyForVerificationUseCase.sync(userId)
        }
    }
}