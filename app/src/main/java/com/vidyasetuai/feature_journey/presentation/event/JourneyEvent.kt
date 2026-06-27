package com.vidyasetuai.feature_journey.presentation.event

import android.content.Context
import com.vidyasetuai.feature_journey.domain.model.JourneyOwnerType

sealed interface JourneyEvent {
    object LoadTemplates : JourneyEvent
    data class EnrollInJourney(
        val context: Context,
        val templateId: String,
        val ownerType: JourneyOwnerType,
        val notificationTime: String?
    ) : JourneyEvent
    object LoadActiveJourneyData : JourneyEvent
    data class ToggleTaskCompletion(val taskId: String, val isCompleted: Boolean) : JourneyEvent
    data class SubmitMCQOption(val mcqId: String, val selectedOption: String) : JourneyEvent
    data class SubmitAllMCQOptions(val answers: Map<String, String>) : JourneyEvent
    object LoadLeaderboard : JourneyEvent
    data class SelectActiveJourney(val journeyId: String) : JourneyEvent
}