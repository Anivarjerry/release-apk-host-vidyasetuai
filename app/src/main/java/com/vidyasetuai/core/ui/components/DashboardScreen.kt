package com.vidyasetuai.core.ui.components

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.vidyasetuai.feature_campus.presentation.screen.ChatRoomScreen
import com.vidyasetuai.feature_feed.presentation.screen.HomeScreen
import com.vidyasetuai.feature_feed.presentation.screen.InstitutionEvent
import com.vidyasetuai.feature_journey.presentation.screen.JourneyScreen
import com.vidyasetuai.feature_feed.presentation.screen.NotificationEvent
import com.vidyasetuai.feature_profile.presentation.screen.ProfileScreen
import com.vidyasetuai.feature_profile.presentation.screen.PublicProfileScreen
import com.vidyasetuai.feature_profile.presentation.screen.InspirationsListScreen
import com.vidyasetuai.feature_feed.presentation.screen.TournamentEvent
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.vidyasetuai.feature_institution.domain.model.ConnectionState
import com.vidyasetuai.feature_institution.data.repository.InstitutionRepositoryImpl
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel
import androidx.compose.runtime.LaunchedEffect

@Composable
fun DashboardScreen(
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    navTarget: String? = null,
    onNavTargetHandled: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == "hi"
    var activeTab by remember { mutableStateOf("home") }
    var selectedCaseStudyId by remember { mutableStateOf<String?>(null) }
    var selectedPublicProfileUserId by remember { mutableStateOf<String?>(null) }
    var inspirationsListUserId by remember { mutableStateOf<String?>(null) }
    var inspirationsDefaultTab by remember { mutableStateOf(0) }
    var navigationStack by remember { mutableStateOf(listOf("home")) }

    fun navigateTo(tab: String) {
        navigationStack = navigationStack + activeTab
        activeTab = tab
    }

    fun navigateBack() {
        if (navigationStack.isNotEmpty()) {
            val prev = navigationStack.last()
            navigationStack = navigationStack.dropLast(1)
            activeTab = prev
        } else {
            activeTab = "home"
        }
    }

    fun selectTab(tab: String) {
        navigationStack = listOf("home")
        activeTab = tab
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
    var connectionState by remember { mutableStateOf<ConnectionState?>(null) }
    val scope = rememberCoroutineScope()
    var isVerified by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.userProfileDao().getProfileFlow(userId).collect { profile ->
                isVerified = profile?.isVerified == true
            }
        }
    }

    val experienceViewModel = remember { ExperienceViewModel() }
    
    val campusViewModel = remember(userId) {
        val campusDao = db.campusDao()
        val remoteDS = com.vidyasetuai.feature_campus.data.remote.datasource.CampusRemoteDataSource()
        val userProfileDao = db.userProfileDao()
        val profileRemoteDS = com.vidyasetuai.feature_profile.data.remote.datasource.ProfileRemoteDataSource()
        val campusRepo = com.vidyasetuai.feature_campus.data.repository.CampusRepositoryImpl(
            campusDao, remoteDS, userProfileDao, profileRemoteDS
        )
        
        val getRooms = com.vidyasetuai.feature_campus.domain.usecase.GetRoomsUseCase(campusRepo)
        val getMessages = com.vidyasetuai.feature_campus.domain.usecase.GetMessagesUseCase(campusRepo)
        val sendMessage = com.vidyasetuai.feature_campus.domain.usecase.SendMessageUseCase(campusRepo)
        val reportMessage = com.vidyasetuai.feature_campus.domain.usecase.ReportMessageUseCase(campusRepo)
        val loadModerationSettings = com.vidyasetuai.feature_campus.domain.usecase.LoadModerationSettingsUseCase(campusRepo)
        
        com.vidyasetuai.feature_campus.presentation.viewmodel.CampusViewModel(
            getRooms, getMessages, sendMessage, reportMessage, loadModerationSettings
        )
    }

    val caseStudyRepo = remember {
        val localDS = com.vidyasetuai.feature_case_study.data.local.datasource.CaseStudyLocalDataSource(db.caseStudyDao())
        val remoteDS = com.vidyasetuai.feature_case_study.data.remote.datasource.CaseStudyRemoteDataSource()
        com.vidyasetuai.feature_case_study.data.repository.CaseStudyRepositoryImpl(localDS, remoteDS)
    }

    var showUploadCaseStudyDialog by remember { mutableStateOf(false) }
    var showUploadExperienceDialog by remember { mutableStateOf(false) }

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
            // First check local Room DB for workspaces or active session to set CONNECTED immediately if cache exists
            val localWorkspaces = db.institutionDao().getWorkspaces()
            val localSession = db.institutionDao().getActiveSession()
            if (localWorkspaces.isNotEmpty() || localSession != null) {
                connectionState = ConnectionState.CONNECTED
            }

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

    if (activeTab == "case_study_detail") {
        com.vidyasetuai.feature_case_study.presentation.screen.CaseStudyDetailScreen(
            caseStudyId = selectedCaseStudyId ?: "",
            userId = userId,
            onBack = { navigateBack() }
        )
    } else if (activeTab == "chat_room") {
        ChatRoomScreen(
            viewModel = campusViewModel,
            userId = userId,
            currentLanguage = currentLanguage,
            currentTheme = currentTheme,
            onBack = {
                campusViewModel.onEvent(com.vidyasetuai.feature_campus.presentation.event.CampusEvent.CloseActiveRoom)
                navigateBack()
            },
            onUserClick = { clickedUserId ->
                selectedPublicProfileUserId = clickedUserId
                navigateTo("public_profile")
            }
        )
    } else if (activeTab == "settings") {
        SettingsScreen(
            currentTheme = currentTheme,
            onThemeChange = onThemeChange,
            currentLanguage = currentLanguage,
            onLanguageChange = onLanguageChange,
            onBack = { navigateBack() }
        )
    } else if (activeTab == "public_profile") {
        PublicProfileScreen(
            currentUserId = userId,
            targetUserId = selectedPublicProfileUserId ?: "",
            currentLanguage = currentLanguage,
            onBackClick = { navigateBack() },
            onInspirationsClick = { targetId, tabIndex ->
                inspirationsListUserId = targetId
                inspirationsDefaultTab = tabIndex
                navigateTo("inspirations_list")
            },
            onCaseStudyClick = { caseStudyId ->
                selectedCaseStudyId = caseStudyId
                navigateTo("case_study_detail")
            }
        )
    } else if (activeTab == "inspirations_list") {
        InspirationsListScreen(
            currentUserId = userId,
            targetUserId = inspirationsListUserId ?: "",
            initialTab = inspirationsDefaultTab,
            currentLanguage = currentLanguage,
            onBackClick = { navigateBack() },
            onUserClick = { clickedUserId ->
                selectedPublicProfileUserId = clickedUserId
                navigateTo("public_profile")
            }
        )
    } else if (activeTab == "campus" || activeTab == "notifications") {
        // Full screen view with simple top bar (Back button + Title) and NO bottom bar
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
                            text = if (activeTab == "campus") {
                                if (isHindi) "कैंपस" else "Campus"
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
                if (activeTab == "campus") {
                    CampusScreen(
                        viewModel = campusViewModel,
                        userId = userId,
                        currentLanguage = currentLanguage,
                        currentTheme = currentTheme,
                        onRoomClick = { room ->
                            campusViewModel.onEvent(com.vidyasetuai.feature_campus.presentation.event.CampusEvent.OpenRoom(room, userId))
                            navigateTo("chat_room")
                        }
                    )
                } else {
                    NotificationEvent(currentLanguage = currentLanguage, currentTheme = currentTheme)
                }
            }
        }
    } else {
        // Standard dashboard layout with top bar and bottom navigation bar
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                    val topBarTitle = when (activeTab) {
                        "home" -> "VidyaSetu AI"
                        "institute" -> if (isHindi) "संस्थान" else "Institute"
                        "journey" -> if (isHindi) "जर्नी" else "Journey"
                        "tournament" -> if (isHindi) "टूर्नामेंट" else "Tournament"
                        "profile" -> if (isHindi) "प्रोफ़ाइल" else "Profile"
                        else -> "VidyaSetu AI"
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = topBarTitle,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            letterSpacing = (-0.5).sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Chat / Campus Icon with Pulsing Green Dot Highlight
                            Box(contentAlignment = Alignment.TopEnd) {
                                IconButton(
                                    onClick = { navigateTo("campus") },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Lucide.MessageCircle,
                                        contentDescription = "Campus",
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                PulsingGreenDot(
                                    modifier = Modifier
                                        .padding(top = 2.dp, end = 2.dp)
                                        .align(Alignment.TopEnd)
                                    )
                            }
                            IconButton(
                                onClick = { navigateTo("settings") },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .navigationBarsPadding()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1st Tab: Home (App logo only)
                        val isHome = activeTab == "home"
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectTab("home") },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_bridge_logo),
                                contentDescription = "Home",
                                tint = if (isHome) AppColors.EmeraldGreen else Color(0xFF8E8E93),
                                modifier = Modifier.size(width = 24.dp, height = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isHindi) "होम" else "Home",
                                fontSize = 10.sp,
                                fontWeight = if (isHome) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isHome) AppColors.EmeraldGreen else Color(0xFF8E8E93)
                            )
                        }

                        // 2nd Tab: Institute (School icon)
                        val isInst = activeTab == "institute"
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectTab("institute") },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Lucide.School,
                                contentDescription = "Institute",
                                tint = if (isInst) AppColors.EmeraldGreen else Color(0xFF8E8E93),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isHindi) "संस्थान" else "Institute",
                                fontSize = 10.sp,
                                fontWeight = if (isInst) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isInst) AppColors.EmeraldGreen else Color(0xFF8E8E93)
                            )
                        }

                        // 3rd Tab: Journey (Compass icon)
                        val isJourney = activeTab == "journey"
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectTab("journey") },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Compass,
                                contentDescription = "Journey",
                                tint = if (isJourney) AppColors.EmeraldGreen else Color(0xFF8E8E93),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isHindi) "जर्नी" else "Journey",
                                fontSize = 10.sp,
                                fontWeight = if (isJourney) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isJourney) AppColors.EmeraldGreen else Color(0xFF8E8E93)
                            )
                        }

                        // 4th Tab: Tournament (Trophy icon)
                        val isTourney = activeTab == "tournament"
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectTab("tournament") },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Trophy,
                                contentDescription = "Tournament",
                                tint = if (isTourney) AppColors.EmeraldGreen else Color(0xFF8E8E93),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isHindi) "टूर्नामेंट" else "Tournament",
                                fontSize = 10.sp,
                                fontWeight = if (isTourney) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isTourney) AppColors.EmeraldGreen else Color(0xFF8E8E93)
                            )
                        }

                        // 5th Tab: Profile (User icon)
                        val isProfile = activeTab == "profile"
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectTab("profile") },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Lucide.User,
                                contentDescription = "Profile",
                                tint = if (isProfile) AppColors.EmeraldGreen else Color(0xFF8E8E93),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isHindi) "प्रोफ़ाइल" else "Profile",
                                fontSize = 10.sp,
                                fontWeight = if (isProfile) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isProfile) AppColors.EmeraldGreen else Color(0xFF8E8E93)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                when (activeTab) {
                    "home" -> HomeScreen(
                        currentLanguage = currentLanguage,
                        experienceViewModel = experienceViewModel,
                        onCaseStudyClick = { caseStudyId ->
                            selectedCaseStudyId = caseStudyId
                            navigateTo("case_study_detail")
                        },
                        onExploreClick = {
                            // Handled inside CaseStudyListScreen onExploreClick
                        },
                        onUploadCaseStudyClick = {
                            checkVerificationAndRun {
                                showUploadCaseStudyDialog = true
                            }
                        },
                        onUploadExperienceClick = {
                            checkVerificationAndRun {
                                showUploadExperienceDialog = true
                            }
                        },
                        onAuthorClick = { authorUserId ->
                            selectedPublicProfileUserId = authorUserId
                            navigateTo("public_profile")
                        }
                    )
                    "institute" -> InstitutionEvent(
                        connectionState = connectionState,
                        onApprove = { approveConnection() },
                        currentLanguage = currentLanguage,
                        currentTheme = currentTheme,
                        viewModel = institutionViewModel,
                        navTarget = navTarget
                    )
                    "journey" -> {
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
                            currentTheme = currentTheme
                        )
                    }
                    "tournament" -> TournamentEvent(currentLanguage = currentLanguage, currentTheme = currentTheme)
                    "profile" -> ProfileScreen(
                        userId = userId,
                        currentLanguage = currentLanguage,
                        onCaseStudyClick = { caseStudyId ->
                            selectedCaseStudyId = caseStudyId
                            navigateTo("case_study_detail")
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

        if (checkingVerification) {
            Dialog(onDismissRequest = {}) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(100.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(color = AppColors.EmeraldGreen)
                    }
                }
            }
        }

        if (showNotVerifiedAlert) {
            Dialog(onDismissRequest = { showNotVerifiedAlert = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Lucide.CircleAlert,
                            contentDescription = "Alert",
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isHindi) "सत्यापन आवश्यक" else "Verification Required",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = verificationStatusMessage,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showNotVerifiedAlert = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.EmeraldGreen
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Text(
                                text = if (isHindi) "ठीक है" else "OK",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
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

private fun processSquareImage(context: android.content.Context, uri: Uri): ByteArray? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
            val width = originalBitmap.width
            val height = originalBitmap.height
            val squareSize = if (width < height) width else height
            val x = (width - squareSize) / 2
            val y = (height - squareSize) / 2
            
            val cropped = Bitmap.createBitmap(originalBitmap, x, y, squareSize, squareSize)
            val scaled = Bitmap.createScaledBitmap(cropped, 600, 600, true)
            
            val outputStream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val bytes = outputStream.toByteArray()
            
            if (cropped != scaled) cropped.recycle()
            if (originalBitmap != cropped) originalBitmap.recycle()
            scaled.recycle()
            
            bytes
        }
    } catch (e: Exception) {
        android.util.Log.e("DashboardScreen", "Error processing square image", e)
        null
    }
}

