package com.vidyasetuai.feature_institution.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_institution.data.local.entity.*

@Dao
interface InstitutionDao {
    @Query("SELECT * FROM workspaces")
    suspend fun getWorkspaces(): List<WorkspaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspaces(workspaces: List<WorkspaceEntity>)

    @Query("UPDATE workspaces SET isActive = 0")
    suspend fun clearActiveWorkspaces()

    @Query("UPDATE workspaces SET isActive = 1 WHERE id = :workspaceId")
    suspend fun setActiveWorkspace(workspaceId: String)

    @Query("DELETE FROM workspaces")
    suspend fun clearAll()

    // --- Offline Student Search DAOs ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<LocalStudentEntity>)

    @Query("""
        SELECT * FROM local_students 
        WHERE (name LIKE :query OR srNumber LIKE :query OR guardianMobile LIKE :query OR guardianName LIKE :query)
          AND (:className IS NULL OR className = :className)
          AND (:sectionName IS NULL OR sectionName = :sectionName)
    """)
    suspend fun searchStudentsWithFilters(
        query: String,
        className: String?,
        sectionName: String?
    ): List<LocalStudentEntity>

    @Query("DELETE FROM local_students")
    suspend fun clearStudents()

    // --- Offline Cache (Key-Value) DAOs ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: OfflineCacheEntity)

    @Query("SELECT * FROM offline_cache WHERE cacheKey = :cacheKey")
    suspend fun getCache(cacheKey: String): OfflineCacheEntity?

    @Query("DELETE FROM offline_cache WHERE cacheKey = :cacheKey")
    suspend fun clearCache(cacheKey: String)

    @Query("DELETE FROM offline_cache")
    suspend fun clearAllCache()

    // --- Leaves ---
    @Query("SELECT * FROM local_leaves WHERE cachedForUserId = :userId")
    suspend fun getLeavesForUser(userId: String): List<LocalLeaveEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaves(leaves: List<LocalLeaveEntity>)

    @Query("DELETE FROM local_leaves WHERE cachedForUserId = :userId")
    suspend fun deleteLeavesForUser(userId: String)

    // --- Fee Payments ---
    @Query("SELECT * FROM local_fee_payments WHERE studentId IN (:studentIds)")
    suspend fun getFeePaymentsForStudents(studentIds: List<String>): List<LocalFeePaymentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeePayments(payments: List<LocalFeePaymentEntity>)

    @Query("DELETE FROM local_fee_payments WHERE studentId IN (:studentIds)")
    suspend fun deleteFeePaymentsForStudents(studentIds: List<String>)

    // --- Student Attendance ---
    @Query("SELECT * FROM local_student_attendance WHERE studentId IN (:studentIds)")
    suspend fun getStudentAttendanceForStudents(studentIds: List<String>): List<LocalStudentAttendanceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentAttendance(attendance: List<LocalStudentAttendanceEntity>)

    @Query("DELETE FROM local_student_attendance WHERE studentId IN (:studentIds)")
    suspend fun deleteStudentAttendanceForStudents(studentIds: List<String>)

    // --- Bus Assignments ---
    @Query("SELECT * FROM local_student_bus_assignments WHERE studentId IN (:studentIds)")
    suspend fun getStudentBusAssignmentsForStudents(studentIds: List<String>): List<LocalStudentBusAssignmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentBusAssignments(assignments: List<LocalStudentBusAssignmentEntity>)

    @Query("DELETE FROM local_student_bus_assignments WHERE studentId IN (:studentIds)")
    suspend fun deleteStudentBusAssignmentsForStudents(studentIds: List<String>)

    // --- Staff Salary Details ---
    @Query("SELECT * FROM local_staff_salary_details WHERE userId = :userId AND parentOrgId = :parentOrgId")
    suspend fun getStaffSalaryDetails(userId: String, parentOrgId: String): LocalStaffSalaryDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffSalaryDetails(details: LocalStaffSalaryDetailsEntity)

    // --- Staff Salary Payments ---
    @Query("SELECT * FROM local_staff_salary_payments WHERE staffUserId = :userId AND parentOrgId = :parentOrgId")
    suspend fun getStaffSalaryPayments(userId: String, parentOrgId: String): List<LocalStaffSalaryPaymentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffSalaryPayments(payments: List<LocalStaffSalaryPaymentEntity>)

    @Query("DELETE FROM local_staff_salary_payments WHERE staffUserId = :userId AND parentOrgId = :parentOrgId")
    suspend fun deleteStaffSalaryPayments(userId: String, parentOrgId: String)

    // --- Content Feed ---
    @Query("SELECT * FROM local_content_feed_items WHERE organizationId = :orgId AND sessionId = :sessionId")
    suspend fun getContentFeedItems(orgId: String, sessionId: String): List<LocalContentFeedItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContentFeedItems(items: List<LocalContentFeedItemEntity>)

    @Query("DELETE FROM local_content_feed_items WHERE organizationId = :orgId AND sessionId = :sessionId")
    suspend fun deleteContentFeedItems(orgId: String, sessionId: String)

    // --- Guardian Students ---
    @Query("SELECT * FROM local_guardian_students WHERE guardianLinkId = :guardianLinkId")
    suspend fun getGuardianStudents(guardianLinkId: String): List<LocalGuardianStudentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuardianStudents(students: List<LocalGuardianStudentEntity>)

    @Query("DELETE FROM local_guardian_students WHERE guardianLinkId = :guardianLinkId")
    suspend fun deleteGuardianStudents(guardianLinkId: String)

    // --- Driver Bus Details ---
    @Query("SELECT * FROM local_driver_bus_details WHERE workspaceId = :workspaceId")
    suspend fun getDriverBusDetails(workspaceId: String): LocalDriverBusDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriverBusDetails(details: LocalDriverBusDetailsEntity)

