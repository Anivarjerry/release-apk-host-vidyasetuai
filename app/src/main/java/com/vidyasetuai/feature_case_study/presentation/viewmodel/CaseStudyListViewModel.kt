package com.vidyasetuai.feature_case_study.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_case_study.domain.usecase.GetCaseStudyListUseCase
import com.vidyasetuai.feature_case_study.domain.usecase.ToggleBookmarkUseCase
import com.vidyasetuai.feature_case_study.domain.usecase.ToggleReactionUseCase
import com.vidyasetuai.feature_case_study.presentation.event.CaseStudyEvent
import com.vidyasetuai.feature_case_study.presentation.state.CaseStudyListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CaseStudyListViewModel(
    private val getCaseStudyListUseCase: GetCaseStudyListUseCase,
    private val toggleReactionUseCase: ToggleReactionUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CaseStudyListUiState())
    val uiState: StateFlow<CaseStudyListUiState> = _uiState.asStateFlow()

    fun loadCaseStudies(userId: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getCaseStudyListUseCase(userId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                }
                .collect { list ->
                    _uiState.update { it.copy(caseStudies = list, isLoading = false) }
                }
        }
        sync(userId)
    }

    fun onEvent(event: CaseStudyEvent, userId: String) {
        when (event) {
            is CaseStudyEvent.RefreshList -> {
                sync(userId)
            }
            is CaseStudyEvent.ToggleReaction -> {
                viewModelScope.launch {
                    toggleReactionUseCase(event.id, userId)
                }
            }
            is CaseStudyEvent.ToggleBookmark -> {
                viewModelScope.launch {
                    toggleBookmarkUseCase(event.id, userId)
                }
            }
            else -> {}
        }
    }

    private fun sync(userId: String) {
        viewModelScope.launch {
            getCaseStudyListUseCase.sync(userId).onFailure { e ->
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }
}
