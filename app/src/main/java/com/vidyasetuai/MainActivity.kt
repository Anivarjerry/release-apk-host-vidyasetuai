package com.vidyasetuai

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.vidyasetuai.core.auth.AuthManager
import com.vidyasetuai.core.auth.PermissionManager
import com.vidyasetuai.core.auth.SessionManager
import com.vidyasetuai.core.security.AppSecurityManager
import com.vidyasetuai.core.ui.components.DashboardScreen
import com.vidyasetuai.core.ui.theme.VidyaStuTheme
import com.vidyasetuai.feature_auth.data.repository.AuthRepositoryImpl
import com.vidyasetuai.feature_auth.presentation.screen.LoginScreen
import com.vidyasetuai.feature_auth.presentation.screen.SignUpScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class MainActivity : FragmentActivity() {
    private val navigateToFlow = MutableStateFlow<String?>(null)
    private val targetRoomIdFlow = MutableStateFlow<String?>(null)
    private val targetPeerUserIdFlow = MutableStateFlow<String?>(null)
    private var isAppReady = false

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val dest = intent.getStringExtra("NAVIGATE_TO")
        if (dest != null) {
            navigateToFlow.value = dest
            targetRoomIdFlow.value = intent.getStringExtra("TARGET_ROOM_ID")
            targetPeerUserIdFlow.value = intent.getStringExtra("TARGET_PEER_USER_ID")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Protect Recent Apps thumbnail and block screenshots globally across the app
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        val splashScreen = installSplashScreen()
        
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null
            && intent.action == Intent.ACTION_MAIN
        ) {
            finish()
            return
        }
        val dest = intent?.getStringExtra("NAVIGATE_TO")
        if (dest != null) {
            navigateToFlow.value = dest
            targetRoomIdFlow.value = intent.getStringExtra("TARGET_ROOM_ID")
            targetPeerUserIdFlow.value = intent.getStringExtra("TARGET_PEER_USER_ID")
        }
        super.onCreate(savedInstanceState)

        // Eagerly pre-warm Store, Campus & Profile Databases on disk
        lifecycleScope.launch(Dispatchers.IO) {
            com.vidyasetuai.feature_store.StoreModuleFacade.prewarmDatabase(applicationContext)
            com.vidyasetuai.feature_campus.CampusModuleFacade.prewarmDatabase(applicationContext)
            com.vidyasetuai.feature_profile.ProfileModuleFacade.prewarmDatabase(applicationContext)
        }
        
        // Keep starting splash screen visible until app logic is fully ready
        splashScreen.setKeepOnScreenCondition {
            !isAppReady
        }
        
        // Custom exit zoom transition for the system splash screen logo
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider: SplashScreenViewProvider ->
            val iconView = splashScreenViewProvider.iconView
            
            val scaleX = android.animation.ObjectAnimator.ofFloat(iconView, android.view.View.SCALE_X, 1f, 1.4f)
            val scaleY = android.animation.ObjectAnimator.ofFloat(iconView, android.view.View.SCALE_Y, 1f, 1.4f)
            val alpha = android.animation.ObjectAnimator.ofFloat(iconView, android.view.View.ALPHA, 1f, 0f)
            val fadeBg = android.animation.ObjectAnimator.ofFloat(splashScreenViewProvider.view, android.view.View.ALPHA, 1f, 0f)
            
            val animatorSet = android.animation.AnimatorSet().apply {
                playTogether(scaleX, scaleY, alpha, fadeBg)
                duration = 500L
                interpolator = android.view.animation.DecelerateInterpolator()
            }
            
            animatorSet.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    splashScreenViewProvider.remove()
                }
            })
            animatorSet.start()
        }
        
        enableEdgeToEdge()
        
        com.vidyasetuai.core.notification.handler.ChatNotificationHandler.initLifecycle(application)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        setContent {
            val context = LocalContext.current
            val prefs = remember { context.getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE) }
            var appTheme by remember { mutableStateOf(prefs.getString("theme", "system") ?: "system") }
            var appLanguage by remember { mutableStateOf(prefs.getString("language", "en") ?: "en") }

            val isDarkTheme = when (appTheme) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            LaunchedEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkTheme) {
                        androidx.activity.SystemBarStyle.dark(
                            android.graphics.Color.TRANSPARENT
                        )
                    } else {
                        androidx.activity.SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    },
                    navigationBarStyle = if (isDarkTheme) {
                        androidx.activity.SystemBarStyle.dark(
                            android.graphics.Color.TRANSPARENT
                        )
                    } else {
                        androidx.activity.SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    }
                )
            }

            VidyaStuTheme(darkTheme = isDarkTheme) {
                val sessionManager = remember { SessionManager(context) }
                val authRepository = remember { AuthRepositoryImpl(sessionManager) }
                val trustedDeviceRepository = remember { com.vidyasetuai.feature_auth.data.repository.TrustedDeviceRepository(sessionManager) }
                
                val versionRemoteDataSource = remember { com.vidyasetuai.core.update.data.remote.datasource.VersionRemoteDataSource() }
                val versionRepository = remember { com.vidyasetuai.core.update.data.repository.VersionRepositoryImpl(versionRemoteDataSource) }
                val checkAppVersionUseCase = remember { com.vidyasetuai.core.update.domain.usecase.CheckAppVersionUseCase(versionRepository) }
                
                var updateType by remember { mutableStateOf(com.vidyasetuai.core.update.domain.model.UpdateType.NONE) }
                var latestVersionInfo by remember { mutableStateOf<com.vidyasetuai.core.update.domain.model.AppVersionInfo?>(null) }
                
                val scope = rememberCoroutineScope()

                var currentScreen by remember { mutableStateOf("") }
                
                var targetScreen by remember { mutableStateOf<String?>(null) }

                // 1. Instant 0ms Local Session & Permission Resolution (Zero Splash Freeze)
                LaunchedEffect(Unit) {
                    val hasLocalSession = sessionManager.hasActiveSession()
                    if (hasLocalSession) {
                        val permissionsGranted = PermissionManager.checkAllPermissions(context)
                        targetScreen = if (permissionsGranted) "home" else "permission_gate"
                        // Trigger background online session verification
                        scope.launch(Dispatchers.IO) {
                            AuthManager.checkSessionOnline(context, sessionManager, authRepository)
                        }
                    } else {
                        targetScreen = "login"
                    }
                    isAppReady = true

                    // 2. Parallel Background Version & Mandatory Update Gate via Traffic Police
                    scope.launch(Dispatchers.IO) {
                        val checkResult = try {
                            checkAppVersionUseCase()
                        } catch (e: Exception) {
                            com.vidyasetuai.core.update.domain.model.UpdateCheckResult(com.vidyasetuai.core.update.domain.model.UpdateType.NONE, null)
                        }

                        android.util.Log.d("VidyaSetu_Version", "App version check result: type=${checkResult.updateType}, latest=${checkResult.info?.versionName}")

                        if (checkResult.updateType == com.vidyasetuai.core.update.domain.model.UpdateType.FORCE && checkResult.info != null) {
                            latestVersionInfo = checkResult.info
                            updateType = com.vidyasetuai.core.update.domain.model.UpdateType.FORCE
                            targetScreen = "update_screen"
                            currentScreen = "update_screen"
                        } else if (checkResult.updateType == com.vidyasetuai.core.update.domain.model.UpdateType.OPTIONAL && checkResult.info != null) {
                            latestVersionInfo = checkResult.info
                            updateType = com.vidyasetuai.core.update.domain.model.UpdateType.OPTIONAL
                        }
                    }
                }

                // Smoothly navigate only after the target screen is identified
                LaunchedEffect(targetScreen) {
                    if (targetScreen != null) {
                        isAppReady = true
                        currentScreen = targetScreen!!
                    }
                }

                // Observe session validity flow and redirect to login if invalidated
                val isSessionValid by AuthManager.isSessionValid.collectAsState()
                LaunchedEffect(isSessionValid) {
                    if (!isSessionValid) {
                        currentScreen = "login"
                    }
                }

                val securityManager = remember { AppSecurityManager(context) }
                var isAppLocked by remember { mutableStateOf(securityManager.shouldTriggerLock() && sessionManager.hasActiveSession()) }

                // Monitor app lifecycle to track background time & trigger lock if timeout elapsed
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_STOP) {
                            securityManager.recordBackgroundTimestamp()
                        } else if (event == Lifecycle.Event.ON_RESUME) {
                            if (currentScreen == "home" && sessionManager.hasActiveSession()) {
                                val permissionsGranted = PermissionManager.checkAllPermissions(context)
                                if (!permissionsGranted) {
                                    currentScreen = "permission_gate"
                                }
                            }
                            if (sessionManager.hasActiveSession() && securityManager.shouldTriggerLock()) {
                                isAppLocked = true
                            }
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        "" -> {
                            // Empty background container to wait for system splash transition
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(if (isDarkTheme) Color(0xFF121212) else Color.White)
                            )
                        }
                        "update_screen" -> {
                            if (latestVersionInfo != null) {
                                com.vidyasetuai.core.update.presentation.components.UpdateScreen(
                                    info = latestVersionInfo!!,
                                    onNavigateBack = {}
                                )
                            }
                        }
                        "login" -> LoginScreen(
                            authRepository = authRepository,
                            sessionManager = sessionManager,
                            trustedDeviceRepository = trustedDeviceRepository,
                            onNavigateToSignUp = { currentScreen = "signup" },
                            onLoginSuccess = {
                                AuthManager.resetSessionState()
                                
                                // Check permissions immediately on login success
                                val permissionsGranted = PermissionManager.checkAllPermissions(context)
                                if (permissionsGranted) {
                                    currentScreen = "home"
                                } else {
                                    currentScreen = "permission_gate"
                                }
                                
                                // Sync FCM token immediately on login success in background
                                scope.launch {
                                    AuthManager.syncFcmToken(context, sessionManager)
                                }

                                // Sync Store, Campus & Profile Workspace data immediately on login success in background
                                scope.launch(Dispatchers.IO) {
                                    com.vidyasetuai.feature_store.StoreModuleFacade.triggerBackgroundSync(context)
                                    com.vidyasetuai.feature_campus.CampusModuleFacade.triggerBackgroundSync(context)
                                    com.vidyasetuai.feature_profile.ProfileModuleFacade.triggerBackgroundSync(context)
                                }
                            }
                        )
                        "signup" -> {
                            androidx.activity.compose.BackHandler {
                                currentScreen = "login"
                            }
                            SignUpScreen(
                                authRepository = authRepository,
                                onNavigateToLogin = { currentScreen = "login" },
                                onSignUpSuccess = {
                                    currentScreen = "login"
                                }
                            )
                        }
                        "permission_gate" -> {
                            com.vidyasetuai.core.ui.components.PermissionGateScreen(
                                onAllPermissionsGranted = {
                                    currentScreen = "home"
                                }
                            )
                        }
                        "home" -> {
                            // Reset session state flow to valid & launch periodic background verification
                            LaunchedEffect(Unit) {
                                AuthManager.resetSessionState()
                                
                                // Immediately run online validation & permission validation in the background
                                scope.launch {
                                    AuthManager.checkSessionOnline(context, sessionManager, authRepository)
                                    val permissionsGranted = PermissionManager.checkAllPermissions(context)
                                    if (!permissionsGranted && AuthManager.isSessionValid.value) {
                                        currentScreen = "permission_gate"
                                    }
                                }

                                // Silent background sync for Store, Campus & Profile Workspace on cold start
                                scope.launch(Dispatchers.IO) {
                                    com.vidyasetuai.feature_store.StoreModuleFacade.triggerBackgroundSync(context)
                                    com.vidyasetuai.feature_campus.CampusModuleFacade.triggerBackgroundSync(context)
                                    com.vidyasetuai.feature_profile.ProfileModuleFacade.triggerBackgroundSync(context)
                                }
                                
                                scope.launch {
                                    while (true) {
                                        delay(60000L) // Check every 60 seconds
                                        AuthManager.checkSessionOnline(context, sessionManager, authRepository)
                                    }
                                }
                            }

                            val navTarget by navigateToFlow.collectAsState()
                            val targetRoomId by targetRoomIdFlow.collectAsState()
                            val targetPeerUserId by targetPeerUserIdFlow.collectAsState()

                            Box(modifier = Modifier.fillMaxSize()) {
                                DashboardScreen(
                                    currentTheme = appTheme,
                                    onThemeChange = { appTheme = it },
                                    currentLanguage = appLanguage,
                                    onLanguageChange = { appLanguage = it },
                                    navTarget = navTarget,
                                    targetRoomId = targetRoomId,
                                    targetPeerUserId = targetPeerUserId,
                                    onNavTargetHandled = { 
                                        navigateToFlow.value = null 
                                        targetRoomIdFlow.value = null
                                        targetPeerUserIdFlow.value = null
                                    }
                                )
                                
                                if (updateType == com.vidyasetuai.core.update.domain.model.UpdateType.OPTIONAL && latestVersionInfo != null) {
                                    com.vidyasetuai.core.update.presentation.components.OptionalUpdateDialog(
                                        info = latestVersionInfo!!,
                                        onDismiss = { updateType = com.vidyasetuai.core.update.domain.model.UpdateType.NONE }
                                    )
                                }
                            }
                        }
                    }

                    if (isAppLocked && sessionManager.hasActiveSession()) {
                        com.vidyasetuai.feature_institution.presentation.screen.subscreens.AppLockOverlayScreen(
                            isHindi = true,
                            isDark = isDarkTheme,
                            onUnlocked = { isAppLocked = false }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Call this composable on any specific screen where screenshots should be temporarily allowed.
 * Automatically restores FLAG_SECURE protection when leaving that screen.
 */
@Composable
fun AllowScreenshotsForThisScreen() {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    DisposableEffect(Unit) {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
