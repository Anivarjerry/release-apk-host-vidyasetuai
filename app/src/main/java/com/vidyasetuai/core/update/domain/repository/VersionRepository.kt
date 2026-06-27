package com.vidyasetuai.core.update.domain.repository

import com.vidyasetuai.core.update.domain.model.AppVersionInfo

interface VersionRepository {
    suspend fun getLatestAppVersion(): Result<AppVersionInfo?>
}
