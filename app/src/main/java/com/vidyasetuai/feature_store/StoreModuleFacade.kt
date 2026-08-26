package com.vidyasetuai.feature_store

import android.content.Context
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.sync.StoreWorkspaceSyncEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * High-level centralized lifecycle and database facade for the `feature_store` micro-module.
 * Ensures zero leakage of internal database or sync logic into MainActivity, LogoutManager, or Dashboard.
 */
object StoreModuleFacade {

    /**
     * Eagerly pre-warms Room SQLite database file on disk during application cold start.
     */
    suspend fun prewarmDatabase(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                StoreDatabase.getDatabase(context).openHelper.writableDatabase
            } catch (e: Exception) {
                // Non-fatal, will initialize on first query
            }
        }
    }

    /**
     * Triggers silent background synchronization for Store workspace (24 tables).
     */
    suspend fun triggerBackgroundSync(context: Context, businessId: String? = null): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                StoreWorkspaceSyncEngine(context).triggerFullNetworkSync(businessId)
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Clears all local tables in store_database.db upon user logout (Pillar 1 & 3 Compliance).
     */
    suspend fun clearLocalData(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                StoreDatabase.getDatabase(context).clearAllTables()
            } catch (e: Exception) {
                // Handle or log error cleanly
            }
        }
    }
}
