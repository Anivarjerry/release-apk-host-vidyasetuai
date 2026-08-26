package com.vidyasetuai.feature_campus.presentation.viewmodel

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_campus.data.repository.CampusRepository
import com.vidyasetuai.feature_campus.domain.crypto.CampusCryptoEngine
import com.vidyasetuai.feature_campus.domain.model.CampusConnection
import com.vidyasetuai.feature_campus.domain.model.CampusConnectionStatus
import com.vidyasetuai.feature_campus.domain.model.CampusMessage
import com.vidyasetuai.feature_campus.domain.model.CampusSearchResult
import com.vidyasetuai.navigation.CampusDestination
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 0ms Offline-First ViewModel for Campus Social & Direct E2EE Chat.
 * WhatsApp-Grade 120 FPS Architecture with Persistent Scroll State & Zero Shimmer Shock.
 */
class CampusViewModel(
    private val repository: CampusRepository
) : ViewModel() {

    // 1. Persistent Scroll State for 0ms Instant Scroll Restoration
    val homeListScrollState = LazyListState()

    // 2. Type-Safe Campus Destination Router
    private val _activeDestination = MutableStateFlow<CampusDestination>(CampusDestination.Home)
    val activeDestination: StateFlow<CampusDestination> = _activeDestination.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // Dedicated Discovery / Connection Search Query
    private val _discoveryQuery = MutableStateFlow("")
    val discoveryQuery: StateFlow<String> = _discoveryQuery.asStateFlow()

    private val _isDiscoverySearching = MutableStateFlow(false)
    val isDiscoverySearching: StateFlow<Boolean> = _isDiscoverySearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<CampusSearchResult>>(emptyList())
    val searchResults: StateFlow<List<CampusSearchResult>> = _searchResults.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _activeSubScreen = MutableStateFlow<String?>(null) // null = Home, "chat" = ChatScreen, "connections" = ConnectionsScreen
    val activeSubScreen: StateFlow<String?> = _activeSubScreen.asStateFlow()

    private val _activeConversation = MutableStateFlow<CampusConnection?>(null)
    val activeConversation: StateFlow<CampusConnection?> = _activeConversation.asStateFlow()

    // 0ms Reactive Connections from Room DB (Single SQL Query)
    val allConnections: StateFlow<List<CampusConnection>> = repository.getConnectionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mutualConnections: StateFlow<List<CampusConnection>> = repository.getMutualConnectionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalUnreadCount: StateFlow<Int> = repository.getTotalUnreadCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Filtered Connections by Search Query (Instant In-Memory Filter)
    val filteredConnections: StateFlow<List<CampusConnection>> = combine(
        allConnections,
        searchQuery
    ) { connections, query ->
        if (query.isBlank()) connections
        else connections.filter {
            it.peerName.contains(query, ignoreCase = true) ||
            it.peerUsername.contains(query, ignoreCase = true) ||
            (it.lastMessageText?.contains(query, ignoreCase = true) == true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat Screen Loading State (Zero Shimmer Shock on Local Cache Hit)
    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Message Pagination Limit (Default: Latest 40 messages for 0ms Instant Load)
    private val _messageLimit = MutableStateFlow(40)

    // Messages for Active Conversation (0ms Plaintext Room Stream)
    @OptIn(ExperimentalCoroutinesApi::class)
    val activeMessages: StateFlow<List<CampusMessage>> = combine(
        _activeConversation,
        _messageLimit
    ) { conv, limit ->
        Pair(conv, limit)
    }.flatMapLatest { (conv, limit) ->
        if (conv == null) {
            _isChatLoading.value = false
            flowOf(emptyList())
        } else {
            val currentUserId = repository.getCurrentUserId() ?: ""
            val conversationId = CampusCryptoEngine.generateConversationId(currentUserId, conv.targetUserId)
            repository.getMessagesFlow(conversationId, limit).onEach {
                _isChatLoading.value = false
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live Peer Presence (Online / Offline / Typing...)
    val peerPresence: StateFlow<com.vidyasetuai.feature_campus.data.repository.PeerPresence> = repository.peerPresenceFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.vidyasetuai.feature_campus.data.repository.PeerPresence())

    private var lastTypingJob: kotlinx.coroutines.Job? = null

    fun onUserInputChanged(text: String) {
        if (_activeConversation.value == null) return
        lastTypingJob?.cancel()
        lastTypingJob = viewModelScope.launch {
            if (text.isNotBlank()) {
                repository.sendTypingIndicator(true)
                kotlinx.coroutines.delay(2000)
                repository.sendTypingIndicator(false)
            } else {
                repository.sendTypingIndicator(false)
            }
        }
    }

    init {
        // Initial Background Sync
        syncCampusData()

        // Debounced Reactive User Discovery Search (400ms Debounce with Skeleton Loading State)
        observeDiscoverySearch()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeDiscoverySearch() {
        viewModelScope.launch {
            _discoveryQuery
                .map { it.trim() }
                .debounce(400)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.length < 2) {
                        _searchResults.value = emptyList()
                        _isDiscoverySearching.value = false
                    } else {
                        _isDiscoverySearching.value = true
                        try {
                            val results = repository.searchUsers(query)
                            _searchResults.value = results
                        } catch (e: Exception) {
                            Log.e("CampusVM", "Error searching users: ${e.message}")
                            _searchResults.value = emptyList()
                        } finally {
                            _isDiscoverySearching.value = false
                        }
                    }
                }
        }
    }

    fun setDiscoveryQuery(query: String) {
        _discoveryQuery.value = query
        if (query.trim().length >= 2) {
            _isDiscoverySearching.value = true
        } else {
            _isDiscoverySearching.value = false
            _searchResults.value = emptyList()
        }
    }

    fun clearDiscoverySearch() {
        _discoveryQuery.value = ""
        _isDiscoverySearching.value = false
        _searchResults.value = emptyList()
    }

    fun toggleInspire(user: CampusSearchResult) {
        val currentResults = _searchResults.value
        val targetIndex = currentResults.indexOfFirst { it.userId == user.userId }
        if (targetIndex == -1 || user.isActionInProgress) return

        // 1. Optimistic UI update
        val updatedList = currentResults.toMutableList()
        val nextStatus = when (user.status) {
            CampusConnectionStatus.INSPIRED -> CampusConnectionStatus.NONE
            CampusConnectionStatus.MUTUAL -> CampusConnectionStatus.INSPIRES_YOU
            CampusConnectionStatus.INSPIRES_YOU -> CampusConnectionStatus.MUTUAL
            CampusConnectionStatus.NONE -> CampusConnectionStatus.INSPIRED
        }
        updatedList[targetIndex] = user.copy(
            status = nextStatus,
            isActionInProgress = true
        )
        _searchResults.value = updatedList

        // 2. Dispatch remote call via Traffic Police
        viewModelScope.launch {
            try {
                val result = repository.toggleInspireUser(
                    targetUserId = user.userId,
                    currentStatus = user.status,
                    peerName = user.fullName,
                    peerUsername = user.username,
                    peerAvatarUrl = user.avatarUrl
                )

                val finalizedList = _searchResults.value.toMutableList()
                val idx = finalizedList.indexOfFirst { it.userId == user.userId }
                if (idx != -1) {
                    finalizedList[idx] = finalizedList[idx].copy(
                        status = result.getOrDefault(nextStatus),
                        isActionInProgress = false
                    )
                    _searchResults.value = finalizedList
                }
            } catch (e: Exception) {
                // Revert on failure
                val revertedList = _searchResults.value.toMutableList()
                val idx = revertedList.indexOfFirst { it.userId == user.userId }
                if (idx != -1) {
                    revertedList[idx] = user.copy(isActionInProgress = false)
                    _searchResults.value = revertedList
                }
            }
        }
    }

    fun openChatFromSearchResult(user: CampusSearchResult) {
        val connection = CampusConnection(
            id = user.userId,
            userId = repository.getCurrentUserId() ?: "",
            targetUserId = user.userId,
            status = "FOLLOWING",
            isMutual = user.isMutual,
            peerName = user.fullName,
            peerUsername = user.username,
            peerAvatarUrl = user.avatarUrl,
            peerAvatarLocalPath = null,
            peerBio = user.bio
        )
        openChat(connection)
    }

    /**
     * FCM Push Notification & Deep-Link Direct Chat Resolver (0ms Instant Entry).
     */
    fun openChatByPeerUserId(peerUserId: String) {
        viewModelScope.launch {
            val connection = repository.getConnectionByPeerUserId(peerUserId)
            if (connection != null) {
                openChat(connection)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            _searchQuery.value = ""
        }
    }

    fun openChat(connection: CampusConnection) {
        _messageLimit.value = 40
        _activeConversation.value = connection
        _activeDestination.value = CampusDestination.Chat(connection)
        _activeSubScreen.value = "chat"

        // Mark chat as read & start Scoped Realtime Channel concurrently (0ms Non-Blocking UI)
        viewModelScope.launch {
            val currentUserId = repository.getCurrentUserId() ?: return@launch
            val conversationId = CampusCryptoEngine.generateConversationId(currentUserId, connection.targetUserId)
            // 1. Silent background sync to pull any messages missed while away into Room DB
            launch {
                repository.syncCampusData()
            }
            // 2. Concurrent background read receipt trigger
            launch {
                repository.markChatRead(conversationId)
            }
            // 3. Start realtime listener for live chat
            repository.startRealtimeChatListener(conversationId)
        }
    }

    fun loadMoreMessages() {
        _messageLimit.value += 30
    }

    fun closeChat() {
        viewModelScope.launch {
            repository.stopRealtimeChatListener()
        }
        _activeConversation.value = null
        _activeDestination.value = CampusDestination.Home
        _activeSubScreen.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            repository.stopRealtimeChatListener()
        }
    }

    fun openConnectionsScreen() {
        _activeDestination.value = CampusDestination.ConnectionsDiscovery
        _activeSubScreen.value = "connections"
    }

    fun closeConnectionsScreen() {
        _activeDestination.value = CampusDestination.Home
        _activeSubScreen.value = null
        clearDiscoverySearch()
    }

    fun sendMessage(text: String) {
        val conv = _activeConversation.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            repository.sendMessage(
                recipientId = conv.targetUserId,
                text = text.trim()
            )
        }
    }

    fun toggleSaveMessage(messageId: String, isSaved: Boolean) {
        viewModelScope.launch {
            repository.toggleSaveMessage(messageId, isSaved)
        }
    }

    fun followUser(targetUserId: String, peerName: String? = null, peerUsername: String? = null, peerAvatarUrl: String? = null) {
        viewModelScope.launch {
            repository.followUser(targetUserId, peerName, peerUsername, peerAvatarUrl)
        }
    }

    fun unfollowUser(targetUserId: String) {
        viewModelScope.launch {
            repository.unfollowUser(targetUserId)
        }
    }

    fun blockUser(targetUserId: String) {
        viewModelScope.launch {
            repository.blockUser(targetUserId)
        }
    }

    fun syncCampusData() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                repository.syncCampusData()
            } finally {
                _isSyncing.value = false
            }
        }
    }

    class Factory(private val repository: CampusRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CampusViewModel(repository) as T
        }
    }
}
