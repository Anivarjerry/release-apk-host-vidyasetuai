package com.vidyasetuai.core.ui.components

import android.content.Context
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
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Globe
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Sun
import com.composables.icons.lucide.LogOut
import com.vidyasetuai.core.ui.colors.AppColors

@Composable
fun SettingsScreen(
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sessionManager = remember { com.vidyasetuai.core.auth.SessionManager(context) }
    val prefs = remember { context.getSharedPreferences("app_settings", Context.MODE_PRIVATE) }
    val isHindi = currentLanguage == "hi"

    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

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

    if (showAboutScreen) {
        AboutScreen(onBack = { showAboutScreen = false })
    } else if (showTermsScreen) {
        TermsScreen(onBack = { showTermsScreen = false })
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .padding(vertical = 8.dp)
                )

                // Section 2: Info & Terms Header
                Text(
                    text = if (isHindi) "अन्य जानकारी" else "More Info",
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
                    icon = Lucide.Info,
                    title = if (isHindi) "हमारे बारे में" else "About Us",
                    subtitle = "v2.0.0",
                    onClick = { showAboutScreen = true }
                )

                SettingsRow(
                    icon = Lucide.LogOut,
                    title = if (isHindi) "लॉग आउट" else "Log Out",
                    subtitle = if (isHindi) "खाता लॉग आउट करें और सभी डेटा साफ़ करें" else "Log out and clear all data",
                    onClick = {
                        com.vidyasetuai.core.auth.AuthManager.logoutAndClearData(context, sessionManager)
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
