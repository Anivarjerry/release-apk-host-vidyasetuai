package com.vidyasetuai.core.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import com.composables.icons.lucide.Zap
import com.composables.icons.lucide.BookOpen
import com.composables.icons.lucide.Sparkles
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Compass
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.School
import com.composables.icons.lucide.Trophy
import com.composables.icons.lucide.User
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CircleAlert
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import java.io.ByteArrayOutputStream
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import com.vidyasetuai.R
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.network.SupabaseStorageHelper
import com.vidyasetuai.feature_feed.presentation.viewmodel.ExperienceViewModel
import com.vidyasetuai.feature_feed.presentation.event.ExperienceEvent
import com.vidyasetuai.feature_campus.presentation.screen.CampusScreen
import com.vidyasetuai.feature_campus.presentation.screen.PrivateChatRoomScreen
import com.vidyasetuai.feature_feed.presentation.screen.InstitutionEvent
import com.vidyasetuai.feature_journey.presentation.screen.JourneyScreen
import com.vidyasetuai.feature_feed.presentation.screen.NotificationEvent
import com.vidyasetuai.feature_profile.presentation.screen.ProfileScreen
import com.vidyasetuai.feature_profile.presentation.screen.PublicProfileScreen
import com.vidyasetuai.feature_profile.presentation.screen.InspirationsListScreen
import com.vidyasetuai.feature_feed.presentation.screen.TournamentEvent
import com.vidyasetuai.feature_profile.presentation.viewmodel.ProfileViewModel
import com.vidyasetuai.feature_profile.data.local.datasource.ProfileLocalDataSource
import com.vidyasetuai.feature_profile.data.remote.datasource.ProfileRemoteDataSource
import com.vidyasetuai.feature_profile.data.repository.ProfileRepositoryImpl
import com.vidyasetuai.feature_profile.domain.usecase.GetUserProfileUseCase
import com.vidyasetuai.feature_profile.domain.usecase.UpdateUserProfileUseCase
import com.vidyasetuai.feature_profile.domain.usecase.CheckUsernameUniqueUseCase
import com.vidyasetuai.feature_profile.domain.usecase.ApplyForVerificationUseCase
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.vidyasetuai.feature_institution.domain.model.ConnectionState
import com.vidyasetuai.feature_institution.data.repository.InstitutionRepositoryImpl
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import com.vidyasetuai.feature_institution.presentation.component.DashboardFloatingActionButton
import com.vidyasetuai.feature_institution.util.DashboardFabRules
import com.vidyasetuai.feature_case_study.presentation.screen.subscreen.AddCaseStudyFabSubScreen
import com.vidyasetuai.feature_case_study.presentation.screen.subscreen.AddExperienceFabSubScreen
import com.vidyasetuai.feature_case_study.presentation.screen.subscreen.QuicksFabSubScreen
import com.vidyasetuai.feature_case_study.presentation.screen.desbord.HomeFeedDesbord
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.AddJourneyFabSubScreen
import com.vidyasetuai.feature_profile.presentation.screen.subscreen.SearchUserFabSubScreen