    // --- Active Session ---
    @Query("SELECT * FROM local_active_session LIMIT 1")
    suspend fun getActiveSession(): LocalActiveSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActiveSession(session: LocalActiveSessionEntity)

    @Query("DELETE FROM local_active_session")
    suspend fun deleteActiveSession()

    // --- Child Orgs ---
    @Query("SELECT * FROM local_child_orgs WHERE parentOrgId = :parentOrgId")
    suspend fun getChildOrgs(parentOrgId: String): List<LocalChildOrgEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChildOrgs(orgs: List<LocalChildOrgEntity>)

    // --- Org Classes ---
    @Query("SELECT * FROM local_org_classes WHERE organizationId = :orgId")
    suspend fun getOrgClasses(orgId: String): List<LocalOrgClassEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrgClasses(classes: List<LocalOrgClassEntity>)

    // --- Org Sections ---
    @Query("SELECT * FROM local_org_sections WHERE classId = :classId")
    suspend fun getOrgSections(classId: String): List<LocalOrgSectionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrgSections(sections: List<LocalOrgSectionEntity>)

    // --- Pending Approvals ---
    @Query("SELECT * FROM local_pending_approvals WHERE userId = :userId")
    suspend fun getPendingApprovals(userId: String): List<LocalPendingApprovalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingApprovals(approvals: List<LocalPendingApprovalEntity>)

    @Query("DELETE FROM local_pending_approvals WHERE userId = :userId")
    suspend fun deletePendingApprovals(userId: String)

    // --- Student Image Vectors ---
    @Query("SELECT * FROM local_student_image_vectors WHERE organizationId IN (:orgIds) AND isDeleted = 0")
    suspend fun getStudentImageVectorsForOrgs(orgIds: List<String>): List<LocalStudentImageVectorEntity>

    @Query("SELECT * FROM local_student_image_vectors WHERE studentId = :studentId")
    suspend fun getStudentImageVectorsForStudent(studentId: String): List<LocalStudentImageVectorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentImageVectors(vectors: List<LocalStudentImageVectorEntity>)

    @Query("DELETE FROM local_student_image_vectors WHERE organizationId IN (:orgIds)")
    suspend fun deleteStudentImageVectorsForOrgs(orgIds: List<String>)

    @Query("DELETE FROM local_student_image_vectors WHERE studentId = :studentId")
    suspend fun deleteStudentImageVectorsByStudent(studentId: String)

    @Query("SELECT * FROM local_students WHERE id IN (:studentIds)")
    suspend fun getStudentsByIds(studentIds: List<String>): List<LocalStudentEntity>

    // --- Parent Bus Trip & Student ID Attendance DAOs ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentQrIdentities(identities: List<LocalStudentQrIdentityEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentIdCards(cards: List<LocalStudentIdCardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentBusTrips(trips: List<LocalParentBusTripEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentBusTripAttendanceLogs(logs: List<LocalParentBusTripAttendanceLogEntity>)

    @Query("SELECT * FROM local_student_qr_identities WHERE qrTokenHash = :hash AND isActive = 1 AND isDeleted = 0 LIMIT 1")
    suspend fun getQrIdentityByHash(hash: String): LocalStudentQrIdentityEntity?

    @Query("SELECT * FROM local_student_qr_identities WHERE studentId = :studentId AND isActive = 1 AND isDeleted = 0 LIMIT 1")
    suspend fun getQrIdentityByStudent(studentId: String): LocalStudentQrIdentityEntity?

    @Query("SELECT * FROM local_student_id_cards WHERE studentId = :studentId AND isActive = 1 AND isDeleted = 0 LIMIT 1")
    suspend fun getStudentIdCardByStudent(studentId: String): LocalStudentIdCardEntity?

    @Query("SELECT * FROM local_parent_bus_trips WHERE driverId = :driverId AND status = :status AND isDeleted = 0 LIMIT 1")
    suspend fun getActiveBusTripForDriver(driverId: String, status: String = "Ongoing"): LocalParentBusTripEntity?

    @Query("SELECT * FROM local_parent_bus_trips WHERE driverId = :driverId AND isDeleted = 0 ORDER BY createdAt DESC")
    suspend fun getParentBusTripsForDriver(driverId: String): List<LocalParentBusTripEntity>

    @Query("SELECT * FROM local_parent_bus_trips WHERE id = :id LIMIT 1")
    suspend fun getParentBusTripById(id: String): LocalParentBusTripEntity?

    @Query("UPDATE local_parent_bus_trips SET status = :status, endTime = :endTime, updatedAt = :updatedAt WHERE id = :tripId")
    suspend fun updateParentBusTripStatus(tripId: String, status: String, endTime: String?, updatedAt: String)

    @Query("SELECT * FROM local_parent_bus_trip_attendance_logs WHERE tripId = :tripId AND isDeleted = 0 ORDER BY scannedAt DESC")
    suspend fun getAttendanceLogsForTrip(tripId: String): List<LocalParentBusTripAttendanceLogEntity>

    @Query("""
        SELECT l.*, s.name as studentName, s.className as studentClassName, s.sectionName as studentSectionName, s.rollNumber as studentRollNumber
        FROM local_parent_bus_trip_attendance_logs l
        LEFT JOIN local_students s ON l.studentId = s.id
        WHERE l.tripId = :tripId AND l.isDeleted = 0
        ORDER BY l.scannedAt DESC
    """)
    suspend fun getAttendanceLogsForTripWithStudentInfo(tripId: String): List<LocalParentBusTripAttendanceLogWithStudentInfo>

