package com.vidyasetuai.feature_profile.presentation.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_profile.domain.model.UserProfile
import com.vidyasetuai.feature_profile.presentation.component.CropShape
import com.vidyasetuai.feature_profile.presentation.component.EditProfileBottomSheet
import com.vidyasetuai.feature_profile.presentation.component.ImageCropDialog
import com.vidyasetuai.feature_profile.presentation.component.ProfileImageLightboxDialog
import com.vidyasetuai.feature_profile.presentation.viewmodel.ProfileEvent
import com.vidyasetuai.feature_profile.presentation.viewmodel.ProfileViewModel
import java.io.File
import java.io.FileOutputStream

/**
 * Flagship Apple Minimalist Flat HIG Profile Screen:
 * - Immersive Edge-to-Edge Cover Banner with Status Bar Scrim Protection
 * - Overlapping Circular DP (92dp) with 3dp Crisp Ring & Direct Upload Badge
 * - Interactive Luxury Stats Cards (Inspirations / Inspired)
 * - Apple Fluid Segmented Slider (Single Physical Sliding Thumb - Zero Glitch)
 * - Actionable Frosted Empty State Cards with Call-to-Actions
 * - Collapsing Floating Glass Top Bar on Scroll (Clean Fade-In only when Header is out of view)
 * - 0ms Room DB Reactive Reads & Resilient Remote Invocation
 * - Pillar 5 Layered Native BackHandler
 */
