package com.vidyasetuai.feature_case_study.presentation.screen.subscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play
import com.vidyasetuai.feature_case_study.domain.model.Quick

@Composable
fun StackedQuicksCard(
    quicks: List<Quick>,
    isDark: Boolean,
    dividerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (quicks.isEmpty()) return

    val latestQuick = quicks.first()
    val totalCount = quicks.size

    // Determine the number of background pages to draw visually (max 3 layers total)
    val visualStackLayers = minOf(3, totalCount)

    // The outer box must fit the offsets (max 8.dp x, 8.dp y) to prevent clipping
    Box(
        modifier = modifier
            .width(128.dp)
            .height(166.dp)
            .clickable { onClick() }
    ) {
        // Draw background cards first (from back-most to front-most)
        for (i in (visualStackLayers - 1) downTo 1) {
            val offsetVal = (i * 6).dp
            val scaleVal = 1f - (i * 0.015f)
            val alphaVal = 1f - (i * 0.2f)

            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF7FAFC)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .size(width = 115.dp, height = 153.dp)
                    .graphicsLayer {
                        translationX = offsetVal.toPx()
                        translationY = offsetVal.toPx()
                        scaleX = scaleVal
                        scaleY = scaleVal
                        alpha = alphaVal
                    }
                    .border(
                        width = 0.5.dp,
                        color = dividerColor.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                // If the stacked quick has an image, show it blurred or slightly visible
                if (i < quicks.size && !quicks[i].coverImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = quicks[i].coverImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.6f }
                    )
                }
            }
        }

        // Draw top (latest) card
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF8FAFC)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .size(width = 115.dp, height = 153.dp)
                .border(
                    width = 0.5.dp,
                    color = dividerColor.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Cover Image
                if (!latestQuick.coverImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = latestQuick.coverImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Dark gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )

                // Top Creator Info & Stack Count Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Creator Avatar & Username
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    ) {
                        if (!latestQuick.authorProfilePicUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = latestQuick.authorProfilePicUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                            )
                        }
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = latestQuick.authorUsername,
                            fontSize = 8.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (latestQuick.isAuthorVerified) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(6.dp)
                                )
                            }
                        }
                    }

                    // Stack Count Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.9f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (totalCount > 1) "$totalCount" else "1",
                            fontSize = 8.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Middle/Center Play Action Indicator
                Icon(
                    imageVector = Lucide.Play,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )

                // Bottom Title Overlay
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = latestQuick.title,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = "⚡ Quicks",
                        fontSize = 7.5.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