    @Query("SELECT * FROM local_parent_bus_trip_attendance_logs WHERE syncStatus = 'Offline_Pending' AND isDeleted = 0 ORDER BY scannedAt ASC")
    suspend fun getPendingSyncAttendanceLogs(): List<LocalParentBusTripAttendanceLogEntity>

    @Query("UPDATE local_parent_bus_trip_attendance_logs SET syncStatus = :syncStatus, updatedAt = :updatedAt WHERE id = :logId")
    suspend fun updateAttendanceLogSyncStatus(logId: String, syncStatus: String, updatedAt: String)

    @Query("UPDATE local_parent_bus_trip_attendance_logs SET isDeleted = 1, syncStatus = 'Failed_Permanent', updatedAt = :updatedAt WHERE id = :logId")
    suspend fun markAttendanceLogAsBadAndDelete(logId: String, updatedAt: String)

    @Query("""
        SELECT s.* FROM local_students s
        INNER JOIN local_student_bus_assignments a ON s.id = a.studentId
        WHERE a.busId = :busId
    """)
    suspend fun getStudentsAssignedToBus(busId: String): List<LocalStudentEntity>

    // --- Remarks, Targets & Roles DAOs ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemarks(remarks: List<LocalRemarkEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemarkTargets(targets: List<LocalRemarkTargetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalStaffRoles(roles: List<LocalGlobalStaffRoleEntity>)

    @Query("SELECT * FROM local_remarks WHERE activeSessionId = :sessionId AND isDeleted = 0 ORDER BY createdAt DESC")
    suspend fun getRemarksForActiveSession(sessionId: String): List<LocalRemarkEntity>

    @Query("SELECT * FROM local_remark_targets WHERE remarkId = :remarkId AND isDeleted = 0")
    suspend fun getRemarkTargetsForRemark(remarkId: String): List<LocalRemarkTargetEntity>

    @Query("SELECT * FROM local_remarks WHERE syncStatus = 'Offline_Pending' AND isDeleted = 0")
    suspend fun getPendingSyncRemarks(): List<LocalRemarkEntity>

    @Query("SELECT * FROM local_remark_targets WHERE syncStatus = 'Offline_Pending' AND isDeleted = 0")
    suspend fun getPendingSyncRemarkTargets(): List<LocalRemarkTargetEntity>

    @Query("UPDATE local_remarks SET syncStatus = :syncStatus, updatedAt = :updatedAt WHERE id = :remarkId")
    suspend fun updateRemarkSyncStatus(remarkId: String, syncStatus: String, updatedAt: String)

    @Query("UPDATE local_remark_targets SET syncStatus = :syncStatus, updatedAt = :updatedAt WHERE id = :targetId")
    suspend fun updateRemarkTargetSyncStatus(targetId: String, syncStatus: String, updatedAt: String)

    @Query("UPDATE local_remarks SET isDeleted = 1, syncStatus = 'Offline_Pending', updatedAt = :updatedAt WHERE id = :remarkId")
    suspend fun softDeleteRemark(remarkId: String, updatedAt: String)

    @Query("UPDATE local_remark_targets SET isDeleted = 1, syncStatus = 'Offline_Pending', updatedAt = :updatedAt WHERE remarkId = :remarkId")
    suspend fun softDeleteRemarkTargets(remarkId: String, updatedAt: String)

    @Query("SELECT * FROM local_global_staff_roles WHERE isActive = 1 AND isDeleted = 0")
    suspend fun getGlobalStaffRoles(): List<LocalGlobalStaffRoleEntity>

    // ═══════════════════════════════════════════════════════════════════════
    // ── Global Entities DAOs ────────────────────────────────────────────────
    // ═══════════════════════════════════════════════════════════════════════

    // --- Global Attendance Status ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalAttendanceStatuses(items: List<LocalGlobalAttendanceStatusEntity>)

    @Query("SELECT * FROM local_global_attendance_status WHERE isActive = 1")
    suspend fun getGlobalAttendanceStatuses(): List<LocalGlobalAttendanceStatusEntity>

    @Query("DELETE FROM local_global_attendance_status")
    suspend fun clearGlobalAttendanceStatuses()

    // --- Global Blood Groups ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalBloodGroups(items: List<LocalGlobalBloodGroupEntity>)

    @Query("SELECT * FROM local_global_blood_groups WHERE isActive = 1")
    suspend fun getGlobalBloodGroups(): List<LocalGlobalBloodGroupEntity>

    @Query("DELETE FROM local_global_blood_groups")
    suspend fun clearGlobalBloodGroups()

    // --- Global Boards ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalBoards(items: List<LocalGlobalBoardEntity>)

    @Query("SELECT * FROM local_global_boards WHERE isActive = 1")
    suspend fun getGlobalBoards(): List<LocalGlobalBoardEntity>

    @Query("DELETE FROM local_global_boards")
    suspend fun clearGlobalBoards()

    // --- Global Classes ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalClasses(items: List<LocalGlobalClassEntity>)

    @Query("SELECT * FROM local_global_classes WHERE isActive = 1 ORDER BY levelOrder ASC")
    suspend fun getGlobalClasses(): List<LocalGlobalClassEntity>

    @Query("DELETE FROM local_global_classes")
    suspend fun clearGlobalClasses()

    // --- Global Exam Subject Rules ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalExamSubjectRules(items: List<LocalGlobalExamSubjectRuleEntity>)

    @Query("SELECT * FROM local_global_exam_subject_rules WHERE isActive = 1")
    suspend fun getGlobalExamSubjectRules(): List<LocalGlobalExamSubjectRuleEntity>

    @Query("SELECT * FROM local_global_exam_subject_rules WHERE classId = :classId AND isActive = 1")
    suspend fun getGlobalExamSubjectRulesByClass(classId: String): List<LocalGlobalExamSubjectRuleEntity>

