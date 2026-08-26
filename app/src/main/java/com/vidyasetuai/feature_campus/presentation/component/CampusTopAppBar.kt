package com.vidyasetuai.feature_campus.presentation.component

import androidx.compose.foundation.background
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
import com.composables.icons.lucide.UserPlus
import com.vidyasetuai.core.ui.colors.AppColors

val CampusEmerald = Color(0xFF10B981)
val CampusEmeraldLight = Color(0xFFECFDF5)
val CampusDark = Color(0xFF0F172A)
val CampusMuted = Color(0xFF64748B)

/**
 * Ultra-Clean Modern Campus Top App Bar.
 * - Left: "Campus" Title (Bold, 20sp, Left-Aligned).
 * - Right: Person with Plus Sign Icon (`Lucide.UserPlus`) with Unread Badge.
 */
@Composable
fun CampusTopAppBar(
    isHindi: Boolean = false,
    onOpenRequests: () -> Unit,
    unreadRequestsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
                text = if (isHindi) "कैंपस (Campus)" else "Campus",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = (-0.5).sp
            )

            // Right Action: UserPlus (Add Friend / Connections)
            Box {
                IconButton(
                    onClick = onOpenRequests,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Lucide.UserPlus,
                        contentDescription = "Campus Friends & Connections",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }

                if (unreadRequestsCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                            .size(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (unreadRequestsCount > 9) "9+" else unreadRequestsCount.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp
                            )
                        )
                    }
                }
            }
        }
    }
}


