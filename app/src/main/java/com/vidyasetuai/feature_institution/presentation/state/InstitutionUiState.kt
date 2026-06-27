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
import com.vidyasetuai.feature_institution.domain.model.StudentHomeLocation


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
    val activeBusLocation: BusLiveLocation? = null,
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
    val contentFeedItems: List<ContentFeedItem> = emptyList(),
    
    // Admin Dashboard aggregates
    val adminTotalCollected: Double = 0.0,
    val adminTotalPending: Double = 0.0,

    // Driver Bus Trip & Attendance Logs State
    val activeBusTrip: com.vidyasetuai.feature_institution.domain.model.ParentBusTrip? = null,
    val busTripAttendanceLogs: List<com.vidyasetuai.feature_institution.data.local.entity.LocalParentBusTripAttendanceLogWithStudentInfo> = emptyList(),
    val isSyncingLogs: Boolean = false,
    val syncSuccessCount: Int = 0,
    val driverBusTrips: List<com.vidyasetuai.feature_institution.domain.model.ParentBusTrip> = emptyList(),
    val assignedStudents: List<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity> = emptyList(),
    val searchQuery: String = "",
    val isCameraScannerOpen: Boolean = false,

    // Remarks System State
    val offlineStudents: List<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity> = emptyList(),
    val remarks: List<com.vidyasetuai.feature_institution.domain.model.Remark> = emptyList(),
    val remarkTargetsMap: Map<String, List<com.vidyasetuai.feature_institution.domain.model.RemarkTarget>> = emptyMap(),
    val latestRemarkAlert: com.vidyasetuai.feature_institution.domain.model.Remark? = null,
    val globalStaffRoles: List<com.vidyasetuai.feature_institution.domain.model.GlobalStaffRole> = emptyList(),
    val offlineStaff: List<com.vidyasetuai.feature_institution.data.local.entity.LocalParentStaffEntity> = emptyList(),

    // Active SubScreen State (persisted via SharedPreferences)
    val activeSubScreen: String? = null,

    // Home Location & Details Screens State
    val selectedStudentDetail: com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity? = null,
    val selectedStudentBusAssignment: StudentBusAssignment? = null,
    val selectedStudentHomeLocation: StudentHomeLocation? = null,
    val isSavingHomeLocation: Boolean = false
)