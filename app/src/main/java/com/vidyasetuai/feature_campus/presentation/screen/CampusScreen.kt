package com.vidyasetuai.feature_campus.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_campus.domain.model.PrivateRoom
import com.vidyasetuai.feature_campus.presentation.state.CampusUiState
import com.vidyasetuai.feature_profile.domain.model.UserProfile
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusScreen(
    state: CampusUiState,
    isHindi: Boolean = false,
    onPrivateChatClick: (UserProfile) -> Unit,
    onOpenSearchUserSubScreen: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var isSearchExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Clean Full Width Search Bar (WhatsApp Style)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(
                        color = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Lucide.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = if (isHindi) "कनेक्शन खोजें..." else "Search connections...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Main Content List
        if (state.isLoadingMutual) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppColors.EmeraldGreen)
            }
        } else if (state.mutualInspirations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Lucide.Users,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isHindi) "कोई प्राइवेट कनेक्शन नहीं है" else "No Mutual Connections Yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHindi)
                            "यूज़र्स को खोजें और Inspire करें। जब दोनों एक-दूसरे को Inspire करेंगे तो 1-on-1 चैट चालू हो जाएगी।"
                        else
                            "Search and inspire users. When both inspire each other, 1-on-1 encrypted chat will unlock.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            val filteredMutuals = state.mutualInspirations
                .filter {
                    val name = it.fullName ?: "${it.firstName ?: ""} ${it.lastName ?: ""}"
                    name.contains(searchQuery, ignoreCase = true) || (it.username ?: "").contains(searchQuery, ignoreCase = true)
                }
                .sortedWith(
                    compareByDescending<UserProfile> { user ->
                        val room = state.privateRooms.firstOrNull { r -> (r.user1Id == user.userId || r.user2Id == user.userId) }
                        room?.unreadCount ?: 0
                    }.thenByDescending { user ->
                        val room = state.privateRooms.firstOrNull { r -> (r.user1Id == user.userId || r.user2Id == user.userId) }
                        room?.lastMessageTime ?: ""
                    }
                )

            // WhatsApp-Style Full Width Connection List
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(filteredMutuals) { index, user ->
                    val room = state.privateRooms.firstOrNull { r ->
                        (r.user1Id == user.userId || r.user2Id == user.userId)
                    }

                    WhatsAppUserItemRow(
                        user = user,
                        room = room,
                        isHindi = isHindi,
                        isDark = isDark,
                        onClick = { onPrivateChatClick(user) }
                    )
                    
                    if (index < filteredMutuals.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 76.dp, end = 16.dp)
                                .height(0.8.dp)
                                .background(if (isDark) Color(0xFF2C2C2E) else Color(0xFFE2E8F0))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WhatsAppUserItemRow(
    user: UserProfile,
    room: PrivateRoom? = null,
    isHindi: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 50dp Large Profile Avatar
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(AppColors.EmeraldGreen.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            if (!user.profilePictureUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = "User Pic",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val initial = if (!user.firstName.isNullOrEmpty()) user.firstName.take(1).uppercase() else "U"
                Text(
                    text = initial,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.EmeraldGreen
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.fullName ?: "${user.firstName ?: ""} ${user.lastName ?: ""}".trim(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isDark) Color.White else Color(0xFF1C1C1E),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            val subText = if (!room?.lastMessageText.isNullOrEmpty()) {
                room!!.lastMessageText
            } else if (!user.username.isNullOrEmpty()) {
                "@${user.username}"
            } else {
                if (isHindi) "कोई संदेश नहीं" else "No messages yet"
            }
            Text(
                text = subText!!,
                fontSize = 13.sp,
                color = if (room != null && room.unreadCount > 0) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontWeight = if (room != null && room.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Lucide.Lock,
                    contentDescription = null,
                    tint = AppColors.EmeraldGreen,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isHindi) "एन्क्रिप्टेड 1-on-1 चैट" else "Encrypted 1-on-1 Chat",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.EmeraldGreen
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            if (!room?.lastMessageTime.isNullOrEmpty()) {
                val formattedTime = try {
                    val inst = Instant.parse(room!!.lastMessageTime)
                    val local = inst.atZone(ZoneId.systemDefault())
                    local.format(DateTimeFormatter.ofPattern("hh:mm a"))
                } catch (e: Exception) {
                    ""
                }
                if (formattedTime.isNotEmpty()) {
                    Text(
                        text = formattedTime,
                        fontSize = 11.sp,
                        color = if ((room?.unreadCount ?: 0) > 0) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = if ((room?.unreadCount ?: 0) > 0) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            if (room != null && room.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .background(AppColors.EmeraldGreen, CircleShape)
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${room.unreadCount}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppColors.EmeraldGreen.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.MessageCircle,
                        contentDescription = "Chat",
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
