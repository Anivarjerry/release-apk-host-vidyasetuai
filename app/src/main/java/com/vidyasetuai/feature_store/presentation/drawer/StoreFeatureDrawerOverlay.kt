package com.vidyasetuai.feature_store.presentation.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vidyasetuai.feature_store.presentation.navigation.StoreRole

/**
 * 100% Autonomous Window-Level Feature Store Drawer Overlay.
 * Renders on Android Window Layer above all Scaffolds, System Bars, and Root BottomNavs.
 * Guarantees zero leakage, zero double-padding, and 100% micro-modular encapsulation.
 */
@Composable
fun StoreFeatureDrawerOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    isHindi: Boolean = false,
    onNavigateToPos: () -> Unit = {},
    onNavigateToKds: () -> Unit = {},
    onNavigateToCatalog: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToInvoices: () -> Unit = {},
    onNavigateToPurchases: () -> Unit = {},
    onNavigateToExpenses: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToParties: () -> Unit = {},
    onNavigateToStaff: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToActiveOrders: () -> Unit = {},
    onNavigateToPastOrders: () -> Unit = {},
    onSelectRole: (StoreRole, String?) -> Unit = { _, _ -> }
) {
    if (!visible) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        // Pillar 5: Layered Native Back Handler
        BackHandler(enabled = true) {
            onDismiss()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(180)),
                exit = slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(180)),
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
            ) {
                Surface(
                    modifier = Modifier
                        .widthIn(max = 320.dp)
                        .fillMaxWidth(0.82f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {}, // Prevent click-through to scrim
                    shape = RectangleShape,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    StoreDrawerContent(
                        isHindi = isHindi,
                        onOpenSettings = {
                            onDismiss()
                            onNavigateToSettings()
                        },
                        onOpenBranches = {
                            onDismiss()
                            onNavigateToStaff()
                        },
                        onOpenStaff = {
                            onDismiss()
                            onNavigateToStaff()
                        },
                        onNavigateToPos = {
                            onDismiss()
                            onNavigateToPos()
                        },
                        onNavigateToKds = {
                            onDismiss()
                            onNavigateToKds()
                        },
                        onNavigateToCatalog = {
                            onDismiss()
                            onNavigateToCatalog()
                        },
                        onNavigateToInventory = {
                            onDismiss()
                            onNavigateToInventory()
                        },
                        onNavigateToInvoices = {
                            onDismiss()
                            onNavigateToInvoices()
                        },
                        onNavigateToPurchases = {
                            onDismiss()
                            onNavigateToPurchases()
                        },
                        onNavigateToExpenses = {
                            onDismiss()
                            onNavigateToExpenses()
                        },
                        onNavigateToReports = {
                            onDismiss()
                            onNavigateToReports()
                        },
                        onNavigateToParties = {
                            onDismiss()
                            onNavigateToParties()
                        },
                        onNavigateToCart = {
                            onDismiss()
                            onNavigateToCart()
                        },
                        onNavigateToActiveOrders = {
                            onDismiss()
                            onNavigateToActiveOrders()
                        },
                        onNavigateToPastOrders = {
                            onDismiss()
                            onNavigateToPastOrders()
                        },
                        onSelectRole = { role, staffRole ->
                            onSelectRole(role, staffRole)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}
