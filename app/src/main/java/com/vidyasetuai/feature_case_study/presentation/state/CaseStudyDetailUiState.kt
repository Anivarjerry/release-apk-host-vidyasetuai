package com.vidyasetuai.feature_case_study.presentation.state

import com.vidyasetuai.feature_case_study.domain.model.CaseStudyDetail

data class CaseStudyDetailUiState(
    val caseStudy: CaseStudyDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
