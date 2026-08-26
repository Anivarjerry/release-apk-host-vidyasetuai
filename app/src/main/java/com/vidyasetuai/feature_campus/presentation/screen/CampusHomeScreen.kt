package com.vidyasetuai.feature_campus.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_campus.presentation.component.CampusChatListItem
import com.vidyasetuai.feature_campus.presentation.component.CampusEmerald
import com.vidyasetuai.feature_campus.presentation.component.CampusMuted
import com.vidyasetuai.feature_campus.presentation.component.CampusTopAppBar
import com.vidyasetuai.feature_campus.presentation.viewmodel.CampusViewModel

/**
 * 0ms Offline-First Campus Home Screen (WhatsApp / Telegram style chat list).
 * Modernized with a Left-aligned Campus App Bar and an Always-Visible Slim Search Bar.
 */
@Composable
fun CampusHomeScreen(
    viewModel: CampusViewModel,
    isHindi: Boolean = false,
    modifier: Modifier = Modifier
) {
    val connections by viewModel.filteredConnections.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Silent background sync when navigating to Campus tab
    LaunchedEffect(Unit) {
        viewModel.syncCampusData()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Top App Bar (Left: "Campus", Right: [👤+])
        CampusTopAppBar(
            isHindi = isHindi,
            onOpenRequests = { viewModel.openConnectionsScreen() }
        )

        // 2. Always-Visible Slim Search Bar (WhatsApp / Telegram Style)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Lucide.Search,
                    contentDescription = "Search",
                    tint = CampusMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(CampusEmerald),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = if (isHindi) "खोजें..." else "Search ...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = CampusMuted,
                                    fontSize = 13.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.setSearchQuery("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Clear Search",
                            tint = CampusMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 3. Content Area (Virtualized 60 FPS List / Empty State)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (connections.isEmpty()) {
                // Empty State with Apple Minimalist aesthetic centered perfectly
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(CampusEmerald.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Users,
                            contentDescription = null,
                            tint = CampusEmerald,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (searchQuery.isNotEmpty()) 
                            (if (isHindi) "कोई चैट नहीं मिली" else "No chats found") 
                        else 
                            (if (isHindi) "अभी कोई बातचीत नहीं है" else "No Conversations Yet"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (searchQuery.isNotEmpty())
                            (if (isHindi) "किसी अन्य नाम या @यूज़रनेम से खोजें।" else "Try searching with a different name or @username.")
                        else
                            (if (isHindi) "सहपाठियों, शिक्षकों और कैंपस दोस्तों से सुरक्षित चैट करें।" else "Connect with classmates, teachers, and campus friends with End-to-End Encryption."),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = CampusMuted,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            } else {
                // 120 FPS Virtualized Chat List with Persistent Scroll State
                LazyColumn(
                    state = viewModel.homeListScrollState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                ) {
                    items(
                        items = connections,
                        key = { it.id }
                    ) { connection ->
                        CampusChatListItem(
                            connection = connection,
                            onClick = { viewModel.openChat(connection) }
                        )
                        Divider(
                            modifier = Modifier.padding(start = 76.dp, end = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

