package com.vidyasetuai.feature_feed.presentation.state

import com.vidyasetuai.feature_feed.domain.model.Experience

data class ExperienceUiState(
    val isLoading: Boolean = false,
    val experiences: List<Experience> = emptyList(),
    val error: String? = null,
    val uploadSuccess: Boolean = false
)
