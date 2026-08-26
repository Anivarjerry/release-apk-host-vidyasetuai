package com.vidyasetuai.feature_campus.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_campus.domain.model.CampusConnection
import com.vidyasetuai.feature_campus.domain.model.CampusConnectionStatus
import com.vidyasetuai.feature_campus.domain.model.CampusSearchResult
import com.vidyasetuai.feature_campus.presentation.component.CampusDiscoverySkeletonList
import com.vidyasetuai.feature_campus.presentation.component.CampusEmerald
import com.vidyasetuai.feature_campus.presentation.viewmodel.CampusViewModel
import java.io.File

/**
 * Flagship Apple Minimalist Campus Discovery & Connections SubScreen.
 * - Top Slim iOS Search Bar with 400ms Debounced remote queries.
 * - Shimmering Skeleton Loader while network results are in-flight.
 * - Social Graph: Inspire (Follow), Inspire Back, Mutual Connection, and 1-tap Direct Chat.
 * - 0ms Offline-First reactive lists from Room DB with 60 FPS virtualized rendering.
 * - Layered BackHandler for smooth native back navigation (Pillar 5 AGENTS.md).
 */
@Composable
fun CampusConnectionsSubScreen(
    viewModel: CampusViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pillar 5: Mandatory Native Back Handler
    BackHandler(enabled = true) {
        onNavigateBack()
    }

    val discoveryQuery by viewModel.discoveryQuery.collectAsState()
    val isDiscoverySearching by viewModel.isDiscoverySearching.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val allConnections by viewModel.allConnections.collectAsState()
    val mutualConnections by viewModel.mutualConnections.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        0 to "Mutual Friends (${mutualConnections.size})",
        1 to "Inspired (${allConnections.size})"
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 1. Top Navigation Bar (Circular Back Button + Title)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
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
                    text = "Campus Discovery & Friends",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0F172A),
                        letterSpacing = (-0.3).sp
                    )
                )
            }

            // 2. Apple iOS Style Slim Search Bar (Always Accessible)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(22.dp))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    BasicTextField(
                        value = discoveryQuery,
                        onValueChange = { viewModel.setDiscoveryQuery(it) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Color(0xFF0F172A),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(Color(0xFF0F172A)),
                        decorationBox = { innerTextField ->
                            if (discoveryQuery.isEmpty()) {
                                Text(
                                    text = "Search by @username, full name or classmate...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 13.sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )

                    if (discoveryQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.clearDiscoverySearch() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Clear",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // 3. Dynamic Content Stream: Search Mode vs Connection List Tabs
            if (discoveryQuery.isNotBlank()) {
                // ==========================================
                // SEARCH DISCOVERY MODE
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when {
                        // A: Shimmering Skeleton Loader while waiting for debounced RPC response
                        isDiscoverySearching -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                CampusDiscoverySkeletonList(count = 6)
                            }
                        }

                        // B: Empty Results or Query too short
                        searchResults.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF1F5F9)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Lucide.UserX,
                                            contentDescription = null,
                                            tint = Color(0xFF64748B),
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }

                                    Text(
                                        text = if (discoveryQuery.trim().length < 2)
                                            "Type at least 2 characters"
                                        else
                                            "No members found for \"$discoveryQuery\"",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF0F172A),
                                            fontSize = 15.sp
                                        ),
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = if (discoveryQuery.trim().length < 2)
                                            "Enter a classmate's name or @username to discover them."
                                        else
                                            "Check the spelling or try searching with their username.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFF64748B),
                                            fontSize = 13.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            }
                        }

                        // C: 60 FPS Virtualized Search Results List
                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    items = searchResults,
                                    key = { it.userId }
                                ) { user ->
                                    CampusSearchResultCard(
                                        user = user,
                                        onToggleInspire = { viewModel.toggleInspire(user) },
                                        onChatClick = {
                                            viewModel.openChatFromSearchResult(user)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // ==========================================
                // TABS MODE: Mutual Friends & Following
                // ==========================================

                // Segmented Pill Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEach { (tabIndex, title) ->
                        val isSelected = (selectedTabIndex == tabIndex)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedTabIndex = tabIndex },
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

                val currentList = if (selectedTabIndex == 0) mutualConnections else allConnections

                if (currentList.isEmpty()) {
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
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Users,
                                    contentDescription = null,
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = if (selectedTabIndex == 0) "No mutual friends yet" else "Not inspiring anyone yet",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F172A),
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = if (selectedTabIndex == 0)
                                    "When you and a classmate inspire each other, you'll become mutual friends and unlock direct chat!"
                                else
                                    "Search campus members using the search bar above to start connecting.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 24.dp)
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
                            items = currentList,
                            key = { it.id }
                        ) { connection ->
                            ConnectionCardItem(
                                connection = connection,
                                onChatClick = {
                                    viewModel.openChat(connection)
                                },
                                onUninspireClick = {
                                    viewModel.unfollowUser(connection.targetUserId)
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}

/**
 * Apple Minimalist Card for Searched Campus User with Inspire Actions.
 */
@Composable
private fun CampusSearchResultCard(
    user: CampusSearchResult,
    onToggleInspire: () -> Unit,
    onChatClick: () -> Unit
) {
    Surface(
        modifier = Modifier
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
            // User Avatar / Initials
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                if (!user.avatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = user.fullName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val initials = user.fullName
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
                            color = Color(0xFF0F172A)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // User Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = user.fullName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        ),
                        maxLines = 1
                    )
                    if (user.isVerified) {
                        Icon(
                            imageVector = Lucide.BadgeCheck,
                            contentDescription = "Verified",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                if (user.username.isNotEmpty()) {
                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        ),
                        maxLines = 1
                    )
                }

                if (!user.bio.isNullOrBlank()) {
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        ),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Action: Inspire / Inspired / Inspire Back / Mutual Chat
            when (user.status) {
                CampusConnectionStatus.MUTUAL -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFECFDF5),
                            border = BorderStroke(1.dp, CampusEmerald.copy(alpha = 0.25f))
                        ) {
                            Text(
                                text = "Mutual",
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CampusEmerald
                                )
                            )
                        }

                        // Premium Smooth Chat Action Pill
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(onClick = onChatClick),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F172A)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.MessageSquare,
                                    contentDescription = "Chat",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Chat",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        }

                        // Uninspire button for Mutual users
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(enabled = !user.isActionInProgress, onClick = onToggleInspire),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = "Uninspire",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier
                                    .padding(horizontal = 6.dp, vertical = 5.dp)
                                    .size(13.dp)
                            )
                        }
                    }
                }


                CampusConnectionStatus.INSPIRES_YOU -> {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !user.isActionInProgress, onClick = onToggleInspire),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0F172A)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (user.isActionInProgress) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(11.dp),
                                    color = Color.White,
                                    strokeWidth = 1.5.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Lucide.Sparkles,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = "Inspire Back",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                CampusConnectionStatus.INSPIRED -> {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !user.isActionInProgress, onClick = onToggleInspire),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (user.isActionInProgress) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(11.dp),
                                    color = Color(0xFF0F172A),
                                    strokeWidth = 1.5.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Lucide.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = "Inspired",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF0F172A)
                                )
                            )
                        }
                    }
                }

                CampusConnectionStatus.NONE -> {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !user.isActionInProgress, onClick = onToggleInspire),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0F172A)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (user.isActionInProgress) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(11.dp),
                                    color = Color.White,
                                    strokeWidth = 1.5.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Lucide.Sparkles,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = "Inspire",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color.White
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
 * Premium Apple Quiet Luxury Connection Card Item.
 */
