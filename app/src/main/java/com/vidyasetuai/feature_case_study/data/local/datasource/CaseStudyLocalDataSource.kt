package com.vidyasetuai.feature_case_study.data.local.datasource

import com.vidyasetuai.feature_case_study.data.local.dao.CaseStudyDao
import com.vidyasetuai.feature_case_study.data.local.entity.CaseStudyDetailEntity
import com.vidyasetuai.feature_case_study.data.local.entity.CaseStudyEntity
import kotlinx.coroutines.flow.Flow

class CaseStudyLocalDataSource(
    private val caseStudyDao: CaseStudyDao
) {
    fun getCaseStudies(): Flow<List<CaseStudyEntity>> = caseStudyDao.getCaseStudies()

    fun getCaseStudyDetail(id: String): Flow<CaseStudyDetailEntity?> = caseStudyDao.getCaseStudyDetail(id)

    suspend fun insertCaseStudies(caseStudies: List<CaseStudyEntity>) = caseStudyDao.insertCaseStudies(caseStudies)

    suspend fun insertCaseStudyDetail(detail: CaseStudyDetailEntity) = caseStudyDao.insertCaseStudyDetail(detail)

    suspend fun updateReaction(id: String, isReacted: Boolean, reactionCount: Int) = caseStudyDao.updateReaction(id, isReacted, reactionCount)

    suspend fun updateBookmark(id: String, isBookmarked: Boolean, bookmarkCount: Int) = caseStudyDao.updateBookmark(id, isBookmarked, bookmarkCount)

    suspend fun clearCaseStudies() = caseStudyDao.clearCaseStudies()

    suspend fun clearCaseStudyDetails() = caseStudyDao.clearCaseStudyDetails()
}
