package com.vidyasetuai.feature_campus.presentation.event

import com.vidyasetuai.feature_campus.domain.model.CampusRoom

sealed interface CampusEvent {
    object LoadRooms : CampusEvent
    data class OpenRoom(val room: CampusRoom, val userId: String) : CampusEvent
    object CloseActiveRoom : CampusEvent
    data class OnMessageInputChange(val input: String) : CampusEvent
    data class SendMessage(val userId: String) : CampusEvent
    data class ReportMessage(val messageId: String, val userId: String, val reason: String) : CampusEvent
    object PauseRealtime : CampusEvent
    data class ResumeRealtime(val userId: String) : CampusEvent
    object DismissAbuseWarning : CampusEvent
    object DismissError : CampusEvent
}