package com.vidyasetuai.feature_campus.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_campus.domain.repository.CampusRepository
import com.vidyasetuai.feature_campus.presentation.event.CampusEvent
import com.vidyasetuai.feature_campus.presentation.state.CampusUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CampusViewModel(
    private val campusRepo: CampusRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CampusUiState())
    val state: StateFlow<CampusUiState> = _state.asStateFlow()

    private var privateMessagesFlowJob: Job? = null
    private var privateRealtimeJob: Job? = null

    init {
        viewModelScope.launch {
            campusRepo.loadModerationSettings()
        }
        viewModelScope.launch {
            campusRepo.getPrivateRoomsFlow().collect { roomList ->
                _state.value = _state.value.copy(privateRooms = roomList)
            }
        }
    }

    fun onEvent(event: CampusEvent) {
        when (event) {
            is CampusEvent.InitializeKeys -> {
                viewModelScope.launch {
                    campusRepo.initializeUserKeys(event.userId)
                }
            }
            is CampusEvent.DismissAbuseWarning -> {
                _state.value = _state.value.copy(showAbuseWarning = false)
            }
            is CampusEvent.DismissError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
            is CampusEvent.LoadMutualInspirations -> {
                // 1. Instant local Room DB Flow observation (0 ms render)
                viewModelScope.launch {
                    campusRepo.getMutualInspirationsFlow(event.userId).collectLatest { list ->
                        _state.value = _state.value.copy(
                            mutualInspirations = list,
                            isLoadingMutual = false
                        )
                    }
                }

                // 2. Silent background sync
                if (_state.value.mutualInspirations.isEmpty()) {
                    _state.value = _state.value.copy(isLoadingMutual = true)
                }
                viewModelScope.launch {
                    campusRepo.getMutualInspirations(event.userId)
                    _state.value = _state.value.copy(isLoadingMutual = false)
                }
            }
            is CampusEvent.OpenPrivateChat -> {
                val isDifferentUser = _state.value.activePrivateUser?.userId != event.otherUser.userId
                _state.value = _state.value.copy(
                    activePrivateUser = event.otherUser,
                    isLoadingMessages = false,
                    privateMessages = if (isDifferentUser) emptyList() else _state.value.privateMessages
                )
                viewModelScope.launch {
                    val isMutual = campusRepo.checkIsMutualConnection(event.activeUserId, event.otherUser.userId)
                    _state.value = _state.value.copy(isCurrentRoomMutual = isMutual)

                    campusRepo.getOrCreatePrivateRoom(event.activeUserId, event.otherUser.userId).fold(
                        onSuccess = { room ->
                            _state.value = _state.value.copy(activePrivateRoomId = room.id)
                            
                            // Mark room & messages as read locally
                            viewModelScope.launch {
                                campusRepo.markRoomAsRead(room.id, event.activeUserId)
                            }
                            
                            // 1. Observe instant local Room DB Flow (0 ms render)
                            privateMessagesFlowJob?.cancel()
                            privateMessagesFlowJob = viewModelScope.launch {
                                campusRepo.getPrivateMessagesFlow(room.id).collectLatest { msgList ->
                                    _state.value = _state.value.copy(
                                        privateMessages = msgList,
                                        isLoadingMessages = false
                                    )
                                }
                            }

                            // 2. Background Sync & Realtime Subscription (Only if mutual)
                            if (isMutual) {
                                viewModelScope.launch {
                                    campusRepo.syncPrivateMessages(room.id, event.otherUser.userId)
                                }

                                privateRealtimeJob?.cancel()
                                privateRealtimeJob = viewModelScope.launch {
                                    runCatching {
                                        campusRepo.observePrivateMessages(room.id, event.otherUser.userId).collect { _ -> }
                                    }
                                }
                            } else {
                                privateRealtimeJob?.cancel()
                            }
                        },
                        onFailure = { error ->
                            val rawMsg = error.message ?: ""
                            val sanitizedError = if (rawMsg.contains("row level security") || rawMsg.contains("Authorization=") || rawMsg.contains("Bearer")) {
                                null
                            } else {
                                rawMsg
                            }
                            _state.value = _state.value.copy(
                                errorMessage = sanitizedError,
                                isLoadingMessages = false
                            )
                        }
                    )
                }
            }
            is CampusEvent.SendPrivateMessage -> {
                val roomId = _state.value.activePrivateRoomId ?: return@onEvent
                val peerUserId = _state.value.activePrivateUser?.userId
                viewModelScope.launch {
                    campusRepo.sendPrivateMessage(roomId, event.activeUserId, event.text, event.mediaUrl, peerUserId).fold(
                        onSuccess = {},
                        onFailure = { error ->
                            val msg = error.message ?: ""
                            if (msg.contains("Blocked keywords")) {
                                _state.value = _state.value.copy(showAbuseWarning = true)
                            } else {
                                _state.value = _state.value.copy(errorMessage = msg)
                            }
                        }
                    )
                }
            }
            is CampusEvent.ToggleSavePrivateMessage -> {
                viewModelScope.launch {
                    campusRepo.toggleSavePrivateMessage(event.messageId, event.isSaved)
                }
            }
            is CampusEvent.ClosePrivateChat -> {
                privateMessagesFlowJob?.cancel()
                privateRealtimeJob?.cancel()
                _state.value = _state.value.copy(
                    activePrivateUser = null,
                    activePrivateRoomId = null,
                    privateMessages = emptyList()
                )
            }
            is CampusEvent.SetUploadingPrivateMedia -> {
                _state.value = _state.value.copy(isUploadingPrivateMedia = event.isUploading)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        privateMessagesFlowJob?.cancel()
        privateRealtimeJob?.cancel()
    }
}