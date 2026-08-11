package com.vidyasetuai.feature_auth.presentation.screen

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShieldAlert
import com.composables.icons.lucide.Smartphone
import com.vidyasetuai.core.auth.SessionManager
import com.vidyasetuai.core.security.CryptoManager
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_auth.data.repository.TrustedDeviceRepository
import kotlinx.coroutines.launch

@Composable
fun ManageTrustedDevicesScreen(
    sessionManager: SessionManager,
    trustedDeviceRepository: TrustedDeviceRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeUserId by remember { mutableStateOf(sessionManager.getUserId() ?: "") }
    var activeUserEmail by remember { mutableStateOf(sessionManager.getUserEmail() ?: "") }
    val deviceId = sessionManager.getDeviceId()
    val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"

    var isTrusted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var publicKeyPem by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (activeUserId.isEmpty()) {
            val account = sessionManager.getTrustedAccounts().firstOrNull()
            if (account != null) {
                activeUserId = account.userId
                activeUserEmail = account.email
            }
        }
        if (activeUserId.isNotEmpty()) {
            publicKeyPem = CryptoManager.getPublicKeyPem(activeUserId)
            isTrusted = publicKeyPem != null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Lucide.ArrowLeft,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Trusted Device Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Device Status Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AppColors.EmeraldGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Smartphone,
                            contentDescription = "Phone",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = deviceName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isTrusted) "🟢 Registered Hardware Trusted Device" else "🔴 Not Registered for Quick Login",
                            fontSize = 12.sp,
                            color = if (isTrusted) AppColors.EmeraldGreen else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable Biometric Quick Login",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = isTrusted,
                        onCheckedChange = { checked ->
                            scope.launch {
                                isLoading = true
                                if (checked) {
                                    val result = trustedDeviceRepository.registerDevicePublicKey(activeUserId, activeUserEmail)
                                    if (result.isSuccess) {
                                        isTrusted = true
                                        publicKeyPem = CryptoManager.getPublicKeyPem(activeUserId)
                                        Toast.makeText(context, "Device registered as Trusted!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Registration failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    val result = trustedDeviceRepository.revokeDeviceTrust(activeUserId, activeUserEmail)
                                    if (result.isSuccess) {
                                        isTrusted = false
                                        publicKeyPem = null
                                        Toast.makeText(context, "Device trust revoked", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                isLoading = false
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = AppColors.EmeraldGreen)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isTrusted) {
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        val result = trustedDeviceRepository.revokeDeviceTrust(activeUserId, activeUserEmail)
                        if (result.isSuccess) {
                            isTrusted = false
                            publicKeyPem = null
                            Toast.makeText(context, "Trusted device removed successfully", Toast.LENGTH_SHORT).show()
                        }
                        isLoading = false
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Lucide.ShieldAlert,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Remove Trust from This Device",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
