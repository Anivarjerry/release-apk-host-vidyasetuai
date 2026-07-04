package com.vidyasetuai.feature_feed.presentation.screen

import android.util.Log
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.database.AppDatabase
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.contentOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailsSubScreen(
    state: InstitutionUiState,
    studentId: String,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    val db = remember { AppDatabase.getDatabase(context) }
    val dao = remember { db.institutionDao() }

    val stateStudent = state.selectedStudentDetail
    var localStudent by remember { mutableStateOf<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity?>(null) }

    LaunchedEffect(stateStudent) {
        if (stateStudent != null) {
            localStudent = stateStudent
        }
    }

    // Local data loading states
    var siblings by remember { mutableStateOf<List<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity>>(emptyList()) }
    var feePayments by remember { mutableStateOf<List<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentFeePaymentEntity>>(emptyList()) }
    var additionalFees by remember { mutableStateOf<List<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentAdditionalFeeEntity>>(emptyList()) }
    var classBasicFee by remember { mutableStateOf<Double>(0.0) }
    var examMarks by remember { mutableStateOf<List<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentExamMarkEntity>>(emptyList()) }
    var examSubjectSettings by remember { mutableStateOf<List<com.vidyasetuai.feature_institution.data.local.entity.LocalExamSubjectSettingEntity>>(emptyList()) }
    var orgExams by remember { mutableStateOf<List<com.vidyasetuai.feature_institution.data.local.entity.LocalOrganizationExamEntity>>(emptyList()) }
    var attendanceList by remember { mutableStateOf<List<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentAttendanceEntity>>(emptyList()) }
    var isLocalLoading by remember { mutableStateOf(true) }

    // Dropdown / selector states for Call / WhatsApp action
    var actionNumbers by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var pendingActionType by remember { mutableStateOf<String?>(null) } // "call" or "whatsapp"
    var showNumberSelector by remember { mutableStateOf(false) }

    // Toggle expansion states
    var isProfileExpanded by remember { mutableStateOf(false) }
    var isParentsExpanded by remember { mutableStateOf(false) }
    var isBankExpanded by remember { mutableStateOf(false) }
    var expandedExamId by remember { mutableStateOf<String?>(null) }
    var isAttendanceExpanded by remember { mutableStateOf(false) }

    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val inputBg = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)

    fun getWhatsAppNumber(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        return if (digits.length == 10) "91$digits" else digits
    }

    // Automatically trigger ViewModel load
    LaunchedEffect(studentId) {
        viewModel.onEvent(InstitutionEvent.LoadStudentProfileDetails(studentId))
    }

    // Fetch related offline info as soon as student profile is loaded by ViewModel
    LaunchedEffect(stateStudent) {
        val currentStudent = stateStudent
        if (currentStudent != null) {
            isLocalLoading = true
            withContext(Dispatchers.IO) {
                try {
                    // 1. Sibling Lookup (Same guardian, different student ID)
                    val sibs = if (currentStudent.guardianId.isNotEmpty()) {
                        dao.getStudentsByGuardianId(currentStudent.guardianId).filter { it.id != currentStudent.id }
                    } else emptyList()

                    // 2. Fee Payments
                    val payments = dao.getStudentFeePayments(listOf(currentStudent.id))

                    // 3. Additional Fees
                    val addFees = dao.getStudentAdditionalFees(currentStudent.id)

                    // 4. Resolve Class Basic Fee from setups table (summing all matched entries)
                    var basicFee = 0.0
                    try {
                        val setup = dao.getChildOrgSetup(currentStudent.organizationId)
                        if (setup != null && setup.feesStructureJson.isNotEmpty()) {
                            val jsonArray = Json.parseToJsonElement(setup.feesStructureJson).jsonArray
                            for (element in jsonArray) {
                                val obj = element.jsonObject
                                val classIdInFee = obj["organization_class_id"]?.jsonPrimitive?.contentOrNull
                                    ?: obj["class_id"]?.jsonPrimitive?.contentOrNull
                                if (classIdInFee == currentStudent.classId) {
                                    basicFee += obj["amount"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("StudentDetails", "Error parsing class basic fee from setup", e)
                    }

                    // 5. Exam Marks
                    val marks = dao.getStudentExamMarksForStudent(currentStudent.id)

                    // 5a. Load exam settings and exams for runtime lookup (Approach B)
                    val settings = dao.getExamSubjectSettings(currentStudent.organizationId, currentStudent.activeSessionId ?: "")
                    val exams = dao.getExams(currentStudent.organizationId, currentStudent.activeSessionId ?: "")

                    // 6. Attendance Logs
                    val atts = dao.getStudentAttendance(listOf(currentStudent.id))

                    withContext(Dispatchers.Main) {
                        siblings = sibs
                        feePayments = payments
                        additionalFees = addFees
                        classBasicFee = basicFee
                        examMarks = marks
                        examSubjectSettings = settings
                        orgExams = exams
                        attendanceList = atts
                        isLocalLoading = false
                    }
                } catch (e: Exception) {
                    Log.e("StudentDetails", "Error loading offline data", e)
                    withContext(Dispatchers.Main) {
                        isLocalLoading = false
                    }
                }
            }
        }
    }

    // Background Network Sync (Runs once per studentId change to pull latest details, fees, and payments)
    var hasSyncedStudentId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(studentId, stateStudent) {
        if (stateStudent != null && hasSyncedStudentId != studentId) {
            hasSyncedStudentId = studentId
            scope.launch(Dispatchers.IO) {
                try {
                    // 1. Sync student profile, enrollment class/section/roll number, parent qualification etc.
                    viewModel.repository.syncStudentProfileDetails(studentId)
                    
                    // 2. Sync additional fees
                    viewModel.repository.syncStudentAdditionalFees(studentId)
                    
                    // 3. Sync fee payments
                    viewModel.repository.getFeePayments(listOf(studentId), forceRefresh = true)
                    
                    // Reload local student and additional stats from DB
                    val updatedStudent = dao.getStudentById(studentId)
                    val updatedPayments = dao.getStudentFeePayments(listOf(studentId))
                    val updatedAddFees = dao.getStudentAdditionalFees(studentId)
                    val updatedSibs = if (updatedStudent?.guardianId?.isNotEmpty() == true) {
                        dao.getStudentsByGuardianId(updatedStudent.guardianId).filter { it.id != studentId }
                    } else emptyList()
                    var updatedBasicFee = 0.0
                    val currentClassId = updatedStudent?.classId ?: stateStudent.classId
                    try {
                        val setup = dao.getChildOrgSetup(stateStudent.organizationId)
                        if (setup != null && setup.feesStructureJson.isNotEmpty()) {
                            val jsonArray = Json.parseToJsonElement(setup.feesStructureJson).jsonArray
                            for (element in jsonArray) {
                                val obj = element.jsonObject
                                val classIdInFee = obj["organization_class_id"]?.jsonPrimitive?.contentOrNull
                                    ?: obj["class_id"]?.jsonPrimitive?.contentOrNull
                                if (classIdInFee == currentClassId) {
                                    updatedBasicFee += obj["amount"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("StudentDetails", "Error parsing class basic fee during sync", e)
                    }

                    withContext(Dispatchers.Main) {
                        if (updatedStudent != null) {
                            localStudent = updatedStudent
                        }
                        siblings = updatedSibs
                        feePayments = updatedPayments
                        additionalFees = updatedAddFees
                        classBasicFee = updatedBasicFee
                    }
                } catch (e: Exception) {
                    Log.e("StudentDetails", "Background sync failed for studentId: $studentId", e)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "छात्र विवरण" else "Student Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val student = localStudent
            if (student == null || isLocalLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AppColors.EmeraldGreen
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ═════════════════════════════════════════════════════════════
                    // 1. Header Profile Card
                    // ═════════════════════════════════════════════════════════════
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(CircleShape)
                                            .background(inputBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val img = student.imageUrl ?: ""
                                        if (img.isNotEmpty()) {
                                            AsyncImage(
                                                model = img,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Lucide.User,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = student.name.uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                            text = "SR: ${student.srNumber} | Roll: ${student.rollNumber ?: "—"}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                                            modifier = Modifier.padding(top = 6.dp)
                                        ) {
                                            val classText = "${student.className ?: "N/A"} - ${student.sectionName ?: "—"}"
                                            Text(
                                                text = classText,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AppColors.EmeraldGreen,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                // Guardian Info Row
                                if (!student.guardianName.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(borderVal))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(inputBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val gImage = student.guardianImageUrl ?: ""
                                            if (gImage.isNotEmpty()) {
                                                AsyncImage(
                                                    model = gImage,
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Lucide.Users,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = student.guardianName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            Text(
                                                text = "${student.guardianRelationshipName ?: (if (isHindi) "अभिभावक" else "Guardian")}",
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                // Action Buttons (Call / WhatsApp)
                                Spacer(modifier = Modifier.height(14.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(borderVal))
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // WhatsApp Button
                                    Button(
                                        onClick = {
                                            val validNumbers = listOfNotNull(
                                                student.guardianMobile?.takeIf { it.isNotBlank() && it != "—" && it != "null" }?.let { (if (isHindi) "अभिभावक" else "Guardian") + " ($it)" to it },
                                                student.fatherMobile?.takeIf { it.isNotBlank() && it != "—" && it != "null" }?.let { (if (isHindi) "पिता" else "Father") + " ($it)" to it },
                                                student.motherMobile?.takeIf { it.isNotBlank() && it != "—" && it != "null" }?.let { (if (isHindi) "माता" else "Mother") + " ($it)" to it }
                                            ).distinctBy { it.second }

                                            if (validNumbers.isEmpty()) {
                                                Toast.makeText(context, if (isHindi) "कोई मोबाइल नंबर उपलब्ध नहीं है" else "No mobile numbers available", Toast.LENGTH_SHORT).show()
                                            } else if (validNumbers.size == 1) {
                                                val number = validNumbers.first().second
                                                val formatted = getWhatsAppNumber(number)
                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                    this.data = Uri.parse("https://api.whatsapp.com/send?phone=$formatted")
                                                }
                                                context.startActivity(intent)
                                            } else {
                                                actionNumbers = validNumbers
                                                pendingActionType = "whatsapp"
                                                showNumberSelector = true
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(imageVector = Lucide.MessageSquareCode, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                            Text(text = if (isHindi) "व्हाट्सएप" else "WhatsApp", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Direct Call Button
                                    Button(
                                        onClick = {
                                            val validNumbers = listOfNotNull(
                                                student.guardianMobile?.takeIf { it.isNotBlank() && it != "—" && it != "null" }?.let { (if (isHindi) "अभिभावक" else "Guardian") + " ($it)" to it },
                                                student.fatherMobile?.takeIf { it.isNotBlank() && it != "—" && it != "null" }?.let { (if (isHindi) "पिता" else "Father") + " ($it)" to it },
                                                student.motherMobile?.takeIf { it.isNotBlank() && it != "—" && it != "null" }?.let { (if (isHindi) "माता" else "Mother") + " ($it)" to it }
                                            ).distinctBy { it.second }

                                            if (validNumbers.isEmpty()) {
                                                Toast.makeText(context, if (isHindi) "कोई मोबाइल नंबर उपलब्ध नहीं है" else "No mobile numbers available", Toast.LENGTH_SHORT).show()
                                            } else if (validNumbers.size == 1) {
                                                val number = validNumbers.first().second
                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                    this.data = Uri.parse("tel:$number")
                                                }
                                                context.startActivity(intent)
                                            } else {
                                                actionNumbers = validNumbers
                                                pendingActionType = "call"
                                                showNumberSelector = true
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(imageVector = Lucide.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                            Text(text = if (isHindi) "कॉल करें" else "Call", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ═════════════════════════════════════════════════════════════
                    // 2. Student Profile Details Card (Collapsible)
                    // ═════════════════════════════════════════════════════════════
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isHindi) "छात्र प्रोफाइल विवरण" else "Student Profile Details",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AppColors.EmeraldGreen
                                    )
                                    IconButton(
                                        onClick = { isProfileExpanded = !isProfileExpanded },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isProfileExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                                            contentDescription = null,
                                            tint = AppColors.EmeraldGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DetailRow(label = if (isHindi) "लिंग:" else "Gender:", value = student.gender)
                                    DetailRow(label = if (isHindi) "जन्म तिथि:" else "Date of Birth:", value = student.dateOfBirth)
                                    DetailRow(label = if (isHindi) "श्रेणी:" else "Category:", value = student.categoryName)
                                    DetailRow(label = if (isHindi) "रक्त समूह:" else "Blood Group:", value = student.bloodGroupName)

                                    if (isProfileExpanded) {
                                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(borderVal))
                                        DetailRow(label = if (isHindi) "मातृभाषा:" else "Mother Tongue:", value = student.motherTongueName)
                                        DetailRow(label = if (isHindi) "धर्म:" else "Religion:", value = student.religion)
                                        DetailRow(label = if (isHindi) "राष्ट्रीयता:" else "Nationality:", value = student.nationality)
                                        DetailRow(label = if (isHindi) "पहचान चिन्ह:" else "ID Mark:", value = student.identificationMark)
                                        DetailRow(label = if (isHindi) "एकल कन्या:" else "Single Girl Child:", value = if (student.isSingleGirlChild) (if (isHindi) "हाँ" else "Yes") else (if (isHindi) "नहीं" else "No"))
                                        DetailRow(label = if (isHindi) "जाति प्रमाण पत्र:" else "Caste Cert No:", value = student.casteCertificateNumber)
                                        DetailRow(label = if (isHindi) "आधार नंबर:" else "Student Aadhaar:", value = student.studentAadhar)

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = if (isHindi) "स्थायी पता" else "Permanent Address", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = inputBg,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "${student.addressDetails ?: "—"} ${student.addressAreaName ?: ""}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                modifier = Modifier.padding(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ═════════════════════════════════════════════════════════════
                    // 3. Parent Details Card (Collapsible)
                    // ═════════════════════════════════════════════════════════════
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isHindi) "अभिभावक विवरण" else "Parent Details",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AppColors.EmeraldGreen
                                    )
                                    IconButton(
                                        onClick = { isParentsExpanded = !isParentsExpanded },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isParentsExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                                            contentDescription = null,
                                            tint = AppColors.EmeraldGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DetailRow(label = if (isHindi) "पिता का नाम:" else "Father Name:", value = student.fatherName)
                                    DetailRow(label = if (isHindi) "पिता का मोबाइल:" else "Father Mobile:", value = student.fatherMobile)

                                    if (isParentsExpanded) {
                                        DetailRow(label = if (isHindi) "पिता की ईमेल:" else "Father Email:", value = student.fatherEmail)
                                        DetailRow(label = if (isHindi) "पिता की योग्यता:" else "Father Qual:", value = student.fatherQualification)
                                        DetailRow(label = if (isHindi) "पिता का व्यवसाय:" else "Father Occ:", value = student.fatherOccupation)
                                        DetailRow(label = if (isHindi) "पिता का आधार:" else "Father Aadhaar:", value = student.parentsAadhaarFather)
                                        
                                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(borderVal))
                                        
                                        DetailRow(label = if (isHindi) "माता का नाम:" else "Mother Name:", value = student.motherName)
                                        DetailRow(label = if (isHindi) "माता का मोबाइल:" else "Mother Mobile:", value = student.motherMobile)
                                        DetailRow(label = if (isHindi) "माता की ईमेल:" else "Mother Email:", value = student.motherEmail)
                                        DetailRow(label = if (isHindi) "माता की योग्यता:" else "Mother Qual:", value = student.motherQualification)
                                        DetailRow(label = if (isHindi) "माता का व्यवसाय:" else "Mother Occ:", value = student.motherOccupation)
                                        DetailRow(label = if (isHindi) "माता का आधार:" else "Mother Aadhaar:", value = student.parentsAadhaarMother)
                                        
                                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(borderVal))
                                        
                                        DetailRow(label = if (isHindi) "पारिवारिक वार्षिक आय:" else "Annual Income:", value = student.familyAnnualIncome?.let { "₹${it.toInt()}" })
                                    }
                                }
                            }
                        }
                    }

                    // ═════════════════════════════════════════════════════════════
                    // 4. Transport Stopped stop details
                    // ═════════════════════════════════════════════════════════════
                    item {
                        val busAssignment = state.selectedStudentBusAssignment
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isHindi) "आवंटित परिवहन" else "Assigned Transport",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AppColors.EmeraldGreen,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                if (busAssignment != null && busAssignment.busNumber.isNotEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(inputBg, RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    color = AppColors.EmeraldGreen.copy(alpha = 0.12f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(imageVector = Lucide.Bus, contentDescription = null, tint = AppColors.EmeraldGreen, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = busAssignment.busNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
                                            Text(text = "${if (isHindi) "रूट" else "Route"}: ${busAssignment.routeName ?: "—"} • Stop: ${busAssignment.pickupStop ?: "—"}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                } else {
                                    Text(
                                        text = if (isHindi) "कोई बस आवंटित नहीं है" else "No bus route assigned.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                    )
                                }
                            }
                        }
                    }

                    // ═════════════════════════════════════════════════════════════
                    // 5. Sibling Card (Calculated Locally)
                    // ═════════════════════════════════════════════════════════════
                    if (siblings.isNotEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                                color = cardBg
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = if (isHindi) "सहोदर छात्र (Siblings)" else "Siblings",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AppColors.EmeraldGreen,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        siblings.forEach { sib ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .border(0.5.dp, borderVal, RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        viewModel.onEvent(InstitutionEvent.LoadStudentProfileDetails(sib.id))
                                                    }
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .background(inputBg),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    val sibImg = sib.imageUrl ?: ""
                                                    if (sibImg.isNotEmpty()) {
                                                        AsyncImage(model = sibImg, contentDescription = null, modifier = Modifier.fillMaxSize())
                                                    } else {
                                                        Icon(imageVector = Lucide.User, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = sib.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                                                    Text(text = "${sib.className ?: "—"} - ${sib.sectionName ?: "—"}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                Icon(imageVector = Lucide.ChevronRight, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ═════════════════════════════════════════════════════════════
                    // 6. Bank Account Card (Collapsible)
                    // ═════════════════════════════════════════════════════════════
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isHindi) "बैंक विवरण" else "Bank Account Details",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AppColors.EmeraldGreen
                                    )
                                    IconButton(
                                        onClick = { isBankExpanded = !isBankExpanded },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isBankExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                                            contentDescription = null,
                                            tint = AppColors.EmeraldGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DetailRow(label = if (isHindi) "बैंक का नाम:" else "Bank Name:", value = student.bankName)
                                    DetailRow(label = if (isHindi) "खाता संख्या:" else "Account Number:", value = student.bankAccountNumber)

                                    if (isBankExpanded) {
                                        DetailRow(label = if (isHindi) "शाखा (Branch):" else "Branch Name:", value = student.bankBranch)
                                        DetailRow(label = "IFSC:", value = student.bankIfsc)
                                        DetailRow(label = if (isHindi) "खाताधारक:" else "Holder Name:", value = student.bankAccountHolder)
                                    }
                                }
                            }
                        }
                    }

                    // ═════════════════════════════════════════════════════════════
                    // 7. Fee Summary Ledger
                    // ═════════════════════════════════════════════════════════════
                    item {
                        val totalAddFee = additionalFees.sumOf { it.amount }
                        val totalExpFee = classBasicFee + totalAddFee
                        val totalPaidFee = feePayments.sumOf { it.amountPaid }
                        val totalPendingFee = totalExpFee - totalPaidFee

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isHindi) "शुल्क सारांश विवरण" else "Fee Summary Ledger",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AppColors.EmeraldGreen,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Expected
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(inputBg, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = if (isHindi) "कुल अपेक्षित" else "Expected", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "₹${totalExpFee.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                    }
                                    // Paid
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(AppColors.EmeraldGreen.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                            .border(0.5.dp, AppColors.EmeraldGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = if (isHindi) "जमा शुल्क" else "Paid", fontSize = 9.sp, color = AppColors.EmeraldGreen)
                                        Text(text = "₹${totalPaidFee.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                                    }
                                    // Pending
                                    val pendingColor = if (totalPendingFee > 0) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                                    val pendingBg = if (totalPendingFee > 0) Color(0xFFEF4444).copy(alpha = 0.08f) else inputBg
                                    val pendingBorder = if (totalPendingFee > 0) Color(0xFFEF4444).copy(alpha = 0.2f) else borderVal
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(pendingBg, RoundedCornerShape(8.dp))
                                            .border(0.5.dp, pendingBorder, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = if (isHindi) "बकाया" else "Pending", fontSize = 9.sp, color = pendingColor)
                                        Text(text = "₹${totalPendingFee.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = pendingColor)
                                    }
                                }

                                if (totalExpFee > 0) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    val ratio = (totalPaidFee / totalExpFee).toFloat()
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = if (isHindi) "फीस संग्रह अनुपात" else "Collection Ratio", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "${(ratio * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = ratio,
                                        color = AppColors.EmeraldGreen,
                                        trackColor = inputBg,
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                                    )
                                }
                            }
                        }
                    }

                    // ═════════════════════════════════════════════════════════════
                    // 8. Exam Marks Accordion (Grouped by Exam ID)
                    // ═════════════════════════════════════════════════════════════
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isHindi) " परीक्षा परिणाम सूची" else "Examinations (Click for Marks)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AppColors.EmeraldGreen,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                if (examMarks.isNotEmpty()) {
                                    val groupedMarks = examMarks.groupBy { it.examId }
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        groupedMarks.forEach { (examId, marks) ->
                                            val isExpanded = expandedExamId == examId
                                            // Lookup exam name from local_organization_exams if null in marks
                                            val examName = marks.firstOrNull()?.examName
                                                ?: orgExams.firstOrNull { it.id == examId }?.name
                                                ?: "Exam"

                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .border(0.5.dp, borderVal, RoundedCornerShape(8.dp))
                                                    .clip(RoundedCornerShape(8.dp))
                                            ) {
                                                // Exam Row Trigger
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { expandedExamId = if (isExpanded) null else examId }
                                                        .padding(12.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(text = examName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                                                    Icon(
                                                        imageVector = if (isExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(14.dp),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                // Subject Marks details
                                                if (isExpanded) {
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(inputBg)
                                                            .padding(10.dp),
                                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        // Table Header
                                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                            Text(text = if (isHindi) "विषय" else "Subject", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            Text(text = if (isHindi) "अंक (Marks)" else "Marks", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            Text(text = if (isHindi) "स्थिति" else "Status", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        }
                                                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(borderVal))

                                                        marks.forEach { mark ->
                                                            val obt = mark.obtainedMarks
                                                            val max = mark.maxMarks ?: 100.0
                                                            val min = mark.minimumPassingMarks ?: 33.0
                                                            val isAbs = mark.isAbsent
                                                            val isMed = mark.isMedicalLeave
                                                            val hasMarks = obt != null
                                                            val isPassed = hasMarks && obt!! >= min

                                                            val statusText = when {
                                                                isAbs -> if (isHindi) "अनुपस्थित" else "Absent"
                                                                isMed -> if (isHindi) "अवकाश" else "Medical"
                                                                hasMarks -> if (isPassed) (if (isHindi) "उत्तीर्ण" else "Passed") else (if (isHindi) "अनुत्तीर्ण" else "Failed")
                                                                else -> if (isHindi) "लंबित" else "Pending"
                                                            }
                                                            val statusColor = when {
                                                                isAbs -> Color(0xFFEF4444)
                                                                isMed -> Color(0xFFF59E0B)
                                                                hasMarks -> if (isPassed) AppColors.EmeraldGreen else Color(0xFFEF4444)
                                                                else -> Color(0xFF8E8E93)
                                                            }

                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                val resolvedSubjectName = null
                                                                Text(text = resolvedSubjectName ?: "—", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                                                                Text(
                                                                    text = if (isAbs || isMed) "—" else "${obt?.toInt() ?: "—"}/${max.toInt()}",
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = MaterialTheme.colorScheme.onBackground
                                                                )
                                                                Surface(
                                                                    shape = RoundedCornerShape(4.dp),
                                                                    color = statusColor.copy(alpha = 0.12f),
                                                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, statusColor.copy(alpha = 0.3f))
                                                                ) {
                                                                    Text(
                                                                        text = statusText,
                                                                        fontSize = 8.sp,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = statusColor,
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
                                } else {
                                    Text(
                                        text = if (isHindi) "कोई परीक्षा डेटा उपलब्ध नहीं है" else "No exam marks recorded.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                    )
                                }
                            }
                        }
                    }

                    // ═════════════════════════════════════════════════════════════
                    // 9. Attendance Summary & logs
                    // ═════════════════════════════════════════════════════════════
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                            color = cardBg
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isHindi) "उपस्थिति सारांश (Attendance)" else "Attendance Summary",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AppColors.EmeraldGreen
                                    )
                                    IconButton(
                                        onClick = { isAttendanceExpanded = !isAttendanceExpanded },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isAttendanceExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                                            contentDescription = null,
                                            tint = AppColors.EmeraldGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                val totalDays = attendanceList.size
                                val presentDays = attendanceList.count { it.status.equals("Present", ignoreCase = true) }
                                val attRatio = if (totalDays > 0) (presentDays.toFloat() / totalDays.toFloat()) else 0.0f

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = if (isHindi) "कुल उपस्थिति" else "Total Attendance", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "$presentDays / $totalDays ${if (isHindi) "दिन" else "days"}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = AppColors.EmeraldGreen.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = "${(attRatio * 100).toInt()}%",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.EmeraldGreen,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                if (isAttendanceExpanded && attendanceList.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(borderVal))
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = if (isHindi) "हालिया उपस्थिति इतिहास" else "Recent Attendance Logs",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        // Display up to 10 logs
                                        attendanceList.take(10).forEach { log ->
                                            val isPresent = log.status.equals("Present", ignoreCase = true)
                                            val statusCol = if (isPresent) AppColors.EmeraldGreen else Color(0xFFEF4444)

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(inputBg, RoundedCornerShape(6.dp))
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = log.attendanceDate, fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = statusCol.copy(alpha = 0.12f)
                                                ) {
                                                    Text(
                                                        text = if (isPresent) (if (isHindi) "उपस्थित" else "Present") else (if (isHindi) "अनुपस्थित" else "Absent"),
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = statusCol,
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
                }
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // MULTIPLE CONTACT SELECTOR MODAL (If student has multiple contact numbers)
    // ═════════════════════════════════════════════════════════════════════════
    if (showNumberSelector) {
        AlertDialog(
            onDismissRequest = { showNumberSelector = false },
            title = {
                Text(
                    text = if (isHindi) "नंबर चुनें" else "Choose Contact Number",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    actionNumbers.forEach { (label, number) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(inputBg, RoundedCornerShape(8.dp))
                                .clickable {
                                    showNumberSelector = false
                                    if (pendingActionType == "whatsapp") {
                                        val formatted = getWhatsAppNumber(number)
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            this.data = Uri.parse("https://api.whatsapp.com/send?phone=$formatted")
                                        }
                                        context.startActivity(intent)
                                    } else {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            this.data = Uri.parse("tel:$number")
                                        }
                                        context.startActivity(intent)
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (pendingActionType == "whatsapp") Lucide.MessageSquareCode else Lucide.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = AppColors.EmeraldGreen
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNumberSelector = false }) {
                    Text(text = if (isHindi) "बंद करें" else "Cancel", color = AppColors.EmeraldGreen)
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String?) {
    val displayValue = if (value.isNullOrBlank() || value == "null") "—" else value
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = displayValue,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}
