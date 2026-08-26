package com.vidyasetuai.feature_campus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * 100% Isolated, Zero-Dependency Room Database for Campus Module (Store Architecture Pattern).
 * Database Name: campus_database.db
 */
@Database(
    entities = [
        CampusConnectionEntity::class,
        CampusMessageEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CampusDatabase : RoomDatabase() {

    abstract fun campusDao(): CampusDao

    companion object {
        private const val DATABASE_NAME = "campus_database.db"

        @Volatile
        private var INSTANCE: CampusDatabase? = null

        fun getDatabase(context: Context): CampusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampusDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
