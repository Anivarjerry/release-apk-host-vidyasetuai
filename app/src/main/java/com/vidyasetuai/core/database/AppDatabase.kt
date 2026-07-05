package com.vidyasetuai.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vidyasetuai.feature_profile.data.local.dao.UserProfileDao
import com.vidyasetuai.feature_profile.data.local.entity.ContributorVerificationEntity
import com.vidyasetuai.feature_profile.data.local.entity.UserProfileEntity
import com.vidyasetuai.feature_journey.data.local.dao.JourneyDao
import com.vidyasetuai.feature_journey.data.local.entity.*
import com.vidyasetuai.feature_campus.data.local.dao.CampusDao
import com.vidyasetuai.feature_campus.data.local.entity.RoomEntity
import com.vidyasetuai.feature_campus.data.local.entity.MessageEntity
import com.vidyasetuai.feature_campus.data.local.entity.ModerationSettingsEntity
import com.vidyasetuai.feature_institution.data.local.dao.InstitutionDao
import com.vidyasetuai.feature_institution.data.local.entity.*

@Database(
    entities = [
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

        WorkspaceEntity::class,
        LocalChildOrgSetupEntity::class,
        LocalParentBusEntity::class,
        LocalStudentEntity::class,
        LocalStudentAdditionalFeeEntity::class,
        LocalStudentFeePaymentEntity::class,
        LocalParentExpenseEntity::class,
        LocalStudentAttendanceEntity::class,
        LocalParentBusTripAttendanceLogEntity::class,
        LocalParentBusTripEntity::class,
        LocalOrganizationLeaveEntity::class,
        LocalOrganizationRemarkEntity::class,
        LocalCalendarEventEntity::class,
        LocalParentStaffAttendanceEntity::class,
        LocalParentStaffEntity::class,
        LocalStudentUserLinkEntity::class,
        LocalOrganizationExamEntity::class,
        LocalExamSubjectSettingEntity::class,
        LocalStudentExamMarkEntity::class,
        LocalBusRouteEntity::class
    ],
    version = 27,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
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
