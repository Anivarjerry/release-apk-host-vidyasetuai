package com.vidyasetuai.feature_case_study.domain.usecase

import com.vidyasetuai.feature_case_study.domain.model.CaseStudy
import com.vidyasetuai.feature_case_study.domain.repository.CaseStudyRepository
import kotlinx.coroutines.flow.Flow

class GetCaseStudyListUseCase(
    private val repository: CaseStudyRepository
) {
    operator fun invoke(userId: String): Flow<List<CaseStudy>> {
        return repository.getCaseStudies(userId)
    }

    suspend fun sync(userId: String): Result<Unit> {
        return repository.syncCaseStudies(userId)
    }
}
