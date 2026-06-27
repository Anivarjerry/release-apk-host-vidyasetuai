package com.vidyasetuai.feature_campus.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.ui.components.PulsingGreenDot
import com.vidyasetuai.feature_campus.domain.model.CampusRoom
import com.vidyasetuai.feature_campus.presentation.viewmodel.CampusViewModel

@Composable
fun CampusScreen(
    viewModel: CampusViewModel,
    userId: String,
    currentLanguage: String,
    currentTheme: String,
    onRoomClick: (CampusRoom) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == "hi"
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (currentTheme) {
        "dark" -> true
        "light" -> false
        else -> isSystemDark
    }
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar Mockup
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Lucide.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "चर्चा कक्ष खोजें..." else "Search discussion rooms...",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Active header
        Text(
            text = if (isHindi) "सक्रिय चर्चा समूह" else "Active Discussion Channels",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.EmeraldGreen
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (state.isLoadingRooms) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppColors.EmeraldGreen)
            }
        } else if (state.rooms.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isHindi) "कोई चर्चा समूह उपलब्ध नहीं है।" else "No discussion channels available.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.rooms) { room ->
                    RoomCard(
                        room = room,
                        isHindi = isHindi,
                        isDark = isDark,
                        onClick = { onRoomClick(room) }
                    )
                }
            }
        }
    }
}

@Composable
fun RoomCard(
    room: CampusRoom,
    isHindi: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    // Assign appropriate icons based on room names
    val icon = when {
        room.name.contains("Global", ignoreCase = true) -> Lucide.Globe
        room.name.contains("Study", ignoreCase = true) -> Lucide.BookOpen
        else -> Lucide.MessageCircle
    }

    val name = room.name
    val cooldownText = if (isHindi) {
        " (${room.messageCooldownSeconds} सेकंड कूलडाउन)"
    } else {
        " (${room.messageCooldownSeconds}s cooldown)"
    }
    val desc = (room.description ?: "") + (if (room.messageCooldownSeconds > 0) cooldownText else "")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Container
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.EmeraldGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Text details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Active members count indicators
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                PulsingGreenDot(modifier = Modifier.size(8.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isHindi) "लाइव" else "Live",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.EmeraldGreen
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(26.dp)
                .background(
                    color = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Lucide.ChevronRight,
                contentDescription = "Join",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
