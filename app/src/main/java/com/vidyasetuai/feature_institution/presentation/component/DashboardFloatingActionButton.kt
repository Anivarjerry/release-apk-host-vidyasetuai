package com.vidyasetuai.feature_institution.presentation.component

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
 * A premium Speed Dial Floating Action Button (FAB) for VidyaSetu AI dashboards.
 * Rendered at the root level, it dynamically shows actions for active tab and collapses on tab switch.
 */
@Composable
fun DashboardFloatingActionButton(
    activeTab: String,
    role: String,
    isHindi: Boolean,
    isDark: Boolean,
    onActionClick: (route: String, requiresToast: Boolean, label: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Automatically collapse the Speed Dial when switching between navigation tabs
    LaunchedEffect(activeTab) {
        isExpanded = false
    }

    // Resolve Speed Dial items for the active tab and user role
    val items = remember(activeTab, role) { DashboardFabRules.getSpeedDialItemsForTab(activeTab, role) }

    // If there are no speed dial items to display on this tab, hide the FAB entirely
    if (items.isEmpty()) return

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
        if (expanded) 0.5f else 0f
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // 1. Full Screen Scrim Overlay (Only blocks clicks when expanded)
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isExpanded = false
                    }
            )
        }

        // 2. FAB & Options Stack Column (adds padding from screen edges)
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 76.dp, end = 16.dp)
        ) {
            // Speed Dial items list
            if (isExpanded) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items.forEach { item ->
                        val label = if (isHindi) item.labelHi else item.labelEn
                        SpeedDialActionRow(
                            item = item,
                            label = label,
                            isDark = isDark,
                            onClick = {
                                isExpanded = false
                                onActionClick(item.route, item.requiresToastOnly, label)
                            }
                        )
                    }
                }
            }

            // Main FAB Trigger (Green circular button with rotating Plus/Cross)
            SmallFloatingActionButton(
                onClick = { isExpanded = !isExpanded },
                shape = CircleShape,
                containerColor = AppColors.EmeraldGreen,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 5.dp,
                    pressedElevation = 10.dp
                )
            ) {
                Icon(
                    imageVector = Lucide.Plus,
                    contentDescription = "Toggle Actions",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer(rotationZ = rotation)
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
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(end = 4.dp)
    ) {
        // Option Text Label Card
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color.White else Color.Black,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Option Icon Mini FAB
        SmallFloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
            containerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA),
            contentColor = if (isDark) Color.White else Color.Black,
            modifier = Modifier.size(36.dp),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = label,
                tint = if (isDark) Color.White else Color.Black,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
