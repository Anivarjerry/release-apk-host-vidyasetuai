package com.vidyasetuai.feature_campus.presentation.state

import com.vidyasetuai.feature_campus.domain.model.PrivateMessage
import com.vidyasetuai.feature_campus.domain.model.PrivateRoom
import com.vidyasetuai.feature_profile.domain.model.UserProfile

data class CampusUiState(
    val errorMessage: String? = null,
    val showAbuseWarning: Boolean = false,
    val mutualInspirations: List<UserProfile> = emptyList(),
    val isLoadingMutual: Boolean = false,
    val privateRooms: List<PrivateRoom> = emptyList(),
    val activePrivateRoomId: String? = null,
    val activePrivateUser: UserProfile? = null,
    val privateMessages: List<PrivateMessage> = emptyList(),
    val isLoadingMessages: Boolean = false,
    val isUploadingPrivateMedia: Boolean = false,
    val isCurrentRoomMutual: Boolean = true
)