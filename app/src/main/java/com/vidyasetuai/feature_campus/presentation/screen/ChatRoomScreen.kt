package com.vidyasetuai.feature_campus.presentation.screen

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.composables.icons.lucide.*
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.ui.components.PulsingGreenDot
import com.vidyasetuai.feature_campus.domain.model.CampusMessage
import com.vidyasetuai.feature_campus.presentation.event.CampusEvent
import com.vidyasetuai.feature_campus.presentation.viewmodel.CampusViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Custom Shape for premium WhatsApp-style chat bubbles with a triangular tail
class ChatBubbleShape(val isMe: Boolean) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val tailWidth = with(density) { 6.dp.toPx() }
            val tailHeight = with(density) { 8.dp.toPx() }
            val cornerRadius = with(density) { 12.dp.toPx() }
            
            if (isMe) {
                // Outgoing bubble: tail on the top-right corner
                addRoundRect(
                    RoundRect(
                        rect = Rect(0f, 0f, size.width - tailWidth, size.height),
                        topLeft = CornerRadius(cornerRadius),
                        bottomLeft = CornerRadius(cornerRadius),
                        bottomRight = CornerRadius(cornerRadius),
                        topRight = CornerRadius(0f)
                    )
                )
                moveTo(size.width - tailWidth, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width - tailWidth, tailHeight)
                close()
            } else {
                // Incoming bubble: tail on the top-left corner
                addRoundRect(
                    RoundRect(
                        rect = Rect(tailWidth, 0f, size.width, size.height),
                        topRight = CornerRadius(cornerRadius),
                        bottomLeft = CornerRadius(cornerRadius),
                        bottomRight = CornerRadius(cornerRadius),
                        topLeft = CornerRadius(0f)
                    )
                )
                moveTo(tailWidth, 0f)
                lineTo(0f, 0f)
                lineTo(tailWidth, tailHeight)
                close()
            }
        }
        return Outline.Generic(path)
    }
}

