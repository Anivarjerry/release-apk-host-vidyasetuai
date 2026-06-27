package com.vidyasetuai.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vidyasetuai.feature_case_study.data.local.dao.CaseStudyDao
import com.vidyasetuai.feature_case_study.data.local.entity.CaseStudyDetailEntity
import com.vidyasetuai.feature_case_study.data.local.entity.CaseStudyEntity
import com.vidyasetuai.feature_profile.data.local.dao.UserProfileDao
import com.vidyasetuai.feature_profile.data.local.entity.ContributorVerificationEntity
import com.vidyasetuai.feature_profile.data.local.entity.UserProfileEntity
import com.vidyasetuai.feature_journey.data.local.dao.JourneyDao
import com.vidyasetuai.feature_journey.data.local.entity.*
import com.vidyasetuai.feature_campus.data.local.dao.CampusDao
import com.vidyasetuai.feature_campus.data.local.entity.RoomEntity
import com.vidyasetuai.feature_campus.data.local.entity.MessageEntity
import com.vidyasetuai.feature_campus.data.local.entity.ModerationSettingsEntity

import com.vidyasetuai.feature_institution.data.local.entity.*
import com.vidyasetuai.feature_institution.data.local.dao.InstitutionDao

@Database(
    entities = [
        // ── Core / Cross-feature ───────────────────────────────────────────
        CaseStudyEntity::class,
        CaseStudyDetailEntity::class,
        UserProfileEntity::class,
        ContributorVerificationEntity::class,
        GlobalJourneyTemplateEntity::class,
        OrganizationJourneyTemplateEntity::class,
        OrganizationParentJourneyTemplateEntity::class,
        GlobalJourneyTaskEntity::class,
        OrganizationJourneyTaskEntity::class,
        OrganizationParentJourneyTaskEntity::class,
        GlobalJourneyMcqEntity::class,
        OrganizationJourneyMcqEntity::class,
        OrganizationParentJourneyMcqEntity::class,
        UserJourneyEntity::class,
        UserJourneyTaskProgressEntity::class,
        UserJourneyMcqProgressEntity::class,
        RoomEntity::class,
        MessageEntity::class,
        ModerationSettingsEntity::class,

        // ── Institution — पुरानी entities (already registered) ─────────────
        WorkspaceEntity::class,
        LocalStudentEntity::class,
        OfflineCacheEntity::class,
        LocalLeaveEntity::class,
        LocalFeePaymentEntity::class,
        LocalStudentAttendanceEntity::class,
        LocalDriverBusDetailsEntity::class,
        LocalStaffSalaryDetailsEntity::class,
        LocalStaffSalaryPaymentEntity::class,
        LocalContentFeedItemEntity::class,
        LocalGuardianStudentEntity::class,
        LocalActiveSessionEntity::class,
        LocalChildOrgEntity::class,
        LocalOrgClassEntity::class,
        LocalOrgSectionEntity::class,
        LocalPendingApprovalEntity::class,
        LocalStudentImageVectorEntity::class,
        LocalStudentQrIdentityEntity::class,
        LocalStudentIdCardEntity::class,
        LocalParentBusTripEntity::class,
        LocalParentBusTripAttendanceLogEntity::class,
        LocalRemarkEntity::class,
        LocalRemarkTargetEntity::class,
        LocalGlobalStaffRoleEntity::class,

        // ── Institution — नई Global entities (LocalGlobalEntities.kt) ──────
        LocalGlobalAttendanceStatusEntity::class,
        LocalGlobalBloodGroupEntity::class,
        LocalGlobalBoardEntity::class,
        LocalGlobalClassEntity::class,
        LocalGlobalExamSubjectRuleEntity::class,
        LocalGlobalExamTypeEntity::class,
        LocalGlobalExpenseTypeEntity::class,
        LocalGlobalFacilityEntity::class,
        LocalGlobalLanguageEntity::class,
        LocalGlobalMediumEntity::class,
        LocalGlobalOrgParentTypeEntity::class,
        LocalGlobalOrgTypeEntity::class,
        LocalGlobalRelationshipTypeEntity::class,
        LocalGlobalResultStatusEntity::class,
        LocalGlobalStudentCategoryEntity::class,
        LocalGlobalStudentStatusEntity::class,
        LocalGlobalSubjectEntity::class,

        // ── Institution — नई Org Core entities (LocalOrgCoreEntities.kt) ───
        LocalOrgAttendanceStatusEntity::class,
        LocalOrgBoardEntity::class,
        LocalBusChildAssignmentEntity::class,
        LocalOrgExamEntity::class,
        LocalFeeAssignmentEntity::class,
        LocalGuardianUserLinkEntity::class,
        LocalHolidayEntity::class,
        LocalOrgLanguageEntity::class,
        LocalOrgMediumEntity::class,
        LocalParentBusEntity::class,
        LocalParentExpenseEntity::class,
        LocalParentOrganizationEntity::class,
        LocalParentOrgProfileEntity::class,
        LocalParentOrgUserEntity::class,
        LocalOrgPeriodEntity::class,
        LocalOrgProfileEntity::class,
        LocalOrgUserEntity::class,

        // ── Institution — नई Student entities (LocalOrgStudentEntities.kt) ─
        LocalStudentEnrollmentEntity::class,
        LocalGuardianEntity::class,
        LocalStudentAdditionalDetailsEntity::class,
        LocalStudentAdditionalFeeEntity::class,
        LocalStudentExamMarkEntity::class,
        LocalStudentHomeworkEntity::class,
        LocalStudentMarkEntity::class,
        LocalStudentSubjectEntity::class,
        LocalStudentUserLinkEntity::class,

        // ── Institution — नई Staff entities (LocalOrgStaffEntities.kt) ─────
        LocalParentStaffEntity::class,
        LocalParentStaffAttendanceEntity::class,
        LocalParentStaffBusEnrollmentEntity::class,
        LocalParentStaffBusFareEntity::class,
        LocalParentStaffLeaveQuotaEntity::class,
        LocalParentStaffSalaryPayoutEntity::class,
        LocalParentStaffUserLinkEntity::class,
        LocalParentBusStaffAssignmentEntity::class,

        // ── Institution — नई Session/Content/Misc entities ─────────────────
        // (LocalStudentBusAssignmentEntity.kt में define हैं)
        LocalSessionClassEntity::class,
        LocalSessionSectionEntity::class,
        LocalSessionSubjectEntity::class,
        LocalContentAssignmentEntity::class,
        LocalTimetableScheduleEntity::class,
        LocalRemarkAttachmentEntity::class,
        LocalRemarkHistoryEntity::class,
        LocalStudentBusAssignmentEntity::class,
    ],
    version = 14,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun caseStudyDao(): CaseStudyDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun journeyDao(): JourneyDao
    abstract fun campusDao(): CampusDao
    abstract fun institutionDao(): InstitutionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vidyasetu_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}