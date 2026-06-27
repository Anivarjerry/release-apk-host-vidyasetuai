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
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.User
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Camera
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
import com.vidyasetuai.feature_case_study.data.repository.CaseStudyRepositoryImpl
import com.vidyasetuai.feature_case_study.domain.model.CaseStudy
import com.vidyasetuai.feature_feed.data.repository.ExperienceRepository
import com.vidyasetuai.feature_feed.domain.model.Experience
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun processProfileImage(context: android.content.Context, uri: Uri): ByteArray? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
            val width = originalBitmap.width
            val height = originalBitmap.height
            val squareSize = if (width < height) width else height
            val x = (width - squareSize) / 2
            val y = (height - squareSize) / 2
            
            val cropped = Bitmap.createBitmap(originalBitmap, x, y, squareSize, squareSize)
            val scaled = Bitmap.createScaledBitmap(cropped, 400, 400, true)
            
            val outputStream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val bytes = outputStream.toByteArray()
            
            if (cropped != scaled) cropped.recycle()
            if (originalBitmap != cropped) originalBitmap.recycle()
            scaled.recycle()
            
            bytes
        }
    } catch (e: Exception) {
        android.util.Log.e("ProfileScreen", "Error processing profile image", e)
        null
    }
}

private fun processCoverImage(context: android.content.Context, uri: Uri): ByteArray? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
            val width = originalBitmap.width
            val height = originalBitmap.height
            
            val targetRatio = 16f / 9f
            val currentRatio = width.toFloat() / height.toFloat()
            
            val cropWidth: Int
            val cropHeight: Int
            if (currentRatio > targetRatio) {
                cropHeight = height
                cropWidth = (height * targetRatio).toInt()
            } else {
                cropWidth = width
                cropHeight = (width / targetRatio).toInt()
            }
            
            val x = (width - cropWidth) / 2
            val y = (height - cropHeight) / 2
            
            val cropped = Bitmap.createBitmap(originalBitmap, x, y, cropWidth, cropHeight)
            val scaled = Bitmap.createScaledBitmap(cropped, 960, 540, true)
            
            val outputStream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val bytes = outputStream.toByteArray()
            
            if (cropped != scaled) cropped.recycle()
            if (originalBitmap != cropped) originalBitmap.recycle()
            scaled.recycle()
            
            bytes
        }
    } catch (e: Exception) {
        android.util.Log.e("ProfileScreen", "Error processing cover image", e)
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
    onCaseStudyClick: (String) -> Unit = {},
    onInspirationsClick: (String, Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isHindi = currentLanguage == "hi"
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()

    val viewModel = remember {
        val db = AppDatabase.getDatabase(context)
        val localDS = ProfileLocalDataSource(db.userProfileDao())
        val remoteDS = ProfileRemoteDataSource()
        val repo = ProfileRepositoryImpl(localDS, remoteDS)
        val getProfileUC = GetUserProfileUseCase(repo)
        val updateProfileUC = UpdateUserProfileUseCase(repo)
        val checkUsernameUC = CheckUsernameUniqueUseCase(repo)
        val applyVerificationUC = ApplyForVerificationUseCase(repo)
        ProfileViewModel(getProfileUC, updateProfileUC, checkUsernameUC, applyVerificationUC)
    }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.onEvent(ProfileEvent.LoadProfile(userId))
        }
    }

    val state by viewModel.uiState.collectAsState()

    var isEditMode by remember { mutableStateOf(false) }

    // Upload & Caching for user's own contributions
    var userCaseStudies by remember { mutableStateOf<List<CaseStudy>>(emptyList()) }
    var userExperiences by remember { mutableStateOf<List<Experience>>(emptyList()) }
    var isUserUploadedLoading by remember { mutableStateOf(false) }
    var inspirersCount by remember { mutableStateOf(0) }
    var inspiringCount by remember { mutableStateOf(0) }

    val profileDb = remember { AppDatabase.getDatabase(context) }
    val caseStudyRepository = remember {
        val localDS = com.vidyasetuai.feature_case_study.data.local.datasource.CaseStudyLocalDataSource(profileDb.caseStudyDao())
        val remoteDS = com.vidyasetuai.feature_case_study.data.remote.datasource.CaseStudyRemoteDataSource()
        CaseStudyRepositoryImpl(localDS, remoteDS)
    }
    val experienceRepository = remember { ExperienceRepository() }
    val profileRepository = remember {
        val localDS = ProfileLocalDataSource(profileDb.userProfileDao())
        val remoteDS = ProfileRemoteDataSource()
        ProfileRepositoryImpl(localDS, remoteDS)
    }

    LaunchedEffect(userId, state.profile?.isVerified, isEditMode) {
        if (userId.isNotEmpty() && !isEditMode) {
            isUserUploadedLoading = true
            scope.launch {
                caseStudyRepository.getUserUploadedCaseStudies(userId).onSuccess { list ->
                    userCaseStudies = list
                }
            }
            scope.launch {
                experienceRepository.getExperiencesByUser(userId, userId).onSuccess { list ->
                    userExperiences = list
                }
            }
            scope.launch {
                profileRepository.getInspiredCount(userId).onSuccess { count ->
                    inspirersCount = count
                }
            }
            scope.launch {
                profileRepository.getInspiringCount(userId).onSuccess { count ->
                    inspiringCount = count
                }
            }
            isUserUploadedLoading = false
        }
    }

    // Edit screen variables
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var profilePicUrl by remember { mutableStateOf("") }
    var coverPhotoUrl by remember { mutableStateOf("") }
    var preferredLang by remember { mutableStateOf("") }

    var isUsernameLocked by remember { mutableStateOf(true) }
    var showApplyForm by remember { mutableStateOf(false) }
    var applicantNote by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }

    // Gallery upload states
    var isUploadingProfilePic by remember { mutableStateOf(false) }
    var isUploadingCoverPhoto by remember { mutableStateOf(false) }

    val profilePicLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isUploadingProfilePic = true
                try {
                    val bytes = processProfileImage(context, uri)
                    if (bytes != null) {
                        val fileName = "profile_${userId}_${System.currentTimeMillis()}.jpg"
                        val publicUrl = SupabaseStorageHelper.uploadImage("users_profile_image", fileName, bytes)
                        profilePicUrl = publicUrl
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ProfileScreen", "Failed to upload profile picture", e)
                } finally {
                    isUploadingProfilePic = false
                }
            }
        }
    }

    val coverPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isUploadingCoverPhoto = true
                try {
                    val bytes = processCoverImage(context, uri)
                    if (bytes != null) {
                        val fileName = "cover_${userId}_${System.currentTimeMillis()}.jpg"
                        val publicUrl = SupabaseStorageHelper.uploadImage("users_cover_image", fileName, bytes)
                        coverPhotoUrl = publicUrl
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ProfileScreen", "Failed to upload cover photo", e)
                } finally {
                    isUploadingCoverPhoto = false
                }
            }
        }
    }

    // Initialize text fields when profile loads
    LaunchedEffect(state.profile) {
        state.profile?.let { profile ->
            firstName = profile.firstName ?: ""
            lastName = profile.lastName ?: ""
            bio = profile.bio ?: ""
            username = profile.username ?: ""
            gender = profile.gender ?: "Unspecified"
            dob = profile.dateOfBirth ?: ""
            profilePicUrl = profile.profilePictureUrl ?: ""
            coverPhotoUrl = profile.coverPhotoUrl ?: ""
            preferredLang = profile.preferredLanguage ?: ""
            isUsernameLocked = !profile.username.isNullOrEmpty()
        }
    }

    // Debounce username unique checks when unlocked
    LaunchedEffect(username, isUsernameLocked) {
        if (!isUsernameLocked) {
            val trimmed = username.trim()
            if (trimmed != (state.profile?.username ?: "")) {
                delay(600)
                if (trimmed.isNotEmpty() && trimmed.matches(Regex("^[a-zA-Z0-9_]{3,15}$"))) {
                    viewModel.onEvent(ProfileEvent.CheckUsername(trimmed, userId))
                }
            } else {
                viewModel.onEvent(ProfileEvent.ResetUsernameCheck)
            }
        }
    }

    val isUsernameFormatValid = username.isEmpty() || username.matches(Regex("^[a-zA-Z0-9_]{3,15}$"))
    val isDobValid = dob.isEmpty() || dob.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))

    val isFormChanged = state.profile?.let {
        firstName.trim() != (it.firstName ?: "") ||
        lastName.trim() != (it.lastName ?: "") ||
        bio.trim() != (it.bio ?: "") ||
        username.trim() != (it.username ?: "") ||
        gender != (it.gender ?: "Unspecified") ||
        dob.trim() != (it.dateOfBirth ?: "") ||
        profilePicUrl.trim() != (it.profilePictureUrl ?: "") ||
        coverPhotoUrl.trim() != (it.coverPhotoUrl ?: "") ||
        preferredLang.trim() != (it.preferredLanguage ?: "")
    } ?: false

    val isSaveEnabled = isFormChanged && 
            firstName.isNotBlank() && 
            isUsernameFormatValid && 
            isDobValid &&
            (isUsernameLocked || username.trim() == (state.profile?.username ?: "") || state.usernameUnique == true)

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (!isEditMode) {
            // ================= OVERVIEW MODE =================
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
                    onClick = { isEditMode = true },
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
                            onClick = { isEditMode = true },
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
                                    onReactionClick = {
                                        scope.launch {
                                            experienceRepository.toggleInspiration(exp.id, userId)
                                            experienceRepository.getExperiencesByUser(userId, userId).onSuccess { list ->
                                                userExperiences = list
                                            }
                                        }
                                    },
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
                                    onReactionClick = {
                                        scope.launch {
                                            caseStudyRepository.toggleReaction(cs.id, userId)
                                            caseStudyRepository.getUserUploadedCaseStudies(userId).onSuccess { list ->
                                                userCaseStudies = list
                                            }
                                        }
                                    },
                                    onBookmarkClick = {
                                        scope.launch {
                                            caseStudyRepository.toggleBookmark(cs.id, userId)
                                            caseStudyRepository.getUserUploadedCaseStudies(userId).onSuccess { list ->
                                                userCaseStudies = list
                                            }
                                        }
                                    },
                                    currentLanguage = currentLanguage,
                                    onExploreClick = { onCaseStudyClick(cs.id) },
                                    onAuthorClick = {}
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // ================= EDIT MODE =================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 32.dp)
            ) {
                // Header of Edit Mode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { isEditMode = false }) {
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
                
                // Group Title: Personal Identification
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

                // Gender Selection
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
                        val displayGender = when (gender) {
                            "Male" -> if (isHindi) "पुरुष" else "Male"
                            "Female" -> if (isHindi) "महिला" else "Female"
                            "Other" -> if (isHindi) "अन्य" else "Other"
                            "Unspecified" -> if (isHindi) "अनिर्दिष्ट" else "Unspecified"
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

                // Date of Birth
                ProfileEditRow(
                    label = if (isHindi) "जन्म तिथि (YYYY-MM-DD)" else "Date of Birth (YYYY-MM-DD)",
                    value = dob,
                    onValueChange = { dob = it },
                    placeholder = if (isHindi) "उदा. 2000-01-01" else "e.g., 2000-01-01",
                    errorText = if (!isDobValid) {
                        if (isHindi) "तिथि का प्रारूप YYYY-MM-DD होना चाहिए" else "Date must match format YYYY-MM-DD"
                    } else null,
                    isDark = isDark
                )

                ProfileEditRow(
                    label = if (isHindi) "पसंदीदा सीखने की भाषा" else "Preferred Learning Language",
                    value = preferredLang,
                    onValueChange = { preferredLang = it },
                    placeholder = if (isHindi) "उदा. हिंदी, इंग्लिश" else "e.g., Hindi, English",
                    isDark = isDark
                )

                // Group Title: Username Credentials
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
                // We've moved media uploads to the Facebook-style header above

                // Group Title: Biography
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
                    isDark = isDark
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Save Profile Button
                Button(
                    onClick = {
                        viewModel.onEvent(
                            ProfileEvent.UpdateProfile(
                                firstName = firstName,
                                lastName = lastName,
                                bio = bio,
                                username = username,
                                gender = if (gender == "Unspecified") null else gender,
                                dateOfBirth = if (dob.isBlank()) null else dob,
                                profilePictureUrl = if (profilePicUrl.isBlank()) null else profilePicUrl,
                                coverPhotoUrl = if (coverPhotoUrl.isBlank()) null else coverPhotoUrl,
                                preferredLanguage = if (preferredLang.isBlank()) null else preferredLang
                            )
                        )
                        isEditMode = false
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

                // Group Title: Contribution & Verification
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
                        // Not Applied
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

                // Inline Application Form
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

                        // Terms & Conditions Checkbox Row
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
                                    viewModel.onEvent(ProfileEvent.ApplyVerification(applicantNote))
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
fun ProfileEditRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    errorText: String? = null,
    isDark: Boolean = false,
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
    isDark: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = if (isDark) "Academic Bio" else "Academic Bio",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
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
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}
