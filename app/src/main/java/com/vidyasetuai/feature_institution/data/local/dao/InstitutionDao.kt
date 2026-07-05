package com.vidyasetuai.feature_institution.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_institution.data.local.entity.*

@Dao
interface InstitutionDao {

    // ── Workspaces ──────────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspaces(workspaces: List<WorkspaceEntity>)
    
    @Query("SELECT * FROM workspaces")
    suspend fun getAllWorkspaces(): List<WorkspaceEntity>

    @Query("SELECT * FROM workspaces")
    suspend fun getWorkspaces(): List<WorkspaceEntity>

    @Query("SELECT * FROM workspaces")
    fun getWorkspacesFlow(): kotlinx.coroutines.flow.Flow<List<WorkspaceEntity>>
    
    @Query("SELECT * FROM workspaces WHERE is_active_now = 1 LIMIT 1")
    suspend fun getActiveWorkspace(): WorkspaceEntity?
    
    @Query("UPDATE workspaces SET is_active_now = 0")
    suspend fun clearActiveWorkspace()
    
    @Query("UPDATE workspaces SET is_active_now = 1 WHERE workspace_id = :workspaceId")
    suspend fun setActiveWorkspace(workspaceId: String)
    
    @Query("DELETE FROM workspaces")
    suspend fun clearWorkspaces()

    // ── Child Org Setup ──────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChildOrgSetup(setup: LocalChildOrgSetupEntity)
    
    @Query("SELECT * FROM local_child_org_setups WHERE organization_id = :orgId")
    suspend fun getChildOrgSetup(orgId: String): LocalChildOrgSetupEntity?

    @Query("SELECT * FROM local_child_org_setups LIMIT 1")
    suspend fun getActiveSession(): LocalChildOrgSetupEntity?
    
    @Query("DELETE FROM local_child_org_setups")
    suspend fun clearChildOrgSetups()

    // ── Parent Buses ─────────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentBuses(buses: List<LocalParentBusEntity>)
    
    @Query("SELECT * FROM local_parent_buses WHERE parent_organization_id = :parentOrgId")
    suspend fun getParentBuses(parentOrgId: String): List<LocalParentBusEntity>
    
    @Query("SELECT * FROM local_parent_buses WHERE id = :busId")
    suspend fun getBusById(busId: String): LocalParentBusEntity?
    
    @Query("DELETE FROM local_parent_buses")
    suspend fun clearParentBuses()

    // ── Bus Routes ───────────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusRoutes(routes: List<LocalBusRouteEntity>)
    
    @Query("SELECT * FROM local_bus_routes WHERE bus_id = :busId AND is_deleted = 0 AND is_active = 1 ORDER BY stop_order ASC")
    suspend fun getBusRoutes(busId: String): List<LocalBusRouteEntity>
    
    @Query("DELETE FROM local_bus_routes")
    suspend fun clearBusRoutes()

    // ── Students ─────────────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<LocalStudentEntity>)
    
    @Query("SELECT * FROM local_students WHERE organization_id = :orgId")
    suspend fun getStudents(orgId: String): List<LocalStudentEntity>
    
    @Query("SELECT * FROM local_students WHERE guardian_id = :guardianId")
    suspend fun getStudentsByGuardianId(guardianId: String): List<LocalStudentEntity>
    
    @Query("SELECT * FROM local_students WHERE id = :studentId")
    suspend fun getStudentById(studentId: String): LocalStudentEntity?
    
    @Query("SELECT * FROM local_students WHERE qr_token_hash = :qrHash LIMIT 1")
    suspend fun getStudentByQrHash(qrHash: String): LocalStudentEntity?
    
    @Query("SELECT * FROM local_students WHERE name LIKE '%' || :query || '%' OR sr_number LIKE '%' || :query || '%'")
    suspend fun searchStudentsOffline(query: String): List<LocalStudentEntity>
    
