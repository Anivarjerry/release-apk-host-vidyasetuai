package com.vidyasetuai.feature_journey.domain.usecase

import com.vidyasetuai.feature_journey.domain.model.JourneyMcqProgress
import com.vidyasetuai.feature_journey.domain.model.JourneyTaskProgress
import com.vidyasetuai.feature_journey.domain.repository.JourneyRepository

class GetJourneyAnalyticsUseCase(private val repository: JourneyRepository) {
    suspend operator fun invoke(journeyId: String, dayNumber: Int): JourneyDayAnalytics {
        val tasks = repository.getTaskProgress(journeyId, dayNumber)
        val mcqs = repository.getMcqProgress(journeyId, dayNumber)
        
        val totalTasks = tasks.size
        val completedTasks = tasks.count { it.isCompleted }
        val taskCompletionPercent = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0

        val totalMcqs = mcqs.size
        val correctMcqs = mcqs.count { it.isCorrect }

        return JourneyDayAnalytics(
            taskCompletionPercentage = taskCompletionPercent,
            totalTasksCount = totalTasks,
            completedTasksCount = completedTasks,
            totalMcqsCount = totalMcqs,
            correctMcqsCount = correctMcqs
        )
    }
}

data class JourneyDayAnalytics(
    val taskCompletionPercentage: Int,
    val totalTasksCount: Int,
    val completedTasksCount: Int,
    val totalMcqsCount: Int,
    val correctMcqsCount: Int
)
