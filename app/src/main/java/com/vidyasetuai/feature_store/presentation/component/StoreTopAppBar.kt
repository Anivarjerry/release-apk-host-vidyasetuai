package com.vidyasetuai.feature_store.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Store

/**
 * Ultra-Clean Modern Store Top App Bar.
 * Left: "Store" Tab Title.
 * Right: Contextual Search (Public only) & Premium Adaptive Profile Avatar Chip.
 */
@Composable
fun StoreTopAppBar(
    isHindi: Boolean = false,
    isPublicCustomer: Boolean = false,
    businessInitial: String = "S",
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Tab Title
            Text(
                text = if (isHindi) "स्टोर (Store)" else "Store",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = (-0.5).sp
            )

            // Right Actions Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Contextual Search (Visible only in Public Shopper Mode)
                if (isPublicCustomer) {
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Search,
                            contentDescription = "Search Stores",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // 2. Premium Adaptive Profile Avatar Chip (Apple & Stripe Minimalist Style)
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() },
                    shape = CircleShape,
                    color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isDark) Color.White.copy(alpha = 0.20f) else Color(0xFFCBD5E1)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (businessInitial.isNotBlank() && businessInitial.length <= 2) {
                            Text(
                                text = businessInitial.uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                            )
                        } else {
                            Icon(
                                imageVector = Lucide.Store,
                                contentDescription = "Store Profile",
                                tint = if (isDark) Color.White else Color(0xFF0F172A),
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}



