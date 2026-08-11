package com.vidyasetuai.core.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Compass
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.School
import com.composables.icons.lucide.User
import com.vidyasetuai.R
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.data.local.entity.WorkspaceEntity

/**
 * Premium Dashboard Bottom Navigation Bar for VidyaSetu AI.
 * Features in-place scale + fade active pill indicator behind selected icons.
 */
@Composable
fun DashboardBottomNav(
    activeTab: String,
    workspacesList: List<WorkspaceEntity>,
    isHindi: Boolean,
    isSubScreenActive: Boolean,
    onTabSelected: (String) -> Unit,
    onHomeTabClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isSubScreenActive) {
        val availableTabs = remember(workspacesList.isNotEmpty()) {
            if (workspacesList.isNotEmpty()) {
                listOf("home", "institute", "journey", "campus", "profile")
            } else {
                listOf("home", "journey", "campus", "profile")
            }
        }

        Column(
            modifier = modifier
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
                    val unselectedColor = if (isDark) Color(0xFFF2F2F7) else Color(0xFF111111)

                    availableTabs.forEach { tabKey ->
                        val isSelected = activeTab == tabKey
                        val interactionSource = remember { MutableInteractionSource() }

                        val pillScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.0f else 0.65f,
                            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                            label = "pill_scale"
                        )
                        val pillAlpha by animateFloatAsState(
                            targetValue = if (isSelected) 0.16f else 0.0f,
                            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                            label = "pill_alpha"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null // Removes default gray click flash
                                ) {
                                    if (tabKey == "home" && isSelected) {
                                        onHomeTabClicked()
                                    } else {
                                        onTabSelected(tabKey)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // In-place Fade + Scale active green pill directly behind tab icon
                            if (pillAlpha > 0.005f) {
                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(44.dp)
                                        .graphicsLayer(
                                            scaleX = pillScale,
                                            scaleY = pillScale,
                                            alpha = pillAlpha / 0.16f
                                        )
                                        .clip(RoundedCornerShape(22.dp))
                                        .background(AppColors.EmeraldGreen.copy(alpha = pillAlpha))
                                )
                            }

                            val tintColor = if (isSelected) AppColors.EmeraldGreen else unselectedColor

                            when (tabKey) {
                                "home" -> Icon(
                                    painter = painterResource(id = R.drawable.ic_bridge_logo),
                                    contentDescription = "Home",
                                    tint = tintColor,
                                    modifier = Modifier.size(width = 32.dp, height = 28.dp)
                                )
                                "institute" -> Icon(
                                    imageVector = Lucide.School,
                                    contentDescription = "Institute",
                                    tint = tintColor,
                                    modifier = Modifier.size(28.dp)
                                )
                                "journey" -> Icon(
                                    imageVector = Lucide.Compass,
                                    contentDescription = "Journey",
                                    tint = tintColor,
                                    modifier = Modifier.size(28.dp)
                                )
                                "campus" -> Icon(
                                    imageVector = Lucide.MessageCircle,
                                    contentDescription = "Campus",
                                    tint = tintColor,
                                    modifier = Modifier.size(28.dp)
                                )
                                "profile" -> Icon(
                                    imageVector = Lucide.User,
                                    contentDescription = "Profile",
                                    tint = tintColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
