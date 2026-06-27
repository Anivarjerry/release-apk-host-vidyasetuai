package com.vidyasetuai.feature_journey.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_journey.data.local.receiver.JourneyAlarmReceiver
import com.vidyasetuai.feature_journey.domain.model.JourneyMcqProgress
import com.vidyasetuai.feature_journey.domain.model.JourneyTaskProgress
import com.vidyasetuai.feature_journey.domain.model.UserJourney
import com.vidyasetuai.feature_journey.domain.repository.JourneyRepository
import com.vidyasetuai.feature_journey.domain.usecase.*
import com.vidyasetuai.feature_journey.presentation.event.JourneyEvent
import com.vidyasetuai.feature_journey.presentation.state.JourneyUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class JourneyViewModel(
    private val userId: String,
    private val repository: JourneyRepository,
    private val getAvailableTemplatesUseCase: GetAvailableTemplatesUseCase,
    private val enrollInJourneyUseCase: EnrollInJourneyUseCase,
    private val getActiveUserJourneyUseCase: GetActiveUserJourneyUseCase,
    private val getDailyTasksUseCase: GetDailyTasksUseCase,
    private val getDailyMCQsUseCase: GetDailyMCQsUseCase,
    private val submitTaskProgressUseCase: SubmitTaskProgressUseCase,
    private val submitMCQAttemptUseCase: SubmitMCQAttemptUseCase,
    private val getJourneyAnalyticsUseCase: GetJourneyAnalyticsUseCase,
    private val getJourneyLeaderboardUseCase: GetJourneyLeaderboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(JourneyUiState())
    val uiState: StateFlow<JourneyUiState> = _uiState.asStateFlow()

    init {
        loadActiveJourneys()
        loadTemplates()
    }

    fun onEvent(event: JourneyEvent) {
        when (event) {
            is JourneyEvent.LoadTemplates -> loadTemplates()
            is JourneyEvent.EnrollInJourney -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true) }
                    val result = enrollInJourneyUseCase(
                        userId = userId,
                        templateId = event.templateId,
                        type = event.ownerType,
                        notificationTime = event.notificationTime
                    )
                    result.fold(
                        onSuccess = { journey ->
                            _uiState.update { it.copy(isLoading = false, activeJourney = journey) }
                            // Schedule alarms on UI thread context
                            JourneyAlarmReceiver.scheduleDailyAlarms(event.context, event.notificationTime)
                            loadDailyContent(journey)
                            loadLeaderboard(journey)
                        },
                        onFailure = { error ->
                            _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                        }
                    )
                }
            }
            is JourneyEvent.LoadActiveJourneyData -> {
                viewModelScope.launch {
                    val active = _uiState.value.activeJourney
                    if (active != null) {
                        loadDailyContent(active)
                    }
                }
            }
            is JourneyEvent.ToggleTaskCompletion -> {
                viewModelScope.launch {
                    val active = _uiState.value.activeJourney ?: return@launch
                    val progress = JourneyTaskProgress(
                        id = UUID.randomUUID().toString(),
                        userJourneyId = active.id,
                        dayNumber = active.currentDay,
                        taskId = event.taskId,
                        isCompleted = event.isCompleted,
                        completedAt = if (event.isCompleted) java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(java.util.Date()) else null
                    )
                    submitTaskProgressUseCase(progress).fold(
                        onSuccess = {
                            val updatedMap = _uiState.value.dailyTaskProgress.toMutableMap().apply {
                                put(event.taskId, event.isCompleted)
                            }
                            _uiState.update { it.copy(dailyTaskProgress = updatedMap) }
                            recalculateAnalytics(active.id, active.currentDay)
                        },
                        onFailure = { error ->
                            _uiState.update { it.copy(errorMessage = error.message) }
                        }
                    )
                }
            }
            is JourneyEvent.SubmitMCQOption -> {
                viewModelScope.launch {
                    val active = _uiState.value.activeJourney ?: return@launch
                    val mcq = _uiState.value.dailyMcqs.find { it.id == event.mcqId } ?: return@launch
                    
                    val progress = JourneyMcqProgress(
                        id = UUID.randomUUID().toString(),
                        userJourneyId = active.id,
                        dayNumber = active.currentDay,
                        mcqId = event.mcqId,
                        selectedOption = event.selectedOption,
                        isCorrect = false, // Will be computed inside usecase/repository
                        scoreEarned = 0,
                        answeredAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(java.util.Date())
                    )

                    submitMCQAttemptUseCase(progress, mcq.correctOption, mcq.points).fold(
                        onSuccess = { isCorrect ->
                            val updatedAnswers = _uiState.value.dailyMcqAnswers.toMutableMap().apply {
                                put(event.mcqId, event.selectedOption)
                            }
                            val updatedCorrections = _uiState.value.dailyMcqCorrections.toMutableMap().apply {
                                put(event.mcqId, isCorrect)
                            }
                            _uiState.update {
                                it.copy(
                                    dailyMcqAnswers = updatedAnswers,
                                    dailyMcqCorrections = updatedCorrections
                                )
                            }
                            recalculateAnalytics(active.id, active.currentDay)
                        },
                        onFailure = { error ->
                            _uiState.update { it.copy(errorMessage = error.message) }
                        }
                    )
                }
            }
            is JourneyEvent.SubmitAllMCQOptions -> {
                viewModelScope.launch {
                    val active = _uiState.value.activeJourney ?: return@launch
                    _uiState.update { it.copy(isLoading = true) }
                    val updatedAnswers = _uiState.value.dailyMcqAnswers.toMutableMap()
                    val updatedCorrections = _uiState.value.dailyMcqCorrections.toMutableMap()
                    
                    event.answers.forEach { (mcqId, selectedOption) ->
                        val mcq = _uiState.value.dailyMcqs.find { it.id == mcqId } ?: return@forEach
                        val progress = JourneyMcqProgress(
                            id = UUID.randomUUID().toString(),
                            userJourneyId = active.id,
                            dayNumber = active.currentDay,
                            mcqId = mcqId,
                            selectedOption = selectedOption,
                            isCorrect = false,
                            scoreEarned = 0,
                            answeredAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(java.util.Date())
                        )
                        submitMCQAttemptUseCase(progress, mcq.correctOption, mcq.points).fold(
                            onSuccess = { isCorrect ->
                                updatedAnswers[mcqId] = selectedOption
                                updatedCorrections[mcqId] = isCorrect
                            },
                            onFailure = { /* ignore single errors to complete batch */ }
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dailyMcqAnswers = updatedAnswers,
                            dailyMcqCorrections = updatedCorrections
                        )
                    }
                    recalculateAnalytics(active.id, active.currentDay)
                }
            }
            is JourneyEvent.LoadLeaderboard -> {
                viewModelScope.launch {
                    val active = _uiState.value.activeJourney
                    if (active != null) {
                        loadLeaderboard(active)
                    }
                }
            }
            is JourneyEvent.SelectActiveJourney -> {
                viewModelScope.launch {
                    val selected = _uiState.value.activeJourneys.find { it.id == event.journeyId }
                    if (selected != null) {
                        _uiState.update { it.copy(activeJourney = selected) }
                        loadDailyContent(selected)
                        loadLeaderboard(selected)
                    }
                }
            }
        }
    }

    private fun loadActiveJourneys() {
        viewModelScope.launch {
            repository.getAllUserJourneys(userId).collectLatest { allJourneys ->
                val activeList = allJourneys.filter { it.status == "active" }
                val currentSelected = _uiState.value.activeJourney
                
                val newSelected = if (currentSelected == null || activeList.none { it.id == currentSelected.id }) {
                    activeList.firstOrNull()
                } else {
                    activeList.first { it.id == currentSelected.id }
                }
                
                _uiState.update { 
                    it.copy(
                        activeJourneys = activeList,
                        activeJourney = newSelected
                    ) 
                }
                
                if (newSelected != null) {
                    loadDailyContent(newSelected)
                    loadLeaderboard(newSelected)
                }
            }
        }
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getAvailableTemplatesUseCase(orgId = null, parentOrgId = null) // Can be extended with actual linked orgId if needed
                .catch { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
                .collect { list ->
                    _uiState.update { it.copy(isLoading = false, templates = list) }
                }
        }
    }

    private suspend fun loadDailyContent(active: UserJourney) {
        try {
            val tasks = getDailyTasksUseCase(active.id, active.templateId, active.templateType, active.currentDay)
            val mcqs = getDailyMCQsUseCase(active.id, active.templateId, active.templateType, active.currentDay)
            
            val taskProgress = repository.getTaskProgress(active.id, active.currentDay)
            val mcqProgress = repository.getMcqProgress(active.id, active.currentDay)
            
            val progressMap = taskProgress.associate { it.taskId to it.isCompleted }
            val answerMap = mcqProgress.associate { it.mcqId to it.selectedOption }
            val correctionMap = mcqProgress.associate { it.mcqId to it.isCorrect }
            
            val analytics = getJourneyAnalyticsUseCase(active.id, active.currentDay)
            
            _uiState.update {
                it.copy(
                    dailyTasks = tasks,
                    dailyTaskProgress = progressMap,
                    dailyMcqs = mcqs,
                    dailyMcqAnswers = answerMap,
                    dailyMcqCorrections = correctionMap,
                    analytics = analytics
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.message) }
        }
    }

    private suspend fun loadLeaderboard(active: UserJourney) {
        try {
            val leaderboardData = getJourneyLeaderboardUseCase(
                globalTemplateId = if (active.templateType == com.vidyasetuai.feature_journey.domain.model.JourneyOwnerType.GLOBAL) active.templateId else null,
                organizationTemplateId = if (active.templateType == com.vidyasetuai.feature_journey.domain.model.JourneyOwnerType.ORGANIZATION) active.templateId else null,
                organizationParentTemplateId = if (active.templateType == com.vidyasetuai.feature_journey.domain.model.JourneyOwnerType.PARENT_ORGANIZATION) active.templateId else null
            )
            _uiState.update { it.copy(leaderboard = leaderboardData) }
        } catch (e: Exception) {
            // fail silently for leaderboard
        }
    }

    private suspend fun recalculateAnalytics(journeyId: String, dayNumber: Int) {
        try {
            val analytics = getJourneyAnalyticsUseCase(journeyId, dayNumber)
            _uiState.update { it.copy(analytics = analytics) }
        } catch (e: Exception) {
            // fail silently
        }
    }
}