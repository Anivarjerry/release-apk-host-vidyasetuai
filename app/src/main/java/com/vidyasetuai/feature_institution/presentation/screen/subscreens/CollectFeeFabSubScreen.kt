package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.data.local.entity.LocalStudentAdditionalFeeEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalStudentFeePaymentEntity
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.text.SimpleDateFormat
import java.util.*

private data class ApplicableFeeHead(
    val id: String,
    val name: String,
    val code: String?,
    val isAdditional: Boolean,
    val totalAssignedAmount: Double
)

private data class FeeLedgerItem(
    val headName: String,
    val amount: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectFeeFabSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val todayDateStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    // --- State Variables ---
    var searchQuery by remember { mutableStateOf("") }
    var searchedStudents by remember { mutableStateOf<List<LocalStudentEntity>>(emptyList()) }
    var selectedStudent by remember { mutableStateOf<LocalStudentEntity?>(null) }

    // Fee Ledger Balance calculations
    var classFeeItems by remember { mutableStateOf<List<FeeLedgerItem>>(emptyList()) }
    var additionalFeeItems by remember { mutableStateOf<List<FeeLedgerItem>>(emptyList()) }
    var applicableFeeHeads by remember { mutableStateOf<List<ApplicableFeeHead>>(emptyList()) }
    var totalDuesSum by remember { mutableStateOf(0.0) }
    var totalPaidSum by remember { mutableStateOf(0.0) }
    var totalDiscountSum by remember { mutableStateOf(0.0) }
    var totalFineSum by remember { mutableStateOf(0.0) }
    var netOutstandingBalance by remember { mutableStateOf(0.0) }
    var isLedgerExpanded by remember { mutableStateOf(true) }

    // Fee Payment Form
    var selectedFeeHead by remember { mutableStateOf<ApplicableFeeHead?>(null) }
    var isFeeHeadDropdownExpanded by remember { mutableStateOf(false) }

    var amountPaidText by remember { mutableStateOf("") }
    var fineAmountText by remember { mutableStateOf("0") }
    var discountAmountText by remember { mutableStateOf("0") }
    var discountReasonText by remember { mutableStateOf("") }

    // Payment Mode
    var selectedPaymentMode by remember { mutableStateOf("Cash") } // Cash, Cheque, UPI, Online

    // Mode specific fields
    var chequeNumber by remember { mutableStateOf("") }
    var chequeDate by remember { mutableStateOf(todayDateStr) }
    var chequeBankName by remember { mutableStateOf("") }

    var onlineTransactionId by remember { mutableStateOf("") }
    var onlinePaymentApp by remember { mutableStateOf("PhonePe") }

    // Receipt details
    // Receipt details
    var receiptNumber by remember { mutableStateOf("REC-" + SimpleDateFormat("yyyy-MMdd", Locale.getDefault()).format(Date()) + "-" + (100000..999999).random()) }
    var paymentDate by remember { mutableStateOf(todayDateStr) }
    var remarksText by remember { mutableStateOf("") }
    var selectedHeadRemainingDue by remember { mutableStateOf(0.0) }

    // Success Receipt Dialog State
    var showSuccessReceiptDialog by remember { mutableStateOf(false) }
    var lastSubmittedPayment by remember { mutableStateOf<LocalStudentFeePaymentEntity?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Theme Colors
    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBgColor = if (isDark) Color(0xFF1E293B) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val primaryColor = AppColors.EmeraldGreen
    val borderClr = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)

    // Trigger Student Search
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

    // Recalculate Student Ledger & Load Student-Filtered Fee Heads
    LaunchedEffect(selectedStudent) {
        val student = selectedStudent
        if (student != null) {
            withContext(Dispatchers.IO) {
                val db = com.vidyasetuai.core.database.AppDatabase.getDatabase(context)
                val dao = db.institutionDao()

                // 1. Fetch Additional Fees assigned to this student
                val addFees = dao.getStudentAdditionalFees(student.id).filter { !it.isDeleted }
                val addBreakdown = addFees.map {
                    FeeLedgerItem(it.globalFeeHeadName ?: "Additional Fee", it.amount)
                }

                // 2. Fetch Existing Payments History
                val payments = dao.getStudentFeePayments(listOf(student.id)).filter { !it.isDeleted }
                val paidSum = payments.sumOf { it.amountPaid }
                val discountSum = payments.sumOf { it.discountAmount }
                val fineSum = payments.sumOf { it.fineAmount }

                // 3. Fetch Standard Class Fees Structure
                val classBreakdown = mutableListOf<FeeLedgerItem>()
                val feeHeadList = mutableListOf<ApplicableFeeHead>()
                var classBasicSum = 0.0

                var setup = dao.getChildOrgSetup(student.organizationId)
                if (setup == null) setup = dao.getActiveSession()

                if (setup != null && setup.feesStructureJson.isNotEmpty()) {
                    try {
                        val allStudents = dao.searchStudentsOffline("")
                        val classMap = allStudents.filter { it.classId != null && it.className != null }
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
                                val headId = obj["fee_head_id"]?.jsonPrimitive?.contentOrNull ?: "fa060000-0000-0000-0000-000000000002"

                                val feeHeadName = when (headId) {
                                    "fa060000-0000-0000-0000-000000000001" -> if (isHindi) "पंजीकरण शुल्क (Registration Fee)" else "Registration Fee"
                                    "fa060000-0000-0000-0000-000000000002" -> if (isHindi) "शिक्षण शुल्क (Tuition Fee)" else "Tuition Fee"
                                    "fa060000-0000-0000-0000-000000000003" -> if (isHindi) "परीक्षा शुल्क (Exam Fee)" else "Examination Fee"
                                    "fa060000-0000-0000-0000-00000000000d" -> if (isHindi) "विकास शुल्क (Development Fee)" else "School Development Fee"
                                    else -> if (isHindi) "कक्षा शुल्क" else "Class Fee"
                                }

                                classBasicSum += amount
                                classBreakdown.add(FeeLedgerItem(feeHeadName, amount))
                                feeHeadList.add(
                                    ApplicableFeeHead(
                                        id = headId,
                                        name = feeHeadName,
                                        code = "CLASS_FEE",
                                        isAdditional = false,
                                        totalAssignedAmount = amount
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Append Student-Specific Additional Fees to Applicable Fee Heads
                addFees.forEach { addFee ->
                    feeHeadList.add(
                        ApplicableFeeHead(
                            id = addFee.globalFeeHeadId,
                            name = addFee.globalFeeHeadName ?: (if (isHindi) "अतिरिक्त शुल्क" else "Additional Fee"),
                            code = addFee.globalFeeHeadCode,
                            isAdditional = true,
                            totalAssignedAmount = addFee.amount
                        )
                    )
                }

                val totalDues = classBasicSum + addFees.sumOf { it.amount }
                val netOutstanding = totalDues - paidSum - discountSum + fineSum

                withContext(Dispatchers.Main) {
                    classFeeItems = classBreakdown
                    additionalFeeItems = addBreakdown
                    applicableFeeHeads = feeHeadList.distinctBy { it.id }
                    totalDuesSum = totalDues
                    totalPaidSum = paidSum
                    totalDiscountSum = discountSum
                    totalFineSum = fineSum
                    netOutstandingBalance = if (netOutstanding < 0) 0.0 else netOutstanding

                    // Auto-select first fee head if available
                    if (feeHeadList.isNotEmpty()) {
                        val first = feeHeadList.first()
                        selectedFeeHead = first
                        val paidForHead = payments.filter { it.feeHeadTypeId == first.id }.sumOf { it.amountPaid }
                        val headDue = first.totalAssignedAmount - paidForHead
                        val remaining = if (headDue < 0) 0.0 else headDue
                        selectedHeadRemainingDue = remaining
                        amountPaidText = if (remaining > 0) remaining.toInt().toString() else "0"
                    }
                }
            }
        }
    }

    // Auto-update Amount Paid when Fee Head changes
    LaunchedEffect(selectedFeeHead) {
        val head = selectedFeeHead
        val student = selectedStudent
        if (head != null && student != null) {
            withContext(Dispatchers.IO) {
                val db = com.vidyasetuai.core.database.AppDatabase.getDatabase(context)
                val payments = db.institutionDao().getStudentFeePayments(listOf(student.id)).filter { !it.isDeleted && it.feeHeadTypeId == head.id }
                val headPaidSum = payments.sumOf { it.amountPaid }
                val remainingHeadDue = head.totalAssignedAmount - headPaidSum
                val remaining = if (remainingHeadDue < 0) 0.0 else remainingHeadDue
                withContext(Dispatchers.Main) {
                    selectedHeadRemainingDue = remaining
                    amountPaidText = if (remaining > 0) remaining.toInt().toString() else "0"
                }
            }
        }
    }

    // Calculate Net Payable Amount & Exceeding Validation
    val paidVal = amountPaidText.toDoubleOrNull() ?: 0.0
    val fineVal = fineAmountText.toDoubleOrNull() ?: 0.0
    val discountVal = discountAmountText.toDoubleOrNull() ?: 0.0
    val netReceivedAmount = (paidVal + fineVal - discountVal).let { if (it < 0) 0.0 else it }

    val isAmountExceedingDue = selectedHeadRemainingDue > 0 && paidVal > selectedHeadRemainingDue

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(bgColor)) {
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
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = textColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "फीस जमा करें (Collect Fee)" else "Collect Student Fee",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                Divider(color = borderClr, thickness = 0.5.dp)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .background(bgColor)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            // --- SECTION 1: STUDENT SELECTION ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isHindi) "1. छात्र का चयन करें" else "1. Select Student",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )

                        if (selectedStudent == null) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        text = if (isHindi) "छात्र का नाम, रोल नंबर या SR से खोजें..." else "Search student by name, roll no or SR...",
                                        fontSize = 13.sp,
                                        color = subtitleColor
                                    )
                                },
                                leadingIcon = {
                                    Icon(imageVector = Lucide.Search, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(18.dp))
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = borderClr
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (searchedStudents.isNotEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 220.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(bgColor)
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    searchedStudents.take(5).forEach { student ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { selectedStudent = student }
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(primaryColor.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = student.name.take(1).uppercase(),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = primaryColor
                                                )
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = student.name,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = textColor
                                                )
                                                Text(
                                                    text = "${if (isHindi) "कक्षा: " else "Class: "}${student.className ?: "N/A"} | ${if (isHindi) "रोल: " else "Roll: "}${student.rollNumber ?: "N/A"}",
                                                    fontSize = 11.sp,
                                                    color = subtitleColor
                                                )
                                            }
                                            Icon(imageVector = Lucide.ChevronRight, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        } else {
                            // Selected Student Profile Preview Card
                            val student = selectedStudent!!
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(primaryColor.copy(alpha = 0.08f))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(primaryColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = student.name.take(1).uppercase(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = student.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    Text(
                                        text = "${if (isHindi) "कक्षा: " else "Class: "}${student.className ?: "N/A"} | SR: ${student.srNumber ?: "N/A"}",
                                        fontSize = 12.sp,
                                        color = subtitleColor
                                    )
                                    if (!student.fatherName.isNullOrEmpty()) {
                                        Text(
                                            text = "${if (isHindi) "पिता: " else "Father: "}${student.fatherName}",
                                            fontSize = 11.sp,
                                            color = subtitleColor
                                        )
                                    }
                                }
                                TextButton(onClick = { selectedStudent = null }) {
                                    Text(
                                        text = if (isHindi) "बदलें" else "Change",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- SECTION 2: FEE LEDGER BALANCE CARD ---
            if (selectedStudent != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isLedgerExpanded = !isLedgerExpanded },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Lucide.FileText, contentDescription = null, tint = primaryColor, modifier = Modifier.size(18.dp))
                                    Column {
                                        Text(
                                            text = if (isHindi) "छात्र फीस बही-खाता (Fee Ledger Balance)" else "FEE LEDGER BALANCE",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = subtitleColor
                                        )
                                        Text(
                                            text = "${if (isHindi) "कुल देय: ₹" else "Dues: ₹"}${totalDuesSum.toInt()} • ${if (isHindi) "जमा: ₹" else "Paid: ₹"}${totalPaidSum.toInt()}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (netOutstandingBalance > 0) Color(0xFFEF4444) else primaryColor
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = if (isLedgerExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                                    contentDescription = null,
                                    tint = subtitleColor
                                )
                            }

                            AnimatedVisibility(
                                visible = isLedgerExpanded,
                                enter = expandVertically(),
                                exit = shrinkVertically()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Divider(color = borderClr, thickness = 0.5.dp)

                                    // Mandatory Standard Class Fees
                                    if (classFeeItems.isNotEmpty()) {
                                        Text(
                                            text = if (isHindi) "STANDARD CLASS FEES (कक्षा अनिवार्य शुल्क)" else "STANDARD CLASS FEES",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = subtitleColor
                                        )
                                        classFeeItems.forEach { item ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = item.headName, fontSize = 13.sp, color = textColor)
                                                Text(text = "₹${item.amount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                                            }
                                        }
                                    }

                                    // Supplemental / Additional Fees
                                    if (additionalFeeItems.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isHindi) "SUPPLEMENTAL FEES (अतिरिक्त शुल्क)" else "SUPPLEMENTAL FEES",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = subtitleColor
                                        )
                                        additionalFeeItems.forEach { item ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = item.headName, fontSize = 13.sp, color = textColor)
                                                Text(text = "₹${item.amount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Ledger Totals Grid
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = bgColor),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(text = if (isHindi) "कुल देय (DUES)" else "TOTAL DUES", fontSize = 10.sp, color = subtitleColor)
                                                Text(text = "₹${totalDuesSum.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
                                            }
                                            Column {
                                                Text(text = if (isHindi) "कुल जमा (PAID)" else "TOTAL PAID", fontSize = 10.sp, color = subtitleColor)
                                                Text(text = "₹${totalPaidSum.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                            }
                                            Column {
                                                Text(text = if (isHindi) "कुल छूट (DISCOUNT)" else "DISCOUNT", fontSize = 10.sp, color = subtitleColor)
                                                Text(text = "₹${totalDiscountSum.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                                            }
                                        }
                                    }

                                    // Net Outstanding Balance Bar
                                    Surface(
                                        color = if (netOutstandingBalance > 0) Color(0xFFFEF2F2) else Color(0xFFECFDF5),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isHindi) "नेट बकाया फीस (Net Outstanding Balance):" else "Net Outstanding Balance:",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (netOutstandingBalance > 0) Color(0xFFDC2626) else primaryColor
                                            )
                                            Text(
                                                text = "₹${netOutstandingBalance.toInt()}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (netOutstandingBalance > 0) Color(0xFFDC2626) else primaryColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- SECTION 3: FEE HEAD & PAYMENT AMOUNTS FORM ---
            if (selectedStudent != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = if (isHindi) "2. फीस मद व राशि जमा विवरण" else "2. Select Fee Head & Amount",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                            // Fee Head Dropdown (Strictly Filtered)
                            Column {
                                Text(
                                    text = if (isHindi) "फीस मद (Fee Head Type)" else "Fee Head Type",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = subtitleColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isFeeHeadDropdownExpanded = true }
                                ) {
                                    OutlinedTextField(
                                        value = selectedFeeHead?.name ?: (if (isHindi) "फीस मद चुनें..." else "Select Fee Head..."),
                                        onValueChange = {},
                                        readOnly = true,
                                        enabled = false,
                                        trailingIcon = {
                                            Icon(imageVector = Lucide.ChevronDown, contentDescription = null, tint = subtitleColor)
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledBorderColor = if (isAmountExceedingDue) Color(0xFFEF4444) else borderClr,
                                            disabledTextColor = textColor,
                                            disabledContainerColor = cardBgColor
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    DropdownMenu(
                                        expanded = isFeeHeadDropdownExpanded,
                                        onDismissRequest = { isFeeHeadDropdownExpanded = false },
                                        modifier = Modifier.fillMaxWidth(0.9f)
                                    ) {
                                        applicableFeeHeads.forEach { head ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(text = head.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                                        Text(text = "₹${head.totalAssignedAmount.toInt()}", fontSize = 12.sp, color = primaryColor, fontWeight = FontWeight.Bold)
                                                    }
                                                },
                                                onClick = {
                                                    selectedFeeHead = head
                                                    isFeeHeadDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Amount Paid & Fine Row
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = if (isHindi) "जमा राशि (₹)" else "Amount Paid (₹)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = subtitleColor)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = amountPaidText,
                                        onValueChange = { amountPaidText = it },
                                        isError = isAmountExceedingDue,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = if (isAmountExceedingDue) Color(0xFFEF4444) else primaryColor,
                                            unfocusedBorderColor = if (isAmountExceedingDue) Color(0xFFEF4444) else borderClr
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    if (isAmountExceedingDue) {
                                        Text(
                                            text = if (isHindi) "राशि बकाया (₹${selectedHeadRemainingDue.toInt()}) से अधिक नहीं हो सकती!" else "Amount cannot exceed remaining due ₹${selectedHeadRemainingDue.toInt()}!",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFEF4444),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = if (isHindi) "विलंब शुल्क / फाइन (₹)" else "Fine Amount (₹)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = subtitleColor)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = fineAmountText,
                                        onValueChange = { input ->
                                            if (input.isEmpty() || (input.length <= 5 && input.all { char -> char.isDigit() })) {
                                                fineAmountText = input
                                                if ((input.toDoubleOrNull() ?: 0.0) > 0) {
                                                    discountAmountText = "0"
                                                    discountReasonText = ""
                                                }
                                            }
                                        },
                                        enabled = discountVal == 0.0,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = primaryColor,
                                            unfocusedBorderColor = borderClr,
                                            disabledBorderColor = borderClr,
                                            disabledTextColor = subtitleColor
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            // Discount Amount & Reason Row
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = if (isHindi) "छूट राशि (₹)" else "Discount (₹)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = subtitleColor)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = discountAmountText,
                                        onValueChange = { input ->
                                            if (input.isEmpty() || (input.length <= 5 && input.all { char -> char.isDigit() })) {
                                                discountAmountText = input
                                                if ((input.toDoubleOrNull() ?: 0.0) > 0) {
                                                    fineAmountText = "0"
                                                }
                                            }
                                        },
                                        enabled = fineVal == 0.0,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = primaryColor,
                                            unfocusedBorderColor = borderClr,
                                            disabledBorderColor = borderClr,
                                            disabledTextColor = subtitleColor
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = if (isHindi) "छूट का कारण" else "Discount Reason", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = subtitleColor)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = discountReasonText,
                                        onValueChange = { if (it.length <= 100) discountReasonText = it },
                                        enabled = discountVal > 0,
                                        placeholder = { Text(text = if (isHindi) "उदा. स्कॉलरशिप" else "e.g. Scholarship", fontSize = 11.sp, color = subtitleColor) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, unfocusedBorderColor = borderClr),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            // Live Net Total Payable Highlight Card
                            Surface(
                                color = primaryColor.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isHindi) "कुल प्राप्त राशि (Net Received Total):" else "Net Received Total:",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    Text(
                                        text = "₹${netReceivedAmount.toInt()}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- SECTION 4: PAYMENT MODE SELECTOR ---
            if (selectedStudent != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = if (isHindi) "3. भुगतान प्रकार (Payment Mode)" else "3. Payment Mode",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                            // Mode Selector Buttons Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val modes = listOf("Cash", "Cheque", "UPI", "Online")
                                modes.forEach { mode ->
                                    val isSelected = selectedPaymentMode == mode
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) primaryColor else bgColor)
                                            .clickable { selectedPaymentMode = mode }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = mode,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else textColor
                                        )
                                    }
                                }
                            }

                            // Dynamic Inputs based on Payment Mode
                            when (selectedPaymentMode) {
                                "Cheque" -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = chequeNumber,
                                            onValueChange = { input ->
                                                if (input.isEmpty() || (input.length <= 6 && input.all { char -> char.isDigit() })) {
                                                    chequeNumber = input
                                                }
                                            },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            label = { Text(text = if (isHindi) "चेक नंबर (6 अंक)" else "Cheque Number (6 digits)", fontSize = 12.sp) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = chequeBankName,
                                            onValueChange = { if (it.length <= 50) chequeBankName = it },
                                            label = { Text(text = if (isHindi) "बैंक का नाम" else "Bank Name", fontSize = 12.sp) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                                "UPI", "Online" -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = onlineTransactionId,
                                            onValueChange = { if (it.length <= 22) onlineTransactionId = it },
                                            label = { Text(text = if (isHindi) "ट्रांजैक्शन ID / UTR (अधिक्स 22 अक्षर)" else "Transaction ID / UTR (Max 22)", fontSize = 12.sp) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = onlinePaymentApp,
                                            onValueChange = { if (it.length <= 30) onlinePaymentApp = it },
                                            label = { Text(text = if (isHindi) "पेमेंट ऐप (PhonePe, Paytm, GPay)" else "Payment App", fontSize = 12.sp) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- SECTION 5: RECEIPT NO, DATE & REMARKS ---
            if (selectedStudent != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isHindi) "4. रसीद एवं विवरण" else "4. Receipt & Remarks",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = if (isHindi) "रसीद नंबर" else "Receipt Number", fontSize = 12.sp, color = subtitleColor)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = receiptNumber,
                                        onValueChange = {},
                                        readOnly = true,
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = borderClr,
                                            unfocusedBorderColor = borderClr
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = if (isHindi) "भुगतान तिथि" else "Payment Date", fontSize = 12.sp, color = subtitleColor)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = paymentDate,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Lucide.Calendar,
                                                contentDescription = null,
                                                tint = subtitleColor,
                                                modifier = Modifier.clickable {
                                                    val cal = Calendar.getInstance()
                                                    DatePickerDialog(
                                                        context,
                                                        { _, year, month, day ->
                                                            paymentDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)
                                                        },
                                                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                                                    ).show()
                                                }
                                            )
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            Column {
                                OutlinedTextField(
                                    value = remarksText,
                                    onValueChange = { if (it.length <= 100) remarksText = it },
                                    placeholder = { Text(text = if (isHindi) "कोई टिप्पणी या नोट लिखें (अधिकतम 100 अक्षर)..." else "Write any remarks or notes (max 100 chars)...", fontSize = 12.sp, color = subtitleColor) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 2.dp, end = 4.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        text = "${remarksText.length}/100",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (remarksText.length >= 100) Color(0xFFEF4444) else subtitleColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- SECTION 6: SUBMIT ACTION BUTTON ---
            if (selectedStudent != null) {
                item {
                    Button(
                        onClick = {
                            val student = selectedStudent ?: return@Button
                            val feeHead = selectedFeeHead
                            if (feeHead == null) {
                                Toast.makeText(context, if (isHindi) "कृपया फीस मद चुनें!" else "Please select a fee head!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (paidVal <= 0) {
                                Toast.makeText(context, if (isHindi) "कृपया जमा राशि दर्ज करें!" else "Please enter amount paid!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isSubmitting = true
                            val activeWorkspace = state.activeWorkspace
                            val activeSessionId = state.activeSessionId.ifEmpty { student.activeSessionId }
                            val sessionManager = com.vidyasetuai.core.auth.SessionManager(context)
                            val realUserId = sessionManager.getUserId()

                            val newPayment = LocalStudentFeePaymentEntity(
                                id = UUID.randomUUID().toString(),
                                organizationId = student.organizationId,
                                activeSessionId = activeSessionId,
                                studentId = student.id,
                                feeHeadTypeId = feeHead.id,
                                receiptNumber = receiptNumber,
                                paymentMode = selectedPaymentMode,
                                paymentDate = paymentDate,
                                amountPaid = paidVal,
                                discountAmount = discountVal,
                                fineAmount = fineVal,
                                discountReason = discountReasonText.ifEmpty { null },
                                cashReceivedByUserId = if (selectedPaymentMode == "Cash") realUserId else null,
                                cashReceivedByUserName = if (selectedPaymentMode == "Cash") activeWorkspace?.role else null,
                                chequeNumber = chequeNumber.ifEmpty { null },
                                chequeDate = chequeDate.ifEmpty { null },
                                chequeBankName = chequeBankName.ifEmpty { null },
                                onlineTransactionId = onlineTransactionId.ifEmpty { null },
                                onlinePaymentApp = onlinePaymentApp.ifEmpty { null },
                                remarks = remarksText.ifEmpty { null },
                                status = "Completed",
                                isActive = true,
                                isDeleted = false,
                                lastSyncedAt = System.currentTimeMillis(),
                                syncState = "PENDING_CREATE"
                            )

                            lastSubmittedPayment = newPayment
                            viewModel.onEvent(InstitutionEvent.CollectStudentFee(newPayment))

                            isSubmitting = false
                            showSuccessReceiptDialog = true
                        },
                        enabled = !isAmountExceedingDue && !isSubmitting && paidVal > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            disabledContainerColor = borderClr
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Lucide.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Text(
                                    text = if (isHindi) "फीस जमा करें व रसीद काटें" else "Submit & Generate Receipt",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- SUCCESS PAYMENT RECEIPT DIALOG ---
    if (showSuccessReceiptDialog && lastSubmittedPayment != null) {
        val payment = lastSubmittedPayment!!
        val student = selectedStudent

        AlertDialog(
            onDismissRequest = {
                showSuccessReceiptDialog = false
                onBack()
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = cardBgColor,
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Lucide.Check, contentDescription = null, tint = primaryColor, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isHindi) "फीस सफलतापूर्वक जमा हुई!" else "Payment Successful!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = "${if (isHindi) "रसीद सं: " else "Receipt No: "}${payment.receiptNumber}",
                        fontSize = 12.sp,
                        color = subtitleColor
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = if (isHindi) "छात्र का नाम:" else "Student Name:", fontSize = 12.sp, color = subtitleColor)
                        Text(text = student?.name ?: "N/A", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = if (isHindi) "कक्षा:" else "Class:", fontSize = 12.sp, color = subtitleColor)
                        Text(text = student?.className ?: "N/A", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = if (isHindi) "भुगतान प्रकार:" else "Payment Mode:", fontSize = 12.sp, color = subtitleColor)
                        Text(text = payment.paymentMode, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                    Divider(color = borderClr, thickness = 0.5.dp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = if (isHindi) "प्राप्त राशि:" else "Amount Received:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Text(text = "₹${payment.amountPaid.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessReceiptDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isHindi) "संपन्न (Done)" else "Done", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        )
    }
}
