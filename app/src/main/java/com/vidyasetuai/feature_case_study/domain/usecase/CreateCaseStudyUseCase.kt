package com.vidyasetuai.feature_case_study.domain.usecase

import com.vidyasetuai.feature_case_study.domain.repository.CaseStudyRepository

class CreateCaseStudyUseCase(
    private val repository: CaseStudyRepository
) {
    suspend operator fun invoke(
        title: String,
        shortDescription: String,
        coverImageUrl: String,
        language: String,
        tags: List<String>,
        readTimeMinutes: Int?,
        detailedContent: String,
        additionalImageUrls: List<String>,
        userId: String
    ): Result<Unit> {
        return repository.createCaseStudy(
            title = title,
            shortDescription = shortDescription,
            coverImageUrl = coverImageUrl,
            language = language,
            tags = tags,
            readTimeMinutes = readTimeMinutes,
            detailedContent = detailedContent,
            additionalImageUrls = additionalImageUrls,
            userId = userId
        )
    }
}