    @Query("SELECT * FROM local_students WHERE (name LIKE '%' || :query || '%' OR sr_number LIKE '%' || :query || '%') AND (:classFilter IS NULL OR class_name = :classFilter) AND (:sectionFilter IS NULL OR section_name = :sectionFilter)")
    suspend fun searchStudentsOfflineWithFilters(query: String, classFilter: String?, sectionFilter: String?): List<LocalStudentEntity>
    
    @Query("DELETE FROM local_students")
    suspend fun clearStudents()

    // ── Student User Link ────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentUserLink(link: LocalStudentUserLinkEntity)
    
    @Query("SELECT * FROM local_student_user_links WHERE user_id = :userId LIMIT 1")
    suspend fun getStudentUserLink(userId: String): LocalStudentUserLinkEntity?
    
    @Query("DELETE FROM local_student_user_links")
    suspend fun clearStudentUserLinks()

    // ── Student Additional Fees ──────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentAdditionalFees(fees: List<LocalStudentAdditionalFeeEntity>)
    
    @Query("SELECT * FROM local_student_additional_fees WHERE student_id = :studentId")
    suspend fun getStudentAdditionalFees(studentId: String): List<LocalStudentAdditionalFeeEntity>
    
    @Query("DELETE FROM local_student_additional_fees")
    suspend fun clearStudentAdditionalFees()

    // ── Student Fee Payments ─────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentFeePayments(payments: List<LocalStudentFeePaymentEntity>)
    
    @Query("SELECT * FROM local_student_fee_payments WHERE student_id IN (:studentIds)")
    suspend fun getStudentFeePayments(studentIds: List<String>): List<LocalStudentFeePaymentEntity>
    
    @Query("DELETE FROM local_student_fee_payments")
    suspend fun clearStudentFeePayments()

    // ── Parent Expenses ──────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentExpenses(expenses: List<LocalParentExpenseEntity>)
    
    @Query("SELECT * FROM local_parent_expenses WHERE parent_organization_id = :parentOrgId")
    suspend fun getParentExpenses(parentOrgId: String): List<LocalParentExpenseEntity>
    
    @Query("DELETE FROM local_parent_expenses")
    suspend fun clearParentExpenses()

    // ── Student Attendance ───────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentAttendance(attendance: List<LocalStudentAttendanceEntity>)
    
    @Query("SELECT * FROM local_student_attendance WHERE student_id IN (:studentIds)")
    suspend fun getStudentAttendance(studentIds: List<String>): List<LocalStudentAttendanceEntity>
    
    @Query("SELECT * FROM local_student_attendance WHERE organization_id = :orgId AND class_id = :classId AND section_id = :sectionId AND attendance_date = :date")
    suspend fun getStudentAttendanceForClass(orgId: String, classId: String, sectionId: String, date: String): List<LocalStudentAttendanceEntity>
    
    @Query("SELECT EXISTS(SELECT 1 FROM local_student_attendance WHERE organization_id = :orgId AND class_id = :classId AND section_id = :sectionId AND attendance_date = :date LIMIT 1)")
    suspend fun checkIfAttendanceMarked(orgId: String, classId: String, sectionId: String, date: String): Boolean
    
    @Query("SELECT * FROM local_student_attendance WHERE sync_state != 'SYNCED'")
    suspend fun getUnsyncedStudentAttendance(): List<LocalStudentAttendanceEntity>
    
    @Query("UPDATE local_student_attendance SET sync_state = 'SYNCED' WHERE id = :id")
    suspend fun markStudentAttendanceSynced(id: String)
    
    @Query("DELETE FROM local_student_attendance")
    suspend fun clearStudentAttendance()

    // ── Bus Trip ─────────────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentBusTrips(trips: List<LocalParentBusTripEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentBusTrip(trip: LocalParentBusTripEntity)
    
    @Query("SELECT * FROM local_parent_bus_trips WHERE driver_id = :driverId AND status = 'Ongoing' LIMIT 1")
    suspend fun getActiveBusTripForDriver(driverId: String): LocalParentBusTripEntity?
    
