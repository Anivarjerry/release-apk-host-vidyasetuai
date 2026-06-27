package com.vidyasetuai.feature_journey.presentation.state

import com.vidyasetuai.feature_journey.domain.model.*
import com.vidyasetuai.feature_journey.domain.usecase.JourneyDayAnalytics

data class JourneyUiState(
    val isLoading: Boolean = false,
    val templates: List<JourneyTemplate> = emptyList(),
    val activeJourneys: List<UserJourney> = emptyList(),
    val activeJourney: UserJourney? = null,
    val dailyTasks: List<JourneyTask> = emptyList(),
    val dailyTaskProgress: Map<String, Boolean> = emptyMap(),
    val dailyMcqs: List<JourneyMcq> = emptyList(),
    val dailyMcqAnswers: Map<String, String> = emptyMap(),
    val dailyMcqCorrections: Map<String, Boolean> = emptyMap(),
    val analytics: JourneyDayAnalytics? = null,
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val errorMessage: String? = null
)