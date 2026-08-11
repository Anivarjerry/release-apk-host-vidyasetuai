package com.vidyasetuai.core.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.vidyasetuai.feature_campus.presentation.screen.CampusScreen
import com.vidyasetuai.feature_campus.presentation.screen.PrivateChatRoomScreen
import com.vidyasetuai.feature_campus.presentation.viewmodel.CampusViewModel
import com.vidyasetuai.feature_case_study.data.repository.QuickRepository
import com.vidyasetuai.feature_case_study.domain.model.Quick
import com.vidyasetuai.feature_case_study.presentation.screen.CaseStudyDetailScreen
import com.vidyasetuai.feature_case_study.presentation.screen.subscreen.QuickViewerScreen
import com.vidyasetuai.feature_feed.presentation.screen.NotificationEvent
import com.vidyasetuai.feature_feed.presentation.screen.TournamentEvent
import com.vidyasetuai.feature_profile.presentation.screen.InspirationsListScreen
import com.vidyasetuai.feature_profile.presentation.screen.PublicProfileScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardSubScreens(
    activeTab: String,
    quickViewerList: List<Quick>,
    quickViewerSelectedIndex: Int,
    selectedCaseStudyId: String?,
    selectedPublicProfileUserId: String?,
    inspirationsListUserId: String?,
    inspirationsDefaultTab: Int,
    navigationStack: List<NavState>,
    userId: String,
    currentLanguage: String,
    currentTheme: String,
    quickRepo: QuickRepository,
    campusViewModel: CampusViewModel,
    onThemeChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    navigateBack: () -> Unit,
    navigateTo: (String) -> Unit,
    onSelectPublicProfileUser: (String) -> Unit,
    onSelectCaseStudy: (String) -> Unit,
    onSelectInspirations: (String, Int) -> Unit,
    onPopNavStackForPublicProfile: (String) -> Unit,
    initialSettingsTarget: String? = null,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == "hi"

    AnimatedContent(
        targetState = activeTab,
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(350)) togetherWith
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(350))
        },
        label = "SubScreenTransition",
        modifier = modifier.fillMaxSize()
    ) { targetTab ->
        when (targetTab) {
            "quick_viewer" -> {
                QuickViewerScreen(
                    quicks = quickViewerList,
                    initialIndex = quickViewerSelectedIndex,
                    currentLanguage = currentLanguage,
                    userId = userId,
                    repository = quickRepo,
                    onBack = { navigateBack() },
                    onAuthorClick = { authorId ->
                        if (authorId.isNotBlank()) {
                            onSelectPublicProfileUser(authorId)
                            navigateTo("public_profile")
                        }
                    }
                )
            }
            "case_study_detail" -> {
                CaseStudyDetailScreen(
                    caseStudyId = selectedCaseStudyId ?: "",
                    userId = userId,
                    onBack = { navigateBack() }
                )
            }
            "chat_room" -> {
                PrivateChatRoomScreen(
                    viewModel = campusViewModel,
                    userId = userId,
                    currentLanguage = currentLanguage,
                    currentTheme = currentTheme,
                    onBack = {
                        campusViewModel.onEvent(com.vidyasetuai.feature_campus.presentation.event.CampusEvent.ClosePrivateChat)
                        navigateBack()
                    },
                    onOpenUserProfile = { clickedUserId ->
                        onSelectPublicProfileUser(clickedUserId)
                        navigateTo("public_profile")
                    }
                )
            }
            "private_chat_room" -> {
                PrivateChatRoomScreen(
                    viewModel = campusViewModel,
                    userId = userId,
                    currentLanguage = currentLanguage,
                    currentTheme = currentTheme,
                    onBack = {
                        campusViewModel.onEvent(com.vidyasetuai.feature_campus.presentation.event.CampusEvent.ClosePrivateChat)
                        navigateBack()
                    },
                    onOpenUserProfile = { targetUserId ->
                        onSelectPublicProfileUser(targetUserId)
                        navigateTo("public_profile")
                    }
                )
            }
            "settings" -> {
                SettingsScreen(
                    currentTheme = currentTheme,
                    onThemeChange = onThemeChange,
                    currentLanguage = currentLanguage,
                    onLanguageChange = onLanguageChange,
                    onBack = { navigateBack() },
                    onOpenTournament = { navigateTo("tournament") },
                    initialTarget = initialSettingsTarget
                )
            }
            "public_profile" -> {
                PublicProfileScreen(
                    currentUserId = userId,
                    targetUserId = selectedPublicProfileUserId ?: "",
                    currentLanguage = currentLanguage,
                    onBackClick = { navigateBack() },
                    onInspirationsClick = { targetId, tabIndex ->
                        onSelectInspirations(targetId, tabIndex)
                        navigateTo("inspirations_list")
                    },
                    onCaseStudyClick = { caseStudyId ->
                        onSelectCaseStudy(caseStudyId)
                        navigateTo("case_study_detail")
                    }
                )
            }
            "inspirations_list" -> {
                InspirationsListScreen(
                    currentUserId = userId,
                    targetUserId = inspirationsListUserId ?: "",
                    initialTab = inspirationsDefaultTab,
                    currentLanguage = currentLanguage,
                    onBackClick = { navigateBack() },
                    onUserClick = { clickedUserId ->
                        if (navigationStack.isNotEmpty() && navigationStack.last().tab == "public_profile") {
                            onPopNavStackForPublicProfile(clickedUserId)
                        } else {
                            onSelectPublicProfileUser(clickedUserId)
                            navigateTo("public_profile")
                        }
                    }
                )
            }
            "tournament", "notifications" -> {
                // Full screen view with simple top bar (Back button + Title) and NO bottom bar
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
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
                                    onClick = { navigateBack() },
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
                                    text = if (targetTab == "tournament") {
                                        if (isHindi) "टूर्नामेंट" else "Tournament"
                                    } else {
                                        if (isHindi) "नोटिफिकेशन" else "Notifications"
                                    },
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(innerPadding)
                    ) {
                        if (targetTab == "tournament") {
                            TournamentEvent(currentLanguage = currentLanguage, currentTheme = currentTheme)
                        } else {
                            NotificationEvent(currentLanguage = currentLanguage, currentTheme = currentTheme)
                        }
                    }
                }
            }
        }
    }
}
