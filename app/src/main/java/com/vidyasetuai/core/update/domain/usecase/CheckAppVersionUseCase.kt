package com.vidyasetuai.core.update.domain.usecase

import com.vidyasetuai.BuildConfig
import com.vidyasetuai.core.update.domain.model.UpdateCheckResult
import com.vidyasetuai.core.update.domain.model.UpdateType
import com.vidyasetuai.core.update.domain.repository.VersionRepository

class CheckAppVersionUseCase(
    private val repository: VersionRepository
) {
    suspend operator fun invoke(): UpdateCheckResult {
        return repository.getLatestAppVersion().fold(
            onSuccess = { info ->
                if (info == null) {
                    UpdateCheckResult(UpdateType.NONE, null)
                } else if (info.versionCode > BuildConfig.VERSION_CODE) {
                    val updateType = if (info.isForceUpdate) {
                        UpdateType.FORCE
                    } else {
                        UpdateType.OPTIONAL
                    }
                    UpdateCheckResult(updateType, info)
                } else {
                    UpdateCheckResult(UpdateType.NONE, info)
                }
            },
            onFailure = {
                // If checking fails, proceed to app for offline stability
                UpdateCheckResult(UpdateType.NONE, null)
            }
        )
    }
}
