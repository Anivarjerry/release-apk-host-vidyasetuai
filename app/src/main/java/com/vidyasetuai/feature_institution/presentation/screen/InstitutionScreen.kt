package com.vidyasetuai.feature_feed.presentation.screen

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_institution.presentation.screen.AllLogsScreen
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.domain.model.ConnectionState
import com.vidyasetuai.feature_institution.domain.model.Workspace
import com.vidyasetuai.feature_institution.domain.model.Leave
import com.vidyasetuai.feature_institution.domain.model.FeePayment
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.ChildProfilesSubScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.StudentHomeLocationDetailSubScreen
import com.vidyasetuai.feature_institution.domain.model.InstitutionStudent
import com.vidyasetuai.feature_institution.domain.model.PendingApproval
import com.vidyasetuai.feature_institution.domain.model.ContentFeedItem
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.DriverStudentAttendanceScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.DriverStudentAttendanceScreenHistory
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.RemarkShowScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.RemarkAddScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.ApplyLeaveFabSubScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.AddRemarksFabSubScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.PendingSyncsSubScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.TakeAttendanceFabSubScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.CollectFeeFabSubScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.AddFinanceFabSubScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.StartTripFabSubScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.AddCaseStudyFabSubScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.AddExperienceFabSubScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.AddJourneyFabSubScreen
import com.vidyasetuai.feature_institution.presentation.screen.subscreens.QuicksFabSubScreen
import com.vidyasetuai.feature_institution.presentation.screen.dashboards.*
import com.vidyasetuai.feature_institution.presentation.component.DashboardFloatingActionButton
import com.vidyasetuai.feature_institution.util.DashboardFabRules
import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.vidyasetuai.feature_institution.data.local.service.LocationTrackingService
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import com.vidyasetuai.feature_profile.data.remote.dto.UserProfileDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import coil.compose.AsyncImage

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InstitutionEvent(
    connectionState: ConnectionState?,
    onApprove: () -> Unit,
    currentLanguage: String,
    currentTheme: String = "system",
    viewModel: InstitutionViewModel,
    navTarget: String? = null,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == "hi"
    val isDark = when (currentTheme) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { com.vidyasetuai.core.auth.SessionManager(context) }
    val userId = sessionManager.getUserId() ?: ""

    val db = remember { com.vidyasetuai.core.database.AppDatabase.getDatabase(context) }
    val state = viewModel.uiState.value

    val profileState = remember(userId) { db.userProfileDao().getProfileFlow(userId) }
        .collectAsState(initial = null)
    var username by remember { mutableStateOf(userId) }

    LaunchedEffect(userId) {
        if (userId.isEmpty()) return@LaunchedEffect
        
        // 1. Check local DB first to prevent redundant network requests
        val cachedProfile = db.userProfileDao().getProfile(userId)
        if (cachedProfile != null && !cachedProfile.username.isNullOrEmpty()) {
            username = cachedProfile.username
            return@LaunchedEffect
        }
        
        // 2. Fetch from network only if missing locally
        try {
            val response = com.vidyasetuai.core.network.SupabaseClient.client.from("user_profiles")
                .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("user_id, username")) {
                    filter { eq("user_id", userId) }
                }.decodeSingleOrNull<UserProfileDto>()
            if (response?.username != null) {
                username = response.username
                db.userProfileDao().insertProfile(
                    com.vidyasetuai.feature_profile.data.local.entity.UserProfileEntity(
                        userId = userId,
                        username = response.username,
                        firstName = null,
                        lastName = null,
                        fullName = null,
                        profilePictureUrl = null,
                        coverPhotoUrl = null,
                        bio = null,
                        preferredLanguage = null,
                        isVerified = false,
                        gender = null,
                        dateOfBirth = null
                    )
                )
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error fetching profile from Supabase", e)
        }
    }

    var lastConnectionState by remember { mutableStateOf<ConnectionState?>(null) }
    LaunchedEffect(userId, connectionState, navTarget) {
        if (userId.isNotEmpty() && connectionState is ConnectionState.CONNECTED && lastConnectionState != connectionState) {
            viewModel.onEvent(InstitutionEvent.LoadWorkspaces(userId, navTarget))
            lastConnectionState = connectionState
        } else if (connectionState != ConnectionState.CONNECTED) {
            lastConnectionState = connectionState
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (connectionState == null) {
            SkeletonLoader(isDark = isDark)
        } else {
            when (connectionState) {
                is ConnectionState.NOT_CONNECTED -> {
                    NotConnectedView(isHindi = isHindi, isDark = isDark, username = username)
                }
                is ConnectionState.PENDING -> {
                    PendingConnectionView(
                        pendingData = connectionState,
                        onApprove = onApprove,
                        isHindi = isHindi,
                        isDark = isDark
                    )
                }
                is ConnectionState.CONNECTED -> {
                    WorkspaceContainer(
                        state = state,
                        isHindi = isHindi,
                        isDark = isDark,
                        userId = userId,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun NotConnectedView(isHindi: Boolean, isDark: Boolean, username: String) {
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val cardBorder = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
            color = cardBg
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(if (isDark) Color(0xFF25352E) else Color(0xFFF4F6F5), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Building2,
                        contentDescription = null,
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isHindi) "संस्थान से जुड़ें" else "Connect to Institution",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isHindi) {
                        "सुरक्षा कारणों से आप स्वयं किसी स्कूल या कोचिंग से नहीं जुड़ सकते। कृपया अपना यूज़रनेम (Username) अपनी संस्था में जमा करें।"
                    } else {
                        "For security reasons, you cannot link to a school or coaching directly. Please submit your username to your institution."
                    },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isHindi) "आपका यूज़रनेम:" else "Your Username:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = username,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.EmeraldGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PendingConnectionView(
    pendingData: ConnectionState.PENDING,
    onApprove: () -> Unit,
    isHindi: Boolean,
    isDark: Boolean
) {
    var isApproving by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "approvalPulse")
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        val badgeBg = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)
        val badgeBorder = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

        Row(
            modifier = Modifier
                .background(badgeBg, shape = RoundedCornerShape(100.dp))
                .border(0.5.dp, badgeBorder, shape = RoundedCornerShape(100.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha)
                        .background(AppColors.EmeraldGreen, shape = CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(AppColors.EmeraldGreen, shape = CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "कनेक्शन की प्रतीक्षा है" else "Awaiting Connection",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (isHindi) "आपका कनेक्शन अनुरोध लंबित है। कृपया स्वीकृत करने से पहले नीचे दिए गए प्रोफ़ाइल विवरण को सत्यापित करें।" else "Your connection request is pending. Please verify the profile details below before approving.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
        val cardBorder = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, cardBorder, shape = RoundedCornerShape(12.dp))
                .background(cardBg, shape = RoundedCornerShape(12.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val initials = pendingData.targetName.take(2).uppercase()
            val avatarBg = if (isDark) Color(0xFF25352E) else Color(0xFFF4F6F5)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(avatarBg, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.EmeraldGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = pendingData.targetName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            val roleText = when (pendingData.role) {
                "Guardian" -> if (isHindi) "अभिभावक (Guardian)" else "Guardian"
                "Student" -> if (isHindi) "छात्र (Student)" else "Student"
                "Staff" -> if (isHindi) "कर्मचारी (Staff)" else "Staff"
                else -> pendingData.role
            }

            Text(
                text = if (isHindi) "$roleText • ${pendingData.personName} के रूप में जुड़ रहे हैं" else "$roleText • Connecting as ${pendingData.personName}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            val hasDetails = !pendingData.email.isNullOrBlank() ||
                             !pendingData.mobileNumber.isNullOrBlank() ||
                             !pendingData.websiteUrl.isNullOrBlank() ||
                             !pendingData.address.isNullOrBlank()

            if (hasDetails) {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (!pendingData.email.isNullOrBlank()) {
                        ProfileDetailRow(
                            icon = Lucide.Mail,
                            label = if (isHindi) "ईमेल" else "Email",
                            value = pendingData.email
                        )
                    }
                    if (!pendingData.mobileNumber.isNullOrBlank()) {
                        ProfileDetailRow(
                            icon = Lucide.Phone,
                            label = if (isHindi) "फ़ोन" else "Phone",
                            value = pendingData.mobileNumber
                        )
                    }
                    if (!pendingData.websiteUrl.isNullOrBlank()) {
                        ProfileDetailRow(
                            icon = Lucide.Globe,
                            label = if (isHindi) "वेबसाइट" else "Website",
                            value = pendingData.websiteUrl
                        )
                    }
                    if (!pendingData.address.isNullOrBlank()) {
                        ProfileDetailRow(
                            icon = Lucide.MapPin,
                            label = if (isHindi) "पता" else "Address",
                            value = pendingData.address
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isApproving = true
                onApprove()
            },
            enabled = !isApproving,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.EmeraldGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(21.dp),
            modifier = Modifier
                .width(180.dp)
                .height(42.dp)
        ) {
            Text(
                text = if (isApproving) {
                    if (isHindi) "स्वीकार किया जा रहा है..." else "Processing..."
                } else {
                    if (isHindi) "स्वीकार करें" else "Approve"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun SkeletonLoader(isDark: Boolean) {
    val shimColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(shimColor)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(shimColor)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(shimColor)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkspaceContainer(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    userId: String,
    viewModel: InstitutionViewModel
) {
    var showSwitcher by remember { mutableStateOf(false) }
    val activeSubScreen = state.activeSubScreen
    var selectedBusId by remember { mutableStateOf<String?>(null) }
    var selectedPaymentReceipt by remember { mutableStateOf<com.vidyasetuai.feature_institution.domain.model.StaffSalaryPayment?>(null) }
    var selectedFeedItem by remember { mutableStateOf<ContentFeedItem?>(null) }
    var selectedStudentId by remember { mutableStateOf<String?>(null) }

    // Back handler: intercept Android system back button when a subscreen is active
    BackHandler(enabled = activeSubScreen != null) {
        when (activeSubScreen) {
            // Nested screens go to their direct parent
            "live_bus" -> viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("transport"))
            "student_details" -> viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("student_directory"))
            "driver_student_attendance_history" -> viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("driver_student_attendance"))
            "full_content_feed" -> viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("content_feed"))
            "remarks_add" -> viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("remarks_show"))
            "child_profile_detail" -> viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("child_profiles_list"))
            // All first-level subscreens go back to the Institute main view
            else -> {
                // Clear any active attendance filters when leaving attendance screens
                if (activeSubScreen == "take_attendance" || activeSubScreen == "teacher_attendance_history") {
                    viewModel.onEvent(InstitutionEvent.ClearAttendanceFilters)
                }
                viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = AppColors.EmeraldGreen,
                trackColor = Color.Transparent
            )
        } else {
            Spacer(modifier = Modifier.height(2.dp))
        }

        if (state.isLoading && activeSubScreen == null) {
            SkeletonLoader(isDark = isDark)
        } else {
            if (activeSubScreen == null) {
                // Workspace Top Bar Switcher Pill
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    state.activeWorkspace?.let { active ->
                        val hasPending = state.pendingApprovals.isNotEmpty()
                        val canSwitch = state.workspaces.size > 1 || hasPending

                        Box {
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isDark) Color(0xFF1E1E20) else Color(0xFFF2F2F7),
                                border = androidx.compose.foundation.BorderStroke(
                                    0.5.dp,
                                    if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
                                ),
                                modifier = Modifier.clickable(enabled = canSwitch) {
                                    showSwitcher = true
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${active.childOrgName ?: active.parentOrgName} • ${if (isHindi) mapRoleHi(active.role) else active.role}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    if (canSwitch) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Lucide.ChevronDown,
                                            contentDescription = "Switch Workspace",
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Red badge if pending approvals exist
                            if (hasPending) {
                                val badgeInfinite = rememberInfiniteTransition(label = "badgePulse")
                                val badgeScale by badgeInfinite.animateFloat(
                                    initialValue = 1f,
                                    targetValue = 1.3f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(900, easing = LinearEasing),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "badgeScale"
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 6.dp, y = (-6).dp)
                                        .graphicsLayer(scaleX = badgeScale, scaleY = badgeScale)
                                        .background(Color(0xFFFF3B30), CircleShape)
                                        .padding(horizontal = 5.dp, vertical = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${state.pendingApprovals.size}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        lineHeight = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    // Sync Center Cloud Icon (on the right)
                    IconButton(
                        onClick = {
                            viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("pending_syncs"))
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = Lucide.Cloud,
                                contentDescription = "Sync Center",
                                tint = if (state.totalUnsyncedCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(24.dp)
                            )
                            if (state.totalUnsyncedCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .offset(x = 2.dp, y = (-2).dp)
                                        .background(Color.Red, CircleShape)
                                        .size(8.dp)
                                )
                            }
                        }
                    }
                }

                // Quick Action Scrollable Icons Row
                state.activeWorkspace?.let { active ->
                    DashboardQuickActionsRow(
                        role = active.role,
                        isHindi = isHindi,
                        onNavigateToStudents = {
                            viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("student_directory"))
                        },
                        onNavigateToLeave = {
                            viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("leave"))
                        },
                        onNavigateToRemarks = {
                            viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("remarks_show"))
                        },
                        onNavigateToFees = {
                            viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("fees"))
                        },
                        onNavigateToFinance = {
                            viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("salary_payouts"))
                        },
                        onNavigateToTransport = {
                            viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("transport"))
                        },
                        onNavigateToNotices = {
                            viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("notices"))
                        },
                        onNavigateToGallery = {
                            viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("gallery"))
                        }
                    )
                }

                // Active Workspace Dashboard
                Box(modifier = Modifier.weight(1f)) {
                    if (state.activeWorkspace != null) {
                         when (state.activeWorkspace.role) {
                            "Guardian" -> GuardianDashboard(
                                state = state,
                                isHindi = isHindi,
                                isDark = isDark,
                                viewModel = viewModel,
                                userId = userId,
                                onNavigateToLeave = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("leave")) },
                                onNavigateToFees = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("fees")) },
                                onNavigateToAttendance = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("attendance")) },
                                onNavigateToTransport = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("transport")) },
                                onNavigateToFeed = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("content_feed")) },
                                onNavigateToRemarks = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("remarks_show")) },
                                onNavigateToChildProfiles = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("child_profiles_list")) },
                                onNavigateToAllLogs = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("all_logs")) }
                            )
                            "Student" -> StudentDashboard(
                                state = state,
                                isHindi = isHindi,
                                isDark = isDark,
                                viewModel = viewModel,
                                userId = userId,
                                onNavigateToLeave = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("leave")) },
                                onNavigateToFeed = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("content_feed")) },
                                onNavigateToRemarks = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("remarks_show")) },
                                onNavigateToSelfProfile = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("student_profile_detail")) },
                                onNavigateToTransport = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("transport")) },
                                onNavigateToAllLogs = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("all_logs")) }
                            )
                            "Teacher" -> TeacherDashboard(
                                state = state,
                                isHindi = isHindi,
                                isDark = isDark,
                                viewModel = viewModel,
                                userId = userId,
                                onNavigateToLeave = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("leave")) },
                                onNavigateToTakeAttendance = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("take_attendance")) },
                                onNavigateToAttendanceHistory = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("teacher_attendance_history")) },
                                onNavigateToSalary = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("salary_payouts")) },
                                onNavigateToFeed = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("content_feed")) },
                                onNavigateToRemarks = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("remarks_show")) },
                                onNavigateToAllLogs = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("all_logs")) }
                            )
                            "Driver" -> DriverDashboard(
                                state = state,
                                isHindi = isHindi,
                                isDark = isDark,
                                viewModel = viewModel,
                                userId = userId,
                                onNavigateToLeave = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("leave")) },
                                onNavigateToSalary = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("salary_payouts")) },
                                onNavigateToFeed = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("content_feed")) },
                                onNavigateToDriverStudentAttendance = {
                                    viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("driver_student_attendance"))
                                    viewModel.onEvent(InstitutionEvent.LoadActiveTrip(userId))
                                },
                                onNavigateToRemarks = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("remarks_show")) },
                                onNavigateToAllLogs = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("all_logs")) }
                            )
                            "Admin", "System Administrator", "School Administrator", "Org Admin", "Principal", "Director", "Owner" -> AdminDashboard(
                                state = state,
                                isHindi = isHindi,
                                isDark = isDark,
                                onNavigateToStudentDirectory = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("student_directory")) },
                                onNavigateToFeed = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("content_feed")) },
                                onNavigateToRemarks = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("remarks_show")) },
                                onNavigateToTransport = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("transport")) },
                                onNavigateToAllLogs = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("all_logs")) }
                            )
                            else -> TeacherDashboard(
                                state = state,
                                isHindi = isHindi,
                                isDark = isDark,
                                viewModel = viewModel,
                                userId = userId,
                                onNavigateToLeave = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("leave")) },
                                onNavigateToTakeAttendance = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("take_attendance")) },
                                onNavigateToAttendanceHistory = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("teacher_attendance_history")) },
                                onNavigateToSalary = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("salary_payouts")) },
                                onNavigateToFeed = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("content_feed")) },
                                onNavigateToRemarks = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("remarks_show")) },
                                onNavigateToAllLogs = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("all_logs")) }
                            )
                        }
                    } else {
                        NotConnectedView(isHindi = isHindi, isDark = isDark, username = "username")
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f)) {
                    when (activeSubScreen) {
                        "leave" -> LeaveSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            userId = userId,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "fees" -> CombinedFeesDetailSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "attendance" -> AttendanceSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "take_attendance" -> TakeAttendanceSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            userId = userId,
                            onBack = {
                                viewModel.onEvent(InstitutionEvent.ClearAttendanceFilters)
                                viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null))
                            }
                        )
                        "transport" -> TransportSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            onNavigateToLiveBus = { busId ->
                                selectedBusId = busId
                                viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("live_bus"))
                                viewModel.onEvent(InstitutionEvent.LoadBusLiveLocation(busId))
                            },
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "live_bus" -> LiveBusMapSubScreen(
                            state = state,
                            busId = selectedBusId ?: "",
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("transport")) }
                        )
                        "teacher_attendance_history" -> TeacherAttendanceHistorySubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            onBack = {
                                viewModel.onEvent(InstitutionEvent.ClearAttendanceFilters)
                                viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null))
                            }
                        )
                        "salary_payouts" -> SalaryPayoutSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onPaymentClick = { selectedPaymentReceipt = it },
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "pending_syncs" -> PendingSyncsSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            userId = userId,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "fab_leave" -> ApplyLeaveFabSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            userId = userId,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "fab_remarks_add" -> AddRemarksFabSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            userId = userId,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "fab_take_attendance" -> TakeAttendanceFabSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "fab_fees" -> CollectFeeFabSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "fab_add_finance" -> AddFinanceFabSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "fab_start_trip" -> StartTripFabSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "fab_add_case_study" -> AddCaseStudyFabSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "fab_add_experience" -> AddExperienceFabSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "fab_add_journey" -> AddJourneyFabSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "fab_quicks" -> QuicksFabSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "content_feed" -> ContentFeedScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onItemClick = { item ->
                                selectedFeedItem = item
                                viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("full_content_feed"))
                            },
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "full_content_feed" -> FullContentFeedScreen(
                            item = selectedFeedItem,
                            isHindi = isHindi,
                            isDark = isDark,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("content_feed")) }
                        )
                        "student_directory" -> StudentDirectorySubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            onStudentClick = { studentId ->
                                selectedStudentId = studentId
                                viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("student_details"))
                            },
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "student_details" -> StudentDetailsSubScreen(
                            state = state,
                            studentId = selectedStudentId ?: "",
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("student_directory")) }
                        )
                        "driver_student_attendance" -> DriverStudentAttendanceScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            onNavigateToHistory = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("driver_student_attendance_history")) },
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "driver_student_attendance_history" -> DriverStudentAttendanceScreenHistory(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("driver_student_attendance")) }
                        )
                        "child_profiles_list" -> ChildProfilesSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onChildClick = { childId ->
                                selectedStudentId = childId
                                viewModel.onEvent(InstitutionEvent.LoadStudentProfileDetails(childId))
                                viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("child_profile_detail"))
                            },
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "set_home_location" -> StudentHomeLocationDetailSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            userId = userId,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "child_profile_detail" -> StudentHomeLocationDetailSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            userId = userId,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("child_profiles_list")) }
                        )
                        "student_profile_detail" -> StudentHomeLocationDetailSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            userId = userId,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "remarks_show" -> RemarkShowScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            onNavigateToAddRemark = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("remarks_add")) },
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "remarks_add" -> RemarkAddScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            viewModel = viewModel,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen("remarks_show")) }
                        )
                        "all_logs" -> AllLogsScreen(
                            role = state.activeWorkspace?.role ?: "",
                            isHindi = isHindi,
                            isDark = isDark,
                            onBackClick = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "notices" -> NoticesSubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                        "gallery" -> GallerySubScreen(
                            state = state,
                            isHindi = isHindi,
                            isDark = isDark,
                            onBack = { viewModel.onEvent(InstitutionEvent.ChangeActiveSubScreen(null)) }
                        )
                    }
                }
            }
        }
    }

    if (selectedPaymentReceipt != null) {
        SalaryReceiptDialog(
            payment = selectedPaymentReceipt!!,
            isHindi = isHindi,
            isDark = isDark,
            onDismiss = { selectedPaymentReceipt = null }
        )
    }

    // Switcher Bottom Dialog
    if (showSwitcher) {
        Dialog(
            onDismissRequest = { showSwitcher = false }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color(0xFF1C1C1E) else Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "कार्यक्षेत्र बदलें" else "Switch Workspace",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // ——— Active Workspaces ———
                        if (state.workspaces.isNotEmpty()) {
                            item {
                                Text(
                                    text = if (isHindi) "सक्रिय कार्यक्षेत्र" else "Active Workspaces",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        items(state.workspaces) { space ->
                            val isActive = space.id == state.activeWorkspace?.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isActive) {
                                            AppColors.EmeraldGreen.copy(alpha = 0.08f)
                                        } else Color.Transparent
                                    )
                                    .border(
                                        width = 0.5.dp,
                                        color = if (isActive) AppColors.EmeraldGreen else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.onEvent(InstitutionEvent.SwitchWorkspace(space.id))
                                        showSwitcher = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = space.childOrgName ?: space.parentOrgName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = if (isHindi) mapRoleHi(space.role) else space.role,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isActive) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(AppColors.EmeraldGreen, CircleShape)
                                    )
                                }
                            }
                        }

                        // ——— Pending Invitations ———
                        if (state.pendingApprovals.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(0.5.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(0xFFFF3B30), CircleShape)
                                    )
                                    Text(
                                        text = if (isHindi) "लंबित आमंत्रण" else "Pending Invitations",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFFF3B30),
                                        modifier = Modifier.padding(bottom = 0.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            items(state.pendingApprovals) { approval ->
                                var isApproving by remember(approval.id) { mutableStateOf(false) }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isDark) Color(0xFF252526) else Color(0xFFFFF8F8)
                                        )
                                        .border(
                                            0.5.dp,
                                            Color(0xFFFF3B30).copy(alpha = 0.3f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = approval.targetName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        val roleLabel = when (approval.role) {
                                            "Guardian" -> if (isHindi) "अभिभावक" else "Guardian"
                                            "Student"  -> if (isHindi) "छात्र" else "Student"
                                            "Staff"    -> if (isHindi) "कर्मचारी" else "Staff"
                                            else       -> approval.role
                                        }
                                        Text(
                                            text = "$roleLabel • ${approval.personName}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (!isApproving) {
                                                isApproving = true
                                                viewModel.onEvent(
                                                    InstitutionEvent.ApproveSpecificConnection(
                                                        linkId = approval.id,
                                                        tableName = approval.tableName,
                                                        userId = userId
                                                    )
                                                )
                                                showSwitcher = false
                                            }
                                        },
                                        enabled = !isApproving,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AppColors.EmeraldGreen,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(20.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Lucide.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isHindi) "स्वीकार करें" else "Approve",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
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
}

