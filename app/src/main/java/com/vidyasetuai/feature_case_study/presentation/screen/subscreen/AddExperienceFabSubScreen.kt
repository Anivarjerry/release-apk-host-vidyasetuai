package com.vidyasetuai.feature_case_study.presentation.screen.subscreen

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.network.SupabaseStorageHelper
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
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isHindi) "अनुभव साझा करें" else "Share Experience",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
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
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen)
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
                // Top Upload Box - 1:1 Aspect Ratio Instagram Style
                val hasImageSelected = selectedUri != null

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
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
                                text = if (isHindi) "गैलरी से फोटो चुनें (1:1)" else "Select Photo from Gallery (1:1)",
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

                // Flat Borderless Fields under image
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    PremiumTextField(
                        value = title,
                        onValueChange = { if (it.length <= 100) title = it },
                        placeholder = if (isHindi) "शीर्षक जोड़ें..." else "Add a title...",
                        fontSize = 14,
                        isDark = isDark
                    )
                    Text(
                        text = "${title.length}/100",
                        fontSize = 10.sp,
                        color = subtitleColor.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PremiumTextField(
                        value = description,
                        onValueChange = { if (it.length <= 500) description = it },
                        placeholder = if (isHindi) "विवरण (Write a caption)..." else "Write a caption...",
                        fontSize = 13,
                        singleLine = false,
                        isDark = isDark,
                        modifier = Modifier.height(110.dp)
                    )
                    Text(
                        text = "${description.length}/500",
                        fontSize = 10.sp,
                        color = subtitleColor.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        textAlign = TextAlign.End
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage!!,
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
                                errorMessage = if (isHindi) "कृपया शीर्षक और विवरण भरें" else "Please fill title and description"
                                return@Button
                            }
                            isSubmitting = true
                            errorMessage = null

                            coroutineScope.launch {
                                try {
                                    var finalImageUrl: String? = null

                                    if (selectedUri != null) {
                                        val inputStream = context.contentResolver.openInputStream(selectedUri!!)
                                        val bytes = inputStream?.readBytes()
                                        if (bytes != null) {
                                            val fileName = "experiences/${UUID.randomUUID()}.jpg"
                                            finalImageUrl = SupabaseStorageHelper.uploadImage("media", fileName, bytes)
                                        }
                                    }

                                    val result = repository.createExperience(
                                        title = title,
                                        description = description,
                                        coverImageUrl = finalImageUrl,
                                        authorUserId = userId
                                    )
                                    isSubmitting = false
                                    result.onSuccess {
                                        isSuccess = true
                                    }.onFailure { e ->
                                        errorMessage = e.localizedMessage ?: "Failed to save experience"
                                    }
                                } catch (e: Exception) {
                                    isSubmitting = false
                                    errorMessage = e.localizedMessage ?: "Failed uploading image to storage"
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
                                text = if (isHindi) "अनुभव साझा करें" else "Share Experience",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
