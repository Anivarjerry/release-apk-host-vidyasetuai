package com.vidyasetuai.feature_institution.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.util.DashboardFabRules

/**
 * A clean, minimalist Speed Dial Floating Action Button (FAB) for VidyaSetu AI dashboards.
 * Features smooth AnimatedVisibility slide out/in from the right edge on side drawer toggle.
 */
@Composable
fun DashboardFloatingActionButton(
    activeTab: String,
    role: String,
    isHindi: Boolean,
    isDark: Boolean,
    isDrawerOpen: Boolean = false,
    onActionClick: (route: String, requiresToast: Boolean, label: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Automatically collapse the Speed Dial when switching navigation tabs or opening side drawer
    LaunchedEffect(activeTab, isDrawerOpen) {
        if (isDrawerOpen) {
            isExpanded = false
        }
    }

    // Resolve Speed Dial items for the active tab and user role
    val items = remember(activeTab, role) { DashboardFabRules.getSpeedDialItemsForTab(activeTab, role) }

    // If there are no speed dial items or on Home tab, hide the FAB
    if (items.isEmpty() || activeTab == "home") return

    val transition = updateTransition(targetState = isExpanded, label = "speedDialTransition")
    
    val rotation by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 250) },
        label = "fabRotation"
    ) { expanded ->
        if (expanded) 45f else 0f
    }
    
    val scrimAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 200) },
        label = "scrimAlpha"
    ) { expanded ->
        if (expanded) 0.45f else 0f
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
            // 1. Full Screen Scrim Overlay (Transparent click handler when expanded)
            if (isExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isExpanded = false
                        }
                )
            }

            // 2. FAB & Options Stack Column
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .offset(y = 3.dp)
                    .padding(bottom = 0.dp, end = 0.dp)
            ) {
                // Speed Dial items list
                if (isExpanded) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items.forEach { item ->
                            val label = if (isHindi) item.labelHi else item.labelEn
                            SpeedDialActionRow(
                                item = item,
                                label = label,
                                isDark = isDark,
                                isDrawerOpen = isDrawerOpen,
                                onClick = {
                                    isExpanded = false
                                    onActionClick(item.route, item.requiresToastOnly, label)
                                }
                            )
                        }
                    }
                }

                // Main Rounded-Square Speed Dial FAB Button
                FloatingActionButton(
                    onClick = { if (!isDrawerOpen) isExpanded = !isExpanded },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = AppColors.EmeraldGreen,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 10.dp
                    ),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Plus,
                        contentDescription = "Quick Actions",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer { rotationZ = rotation }
                    )
                }
            }
        }
    }

@Composable
private fun SpeedDialActionRow(
    item: DashboardFabRules.SpeedDialItem,
    label: String,
    isDark: Boolean,
    isDrawerOpen: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onClick() }
    ) {
        // Label Chip
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isDark) Color(0xFF2C2C2E) else Color.White,
            shadowElevation = 4.dp,
            tonalElevation = 2.dp
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF1C1C1E),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
            )
        }

        // Action Icon FAB
        FloatingActionButton(
            onClick = onClick,
            shape = RoundedCornerShape(14.dp),
            containerColor = AppColors.EmeraldGreen,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
            modifier = Modifier.size(46.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
