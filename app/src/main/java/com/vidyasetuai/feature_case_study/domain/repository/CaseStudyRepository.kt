package com.vidyasetuai.feature_case_study.domain.repository

import com.vidyasetuai.feature_case_study.domain.model.CaseStudy
import com.vidyasetuai.feature_case_study.domain.model.CaseStudyDetail
import kotlinx.coroutines.flow.Flow

interface CaseStudyRepository {
    fun getCaseStudies(userId: String): Flow<List<CaseStudy>>
    fun getCaseStudyDetail(caseStudyId: String, userId: String): Flow<CaseStudyDetail?>
    suspend fun toggleReaction(caseStudyId: String, userId: String): Result<Unit>
    suspend fun toggleBookmark(caseStudyId: String, userId: String): Result<Unit>
    suspend fun syncCaseStudies(userId: String): Result<Unit>
    suspend fun syncCaseStudyDetail(caseStudyId: String, userId: String): Result<Unit>
    suspend fun createCaseStudy(
        title: String,
        shortDescription: String,
        coverImageUrl: String,
        language: String,
        tags: List<String>,
        readTimeMinutes: Int?,
        detailedContent: String,
        additionalImageUrls: List<String>,
        userId: String
    ): Result<Unit>
    suspend fun getUserUploadedCaseStudies(userId: String): Result<List<CaseStudy>>
}
