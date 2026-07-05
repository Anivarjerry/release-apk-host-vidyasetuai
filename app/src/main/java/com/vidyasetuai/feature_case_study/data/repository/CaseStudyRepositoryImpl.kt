package com.vidyasetuai.feature_case_study.data.repository

import com.vidyasetuai.feature_case_study.data.remote.datasource.CaseStudyRemoteDataSource
import com.vidyasetuai.feature_case_study.domain.model.CaseStudy
import com.vidyasetuai.feature_case_study.domain.model.CaseStudyDetail
import com.vidyasetuai.feature_case_study.domain.repository.CaseStudyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import com.vidyasetuai.feature_case_study.domain.model.ContentBlock

class CaseStudyRepositoryImpl(
    private val remoteDataSource: CaseStudyRemoteDataSource
) : CaseStudyRepository {

    private var cachedCaseStudies: List<CaseStudy>? = null

    override fun getCaseStudies(userId: String): Flow<List<CaseStudy>> = flow {
        val cached = cachedCaseStudies
        if (cached != null) {
            emit(cached)
            return@flow
        }

        try {
            val dtos = remoteDataSource.getCaseStudies()
            val userReactions = remoteDataSource.getUserReactions(userId).map { it.case_study_id }.toSet()
            val allReactions = remoteDataSource.getAllReactions()
            val reactionCounts = allReactions.groupBy { it.case_study_id }.mapValues { it.value.size }

            val authorIds = dtos.mapNotNull { dto -> dto.author_user_id }.distinct()
            val profiles = remoteDataSource.getAuthorProfiles(authorIds).associateBy { it.user_id }

            val caseStudies = dtos.map { dto ->
                val caseStudyId = dto.id
                val profile = profiles[dto.author_user_id]
                val isReacted = userReactions.contains(caseStudyId)
                val reactionCount = reactionCounts[caseStudyId] ?: 0

                CaseStudy(
                    id = dto.id,
                    title = dto.title,
                    slug = dto.slug,
                    coverImageUrl = dto.cover_image_url ?: "",
                    shortDescription = dto.short_description,
                    language = dto.language,
                    tags = dto.tags,
                    readTimeMinutes = dto.read_time_minutes,
                    authorType = dto.author_type,
                    authorUserId = dto.author_user_id,
                    authorName = if (dto.author_type == "platform") "VidyaSetu AI" else if (dto.author_type == "user") (profile?.full_name ?: profile?.first_name ?: "Academic Scholar") else "Academic Partner",
                    authorUsername = if (dto.author_type == "platform") "vidyasetu" else if (dto.author_type == "user") (profile?.username ?: "scholar") else "partner",
                    authorProfilePicUrl = if (dto.author_type == "user") profile?.profile_picture_url else null,
                    isAuthorVerified = if (dto.author_type == "platform") true else (profile?.is_verified ?: false),
                    viewCount = dto.view_count,
                    publishedAt = dto.published_at,
                    createdAt = dto.created_at,
                    updatedAt = dto.updated_at,
                    isReacted = isReacted,
                    reactionCount = reactionCount,
                    isBookmarked = false,
                    bookmarkCount = 0
                )
            }
            cachedCaseStudies = caseStudies
            emit(caseStudies)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            android.util.Log.e("CaseStudyRepoImpl", "Error loading case studies", e)
            emit(emptyList())
        }
    }

    override fun getCaseStudyDetail(caseStudyId: String, userId: String): Flow<CaseStudyDetail?> = flow {
        try {
            val dto = remoteDataSource.getCaseStudyDetail(caseStudyId)
            val isReacted = remoteDataSource.getReactionsForCaseStudy(caseStudyId).any { it.user_id == userId }
            val reactionCount = remoteDataSource.getReactionsForCaseStudy(caseStudyId).size

            val profile = dto.author_user_id?.let { authorId ->
                remoteDataSource.getAuthorProfiles(listOf(authorId)).firstOrNull()
            }

            val blocks = mutableListOf<ContentBlock>()
            try {
                val sections = dto.content["sections"]?.jsonArray
                sections?.forEach { sectionElement ->
                    val sectionObj = sectionElement as? JsonObject
                    val heading = sectionObj?.get("heading")?.jsonPrimitive?.contentOrNull
                    val description = sectionObj?.get("description")?.jsonPrimitive?.contentOrNull
                    val subHeadings = sectionObj?.get("subHeadings")?.jsonArray
                    
                    if (!heading.isNullOrBlank()) {
                        blocks.add(ContentBlock.Title(heading))
                    }
                    if (!description.isNullOrBlank()) {
                        blocks.add(ContentBlock.Paragraph(description))
                    }
                    subHeadings?.forEach { subElement ->
                        val subObj = subElement as? JsonObject
                        val subTitle = subObj?.get("title")?.jsonPrimitive?.contentOrNull
                        val subText = subObj?.get("text")?.jsonPrimitive?.contentOrNull
                        if (!subTitle.isNullOrBlank()) {
                            blocks.add(ContentBlock.SubSection(subTitle))
                        }
                        if (!subText.isNullOrBlank()) {
                            blocks.add(ContentBlock.Paragraph(subText))
                        }
                    }
                }
            } catch (ex: Exception) {
                android.util.Log.e("CaseStudyRepoImpl", "Error parsing content sections", ex)
            }

            val detail = CaseStudyDetail(
                id = dto.id,
                title = dto.title,
                slug = dto.slug,
                coverImageUrl = dto.cover_image_url ?: "",
                shortDescription = dto.short_description,
                language = dto.language,
                tags = dto.tags,
                readTimeMinutes = dto.read_time_minutes,
                authorType = dto.author_type,
                authorUserId = dto.author_user_id,
                authorName = if (dto.author_type == "platform") "VidyaSetu AI" else if (dto.author_type == "user") (profile?.full_name ?: profile?.first_name ?: "Academic Scholar") else "Academic Partner",
                authorUsername = if (dto.author_type == "platform") "vidyasetu" else if (dto.author_type == "user") (profile?.username ?: "scholar") else "partner",
                authorProfilePicUrl = if (dto.author_type == "user") profile?.profile_picture_url else null,
                isAuthorVerified = if (dto.author_type == "platform") true else (profile?.is_verified ?: false),
                viewCount = dto.view_count,
                publishedAt = dto.published_at,
                createdAt = dto.created_at,
                updatedAt = dto.updated_at,
                contentBlocks = blocks,
                additionalImageUrls = dto.additional_image_urls,
                isReacted = isReacted,
                reactionCount = reactionCount,
                isBookmarked = false,
                bookmarkCount = 0
            )
            emit(detail)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            android.util.Log.e("CaseStudyRepoImpl", "Error loading case study detail", e)
            emit(null)
        }
    }

    override suspend fun syncCaseStudies(userId: String): Result<Unit> = Result.success(Unit)

    override suspend fun syncCaseStudyDetail(caseStudyId: String, userId: String): Result<Unit> = Result.success(Unit)

    override suspend fun toggleReaction(caseStudyId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userReactions = remoteDataSource.getReactionsForCaseStudy(caseStudyId)
            val existingReaction = userReactions.firstOrNull { it.user_id == userId }

            if (existingReaction != null) {
                remoteDataSource.removeReaction(caseStudyId, userId)
            } else {
                remoteDataSource.addReaction(caseStudyId, userId, "helpful")
            }
            cachedCaseStudies = null
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            android.util.Log.e("VidyaSetu_CaseStudyRepo", "Error toggling reaction on case study: $caseStudyId for user: $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun toggleBookmark(caseStudyId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userBookmarks = remoteDataSource.getBookmarksForCaseStudy(caseStudyId)
            val existingBookmark = userBookmarks.firstOrNull { it.user_id == userId }

            if (existingBookmark != null) {
                remoteDataSource.removeBookmark(caseStudyId, userId)
            } else {
                remoteDataSource.addBookmark(caseStudyId, userId)
            }
            cachedCaseStudies = null
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
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
            cachedCaseStudies = null
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
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
                    coverImageUrl = dto.cover_image_url ?: "",
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
            if (e is kotlinx.coroutines.CancellationException) throw e
            android.util.Log.e("VidyaSetu_CaseStudyRepo", "Error fetching user uploaded case studies", e)
            Result.failure(e)
        }
    }
}
