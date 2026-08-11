package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_institution.domain.model.StaffSalaryOverview
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalaryPayoutSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel? = null,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val backgroundColor = if (isDark) Color(0xFF121212) else Color(0xFFF8FAF9)
    val cardBgColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDark) Color(0xFFF3F4F6) else Color(0xFF1F2937)
    val subtitleColor = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val primaryColor = MaterialTheme.colorScheme.primary
    val borderClr = if (isDark) Color(0xFF2E2E2E) else Color(0xFFE5E7EB)

    val userRole = state.activeWorkspace?.role ?: ""
    val isAdmin = remember(userRole) {
        val r = userRole.uppercase().trim()
        r in listOf(
            "ADMIN", "SYSTEM ADMINISTRATOR", "SCHOOL ADMINISTRATOR", 
            "ORG ADMIN", "PRINCIPAL", "DIRECTOR", "OWNER", "SYSTEM_ADMINISTRATOR"
        )
    }
    val currentStaffId = state.activeWorkspace?.staffId ?: ""

    var searchQuery by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf(state.selectedSalaryMonth) }
    var selectedYear by remember { mutableStateOf(state.selectedSalaryYear) }

    var expandedStaffId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel?.loadStaffSalaryOverviews(selectedMonth, selectedYear)
    }

    val displayedOverviews = remember(state.salaryOverviews, searchQuery, isAdmin, currentStaffId) {
        val listForRole = if (isAdmin) {
            state.salaryOverviews
        } else {
            val myStaff = state.salaryOverviews.filter { 
                (currentStaffId.isNotEmpty() && it.staff.id == currentStaffId) ||
                (state.userId.isNotEmpty() && it.staff.id == state.userId)
            }
            if (myStaff.isNotEmpty()) myStaff else state.salaryOverviews
        }
        if (searchQuery.isBlank() || !isAdmin) {
            listForRole
        } else {
            listForRole.filter {
                it.staff.name.contains(searchQuery, ignoreCase = true) ||
                it.staff.mobileNumber.contains(searchQuery, ignoreCase = true) ||
                (it.staff.roleName ?: "").contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val myOverview = remember(displayedOverviews) { displayedOverviews.firstOrNull() }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(cardBgColor)) {
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
                        text = if (isAdmin) {
                            if (isHindi) "स्टाफ वेतन भुगतान" else "Staff Salary Payouts"
                        } else {
                            if (isHindi) "मेरी सैलरी विवरण" else "My Salary Details"
                        },
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(borderClr)
                )
            }
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SECTION 1: MONTH SELECTOR & METRIC OVERVIEW ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Month Selector Chips Row with Horizontal Scroll
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (isHindi) "पेरोल महीना / सत्र" else "Payroll Month / Session",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = subtitleColor
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(
                                0 to (if (isHindi) "पूरा सत्र" else "All Session"),
                                4 to "Apr", 5 to "May", 6 to "Jun", 7 to "Jul", 8 to "Aug", 9 to "Sep",
                                10 to "Oct", 11 to "Nov", 12 to "Dec", 1 to "Jan", 2 to "Feb", 3 to "Mar"
                            ).forEach { (m, name) ->
                                val isSelected = m == selectedMonth
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) primaryColor else cardBgColor)
                                        .border(1.dp, if (isSelected) primaryColor else borderClr, RoundedCornerShape(20.dp))
                                        .clickable {
                                            selectedMonth = m
                                            viewModel?.loadStaffSalaryOverviews(m, selectedYear)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = name,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else textColor
                                    )
                                }
                            }
                        }
                    }

                    // Dashboard Summary Cards (Option B: 4 Cards 2x2 Grid for Admin)
                    if (isAdmin) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MetricSummaryCard(
                                    modifier = Modifier.weight(1f),
                                    title = if (isHindi) "कुल निर्मित पेरोल" else "Total Net Payroll",
                                    value = "₹${state.totalPayrollAmount.toInt()}",
                                    icon = Lucide.Coins,
                                    iconTint = primaryColor,
                                    bgColor = cardBgColor,
                                    textColor = textColor,
                                    subtitleColor = subtitleColor,
                                    borderColor = borderClr
                                )
                                MetricSummaryCard(
                                    modifier = Modifier.weight(1f),
                                    title = if (isHindi) "वास्तविक कुल बकाया" else "Actual Staff Due",
                                    value = "₹${state.totalSalaryPendingAmount.toInt()}",
                                    icon = Lucide.Clock,
                                    iconTint = Color(0xFFEF4444),
                                    bgColor = cardBgColor,
                                    textColor = textColor,
                                    subtitleColor = subtitleColor,
                                    borderColor = borderClr
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MetricSummaryCard(
                                    modifier = Modifier.weight(1f),
                                    title = if (isHindi) "पेरोल भुगतान" else "Payroll Paid",
                                    value = "₹${state.totalPayrollPaidAmount.toInt()}",
                                    icon = Lucide.Check,
                                    iconTint = Color(0xFF10B981),
                                    bgColor = cardBgColor,
                                    textColor = textColor,
                                    subtitleColor = subtitleColor,
                                    borderColor = borderClr
                                )
                                MetricSummaryCard(
                                    modifier = Modifier.weight(1f),
                                    title = if (isHindi) "अग्रिम/डायरेक्ट भुगतान" else "Advance Paid",
                                    value = "₹${state.totalAdvancePaidAmount.toInt()}",
                                    icon = Lucide.Wallet,
                                    iconTint = Color(0xFF3B82F6),
                                    bgColor = cardBgColor,
                                    textColor = textColor,
                                    subtitleColor = subtitleColor,
                                    borderColor = borderClr
                                )
                            }
                        }
                    } else {
                        // Normal Staff View: 3 Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricSummaryCard(
                                modifier = Modifier.weight(1f),
                                title = if (isHindi) "मासिक तय वेतन" else "Base Salary",
                                value = "₹${myOverview?.baseMonthlySalary?.toInt() ?: 0}",
                                icon = Lucide.Coins,
                                iconTint = primaryColor,
                                bgColor = cardBgColor,
                                textColor = textColor,
                                subtitleColor = subtitleColor,
                                borderColor = borderClr
                            )
                            MetricSummaryCard(
                                modifier = Modifier.weight(1f),
                                title = if (isHindi) "प्राप्त भुगतान" else "Total Paid",
                                value = "₹${myOverview?.totalAmountPaid?.toInt() ?: 0}",
                                icon = Lucide.Check,
                                iconTint = Color(0xFF10B981),
                                bgColor = cardBgColor,
                                textColor = textColor,
                                subtitleColor = subtitleColor,
                                borderColor = borderClr
                            )
                            MetricSummaryCard(
                                modifier = Modifier.weight(1f),
                                title = if (isHindi) "शेष बकाया" else "Pending Due",
                                value = "₹${myOverview?.dueBalance?.toInt() ?: 0}",
                                icon = Lucide.Clock,
                                iconTint = Color(0xFFEF4444),
                                bgColor = cardBgColor,
                                textColor = textColor,
                                subtitleColor = subtitleColor,
                                borderColor = borderClr
                            )
                        }
                    }
                }
            }

            // --- SECTION 2: SEARCH BAR (ADMIN ONLY) ---
            if (isAdmin) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = if (isHindi) "स्टाफ का नाम, रोल या फोन से खोजें..." else "Search staff by name, role or mobile...",
                                fontSize = 13.sp,
                                color = subtitleColor
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Lucide.Search, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Lucide.X, contentDescription = "Clear", tint = subtitleColor, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBgColor,
                            unfocusedContainerColor = cardBgColor,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = borderClr
                        ),
                        singleLine = true
                    )
                }
            }

            // --- SECTION 3: STAFF SALARY LIST / SINGLE STAFF VIEW ---
            if (displayedOverviews.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Lucide.Users, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isHindi) "कोई सैलरी रिकॉर्ड नहीं मिला" else "No salary records found",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor
                            )
                            Text(
                                text = if (isHindi) "सैलरी का विवरण लोड करने के लिए नेटवर्क सिंक करें" else "Sync workspace to load salary details",
                                fontSize = 13.sp,
                                color = subtitleColor
                            )
                        }
                    }
                }
            } else {
                items(displayedOverviews, key = { it.staff.id }) { overview ->
                    StaffSalaryCard(
                        overview = overview,
                        isAdmin = isAdmin,
                        isHindi = isHindi,
                        isDark = isDark,
                        isExpanded = if (isAdmin) expandedStaffId == overview.staff.id else true,
                        onToggleExpand = {
                            expandedStaffId = if (expandedStaffId == overview.staff.id) null else overview.staff.id
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricSummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    bgColor: Color,
    textColor: Color,
    subtitleColor: Color,
    borderColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = subtitleColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StaffSalaryCard(
    overview: StaffSalaryOverview,
    isAdmin: Boolean,
    isHindi: Boolean,
    isDark: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val cardBgColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDark) Color(0xFFF3F4F6) else Color(0xFF1F2937)
    val subtitleColor = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val borderClr = if (isDark) Color(0xFF2E2E2E) else Color(0xFFE5E7EB)
    val primaryColor = MaterialTheme.colorScheme.primary

    val (statusBg, statusFg, statusText) = when (overview.paymentStatus) {
        "PAID" -> Triple(Color(0xFFD1FAE5), Color(0xFF065F46), if (isHindi) "भुगतान सफल" else "PAID")
        "PARTIAL" -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), if (isHindi) "आंशिक भुगतान" else "PARTIAL")
        "PENDING" -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), if (isHindi) "बकाया" else "PENDING")
        else -> Triple(Color(0xFFF3F4F6), Color(0xFF6B7280), if (isHindi) "पेआउट जनरेट नहीं" else "NOT GENERATED")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderClr)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Header Row: Name, Role & Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.User,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = overview.staff.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = overview.staff.roleName ?: overview.staff.mobileNumber,
                        fontSize = 12.sp,
                        color = subtitleColor
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusFg
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Middle Stats Grid: Base Salary, Net Payroll, Paid, Due
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF262626) else Color(0xFFF9FAFB))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = if (isHindi) "तय सैलरी" else "Base Salary", fontSize = 11.sp, color = subtitleColor)
                    Text(text = "₹${overview.baseMonthlySalary.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
                }
                Column {
                    Text(text = if (isHindi) "मासिक पेआउट" else "Net Payroll", fontSize = 11.sp, color = subtitleColor)
                    Text(
                        text = if (overview.hasPayoutGenerated) "₹${overview.netSalaryPayable.toInt()}" else "-",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
                Column {
                    Text(text = if (isHindi) "दिया गया" else "Paid", fontSize = 11.sp, color = subtitleColor)
                    Text(text = "₹${overview.totalAmountPaid.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                }
                Column {
                    Text(text = if (isHindi) "शेष बकाया" else "Due", fontSize = 11.sp, color = subtitleColor)
                    Text(
                        text = if (overview.hasPayoutGenerated) "₹${overview.dueBalance.toInt()}" else "₹0",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (overview.dueBalance > 0 && overview.hasPayoutGenerated) Color(0xFFEF4444) else textColor
                    )
                }
            }

            // Payout Not Generated Alert Banner
            if (!overview.hasPayoutGenerated) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color(0xFF2A2A2A) else Color(0xFFFFFBEB))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Lucide.CircleAlert, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "इस महीने का पेआउट सॉफ़्टवेयर द्वारा अभी जनरेट नहीं हुआ है।" else "Payroll payout for this month has not been generated yet.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFB45309)
                    )
                }
            }

            // Bus Auto Deduct Info Banner if enrolled
            if (overview.busEnrollment != null && overview.busEnrollment.autoDeductFromSalary) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Lucide.Bus, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "बस पास ऑटो-कटौती: ₹${overview.busDeductionAmount.toInt()}/महीना लागू है" else "Bus fare auto-deduct: ₹${overview.busDeductionAmount.toInt()}/mo active",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E40AF)
                    )
                }
            }

            // Bank Account & UPI Information Banner (Visible to all)
            if (overview.salaryConfig != null && (!overview.salaryConfig.bankName.isNullOrBlank() || !overview.salaryConfig.upiId.isNullOrBlank())) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color(0xFF242424) else Color(0xFFF3F4F6))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    if (!overview.salaryConfig.bankName.isNullOrBlank()) {
                        Text(
                            text = "Bank: ${overview.salaryConfig.bankName} | A/C: ${overview.salaryConfig.bankAccountNumber ?: "N/A"} | IFSC: ${overview.salaryConfig.ifscCode ?: "N/A"}",
                            fontSize = 11.sp,
                            color = subtitleColor
                        )
                    }
                    if (!overview.salaryConfig.upiId.isNullOrBlank()) {
                        Text(
                            text = "UPI ID: ${overview.salaryConfig.upiId}",
                            fontSize = 11.sp,
                            color = subtitleColor
                        )
                    }
                }
            }

            // Toggle Receipts Header Bar
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onToggleExpand() }
                    .background(if (isDark) Color(0xFF242424) else Color(0xFFF3F4F6))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Lucide.Receipt, contentDescription = null, tint = primaryColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "भुगतान इतिहास व रसीदें (${overview.payments.size})" else "Payment Receipts (${overview.payments.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                    contentDescription = "Toggle Receipts",
                    tint = subtitleColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Expanded Payment History List
            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                if (overview.payments.isEmpty()) {
                    Text(
                        text = if (isHindi) "इस महीने कोई भुगतान रसीद नहीं है।" else "No payment transactions recorded yet.",
                        fontSize = 12.sp,
                        color = subtitleColor,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                } else {
                    overview.payments.forEach { pay ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(primaryColor.copy(alpha = 0.1f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = pay.paymentMode, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = pay.paymentDate, fontSize = 12.sp, color = textColor)
                                }
                                if (!pay.remarks.isNullOrBlank()) {
                                    Text(text = pay.remarks, fontSize = 11.sp, color = subtitleColor)
                                }
                            }
                            Text(
                                text = "₹${pay.amountPaid.toInt()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }
        }
    }
}
