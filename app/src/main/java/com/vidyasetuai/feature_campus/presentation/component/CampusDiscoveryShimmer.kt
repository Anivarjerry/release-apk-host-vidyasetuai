package com.vidyasetuai.feature_campus.presentation.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Flagship Apple iOS Style Shimmering Skeleton Loader for Campus User Search Cards.
 * Features a buttery smooth linear gradient sweep across avatar, text blocks, and button.
 */
@Composable
fun CampusUserCardShimmer(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "CampusShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )

    val shimmerColors = listOf(
        Color(0xFFE2E8F0).copy(alpha = 0.5f),
        Color(0xFFF1F5F9).copy(alpha = 0.9f),
        Color(0xFFE2E8F0).copy(alpha = 0.5f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 300f, y = translateAnim - 300f),
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Skeleton
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(brush)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text Info Skeletons
            Column(modifier = Modifier.weight(1f)) {
                // Name Skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(brush)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Username Skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .height(11.dp)
                        .clip(RoundedCornerShape(5.5.dp))
                        .background(brush)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Bio Skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(brush)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Action Button Skeleton (Pill)
            Box(
                modifier = Modifier
                    .width(84.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush)
            )
        }
    }
}

@Composable
fun CampusDiscoverySkeletonList(
    count: Int = 5,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(count) {
            CampusUserCardShimmer()
        }
    }
}

/**
 * Flagship Apple iOS Style Shimmering Skeleton Loader for Campus Chat Messages.
 * Alternates incoming (left) and outgoing (right) bubbles for a buttery-smooth 0ms visual transition.
 */
@Composable
fun CampusChatSkeletonList(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "CampusChatShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "chat_shimmer_anim"
    )

    val shimmerColors = listOf(
        Color(0xFFE2E8F0).copy(alpha = 0.5f),
        Color(0xFFF1F5F9).copy(alpha = 0.9f),
        Color(0xFFE2E8F0).copy(alpha = 0.5f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 300f, y = translateAnim - 300f),
        end = Offset(x = translateAnim, y = translateAnim)
    )

    val bubbleConfigs = listOf(
        Pair(false, 0.55f), // Left (Incoming) 55% width
        Pair(true, 0.45f),  // Right (Outgoing) 45% width
        Pair(false, 0.70f), // Left (Incoming) 70% width
        Pair(true, 0.60f),  // Right (Outgoing) 60% width
        Pair(true, 0.35f),  // Right (Outgoing) 35% width
        Pair(false, 0.50f)  // Left (Incoming) 50% width
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        bubbleConfigs.forEach { (isOutgoing, widthFraction) ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = if (isOutgoing) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(widthFraction)
                        .height(44.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp,
                                bottomStart = if (isOutgoing) 18.dp else 4.dp,
                                bottomEnd = if (isOutgoing) 4.dp else 18.dp
                            )
                        )
                        .background(brush)
                )
            }
        }
    }
}