    @Query("SELECT * FROM local_parent_bus_trips WHERE driver_id = :driverId")
    suspend fun getParentBusTripsForDriver(driverId: String): List<LocalParentBusTripEntity>
    
    @Query("SELECT * FROM local_parent_bus_trips WHERE id = :tripId")
    suspend fun getBusTripById(tripId: String): LocalParentBusTripEntity?
    
    @Query("UPDATE local_parent_bus_trips SET status = :status, start_time = :startTime, end_time = :endTime, sync_state = :syncState WHERE id = :tripId")
    suspend fun updateBusTripStatus(tripId: String, status: String, startTime: String?, endTime: String?, syncState: String)
    
    @Query("DELETE FROM local_parent_bus_trips")
    suspend fun clearParentBusTrips()

    // ── Bus Trip Attendance Log ──────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentBusTripAttendanceLogs(logs: List<LocalParentBusTripAttendanceLogEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentBusTripAttendanceLog(log: LocalParentBusTripAttendanceLogEntity)
    
    @Query("SELECT * FROM local_parent_bus_trip_attendance_logs WHERE trip_id = :tripId")
    suspend fun getBusTripAttendanceLogs(tripId: String): List<LocalParentBusTripAttendanceLogEntity>
    
    @Query("SELECT * FROM local_parent_bus_trip_attendance_logs WHERE sync_state = 'PENDING_INSERT'")
    suspend fun getUnsyncedBusTripAttendanceLogs(): List<LocalParentBusTripAttendanceLogEntity>
    
    @Query("UPDATE local_parent_bus_trip_attendance_logs SET sync_state = 'SYNCED' WHERE id = :id")
    suspend fun markBusTripAttendanceLogSynced(id: String)
    
    @Query("""
        SELECT 
            l.student_id AS studentId,
            s.name AS studentName,
            s.class_name AS studentClassName,
            s.section_name AS studentSectionName,
            s.roll_number AS studentRollNumber,
            l.scanned_at AS scannedAt,
            l.status AS status
        FROM local_parent_bus_trip_attendance_logs l
        LEFT JOIN local_students s ON l.student_id = s.id
        WHERE l.trip_id = :tripId
    """)
    suspend fun getBusTripAttendanceLogsWithStudentInfo(tripId: String): List<LocalParentBusTripAttendanceLogWithStudentInfo>
    
    @Query("DELETE FROM local_parent_bus_trip_attendance_logs")
    suspend fun clearBusTripAttendanceLogs()

    // ── Leaves ───────────────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaves(leaves: List<LocalOrganizationLeaveEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeave(leave: LocalOrganizationLeaveEntity)
    
    @Query("SELECT * FROM local_organization_leaves WHERE staff_id = :staffId OR student_id = :studentId OR action_by = :userId")
    suspend fun getLeavesForUser(staffId: String?, studentId: String?, userId: String): List<LocalOrganizationLeaveEntity>
    
    @Query("SELECT * FROM local_organization_leaves")
    suspend fun getAllLeaves(): List<LocalOrganizationLeaveEntity>
    
    @Query("SELECT * FROM local_organization_leaves WHERE sync_state != 'SYNCED'")
    suspend fun getUnsyncedLeaves(): List<LocalOrganizationLeaveEntity>
    
    @Query("UPDATE local_organization_leaves SET sync_state = 'SYNCED' WHERE id = :id")
    suspend fun markLeaveSynced(id: String)
    
    @Query("DELETE FROM local_organization_leaves")
    suspend fun clearLeaves()

    // ── Remarks ──────────────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemarks(remarks: List<LocalOrganizationRemarkEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemark(remark: LocalOrganizationRemarkEntity)
    
    @Query("SELECT * FROM local_organization_remarks WHERE target_student_id = :studentId OR target_staff_id = :staffId OR target_user_id = :userId")
    suspend fun getRemarksForUser(studentId: String?, staffId: String?, userId: String): List<LocalOrganizationRemarkEntity>
    
