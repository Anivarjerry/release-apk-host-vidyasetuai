package com.vidyasetuai.feature_case_study.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_case_study.domain.usecase.GetCaseStudyDetailUseCase
import com.vidyasetuai.feature_case_study.domain.usecase.ToggleBookmarkUseCase
import com.vidyasetuai.feature_case_study.domain.usecase.ToggleReactionUseCase
import com.vidyasetuai.feature_case_study.presentation.event.CaseStudyEvent
import com.vidyasetuai.feature_case_study.presentation.state.CaseStudyDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CaseStudyDetailViewModel(
    private val getCaseStudyDetailUseCase: GetCaseStudyDetailUseCase,
    private val toggleReactionUseCase: ToggleReactionUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CaseStudyDetailUiState())
    val uiState: StateFlow<CaseStudyDetailUiState> = _uiState.asStateFlow()

    fun loadCaseStudyDetail(caseStudyId: String, userId: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getCaseStudyDetailUseCase(caseStudyId, userId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                }
                .collect { detail ->
                    _uiState.update { it.copy(caseStudy = detail, isLoading = false) }
                }
        }
        sync(caseStudyId, userId)
    }

    fun onEvent(event: CaseStudyEvent, userId: String) {
        when (event) {
            is CaseStudyEvent.RefreshDetail -> {
                sync(event.id, userId)
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

    private fun sync(caseStudyId: String, userId: String) {
        viewModelScope.launch {
            getCaseStudyDetailUseCase.sync(caseStudyId, userId).onFailure { e ->
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }
}
