package com.vidyasetuai.core.update.data.mapper

import com.vidyasetuai.core.update.data.remote.dto.VersionDto
import com.vidyasetuai.core.update.domain.model.AppVersionInfo

fun VersionDto.toDomain(): AppVersionInfo {
    val notes = changelog ?: ""
    return AppVersionInfo(
        versionCode = build_number,
        versionName = version_number,
        apkUrl = file_url,
        isForceUpdate = force_update,
        releaseNotesEn = notes,
        releaseNotesHi = notes
    )
}
