package com.vidyasetuai.feature_campus.presentation.event

import com.vidyasetuai.feature_profile.domain.model.UserProfile

sealed interface CampusEvent {
    object DismissAbuseWarning : CampusEvent
    object DismissError : CampusEvent
    
    // Private Campus Events
    data class InitializeKeys(val userId: String) : CampusEvent
    data class LoadMutualInspirations(val userId: String) : CampusEvent
    data class OpenPrivateChat(val otherUser: UserProfile, val activeUserId: String) : CampusEvent
    data class SendPrivateMessage(val activeUserId: String, val text: String?, val mediaUrl: String?) : CampusEvent
    data class ToggleSavePrivateMessage(val messageId: String, val isSaved: Boolean) : CampusEvent
    object ClosePrivateChat : CampusEvent
    data class SetUploadingPrivateMedia(val isUploading: Boolean) : CampusEvent
}