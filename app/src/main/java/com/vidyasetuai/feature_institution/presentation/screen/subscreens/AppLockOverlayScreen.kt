package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import com.composables.icons.lucide.*
import com.vidyasetuai.core.security.AppSecurityManager

@Composable
fun AppLockOverlayScreen(
    isHindi: Boolean,
    isDark: Boolean,
    onUnlocked: () -> Unit
) {
    val context = LocalContext.current
    val securityManager = remember { AppSecurityManager(context) }
    val pinLength = remember { securityManager.getPinLength() }
    val isBiometricEnabled = remember { securityManager.isBiometricEnabled() }

    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Dynamic Theme-based Colors
    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val subTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val numpadBgColor = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)
    val digitTextColor = if (isDark) Color.White else Color(0xFF0F172A)
    val iconColor = if (isDark) Color.White else Color(0xFF334155)
    val unfilledDotColor = if (isDark) Color.White.copy(alpha = 0.25f) else Color(0xFFCBD5E1)
    val primaryColor = Color(0xFF10B981)

    // Function to launch Biometric Prompt
    val launchBiometric = remember(context) {
        {
            if (context is FragmentActivity && isBiometricEnabled && securityManager.isBiometricHardwareAvailable(context)) {
                val executor = ContextCompat.getMainExecutor(context)
                val biometricPrompt = BiometricPrompt(
                    context,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            onUnlocked()
                        }
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                        }
                    }
                )

                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(if (isHindi) "विद्यासेतु AI अनलॉक करें" else "Unlock VidyaSetu AI")
                    .setSubtitle(if (isHindi) "अपना फिंगरप्रिंट का उपयोग करें" else "Use your fingerprint to continue")
                    .setNegativeButtonText(if (isHindi) "PIN का उपयोग करें" else "Use PIN")
                    .build()

                biometricPrompt.authenticate(promptInfo)
            }
        }
    }

    // Auto-launch biometric on screen presentation
    LaunchedEffect(Unit) {
        if (isBiometricEnabled) {
            launchBiometric()
        }
    }

    // Auto-verify PIN when length reaches target length
    LaunchedEffect(enteredPin) {
        if (enteredPin.length == pinLength) {
            if (securityManager.verifyPin(enteredPin)) {
                onUnlocked()
            } else {
                errorMessage = if (isHindi) "गलत PIN दर्ज किया गया!" else "Incorrect PIN entered!"
                enteredPin = ""
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Lock,
                        contentDescription = "Locked",
                        tint = primaryColor,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "VidyaSetu AI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = textColor
                )
                Text(
                    text = if (isHindi) "ऐप अनलॉक करने के लिए PIN दर्ज करें" else "Enter PIN to Unlock",
                    fontSize = 14.sp,
                    color = subTextColor
                )
            }

            // PIN Dots Indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pinLength) { index ->
                        val isFilled = index < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFilled) primaryColor else unfilledDotColor
                                )
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFEF4444),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Keypad & Biometric Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val numpad = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("bio", "0", "del")
                )

                numpad.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { digit ->
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (digit) {
                                            "bio", "del" -> Color.Transparent
                                            else -> numpadBgColor
                                        }
                                    )
                                    .clickable {
                                        errorMessage = null
                                        when (digit) {
                                            "bio" -> launchBiometric()
                                            "del" -> if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                            else -> if (enteredPin.length < pinLength) enteredPin += digit
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                when (digit) {
                                    "bio" -> {
                                        if (isBiometricEnabled) {
                                            Icon(
                                                imageVector = Lucide.Fingerprint,
                                                contentDescription = "Biometric Fingerprint",
                                                tint = primaryColor,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                    }
                                    "del" -> {
                                        Icon(
                                            imageVector = Lucide.Delete,
                                            contentDescription = "Delete",
                                            tint = iconColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = digit,
                                            color = digitTextColor,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
