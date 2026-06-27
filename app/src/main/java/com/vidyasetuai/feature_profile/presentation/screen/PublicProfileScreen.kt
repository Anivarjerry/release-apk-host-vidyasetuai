package com.vidyasetuai.feature_profile.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Trophy
import com.vidyasetuai.core.database.AppDatabase
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_case_study.data.repository.CaseStudyRepositoryImpl
import com.vidyasetuai.feature_case_study.domain.model.CaseStudy
import com.vidyasetuai.feature_case_study.presentation.component.CaseStudyCard
import com.vidyasetuai.feature_feed.data.repository.ExperienceRepository
import com.vidyasetuai.feature_feed.domain.model.Experience
import com.vidyasetuai.feature_feed.presentation.component.ExperienceCard
import com.vidyasetuai.feature_profile.data.local.datasource.ProfileLocalDataSource
import com.vidyasetuai.feature_profile.data.remote.datasource.ProfileRemoteDataSource
import com.vidyasetuai.feature_profile.data.repository.ProfileRepositoryImpl
import com.vidyasetuai.feature_profile.domain.model.UserProfile
import kotlinx.coroutines.launch

@Composable
fun PublicProfileScreen(
    currentUserId: String,
    targetUserId: String,
    currentLanguage: String,
    onBackClick: () -> Unit,
    onInspirationsClick: (String, Int) -> Unit,
    onCaseStudyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isHindi = currentLanguage == "hi"
    val isDark = isSystemInDarkTheme()

    val profileDb = remember { AppDatabase.getDatabase(context) }
    val profileRepository = remember {
        val localDS = ProfileLocalDataSource(profileDb.userProfileDao())
        val remoteDS = ProfileRemoteDataSource()
        ProfileRepositoryImpl(localDS, remoteDS)
    }
    val caseStudyRepository = remember {
        val localDS = com.vidyasetuai.feature_case_study.data.local.datasource.CaseStudyLocalDataSource(profileDb.caseStudyDao())
        val remoteDS = com.vidyasetuai.feature_case_study.data.remote.datasource.CaseStudyRemoteDataSource()
        CaseStudyRepositoryImpl(localDS, remoteDS)
    }
    val experienceRepository = remember { ExperienceRepository() }

    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var isInspired by remember { mutableStateOf(false) }
    var inspirersCount by remember { mutableStateOf(0) }
    var inspiringCount by remember { mutableStateOf(0) }
    var caseStudies by remember { mutableStateOf<List<CaseStudy>>(emptyList()) }
    var experiences by remember { mutableStateOf<List<Experience>>(emptyList()) }
    var isLoadingProfile by remember { mutableStateOf(true) }
    var isLoadingContent by remember { mutableStateOf(true) }
    var activeTab by remember { mutableStateOf("case_studies") }

    LaunchedEffect(targetUserId, currentUserId) {
        if (targetUserId.isNotEmpty()) {
            isLoadingProfile = true
            isLoadingContent = true
            
            profileRepository.getProfileById(targetUserId).onSuccess {
                profile = it
            }
            isLoadingProfile = false

            if (currentUserId.isNotEmpty()) {
                profileRepository.isInspiredBy(currentUserId, targetUserId).onSuccess {
                    isInspired = it
                }
            }

            profileRepository.getInspiredCount(targetUserId).onSuccess {
                inspirersCount = it
            }

            profileRepository.getInspiringCount(targetUserId).onSuccess {
                inspiringCount = it
            }

            launch {
                caseStudyRepository.getUserUploadedCaseStudies(targetUserId).onSuccess {
                    caseStudies = it
                }
            }
            launch {
                experienceRepository.getExperiencesByUser(targetUserId, currentUserId).onSuccess {
                    experiences = it
                }
            }
            isLoadingContent = false
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                    text = if (isHindi) "अकादमिक प्रोफ़ाइल" else "Academic Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (isLoadingProfile) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.EmeraldGreen)
                }
            }
        } else {
            val targetProfile = profile
            if (targetProfile != null) {
                item {
                    ProfileMediaHeader(
                        coverPhotoUrl = targetProfile.coverPhotoUrl ?: "",
                        profilePicUrl = targetProfile.profilePictureUrl ?: "",
                        firstName = targetProfile.firstName ?: "",
                        isEditMode = false,
                        isUploadingProfile = false,
                        isUploadingCover = false,
                        onProfileClick = {},
                        onCoverClick = {}
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = targetProfile.fullName ?: "${targetProfile.firstName ?: ""} ${targetProfile.lastName ?: ""}".trim().ifEmpty { "Academic Contributor" },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            if (targetProfile.isVerified) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2196F3)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.Check,
                                        contentDescription = "Verified",
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "@${targetProfile.username ?: "contributor"}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(
                            count = caseStudies.size,
                            label = if (isHindi) "केस स्टडीज" else "Case Studies"
                        )
                        StatItem(
                            count = experiences.size,
                            label = if (isHindi) "अनुभव" else "Experiences"
                        )
                        StatItem(
                            count = inspirersCount,
                            label = if (isHindi) "प्रेरक (Inspirers)" else "Inspirers",
                            onClick = { onInspirationsClick(targetUserId, 0) }
                        )
                        StatItem(
                            count = inspiringCount,
                            label = if (isHindi) "प्रेरित (Inspiring)" else "Inspiring",
                            onClick = { onInspirationsClick(targetUserId, 1) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentUserId != targetUserId) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isInspired) {
                                OutlinedButton(
                                    onClick = {
                                        if (currentUserId.isNotEmpty()) {
                                            scope.launch {
                                                profileRepository.toggleUserInspiration(currentUserId, targetUserId).onSuccess { next ->
                                                    isInspired = next
                                                    profileRepository.getInspiredCount(targetUserId).onSuccess { c ->
                                                        inspirersCount = c
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    border = BorderStroke(1.dp, AppColors.EmeraldGreen),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.EmeraldGreen),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp)
                                ) {
                                    Text(
                                        text = if (isHindi) "प्रेरित (Inspired)" else "Inspired",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        if (currentUserId.isNotEmpty()) {
                                            scope.launch {
                                                profileRepository.toggleUserInspiration(currentUserId, targetUserId).onSuccess { next ->
                                                    isInspired = next
                                                    profileRepository.getInspiredCount(targetUserId).onSuccess { c ->
                                                        inspirersCount = c
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp)
                                ) {
                                    Text(
                                        text = if (isHindi) "प्रेरित हों (Be Inspired)" else "Be Inspired",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (!targetProfile.bio.isNullOrBlank()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = if (isHindi) "अकादमिक बायो" else "Academic Bio",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.EmeraldGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = targetProfile.bio!!,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Lucide.Trophy,
                                contentDescription = "Leaderboard",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "लीडरबोर्ड (आगामी सुविधा)" else "Leaderboard (Upcoming Feature)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isHindi) "प्रेरणादायक योगदानकर्ताओं की साप्ताहिक रैंकिंग जल्द ही आ रही है!" else "Weekly rankings of inspiring contributors coming soon!",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val isCase = activeTab == "case_studies"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { activeTab = "case_studies" }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (isHindi) "केस स्टडीज" else "Case Studies",
                                fontSize = 14.sp,
                                fontWeight = if (isCase) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCase) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(2.dp)
                                    .background(if (isCase) AppColors.EmeraldGreen else Color.Transparent)
                            )
                        }

                        Spacer(modifier = Modifier.width(32.dp))

                        val isExp = activeTab == "experiences"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { activeTab = "experiences" }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (isHindi) "अनुभव" else "Experiences",
                                fontSize = 14.sp,
                                fontWeight = if (isExp) FontWeight.Bold else FontWeight.Medium,
                                color = if (isExp) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(2.dp)
                                    .background(if (isExp) AppColors.EmeraldGreen else Color.Transparent)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (isLoadingContent) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AppColors.EmeraldGreen)
                        }
                    }
                } else {
                    if (activeTab == "case_studies") {
                        if (caseStudies.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isHindi) "कोई केस स्टडी प्रकाशित नहीं है।" else "No published case studies.",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(caseStudies, key = { it.id }) { caseStudy ->
                                CaseStudyCard(
                                    caseStudy = caseStudy,
                                    onClick = { onCaseStudyClick(caseStudy.id) },
                                    onReactionClick = {
                                        scope.launch {
                                            caseStudyRepository.toggleReaction(caseStudy.id, currentUserId).onSuccess {
                                                caseStudyRepository.getUserUploadedCaseStudies(targetUserId).onSuccess { list ->
                                                    caseStudies = list
                                                }
                                            }
                                        }
                                    },
                                    onBookmarkClick = {
                                        scope.launch {
                                            caseStudyRepository.toggleBookmark(caseStudy.id, currentUserId).onSuccess {
                                                caseStudyRepository.getUserUploadedCaseStudies(targetUserId).onSuccess { list ->
                                                    caseStudies = list
                                                }
                                            }
                                        }
                                    },
                                    currentLanguage = currentLanguage,
                                    onExploreClick = { onCaseStudyClick(caseStudy.id) },
                                    onAuthorClick = {},
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    } else {
                        if (experiences.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isHindi) "कोई अनुभव प्रकाशित नहीं है।" else "No published experiences.",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(experiences, key = { it.id }) { experience ->
                                ExperienceCard(
                                    experience = experience,
                                    onReactionClick = {
                                        scope.launch {
                                            experienceRepository.toggleInspiration(experience.id, currentUserId).onSuccess {
                                                experienceRepository.getExperiencesByUser(targetUserId, currentUserId).onSuccess { list ->
                                                    experiences = list
                                                }
                                            }
                                        }
                                    },
                                    currentLanguage = currentLanguage,
                                    onAuthorClick = {},
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isHindi) "यूज़र प्रोफ़ाइल लोड करने में विफल।" else "Failed to load user profile.",
                            fontSize = 16.sp,
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    count: Int,
    label: String,
    onClick: (() -> Unit)? = null
) {
    val modifier = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else {
        Modifier
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = count.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
