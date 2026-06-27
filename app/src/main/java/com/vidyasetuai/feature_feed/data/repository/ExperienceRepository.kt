package com.vidyasetuai.feature_feed.data.repository

import com.vidyasetuai.feature_feed.data.remote.datasource.ExperienceRemoteDataSource
import com.vidyasetuai.feature_feed.domain.model.Experience
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExperienceRepository(
    private val remoteDataSource: ExperienceRemoteDataSource = ExperienceRemoteDataSource()
) {
    suspend fun getExperiences(userId: String): Result<List<Experience>> = withContext(Dispatchers.IO) {
        try {
            val dtos = remoteDataSource.getExperiences()
            val authorIds = dtos.map { it.author_user_id }.distinct()
            val profiles = remoteDataSource.getAuthorProfiles(authorIds).associateBy { it.user_id }
            val userInspirations = remoteDataSource.getUserInspirations(userId).map { it.experience_id }.toSet()
            
            val experiences = dtos.map { dto ->
                val profile = profiles[dto.author_user_id]
                Experience(
                    id = dto.id,
                    title = dto.title,
                    coverImageUrl = dto.cover_image_url,
                    description = dto.description,
                    authorUserId = dto.author_user_id,
                    inspiredCount = dto.inspired_count,
                    isInspired = userInspirations.contains(dto.id),
                    createdAt = dto.created_at,
                    authorName = profile?.full_name ?: profile?.first_name ?: "Academic Scholar",
                    authorUsername = profile?.username ?: "scholar",
                    authorProfilePicUrl = profile?.profile_picture_url,
                    isAuthorVerified = profile?.is_verified ?: false
                )
            }
            Result.success(experiences)
        } catch (e: Exception) {
            android.util.Log.e("ExperienceRepository", "Error fetching experiences", e)
            Result.failure(e)
        }
    }
    
    suspend fun getExperiencesByUser(userId: String, authorUserId: String): Result<List<Experience>> = withContext(Dispatchers.IO) {
        try {
            val dtos = remoteDataSource.getExperiencesByUser(authorUserId)
            val profiles = remoteDataSource.getAuthorProfiles(listOf(authorUserId)).associateBy { it.user_id }
            val userInspirations = remoteDataSource.getUserInspirations(userId).map { it.experience_id }.toSet()
            
            val experiences = dtos.map { dto ->
                val profile = profiles[dto.author_user_id]
                Experience(
                    id = dto.id,
                    title = dto.title,
                    coverImageUrl = dto.cover_image_url,
                    description = dto.description,
                    authorUserId = dto.author_user_id,
                    inspiredCount = dto.inspired_count,
                    isInspired = userInspirations.contains(dto.id),
                    createdAt = dto.created_at,
                    authorName = profile?.full_name ?: profile?.first_name ?: "Academic Scholar",
                    authorUsername = profile?.username ?: "scholar",
                    authorProfilePicUrl = profile?.profile_picture_url,
                    isAuthorVerified = profile?.is_verified ?: false
                )
            }
            Result.success(experiences)
        } catch (e: Exception) {
            android.util.Log.e("ExperienceRepository", "Error fetching experiences for user $authorUserId", e)
            Result.failure(e)
        }
    }

    suspend fun toggleInspiration(experienceId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val inspirations = remoteDataSource.getInspirationsForExperience(experienceId)
            val hasInspired = inspirations.any { it.user_id == userId }
            if (hasInspired) {
                remoteDataSource.removeInspiration(experienceId, userId)
            } else {
                remoteDataSource.addInspiration(experienceId, userId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("ExperienceRepository", "Error toggling inspiration", e)
            Result.failure(e)
        }
    }

    suspend fun createExperience(title: String, description: String, coverImageUrl: String?, authorUserId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.createExperience(title, description, coverImageUrl, authorUserId)
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("ExperienceRepository", "Error creating experience", e)
            Result.failure(e)
        }
    }
}
