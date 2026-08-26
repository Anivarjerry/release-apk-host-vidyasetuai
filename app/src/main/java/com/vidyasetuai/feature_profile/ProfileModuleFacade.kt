package com.vidyasetuai.feature_profile

import android.content.Context
import android.util.Log
import com.vidyasetuai.feature_profile.data.local.ProfileAvatarCacheManager
import com.vidyasetuai.feature_profile.data.local.ProfileDatabase
import com.vidyasetuai.feature_profile.data.remote.ProfileRemoteDataSource
import com.vidyasetuai.feature_profile.data.repository.ProfileRepository
import com.vidyasetuai.feature_profile.data.repository.ProfileRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Enterprise Facade for Profile Module.
 * Standardized across VidyaSetu AI with prewarmDatabase, triggerBackgroundSync, and clearLocalData.
 */
object ProfileModuleFacade {

    @Volatile
    private var repositoryInstance: ProfileRepository? = null

    fun getRepository(context: Context): ProfileRepository {
        return repositoryInstance ?: synchronized(this) {
            val db = ProfileDatabase.getDatabase(context)
            val dao = db.profileDao()
            val remoteDS = ProfileRemoteDataSource(context)
            val avatarCacheManager = ProfileAvatarCacheManager(context, dao)
            val repo = ProfileRepositoryImpl(context, dao, remoteDS, avatarCacheManager)
            repositoryInstance = repo
            repo
        }
    }

    /**
     * Pre-warms the SQLite database on App Cold Start.
     */
    fun prewarmDatabase(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = ProfileDatabase.getDatabase(context)
                db.profileDao().getMyProfile()
                Log.d("ProfileModuleFacade", "Profile database prewarmed successfully.")
            } catch (e: Exception) {
                Log.e("ProfileModuleFacade", "Failed to prewarm profile database: ${e.message}")
            }
        }
    }

    /**
     * Triggers background synchronization with Supabase via Traffic Police.
     */
    fun triggerBackgroundSync(context: Context, targetUserId: String? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = getRepository(context)
                val result = repo.syncProfileData(targetUserId)
                Log.d("ProfileModuleFacade", "Profile background sync completed: isSuccess=${result.isSuccess}")
            } catch (e: Exception) {
                Log.e("ProfileModuleFacade", "Background sync exception: ${e.message}")
            }
        }
    }

    /**
     * Cleans up local profile database and disk media cache upon logout.
     */
    fun clearLocalData(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = getRepository(context)
                repo.clearLocalData()
                Log.d("ProfileModuleFacade", "Profile local data cleared.")
            } catch (e: Exception) {
                Log.e("ProfileModuleFacade", "Failed to clear profile local data: ${e.message}")
            }
        }
    }
}
