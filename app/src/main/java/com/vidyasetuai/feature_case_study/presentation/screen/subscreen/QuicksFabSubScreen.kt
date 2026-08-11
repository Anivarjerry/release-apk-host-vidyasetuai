package com.vidyasetuai.feature_case_study.presentation.screen.subscreen

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed as gridItemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.network.SupabaseStorageHelper
import com.vidyasetuai.feature_case_study.data.repository.QuickRepository
import com.vidyasetuai.feature_case_study.domain.model.Quick
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import kotlinx.coroutines.launch
import java.util.*

/**
 * WhatsApp / Instagram Story Style Create Quick Screen & Subscreen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuicksFabSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    userId: String,
    repository: QuickRepository,
    checkVerification: (() -> Unit) -> Unit,
    onQuickClick: (List<Quick>, Int) -> Unit,
    cachedQuicksList: List<Quick>,
    onQuicksListChange: (List<Quick>) -> Unit,
    isQuicksLoaded: Boolean,
    onQuicksLoadedChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    initialImageUri: Uri? = null
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardBgColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF8FAFC)
    val textColor = MaterialTheme.colorScheme.onBackground
    val subtitleColor = if (isDark) Color(0xFFA0AEC0) else Color(0xFF718096)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    val quicksList = cachedQuicksList
    var isLoading by remember { mutableStateOf(!isQuicksLoaded) }

    // Creation Form states
    var showCreateForm by remember(initialImageUri) { mutableStateOf(initialImageUri != null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedUri by remember(initialImageUri) { mutableStateOf<Uri?>(initialImageUri) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
        offset += offsetChange
    }

    var isSubmitting by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    // Gallery Picker Launcher
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            scale = 1f
            offset = Offset.Zero
            showCreateForm = true
        }
    }

    fun loadQuicks(showLoader: Boolean = false) {
        if (showLoader) {
            isLoading = true
        }
        coroutineScope.launch {
            repository.getQuicks(userId).onSuccess { list ->
                onQuicksListChange(list)
            }.onFailure {
                onQuicksListChange(emptyList())
            }
            if (showLoader) {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!isQuicksLoaded) {
            loadQuicks(showLoader = true)
            onQuicksLoadedChange(true)
        }
    }

    // Fullscreen WhatsApp Status / Instagram Story Style Creator View
    if (showCreateForm) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // 1. Story Canvas Preview (Natural ContentScale.Fit - No Crop)
            if (selectedUri != null) {
                AsyncImage(
                    model = selectedUri,
                    contentDescription = "Selected Quick Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .transformable(state = transformState)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                )
            } else {
                // Empty Image Selector Canvas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { pickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(AppColors.EmeraldGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Camera,
                                contentDescription = "Camera",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isHindi) "गैलरी से फ़ोटो चुनें" else "Select Photo from Gallery",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHindi) "अपनी स्टोरी / क्विक साझा करें" else "Share your Quick story",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Top Control Bar (Close X button + Image change tool)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        showCreateForm = false
                        selectedUri = null
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Change Image Tool
                    IconButton(
                        onClick = { pickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Lucide.Image,
                            contentDescription = "Change Image",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Reset Zoom Tool
                    if (scale != 1f) {
                        IconButton(
                            onClick = {
                                scale = 1f
                                offset = Offset.Zero
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Lucide.RotateCcw,
                                contentDescription = "Reset Zoom",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Caption & Control Glass Panel (WhatsApp Status Style)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.95f))
                        .padding(16.dp)
                ) {
                    Column {
                        // Title Input Field
                        OutlinedTextField(
                            value = title,
                            onValueChange = { if (it.length <= 80) title = it },
                            placeholder = {
                                Text(
                                    text = if (isHindi) "शीर्षक जोड़ें..." else "Add a title...",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color.White.copy(alpha = 0.15f)))

                        // Description Input Field
                        OutlinedTextField(
                            value = description,
                            onValueChange = { if (it.length <= 300) description = it },
                            placeholder = {
                                Text(
                                    text = if (isHindi) "कैप्शन या टिप लिखें..." else "Add a caption or description...",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 13.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (formError != null) {
                            Text(
                                text = formError!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bottom Actions Row: 24h Expiry Badge (Left) + Floating Send Button (Right)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 24h Expiry Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AppColors.EmeraldGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Lucide.Zap,
                                        contentDescription = null,
                                        tint = AppColors.EmeraldGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isHindi) "24 घंटे सक्रिय" else "Active for 24h",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.EmeraldGreen
                                    )
                                }
                            }

                            // Circular Send Floating Action Button
                            IconButton(
                                onClick = {
                                    if (selectedUri == null && title.isBlank() && description.isBlank()) {
                                        formError = if (isHindi) "कम से कम फ़ोटो या विवरण आवश्यक है" else "Add photo, title, or description"
                                        return@IconButton
                                    }
                                    isSubmitting = true
                                    formError = null

                                    coroutineScope.launch {
                                        try {
                                            var finalImageUrl: String? = null

                                            if (selectedUri != null) {
                                                val inputStream = context.contentResolver.openInputStream(selectedUri!!)
                                                val bytes = inputStream?.readBytes()
                                                if (bytes != null) {
                                                    val fileName = "quicks/${UUID.randomUUID()}.jpg"
                                                    finalImageUrl = SupabaseStorageHelper.uploadImage("media", fileName, bytes)
                                                }
                                            }

                                            val result = repository.createQuick(
                                                title = title.trim(),
                                                description = description.trim(),
                                                coverImageUrl = finalImageUrl,
                                                authorUserId = userId
                                            )
                                            isSubmitting = false
                                            result.onSuccess {
                                                title = ""
                                                description = ""
                                                selectedUri = null
                                                showCreateForm = false
                                                loadQuicks(showLoader = true)
                                            }.onFailure { e ->
                                                formError = e.localizedMessage ?: "Failed to publish quick"
                                            }
                                        } catch (e: Exception) {
                                            isSubmitting = false
                                            formError = e.localizedMessage ?: "Failed uploading image"
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(AppColors.EmeraldGreen, CircleShape),
                                enabled = !isSubmitting
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                                } else {
                                    Icon(
                                        imageVector = Lucide.Send,
                                        contentDescription = "Post Quick",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Normal Quicks List View
        Scaffold(
            topBar = {
                Column(modifier = Modifier.fillMaxWidth().background(backgroundColor)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .height(50.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowLeft,
                                contentDescription = "Back",
                                tint = textColor
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = if (isHindi) "क्विक्स (Quicks)" else "Quicks",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(dividerColor.copy(alpha = 0.3f)))
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { pickerLauncher.launch("image/*") },
                    containerColor = AppColors.EmeraldGreen,
                    contentColor = Color.White
                ) {
                    Icon(imageVector = Lucide.Plus, contentDescription = "Add Quick")
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor)
            ) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppColors.EmeraldGreen)
                    }
                } else if (quicksList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                            Icon(
                                imageVector = Lucide.Zap,
                                contentDescription = null,
                                tint = subtitleColor.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isHindi) "कोई भी सक्रिय क्विक उपलब्ध नहीं है" else "No Active Quicks Available",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isHindi) "क्विक 24 घंटे के बाद स्वचालित रूप से समाप्त हो जाते हैं।" else "Quicks automatically expire after 24 hours.",
                                fontSize = 12.sp,
                                color = subtitleColor,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { pickerLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Lucide.Plus, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = if (isHindi) "पहला क्विक पोस्ट करें" else "Post First Quick")
                            }
                        }
                    }
                } else {
                    val groupedQuicks = remember(quicksList) {
                        quicksList.groupBy { it.authorUserId }.values.toList()
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        gridItemsIndexed(groupedQuicks, key = { _, stack -> "fab-quick-stack-${stack.first().authorUserId}" }) { index, stack ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                StackedQuicksCard(
                                    quicks = stack,
                                    isDark = isDark,
                                    dividerColor = dividerColor,
                                    onClick = { onQuickClick(stack, 0) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