    @Query("DELETE FROM local_global_exam_subject_rules")
    suspend fun clearGlobalExamSubjectRules()

    // --- Global Exam Types ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalExamTypes(items: List<LocalGlobalExamTypeEntity>)

    @Query("SELECT * FROM local_global_exam_types WHERE isActive = 1")
    suspend fun getGlobalExamTypes(): List<LocalGlobalExamTypeEntity>

    @Query("DELETE FROM local_global_exam_types")
    suspend fun clearGlobalExamTypes()

    // --- Global Expense Types ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalExpenseTypes(items: List<LocalGlobalExpenseTypeEntity>)

    @Query("SELECT * FROM local_global_expense_types WHERE isActive = 1")
    suspend fun getGlobalExpenseTypes(): List<LocalGlobalExpenseTypeEntity>

    @Query("DELETE FROM local_global_expense_types")
    suspend fun clearGlobalExpenseTypes()

    // --- Global Facilities ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalFacilities(items: List<LocalGlobalFacilityEntity>)

    @Query("SELECT * FROM local_global_facilities WHERE isActive = 1")
    suspend fun getGlobalFacilities(): List<LocalGlobalFacilityEntity>

    @Query("DELETE FROM local_global_facilities")
    suspend fun clearGlobalFacilities()

    // --- Global Languages ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalLanguages(items: List<LocalGlobalLanguageEntity>)

    @Query("SELECT * FROM local_global_languages WHERE isActive = 1")
    suspend fun getGlobalLanguages(): List<LocalGlobalLanguageEntity>

    @Query("DELETE FROM local_global_languages")
    suspend fun clearGlobalLanguages()

    // --- Global Mediums ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalMediums(items: List<LocalGlobalMediumEntity>)

    @Query("SELECT * FROM local_global_mediums WHERE isActive = 1")
    suspend fun getGlobalMediums(): List<LocalGlobalMediumEntity>

    @Query("SELECT * FROM local_global_mediums WHERE boardId = :boardId AND isActive = 1")
    suspend fun getGlobalMediumsByBoard(boardId: String): List<LocalGlobalMediumEntity>

    @Query("DELETE FROM local_global_mediums")
    suspend fun clearGlobalMediums()

    // --- Global Org Parent Types ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalOrgParentTypes(items: List<LocalGlobalOrgParentTypeEntity>)

    @Query("SELECT * FROM local_global_org_parent_types WHERE isActive = 1")
    suspend fun getGlobalOrgParentTypes(): List<LocalGlobalOrgParentTypeEntity>

    @Query("DELETE FROM local_global_org_parent_types")
    suspend fun clearGlobalOrgParentTypes()

    // --- Global Org Types ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalOrgTypes(items: List<LocalGlobalOrgTypeEntity>)

    @Query("SELECT * FROM local_global_org_types WHERE isActive = 1")
    suspend fun getGlobalOrgTypes(): List<LocalGlobalOrgTypeEntity>

    @Query("DELETE FROM local_global_org_types")
    suspend fun clearGlobalOrgTypes()

    // --- Global Relationship Types ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalRelationshipTypes(items: List<LocalGlobalRelationshipTypeEntity>)

    @Query("SELECT * FROM local_global_relationship_types WHERE isActive = 1")
    suspend fun getGlobalRelationshipTypes(): List<LocalGlobalRelationshipTypeEntity>

    @Query("DELETE FROM local_global_relationship_types")
    suspend fun clearGlobalRelationshipTypes()

    // --- Global Result Status ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalResultStatuses(items: List<LocalGlobalResultStatusEntity>)

    @Query("SELECT * FROM local_global_result_status WHERE isActive = 1")
    suspend fun getGlobalResultStatuses(): List<LocalGlobalResultStatusEntity>

    @Query("DELETE FROM local_global_result_status")
    suspend fun clearGlobalResultStatuses()

    // --- Global Student Categories ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalStudentCategories(items: List<LocalGlobalStudentCategoryEntity>)

    @Query("SELECT * FROM local_global_student_categories WHERE isActive = 1")
    suspend fun getGlobalStudentCategories(): List<LocalGlobalStudentCategoryEntity>

    @Query("DELETE FROM local_global_student_categories")
    suspend fun clearGlobalStudentCategories()

    // --- Global Student Status ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalStudentStatuses(items: List<LocalGlobalStudentStatusEntity>)

    @Query("SELECT * FROM local_global_student_status WHERE isActive = 1")
    suspend fun getGlobalStudentStatuses(): List<LocalGlobalStudentStatusEntity>

    @Query("DELETE FROM local_global_student_status")
    suspend fun clearGlobalStudentStatuses()

    // --- Global Subjects ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalSubjects(items: List<LocalGlobalSubjectEntity>)

    @Query("SELECT * FROM local_global_subjects WHERE isActive = 1")
    suspend fun getGlobalSubjects(): List<LocalGlobalSubjectEntity>

    @Query("DELETE FROM local_global_subjects")
    suspend fun clearGlobalSubjects()

    // ═══════════════════════════════════════════════════════════════════════
    // ── Org Core Entities DAOs ──────────────────────────────────────────────
    // ═══════════════════════════════════════════════════════════════════════

    // --- Org Attendance Status ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrgAttendanceStatuses(items: List<LocalOrgAttendanceStatusEntity>)

    @Query("SELECT * FROM local_org_attendance_status WHERE organizationId = :orgId AND isActive = 1")
    suspend fun getOrgAttendanceStatuses(orgId: String): List<LocalOrgAttendanceStatusEntity>

    @Query("DELETE FROM local_org_attendance_status WHERE organizationId = :orgId")
    suspend fun clearOrgAttendanceStatuses(orgId: String)

    // --- Org Boards ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrgBoards(items: List<LocalOrgBoardEntity>)

