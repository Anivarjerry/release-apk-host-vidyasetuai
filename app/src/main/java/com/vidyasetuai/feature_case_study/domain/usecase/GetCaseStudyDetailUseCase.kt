package com.vidyasetuai.feature_case_study.domain.usecase

import com.vidyasetuai.feature_case_study.domain.model.CaseStudyDetail
import com.vidyasetuai.feature_case_study.domain.repository.CaseStudyRepository
import kotlinx.coroutines.flow.Flow

class GetCaseStudyDetailUseCase(
    private val repository: CaseStudyRepository
) {
    operator fun invoke(caseStudyId: String, userId: String): Flow<CaseStudyDetail?> {
        return repository.getCaseStudyDetail(caseStudyId, userId)
    }

    suspend fun sync(caseStudyId: String, userId: String): Result<Unit> {
        return repository.syncCaseStudyDetail(caseStudyId, userId)
    }
}
