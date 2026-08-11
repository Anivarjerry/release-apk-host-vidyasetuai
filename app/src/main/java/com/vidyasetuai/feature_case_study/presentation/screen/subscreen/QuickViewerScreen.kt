package com.vidyasetuai.feature_case_study.presentation.screen.subscreen

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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_case_study.data.repository.QuickRepository
import com.vidyasetuai.feature_case_study.domain.model.Quick
import com.vidyasetuai.feature_profile.data.remote.dto.UserProfileDto
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

private fun downloadImageToGallery(context: Context, imageUrl: String, title: String) {
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
            
            val filename = "Quick_${title.replace(Regex("[^a-zA-Z0-9]"), "_")}_${System.currentTimeMillis()}.jpg"
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuickViewerScreen(
    quicks: List<Quick>,
    initialIndex: Int,
    currentLanguage: String,
    userId: String,
    repository: QuickRepository,
    onBack: () -> Unit,
    onAuthorClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (quicks.isEmpty()) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isHindi = currentLanguage == "hi"

    val segmentSize = 4
    var activeItems by remember(quicks) { mutableStateOf(quicks.take(segmentSize)) }

    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, activeItems.size - 1),
        pageCount = { activeItems.size }
    )

    // Progress bar value for active story (0.0f to 1.0f over 10 seconds)
    var storyProgress by remember { mutableStateOf(0f) }
    var isCurrentStoryPaused by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Self Story Insights & Viewers Bottom Sheet State
    var showInsightsSheet by remember { mutableStateOf(false) }
    var activeInsightsTab by remember { mutableStateOf(0) } // 0 = Viewers, 1 = Helpful Users
    var viewersList by remember { mutableStateOf<List<UserProfileDto>>(emptyList()) }
    var helpfulsList by remember { mutableStateOf<List<UserProfileDto>>(emptyList()) }
    var isLoadingInsights by remember { mutableStateOf(false) }
    val currentActiveQuick = activeItems.getOrNull(pagerState.currentPage) ?: quicks.first()

    // Handle system back button to close Analytics Bottom Sheet first if open
    BackHandler(enabled = showInsightsSheet) {
        showInsightsSheet = false
        isCurrentStoryPaused = false
    }

    LaunchedEffect(pagerState.currentPage) {
        storyProgress = 0f
        isCurrentStoryPaused = false
        if (pagerState.currentPage >= activeItems.size - 2 && activeItems.size < quicks.size) {
            val nextSize = minOf(quicks.size, activeItems.size + segmentSize)
            activeItems = quicks.take(nextSize)
        }
    }

    // Top-Level 10-Second Story Auto-Advance Timer (Runs cleanly without cancellation during scroll)
    LaunchedEffect(pagerState.currentPage, isCurrentStoryPaused) {
        if (!isCurrentStoryPaused) {
            val stepMillis = 50L
            val totalMillis = 10000L // 10 seconds per story
            val increment = stepMillis.toFloat() / totalMillis.toFloat()

            while (storyProgress < 1.0f) {
                delay(stepMillis)
                storyProgress += increment
            }

            val nextPage = pagerState.currentPage + 1
            if (nextPage < quicks.size) {
                storyProgress = 0f
                if (nextPage >= activeItems.size) {
                    val nextSize = minOf(quicks.size, activeItems.size + segmentSize)
                    activeItems = quicks.take(nextSize)
                }
                coroutineScope.launch {
                    pagerState.animateScrollToPage(nextPage)
                }
            } else {
                onBack()
            }
        }
    }

    BackHandler(onBack = onBack)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val quick = activeItems.getOrNull(page) ?: return@HorizontalPager
            var localQuickState by remember(quick.id) { mutableStateOf(quick) }
            var isDescriptionExpanded by remember { mutableStateOf(false) }

            // Log view when page becomes active (Only for OTHER users, self views are not logged)
            LaunchedEffect(pagerState.currentPage) {
                if (pagerState.currentPage == page && quick.authorUserId != userId) {
                    repository.viewQuick(quick.id, userId)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // 1. Fullscreen Image in natural aspect ratio (ContentScale.Fit - No Crop)
                if (!localQuickState.coverImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = localQuickState.coverImageUrl,
                        contentDescription = "Quick Cover Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        AppColors.EmeraldGreen.copy(alpha = 0.6f),
                                        Color(0xFF1E293B)
                                    )
                                )
                            )
                    )
                }

                // Top Vignette Shader
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                            )
                        )
                        .align(Alignment.TopCenter)
                )

                // Bottom Vignette Shader
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                            )
                        )
                        .align(Alignment.BottomCenter)
                )

                // 2. Gesture tap zones (Left 35% goes back, Right 65% goes forward)
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(3.5f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                storyProgress = 0f
                                if (pagerState.currentPage > 0) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                } else {
                                    onBack()
                                }
                            }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(6.5f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                storyProgress = 0f
                                val nextPage = pagerState.currentPage + 1
                                if (nextPage < quicks.size) {
                                    if (nextPage >= activeItems.size) {
                                        val nextSize = minOf(quicks.size, activeItems.size + segmentSize)
                                        activeItems = quicks.take(nextSize)
                                    }
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(nextPage)
                                    }
                                } else {
                                    onBack()
                                }
                            }
                    )
                }

                // 3. Floating Overlay Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    val currentAuthorUserId = localQuickState.authorUserId
                    val currentAuthorQuicks = remember(currentAuthorUserId, activeItems) {
                        activeItems.filter { it.authorUserId == currentAuthorUserId }
                    }
                    val relativeIndex = remember(currentAuthorUserId, localQuickState.id, currentAuthorQuicks) {
                        currentAuthorQuicks.indexOfFirst { it.id == localQuickState.id }.coerceAtLeast(0)
                    }

                    // Segmented Story Indicator bars (Per-author story count like WhatsApp/Instagram)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(currentAuthorQuicks.size) { index ->
                            val barFillFraction = when {
                                index < relativeIndex -> 1f
                                index == relativeIndex -> storyProgress.coerceIn(0f, 1f)
                                else -> 0f
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.White.copy(alpha = 0.35f))
                            ) {
                                if (barFillFraction > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(barFillFraction)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color.White)
                                    )
                                }
                            }
                        }
                    }

                    // Top Bar Info: Creator avatar, Name, X-back, Download button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowLeft,
                                contentDescription = "Close Viewer",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))

                        // Creator Details - Clickable to open Public Profile
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onAuthorClick(localQuickState.authorUserId) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!localQuickState.authorProfilePicUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = localQuickState.authorProfilePicUrl,
                                        contentDescription = "Author Pic",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Lucide.User,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = localQuickState.authorName,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (localQuickState.isAuthorVerified) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF2196F3)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Lucide.Check,
                                                contentDescription = "Verified",
                                                tint = Color.White,
                                                modifier = Modifier.size(8.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = if (localQuickState.authorUserId == userId) {
                                        if (isHindi) "आपका क्विक" else "Your Quick"
                                    } else {
                                        "@${localQuickState.authorUsername}"
                                    },
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Download Image Button
                            if (!localQuickState.coverImageUrl.isNullOrBlank()) {
                                IconButton(
                                    onClick = { downloadImageToGallery(context, localQuickState.coverImageUrl!!, localQuickState.title) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Lucide.Download,
                                        contentDescription = "Download Image",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Delete Quick Button (Only for own Quick - styled like Download button with red icon)
                            if (localQuickState.authorUserId == userId) {
                                IconButton(
                                    onClick = {
                                        isCurrentStoryPaused = true
                                        showDeleteDialog = true
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Lucide.Trash2,
                                        contentDescription = "Delete Quick",
                                        tint = Color.Red,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Confirmation Dialog for Quick Deletion
                        if (showDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = {
                                    showDeleteDialog = false
                                    isCurrentStoryPaused = false
                                },
                                title = {
                                    Text(
                                        text = if (isHindi) "क्विक हटाएं?" else "Delete Quick?",
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                text = {
                                    Text(
                                        text = if (isHindi) "क्या आप वाकई इस क्विक को हटाना चाहते हैं?" else "Are you sure you want to delete this Quick?"
                                    )
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showDeleteDialog = false
                                            coroutineScope.launch {
                                                repository.deleteQuick(localQuickState.id)
                                                Toast.makeText(context, if (isHindi) "क्विक हटा दिया गया" else "Quick deleted", Toast.LENGTH_SHORT).show()
                                                val nextPage = pagerState.currentPage + 1
                                                if (nextPage < quicks.size) {
                                                    pagerState.animateScrollToPage(nextPage)
                                                } else {
                                                    onBack()
                                                }
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = if (isHindi) "हटाएं" else "Delete",
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            showDeleteDialog = false
                                            isCurrentStoryPaused = false
                                        }
                                    ) {
                                        Text(text = if (isHindi) "रद्द करें" else "Cancel")
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Bottom Panel containing title, description & Helpful action
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (!localQuickState.title.isNullOrBlank()) {
                            Text(
                                text = localQuickState.title,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        if (!localQuickState.description.isNullOrBlank()) {
                            // Custom show-more expander (Pauses 10s story timer when expanded)
                            val words = localQuickState.description.trim().split(" ")
                            val isLongDescription = words.size > 4
                            val shortDesc = if (isLongDescription) {
                                words.take(4).joinToString(" ")
                            } else {
                                localQuickState.description
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        if (isLongDescription) {
                                            isDescriptionExpanded = !isDescriptionExpanded
                                            isCurrentStoryPaused = isDescriptionExpanded
                                        }
                                    }
                            ) {
                                Text(
                                    text = if (isDescriptionExpanded || !isLongDescription) {
                                        localQuickState.description
                                    } else {
                                        shortDesc
                                    },
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )
                                if (isLongDescription) {
                                    Text(
                                        text = if (isDescriptionExpanded) {
                                            if (isHindi) "कम दिखाएं ↑" else "Show Less ↑"
                                        } else {
                                            if (isHindi) "... और देखें →" else "... Show More →"
                                        },
                                        color = AppColors.EmeraldGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bottom Action: Self Insights Button OR Helpful Reaction Toggle Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (localQuickState.authorUserId == userId) {
                                // Self Story Viewers & Insights Button
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .border(
                                            width = 1.dp,
                                            color = Color.White.copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable {
                                            isCurrentStoryPaused = true
                                            showInsightsSheet = true
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Lucide.Eye,
                                        contentDescription = "Story Views",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "दर्शक (${viewersList.size})" else "Viewers (${viewersList.size})",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                // Other User Story Helpful Reaction Button
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (localQuickState.isHelpful)
                                                AppColors.EmeraldGreen.copy(alpha = 0.25f)
                                            else
                                                Color.White.copy(alpha = 0.12f)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (localQuickState.isHelpful) AppColors.EmeraldGreen else Color.White.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable {
                                            val newHelpful = !localQuickState.isHelpful
                                            val newCount = if (newHelpful) localQuickState.helpfulCount + 1 else (localQuickState.helpfulCount - 1).coerceAtLeast(0)
                                            localQuickState = localQuickState.copy(isHelpful = newHelpful, helpfulCount = newCount)
                                            coroutineScope.launch {
                                                repository.toggleHelpful(localQuickState.id, userId)
                                            }
                                        }
                                        .padding(horizontal = 14.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Lucide.ThumbsUp,
                                        contentDescription = "Helpful",
                                        tint = if (localQuickState.isHelpful) AppColors.EmeraldGreen else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (localQuickState.helpfulCount > 0) "${localQuickState.helpfulCount} " + (if (isHindi) "मददगार" else "Helpful") else (if (isHindi) "मददगार" else "Helpful"),
                                        color = if (localQuickState.isHelpful) AppColors.EmeraldGreen else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Self Quick Insights & Viewers List
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val sheetBgColor = if (isDark) Color(0xFF18181B) else Color(0xFFFFFFFF)
    val sheetCardBgColor = if (isDark) Color(0xFF27272A) else Color(0xFFF4F4F5)
    val sheetTextColor = if (isDark) Color.White else Color(0xFF09090B)
    val sheetSubTextColor = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF71717A)

    if (showInsightsSheet) {
        LaunchedEffect(currentActiveQuick.id) {
            isLoadingInsights = true
            val vResult = repository.getQuickViewers(currentActiveQuick.id)
            val hResult = repository.getQuickHelpfulUsers(currentActiveQuick.id)
            // EXCLUDE self user from viewers & helpful lists (self views are negative/ignored)
            viewersList = vResult.getOrDefault(emptyList()).filter { it.user_id != userId }
            helpfulsList = hResult.getOrDefault(emptyList()).filter { it.user_id != userId }
            isLoadingInsights = false
        }

        ModalBottomSheet(
            onDismissRequest = {
                showInsightsSheet = false
                isCurrentStoryPaused = false
            },
            containerColor = sheetBgColor,
            scrimColor = Color.Black.copy(alpha = 0.6f),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            BackHandler {
                showInsightsSheet = false
                isCurrentStoryPaused = false
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Header Title
                Text(
                    text = if (isHindi) "क्विक विश्लेषक व दर्शक" else "Quick Analytics & Viewers",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = sheetTextColor
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Cards Row (Total Views & Helpful Reactions)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(sheetCardBgColor)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Lucide.Eye,
                                contentDescription = null,
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${viewersList.size}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = sheetTextColor
                            )
                            Text(
                                text = if (isHindi) "कुल दृश्य" else "Total Views",
                                fontSize = 11.sp,
                                color = sheetSubTextColor
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(sheetCardBgColor)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Lucide.ThumbsUp,
                                contentDescription = null,
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${helpfulsList.size}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = sheetTextColor
                            )
                            Text(
                                text = if (isHindi) "मददगार" else "Helpful Reactions",
                                fontSize = 11.sp,
                                color = sheetSubTextColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Selection Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeInsightsTab == 0) AppColors.EmeraldGreen else sheetCardBgColor)
                            .clickable { activeInsightsTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isHindi) "👁️ दर्शक (${viewersList.size})" else "👁️ Viewers (${viewersList.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeInsightsTab == 0) Color.White else sheetSubTextColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeInsightsTab == 1) AppColors.EmeraldGreen else sheetCardBgColor)
                            .clickable { activeInsightsTab = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isHindi) "👍 मददगार (${helpfulsList.size})" else "👍 Helpful (${helpfulsList.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeInsightsTab == 1) Color.White else sheetSubTextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // User List Content
                if (isLoadingInsights) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.EmeraldGreen, modifier = Modifier.size(32.dp))
                    }
                } else {
                    val currentList = if (activeInsightsTab == 0) viewersList else helpfulsList
                    if (currentList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (activeInsightsTab == 0) {
                                    if (isHindi) "अभी तक कोई अन्य दर्शक नहीं हैं" else "No other viewers recorded yet"
                                } else {
                                    if (isHindi) "अभी तक कोई मददगार प्रतिक्रिया नहीं है" else "No helpful reactions yet"
                                },
                                color = sheetSubTextColor,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(currentList) { uProfile ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(sheetCardBgColor)
                                        .clickable {
                                            showInsightsSheet = false
                                            isCurrentStoryPaused = false
                                            onAuthorClick(uProfile.user_id)
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.08f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!uProfile.profile_picture_url.isNullOrBlank()) {
                                            AsyncImage(
                                                model = uProfile.profile_picture_url,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Lucide.User,
                                                contentDescription = null,
                                                tint = sheetTextColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = uProfile.full_name ?: uProfile.first_name ?: "Academic Scholar",
                                                color = sheetTextColor,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (uProfile.is_verified) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(12.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF2196F3)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Lucide.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(8.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "@${uProfile.username ?: "scholar"}",
                                            color = sheetSubTextColor,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
