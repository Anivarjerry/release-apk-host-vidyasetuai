package com.vidyasetuai.feature_profile.presentation.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.BorderStroke
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import com.composables.icons.lucide.*
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.database.AppDatabase
import com.vidyasetuai.core.network.SupabaseStorageHelper
import com.vidyasetuai.feature_profile.data.local.datasource.ProfileLocalDataSource
import com.vidyasetuai.feature_profile.data.remote.datasource.ProfileRemoteDataSource
import com.vidyasetuai.feature_profile.data.repository.ProfileRepositoryImpl
import com.vidyasetuai.feature_profile.domain.usecase.ApplyForVerificationUseCase
import com.vidyasetuai.feature_profile.domain.usecase.CheckUsernameUniqueUseCase
import com.vidyasetuai.feature_profile.domain.usecase.GetUserProfileUseCase
import com.vidyasetuai.feature_profile.domain.usecase.UpdateUserProfileUseCase
import com.vidyasetuai.feature_profile.presentation.event.ProfileEvent
import com.vidyasetuai.feature_profile.presentation.viewmodel.ProfileViewModel
import com.vidyasetuai.feature_profile.presentation.state.ProfileUiState
import com.vidyasetuai.feature_case_study.data.repository.CaseStudyRepositoryImpl
import com.vidyasetuai.feature_case_study.domain.model.CaseStudy
import com.vidyasetuai.feature_feed.data.repository.ExperienceRepository
import com.vidyasetuai.feature_feed.domain.model.Experience
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun cropBitmapWithTransform(
    context: android.content.Context,
    uri: Uri,
    isProfile: Boolean,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    containerWidthPx: Int,
    containerHeightPx: Int
): ByteArray? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
        
        val bmpWidth = originalBitmap.width
        val bmpHeight = originalBitmap.height
        
        val targetWidth = if (isProfile) 400 else 960
        val targetHeight = if (isProfile) 400 else 540
        
        val croppedBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(croppedBitmap)
        
        canvas.drawColor(android.graphics.Color.WHITE)
        
        val matrix = android.graphics.Matrix()
        
        val fitScale = Math.max(targetWidth.toFloat() / bmpWidth, targetHeight.toFloat() / bmpHeight)
        
        val dxBase = (targetWidth - bmpWidth * fitScale) / 2f
        val dyBase = (targetHeight - bmpHeight * fitScale) / 2f
        
        matrix.postScale(fitScale, fitScale)
        matrix.postTranslate(dxBase, dyBase)
        
        matrix.postScale(scale, scale, targetWidth / 2f, targetHeight / 2f)
        
        val density = context.resources.displayMetrics.density
        val displayWidth = context.resources.displayMetrics.widthPixels
        val approxContainerWidth = displayWidth - (32 * density)
        
        val scaleOffsetFactor = targetWidth.toFloat() / approxContainerWidth
        val finalOffsetX = offsetX * scaleOffsetFactor
        val finalOffsetY = offsetY * scaleOffsetFactor
        
        matrix.postTranslate(finalOffsetX, finalOffsetY)
        
        val paint = android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(originalBitmap, matrix, paint)
        
        val outputStream = ByteArrayOutputStream()
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val resultBytes = outputStream.toByteArray()
        
        originalBitmap.recycle()
        croppedBitmap.recycle()
        
        resultBytes
    } catch (e: Exception) {
        android.util.Log.e("ProfileScreen", "Error cropping bitmap on canvas", e)
        null
    }
}

