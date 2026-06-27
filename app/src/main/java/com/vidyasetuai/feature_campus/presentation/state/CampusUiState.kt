package com.vidyasetuai.feature_campus.presentation.state

import com.vidyasetuai.feature_campus.domain.model.CampusMessage
import com.vidyasetuai.feature_campus.domain.model.CampusRoom

data class CampusUiState(
    val rooms: List<CampusRoom> = emptyList(),
    val isLoadingRooms: Boolean = false,
    val activeRoom: CampusRoom? = null,
    val messages: List<CampusMessage> = emptyList(),
    val isLoadingMessages: Boolean = false,
    val messageInput: String = "",
    val cooldownSecondsRemaining: Int = 0,
    val activePresenceCount: Int = 0,
    val errorMessage: String? = null,
    val showAbuseWarning: Boolean = false
)