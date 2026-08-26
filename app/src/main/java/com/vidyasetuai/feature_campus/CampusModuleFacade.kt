package com.vidyasetuai.feature_campus

import android.content.Context
import android.util.Log
import com.vidyasetuai.feature_campus.data.local.CampusDatabase
import com.vidyasetuai.feature_campus.data.repository.CampusRepository
import com.vidyasetuai.feature_campus.data.repository.CampusRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Enterprise Singleton & Centralized Lifecycle Facade for `feature_campus` micro-module.
 * Conforms to the 5 Pillars of Enterprise Auth & Session Resilience (AGENTS.md).
 */
object CampusModuleFacade {
    private const val TAG = "CampusModuleFacade"

    @Volatile
    private var repository: CampusRepository? = null

    /**
     * Initializes Campus Module and Repository from isolated CampusDatabase.
     */
    fun initialize(context: Context): CampusRepository {
        return repository ?: synchronized(this) {
            val db = CampusDatabase.getDatabase(context)
            val repo = CampusRepositoryImpl(
                campusDao = db.campusDao(),
                context = context.applicationContext
            )
            repository = repo
            repo
        }
    }

    fun getRepository(): CampusRepository? = repository

    /**
     * Eagerly pre-warms Campus Room SQLite database file on disk during application cold start.
     */
    suspend fun prewarmDatabase(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                CampusDatabase.getDatabase(context).openHelper.writableDatabase
                Log.d(TAG, "Campus database prewarmed successfully.")
            } catch (e: Exception) {
                Log.w(TAG, "Campus database prewarm deferred: ${e.message}")
            }
        }
    }

    /**
     * Triggers silent background synchronization for Campus workspace via Traffic Police SafeSupabaseInvoker.
     */
    suspend fun triggerBackgroundSync(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val repo = initialize(context)
                val result = repo.syncCampusData()
                Log.d(TAG, "Campus background sync completed: isSuccess=${result.isSuccess}")
                result.isSuccess
            } catch (e: Exception) {
                Log.w(TAG, "Campus background sync error: ${e.message}")
                false
            }
        }
    }

    /**
     * Clears all local tables and caches in campus_database.db upon user logout (Pillar 3 & 4 Compliance).
     */
    suspend fun clearLocalData(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val repo = repository
                repo?.clearAllLocalData()
                CampusDatabase.getDatabase(context).clearAllTables()
                Log.d(TAG, "Campus local data wiped on logout.")
            } catch (e: Exception) {
                Log.e(TAG, "Error wiping campus data on logout: ${e.message}", e)
            }
        }
    }
}

