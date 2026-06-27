package com.vidyasetuai.feature_case_study.domain.usecase

import com.vidyasetuai.feature_case_study.domain.repository.CaseStudyRepository

class ToggleBookmarkUseCase(
    private val repository: CaseStudyRepository
) {
    suspend operator fun invoke(caseStudyId: String, userId: String): Result<Unit> {
        return repository.toggleBookmark(caseStudyId, userId)
    }
}
