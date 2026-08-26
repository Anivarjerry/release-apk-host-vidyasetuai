package com.vidyasetuai.feature_profile.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_profile.domain.model.ProfileInspiration
import com.vidyasetuai.feature_profile.presentation.viewmodel.ProfileEvent
import com.vidyasetuai.feature_profile.presentation.viewmodel.ProfileViewModel
import java.io.File

/**
 * Flagship Inspirations & Inspired List Screen with 60 FPS virtualized rendering and layered BackHandler.
 */
@Composable
fun InspirationsListScreen(
    currentUserId: String = "",
    targetUserId: String = "",
    initialTab: Int = 0,
    currentLanguage: String = "en",
    onBackClick: () -> Unit = {},
    onUserClick: (String) -> Unit = {},
    viewModel: ProfileViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }
    val state by viewModel.uiState.collectAsState()

    val queryId = targetUserId.ifBlank { currentUserId }
    LaunchedEffect(queryId) {
        if (queryId.isNotBlank()) {
            viewModel.onEvent(ProfileEvent.LoadProfile(queryId))
        }
    }

    BackHandler(enabled = true) {
        onBackClick()
    }

    val activeList = if (selectedTab == 0) state.followingList else state.followersList

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                ) {
                    Icon(
                        imageVector = Lucide.ArrowLeft,
                        contentDescription = "Back",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = state.profile?.fullName ?: "Network",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0F172A)
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    0 to "Inspirations (${state.followingList.size})",
                    1 to "Inspired (${state.followersList.size})"
                ).forEach { (tabIndex, title) ->
                    val isSelected = (selectedTab == tabIndex)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedTab = tabIndex },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(
                            text = title,
                            modifier = Modifier.padding(vertical = 10.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isSelected) Color.White else Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (activeList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Users,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = if (selectedTab == 0) "Not following anyone yet" else "No followers yet",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF64748B),
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = activeList,
                        key = { item -> item.id }
                    ) { item ->
                        InspirationCardItem(
                            item = item,
                            onClick = { onUserClick(item.targetUserId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InspirationCardItem(
    item: ProfileInspiration,
    onClick: () -> Unit
) {
    val avatarSource = item.peerAvatarLocalPath?.takeIf { File(it).exists() } ?: item.peerAvatarUrl

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
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
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                if (!avatarSource.isNullOrBlank()) {
                    AsyncImage(
                        model = avatarSource,
                        contentDescription = item.peerName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val initials = item.peerName
                        .split(" ")
                        .filter { it.isNotEmpty() }
                        .take(2)
                        .map { it.first().uppercase() }
                        .joinToString("")
                        .ifEmpty { "U" }

                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.peerName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        ),
                        maxLines = 1
                    )
                    if (item.isVerified) {
                        Icon(
                            imageVector = Lucide.ShieldCheck,
                            contentDescription = "Verified",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = "@${item.peerUsername}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                )
            }

            if (item.isMutual) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFECFDF5),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Users,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Mutual",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        )
                    }
                }
            }
        }
    }
}