@Composable
fun ProfileScreen(
    userId: String = "",
    currentLanguage: String = "en",
    onCaseStudyClick: (String) -> Unit = {},
    onInspirationsClick: (String, Int) -> Unit = { _, _ -> },
    onSettingsClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    var cropUri by remember { mutableStateOf<Uri?>(null) }
    var cropShape by remember { mutableStateOf(CropShape.CIRCLE_AVATAR) }

    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            cropUri = uri
            cropShape = CropShape.CIRCLE_AVATAR
        }
    }

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            cropUri = uri
            cropShape = CropShape.RECT_COVER
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(ProfileEvent.ClearMessages)
        }
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(ProfileEvent.ClearMessages)
        }
    }

    BackHandler(enabled = true) {
        if (cropUri != null) {
            cropUri = null
        } else if (state.isImageLightboxOpen) {
            viewModel.onEvent(ProfileEvent.ToggleImageLightbox(false))
        } else if (state.isEditSheetOpen) {
            viewModel.onEvent(ProfileEvent.ToggleEditSheet(false))
        } else {
            onNavigateBack()
        }
    }

    // Scroll threshold fixed to 900px so top bar only appears when large avatar & name have fully scrolled out of view
    val isScrolledPastCover by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 900
        }
    }

    var isSheetBlurActive by remember { mutableStateOf(false) }
    LaunchedEffect(state.isEditSheetOpen) {
        if (state.isEditSheetOpen) {
            isSheetBlurActive = true
        }
    }

    val animatedBlurRadius by animateDpAsState(
        targetValue = if (isSheetBlurActive && state.isEditSheetOpen) 16.dp else 0.dp,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "profile_bg_blur"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF0B1120) else Color.White)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .blur(animatedBlurRadius),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item(key = "profile_header") {
                val profile = state.profile
                val avatarSource = profile?.profilePictureLocalPath?.takeIf { File(it).exists() } ?: profile?.profilePictureUrl
                val coverSource = profile?.coverPhotoLocalPath?.takeIf { File(it).exists() } ?: profile?.coverPhotoUrl

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Immersive Edge-to-Edge Cover Banner with Top Protection Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                                )
                            )
                    ) {
                        if (!coverSource.isNullOrBlank()) {
                            AsyncImage(
                                model = coverSource,
                                contentDescription = "Cover Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Top Gradient Protection Scrim (Status Bar Safety)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(84.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.55f),
                                            Color.Black.copy(alpha = 0.20f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Top Action Controls (Cover & Settings)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Cover Picker Glass Pill
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { coverPickerLauncher.launch("image/*") },
                                shape = RoundedCornerShape(20.dp),
                                color = Color.Black.copy(alpha = 0.45f),
                                border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.25f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Lucide.Camera,
                                        contentDescription = "Change Cover",
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Cover",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.5.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            // Settings Glass Button
                            IconButton(
                                onClick = onSettingsClick,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                            ) {
                                Icon(
                                    imageVector = Lucide.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // 2. Overlapping Avatar
                    Box(
                        modifier = Modifier
                            .offset(y = (-46).dp)
                            .size(92.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    if (!avatarSource.isNullOrBlank()) {
                                        viewModel.onEvent(ProfileEvent.ToggleImageLightbox(true, avatarSource))
                                    }
                                },
                            shape = CircleShape,
                            color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                            border = BorderStroke(3.dp, if (isDark) Color(0xFF0B1120) else Color.White),
                            shadowElevation = 6.dp
                        ) {
                            if (!avatarSource.isNullOrBlank()) {
                                AsyncImage(
                                    model = avatarSource,
                                    contentDescription = profile?.fullName ?: "Avatar",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                val initials = (profile?.fullName ?: "User")
                                    .split(" ")
                                    .filter { it.isNotEmpty() }
                                    .take(2)
                                    .map { it.first().uppercase() }
                                    .joinToString("")
                                    .ifEmpty { "U" }

                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp,
                                            color = Color(0xFF10B981)
                                        )
                                    )
                                }
                            }
                        }

                        // Avatar Camera Badge
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .clip(CircleShape)
                                .clickable { avatarPickerLauncher.launch("image/*") },
                            shape = CircleShape,
                            color = Color(0xFF10B981),
                            border = BorderStroke(2.dp, if (isDark) Color(0xFF0B1120) else Color.White),
                            shadowElevation = 3.dp
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Camera,
                                    contentDescription = "Change DP",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    // 3. User Identity & Details
                    Column(
                        modifier = Modifier
                            .offset(y = (-36).dp)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = profile?.fullName?.ifBlank { "Member" } ?: "Member",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 21.sp,
                                    color = if (isDark) Color.White else Color(0xFF0F172A)
                                )
                            )

                            if (profile?.isVerified == true) {
                                Icon(
                                    imageVector = Lucide.ShieldCheck,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "@${profile?.username ?: "user"}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        )

                        if (!profile?.bio.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = profile?.bio ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.5.sp,
                                    lineHeight = 19.sp,
                                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Action Buttons ([Edit Profile], [Share])
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .clickable { viewModel.onEvent(ProfileEvent.ToggleEditSheet(true)) },
                                shape = RoundedCornerShape(22.dp),
                                color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.16f) else Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.Pencil,
                                        contentDescription = null,
                                        tint = if (isDark) Color.White else Color(0xFF0F172A),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Edit Profile",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isDark) Color.White else Color(0xFF0F172A)
                                        )
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .clickable {
                                        val shareText = "Connect with ${profile?.fullName ?: "me"} on VidyaSetu AI: @${profile?.username}"
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Profile"))
                                    },
                                shape = RoundedCornerShape(22.dp),
                                color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.16f) else Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.Share2,
                                        contentDescription = null,
                                        tint = if (isDark) Color.White else Color(0xFF0F172A),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Share",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isDark) Color.White else Color(0xFF0F172A)
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactive Stats Cards (Inspirations | Inspired)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable {
                                        onInspirationsClick(profile?.userId ?: "", 0)
                                    },
                                shape = RoundedCornerShape(18.dp),
                                color = if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0))
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${profile?.totalInspiringCount ?: 0}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = if (isDark) Color.White else Color(0xFF0F172A)
                                        )
                                    )
                                    Text(
                                        text = "Inspirations",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable {
                                        onInspirationsClick(profile?.userId ?: "", 1)
                                    },
                                shape = RoundedCornerShape(18.dp),
                                color = if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0))
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${profile?.totalInspiredCount ?: 0}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = if (isDark) Color.White else Color(0xFF0F172A)
                                        )
                                    )
                                    Text(
                                        text = "Inspired",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Apple Fluid Segmented Slider (Single Physical Sliding Thumb - Zero Glitch)
            item(key = "profile_tabs") {
                val profile = state.profile
                val tabs = listOf(
                    "Case Studies (${profile?.totalCaseStudiesCount ?: 0})",
                    "Experiences"
                )
                val selectedIndex = state.selectedTab.coerceIn(0, 1)
                val indicatorBias by animateFloatAsState(
                    targetValue = if (selectedIndex == 0) -1f else 1f,
                    animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
                    label = "tab_indicator_bias"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF1F5F9))
                        .border(1.dp, if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .padding(4.dp)
                ) {
                    // 1. Single physical sliding thumb card (Fractional Bias Alignment - Zero Glitch & No Sub-composition)
                    Box(
                        modifier = Modifier
                            .align(BiasAlignment(indicatorBias, 0f))
                            .fillMaxWidth(0.5f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0xFF1E293B) else Color.White)
                            .then(
                                if (!isDark) {
                                    Modifier.border(0.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                                } else Modifier
                            )
                    )

                    // 2. Clickable text row layered on top
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        tabs.forEachIndexed { index, title ->
                            val isSelected = selectedIndex == index
                            val animatedTextColor by animateColorAsState(
                                targetValue = if (isSelected) {
                                    if (isDark) Color.White else Color(0xFF0F172A)
                                } else {
                                    if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                },
                                animationSpec = tween(durationMillis = 200),
                                label = "tab_text_color"
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.onEvent(ProfileEvent.SelectTab(index)) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.5.sp,
                                        color = animatedTextColor,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 5. Actionable Frosted Empty State Card
            item(key = "tab_content") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) Color.White.copy(alpha = 0.04f) else Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (state.selectedTab == 0) Lucide.BookOpen else Lucide.Sparkles,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Text(
                            text = if (state.selectedTab == 0) "No Case Studies Published Yet" else "No Shared Experiences Yet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                        )

                        Text(
                            text = if (state.selectedTab == 0)
                                "Document institutional case studies and share key learnings with other educators."
                            else
                                "Showcase your educational achievements, roles, and career milestones.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 12.5.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            // 6. Big Scroll Testing Box (For Testing Top Bar Scroll Animation)
            item(key = "scroll_test_box") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .height(800.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = if (isDark) Color.White.copy(alpha = 0.04f) else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📜 SCROLL TESTING AREA",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                fontSize = 16.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Scroll down freely to test the Top Bar gliding in smoothly!",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 13.5.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }

        // 6. Floating Glass Top Bar (Smooth Glide-In only when Header is out of view)
        AnimatedVisibility(
            visible = isScrolledPastCover,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(200)),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(180)),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            val profile = state.profile
            val avatarSource = profile?.profilePictureLocalPath?.takeIf { File(it).exists() } ?: profile?.profilePictureUrl

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                shape = RoundedCornerShape(32.dp),
                color = if (isDark) Color(0xFF0F172A).copy(alpha = 0.92f) else Color.White.copy(alpha = 0.92f),
                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.16f) else Color(0xFFE2E8F0)),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!avatarSource.isNullOrBlank()) {
                                AsyncImage(
                                    model = avatarSource,
                                    contentDescription = profile?.fullName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = (profile?.fullName ?: "U").take(1).uppercase(),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                )
                            }
                        }

                        Text(
                            text = profile?.fullName ?: "Member",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                            )
                        )
                    }

                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Settings,
                            contentDescription = "Settings",
                            tint = if (isDark) Color.White else Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 7. Sub-Modals & Dialogs
        if (state.isImageLightboxOpen) {
            ProfileImageLightboxDialog(
                imageUrl = state.lightboxImageUrl,
                onDismiss = { viewModel.onEvent(ProfileEvent.ToggleImageLightbox(false)) }
            )
        }

        if (state.isEditSheetOpen) {
            EditProfileBottomSheet(
                profile = state.profile,
                isLoading = state.isLoading,
                onDismiss = {
                    isSheetBlurActive = false
                    viewModel.onEvent(ProfileEvent.ToggleEditSheet(false))
                },
                onStartDismiss = {
                    isSheetBlurActive = false
                },
                onCheckUsername = { viewModel.checkUsernameAvailability(it) },
                onSave = { uname, first, last, bio, gender, dob, lang, isPriv ->
                    viewModel.onEvent(
                        ProfileEvent.UpdateProfileInfo(
                            username = uname,
                            firstName = first,
                            lastName = last,
                            bio = bio,
                            gender = gender,
                            dateOfBirth = dob,
                            preferredLanguage = lang,
                            isPrivate = isPriv
                        )
                    )
                }
            )
        }

        if (cropUri != null) {
            ImageCropDialog(
                imageUri = cropUri!!,
                cropShape = cropShape,
                onDismiss = { cropUri = null },
                onCropConfirmed = { croppedFile ->
                    cropUri = null
                    if (cropShape == CropShape.CIRCLE_AVATAR) {
                        viewModel.onEvent(ProfileEvent.UploadAvatar(croppedFile))
                    } else {
                        viewModel.onEvent(ProfileEvent.UploadCover(croppedFile))
                    }
                }
            )
        }
    }
}

private fun copyUriToTempFile(context: Context, uri: Uri, fileName: String): File? {
    return try {
        val tempFile = File(context.cacheDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        tempFile
    } catch (e: Exception) {
        null
    }
}