@Composable
fun UploadCaseStudyDialog(
    userId: String,
    currentLanguage: String,
    onDismiss: () -> Unit,
    onSubmit: (
        title: String,
        shortDescription: String,
        coverImageUrl: String,
        language: String,
        tags: List<String>,
        readTimeMinutes: Int?,
        detailedContent: String,
        additionalImageUrls: List<String>
    ) -> Unit
) {
    val isHindi = currentLanguage == "hi"
    var title by remember { mutableStateOf("") }
    var shortDescription by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("hindi") } // "hindi", "english", "bilingual"
    var tagsInput by remember { mutableStateOf("") }
    var readTimeInput by remember { mutableStateOf("") }
    var detailedContent by remember { mutableStateOf("") }
    
    var coverImageUri by remember { mutableStateOf<Uri?>(null) }
    var additionalImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    var isUploading by remember { mutableStateOf(false) }
    var uploadStatusText by remember { mutableStateOf("") }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        coverImageUri = uri
    }
    
    val additionalPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (additionalImageUris.size < 4) {
                additionalImageUris = additionalImageUris + it
            }
        }
    }

    val handleFormSubmit = {
        if (title.isNotBlank() && shortDescription.isNotBlank() && detailedContent.isNotBlank() && coverImageUri != null) {
            scope.launch {
                isUploading = true
                try {
                    // 1. Process and upload cover photo
                    uploadStatusText = if (isHindi) "कवर फोटो अपलोड हो रही है..." else "Uploading cover photo..."
                    val coverBytes = processSquareImage(context, coverImageUri!!)
                    if (coverBytes == null) {
                        throw Exception("Failed to process cover image")
                    }
                    val coverFileName = "covers/cover_${System.currentTimeMillis()}_${java.util.UUID.randomUUID().toString().take(4)}.jpg"
                    val coverUrl = SupabaseStorageHelper.uploadImage("case-study-images", coverFileName, coverBytes)
                    
                    // 2. Process and upload additional content photos
                    val contentUrls = mutableListOf<String>()
                    additionalImageUris.forEachIndexed { index, uri ->
                        uploadStatusText = if (isHindi) {
                            "अतिरिक्त चित्र अपलोड हो रहा है (${index + 1}/${additionalImageUris.size})..."
                        } else {
                            "Uploading additional image (${index + 1}/${additionalImageUris.size})..."
                        }
                        val imageBytes = processSquareImage(context, uri)
                        if (imageBytes != null) {
                            val contentFileName = "content/content_${System.currentTimeMillis()}_${index}_${java.util.UUID.randomUUID().toString().take(4)}.jpg"
                            val contentUrl = SupabaseStorageHelper.uploadImage("case-study-images", contentFileName, imageBytes)
                            contentUrls.add(contentUrl)
                        }
                    }
                    
                    // 3. Process tags
                    val tagsList = tagsInput.split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        
                    // 4. Process read time
                    val readTime = readTimeInput.toIntOrNull()
                    
                    uploadStatusText = if (isHindi) "केस स्टडी जमा हो रही है..." else "Submitting case study..."
                    onSubmit(
                        title,
                        shortDescription,
                        coverUrl,
                        selectedLanguage,
                        tagsList,
                        readTime,
                        detailedContent,
                        contentUrls
                    )
                } catch (e: Exception) {
                    android.util.Log.e("UploadCaseStudy", "Error uploading case study", e)
                } finally {
                    isUploading = false
                    uploadStatusText = ""
                }
            }
        }
    }

    Dialog(onDismissRequest = { if (!isUploading) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Text(
                        text = if (isHindi) "केस स्टडी अपलोड करें" else "Upload Case Study",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Cover Image Selection Card
                    Text(
                        text = if (isHindi) "कवर फोटो (Cover Photo) *" else "Cover Photo *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(
                                MaterialTheme.colorScheme.background,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable(enabled = !isUploading) { coverPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (coverImageUri != null) {
                            AsyncImage(
                                model = coverImageUri,
                                contentDescription = "Cover Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f))
                            )
                            Text(
                                text = if (isHindi) "कवर बदलें" else "Change Cover",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Lucide.Plus,
                                    contentDescription = "Add Cover",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isHindi) "मुख्य कवर फोटो चुनें (1:1 क्रॉप होगी)" else "Choose Cover Photo (will crop 1:1)",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Title Field
                    Text(
                        text = if (isHindi) "शीर्षक (Title) *" else "Title *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicTextField(
                        value = title,
                        onValueChange = { if (it.length <= 200) title = it },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp),
                        enabled = !isUploading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        decorationBox = { innerTextField ->
                            if (title.isEmpty()) {
                                Text(
                                    text = if (isHindi) "शीर्षक दर्ज करें..." else "Enter title...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Short Description Field
                    Text(
                        text = if (isHindi) "संक्षिप्त विवरण (Short Description) *" else "Short Description *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicTextField(
                        value = shortDescription,
                        onValueChange = { if (it.length <= 300) shortDescription = it },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp),
                        enabled = !isUploading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        decorationBox = { innerTextField ->
                            if (shortDescription.isEmpty()) {
                                Text(
                                    text = if (isHindi) "संक्षिप्त विवरण दर्ज करें (अधिकतम 300 वर्ण)..." else "Enter short description (max 300 chars)...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Language Choice (Hindi / English / Bilingual)
                    Text(
                        text = if (isHindi) "भाषा (Language) *" else "Language *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("hindi", "english", "bilingual").forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            val label = when (lang) {
                                "hindi" -> if (isHindi) "हिंदी" else "Hindi"
                                "english" -> if (isHindi) "अंग्रेजी" else "English"
                                else -> if (isHindi) "द्विभाषी" else "Bilingual"
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.background,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .clickable(enabled = !isUploading) { selectedLanguage = lang }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Tags Input (comma separated)
                    Text(
                        text = if (isHindi) "टैग (Tags) - अल्पविराम (comma) से अलग करें" else "Tags - separated by comma",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicTextField(
                        value = tagsInput,
                        onValueChange = { tagsInput = it },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp),
                        enabled = !isUploading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        decorationBox = { innerTextField ->
                            if (tagsInput.isEmpty()) {
                                Text(
                                    text = if (isHindi) "उदा. Education, AI, Tech" else "e.g. Education, AI, Tech",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Read Time Minutes (numeric)
                    Text(
                        text = if (isHindi) "पढ़ने का समय (मिनट में)" else "Read Time (in minutes)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicTextField(
                        value = readTimeInput,
                        onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) readTimeInput = it },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp),
                        enabled = !isUploading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        decorationBox = { innerTextField ->
                            if (readTimeInput.isEmpty()) {
                                Text(
                                    text = if (isHindi) "उदा. 5" else "e.g. 5",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Detailed Content (multiline text area)
                    Text(
                        text = if (isHindi) "विस्तृत सामग्री (Detailed Content) *" else "Detailed Content *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicTextField(
                        value = detailedContent,
                        onValueChange = { detailedContent = it },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp),
                        enabled = !isUploading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        decorationBox = { innerTextField ->
                            if (detailedContent.isEmpty()) {
                                Text(
                                    text = if (isHindi) "केस स्टडी का मुख्य विवरण यहाँ लिखें..." else "Write main case study detailed content here...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Additional Images (max 4, horizontal thumbnails + plus card)
                    Text(
                        text = if (isHindi) "अतिरिक्त चित्र (अधिक्तम 4)" else "Additional Images (max 4)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        additionalImageUris.forEachIndexed { idx, uri ->
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Content Image $idx",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .background(Color.Red.copy(alpha = 0.7f), CircleShape)
                                        .clickable(enabled = !isUploading) {
                                            additionalImageUris = additionalImageUris.filterIndexed { index, _ -> index != idx }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "×", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 2.dp))
                                }
                            }
                        }
                        if (additionalImageUris.size < 4) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                    .clickable(enabled = !isUploading) { additionalPickerLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Plus,
                                    contentDescription = "Add Content Image",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(28.dp))
                    
                    // Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            enabled = !isUploading,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = if (isHindi) "रद्द करें" else "Cancel")
                        }
                        Button(
                            onClick = handleFormSubmit,
                            enabled = title.isNotBlank() && shortDescription.isNotBlank() && detailedContent.isNotBlank() && coverImageUri != null && !isUploading,
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(text = if (isHindi) "सबमिट करें" else "Submit", color = Color.White)
                        }
                    }
                }
                
                // Loading Overlay inside the Dialog
                if (isUploading) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(enabled = false) {},
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = AppColors.EmeraldGreen)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = uploadStatusText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UploadExperienceDialog(
    userId: String,
    currentLanguage: String,
    onDismiss: () -> Unit,
    onSubmit: (title: String, description: String, imageUrl: String?) -> Unit
) {
    val isHindi = currentLanguage == "hi"
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var coverImageUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            if (bytes != null) {
                val fileName = "experience_${System.currentTimeMillis()}.jpg"
                scope.launch {
                    isUploading = true
                    try {
                        val publicUrl = SupabaseStorageHelper.uploadImage("users_cover_image", fileName, bytes)
                        coverImageUrl = publicUrl
                    } catch (e: Exception) {
                        android.util.Log.e("UploadDialog", "Failed to upload experience image", e)
                    } finally {
                        isUploading = false
                    }
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isHindi) "अनुभव साझा करें" else "Share Experience",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title Field
                Text(
                    text = if (isHindi) "शीर्षक (Title)" else "Title",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp))
                        .padding(10.dp),
                    decorationBox = { innerTextField ->
                        if (title.isEmpty()) {
                            Text(
                                text = if (isHindi) "अपना अनुभव का शीर्षक लिखें..." else "Enter title...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Description Field
                Text(
                    text = if (isHindi) "विवरण (Description)" else "Description",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                BasicTextField(
                    value = description,
                    onValueChange = { description = it },
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp))
                        .padding(10.dp),
                    decorationBox = { innerTextField ->
                        if (description.isEmpty()) {
                            Text(
                                text = if (isHindi) "विवरण लिखें..." else "Enter description...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Image Upload Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { pickerLauncher.launch("image/*") },
                        enabled = !isUploading,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isUploading) {
                                if (isHindi) "अपलोड हो रहा है..." else "Uploading..."
                            } else {
                                if (isHindi) "कवर फोटो चुनें (वैकल्पिक)" else "Choose Cover Photo (Optional)"
                            }
                        )
                    }
                    if (coverImageUrl != null) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Lucide.Check,
                            contentDescription = "Uploaded",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = if (isHindi) "रद्द करें" else "Cancel")
                    }
                    Button(
                        onClick = { onSubmit(title, description, coverImageUrl) },
                        enabled = title.isNotBlank() && description.isNotBlank() && !isUploading,
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text(text = if (isHindi) "साझा करें" else "Share", color = Color.White)
                    }
                }
            }
        }
    }
}
