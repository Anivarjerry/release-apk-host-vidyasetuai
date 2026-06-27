package com.vidyasetuai.feature_case_study.presentation.event

sealed class CaseStudyEvent {
    object RefreshList : CaseStudyEvent()
    data class RefreshDetail(val id: String) : CaseStudyEvent()
    data class ToggleReaction(val id: String) : CaseStudyEvent()
    data class ToggleBookmark(val id: String) : CaseStudyEvent()
}
