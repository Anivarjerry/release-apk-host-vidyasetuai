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
import com.vidyasetuai.feature_case_study.data.repository.CaseStudyRepositoryImpl
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import kotlinx.coroutines.launch
import java.util.UUID

class SubHeadingState(initialTitle: String = "", initialText: String = "") {
    var title by mutableStateOf(initialTitle)
    var text by mutableStateOf(initialText)
}

class HeadingState(initialHeading: String = "", initialDescription: String = "") {
    var heading by mutableStateOf(initialHeading)
    var description by mutableStateOf(initialDescription)
    val subHeadings = mutableStateListOf(SubHeadingState())
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 13,
    singleLine: Boolean = true,
    isDark: Boolean = false
) {
    val lineBaseColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.08f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = lineBaseColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
            .padding(vertical = 8.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = TextStyle(
                fontSize = fontSize.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = fontSize.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.35f)
                    )
                }
                innerTextField()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCaseStudyFabSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    userId: String,
    repository: CaseStudyRepositoryImpl,
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
    val inputBgColor = Color.Transparent

    // Image upload states (moved to top of function body to resolve initialization issues)
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
        offset += offsetChange
    }

    // Dynamic state representation
    val headingsList = remember { mutableStateListOf(HeadingState()) }

    // Form states
    var title by remember { mutableStateOf("") }
    var shortDesc by remember { mutableStateOf("") }

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
                        text = if (isHindi) "केस स्टडी जोड़ें" else "Add Case Study",
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
                        text = if (isHindi) "केस स्टडी सफलतापूर्वक सबमिट की गई!" else "Case Study Submitted Successfully!",
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
                                contentDescription = "Cover Preview",
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
                                text = if (isHindi) "गैलरी से कवर फोटो चुनें (1:1)" else "Select Cover Photo (1:1 Ratio)",
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

                // Metadata Section
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = (if (isHindi) "केस स्टडी विवरण" else "CASE STUDY INFORMATION").uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    PremiumTextField(
                        value = title,
                        onValueChange = { if (it.length <= 100) title = it },
                        placeholder = if (isHindi) "शीर्षक..." else "Title...",
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

                    Spacer(modifier = Modifier.height(8.dp))

                    PremiumTextField(
                        value = shortDesc,
                        onValueChange = { if (it.length <= 250) shortDesc = it },
                        placeholder = if (isHindi) "संक्षिप्त विवरण..." else "Short Description...",
                        fontSize = 13,
                        isDark = isDark
                    )
                    Text(
                        text = "${shortDesc.length}/250",
                        fontSize = 10.sp,
                        color = subtitleColor.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Content Headings Block
                    Text(
                        text = (if (isHindi) "केस स्टडी कंटेंट" else "CASE STUDY CONTENT").uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Render headings list (Editorial minimal design, no cards background)
                    headingsList.forEachIndexed { hIndex, headingState ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            // Section header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${if (isHindi) "हेडिंग" else "HEADING"} #${hIndex + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.EmeraldGreen.copy(alpha = 0.8f)
                                )
                                if (headingsList.size > 1) {
                                    IconButton(
                                        onClick = { headingsList.removeAt(hIndex) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Lucide.Trash,
                                            contentDescription = "Delete Heading",
                                            tint = Color.Red.copy(alpha = 0.6f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            PremiumTextField(
                                value = headingState.heading,
                                onValueChange = { if (it.length <= 100) headingState.heading = it },
                                placeholder = if (isHindi) "हेडिंग शीर्षक..." else "Heading Title...",
                                fontSize = 13,
                                isDark = isDark
                            )
                            Text(
                                text = "${headingState.heading.length}/100",
                                fontSize = 9.sp,
                                color = subtitleColor.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth().padding(top = 1.dp),
                                textAlign = TextAlign.End
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            PremiumTextField(
                                value = headingState.description,
                                onValueChange = { if (it.length <= 300) headingState.description = it },
                                placeholder = if (isHindi) "हेडिंग विवरण..." else "Heading Description...",
                                fontSize = 12,
                                isDark = isDark
                            )
                            Text(
                                text = "${headingState.description.length}/300",
                                fontSize = 9.sp,
                                color = subtitleColor.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth().padding(top = 1.dp),
                                textAlign = TextAlign.End
                            )

                            // Render Subheadings with left thin alignment line (lots of whitespace)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, top = 8.dp)
                                    .drawBehind {
                                        val lineBaseColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
                                        drawLine(
                                            color = lineBaseColor,
                                            start = Offset(0f, 0f),
                                            end = Offset(0f, size.height),
                                            strokeWidth = 2.dp.toPx()
                                        )
                                    }
                                    .padding(start = 16.dp)
                            ) {
                                headingState.subHeadings.forEachIndexed { sIndex, subHeadingState ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${if (isHindi) "सब-हेडिंग" else "SUB-HEADING"} #${sIndex + 1}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = textColor.copy(alpha = 0.6f)
                                            )
                                            if (headingState.subHeadings.size > 1) {
                                                IconButton(
                                                    onClick = { headingState.subHeadings.removeAt(sIndex) },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Lucide.Trash,
                                                        contentDescription = "Delete Subheading",
                                                        tint = Color.Red.copy(alpha = 0.5f),
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))

                                        PremiumTextField(
                                            value = subHeadingState.title,
                                            onValueChange = { if (it.length <= 100) subHeadingState.title = it },
                                            placeholder = if (isHindi) "सब-हेडिंग शीर्षक..." else "Sub-heading Title...",
                                            fontSize = 12,
                                            isDark = isDark
                                        )
                                        Text(
                                            text = "${subHeadingState.title.length}/100",
                                            fontSize = 9.sp,
                                            color = subtitleColor.copy(alpha = 0.6f),
                                            modifier = Modifier.fillMaxWidth().padding(top = 1.dp),
                                            textAlign = TextAlign.End
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        PremiumTextField(
                                            value = subHeadingState.text,
                                            onValueChange = { if (it.length <= 2000) subHeadingState.text = it },
                                            placeholder = if (isHindi) "सब-हेडिंग टेक्स्ट..." else "Sub-heading Text...",
                                            fontSize = 12,
                                            isDark = isDark
                                        )
                                        Text(
                                            text = "${subHeadingState.text.length}/2000",
                                            fontSize = 9.sp,
                                            color = subtitleColor.copy(alpha = 0.6f),
                                            modifier = Modifier.fillMaxWidth().padding(top = 1.dp),
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }

                                // "+ Add Subheading" small button
                                TextButton(
                                    onClick = { headingState.subHeadings.add(SubHeadingState()) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = AppColors.EmeraldGreen),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(imageVector = Lucide.Plus, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isHindi) "सब-हेडिंग जोड़ें" else "Add Sub-heading",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(dividerColor.copy(alpha = 0.5f)))
                        }
                    }

                    // "+ Add Heading Block" small clean button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = { headingsList.add(HeadingState()) },
                            colors = ButtonDefaults.textButtonColors(contentColor = AppColors.EmeraldGreen)
                        ) {
                            Icon(imageVector = Lucide.Plus, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "नई हेडिंग जोड़ें" else "Add Heading Block",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

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

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (title.isBlank() || shortDesc.isBlank()) {
                                errorMessage = if (isHindi) "कृपया शीर्षक और विवरण भरें" else "Please fill title and description"
                                return@Button
                            }
                            isSubmitting = true
                            errorMessage = null

                            coroutineScope.launch {
                                try {
                                    var finalImageUrl = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=500"

                                    if (selectedUri != null) {
                                        val inputStream = context.contentResolver.openInputStream(selectedUri!!)
                                        val bytes = inputStream?.readBytes()
                                        if (bytes != null) {
                                            val fileName = "case-studies/${UUID.randomUUID()}.jpg"
                                            finalImageUrl = SupabaseStorageHelper.uploadImage("media", fileName, bytes)
                                        }
                                    }

                                    val sectionsString = headingsList.joinToString(separator = ",") { headingState ->
                                        val subheadingsString = headingState.subHeadings.joinToString(separator = ",") { subState ->
                                            """
                                                {
                                                  "title": "${subState.title.replace("\"", "\\\"")}",
                                                  "text": "${subState.text.replace("\"", "\\\"")}"
                                                }
                                            """.trimIndent()
                                        }
                                        """
                                            {
                                              "heading": "${headingState.heading.replace("\"", "\\\"")}",
                                              "headingImage": "",
                                              "description": "${headingState.description.replace("\"", "\\\"")}",
                                              "subHeadings": [$subheadingsString]
                                            }
                                        """.trimIndent()
                                    }

                                    val finalJsonContent = """
                                        {
                                          "version": 1,
                                          "sections": [$sectionsString]
                                        }
                                    """.trimIndent()

                                    val result = repository.createCaseStudy(
                                        title = title,
                                        shortDescription = shortDesc,
                                        coverImageUrl = finalImageUrl,
                                        language = if (isHindi) "hi" else "en",
                                        tags = listOf("Education", "AI"),
                                        readTimeMinutes = 5,
                                        detailedContent = finalJsonContent,
                                        additionalImageUrls = emptyList(),
                                        userId = userId
                                    )
                                    isSubmitting = false
                                    result.onSuccess {
                                        isSuccess = true
                                    }.onFailure { e ->
                                        errorMessage = e.localizedMessage ?: "Failed to upload case study"
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
                                text = if (isHindi) "केस स्टडी पब्लिश करें" else "Publish Case Study",
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
