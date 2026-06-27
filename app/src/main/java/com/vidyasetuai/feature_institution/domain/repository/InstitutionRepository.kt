package com.vidyasetuai.feature_institution.domain.repository

import com.vidyasetuai.feature_institution.domain.model.ConnectionState
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
import com.vidyasetuai.feature_institution.domain.model.StaffSalaryDetails
import com.vidyasetuai.feature_institution.domain.model.ContentFeedItem
import com.vidyasetuai.feature_institution.domain.model.StudentSearchResult
import com.vidyasetuai.feature_institution.domain.model.ParentBusTrip
import com.vidyasetuai.feature_institution.domain.model.ParentBusTripAttendanceLog
import com.vidyasetuai.feature_institution.domain.model.Remark
import com.vidyasetuai.feature_institution.domain.model.RemarkTarget
import com.vidyasetuai.feature_institution.domain.model.GlobalStaffRole
import com.vidyasetuai.feature_institution.domain.model.StudentHomeLocation


interface InstitutionRepository {
    suspend fun checkConnectionStatus(userId: String): Result<ConnectionState>
    suspend fun approveConnection(userId: String): Result<Unit>
    
    // Multi-Workspace support
    suspend fun getWorkspaces(userId: String): Result<List<Workspace>>
    suspend fun setActiveWorkspace(workspaceId: String): Result<Unit>
    suspend fun getGuardianStudents(guardianLinkId: String, forceRefresh: Boolean = false): Result<List<InstitutionStudent>>
    suspend fun getPendingApprovals(userId: String, forceRefresh: Boolean = false): Result<List<PendingApproval>>
    suspend fun approveSpecificConnection(linkId: String, tableName: String): Result<Unit>

    
    // Leave Management
    suspend fun submitLeave(
        parentOrgId: String,
        orgId: String?,
        sessionId: String,
        applicantType: String,
        staffId: String?,
        studentId: String?,
        leaveType: String,
        startDate: String,
        endDate: String,
        isHalfDay: Boolean,
        halfDayPeriod: String?,
        reason: String?,
        createdBy: String
    ): Result<Unit>
    
    suspend fun getLeaveQuotas(staffId: String): Result<Double>
    suspend fun getStaffLeaveQuotaAndRemaining(userId: String, parentOrgId: String, forceRefresh: Boolean = false): Result<Pair<Double, Double>>
    suspend fun getLeaves(userId: String, role: String, forceRefresh: Boolean = false): Result<List<Leave>>
    
    // Fee Payments & Receipts
    suspend fun getFeePayments(studentIds: List<String>, forceRefresh: Boolean = false): Result<List<FeePayment>>
    suspend fun getAdminFeeStats(parentOrgId: String, forceRefresh: Boolean = false): Result<Pair<Double, Double>>
    
    // Bus Live Location
    suspend fun updateBusLiveLocation(
        busId: String,
        parentOrgId: String,
        latitude: Double,
        longitude: Double,
        speed: Double,
        sessionId: String?
    ): Result<Unit>

    suspend fun getStudentAttendance(studentIds: List<String>, forceRefresh: Boolean = false): Result<List<StudentAttendance>>
    suspend fun getStudentBusAssignments(studentIds: List<String>, forceRefresh: Boolean = false): Result<List<StudentBusAssignment>>
    suspend fun getBusLiveLocation(busId: String): Result<BusLiveLocation?>
    suspend fun getDriverBusDetails(workspaceId: String, forceRefresh: Boolean = false): Result<DriverBusDetails?>

    // Student Attendance Management
    suspend fun getOrganizations(parentOrgId: String, forceRefresh: Boolean = false): Result<List<ChildOrg>>
    suspend fun getClasses(orgId: String, forceRefresh: Boolean = false): Result<List<OrgClass>>
    suspend fun getSections(classId: String, forceRefresh: Boolean = false): Result<List<OrgSection>>
    suspend fun getStudentsForAttendance(orgId: String, classId: String, sectionId: String, date: String): Result<List<StudentAttendanceInfo>>
    suspend fun submitStudentAttendance(orgId: String, date: String, attendanceList: List<StudentAttendanceInfo>, staffUserId: String, parentOrgId: String): Result<Unit>
    suspend fun getClassTeacherAssignment(userId: String, parentOrgId: String): Result<AssignedSection?>
    suspend fun getStaffSalaryDetails(userId: String, parentOrgId: String, forceRefresh: Boolean = false): Result<StaffSalaryDetails>
    suspend fun getActiveSessionDetails(forceRefresh: Boolean = false): Result<Pair<String, String>>
    suspend fun checkIfAttendanceMarked(orgId: String, classId: String, sectionId: String, date: String): Result<Boolean>
    suspend fun getContentFeed(workspace: Workspace, sessionId: String, forceRefresh: Boolean = false): Result<List<ContentFeedItem>>

    // --- Offline Sync & Search Support ---
    suspend fun syncWorkspaceData(userId: String, workspace: Workspace, sessionId: String): Result<Unit>
    suspend fun searchStudentsOffline(query: String, classFilterName: String?, sectionFilterName: String?): Result<List<StudentSearchResult>>
    suspend fun getLocalStudentById(studentId: String): Result<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity?>
    suspend fun getOfflineStaff(parentOrgId: String): Result<List<com.vidyasetuai.feature_institution.data.local.entity.LocalParentStaffEntity>>

    // --- Driver Bus Trip & Attendance Sync Support ---
    suspend fun getActiveBusTrip(driverId: String): Result<ParentBusTrip?>
    suspend fun getParentBusTripsForDriver(driverId: String): Result<List<ParentBusTrip>>
    suspend fun getStudentByQrHash(hash: String): Result<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity?>
    suspend fun startBusTrip(parentOrgId: String, sessionId: String, busId: String, driverId: String, tripType: String): Result<ParentBusTrip>
    suspend fun endBusTrip(tripId: String): Result<Unit>
    suspend fun submitBusAttendanceLog(log: ParentBusTripAttendanceLog): Result<ParentBusTripAttendanceLog>
    suspend fun getBusTripAttendanceLogs(tripId: String, forceRefresh: Boolean = false): Result<List<ParentBusTripAttendanceLog>>
    suspend fun getBusTripAttendanceLogsWithStudentInfo(tripId: String): Result<List<com.vidyasetuai.feature_institution.data.local.entity.LocalParentBusTripAttendanceLogWithStudentInfo>>
    suspend fun syncOfflineAttendanceLogs(): Result<Int>
    suspend fun getRemarks(sessionId: String): Result<List<Remark>>
    suspend fun getRemarkTargets(remarkId: String): Result<List<RemarkTarget>>
    suspend fun addRemark(remark: Remark, targets: List<RemarkTarget>): Result<Unit>
    suspend fun softDeleteRemark(remarkId: String): Result<Unit>
    suspend fun getGlobalStaffRoles(): Result<List<GlobalStaffRole>>
    suspend fun loadGlobalStaffRolesFromServer(): Result<Unit>
    suspend fun syncRemarksOffline(): Result<Unit>
    suspend fun fetchRemarksFromServer(sessionId: String, parentOrgId: String): Result<Unit>
    suspend fun getStudentsAssignedToBus(busId: String): Result<List<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity>>
    suspend fun getStudentHomeLocation(studentId: String): Result<StudentHomeLocation?>
    suspend fun saveStudentHomeLocation(
        organizationId: String,
        studentId: String,
        latitude: Double,
        longitude: Double,
        userId: String
    ): Result<Unit>
    suspend fun getStudentLinkByUserId(userId: String): Result<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentUserLinkEntity?>
}