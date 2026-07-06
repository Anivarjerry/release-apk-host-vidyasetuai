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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuickViewerScreen(
    quicks: List<Quick>,
    initialIndex: Int,
    currentLanguage: String,
    userId: String,
    repository: QuickRepository,
    onBack: () -> Unit,
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

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage >= activeItems.size - 2 && activeItems.size < quicks.size) {
            val nextSize = minOf(quicks.size, activeItems.size + segmentSize)
            activeItems = quicks.take(nextSize)
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

            // Log view when page becomes active
            LaunchedEffect(pagerState.currentPage) {
                if (pagerState.currentPage == page) {
                    repository.viewQuick(quick.id, userId)
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                // 1. Fullscreen Image or Gradient Background
                if (!localQuickState.coverImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = localQuickState.coverImageUrl,
                        contentDescription = "Quick Background Image",
                        contentScale = ContentScale.Crop,
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
                                colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
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

                // 2. Gesture tap zones (Left 40% goes back, Right 60% goes forward)
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(4f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
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
                            .weight(6f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
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
                    // Segmented Story Indicator bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(quicks.size) { index ->
                            val isCompleted = index < pagerState.currentPage
                            val isActive = index == pagerState.currentPage
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        when {
                                            isCompleted -> Color.White
                                            isActive -> Color.White
                                            else -> Color.White.copy(alpha = 0.35f)
                                        }
                                    )
                            )
                        }
                    }

                    // Top Bar Info: Creator avatar, Name, X-back, Download button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
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

                        // Creator Details
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

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
                                text = "@${localQuickState.authorUsername}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

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
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Bottom Panel containing title, descriptions & stats
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = localQuickState.title,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Custom show-more expander
                        val words = localQuickState.description.split(" ")
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

                        Spacer(modifier = Modifier.height(20.dp))

                        // Stats Actions (views, helpful toggle)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Lucide.Eye,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${localQuickState.viewsCount} ${if (isHindi) "दृश्य" else "views"}",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                            }

                            // Helpful toggle directly inside viewer
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (localQuickState.isHelpful) AppColors.EmeraldGreen else Color.White.copy(alpha = 0.15f))
                                    .clickable {
                                        coroutineScope.launch {
                                            repository.toggleHelpful(localQuickState.id, userId).onSuccess {
                                                // Sync locally updated count
                                                val newHelpful = !localQuickState.isHelpful
                                                val newCount = if (newHelpful) localQuickState.helpfulCount + 1 else localQuickState.helpfulCount - 1
                                                localQuickState = localQuickState.copy(isHelpful = newHelpful, helpfulCount = newCount)
                                            }
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.ThumbsUp,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${localQuickState.helpfulCount} ${if (isHindi) "मददगार" else "Helpful"}",
                                    color = Color.White,
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
