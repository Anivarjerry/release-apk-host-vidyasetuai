package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.security.AppSecurityManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSecuritySettingsSubScreen(
    isHindi: Boolean,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val securityManager = remember { AppSecurityManager(context) }

    var isLockEnabled by remember { mutableStateOf(securityManager.isAppLockEnabled()) }
    var isBiometricEnabled by remember { mutableStateOf(securityManager.isBiometricEnabled()) }
    var selectedTimeout by remember { mutableStateOf(securityManager.getAutoLockTimeoutSeconds()) }
    var pinLength by remember { mutableStateOf(securityManager.getPinLength()) }
    var hasPinSet by remember { mutableStateOf(securityManager.hasPin()) }

    var showPinDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var isConfirmingPin by remember { mutableStateOf(false) }

    val isBiometricAvailable = remember { securityManager.isBiometricHardwareAvailable(context) }

    val bgColor = if (isDark) Color(0xFF121212) else Color(0xFFF8F9FA)
    val cardColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF212529)
    val subTextColor = if (isDark) Color.LightGray else Color(0xFF6C757D)
    val primaryColor = Color(0xFF10B981)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "ऐप लॉक और सुरक्षा" else "App Lock & Security",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = cardColor)
            )
        },
        containerColor = bgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: Enable App Lock
            Card(
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Lucide.Lock,
                            contentDescription = null,
                            tint = if (isLockEnabled) primaryColor else subTextColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = if (isHindi) "ऐप लॉक इनेबल करें" else "Enable App Lock",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = textColor
                            )
                            Text(
                                text = if (isHindi) "ऐप खोलने के लिए सुरक्षा मांगें" else "Require authentication to open app",
                                fontSize = 12.sp,
                                color = subTextColor
                            )
                        }
                    }
                    Switch(
                        checked = isLockEnabled,
                        onCheckedChange = { checked ->
                            if (checked && !hasPinSet) {
                                showPinDialog = true
                            } else {
                                isLockEnabled = checked
                                securityManager.setAppLockEnabled(checked)
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = primaryColor)
                    )
                }
            }

            if (isLockEnabled) {
                // Card 2: Biometrics Option (if available)
                if (isBiometricAvailable) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHindi) "फिंगरप्रिंट / बायोमेट्रिक अनलॉक" else "Fingerprint / Biometric Unlock",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    color = textColor
                                )
                                Text(
                                    text = if (isHindi) "फोन का फिंगरप्रिंट सेंसर इस्तेमाल करें" else "Use phone's fingerprint scanner",
                                    fontSize = 12.sp,
                                    color = subTextColor
                                )
                            }
                            Switch(
                                checked = isBiometricEnabled,
                                onCheckedChange = { checked ->
                                    isBiometricEnabled = checked
                                    securityManager.setBiometricEnabled(checked)
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = primaryColor)
                            )
                        }
                    }
                }

                // Card 3: PIN Setup & Options
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isHindi) "ऐप PIN कोड" else "App PIN Code",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = textColor
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = pinLength == 4,
                                onClick = {
                                    pinLength = 4
                                    showPinDialog = true
                                },
                                label = { Text("4 Digits PIN") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = primaryColor, selectedLabelColor = Color.White)
                            )
                            FilterChip(
                                selected = pinLength == 6,
                                onClick = {
                                    pinLength = 6
                                    showPinDialog = true
                                },
                                label = { Text("6 Digits PIN") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = primaryColor, selectedLabelColor = Color.White)
                            )
                        }

                        Button(
                            onClick = { showPinDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (hasPinSet) {
                                    if (isHindi) "PIN बदलें" else "Change PIN Code"
                                } else {
                                    if (isHindi) "नया PIN बनाएँ" else "Set PIN Code"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Card 4: Auto-Lock Timeout
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isHindi) "ऑटो-लॉक टाइमआउट" else "Auto-Lock Timeout",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = textColor
                        )
                        Text(
                            text = if (isHindi) "ऐप बैकग्राउंड में जाने के कितने समय बाद लॉक होगी?" else "Lock app after leaving background for:",
                            fontSize = 12.sp,
                            color = subTextColor
                        )

                        val timeoutOptions = listOf(
                            0L to (if (isHindi) "तुरंत (Immediately)" else "Immediately"),
                            30L to (if (isHindi) "30 सेकंड बाद" else "After 30 Seconds"),
                            60L to (if (isHindi) "1 मिनट बाद" else "After 1 Minute"),
                            300L to (if (isHindi) "5 मिनट बाद" else "After 5 Minutes")
                        )

                        timeoutOptions.forEach { (seconds, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedTimeout = seconds
                                        securityManager.setAutoLockTimeoutSeconds(seconds)
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    color = if (selectedTimeout == seconds) primaryColor else textColor,
                                    fontWeight = if (selectedTimeout == seconds) FontWeight.Bold else FontWeight.Normal
                                )
                                if (selectedTimeout == seconds) {
                                    Icon(
                                        imageVector = Lucide.Check,
                                        contentDescription = null,
                                        tint = primaryColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Set/Change PIN Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                newPinInput = ""
                confirmPinInput = ""
                isConfirmingPin = false
            },
            title = {
                Text(
                    text = if (!isConfirmingPin) {
                        if (isHindi) "नया $pinLength डिजिट PIN दर्ज करें" else "Enter New $pinLength Digit PIN"
                    } else {
                        if (isHindi) "PIN की पुष्टि करें" else "Confirm Your PIN"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = if (!isConfirmingPin) newPinInput else confirmPinInput,
                        onValueChange = { input ->
                            if (input.length <= pinLength && input.all { it.isDigit() }) {
                                if (!isConfirmingPin) newPinInput = input else confirmPinInput = input
                            }
                        },
                        label = { Text("PIN ($pinLength digits)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentInput = if (!isConfirmingPin) newPinInput else confirmPinInput
                        if (currentInput.length != pinLength) {
                            Toast.makeText(context, "PIN must be $pinLength digits", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (!isConfirmingPin) {
                            isConfirmingPin = true
                        } else {
                            if (newPinInput == confirmPinInput) {
                                securityManager.setPin(confirmPinInput)
                                securityManager.setAppLockEnabled(true)
                                isLockEnabled = true
                                hasPinSet = true
                                showPinDialog = false
                                newPinInput = ""
                                confirmPinInput = ""
                                isConfirmingPin = false
                                Toast.makeText(context, if (isHindi) "PIN सफलतापूर्वक सेट हुआ!" else "PIN set successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, if (isHindi) "PIN मैच नहीं हुआ" else "PINs do not match", Toast.LENGTH_SHORT).show()
                                confirmPinInput = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text(if (!isConfirmingPin) (if (isHindi) "आगे बढ़ें" else "Next") else (if (isHindi) "सेव करें" else "Save"))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPinDialog = false
                        newPinInput = ""
                        confirmPinInput = ""
                        isConfirmingPin = false
                    }
                ) {
                    Text(if (isHindi) "रद्द करें" else "Cancel")
                }
            }
        )
    }
}
