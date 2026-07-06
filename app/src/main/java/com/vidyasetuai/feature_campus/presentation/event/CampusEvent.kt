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
    
    // Private Campus Events
    data class ToggleTab(val tab: String) : CampusEvent
    data class LoadMutualInspirations(val userId: String) : CampusEvent
    data class OpenPrivateChat(val otherUser: com.vidyasetuai.feature_profile.domain.model.UserProfile, val activeUserId: String) : CampusEvent
    data class SendPrivateMessage(val activeUserId: String, val text: String?, val mediaUrl: String?) : CampusEvent
    data class ToggleSavePrivateMessage(val messageId: String, val isSaved: Boolean) : CampusEvent
    object ClosePrivateChat : CampusEvent
    data class SetUploadingPrivateMedia(val isUploading: Boolean) : CampusEvent
}