private fun mapRoleHi(role: String): String {
    return when (role) {
        "Guardian" -> "अभिभावक"
        "Student" -> "छात्र"
        "Teacher" -> "शिक्षक"
        "Driver" -> "ड्राइवर"
        "Admin" -> "प्रशासक"
        "System Administrator" -> "सिस्टम प्रशासक"
        "School Administrator" -> "स्कूल प्रशासक"
        "Org Admin" -> "संस्था प्रशासक"
        else -> role
    }
}

@Composable
fun ProfileDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun SalaryReceiptDialog(
    payment: com.vidyasetuai.feature_institution.domain.model.StaffSalaryPayment,
    isHindi: Boolean,
    isDark: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = if (isHindi) "बंद करें" else "Close", color = AppColors.EmeraldGreen)
            }
        },
        title = {
            Text(text = if (isHindi) "वेतन रसीद" else "Salary Receipt", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(text = "${if (isHindi) "राशि:" else "Amount:"} ₹${payment.amountPaid}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${if (isHindi) "दिनांक:" else "Date:"} ${payment.paymentDate}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${if (isHindi) "भुगतान माध्यम:" else "Mode:"} ${payment.paymentMode}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${if (isHindi) "ट्रांजैक्शन आईडी:" else "Transaction ID:"} ${payment.transactionId ?: ""}")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun DashboardQuickActionsRow(
    role: String,
    isHindi: Boolean,
    onNavigateToStudents: () -> Unit,
    onNavigateToLeave: () -> Unit,
    onNavigateToRemarks: () -> Unit,
    onNavigateToFees: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToTransport: () -> Unit,
    onNavigateToNotices: () -> Unit,
    onNavigateToGallery: () -> Unit
) {
    val isStaff = role != "Guardian" && role != "Student"

    // Define items to show
    val items = remember(role, isHindi) {
        buildList {
            // 1. Student / Child / Self
            val studentLabel = when (role) {
                "Guardian" -> if (isHindi) "बच्चा" else "Child"
                "Student" -> if (isHindi) "स्वयं" else "Self"
                else -> if (isHindi) "छात्र" else "Students"
            }
            add(QuickActionItem(studentLabel, Lucide.Users, onNavigateToStudents))

            // 2. Leaves
            add(QuickActionItem(if (isHindi) "छुट्टियाँ" else "Leaves", Lucide.Calendar, onNavigateToLeave))

            // 3. Remarks
            add(QuickActionItem(if (isHindi) "टिप्पणियाँ" else "Remarks", Lucide.MessageCircle, onNavigateToRemarks))

            // 4. Fees (Visible to Guardian, Student, and Staff roles)
            add(QuickActionItem(if (isHindi) "फीस" else "Fees", Lucide.CreditCard, onNavigateToFees))

            // 5. Finance/Salary (Visible only to Staff roles)
            if (isStaff) {
                add(QuickActionItem(if (isHindi) "सैलरी" else "Salary", Lucide.Coins, onNavigateToFinance))
            }

            // 6. Transport/Bus (Hide for Driver)
            if (role != "Driver") {
                add(QuickActionItem(if (isHindi) "परिवहन" else "Transport", Lucide.Bus, onNavigateToTransport))
            }

            // 7. Notices
            add(QuickActionItem(if (isHindi) "सूचनाएं" else "Notices", Lucide.Megaphone, onNavigateToNotices))

            // 8. Gallery
            add(QuickActionItem(if (isHindi) "गैलरी" else "Gallery", Lucide.Image, onNavigateToGallery))
        }
    }

    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(items) { item ->
            Column(
                modifier = Modifier
                    .clickable { item.onClick() }
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

data class QuickActionItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)

@Composable
fun NoticesSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBg = if (isDark) Color(0xFF1E1E20) else Color.White
    val cardBorderColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val textColorSecondary = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    val notices = remember(state.contentFeedItems) {
        state.contentFeedItems.filter { it.contentType == "notice" }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(backgroundColor)) {
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
                            tint = textColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isHindi) "सूचनाएं" else "Notices",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(dividerColor)
                )
            }
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            if (notices.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp).fillMaxSize()
                ) {
                    Icon(
                        imageVector = Lucide.Megaphone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isHindi) "कोई नई सूचना नहीं है" else "No new notices",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHindi) "संस्थान द्वारा जारी सभी महत्वपूर्ण सूचनाएं यहाँ दिखाई देंगी।" else "All important notices issued by the institution will appear here.",
                        fontSize = 13.sp,
                        color = textColorSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notices) { notice ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notice.publisherName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.EmeraldGreen
                                    )
                                    Text(
                                        text = notice.createdAt.split("T").firstOrNull() ?: "",
                                        fontSize = 11.sp,
                                        color = textColorSecondary
                                    )
                                }
                                if (!notice.title.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = notice.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                }
                                if (!notice.description.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = notice.description,
                                        fontSize = 13.sp,
                                        color = textColorSecondary,
                                        lineHeight = 18.sp
                                    )
                                }
                                if (!notice.imageUrl.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    AsyncImage(
                                        model = notice.imageUrl,
                                        contentDescription = notice.title,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
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

@Composable
fun GallerySubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBg = if (isDark) Color(0xFF1E1E20) else Color.White
    val cardBorderColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val textColorSecondary = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    val galleryItems = remember(state.contentFeedItems) {
        state.contentFeedItems.filter { it.contentType == "gallery" }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(backgroundColor)) {
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
                            tint = textColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isHindi) "गैलरी" else "Gallery",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(dividerColor)
                )
            }
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            if (galleryItems.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp).fillMaxSize()
                ) {
                    Icon(
                        imageVector = Lucide.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isHindi) "गैलरी खाली है" else "Gallery is empty",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHindi) "संस्थान द्वारा साझा की गई इवेंट तस्वीरें यहाँ दिखाई देंगी।" else "Event photos shared by the institution will be displayed here.",
                        fontSize = 13.sp,
                        color = textColorSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val chunkedItems = remember(galleryItems) { galleryItems.chunked(2) }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(chunkedItems) { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { item ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBg)
                                ) {
                                    Column {
                                        AsyncImage(
                                            model = item.imageUrl ?: "",
                                            contentDescription = item.title,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(130.dp)
                                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Companion.Crop
                                        )
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(
                                                text = item.title ?: (if (isHindi) "संस्थान इवेंट" else "Campus Event"),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textColor,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = item.publisherName,
                                                fontSize = 10.sp,
                                                color = textColorSecondary,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                            if (row.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}


