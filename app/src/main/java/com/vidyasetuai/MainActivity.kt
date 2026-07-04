package com.vidyasetuai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import com.vidyasetuai.core.ui.components.DashboardScreen
import com.vidyasetuai.core.auth.SessionManager
import com.vidyasetuai.core.ui.theme.VidyaStuTheme
import com.vidyasetuai.feature_auth.data.repository.AuthRepositoryImpl
import com.vidyasetuai.feature_auth.presentation.screen.LoginScreen
import android.content.Intent
import com.vidyasetuai.feature_auth.presentation.screen.SignUpScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider

class MainActivity : ComponentActivity() {
    private val navigateToFlow = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    private var isAppReady = false

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val dest = intent.getStringExtra("NAVIGATE_TO")
        if (dest != null) {
            navigateToFlow.value = dest
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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
        }
        super.onCreate(savedInstanceState)
        
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
        setContent {
            val context = LocalContext.current
            val prefs = remember { context.getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE) }
            var appTheme by remember { mutableStateOf(prefs.getString("theme", "system") ?: "system") }
            var appLanguage by remember { mutableStateOf(prefs.getString("language", "en") ?: "en") }

            val isDarkTheme = when (appTheme) {
                "dark" -> true
                "light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
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
                
                val versionRemoteDataSource = remember { com.vidyasetuai.core.update.data.remote.datasource.VersionRemoteDataSource() }
                val versionRepository = remember { com.vidyasetuai.core.update.data.repository.VersionRepositoryImpl(versionRemoteDataSource) }
                val checkAppVersionUseCase = remember { com.vidyasetuai.core.update.domain.usecase.CheckAppVersionUseCase(versionRepository) }
                
                var updateType by remember { mutableStateOf(com.vidyasetuai.core.update.domain.model.UpdateType.NONE) }
                var latestVersionInfo by remember { mutableStateOf<com.vidyasetuai.core.update.domain.model.AppVersionInfo?>(null) }
                
                val scope = rememberCoroutineScope()

                var currentScreen by remember { mutableStateOf("") }
                
                var targetScreen by remember { mutableStateOf<String?>(null) }

                // Parallelize checking of session, version, and permissions on startup
                LaunchedEffect(Unit) {
                    scope.launch {
                        // 1. Version check with 2.5 second timeout
                        val checkResult = try {
                            kotlinx.coroutines.withTimeout(2500L) {
                                checkAppVersionUseCase()
                            }
                        } catch (e: Exception) {
                            com.vidyasetuai.core.update.domain.model.UpdateCheckResult(com.vidyasetuai.core.update.domain.model.UpdateType.NONE, null)
                        }

                        // 2. Local session check (instant)
                        val hasLocalSession = sessionManager.hasActiveSession()

                        if (checkResult.updateType == com.vidyasetuai.core.update.domain.model.UpdateType.FORCE) {
                            latestVersionInfo = checkResult.info
                            updateType = com.vidyasetuai.core.update.domain.model.UpdateType.FORCE
                            targetScreen = "update_screen"
                        } else {
                            if (checkResult.updateType == com.vidyasetuai.core.update.domain.model.UpdateType.OPTIONAL) {
                                latestVersionInfo = checkResult.info
                                updateType = com.vidyasetuai.core.update.domain.model.UpdateType.OPTIONAL
                            }
                            if (hasLocalSession) {
                                val permissionsGranted = com.vidyasetuai.core.auth.PermissionManager.checkAllPermissions(context)
                                if (permissionsGranted) {
                                    targetScreen = "home"
                                } else {
                                    targetScreen = "permission_gate"
                                }
                                // Trigger background session validation online
                                scope.launch {
                                    com.vidyasetuai.core.auth.AuthManager.checkSessionOnline(context, sessionManager, authRepository)
                                }
                            } else {
                                targetScreen = "login"
                            }
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
                val isSessionValid by com.vidyasetuai.core.auth.AuthManager.isSessionValid.collectAsState()
                LaunchedEffect(isSessionValid) {
                    if (!isSessionValid) {
                        currentScreen = "login"
                    }
                }

                // Monitor app lifecycle to force Permission Gate if permissions are revoked while app is minimized
                val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
                androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
                    val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                        if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                            if (currentScreen == "home" && sessionManager.hasActiveSession()) {
                                val permissionsGranted = com.vidyasetuai.core.auth.PermissionManager.checkAllPermissions(context)
                                if (!permissionsGranted) {
                                    currentScreen = "permission_gate"
                                }
                            }
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

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
                        onNavigateToSignUp = { currentScreen = "signup" },
                        onLoginSuccess = {
                            com.vidyasetuai.core.auth.AuthManager.resetSessionState()
                            
                            // Check permissions immediately on login success
                            val permissionsGranted = com.vidyasetuai.core.auth.PermissionManager.checkAllPermissions(context)
                            if (permissionsGranted) {
                                currentScreen = "home"
                            } else {
                                currentScreen = "permission_gate"
                            }
                            
                            // Sync FCM token immediately on login success in background
                            scope.launch {
                                com.vidyasetuai.core.auth.AuthManager.syncFcmToken(context, sessionManager)
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
                            com.vidyasetuai.core.auth.AuthManager.resetSessionState()
                            
                            // Immediately run online validation & permission validation in the background
                            scope.launch {
                                com.vidyasetuai.core.auth.AuthManager.checkSessionOnline(context, sessionManager, authRepository)
                                val permissionsGranted = com.vidyasetuai.core.auth.PermissionManager.checkAllPermissions(context)
                                if (!permissionsGranted && com.vidyasetuai.core.auth.AuthManager.isSessionValid.value) {
                                    currentScreen = "permission_gate"
                                }
                            }
                            
                            scope.launch {
                                while (true) {
                                    kotlinx.coroutines.delay(60000L) // Check every 60 seconds
                                    com.vidyasetuai.core.auth.AuthManager.checkSessionOnline(context, sessionManager, authRepository)
                                }
                            }
                        }

                        val navTarget by navigateToFlow.collectAsState()

                        Box(modifier = Modifier.fillMaxSize()) {
                            DashboardScreen(
                                currentTheme = appTheme,
                                onThemeChange = { appTheme = it },
                                currentLanguage = appLanguage,
                                onLanguageChange = { appLanguage = it },
                                navTarget = navTarget,
                                onNavTargetHandled = { navigateToFlow.value = null }
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
            }
        }
    }
}
