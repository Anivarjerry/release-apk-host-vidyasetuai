package com.vidyasetuai.feature_case_study.data.mapper

import com.vidyasetuai.feature_case_study.data.local.entity.CaseStudyDetailEntity
import com.vidyasetuai.feature_case_study.data.local.entity.CaseStudyEntity
import com.vidyasetuai.feature_case_study.data.remote.dto.CaseStudyDetailDto
import com.vidyasetuai.feature_case_study.data.remote.dto.CaseStudyDto
import com.vidyasetuai.feature_case_study.domain.model.CaseStudy
import com.vidyasetuai.feature_case_study.domain.model.CaseStudyDetail
import com.vidyasetuai.feature_case_study.domain.model.ContentBlock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray

fun CaseStudyDto.toEntity(
    isReacted: Boolean,
    reactionCount: Int,
    isBookmarked: Boolean,
    bookmarkCount: Int,
    authorName: String = "Academic Scholar",
    authorUsername: String = "scholar",
    authorProfilePicUrl: String? = null,
    isAuthorVerified: Boolean = false
): CaseStudyEntity {
    return CaseStudyEntity(
        id = id,
        title = title,
        slug = slug,
        coverImageUrl = cover_image_url,
        shortDescription = short_description,
        language = language,
        tags = tags,
        readTimeMinutes = read_time_minutes,
        authorType = author_type,
        authorUserId = author_user_id,
        authorName = authorName,
        authorUsername = authorUsername,
        authorProfilePicUrl = authorProfilePicUrl,
        isAuthorVerified = isAuthorVerified,
        viewCount = view_count,
        publishedAt = published_at,
        createdAt = created_at,
        updatedAt = updated_at,
        isReacted = isReacted,
        reactionCount = reactionCount,
        isBookmarked = isBookmarked,
        bookmarkCount = bookmarkCount
    )
}

fun CaseStudyDetailDto.toEntity(
    isReacted: Boolean,
    reactionCount: Int,
    isBookmarked: Boolean,
    bookmarkCount: Int,
    authorName: String = "Academic Scholar",
    authorUsername: String = "scholar",
    authorProfilePicUrl: String? = null,
    isAuthorVerified: Boolean = false
): CaseStudyDetailEntity {
    return CaseStudyDetailEntity(
        id = id,
        title = title,
        slug = slug,
        coverImageUrl = cover_image_url,
        shortDescription = short_description,
        language = language,
        tags = tags,
        readTimeMinutes = read_time_minutes,
        authorType = author_type,
        authorUserId = author_user_id,
        authorName = authorName,
        authorUsername = authorUsername,
        authorProfilePicUrl = authorProfilePicUrl,
        isAuthorVerified = isAuthorVerified,
        viewCount = view_count,
        publishedAt = published_at,
        createdAt = created_at,
        updatedAt = updated_at,
        contentBlocksJson = content_blocks.toString(),
        additionalImageUrls = additional_image_urls,
        isReacted = isReacted,
        reactionCount = reactionCount,
        isBookmarked = isBookmarked,
        bookmarkCount = bookmarkCount
    )
}

fun CaseStudyEntity.toDomain(): CaseStudy {
    return CaseStudy(
        id = id,
        title = title,
        slug = slug,
        coverImageUrl = coverImageUrl,
        shortDescription = shortDescription,
        language = language,
        tags = tags,
        readTimeMinutes = readTimeMinutes,
        authorType = authorType,
        authorUserId = authorUserId,
        authorName = authorName,
        authorUsername = authorUsername,
        authorProfilePicUrl = authorProfilePicUrl,
        isAuthorVerified = isAuthorVerified,
        viewCount = viewCount,
        publishedAt = publishedAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isReacted = isReacted,
        reactionCount = reactionCount,
        isBookmarked = isBookmarked,
        bookmarkCount = bookmarkCount
    )
}

fun CaseStudyDetailEntity.toDomain(): CaseStudyDetail {
    val blocks = try {
        val jsonObject = Json.parseToJsonElement(contentBlocksJson) as? JsonObject
        val blocksArray = jsonObject?.get("blocks")?.jsonArray
        blocksArray?.map { blockElement ->
            ContentBlock.fromJson(blockElement as JsonObject)
        } ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    return CaseStudyDetail(
        id = id,
        title = title,
        slug = slug,
        coverImageUrl = coverImageUrl,
        shortDescription = shortDescription,
        language = language,
        tags = tags,
        readTimeMinutes = readTimeMinutes,
        authorType = authorType,
        authorUserId = authorUserId,
        authorName = authorName,
        authorUsername = authorUsername,
        authorProfilePicUrl = authorProfilePicUrl,
        isAuthorVerified = isAuthorVerified,
        viewCount = viewCount,
        publishedAt = publishedAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
        contentBlocks = blocks,
        additionalImageUrls = additionalImageUrls,
        isReacted = isReacted,
        reactionCount = reactionCount,
        isBookmarked = isBookmarked,
        bookmarkCount = bookmarkCount
    )
}