// Custom DrawBehind Modifier to render subtle educational doodles on the canvas
fun Modifier.whatsappWallpaper(isDark: Boolean): Modifier = this.drawBehind {
    val tintColor = if (isDark) Color.White.copy(alpha = 0.015f) else Color.Black.copy(alpha = 0.02f)
    val strokeWidth = 1.5.dp.toPx()
    
    val spacingX = 110.dp.toPx()
    val spacingY = 110.dp.toPx()
    
    val cols = (size.width / spacingX).toInt() + 1
    val rows = (size.height / spacingY).toInt() + 1
    
    for (c in 0 until cols) {
        for (r in 0 until rows) {
            val x = c * spacingX + (if (r % 2 == 0) spacingX / 2 else 0f)
            val y = r * spacingY
            
            when ((c + r) % 5) {
                0 -> { // Speech bubble
                    drawRoundRect(
                        color = tintColor,
                        topLeft = Offset(x, y),
                        size = Size(20.dp.toPx(), 14.dp.toPx()),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                        style = Stroke(width = strokeWidth)
                    )
                    val path = Path().apply {
                        moveTo(x + 3.dp.toPx(), y + 14.dp.toPx())
                        lineTo(x + 1.dp.toPx(), y + 17.dp.toPx())
                        lineTo(x + 6.dp.toPx(), y + 14.dp.toPx())
                        close()
                    }
                    drawPath(path, color = tintColor, style = Stroke(width = strokeWidth))
                }
                1 -> { // Book
                    drawRect(
                        color = tintColor,
                        topLeft = Offset(x, y),
                        size = Size(14.dp.toPx(), 19.dp.toPx()),
                        style = Stroke(width = strokeWidth)
                    )
                    drawLine(
                        color = tintColor,
                        start = Offset(x + 3.dp.toPx(), y + 5.dp.toPx()),
                        end = Offset(x + 11.dp.toPx(), y + 5.dp.toPx()),
                        strokeWidth = strokeWidth
                    )
                    drawLine(
                        color = tintColor,
                        start = Offset(x + 3.dp.toPx(), y + 10.dp.toPx()),
                        end = Offset(x + 11.dp.toPx(), y + 10.dp.toPx()),
                        strokeWidth = strokeWidth
                    )
                }
                2 -> { // Star
                    val path = Path().apply {
                        val sizePx = 15.dp.toPx()
                        val cx = x + sizePx / 2
                        val cy = y + sizePx / 2
                        val points = 5
                        val outerRadius = sizePx / 2
                        val innerRadius = outerRadius / 2
                        var angle = Math.PI / 2
                        val increment = Math.PI / points
                        for (i in 0 until (points * 2)) {
                            val r = if (i % 2 == 0) outerRadius else innerRadius
                            val px = cx + (r * Math.cos(angle)).toFloat()
                            val py = cy - (r * Math.sin(angle)).toFloat()
                            if (i == 0) moveTo(px, py) else lineTo(px, py)
                            angle += increment
                        }
                        close()
                    }
                    drawPath(path, color = tintColor, style = Stroke(width = strokeWidth))
                }
                3 -> { // Graduation Cap
                    val w = 18.dp.toPx()
                    val h = 8.dp.toPx()
                    val path = Path().apply {
                        moveTo(x + w / 2, y)
                        lineTo(x + w, y + h / 2)
                        lineTo(x + w / 2, y + h)
                        lineTo(x, y + h / 2)
                        close()
                    }
                    drawPath(path, color = tintColor, style = Stroke(width = strokeWidth))
                    drawArc(
                        color = tintColor,
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(x + w/4, y + h/2),
                        size = Size(w/2, h),
                        style = Stroke(width = strokeWidth)
                    )
                }
                4 -> { // Heart
                    val path = Path().apply {
                        val w = 15.dp.toPx()
                        val h = 15.dp.toPx()
                        moveTo(x + w / 2, y + h / 4)
                        cubicTo(x + w / 4, y, x, y, x, y + h / 2)
                        cubicTo(x, y + h * 3 / 4, x + w / 2, y + h, x + w / 2, y + h)
                        cubicTo(x + w / 2, y + h, x + w, y + h * 3 / 4, x + w, y + h / 2)
                        cubicTo(x + w, y, x + w * 3 / 4, y, x + w / 2, y + h / 4)
                        close()
                    }
                    drawPath(path, color = tintColor, style = Stroke(width = strokeWidth))
                }
            }
        }
    }
}

// Sealed interface for handling list items with date dividers cleanly
sealed interface ChatUiItem {
    data class DateHeader(val date: String) : ChatUiItem
    data class Message(val message: CampusMessage) : ChatUiItem
}

private fun getUsernameColor(username: String): Color {
    val colors = listOf(
        Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF3F51B5), 
        Color(0xFF009688), Color(0xFF4CAF50), Color(0xFFFF9800), 
        Color(0xFF795548), Color(0xFF00BCD4), Color(0xFFE65100),
        Color(0xFF006064), Color(0xFF1B5E20), Color(0xFF311B92)
    )
    val index = Math.abs(username.hashCode()) % colors.size
    return colors[index]
}

@RequiresApi(Build.VERSION_CODES.O)
private fun parseCreatedAt(createdAt: String): Instant {
    if (createdAt.isEmpty()) return Instant.now()
    val trimmed = createdAt.trim()
    
    // 1. Try ISO-8601 parsing directly (Instant.parse)
    try {
        return Instant.parse(trimmed)
    } catch (e: Exception) {
        // Ignore
    }
    
    // 2. Try normalized ISO-8601 parsing (space replaced by 'T')
    val normalized = trimmed.replace(' ', 'T')
    try {
        return Instant.parse(normalized)
    } catch (e: Exception) {
        // Ignore
    }

    // 3. Try parsing with timezone offset normalization (e.g. +00 to +00:00)
    try {
        if (normalized.matches(Regex(".*[+-]\\d{2}"))) {
            return Instant.parse(normalized + ":00")
        }
    } catch (e: Exception) {
        // Ignore
    }

    // 4. Try parsing as LocalDateTime (assuming UTC if no timezone is provided)
    try {
        val dtString = if (normalized.contains('.')) normalized.substringBefore('.') else normalized
        val localDateTime = java.time.LocalDateTime.parse(dtString)
        return localDateTime.toInstant(java.time.ZoneOffset.UTC)
    } catch (e: Exception) {
        // Ignore
    }

    // 5. Try OffsetDateTime parsing
    try {
        return java.time.OffsetDateTime.parse(normalized).toInstant()
    } catch (e: Exception) {
        // Ignore
    }

    return Instant.now()
}

private fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    viewModel: CampusViewModel,
    userId: String,
    currentLanguage: String,
    currentTheme: String,
    onBack: () -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == "hi"
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (currentTheme) {
        "dark" -> true
        "light" -> false
        else -> isSystemDark
    }

    val view = LocalView.current
    val context = androidx.compose.ui.platform.LocalContext.current
    if (!view.isInEditMode) {
        @Suppress("DEPRECATION")
        SideEffect {
            val activity = (context as? Activity) ?: context.findActivity()
            val window = activity?.window
            if (window != null) {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    BackHandler(onBack = onBack)
    val state by viewModel.state.collectAsState()
    val activeRoom = state.activeRoom ?: return

    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    LaunchedEffect(showToast) {
        if (showToast) {
            delay(2500)
            showToast = false
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val currentUserId by rememberUpdatedState(userId)
    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.onEvent(CampusEvent.ResumeRealtime(currentUserId))
                }
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.onEvent(CampusEvent.PauseRealtime)
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val expandedMessageIds = remember { mutableStateMapOf<String, Boolean>() }

    var selectedReportMessageId by remember { mutableStateOf<String?>(null) }
    var reportReason by remember { mutableStateOf("") }
    var showReportDialog by remember { mutableStateOf(false) }

    // Map raw messages list to list containing date dividers (Today, Yesterday, Date)
    val chatUiItems = remember(state.messages, isHindi) {
        val itemsList = mutableListOf<ChatUiItem>()
        var lastDateStr = ""
        
        state.messages.forEach { message ->
            val dateStr = try {
                val instant = parseCreatedAt(message.createdAt)
                val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
                    .withZone(ZoneId.systemDefault())
                
                val msgDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
                val today = java.time.LocalDate.now()
                val yesterday = today.minusDays(1)
                
                when (msgDate) {
                    today -> if (isHindi) "आज" else "Today"
                    yesterday -> if (isHindi) "कल" else "Yesterday"
                    else -> formatter.format(instant)
                }
            } catch (e: Exception) {
                ""
            }
            
            if (dateStr.isNotEmpty() && dateStr != lastDateStr) {
                itemsList.add(ChatUiItem.DateHeader(dateStr))
                lastDateStr = dateStr
            }
            itemsList.add(ChatUiItem.Message(message))
        }
        itemsList
    }

    LaunchedEffect(chatUiItems.size) {
        if (chatUiItems.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(chatUiItems.size - 1)
            }
        }
    }

    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    LaunchedEffect(isKeyboardOpen) {
        if (isKeyboardOpen && chatUiItems.isNotEmpty()) {
            delay(150)
            listState.animateScrollToItem(chatUiItems.size - 1)
        }
    }

    val roomTitle = activeRoom.name

    // Classic WhatsApp iOS Wallpapers & Colors
    val chatBackground = if (isDark) Color.Black else Color(0xFFEFEAE2)
    val headerColor = if (isDark) Color(0xFF121212) else Color(0xFFF6F6F6)
    val inputBarColor = if (isDark) Color(0xFF121212) else Color(0xFFF6F6F6)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                state.errorMessage?.let { error ->
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        action = {
                            TextButton(onClick = { viewModel.onEvent(CampusEvent.DismissError) }) {
                                Text(
                                    text = if (isHindi) "ठीक है" else "Dismiss",
                                    color = Color(0xFF00A884)
                                )
                            }
                        }
                    ) {
                        Text(text = error, fontSize = 13.sp)
                    }
                }
            }
        ) { innerPadding ->
            val density = LocalDensity.current
            val imePadding = with(density) {
                WindowInsets.ime.getBottom(density).toDp()
            }
            val navBarPadding = with(density) {
                WindowInsets.navigationBars.getBottom(density).toDp()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .background(chatBackground)
                    .whatsappWallpaper(isDark)
                    .padding(bottom = imePadding)
            ) {
                // Message Bubbles Area
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(top = 96.dp, bottom = 10.dp)
                ) {
                items(
                    items = chatUiItems,
                    key = { item ->
                        when (item) {
                            is ChatUiItem.DateHeader -> "date_${item.date}"
                            is ChatUiItem.Message -> "msg_${item.message.id}"
                        }
                    }
                ) { item ->
                    when (item) {
                        is ChatUiItem.DateHeader -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    color = if (isDark) Color(0xFF182229) else Color(0xFFFFFEFC).copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(8.dp),
                                    shadowElevation = 0.5.dp
                                ) {
                                    Text(
                                        text = item.date,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) Color(0xFF8696A0) else Color(0xFF54656F),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                        is ChatUiItem.Message -> {
                            val isMe = item.message.userId == userId
                            val isExpanded = expandedMessageIds[item.message.id] ?: false
                            MessageBubble(
                                message = item.message,
                                isMe = isMe,
                                isDark = isDark,
                                isHindi = isHindi,
                                isExpanded = isExpanded,
                                onToggleExpand = { expandedMessageIds[item.message.id] = !isExpanded },
                                onReportClick = {
                                    selectedReportMessageId = item.message.id
                                    reportReason = ""
                                    showReportDialog = true
                                },
                                onUserClick = onUserClick
                            )
                        }
                    }
                }
            }

            // iOS WhatsApp Styled Input Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (imePadding == 0.dp) navBarPadding else 0.dp),
                color = Color.Transparent,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val inCooldown = state.cooldownSecondsRemaining > 0
                    val inputBg = if (isDark) Color(0xFF2A3942) else Color.White

                    // Attachment Plus Button on left (iOS Style)
                    IconButton(
                        onClick = {
                            toastMessage = if (isHindi) "इमेज शेयरिंग जल्द ही आने वाला है!" else "Image sharing is an upcoming feature!"
                            showToast = true
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Plus,
                            contentDescription = "Attach",
                            tint = if (isDark) Color(0xFF8696A0) else Color(0xFF007AFF),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Input Text Capsule
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 36.dp)
                            .background(inputBg, RoundedCornerShape(18.dp))
                            .border(
                                1.dp,
                                if (isDark) Color(0xFF3B4A54) else Color(0xFFE1E6EB),
                                RoundedCornerShape(18.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (inCooldown) {
                            Icon(
                                imageVector = Lucide.Lock,
                                contentDescription = "Lock",
                                tint = if (isDark) Color(0xFF8696A0) else Color(0xFF8E8E93),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) {
                                    "कृपया ${state.cooldownSecondsRemaining} सेकंड प्रतीक्षा करें..."
                                } else {
                                    "Please wait ${state.cooldownSecondsRemaining}s..."
                                },
                                fontSize = 13.sp,
                                color = if (isDark) Color(0xFF8696A0) else Color(0xFF8E8E93)
                            )
                        } else {
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (state.messageInput.isEmpty()) {
                                    Text(
                                        text = if (isHindi) "अपना संदेश यहाँ लिखें..." else "Write your message here...",
                                        fontSize = 14.sp,
                                        color = if (isDark) Color(0xFF8696A0) else Color(0xFF8E8E93)
                                    )
                                }
                                BasicTextField(
                                    value = state.messageInput,
                                    onValueChange = { viewModel.onEvent(CampusEvent.OnMessageInputChange(it)) },
                                    textStyle = TextStyle(
                                        color = if (isDark) Color.White else Color.Black,
                                        fontSize = 14.sp
                                    ),
                                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                                    maxLines = 4,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Solid Green Round Send Button
                    val sendEnabled = state.messageInput.isNotBlank() && !inCooldown
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                color = if (sendEnabled) Color(0xFF00A884) else Color(0xFF8696A0).copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .clickable(enabled = sendEnabled) {
                                viewModel.onEvent(CampusEvent.SendMessage(userId))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Send,
                            contentDescription = "Send",
                            tint = if (sendEnabled) Color.White else Color(0xFF8696A0).copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // ABUSE WARNING DIALOG
        if (state.showAbuseWarning) {
            Dialog(onDismissRequest = { viewModel.onEvent(CampusEvent.DismissAbuseWarning) }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFFFEBEB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.ShieldAlert,
                                contentDescription = null,
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isHindi) "मर्यादा चेतावनी" else "Content Moderation Warning",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isHindi) {
                                "आपके संदेश में कुछ ऐसे शब्द हैं जो चर्चा नियमों के विरुद्ध हैं। कृपया संयमित एवं मर्यादित भाषा का प्रयोग करें।"
                            } else {
                                "Your message contains keywords that violate our discussion rules. Please use clean and appropriate language."
                            },
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.onEvent(CampusEvent.DismissAbuseWarning) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A884)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .width(140.dp)
                                .height(40.dp)
                        ) {
                            Text(
                                text = if (isHindi) "ठीक है" else "Understood",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // REPORT DIALOG
        if (showReportDialog && selectedReportMessageId != null) {
            Dialog(onDismissRequest = { showReportDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = if (isHindi) "संदेश की रिपोर्ट करें" else "Report Message",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isHindi) "रिपोर्ट का कारण चुनें:" else "Select reason for reporting:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val reasons = listOf(
                            if (isHindi) "अभद्र भाषा या गाली-गलौज" else "Abusive language / hate speech",
                            if (isHindi) "स्पैम या विज्ञापन" else "Spam or advertising",
                            if (isHindi) "अप्रासंगिक / विषय से बाहर" else "Off-topic / irrelevant",
                            if (isHindi) "अन्य" else "Other"
                        )

                        reasons.forEach { reason ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { reportReason = reason }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = reportReason == reason,
                                    onClick = { reportReason = reason },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00A884))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = reason,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { showReportDialog = false },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "रद्द करें" else "Cancel",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    viewModel.onEvent(
                                        CampusEvent.ReportMessage(
                                            messageId = selectedReportMessageId!!,
                                            userId = userId,
                                            reason = reportReason
                                        )
                                    )
                                    showReportDialog = false
                                },
                                enabled = reportReason.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A884)),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "रिपोर्ट करें" else "Submit",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Snackbar is now handled by Scaffold's snackbarHost slot
        }

        // Custom Floating Toast
        AnimatedVisibility(
            visible = showToast,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 12.dp)
                .zIndex(999f)
        ) {
            Surface(
                color = if (isDark) Color(0xFF1E1E1E) else Color.White,
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF333333) else Color(0xFFE2E8F0)
                ),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .wrapContentSize()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Lucide.Image,
                        contentDescription = null,
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = toastMessage,
                        color = if (isDark) Color.White else Color(0xFF1A202C),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Top Fade Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            chatBackground,
                            chatBackground.copy(alpha = 0.9f),
                            chatBackground.copy(alpha = 0.6f),
                            chatBackground.copy(alpha = 0.0f)
                        )
                    )
                )
                .align(Alignment.TopCenter)
                .zIndex(98f)
        )

        // Floating Capsule Header
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .statusBarsPadding()
                .align(Alignment.TopCenter)
                .zIndex(99f)
        ) {
            Surface(
                color = if (isDark) Color.Black else Color.White,
                contentColor = if (isDark) Color.Black else Color.White,
                shape = RoundedCornerShape(24.dp),

                shadowElevation = 6.dp,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isDark) Color.White else Color.Black
                ),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .wrapContentSize()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = if (activeRoom.name.contains("Global", ignoreCase = true)) Lucide.Globe else Lucide.BookOpen
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = roomTitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PulsingGreenDot(modifier = Modifier.size(5.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            val presenceCount = maxOf(1, state.activePresenceCount)
                            Text(
                                text = if (isHindi) "$presenceCount सक्रिय सदस्य" else "$presenceCount active members",
                                fontSize = 9.sp,
                                color = AppColors.EmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessageBubble(
    message: CampusMessage,
    isMe: Boolean,
    isDark: Boolean,
    isHindi: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onReportClick: () -> Unit,
    onUserClick: (String) -> Unit
) {
    // Official WhatsApp iOS colors
    val bubbleColor = if (isMe) {
        if (isDark) Color(0xFF005C4B) else Color(0xFFD9FDD3)
    } else {
        if (isDark) Color(0xFF262626) else Color.White
    }

    val bubbleAlignment = if (isMe) Alignment.End else Alignment.Start
    val textColor = if (isDark) Color.White else Color(0xFF111B21)
    val timeColor = if (isMe) {
        if (isDark) Color(0xFF8696A0) else Color(0xFF5F7560)
    } else {
        if (isDark) Color(0xFF8696A0) else Color(0xFF667781)
    }

    val formattedTime = remember(message.createdAt) {
        try {
            val instant = parseCreatedAt(message.createdAt)
            val formatter = DateTimeFormatter.ofPattern("hh:mm a")
                .withZone(ZoneId.systemDefault())
            formatter.format(instant)
        } catch (e: Exception) {
            ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalAlignment = bubbleAlignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isMe) {
                Icon(
                    imageVector = Lucide.Flag,
                    contentDescription = "Report",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onReportClick() }
                        .padding(2.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            val tailWidthPadding = 6.dp
            Surface(
                color = bubbleColor,
                shape = ChatBubbleShape(isMe = isMe),
                shadowElevation = 1.dp,
                modifier = Modifier.widthIn(max = 285.dp)
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = if (isMe) 10.dp else 10.dp + tailWidthPadding,
                        end = if (isMe) 10.dp + tailWidthPadding else 10.dp,
                        top = 6.dp,
                        bottom = 6.dp
                    )
                ) {
                    if (!isMe) {
                        val senderName = message.senderUsername ?: "User"
                        Text(
                            text = senderName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = getUsernameColor(senderName),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable { onUserClick(message.userId) }
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    var isCollapsible by remember(message.id) { mutableStateOf(false) }

                    Text(
                        text = message.content,
                        fontSize = 14.sp,
                        color = textColor,
                        lineHeight = 18.sp,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                        onTextLayout = { textLayoutResult ->
                            if (!isExpanded) {
                                isCollapsible = textLayoutResult.didOverflowHeight || textLayoutResult.lineCount > 4
                            }
                        }
                    )

                    if (isCollapsible) {
                        Text(
                            text = if (isExpanded) {
                                if (isHindi) "कम दिखाएं" else "See Less"
                            } else {
                                if (isHindi) "और दिखाएं" else "See More"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMe) {
                                if (isDark) Color(0xFF53BDEB) else Color(0xFF007AFF)
                            } else {
                                if (isDark) Color(0xFF8696A0) else Color(0xFF00A884)
                            },
                            modifier = Modifier
                                .clickable { onToggleExpand() }
                                .padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formattedTime,
                            fontSize = 9.sp,
                            color = timeColor
                        )
                        if (isMe) {
                            Spacer(modifier = Modifier.width(3.dp))
                            if (message.isFailed) {
                                Icon(
                                    imageVector = Lucide.CircleAlert,
                                    contentDescription = "Failed",
                                    tint = Color.Red,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (isHindi) "विफल" else "Failed",
                                    fontSize = 8.sp,
                                    color = Color.Red
                                )
                            } else if (!message.isSynced) {
                                Icon(
                                    imageVector = Lucide.Clock,
                                    contentDescription = "Pending",
                                    tint = Color(0xFF8696A0),
                                    modifier = Modifier.size(9.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Lucide.Check,
                                    contentDescription = "Synced",
                                    tint = Color(0xFF53BDEB),
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
