package com.vidyasetuai.feature_campus.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vidyasetuai.feature_campus.CampusModuleFacade
import com.vidyasetuai.feature_campus.presentation.viewmodel.CampusViewModel
import com.vidyasetuai.navigation.CampusDestination

/**
 * 100% Autonomous Root Screen for the `feature_campus` micro-module.
 * WhatsApp-Grade 120 FPS Architecture with Type-Safe Routing & FCM Deep-Linking Bridge.
 */
@Composable
fun CampusRootScreen(
    targetPeerUserId: String? = null,
    onTargetHandled: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onSubScreenChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = CampusModuleFacade.initialize(context)
    val viewModel: CampusViewModel = viewModel(
        factory = CampusViewModel.Factory(repository)
    )

    val activeDestination by viewModel.activeDestination.collectAsState()

    // 1. Direct FCM Push Notification / Deep-Link Resolver
    LaunchedEffect(targetPeerUserId) {
        if (!targetPeerUserId.isNullOrBlank()) {
            viewModel.openChatByPeerUserId(targetPeerUserId)
            onTargetHandled()
        }
    }

    // 2. Notify parent container of full-screen subscreen transitions
    LaunchedEffect(activeDestination) {
        onSubScreenChange(activeDestination !is CampusDestination.Home)
    }

    // 3. Pillar 5: Layered Back Navigation Resilience (Zero Trap Backstacks)
    BackHandler(enabled = true) {
        when (activeDestination) {
            is CampusDestination.Chat -> {
                viewModel.closeChat()
            }
            is CampusDestination.ConnectionsDiscovery -> {
                viewModel.closeConnectionsScreen()
            }
            CampusDestination.Home -> {
                onNavigateBack()
            }
        }
    }

    // 4. Butter-Smooth 120 FPS Horizontal Slide Transition (WhatsApp / Telegram Standard)
    AnimatedContent(
        targetState = activeDestination,
        transitionSpec = {
            if (targetState !is CampusDestination.Home) {
                // Forward: Slide In from Right to Left
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(200)) togetherWith
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 4 },
                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(200))
            } else {
                // Backward: Slide In from Left to Right
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 4 },
                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(200)) togetherWith
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(200))
            }
        },
        label = "CampusScreenTransition",
        modifier = modifier.fillMaxSize()
    ) { destination ->
        when (destination) {
            is CampusDestination.Chat -> {
                CampusChatScreen(
                    connection = destination.connection,
                    viewModel = viewModel,
                    onNavigateBack = { viewModel.closeChat() }
                )
            }
            is CampusDestination.ConnectionsDiscovery -> {
                CampusConnectionsSubScreen(
                    viewModel = viewModel,
                    onNavigateBack = { viewModel.closeConnectionsScreen() }
                )
            }
            CampusDestination.Home -> {
                CampusHomeScreen(viewModel = viewModel)
            }
        }
    }
}
