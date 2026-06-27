package com.vidyasetuai.feature_feed.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_feed.data.repository.ExperienceRepository
import com.vidyasetuai.feature_feed.presentation.event.ExperienceEvent
import com.vidyasetuai.feature_feed.presentation.state.ExperienceUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExperienceViewModel(
    private val repository: ExperienceRepository = ExperienceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExperienceUiState())
    val uiState: StateFlow<ExperienceUiState> = _uiState.asStateFlow()

    fun loadExperiences(userId: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getExperiences(userId)
                .onSuccess { list ->
                    _uiState.update { it.copy(experiences = list, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                }
        }
    }

    fun onEvent(event: ExperienceEvent) {
        when (event) {
            is ExperienceEvent.LoadExperiences -> {
                loadExperiences(event.userId)
            }
            is ExperienceEvent.Refresh -> {
                loadExperiences(event.userId)
            }
            is ExperienceEvent.ToggleInspiration -> {
                viewModelScope.launch {
                    repository.toggleInspiration(event.experienceId, event.userId)
                        .onSuccess {
                            loadExperiences(event.userId)
                        }
                }
            }
            is ExperienceEvent.UploadExperience -> {
                _uiState.update { it.copy(isLoading = true) }
                viewModelScope.launch {
                    repository.createExperience(
                        title = event.title,
                        description = event.description,
                        coverImageUrl = event.coverImageUrl,
                        authorUserId = event.authorUserId
                    ).onSuccess {
                        _uiState.update { it.copy(uploadSuccess = true, isLoading = false) }
                        loadExperiences(event.authorUserId)
                    }.onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                    }
                }
            }
            is ExperienceEvent.DismissSuccess -> {
                _uiState.update { it.copy(uploadSuccess = false) }
            }
        }
    }
}
