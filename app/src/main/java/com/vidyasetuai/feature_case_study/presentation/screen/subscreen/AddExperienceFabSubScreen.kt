package com.vidyasetuai.feature_case_study.presentation.screen.subscreen

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
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
import com.vidyasetuai.core.network.SupabaseStorageHelper
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_feed.data.repository.ExperienceRepository
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExperienceFabSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    userId: String,
    repository: ExperienceRepository,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val subtitleColor = if (isDark) Color(0xFFA0AEC0) else Color(0xFF718096)
    val cardBgColor = if (isDark) Color(0xFF1E1E24) else Color(0xFFF8FAFC)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    // Image upload states
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
        offset += offsetChange
    }

    // Form inputs
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    // Android gallery image picker
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            scale = 1f
            offset = Offset.Zero
        }
    }

    val canSubmit = selectedUri != null || title.isNotBlank() || description.isNotBlank()

    val submitExperienceAction = {
        if (!canSubmit) {
            errorMessage = if (isHindi) "कृपया एक फोटो चुनें या शीर्षक/विवरण लिखें" else "Please select a photo or add title/description"
        } else {
            isSubmitting = true
            errorMessage = null

            coroutineScope.launch {
                try {
                    var finalImageUrl: String? = null

                    if (selectedUri != null) {
                        val bytes = processCroppedBitmap(context, selectedUri!!, scale, offset)
                            ?: context.contentResolver.openInputStream(selectedUri!!)?.readBytes()

                        if (bytes != null) {
                            val fileName = "experiences/${UUID.randomUUID()}.jpg"
                            finalImageUrl = SupabaseStorageHelper.uploadImage("media", fileName, bytes)
                        }
                    }

                    val result = repository.createExperience(
                        title = title.ifBlank { "" },
                        description = description.ifBlank { "" },
                        coverImageUrl = finalImageUrl,
                        authorUserId = userId
                    )
                    isSubmitting = false
                    result.onSuccess {
                        isSuccess = true
                    }.onFailure {
                        errorMessage = if (isHindi) "अनुभव साझा करने में समस्या आई। कृपया पुनः प्रयास करें।" else "Failed to share experience. Please try again."
                    }
                } catch (e: Exception) {
                    isSubmitting = false
                    errorMessage = if (isHindi) "इमेज अपलोड करने में त्रुटि। कृपया पुनः प्रयास करें।" else "Failed uploading image. Please try again."
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(backgroundColor)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "अनुभव साझा करें" else "Share Experience",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )

                    // Top Bar Compact Share Pill Button
                    Button(
                        onClick = { submitExperienceAction() },
                        enabled = !isSubmitting && canSubmit,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.EmeraldGreen,
                            disabledContainerColor = AppColors.EmeraldGreen.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = if (isHindi) "साझा करें" else "Share",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(dividerColor.copy(alpha = 0.5f))
                )
            }
        },
        containerColor = backgroundColor,
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        if (isSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Lucide.CircleCheck,
                        contentDescription = null,
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isHindi) "अनुभव सफलतापूर्वक साझा किया गया!" else "Experience Shared Successfully!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = if (isHindi) "वापस जाएं" else "Go Back", color = Color.White)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
            ) {
                // 1. Top Image Container - Entire Area Clickable to open Gallery
                val hasImageSelected = selectedUri != null

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.1f)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (hasImageSelected) Color.Black else cardBgColor)
                        .border(
                            width = if (hasImageSelected) 0.dp else 1.dp,
                            color = if (hasImageSelected) Color.Transparent else AppColors.EmeraldGreen.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { pickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (hasImageSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .transformable(state = transformState)
                        ) {
                            AsyncImage(
                                model = selectedUri,
                                contentDescription = "Cover Image Preview",
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

                            // Clear Photo Action Button
                            IconButton(
                                onClick = {
                                    selectedUri = null
                                    scale = 1f
                                    offset = Offset.Zero
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    .size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.X,
                                    contentDescription = "Clear Photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Change Photo Pill Overlay
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Lucide.Image,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "बदलें" else "Change Photo",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.EmeraldGreen.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Camera,
                                    contentDescription = null,
                                    tint = AppColors.EmeraldGreen,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isHindi) "गैलरी से फोटो चुनें (1:1)" else "Select Photo from Gallery (1:1)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isHindi) "गैलरी खोलने के लिए कहीं भी टैप करें" else "Tap anywhere in box to upload image",
                                fontSize = 11.sp,
                                color = subtitleColor
                            )
                        }
                    }
                }

                if (hasImageSelected) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 2.dp),
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

                Spacer(modifier = Modifier.height(12.dp))

                // Premium Flat Text Fields
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    PremiumTextField(
                        value = title,
                        onValueChange = { if (it.length <= 100) title = it },
                        placeholder = if (isHindi) "शीर्षक दर्ज करें..." else "Add a title...",
                        fontSize = 15,
                        isDark = isDark
                    )
                    Text(
                        text = "${title.length}/100",
                        fontSize = 10.sp,
                        color = subtitleColor.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PremiumTextField(
                        value = description,
                        onValueChange = { if (it.length <= 500) description = it },
                        placeholder = if (isHindi) "अपना अनुभव या विवरण लिखें..." else "Write a caption or describe your experience...",
                        fontSize = 13,
                        singleLine = false,
                        isDark = isDark,
                        modifier = Modifier.height(120.dp)
                    )
                    Text(
                        text = "${description.length}/500",
                        fontSize = 10.sp,
                        color = subtitleColor.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        textAlign = TextAlign.End
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private fun processCroppedBitmap(context: android.content.Context, uri: Uri, scale: Float, offset: Offset): ByteArray? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val originalBitmap = android.graphics.BitmapFactory.decodeStream(inputStream) ?: return null

        val targetSize = 1080
        val croppedBitmap = android.graphics.Bitmap.createBitmap(targetSize, targetSize, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(croppedBitmap)
        canvas.drawColor(android.graphics.Color.BLACK)

        val matrix = android.graphics.Matrix()
        val srcWidth = originalBitmap.width.toFloat()
        val srcHeight = originalBitmap.height.toFloat()

        val baseScale = Math.max(targetSize / srcWidth, targetSize / srcHeight)
        val finalScale = baseScale * scale

        val dx = (targetSize - srcWidth * finalScale) / 2f + (offset.x * (targetSize / 360f))
        val dy = (targetSize - srcHeight * finalScale) / 2f + (offset.y * (targetSize / 360f))

        matrix.postScale(finalScale, finalScale)
        matrix.postTranslate(dx, dy)

        canvas.drawBitmap(originalBitmap, matrix, null)

        val outputStream = java.io.ByteArrayOutputStream()
        croppedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.toByteArray()
    } catch (e: Exception) {
        null
    }
}