    @Query("SELECT * FROM local_organization_remarks")
    suspend fun getAllRemarks(): List<LocalOrganizationRemarkEntity>
    
    @Query("SELECT * FROM local_organization_remarks WHERE sync_state != 'SYNCED'")
    suspend fun getUnsyncedRemarks(): List<LocalOrganizationRemarkEntity>
    
    @Query("UPDATE local_organization_remarks SET sync_state = 'SYNCED' WHERE id = :id")
    suspend fun markRemarkSynced(id: String)
    
    @Query("UPDATE local_organization_remarks SET is_deleted = 1, sync_state = 'PENDING_UPDATE' WHERE id = :id")
    suspend fun softDeleteRemark(id: String)
    
    @Query("DELETE FROM local_organization_remarks")
    suspend fun clearRemarks()

    // ── Calendar Events ──────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarEvents(events: List<LocalCalendarEventEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarEvent(event: LocalCalendarEventEntity)
    
    @Query("SELECT * FROM local_calendar_events WHERE parent_organization_id = :parentOrgId")
    suspend fun getCalendarEvents(parentOrgId: String): List<LocalCalendarEventEntity>
    
    @Query("DELETE FROM local_calendar_events")
    suspend fun clearCalendarEvents()

    // ── Staff Attendance ─────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffAttendance(attendance: List<LocalParentStaffAttendanceEntity>)
    
    @Query("SELECT * FROM local_parent_staff_attendance WHERE parent_organization_id = :parentOrgId AND attendance_date = :date")
    suspend fun getStaffAttendance(parentOrgId: String, date: String): List<LocalParentStaffAttendanceEntity>
    
    @Query("SELECT * FROM local_parent_staff_attendance WHERE sync_state != 'SYNCED'")
    suspend fun getUnsyncedStaffAttendance(): List<LocalParentStaffAttendanceEntity>
    
    @Query("UPDATE local_parent_staff_attendance SET sync_state = 'SYNCED' WHERE id = :id")
    suspend fun markStaffAttendanceSynced(id: String)
    
    @Query("DELETE FROM local_parent_staff_attendance")
    suspend fun clearStaffAttendance()

    // ── Staff Profile ────────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffProfiles(staffList: List<LocalParentStaffEntity>)
    
    @Query("SELECT * FROM local_parent_staff WHERE parent_organization_id = :parentOrgId")
    suspend fun getStaffProfiles(parentOrgId: String): List<LocalParentStaffEntity>
    
    @Query("SELECT * FROM local_parent_staff WHERE id = :staffId")
    suspend fun getStaffProfileById(staffId: String): LocalParentStaffEntity?
    
    @Query("DELETE FROM local_parent_staff")
    suspend fun clearStaff()

    // ── Exams ────────────────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<LocalOrganizationExamEntity>)
    
    @Query("SELECT * FROM local_organization_exams WHERE organization_id = :orgId AND active_session_id = :sessionId")
    suspend fun getExams(orgId: String, sessionId: String): List<LocalOrganizationExamEntity>
    
    @Query("DELETE FROM local_organization_exams")
    suspend fun clearExams()

    // ── Exam Subject Settings ────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamSubjectSettings(settings: List<LocalExamSubjectSettingEntity>)
    
    @Query("SELECT * FROM local_exam_subject_settings WHERE organization_id = :orgId AND active_session_id = :sessionId")
    suspend fun getExamSubjectSettings(orgId: String, sessionId: String): List<LocalExamSubjectSettingEntity>
    
    @Query("DELETE FROM local_exam_subject_settings")
    suspend fun clearExamSubjectSettings()

    // ── Student Exam Marks ───────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentExamMarks(marks: List<LocalStudentExamMarkEntity>)
    
    @Query("SELECT * FROM local_student_exam_marks WHERE exam_id = :examId AND class_id = :classId AND subject_id = :subjectId")
    suspend fun getStudentExamMarks(examId: String, classId: String, subjectId: String): List<LocalStudentExamMarkEntity>
    