data class NavState(
    val tab: String,
    val selectedCaseStudyId: String? = null,
    val selectedPublicProfileUserId: String? = null,
    val inspirationsListUserId: String? = null,
    val inspirationsDefaultTab: Int = 0
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    navTarget: String? = null,
    targetRoomId: String? = null,
    targetPeerUserId: String? = null,
    onNavTargetHandled: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == "hi"
    var activeTab by remember { mutableStateOf("home") }
    var previousTabBeforeSubScreen by remember { mutableStateOf<String?>(null) }
    var selectedCaseStudyId by remember { mutableStateOf<String?>(null) }
    var selectedPublicProfileUserId by remember { mutableStateOf<String?>(null) }
    var inspirationsListUserId by remember { mutableStateOf<String?>(null) }
    var inspirationsDefaultTab by remember { mutableStateOf(0) }
    
    var navigationStack by remember { 
        mutableStateOf(
            listOf(
                NavState(
                    tab = "home",
                    selectedCaseStudyId = null,
                    selectedPublicProfileUserId = null,
                    inspirationsListUserId = null,
                    inspirationsDefaultTab = 0
                )
            )
        )
    }

    fun navigateTo(tab: String) {
        val currentState = NavState(
            tab = activeTab,
            selectedCaseStudyId = selectedCaseStudyId,
            selectedPublicProfileUserId = selectedPublicProfileUserId,
            inspirationsListUserId = inspirationsListUserId,
            inspirationsDefaultTab = inspirationsDefaultTab
        )
        navigationStack = navigationStack + currentState
        activeTab = tab
    }

    fun navigateBack() {
        if (navigationStack.size > 1) {
            val prevState = navigationStack.last()
            navigationStack = navigationStack.dropLast(1)
            
            activeTab = prevState.tab
            selectedCaseStudyId = prevState.selectedCaseStudyId
            selectedPublicProfileUserId = prevState.selectedPublicProfileUserId
            inspirationsListUserId = prevState.inspirationsListUserId
            inspirationsDefaultTab = prevState.inspirationsDefaultTab
        } else {
            activeTab = "home"
            selectedCaseStudyId = null
            selectedPublicProfileUserId = null
            inspirationsListUserId = null
            inspirationsDefaultTab = 0
            navigationStack = listOf(
                NavState(
                    tab = "home",
                    selectedCaseStudyId = null,
                    selectedPublicProfileUserId = null,
                    inspirationsListUserId = null,
                    inspirationsDefaultTab = 0
                )
            )
        }
    }

    fun selectTab(tab: String) {
        activeTab = tab
        selectedCaseStudyId = null
        selectedPublicProfileUserId = null
        inspirationsListUserId = null
        inspirationsDefaultTab = 0
        navigationStack = listOf(
            NavState(
                tab = tab,
                selectedCaseStudyId = null,
                selectedPublicProfileUserId = null,
                inspirationsListUserId = null,
                inspirationsDefaultTab = 0
            )
        )
    }

    LaunchedEffect(navTarget) {
        if (navTarget == "driver_trip") {
            selectTab("institute")
            kotlinx.coroutines.delay(1000)
            onNavTargetHandled()
        }
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { com.vidyasetuai.core.auth.SessionManager(context) }
    val userId = sessionManager.getUserId() ?: ""
    android.util.Log.d("VidyaSetu_Dashboard", "Active userId is: '$userId'")

    val db = remember { com.vidyasetuai.core.database.AppDatabase.getDatabase(context) }
    val repository = remember { com.vidyasetuai.feature_institution.data.repository.InstitutionRepositoryImpl(db.institutionDao()) }
    val institutionViewModel = remember(userId) { InstitutionViewModel(repository, context.applicationContext) }

    val activeSubScreen = institutionViewModel.uiState.value.activeSubScreen
    LaunchedEffect(activeSubScreen) {
        if (activeSubScreen == null && previousTabBeforeSubScreen != null) {
            selectTab(previousTabBeforeSubScreen!!)
            previousTabBeforeSubScreen = null
        }
    }
    var connectionState by remember { mutableStateOf<ConnectionState?>(null) }
    val scope = rememberCoroutineScope()
    var isVerified by remember { mutableStateOf(false) }
    
    val workspacesList by remember { db.institutionDao().getWorkspacesFlow() }
        .collectAsState(initial = emptyList())

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.userProfileDao().getProfileFlow(userId).collect { profile ->
                isVerified = profile?.isVerified == true
            }
        }
    }

    LaunchedEffect(workspacesList) {
        if (workspacesList.isEmpty() && activeTab == "institute") {
            activeTab = "home"
        }
    }

    val experienceViewModel = remember { ExperienceViewModel() }
    
    val profileViewModel = remember(userId) {
        val localDS = ProfileLocalDataSource(db.userProfileDao())
        val remoteDS = ProfileRemoteDataSource()
        val repo = ProfileRepositoryImpl(localDS, remoteDS, context)
        val getProfileUC = GetUserProfileUseCase(repo)
        val updateProfileUC = UpdateUserProfileUseCase(repo)
        val checkUsernameUC = CheckUsernameUniqueUseCase(repo)
        val applyVerificationUC = ApplyForVerificationUseCase(repo)
        ProfileViewModel(getProfileUC, updateProfileUC, checkUsernameUC, applyVerificationUC)
    }
    
    val campusViewModel = remember(userId) {
        val campusDao = db.campusDao()
        val remoteDS = com.vidyasetuai.feature_campus.data.remote.datasource.CampusRemoteDataSource()
        val userProfileDao = db.userProfileDao()
        val profileRemoteDS = com.vidyasetuai.feature_profile.data.remote.datasource.ProfileRemoteDataSource()
        val campusRepo = com.vidyasetuai.feature_campus.data.repository.CampusRepositoryImpl(
            campusDao, remoteDS, userProfileDao, profileRemoteDS, context
        )
        
        val vm = com.vidyasetuai.feature_campus.presentation.viewmodel.CampusViewModel(campusRepo)
        if (userId.isNotEmpty()) {
            vm.onEvent(com.vidyasetuai.feature_campus.presentation.event.CampusEvent.LoadMutualInspirations(userId))
            vm.onEvent(com.vidyasetuai.feature_campus.presentation.event.CampusEvent.InitializeKeys(userId))
        }
        vm
    }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            campusViewModel.onEvent(com.vidyasetuai.feature_campus.presentation.event.CampusEvent.LoadMutualInspirations(userId))
        }
    }

    LaunchedEffect(navTarget, targetPeerUserId, targetRoomId) {
        if (navTarget == "private_chat_room" && targetPeerUserId != null && userId.isNotEmpty()) {
            val peerUser = com.vidyasetuai.feature_profile.domain.model.UserProfile(
                userId = targetPeerUserId!!,
                email = "",
                isActive = true,
                isDeleted = false,
                username = null,
                firstName = "User",
                lastName = null,
                fullName = "User",
                profilePictureUrl = null,
                coverPhotoUrl = null,
                bio = null,
                preferredLanguage = null,
                isVerified = false,
                gender = null,
                dateOfBirth = null
            )
            campusViewModel.onEvent(
                com.vidyasetuai.feature_campus.presentation.event.CampusEvent.OpenPrivateChat(peerUser, userId)
            )
            navigateTo("private_chat_room")
            onNavTargetHandled()
        }
    }

    val caseStudyRepo = remember {
        val remoteDS = com.vidyasetuai.feature_case_study.data.remote.datasource.CaseStudyRemoteDataSource()
        com.vidyasetuai.feature_case_study.data.repository.CaseStudyRepositoryImpl(remoteDS)
    }
    
    val experienceRepo = remember {
        com.vidyasetuai.feature_feed.data.repository.ExperienceRepository()
    }
    
    val quickRepo = remember {
        com.vidyasetuai.feature_case_study.data.repository.QuickRepository()
    }

    var showUploadCaseStudyDialog by remember { mutableStateOf(false) }
    var showUploadExperienceDialog by remember { mutableStateOf(false) }

    val cachedQuicksList = remember { mutableStateOf<List<com.vidyasetuai.feature_case_study.domain.model.Quick>>(emptyList()) }
    val cachedFullFeedList = remember { mutableStateListOf<com.vidyasetuai.feature_case_study.presentation.screen.desbord.FeedItem>() }
    var isFeedLoaded by remember { mutableStateOf(false) }
    val homeFeedListState = androidx.compose.foundation.lazy.rememberLazyListState()
    var quickViewerList by remember { mutableStateOf<List<com.vidyasetuai.feature_case_study.domain.model.Quick>>(emptyList()) }
    var quickViewerSelectedIndex by remember { mutableStateOf(0) }
    
    val cachedFabQuicksList = remember { mutableStateOf<List<com.vidyasetuai.feature_case_study.domain.model.Quick>>(emptyList()) }
    var isFabQuicksLoaded by remember { mutableStateOf(false) }
    var homeTabClickCount by remember { mutableStateOf(0) }

    val profileRemoteDS = remember { com.vidyasetuai.feature_profile.data.remote.datasource.ProfileRemoteDataSource() }
    var checkingVerification by remember { mutableStateOf(false) }
    var showNotVerifiedAlert by remember { mutableStateOf(false) }
    var verificationStatusMessage by remember { mutableStateOf("") }

    fun checkVerificationAndRun(onVerified: () -> Unit) {
        if (userId.isEmpty()) return
        scope.launch {
            checkingVerification = true
            try {
                val verification = profileRemoteDS.getVerification(userId)
                if (verification?.status == "approved") {
                    onVerified()
                } else {
                    verificationStatusMessage = when (verification?.status) {
                        "pending" -> if (isHindi) {
                            "सत्यापन प्रगति पर है: आपका सत्यापन अनुरोध समीक्षा के अधीन है। कृपया समीक्षा पूरी होने की प्रतीक्षा करें।"
                        } else {
                            "Verification in Progress: Your contributor verification is under review. Please wait for approval."
                        }
                        "rejected" -> if (isHindi) {
                            "सत्यापन अस्वीकार: आपका योगदानकर्ता अनुरोध अस्वीकृत कर दिया गया है। (कारण: ${verification.rejection_reason ?: "योग्यता मानदंड पूरे नहीं हुए"}). कृपया प्रोफाइल में जाकर पुनः आवेदन करें।"
                        } else {
                            "Verification Rejected: Your contributor request has been rejected (Reason: ${verification.rejection_reason ?: "Criteria not met"}). Please re-apply in your profile."
                        }
                        "suspended" -> if (isHindi) {
                            "सत्यापन निलंबित: आपका योगदानकर्ता अधिकार निलंबित कर दिया गया है। कृपया सहायता टीम से संपर्क करें।"
                        } else {
                            "Verification Suspended: Your contributor access has been suspended. Please contact support."
                        }
                        else -> if (isHindi) {
                            "सत्यापन आवश्यक: केस स्टडीज या अनुभव पोस्ट करने के लिए कृपया पहले अपनी प्रोफाइल में जाकर सत्यापन (Verification) के लिए आवेदन करें।"
                        } else {
                            "Verification Required: To upload case studies or experiences, please go to your profile and apply for verification first."
                        }
                    }
                    showNotVerifiedAlert = true
                }
            } catch (e: Exception) {
                android.util.Log.e("DashboardScreen", "Error checking verification status", e)
            } finally {
                checkingVerification = false
            }
        }
    }

    fun triggerCheck() {
        if (userId.isNotEmpty()) {
            scope.launch {
                repository.checkConnectionStatus(userId).fold(
                    onSuccess = { state ->
                        connectionState = state
                    },
                    onFailure = { error ->
                        android.util.Log.e("VidyaSetu_Dashboard", "Error checking connection status", error)
                        if (connectionState == null) {
                            connectionState = ConnectionState.NOT_CONNECTED
                        }
                    }
                )
            }
        }
    }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            // 1. First check local Room DB for workspaces or active session to set CONNECTED immediately if cache exists
            val localWorkspaces = db.institutionDao().getWorkspaces()
            val localSession = db.institutionDao().getActiveSession()
            if (localWorkspaces.isNotEmpty() || localSession != null) {
                connectionState = ConnectionState.CONNECTED
            }

            // 2. Fetch and sync workspaces from Supabase in the background so bottom nav updates
            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                repository.getWorkspaces(userId).fold(
                    onSuccess = { list ->
                        android.util.Log.d("VidyaSetu_Dashboard", "Successfully fetched workspaces: ${list.size}")
                    },
                    onFailure = { error ->
                        android.util.Log.e("VidyaSetu_Dashboard", "Error syncing workspaces in background", error)
                    }
                )
            }

            // 3. Keep connection status checking active
            while (true) {
                triggerCheck()
                kotlinx.coroutines.delay(5000)
            }
        }
    }

    fun approveConnection() {
        if (userId.isNotEmpty()) {
            scope.launch {
                repository.approveConnection(userId).fold(
                    onSuccess = {
                        triggerCheck()
                    },
                    onFailure = {
                        // Fail silently
                    }
                )
            }
        }
    }

    if (activeTab != "home") {
        androidx.activity.compose.BackHandler {
            navigateBack()
        }
    }

    var settingsTarget by remember { mutableStateOf<String?>(null) }
    var isEditingProfile by remember { mutableStateOf(false) }
    var isBrowsingTemplatesInJourney by remember { mutableStateOf(false) }
    var showHomeCreateSheet by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val isSubScreenTab = activeTab in listOf(
        "quick_viewer", "case_study_detail", "chat_room", "private_chat_room",
        "settings", "public_profile", "inspirations_list", "tournament", "notifications"
    )

    AnimatedContent(
        targetState = isSubScreenTab,
        transitionSpec = {
            if (targetState) {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(350)) togetherWith
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 4 },
                    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(350))
            } else {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 4 },
                    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(350)) togetherWith
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(350))
            }
        },
        label = "GlobalSubScreenTransition",
        modifier = modifier.fillMaxSize()
    ) { showSubScreen ->
        if (showSubScreen) {
        DashboardSubScreens(
            activeTab = activeTab,
            quickViewerList = quickViewerList,
            quickViewerSelectedIndex = quickViewerSelectedIndex,
            selectedCaseStudyId = selectedCaseStudyId,
            selectedPublicProfileUserId = selectedPublicProfileUserId,
            inspirationsListUserId = inspirationsListUserId,
            inspirationsDefaultTab = inspirationsDefaultTab,
            navigationStack = navigationStack,
            userId = userId,
            currentLanguage = currentLanguage,
            currentTheme = currentTheme,
            quickRepo = quickRepo,
            campusViewModel = campusViewModel,
            onThemeChange = onThemeChange,
            onLanguageChange = onLanguageChange,
            navigateBack = { navigateBack() },
            navigateTo = { tab -> navigateTo(tab) },
            onSelectPublicProfileUser = { clickedId -> selectedPublicProfileUserId = clickedId },
            onSelectCaseStudy = { caseStudyId -> selectedCaseStudyId = caseStudyId },
            onSelectInspirations = { targetId, tabIndex ->
                inspirationsListUserId = targetId
                inspirationsDefaultTab = tabIndex
            },
            onPopNavStackForPublicProfile = { clickedUserId ->
                val prevState = navigationStack.last()
                navigationStack = navigationStack.dropLast(1)
                activeTab = "public_profile"
                selectedPublicProfileUserId = clickedUserId
                selectedCaseStudyId = null
                inspirationsListUserId = null
            },
            initialSettingsTarget = settingsTarget,
            modifier = modifier
        )
    } else {
        val isSubScreenActive = institutionViewModel.uiState.value.activeSubScreen != null || isEditingProfile
        
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = !isSubScreenActive,
            scrimColor = Color.Black.copy(alpha = 0.65f),
            drawerContent = {
                DashboardDrawerContent(
                    activeTab = activeTab,
                    isHindi = isHindi,
                    workspacesList = institutionViewModel.uiState.value.workspaces,
                    activeWorkspace = institutionViewModel.uiState.value.activeWorkspace,
                    onSwitchWorkspace = { space ->
                        institutionViewModel.onEvent(
                            com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.SwitchWorkspace(space.id)
                        )
                    },
                    onOpenSyncCenter = {
                        institutionViewModel.onEvent(
                            com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen("pending_syncs")
                        )
                    },
                    onNavigateToSubScreen = { route ->
                        institutionViewModel.onEvent(
                            com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(route)
                        )
                    },
                    totalUnsyncedCount = institutionViewModel.uiState.value.totalUnsyncedCount,
                    onNavigateToSettings = { target -> 
                        settingsTarget = target
                        navigateTo("settings") 
                    },
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Standard dashboard layout with top bar and bottom navigation bar
                Scaffold(
                    modifier = modifier.fillMaxSize(),
                    topBar = {
                        DashboardTopBar(
                            activeTab = activeTab,
                            isHindi = isHindi,
                            isSubScreenActive = isSubScreenActive,
                            onNavigateToSettings = { navigateTo("settings") },
                            onOpenDrawer = {
                                scope.launch { drawerState.open() }
                            },
                            onForceSyncWorkspace = {
                                institutionViewModel.onEvent(
                                    com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ForceRefreshActiveWorkspace
                                )
                            },
                            onOpenSearchUser = {
                                institutionViewModel.onEvent(
                                    com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen("fab_search_user")
                                )
                            },
                            onEditProfileClick = {
                                isEditingProfile = true
                            },
                            onOpenHomeCreateOptions = {
                                showHomeCreateSheet = true
                            },
                            onOpenAddJourney = {
                                isBrowsingTemplatesInJourney = true
                            },
                            isRefreshingWorkspace = institutionViewModel.uiState.value.isLoading
                        )
                    },
                    bottomBar = {
                        DashboardBottomNav(
                            activeTab = activeTab,
                            workspacesList = workspacesList,
                            isHindi = isHindi,
                            isSubScreenActive = isSubScreenActive,
                            onTabSelected = { tab -> selectTab(tab) },
                            onHomeTabClicked = { homeTabClickCount++ }
                        )
                    },
                    floatingActionButton = {
                        if (institutionViewModel.uiState.value.activeSubScreen == null) {
                            DashboardFloatingActionButton(
                                activeTab = activeTab,
                                role = institutionViewModel.uiState.value.activeWorkspace?.role ?: "",
                                isHindi = isHindi,
                                isDark = when (currentTheme) {
                                    "dark" -> true
                                    "light" -> false
                                    else -> androidx.compose.foundation.isSystemInDarkTheme()
                                },
                                isDrawerOpen = drawerState.isOpen || drawerState.isAnimationRunning,
                                onActionClick = { route, requiresToast, label ->
                                    if (drawerState.isOpen) {
                                        scope.launch { drawerState.close() }
                                    }
                                    if (requiresToast) {
                                        android.widget.Toast.makeText(context, label, android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        when (route) {
                                            "add_case_study" -> {
                                                checkVerificationAndRun {
                                                    showUploadCaseStudyDialog = true
                                                }
                                            }
                                            "add_experience" -> {
                                                checkVerificationAndRun {
                                                    showUploadExperienceDialog = true
                                                }
                                            }
                                            "fab_add_case_study" -> {
                                                checkVerificationAndRun {
                                                    institutionViewModel.onEvent(
                                                        com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(route)
                                                    )
                                                }
                                            }
                                            "fab_add_experience" -> {
                                                institutionViewModel.onEvent(
                                                    com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(route)
                                                )
                                            }
                                            "add_journey" -> {
                                                isBrowsingTemplatesInJourney = true
                                            }
                                            else -> {
                                                val isHomeOrJourneyRoute = route in listOf(
                                                    "fab_add_case_study",
                                                    "fab_add_experience",
                                                    "fab_quicks",
                                                    "fab_add_journey",
                                                    "fab_search_user"
                                                )
                                                if (!isHomeOrJourneyRoute && activeTab != "institute") {
                                                    previousTabBeforeSubScreen = activeTab
                                                    selectTab("institute")
                                                }
                                                institutionViewModel.onEvent(
                                                    com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(route)
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(if (isSubScreenActive) PaddingValues(0.dp) else innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = activeTab,
                            transitionSpec = {
                                val tabOrder = listOf("home", "institute", "journey", "campus", "profile")
                                val initialIndex = tabOrder.indexOf(initialState).let { if (it == -1) 0 else it }
                                val targetIndex = tabOrder.indexOf(targetState).let { if (it == -1) 0 else it }

                                if (targetIndex > initialIndex) {
                                    // Moving Forward (e.g. Home -> Campus): Slide In from Right to Left
                                    slideInHorizontally(
                                        initialOffsetX = { fullWidth -> fullWidth },
                                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                                    ) + fadeIn(tween(durationMillis = 200)) togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> -fullWidth },
                                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                                    ) + fadeOut(tween(durationMillis = 200))
                                } else {
                                    // Moving Backward (e.g. Campus -> Home): Slide In from Left to Right
                                    slideInHorizontally(
                                        initialOffsetX = { fullWidth -> -fullWidth },
                                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                                    ) + fadeIn(tween(durationMillis = 200)) togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> fullWidth },
                                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                                    ) + fadeOut(tween(durationMillis = 200))
                                }
                            },
                            label = "MainTabTransition",
                            modifier = Modifier.fillMaxSize()
                        ) { targetTab ->
                            when (targetTab) {
                    "home" -> {
                        val activeSub = institutionViewModel.uiState.value.activeSubScreen
                        if (activeSub in listOf("fab_add_case_study", "fab_add_experience", "fab_quicks") || activeSub?.startsWith("fab_add_quick:") == true) {
                            val state = institutionViewModel.uiState.value
                            val isDark = when (currentTheme) {
                                "dark" -> true
                                "light" -> false
                                else -> androidx.compose.foundation.isSystemInDarkTheme()
                            }
                            val initialQuickUri = if (activeSub?.startsWith("fab_add_quick:") == true) {
                                activeSub.substringAfter("fab_add_quick:").takeIf { it.isNotBlank() }?.let { android.net.Uri.parse(it) }
                            } else null

                            when {
                                activeSub == "fab_add_case_study" -> AddCaseStudyFabSubScreen(
                                    state = state,
                                    isHindi = isHindi,
                                    isDark = isDark,
                                    userId = userId,
                                    repository = caseStudyRepo,
                                    onBack = { institutionViewModel.onEvent(com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(null)) }
                                )
                                activeSub == "fab_add_experience" -> AddExperienceFabSubScreen(
                                    state = state,
                                    isHindi = isHindi,
                                    isDark = isDark,
                                    userId = userId,
                                    repository = experienceRepo,
                                    onBack = { institutionViewModel.onEvent(com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(null)) }
                                )
                                activeSub == "fab_quicks" || activeSub?.startsWith("fab_add_quick:") == true -> QuicksFabSubScreen(
                                    state = state,
                                    isHindi = isHindi,
                                    isDark = isDark,
                                    userId = userId,
                                    repository = quickRepo,
                                    checkVerification = { onVerified -> checkVerificationAndRun { onVerified() } },
                                    onQuickClick = { list, index ->
                                         quickViewerList = list
                                         quickViewerSelectedIndex = index
                                         navigateTo("quick_viewer")
                                     },
                                    cachedQuicksList = cachedFabQuicksList.value,
                                    onQuicksListChange = { cachedFabQuicksList.value = it },
                                    isQuicksLoaded = isFabQuicksLoaded,
                                    onQuicksLoadedChange = { isFabQuicksLoaded = it },
                                    onBack = { institutionViewModel.onEvent(com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(null)) },
                                    initialImageUri = initialQuickUri
                                )
                            }
                        } else {
                            com.vidyasetuai.feature_case_study.presentation.screen.desbord.HomeFeedDesbord(
                                currentLanguage = currentLanguage,
                                currentTheme = currentTheme,
                                userId = userId,
                                caseStudyRepo = caseStudyRepo,
                                experienceRepo = experienceRepo,
                                quickRepo = quickRepo,
                                onNavigateToSubScreen = { route ->
                                    if (route.startsWith("case_study_detail:")) {
                                        val id = route.substringAfter("case_study_detail:")
                                        selectedCaseStudyId = id
                                        navigateTo("case_study_detail")
                                    } else if (route.startsWith("public_profile:")) {
                                        val authorId = route.substringAfter("public_profile:")
                                        if (authorId.isNotBlank()) {
                                            selectedPublicProfileUserId = authorId
                                            navigateTo("public_profile")
                                        }
                                    } else if (route == "fab_add_case_study" || route == "fab_add_experience") {
                                        checkVerificationAndRun {
                                            institutionViewModel.onEvent(com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(route))
                                        }
                                    } else {
                                        institutionViewModel.onEvent(com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(route))
                                    }
                                },
                                cachedQuicksList = cachedQuicksList.value,
                                onQuicksListChange = { cachedQuicksList.value = it },
                                cachedFullFeedList = cachedFullFeedList,
                                isFeedLoaded = isFeedLoaded,
                                onFeedLoadedChange = { isFeedLoaded = it },
                                onQuickClick = { list, index ->
                                    quickViewerList = list
                                    quickViewerSelectedIndex = index
                                    navigateTo("quick_viewer")
                                },
                                homeTabClickCount = homeTabClickCount,
                                checkVerification = { onVerified -> checkVerificationAndRun { onVerified() } },
                                listState = homeFeedListState,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    "institute" -> InstitutionEvent(
                        connectionState = connectionState,
                        onApprove = { approveConnection() },
                        currentLanguage = currentLanguage,
                        currentTheme = currentTheme,
                        viewModel = institutionViewModel,
                        navTarget = navTarget
                    )
                    "journey" -> {
                        val activeSub = institutionViewModel.uiState.value.activeSubScreen
                        if (activeSub == "fab_add_journey") {
                            val isDark = when (currentTheme) {
                                "dark" -> true
                                "light" -> false
                                else -> androidx.compose.foundation.isSystemInDarkTheme()
                            }
                            AddJourneyFabSubScreen(
                                state = institutionViewModel.uiState.value,
                                isHindi = isHindi,
                                isDark = isDark,
                                onBack = { institutionViewModel.onEvent(com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(null)) }
                            )
                        } else {
                            val db = remember { com.vidyasetuai.core.database.AppDatabase.getDatabase(context) }
                            val localSource = remember { com.vidyasetuai.feature_journey.data.local.datasource.JourneyLocalDataSource(db.journeyDao()) }
                            val remoteSource = remember { com.vidyasetuai.feature_journey.data.remote.datasource.JourneyRemoteDataSource() }
                            val repo = remember { com.vidyasetuai.feature_journey.data.repository.JourneyRepositoryImpl(localSource, remoteSource) }

                            val getAvailableTemplates = remember { com.vidyasetuai.feature_journey.domain.usecase.GetAvailableTemplatesUseCase(repo) }
                            val enroll = remember { com.vidyasetuai.feature_journey.domain.usecase.EnrollInJourneyUseCase(repo) }
                            val getActive = remember { com.vidyasetuai.feature_journey.domain.usecase.GetActiveUserJourneyUseCase(repo) }
                            val getTasks = remember { com.vidyasetuai.feature_journey.domain.usecase.GetDailyTasksUseCase(repo) }
                            val getMcqs = remember { com.vidyasetuai.feature_journey.domain.usecase.GetDailyMCQsUseCase(repo) }
                            val submitTask = remember { com.vidyasetuai.feature_journey.domain.usecase.SubmitTaskProgressUseCase(repo) }
                            val submitMcq = remember { com.vidyasetuai.feature_journey.domain.usecase.SubmitMCQAttemptUseCase(repo) }
                            val getAnalytics = remember { com.vidyasetuai.feature_journey.domain.usecase.GetJourneyAnalyticsUseCase(repo) }
                            val getLeaderboard = remember { com.vidyasetuai.feature_journey.domain.usecase.GetJourneyLeaderboardUseCase(repo) }

                            val journeyViewModel = remember(userId) {
                                com.vidyasetuai.feature_journey.presentation.viewmodel.JourneyViewModel(
                                    userId = userId,
                                    repository = repo,
                                    getAvailableTemplatesUseCase = getAvailableTemplates,
                                    enrollInJourneyUseCase = enroll,
                                    getActiveUserJourneyUseCase = getActive,
                                    getDailyTasksUseCase = getTasks,
                                    getDailyMCQsUseCase = getMcqs,
                                    submitTaskProgressUseCase = submitTask,
                                    submitMCQAttemptUseCase = submitMcq,
                                    getJourneyAnalyticsUseCase = getAnalytics,
                                    getJourneyLeaderboardUseCase = getLeaderboard
                                )
                            }

                            JourneyScreen(
                                viewModel = journeyViewModel,
                                currentLanguage = currentLanguage,
                                currentTheme = currentTheme,
                                isBrowsingTemplates = isBrowsingTemplatesInJourney,
                                onBrowsingTemplatesChange = { isBrowsingTemplatesInJourney = it }
                            )
                        }
                    }
                    "campus" -> {
                        val activeSub = institutionViewModel.uiState.value.activeSubScreen
                        if (activeSub == "fab_search_user") {
                            SearchUserFabSubScreen(
                                isHindi = isHindi,
                                isDark = when (currentTheme) {
                                    "dark" -> true
                                    "light" -> false
                                    else -> androidx.compose.foundation.isSystemInDarkTheme()
                                },
                                currentUserId = userId,
                                onBack = {
                                    institutionViewModel.onEvent(
                                        com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(null)
                                    )
                                },
                                onUserClick = { targetUserId ->
                                    institutionViewModel.onEvent(
                                        com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(null)
                                    )
                                    selectedPublicProfileUserId = targetUserId
                                    navigateTo("public_profile")
                                }
                            )
                        } else {
                        val campusState by campusViewModel.state.collectAsState()
                        CampusScreen(
                            state = campusState,
                            isHindi = isHindi,
                            onPrivateChatClick = { targetUser ->
                                campusViewModel.onEvent(
                                    com.vidyasetuai.feature_campus.presentation.event.CampusEvent.OpenPrivateChat(targetUser, userId)
                                )
                                navigateTo("private_chat_room")
                            },
                            onOpenSearchUserSubScreen = {
                                institutionViewModel.onEvent(
                                    com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen("fab_search_user")
                                )
                            }
                        )
                        }
                    }
                    "profile" -> {
                        val activeSub = institutionViewModel.uiState.value.activeSubScreen
                        if (activeSub == "fab_search_user") {
                            SearchUserFabSubScreen(
                                isHindi = isHindi,
                                isDark = when (currentTheme) {
                                    "dark" -> true
                                    "light" -> false
                                    else -> androidx.compose.foundation.isSystemInDarkTheme()
                                },
                                currentUserId = userId,
                                onBack = {
                                    institutionViewModel.onEvent(
                                        com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(null)
                                    )
                                },
                                onUserClick = { targetUserId ->
                                    institutionViewModel.onEvent(
                                        com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(null)
                                    )
                                    selectedPublicProfileUserId = targetUserId
                                    navigateTo("public_profile")
                                }
                            )
                        } else {
                            ProfileScreen(
                                userId = userId,
                                currentLanguage = currentLanguage,
                                viewModel = profileViewModel,
                                onCaseStudyClick = { caseStudyId ->
                                    selectedCaseStudyId = caseStudyId
                                    navigateTo("case_study_detail")
                                },
                                isEditModeRequested = isEditingProfile,
                                onEditModeChange = { editing ->
                                    isEditingProfile = editing
                                },
                                onInspirationsClick = { targetId, tabIndex ->
                                    inspirationsListUserId = targetId
                                    inspirationsDefaultTab = tabIndex
                                    navigateTo("inspirations_list")
                                }
                            )
                        }
                    }
                }
        }
    }
}
        }
    }
}

        // Render Upload Dialogs
        if (showUploadCaseStudyDialog) {
            UploadCaseStudyDialog(
                userId = userId,
                currentLanguage = currentLanguage,
                onDismiss = { showUploadCaseStudyDialog = false },
                onSubmit = { title, shortDesc, coverUrl, lang, tags, readTime, detailed, contentUrls ->
                    scope.launch {
                        caseStudyRepo.createCaseStudy(
                            title = title,
                            shortDescription = shortDesc,
                            coverImageUrl = coverUrl,
                            language = lang,
                            tags = tags,
                            readTimeMinutes = readTime,
                            detailedContent = detailed,
                            additionalImageUrls = contentUrls,
                            userId = userId
                        )
                        showUploadCaseStudyDialog = false
                        // Refresh the case study list by re-fetching
                        caseStudyRepo.syncCaseStudies(userId)
                    }
                }
            )
        }

        if (showUploadExperienceDialog) {
            UploadExperienceDialog(
                userId = userId,
                currentLanguage = currentLanguage,
                onDismiss = { showUploadExperienceDialog = false },
                onSubmit = { title, description, imageUrl ->
                    experienceViewModel.onEvent(
                        ExperienceEvent.UploadExperience(
                            title = title,
                            description = description,
                            coverImageUrl = imageUrl,
                            authorUserId = userId
                        )
                    )
                    showUploadExperienceDialog = false
                }
            )
        }

        HomeCreateBottomSheet(
            visible = showHomeCreateSheet,
            isHindi = isHindi,
            isDark = when (currentTheme) {
                "dark" -> true
                "light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            },
            onDismiss = { showHomeCreateSheet = false },
            onOptionSelected = { route ->
                checkVerificationAndRun {
                    institutionViewModel.onEvent(
                        com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent.ChangeActiveSubScreen(route)
                    )
                }
            }
        )

        VerificationAlerts(
            checkingVerification = checkingVerification,
            showNotVerifiedAlert = showNotVerifiedAlert,
            verificationStatusMessage = verificationStatusMessage,
            isHindi = isHindi,
            onDismissAlert = { showNotVerifiedAlert = false }
        )
    }
}

@Composable
fun PulsingGreenDot(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier.size(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    alpha = alpha
                )
                .background(AppColors.EmeraldGreen, shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(AppColors.EmeraldGreen, shape = CircleShape)
        )
    }
}

@Composable
fun HomeCreateBottomSheet(
    visible: Boolean,
    isHindi: Boolean,
    isDark: Boolean,
    onDismiss: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(180)),
        exit = fadeOut(tween(220))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.54f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onDismiss()
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(180)),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(180))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(if (isDark) Color(0xFF1C1C1E) else Color.White)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {} // Prevent click through to backdrop scrim
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 14.dp)
                            .width(38.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isDark) Color(0xFF3A3A3C) else Color(0xFFE5E5EA))
                    )

                    Text(
                        text = if (isHindi) "नया कंटेंट बनाएँ" else "Create New Content",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Option 1: Quicks
                    HomeCreateOptionRow(
                        icon = Lucide.Zap,
                        title = if (isHindi) "क्विक्स (Shorts & Video)" else "Quicks (Shorts & Video)",
                        subtitle = if (isHindi) "शॉर्ट वीडियो व क्विक पोस्ट बनाएँ" else "Share quick short videos & posts",
                        iconTint = AppColors.EmeraldGreen,
                        isDark = isDark,
                        onClick = {
                            onDismiss()
                            onOptionSelected("fab_quicks")
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 2: Case Study Upload
                    HomeCreateOptionRow(
                        icon = Lucide.BookOpen,
                        title = if (isHindi) "केस स्टडी (Upload Case Study)" else "Upload Case Study",
                        subtitle = if (isHindi) "विस्तृत केस स्टडी और रिसर्च पब्लिश करें" else "Publish detailed case studies & research",
                        iconTint = Color(0xFF007AFF),
                        isDark = isDark,
                        onClick = {
                            onDismiss()
                            onOptionSelected("fab_add_case_study")
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 3: Experience Share
                    HomeCreateOptionRow(
                        icon = Lucide.Sparkles,
                        title = if (isHindi) "अनुभव शेयर (Share Experience)" else "Share Experience",
                        subtitle = if (isHindi) "अपना अनुभव व सीख शेयर करें" else "Share your experience & learning journey",
                        iconTint = Color(0xFFFF9500),
                        isDark = isDark,
                        onClick = {
                            onDismiss()
                            onOptionSelected("fab_add_experience")
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeCreateOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

