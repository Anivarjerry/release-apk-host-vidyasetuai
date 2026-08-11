package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_institution.data.local.entity.LocalStudentAdditionalFeeEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class FeeHeadOption(
    val id: String,
    val name: String,
    val code: String? = null
)

data class LedgerFeeItem(
    val name: String,
    val amount: Double
)

@Serializable
private data class RemoteGlobalFeeHeadDto(
    val id: String,
    val name: String,
    val code: String? = null,
    val is_additional: Boolean = false,
    val is_active: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddAdditionalFeeFabSubScreen(
    viewModel: InstitutionViewModel,
    state: InstitutionUiState,
    isHindi: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val surfaceBg = if (isDark) AppColors.CharcoalGray else AppColors.PureWhite
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF9FAFB)
    val cardBorderColor = if (isDark) Color(0xFF262626) else Color(0xFFE5E5E5)
    val textColor = if (isDark) AppColors.PureWhite else Color(0xFF171717)
    val subtitleColor = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)

    var searchQuery by remember { mutableStateOf("") }
    var searchedStudents by remember { mutableStateOf<List<LocalStudentEntity>>(emptyList()) }
    var selectedStudent by remember { mutableStateOf<LocalStudentEntity?>(null) }

    var selectedFeeHead by remember { mutableStateOf<FeeHeadOption?>(null) }
    var isFeeHeadDropdownExpanded by remember { mutableStateOf(false) }

    var feeAmount by remember { mutableStateOf("") }
    var existingAssignedFee by remember { mutableStateOf<LocalStudentAdditionalFeeEntity?>(null) }

    var liveFeeHeads by remember { mutableStateOf<List<FeeHeadOption>>(emptyList()) }

    // Student Fee Ledger Overview State
    var isLedgerExpanded by remember { mutableStateOf(false) }
    var classFeeBreakdown by remember { mutableStateOf<List<LedgerFeeItem>>(emptyList()) }
    var additionalFeeBreakdown by remember { mutableStateOf<List<LedgerFeeItem>>(emptyList()) }
    var totalDues by remember { mutableStateOf(0.0) }
    var totalPaid by remember { mutableStateOf(0.0) }
    var totalDiscount by remember { mutableStateOf(0.0) }
    var totalFine by remember { mutableStateOf(0.0) }
    var netOutstanding by remember { mutableStateOf(0.0) }

    // Fallback list of exact global_fee_heads where is_additional = true
    val fallbackFeeHeadOptions = remember {
        listOf(
            FeeHeadOption("fa060000-0000-0000-0000-000000000004", "Transport Fee / परिवहन शुल्क", "TRANSPORT-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000005", "Sports & Activity Fee / खेलकूद शुल्क", "SPORTS-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000006", "Library Caution Money / पुस्तकालय शुल्क", "LIBRARY-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000007", "Computer Lab Fee / कंप्यूटर लैब शुल्क", "COMPUTER-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000008", "Science Laboratory Fee / विज्ञान लैब शुल्क", "SCIENCE-LAB-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000009", "Smart Class & Multimedia Fee / स्मार्ट क्लास शुल्क", "SMART-CLASS-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-00000000000a", "Annual Function & Cultural Fee / वार्षिक उत्सव शुल्क", "ANNUAL-CULTURAL-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-00000000000b", "School Diary & Syllabus Fee / डायरी शुल्क", "SCHOOL-DIARY-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-00000000000c", "Identity Card Fee / पहचान पत्र (I-Card) शुल्क", "ICARD-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-00000000000e", "Educational Tour & Field Trip Fee / शैक्षणिक भ्रमण शुल्क", "TOUR-TRIP-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-00000000000f", "Cycle & Vehicle Stand Fee / वाहन स्टैंड शुल्क", "PARKING-STAND-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000010", "Medical & First Aid Fee / चिकित्सा व प्राथमिक उपचार शुल्क", "MEDICAL-FIRSTAID-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000011", "Student Group Insurance Fee / छात्र समूह बीमा शुल्क", "GROUP-INSURANCE-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000012", "Co-curricular Activities & Clubs Fee / सह-पाठ्यचर्या शुल्क", "CLUBS-ACTIVITIES-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000013", "Hostel Accommodation Fee / छात्रावास शुल्क", "HOSTEL-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000014", "Mess & Boarding Meal Fee / मेस भोजन शुल्क", "MESS-MEAL-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000015", "Admission Caution Deposit / कॉशन मनी शुल्क", "CAUTION-MONEY-DEPOSIT"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000016", "School Uniform Fee / ड्रेस व पोशाक शुल्क", "UNIFORM-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000017", "Textbooks & Stationery Fee / पाठ्यपुस्तक शुल्क", "BOOKS-STATIONERY-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000018", "Prospectus & Registration Form Fee / विवरण पत्रिका शुल्क", "PROSPECTUS-FORM-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-000000000019", "Backlog Exam Fee / बैकलाग परीक्षा शुल्क", "BACKLOG-EXAM-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-00000000001a", "Repeat Course Fee / रीपीट कोर्स शुल्क", "REPEAT-COURSE-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-00000000001b", "Practical Exam Fee / प्रायोगिक परीक्षा शुल्क", "PRACTICAL-EXAM-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-00000000001c", "Agricultural Field Work & RAWE Fee / कृषि क्षेत्र कार्य शुल्क", "RAWE-FIELD-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-00000000001d", "Re-evaluation & Scrutiny Fee / पुनः मूल्यांकन शुल्क", "REEVALUATION-FEE"),
            FeeHeadOption("fa060000-0000-0000-0000-00000000001e", "Degree & Convocation Fee / दीक्षांत समारोह शुल्क", "DEGREE-CONVOCATION-FEE"),
            FeeHeadOption("9a23b048-3ce1-4c16-ae1a-35035d00992f", "Repeat Back Log Fee / बैकलाग दोहराव शुल्क", "REPEAT-BACK-LOG-FEE")
        )
    }

    val displayFeeHeads = if (liveFeeHeads.isNotEmpty()) liveFeeHeads else fallbackFeeHeadOptions

    // Fetch fresh live fee heads directly from Supabase global_fee_heads table
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val remoteList = SupabaseClient.client
                    .from("global_fee_heads")
                    .select {
                        filter {
                            eq("is_additional", true)
                            eq("is_active", true)
                        }
                    }
                    .decodeList<RemoteGlobalFeeHeadDto>()

                if (remoteList.isNotEmpty()) {
                    val mapped = remoteList.map { head ->
                        FeeHeadOption(
                            id = head.id,
                            name = head.name,
                            code = head.code
                        )
                    }
                    withContext(Dispatchers.Main) {
                        liveFeeHeads = mapped
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Trigger student search on query change
    LaunchedEffect(searchQuery) {
        withContext(Dispatchers.IO) {
            val db = com.vidyasetuai.core.database.AppDatabase.getDatabase(context)
            val results = if (searchQuery.isBlank()) {
                db.institutionDao().searchStudentsOffline("")
            } else {
                db.institutionDao().searchStudentsOffline("%$searchQuery%")
            }
            withContext(Dispatchers.Main) {
                searchedStudents = results
            }
        }
    }

    // Calculate complete student fee ledger balance whenever selected student changes
    LaunchedEffect(selectedStudent) {
        val student = selectedStudent
        if (student != null) {
            withContext(Dispatchers.IO) {
                val db = com.vidyasetuai.core.database.AppDatabase.getDatabase(context)
                val dao = db.institutionDao()

                // 1. Additional Fees
                val addFees = dao.getStudentAdditionalFees(student.id).filter { !it.isDeleted }
                val addBreakdown = addFees.map {
                    LedgerFeeItem(it.globalFeeHeadName ?: "Additional Fee", it.amount)
                }

                // 2. Payments History
                val payments = dao.getStudentFeePayments(listOf(student.id)).filter { !it.isDeleted }
                val paidSum = payments.sumOf { it.amountPaid }
                val discountSum = payments.sumOf { it.discountAmount }
                val fineSum = payments.sumOf { it.fineAmount }

                // 3. Standard Class Fees
                val classBreakdown = mutableListOf<LedgerFeeItem>()
                var classBasicSum = 0.0

                var setup = dao.getChildOrgSetup(student.organizationId)
                if (setup == null) setup = dao.getActiveSession()

                if (setup != null && setup.feesStructureJson.isNotEmpty()) {
                    try {
                        val allDbStudents = dao.searchStudentsOffline("")
                        val classMap = allDbStudents.filter { it.classId != null && it.className != null }
                            .associate { it.classId!! to it.className!! }

                        val jsonArray = Json.parseToJsonElement(setup.feesStructureJson).jsonArray
                        for (element in jsonArray) {
                            val obj = element.jsonObject
                            val classIdInFee = obj["organization_class_id"]?.jsonPrimitive?.contentOrNull
                                ?: obj["class_id"]?.jsonPrimitive?.contentOrNull

                            val feeClassName = classMap[classIdInFee]
                            val isMatched = classIdInFee == student.classId ||
                                    (feeClassName != null && student.className != null && feeClassName.equals(student.className, ignoreCase = true))

                            if (isMatched) {
                                val amount = obj["amount"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                                val headId = obj["fee_head_id"]?.jsonPrimitive?.contentOrNull ?: ""

                                val feeHeadName = when (headId) {
                                    "fa060000-0000-0000-0000-000000000001" -> "Registration Fee"
                                    "fa060000-0000-0000-0000-000000000002" -> "Tuition Fee"
                                    "fa060000-0000-0000-0000-000000000003" -> "Examination Fee"
                                    "fa060000-0000-0000-0000-00000000000d" -> "School Development Fee"
                                    else -> "Class Standard Fee"
                                }

                                classBasicSum += amount
                                classBreakdown.add(LedgerFeeItem(feeHeadName, amount))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Fallback standard fees if class structure not found
                if (classBreakdown.isEmpty()) {
                    classBreakdown.add(LedgerFeeItem("Registration Fee", 6000.0))
                    classBreakdown.add(LedgerFeeItem("Tuition Fee", 1400.0))
                    classBreakdown.add(LedgerFeeItem("Examination Fee", 500.0))
                    classBreakdown.add(LedgerFeeItem("School Development Fee", 0.0))
                    classBasicSum = 7900.0
                }

                val duesSum = classBasicSum + addBreakdown.sumOf { it.amount }
                val netBalance = (duesSum + fineSum - (paidSum + discountSum)).coerceAtLeast(0.0)

                withContext(Dispatchers.Main) {
                    classFeeBreakdown = classBreakdown
                    additionalFeeBreakdown = addBreakdown
                    totalDues = duesSum
                    totalPaid = paidSum
                    totalDiscount = discountSum
                    totalFine = fineSum
                    netOutstanding = netBalance
                }
            }
        } else {
            classFeeBreakdown = emptyList()
            additionalFeeBreakdown = emptyList()
            totalDues = 0.0
            totalPaid = 0.0
            totalDiscount = 0.0
            totalFine = 0.0
            netOutstanding = 0.0
        }
    }

    // Check existing assigned fee whenever selected student or fee head changes
    LaunchedEffect(selectedStudent, selectedFeeHead) {
        val student = selectedStudent
        val head = selectedFeeHead
        if (student != null && head != null) {
            withContext(Dispatchers.IO) {
                val db = com.vidyasetuai.core.database.AppDatabase.getDatabase(context)
                val existingList = db.institutionDao().getStudentAdditionalFees(student.id)
                val match = existingList.find { it.globalFeeHeadId == head.id && !it.isDeleted }
                withContext(Dispatchers.Main) {
                    existingAssignedFee = match
                    if (match != null) {
                        feeAmount = if (match.amount % 1.0 == 0.0) {
                            match.amount.toLong().toString()
                        } else {
                            match.amount.toString()
                        }
                    } else {
                        feeAmount = ""
                    }
                }
            }
        } else {
            existingAssignedFee = null
            if (selectedFeeHead == null) feeAmount = ""
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) AppColors.CharcoalGray else Color(0xFFF9FAFB))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Lucide.ArrowLeft,
                    contentDescription = "Back",
                    tint = textColor
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "अतिरिक्त फीस असाइन करें 🏷️" else "Add Additional Fee 🏷️",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            // Card 1: Student Selection
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "1. छात्र चुनें (Select Student)" else "1. Select Student",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedStudent != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AppColors.EmeraldGreen.copy(alpha = 0.1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedStudent = null
                                    existingAssignedFee = null
                                    feeAmount = ""
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = selectedStudent!!.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.EmeraldGreen
                                    )
                                    Text(
                                        text = "${selectedStudent!!.className ?: ""} ${selectedStudent!!.sectionName ?: ""} • SR: ${selectedStudent!!.srNumber ?: "N/A"}",
                                        fontSize = 12.sp,
                                        color = subtitleColor
                                    )
                                }
                                Icon(
                                    imageVector = Lucide.X,
                                    contentDescription = "Change",
                                    tint = AppColors.EmeraldGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(if (isHindi) "छात्र का नाम या SR नंबर खोजें..." else "Search student name or SR No...") },
                            leadingIcon = { Icon(Lucide.Search, contentDescription = null, tint = subtitleColor) },
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 160.dp)
                        ) {
                            items(searchedStudents.take(6)) { student ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedStudent = student }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Lucide.User,
                                        contentDescription = null,
                                        tint = subtitleColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = student.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textColor
                                        )
                                        Text(
                                            text = "${student.className ?: ""} ${student.sectionName ?: ""} • SR: ${student.srNumber ?: "N/A"}",
                                            fontSize = 11.sp,
                                            color = subtitleColor
                                        )
                                    }
                                }
                                Divider(color = cardBorderColor.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }

            // Expandable Fee Ledger Summary Card when student is selected
            if (selectedStudent != null) {
                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isLedgerExpanded = !isLedgerExpanded },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Lucide.FileText,
                                    contentDescription = null,
                                    tint = AppColors.EmeraldGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isHindi) "FEE LEDGER BALANCE (शुल्क लेजर सारांश)" else "FEE LEDGER BALANCE",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = subtitleColor,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Outstanding: ₹${netOutstanding.toInt()} • Dues: ₹${totalDues.toInt()} • Paid: ₹${totalPaid.toInt()}",
                                        fontSize = 12.sp,
                                        color = if (netOutstanding > 0) Color(0xFFBE123C) else AppColors.EmeraldGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (isLedgerExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                                contentDescription = null,
                                tint = subtitleColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AnimatedVisibility(visible = isLedgerExpanded) {
                            Column(modifier = Modifier.padding(top = 16.dp)) {
                                Divider(color = cardBorderColor.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(12.dp))

                                // STANDARD CLASS FEES
                                Text(
                                    text = "STANDARD CLASS FEES (कक्षा अनिवार्य शुल्क)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = subtitleColor,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                classFeeBreakdown.forEach { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(item.name, fontSize = 13.sp, color = textColor)
                                        Text("₹${item.amount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = cardBorderColor.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(12.dp))

                                // SUPPLEMENTAL FEES
                                Text(
                                    text = "SUPPLEMENTAL FEES (अतिरिक्त शुल्क)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = subtitleColor,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                if (additionalFeeBreakdown.isEmpty()) {
                                    Text(
                                        text = if (isHindi) "कोई अतिरिक्त शुल्क नहीं" else "No supplemental fees assigned yet",
                                        fontSize = 12.sp,
                                        color = subtitleColor
                                    )
                                } else {
                                    additionalFeeBreakdown.forEach { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(item.name, fontSize = 13.sp, color = textColor)
                                            Text("₹${item.amount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // SUMMARY BOX
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = cardBg,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, cardBorderColor, RoundedCornerShape(10.dp))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("TOTAL DUES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = subtitleColor)
                                                Text("₹${totalDues.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                                            }
                                            Column {
                                                Text("TOTAL PAID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = subtitleColor)
                                                Text("₹${totalPaid.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("TOTAL DISCOUNT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = subtitleColor)
                                                Text("₹${totalDiscount.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                                            }
                                            Column {
                                                Text("TOTAL FINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = subtitleColor)
                                                Text("₹${totalFine.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // NET OUTSTANDING BALANCE BOX
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFFF1F2),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Net Outstanding Balance:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFBE123C))
                                        Text("₹${netOutstanding.toInt()}", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFBE123C))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card 2: Fee Head Selection
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "2. फीस का प्रकार (Select Fee Head)" else "2. Select Fee Head",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedFeeHead?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isFeeHeadDropdownExpanded = true },
                            placeholder = { Text(if (isHindi) "फीस का प्रकार चुनें..." else "Select Fee Head...") },
                            trailingIcon = { Icon(Lucide.ChevronDown, contentDescription = null) },
                            shape = RoundedCornerShape(8.dp)
                        )

                        DropdownMenu(
                            expanded = isFeeHeadDropdownExpanded,
                            onDismissRequest = { isFeeHeadDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            displayFeeHeads.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.name, fontSize = 14.sp) },
                                    onClick = {
                                        selectedFeeHead = option
                                        isFeeHeadDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card 3: Fee Amount Input
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "3. फीस राशि (Enter Fee Amount ₹)" else "3. Enter Fee Amount (₹)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = feeAmount,
                        onValueChange = { input ->
                            val clean = input.filter { char -> char.isDigit() }
                            if (clean.length <= 6) {
                                feeAmount = clean
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(if (isHindi) "उदा. 2500" else "e.g. 2500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Text("₹", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen, modifier = Modifier.padding(start = 12.dp)) },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    // Alert Banner if fee is already assigned for this student & fee head
                    if (existingAssignedFee != null) {
                        val prevAmountInt = existingAssignedFee!!.amount.toInt()
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF3C7),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.Info,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi)
                                        "पहले से असाइन की गई फीस: ₹$prevAmountInt। आप इसे केवल ₹$prevAmountInt से अधिक राशि दर्ज करके ही अपडेट कर सकते हैं।"
                                    else
                                        "Previously assigned fee: ₹$prevAmountInt. You can only update to an amount greater than ₹$prevAmountInt.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Submit Button
        Button(
            onClick = {
                val student = selectedStudent
                val head = selectedFeeHead
                val amount = feeAmount.toDoubleOrNull() ?: 0.0
                val existing = existingAssignedFee

                if (student == null) {
                    Toast.makeText(context, if (isHindi) "कृपया पहले छात्र चुनें!" else "Please select a student!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (head == null) {
                    Toast.makeText(context, if (isHindi) "कृपया फीस का प्रकार चुनें!" else "Please select a fee head!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (amount <= 0.0) {
                    Toast.makeText(context, if (isHindi) "कृपया सही फीस राशि दर्ज करें!" else "Please enter valid fee amount!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (existing != null && amount <= existing.amount) {
                    val prevInt = existing.amount.toInt()
                    Toast.makeText(
                        context,
                        if (isHindi)
                            "नई राशि पहले से असाइन की गई राशि (₹$prevInt) से अधिक होनी चाहिए!"
                        else
                            "New amount must be greater than previously assigned amount (₹$prevInt)!",
                        Toast.LENGTH_LONG
                    ).show()
                    return@Button
                }

                val activeSession = state.activeSessionId.ifEmpty { "11111111-1111-1111-1111-111111111111" }
                val orgId = state.activeWorkspace?.childOrgId ?: student.organizationId

                viewModel.onEvent(
                    InstitutionEvent.SubmitAdditionalFee(
                        organizationId = orgId,
                        activeSessionId = activeSession,
                        studentId = student.id,
                        globalFeeHeadId = head.id,
                        amount = amount,
                        globalFeeHeadName = head.name,
                        globalFeeHeadCode = head.code
                    )
                )

                Toast.makeText(
                    context,
                    if (existing != null) {
                        if (isHindi) "अतिरिक्त फीस सफलतापूर्वक अपडेट कर दी गई!" else "Additional fee updated successfully!"
                    } else {
                        if (isHindi) "अतिरिक्त फीस सफलतापूर्वक असाइन कर दी गई!" else "Additional fee assigned successfully!"
                    },
                    Toast.LENGTH_LONG
                ).show()
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen)
        ) {
            Icon(
                imageVector = Lucide.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (existingAssignedFee != null) {
                    if (isHindi) "अतिरिक्त फीस अपडेट करें" else "Update Additional Fee"
                } else {
                    if (isHindi) "अतिरिक्त फीस असाइन करें" else "Assign Additional Fee"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
