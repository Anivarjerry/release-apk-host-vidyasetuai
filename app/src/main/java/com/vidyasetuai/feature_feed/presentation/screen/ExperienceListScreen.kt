package com.vidyasetuai.feature_feed.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_feed.presentation.component.ExperienceCard
import com.vidyasetuai.feature_feed.presentation.event.ExperienceEvent
import com.vidyasetuai.feature_feed.presentation.viewmodel.ExperienceViewModel

@Composable
fun ExperienceListScreen(
    userId: String,
    currentLanguage: String,
    viewModel: ExperienceViewModel,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.onEvent(ExperienceEvent.LoadExperiences(userId))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.isLoading && state.experiences.isEmpty()) {
            CircularProgressIndicator(
                color = AppColors.EmeraldGreen,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (state.experiences.isEmpty() && !state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (currentLanguage == "hi") "कोई अनुभव उपलब्ध नहीं है।" else "No experiences available.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.experiences, key = { it.id }) { experience ->
                    ExperienceCard(
                        experience = experience,
                        onReactionClick = {
                            viewModel.onEvent(ExperienceEvent.ToggleInspiration(experience.id, userId))
                        },
                        currentLanguage = currentLanguage,
                        onAuthorClick = onAuthorClick
                    )
                }
            }
        }
    }
}
