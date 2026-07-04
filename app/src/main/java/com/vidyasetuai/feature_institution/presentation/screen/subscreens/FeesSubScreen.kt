package com.vidyasetuai.feature_feed.presentation.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.data.local.dao.InstitutionDao
import com.vidyasetuai.feature_institution.data.local.entity.LocalStudentAdditionalFeeEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalStudentFeePaymentEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.contentOrNull

@Composable
fun CombinedFeesDetailSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val db = remember { com.vidyasetuai.core.database.AppDatabase.getDatabase(context) }
    val activeWorkspace = state.activeWorkspace
    val role = activeWorkspace?.role ?: ""

    // ── States ──
    var searchQuery by remember { mutableStateOf("") }
    var allResolvedStudents by remember { mutableStateOf<List<ResolvedStudentFee>>(emptyList()) }
    var expandedStudentIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isLoading by remember { mutableStateOf(true) }

    // Build head name cache in memory to speed up name lookups
    val feeHeadNameCache = remember { mutableMapOf<String, String>() }

    // Load data from Room Database
    LaunchedEffect(state.guardianStudents, activeWorkspace, role) {
        isLoading = true
        val dao = db.institutionDao()
        val students = when (role) {
            "Guardian" -> {
                val guardianId = activeWorkspace?.guardianId ?: ""
                if (guardianId.isNotEmpty()) {
                    dao.getStudentsByGuardianId(guardianId)
                } else emptyList()
            }
            "Student" -> {
                val studentId = activeWorkspace?.studentId ?: ""
                if (studentId.isNotEmpty()) {
                    dao.getStudentById(studentId)?.let { listOf(it) } ?: emptyList()
                } else emptyList()
            }
            else -> {
                // Staff / Admin: load all offline students
                dao.searchStudentsOffline("")
            }
        }

        // Build a global class name mapping using all database students
        val allDbStudents = dao.searchStudentsOffline("")
        val classMap = allDbStudents.filter { it.classId != null && it.className != null }
            .associate { it.classId!! to it.className!! }

        val resolvedList = mutableListOf<ResolvedStudentFee>()
        for (student in students) {
            val additionalFees = dao.getStudentAdditionalFees(student.id)
            val payments = dao.getStudentFeePayments(listOf(student.id))

            // Put student's additional fee names in the cache map
            additionalFees.forEach { fee ->
                if (fee.globalFeeHeadName != null) {
                    feeHeadNameCache[fee.globalFeeHeadId] = fee.globalFeeHeadName
                }
            }

            // Resolve Class Basic Fee from setup JSON (with fallback)
            var classBasicFeeSum = 0.0
            val classFeeHeads = mutableListOf<ResolvedFeeHead>()
            
            var setup = dao.getChildOrgSetup(student.organizationId)
            if (setup == null) {
                setup = dao.getActiveSession() // Fallback to active setup
            }

            if (setup != null && setup.feesStructureJson.isNotEmpty()) {
                try {
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
                            classBasicFeeSum += amount
                            classFeeHeads.add(ResolvedFeeHead(id = headId, amount = amount, isAdditional = false))
                        }
                    }
                } catch (e: Exception) {
                    Log.e("FeesSubScreen", "Error resolving class basic fee for student: ${student.id}", e)
                }
            }

            val additionalFeeHeads = additionalFees.map { 
                ResolvedFeeHead(
                    id = it.globalFeeHeadId,
                    amount = it.amount,
                    name = it.globalFeeHeadName,
                    isAdditional = true
                )
            }

            val totalAssigned = classBasicFeeSum + additionalFees.sumOf { it.amount }
            val totalPaid = payments.sumOf { it.amountPaid }
            val totalPending = (totalAssigned - totalPaid).coerceAtLeast(0.0)

            resolvedList.add(
                ResolvedStudentFee(
                    id = student.id,
                    name = student.name,
                    className = student.className,
                    sectionName = student.sectionName,
                    fatherName = student.fatherName,
                    student = student,
                    classBasicFee = classBasicFeeSum,
                    additionalFees = additionalFees,
                    payments = payments,
                    totalAssigned = totalAssigned,
                    totalPaid = totalPaid,
                    totalPending = totalPending,
                    allFeeHeads = classFeeHeads + additionalFeeHeads
                )
            )
        }

        allResolvedStudents = resolvedList
        isLoading = false
    }

    // Filter students by query
    val filteredStudents = remember(searchQuery, allResolvedStudents) {
        if (searchQuery.isBlank()) {
            allResolvedStudents
        } else {
            val q = searchQuery.trim()
            allResolvedStudents.filter { r ->
                r.name.contains(q, ignoreCase = true) ||
                r.fatherName?.contains(q, ignoreCase = true) == true ||
                r.student.guardianName?.contains(q, ignoreCase = true) == true ||
                r.student.guardianMobile?.contains(q, ignoreCase = true) == true ||
                r.student.fatherMobile?.contains(q, ignoreCase = true) == true ||
                r.student.motherMobile?.contains(q, ignoreCase = true) == true ||
                (r.className != null && r.className.contains(q, ignoreCase = true))
            }
        }
    }

    // Compute totals dynamically for display
    val displayExpectedSum = remember(filteredStudents) { filteredStudents.sumOf { it.totalAssigned } }
    val displayPendingSum = remember(filteredStudents) { filteredStudents.sumOf { it.totalPending } }

    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBg = if (isDark) Color(0xFF1E1E20) else Color.White
    val cardBorderColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val subtitleColor = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(backgroundColor)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = textColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isHindi) "फीस विवरण" else "Fees Details",
                        fontSize = 20.sp,
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
        containerColor = backgroundColor
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppColors.EmeraldGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── 1. Top Summary Card (Total Fee and Pending Fee) ──
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, cardBorderColor, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        color = cardBg
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isHindi) "कुल निर्धारित शुल्क" else "Total Assigned Fee",
                                    fontSize = 12.sp,
                                    color = subtitleColor,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹$displayExpectedSum",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(cardBorderColor)
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isHindi) "कुल लंबित फीस" else "Total Pending Fee",
                                    fontSize = 12.sp,
                                    color = subtitleColor,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹$displayPendingSum",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (displayPendingSum > 0.0) {
                                        if (isDark) Color(0xFFE57373) else Color(0xFFC62828)
                                    } else {
                                        if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
                                    }
                                )
                            }
                        }
                    }
                }

                // ── 2. Search Box Filter ──
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { 
                            Text(
                                text = if (isHindi) "छात्र, पिता का नाम या मोबाइल नंबर खोजें..." else "Search student, father name or mobile...",
                                fontSize = 13.sp,
                                color = subtitleColor
                            )
                        },
                        leadingIcon = { 
                            Icon(
                                imageVector = Lucide.Search, 
                                contentDescription = null, 
                                modifier = Modifier.size(20.dp),
                                tint = subtitleColor
                            ) 
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Lucide.X, 
                                        contentDescription = "Clear",
                                        modifier = Modifier.size(18.dp),
                                        tint = subtitleColor
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.EmeraldGreen,
                            unfocusedBorderColor = cardBorderColor,
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg
                        ),
                        singleLine = true
                    )
                }

                // ── 3. Students List (Individual expand cards) ──
                if (filteredStudents.isEmpty()) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.User,
                                contentDescription = null,
                                tint = subtitleColor,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isHindi) "कोई परिणाम नहीं मिला" else "No students found matching filter",
                                fontSize = 14.sp,
                                color = subtitleColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(filteredStudents) { resolved ->
                        val isExpanded = expandedStudentIds.contains(resolved.id)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, cardBorderColor, RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            color = cardBg
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedStudentIds = if (isExpanded) {
                                            expandedStudentIds - resolved.id
                                        } else {
                                            expandedStudentIds + resolved.id
                                        }
                                    }
                                    .padding(16.dp)
                            ) {
                                // Short info Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = resolved.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                        Text(
                                            text = "${resolved.className ?: "—"} - ${resolved.sectionName ?: "—"}",
                                            fontSize = 12.sp,
                                            color = subtitleColor
                                        )
                                        if (!resolved.fatherName.isNullOrBlank()) {
                                            Text(
                                                text = "${if (isHindi) "पिता: " else "Father: "} ${resolved.fatherName}",
                                                fontSize = 12.sp,
                                                color = subtitleColor,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = if (isExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                                        contentDescription = "Expand/Collapse details",
                                        tint = subtitleColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // Fee summary line in short view
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${if (isHindi) "कुल निर्धारित: " else "Total Fee: "} ₹${resolved.totalAssigned.toInt()}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textColor
                                    )
                                    Text(
                                        text = if (resolved.totalPending > 0.0) {
                                            "${if (isHindi) "बकाया: " else "Pending: "} ₹${resolved.totalPending.toInt()}"
                                        } else {
                                            if (isHindi) "पूर्ण जमा" else "Paid"
                                        },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (resolved.totalPending > 0.0) {
                                            if (isDark) Color(0xFFE57373) else Color(0xFFC62828)
                                        } else {
                                            if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
                                        }
                                    )
                                }

                                // Expanded details (fee structure and payments)
                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Divider(color = cardBorderColor.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Fee Structure Header
                                    Text(
                                        text = if (isHindi) "शुल्क विवरण (Fee Heads)" else "Fee Structure",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (resolved.allFeeHeads.isEmpty()) {
                                        Text(
                                            text = if (isHindi) "कोई शुल्क असाइन नहीं है" else "No fees assigned",
                                            fontSize = 13.sp,
                                            color = subtitleColor
                                        )
                                    } else {
                                        resolved.allFeeHeads.forEach { head ->
                                            val name = getFeeHeadName(head.id, isHindi, feeHeadNameCache)
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = name,
                                                    fontSize = 13.sp,
                                                    color = textColor
                                                )
                                                Text(
                                                    text = "₹${head.amount.toInt()}",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = textColor
                                                )
                                            }
                                        }
                                    }

                                    // Payment Transactions list
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider(color = cardBorderColor.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = if (isHindi) "भुगतान इतिहास (Receipts)" else "Payment History",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (resolved.payments.isEmpty()) {
                                        Text(
                                            text = if (isHindi) "कोई भुगतान प्राप्त नहीं हुआ है" else "No payments recorded yet",
                                            fontSize = 13.sp,
                                            color = subtitleColor
                                        )
                                    } else {
                                        resolved.payments.forEach { payment ->
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                color = backgroundColor,
                                                border = androidx.compose.foundation.BorderStroke(0.5.dp, cardBorderColor)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Text(
                                                            text = "${if (isHindi) "रसीद: " else "Receipt: "} ${payment.receiptNumber}",
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = textColor
                                                        )
                                                        Text(
                                                            text = "${payment.paymentDate} • ${payment.paymentMode}",
                                                            fontSize = 11.sp,
                                                            color = subtitleColor
                                                        )
                                                    }
                                                    Text(
                                                        text = "₹${payment.amountPaid.toInt()}",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
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
}

// ── Fee Head Name Resolver Helper ──
private fun getFeeHeadName(id: String, isHindi: Boolean, cache: Map<String, String>): String {
    cache[id]?.let { return it }
    return when (id) {
        "fa060000-0000-0000-0000-000000000001" -> if (isHindi) "शिक्षण शुल्क (Tuition)" else "Tuition Fee"
        "fa060000-0000-0000-0000-000000000002" -> if (isHindi) "प्रवेश शुल्क (Admission)" else "Admission Fee"
        "fa060000-0000-0000-0000-000000000003" -> if (isHindi) "गतिविधि शुल्क (Activity)" else "Activity Fee"
        "fa060000-0000-0000-0000-000000000004" -> if (isHindi) "परीक्षा शुल्क (Exam)" else "Exam Fee"
        "fa060000-0000-0000-0000-000000000005" -> if (isHindi) "परिवहन शुल्क (Transport)" else "Transport Fee"
        else -> if (isHindi) "सामान्य वार्षिक शुल्क" else "Annual School Fee"
    }
}

// ── UI Data Structures ──
data class ResolvedStudentFee(
    val id: String,
    val name: String,
    val className: String?,
    val sectionName: String?,
    val fatherName: String?,
    val student: LocalStudentEntity,
    val classBasicFee: Double,
    val additionalFees: List<LocalStudentAdditionalFeeEntity>,
    val payments: List<LocalStudentFeePaymentEntity>,
    val totalAssigned: Double,
    val totalPaid: Double,
    val totalPending: Double,
    val allFeeHeads: List<ResolvedFeeHead>
)

data class ResolvedFeeHead(
    val id: String,
    val amount: Double,
    val name: String? = null,
    val isAdditional: Boolean
)