    @Query("SELECT * FROM local_student_exam_marks WHERE student_id = :studentId")
    suspend fun getStudentExamMarksForStudent(studentId: String): List<LocalStudentExamMarkEntity>
    
    @Query("SELECT * FROM local_student_exam_marks WHERE sync_state != 'SYNCED'")
    suspend fun getUnsyncedStudentExamMarks(): List<LocalStudentExamMarkEntity>
    
    @Query("UPDATE local_student_exam_marks SET sync_state = 'SYNCED' WHERE id = :id")
    suspend fun markStudentExamMarkSynced(id: String)
    
    @Query("DELETE FROM local_student_exam_marks")
    suspend fun clearStudentExamMarks()

    // ── Database Replace Helper Transaction ───────────────────────────────────
    @Transaction
    suspend fun replaceWorkspaceDataPayload(
        setups: List<LocalChildOrgSetupEntity>,
        students: List<LocalStudentEntity>,
        fees: List<LocalStudentAdditionalFeeEntity>,
        payments: List<LocalStudentFeePaymentEntity>,
        attendance: List<LocalStudentAttendanceEntity>,
        buses: List<LocalParentBusEntity>,
        routes: List<LocalBusRouteEntity>,
        staff: List<LocalParentStaffEntity>,
        expenses: List<LocalParentExpenseEntity>,
        exams: List<LocalOrganizationExamEntity>,
        trips: List<LocalParentBusTripEntity>,
        tripLogs: List<LocalParentBusTripAttendanceLogEntity>,
        calendarEvents: List<LocalCalendarEventEntity>,
        examSettings: List<LocalExamSubjectSettingEntity>
    ) {
        clearChildOrgSetups()
        clearStudents()
        clearStudentAdditionalFees()
        clearStudentFeePayments()
        clearStudentAttendance()
        clearParentBuses()
        clearBusRoutes()
        clearStaff()
        clearParentExpenses()
        clearExams()
        clearParentBusTrips()
        clearBusTripAttendanceLogs()
        clearCalendarEvents()
        clearExamSubjectSettings()

        if (setups.isNotEmpty()) {
            setups.forEach { insertChildOrgSetup(it) }
        }
        if (students.isNotEmpty()) insertStudents(students)
        if (fees.isNotEmpty()) insertStudentAdditionalFees(fees)
        if (payments.isNotEmpty()) insertStudentFeePayments(payments)
        if (attendance.isNotEmpty()) insertStudentAttendance(attendance)
        if (buses.isNotEmpty()) insertParentBuses(buses)
        if (routes.isNotEmpty()) insertBusRoutes(routes)
        if (staff.isNotEmpty()) insertStaffProfiles(staff)
        if (expenses.isNotEmpty()) insertParentExpenses(expenses)
        if (exams.isNotEmpty()) insertExams(exams)
        if (trips.isNotEmpty()) insertParentBusTrips(trips)
        if (tripLogs.isNotEmpty()) insertParentBusTripAttendanceLogs(tripLogs)
        if (calendarEvents.isNotEmpty()) insertCalendarEvents(calendarEvents)
        if (examSettings.isNotEmpty()) insertExamSubjectSettings(examSettings)
    }


    // ── Database Clear Helper Transaction ─────────────────────────────────────
    @Transaction
    suspend fun clearAllInstitutionData() {
        clearWorkspaces()
        clearChildOrgSetups()
        clearParentBuses()
        clearStudents()
        clearStudentAdditionalFees()
        clearStudentFeePayments()
        clearParentExpenses()
        clearStudentAttendance()
        clearBusTripAttendanceLogs()
        clearParentBusTrips()
        clearLeaves()
        clearRemarks()
        clearCalendarEvents()
        clearStaffAttendance()
        clearStaff()
        clearStudentUserLinks()
        clearExams()
        clearExamSubjectSettings()
        clearStudentExamMarks()
    }
}
