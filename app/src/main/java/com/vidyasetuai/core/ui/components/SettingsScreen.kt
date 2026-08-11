package com.vidyasetuai.core.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onBack: () -> Unit,
    onOpenTournament: (() -> Unit)? = null,
    initialTarget: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sessionManager = remember { com.vidyasetuai.core.auth.SessionManager(context) }
    val prefs = remember { context.getSharedPreferences("app_settings", Context.MODE_PRIVATE) }
    val isHindi = currentLanguage == "hi"

    // Dynamically retrieve real app version from package manager
    val packageInfo = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: Exception) {
            null
        }
    }
    val versionName = packageInfo?.versionName ?: "1.8"

    var showThemeDialog by remember(initialTarget) { mutableStateOf(initialTarget == "theme") }
    var showLanguageDialog by remember(initialTarget) { mutableStateOf(initialTarget == "language") }
    var showManageTrustedDevices by remember { mutableStateOf(false) }
    var showLogoutConfirmationDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val trustedDeviceRepository = remember { com.vidyasetuai.feature_auth.data.repository.TrustedDeviceRepository(sessionManager) }

    val currentThemeLabel = when (currentTheme) {
        "light" -> if (isHindi) "लाइट थीम (Light Theme)" else "Light Theme"
        "dark" -> if (isHindi) "डार्क थीम (Dark Theme)" else "Dark Theme"
        else -> if (isHindi) "सिस्टम डिफ़ॉल्ट (System Default)" else "System Default"
    }

    val currentLanguageLabel = when (currentLanguage) {
        "hi" -> "हिंदी (Hindi)"
        else -> "English"
    }

    var showAboutScreen by remember { mutableStateOf(false) }
    var showTermsScreen by remember { mutableStateOf(false) }
    var showHelpScreen by remember(initialTarget) { mutableStateOf(initialTarget == "help") }
    var showSecurityScreen by remember(initialTarget) { mutableStateOf(initialTarget == "app_lock" || initialTarget == "security") }

    if (showSecurityScreen) {
        com.vidyasetuai.feature_institution.presentation.screen.subscreens.AppSecuritySettingsSubScreen(
            isHindi = isHindi,
            isDark = currentTheme == "dark",
            onBack = { showSecurityScreen = false }
        )
    } else if (showAboutScreen) {
        AboutScreen(onBack = { showAboutScreen = false })
    } else if (showTermsScreen) {
        TermsScreen(onBack = { showTermsScreen = false })
    } else if (showHelpScreen) {
        HelpSupportScreen(onBack = { showHelpScreen = false })
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowLeft,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isHindi) "सेटिंग्स" else "Settings",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                // Section 1: App Settings Header
                Text(
                    text = if (isHindi) "एप्लिकेशन सेटिंग्स" else "App Settings",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.EmeraldGreen,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )

                // Settings Items (Flat, no card border, WhatsApp style)
                SettingsRow(
                    icon = Lucide.Sun,
                    title = if (isHindi) "थीम चुनें" else "Choose Theme",
                    subtitle = currentThemeLabel,
                    onClick = { showThemeDialog = true }
                )

                SettingsRow(
                    icon = Lucide.Globe,
                    title = if (isHindi) "भाषा चुनें" else "Choose Language",
                    subtitle = currentLanguageLabel,
                    onClick = { showLanguageDialog = true }
                )

                SettingsRow(
                    icon = Lucide.Lock,
                    title = if (isHindi) "ऐप लॉक और सुरक्षा" else "App Lock & Security",
                    subtitle = if (isHindi) "फिंगरप्रिंट और PIN से ऐप सुरक्षित करें" else "Protect app with Fingerprint & PIN",
                    onClick = { showSecurityScreen = true }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .padding(vertical = 8.dp)
                )

                // Section: Upcoming Features Header
                Text(
                    text = if (isHindi) "आगामी फ़ीचर्स" else "Upcoming Features",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.EmeraldGreen,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )

                SettingsRow(
                    icon = Lucide.Trophy,
                    title = if (isHindi) "टूर्नामेंट अरीना (कमिंग सून)" else "Tournament Arena (Coming Soon)",
                    subtitle = if (isHindi) "क्विज़, चैलेंजेस और लीडरबोर्ड का प्रीव्यू देखें" else "Play, Learn & Level Up challenges preview",
                    onClick = { onOpenTournament?.invoke() }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .padding(vertical = 8.dp)
                )

                // Section 2: Support & Community Header
                Text(
                    text = if (isHindi) "सहायता और समुदाय" else "Support & Community",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.EmeraldGreen,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )

                SettingsRow(
                    icon = Lucide.Info,
                    title = if (isHindi) "मदद और सहायता" else "Help & Support",
                    subtitle = if (isHindi) "ईमेल, कॉल और सोशल मीडिया लिंक" else "Email, call & social media links",
                    onClick = { showHelpScreen = true }
                )

                SettingsRow(
                    icon = Lucide.MessageCircle,
                    title = if (isHindi) "कीड़ा रिपोर्ट / फीडबैक" else "Report a Bug / Feedback",
                    subtitle = if (isHindi) "व्हाट्सएप ग्रुप पर हमसे बात करें" else "Talk to us on WhatsApp group",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chat.whatsapp.com/KingWyOnciSFOVUTE88j1Z"))
                        context.startActivity(intent)
                    }
                )

                SettingsRow(
                    icon = Lucide.ExternalLink,
                    title = if (isHindi) "ऐप शेयर करें" else "Share App",
                    subtitle = if (isHindi) "विद्यासेतु ऐप साझा करें" else "Share VidyaSetu AI with others",
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Hey! Check out the VidyaSetu AI app: https://vidyasetuai.com")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, if (isHindi) "ऐप साझा करें" else "Share App via"))
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .padding(vertical = 8.dp)
                )

                // Section 3: Legal & Security Header
                Text(
                    text = if (isHindi) "कानूनी और सुरक्षा" else "Legal & Security",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.EmeraldGreen,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )

                SettingsRow(
                    icon = Lucide.FileText,
                    title = if (isHindi) "नियम और शर्तें" else "Terms & Conditions",
                    subtitle = if (isHindi) "ऐप के उपयोग के नियम" else "App usage rules",
                    onClick = { showTermsScreen = true }
                )

                SettingsRow(
                    icon = Lucide.Trash,
                    title = if (isHindi) "खाता हटाएं" else "Delete Account",
                    subtitle = if (isHindi) "व्हाट्सएप द्वारा खाता हटाने का अनुरोध करें" else "Request account deletion via WhatsApp",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chat.whatsapp.com/KingWyOnciSFOVUTE88j1Z"))
                        context.startActivity(intent)
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .padding(vertical = 8.dp)
                )

                // Section 4: App Info Header
                Text(
                    text = if (isHindi) "ऐप की जानकारी" else "App Info",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.EmeraldGreen,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )

                SettingsRow(
                    icon = Lucide.Lock,
                    title = if (isHindi) "भरोसेमंद डिवाइस और सुरक्षा" else "Trusted Devices & Security",
                    subtitle = if (isHindi) "बायोमेट्रिक क्विक लॉगिन प्रबंधित करें" else "Manage Biometric Quick Login & Keys",
                    onClick = { showManageTrustedDevices = true }
                )

                SettingsRow(
                    icon = Lucide.Sparkles,
                    title = if (isHindi) "हमारे बारे में" else "About Us",
                    subtitle = "v$versionName (${if (isHindi) "अप-टू-डेट" else "Up to date"})",
                    onClick = { showAboutScreen = true }
                )

                SettingsRow(
                    icon = Lucide.LogOut,
                    title = if (isHindi) "लॉग आउट" else "Log Out",
                    subtitle = if (isHindi) "खाता लॉग आउट करें" else "Log out of active session",
                    onClick = {
                        showLogoutConfirmationDialog = true
                    }
                )
            }
        }
    }

    // Theme Selector Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { 
                Text(
                    text = if (isHindi) "थीम चुनें" else "Choose Theme", 
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ) 
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val themes = listOf(
                        "system" to (if (isHindi) "सिस्टम डिफ़ॉल्ट (System Default)" else "System Default"),
                        "light" to (if (isHindi) "लाइट थीम (Light Theme)" else "Light Theme"),
                        "dark" to (if (isHindi) "डार्क थीम (Dark Theme)" else "Dark Theme")
                    )
                    themes.forEach { (themeKey, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    prefs.edit().putString("theme", themeKey).apply()
                                    onThemeChange(themeKey)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentTheme == themeKey,
                                onClick = {
                                    prefs.edit().putString("theme", themeKey).apply()
                                    onThemeChange(themeKey)
                                    showThemeDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = AppColors.EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = label, 
                                fontSize = 15.sp, 
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(
                        text = if (isHindi) "रद्द करें" else "Cancel", 
                        color = AppColors.EmeraldGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Language Selector Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { 
                Text(
                    text = if (isHindi) "भाषा चुनें" else "Choose Language", 
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ) 
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val languages = listOf(
                        "en" to "English",
                        "hi" to "हिंदी (Hindi)"
                    )
                    languages.forEach { (langKey, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    prefs.edit().putString("language", langKey).apply()
                                    onLanguageChange(langKey)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLanguage == langKey,
                                onClick = {
                                    prefs.edit().putString("language", langKey).apply()
                                    onLanguageChange(langKey)
                                    showLanguageDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = AppColors.EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = label, 
                                fontSize = 15.sp, 
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(
                        text = if (isHindi) "रद्द करें" else "Cancel", 
                        color = AppColors.EmeraldGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showManageTrustedDevices) {
        com.vidyasetuai.feature_auth.presentation.screen.ManageTrustedDevicesScreen(
            sessionManager = sessionManager,
            trustedDeviceRepository = trustedDeviceRepository,
            onNavigateBack = { showManageTrustedDevices = false }
        )
    }

    if (showLogoutConfirmationDialog) {
        com.vidyasetuai.core.ui.components.LogoutConfirmationDialog(
            onLogoutKeepTrusted = {
                showLogoutConfirmationDialog = false
                com.vidyasetuai.core.auth.AuthManager.logoutAndClearData(context, sessionManager)
            },
            onLogoutRemoveTrusted = {
                showLogoutConfirmationDialog = false
                scope.launch {
                    val userId = sessionManager.getUserId() ?: ""
                    val email = sessionManager.getUserEmail() ?: ""
                    if (userId.isNotEmpty()) {
                        trustedDeviceRepository.revokeDeviceTrust(userId, email)
                    }
                    com.vidyasetuai.core.auth.AuthManager.logoutAndClearData(context, sessionManager)
                }
            },
            onDismiss = { showLogoutConfirmationDialog = false }
        )
    }
}

@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }
    }
}
