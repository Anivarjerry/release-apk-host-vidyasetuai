package com.vidyasetuai.core.update.domain.model

enum class UpdateType {
    NONE,
    OPTIONAL,
    FORCE
}

data class AppVersionInfo(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val isForceUpdate: Boolean,
    val releaseNotesEn: String,
    val releaseNotesHi: String
)

data class UpdateCheckResult(
    val updateType: UpdateType,
    val info: AppVersionInfo?
)
