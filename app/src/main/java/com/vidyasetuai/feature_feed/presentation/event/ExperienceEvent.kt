package com.vidyasetuai.feature_feed.presentation.event

sealed interface ExperienceEvent {
    data class LoadExperiences(val userId: String) : ExperienceEvent
    data class Refresh(val userId: String) : ExperienceEvent
    data class ToggleInspiration(val experienceId: String, val userId: String) : ExperienceEvent
    data class UploadExperience(
        val title: String,
        val description: String,
        val coverImageUrl: String?,
        val authorUserId: String
    ) : ExperienceEvent
    object DismissSuccess : ExperienceEvent
}