    @Query("SELECT * FROM local_org_boards WHERE organizationId = :orgId AND isActive = 1 AND isDeleted = 0")
    suspend fun getOrgBoards(orgId: String): List<LocalOrgBoardEntity>

    @Query("DELETE FROM local_org_boards WHERE organizationId = :orgId")
    suspend fun clearOrgBoards(orgId: String)

    // --- Bus Child Assignments ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusChildAssignments(items: List<LocalBusChildAssignmentEntity>)

    @Query("SELECT * FROM local_bus_child_assignments WHERE organizationId = :orgId AND isActive = 1 AND isDeleted = 0")
    suspend fun getBusChildAssignmentsByOrg(orgId: String): List<LocalBusChildAssignmentEntity>

    @Query("DELETE FROM local_bus_child_assignments WHERE organizationId = :orgId")
    suspend fun clearBusChildAssignments(orgId: String)

    // --- Org Exams ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrgExams(items: List<LocalOrgExamEntity>)

    @Query("SELECT * FROM local_org_exams WHERE organizationId = :orgId AND activeSessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getOrgExams(orgId: String, sessionId: String): List<LocalOrgExamEntity>

    @Query("DELETE FROM local_org_exams WHERE organizationId = :orgId AND activeSessionId = :sessionId")
    suspend fun clearOrgExams(orgId: String, sessionId: String)

    // --- Fee Assignments ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeAssignments(items: List<LocalFeeAssignmentEntity>)

    @Query("SELECT * FROM local_fee_assignments WHERE organizationId = :orgId AND sessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getFeeAssignments(orgId: String, sessionId: String): List<LocalFeeAssignmentEntity>

    @Query("DELETE FROM local_fee_assignments WHERE organizationId = :orgId AND sessionId = :sessionId")
    suspend fun clearFeeAssignments(orgId: String, sessionId: String)

    // --- Guardian User Links ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuardianUserLinks(items: List<LocalGuardianUserLinkEntity>)

    @Query("SELECT * FROM local_guardian_user_links WHERE organizationId = :orgId AND isActive = 1 AND isDeleted = 0")
    suspend fun getGuardianUserLinks(orgId: String): List<LocalGuardianUserLinkEntity>

    @Query("DELETE FROM local_guardian_user_links WHERE organizationId = :orgId")
    suspend fun clearGuardianUserLinks(orgId: String)

    // --- Holidays ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolidays(items: List<LocalHolidayEntity>)

    @Query("SELECT * FROM local_holidays WHERE organizationId = :orgId AND activeSessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getHolidays(orgId: String, sessionId: String): List<LocalHolidayEntity>

    @Query("DELETE FROM local_holidays WHERE organizationId = :orgId AND activeSessionId = :sessionId")
    suspend fun clearHolidays(orgId: String, sessionId: String)

    // --- Org Languages ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrgLanguages(items: List<LocalOrgLanguageEntity>)

    @Query("SELECT * FROM local_org_languages WHERE organizationId = :orgId AND isActive = 1 AND isDeleted = 0")
    suspend fun getOrgLanguages(orgId: String): List<LocalOrgLanguageEntity>

    @Query("DELETE FROM local_org_languages WHERE organizationId = :orgId")
    suspend fun clearOrgLanguages(orgId: String)

    // --- Org Mediums ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrgMediums(items: List<LocalOrgMediumEntity>)

    @Query("SELECT * FROM local_org_mediums WHERE organizationId = :orgId AND isActive = 1 AND isDeleted = 0")
    suspend fun getOrgMediums(orgId: String): List<LocalOrgMediumEntity>

    @Query("DELETE FROM local_org_mediums WHERE organizationId = :orgId")
    suspend fun clearOrgMediums(orgId: String)

    // --- Parent Buses ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentBuses(items: List<LocalParentBusEntity>)

    @Query("SELECT * FROM local_parent_buses WHERE parentOrganizationId = :parentOrgId AND isActive = 1 AND isDeleted = 0")
    suspend fun getParentBuses(parentOrgId: String): List<LocalParentBusEntity>

    @Query("SELECT * FROM local_parent_buses WHERE id = :busId LIMIT 1")
    suspend fun getParentBusById(busId: String): LocalParentBusEntity?

    @Query("DELETE FROM local_parent_buses WHERE parentOrganizationId = :parentOrgId")
    suspend fun clearParentBuses(parentOrgId: String)

    // --- Parent Expenses ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentExpenses(items: List<LocalParentExpenseEntity>)

    @Query("SELECT * FROM local_parent_expenses WHERE parentOrganizationId = :parentOrgId AND activeSessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getParentExpenses(parentOrgId: String, sessionId: String): List<LocalParentExpenseEntity>

    @Query("DELETE FROM local_parent_expenses WHERE parentOrganizationId = :parentOrgId AND activeSessionId = :sessionId")
    suspend fun clearParentExpenses(parentOrgId: String, sessionId: String)

    // --- Parent Organizations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentOrganizations(items: List<LocalParentOrganizationEntity>)

    @Query("SELECT * FROM local_parent_organizations WHERE isActive = 1 AND isDeleted = 0")
    suspend fun getParentOrganizations(): List<LocalParentOrganizationEntity>

    @Query("SELECT * FROM local_parent_organizations WHERE id = :id LIMIT 1")
    suspend fun getParentOrganizationById(id: String): LocalParentOrganizationEntity?

    @Query("DELETE FROM local_parent_organizations")
    suspend fun clearParentOrganizations()

    // --- Parent Org Profiles ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentOrgProfiles(items: List<LocalParentOrgProfileEntity>)

    @Query("SELECT * FROM local_parent_org_profiles WHERE parentOrganizationId = :parentOrgId AND isActive = 1 LIMIT 1")
    suspend fun getParentOrgProfile(parentOrgId: String): LocalParentOrgProfileEntity?

    @Query("DELETE FROM local_parent_org_profiles WHERE parentOrganizationId = :parentOrgId")
    suspend fun clearParentOrgProfiles(parentOrgId: String)

    // --- Parent Org Users ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentOrgUsers(items: List<LocalParentOrgUserEntity>)

    @Query("SELECT * FROM local_parent_org_users WHERE parentOrganizationId = :parentOrgId AND isActive = 1 AND isDeleted = 0")
    suspend fun getParentOrgUsers(parentOrgId: String): List<LocalParentOrgUserEntity>

    @Query("DELETE FROM local_parent_org_users WHERE parentOrganizationId = :parentOrgId")
    suspend fun clearParentOrgUsers(parentOrgId: String)

    // --- Org Periods ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrgPeriods(items: List<LocalOrgPeriodEntity>)

    @Query("SELECT * FROM local_org_periods WHERE organizationId = :orgId AND isActive = 1 AND isDeleted = 0")
    suspend fun getOrgPeriods(orgId: String): List<LocalOrgPeriodEntity>

    @Query("DELETE FROM local_org_periods WHERE organizationId = :orgId")
    suspend fun clearOrgPeriods(orgId: String)

    // --- Org Profiles ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrgProfiles(items: List<LocalOrgProfileEntity>)

    @Query("SELECT * FROM local_org_profiles WHERE organizationId = :orgId AND isActive = 1 LIMIT 1")
    suspend fun getOrgProfile(orgId: String): LocalOrgProfileEntity?

    @Query("DELETE FROM local_org_profiles WHERE organizationId = :orgId")
    suspend fun clearOrgProfiles(orgId: String)

    // --- Org Users ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrgUsers(items: List<LocalOrgUserEntity>)

    @Query("SELECT * FROM local_org_users WHERE organizationId = :orgId AND isActive = 1 AND isDeleted = 0")
    suspend fun getOrgUsers(orgId: String): List<LocalOrgUserEntity>

    @Query("DELETE FROM local_org_users WHERE organizationId = :orgId")
    suspend fun clearOrgUsers(orgId: String)

    // ═══════════════════════════════════════════════════════════════════════
    // ── Student Entities DAOs ───────────────────────────────────────────────
    // ═══════════════════════════════════════════════════════════════════════

    // --- Student Enrollments ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentEnrollments(items: List<LocalStudentEnrollmentEntity>)

    @Query("SELECT * FROM local_student_enrollments WHERE organizationId = :orgId AND activeSessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getStudentEnrollments(orgId: String, sessionId: String): List<LocalStudentEnrollmentEntity>

    @Query("SELECT * FROM local_student_enrollments WHERE studentId = :studentId AND isActive = 1 AND isDeleted = 0")
    suspend fun getEnrollmentsForStudent(studentId: String): List<LocalStudentEnrollmentEntity>

    @Query("DELETE FROM local_student_enrollments WHERE organizationId = :orgId AND activeSessionId = :sessionId")
    suspend fun clearStudentEnrollments(orgId: String, sessionId: String)

    // --- Guardians ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuardians(items: List<LocalGuardianEntity>)

    @Query("SELECT * FROM local_guardians WHERE organizationId = :orgId AND isActive = 1 AND isDeleted = 0")
    suspend fun getGuardians(orgId: String): List<LocalGuardianEntity>

    @Query("SELECT * FROM local_guardians WHERE id = :id LIMIT 1")
    suspend fun getGuardianById(id: String): LocalGuardianEntity?

    @Query("DELETE FROM local_guardians WHERE organizationId = :orgId")
    suspend fun clearGuardians(orgId: String)

    // --- Student Additional Details ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentAdditionalDetails(items: List<LocalStudentAdditionalDetailsEntity>)

    @Query("SELECT * FROM local_student_additional_details WHERE studentId = :studentId AND isActive = 1 AND isDeleted = 0 LIMIT 1")
    suspend fun getStudentAdditionalDetails(studentId: String): LocalStudentAdditionalDetailsEntity?

    @Query("DELETE FROM local_student_additional_details WHERE organizationId = :orgId")
    suspend fun clearStudentAdditionalDetails(orgId: String)

    // --- Student Additional Fees ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentAdditionalFees(items: List<LocalStudentAdditionalFeeEntity>)

    @Query("SELECT * FROM local_student_additional_fees WHERE studentId = :studentId AND isActive = 1 AND isDeleted = 0")
    suspend fun getStudentAdditionalFees(studentId: String): List<LocalStudentAdditionalFeeEntity>

    @Query("DELETE FROM local_student_additional_fees WHERE organizationId = :orgId AND sessionId = :sessionId")
    suspend fun clearStudentAdditionalFees(orgId: String, sessionId: String)

    // --- Student Exam Marks ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentExamMarks(items: List<LocalStudentExamMarkEntity>)

    @Query("SELECT * FROM local_student_exam_marks WHERE studentId = :studentId AND activeSessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getStudentExamMarks(studentId: String, sessionId: String): List<LocalStudentExamMarkEntity>

    @Query("DELETE FROM local_student_exam_marks WHERE organizationId = :orgId AND activeSessionId = :sessionId")
    suspend fun clearStudentExamMarks(orgId: String, sessionId: String)

    // --- Student Homeworks ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentHomeworks(items: List<LocalStudentHomeworkEntity>)

    @Query("SELECT * FROM local_student_homeworks WHERE studentId = :studentId AND activeSessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getStudentHomeworks(studentId: String, sessionId: String): List<LocalStudentHomeworkEntity>

    @Query("DELETE FROM local_student_homeworks WHERE organizationId = :orgId AND activeSessionId = :sessionId")
    suspend fun clearStudentHomeworks(orgId: String, sessionId: String)

    // --- Student Marks ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentMarks(items: List<LocalStudentMarkEntity>)

    @Query("SELECT * FROM local_student_marks WHERE studentId = :studentId AND activeSessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getStudentMarks(studentId: String, sessionId: String): List<LocalStudentMarkEntity>

    @Query("DELETE FROM local_student_marks WHERE organizationId = :orgId AND activeSessionId = :sessionId")
    suspend fun clearStudentMarks(orgId: String, sessionId: String)

    // --- Student Subjects ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentSubjects(items: List<LocalStudentSubjectEntity>)

    @Query("SELECT * FROM local_student_subjects WHERE studentId = :studentId AND activeSessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getStudentSubjects(studentId: String, sessionId: String): List<LocalStudentSubjectEntity>

    @Query("DELETE FROM local_student_subjects WHERE organizationId = :orgId AND activeSessionId = :sessionId")
    suspend fun clearStudentSubjects(orgId: String, sessionId: String)

    // --- Student User Links ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentUserLinks(items: List<LocalStudentUserLinkEntity>)

    @Query("SELECT * FROM local_student_user_links WHERE studentId = :studentId AND isActive = 1 AND isDeleted = 0")
    suspend fun getStudentUserLinks(studentId: String): List<LocalStudentUserLinkEntity>

    @Query("SELECT * FROM local_student_user_links WHERE userId = :userId AND isActive = 1 AND isDeleted = 0 LIMIT 1")
    suspend fun getStudentLinkByUserId(userId: String): LocalStudentUserLinkEntity?

    @Query("DELETE FROM local_student_user_links WHERE organizationId = :orgId AND activeSessionId = :sessionId")
    suspend fun clearStudentUserLinks(orgId: String, sessionId: String)

    // ═══════════════════════════════════════════════════════════════════════
    // ── Staff Entities DAOs ─────────────────────────────────────────────────
    // ═══════════════════════════════════════════════════════════════════════

    // --- Parent Staff ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentStaff(items: List<LocalParentStaffEntity>)

    @Query("SELECT * FROM local_parent_staff WHERE parentOrganizationId = :parentOrgId AND isActive = 1 AND isDeleted = 0")
    suspend fun getParentStaff(parentOrgId: String): List<LocalParentStaffEntity>

    @Query("SELECT * FROM local_parent_staff WHERE id = :id LIMIT 1")
    suspend fun getParentStaffById(id: String): LocalParentStaffEntity?

    @Query("DELETE FROM local_parent_staff WHERE parentOrganizationId = :parentOrgId")
    suspend fun clearParentStaff(parentOrgId: String)

    // --- Parent Staff Attendance ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentStaffAttendance(items: List<LocalParentStaffAttendanceEntity>)

    @Query("SELECT * FROM local_parent_staff_attendance WHERE parentOrganizationId = :parentOrgId AND activeSessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getParentStaffAttendance(parentOrgId: String, sessionId: String): List<LocalParentStaffAttendanceEntity>

    @Query("SELECT * FROM local_parent_staff_attendance WHERE staffId = :staffId AND attendanceDate = :date AND isDeleted = 0 LIMIT 1")
    suspend fun getStaffAttendanceForDate(staffId: String, date: String): LocalParentStaffAttendanceEntity?

    @Query("DELETE FROM local_parent_staff_attendance WHERE parentOrganizationId = :parentOrgId AND activeSessionId = :sessionId")
    suspend fun clearParentStaffAttendance(parentOrgId: String, sessionId: String)

    // --- Parent Staff Bus Enrollments ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentStaffBusEnrollments(items: List<LocalParentStaffBusEnrollmentEntity>)

    @Query("SELECT * FROM local_parent_staff_bus_enrollments WHERE parentOrganizationId = :parentOrgId AND isActive = 1 AND isDeleted = 0")
    suspend fun getParentStaffBusEnrollments(parentOrgId: String): List<LocalParentStaffBusEnrollmentEntity>

    @Query("DELETE FROM local_parent_staff_bus_enrollments WHERE parentOrganizationId = :parentOrgId")
    suspend fun clearParentStaffBusEnrollments(parentOrgId: String)

    // --- Parent Staff Bus Fares ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentStaffBusFares(items: List<LocalParentStaffBusFareEntity>)

    @Query("SELECT * FROM local_parent_staff_bus_fares WHERE parentOrganizationId = :parentOrgId AND isActive = 1 AND isDeleted = 0")
    suspend fun getParentStaffBusFares(parentOrgId: String): List<LocalParentStaffBusFareEntity>

    @Query("DELETE FROM local_parent_staff_bus_fares WHERE parentOrganizationId = :parentOrgId")
    suspend fun clearParentStaffBusFares(parentOrgId: String)

    // --- Parent Staff Leave Quotas ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentStaffLeaveQuotas(items: List<LocalParentStaffLeaveQuotaEntity>)

    @Query("SELECT * FROM local_parent_staff_leave_quotas WHERE staffId = :staffId AND isActive = 1 AND isDeleted = 0")
    suspend fun getParentStaffLeaveQuotas(staffId: String): List<LocalParentStaffLeaveQuotaEntity>

    @Query("DELETE FROM local_parent_staff_leave_quotas WHERE parentOrganizationId = :parentOrgId AND activeSessionId = :sessionId")
    suspend fun clearParentStaffLeaveQuotas(parentOrgId: String, sessionId: String)

    // --- Parent Staff Salary Payouts ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentStaffSalaryPayouts(items: List<LocalParentStaffSalaryPayoutEntity>)

    @Query("SELECT * FROM local_parent_staff_salary_payouts WHERE staffId = :staffId AND isActive = 1 AND isDeleted = 0")
    suspend fun getParentStaffSalaryPayouts(staffId: String): List<LocalParentStaffSalaryPayoutEntity>

    @Query("DELETE FROM local_parent_staff_salary_payouts WHERE parentOrganizationId = :parentOrgId AND activeSessionId = :sessionId")
    suspend fun clearParentStaffSalaryPayouts(parentOrgId: String, sessionId: String)

    // --- Parent Staff User Links ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentStaffUserLinks(items: List<LocalParentStaffUserLinkEntity>)

    @Query("SELECT * FROM local_parent_staff_user_links WHERE staffId = :staffId AND isActive = 1 AND isDeleted = 0")
    suspend fun getParentStaffUserLinks(staffId: String): List<LocalParentStaffUserLinkEntity>

    @Query("DELETE FROM local_parent_staff_user_links WHERE parentOrganizationId = :parentOrgId")
    suspend fun clearParentStaffUserLinks(parentOrgId: String)

    // --- Parent Bus Staff Assignments ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentBusStaffAssignments(items: List<LocalParentBusStaffAssignmentEntity>)

    @Query("SELECT * FROM local_parent_bus_staff_assignments WHERE busId = :busId AND isActive = 1 AND isDeleted = 0")
    suspend fun getParentBusStaffAssignments(busId: String): List<LocalParentBusStaffAssignmentEntity>

    @Query("DELETE FROM local_parent_bus_staff_assignments WHERE parentOrganizationId = :parentOrgId")
    suspend fun clearParentBusStaffAssignments(parentOrgId: String)

    // ═══════════════════════════════════════════════════════════════════════
    // ── Session / Content / Timetable / Remark Entities DAOs ───────────────
    // ═══════════════════════════════════════════════════════════════════════

    // --- Session Classes ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessionClasses(items: List<LocalSessionClassEntity>)

    @Query("SELECT * FROM local_session_classes WHERE organizationId = :orgId AND activeSessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getSessionClasses(orgId: String, sessionId: String): List<LocalSessionClassEntity>

    @Query("DELETE FROM local_session_classes WHERE organizationId = :orgId AND activeSessionId = :sessionId")
    suspend fun clearSessionClasses(orgId: String, sessionId: String)

    // --- Session Sections ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessionSections(items: List<LocalSessionSectionEntity>)

    @Query("SELECT * FROM local_session_sections WHERE organizationId = :orgId AND activeSessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getSessionSections(orgId: String, sessionId: String): List<LocalSessionSectionEntity>

    @Query("SELECT * FROM local_session_sections WHERE organizationClassId = :classId AND isActive = 1 AND isDeleted = 0")
    suspend fun getSessionSectionsByClass(classId: String): List<LocalSessionSectionEntity>

    @Query("DELETE FROM local_session_sections WHERE organizationId = :orgId AND activeSessionId = :sessionId")
    suspend fun clearSessionSections(orgId: String, sessionId: String)

    // --- Session Subjects ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessionSubjects(items: List<LocalSessionSubjectEntity>)

    @Query("SELECT * FROM local_session_subjects WHERE organizationId = :orgId AND activeSessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getSessionSubjects(orgId: String, sessionId: String): List<LocalSessionSubjectEntity>

    @Query("DELETE FROM local_session_subjects WHERE organizationId = :orgId AND activeSessionId = :sessionId")
    suspend fun clearSessionSubjects(orgId: String, sessionId: String)

    // --- Content Assignments ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContentAssignments(items: List<LocalContentAssignmentEntity>)

    @Query("SELECT * FROM local_content_assignments WHERE targetOrganizationId = :orgId")
    suspend fun getContentAssignments(orgId: String): List<LocalContentAssignmentEntity>

    @Query("DELETE FROM local_content_assignments WHERE targetOrganizationId = :orgId")
    suspend fun clearContentAssignments(orgId: String)

    // --- Timetable Schedules ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableSchedules(items: List<LocalTimetableScheduleEntity>)

    @Query("SELECT * FROM local_timetable_schedules WHERE organizationId = :orgId AND activeSessionId = :sessionId AND isActive = 1 AND isDeleted = 0")
    suspend fun getTimetableSchedules(orgId: String, sessionId: String): List<LocalTimetableScheduleEntity>

    @Query("SELECT * FROM local_timetable_schedules WHERE organizationSectionId = :sectionId AND dayOfWeek = :dayOfWeek AND isActive = 1 AND isDeleted = 0")
    suspend fun getTimetableForSectionDay(sectionId: String, dayOfWeek: Int): List<LocalTimetableScheduleEntity>

    @Query("DELETE FROM local_timetable_schedules WHERE organizationId = :orgId AND activeSessionId = :sessionId")
    suspend fun clearTimetableSchedules(orgId: String, sessionId: String)

    // --- Remark Attachments ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemarkAttachments(items: List<LocalRemarkAttachmentEntity>)

    @Query("SELECT * FROM local_remark_attachments WHERE remarkId = :remarkId AND isActive = 1 AND isDeleted = 0")
    suspend fun getRemarkAttachments(remarkId: String): List<LocalRemarkAttachmentEntity>

    @Query("DELETE FROM local_remark_attachments WHERE remarkId = :remarkId")
    suspend fun clearRemarkAttachments(remarkId: String)

    // --- Remark History ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemarkHistory(items: List<LocalRemarkHistoryEntity>)

    @Query("SELECT * FROM local_remark_history WHERE remarkId = :remarkId ORDER BY createdAt DESC")
    suspend fun getRemarkHistory(remarkId: String): List<LocalRemarkHistoryEntity>

    @Query("DELETE FROM local_remark_history WHERE remarkId = :remarkId")
    suspend fun clearRemarkHistory(remarkId: String)
}