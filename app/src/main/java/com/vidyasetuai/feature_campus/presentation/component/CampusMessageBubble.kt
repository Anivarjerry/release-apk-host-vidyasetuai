package com.vidyasetuai.feature_campus.presentation.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_campus.domain.model.CampusMessage

val OutgoingBubbleColor = Color(0xFFDCFCE7) // Emerald-50 / Apple Chat green tint
val IncomingBubbleColor = Color(0xFFF1F5F9) // Slate-100 / Light neutral

private const val LONG_MESSAGE_CHAR_LIMIT = 200
private const val LONG_MESSAGE_MAX_LINES = 4

/**
 * Enterprise WhatsApp/Telegram grade Message Bubble with:
 * - Read more / Read less toggle with strict maxLines = 4 vertical capping
 * - Delivery tick lifecycle (Pending, Sent, Delivered, Read, Failed)
 * - Starred indicators & Haptic long-press support
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CampusMessageBubble(
    message: CampusMessage,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOutgoing = message.isOutgoing
    var isExpanded by remember { mutableStateOf(false) }

    val isLongMessage = remember(message.text) {
        message.text.length > LONG_MESSAGE_CHAR_LIMIT || message.text.count { it == '\n' } >= LONG_MESSAGE_MAX_LINES
    }

    val bubbleShape = if (isOutgoing) {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 16.dp,
            bottomEnd = 3.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 3.dp,
            bottomEnd = 16.dp
        )
    }

    val backgroundColor = if (isOutgoing) {
        if (message.status == "FAILED") Color(0xFFFEE2E2) // Soft Red tint for failed
        else OutgoingBubbleColor
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    }

    val contentColor = if (isOutgoing) {
        if (message.status == "FAILED") Color(0xFF991B1B)
        else Color(0xFF064E3B)
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isOutgoing) 48.dp else 12.dp,
                end = if (isOutgoing) 12.dp else 48.dp,
                top = 2.dp,
                bottom = 2.dp
            ),
        contentAlignment = if (isOutgoing) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .clip(bubbleShape)
                .background(backgroundColor)
                .combinedClickable(
                    onClick = {
                        if (isLongMessage) isExpanded = !isExpanded
                    },
                    onLongClick = onLongClick
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Text Message Content with strict maxLines capping
            if (isLongMessage && !isExpanded) {
                Text(
                    text = message.text,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                        color = contentColor
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Read more",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOutgoing) Color(0xFF047857) else CampusEmerald
                    )
                )
            } else {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                        color = contentColor
                    )
                )
                if (isLongMessage) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Read less",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOutgoing) Color(0xFF047857) else CampusEmerald
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Footer: Star + Timestamp + Delivery Tick
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (message.isSaved) {
                    Icon(
                        imageVector = Lucide.Star,
                        contentDescription = "Starred",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(11.dp)
                    )
                }

                Text(
                    text = message.createdAtFormatted,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                )

                if (isOutgoing) {
                    when (message.status) {
                        "PENDING" -> {
                            Icon(
                                imageVector = Lucide.Clock,
                                contentDescription = "Pending",
                                tint = contentColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        "SENT" -> {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = "Sent",
                                tint = contentColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        "DELIVERED" -> {
                            Icon(
                                imageVector = Lucide.CheckCheck,
                                contentDescription = "Delivered",
                                tint = contentColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        "READ" -> {
                            Icon(
                                imageVector = Lucide.CheckCheck,
                                contentDescription = "Read",
                                tint = Color(0xFF2563EB), // Double blue ticks
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        "FAILED" -> {
                            Icon(
                                imageVector = Lucide.CircleAlert,
                                contentDescription = "Delivery Failed",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Failed",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = Color(0xFFDC2626),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Centered Floating Date Badge (Today, Yesterday, 24 Aug 2026).
 */
@Composable
fun CampusDateHeader(
    dateText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            tonalElevation = 1.dp
        ) {
            Text(
                text = dateText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}
