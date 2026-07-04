package com.vidyasetuai.feature_feed.presentation.screen

import androidx.compose.runtime.Composable
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.domain.model.ContentFeedItem

@Composable
fun ContentFeedScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    onItemClick: (ContentFeedItem) -> Unit,
    onBack: () -> Unit
) {
    // Blank placeholder
}
