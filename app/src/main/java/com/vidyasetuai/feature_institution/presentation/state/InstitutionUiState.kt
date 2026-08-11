package com.vidyasetuai.feature_institution.presentation.state

import com.vidyasetuai.feature_institution.domain.model.Workspace
import com.vidyasetuai.feature_institution.domain.model.Leave
import com.vidyasetuai.feature_institution.domain.model.FeePayment
import com.vidyasetuai.feature_institution.domain.model.InstitutionStudent
import com.vidyasetuai.feature_institution.domain.model.StudentAttendance
import com.vidyasetuai.feature_institution.domain.model.StudentBusAssignment
import com.vidyasetuai.feature_institution.domain.model.BusLiveLocation
import com.vidyasetuai.feature_institution.domain.model.PendingApproval
import com.vidyasetuai.feature_institution.domain.model.DriverBusDetails
import com.vidyasetuai.feature_institution.domain.model.ChildOrg
import com.vidyasetuai.feature_institution.domain.model.OrgClass
import com.vidyasetuai.feature_institution.domain.model.OrgSection
import com.vidyasetuai.feature_institution.domain.model.StudentAttendanceInfo
import com.vidyasetuai.feature_institution.domain.model.AssignedSection
import com.vidyasetuai.feature_institution.domain.model.StaffSalaryPayment
import com.vidyasetuai.feature_institution.domain.model.ContentFeedItem
import com.vidyasetuai.feature_institution.domain.model.BusRouteStop
import com.vidyasetuai.feature_institution.domain.model.StudentHomeLocation
import com.vidyasetuai.feature_institution.domain.model.ParentBusTrip
import com.vidyasetuai.feature_institution.domain.model.Remark
import com.vidyasetuai.feature_institution.domain.model.RemarkTarget
import com.vidyasetuai.feature_institution.domain.model.GlobalStaffRole
import com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalParentStaffEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalParentBusEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalParentBusTripAttendanceLogWithStudentInfo

data class TodayLogItem(
    val id: String,
    val title: String,
    val titleHi: String? = null,
    val subtitle: String,
    val type: String, // ATTENDANCE, LEAVE, FEE, BUS, REMARK, NOTICE
    val iconType: String // user, calendar, card, bus, info
)

data class InstitutionUiState(
    val userId: String = "",
    val isLoading: Boolean = false,
    val workspaces: List<Workspace> = emptyList(),
    val pendingApprovals: List<PendingApproval> = emptyList(),
    val activeWorkspace: Workspace? = null,
    val leaves: List<Leave> = emptyList(),
    val leaveQuota: Double = 12.0,
    val remainingLeaves: Double = 12.0,
    val feePayments: List<FeePayment> = emptyList(),
    val guardianStudents: List<InstitutionStudent> = emptyList(),
    val isTripActive: Boolean = false,
    val tripDurationSeconds: Long = 0L,
    val errorMessage: String? = null,
    val selectedChildOrg: String? = null,
    val selectedClass: String? = null,
    val selectedSection: String? = null,
    val notificationSubscribed: Boolean = false,
    val studentAttendance: List<StudentAttendance> = emptyList(),
    val studentBuses: List<StudentBusAssignment> = emptyList(),
    val allBuses: List<LocalParentBusEntity> = emptyList(),
    val activeBusLocation: BusLiveLocation? = null,
    val activeBusRoutes: Map<String, List<BusRouteStop>> = emptyMap(), // busId -> route stops
    val isTrackingActive: Boolean = false,
    val driverBusDetails: DriverBusDetails? = null,
    
    // Student Attendance Marking State
    val childOrganizations: List<ChildOrg> = emptyList(),
    val activeClasses: List<OrgClass> = emptyList(),
    val activeSections: List<OrgSection> = emptyList(),
    val studentsForAttendance: List<StudentAttendanceInfo> = emptyList(),
    val assignedClassTeacherSection: AssignedSection? = null,
    val attendanceSubmittedSuccess: Boolean = false,
    val attendanceDate: String = "2026-06-15",
    
    // Active Session & Constraints State
    val activeSessionId: String = "",
    val activeSessionName: String = "",
    val sessionStartDate: String = "2026-04-01",
    val sessionEndDate: String = "2027-03-31",
    val isAttendanceAlreadyMarked: Boolean = false,
    val isAttendanceEditEnabled: Boolean = false,
    
    // Salary Dashboard State
    val monthlySalary: Double = 0.0,
    val totalSalaryPaid: Double = 0.0,
    val salaryPayments: List<StaffSalaryPayment> = emptyList(),
    val salaryOverviews: List<com.vidyasetuai.feature_institution.domain.model.StaffSalaryOverview> = emptyList(),
    val selectedSalaryMonth: Int = 7,
    val selectedSalaryYear: Int = 2026,
    val totalPayrollAmount: Double = 0.0,
    val totalSalaryPaidAmount: Double = 0.0,
    val totalSalaryPendingAmount: Double = 0.0,
    val totalPayrollPaidAmount: Double = 0.0,
    val totalAdvancePaidAmount: Double = 0.0,
    val selectedStaffOverview: com.vidyasetuai.feature_institution.domain.model.StaffSalaryOverview? = null,
    val isRecordPaymentDialogOpen: Boolean = false,
    val isSetSalaryDialogOpen: Boolean = false,
    val contentFeedItems: List<ContentFeedItem> = emptyList(),
    
    // Admin Dashboard aggregates
    val adminTotalCollected: Double = 0.0,
    val adminTotalPending: Double = 0.0,

    // Driver Bus Trip & Attendance Logs State
    val activeBusTrip: ParentBusTrip? = null,
    val busTripAttendanceLogs: List<LocalParentBusTripAttendanceLogWithStudentInfo> = emptyList(),
    val isSyncingLogs: Boolean = false,
    val syncSuccessCount: Int = 0,

    // Offline Unsynced Counts State
    val unsyncedAttendanceCount: Int = 0,
    val unsyncedLeavesCount: Int = 0,
    val unsyncedRemarksCount: Int = 0,
    val totalUnsyncedCount: Int = 0,

    // Dynamic Today's Logs State
    val todayLogs: List<TodayLogItem> = emptyList(),

    // SubScreen & Search State
    val searchQuery: String = "",
    val isCameraScannerOpen: Boolean = false,
    val activeSubScreen: String? = null,
    
    // Audio Proximity Self Attendance Code
    val staffAudioCode: String? = null,

    // Additional Directory / Entities State
    val offlineStudents: List<LocalStudentEntity> = emptyList(),
    val offlineStaff: List<LocalParentStaffEntity> = emptyList(),
    val assignedStudents: List<LocalStudentEntity> = emptyList(),

    // Remarks State
    val remarks: List<Remark> = emptyList(),
    val remarkTargetsMap: Map<String, List<RemarkTarget>> = emptyMap(),
    val latestRemarkAlert: Remark? = null,

    // Global Roles & Details State
    val globalStaffRoles: List<GlobalStaffRole> = emptyList(),
    val selectedStudentDetail: LocalStudentEntity? = null,
    val selectedStudentBusAssignment: StudentBusAssignment? = null,
    val selectedStudentHomeLocation: StudentHomeLocation? = null,
    val isSavingHomeLocation: Boolean = false,
    val driverBusTrips: List<ParentBusTrip> = emptyList()
)