package com.vidyasetuai.feature_case_study.presentation.screen.subscreen

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.RectangleShape
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuicksFabSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    userId: String,
    repository: QuickRepository,
    checkVerification: (() -> Unit) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardBgColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF8FAFC)
    val textColor = MaterialTheme.colorScheme.onBackground
    val subtitleColor = if (isDark) Color(0xFFA0AEC0) else Color(0xFF718096)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    var quicksList by remember { mutableStateOf<List<Quick>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showCreateForm by remember { mutableStateOf(false) }

    // Creation Form states
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
        offset += offsetChange
    }

    var isSubmitting by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    // Gallery Picker
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            scale = 1f
            offset = Offset.Zero
        }
    }

    fun loadQuicks(showLoader: Boolean = false) {
        if (showLoader) {
            isLoading = true
        }
        coroutineScope.launch {
            repository.getQuicks(userId).onSuccess { list ->
                quicksList = list
            }.onFailure {
                quicksList = emptyList()
            }
            if (showLoader) {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadQuicks(showLoader = true)
    }

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
                        onClick = {
                            if (showCreateForm) {
                                showCreateForm = false
                            } else {
                                onBack()
                            }
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (showCreateForm) {
                            if (isHindi) "क्विक साझा करें" else "Create Quick"
                        } else {
                            if (isHindi) "क्विक एक्शन" else "Quicks"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(AppColors.EmeraldGreen)
                            .clickable {
                                checkVerification {
                                    showCreateForm = !showCreateForm
                                    if (showCreateForm) {
                                        title = ""
                                        description = ""
                                        selectedUri = null
                                        scale = 1f
                                        offset = Offset.Zero
                                        formError = null
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (showCreateForm) Lucide.X else Lucide.Plus,
                            contentDescription = "Create",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(dividerColor)
                )
            }
        },
        containerColor = backgroundColor,
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            if (showCreateForm) {
                // Creator Panel - Scrollable Instagram-style form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    val hasImageSelected = selectedUri != null

                    // 3:4 aspect ratio top box (3 width, 4 height)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f)
                            .background(if (isDark) Color(0xFF161616) else Color(0xFFF7FAFC))
                            .clip(RectangleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasImageSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RectangleShape)
                                    .transformable(state = transformState)
                            ) {
                                AsyncImage(
                                    model = selectedUri,
                                    contentDescription = "Quick Image Preview",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer(
                                            scaleX = scale,
                                            scaleY = scale,
                                            translationX = offset.x,
                                            translationY = offset.y
                                        ),
                                    contentScale = ContentScale.Crop
                                )

                                IconButton(
                                    onClick = {
                                        selectedUri = null
                                        scale = 1f
                                        offset = Offset.Zero
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(12.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                        .size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Lucide.X,
                                        contentDescription = "Clear",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { pickerLauncher.launch("image/*") }
                                    .padding(24.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Camera,
                                    contentDescription = null,
                                    tint = subtitleColor.copy(alpha = 0.7f),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isHindi) "फोटो चुनें (3:4)" else "Select Photo (3:4 Ratio)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = subtitleColor
                                )
                            }
                        }
                    }

                    if (hasImageSelected) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Lucide.ZoomOut, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(14.dp))
                            Slider(
                                value = scale,
                                onValueChange = { scale = it },
                                valueRange = 1f..4f,
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = AppColors.EmeraldGreen,
                                    activeTrackColor = AppColors.EmeraldGreen
                                )
                            )
                            Icon(imageVector = Lucide.ZoomIn, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Borderless TextFields under image
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        PremiumTextField(
                            value = title,
                            onValueChange = { if (it.length <= 80) title = it },
                            placeholder = if (isHindi) "शीर्षक जोड़ें..." else "Add a title...",
                            fontSize = 14,
                            isDark = isDark
                        )
                        Text(
                            text = "${title.length}/80",
                            fontSize = 10.sp,
                            color = subtitleColor.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            textAlign = TextAlign.End
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PremiumTextField(
                            value = description,
                            onValueChange = { if (it.length <= 300) description = it },
                            placeholder = if (isHindi) "विवरण या टिप लिखें..." else "Write a description or tip...",
                            fontSize = 13,
                            singleLine = false,
                            isDark = isDark,
                            modifier = Modifier.height(110.dp)
                        )
                        Text(
                            text = "${description.length}/300",
                            fontSize = 10.sp,
                            color = subtitleColor.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            textAlign = TextAlign.End
                        )

                        if (formError != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = formError!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                if (title.isBlank() || description.isBlank()) {
                                    formError = if (isHindi) "सभी फ़ील्ड आवश्यक हैं" else "All fields are required"
                                    return@Button
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
                                            title = title,
                                            description = description,
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
                                        formError = e.localizedMessage ?: "Failed uploading image to storage"
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = if (isHindi) "प्रकाशित करें (24 घंटे सक्रिय)" else "Publish Quick (Active for 24h)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            } else {
                // List Panel - Normal feed view
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
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(quicksList, key = { it.id }) { quick ->
                            LaunchedEffect(quick.id) {
                                repository.viewQuick(quick.id, userId)
                            }

                            val remainingHours = try {
                                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                                val expiryDate = sdf.parse(quick.expiresAt.substring(0, 19))
                                val diff = expiryDate.time - System.currentTimeMillis()
                                (diff / (1000 * 60 * 60)).coerceAtLeast(0)
                            } catch (e: Exception) {
                                23
                            }

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    if (!quick.coverImageUrl.isNullOrBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(3f / 4f)
                                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                        ) {
                                            AsyncImage(
                                                model = quick.coverImageUrl,
                                                contentDescription = "Quick cover",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(AppColors.EmeraldGreen.copy(alpha = 0.1f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Lucide.User,
                                                    contentDescription = null,
                                                    tint = AppColors.EmeraldGreen,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = quick.authorName,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = textColor
                                                    )
                                                    if (quick.isAuthorVerified) {
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Icon(
                                                            imageVector = Lucide.Check,
                                                            contentDescription = "Verified",
                                                            tint = AppColors.EmeraldGreen,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = "@${quick.authorUsername}",
                                                    fontSize = 11.sp,
                                                    color = subtitleColor
                                                )
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (remainingHours < 4) Color(0xFFEF4444).copy(alpha = 0.15f) else AppColors.EmeraldGreen.copy(alpha = 0.1f))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Lucide.Clock,
                                                    contentDescription = null,
                                                    tint = if (remainingHours < 4) Color(0xFFEF4444) else AppColors.EmeraldGreen,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (isHindi) "${remainingHours}घंटे बचे" else "${remainingHours}h left",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (remainingHours < 4) Color(0xFFEF4444) else AppColors.EmeraldGreen
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = quick.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = quick.description,
                                            fontSize = 13.sp,
                                            color = subtitleColor,
                                            lineHeight = 18.sp
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(0.5.dp)
                                                .background(dividerColor)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Lucide.Eye,
                                                    contentDescription = null,
                                                    tint = subtitleColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${quick.viewsCount} ${if (isHindi) "दृश्य" else "views"}",
                                                    fontSize = 12.sp,
                                                    color = subtitleColor
                                                )
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (quick.isHelpful) AppColors.EmeraldGreen.copy(alpha = 0.15f) else Color.Transparent)
                                                    .clickable {
                                                        coroutineScope.launch {
                                                            repository.toggleHelpful(quick.id, userId).onSuccess {
                                                                loadQuicks(showLoader = false)
                                                            }
                                                        }
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Lucide.ThumbsUp,
                                                    contentDescription = null,
                                                    tint = if (quick.isHelpful) AppColors.EmeraldGreen else subtitleColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${quick.helpfulCount} ${if (isHindi) "मददगार" else "Helpful"}",
                                                    fontSize = 12.sp,
                                                    fontWeight = if (quick.isHelpful) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (quick.isHelpful) AppColors.EmeraldGreen else subtitleColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
