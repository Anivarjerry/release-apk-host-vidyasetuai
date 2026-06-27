package com.vidyasetuai.core.update.data.repository

import com.vidyasetuai.core.update.data.remote.datasource.VersionRemoteDataSource
import com.vidyasetuai.core.update.data.mapper.toDomain
import com.vidyasetuai.core.update.domain.model.AppVersionInfo
import com.vidyasetuai.core.update.domain.repository.VersionRepository

class VersionRepositoryImpl(
    private val remoteDataSource: VersionRemoteDataSource
) : VersionRepository {
    override suspend fun getLatestAppVersion(): Result<AppVersionInfo?> {
        return try {
            val dto = remoteDataSource.getLatestVersion()
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
