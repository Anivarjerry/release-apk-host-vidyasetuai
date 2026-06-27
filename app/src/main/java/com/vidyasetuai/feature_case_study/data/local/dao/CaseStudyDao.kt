package com.vidyasetuai.feature_case_study.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_case_study.data.local.entity.CaseStudyDetailEntity
import com.vidyasetuai.feature_case_study.data.local.entity.CaseStudyEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CaseStudyDao {
    @Query("SELECT * FROM case_studies_cache ORDER BY publishedAt DESC")
    abstract fun getCaseStudies(): Flow<List<CaseStudyEntity>>

    @Query("SELECT * FROM case_study_details_cache WHERE id = :id")
    abstract fun getCaseStudyDetail(id: String): Flow<CaseStudyDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCaseStudies(caseStudies: List<CaseStudyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCaseStudyDetail(detail: CaseStudyDetailEntity)

    @Query("UPDATE case_studies_cache SET isReacted = :isReacted, reactionCount = :reactionCount WHERE id = :id")
    abstract suspend fun updateListReaction(id: String, isReacted: Boolean, reactionCount: Int)

    @Query("UPDATE case_study_details_cache SET isReacted = :isReacted, reactionCount = :reactionCount WHERE id = :id")
    abstract suspend fun updateDetailReaction(id: String, isReacted: Boolean, reactionCount: Int)

    @Transaction
    open suspend fun updateReaction(id: String, isReacted: Boolean, reactionCount: Int) {
        updateListReaction(id, isReacted, reactionCount)
        updateDetailReaction(id, isReacted, reactionCount)
    }

    @Query("UPDATE case_studies_cache SET isBookmarked = :isBookmarked, bookmarkCount = :bookmarkCount WHERE id = :id")
    abstract suspend fun updateListBookmark(id: String, isBookmarked: Boolean, bookmarkCount: Int)

    @Query("UPDATE case_study_details_cache SET isBookmarked = :isBookmarked, bookmarkCount = :bookmarkCount WHERE id = :id")
    abstract suspend fun updateDetailBookmark(id: String, isBookmarked: Boolean, bookmarkCount: Int)

    @Transaction
    open suspend fun updateBookmark(id: String, isBookmarked: Boolean, bookmarkCount: Int) {
        updateListBookmark(id, isBookmarked, bookmarkCount)
        updateDetailBookmark(id, isBookmarked, bookmarkCount)
    }

    @Query("DELETE FROM case_studies_cache")
    abstract suspend fun clearCaseStudies()

    @Query("DELETE FROM case_study_details_cache")
    abstract suspend fun clearCaseStudyDetails()
}
