package com.vidyasetuai.feature_profile.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Isolated Standalone Room Database for Profile module (`profile_database.db`).
 * Zero cross-feature lock contention (Pillar 2 AGENTS.md).
 */
@Database(
    entities = [
        UserProfileEntity::class,
        ProfileInspirationEntity::class,
        ProfileVerificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ProfileDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: ProfileDatabase? = null

        fun getDatabase(context: Context): ProfileDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProfileDatabase::class.java,
                    "profile_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