@Composable
private fun ConnectionCardItem(
    connection: CampusConnection,
    onChatClick: () -> Unit,
    onUninspireClick: () -> Unit
) {

    val avatarSource = connection.peerAvatarLocalPath?.takeIf { File(it).exists() } ?: connection.peerAvatarUrl

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onChatClick),
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
            // Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                if (!avatarSource.isNullOrBlank()) {
                    AsyncImage(
                        model = avatarSource,
                        contentDescription = connection.peerName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
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
                            color = Color(0xFF0F172A)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Peer info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = connection.peerName,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    ),
                    maxLines = 1
                )

                if (connection.peerUsername.isNotEmpty()) {
                    Text(
                        text = "@${connection.peerUsername}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        ),
                        maxLines = 1
                    )
                }
            }

            // Right Actions: Soft "Mutual" badge + Sleek Apple Chat Pill Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (connection.isMutual) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFECFDF5),
                        border = BorderStroke(1.dp, CampusEmerald.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Users,
                                contentDescription = null,
                                tint = CampusEmerald,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Mutual",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CampusEmerald
                                )
                            )
                        }
                    }
                }

                // Sleek Quiet Luxury Chat Button (Small, Smooth, Rounded)
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onChatClick),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F172A)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.MessageSquare,
                            contentDescription = "Chat",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Chat",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = Color.White
                            )
                        )
                    }
                }

                // Clean Minimal 3-Dots Options Menu (Zero Bulky Background)
                var isItemMenuExpanded by remember { mutableStateOf(false) }
                Box {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { isItemMenuExpanded = true }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = isItemMenuExpanded,
                        onDismissRequest = { isItemMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Uninspire",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF0F172A),
                                        fontSize = 13.sp
                                    )
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Lucide.Sparkles,
                                    contentDescription = null,
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(15.dp)
                                )
                            },
                            onClick = {
                                isItemMenuExpanded = false
                                onUninspireClick()
                            }
                        )
                    }
                }


            }
        }
    }
}

