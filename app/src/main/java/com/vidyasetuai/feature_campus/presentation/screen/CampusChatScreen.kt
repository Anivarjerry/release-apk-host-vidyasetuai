package com.vidyasetuai.feature_campus.presentation.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_campus.domain.model.CampusConnection
import com.vidyasetuai.feature_campus.domain.model.CampusMessage
import com.vidyasetuai.feature_campus.presentation.component.*
import com.vidyasetuai.feature_campus.domain.util.CampusDateTimeUtils
import com.vidyasetuai.feature_campus.presentation.viewmodel.CampusViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

/**
 * Flagship WhatsApp / Telegram standard E2EE Chat Screen:
 * - reverseLayout = true (0ms instant bottom-anchored newest messages)
 * - True native keyboard lifting with Scaffold imePadding()
 * - Floating Dynamic Glass Capsule Top & Bottom bars
 * - TimeZone-aware chronological Date Headers
 * - Pillar 5 Mandatory Native Layered BackHandler
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CampusChatScreen(
    connection: CampusConnection,
    viewModel: CampusViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val messages by viewModel.activeMessages.collectAsState()
    val reversedMessages = remember(messages) { messages.asReversed() }

    val peerPresence by viewModel.peerPresence.collectAsState()

    var inputMessageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Long press selected message for Action Sheet
    var selectedMessageForAction by remember { mutableStateOf<CampusMessage?>(null) }

    // Pillar 5: Mandatory Native Back Handler - Dismiss modal first if open
    BackHandler(enabled = true) {
        if (selectedMessageForAction != null) {
            selectedMessageForAction = null
        } else {
            onNavigateBack()
        }
    }

    // Infinite Pagination: Detect when user scrolls up near the top (older messages)
    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = reversedMessages.size
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems >= 40 && lastVisibleIndex >= totalItems - 6
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMoreMessages()
        }
    }

    // Scroll to index 0 (bottom newest message) only if user is at the bottom when a new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty() && listState.firstVisibleItemIndex <= 1) {
            listState.animateScrollToItem(0)
        }
    }


    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            // FLOATING DYNAMIC CAPSULE TOP BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 6.dp)

            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White.copy(alpha = 0.96f),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back Button
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFFFFFFF))
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowLeft,
                                contentDescription = "Back",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Peer Avatar (Room DB Local Path -> Fallback Remote URL -> Fallback Initials)
                        val avatarSource = connection.peerAvatarLocalPath?.takeIf { File(it).exists() } ?: connection.peerAvatarUrl
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (connection.isMutual) CampusEmerald.copy(alpha = 0.12f)
                                    else Color(0xFFF1F5F9)
                                ),
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
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (connection.isMutual) CampusEmerald else Color(0xFF64748B)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Peer Name & Live Presence / E2EE Subtitle
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = connection.peerName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = Color(0xFF0F172A),
                                maxLines = 1
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (peerPresence.isTyping) {
                                    Text(
                                        text = "typing...",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.5.sp,
                                            color = CampusEmerald,
                                            fontWeight = FontWeight.SemiBold,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    )
                                } else if (connection.isMutual) {
                                    Icon(
                                        imageVector = Lucide.ShieldCheck,
                                        contentDescription = null,
                                        tint = CampusEmerald,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "End-to-End Encrypted",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            color = CampusEmerald,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                } else if (connection.status == "BLOCKED") {
                                    Icon(
                                        imageVector = Lucide.ShieldAlert,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "Unavailable",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    )
                                } else {
                                    Icon(
                                        imageVector = Lucide.Clock,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "Pending Connection",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // 3-Dots Options Menu (Uninspire, Block User)
                        var isChatMenuExpanded by remember { mutableStateOf(false) }
                        Box {
                            IconButton(
                                onClick = { isChatMenuExpanded = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFFFFFFF))
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(18.dp)
                                )
                            }


                            DropdownMenu(
                                modifier = Modifier.background(Color.White),
                                offset = DpOffset(12.dp, 12.dp),
                                expanded = isChatMenuExpanded,
                                onDismissRequest = { isChatMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Uninspire",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF0F172A),
                                                fontSize = 13.5.sp
                                            )
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Lucide.Sparkles,
                                            contentDescription = null,
                                            tint = Color(0xFF0F172A),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    onClick = {
                                        isChatMenuExpanded = false
                                        viewModel.unfollowUser(connection.targetUserId)
                                        onNavigateBack()
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Block User",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFEF4444),
                                                fontSize = 13.5.sp
                                            )
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Lucide.Ban,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    onClick = {
                                        isChatMenuExpanded = false
                                        viewModel.blockUser(connection.targetUserId)
                                        onNavigateBack()
                                    }
                                )
                            }


                        }
                    }
                }
            }
        },

        bottomBar = {
            // FLOATING DYNAMIC CAPSULE BOTTOM BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.Transparent)
            ) {
                if (connection.status == "BLOCKED") {
                    // Blocked state capsule
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White.copy(alpha = 0.96f),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Info,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Direct messaging is currently unavailable.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                } else if (!connection.isMutual) {
                    // Non-mutual friend banner capsule
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White.copy(alpha = 0.96f),
                        border = BorderStroke(1.dp, CampusEmerald.copy(alpha = 0.25f)),
                        shadowElevation = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(CampusEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.UserCheck,
                                    contentDescription = null,
                                    tint = CampusEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Mutual connection required",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF0F172A),
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = "You can send messages once ${connection.peerName} follows you back.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                } else {
                    // Mutual Active Floating Input Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Floating Capsule Input Pill
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 46.dp, max = 120.dp),
                            shape = RoundedCornerShape(26.dp),
                            color = Color.White.copy(alpha = 0.96f),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Attachment Icon
                                IconButton(
                                    onClick = { /* Media attachment */ },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Lucide.Paperclip,
                                        contentDescription = "Attach",
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Message Input Field
                                BasicTextField(
                                    value = inputMessageText,
                                    onValueChange = { 
                                        inputMessageText = it
                                        viewModel.onUserInputChanged(it)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 6.dp, vertical = 6.dp),
                                    textStyle = TextStyle(
                                        color = Color(0xFF0F172A),
                                        fontSize = 15.sp
                                    ),
                                    cursorBrush = SolidColor(CampusEmerald),
                                    decorationBox = { innerTextField ->
                                        if (inputMessageText.isEmpty()) {
                                            Text(
                                                text = "Type an encrypted message...",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 14.sp
                                                )
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Floating Emerald Send Button
                        val isTyped = inputMessageText.isNotBlank()
                        IconButton(
                            onClick = {
                                if (isTyped) {
                                    val messageToSend = inputMessageText
                                    inputMessageText = ""
                                    viewModel.onUserInputChanged("")
                                    viewModel.sendMessage(messageToSend)
                                    coroutineScope.launch {
                                        if (reversedMessages.isNotEmpty()) {
                                            listState.animateScrollToItem(0)
                                        }
                                    }
                                }
                            },
                            enabled = isTyped,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isTyped) CampusEmerald else Color(0xFFE2E8F0)
                                )
                        ) {
                            Icon(
                                imageVector = Lucide.Send,
                                contentDescription = "Send",
                                tint = if (isTyped) Color.White else Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // 1. Shimmering Skeleton Loader on initial open (0ms Instant feedback)
                isChatLoading && messages.isEmpty() -> {
                    CampusChatSkeletonList()
                }

                // 2. Empty Chat View
                messages.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (!connection.isMutual) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color.White)
                                    .border(BorderStroke(1.dp, Color(0xFFE2E8F0)), RoundedCornerShape(18.dp))
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Lucide.Users,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        text = "Not Mutual Connections Yet",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                    )
                                    Text(
                                        text = "You are following ${connection.peerName}. Once they follow you back, direct messages will unlock automatically.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF64748B),
                                            fontSize = 12.sp
                                        ),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .border(BorderStroke(1.dp, CampusEmerald.copy(alpha = 0.25f)), RoundedCornerShape(16.dp))
                                    .padding(18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Lucide.ShieldCheck,
                                        contentDescription = null,
                                        tint = CampusEmerald,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "End-to-End Encrypted",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CampusEmerald
                                        )
                                    )
                                    Text(
                                        text = "Messages are secured with AES-256 GCM encryption. Only you and ${connection.peerName} can read them.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF64748B),
                                            fontSize = 12.sp
                                        ),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. 60 FPS Virtualized Messages with reverseLayout = true (WhatsApp / Telegram Gold Standard)
                else -> {
                    LazyColumn(
                        state = listState,
                        reverseLayout = true,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        items(
                            count = reversedMessages.size,
                            key = { index -> reversedMessages[index].id }
                        ) { index ->
                            val currentMsg = reversedMessages[index]

                            // Message Bubble
                            CampusMessageBubble(
                                message = currentMsg,
                                onLongClick = {
                                    selectedMessageForAction = currentMsg
                                }
                            )

                            // In reverseLayout (bottom to top), the older message is at index + 1.
                            // When currentMsg is from a different day than olderMsg (or is the oldest message in chat),
                            // we render the DateHeader above it (which in reverseLayout means right after this item).
                            val showDateHeader = if (index == reversedMessages.lastIndex) {
                                true
                            } else {
                                val olderMsg = reversedMessages[index + 1]
                                CampusDateTimeUtils.getChatLocalDateKey(currentMsg.createdAt) != CampusDateTimeUtils.getChatLocalDateKey(olderMsg.createdAt)
                            }

                            if (showDateHeader) {
                                CampusDateHeader(dateText = CampusDateTimeUtils.formatChatDateHeader(currentMsg.createdAt))
                            }
                        }
                    }
                }
            }
        }
    }


    // LONG PRESS ACTION SHEET MODAL
    selectedMessageForAction?.let { msg ->
        ModalBottomSheet(
            onDismissRequest = { selectedMessageForAction = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Message Options",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Copy Text Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Campus Message", msg.text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Message copied to clipboard", Toast.LENGTH_SHORT).show()
                            selectedMessageForAction = null
                        }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Copy,
                        contentDescription = "Copy",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Copy Text",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = Color(0xFF0F172A)
                    )
                }

                // Star / Unstar Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            viewModel.toggleSaveMessage(msg.id, !msg.isSaved)
                            val toastMsg = if (!msg.isSaved) "Message starred (Won't expire in 24h)" else "Message unstarred"
                            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                            selectedMessageForAction = null
                        }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Star,
                        contentDescription = "Star",
                        tint = if (msg.isSaved) Color(0xFFF59E0B) else Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (msg.isSaved) "Unstar Message" else "Star Message (Keep permanently)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


