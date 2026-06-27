package com.vidyasetuai.core.update.presentation.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.work.*
import com.vidyasetuai.R
import com.vidyasetuai.core.update.domain.model.AppVersionInfo
import com.vidyasetuai.core.update.presentation.download.ApkDownloadWorker
import com.vidyasetuai.core.update.presentation.install.ApkInstaller
import java.io.File

@Composable
fun UpdateScreen(
    info: AppVersionInfo,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val destinationFile = remember { File(context.getExternalFilesDir(null), "app_update.apk") }

    val workInfos by WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkFlow("apk_download")
        .collectAsState(initial = emptyList())

    val activeWorkInfo = workInfos.firstOrNull()
    val progressData = activeWorkInfo?.progress
    val downloadStatus = progressData?.getString("status") ?: activeWorkInfo?.state?.name ?: "IDLE"
    val progressPercent = progressData?.getInt("progress", 0) ?: 0
    val downloadedBytes = progressData?.getLong("downloaded_bytes", 0L) ?: 0L
    val totalBytes = progressData?.getLong("total_bytes", 0L) ?: 0L

    var showPermissionDialog by remember { mutableStateOf(false) }

    val downloadedMb = String.format("%.2f", downloadedBytes / (1024.0 * 1024.0))
    val totalMb = String.format("%.2f", totalBytes / (1024.0 * 1024.0))

    // Handle completed state
    LaunchedEffect(activeWorkInfo?.state) {
        if (activeWorkInfo?.state == WorkInfo.State.SUCCEEDED) {
            // Trigger install automatically when done
            if (ApkInstaller.canInstallApk(context)) {
                ApkInstaller.installApk(context, destinationFile)
            } else {
                showPermissionDialog = true
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.update_required),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "v${info.versionName} (${info.versionCode})",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Localized Release Notes
            val currentLang = context.resources.configuration.locales[0].language
            val releaseNotes = if (currentLang == "hi") info.releaseNotesHi else info.releaseNotesEn

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 160.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.whats_new),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = releaseNotes.ifEmpty { "Performance improvements and bug fixes." },
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Download Status & Progress
            if (downloadStatus == "DOWNLOADING" || downloadStatus == "PAUSED" || activeWorkInfo?.state == WorkInfo.State.RUNNING) {
                LinearProgressIndicator(
                    progress = progressPercent / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (downloadStatus == "PAUSED") {
                            stringResource(id = R.string.download_paused)
                        } else {
                            stringResource(id = R.string.downloading_update)
                        },
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$progressPercent% ($downloadedMb MB / $totalMb MB)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (downloadStatus == "PAUSED") {
                        Button(
                            onClick = { triggerDownload(context, info.apkUrl, destinationFile.absolutePath) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = stringResource(id = R.string.resume))
                        }
                    } else {
                        Button(
                            onClick = { WorkManager.getInstance(context).cancelUniqueWork("apk_download") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(text = stringResource(id = R.string.pause))
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            WorkManager.getInstance(context).cancelUniqueWork("apk_download")
                            destinationFile.delete()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }
            } else if (activeWorkInfo?.state == WorkInfo.State.SUCCEEDED) {
                Text(
                    text = stringResource(id = R.string.download_completed),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (ApkInstaller.canInstallApk(context)) {
                            ApkInstaller.installApk(context, destinationFile)
                        } else {
                            showPermissionDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.install_update))
                }
            } else if (activeWorkInfo?.state == WorkInfo.State.FAILED) {
                val errorMsg = progressData?.getString("error_message") ?: "Unknown network failure"
                Text(
                    text = stringResource(id = R.string.download_failed),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.error_download, errorMsg),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { triggerDownload(context, info.apkUrl, destinationFile.absolutePath) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.retry))
                }
            } else {
                // Initial State
                Button(
                    onClick = { triggerDownload(context, info.apkUrl, destinationFile.absolutePath) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.update_now))
                }
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(text = stringResource(id = R.string.install_permission_title)) },
            text = { Text(text = stringResource(id = R.string.install_permission_desc)) },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        ApkInstaller.requestInstallPermission(context)
                    }
                ) {
                    Text(text = stringResource(id = R.string.go_to_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun OptionalUpdateDialog(
    info: AppVersionInfo,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val destinationFile = remember { File(context.getExternalFilesDir(null), "app_update_optional.apk") }

    val workInfos by WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkFlow("apk_download")
        .collectAsState(initial = emptyList())

    val activeWorkInfo = workInfos.firstOrNull()
    val progressData = activeWorkInfo?.progress
    val downloadStatus = progressData?.getString("status") ?: activeWorkInfo?.state?.name ?: "IDLE"
    val progressPercent = progressData?.getInt("progress", 0) ?: 0
    val downloadedBytes = progressData?.getLong("downloaded_bytes", 0L) ?: 0L
    val totalBytes = progressData?.getLong("total_bytes", 0L) ?: 0L

    var showPermissionDialog by remember { mutableStateOf(false) }

    val downloadedMb = String.format("%.2f", downloadedBytes / (1024.0 * 1024.0))
    val totalMb = String.format("%.2f", totalBytes / (1024.0 * 1024.0))

    // Handle completed state
    LaunchedEffect(activeWorkInfo?.state) {
        if (activeWorkInfo?.state == WorkInfo.State.SUCCEEDED) {
            if (ApkInstaller.canInstallApk(context)) {
                ApkInstaller.installApk(context, destinationFile)
                onDismiss()
            } else {
                showPermissionDialog = true
            }
        }
    }

    Dialog(
        onDismissRequest = {
            if (downloadStatus != "DOWNLOADING" && activeWorkInfo?.state != WorkInfo.State.RUNNING) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = (downloadStatus != "DOWNLOADING"),
            dismissOnClickOutside = (downloadStatus != "DOWNLOADING")
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.update_available),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "v${info.versionName} (${info.versionCode})",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Release notes
                val currentLang = context.resources.configuration.locales[0].language
                val releaseNotes = if (currentLang == "hi") info.releaseNotesHi else info.releaseNotesEn

                Text(
                    text = releaseNotes.ifEmpty { "New version is available with optimizations." },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (downloadStatus == "DOWNLOADING" || downloadStatus == "PAUSED" || activeWorkInfo?.state == WorkInfo.State.RUNNING) {
                    LinearProgressIndicator(
                        progress = progressPercent / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (downloadStatus == "PAUSED") stringResource(id = R.string.download_paused) else stringResource(id = R.string.downloading_update),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$progressPercent%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (downloadStatus == "PAUSED") {
                            TextButton(
                                onClick = { triggerDownload(context, info.apkUrl, destinationFile.absolutePath) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = stringResource(id = R.string.resume))
                            }
                        } else {
                            TextButton(
                                onClick = { WorkManager.getInstance(context).cancelUniqueWork("apk_download") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = stringResource(id = R.string.pause))
                            }
                        }

                        TextButton(
                            onClick = {
                                WorkManager.getInstance(context).cancelUniqueWork("apk_download")
                                destinationFile.delete()
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                } else if (activeWorkInfo?.state == WorkInfo.State.SUCCEEDED) {
                    Button(
                        onClick = {
                            if (ApkInstaller.canInstallApk(context)) {
                                ApkInstaller.installApk(context, destinationFile)
                                onDismiss()
                            } else {
                                showPermissionDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.install_update))
                    }
                } else if (activeWorkInfo?.state == WorkInfo.State.FAILED) {
                    Text(
                        text = stringResource(id = R.string.download_failed),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { triggerDownload(context, info.apkUrl, destinationFile.absolutePath) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = stringResource(id = R.string.retry))
                        }
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(text = stringResource(id = R.string.update_later))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { triggerDownload(context, info.apkUrl, destinationFile.absolutePath) }
                        ) {
                            Text(text = stringResource(id = R.string.update_now))
                        }
                    }
                }
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(text = stringResource(id = R.string.install_permission_title)) },
            text = { Text(text = stringResource(id = R.string.install_permission_desc)) },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        ApkInstaller.requestInstallPermission(context)
                    }
                ) {
                    Text(text = stringResource(id = R.string.go_to_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

private fun triggerDownload(context: Context, url: String, destPath: String) {
    val workRequest = OneTimeWorkRequestBuilder<ApkDownloadWorker>()
        .setInputData(
            workDataOf(
                "apk_url" to url,
                "destination_path" to destPath
            )
        )
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .addTag("apk_download")
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "apk_download",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}