@Composable
fun ProfileMediaHeader(
    coverPhotoUrl: String,
    profilePicUrl: String,
    firstName: String,
    isEditMode: Boolean,
    isUploadingProfile: Boolean,
    isUploadingCover: Boolean,
    onProfileClick: () -> Unit,
    onCoverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val avatarBg = if (isDark) Color(0xFF25352E) else Color(0xFFE8F8F5)
    val borderCol = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
    ) {
        // 1. Cover Photo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(
                    if (isDark) Color(0xFF1F2E28) else Color(0xFFF0F4F2)
                )
        ) {
            if (coverPhotoUrl.isNotEmpty()) {
                AsyncImage(
                    model = coverPhotoUrl,
                    contentDescription = "Cover Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    AppColors.EmeraldGreen.copy(alpha = 0.8f),
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        )
                )
            }

            // Cover Photo Upload Button (Edit Mode Only)
            if (isEditMode) {
                IconButton(
                    onClick = onCoverClick,
                    enabled = !isUploadingCover,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    if (isUploadingCover) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Lucide.Camera,
                            contentDescription = "Edit Cover",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 2. Profile Avatar (circular) overlapping cover photo
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(110.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(avatarBg, CircleShape)
                    .border(3.dp, borderCol, CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (profilePicUrl.isNotEmpty()) {
                    AsyncImage(
                        model = profilePicUrl,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    val initialLetter = if (firstName.isNotEmpty()) firstName.take(1).uppercase() else ""
                    if (initialLetter.isNotEmpty()) {
                        Text(
                            text = initialLetter,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.EmeraldGreen
                        )
                    } else {
                        Icon(
                            imageVector = Lucide.User,
                            contentDescription = "Profile",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }
            }

            // Profile Photo Upload Button (Edit Mode Only)
            if (isEditMode) {
                IconButton(
                    onClick = onProfileClick,
                    enabled = !isUploadingProfile,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(AppColors.EmeraldGreen, CircleShape)
                        .border(2.dp, borderCol, CircleShape)
                ) {
                    if (isUploadingProfile) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 1.5.dp
                        )
                    } else {
                        Icon(
                            imageVector = Lucide.Camera,
                            contentDescription = "Edit Profile Pic",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    userId: String,
    currentLanguage: String,
    viewModel: ProfileViewModel,
    onCaseStudyClick: (String) -> Unit = {},
    onInspirationsClick: (String, Int) -> Unit = { _, _ -> },
    onEditModeChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isHindi = currentLanguage == "hi"
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.onEvent(ProfileEvent.LoadProfile(userId))
        }
    }

    val state by viewModel.uiState.collectAsState()

    var isEditMode by remember { mutableStateOf(false) }
    val setEditMode = { value: Boolean ->
        isEditMode = value
        onEditModeChange(value)
    }

    androidx.activity.compose.BackHandler(enabled = isEditMode) {
        setEditMode(false)
    }

    val profileDb = remember { AppDatabase.getDatabase(context) }
    val caseStudyRepository = remember {
        val remoteDS = com.vidyasetuai.feature_case_study.data.remote.datasource.CaseStudyRemoteDataSource()
        CaseStudyRepositoryImpl(remoteDS)
    }
    val experienceRepository = remember { ExperienceRepository() }

    LaunchedEffect(userId, state.profile?.isVerified, isEditMode) {
        if (userId.isNotEmpty() && !isEditMode) {
            if (state.userCaseStudies.isEmpty() && state.userExperiences.isEmpty()) {
                viewModel.onEvent(ProfileEvent.SetUserUploadedLoading(true))
            }
            launch {
                caseStudyRepository.getUserUploadedCaseStudies(userId).onSuccess { list ->
                    viewModel.onEvent(ProfileEvent.UpdateUserCaseStudies(list))
                }
            }
            launch {
                experienceRepository.getExperiencesByUser(userId, userId).onSuccess { list ->
                    viewModel.onEvent(ProfileEvent.UpdateUserExperiences(list))
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (!isEditMode) {
            ProfileOverviewMode(
                userId = userId,
                state = state,
                coverPhotoUrl = state.profile?.coverPhotoUrl ?: "",
                profilePicUrl = state.profile?.profilePictureUrl ?: "",
                firstName = state.profile?.firstName ?: "",
                username = state.profile?.username ?: "",
                bio = state.profile?.bio ?: "",
                userCaseStudies = state.userCaseStudies,
                userExperiences = state.userExperiences,
                inspirersCount = state.profile?.totalInspiringCount ?: 0,
                inspiringCount = state.profile?.totalInspiredCount ?: 0,
                isHindi = isHindi,
                onInspirationsClick = onInspirationsClick,
                onCaseStudyClick = onCaseStudyClick,
                onEditModeChange = { setEditMode(it) },
                onExperienceReactionClick = { exp ->
                    scope.launch {
                        experienceRepository.toggleInspiration(exp.id, userId)
                        experienceRepository.getExperiencesByUser(userId, userId).onSuccess { list ->
                            viewModel.onEvent(ProfileEvent.UpdateUserExperiences(list))
                        }
                    }
                },
                onCaseStudyReactionClick = { cs ->
                    scope.launch {
                        caseStudyRepository.toggleReaction(cs.id, userId)
                        caseStudyRepository.getUserUploadedCaseStudies(userId).onSuccess { list ->
                            viewModel.onEvent(ProfileEvent.UpdateUserCaseStudies(list))
                        }
                    }
                },
                onCaseStudyBookmarkClick = { cs ->
                    scope.launch {
                        caseStudyRepository.toggleBookmark(cs.id, userId)
                        caseStudyRepository.getUserUploadedCaseStudies(userId).onSuccess { list ->
                            viewModel.onEvent(ProfileEvent.UpdateUserCaseStudies(list))
                        }
                    }
                },
                currentLanguage = currentLanguage,
                isUserUploadedLoading = state.isUserUploadedLoading
            )
        } else {
            ProfileEditMode(
                userId = userId,
                state = state,
                isHindi = isHindi,
                isDark = isDark,
                onBackClick = { setEditMode(false) },
                onCheckUsername = { username ->
                    viewModel.onEvent(ProfileEvent.CheckUsername(username, userId))
                },
                onResetUsernameCheck = {
                    viewModel.onEvent(ProfileEvent.ResetUsernameCheck)
                },
                onSaveClick = { firstName, lastName, bio, username, gender, dob, profilePicUrl, coverPhotoUrl, preferredLang ->
                    viewModel.onEvent(
                        ProfileEvent.UpdateProfile(
                            firstName = firstName,
                            lastName = lastName,
                            bio = bio,
                            username = username,
                            gender = gender,
                            dateOfBirth = dob,
                            profilePictureUrl = profilePicUrl,
                            coverPhotoUrl = coverPhotoUrl,
                            preferredLanguage = preferredLang
                        )
                    )
                    setEditMode(false)
                },
                onApplyVerificationClick = { note ->
                    viewModel.onEvent(ProfileEvent.ApplyVerification(note))
                }
            )
        }
    }

    // Success dialogs or snackbars
    if (state.updateSuccess) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ProfileEvent.DismissSuccess) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(ProfileEvent.DismissSuccess) }) {
                    Text("OK", color = AppColors.EmeraldGreen)
                }
            },
            title = {
                Text(
                    text = if (isHindi) "प्रोफ़ाइल अपडेट की गई" else "Profile Updated",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = if (isHindi) "आपके अकादमिक प्रोफ़ाइल विवरण सफलतापूर्वक अपडेट कर दिए गए हैं।" else "Your academic profile details have been successfully updated.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    if (state.applySuccess) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ProfileEvent.DismissSuccess) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(ProfileEvent.DismissSuccess) }) {
                    Text("OK", color = AppColors.EmeraldGreen)
                }
            },
            title = {
                Text(
                    text = if (isHindi) "आवेदन जमा किया गया" else "Application Submitted",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = if (isHindi) "आपका सत्यापन अनुरोध सफलतापूर्वक सबमिट कर दिया गया है और समीक्षा के अधीन है।" else "Your verification request has been successfully submitted and is under review.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun ProfileOverviewMode(
    userId: String,
    state: ProfileUiState,
    coverPhotoUrl: String,
    profilePicUrl: String,
    firstName: String,
    username: String,
    bio: String,
    userCaseStudies: List<CaseStudy>,
    userExperiences: List<Experience>,
    inspirersCount: Int,
    inspiringCount: Int,
    isHindi: Boolean,
    onInspirationsClick: (String, Int) -> Unit,
    onCaseStudyClick: (String) -> Unit,
    onEditModeChange: (Boolean) -> Unit,
    onExperienceReactionClick: (Experience) -> Unit,
    onCaseStudyReactionClick: (CaseStudy) -> Unit,
    onCaseStudyBookmarkClick: (CaseStudy) -> Unit,
    currentLanguage: String,
    isUserUploadedLoading: Boolean
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileMediaHeader(
            coverPhotoUrl = coverPhotoUrl,
            profilePicUrl = profilePicUrl,
            firstName = firstName,
            isEditMode = false,
            isUploadingProfile = false,
            isUploadingCover = false,
            onProfileClick = {},
            onCoverClick = {}
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = state.profile?.fullName ?: (if (isHindi) "अकादमिक विद्वान" else "Academic Scholar"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (username.isNotEmpty()) "@$username" else (if (isHindi) "कोई यूज़रनेम सेट नहीं है" else "No username set"),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (bio.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = bio,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 24.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                count = userCaseStudies.size,
                label = if (isHindi) "केस स्टडीज" else "Case Studies"
            )
            StatItem(
                count = userExperiences.size,
                label = if (isHindi) "अनुभव" else "Experiences"
            )
            StatItem(
                count = inspirersCount,
                label = if (isHindi) "प्रेरक (Inspirers)" else "Inspirers",
                onClick = { onInspirationsClick(userId, 0) }
            )
            StatItem(
                count = inspiringCount,
                label = if (isHindi) "प्रेरित (Inspiring)" else "Inspiring",
                onClick = { onInspirationsClick(userId, 1) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Edit Profile Button
        Button(
            onClick = { onEditModeChange(true) },
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(44.dp)
        ) {
            Text(text = if (isHindi) "प्रोफ़ाइल संपादित करें" else "Edit Profile", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tabs for Experiences / Case Studies
        var activeOverviewTab by remember { mutableStateOf("experiences") } // "experiences" or "case_studies"

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val isExpActive = activeOverviewTab == "experiences"
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { activeOverviewTab = "experiences" }
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isHindi) "अनुभव (${userExperiences.size})" else "Experiences (${userExperiences.size})",
                    fontWeight = if (isExpActive) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp,
                    color = if (isExpActive) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isExpActive) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.width(60.dp).height(2.dp).background(AppColors.EmeraldGreen))
                }
            }

            val isCsActive = activeOverviewTab == "case_studies"
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { activeOverviewTab = "case_studies" }
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isHindi) "केस स्टडीज (${userCaseStudies.size})" else "Case Studies (${userCaseStudies.size})",
                    fontWeight = if (isCsActive) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp,
                    color = if (isCsActive) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isCsActive) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.width(60.dp).height(2.dp).background(AppColors.EmeraldGreen))
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        // Tab Contents
        Spacer(modifier = Modifier.height(16.dp))

        val isUserVerified = state.profile?.isVerified == true
        if (!isUserVerified) {
            // WARNING MESSAGE FOR UNVERIFIED USERS IN BOTH TABS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Lucide.CircleAlert,
                    contentDescription = "Not Verified",
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isHindi) {
                        "अनुभव साझा करने या केस स्टडीज अपलोड करने के लिए पहले प्रोफाइल पूरी 100% भरें और वेरिफिकेशन के लिए भेजें"
                    } else {
                        "To share experiences or upload case studies, please complete your profile 100% and submit for verification."
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onEditModeChange(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isHindi) "प्रोफ़ाइल पूर्ण करें (100%)" else "Complete Profile (100%)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Show actual lists for verified contributors
            if (activeOverviewTab == "experiences") {
                if (userExperiences.isEmpty()) {
                    Text(
                        text = if (isHindi) "कोई अनुभव अपलोड नहीं किया गया है।" else "No experiences uploaded yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(32.dp)
                    )
                } else {
                    userExperiences.forEach { exp ->
                        com.vidyasetuai.feature_feed.presentation.component.ExperienceCard(
                            experience = exp,
                            onReactionClick = { onExperienceReactionClick(exp) },
                            currentLanguage = currentLanguage,
                            onAuthorClick = {},
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            } else {
                if (userCaseStudies.isEmpty()) {
                    Text(
                        text = if (isHindi) "कोई केस स्टडी अपलोड नहीं की गई है।" else "No case studies uploaded yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(32.dp)
                    )
                } else {
                    userCaseStudies.forEach { cs ->
                        com.vidyasetuai.feature_case_study.presentation.component.CaseStudyCard(
                            caseStudy = cs,
                            onClick = { onCaseStudyClick(cs.id) },
                            onReactionClick = { onCaseStudyReactionClick(cs) },
                            onBookmarkClick = { onCaseStudyBookmarkClick(cs) },
                            currentLanguage = currentLanguage,
                            onExploreClick = { onCaseStudyClick(cs.id) },
                            onAuthorClick = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileEditMode(
    userId: String,
    state: ProfileUiState,
    isHindi: Boolean,
    isDark: Boolean,
    onBackClick: () -> Unit,
    onCheckUsername: (String) -> Unit,
    onResetUsernameCheck: () -> Unit,
    onSaveClick: (
        firstName: String,
        lastName: String,
        bio: String,
        username: String,
        gender: String?,
        dateOfBirth: String?,
        profilePictureUrl: String?,
        coverPhotoUrl: String?,
        preferredLanguage: String?
    ) -> Unit,
    onApplyVerificationClick: (note: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var firstName by remember(state.profile) { mutableStateOf(state.profile?.firstName ?: "") }
    var lastName by remember(state.profile) { mutableStateOf(state.profile?.lastName ?: "") }
    var bio by remember(state.profile) { mutableStateOf(state.profile?.bio ?: "") }
    var username by remember(state.profile) { mutableStateOf(state.profile?.username ?: "") }
    var gender by remember(state.profile) { mutableStateOf(state.profile?.gender ?: "Unspecified") }
    var dob by remember(state.profile) { mutableStateOf(state.profile?.dateOfBirth ?: "") }
    var profilePicUrl by remember(state.profile) { mutableStateOf(state.profile?.profilePictureUrl ?: "") }
    var coverPhotoUrl by remember(state.profile) { mutableStateOf(state.profile?.coverPhotoUrl ?: "") }

    val preferredLangsList = remember(state.profile) {
        mutableStateListOf<String>().apply {
            val dbLang = state.profile?.preferredLanguage ?: ""
            if (dbLang.isNotBlank()) {
                addAll(dbLang.split(",").map { it.trim() }.filter { it.isNotEmpty() })
            }
        }
    }
    var langInputText by remember { mutableStateOf("") }

    var isUsernameLocked by remember(state.profile) { mutableStateOf(!state.profile?.username.isNullOrEmpty()) }
    var showApplyForm by remember { mutableStateOf(false) }
    var applicantNote by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }

    var isUploadingProfilePic by remember { mutableStateOf(false) }
    var isUploadingCoverPhoto by remember { mutableStateOf(false) }

    // Cropping Dialog States
    var imageToCropUri by remember { mutableStateOf<Uri?>(null) }
    var isCroppingProfile by remember { mutableStateOf(false) }
    var isCroppingCover by remember { mutableStateOf(false) }
    var cropScale by remember { mutableStateOf(1f) }
    var cropOffset by remember { mutableStateOf(Offset.Zero) }
    val cropTransformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        cropScale = (cropScale * zoomChange).coerceIn(1f, 4f)
        cropOffset += offsetChange
    }

    val profilePicLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageToCropUri = uri
            isCroppingProfile = true
            isCroppingCover = false
            cropScale = 1f
            cropOffset = Offset.Zero
        }
    }

    val coverPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageToCropUri = uri
            isCroppingProfile = false
            isCroppingCover = true
            cropScale = 1f
            cropOffset = Offset.Zero
        }
    }

    val showDatePicker = {
        val calendar = java.util.Calendar.getInstance()
        if (dob.isNotEmpty()) {
            try {
                val parts = dob.split("-")
                if (parts.size == 3) {
                    calendar.set(java.util.Calendar.YEAR, parts[0].toInt())
                    calendar.set(java.util.Calendar.MONTH, parts[1].toInt() - 1)
                    calendar.set(java.util.Calendar.DAY_OF_MONTH, parts[2].toInt())
                }
            } catch (e: Exception) {}
        }
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedMonth = String.format("%02d", month + 1)
                val formattedDay = String.format("%02d", dayOfMonth)
                dob = "$year-$formattedMonth-$formattedDay"
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        ).show()
    }

    LaunchedEffect(username, isUsernameLocked) {
        if (!isUsernameLocked) {
            val trimmed = username.trim()
            if (trimmed != (state.profile?.username ?: "")) {
                delay(600)
                if (trimmed.isNotEmpty() && trimmed.matches(Regex("^[a-zA-Z0-9_]{3,15}$"))) {
                    onCheckUsername(trimmed)
                }
            } else {
                onResetUsernameCheck()
            }
        }
    }

    val isUsernameFormatValid = username.isEmpty() || username.matches(Regex("^[a-zA-Z0-9_]{3,15}$"))
    val isDobValid = dob.isEmpty() || dob.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))

    val preferredLangSerialized = preferredLangsList.joinToString(",")

    val isFormChanged = state.profile?.let {
        firstName.trim() != (it.firstName ?: "") ||
        lastName.trim() != (it.lastName ?: "") ||
        bio.trim() != (it.bio ?: "") ||
        username.trim() != (it.username ?: "") ||
        gender != (it.gender ?: "Unspecified") ||
        dob.trim() != (it.dateOfBirth ?: "") ||
        profilePicUrl.trim() != (it.profilePictureUrl ?: "") ||
        coverPhotoUrl.trim() != (it.coverPhotoUrl ?: "") ||
        preferredLangSerialized.trim() != (it.preferredLanguage ?: "")
    } ?: false

    val isSaveEnabled = isFormChanged && 
            firstName.isNotBlank() && 
            isUsernameFormatValid && 
            isDobValid &&
            (isUsernameLocked || username.trim() == (state.profile?.username ?: "") || state.usernameUnique == true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Lucide.ArrowLeft,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "प्रोफ़ाइल संपादित करें" else "Edit Profile",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        ProfileMediaHeader(
            coverPhotoUrl = coverPhotoUrl,
            profilePicUrl = profilePicUrl,
            firstName = firstName,
            isEditMode = true,
            isUploadingProfile = isUploadingProfilePic,
            isUploadingCover = isUploadingCoverPhoto,
            onProfileClick = { profilePicLauncher.launch("image/*") },
            onCoverClick = { coverPhotoLauncher.launch("image/*") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isHindi) "व्यक्तिगत पहचान" else "Personal Identification",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.EmeraldGreen,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        ProfileEditRow(
            label = if (isHindi) "पहला नाम" else "First Name",
            value = firstName,
            onValueChange = { firstName = it },
            placeholder = if (isHindi) "अपना पहला नाम दर्ज करें" else "Enter your first name",
            isDark = isDark
        )

        ProfileEditRow(
            label = if (isHindi) "अंतिम नाम" else "Last Name",
            value = lastName,
            onValueChange = { lastName = it },
            placeholder = if (isHindi) "अपना अंतिम नाम दर्ज करें" else "Enter your last name",
            isDark = isDark
        )

        var showGenderDropdown by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = if (isHindi) "लिंग" else "Gender",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                val displayGender = when (gender.lowercase()) {
                    "male" -> if (isHindi) "पुरुष" else "Male"
                    "female" -> if (isHindi) "महिला" else "Female"
                    "other" -> if (isHindi) "अन्य" else "Other"
                    "unspecified" -> if (isHindi) "अनिर्दिष्ट" else "Unspecified"
                    else -> gender
                }
                Text(
                    text = displayGender,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showGenderDropdown = true }
                        .padding(vertical = 4.dp)
                )
                DropdownMenu(
                    expanded = showGenderDropdown,
                    onDismissRequest = { showGenderDropdown = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    val options = listOf(
                        "Male" to (if (isHindi) "पुरुष (Male)" else "Male"),
                        "Female" to (if (isHindi) "महिला (Female)" else "Female"),
                        "Other" to (if (isHindi) "अन्य (Other)" else "Other"),
                        "Unspecified" to (if (isHindi) "अनिर्दिष्ट (Unspecified)" else "Unspecified")
                    )
                    options.forEach { (optionKey, optionLabel) ->
                        DropdownMenuItem(
                            text = { Text(optionLabel, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                gender = optionKey
                                showGenderDropdown = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker() }
        ) {
            ProfileEditRow(
                label = if (isHindi) "जन्म तिथि (YYYY-MM-DD)" else "Date of Birth (YYYY-MM-DD)",
                value = dob,
                onValueChange = {},
                placeholder = if (isHindi) "उदा. 2000-01-01" else "e.g., 2000-01-01",
                errorText = if (!isDobValid) {
                    if (isHindi) "तिथि का प्रारूप YYYY-MM-DD होना चाहिए" else "Date must match format YYYY-MM-DD"
                } else null,
                isDark = isDark,
                readOnly = true,
                trailingContent = {
                    IconButton(
                        onClick = { showDatePicker() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Calendar,
                            contentDescription = "Select Date",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                modifier = Modifier.clickable { showDatePicker() }
            )
        }

        ProfileEditRow(
            label = if (isHindi) "पसंदीदा सीखने की भाषा" else "Preferred Learning Language",
            value = langInputText,
            onValueChange = { text ->
                if (text.endsWith(" ") || text.endsWith(",")) {
                    val cleanWord = text.replace(",", "").trim()
                    if (cleanWord.isNotEmpty() && !preferredLangsList.contains(cleanWord)) {
                        val potentialSerialized = (preferredLangsList + cleanWord).joinToString(",")
                        if (potentialSerialized.length <= 10) {
                            preferredLangsList.add(cleanWord)
                            langInputText = ""
                        } else {
                            android.widget.Toast.makeText(context, if (isHindi) "अधिकतम 10 अक्षर!" else "Max 10 characters!", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        langInputText = ""
                    }
                } else {
                    langInputText = text
                }
            },
            placeholder = if (isHindi) "भाषा टाइप करें और स्पेस दबाएं (उदा. English )" else "Type language and press space (e.g. English )",
            isDark = isDark
        )

        if (preferredLangsList.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                preferredLangsList.forEach { lang ->
                    Box(
                        modifier = Modifier
                            .background(AppColors.EmeraldGreen.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .border(1.dp, AppColors.EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = lang,
                                fontSize = 12.sp,
                                color = AppColors.EmeraldGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Remove",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier
                                    .size(12.dp)
                                    .clickable { preferredLangsList.remove(lang) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isHindi) "यूज़रनेम क्रेडेंशियल्स" else "Username Credentials",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.EmeraldGreen,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        val usernameError = if (!isUsernameLocked && !isUsernameFormatValid) {
            if (isHindi) "यूज़रनेम 3-15 वर्णों का होना चाहिए (केवल अक्षर, संख्या, अंडरस्कोर)" else "Username must be 3-15 chars (letters, numbers, underscores only)"
        } else if (!isUsernameLocked && username.trim().isNotEmpty() && 
                   username.trim() != (state.profile?.username ?: "") && 
                   state.usernameUnique == false) {
            if (isHindi) "यह यूज़रनेम पहले से लिया जा चुका है" else "This username is already taken"
        } else null

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = if (isHindi) "अद्वितीय यूज़रनेम" else "Unique Username",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isUsernameLocked) {
                    Text(
                        text = username,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (isHindi) "बदलें" else "Change",
                        color = AppColors.EmeraldGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clickable { isUsernameLocked = false }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                } else {
                    BasicTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        ),
                        decorationBox = { innerTextField ->
                            if (username.isEmpty()) {
                                Text(
                                    text = if (isHindi) "एक अद्वितीय यूज़रनेम चुनें" else "Pick a unique username",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    if (username.trim().isNotEmpty() && username.trim() != (state.profile?.username ?: "")) {
                        if (state.usernameChecking) {
                            CircularProgressIndicator(
                                color = AppColors.EmeraldGreen,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else if (state.usernameUnique == true) {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = "Available",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        } else if (state.usernameUnique == false || !isUsernameFormatValid) {
                            Icon(
                                imageVector = Lucide.CircleAlert,
                                contentDescription = "Not Available",
                                tint = Color.Red,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (!state.profile?.username.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "रद्द करें" else "Cancel",
                            color = Color.Red,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clickable {
                                    isUsernameLocked = true
                                    username = state.profile?.username ?: ""
                                }
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
            if (usernameError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = usernameError,
                    color = Color.Red,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isHindi) "अकादमिक बायो" else "Academic Bio",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.EmeraldGreen,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        ProfileBioRow(
            value = bio,
            onValueChange = { bio = it },
            placeholder = if (isHindi) "अपनी पढ़ाई या शिक्षण लक्ष्यों के बारे में बताएं..." else "Tell us about your studies or teaching goals...",
            isDark = isDark,
            isHindi = isHindi
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onSaveClick(
                    firstName,
                    lastName,
                    bio,
                    username,
                    if (gender == "Unspecified") null else gender.lowercase(),
                    if (dob.isBlank()) null else dob,
                    if (profilePicUrl.isBlank()) null else profilePicUrl,
                    if (coverPhotoUrl.isBlank()) null else coverPhotoUrl,
                    if (preferredLangSerialized.isBlank()) null else preferredLangSerialized
                )
            },
            enabled = isSaveEnabled && !state.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.EmeraldGreen,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(46.dp)
        ) {
            Text(
                text = if (isHindi) "प्रोफ़ाइल विवरण सहेजें" else "Save Profile Details",
                color = if (isSaveEnabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = if (isHindi) "सत्यापन और योगदान स्थिति" else "Verification & Contribution Status",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.EmeraldGreen,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val verification = state.verification
            if (state.profile?.isVerified == true || verification?.status == "approved") {
                val approvedBg = if (isDark) Color(0xFF1B3D2F) else Color(0xFFF4FBF7)
                val approvedBorder = if (isDark) Color(0xFF2E614A) else Color(0xFFD1F2E5)
                val approvedText = if (isDark) Color(0xFFD1F2E5) else Color(0xFF0F5132)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(approvedBg, RoundedCornerShape(8.dp))
                        .border(1.dp, approvedBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Check,
                        contentDescription = "Approved",
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = if (isHindi) "सत्यापित अकादमिक योगदानकर्ता" else "Verified Academic Contributor",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = approvedText
                        )
                        Text(
                            text = if (isHindi) "आपको केस स्टडीज अपलोड और संपादित करने की स्वीकृति है।" else "You are approved to upload and edit case studies.",
                            fontSize = 12.sp,
                            color = approvedText.copy(alpha = 0.8f)
                        )
                    }
                }
            } else if (verification?.status == "pending") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Lucide.CircleAlert,
                        contentDescription = "Pending",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = if (isHindi) "सत्यापन स्थिति: समीक्षा के अधीन" else "Verification Status: Under Review",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isHindi) "अध्ययन प्रकाशित करने का आपका अनुरोध सत्यापित किया जा रहा है।" else "Your request to publish studies is currently being verified.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (verification?.status == "rejected") {
                val rejectedBg = if (isDark) Color(0xFF3D1E1E) else Color(0xFFFFF5F5)
                val rejectedBorder = if (isDark) Color(0xFF6E2E2E) else Color(0xFFFFD1D1)
                val rejectedText = if (isDark) Color(0xFFFFD1D1) else Color(0xFF842029)

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(rejectedBg, RoundedCornerShape(8.dp))
                            .border(1.dp, rejectedBorder, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.CircleAlert,
                            contentDescription = "Rejected",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = if (isHindi) "सत्यापन अनुरोध अस्वीकार कर दिया गया" else "Verification Request Rejected",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = rejectedText
                            )
                            Text(
                                text = if (isHindi) "कारण: ${verification.rejectionReason ?: "मानदंड पूरे नहीं हुए"}" else "Reason: ${verification.rejectionReason ?: "Note criteria not met"}",
                                fontSize = 12.sp,
                                color = rejectedText.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (!showApplyForm) {
                        OutlinedButton(
                            onClick = { showApplyForm = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.EmeraldGreen),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Text(
                                text = if (isHindi) "नया अनुरोध सबमिट करें" else "Submit New Request",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!showApplyForm) {
                        OutlinedButton(
                            onClick = { showApplyForm = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.EmeraldGreen),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Text(
                                text = if (isHindi) "केस स्टडीज प्रकाशित करने के लिए आवेदन करें" else "Apply to Publish Case Studies",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (showApplyForm && state.verification?.status != "approved" && state.verification?.status != "pending") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isHindi) "सत्यापन के लिए आवेदन करें" else "Apply for Verification",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isHindi) "संक्षेप में बताएं कि आप VidyaSetu AI पर केस स्टडीज क्यों लिखना और प्रकाशित करना चाहते हैं:" else "Briefly explain why you want to write and publish case studies on VidyaSetu AI:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = applicantNote,
                    onValueChange = { applicantNote = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                        .padding(10.dp),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    decorationBox = { innerTextField ->
                        if (applicantNote.isEmpty()) {
                            Text(
                                text = if (isHindi) "उदा. मैं रोडमैप साझा करने का इच्छुक एंड्रॉइड डेवलपर हूँ..." else "e.g., I am an Android developer wanting to share roadmaps...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { termsAccepted = !termsAccepted }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(checkedColor = AppColors.EmeraldGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "मैं योगदानकर्ता दिशानिर्देशों से सहमत हूँ। मैं समझता हूँ कि सभी सबमिट की गई केस स्टडीज लाइव होने से पहले प्रशासकों द्वारा समीक्षा की जाएंगी।" else "I agree to the Contributor Guidelines. I understand that all submitted case studies will be reviewed by administrators before going live.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val isSubmitEnabled = applicantNote.isNotBlank() && termsAccepted

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { 
                            showApplyForm = false 
                            applicantNote = ""
                            termsAccepted = false
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                    ) {
                        Text(
                            text = if (isHindi) "रद्द करें" else "Cancel",
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            onApplyVerificationClick(applicantNote)
                            showApplyForm = false
                            applicantNote = ""
                            termsAccepted = false
                        },
                        enabled = isSubmitEnabled && !state.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.EmeraldGreen,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(38.dp)
                    ) {
                        Text(
                            text = if (isHindi) "अनुरोध सबमिट करें" else "Submit Request",
                            color = if (isSubmitEnabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (imageToCropUri != null) {
        val isProfile = isCroppingProfile
        Dialog(onDismissRequest = { 
            imageToCropUri = null
            isCroppingProfile = false
            isCroppingCover = false
            cropScale = 1f
            cropOffset = Offset.Zero
        }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isProfile) {
                            if (isHindi) "प्रोफ़ाइल फोटो क्रॉप करें" else "Crop Profile Photo"
                        } else {
                            if (isHindi) "कवर फोटो क्रॉप करें" else "Crop Cover Photo"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(if (isProfile) 1f else 16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = imageToCropUri,
                            contentDescription = "Crop Preview",
                            modifier = Modifier
                                .fillMaxSize()
                                .transformable(state = cropTransformState)
                                .graphicsLayer(
                                    scaleX = cropScale,
                                    scaleY = cropScale,
                                    translationX = cropOffset.x,
                                    translationY = cropOffset.y
                                ),
                            contentScale = ContentScale.Fit
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    2.dp, 
                                    Color.White.copy(alpha = 0.8f), 
                                    if (isProfile) CircleShape else RectangleShape
                                )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "—",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        Slider(
                            value = cropScale,
                            onValueChange = { cropScale = it },
                            valueRange = 1f..4f,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = AppColors.EmeraldGreen,
                                activeTrackColor = AppColors.EmeraldGreen
                            )
                        )
                        Text(
                            text = "+",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                imageToCropUri = null
                                isCroppingProfile = false
                                isCroppingCover = false
                                cropScale = 1f
                                cropOffset = Offset.Zero
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = if (isHindi) "रद्द करें" else "Cancel")
                        }
                        
                        Button(
                            onClick = {
                                scope.launch {
                                    if (isProfile) isUploadingProfilePic = true else isUploadingCoverPhoto = true
                                    val uri = imageToCropUri!!
                                    imageToCropUri = null
                                    
                                    val croppedBytes = withContext(Dispatchers.IO) {
                                        cropBitmapWithTransform(
                                            context = context,
                                            uri = uri,
                                            isProfile = isProfile,
                                            scale = cropScale,
                                            offsetX = cropOffset.x,
                                            offsetY = cropOffset.y,
                                            containerWidthPx = 1080,
                                            containerHeightPx = if (isProfile) 1080 else 607
                                        )
                                    }
                                    
                                    if (croppedBytes != null) {
                                        val timestamp = System.currentTimeMillis()
                                        if (isProfile) {
                                            val fileName = "profiles/profile_${userId}_$timestamp.jpg"
                                            val publicUrl = SupabaseStorageHelper.uploadImage("user_profile_media", fileName, croppedBytes)
                                            profilePicUrl = publicUrl
                                            isUploadingProfilePic = false
                                        } else {
                                            val fileName = "profiles/cover_${userId}_$timestamp.jpg"
                                            val publicUrl = SupabaseStorageHelper.uploadImage("user_profile_media", fileName, croppedBytes)
                                            coverPhotoUrl = publicUrl
                                            isUploadingCoverPhoto = false
                                        }
                                    } else {
                                        isUploadingProfilePic = false
                                        isUploadingCoverPhoto = false
                                    }
                                    
                                    isCroppingProfile = false
                                    isCroppingCover = false
                                    cropScale = 1f
                                    cropOffset = Offset.Zero
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(text = if (isHindi) "क्रॉप और सेव" else "Crop & Save", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileEditRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    errorText: String? = null,
    isDark: Boolean = false,
    readOnly: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                readOnly = readOnly,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )
            if (trailingContent != null) {
                trailingContent()
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
        if (errorText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorText,
                color = Color.Red,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun ProfileBioRow(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isDark: Boolean = false,
    maxLength: Int = 250,
    isHindi: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = if (isHindi) "अकादमिक बायो" else "Academic Bio",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        BasicTextField(
            value = value,
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChange(it)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp
            ),
            decorationBox = { innerTextField ->
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 15.sp
                    )
                }
                innerTextField()
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${value.length}/$maxLength",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}
