package com.vidyasetuai.feature_journey.data.remote.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vidyasetuai.core.database.AppDatabase
import com.vidyasetuai.feature_journey.data.local.datasource.JourneyLocalDataSource
import com.vidyasetuai.feature_journey.data.remote.datasource.JourneyRemoteDataSource
import com.vidyasetuai.feature_journey.data.repository.JourneyRepositoryImpl

class JourneySyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val localDataSource = JourneyLocalDataSource(database.journeyDao())
        val remoteDataSource = JourneyRemoteDataSource()
        val repository = JourneyRepositoryImpl(localDataSource, remoteDataSource)

        return try {
            val syncResult = repository.performBackgroundSync()
            if (syncResult.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
