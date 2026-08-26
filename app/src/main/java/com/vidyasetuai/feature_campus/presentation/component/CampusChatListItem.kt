package com.vidyasetuai.feature_campus.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_campus.domain.model.CampusConnection

/**
 * 60 FPS Virtualized Chat List Item for Campus (WhatsApp / Telegram style).
 */
@Composable
fun CampusChatListItem(
    connection: CampusConnection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with Mutual Green Ring
        Box(modifier = Modifier.size(52.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        if (connection.isMutual) CampusEmerald.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                val avatarSource = connection.peerAvatarLocalPath?.takeIf { java.io.File(it).exists() } ?: connection.peerAvatarUrl
                if (!avatarSource.isNullOrBlank()) {
                    coil.compose.AsyncImage(
                        model = avatarSource,
                        contentDescription = connection.peerName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    val initials = connection.peerName
                        .split(" ")
                        .filter { it.isNotEmpty() }
                        .take(2)
                        .map { it.first().uppercase() }
                        .joinToString("")
                        .ifEmpty { "C" }

                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (connection.isMutual) CampusEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // Mutual indicator badge
            if (connection.isMutual) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(CampusEmerald)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Center: Name & Last Message
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = connection.peerName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (connection.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (!connection.lastMessageTime.isNullOrEmpty()) {
                    Text(
                        text = connection.lastMessageTime,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = if (connection.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (connection.unreadCount > 0) CampusEmerald else CampusMuted
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Delivery Tick
                    when (connection.lastMessageStatus) {
                        "PENDING" -> {
                            Icon(
                                imageVector = Lucide.Clock,
                                contentDescription = "Pending",
                                tint = CampusMuted,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        "SENT" -> {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = "Sent",
                                tint = CampusMuted,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        "DELIVERED" -> {
                            Icon(
                                imageVector = Lucide.CheckCheck,
                                contentDescription = "Delivered",
                                tint = CampusMuted,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        "READ" -> {
                            Icon(
                                imageVector = Lucide.CheckCheck,
                                contentDescription = "Read",
                                tint = Color(0xFF3B82F6), // Blue ticks
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        "FAILED" -> {
                            Icon(
                                imageVector = Lucide.CircleAlert,
                                contentDescription = "Failed",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Text(
                        text = connection.lastMessageText ?: "Tap to start conversation",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.5.sp,
                            color = if (connection.unreadCount > 0) MaterialTheme.colorScheme.onSurface else CampusMuted,
                            fontWeight = if (connection.unreadCount > 0) FontWeight.Medium else FontWeight.Normal
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Unread Count Badge
                if (connection.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(CampusEmerald)
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (connection.unreadCount > 99) "99+" else connection.unreadCount.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
