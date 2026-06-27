package com.vidyasetuai.feature_feed.presentation.screen

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import com.vidyasetuai.feature_institution.domain.model.StudentSearchResult
import androidx.camera.core.ImageProxy
import com.vidyasetuai.core.ml.FaceNetClassifier

fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val rotation = image.imageInfo.rotationDegrees
    return if (rotation != 0) {
        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
}

fun calculateCosineSimilarity(v1: FloatArray, v2: List<Float>): Float {
    if (v1.size != v2.size || v1.isEmpty()) return 0f
    var dotProduct = 0f
    var normA = 0f
    var normB = 0f
    for (i in v1.indices) {
        dotProduct += v1[i] * v2[i]
        normA += v1[i] * v1[i]
        normB += v2[i] * v2[i]
    }
    if (normA == 0f || normB == 0f) return 0f
    return dotProduct / (Math.sqrt(normA.toDouble()) * Math.sqrt(normB.toDouble())).toFloat()
}

@Serializable
data class FaceMatchResult(
    val student_id: String,
    val similarity: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDirectorySubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onStudentClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val faceClassifier = remember { FaceNetClassifier(context) }
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<StudentSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    // Dropdown/Filter options
    var classes by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var sections by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var selectedClassId by remember { mutableStateOf<String?>(null) }
    var selectedSectionId by remember { mutableStateOf<String?>(null) }

    var showClassDialog by remember { mutableStateOf(false) }
    var showSectionDialog by remember { mutableStateOf(false) }

    // Face search state
    var showCameraDialog by remember { mutableStateOf(false) }
    var isMatchingFace by remember { mutableStateOf(false) }

    val activeWorkspace = state.activeWorkspace
    val orgId = activeWorkspace?.childOrgId ?: activeWorkspace?.parentOrgId ?: ""
    val isParent = activeWorkspace?.childOrgId.isNullOrEmpty()

    // 1. Request camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCameraDialog = true
        } else {
            Toast.makeText(context, if (isHindi) "कैमरा अनुमति आवश्यक है।" else "Camera permission is required.", Toast.LENGTH_SHORT).show()
        }
    }

    var showOptionDialog by remember { mutableStateOf(false) }
    var isGalleryMatching by remember { mutableStateOf(false) }
    var topMatches by remember { mutableStateOf<List<Pair<StudentSearchResult, Double>>>(emptyList()) }
    var showTopMatchesDialog by remember { mutableStateOf(false) }

    val db = remember { com.vidyasetuai.core.database.AppDatabase.getDatabase(context) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            isGalleryMatching = true
            scope.launch {
                delay(800)
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { stream ->
                            BitmapFactory.decodeStream(stream)
                        }
                    }

                    if (bitmap == null) {
                        Toast.makeText(context, if (isHindi) "छवि लोड करने में विफल" else "Failed to load image.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val faceVector = faceClassifier.detectAndExtractVector(bitmap)
                    if (faceVector == null) {
                        Toast.makeText(context, if (isHindi) "फ़ोटो में कोई चेहरा नहीं मिला।" else "No face detected in photo.", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    val localVectors = withContext(Dispatchers.IO) {
                        db.institutionDao().getStudentImageVectorsForOrgs(
                            listOfNotNull(activeWorkspace?.childOrgId, activeWorkspace?.parentOrgId)
                        )
                    }

                    if (localVectors.isEmpty()) {
                        Toast.makeText(context, if (isHindi) "डेटाबेस में कोई पंजीकृत फ़ेस वेक्टर नहीं मिला।" else "No registered face vectors found in local database.", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    val matchedStudentsAndScores = localVectors.mapNotNull { localVec ->
                        val similarity = calculateCosineSimilarity(faceVector, localVec.faceVector)
                        if (similarity >= 0.40f) {
                            localVec.studentId to similarity
                        } else {
                            null
                        }
                    }

                    if (matchedStudentsAndScores.isNotEmpty()) {
                        val sortedMatches = matchedStudentsAndScores.sortedByDescending { it.second }.take(3)
                        val studentIds = sortedMatches.map { it.first }
                        
                        val students = withContext(Dispatchers.IO) {
                            db.institutionDao().getStudentsByIds(studentIds)
                        }
                        
                        val matches = sortedMatches.mapNotNull { match ->
                            val studentEntity = students.find { it.id == match.first }
                            if (studentEntity != null) {
                                val searchResult = StudentSearchResult(
                                    id = studentEntity.id,
                                    name = studentEntity.name,
                                    sr_number = studentEntity.srNumber,
                                    roll_number = studentEntity.rollNumber,
                                    class_name = studentEntity.className,
                                    section_name = studentEntity.sectionName,
                                    guardian_name = studentEntity.guardianName,
                                    guardian_mobile = studentEntity.guardianMobile,
                                    image_url = studentEntity.imageUrl
                                )
                                val percentageScore = match.second * 100.0
                                searchResult to percentageScore
                            } else {
                                null
                            }
                        }
                        
                        if (matches.isNotEmpty()) {
                            topMatches = matches
                            showTopMatchesDialog = true
                        } else {
                            Toast.makeText(context, if (isHindi) "कोई मेल खाता चेहरा नहीं मिला।" else "No matching face found.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, if (isHindi) "कोई मेल खाता चेहरा नहीं मिला।" else "No matching face found.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("GallerySearch", "Face matching failed", e)
                    Toast.makeText(context, "Lookup Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    isGalleryMatching = false
                }
            }
        }
    }

    // 2. Fetch Classes and Sections
    LaunchedEffect(orgId) {
        if (orgId.isNotEmpty()) {
            scope.launch {
                try {
                    val response = SupabaseClient.client.postgrest.rpc(
                        "get_student_directory_profile",
                        buildJsonObject {
                            put("p_student_id", "00000000-0000-0000-0000-000000000000")
                        }
                    )
                    // Let's directly select classes for organization to populate filter dropdowns
                    val orgClasses = SupabaseClient.client.postgrest.from("organization_classes")
                        .select(io.github.jan.supabase.postgrest.query.Columns.raw("id, custom_class_name, class_id")) {
                            filter {
                                eq("organization_id", orgId)
                                eq("is_deleted", false)
                            }
                        }.decodeList<kotlinx.serialization.json.JsonObject>()

                    val globalClasses = SupabaseClient.client.postgrest.from("global_classes")
                        .select(io.github.jan.supabase.postgrest.query.Columns.raw("id, name")) {
                            filter { eq("is_deleted", false) }
                        }.decodeList<kotlinx.serialization.json.JsonObject>()

                    val classPairs = orgClasses.map { orgCls ->
                        val id = orgCls["id"]?.toString()?.replace("\"", "") ?: ""
                        val customName = orgCls["custom_class_name"]?.toString()?.replace("\"", "")
                        val classId = orgCls["class_id"]?.toString()?.replace("\"", "") ?: ""
                        val globalName = globalClasses.find {
                            it["id"]?.toString()?.replace("\"", "") == classId
                        }?.get("name")?.toString()?.replace("\"", "") ?: "Class"
                        id to (customName ?: globalName)
                    }
                    classes = classPairs

                    val classIds = classPairs.map { it.first }
                    val orgSections = if (classIds.isNotEmpty()) {
                        SupabaseClient.client.postgrest.from("organization_sections")
                            .select(io.github.jan.supabase.postgrest.query.Columns.raw("id, name")) {
                                filter {
                                    isIn("organization_class_id", classIds)
                                    eq("is_deleted", false)
                                }
                            }.decodeList<kotlinx.serialization.json.JsonObject>()
                    } else {
                        emptyList()
                    }
                    sections = orgSections.map {
                        val id = it["id"]?.toString()?.replace("\"", "") ?: ""
                        val name = it["name"]?.toString()?.replace("\"", "") ?: "Sec"
                        id to name
                    }
                } catch (e: Exception) {
                    Log.e("StudentSearch", "Error fetching class/sections filters", e)
                }
            }
        }
    }

    // 3. Search triggers on query change or filter change
    LaunchedEffect(searchQuery, selectedClassId, selectedSectionId, orgId) {
        if (orgId.isNotEmpty()) {
            delay(300) // Debounce text input
            scope.launch {
                try {
                    isSearching = true
                    val results = withContext(Dispatchers.IO) {
                        SupabaseClient.client.postgrest.rpc(
                            "search_students_directory",
                            buildJsonObject {
                                put("p_query", searchQuery)
                                put("p_org_id", orgId)
                                put("p_is_parent", isParent)
                                put("p_class_id", selectedClassId)
                                put("p_section_id", selectedSectionId)
                            }
                        ).decodeList<StudentSearchResult>()
                    }
                    searchResults = results
                } catch (e: Exception) {
                    Log.e("StudentSearch", "Error searching student directory", e)
                } finally {
                    isSearching = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Search Engine Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = if (isHindi) "नाम, मोबाइल, रोल नंबर, गाँव..." else "Name, mobile, roll, village...",
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Lucide.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.EmeraldGreen,
                    unfocusedBorderColor = borderVal
                )
            )

            // Image/Face Search Selector Button
            IconButton(
                onClick = {
                    showOptionDialog = true
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Lucide.Image,
                    contentDescription = "Face Image Search Options",
                    tint = AppColors.EmeraldGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Filter chips bar removed as per request
        Spacer(modifier = Modifier.height(8.dp))

        // Results Container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (isSearching) {
                CircularProgressIndicator(color = AppColors.EmeraldGreen, modifier = Modifier.size(36.dp))
            } else if (searchResults.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Users,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isHindi) "कोई छात्र नहीं मिला" else "No students found",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isHindi) "खोज पट्टी में अन्य कीवर्ड डालकर फिर प्रयास करें।" else "Try entering a different name, class, mobile or root keyword.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(searchResults) { student ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onStudentClick(student.id) },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, borderVal),
                            color = cardBg
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Student Image Avatar
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!student.image_url.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = student.image_url,
                                            contentDescription = student.name,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Lucide.User,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Details column
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = student.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "SR: ${student.sr_number ?: "—"} | Roll: ${student.roll_number ?: "—"}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                    if (!student.guardian_name.isNullOrEmpty()) {
                                        Text(
                                            text = "G: ${student.guardian_name} (${student.guardian_mobile ?: "—"})",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )
                                    }
                                }

                                // Class Badge
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AppColors.EmeraldGreen.copy(alpha = 0.1f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        AppColors.EmeraldGreen.copy(alpha = 0.2f)
                                    )
                                ) {
                                    val clsName = student.class_name?.takeIf { it != "null" && it.isNotBlank() }
                                    val secName = student.section_name?.takeIf { it != "null" && it.isNotBlank() }
                                    val classText = if (clsName != null) "$clsName-${secName ?: "A"}" else "N/A"
                                    Text(
                                        text = classText,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = AppColors.EmeraldGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ── CLASS SELECTOR DIALOG ──
    if (showClassDialog) {
        Dialog(onDismissRequest = { showClassDialog = false }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF1C1C1E) else Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .wrapContentHeight()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "कक्षा फ़िल्टर" else "Filter by Class",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.heightIn(max = 240.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedClassId = null
                                        showClassDialog = false
                                    }
                                    .padding(vertical = 10.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "सभी कक्षाएं" else "All Classes",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        items(classes) { cls ->
                            val isSelected = cls.first == selectedClassId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AppColors.EmeraldGreen.copy(alpha = 0.1f) else Color.Transparent)
                                    .clickable {
                                        selectedClassId = cls.first
                                        showClassDialog = false
                                    }
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cls.second,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ── SECTION SELECTOR DIALOG ──
    if (showSectionDialog) {
        Dialog(onDismissRequest = { showSectionDialog = false }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF1C1C1E) else Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .wrapContentHeight()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "सेक्शन फ़िल्टर" else "Filter by Section",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.heightIn(max = 240.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedSectionId = null
                                        showSectionDialog = false
                                    }
                                    .padding(vertical = 10.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "सभी सेक्शन्स" else "All Sections",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        items(sections) { sec ->
                            val isSelected = sec.first == selectedSectionId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AppColors.EmeraldGreen.copy(alpha = 0.1f) else Color.Transparent)
                                    .clickable {
                                        selectedSectionId = sec.first
                                        showSectionDialog = false
                                    }
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sec.second,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ── FACE SCAN CAMERA DIALOG (CameraX Preview & Vector Similarity query) ──
    if (showCameraDialog) {
        Dialog(
            onDismissRequest = { showCameraDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            val lifecycleOwner = LocalLifecycleOwner.current
            val imageCapture = remember { ImageCapture.Builder().build() }
            var matchMessage by remember { mutableStateOf<String?>(null) }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Camera view container
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().apply {
                                    setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imageCapture
                                    )
                                } catch (exc: Exception) {
                                    Log.e("StudentSearch", "Use case binding failed", exc)
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Bounding overlay mask (Face silhouette)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .aspectRatio(1.2f)
                                .border(2.dp, AppColors.EmeraldGreen, RoundedCornerShape(100.dp))
                        )
                    }

                    // Floating details
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isHindi) "सहोदर/छात्र फेस स्कैनर" else "Face Scanner lookup",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isHindi) "चेहरे को सर्कल के बीच में रखें" else "Position face in the circle area",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Bottom Trigger button
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isMatchingFace) {
                            CircularProgressIndicator(color = AppColors.EmeraldGreen, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = matchMessage ?: (if (isHindi) "चेहरा पहचान रहे हैं..." else "Identifying face..."),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Button(
                                onClick = {
                                    isMatchingFace = true
                                    matchMessage = if (isHindi) "फोटो खींच रहे हैं..." else "Capturing photo..."
                                    
                                    try {
                                        imageCapture.takePicture(
                                            ContextCompat.getMainExecutor(context),
                                            object : ImageCapture.OnImageCapturedCallback() {
                                                override fun onCaptureSuccess(image: ImageProxy) {
                                                    scope.launch {
                                                        try {
                                                            matchMessage = if (isHindi) "चेहरे की विशेषताएं पहचान रहे हैं..." else "Analyzing face features..."
                                                            val bitmap = withContext(Dispatchers.IO) {
                                                                imageProxyToBitmap(image)
                                                            }
                                                            image.close()
                                                            
                                                            val faceVector = faceClassifier.detectAndExtractVector(bitmap)
                                                            if (faceVector == null) {
                                                                isMatchingFace = false
                                                                matchMessage = null
                                                                Toast.makeText(context, if (isHindi) "फ़ोटो में कोई चेहरा नहीं मिला।" else "No face detected in photo.", Toast.LENGTH_LONG).show()
                                                                return@launch
                                                            }

                                                            matchMessage = if (isHindi) "डेटाबेस से मिलान कर रहे हैं..." else "Matching with database..."
                                                            
                                                            val localVectors = withContext(Dispatchers.IO) {
                                                                db.institutionDao().getStudentImageVectorsForOrgs(
                                                                    listOfNotNull(activeWorkspace?.childOrgId, activeWorkspace?.parentOrgId)
                                                                )
                                                            }

                                                            if (localVectors.isEmpty()) {
                                                                isMatchingFace = false
                                                                matchMessage = null
                                                                Toast.makeText(context, if (isHindi) "डेटाबेस में कोई पंजीकृत फ़ेस वेक्टर नहीं मिला。" else "No registered face vectors in database.", Toast.LENGTH_LONG).show()
                                                                return@launch
                                                            }

                                                            val matchedStudentsAndScores = localVectors.mapNotNull { localVec ->
                                                                val similarity = calculateCosineSimilarity(faceVector, localVec.faceVector)
                                                                if (similarity >= 0.40f) {
                                                                    localVec.studentId to similarity
                                                                } else {
                                                                    null
                                                                }
                                                            }

                                                            if (matchedStudentsAndScores.isNotEmpty()) {
                                                                val sortedMatches = matchedStudentsAndScores.sortedByDescending { it.second }.take(3)
                                                                val studentIds = sortedMatches.map { it.first }
                                                                
                                                                val students = withContext(Dispatchers.IO) {
                                                                    db.institutionDao().getStudentsByIds(studentIds)
                                                                }
                                                                
                                                                val matches = sortedMatches.mapNotNull { match ->
                                                                    val studentEntity = students.find { it.id == match.first }
                                                                    if (studentEntity != null) {
                                                                        val searchResult = StudentSearchResult(
                                                                            id = studentEntity.id,
                                                                            name = studentEntity.name,
                                                                            sr_number = studentEntity.srNumber,
                                                                            roll_number = studentEntity.rollNumber,
                                                                            class_name = studentEntity.className,
                                                                            section_name = studentEntity.sectionName,
                                                                            guardian_name = studentEntity.guardianName,
                                                                            guardian_mobile = studentEntity.guardianMobile,
                                                                            image_url = studentEntity.imageUrl
                                                                        )
                                                                        val percentageScore = match.second * 100.0
                                                                        searchResult to percentageScore
                                                                    } else {
                                                                        null
                                                                    }
                                                                }

                                                                if (matches.isNotEmpty()) {
                                                                    topMatches = matches
                                                                    showCameraDialog = false
                                                                    showTopMatchesDialog = true
                                                                } else {
                                                                    Toast.makeText(context, if (isHindi) "कोई मेल खाता चेहरा नहीं मिला।" else "No matching face found.", Toast.LENGTH_LONG).show()
                                                                }
                                                            } else {
                                                                Toast.makeText(context, if (isHindi) "कोई मेल खाता चेहरा नहीं मिला।" else "No matching face found.", Toast.LENGTH_LONG).show()
                                                            }
                                                        } catch (e: Exception) {
                                                            Log.e("FaceSearch", "Face matching failed", e)
                                                            Toast.makeText(context, "Face Lookup Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                                        } finally {
                                                            isMatchingFace = false
                                                            matchMessage = null
                                                        }
                                                    }
                                                }

                                                override fun onError(exception: ImageCaptureException) {
                                                    Log.e("CameraCapture", "Failed to capture image", exception)
                                                    isMatchingFace = false
                                                    matchMessage = null
                                                    Toast.makeText(context, "Camera Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        )
                                    } catch (e: Exception) {
                                        Log.e("CameraCapture", "Failed to start capture", e)
                                        isMatchingFace = false
                                        matchMessage = null
                                        Toast.makeText(context, "Camera Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(72.dp)
                                    .border(3.dp, Color.White, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Lucide.Camera,
                                    contentDescription = "Trigger Face Lookup",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Close scanner button
                        IconButton(
                            onClick = { showCameraDialog = false },
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(imageVector = Lucide.X, contentDescription = "Close Scanner", tint = Color.White)
                        }
                    }
                }
            }
        }
    }

    // ── IMAGE SEARCH SELECTION DIALOG ──
    if (showOptionDialog) {
        Dialog(onDismissRequest = { showOptionDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color(0xFF1C1C1E) else Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isHindi) "फ़ोटो खोज विकल्प" else "Photo Search Options",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Option 1: Camera
                    Button(
                        onClick = {
                            showOptionDialog = false
                            val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            if (permission == PackageManager.PERMISSION_GRANTED) {
                                showCameraDialog = true
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Lucide.Camera,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "कैमरा से फ़ोटो लें" else "Take Photo from Camera",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 2: Gallery
                    Button(
                        onClick = {
                            showOptionDialog = false
                            galleryLauncher.launch("image/*")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen.copy(alpha = 0.12f), contentColor = AppColors.EmeraldGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Lucide.Image,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "गैलरी से अपलोड करें" else "Upload from Gallery",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Cancel
                    TextButton(
                        onClick = { showOptionDialog = false }
                    ) {
                        Text(
                            text = if (isHindi) "रद्द करें" else "Cancel",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // ── GALLERY ANALYSIS LOADING DIALOG ──
    if (isGalleryMatching) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF1C1C1E) else Color.White,
                modifier = Modifier.padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = AppColors.EmeraldGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isHindi) "फ़ोटो का विश्लेषण कर रहे हैं..." else "Analyzing photo details...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }

    // ── TOP MATCHES DIALOG ──
    if (showTopMatchesDialog) {
        Dialog(
            onDismissRequest = { showTopMatchesDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color(0xFF1C1C1E) else Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isHindi) "शीर्ष मिलान (Top Matches)" else "Top Matches",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        items(topMatches) { (student, score) ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showTopMatchesDialog = false
                                        onStudentClick(student.id)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    0.5.dp,
                                    if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
                                ),
                                color = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Student Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(if (isDark) Color(0xFF1C1C1E) else Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!student.image_url.isNullOrEmpty()) {
                                            AsyncImage(
                                                model = student.image_url,
                                                contentDescription = student.name,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Lucide.User,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Name and Class
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = student.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        val clsName = student.class_name?.takeIf { it != "null" && it.isNotBlank() }
                                        val secName = student.section_name?.takeIf { it != "null" && it.isNotBlank() }
                                        val classText = if (clsName != null) "$clsName-${secName ?: "A"}" else "N/A"
                                        Text(
                                            text = classText,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Similarity Score Badge
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(
                                            0.5.dp,
                                            AppColors.EmeraldGreen.copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text(
                                            text = "$score% Match",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.EmeraldGreen,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { showTopMatchesDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                    ) {
                        Text(
                            text = if (isHindi) "रद्द करें" else "Cancel",
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
