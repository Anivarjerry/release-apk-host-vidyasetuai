package com.vidyasetuai.feature_case_study.data.repository

import com.vidyasetuai.feature_case_study.data.remote.datasource.QuickRemoteDataSource
import com.vidyasetuai.feature_case_study.domain.model.Quick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuickRepository(
    private val remoteDataSource: QuickRemoteDataSource = QuickRemoteDataSource()
) {
    private var cachedQuicks: List<Quick>? = null

    suspend fun getQuicks(userId: String): Result<List<Quick>> = withContext(Dispatchers.IO) {
        val cached = cachedQuicks
        if (cached != null) {
            return@withContext Result.success(cached)
        }
        try {
            val dtos = remoteDataSource.getQuicks()
            val userHelpfuls = remoteDataSource.getUserHelpfuls(userId).map { it.quick_id }.toSet()
            val authorIds = dtos.map { it.author_user_id }.distinct()
            val profiles = remoteDataSource.getAuthorProfiles(authorIds).associateBy { it.user_id }

            val quicks = dtos.map { dto ->
                val profile = profiles[dto.author_user_id]
                Quick(
                    id = dto.id,
                    authorUserId = dto.author_user_id,
                    title = dto.title,
                    description = dto.description,
                    coverImageUrl = dto.cover_image_url,
                    viewsCount = dto.views_count,
                    helpfulCount = dto.helpful_count,
                    status = dto.status,
                    createdAt = dto.created_at,
                    expiresAt = dto.expires_at,
                    authorName = profile?.full_name ?: profile?.first_name ?: "Academic Scholar",
                    authorUsername = profile?.username ?: "scholar",
                    authorProfilePicUrl = profile?.profile_picture_url,
                    isAuthorVerified = profile?.is_verified ?: false,
                    isHelpful = userHelpfuls.contains(dto.id)
                )
            }
            cachedQuicks = quicks
            Result.success(quicks)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            android.util.Log.e("QuickRepository", "Error fetching quicks", e)
            Result.failure(e)
        }
    }

    suspend fun toggleHelpful(quickId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val helpfuls = remoteDataSource.getUserHelpfuls(userId)
            val hasHelpful = helpfuls.any { it.quick_id == quickId }
            if (hasHelpful) {
                remoteDataSource.removeHelpful(quickId, userId)
            } else {
                remoteDataSource.addHelpful(quickId, userId)
            }
            cachedQuicks = null
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            android.util.Log.e("QuickRepository", "Error toggling helpful on quick", e)
            Result.failure(e)
        }
    }

    suspend fun createQuick(title: String, description: String, coverImageUrl: String?, authorUserId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.createQuick(title, description, coverImageUrl, authorUserId)
            cachedQuicks = null
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            android.util.Log.e("QuickRepository", "Error creating quick", e)
            Result.failure(e)
        }
    }

    suspend fun viewQuick(quickId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.incrementQuickViews(quickId, userId)
            cachedQuicks = null
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            android.util.Log.e("QuickRepository", "Error incrementing views on quick", e)
            Result.failure(e)
        }
    }
}
