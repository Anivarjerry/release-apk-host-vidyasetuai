package com.vidyasetuai.feature_case_study.presentation.state

import com.vidyasetuai.feature_case_study.domain.model.CaseStudy

data class CaseStudyListUiState(
    val caseStudies: List<CaseStudy> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
