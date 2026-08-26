package com.vidyasetuai.feature_profile.presentation.component

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.components.VidyaSetuWheelDatePicker
import com.vidyasetuai.feature_profile.domain.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Flagship Apple Adaptive (Light & Dark Theme Parity) Floating Island Bottom Sheet:
 * - Soft Monochromatic Quiet Luxury Design
 * - Smart Has-Changes Detection for Save Button (Soft Gray Disabled -> Elegant Slate Charcoal Active)
 * - Soft Gray Tinted Segmented Chips for Gender & Language (No harsh jet-black patches)
 * - 120 FPS Synchronized Slide & Scale (0.96x -> 1.00x) Opening & Dismissal
 * - Reusable Universal VidyaSetuWheelDatePicker Integration
 * - Live Debounced @username Availability Checker
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileBottomSheet(
    profile: UserProfile?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onStartDismiss: (() -> Unit)? = null,
    onCheckUsername: (suspend (String) -> Boolean?)? = null,
    onSave: (
        username: String?,
        firstName: String?,
        lastName: String?,
        bio: String?,
        gender: String?,
        dateOfBirth: String?,
        preferredLanguage: String?,
        isPrivate: Boolean?
    ) -> Unit
) {
    val isDark = isSystemInDarkTheme()

    var username by remember(profile) { mutableStateOf(profile?.username ?: "") }
    var firstName by remember(profile) { mutableStateOf(profile?.firstName ?: "") }
    var lastName by remember(profile) { mutableStateOf(profile?.lastName ?: "") }
    var bio by remember(profile) { mutableStateOf(profile?.bio ?: "") }
    var gender by remember(profile) { mutableStateOf(profile?.gender?.lowercase() ?: "male") }
    var dateOfBirth by remember(profile) { mutableStateOf(profile?.dateOfBirth ?: "") }
    var preferredLanguage by remember(profile) { mutableStateOf(profile?.preferredLanguage?.lowercase() ?: "en") }
    var isPrivate by remember(profile) { mutableStateOf(profile?.isPrivate ?: false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Smart Has-Changes State: Only true when the user actually modified any profile field
    val hasChanges by remember(
        username, firstName, lastName, bio, gender, dateOfBirth, preferredLanguage, isPrivate, profile
    ) {
        derivedStateOf {
            val origUsername = profile?.username ?: ""
            val origFirst = profile?.firstName ?: ""
            val origLast = profile?.lastName ?: ""
            val origBio = profile?.bio ?: ""
            val origGender = profile?.gender?.lowercase() ?: "male"
            val origDob = profile?.dateOfBirth ?: ""
            val origLang = profile?.preferredLanguage?.lowercase() ?: "en"
            val origPrivate = profile?.isPrivate ?: false

            val isUsernameChanged = !username.trim().equals(origUsername.trim(), ignoreCase = true)
            val isFirstChanged = firstName.trim() != origFirst.trim()
            val isLastChanged = lastName.trim() != origLast.trim()
            val isBioChanged = bio.trim() != origBio.trim()
            val isGenderChanged = gender.lowercase() != origGender
            val isDobChanged = dateOfBirth.trim() != origDob.trim()
            val isLangChanged = preferredLanguage.lowercase() != origLang
            val isPrivateChanged = isPrivate != origPrivate

            isUsernameChanged || isFirstChanged || isLastChanged || isBioChanged ||
                    isGenderChanged || isDobChanged || isLangChanged || isPrivateChanged
        }
    }

    // Live Debounced Username State
    var isCheckingUsername by remember { mutableStateOf(false) }
    var isUsernameAvailable by remember { mutableStateOf<Boolean?>(null) }
    var usernameValidationMessage by remember { mutableStateOf<String?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // Flag for synchronized 120 FPS smooth closing
    var isDismissing by remember { mutableStateOf(false) }
    val performSmoothDismiss: () -> Unit = {
        if (!isDismissing) {
            isDismissing = true
            onStartDismiss?.invoke() // Trigger background un-blur and icon restoration in parallel
            coroutineScope.launch {
                try {
                    sheetState.hide()
                } catch (_: Exception) {
                } finally {
                    onDismiss()
                }
            }
        }
    }

    // 🛡️ 1:1 Parallel Backdrop Scrim Tap & Drag Synchronization (Zero Blur Lag):
    var hasBeenExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == SheetValue.Expanded) {
            hasBeenExpanded = true
        }
    }

    LaunchedEffect(sheetState.targetValue, hasBeenExpanded) {
        if (hasBeenExpanded && sheetState.targetValue == SheetValue.Hidden) {
            onStartDismiss?.invoke()
        }
    }

    // Apple Micro-Scale Soft Landing Transition (0.96x -> 1.00x)
    var isContentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isContentVisible = true
    }

    val contentScale by animateFloatAsState(
        targetValue = if (isContentVisible && !isDismissing) 1f else 0.96f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "sheet_scale"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (isContentVisible && !isDismissing) 1f else 0.85f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "sheet_alpha"
    )

    // Smoothly turn Status Bar & Navigation Bar icons to WHITE when sheet is open (dark scrim active), and restore on close
    val view = LocalView.current
    DisposableEffect(view, isDark) {
        val window = (view.context as? Activity)?.window
        if (window != null && !view.isInEditMode) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            val originalLightStatus = insetsController.isAppearanceLightStatusBars
            val originalLightNav = insetsController.isAppearanceLightNavigationBars

            // Dark scrim is active behind sheet -> turn system icons to White for crisp readability
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false

            onDispose {
                insetsController.isAppearanceLightStatusBars = originalLightStatus
                insetsController.isAppearanceLightNavigationBars = originalLightNav
            }
        } else {
            onDispose {}
        }
    }

    // Debounced Live Username Availability Checker (500ms debounce)
    LaunchedEffect(username) {
        val clean = username.trim().lowercase()
        if (clean.isBlank()) {
            isCheckingUsername = false
            isUsernameAvailable = null
            usernameValidationMessage = null
            return@LaunchedEffect
        }
        if (clean.length < 3) {
            isCheckingUsername = false
            isUsernameAvailable = false
            usernameValidationMessage = "Must be at least 3 characters"
            return@LaunchedEffect
        }
        if (!clean.matches(Regex("^[a-z0-9_.]+$"))) {
            isCheckingUsername = false
            isUsernameAvailable = false
            usernameValidationMessage = "Only letters, numbers, _ and . allowed"
            return@LaunchedEffect
        }
        if (clean.equals(profile?.username?.trim()?.lowercase(), ignoreCase = true)) {
            isCheckingUsername = false
            isUsernameAvailable = true
            usernameValidationMessage = null
            return@LaunchedEffect
        }

        // Debounce 500ms before calling Supabase
        isCheckingUsername = true
        isUsernameAvailable = null
        usernameValidationMessage = "Checking availability..."
        delay(500)

        try {
            val isAvail = onCheckUsername?.invoke(clean)
            isCheckingUsername = false
            if (isAvail == true) {
                isUsernameAvailable = true
                usernameValidationMessage = "Username available"
            } else if (isAvail == false) {
                isUsernameAvailable = false
                usernameValidationMessage = "Username is already taken"
            } else {
                isUsernameAvailable = null
                usernameValidationMessage = null
            }
        } catch (_: Exception) {
            isCheckingUsername = false
            isUsernameAvailable = null
            usernameValidationMessage = null
        }
    }

    // Pillar 5: Layered BackHandler (Dismiss sub-modal DatePicker first, then smoothly slide sheet down)
    BackHandler(enabled = true) {
        if (showDatePicker) {
            showDatePicker = false
        } else {
            performSmoothDismiss()
        }
    }

    // Universal App-Level Reusable Apple 3D Drum Wheel Date Picker Dialog
    if (showDatePicker) {
        VidyaSetuWheelDatePicker(
            initialDateString = dateOfBirth,
            minYear = 1920,
            maxYear = Calendar.getInstance().get(Calendar.YEAR),
            onDismiss = { showDatePicker = false },
            onDateSelected = { selectedDate ->
                dateOfBirth = selectedDate
                showDatePicker = false
            }
        )
    }

    // Adaptive Soft Colors
    val cardBgColor = if (isDark) Color(0xFF0F172A).copy(alpha = 0.90f) else Color.White
    val cardBorder = if (isDark) {
        BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.10f))
            )
        )
    } else {
        BorderStroke(1.dp, Color(0xFFE2E8F0))
    }

    val primaryTextColor = if (isDark) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val labelTextColor = if (isDark) Color(0xFFE2E8F0) else Color(0xFF475569)

    val inputBgColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF8FAFC)
    val inputBorderColor = if (isDark) Color.White.copy(alpha = 0.16f) else Color(0xFFE2E8F0)
    val inputTextColor = if (isDark) Color.White else Color(0xFF0F172A)
    val inputIconTint = if (isDark) Color(0xFFCBD5E1) else Color(0xFF94A3B8)

    val closeBtnBg = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFF1F5F9)
    val closeBtnBorder = if (isDark) Color.White.copy(alpha = 0.20f) else Color(0xFFE2E8F0)
    val closeBtnTint = if (isDark) Color.White else Color(0xFF64748B)
    val dragHandleColor = if (isDark) Color.White.copy(alpha = 0.30f) else Color(0xFFCBD5E1)

    val privateCardBg = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF8FAFC)
    val privateCardBorder = if (isDark) Color.White.copy(alpha = 0.16f) else Color(0xFFE2E8F0)
    val lockIconBg = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFF1F5F9)
    val lockIconTint = if (isDark) Color.White else Color(0xFF475569)

    // Save Button Color Palette (Soft Monochromatic: Soft Gray when disabled -> Slate Charcoal when active)
    val isSaveDisabled = !hasChanges || isLoading || isCheckingUsername || isUsernameAvailable == false

    val ctaBgColor = if (isDark) Color.White else Color(0xFF1E293B) // Soft Slate Charcoal (not harsh pitch-black)
    val ctaTextColor = if (isDark) Color(0xFF0F172A) else Color.White
    val ctaDisabledBg = if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFF1F5F9) // Subtle Soft Gray when inactive
    val ctaDisabledText = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)

    val backBtnBg = if (isDark) Color.White.copy(alpha = 0.14f) else Color.White
    val backBtnBorder = if (isDark) Color.White.copy(alpha = 0.24f) else Color(0xFFE2E8F0)
    val backBtnTint = if (isDark) Color.White else Color(0xFF0F172A)

    ModalBottomSheet(
        onDismissRequest = performSmoothDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent, // Transparent sheet canvas so inner island card floats
        scrimColor = Color.Black.copy(alpha = 0.50f),
        windowInsets = WindowInsets(0, 0, 0, 0), // Full edge-to-edge status bar & nav bar fade
        tonalElevation = 0.dp,
        dragHandle = null // Custom centered pill inside the floating island
    ) {
        // Floating Island Card (Adaptive Light/Dark Surface with Apple Micro-Scale 0.96x -> 1.00x)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 44.dp)
                .graphicsLayer {
                    scaleX = contentScale
                    scaleY = contentScale
                    alpha = contentAlpha
                },
            shape = RoundedCornerShape(28.dp),
            color = cardBgColor,
            border = cardBorder,
            shadowElevation = if (isDark) 16.dp else 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Centered Top Drag Handle Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 12.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(dragHandleColor)
                )

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = primaryTextColor
                            )
                        )
                        Text(
                            text = "Personal details & preferences",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = secondaryTextColor,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(closeBtnBg)
                            .border(0.5.dp, closeBtnBorder, CircleShape)
                            .clickable { performSmoothDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Close",
                            tint = closeBtnTint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // First Name & Last Name (Symmetrical Inputs)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "First Name",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = labelTextColor,
                                fontSize = 12.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        ProfileInputField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            placeholder = "First name",
                            icon = Lucide.User,
                            bgColor = inputBgColor,
                            borderColor = inputBorderColor,
                            textColor = inputTextColor,
                            iconTint = inputIconTint
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Last Name",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = labelTextColor,
                                fontSize = 12.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        ProfileInputField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            placeholder = "Last name",
                            icon = Lucide.User,
                            bgColor = inputBgColor,
                            borderColor = inputBorderColor,
                            textColor = inputTextColor,
                            iconTint = inputIconTint
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Username Field with @ Prefix and Live Debounced Status Indicator
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Username",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = labelTextColor,
                                fontSize = 12.5.sp
                            )
                        )

                        if (usernameValidationMessage != null) {
                            Text(
                                text = usernameValidationMessage!!,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = when (isUsernameAvailable) {
                                        true -> Color(0xFF059669)
                                        false -> Color(0xFFE11D48)
                                        null -> secondaryTextColor
                                    }
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    ProfileInputField(
                        value = username,
                        onValueChange = { input ->
                            username = input.lowercase().filter { it.isLetterOrDigit() || it == '_' || it == '.' }
                        },
                        placeholder = "username",
                        leadingText = "@",
                        bgColor = inputBgColor,
                        borderColor = inputBorderColor,
                        textColor = inputTextColor,
                        iconTint = inputIconTint,
                        trailingIcon = {
                            if (isCheckingUsername) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 1.5.dp,
                                    color = secondaryTextColor
                                )
                            } else if (isUsernameAvailable == true && !username.equals(profile?.username, ignoreCase = true)) {
                                Icon(
                                    imageVector = Lucide.CircleCheck,
                                    contentDescription = "Available",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(16.dp)
                                )
                            } else if (isUsernameAvailable == false) {
                                Icon(
                                    imageVector = Lucide.CircleAlert,
                                    contentDescription = "Unavailable",
                                    tint = Color(0xFFE11D48),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bio
                Text(
                    text = "Bio",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = labelTextColor,
                        fontSize = 12.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                ProfileInputField(
                    value = bio,
                    onValueChange = { bio = it },
                    placeholder = "Write a short bio about yourself...",
                    icon = Lucide.FileText,
                    minLines = 2,
                    maxLines = 4,
                    bgColor = inputBgColor,
                    borderColor = inputBorderColor,
                    textColor = inputTextColor,
                    iconTint = inputIconTint
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Date of Birth (Instant Click to Universal VidyaSetuWheelDatePicker)
                Text(
                    text = "Date of Birth",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = labelTextColor,
                        fontSize = 12.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                ProfileInputField(
                    value = dateOfBirth,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = "YYYY-MM-DD",
                    bgColor = inputBgColor,
                    borderColor = inputBorderColor,
                    textColor = inputTextColor,
                    iconTint = inputIconTint,
                    trailingIcon = {
                        Icon(
                            imageVector = Lucide.Calendar,
                            contentDescription = "Select Date",
                            tint = inputIconTint,
                            modifier = Modifier.size(17.dp)
                        )
                    },
                    onClick = { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section Label
                Text(
                    text = "PREFERENCES & PRIVACY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Gender Row (Soft Tinted Monochromatic Chips)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Select gender:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = primaryTextColor,
                            fontSize = 13.5.sp
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "male" to "Male",
                            "female" to "Female",
                            "other" to "Other"
                        ).forEach { (key, label) ->
                            val isSelected = gender.equals(key, ignoreCase = true)
                            // Soft gray selected background + dark text (No harsh jet-black)
                            val activeBg = if (isDark) Color.White.copy(alpha = 0.20f) else Color(0xFFE2E8F0)
                            val inactiveBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF8FAFC)
                            val activeText = if (isDark) Color.White else Color(0xFF0F172A)
                            val inactiveText = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                            val activeBorder = if (isDark) Color.White.copy(alpha = 0.35f) else Color(0xFFCBD5E1)
                            val inactiveBorder = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)

                            val animatedBg by animateColorAsState(
                                targetValue = if (isSelected) activeBg else inactiveBg,
                                label = "gender_bg"
                            )
                            val animatedTextColor by animateColorAsState(
                                targetValue = if (isSelected) activeText else inactiveText,
                                label = "gender_text"
                            )
                            val animatedBorderColor by animateColorAsState(
                                targetValue = if (isSelected) activeBorder else inactiveBorder,
                                label = "gender_border"
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(animatedBg)
                                    .border(1.dp, animatedBorderColor, RoundedCornerShape(20.dp))
                                    .clickable { gender = key }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = animatedTextColor,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Preferred Language (Soft Tinted Monochromatic Chips)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Preferred language:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = primaryTextColor,
                            fontSize = 13.5.sp
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "en" to "English",
                            "hi" to "हिंदी"
                        ).forEach { (code, label) ->
                            val isSelected = preferredLanguage.equals(code, ignoreCase = true)
                            val activeBg = if (isDark) Color.White.copy(alpha = 0.20f) else Color(0xFFE2E8F0)
                            val inactiveBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF8FAFC)
                            val activeText = if (isDark) Color.White else Color(0xFF0F172A)
                            val inactiveText = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                            val activeBorder = if (isDark) Color.White.copy(alpha = 0.35f) else Color(0xFFCBD5E1)
                            val inactiveBorder = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)

                            val animatedBg by animateColorAsState(
                                targetValue = if (isSelected) activeBg else inactiveBg,
                                label = "lang_bg"
                            )
                            val animatedTextColor by animateColorAsState(
                                targetValue = if (isSelected) activeText else inactiveText,
                                label = "lang_text"
                            )
                            val animatedBorderColor by animateColorAsState(
                                targetValue = if (isSelected) activeBorder else inactiveBorder,
                                label = "lang_border"
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(animatedBg)
                                    .border(1.dp, animatedBorderColor, RoundedCornerShape(20.dp))
                                    .clickable { preferredLanguage = code }
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = animatedTextColor,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Private Account Toggle Card (Adaptive)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(privateCardBg)
                        .border(1.dp, privateCardBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(lockIconBg)
                                .border(1.dp, privateCardBorder, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Lock,
                                contentDescription = null,
                                tint = lockIconTint,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Private Account",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = primaryTextColor,
                                    fontSize = 13.5.sp
                                )
                            )
                            Text(
                                text = "Only mutual inspirations can view your studies",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = secondaryTextColor,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Switch(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF10B981),
                            uncheckedThumbColor = if (isDark) Color(0xFF94A3B8) else Color.White,
                            uncheckedTrackColor = if (isDark) Color.White.copy(alpha = 0.20f) else Color(0xFFCBD5E1),
                            uncheckedBorderColor = Color.Transparent
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Split Action Bar (Soft Monochromatic Save Button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Action Button: [ ← ] Pill Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(backBtnBg)
                            .border(1.dp, backBtnBorder, CircleShape)
                            .clickable { performSmoothDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = backBtnTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Right Primary Button: [ SAVE CHANGES ] (Smart Active/Disabled State)
                    Button(
                        onClick = {
                            onSave(
                                username.trim().lowercase().ifBlank { null },
                                firstName.ifBlank { null },
                                lastName.ifBlank { null },
                                bio.ifBlank { null },
                                gender.ifBlank { null },
                                dateOfBirth.ifBlank { null },
                                preferredLanguage.ifBlank { null },
                                isPrivate
                            )
                        },
                        enabled = !isSaveDisabled,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ctaBgColor,
                            contentColor = ctaTextColor,
                            disabledContainerColor = ctaDisabledBg,
                            disabledContentColor = ctaDisabledText
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = if (isSaveDisabled) ctaDisabledText else ctaTextColor,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "SAVE CHANGES",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSaveDisabled) ctaDisabledText else ctaTextColor,
                                    fontSize = 13.5.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * Adaptive Input Field with 100% Reliable Pointer Click Handling.
 */
@Composable
private fun ProfileInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    leadingText: String? = null,
    bgColor: Color = Color(0xFFF8FAFC),
    borderColor: Color = Color(0xFFE2E8F0),
    textColor: Color = Color(0xFF0F172A),
    iconTint: Color = Color(0xFF94A3B8),
    trailingIcon: (@Composable () -> Unit)? = null,
    minLines: Int = 1,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val isClickableOnly = onClick != null

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = isClickableOnly) { onClick?.invoke() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isClickableOnly,
            textStyle = TextStyle(
                color = textColor,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Normal
            ),
            cursorBrush = SolidColor(textColor),
            minLines = minLines,
            maxLines = maxLines,
            readOnly = readOnly,
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = if (minLines > 1) Alignment.Top else Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier
                                .padding(top = if (minLines > 1) 2.dp else 0.dp)
                                .size(15.dp)
                        )
                    }

                    if (leadingText != null) {
                        Text(
                            text = leadingText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = iconTint,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 13.5.sp
                                )
                            )
                        }
                        innerTextField()
                    }

                    if (trailingIcon != null) {
                        trailingIcon()
                    }
                }
            }
        )
    }
}
