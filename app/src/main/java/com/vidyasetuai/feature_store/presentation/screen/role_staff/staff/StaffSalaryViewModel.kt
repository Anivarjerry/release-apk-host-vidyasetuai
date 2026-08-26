package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.entity.*
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

class StaffSalaryViewModel(
    private val context: Context,
    private val remoteDataSource: StoreRemoteDataSource = StoreRemoteDataSource()
) : ViewModel() {

    private val storeDb = StoreDatabase.getDatabase(context)
    private val _uiState = MutableStateFlow(StaffSalaryUiState())
    val uiState: StateFlow<StaffSalaryUiState> = _uiState.asStateFlow()

    private var dataJob: Job? = null

    init {
        observeActiveBusiness()
    }

    private fun observeActiveBusiness() {
        viewModelScope.launch(Dispatchers.IO) {
            storeDb.businessDao().getAnyActiveBusinessFlow().collectLatest { business ->
                val bId = business?.id ?: ""
                _uiState.update { it.copy(businessId = bId) }
                if (bId.isNotBlank()) {
                    loadSalaryData(bId, _uiState.value.selectedMonth, _uiState.value.selectedYear)
                }
            }
        }
    }

    fun changeMonth(month: Int, year: Int) {
        _uiState.update { it.copy(selectedMonth = month, selectedYear = year) }
        val bId = _uiState.value.businessId
        if (bId.isNotBlank()) {
            loadSalaryData(bId, month, year)
        }
    }

    private fun loadSalaryData(businessId: String, month: Int, year: Int) {
        dataJob?.cancel()
        dataJob = viewModelScope.launch(Dispatchers.IO) {
            val monthStr = String.format("%02d", month)
            val startDate = "$year-$monthStr-01"
            val endDate = "$year-$monthStr-31"

            combine(
                storeDb.businessStaffDao().getStaffFlow(businessId),
                storeDb.businessStaffSalaryDao().getSalaryProfilesFlow(businessId),
                storeDb.businessStaffSalaryDao().getPayoutsForMonthFlow(businessId, month, year),
                storeDb.businessStaffSalaryDao().getAllPaymentsForBusinessFlow(businessId)
            ) { staffList, profiles, payouts, allPayments ->
                val profileMap = profiles.associateBy { it.staffId }
                val payoutMap = payouts.associateBy { it.staffId }
                val unsettledAdvanceMap = allPayments
                    .filter { it.paymentType == "ADVANCE" && !it.isAdvanceSettled }
                    .groupBy { it.staffId }
                    .mapValues { (_, list) -> list.sumOf { it.amountPaid } }

                staffList.map { staff ->
                    val profile = profileMap[staff.id]
                    val payout = payoutMap[staff.id]
                    val advance = unsettledAdvanceMap[staff.id] ?: 0.0

                    // Fetch attendance records from Room for this staff & month
                    val attendance = storeDb.businessStaffSalaryDao().getAttendanceForMonth(
                        businessId = businessId,
                        staffId = staff.id,
                        startDate = startDate,
                        endDate = endDate
                    )

                    StaffSalaryRowUiModel(
                        staff = staff,
                        profile = profile,
                        payout = payout,
                        attendanceRecords = attendance,
                        unsettledAdvance = advance
                    )
                }
            }.collectLatest { rows ->
                _uiState.update {
                    it.copy(
                        staffRows = rows,
                        isLoading = false
                    )
                }
            }
        }
    }

    // =========================================================================
    // ⚙️ 1. SALARY PROFILE MODAL
    // =========================================================================

    fun openProfileModal(row: StaffSalaryRowUiModel) {
        _uiState.update { it.copy(selectedStaffRow = row, isProfileModalOpen = true, errorMessage = null) }
    }

    fun closeProfileModal() {
        _uiState.update { it.copy(isProfileModalOpen = false, selectedStaffRow = null) }
    }

    fun saveSalaryProfile(
        staffId: String,
        salaryType: String,
        baseSalary: Double,
        workingDays: Int,
        bankName: String?,
        accountNo: String?,
        ifsc: String?,
        holderName: String?,
        upiId: String?
    ) {
        val bId = _uiState.value.businessId
        if (bId.isBlank()) return

        val existing = _uiState.value.selectedStaffRow?.profile
        val nowIso = Instant.now().toString()

        val profile = BusinessStaffSalaryProfileEntity(
            id = existing?.id ?: UUID.randomUUID().toString(),
            businessId = bId,
            branchId = _uiState.value.selectedStaffRow?.staff?.branchId,
            staffId = staffId,
            salaryType = salaryType,
            baseSalary = baseSalary,
            workingDaysPerMonth = workingDays,
            bankName = bankName?.trim()?.ifBlank { null },
            accountNumber = accountNo?.trim()?.ifBlank { null },
            ifscCode = ifsc?.trim()?.ifBlank { null },
            accountHolderName = holderName?.trim()?.ifBlank { null },
            upiId = upiId?.trim()?.ifBlank { null },
            isActive = true,
            isDeleted = false,
            createdAt = existing?.createdAt ?: nowIso,
            updatedAt = nowIso
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSaving = true) }
            val remoteSuccess = remoteDataSource.saveStaffSalaryProfile(profile)
            if (remoteSuccess) {
                storeDb.businessStaffSalaryDao().upsertProfile(profile)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isProfileModalOpen = false,
                        selectedStaffRow = null,
                        successMessage = "Salary profile updated successfully!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Unable to save salary profile online. Please check network connection."
                    )
                }
            }
        }
    }

    // =========================================================================
    // 📅 2. INDIVIDUAL ATTENDANCE CALENDAR MODAL
    // =========================================================================

    fun openAttendanceModal(row: StaffSalaryRowUiModel) {
        _uiState.update {
            it.copy(
                selectedStaffRow = row,
                selectedStaffAttendance = row.attendanceRecords,
                isAttendanceModalOpen = true,
                errorMessage = null
            )
        }
    }

    fun closeAttendanceModal() {
        _uiState.update { it.copy(isAttendanceModalOpen = false, selectedStaffRow = null, selectedStaffAttendance = emptyList()) }
    }

    fun toggleDayAttendance(date: String, status: String) {
        val staffRow = _uiState.value.selectedStaffRow ?: return
        val bId = _uiState.value.businessId
        val nowIso = Instant.now().toString()

        val existing = _uiState.value.selectedStaffAttendance.find { it.attendanceDate == date }
        val attendance = BusinessStaffAttendanceEntity(
            id = existing?.id ?: UUID.randomUUID().toString(),
            businessId = bId,
            branchId = staffRow.staff.branchId,
            staffId = staffRow.staff.id,
            attendanceDate = date,
            status = status,
            isActive = true,
            isDeleted = false,
            createdAt = existing?.createdAt ?: nowIso,
            updatedAt = nowIso
        )

        viewModelScope.launch(Dispatchers.IO) {
            val remoteSuccess = remoteDataSource.recordStaffAttendance(attendance)
            if (remoteSuccess) {
                storeDb.businessStaffSalaryDao().upsertAttendance(attendance)
                // Update local state list
                val updated = _uiState.value.selectedStaffAttendance.filter { it.attendanceDate != date } + attendance
                _uiState.update { it.copy(selectedStaffAttendance = updated.sortedBy { a -> a.attendanceDate }, errorMessage = null) }
                loadSalaryData(bId, _uiState.value.selectedMonth, _uiState.value.selectedYear)
            } else {
                _uiState.update {
                    it.copy(errorMessage = "Unable to record attendance online. Please check network connection.")
                }
            }
        }
    }

    // =========================================================================
    // 👥 3. DAILY STORE-WIDE ATTENDANCE MODAL
    // =========================================================================

    fun openDailyAttendanceModal() {
        viewModelScope.launch(Dispatchers.IO) {
            val bId = _uiState.value.businessId
            val date = LocalDate.now().toString()
            val staff = storeDb.businessStaffDao().getStaffFlow(bId).first()
            val todayAttendance = storeDb.businessStaffSalaryDao().getDailyAttendanceFlow(bId, date).first()
            val aMap = todayAttendance.associate { it.staffId to it.status }

            _uiState.update {
                it.copy(
                    isDailyAttendanceModalOpen = true,
                    dailyAttendanceStaff = staff,
                    dailyAttendanceDate = date,
                    dailyAttendanceMap = aMap,
                    errorMessage = null
                )
            }
        }
    }

    fun closeDailyAttendanceModal() {
        _uiState.update { it.copy(isDailyAttendanceModalOpen = false) }
    }

    fun setDailyAttendanceStatus(staffId: String, status: String) {
        val current = _uiState.value.dailyAttendanceMap.toMutableMap()
        current[staffId] = status
        _uiState.update { it.copy(dailyAttendanceMap = current) }
    }

    fun saveAllDailyAttendance() {
        val bId = _uiState.value.businessId
        val date = _uiState.value.dailyAttendanceDate
        val aMap = _uiState.value.dailyAttendanceMap
        val staffList = _uiState.value.dailyAttendanceStaff
        val nowIso = Instant.now().toString()

        val listToSave = staffList.map { staff ->
            val status = aMap[staff.id] ?: "PRESENT"
            BusinessStaffAttendanceEntity(
                id = UUID.randomUUID().toString(),
                businessId = bId,
                branchId = staff.branchId,
                staffId = staff.id,
                attendanceDate = date,
                status = status,
                isActive = true,
                isDeleted = false,
                createdAt = nowIso,
                updatedAt = nowIso
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSaving = true) }
            var allSuccessful = true
            for (att in listToSave) {
                val ok = remoteDataSource.recordStaffAttendance(att)
                if (!ok) {
                    allSuccessful = false
                    break
                }
            }
            if (allSuccessful) {
                storeDb.businessStaffSalaryDao().upsertAllAttendance(listToSave)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isDailyAttendanceModalOpen = false,
                        successMessage = "Daily attendance recorded for ${listToSave.size} staff members!"
                    )
                }
                loadSalaryData(bId, _uiState.value.selectedMonth, _uiState.value.selectedYear)
            } else {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Unable to save daily attendance online. Please check network connection."
                    )
                }
            }
        }
    }

    // =========================================================================
    // 💵 4. SALARY / ADVANCE PAYMENT MODAL
    // =========================================================================

    fun openPaymentModal(row: StaffSalaryRowUiModel) {
        _uiState.update { it.copy(selectedStaffRow = row, isPaymentModalOpen = true, errorMessage = null) }
    }

    fun closePaymentModal() {
        _uiState.update { it.copy(isPaymentModalOpen = false, selectedStaffRow = null) }
    }

    fun savePayment(
        paymentType: String,
        amount: Double,
        mode: String,
        ref: String?,
        remarks: String?
    ) {
        val row = _uiState.value.selectedStaffRow ?: return
        val bId = _uiState.value.businessId
        val nowIso = Instant.now().toString()
        val today = LocalDate.now().toString()

        val paymentEntity = BusinessStaffSalaryPaymentEntity(
            id = UUID.randomUUID().toString(),
            businessId = bId,
            staffId = row.staff.id,
            payoutId = row.payout?.id,
            paymentType = paymentType,
            amountPaid = amount,
            paymentMode = mode,
            paymentDate = today,
            transactionRef = ref?.trim()?.ifBlank { null },
            remarks = remarks?.trim()?.ifBlank { null },
            isAdvanceSettled = false,
            isActive = true,
            isDeleted = false,
            createdAt = nowIso,
            updatedAt = nowIso
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSaving = true) }
            val remoteSuccess = remoteDataSource.recordStaffSalaryPayment(
                businessId = bId,
                staffId = row.staff.id,
                payoutId = row.payout?.id,
                paymentType = paymentType,
                amountPaid = amount,
                paymentMode = mode,
                transactionRef = ref,
                remarks = remarks
            )
            if (remoteSuccess) {
                storeDb.businessStaffSalaryDao().upsertPayment(paymentEntity)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isPaymentModalOpen = false,
                        selectedStaffRow = null,
                        successMessage = "₹$amount $paymentType recorded successfully!"
                    )
                }
                loadSalaryData(bId, _uiState.value.selectedMonth, _uiState.value.selectedYear)
            } else {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Unable to record payment online. Please check network connection."
                    )
                }
            }
        }
    }

    // =========================================================================
    // 📑 5. PASSBOOK & WHATSAPP SLIP MODAL
    // =========================================================================

    fun openPassbookModal(row: StaffSalaryRowUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val payments = storeDb.businessStaffSalaryDao().getPaymentsForStaff(row.staff.id)
            _uiState.update {
                it.copy(
                    selectedStaffRow = row,
                    selectedStaffPayments = payments,
                    isPassbookModalOpen = true,
                    errorMessage = null
                )
            }
        }
    }

    fun closePassbookModal() {
        _uiState.update { it.copy(isPassbookModalOpen = false, selectedStaffRow = null, selectedStaffPayments = emptyList()) }
    }

    // =========================================================================
    // 🔒 6. MONTHLY PAYROLL GENERATION & LOCKING MODAL
    // =========================================================================

    fun openPayrollModal() {
        _uiState.update { it.copy(isPayrollModalOpen = true, errorMessage = null) }
    }

    fun closePayrollModal() {
        _uiState.update { it.copy(isPayrollModalOpen = false) }
    }

    fun generatePayroll(lockMonth: Boolean) {
        val bId = _uiState.value.businessId
        val m = _uiState.value.selectedMonth
        val y = _uiState.value.selectedYear

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSaving = true) }
            val ok = remoteDataSource.generateMonthlyPayroll(bId, m, y, lockMonth)
            if (ok) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isPayrollModalOpen = false,
                        successMessage = if (lockMonth) "Payroll for $m/$y has been locked!" else "Payroll slips generated successfully!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Failed to generate payroll. Check internet connection."
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
