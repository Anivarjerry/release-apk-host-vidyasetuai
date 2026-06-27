package com.vidyasetuai.core.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.composables.icons.lucide.*
import com.vidyasetuai.R
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.auth.PermissionManager

data class PermissionItem(
    val titleRes: Int,
    val descRes: Int,
    val isGranted: Boolean,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun PermissionGateScreen(
    onAllPermissionsGranted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observe permission states locally
    var hasNotification by remember { mutableStateOf(PermissionManager.isNotificationPermissionGranted(context)) }
    var hasCamera by remember { mutableStateOf(PermissionManager.isCameraPermissionGranted(context)) }
    var hasBatteryExemption by remember { mutableStateOf(PermissionManager.isBatteryOptimizationExemptionGranted(context)) }

    fun refreshStates() {
        hasNotification = PermissionManager.isNotificationPermissionGranted(context)
        hasCamera = PermissionManager.isCameraPermissionGranted(context)
        hasBatteryExemption = PermissionManager.isBatteryOptimizationExemptionGranted(context)

        // If everything is satisfied, trigger callback to return to dashboard
        if (hasNotification && hasCamera && hasBatteryExemption) {
            onAllPermissionsGranted()
        }
    }

    // Refresh states when screen is first shown
    LaunchedEffect(Unit) {
        refreshStates()
    }

    // Monitor App Lifecycle to refresh status automatically when returning from App Settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshStates()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Standard Permissions Launcher (Camera, Notifications)
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        refreshStates()
    }

    val permissionItems = remember(hasNotification, hasCamera, hasBatteryExemption) {
        listOf(
            PermissionItem(
                titleRes = R.string.perm_notification_title,
                descRes = R.string.perm_notification_desc,
                isGranted = hasNotification,
                icon = Lucide.Bell
            ),
            PermissionItem(
                titleRes = R.string.perm_camera_title,
                descRes = R.string.perm_camera_desc,
                isGranted = hasCamera,
                icon = Lucide.Camera
            ),
            PermissionItem(
                titleRes = R.string.perm_battery_title,
                descRes = R.string.perm_battery_desc,
                isGranted = hasBatteryExemption,
                icon = Lucide.CircleAlert
            )
        )
    }

    val isDark = isSystemInDarkTheme()
    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = 24.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // Branding
        VidyaSetuLogoBranding(logoWidth = 120.dp, textSize = 22.sp)

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = stringResource(id = R.string.permission_gate_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(id = R.string.permission_gate_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Permission Checklist
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(permissionItems) { item ->
                PermissionRow(item = item, surfaceColor = surfaceColor, borderColor = borderColor, isDark = isDark)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actions Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Secondary button: Settings
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(21.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.go_to_settings),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Primary button: Grant permissions / Enable optimizations ignore
            Button(
                onClick = {
                    when {
                        // 1. Request normal runtime permissions first (Camera, Notifications)
                        !hasCamera || !hasNotification -> {
                            val requestList = mutableListOf<String>()
                            if (!hasCamera) requestList.add(Manifest.permission.CAMERA)
                            if (!hasNotification && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                requestList.add(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            permissionsLauncher.launch(requestList.toTypedArray())
                        }
                        // 2. Prompt ignoring battery optimization
                        !hasBatteryExemption -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                                if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
                                     val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                         data = Uri.parse("package:${context.packageName}")
                                     }
                                     try {
                                         context.startActivity(intent)
                                     } catch (e: Exception) {
                                         // Fallback if settings intent fails
                                         val fallBackIntent = Intent(Settings.ACTION_SETTINGS)
                                         context.startActivity(fallBackIntent)
                                     }
                                }
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(21.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.EmeraldGreen,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1.2f)
                    .height(42.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.grant_permissions_btn),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PermissionRow(
    item: PermissionItem,
    surfaceColor: Color,
    borderColor: Color,
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(surfaceColor)
            .border(0.5.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Circle
        val circleBg = if (item.isGranted) {
            AppColors.EmeraldGreen.copy(alpha = 0.08f)
        } else {
            if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
        }
        val circleTint = if (item.isGranted) AppColors.EmeraldGreen else Color.Gray

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(circleBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = circleTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title and Desc
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(id = item.titleRes),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(id = item.descRes),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Status Indicator
        if (item.isGranted) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(AppColors.EmeraldGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.Check,
                    contentDescription = "Granted",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(if (isDark) Color(0xFF332020) else Color(0xFFFFEBEE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.CircleAlert,
                    contentDescription = "Pending",
                    tint = if (isDark) Color(0xFFFF453A) else Color(0xFFD32F2F),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
