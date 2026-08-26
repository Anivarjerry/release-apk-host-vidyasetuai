package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSalaryScreen(
    isHindi: Boolean = false,
    onNavigateBack: () -> Unit,
    viewModel: StaffSalaryViewModel = viewModel(factory = StaffSalaryViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Pillar 5: Mandatory Native Back Handler
    BackHandler(enabled = true) {
        when {
            uiState.isProfileModalOpen -> viewModel.closeProfileModal()
            uiState.isAttendanceModalOpen -> viewModel.closeAttendanceModal()
            uiState.isDailyAttendanceModalOpen -> viewModel.closeDailyAttendanceModal()
            uiState.isPaymentModalOpen -> viewModel.closePaymentModal()
            uiState.isPassbookModalOpen -> viewModel.closePassbookModal()
            uiState.isPayrollModalOpen -> viewModel.closePayrollModal()
            else -> onNavigateBack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isHindi) "कर्मचारी वेतन व हाजिरी" else "Staff Salary & Payroll",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${if (isHindi) uiState.monthNameHindi else uiState.monthName} ${uiState.selectedYear}",
                            fontSize = 12.sp,
                            color = AppColors.EmeraldGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Lucide.ArrowLeft, contentDescription = "Back")
                    }
                },
                actions = {
                    // Month Navigation Prev / Next
                    IconButton(
                        onClick = {
                            val newM = if (uiState.selectedMonth == 1) 12 else uiState.selectedMonth - 1
                            val newY = if (uiState.selectedMonth == 1) uiState.selectedYear - 1 else uiState.selectedYear
                            viewModel.changeMonth(newM, newY)
                        }
                    ) {
                        Icon(imageVector = Lucide.ChevronLeft, contentDescription = "Previous Month")
                    }

                    IconButton(
                        onClick = {
                            val newM = if (uiState.selectedMonth == 12) 1 else uiState.selectedMonth + 1
                            val newY = if (uiState.selectedMonth == 12) uiState.selectedYear + 1 else uiState.selectedYear
                            viewModel.changeMonth(newM, newY)
                        }
                    ) {
                        Icon(imageVector = Lucide.ChevronRight, contentDescription = "Next Month")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // =========================================================================
            // 📊 1. METRIC CARDS ROW (4 Apple HIG Cards)
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SalaryMetricCard(
                        title = if (isHindi) "कुल पेरोल" else "Total Payroll",
                        amount = "₹${uiState.totalPayroll.toInt()}",
                        color = AppColors.EmeraldGreen,
                        icon = Lucide.Wallet,
                        modifier = Modifier.weight(1f)
                    )
                    SalaryMetricCard(
                        title = if (isHindi) "भुगतान हुआ" else "Disbursed",
                        amount = "₹${uiState.totalDisbursed.toInt()}",
                        color = Color(0xFF3B82F6),
                        icon = Lucide.CheckCheck,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SalaryMetricCard(
                        title = if (isHindi) "बकाया वेतन" else "Balance Due",
                        amount = "₹${uiState.totalBalanceDue.toInt()}",
                        color = if (uiState.totalBalanceDue > 0) Color(0xFFEF4444) else AppColors.EmeraldGreen,
                        icon = Lucide.CircleAlert,
                        modifier = Modifier.weight(1f)
                    )
                    SalaryMetricCard(
                        title = if (isHindi) "चालू अग्रिम" else "Advances",
                        amount = "₹${uiState.totalRunningAdvances.toInt()}",
                        color = Color(0xFFF59E0B),
                        icon = Lucide.TrendingDown,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // =========================================================================
            // ⚡ 2. STORE-WIDE FAST ACTION BUTTONS
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Today's Daily Attendance
                Button(
                    onClick = { viewModel.openDailyAttendanceModal() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Lucide.CalendarCheck, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "आज की हाजिरी" else "Daily Attendance",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Generate / Lock Payroll
                Button(
                    onClick = { viewModel.openPayrollModal() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Lucide.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "पेरोल लॉक / स्लिप" else "Monthly Payroll",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // =========================================================================
            // 👥 3. REACTIVE STAFF SALARY LIST
            // =========================================================================
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.EmeraldGreen)
                }
            } else if (uiState.staffRows.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Lucide.Users, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Text(
                            text = if (isHindi) "कोई कर्मचारी नहीं मिला।" else "No staff members registered.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.staffRows, key = { it.staff.id }) { row ->
                        StaffSalaryCard(
                            isHindi = isHindi,
                            row = row,
                            onPay = { viewModel.openPaymentModal(row) },
                            onAttendance = { viewModel.openAttendanceModal(row) },
                            onProfile = { viewModel.openProfileModal(row) },
                            onPassbook = { viewModel.openPassbookModal(row) }
                        )
                    }
                }
            }
        }
    }

    // =========================================================================
    // 🗂️ BOTTOM SHEETS
    // =========================================================================

    if (uiState.isProfileModalOpen && uiState.selectedStaffRow != null) {
        StaffSalaryProfileBottomSheet(
            isHindi = isHindi,
            staffRow = uiState.selectedStaffRow!!,
            isSaving = uiState.isSaving,
            onDismiss = { viewModel.closeProfileModal() },
            onSave = { salaryType, baseSalary, workingDays, bankName, accountNo, ifsc, holderName, upiId ->
                viewModel.saveSalaryProfile(
                    staffId = uiState.selectedStaffRow!!.staff.id,
                    salaryType = salaryType,
                    baseSalary = baseSalary,
                    workingDays = workingDays,
                    bankName = bankName,
                    accountNo = accountNo,
                    ifsc = ifsc,
                    holderName = holderName,
                    upiId = upiId
                )
            }
        )
    }

    if (uiState.isAttendanceModalOpen && uiState.selectedStaffRow != null) {
        StaffAttendanceBottomSheet(
            isHindi = isHindi,
            staffRow = uiState.selectedStaffRow!!,
            month = uiState.selectedMonth,
            year = uiState.selectedYear,
            attendanceRecords = uiState.selectedStaffAttendance,
            onToggleDay = { date, status -> viewModel.toggleDayAttendance(date, status) },
            onDismiss = { viewModel.closeAttendanceModal() }
        )
    }

    if (uiState.isDailyAttendanceModalOpen) {
        StaffDailyAttendanceBottomSheet(
            isHindi = isHindi,
            date = uiState.dailyAttendanceDate,
            staffList = uiState.dailyAttendanceStaff,
            attendanceMap = uiState.dailyAttendanceMap,
            isSaving = uiState.isSaving,
            onSetStatus = { staffId, status -> viewModel.setDailyAttendanceStatus(staffId, status) },
            onSaveAll = { viewModel.saveAllDailyAttendance() },
            onDismiss = { viewModel.closeDailyAttendanceModal() }
        )
    }

    if (uiState.isPaymentModalOpen && uiState.selectedStaffRow != null) {
        StaffSalaryPaymentBottomSheet(
            isHindi = isHindi,
            staffRow = uiState.selectedStaffRow!!,
            isSaving = uiState.isSaving,
            onDismiss = { viewModel.closePaymentModal() },
            onSavePayment = { paymentType, amount, mode, ref, remarks ->
                viewModel.savePayment(paymentType, amount, mode, ref, remarks)
            }
        )
    }

    if (uiState.isPassbookModalOpen && uiState.selectedStaffRow != null) {
        StaffSalaryPassbookBottomSheet(
            isHindi = isHindi,
            staffRow = uiState.selectedStaffRow!!,
            payments = uiState.selectedStaffPayments,
            onDismiss = { viewModel.closePassbookModal() }
        )
    }

    if (uiState.isPayrollModalOpen) {
        StaffPayrollGenerationBottomSheet(
            isHindi = isHindi,
            uiState = uiState,
            onGenerate = { lockMonth -> viewModel.generatePayroll(lockMonth) },
            onDismiss = { viewModel.closePayrollModal() }
        )
    }
}

@Composable
private fun SalaryMetricCard(
    title: String,
    amount: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = amount, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = color)
            }
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun StaffSalaryCard(
    isHindi: Boolean,
    row: StaffSalaryRowUiModel,
    onPay: () -> Unit,
    onAttendance: () -> Unit,
    onProfile: () -> Unit,
    onPassbook: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Staff Name, Role, Verified @username & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = row.staff.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!row.staff.username.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AppColors.EmeraldGreen.copy(alpha = 0.12f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Icon(imageVector = Lucide.Check, contentDescription = null, tint = AppColors.EmeraldGreen, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "@${row.staff.username}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.EmeraldGreen
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = "${row.staff.role} • Base: ₹${row.baseSalary.toInt()}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Payment Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (row.paymentStatus) {
                        "PAID" -> Color(0xFF10B981).copy(alpha = 0.15f)
                        "PARTIALLY_PAID" -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                        else -> Color(0xFFEF4444).copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = when (row.paymentStatus) {
                            "PAID" -> if (isHindi) "चुका दिया (PAID)" else "PAID"
                            "PARTIALLY_PAID" -> if (isHindi) "आंशिक (PARTIAL)" else "PARTIAL"
                            else -> if (isHindi) "बाकी (UNPAID)" else "UNPAID"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (row.paymentStatus) {
                            "PAID" -> Color(0xFF047857)
                            "PARTIALLY_PAID" -> Color(0xFFB45309)
                            else -> Color(0xFFB91C1C)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Row 2: Attendance Pro-rata & Financial Figures
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isHindi) "हाजिरी (Attendance):" else "Attendance:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${row.presentDays.toInt()}P • ${row.halfDays.toInt()}HD • ${row.paidLeaves.toInt()}PL • ${row.absentDays.toInt()}A",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = if (isHindi) "देय (Payable)" else "Payable", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "₹${row.netPayable.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = if (isHindi) "बकाया (Due)" else "Balance Due", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "₹${row.balanceDue.toInt()}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (row.balanceDue > 0) Color(0xFFEF4444) else AppColors.EmeraldGreen
                        )
                    }
                }
            }

            // Row 3: 4 Fast Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Pay Button
                Button(
                    onClick = onPay,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(imageVector = Lucide.Banknote, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isHindi) "भुगतान" else "Pay", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Attendance Calendar Button
                OutlinedButton(
                    onClick = onAttendance,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Lucide.Calendar, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = if (isHindi) "हाजिरी" else "Attnd", fontSize = 10.sp)
                }

                // Passbook / Slip Button
                OutlinedButton(
                    onClick = onPassbook,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Lucide.FileText, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = if (isHindi) "पर्ची" else "Slip", fontSize = 10.sp)
                }

                // Settings / Profile Button
                IconButton(
                    onClick = onProfile,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(imageVector = Lucide.Settings, contentDescription = "Profile", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

class StaffSalaryViewModelFactory(private val context: android.content.Context) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return StaffSalaryViewModel(context) as T
    }
}
