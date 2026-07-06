package com.vidyasetuai.feature_campus.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_campus.domain.model.CampusMessage
import com.vidyasetuai.feature_campus.domain.usecase.GetMessagesUseCase
import com.vidyasetuai.feature_campus.domain.usecase.GetRoomsUseCase
import com.vidyasetuai.feature_campus.domain.usecase.LoadModerationSettingsUseCase
import com.vidyasetuai.feature_campus.domain.usecase.ReportMessageUseCase
import com.vidyasetuai.feature_campus.domain.usecase.SendMessageUseCase
import com.vidyasetuai.feature_campus.presentation.event.CampusEvent
import com.vidyasetuai.feature_campus.presentation.state.CampusUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

class CampusViewModel(
    private val getRoomsUseCase: GetRoomsUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val reportMessageUseCase: ReportMessageUseCase,
    private val loadModerationSettingsUseCase: LoadModerationSettingsUseCase,
    private val campusRepo: com.vidyasetuai.feature_campus.domain.repository.CampusRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CampusUiState())
    val state: StateFlow<CampusUiState> = _state.asStateFlow()

    private var messagesJob: Job? = null
    private var realtimeJob: Job? = null
    private var presenceJob: Job? = null
    private var cooldownJob: Job? = null
    private var privateRealtimeJob: Job? = null

    init {
        onEvent(CampusEvent.LoadRooms)
        viewModelScope.launch {
            loadModerationSettingsUseCase()
        }
    }

    fun onEvent(event: CampusEvent) {
        when (event) {
            is CampusEvent.LoadRooms -> {
                _state.value = _state.value.copy(isLoadingRooms = true)
                viewModelScope.launch {
                    getRoomsUseCase().collectLatest { roomsList ->
                        _state.value = _state.value.copy(
                            rooms = roomsList,
                            isLoadingRooms = false
                        )
                    }
                }
                viewModelScope.launch {
                    getRoomsUseCase.sync()
                }
            }
            is CampusEvent.OpenRoom -> {
                _state.value = _state.value.copy(
                    activeRoom = event.room,
                    isLoadingMessages = true,
                    messages = emptyList(),
                    cooldownSecondsRemaining = 0
                )
                
                // 1. Subscribe to local cached messages Flow
                messagesJob?.cancel()
                messagesJob = viewModelScope.launch {
                    getMessagesUseCase(event.room.id).collectLatest { messagesList ->
                        _state.value = _state.value.copy(
                            messages = messagesList,
                            isLoadingMessages = false
                        )
                        // Auto-calculate if we are in cooldown from the last message sent
                        checkInitialCooldown(messagesList, event.userId, event.room.messageCooldownSeconds)
                    }
                }

                // 2. Fetch/Sync remote messages
                viewModelScope.launch {
                    getMessagesUseCase.sync(event.room.id)
                }
            }
            is CampusEvent.CloseActiveRoom -> {
                messagesJob?.cancel()
                realtimeJob?.cancel()
                presenceJob?.cancel()
                cooldownJob?.cancel()
                _state.value = _state.value.copy(
                    activeRoom = null,
                    messages = emptyList(),
                    cooldownSecondsRemaining = 0,
                    activePresenceCount = 0
                )
            }
            is CampusEvent.OnMessageInputChange -> {
                _state.value = _state.value.copy(messageInput = event.input)
            }
            is CampusEvent.SendMessage -> {
                val input = _state.value.messageInput.trim()
                val active = _state.value.activeRoom
                if (input.isEmpty() || active == null) return

                // Check client-side cooldown to be double safe
                if (_state.value.cooldownSecondsRemaining > 0) return

                _state.value = _state.value.copy(messageInput = "")
                viewModelScope.launch {
                    sendMessageUseCase(active.id, event.userId, input).fold(
                        onSuccess = {
                            // Cooldown started immediately on success
                            startCooldownTimer(active.messageCooldownSeconds)
                        },
                        onFailure = { error ->
                            val errorMsg = error.message ?: ""
                            if (errorMsg.contains("Blocked keywords")) {
                                _state.value = _state.value.copy(
                                    showAbuseWarning = true,
                                    messageInput = input
                                )
                            } else if (errorMsg.contains("Spam Protection")) {
                                startCooldownTimer(active.messageCooldownSeconds)
                            } else {
                                _state.value = _state.value.copy(
                                    errorMessage = errorMsg,
                                    messageInput = input
                                )
                            }
                        }
                    )
                }
            }
            is CampusEvent.ReportMessage -> {
                viewModelScope.launch {
                    reportMessageUseCase(event.messageId, event.userId, event.reason).fold(
                        onSuccess = {
                            _state.value = _state.value.copy(errorMessage = "रिपोर्ट सफलतापूर्वक सबमिट कर दी गई है।")
                        },
                        onFailure = { error ->
                            _state.value = _state.value.copy(errorMessage = error.message)
                        }
                    )
                }
            }
            is CampusEvent.PauseRealtime -> {
                realtimeJob?.cancel()
                presenceJob?.cancel()
                _state.value = _state.value.copy(activePresenceCount = 0)
            }
            is CampusEvent.ResumeRealtime -> {
                val active = _state.value.activeRoom ?: return
                realtimeJob?.cancel()
                realtimeJob = viewModelScope.launch {
                    getMessagesUseCase.observeRealtime(active.id).collect { _ -> }
                }
                presenceJob?.cancel()
                presenceJob = viewModelScope.launch {
                    getMessagesUseCase.observePresence(active.id, event.userId).collectLatest { count ->
                        _state.value = _state.value.copy(activePresenceCount = count)
                    }
                }
            }
            is CampusEvent.DismissAbuseWarning -> {
                _state.value = _state.value.copy(showAbuseWarning = false)
            }
            is CampusEvent.DismissError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
            is CampusEvent.ToggleTab -> {
                _state.value = _state.value.copy(activeTab = event.tab)
            }
            is CampusEvent.LoadMutualInspirations -> {
                _state.value = _state.value.copy(isLoadingMutual = true)
                viewModelScope.launch {
                    campusRepo.getMutualInspirations(event.userId).fold(
                        onSuccess = { list ->
                            _state.value = _state.value.copy(
                                mutualInspirations = list,
                                isLoadingMutual = false
                            )
                        },
                        onFailure = { error ->
                            _state.value = _state.value.copy(
                                errorMessage = error.message,
                                isLoadingMutual = false
                            )
                        }
                    )
                }
            }
            is CampusEvent.OpenPrivateChat -> {
                _state.value = _state.value.copy(
                    activePrivateUser = event.otherUser,
                    isLoadingMessages = true,
                    privateMessages = emptyList()
                )
                viewModelScope.launch {
                    campusRepo.getOrCreatePrivateRoom(event.activeUserId, event.otherUser.userId).fold(
                        onSuccess = { room ->
                            _state.value = _state.value.copy(activePrivateRoomId = room.id)
                            campusRepo.getPrivateMessages(room.id).fold(
                                onSuccess = { msgList ->
                                    _state.value = _state.value.copy(
                                        privateMessages = msgList,
                                        isLoadingMessages = false
                                    )
                                },
                                onFailure = { error ->
                                    _state.value = _state.value.copy(
                                        errorMessage = error.message,
                                        isLoadingMessages = false
                                    )
                                }
                            )
                            privateRealtimeJob?.cancel()
                            privateRealtimeJob = viewModelScope.launch {
                                campusRepo.observePrivateMessages(room.id).collect { newMsg ->
                                    val currentList = _state.value.privateMessages.toMutableList()
                                    val idx = currentList.indexOfFirst { it.id == newMsg.id }
                                    if (idx != -1) {
                                        currentList[idx] = newMsg
                                    } else {
                                        currentList.add(newMsg)
                                    }
                                    _state.value = _state.value.copy(privateMessages = currentList)
                                }
                            }
                        },
                        onFailure = { error ->
                            _state.value = _state.value.copy(
                                errorMessage = error.message,
                                isLoadingMessages = false
                            )
                        }
                    )
                }
            }
            is CampusEvent.SendPrivateMessage -> {
                val roomId = _state.value.activePrivateRoomId ?: return@onEvent
                viewModelScope.launch {
                    campusRepo.sendPrivateMessage(roomId, event.activeUserId, event.text, event.mediaUrl).fold(
                        onSuccess = {},
                        onFailure = { error ->
                            _state.value = _state.value.copy(errorMessage = error.message)
                        }
                    )
                }
            }
            is CampusEvent.ToggleSavePrivateMessage -> {
                viewModelScope.launch {
                    campusRepo.toggleSavePrivateMessage(event.messageId, event.isSaved).fold(
                        onSuccess = { updatedMsg ->
                            val currentList = _state.value.privateMessages.toMutableList()
                            val idx = currentList.indexOfFirst { it.id == updatedMsg.id }
                            if (idx != -1) {
                                currentList[idx] = updatedMsg
                                _state.value = _state.value.copy(privateMessages = currentList)
                            }
                        },
                        onFailure = { error ->
                            _state.value = _state.value.copy(errorMessage = error.message)
                        }
                    )
                }
            }
            is CampusEvent.ClosePrivateChat -> {
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

    private fun checkInitialCooldown(messages: List<CampusMessage>, userId: String, cooldownSeconds: Int) {
        if (_state.value.cooldownSecondsRemaining > 0) return
        val lastMsg = messages.filter { it.userId == userId && !it.isFailed }
            .maxByOrNull { it.createdAt } ?: return

        try {
            val lastTime = Instant.parse(lastMsg.createdAt)
            val now = Instant.now()
            val diff = Duration.between(lastTime, now).seconds
            if (diff < cooldownSeconds) {
                startCooldownTimer((cooldownSeconds - diff).toInt())
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun startCooldownTimer(seconds: Int) {
        cooldownJob?.cancel()
        _state.value = _state.value.copy(cooldownSecondsRemaining = seconds)
        cooldownJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _state.value = _state.value.copy(cooldownSecondsRemaining = remaining)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        messagesJob?.cancel()
        realtimeJob?.cancel()
        presenceJob?.cancel()
        cooldownJob?.cancel()
        privateRealtimeJob?.cancel()
    }
}