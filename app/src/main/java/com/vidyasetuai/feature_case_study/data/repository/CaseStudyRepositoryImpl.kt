package com.vidyasetuai.feature_case_study.data.repository

import com.vidyasetuai.feature_case_study.data.local.datasource.CaseStudyLocalDataSource
import com.vidyasetuai.feature_case_study.data.mapper.toDomain
import com.vidyasetuai.feature_case_study.data.mapper.toEntity
import com.vidyasetuai.feature_case_study.data.remote.datasource.CaseStudyRemoteDataSource
import com.vidyasetuai.feature_case_study.domain.model.CaseStudy
import com.vidyasetuai.feature_case_study.domain.model.CaseStudyDetail
import com.vidyasetuai.feature_case_study.domain.repository.CaseStudyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CaseStudyRepositoryImpl(
    private val localDataSource: CaseStudyLocalDataSource,
    private val remoteDataSource: CaseStudyRemoteDataSource
) : CaseStudyRepository {

    override fun getCaseStudies(userId: String): Flow<List<CaseStudy>> {
        return localDataSource.getCaseStudies().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCaseStudyDetail(caseStudyId: String, userId: String): Flow<CaseStudyDetail?> {
        return localDataSource.getCaseStudyDetail(caseStudyId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun syncCaseStudies(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val dtos = remoteDataSource.getCaseStudies()
            val userReactions = remoteDataSource.getUserReactions(userId).map { it.case_study_id }.toSet()
            val userBookmarks = remoteDataSource.getUserBookmarks(userId).map { it.case_study_id }.toSet()
            val allReactions = remoteDataSource.getAllReactions()
            val allBookmarks = remoteDataSource.getAllBookmarks()

            val reactionCounts = allReactions.groupBy { it.case_study_id }.mapValues { it.value.size }
            val bookmarkCounts = allBookmarks.groupBy { it.case_study_id }.mapValues { it.value.size }

            val authorIds = dtos.mapNotNull { dto -> dto.author_user_id }.distinct()
            val profiles = remoteDataSource.getAuthorProfiles(authorIds).associateBy { it.user_id }

            val entities = dtos.map { dto ->
                val caseStudyId = dto.id
                val profile = profiles[dto.author_user_id]
                dto.toEntity(
                    isReacted = userReactions.contains(caseStudyId),
                    reactionCount = reactionCounts[caseStudyId] ?: 0,
                    isBookmarked = userBookmarks.contains(caseStudyId),
                    bookmarkCount = bookmarkCounts[caseStudyId] ?: 0,
                    authorName = if (dto.author_type == "platform") "VidyaSetu AI" else if (dto.author_type == "user") (profile?.full_name ?: profile?.first_name ?: "Academic Scholar") else "Academic Partner",
                    authorUsername = if (dto.author_type == "platform") "vidyasetu" else if (dto.author_type == "user") (profile?.username ?: "scholar") else "partner",
                    authorProfilePicUrl = if (dto.author_type == "user") profile?.profile_picture_url else null,
                    isAuthorVerified = if (dto.author_type == "platform") true else (profile?.is_verified ?: false)
                )
            }

            localDataSource.insertCaseStudies(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("VidyaSetu_CaseStudyRepo", "Error syncing case studies for user: $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun syncCaseStudyDetail(caseStudyId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val dto = remoteDataSource.getCaseStudyDetail(caseStudyId)
            val isReacted = remoteDataSource.getReactionsForCaseStudy(caseStudyId).any { it.user_id == userId }
            val reactionCount = remoteDataSource.getReactionsForCaseStudy(caseStudyId).size
            val isBookmarked = remoteDataSource.getBookmarksForCaseStudy(caseStudyId).any { it.user_id == userId }
            val bookmarkCount = remoteDataSource.getBookmarksForCaseStudy(caseStudyId).size

            val profile = dto.author_user_id?.let { authorId ->
                remoteDataSource.getAuthorProfiles(listOf(authorId)).firstOrNull()
            }

            val entity = dto.toEntity(
                isReacted = isReacted,
                reactionCount = reactionCount,
                isBookmarked = isBookmarked,
                bookmarkCount = bookmarkCount,
                authorName = if (dto.author_type == "platform") "VidyaSetu AI" else if (dto.author_type == "user") (profile?.full_name ?: profile?.first_name ?: "Academic Scholar") else "Academic Partner",
                authorUsername = if (dto.author_type == "platform") "vidyasetu" else if (dto.author_type == "user") (profile?.username ?: "scholar") else "partner",
                authorProfilePicUrl = if (dto.author_type == "user") profile?.profile_picture_url else null,
                isAuthorVerified = if (dto.author_type == "platform") true else (profile?.is_verified ?: false)
            )

            localDataSource.insertCaseStudyDetail(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("VidyaSetu_CaseStudyRepo", "Error syncing case study detail for id: $caseStudyId, user: $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun toggleReaction(caseStudyId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userReactions = remoteDataSource.getReactionsForCaseStudy(caseStudyId)
            val existingReaction = userReactions.firstOrNull { it.user_id == userId }

            val isReacted: Boolean
            if (existingReaction != null) {
                remoteDataSource.removeReaction(caseStudyId, userId)
                isReacted = false
            } else {
                remoteDataSource.addReaction(caseStudyId, userId, "helpful")
                isReacted = true
            }

            val updatedReactions = remoteDataSource.getReactionsForCaseStudy(caseStudyId)
            val reactionCount = updatedReactions.size

            localDataSource.updateReaction(caseStudyId, isReacted, reactionCount)
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("VidyaSetu_CaseStudyRepo", "Error toggling reaction on case study: $caseStudyId for user: $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun toggleBookmark(caseStudyId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userBookmarks = remoteDataSource.getBookmarksForCaseStudy(caseStudyId)
            val existingBookmark = userBookmarks.firstOrNull { it.user_id == userId }

            val isBookmarked: Boolean
            if (existingBookmark != null) {
                remoteDataSource.removeBookmark(caseStudyId, userId)
                isBookmarked = false
            } else {
                remoteDataSource.addBookmark(caseStudyId, userId)
                isBookmarked = true
            }

            val updatedBookmarks = remoteDataSource.getBookmarksForCaseStudy(caseStudyId)
            val bookmarkCount = updatedBookmarks.size

            localDataSource.updateBookmark(caseStudyId, isBookmarked, bookmarkCount)
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("VidyaSetu_CaseStudyRepo", "Error toggling bookmark on case study: $caseStudyId for user: $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun createCaseStudy(
        title: String,
        shortDescription: String,
        coverImageUrl: String,
        language: String,
        tags: List<String>,
        readTimeMinutes: Int?,
        detailedContent: String,
        additionalImageUrls: List<String>,
        userId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.createCaseStudy(
                title = title,
                shortDescription = shortDescription,
                coverImageUrl = coverImageUrl,
                language = language,
                tags = tags,
                readTimeMinutes = readTimeMinutes,
                detailedContent = detailedContent,
                additionalImageUrls = additionalImageUrls,
                authorUserId = userId
            )
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("VidyaSetu_CaseStudyRepo", "Error creating case study", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserUploadedCaseStudies(userId: String): Result<List<CaseStudy>> = withContext(Dispatchers.IO) {
        try {
            val dtos = remoteDataSource.getCaseStudiesByUser(userId)
            val profile = remoteDataSource.getAuthorProfiles(listOf(userId)).firstOrNull()
            val caseStudies = dtos.map { dto ->
                CaseStudy(
                    id = dto.id,
                    title = dto.title,
                    slug = dto.slug,
                    coverImageUrl = dto.cover_image_url,
                    shortDescription = dto.short_description,
                    language = dto.language,
                    tags = dto.tags,
                    readTimeMinutes = dto.read_time_minutes,
                    authorType = dto.author_type,
                    authorUserId = dto.author_user_id,
                    authorName = profile?.full_name ?: profile?.first_name ?: "Academic Scholar",
                    authorUsername = profile?.username ?: "scholar",
                    authorProfilePicUrl = profile?.profile_picture_url,
                    isAuthorVerified = profile?.is_verified ?: false,
                    viewCount = dto.view_count,
                    publishedAt = dto.published_at,
                    createdAt = dto.created_at,
                    updatedAt = dto.updated_at
                )
            }
            Result.success(caseStudies)
        } catch (e: Exception) {
            android.util.Log.e("VidyaSetu_CaseStudyRepo", "Error fetching user uploaded case studies", e)
            Result.failure(e)
        }
    }
}
