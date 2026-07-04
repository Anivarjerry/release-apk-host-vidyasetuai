package com.vidyasetuai.feature_institution.data.remote.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vidyasetuai.core.database.AppDatabase
import com.vidyasetuai.feature_institution.data.repository.InstitutionRepositoryImpl

class InstitutionSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = InstitutionRepositoryImpl(database.institutionDao())

        return try {
            val attendanceResult = repository.syncOfflineAttendanceLogs()
            val remarksResult = repository.syncRemarksOffline()
            
            if (attendanceResult.isSuccess && remarksResult.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
