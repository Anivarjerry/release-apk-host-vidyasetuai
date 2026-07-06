package com.vidyasetuai.feature_campus.presentation.screen

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.network.SupabaseStorageHelper
import com.vidyasetuai.feature_campus.domain.model.PrivateMessage
import com.vidyasetuai.feature_campus.presentation.event.CampusEvent
import com.vidyasetuai.feature_campus.presentation.viewmodel.CampusViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatRoomScreen(
    viewModel: CampusViewModel,
    userId: String,
    currentLanguage: String,
    currentTheme: String,
    onBack: () -> Unit,
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
    val partner = state.activePrivateUser ?: return

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var textInput by remember { mutableStateOf("") }
    var imageToCropUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    
    // Zoom and pan states for cropping
    var cropScale by remember { mutableFloatStateOf(1f) }
    var cropOffset by remember { mutableStateOf(Offset.Zero) }

    // Scroll to bottom when messages list size changes
    LaunchedEffect(state.privateMessages.size) {
        if (state.privateMessages.isNotEmpty()) {
            listState.animateScrollToItem(state.privateMessages.size - 1)
        }
    }

    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    LaunchedEffect(isKeyboardOpen) {
        if (isKeyboardOpen && state.privateMessages.isNotEmpty()) {
            delay(150)
            listState.animateScrollToItem(state.privateMessages.size - 1)
        }
    }

    BackHandler(enabled = true) {
        if (selectedImageUrl != null) {
            selectedImageUrl = null
        } else if (imageToCropUri != null) {
            imageToCropUri = null
        } else {
            onBack()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageToCropUri = it
            cropScale = 1f
            cropOffset = Offset.Zero
        }
    }

    val chatBackground = if (isDark) Color.Black else Color(0xFFEFEAE2)

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
                    item {
                        // Expiry Banner inside message list so it scrolls naturally or stays at top
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF1E2229) else Color(0xFFFFFEFC).copy(alpha = 0.9f)
                            ),
                            border = BorderStroke(
                                0.5.dp,
                                if (isDark) Color(0xFF2A3942) else Color(0xFFE1E6EB)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.Info,
                                    contentDescription = null,
                                    tint = Color(0xFF00A884),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) 
                                        "प्राइवेट चैट के संदेश 24 घंटे में स्वतः मिट जाएंगे। स्थायी रूप से रखने के लिए संदेश के पास बने सेव (बुकमार्क) आइकन पर क्लिक करें।" 
                                    else 
                                        "Messages disappear in 24 hours. Tap the save (bookmark) icon to keep a message permanently.",
                                    fontSize = 11.sp,
                                    color = if (isDark) Color(0xFF8696A0) else Color(0xFF54656F),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    items(state.privateMessages) { message ->
                        val isMe = message.senderId == userId
                        PrivateMessageBubbleItem(
                            message = message,
                            isMe = isMe,
                            isDark = isDark,
                            isHindi = isHindi,
                            onSaveToggle = {
                                viewModel.onEvent(
                                    CampusEvent.ToggleSavePrivateMessage(
                                        message.id,
                                        !message.isSaved
                                    )
                                )
                            },
                            onImageClick = { url ->
                                selectedImageUrl = url
                            }
                        )
                    }
                }

                // Media uploading progress bar
                if (state.isUploadingPrivateMedia) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                        color = Color(0xFF00A884)
                    )
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
                        val inputBg = if (isDark) Color(0xFF2A3942) else Color.White

                        // Attachment Plus Button on left (iOS Style)
                        IconButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
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
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (textInput.isEmpty()) {
                                    Text(
                                        text = if (isHindi) "अपना संदेश यहाँ लिखें..." else "Write your message here...",
                                        fontSize = 14.sp,
                                        color = if (isDark) Color(0xFF8696A0) else Color(0xFF8E8E93)
                                    )
                                }
                                BasicTextField(
                                    value = textInput,
                                    onValueChange = { textInput = it },
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

                        Spacer(modifier = Modifier.width(6.dp))

                        // Solid Green Round Send Button
                        val sendEnabled = textInput.isNotBlank() && !state.isUploadingPrivateMedia
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    color = if (sendEnabled) Color(0xFF00A884) else Color(0xFF8696A0).copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                                .clickable(
                                    enabled = sendEnabled,
                                    onClick = {
                                        viewModel.onEvent(
                                            CampusEvent.SendPrivateMessage(
                                                activeUserId = userId,
                                                text = textInput.trim(),
                                                mediaUrl = null
                                            )
                                        )
                                        textInput = ""
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Send,
                                contentDescription = "Send",
                                tint = if (sendEnabled) Color.White else if (isDark) Color(0xFF8696A0) else Color(0xFF54656F),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
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
                    color = if (isDark) Color(0xFF333333) else Color(0xFFE2E8F0)
                ),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .wrapContentSize()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = if (isDark) Color.White else Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7))
                    ) {
                        if (!partner.profilePictureUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = partner.profilePictureUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Lucide.User,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp).align(Alignment.Center)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = partner.fullName ?: partner.username ?: "User",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (partner.isVerified) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Lucide.Check,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF00A884),
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color(0xFF00A884).copy(alpha = 0.1f), CircleShape)
                                        .padding(1.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isHindi) "प्राइवेट चैट" else "Private Chat",
                            fontSize = 9.sp,
                            color = Color(0xFF00A884),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Crop Image Dialog
    if (imageToCropUri != null) {
        val cropTransformState = rememberTransformableState { zoomChange, offsetChange, _ ->
            cropScale = (cropScale * zoomChange).coerceIn(1f, 4f)
            cropOffset += offsetChange
        }

        Dialog(onDismissRequest = { 
            imageToCropUri = null 
        }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isHindi) "फोटो भेजें" else "Send Photo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Square crop window preview container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        AsyncImage(
                            model = imageToCropUri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .transformable(state = cropTransformState)
                                .graphicsLayer(
                                    scaleX = cropScale,
                                    scaleY = cropScale,
                                    translationX = cropOffset.x,
                                    translationY = cropOffset.y
                                ),
                            contentScale = ContentScale.Fit
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Zoom slider indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "—",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        Slider(
                            value = cropScale,
                            onValueChange = { cropScale = it },
                            valueRange = 1f..4f,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF00A884),
                                activeTrackColor = Color(0xFF00A884)
                            )
                        )
                        Text(
                            text = "+",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { imageToCropUri = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = if (isHindi) "रद्द करें" else "Cancel")
                        }
                        
                        Button(
                            onClick = {
                                val uri = imageToCropUri
                                if (uri != null) {
                                    imageToCropUri = null
                                    viewModel.onEvent(CampusEvent.SetUploadingPrivateMedia(true))
                                    coroutineScope.launch {
                                        try {
                                            val croppedBytes = cropBitmapWithTransform(
                                                context = context,
                                                uri = uri,
                                                scale = cropScale,
                                                offsetX = cropOffset.x,
                                                offsetY = cropOffset.y
                                            )
                                            if (croppedBytes != null) {
                                                val fileName = "chats/chat_${System.currentTimeMillis()}.jpg"
                                                val publicUrl = SupabaseStorageHelper.uploadImage(
                                                    "private_chat_media",
                                                    fileName,
                                                    croppedBytes
                                                )
                                                viewModel.onEvent(
                                                    CampusEvent.SendPrivateMessage(
                                                        activeUserId = userId,
                                                        text = null,
                                                        mediaUrl = publicUrl
                                                    )
                                                )
                                            }
                                        } catch (_: Exception) {
                                            // Fail silently or handle
                                        } finally {
                                            viewModel.onEvent(CampusEvent.SetUploadingPrivateMedia(false))
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A884)),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(text = if (isHindi) "भेजें" else "Send", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (selectedImageUrl != null) {
        Dialog(
            onDismissRequest = { selectedImageUrl = null },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            var scale by remember { mutableFloatStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }
            val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
                scale = (scale * zoomChange).coerceIn(1f, 4f)
                offset += offsetChange
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Interactive Image Container
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                ) {
                    AsyncImage(
                        model = selectedImageUrl,
                        contentDescription = "Full Screen Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .transformable(state = transformState)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            ),
                        contentScale = ContentScale.Fit
                    )
                }
                
                // Top control bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { selectedImageUrl = null },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            selectedImageUrl?.let { url ->
                                downloadChatImageToGallery(context, url)
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Download,
                            contentDescription = "Download",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PrivateMessageBubbleItem(
    message: PrivateMessage,
    isMe: Boolean,
    isDark: Boolean,
    isHindi: Boolean,
    onSaveToggle: () -> Unit,
    onImageClick: (String) -> Unit
) {
    // Official WhatsApp iOS colors
    val bubbleColor = if (isMe) {
        if (isDark) Color(0xFF005C4B) else Color(0xFFD9FDD3)
    } else {
        if (isDark) Color(0xFF262626) else Color.White
    }

    val textColor = if (isDark) Color.White else Color(0xFF111B21)
    val timeColor = if (isMe) {
        if (isDark) Color(0xFF8696A0) else Color(0xFF5F7560)
    } else {
        if (isDark) Color(0xFF8696A0) else Color(0xFF667781)
    }

    val formattedTime = remember(message.createdAt) {
        try {
            val cleanTime = message.createdAt.trim().replace(' ', 'T')
            val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = sdfInput.parse(cleanTime)
            if (date != null) {
                val sdfOutput = SimpleDateFormat("hh:mm a", Locale.getDefault())
                sdfOutput.format(date)
            } else {
                ""
            }
        } catch (_: Exception) {
            ""
        }
    }

    val bubbleAlignment = if (isMe) Alignment.End else Alignment.Start

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
            // Bookmark save button on left for incoming messages (to prevent visual overlap)
            if (!isMe) {
                IconButton(
                    onClick = onSaveToggle,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (message.isSaved) Lucide.BookmarkCheck else Lucide.Bookmark,
                        contentDescription = "Save Message",
                        tint = if (message.isSaved) Color(0xFF00A884) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier.size(16.dp)
                    )
                }
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
                    if (!message.mediaUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = message.mediaUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.2f)
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onImageClick(message.mediaUrl) },
                            contentScale = ContentScale.Crop
                        )
                        if (!message.messageText.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    if (!message.messageText.isNullOrEmpty()) {
                        Text(
                            text = message.messageText,
                            color = textColor,
                            fontSize = 14.sp,
                            lineHeight = 18.sp
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

            // Bookmark save button on right for outgoing messages
            if (isMe) {
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onSaveToggle,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (message.isSaved) Lucide.BookmarkCheck else Lucide.Bookmark,
                        contentDescription = "Save Message",
                        tint = if (message.isSaved) Color(0xFF00A884) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Custom lightweight canvas matrix crop logic matching ProfileScreen structure
private fun cropBitmapWithTransform(
    context: android.content.Context,
    uri: Uri,
    scale: Float,
    offsetX: Float,
    offsetY: Float
): ByteArray? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
        
        val bmpWidth = originalBitmap.width
        val bmpHeight = originalBitmap.height
        
        val targetSize = 600
        
        val croppedBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(croppedBitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        
        val matrix = android.graphics.Matrix()
        val fitScale = maxOf(targetSize.toFloat() / bmpWidth, targetSize.toFloat() / bmpHeight)
        
        val dxBase = (targetSize - bmpWidth * fitScale) / 2f
        val dyBase = (targetSize - bmpHeight * fitScale) / 2f
        
        matrix.postScale(fitScale, fitScale)
        matrix.postTranslate(dxBase, dyBase)
        
        matrix.postScale(scale, scale, targetSize / 2f, targetSize / 2f)
        
        val density = context.resources.displayMetrics.density
        val displayWidth = context.resources.displayMetrics.widthPixels
        val approxContainerWidth = displayWidth - (32 * density)
        
        val scaleOffsetFactor = targetSize.toFloat() / approxContainerWidth
        val finalOffsetX = offsetX * scaleOffsetFactor
        val finalOffsetY = offsetY * scaleOffsetFactor
        
        matrix.postTranslate(finalOffsetX, finalOffsetY)
        
        val paint = android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(originalBitmap, matrix, paint)
        
        val outputStream = ByteArrayOutputStream()
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val resultBytes = outputStream.toByteArray()
        
        originalBitmap.recycle()
        croppedBitmap.recycle()
        
        resultBytes
    } catch (_: Exception) {
        null
    }
}

private fun downloadChatImageToGallery(context: Context, imageUrl: String) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    coroutineScope.launch {
        try {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Downloading image...", Toast.LENGTH_SHORT).show()
            }
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(input) ?: throw Exception("Failed to decode image bytes")
            
            val filename = "CampusChat_${System.currentTimeMillis()}.jpg"
            var outputStream: OutputStream? = null
            val resolver = context.contentResolver
            
            val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs()
                }
                val imageFile = java.io.File(imagesDir, filename)
                Uri.fromFile(imageFile)
            }
            
            imageUri?.let { uri ->
                outputStream = resolver.openOutputStream(uri)
            }
            
            outputStream?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
            
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Saved to Pictures Gallery!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Download failed: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
