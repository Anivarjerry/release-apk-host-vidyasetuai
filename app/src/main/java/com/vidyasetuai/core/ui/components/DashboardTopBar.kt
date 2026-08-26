package com.vidyasetuai.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Menu
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.UserPlus
import com.vidyasetuai.core.ui.colors.AppColors

@Composable
fun DashboardTopBar(
    activeTab: String,
    isHindi: Boolean,
    isSubScreenActive: Boolean,
    onNavigateToSettings: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    onForceSyncWorkspace: (() -> Unit)? = null,
    onOpenSearchUser: (() -> Unit)? = null,
    onEditProfileClick: (() -> Unit)? = null,
    onOpenHomeCreateOptions: (() -> Unit)? = null,
    isRefreshingWorkspace: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (!isSubScreenActive && activeTab !in listOf("store", "campus", "profile")) {
        val haptic = LocalHapticFeedback.current
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Top Left: Navigation Drawer Menu Icon for ALL tabs (including Home)
                IconButton(
                    onClick = { onOpenDrawer?.invoke() },
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Lucide.Menu,
                        contentDescription = "Drawer Menu",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Top Center: Active Tab Title / Brand Centered
                val centerTitle = when (activeTab) {
                    "home" -> "VidyaSetu AI"
                    "institute" -> if (isHindi) "संस्थान" else "Institute"
                    "store" -> if (isHindi) "स्टोर" else "Store"
                    "campus" -> if (isHindi) "कैंपस" else "Campus"
                    else -> if (isHindi) "प्रोफ़ाइल" else "Profile"
                }

                Text(
                    text = centerTitle,
                    fontSize = if (activeTab == "home") 20.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = if (activeTab == "home") (-0.5).sp else 0.sp,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Top Right: Contextual action icons based on active tab
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (activeTab == "institute") {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onForceSyncWorkspace?.invoke()
                            },
                            enabled = !isRefreshingWorkspace,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.RefreshCw,
                                contentDescription = "Force Refresh",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (activeTab == "campus") {
                        IconButton(
                            onClick = { onOpenSearchUser?.invoke() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.UserPlus,
                                contentDescription = "Connect People",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    if (activeTab == "profile") {
                        IconButton(
                            onClick = { onEditProfileClick?.invoke() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Pencil,
                                contentDescription = "Edit Profile",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    if (activeTab == "home") {
                        IconButton(
                            onClick = { onOpenHomeCreateOptions?.invoke() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Plus,
                                contentDescription = "Create Options",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
