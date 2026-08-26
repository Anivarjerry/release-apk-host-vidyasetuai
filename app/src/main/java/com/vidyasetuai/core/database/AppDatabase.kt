package com.vidyasetuai.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vidyasetuai.feature_institution.data.local.dao.InstitutionDao
import com.vidyasetuai.feature_institution.data.local.entity.*

@Database(
    entities = [
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
        LocalBusRouteEntity::class,
        LocalParentStaffSalaryEntity::class,
        LocalParentStaffSalaryPayoutEntity::class,
        LocalParentStaffSalaryPaymentEntity::class,
        LocalParentStaffBusEnrollmentEntity::class,
        LocalParentStaffBusFareEntity::class,
        LocalGlobalSessionEntity::class,
        LocalStaffAudioBeaconEntity::class
    ],
    version = 39,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
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
