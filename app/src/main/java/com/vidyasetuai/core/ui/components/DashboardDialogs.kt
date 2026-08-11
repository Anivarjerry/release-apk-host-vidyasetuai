package com.vidyasetuai.core.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.vidyasetuai.core.network.SupabaseStorageHelper
import com.vidyasetuai.core.ui.colors.AppColors
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

private fun processSquareImage(context: android.content.Context, uri: Uri): ByteArray? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
            val width = originalBitmap.width
            val height = originalBitmap.height
            val squareSize = if (width < height) width else height
            val x = (width - squareSize) / 2
            val y = (height - squareSize) / 2
            
            val cropped = Bitmap.createBitmap(originalBitmap, x, y, squareSize, squareSize)
            val scaled = Bitmap.createScaledBitmap(cropped, 600, 600, true)
            
            val outputStream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val bytes = outputStream.toByteArray()
            
            if (cropped != scaled) cropped.recycle()
            if (originalBitmap != cropped) originalBitmap.recycle()
            scaled.recycle()
            
            bytes
        }
    } catch (e: Exception) {
        android.util.Log.e("DashboardScreen", "Error processing square image", e)
        null
    }
}

@Composable
fun UploadCaseStudyDialog(
    userId: String,
    currentLanguage: String,
    onDismiss: () -> Unit,
    onSubmit: (
        title: String,
        shortDescription: String,
        coverImageUrl: String,
        language: String,
        tags: List<String>,
        readTimeMinutes: Int?,
        detailedContent: String,
        additionalImageUrls: List<String>
    ) -> Unit
) {
    val isHindi = currentLanguage == "hi"
    var title by remember { mutableStateOf("") }
    var shortDescription by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("hindi") } // "hindi", "english", "bilingual"
    var tagsInput by remember { mutableStateOf("") }
    var readTimeInput by remember { mutableStateOf("") }
    var detailedContent by remember { mutableStateOf("") }
    
    var coverImageUri by remember { mutableStateOf<Uri?>(null) }
    var additionalImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    var isUploading by remember { mutableStateOf(false) }
    var uploadStatusText by remember { mutableStateOf("") }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        coverImageUri = uri
    }
    
    val additionalPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (additionalImageUris.size < 4) {
                additionalImageUris = additionalImageUris + it
            }
        }
    }

    val handleFormSubmit = {
        if (title.isNotBlank() && shortDescription.isNotBlank() && detailedContent.isNotBlank() && coverImageUri != null) {
            scope.launch {
                isUploading = true
                try {
                    // 1. Process and upload cover photo
                    uploadStatusText = if (isHindi) "कवर फोटो अपलोड हो रही है..." else "Uploading cover photo..."
                    val coverBytes = processSquareImage(context, coverImageUri!!)
                    if (coverBytes == null) {
                        throw Exception("Failed to process cover image")
                    }
                    val coverFileName = "covers/cover_${System.currentTimeMillis()}_${java.util.UUID.randomUUID().toString().take(4)}.jpg"
                    val coverUrl = SupabaseStorageHelper.uploadImage("case-study-images", coverFileName, coverBytes)
                    
                    // 2. Process and upload additional content photos
                    val contentUrls = mutableListOf<String>()
                    additionalImageUris.forEachIndexed { index, uri ->
                        uploadStatusText = if (isHindi) {
                            "अतिरिक्त चित्र अपलोड हो रहा है (${index + 1}/${additionalImageUris.size})..."
                        } else {
                            "Uploading additional image (${index + 1}/${additionalImageUris.size})..."
                        }
                        val imageBytes = processSquareImage(context, uri)
                        if (imageBytes != null) {
                            val contentFileName = "content/content_${System.currentTimeMillis()}_${index}_${java.util.UUID.randomUUID().toString().take(4)}.jpg"
                            val contentUrl = SupabaseStorageHelper.uploadImage("case-study-images", contentFileName, imageBytes)
                            contentUrls.add(contentUrl)
                        }
                    }
                    
                    // 3. Process tags
                    val tagsList = tagsInput.split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        
                    // 4. Process read time
                    val readTime = readTimeInput.toIntOrNull()
                    
                    uploadStatusText = if (isHindi) "केस स्टडी जमा हो रही है..." else "Submitting case study..."
                    onSubmit(
                        title,
                        shortDescription,
                        coverUrl,
                        selectedLanguage,
                        tagsList,
                        readTime,
                        detailedContent,
                        contentUrls
                    )
                } catch (e: Exception) {
                    android.util.Log.e("UploadCaseStudy", "Error uploading case study", e)
                } finally {
                    isUploading = false
                    uploadStatusText = ""
                }
            }
        }
    }

    Dialog(onDismissRequest = { if (!isUploading) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Text(
                        text = if (isHindi) "केस स्टडी अपलोड करें" else "Upload Case Study",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldGreen
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Cover Image Selection Card
                    Text(
                        text = if (isHindi) "कवर फोटो (Cover Photo) *" else "Cover Photo *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(
                                MaterialTheme.colorScheme.background,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable(enabled = !isUploading) { coverPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (coverImageUri != null) {
                            AsyncImage(
                                model = coverImageUri,
                                contentDescription = "Cover Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f))
                            )
                            Text(
                                text = if (isHindi) "कवर बदलें" else "Change Cover",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Lucide.Plus,
                                    contentDescription = "Add Cover",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isHindi) "मुख्य कवर फोटो चुनें (1:1 क्रॉप होगी)" else "Choose Cover Photo (will crop 1:1)",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Title Field
                    Text(
                        text = if (isHindi) "शीर्षक (Title) *" else "Title *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicTextField(
                        value = title,
                        onValueChange = { if (it.length <= 200) title = it },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp),
                        enabled = !isUploading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        decorationBox = { innerTextField ->
                            if (title.isEmpty()) {
                                Text(
                                    text = if (isHindi) "शीर्षक दर्ज करें..." else "Enter title...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Short Description Field
                    Text(
                        text = if (isHindi) "संक्षिप्त विवरण (Short Description) *" else "Short Description *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicTextField(
                        value = shortDescription,
                        onValueChange = { if (it.length <= 300) shortDescription = it },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp),
                        enabled = !isUploading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        decorationBox = { innerTextField ->
                            if (shortDescription.isEmpty()) {
                                Text(
                                    text = if (isHindi) "संक्षिप्त विवरण दर्ज करें (अधिकतम 300 वर्ण)..." else "Enter short description (max 300 chars)...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Language Choice (Hindi / English / Bilingual)
                    Text(
                        text = if (isHindi) "भाषा (Language) *" else "Language *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("hindi", "english", "bilingual").forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            val label = when (lang) {
                                "hindi" -> if (isHindi) "हिंदी" else "Hindi"
                                "english" -> if (isHindi) "अंग्रेजी" else "English"
                                else -> if (isHindi) "द्विभाषी" else "Bilingual"
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.background,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .clickable(enabled = !isUploading) { selectedLanguage = lang }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Tags Input (comma separated)
                    Text(
                        text = if (isHindi) "टैग (Tags) - अल्पविराम (comma) से अलग करें" else "Tags - separated by comma",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicTextField(
                        value = tagsInput,
                        onValueChange = { tagsInput = it },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp),
                        enabled = !isUploading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        decorationBox = { innerTextField ->
                            if (tagsInput.isEmpty()) {
                                Text(
                                    text = if (isHindi) "उदा. Education, AI, Tech" else "e.g. Education, AI, Tech",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Read Time Minutes (numeric)
                    Text(
                        text = if (isHindi) "पढ़ने का समय (मिनट में)" else "Read Time (in minutes)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicTextField(
                        value = readTimeInput,
                        onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) readTimeInput = it },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp),
                        enabled = !isUploading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        decorationBox = { innerTextField ->
                            if (readTimeInput.isEmpty()) {
                                Text(
                                    text = if (isHindi) "उदा. 5" else "e.g. 5",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Detailed Content (multiline text area)
                    Text(
                        text = if (isHindi) "विस्तृत सामग्री (Detailed Content) *" else "Detailed Content *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicTextField(
                        value = detailedContent,
                        onValueChange = { detailedContent = it },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp),
                        enabled = !isUploading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        decorationBox = { innerTextField ->
                            if (detailedContent.isEmpty()) {
                                Text(
                                    text = if (isHindi) "केस स्टडी का मुख्य विवरण यहाँ लिखें..." else "Write main case study detailed content here...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Additional Images (max 4, horizontal thumbnails + plus card)
                    Text(
                        text = if (isHindi) "अतिरिक्त चित्र (अधिक्तम 4)" else "Additional Images (max 4)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        additionalImageUris.forEachIndexed { idx, uri ->
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Content Image $idx",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .background(Color.Red.copy(alpha = 0.7f), CircleShape)
                                        .clickable(enabled = !isUploading) {
                                            additionalImageUris = additionalImageUris.filterIndexed { index, _ -> index != idx }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "×", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 2.dp))
                                }
                            }
                        }
                        if (additionalImageUris.size < 4) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                    .clickable(enabled = !isUploading) { additionalPickerLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Plus,
                                    contentDescription = "Add Content Image",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(28.dp))
                    
                    // Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            enabled = !isUploading,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = if (isHindi) "रद्द करें" else "Cancel")
                        }
                        Button(
                            onClick = handleFormSubmit,
                            enabled = title.isNotBlank() && shortDescription.isNotBlank() && detailedContent.isNotBlank() && coverImageUri != null && !isUploading,
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(text = if (isHindi) "सबमिट करें" else "Submit", color = Color.White)
                        }
                    }
                }
                
                // Loading Overlay inside the Dialog
                if (isUploading) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(enabled = false) {},
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = AppColors.EmeraldGreen)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = uploadStatusText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UploadExperienceDialog(
    userId: String,
    currentLanguage: String,
    onDismiss: () -> Unit,
    onSubmit: (title: String, description: String, imageUrl: String?) -> Unit
) {
    val isHindi = currentLanguage == "hi"
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var coverImageUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            if (bytes != null) {
                val fileName = "experiences/experience_${System.currentTimeMillis()}.jpg"
                scope.launch {
                    isUploading = true
                    try {
                        val publicUrl =
                            SupabaseStorageHelper.uploadImage("media", fileName, bytes)
                        coverImageUrl = publicUrl
                    } catch (e: Exception) {
                        android.util.Log.e("UploadDialog", "Failed to upload experience image", e)
                    } finally {
                        isUploading = false
                    }
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isHindi) "अनुभव साझा करें" else "Share Experience",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Title Field
                Text(
                    text = if (isHindi) "शीर्षक (Title)" else "Title",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp))
                        .padding(10.dp),
                    decorationBox = { innerTextField ->
                        if (title.isEmpty()) {
                            Text(
                                text = if (isHindi) "अपना अनुभव का शीर्षक लिखें..." else "Enter title...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Description Field
                Text(
                    text = if (isHindi) "विवरण (Description)" else "Description",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                BasicTextField(
                    value = description,
                    onValueChange = { description = it },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp))
                        .padding(10.dp),
                    decorationBox = { innerTextField ->
                        if (description.isEmpty()) {
                            Text(
                                text = if (isHindi) "विवरण लिखें..." else "Enter description...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Image Upload Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { pickerLauncher.launch("image/*") },
                        enabled = !isUploading,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isUploading) {
                                if (isHindi) "अपलोड हो रहा है..." else "Uploading..."
                            } else {
                                if (isHindi) "कवर फोटो चुनें (वैकल्पिक)" else "Choose Cover Photo (Optional)"
                            }
                        )
                    }
                    if (coverImageUrl != null) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Lucide.Check,
                            contentDescription = "Uploaded",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = if (isHindi) "रद्द करें" else "Cancel")
                    }
                    Button(
                        onClick = { onSubmit(title, description, coverImageUrl) },
                        enabled = title.isNotBlank() && description.isNotBlank() && !isUploading,
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text(text = if (isHindi) "साझा करें" else "Share", color = Color.White)
                    }
                }
            }
        }
    }
}
