package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff

import com.vidyasetuai.feature_store.data.local.entity.*
import java.time.LocalDate

data class StaffSalaryRowUiModel(
    val staff: BusinessStaffEntity,
    val profile: BusinessStaffSalaryProfileEntity?,
    val payout: BusinessStaffSalaryPayoutEntity?,
    val attendanceRecords: List<BusinessStaffAttendanceEntity> = emptyList(),
    val unsettledAdvance: Double = 0.0
) {
    val presentDays: Double
        get() = if (payout != null) payout.presentDays else attendanceRecords.count { it.status == "PRESENT" }.toDouble()

    val absentDays: Double
        get() = if (payout != null) payout.absentDays else attendanceRecords.count { it.status == "ABSENT" }.toDouble()

    val halfDays: Double
        get() = if (payout != null) payout.halfDays else attendanceRecords.count { it.status == "HALF_DAY" }.toDouble()

    val paidLeaves: Double
        get() = if (payout != null) payout.paidLeaves else attendanceRecords.count { it.status == "PAID_LEAVE" || it.status == "HOLIDAY" }.toDouble()

    val baseSalary: Double
        get() = payout?.baseSalary ?: profile?.baseSalary ?: 0.0

    val netPayable: Double
        get() = payout?.netPayableAmount ?: run {
            val dailyRate = if ((profile?.workingDaysPerMonth ?: 30) > 0) baseSalary / (profile?.workingDaysPerMonth ?: 30) else 0.0
            val effectivePresent = presentDays + (halfDays * 0.5) + paidLeaves
            val gross = effectivePresent * dailyRate
            (gross - unsettledAdvance).coerceAtLeast(0.0)
        }

    val paidAmount: Double
        get() = payout?.paidAmount ?: 0.0

    val balanceDue: Double
        get() = (netPayable - paidAmount).coerceAtLeast(0.0)

    val paymentStatus: String
        get() = when {
            balanceDue <= 0.0 && netPayable > 0.0 -> "PAID"
            paidAmount > 0.0 -> "PARTIALLY_PAID"
            else -> "UNPAID"
        }
}

data class StaffSalaryUiState(
    val businessId: String = "",
    val selectedMonth: Int = LocalDate.now().monthValue,
    val selectedYear: Int = LocalDate.now().year,
    val staffRows: List<StaffSalaryRowUiModel> = emptyList(),
    val dailyAttendanceStaff: List<BusinessStaffEntity> = emptyList(),
    val dailyAttendanceDate: String = LocalDate.now().toString(),
    val dailyAttendanceMap: Map<String, String> = emptyMap(), // staffId -> status

    // Active Selected Employee for Modals
    val selectedStaffRow: StaffSalaryRowUiModel? = null,
    val selectedStaffAttendance: List<BusinessStaffAttendanceEntity> = emptyList(),
    val selectedStaffPayments: List<BusinessStaffSalaryPaymentEntity> = emptyList(),

    // Sheet / Modal Open States
    val isProfileModalOpen: Boolean = false,
    val isAttendanceModalOpen: Boolean = false,
    val isDailyAttendanceModalOpen: Boolean = false,
    val isPaymentModalOpen: Boolean = false,
    val isPassbookModalOpen: Boolean = false,
    val isPayrollModalOpen: Boolean = false,

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val totalPayroll: Double get() = staffRows.sumOf { it.netPayable }
    val totalDisbursed: Double get() = staffRows.sumOf { it.paidAmount }
    val totalBalanceDue: Double get() = staffRows.sumOf { it.balanceDue }
    val totalRunningAdvances: Double get() = staffRows.sumOf { it.unsettledAdvance }

    val monthName: String
        get() = when (selectedMonth) {
            1 -> "January"; 2 -> "February"; 3 -> "March"; 4 -> "April"
            5 -> "May"; 6 -> "June"; 7 -> "July"; 8 -> "August"
            9 -> "September"; 10 -> "October"; 11 -> "November"; 12 -> "December"
            else -> "Month $selectedMonth"
        }

    val monthNameHindi: String
        get() = when (selectedMonth) {
            1 -> "जनवरी"; 2 -> "फरवरी"; 3 -> "मार्च"; 4 -> "अप्रैल"
            5 -> "मई"; 6 -> "जून"; 7 -> "जुलाई"; 8 -> "अगस्त"
            9 -> "सितंबर"; 10 -> "अक्टूबर"; 11 -> "नवंबर"; 12 -> "दिसंबर"
            else -> "माह $selectedMonth"
        }
}
