package com.vidyasetuai.feature_institution.presentation.event

sealed class InstitutionEvent {
    data class LoadWorkspaces(val userId: String, val navTarget: String? = null) : InstitutionEvent()
    data class SwitchWorkspace(val workspaceId: String) : InstitutionEvent()
    data class SubmitLeaveRequest(
        val parentOrgId: String,
        val orgId: String?,
        val sessionId: String,
        val applicantType: String,
        val staffId: String?,
        val studentId: String?,
        val leaveType: String,
        val startDate: String,
        val endDate: String,
        val isHalfDay: Boolean,
        val halfDayPeriod: String?,
        val reason: String?,
        val createdBy: String
    ) : InstitutionEvent()
    data class LoadLeaves(val userId: String, val role: String) : InstitutionEvent()
    data class LoadFeePayments(val studentIds: List<String>) : InstitutionEvent()
    data class ToggleDriverTrip(val busId: String, val parentOrgId: String, val sessionId: String?) : InstitutionEvent()
    data class SelectChildOrg(val orgId: String) : InstitutionEvent()
    data class SelectClass(val classId: String) : InstitutionEvent()
    data class SelectSection(val sectionId: String) : InstitutionEvent()
    data class LoadAttendance(val studentIds: List<String>) : InstitutionEvent()
    data class LoadStudentBuses(val studentIds: List<String>) : InstitutionEvent()
    data class LoadBusLiveLocation(val busId: String) : InstitutionEvent()
    data class LoadPendingApprovals(val userId: String) : InstitutionEvent()
    data class ApproveSpecificConnection(val linkId: String, val tableName: String, val userId: String) : InstitutionEvent()
    
    // Student Attendance Events
    data class LoadAttendanceDropdowns(val parentOrgId: String) : InstitutionEvent()
    data class LoadStudentsForAttendance(val orgId: String, val classId: String, val sectionId: String, val date: String) : InstitutionEvent()
    data class UpdateStudentAttendanceStatus(val studentId: String, val status: String) : InstitutionEvent()
    data class SubmitStudentsAttendance(val orgId: String, val date: String, val staffUserId: String, val parentOrgId: String) : InstitutionEvent()
    data class LoadClassTeacherAssignment(val userId: String, val parentOrgId: String, val autoLoadIfMarked: Boolean = false) : InstitutionEvent()
    data class ChangeAttendanceDate(val date: String) : InstitutionEvent()
    data class LoadSalaryDetails(val userId: String, val parentOrgId: String) : InstitutionEvent()
    data class SetAttendanceEditEnabled(val enabled: Boolean) : InstitutionEvent()
    object ClearAttendanceFilters : InstitutionEvent()
    object LoadContentFeed : InstitutionEvent()

    // Driver Bus Trip & Attendance Events
    data class LoadActiveTrip(val driverId: String) : InstitutionEvent()
    data class StartBusTrip(val parentOrgId: String, val sessionId: String, val busId: String, val driverId: String, val tripType: String) : InstitutionEvent()
    data class EndBusTrip(val tripId: String) : InstitutionEvent()
    data class ScanStudentQrCode(val qrToken: String, val latitude: Double?, val longitude: Double?, val staffId: String) : InstitutionEvent()
    data class LoadTripAttendanceLogs(val tripId: String) : InstitutionEvent()
    object SyncOfflineLogs : InstitutionEvent()
    data class LoadDriverTrips(val driverId: String) : InstitutionEvent()
    data class LoadAssignedStudents(val busId: String) : InstitutionEvent()
    data class UpdateSearchQuery(val query: String) : InstitutionEvent()
    data class ToggleCameraScanner(val open: Boolean) : InstitutionEvent()
    data class MarkStudentBoarded(val studentId: String, val latitude: Double?, val longitude: Double?, val staffId: String) : InstitutionEvent()

    // Remarks System Events
    data class LoadRemarks(val sessionId: String, val parentOrgId: String) : InstitutionEvent()
    data class SubmitRemark(
        val content: String,
        val category: String,
        val priority: String,
        val visibilityType: String,
        val visibilityAudience: List<String>,
        val targetType: String,
        val targetStudentId: String?,
        val targetGuardianId: String?,
        val targetStaffId: String?
    ) : InstitutionEvent()
    data class DeleteRemark(val remarkId: String) : InstitutionEvent()
    object SyncRemarks : InstitutionEvent()

    // SubScreen Navigation State
    data class ChangeActiveSubScreen(val subScreen: String?) : InstitutionEvent()

    // Home Location & Details Events
    data class LoadStudentProfileDetails(val studentId: String) : InstitutionEvent()
    data class SaveStudentHomeCoordinates(
        val organizationId: String,
        val studentId: String,
        val latitude: Double,
        val longitude: Double,
        val userId: String
    ) : InstitutionEvent()
}