package com.vidyasetuai.feature_institution.data.repository

import com.vidyasetuai.feature_institution.domain.repository.InstitutionRepository
import com.vidyasetuai.feature_institution.data.local.dao.InstitutionDao
import com.vidyasetuai.feature_institution.data.remote.datasource.InstitutionRemoteDataSource
import com.vidyasetuai.feature_institution.domain.model.*
import com.vidyasetuai.feature_institution.data.local.entity.*
import com.vidyasetuai.feature_institution.data.remote.dto.*
import kotlinx.serialization.json.*
import com.vidyasetuai.core.network.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class InstitutionRepositoryImpl(
    private val dao: InstitutionDao,
    private val remoteDataSource: InstitutionRemoteDataSource = InstitutionRemoteDataSource()
) : InstitutionRepository {

    override suspend fun checkConnectionStatus(userId: String): Result<ConnectionState> = Result.success(ConnectionState.CONNECTED)
    
    override suspend fun approveConnection(userId: String): Result<Unit> = Result.success(Unit)
    
    override suspend fun getWorkspaces(userId: String): Result<List<Workspace>> = runCatching {
        try {
            val studentLinks = try { remoteDataSource.fetchStudentWorkspaces(userId) } catch (e: Exception) { 
                android.util.Log.e("OfflineSync", "Error fetching student workspaces", e)
                emptyList() 
            }
            val staffLinks = try { remoteDataSource.fetchStaffWorkspaces(userId) } catch (e: Exception) { 
                android.util.Log.e("OfflineSync", "Error fetching staff workspaces", e)
                emptyList() 
            }
            val guardianLinks = try { remoteDataSource.fetchGuardianWorkspaces(userId) } catch (e: Exception) { 
                android.util.Log.e("OfflineSync", "Error fetching guardian workspaces", e)
                emptyList() 
            }

            // 1. Gather all unique child organization IDs and parent organization IDs
            val childOrgIds = (studentLinks.mapNotNull { it.child_organization_id ?: it.organization_id } +
                               guardianLinks.mapNotNull { it.child_organization_id ?: it.organization_id } +
                               staffLinks.mapNotNull { it.child_organization_id }).distinct().filter { it.isNotEmpty() }
            
            val parentOrgIds = (studentLinks.map { it.parent_organization_id } +
                                guardianLinks.map { it.parent_organization_id } +
                                staffLinks.map { it.parent_organization_id }).distinct().filter { it.isNotEmpty() }

            val staffIds = staffLinks.mapNotNull { it.staff_id }.distinct().filter { it.isNotEmpty() }

            // 2. Fetch Child Organizations in Bulk
            val orgsMap = mutableMapOf<String, OrgNameDto>()
            if (childOrgIds.isNotEmpty()) {
                try {
                    SupabaseClient.client.from("organizations")
                        .select(columns = Columns.raw("id, name, parent_organization_id")) {
                            filter { isIn("id", childOrgIds) }
                        }.decodeList<OrgNameDto>().forEach { orgsMap[it.id] = it }
                } catch (e: Exception) {
                    android.util.Log.e("OfflineSync", "Error bulk fetching child orgs", e)
                }
            }

            // 3. Fetch Parent Organizations in Bulk
            val allParentOrgIds = (parentOrgIds + orgsMap.values.mapNotNull { it.parent_organization_id }).distinct().filter { it.isNotEmpty() }
            val parentOrgsMap = mutableMapOf<String, String>()
            if (allParentOrgIds.isNotEmpty()) {
                try {
                    SupabaseClient.client.from("organization_parents")
                        .select(columns = Columns.raw("id, name")) {
                            filter { isIn("id", allParentOrgIds) }
                        }.decodeList<ParentOrgNameDto>().forEach { parentOrgsMap[it.id] = it.name }
                } catch (e: Exception) {
                    android.util.Log.e("OfflineSync", "Error bulk fetching parent orgs", e)
                }
            }

            // 4. Fetch Staff Profiles in Bulk to get role_ids and names
            val staffRoleIdsMap = mutableMapOf<String, String>()
            val staffNamesMap = mutableMapOf<String, String>()
            if (staffIds.isNotEmpty()) {
                try {
                    SupabaseClient.client.from("organization_parent_staff")
                        .select(columns = Columns.raw("id, role_id, name")) {
                            filter { isIn("id", staffIds) }
                        }.decodeList<StaffRoleIdDto>().forEach { staff ->
                            staff.role_id?.let { staffRoleIdsMap[staff.id] = it }
                            staff.name?.let { staffNamesMap[staff.id] = it }
                        }
                } catch (e: Exception) {
                    android.util.Log.e("OfflineSync", "Error bulk fetching staff profiles", e)
                }
            }

            // 4b. Fetch Student Names in Bulk
            val studentIds = studentLinks.mapNotNull { it.student_id }.distinct().filter { it.isNotEmpty() }
            val studentNamesMap = mutableMapOf<String, String>()
            if (studentIds.isNotEmpty()) {
                try {
                    SupabaseClient.client.from("organization_students")
                        .select(columns = Columns.raw("id, name")) {
                            filter { isIn("id", studentIds) }
                        }.decodeList<StaffIdNameDto>().forEach { student ->
                            studentNamesMap[student.id] = student.name
                        }
                } catch (e: Exception) {
                    android.util.Log.e("OfflineSync", "Error bulk fetching student names", e)
                }
            }

            // 4c. Fetch Guardian Names in Bulk
            val guardianIds = guardianLinks.mapNotNull { it.guardian_id }.distinct().filter { it.isNotEmpty() }
            val guardianNamesMap = mutableMapOf<String, String>()
            if (guardianIds.isNotEmpty()) {
                try {
                    SupabaseClient.client.from("organization_guardians")
                        .select(columns = Columns.raw("id, name")) {
                            filter { isIn("id", guardianIds) }
                        }.decodeList<StaffIdNameDto>().forEach { guardian ->
                            guardianNamesMap[guardian.id] = guardian.name
                        }
                } catch (e: Exception) {
                    android.util.Log.e("OfflineSync", "Error bulk fetching guardian names", e)
                }
            }

            // 5. Fetch Global Staff Roles in Bulk
            val roleIds = staffRoleIdsMap.values.distinct().filter { it.isNotEmpty() }
            val rolesMap = mutableMapOf<String, String>()
            if (roleIds.isNotEmpty()) {
                try {
                    SupabaseClient.client.from("global_staff_roles")
                        .select(columns = Columns.raw("id, name")) {
                            filter { isIn("id", roleIds) }
                        }.decodeList<RoleNameDto>().forEach { role ->
                            rolesMap[role.id] = role.name
                        }
                } catch (e: Exception) {
                    android.util.Log.e("OfflineSync", "Error bulk fetching staff roles", e)
                }
            }

            // 6. Map everything back to WorkspaceEntity
            val list = mutableListOf<WorkspaceEntity>()

            // Map Student Links
            studentLinks.forEach { link ->
                val orgId = link.child_organization_id ?: link.organization_id ?: ""
                val childOrgName = orgsMap[orgId]?.name
                val parentOrgId = link.parent_organization_id.ifEmpty { orgsMap[orgId]?.parent_organization_id ?: "" }
                val parentOrgName = parentOrgsMap[parentOrgId]

                list.add(
                    WorkspaceEntity(
                        workspaceId = link.id,
                        workspaceRole = "Student",
                        workspaceSubRole = "",
                        parentOrganizationId = parentOrgId,
                        parentOrganizationName = parentOrgName,
                        parentOrgLogoUrl = null,
                        parentOrgLogoLocalPath = null,
                        parentOrgEmail = null,
                        parentOrgMobile = null,
                        parentOrgActiveSessionId = "",
                        parentOrgActiveSessionName = null,
                        parentOrgWebsiteUrl = null,
                        childOrganizationId = orgId,
                        childOrganizationName = childOrgName,
                        childOrgLogoUrl = null,
                        childOrgLogoLocalPath = null,
                        childOrgEmail = null,
                        childOrgMobileNumber = null,
                        childOrgAlternateMobile = null,
                        childOrgActiveSessionId = null,
                        childOrgActiveSessionName = null,
                        childOrgAddressLine1 = null,
                        childOrgAddressLine2 = null,
                        childOrgCity = null,
                        childOrgState = "Rajasthan",
                        childOrgPincode = null,
                        staffId = null,
                        studentId = link.student_id,
                        guardianId = null,
                        roleDisplayName = studentNamesMap[link.student_id ?: ""],
                        roleMobileNumber = null,
                        roleImageUrl = null,
                        roleImageLocalPath = null,
                        isApproved = link.is_approved,
                        approvedBy = link.approved_by,
                        approvedAt = link.approved_at,
                        isActiveNow = false,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                )
            }

            // Map Staff Links
            staffLinks.forEach { link ->
                val parentOrgId = link.parent_organization_id
                val parentOrgName = parentOrgsMap[parentOrgId]
                val staffId = link.staff_id ?: ""
                val roleId = staffRoleIdsMap[staffId] ?: ""
                val resolvedRole = rolesMap[roleId] ?: "Teacher"

                list.add(
                    WorkspaceEntity(
                        workspaceId = link.id,
                        workspaceRole = resolvedRole,
                        workspaceSubRole = resolvedRole,
                        parentOrganizationId = parentOrgId,
                        parentOrganizationName = parentOrgName,
                        parentOrgLogoUrl = null,
                        parentOrgLogoLocalPath = null,
                        parentOrgEmail = null,
                        parentOrgMobile = null,
                        parentOrgActiveSessionId = "",
                        parentOrgActiveSessionName = null,
                        parentOrgWebsiteUrl = null,
                        childOrganizationId = link.child_organization_id,
                        childOrganizationName = null,
                        childOrgLogoUrl = null,
                        childOrgLogoLocalPath = null,
                        childOrgEmail = null,
                        childOrgMobileNumber = null,
                        childOrgAlternateMobile = null,
                        childOrgActiveSessionId = null,
                        childOrgActiveSessionName = null,
                        childOrgAddressLine1 = null,
                        childOrgAddressLine2 = null,
                        childOrgCity = null,
                        childOrgState = "Rajasthan",
                        childOrgPincode = null,
                        staffId = link.staff_id,
                        studentId = null,
                        guardianId = null,
                        roleDisplayName = staffNamesMap[link.staff_id ?: ""],
                        roleMobileNumber = null,
                        roleImageUrl = null,
                        roleImageLocalPath = null,
                        isApproved = link.is_approved,
                        approvedBy = link.approved_by,
                        approvedAt = link.approved_at,
                        isActiveNow = false,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                )
            }

            // Map Guardian Links
            guardianLinks.forEach { link ->
                val orgId = link.child_organization_id ?: link.organization_id ?: ""
                val childOrgName = orgsMap[orgId]?.name
                val parentOrgId = link.parent_organization_id.ifEmpty { orgsMap[orgId]?.parent_organization_id ?: "" }
                val parentOrgName = parentOrgsMap[parentOrgId]

                list.add(
                    WorkspaceEntity(
                        workspaceId = link.id,
                        workspaceRole = "Guardian",
                        workspaceSubRole = "",
                        parentOrganizationId = parentOrgId,
                        parentOrganizationName = parentOrgName,
                        parentOrgLogoUrl = null,
                        parentOrgLogoLocalPath = null,
                        parentOrgEmail = null,
                        parentOrgMobile = null,
                        parentOrgActiveSessionId = "",
                        parentOrgActiveSessionName = null,
                        parentOrgWebsiteUrl = null,
                        childOrganizationId = orgId,
                        childOrganizationName = childOrgName,
                        childOrgLogoUrl = null,
                        childOrgLogoLocalPath = null,
                        childOrgEmail = null,
                        childOrgMobileNumber = null,
                        childOrgAlternateMobile = null,
                        childOrgActiveSessionId = null,
                        childOrgActiveSessionName = null,
                        childOrgAddressLine1 = null,
                        childOrgAddressLine2 = null,
                        childOrgCity = null,
                        childOrgState = "Rajasthan",
                        childOrgPincode = null,
                        staffId = null,
                        studentId = null,
                        guardianId = link.guardian_id,
                        roleDisplayName = guardianNamesMap[link.guardian_id ?: ""],
                        roleMobileNumber = null,
                        roleImageUrl = null,
                        roleImageLocalPath = null,
                        isApproved = link.is_approved,
                        approvedBy = link.approved_by,
                        approvedAt = link.approved_at,
                        isActiveNow = false,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                )
            }

            if (list.isNotEmpty()) {
                val distinctList = list.distinctBy { 
                    "${it.workspaceRole}_${it.staffId}_${it.studentId}_${it.guardianId}_${it.childOrganizationId}_${it.parentOrganizationId}" 
                }
                
                val localActiveWorkspace = dao.getActiveWorkspace()
                val activeId = localActiveWorkspace?.workspaceId
                
                val finalWorkspaces = distinctList.mapIndexed { idx, item ->
                    if (activeId != null) {
                        item.copy(isActiveNow = item.workspaceId == activeId)
                    } else {
                        item.copy(isActiveNow = idx == 0)
                    }
                }
                
                dao.clearWorkspaces()
                dao.insertWorkspaces(finalWorkspaces)
            }
        } catch (e: Exception) {
            android.util.Log.e("OfflineSync", "Failed to refresh workspaces from remote, using local cache", e)
        }
        dao.getAllWorkspaces().map { it.toDomain() }
    }
    
    override suspend fun getCachedWorkspaces(): Result<List<Workspace>> = runCatching {
        dao.getAllWorkspaces().map { it.toDomain() }
    }
    
    override suspend fun setActiveWorkspace(workspaceId: String): Result<Unit> = runCatching {
        dao.clearActiveWorkspace()
        dao.setActiveWorkspace(workspaceId)
    }
    
    override suspend fun getGuardianStudents(guardianLinkId: String, forceRefresh: Boolean): Result<List<InstitutionStudent>> = runCatching {
        val workspaceEntity = dao.getAllWorkspaces().firstOrNull { it.workspaceId == guardianLinkId }
        val guardianId = workspaceEntity?.guardianId ?: ""
        val students = if (guardianId.isNotEmpty()) dao.getStudentsByGuardianId(guardianId) else emptyList()
        students.map { student ->
            val totalFee = dao.getStudentAdditionalFees(student.id).sumOf { it.amount }
            val paidFee = dao.getStudentFeePayments(listOf(student.id)).sumOf { it.amountPaid }
            student.toInstitutionStudent(totalFee = totalFee, paidFee = paidFee)
        }
    }
    
    override suspend fun getPendingApprovals(userId: String, forceRefresh: Boolean): Result<List<PendingApproval>> = runCatching {
        val workspaces = dao.getAllWorkspaces().filter { !it.isApproved }
        workspaces.map { 
            val tblName = when (it.workspaceRole.trim().uppercase()) {
                "STUDENT" -> "organization_student_user_links"
                "STAFF" -> "organization_parent_staff_user_links"
                "GUARDIAN" -> "organization_guardian_user_links"
                else -> ""
            }
            PendingApproval(
                id = it.workspaceId,
                role = it.workspaceRole,
                targetName = it.parentOrganizationName ?: "",
                personName = it.roleDisplayName ?: "",
                tableName = tblName
            )
        }
    }
    
    override suspend fun approveSpecificConnection(linkId: String, tableName: String): Result<Unit> = Result.success(Unit)
    
    override suspend fun submitLeave(
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
    ): Result<Unit> = runCatching {
        val id = java.util.UUID.randomUUID().toString()
        val leaveEntity = LocalOrganizationLeaveEntity(
            id = id,
            parentOrganizationId = parentOrgId,
            organizationId = orgId,
            activeSessionId = sessionId,
            applicantType = applicantType,
            staffId = staffId,
            staffName = null,
            staffRoleName = null,
            studentId = studentId,
            studentName = null,
            className = null,
            sectionName = null,
            leaveType = leaveType,
            startDate = startDate,
            endDate = endDate,
            isHalfDay = isHalfDay,
            halfDayPeriod = halfDayPeriod,
            reason = reason,
            status = "Pending",
            actionRemarks = null,
            actionBy = null,
            actionByName = null,
            actionAt = null,
            isActive = true,
            isDeleted = false,
            lastSyncedAt = System.currentTimeMillis(),
            syncState = "PENDING_INSERT"
        )
        dao.insertLeave(leaveEntity)
    }
    
    override suspend fun getLeaveQuotas(staffId: String): Result<Double> = Result.success(12.0)
    
    override suspend fun getStaffLeaveQuotaAndRemaining(userId: String, parentOrgId: String, forceRefresh: Boolean): Result<Pair<Double, Double>> = runCatching {
        val staff = dao.getStaffProfileById(userId)
        val total = staff?.totalLeaves ?: 12.0
        val approvedLeavesCount = dao.getLeavesForUser(userId, null, userId)
            .filter { it.status == "Approved" }
            .size.toDouble()
        Pair(total, total - approvedLeavesCount)
    }
    
    override suspend fun getLeaves(userId: String, role: String, forceRefresh: Boolean): Result<List<Leave>> = runCatching {
        val activeWorkspace = dao.getActiveWorkspace()
        val parentOrgId = activeWorkspace?.parentOrganizationId ?: ""
        if (forceRefresh || dao.getAllLeaves().isEmpty()) {
            val list = remoteDataSource.fetchLeaves(parentOrgId)
            dao.insertLeaves(
                list.map { leave ->
                    LocalOrganizationLeaveEntity(
                        id = leave.id,
                        parentOrganizationId = leave.parent_organization_id,
                        organizationId = leave.organization_id,
                        activeSessionId = leave.active_session_id,
                        applicantType = leave.applicant_type,
                        staffId = leave.staff_id,
                        staffName = null,
                        staffRoleName = null,
                        studentId = leave.student_id,
                        studentName = null,
                        className = null,
                        sectionName = null,
                        leaveType = leave.leave_type,
                        startDate = leave.start_date,
                        endDate = leave.end_date,
                        isHalfDay = leave.is_half_day,
                        halfDayPeriod = leave.half_day_period,
                        reason = leave.reason,
                        status = leave.status,
                        actionRemarks = leave.action_remarks,
                        actionBy = leave.action_by,
                        actionByName = null,
                        actionAt = leave.action_at,
                        isActive = leave.is_active,
                        isDeleted = leave.is_deleted,
                        lastSyncedAt = System.currentTimeMillis(),
                        syncState = "SYNCED"
                    )
                }
            )
        }
        val local = dao.getLeavesForUser(userId, userId, userId)
        local.map { it.toDomain() }
    }
    
    override suspend fun getFeePayments(studentIds: List<String>, forceRefresh: Boolean): Result<List<FeePayment>> = runCatching {
        if (forceRefresh || dao.getStudentFeePayments(studentIds).isEmpty()) {
            val list = remoteDataSource.fetchStudentFeePayments(studentIds)
            dao.insertStudentFeePayments(
                list.map { payment ->
                    LocalStudentFeePaymentEntity(
                        id = payment.id,
                        organizationId = payment.organization_id,
                        activeSessionId = payment.active_session_id,
                        studentId = payment.student_id,
                        receiptNumber = payment.receipt_number,
                        paymentMode = payment.payment_mode,
                        paymentDate = payment.payment_date,
                        amountPaid = payment.amount_paid,
                        discountAmount = payment.discount_amount,
                        fineAmount = payment.fine_amount,
                        discountReason = payment.discount_reason,
                        remarks = payment.remarks,
                        status = payment.status,
                        cashReceivedByUserId = null,
                        cashReceivedByUserName = null,
                        chequeNumber = null,
                        chequeDate = null,
                        chequeBankName = null,
                        onlineTransactionId = null,
                        onlinePaymentApp = null,
                        isActive = payment.is_active,
                        isDeleted = payment.is_deleted,
                        lastSyncedAt = System.currentTimeMillis(),
                        syncState = "SYNCED"
                    )
                }
            )
        }
        val local = dao.getStudentFeePayments(studentIds)
        local.map { it.toDomain() }
    }
    
    override suspend fun getAdminFeeStats(parentOrgId: String, forceRefresh: Boolean): Result<Pair<Double, Double>> = runCatching {
        val workspaces = dao.getAllWorkspaces().filter { it.parentOrganizationId == parentOrgId }
        val studentIds = workspaces.mapNotNull { it.studentId }
        val totalFee = studentIds.sumOf { studentId ->
            dao.getStudentAdditionalFees(studentId).sumOf { it.amount }
        }
        val paidFee = dao.getStudentFeePayments(studentIds).sumOf { it.amountPaid }
        Pair(totalFee, paidFee)
    }
    
    override suspend fun updateBusLiveLocation(
        busId: String,
        parentOrgId: String,
        latitude: Double,
        longitude: Double,
        speed: Double,
        sessionId: String?
    ): Result<Unit> = runCatching {
        val bus = dao.getBusById(busId)
        if (bus != null) {
            val updated = bus.copy(
                lastLatitude = latitude,
                lastLongitude = longitude,
                lastLocationUpdatedAt = System.currentTimeMillis()
            )
            dao.insertParentBuses(listOf(updated))
        }
    }
    
    override suspend fun getStudentAttendance(studentIds: List<String>, forceRefresh: Boolean): Result<List<StudentAttendance>> = runCatching {
        if (forceRefresh || dao.getStudentAttendance(studentIds).isEmpty()) {
            val list = remoteDataSource.fetchStudentAttendance(studentIds)
            dao.insertStudentAttendance(
                list.map { item ->
                    LocalStudentAttendanceEntity(
                        id = item.id,
                        organizationId = item.organization_id,
                        activeSessionId = item.active_session_id,
                        studentId = item.student_id,
                        studentName = null,
                        rollNumber = null,
                        classId = null,
                        className = null,
                        sectionId = null,
                        sectionName = null,
                        attendanceDate = item.attendance_date,
                        status = item.status,
                        remarks = item.remarks,
                        markedByStaffId = item.marked_by_staff_id,
                        markedByStaffName = null,
                        isActive = item.is_active,
                        isDeleted = item.is_deleted,
                        lastSyncedAt = System.currentTimeMillis(),
                        syncState = "SYNCED"
                    )
                }
            )
        }
        val local = dao.getStudentAttendance(studentIds)
        local.map { it.toDomain() }
    }
    
    override suspend fun getStudentBusAssignments(studentIds: List<String>, forceRefresh: Boolean): Result<List<StudentBusAssignment>> = runCatching {
        android.util.Log.d("BusTracking", "getStudentBusAssignments called for studentIds: $studentIds")
        val remoteAssignments = remoteDataSource.fetchStudentBusAssignments(studentIds)
        android.util.Log.d("BusTracking", "Remote assignments fetched: ${remoteAssignments.size}")
        remoteAssignments.map { assignment ->
            val student = dao.getStudentById(assignment.student_id)
            val bus = dao.getBusById(assignment.bus_id)
            android.util.Log.d("BusTracking", "Mapping remote assignment: studentId=${assignment.student_id}, busId=${assignment.bus_id}, studentName=${student?.name}, busFound=${bus != null}, busNumber=${bus?.busNumber}, routeName=${bus?.routeName}")
            StudentBusAssignment(
                studentId = assignment.student_id,
                studentName = student?.name ?: "Student",
                busId = assignment.bus_id,
                busNumber = bus?.busNumber ?: "",
                busName = bus?.busName,
                routeName = bus?.routeName,
                pickupStop = assignment.pickup_stop ?: student?.addressAreaName
            )
        }
    }
    
    override suspend fun getBusLiveLocation(busId: String): Result<BusLiveLocation?> = runCatching {
        val bus = dao.getBusById(busId) ?: return@runCatching null
        BusLiveLocation(
            busId = bus.id,
            latitude = bus.lastLatitude ?: 0.0,
            longitude = bus.lastLongitude ?: 0.0,
            speed = 0.0,
            updatedAt = bus.lastLocationUpdatedAt?.toString() ?: "",
            isLive = bus.lastLocationUpdatedAt != null
        )
    }
    
    override suspend fun getBusRoute(busId: String, forceRefresh: Boolean): Result<List<BusRouteStop>> = runCatching {
        if (forceRefresh) {
            val activeWs = dao.getActiveWorkspace()
            val parentOrgId = activeWs?.parentOrganizationId ?: ""
            if (parentOrgId.isNotEmpty()) {
                val routes = remoteDataSource.fetchBusRoutes(parentOrgId)
                dao.insertBusRoutes(
                    routes.map { route ->
                        com.vidyasetuai.feature_institution.data.local.entity.LocalBusRouteEntity(
                            id = route.id,
                            parentOrganizationId = route.parent_organization_id,
                            activeSessionId = route.active_session_id,
                            busId = route.bus_id,
                            stopName = route.stop_name,
                            stopOrder = route.stop_order,
                            latitude = route.latitude,
                            longitude = route.longitude,
                            scheduledTime = route.scheduled_time,
                            isActive = route.is_active,
                            isDeleted = route.is_deleted
                        )
                    }
                )
            }
        }
        dao.getBusRoutes(busId).map { route ->
            BusRouteStop(
                id = route.id,
                parentOrgId = route.parentOrganizationId,
                activeSessionId = route.activeSessionId,
                busId = route.busId,
                stopName = route.stopName,
                stopOrder = route.stopOrder,
                latitude = route.latitude,
                longitude = route.longitude,
                scheduledTime = route.scheduledTime,
                isActive = route.isActive
            )
        }
    }
    
    override suspend fun getParentBuses(parentOrgId: String, forceRefresh: Boolean): Result<List<LocalParentBusEntity>> = runCatching {
        if (forceRefresh) {
            val remoteBuses = remoteDataSource.fetchParentBuses(parentOrgId)
            dao.insertParentBuses(
                remoteBuses.map { bus ->
                    LocalParentBusEntity(
                        id = bus.id,
                        parentOrganizationId = bus.parent_organization_id,
                        activeSessionId = bus.active_session_id,
                        busNumber = bus.bus_number,
                        busName = bus.bus_name,
                        routeName = bus.route_name,
                        maxCapacity = bus.max_capacity,
                        isActive = bus.is_active,
                        isDeleted = bus.is_deleted,
                        insuranceExpiryDate = bus.insurance_expiry_date,
                        insuranceImageUrl = bus.insurance_image_url,
                        insuranceImageLocalPath = null,
                        fitnessExpiryDate = bus.fitness_expiry_date,
                        fitnessImageUrl = bus.fitness_image_url,
                        fitnessImageLocalPath = null,
                        pollutionExpiryDate = bus.pollution_expiry_date,
                        pollutionImageUrl = bus.pollution_image_url,
                        pollutionImageLocalPath = null,
                        driverId = bus.driver_id,
                        driverName = bus.driver_name,
                        driverMobile = bus.driver_mobile,
                        conductorId = bus.conductor_id,
                        conductorName = bus.conductor_name,
                        conductorMobile = bus.conductor_mobile,
                        lastLatitude = null,
                        lastLongitude = null,
                        lastLocationUpdatedAt = null,
                        lastSyncedAt = System.currentTimeMillis(),
                        syncState = "SYNCED"
                    )
                }
            )
        }
        dao.getParentBuses(parentOrgId)
    }
    
    override suspend fun getDriverBusDetails(workspaceId: String, forceRefresh: Boolean): Result<DriverBusDetails?> = runCatching {
        val active = dao.getActiveWorkspace() ?: return@runCatching null
        val driverId = active.staffId ?: return@runCatching null
        val staff = dao.getStaffProfileById(driverId) ?: return@runCatching null
        val busId = staff.busId ?: return@runCatching null
        val bus = dao.getBusById(busId) ?: return@runCatching null
        DriverBusDetails(
            busId = bus.id,
            busNumber = bus.busNumber,
            routeName = bus.routeName,
            staffId = driverId,
            parentOrgId = active.parentOrganizationId,
            activeSessionId = active.parentOrgActiveSessionId ?: ""
        )
    }
    
    override suspend fun getOrganizations(parentOrgId: String, forceRefresh: Boolean): Result<List<ChildOrg>> = runCatching {
        val workspaces = dao.getAllWorkspaces().filter { it.parentOrganizationId == parentOrgId }
        workspaces.mapNotNull { ws ->
            ws.childOrganizationId?.let { id ->
                ChildOrg(id = id, name = ws.childOrganizationName ?: "")
            }
        }.distinctBy { it.id }
    }
    
    override suspend fun getClasses(orgId: String, forceRefresh: Boolean): Result<List<OrgClass>> = runCatching {
        val setup = dao.getChildOrgSetup(orgId) ?: return@runCatching emptyList()
        val jsonArray = Json.parseToJsonElement(setup.classStructureJson).jsonArray
        jsonArray.map { element ->
            val obj = element.jsonObject
            OrgClass(
                id = obj["class_id"]?.jsonPrimitive?.content ?: "",
                name = obj["class_name"]?.jsonPrimitive?.content ?: ""
            )
        }
    }
    
    override suspend fun getSections(classId: String, forceRefresh: Boolean): Result<List<OrgSection>> = runCatching {
        val activeWorkspace = dao.getActiveWorkspace() ?: return@runCatching emptyList()
        val orgId = activeWorkspace.childOrganizationId ?: return@runCatching emptyList()
        val setup = dao.getChildOrgSetup(orgId) ?: return@runCatching emptyList()
        val jsonArray = Json.parseToJsonElement(setup.classStructureJson).jsonArray
        val classObj = jsonArray.firstOrNull { it.jsonObject["class_id"]?.jsonPrimitive?.content == classId }?.jsonObject
        val sectionsArray = classObj?.get("sections")?.jsonArray ?: return@runCatching emptyList()
        sectionsArray.map { element ->
            val obj = element.jsonObject
            OrgSection(
                id = obj["section_id"]?.jsonPrimitive?.content ?: "",
                name = obj["section_name"]?.jsonPrimitive?.content ?: ""
            )
        }
    }
    
    override suspend fun getStudentsForAttendance(orgId: String, classId: String, sectionId: String, date: String): Result<List<StudentAttendanceInfo>> = runCatching {
        val students = dao.getStudents(orgId).filter { 
            it.classId == classId && it.sectionId == sectionId 
        }
        val attendanceMap = dao.getStudentAttendanceForClass(orgId, classId, sectionId, date)
            .associateBy { it.studentId }
        students.map { student ->
            val localAttendance = attendanceMap[student.id]
            StudentAttendanceInfo(
                studentId = student.id,
                name = student.name,
                srNumber = student.srNumber,
                status = localAttendance?.status ?: "Present"
            )
        }
    }
    
    override suspend fun submitStudentAttendance(
        orgId: String,
        date: String,
        attendanceList: List<StudentAttendanceInfo>,
        staffUserId: String,
        parentOrgId: String
    ): Result<Unit> = runCatching {
        val activeWorkspace = dao.getActiveWorkspace()
        val sessionId = activeWorkspace?.parentOrgActiveSessionId ?: ""
        val staffName = activeWorkspace?.roleDisplayName ?: ""
        val entities = attendanceList.map { info ->
            LocalStudentAttendanceEntity(
                id = java.util.UUID.randomUUID().toString(),
                organizationId = orgId,
                activeSessionId = sessionId,
                studentId = info.studentId,
                studentName = info.name,
                rollNumber = null,
                classId = null,
                className = null,
                sectionId = null,
                sectionName = null,
                attendanceDate = date,
                status = info.status,
                remarks = null,
                markedByStaffId = staffUserId,
                markedByStaffName = staffName,
                isActive = true,
                isDeleted = false,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "PENDING_INSERT"
            )
        }
        dao.insertStudentAttendance(entities)
        
        // Immediate sync upload
        val dtos = entities.map { item ->
            StudentAttendanceDto(
                id = item.id,
                organization_id = item.organizationId,
                active_session_id = item.activeSessionId,
                student_id = item.studentId,
                attendance_date = item.attendanceDate,
                status = item.status,
                remarks = item.remarks,
                marked_by_staff_id = item.markedByStaffId,
                is_active = item.isActive,
                is_deleted = item.isDeleted
            )
        }
        runCatching {
            remoteDataSource.upsertStudentAttendance(dtos)
            entities.forEach { dao.markStudentAttendanceSynced(it.id) }
        }
    }
    
    override suspend fun getClassTeacherAssignment(userId: String, parentOrgId: String): Result<AssignedSection?> = runCatching {
        val staff = dao.getStaffProfileById(userId) ?: return@runCatching null
        AssignedSection(
            orgId = staff.parentOrganizationId,
            classId = "",
            sectionId = ""
        )
    }
    
    override suspend fun getStaffSalaryDetails(userId: String, parentOrgId: String, forceRefresh: Boolean): Result<StaffSalaryDetails> = runCatching {
        val staff = dao.getStaffProfileById(userId)
        val monthly = staff?.monthlySalary ?: 0.0
        StaffSalaryDetails(
            monthlySalary = monthly,
            totalPaid = 0.0,
            payments = emptyList()
        )
    }
    
    override suspend fun getActiveSessionDetails(forceRefresh: Boolean): Result<Pair<String, String>> = runCatching {
        val active = dao.getActiveWorkspace()
        Pair(active?.parentOrgActiveSessionId ?: "", active?.parentOrgActiveSessionName ?: "")
    }
    
    override suspend fun checkIfAttendanceMarked(orgId: String, classId: String, sectionId: String, date: String): Result<Boolean> = runCatching {
        dao.checkIfAttendanceMarked(orgId, classId, sectionId, date)
    }
    
    override suspend fun getContentFeed(workspace: Workspace, sessionId: String, forceRefresh: Boolean): Result<List<ContentFeedItem>> = Result.success(emptyList())
    
    override suspend fun syncWorkspaceData(userId: String, workspace: Workspace, sessionId: String): Result<Unit> = runCatching {
        android.util.Log.d("OfflineSync", "syncWorkspaceData CONSOLIDATED CALLED: userId=$userId, workspaceId=${workspace.id}, role=${workspace.role}, childOrgId=${workspace.childOrgId}, parentOrgId=${workspace.parentOrgId}, sessionId=$sessionId")
        val parentOrgId = workspace.parentOrgId
        val orgId = workspace.childOrgId ?: ""
        val userRole = if (workspace.role.equals("Driver", ignoreCase = true)) "DRIVER" else workspace.role
        
        android.util.Log.d("OfflineSync", "Calling consolidated fetchEntireWorkspacePayload...")
        val payload = remoteDataSource.fetchEntireWorkspacePayload(
            parentOrgId = parentOrgId,
            childOrgId = if (orgId.isEmpty()) null else orgId,
            userRole = userRole,
            userId = userId,
            lastSyncedAt = null // Fresh sync pulls all
        )
        
        // 1. Map Child Setup
        val localSetup = payload.setup?.let { setup ->
            LocalChildOrgSetupEntity(
                organizationId = setup.organization_id,
                sessionId = setup.session_id,
                sessionName = setup.session_name,
                boardsJson = setup.boards_json,
                mediumsJson = setup.mediums_json,
                languagesJson = setup.languages_json,
                classStructureJson = setup.class_structure_json,
                periodsJson = setup.periods_json,
                feesStructureJson = setup.fees_structure_json,
                isSetupComplete = setup.is_setup_complete,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }
        
        // 2. Map Students
        val localStudents = payload.students.map { dto ->
            LocalStudentEntity(
                id = dto.id,
                organizationId = dto.organization_id,
                activeSessionId = dto.active_session_id,
                name = dto.name,
                gender = dto.gender,
                srNumber = dto.sr_number,
                admissionDate = dto.admission_date,
                dateOfBirth = dto.date_of_birth,
                enrollmentNumber = dto.enrollment_number,
                imageUrl = dto.image_url,
                imageLocalPath = null,
                guardianImageUrl = null,
                guardianImageLocalPath = null,
                isActive = dto.is_active,
                isDeleted = dto.is_deleted,
                guardianId = dto.guardian_id,
                guardianName = dto.guardian_name,
                guardianMobile = dto.guardian_mobile,
                guardianRelationshipName = dto.guardian_relationship_name,
                categoryId = dto.category_id,
                categoryName = dto.category_name,
                bloodGroupId = dto.blood_group_id,
                bloodGroupName = dto.blood_group_name,
                studentStatusId = dto.student_status_id,
                studentStatusName = dto.student_status_name,
                addressAreaId = dto.address_area_id,
                addressAreaName = dto.address_area_name,
                addressDetails = dto.address_details,
                classId = dto.class_id,
                className = dto.class_name,
                sectionId = dto.section_id,
                sectionName = dto.section_name,
                rollNumber = dto.roll_number,
                qrIdentityId = dto.qr_identity_id,
                qrTokenHash = dto.qr_token_hash,
                qrStatus = dto.qr_status,
                qrExpiryDate = dto.qr_expiry_date,
                idCardId = dto.id_card_id,
                cardNumber = dto.card_number,
                idCardStatus = dto.id_card_status,
                idCardReissueReason = dto.id_card_reissue_reason,
                homeLatitude = dto.home_latitude,
                homeLongitude = dto.home_longitude,
                motherTongueId = dto.mother_tongue_id,
                motherTongueName = dto.mother_tongue_name,
                religion = dto.religion,
                nationality = dto.nationality,
                identificationMark = dto.identification_mark,
                isSingleGirlChild = dto.is_single_girl_child,
                casteCertificateNumber = dto.caste_certificate_number,
                fatherName = dto.father_name,
                fatherMobile = dto.father_mobile,
                fatherEmail = dto.father_email,
                fatherQualification = dto.father_qualification,
                fatherOccupation = dto.father_occupation,
                parentsAadhaarFather = dto.parents_aadhaar_father,
                motherName = dto.mother_name,
                motherMobile = dto.mother_mobile,
                motherEmail = dto.mother_email,
                motherQualification = dto.mother_qualification,
                motherOccupation = dto.mother_occupation,
                parentsAadhaarMother = dto.parents_aadhaar_mother,
                familyAnnualIncome = dto.family_annual_income,
                permanentAddressDetails = dto.permanent_address_details,
                permanentAddressArea = dto.permanent_address_area,
                permanentAddressAreaId = dto.permanent_address_area_id,
                permanentAreaName = dto.permanent_area_name,
                previousSchoolName = dto.previous_school_name,
                previousClass = dto.previous_class,
                previousBoard = dto.previous_board,
                tcNumber = dto.tc_number,
                tcDate = dto.tc_date,
                previousMarks = dto.previous_marks,
                height = dto.height,
                weight = dto.weight,
                medicalConditions = dto.medical_conditions,
                regularMedications = dto.regular_medications,
                emergencyContactName = dto.emergency_contact_name,
                emergencyContactPhone = dto.emergency_contact_phone,
                bankAccountNumber = dto.bank_account_number,
                bankName = dto.bank_name,
                bankBranch = dto.bank_branch,
                bankIfsc = dto.bank_ifsc,
                bankAccountHolder = dto.bank_account_holder,
                studentAadhar = dto.student_aadhar,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }

        // 3. Map Additional Fees
        val localFees = payload.student_additional_fees.map { fee ->
            LocalStudentAdditionalFeeEntity(
                id = fee.id,
                organizationId = fee.organization_id,
                activeSessionId = fee.active_session_id,
                studentId = fee.student_id,
                globalFeeHeadId = fee.global_fee_head_id,
                globalFeeHeadName = fee.global_fee_head_name ?: "Supplemental Fee",
                globalFeeHeadCode = fee.global_fee_head_code,
                amount = fee.amount,
                isActive = fee.is_active,
                isDeleted = fee.is_deleted,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }

        // 4. Map Fee Payments
        val localPayments = payload.student_fee_payments.map { pay ->
            LocalStudentFeePaymentEntity(
                id = pay.id,
                organizationId = pay.organization_id,
                activeSessionId = pay.active_session_id,
                studentId = pay.student_id,
                receiptNumber = pay.receipt_number,
                paymentMode = pay.payment_mode,
                paymentDate = pay.payment_date,
                amountPaid = pay.amount_paid,
                discountAmount = pay.discount_amount,
                fineAmount = pay.fine_amount,
                discountReason = pay.discount_reason,
                remarks = pay.remarks,
                status = pay.status,
                cashReceivedByUserId = pay.cash_received_by_user_id,
                cashReceivedByUserName = pay.cash_received_by_user_name,
                chequeNumber = pay.cheque_number,
                chequeDate = null,
                chequeBankName = null,
                onlineTransactionId = pay.online_transaction_id,
                onlinePaymentApp = null,
                isActive = pay.is_active,
                isDeleted = pay.is_deleted,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }

        // 5. Map Attendance
        val localAttendance = payload.student_attendance.map { att ->
            LocalStudentAttendanceEntity(
                id = att.id,
                organizationId = att.organization_id,
                activeSessionId = att.active_session_id,
                studentId = att.student_id,
                studentName = att.student_name,
                rollNumber = att.roll_number,
                classId = att.class_id,
                className = att.class_name,
                sectionId = att.section_id,
                sectionName = att.section_name,
                attendanceDate = att.attendance_date,
                status = att.status,
                remarks = att.remarks,
                markedByStaffId = att.marked_by_staff_id,
                markedByStaffName = att.marked_by_staff_name,
                isActive = att.is_active,
                isDeleted = att.is_deleted,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }

        // 6. Map Buses
        val localBuses = payload.buses.map { bus ->
            LocalParentBusEntity(
                id = bus.id,
                parentOrganizationId = bus.parent_organization_id,
                activeSessionId = bus.active_session_id,
                busNumber = bus.bus_number,
                busName = bus.bus_name,
                routeName = bus.route_name,
                maxCapacity = bus.max_capacity,
                isActive = bus.is_active,
                isDeleted = bus.is_deleted,
                insuranceExpiryDate = bus.insurance_expiry_date,
                insuranceImageUrl = bus.insurance_image_url,
                insuranceImageLocalPath = null,
                fitnessExpiryDate = bus.fitness_expiry_date,
                fitnessImageUrl = bus.fitness_image_url,
                fitnessImageLocalPath = null,
                pollutionExpiryDate = bus.pollution_expiry_date,
                pollutionImageUrl = bus.pollution_image_url,
                pollutionImageLocalPath = null,
                driverId = bus.driver_id,
                driverName = bus.driver_name,
                driverMobile = bus.driver_mobile,
                conductorId = bus.conductor_id,
                conductorName = bus.conductor_name,
                conductorMobile = bus.conductor_mobile,
                lastLatitude = null,
                lastLongitude = null,
                lastLocationUpdatedAt = null,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }

        // 7. Map Bus Routes
        val localRoutes = payload.bus_routes.map { route ->
            LocalBusRouteEntity(
                id = route.id,
                parentOrganizationId = route.parent_organization_id,
                activeSessionId = route.active_session_id,
                busId = route.bus_id,
                stopName = route.stop_name,
                stopOrder = route.stop_order,
                latitude = route.latitude,
                longitude = route.longitude,
                scheduledTime = route.scheduled_time,
                isActive = route.is_active,
                isDeleted = route.is_deleted
            )
        }

        // 8. Map Staff Members
        val localStaff = payload.staff_members.map { staff ->
            LocalParentStaffEntity(
                id = staff.id,
                parentOrganizationId = staff.parent_organization_id,
                activeSessionId = staff.active_session_id,
                name = staff.name,
                mobileNumber = staff.mobile_number,
                email = staff.email,
                gender = staff.gender,
                dateOfJoining = staff.date_of_joining,
                dateOfBirth = staff.date_of_birth,
                panNumber = staff.pan_number,
                aadhaarNumber = staff.aadhaar_number,
                licenseNumber = staff.license_number,
                licenseExpiryDate = staff.license_expiry_date,
                isActive = staff.is_active,
                isDeleted = staff.is_deleted,
                roleId = staff.role_id,
                roleName = null,
                subjectId = staff.subject_id,
                subjectName = null,
                addressAreaId = staff.address_area_id,
                addressAreaName = null,
                leaveQuotaId = null,
                totalLeaves = 12.0,
                salaryId = null,
                monthlySalary = 0.0,
                bankName = null,
                bankAccountNumber = null,
                ifscCode = null,
                upiId = null,
                busAssignmentId = null,
                busId = null,
                busNumber = null,
                busName = null,
                routeName = null,
                roleInBus = null,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }

        // 9. Map Expenses
        val localExpenses = payload.expenses.map { ex ->
            LocalParentExpenseEntity(
                id = ex.id,
                parentOrganizationId = ex.parent_organization_id,
                activeSessionId = ex.active_session_id,
                expenseTypeId = ex.expense_type_id,
                expenseTypeName = null,
                expenseTypeCode = null,
                paymentMethod = ex.payment_method,
                amount = ex.amount,
                receiptUuid = ex.receipt_uuid,
                receiptLocalPath = null,
                adminNote = ex.admin_note,
                expenseDate = ex.expense_date,
                referenceId = ex.reference_id,
                referenceType = ex.reference_type,
                referenceName = null,
                cashPaidTo = null,
                chequeNumber = null,
                chequeDate = null,
                chequeBankName = null,
                onlineTransactionId = null,
                onlinePaymentApp = null,
                vendorName = ex.vendor_name,
                billNumber = ex.bill_number,
                isActive = ex.is_active,
                createdBy = ex.created_by,
                createdByName = null,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }

        // 10. Map Exams
        val localExams = payload.exams.map { exam ->
            LocalOrganizationExamEntity(
                id = exam.id,
                organizationId = exam.organization_id,
                activeSessionId = exam.active_session_id,
                examTypeId = exam.exam_type_id,
                examTypeName = null,
                examTypeCode = null,
                name = exam.name,
                startDate = exam.start_date,
                endDate = exam.end_date,
                isActive = exam.is_active,
                isDeleted = exam.is_deleted,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }

        // 11. Map Bus Trips
        val localTrips = payload.bus_trips.map { t ->
            LocalParentBusTripEntity(
                id = t.id,
                parentOrganizationId = t.parent_organization_id,
                activeSessionId = t.active_session_id,
                busId = t.bus_id,
                busNumber = t.bus_number,
                busName = t.bus_name,
                driverId = t.driver_id,
                driverName = t.driver_name,
                driverPhone = t.driver_phone,
                tripType = t.trip_type,
                status = t.status,
                startTime = t.start_time,
                endTime = t.end_time,
                isActive = t.is_active,
                isDeleted = t.is_deleted,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }

        // 12. Map Bus Trip Logs
        val localTripLogs = payload.bus_trip_attendance_logs.map { l ->
            LocalParentBusTripAttendanceLogEntity(
                id = l.id,
                parentOrganizationId = l.parent_organization_id,
                organizationId = l.organization_id,
                activeSessionId = l.active_session_id,
                tripId = l.trip_id,
                studentId = l.student_id,
                studentName = l.student_name,
                rollNumber = l.roll_number,
                className = l.class_name,
                sectionName = l.section_name,
                status = l.status,
                scanLatitude = l.scan_latitude,
                scanLongitude = l.scan_longitude,
                scannedAt = l.scanned_at,
                scannedByStaffId = l.scanned_by_staff_id,
                scannedByStaffName = l.scanned_by_staff_name,
                isActive = l.is_active,
                isDeleted = l.is_deleted,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }

        // 13. Map Calendar Events
        val localCalendarEvents = payload.calendar_events.map { ev ->
            LocalCalendarEventEntity(
                id = ev.id,
                parentOrganizationId = ev.parent_organization_id,
                parentOrganizationName = null,
                organizationId = ev.organization_id,
                organizationName = null,
                activeSessionId = ev.active_session_id,
                name = ev.name,
                description = ev.description,
                startDate = ev.start_date,
                endDate = ev.end_date,
                eventType = ev.event_type,
                isSchoolClosed = ev.is_school_closed,
                isActive = ev.is_active,
                isDeleted = ev.is_deleted,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }

        // 14. Map Exam Subject Settings
        val localExamSettings = payload.exam_subject_settings.map { s ->
            LocalExamSubjectSettingEntity(
                id = s.id,
                organizationId = s.organization_id,
                activeSessionId = s.active_session_id,
                examId = s.exam_id,
                classId = s.class_id,
                subjectId = s.subject_id,
                className = s.class_name,
                subjectName = s.subject_name,
                maxMarks = s.max_marks,
                minimumPassingMarks = s.minimum_passing_marks,
                gradingSystem = s.grading_system?.toString(),
                isDeleted = s.is_deleted,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }

        android.util.Log.d("OfflineSync", "Overwriting local database atomically with replaceWorkspaceDataPayload transaction...")
        dao.replaceWorkspaceDataPayload(
            setup = localSetup,
            students = localStudents,
            fees = localFees,
            payments = localPayments,
            attendance = localAttendance,
            buses = localBuses,
            routes = localRoutes,
            staff = localStaff,
            expenses = localExpenses,
            exams = localExams,
            trips = localTrips,
            tripLogs = localTripLogs,
            calendarEvents = localCalendarEvents,
            examSettings = localExamSettings
        )
        
        android.util.Log.d("OfflineSync", "Consolidated Sync Workspace Completed Successfully!")
    }
    
    override suspend fun searchStudentsOffline(query: String, classFilterName: String?, sectionFilterName: String?): Result<List<StudentSearchResult>> = runCatching {
        val local = dao.searchStudentsOfflineWithFilters(query, classFilterName, sectionFilterName)
        local.map { 
            StudentSearchResult(
                id = it.id,
                name = it.name,
                roll_number = it.rollNumber,
                class_name = it.className,
                section_name = it.sectionName,
                sr_number = it.srNumber,
                guardian_name = it.guardianName,
                guardian_mobile = it.guardianMobile,
                image_url = it.imageUrl
            )
        }
    }
    
    override suspend fun getLocalStudentById(studentId: String): Result<LocalStudentEntity?> = runCatching {
        var student = dao.getStudentById(studentId)
        if (student != null && student.className.isNullOrEmpty()) {
            fetchAndUpdateStudentClassName(studentId)?.let { updated ->
                student = updated
            }
        }
        student
    }
    
    override suspend fun getOfflineStaff(parentOrgId: String): Result<List<LocalParentStaffEntity>> = runCatching {
        dao.getStaffProfiles(parentOrgId)
    }

    override suspend fun syncStudentAdditionalFees(studentId: String): Result<List<LocalStudentAdditionalFeeEntity>> = runCatching {
        val remote = remoteDataSource.fetchStudentAdditionalFees(studentId)
        val entities = remote.map { fee ->
            val headName = try {
                SupabaseClient.client.from("global_fee_heads")
                    .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("name")) {
                        filter { eq("id", fee.global_fee_head_id) }
                    }.decodeSingleOrNull<FeeHeadNameDto>()?.name
            } catch(e: Exception) {
                null
            }
            LocalStudentAdditionalFeeEntity(
                id = fee.id,
                organizationId = fee.organization_id,
                activeSessionId = fee.active_session_id,
                studentId = fee.student_id,
                globalFeeHeadId = fee.global_fee_head_id,
                globalFeeHeadName = headName ?: "Supplemental Fee",
                globalFeeHeadCode = null,
                amount = fee.amount,
                isActive = fee.is_active,
                isDeleted = fee.is_deleted,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "SYNCED"
            )
        }
        dao.insertStudentAdditionalFees(entities)
        entities
    }

    override suspend fun syncStudentProfileDetails(studentId: String): Result<LocalStudentEntity?> = runCatching {
        fetchAndUpdateStudentClassName(studentId)
    }
    
    override suspend fun getActiveBusTrip(driverId: String): Result<ParentBusTrip?> = runCatching {
        dao.getActiveBusTripForDriver(driverId)?.toDomain()
    }
    
    override suspend fun getParentBusTripsForDriver(driverId: String): Result<List<ParentBusTrip>> = runCatching {
        val local = dao.getParentBusTripsForDriver(driverId)
        local.map { it.toDomain() }
    }
    
    override suspend fun getStudentByQrHash(hash: String): Result<LocalStudentEntity?> = runCatching {
        dao.getStudentByQrHash(hash)
    }
    
    override suspend fun startBusTrip(
        parentOrgId: String,
        sessionId: String,
        busId: String,
        driverId: String,
        tripType: String
    ): Result<ParentBusTrip> = runCatching {
        val id = java.util.UUID.randomUUID().toString()
        val bus = dao.getBusById(busId)
        val driver = dao.getStaffProfileById(driverId)
        val trip = LocalParentBusTripEntity(
            id = id,
            parentOrganizationId = parentOrgId,
            activeSessionId = sessionId,
            busId = busId,
            busNumber = bus?.busNumber,
            busName = bus?.busName,
            driverId = driverId,
            driverName = driver?.name,
            driverPhone = driver?.mobileNumber,
            tripType = tripType,
            status = "Ongoing",
            startTime = System.currentTimeMillis().toString(),
            endTime = null,
            isActive = true,
            isDeleted = false,
            lastSyncedAt = System.currentTimeMillis(),
            syncState = "PENDING_UPDATE"
        )
        dao.insertParentBusTrip(trip)
        
        // Sync push
        val dto = ParentBusTripDto(
            id = trip.id,
            parent_organization_id = trip.parentOrganizationId,
            active_session_id = trip.activeSessionId,
            bus_id = trip.busId,
            driver_id = trip.driverId,
            trip_type = trip.tripType,
            status = trip.status,
            start_time = trip.startTime,
            end_time = trip.endTime,
            is_active = trip.isActive,
            is_deleted = trip.isDeleted
        )
        runCatching {
            remoteDataSource.upsertBusTrip(dto)
        }
        trip.toDomain()
    }
    
    override suspend fun endBusTrip(tripId: String): Result<Unit> = runCatching {
        dao.updateBusTripStatus(tripId, "Completed", null, System.currentTimeMillis().toString(), "PENDING_UPDATE")
        val trip = dao.getBusTripById(tripId)
        if (trip != null) {
            val dto = ParentBusTripDto(
                id = trip.id,
                parent_organization_id = trip.parentOrganizationId,
                active_session_id = trip.activeSessionId,
                bus_id = trip.busId,
                driver_id = trip.driverId,
                trip_type = trip.tripType,
                status = trip.status,
                start_time = trip.startTime,
                end_time = trip.endTime,
                is_active = trip.isActive,
                is_deleted = trip.isDeleted
            )
            runCatching {
                remoteDataSource.upsertBusTrip(dto)
            }
        }
    }
    
    override suspend fun submitBusAttendanceLog(log: ParentBusTripAttendanceLog): Result<ParentBusTripAttendanceLog> = runCatching {
        val student = dao.getStudentById(log.studentId)
        val entity = LocalParentBusTripAttendanceLogEntity(
            id = log.id,
            parentOrganizationId = log.parentOrganizationId,
            organizationId = log.organizationId,
            activeSessionId = log.activeSessionId,
            tripId = log.tripId,
            studentId = log.studentId,
            studentName = student?.name ?: "",
            rollNumber = student?.rollNumber,
            className = student?.className,
            sectionName = student?.sectionName,
            status = log.status,
            scanLatitude = log.scanLatitude,
            scanLongitude = log.scanLongitude,
            scannedAt = log.scannedAt,
            scannedByStaffId = log.scannedByStaffId,
            scannedByStaffName = log.createdBy,
            isActive = true,
            isDeleted = false,
            lastSyncedAt = System.currentTimeMillis(),
            syncState = "PENDING_INSERT"
        )
        dao.insertParentBusTripAttendanceLog(entity)
        
        // Immediate sync push
        val dto = ParentBusTripAttendanceLogDto(
            id = entity.id,
            parent_organization_id = entity.parentOrganizationId,
            organization_id = entity.organizationId,
            active_session_id = entity.activeSessionId,
            trip_id = entity.tripId,
            student_id = entity.studentId,
            status = entity.status,
            scan_latitude = entity.scanLatitude,
            scan_longitude = entity.scanLongitude,
            scanned_at = entity.scannedAt,
            scanned_by_staff_id = entity.scannedByStaffId,
            is_active = entity.isActive,
            is_deleted = entity.isDeleted
        )
        runCatching {
            remoteDataSource.upsertBusAttendanceLogs(listOf(dto))
            dao.markBusTripAttendanceLogSynced(entity.id)
        }
        log
    }
    
    override suspend fun getBusTripAttendanceLogs(tripId: String, forceRefresh: Boolean): Result<List<ParentBusTripAttendanceLog>> = runCatching {
        if (forceRefresh || dao.getBusTripAttendanceLogs(tripId).isEmpty()) {
            val list = remoteDataSource.fetchBusTripAttendanceLogs(tripId)
            dao.insertParentBusTripAttendanceLogs(
                list.map { log ->
                    LocalParentBusTripAttendanceLogEntity(
                        id = log.id,
                        parentOrganizationId = log.parent_organization_id,
                        organizationId = log.organization_id,
                        activeSessionId = log.active_session_id,
                        tripId = log.trip_id,
                        studentId = log.student_id,
                        studentName = null,
                        rollNumber = null,
                        className = null,
                        sectionName = null,
                        status = log.status,
                        scanLatitude = log.scan_latitude,
                        scanLongitude = log.scan_longitude,
                        scannedAt = log.scanned_at,
                        scannedByStaffId = log.scanned_by_staff_id,
                        scannedByStaffName = null,
                        isActive = log.is_active,
                        isDeleted = log.is_deleted,
                        lastSyncedAt = System.currentTimeMillis(),
                        syncState = "SYNCED"
                    )
                }
            )
        }
        val local = dao.getBusTripAttendanceLogs(tripId)
        local.map { it.toDomain() }
    }
    
    override suspend fun getBusTripAttendanceLogsWithStudentInfo(tripId: String): Result<List<LocalParentBusTripAttendanceLogWithStudentInfo>> = runCatching {
        dao.getBusTripAttendanceLogsWithStudentInfo(tripId)
    }
    
    override suspend fun syncOfflineAttendanceLogs(): Result<Int> = runCatching {
        val unsynced = dao.getUnsyncedBusTripAttendanceLogs()
        if (unsynced.isNotEmpty()) {
            val dtos = unsynced.map { log ->
                ParentBusTripAttendanceLogDto(
                    id = log.id,
                    parent_organization_id = log.parentOrganizationId,
                    organization_id = log.organizationId,
                    active_session_id = log.activeSessionId,
                    trip_id = log.tripId,
                    student_id = log.studentId,
                    status = log.status,
                    scan_latitude = log.scanLatitude,
                    scan_longitude = log.scanLongitude,
                    scanned_at = log.scannedAt,
                    scanned_by_staff_id = log.scannedByStaffId,
                    is_active = log.isActive,
                    is_deleted = log.isDeleted
                )
            }
            remoteDataSource.upsertBusAttendanceLogs(dtos)
            unsynced.forEach { dao.markBusTripAttendanceLogSynced(it.id) }
        }
        unsynced.size
    }
    
    override suspend fun getRemarks(sessionId: String): Result<List<Remark>> = runCatching {
        val local = dao.getAllRemarks()
        local.map { it.toRemark() }
    }
    
    override suspend fun getRemarkTargets(remarkId: String): Result<List<RemarkTarget>> = runCatching {
        val local = dao.getAllRemarks().filter { it.id == remarkId }
        local.map { it.toRemarkTarget() }
    }
    
    override suspend fun addRemark(remark: Remark, targets: List<RemarkTarget>): Result<Unit> = runCatching {
        val entities = targets.map { target ->
            LocalOrganizationRemarkEntity(
                targetId = target.id,
                id = remark.id,
                parentOrganizationId = remark.parentOrgId,
                organizationId = remark.organizationId,
                activeSessionId = remark.activeSessionId,
                content = remark.content,
                category = remark.category,
                priority = remark.priority,
                creatorUserId = remark.creatorUserId,
                creatorUserName = remark.createdBy,
                creatorRoleName = remark.creatorWorkspaceRoleId,
                visibilityType = remark.visibilityType,
                visibilityAudienceJson = "[]",
                isPinned = remark.isPinned,
                pinExpiresAt = remark.pinExpiresAt,
                expiresAt = remark.expiresAt,
                targetType = target.targetType,
                targetStudentId = target.targetStudentId,
                targetStudentName = null,
                targetClassName = null,
                targetSectionName = null,
                targetGuardianId = target.targetGuardianId,
                targetGuardianName = null,
                targetStaffId = target.targetStaffId,
                targetStaffName = null,
                targetUserId = target.targetUserId,
                attachmentsJson = "[]",
                isActive = remark.isActive,
                isDeleted = remark.isDeleted,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "PENDING_INSERT"
            )
        }
        entities.forEach { dao.insertRemark(it) }
        
        // Sync push
        val dtos = entities.map { item ->
            OrganizationRemarkDto(
                id = item.id,
                parent_organization_id = item.parentOrganizationId,
                organization_id = item.organizationId,
                active_session_id = item.activeSessionId,
                content = item.content,
                category = item.category,
                priority = item.priority,
                creator_user_id = item.creatorUserId,
                visibility_type = item.visibilityType,
                visibility_audience_json = item.visibilityAudienceJson ?: "[]",
                is_pinned = item.isPinned,
                pin_expires_at = item.pinExpiresAt,
                expires_at = item.expiresAt,
                target_id = item.targetId,
                target_type = item.targetType,
                target_student_id = item.targetStudentId,
                target_guardian_id = item.targetGuardianId,
                target_staff_id = item.targetStaffId,
                target_user_id = item.targetUserId,
                attachments_json = item.attachmentsJson ?: "[]",
                is_active = item.isActive,
                is_deleted = item.isDeleted
            )
        }
        runCatching {
            remoteDataSource.upsertRemarks(dtos)
            entities.forEach { dao.markRemarkSynced(it.id) }
        }
    }
    
    override suspend fun softDeleteRemark(remarkId: String): Result<Unit> = runCatching {
        dao.softDeleteRemark(remarkId)
    }
    
    override suspend fun getGlobalStaffRoles(): Result<List<GlobalStaffRole>> = Result.success(emptyList())
    
    override suspend fun loadGlobalStaffRolesFromServer(): Result<Unit> = Result.success(Unit)
    
    override suspend fun syncRemarksOffline(): Result<Unit> = runCatching {
        val unsynced = dao.getUnsyncedRemarks()
        if (unsynced.isNotEmpty()) {
            val dtos = unsynced.map { remark ->
                OrganizationRemarkDto(
                    id = remark.id,
                    parent_organization_id = remark.parentOrganizationId,
                    organization_id = remark.organizationId,
                    active_session_id = remark.activeSessionId,
                    content = remark.content,
                    category = remark.category,
                    priority = remark.priority,
                    creator_user_id = remark.creatorUserId,
                    visibility_type = remark.visibilityType,
                    visibility_audience_json = remark.visibilityAudienceJson ?: "[]",
                    is_pinned = remark.isPinned,
                    pin_expires_at = remark.pinExpiresAt,
                    expires_at = remark.expiresAt,
                    target_id = remark.targetId,
                    target_type = remark.targetType,
                    target_student_id = remark.targetStudentId,
                    target_guardian_id = remark.targetGuardianId,
                    target_staff_id = remark.targetStaffId,
                    target_user_id = remark.targetUserId,
                    attachments_json = remark.attachmentsJson ?: "[]",
                    is_active = remark.isActive,
                    is_deleted = remark.isDeleted
                )
            }
            remoteDataSource.upsertRemarks(dtos)
            unsynced.forEach { dao.markRemarkSynced(it.id) }
        }
    }
    
    override suspend fun fetchRemarksFromServer(sessionId: String, parentOrgId: String): Result<Unit> = runCatching {
        val list = remoteDataSource.fetchRemarks(parentOrgId)
        dao.insertRemarks(
            list.map { remark ->
                LocalOrganizationRemarkEntity(
                    targetId = remark.target_id,
                    id = remark.id,
                    parentOrganizationId = remark.parent_organization_id,
                    organizationId = remark.organization_id,
                    activeSessionId = remark.active_session_id,
                    content = remark.content,
                    category = remark.category,
                    priority = remark.priority,
                    creatorUserId = remark.creator_user_id,
                    creatorUserName = null,
                    creatorRoleName = null,
                    visibilityType = remark.visibility_type,
                    visibilityAudienceJson = remark.visibility_audience_json,
                    isPinned = remark.is_pinned,
                    pinExpiresAt = remark.pin_expires_at,
                    expiresAt = remark.expires_at,
                    targetType = remark.target_type,
                    targetStudentId = remark.target_student_id,
                    targetStudentName = null,
                    targetClassName = null,
                    targetSectionName = null,
                    targetGuardianId = remark.target_guardian_id,
                    targetGuardianName = null,
                    targetStaffId = remark.target_staff_id,
                    targetStaffName = null,
                    targetUserId = remark.target_user_id,
                    attachmentsJson = remark.attachments_json,
                    isActive = remark.is_active,
                    isDeleted = remark.is_deleted,
                    lastSyncedAt = System.currentTimeMillis(),
                    syncState = "SYNCED"
                )
            }
        )
    }
    
    override suspend fun getStudentsAssignedToBus(busId: String): Result<List<LocalStudentEntity>> = Result.success(emptyList())
    
    override suspend fun getStudentHomeLocation(studentId: String): Result<StudentHomeLocation?> = runCatching {
        try {
            val response = SupabaseClient.client.from("organization_student_home_locations")
                .select {
                    filter {
                        eq("student_id", studentId)
                    }
                }.decodeSingleOrNull<StudentHomeLocation>()
            if (response != null) {
                val student = dao.getStudentById(studentId)
                if (student != null && (student.homeLatitude != response.latitude || student.homeLongitude != response.longitude)) {
                    val updated = student.copy(
                        homeLatitude = response.latitude,
                        homeLongitude = response.longitude
                    )
                    dao.insertStudents(listOf(updated))
                }
                return@runCatching response
            }
        } catch (e: Exception) {
            android.util.Log.e("VidyaSetu_HomeLoc", "Error fetching remote home location, falling back to local DB", e)
        }

        val student = dao.getStudentById(studentId) ?: return@runCatching null
        if (student.homeLatitude != null && student.homeLongitude != null) {
            StudentHomeLocation(
                organizationId = student.organizationId,
                studentId = student.id,
                latitude = student.homeLatitude,
                longitude = student.homeLongitude
            )
        } else {
            null
        }
    }
    
    override suspend fun saveStudentHomeLocation(
        organizationId: String,
        studentId: String,
        latitude: Double,
        longitude: Double,
        userId: String
    ): Result<Unit> = runCatching {
        val student = dao.getStudentById(studentId)
        
        android.util.Log.d("VidyaSetu_HomeLocation", "saveStudentHomeLocation: studentId=$studentId, organizationId=$organizationId, userId=$userId, latitude=$latitude, longitude=$longitude")
        
        val json = buildJsonObject {
            put("organization_id", organizationId)
            put("student_id", studentId)
            put("latitude", latitude)
            put("longitude", longitude)
            put("created_by", userId)
            put("updated_by", userId)
        }
        
        try {
            SupabaseClient.client.from("organization_student_home_locations").upsert(json) {
                onConflict = "student_id"
            }
            android.util.Log.d("VidyaSetu_HomeLocation", "saveStudentHomeLocation: Success saving to remote database")
        } catch (e: Exception) {
            android.util.Log.e("VidyaSetu_HomeLocation", "saveStudentHomeLocation: FAILED saving to remote database! Error: ${e.message}", e)
            throw e
        }

        if (student != null) {
            val updated = student.copy(
                homeLatitude = latitude,
                homeLongitude = longitude
            )
            dao.insertStudents(listOf(updated))
            android.util.Log.d("VidyaSetu_HomeLocation", "saveStudentHomeLocation: Success saving to local SQLite DB")
        }
    }
    
    override suspend fun getStudentLinkByUserId(userId: String): Result<LocalStudentUserLinkEntity?> = runCatching {
        dao.getStudentUserLink(userId)
    }
    
    override suspend fun getCalendarEvents(parentOrgId: String, sessionId: String, forceRefresh: Boolean): Result<List<CalendarEvent>> = runCatching {
        if (forceRefresh || dao.getCalendarEvents(parentOrgId).isEmpty()) {
            val list = remoteDataSource.fetchCalendarEvents(parentOrgId)
            dao.insertCalendarEvents(
                list.map { event ->
                    LocalCalendarEventEntity(
                        id = event.id,
                        parentOrganizationId = event.parent_organization_id,
                        parentOrganizationName = null,
                        organizationId = event.organization_id,
                        organizationName = null,
                        activeSessionId = event.active_session_id,
                        name = event.name,
                        description = event.description,
                        startDate = event.start_date,
                        endDate = event.end_date,
                        eventType = event.event_type,
                        isSchoolClosed = event.is_school_closed,
                        isActive = event.is_active,
                        isDeleted = event.is_deleted,
                        lastSyncedAt = System.currentTimeMillis(),
                        syncState = "SYNCED"
                    )
                }
            )
        }
        val local = dao.getCalendarEvents(parentOrgId)
        local.map { it.toDomain() }
    }
    
    override suspend fun addCalendarEvent(event: CalendarEvent): Result<Unit> = runCatching {
        val entity = LocalCalendarEventEntity(
            id = event.id,
            parentOrganizationId = event.parentOrganizationId,
            parentOrganizationName = event.parentOrganizationName,
            organizationId = event.organizationId,
            organizationName = event.organizationName,
            activeSessionId = event.activeSessionId,
            name = event.name,
            description = event.description,
            startDate = event.startDate,
            endDate = event.endDate,
            eventType = event.eventType,
            isSchoolClosed = event.isSchoolClosed,
            isActive = true,
            isDeleted = false,
            lastSyncedAt = System.currentTimeMillis(),
            syncState = "PENDING_INSERT"
        )
        dao.insertCalendarEvent(entity)
    }
    
    override suspend fun getStaffAttendance(parentOrgId: String, date: String, forceRefresh: Boolean): Result<List<StaffAttendance>> = runCatching {
        if (forceRefresh || dao.getStaffAttendance(parentOrgId, date).isEmpty()) {
            val list = remoteDataSource.fetchStaffAttendance(parentOrgId, date)
            dao.insertStaffAttendance(
                list.map { item ->
                    LocalParentStaffAttendanceEntity(
                        id = item.id,
                        parentOrganizationId = item.parent_organization_id,
                        activeSessionId = item.active_session_id,
                        staffId = item.staff_id,
                        staffName = null,
                        staffRole = null,
                        attendanceDate = item.attendance_date,
                        statusId = item.status_id,
                        statusCode = "",
                        statusName = "",
                        isPaidLeave = item.is_paid_leave,
                        checkInTime = item.check_in_time,
                        checkOutTime = item.check_out_time,
                        remarks = item.remarks,
                        isActive = item.is_active,
                        isDeleted = item.is_deleted,
                        lastSyncedAt = System.currentTimeMillis(),
                        syncState = "SYNCED"
                    )
                }
            )
        }
        val local = dao.getStaffAttendance(parentOrgId, date)
        local.map { it.toDomain() }
    }
    
    override suspend fun submitStaffAttendance(attendanceList: List<StaffAttendance>): Result<Unit> = runCatching {
        val entities = attendanceList.map { item ->
            LocalParentStaffAttendanceEntity(
                id = item.id,
                parentOrganizationId = item.parentOrganizationId,
                activeSessionId = item.activeSessionId,
                staffId = item.staffId,
                staffName = item.staffName,
                staffRole = item.staffRole,
                attendanceDate = item.attendanceDate,
                statusId = item.statusId,
                statusCode = item.statusCode,
                statusName = item.statusName,
                isPaidLeave = item.isPaidLeave,
                checkInTime = item.checkInTime,
                checkOutTime = item.checkOutTime,
                remarks = item.remarks,
                isActive = true,
                isDeleted = false,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "PENDING_INSERT"
            )
        }
        dao.insertStaffAttendance(entities)
    }
    
    override suspend fun getOrganizationExams(orgId: String, sessionId: String, forceRefresh: Boolean): Result<List<OrganizationExam>> = runCatching {
        if (forceRefresh || dao.getExams(orgId, sessionId).isEmpty()) {
            val list = remoteDataSource.fetchExams(orgId, sessionId)
            dao.insertExams(
                list.map { exam ->
                    LocalOrganizationExamEntity(
                        id = exam.id,
                        organizationId = exam.organization_id,
                        activeSessionId = exam.active_session_id,
                        examTypeId = exam.exam_type_id,
                        examTypeName = null,
                        examTypeCode = null,
                        name = exam.name,
                        startDate = exam.start_date,
                        endDate = exam.end_date,
                        isActive = exam.is_active,
                        isDeleted = exam.is_deleted,
                        lastSyncedAt = System.currentTimeMillis(),
                        syncState = "SYNCED"
                    )
                }
            )
        }
        val local = dao.getExams(orgId, sessionId)
        local.map { 
            OrganizationExam(
                id = it.id,
                organizationId = it.organizationId,
                activeSessionId = it.activeSessionId,
                examTypeId = it.examTypeId,
                examTypeName = it.examTypeName,
                examTypeCode = it.examTypeCode,
                name = it.name,
                startDate = it.startDate,
                endDate = it.endDate
            )
        }
    }
    
    override suspend fun getExamSubjectSettings(orgId: String, sessionId: String, forceRefresh: Boolean): Result<List<ExamSubjectSetting>> = runCatching {
        if (forceRefresh || dao.getExamSubjectSettings(orgId, sessionId).isEmpty()) {
            val list = remoteDataSource.fetchExamSubjectSettings(orgId, sessionId)
            dao.insertExamSubjectSettings(
                list.map { setting ->
                    LocalExamSubjectSettingEntity(
                        id = setting.id,
                        organizationId = setting.organization_id,
                        activeSessionId = setting.active_session_id,
                        examId = setting.exam_id,
                        classId = setting.class_id,
                        subjectId = setting.subject_id,
                        className = null,
                        subjectName = null,
                        maxMarks = setting.max_marks,
                        minimumPassingMarks = setting.minimum_passing_marks,
                        gradingSystem = setting.grading_system?.toString(),
                        isDeleted = setting.is_deleted,
                        lastSyncedAt = System.currentTimeMillis(),
                        syncState = "SYNCED"
                    )
                }
            )
        }
        val local = dao.getExamSubjectSettings(orgId, sessionId)
        local.map { 
            ExamSubjectSetting(
                id = it.id,
                organizationId = it.organizationId,
                activeSessionId = it.activeSessionId,
                examId = it.examId,
                classId = it.classId,
                subjectId = it.subjectId,
                className = it.className,
                subjectName = it.subjectName,
                maxMarks = it.maxMarks,
                minimumPassingMarks = it.minimumPassingMarks,
                gradingSystem = it.gradingSystem
            )
        }
    }
    
    override suspend fun getStudentExamMarks(examId: String, classId: String, subjectId: String, forceRefresh: Boolean): Result<List<StudentExamMark>> = runCatching {
        if (forceRefresh || dao.getStudentExamMarks(examId, classId, subjectId).isEmpty()) {
            val list = remoteDataSource.fetchStudentExamMarks(examId, classId, subjectId)
            dao.insertStudentExamMarks(
                list.map { mark ->
                    LocalStudentExamMarkEntity(
                        id = mark.id,
                        organizationId = mark.organization_id,
                        activeSessionId = mark.active_session_id,
                        examId = mark.exam_id,
                        classId = mark.class_id,
                        subjectId = mark.subject_id,
                        studentId = mark.student_id,
                        obtainedMarks = mark.obtained_marks,
                        isAbsent = mark.is_absent,
                        isMedicalLeave = mark.is_medical_leave,
                        teacherRemarks = mark.teacher_remarks,
                        isActive = mark.is_active,
                        isDeleted = mark.is_deleted,
                        studentName = null,
                        rollNumber = null,
                        subjectName = null,
                        examName = null,
                        maxMarks = null,
                        minimumPassingMarks = null,
                        lastSyncedAt = System.currentTimeMillis(),
                        syncState = "SYNCED"
                    )
                }
            )
        }
        val local = dao.getStudentExamMarks(examId, classId, subjectId)
        local.map { 
            StudentExamMark(
                id = it.id,
                organizationId = it.organizationId,
                activeSessionId = it.activeSessionId,
                examId = it.examId,
                classId = it.classId,
                subjectId = it.subjectId,
                studentId = it.studentId,
                obtainedMarks = it.obtainedMarks,
                isAbsent = it.isAbsent,
                isMedicalLeave = it.isMedicalLeave,
                teacherRemarks = it.teacherRemarks,
                studentName = it.studentName,
                rollNumber = it.rollNumber,
                subjectName = it.subjectName,
                examName = it.examName,
                maxMarks = it.maxMarks,
                minimumPassingMarks = it.minimumPassingMarks
            )
        }
    }
    
    override suspend fun submitStudentExamMarks(marksList: List<StudentExamMark>): Result<Unit> = runCatching {
        val entities = marksList.map { mark ->
            LocalStudentExamMarkEntity(
                id = mark.id,
                organizationId = mark.organizationId,
                activeSessionId = mark.activeSessionId,
                examId = mark.examId,
                classId = mark.classId,
                subjectId = mark.subjectId,
                studentId = mark.studentId,
                obtainedMarks = mark.obtainedMarks,
                isAbsent = mark.isAbsent,
                isMedicalLeave = mark.isMedicalLeave,
                teacherRemarks = mark.teacherRemarks,
                isActive = true,
                isDeleted = false,
                studentName = mark.studentName,
                rollNumber = mark.rollNumber,
                subjectName = mark.subjectName,
                examName = mark.examName,
                maxMarks = mark.maxMarks,
                minimumPassingMarks = mark.minimumPassingMarks,
                lastSyncedAt = System.currentTimeMillis(),
                syncState = "PENDING_INSERT"
            )
        }
        dao.insertStudentExamMarks(entities)
        
        // Immediate sync push
        val dtos = entities.map { mark ->
            StudentExamMarkDto(
                id = mark.id,
                organization_id = mark.organizationId,
                active_session_id = mark.activeSessionId,
                exam_id = mark.examId,
                class_id = mark.classId,
                subject_id = mark.subjectId,
                student_id = mark.studentId,
                obtained_marks = mark.obtainedMarks,
                is_absent = mark.isAbsent,
                is_medical_leave = mark.isMedicalLeave,
                teacher_remarks = mark.teacherRemarks,
                is_active = mark.isActive,
                is_deleted = mark.isDeleted
            )
        }
        runCatching {
            remoteDataSource.upsertStudentExamMarks(dtos)
            entities.forEach { dao.markStudentExamMarkSynced(it.id) }
        }
    }

    // ── Mapping Extension Helpers ─────────────────────────────────────────────
    
    private fun WorkspaceEntity.toDomain() = Workspace(
        id = workspaceId,
        parentOrgId = parentOrganizationId,
        parentOrgName = parentOrganizationName ?: "",
        childOrgId = childOrganizationId,
        childOrgName = childOrganizationName,
        role = workspaceRole,
        isActive = isActiveNow,
        roleDisplayName = roleDisplayName,
        workspaceSubRole = workspaceSubRole,
        roleImageUrl = roleImageUrl,
        roleImageLocalPath = roleImageLocalPath,
        studentId = studentId,
        guardianId = guardianId
    )
    
    private fun LocalStudentEntity.toInstitutionStudent(totalFee: Double, paidFee: Double) = InstitutionStudent(
        id = id,
        name = name,
        classId = classId,
        className = className,
        totalFee = totalFee,
        paidFee = paidFee,
        pendingFee = totalFee - paidFee
    )
    
    private fun LocalOrganizationLeaveEntity.toDomain() = Leave(
        id = id,
        parentOrgId = parentOrganizationId,
        organizationId = organizationId,
        applicantType = applicantType,
        staffId = staffId,
        studentId = studentId,
        leaveType = leaveType,
        startDate = startDate,
        endDate = endDate,
        isHalfDay = isHalfDay,
        halfDayPeriod = halfDayPeriod,
        reason = reason,
        status = status
    )
    
    private fun LocalStudentFeePaymentEntity.toDomain() = FeePayment(
        id = id,
        studentId = studentId,
        receiptNumber = receiptNumber,
        paymentMode = paymentMode,
        paymentDate = paymentDate,
        amountPaid = amountPaid,
        discountAmount = discountAmount,
        fineAmount = fineAmount,
        discountReason = discountReason,
        remarks = remarks
    )
    
    private fun LocalStudentAttendanceEntity.toDomain() = StudentAttendance(
        id = id,
        studentId = studentId,
        attendanceDate = attendanceDate,
        status = status,
        remarks = remarks
    )
    
    private fun LocalParentBusTripEntity.toDomain() = ParentBusTrip(
        id = id,
        parentOrganizationId = parentOrganizationId,
        activeSessionId = activeSessionId,
        busId = busId,
        driverId = driverId,
        tripType = tripType,
        status = status,
        startTime = startTime,
        endTime = endTime,
        isActive = isActive,
        isDeleted = isDeleted,
        createdAt = "",
        updatedAt = "",
        createdBy = driverName,
        updatedBy = null
    )
    
    private fun LocalParentBusTripAttendanceLogEntity.toDomain() = ParentBusTripAttendanceLog(
        id = id,
        parentOrganizationId = parentOrganizationId,
        organizationId = organizationId,
        activeSessionId = activeSessionId,
        tripId = tripId,
        studentId = studentId,
        status = status,
        scanLatitude = scanLatitude,
        scanLongitude = scanLongitude,
        scannedAt = scannedAt,
        scannedByStaffId = scannedByStaffId,
        syncStatus = if (syncState == "SYNCED") "Synced" else "Offline_Pending",
        isActive = isActive,
        isDeleted = isDeleted,
        createdAt = "",
        updatedAt = "",
        createdBy = scannedByStaffName,
        updatedBy = null
    )
    
    private fun LocalCalendarEventEntity.toDomain() = CalendarEvent(
        id = id,
        parentOrganizationId = parentOrganizationId,
        parentOrganizationName = parentOrganizationName,
        organizationId = organizationId,
        organizationName = organizationName,
        activeSessionId = activeSessionId,
        name = name,
        description = description,
        startDate = startDate,
        endDate = endDate,
        eventType = eventType,
        isSchoolClosed = isSchoolClosed
    )
    
    private fun LocalParentStaffAttendanceEntity.toDomain() = StaffAttendance(
        id = id,
        parentOrganizationId = parentOrganizationId,
        activeSessionId = activeSessionId,
        staffId = staffId,
        staffName = staffName,
        staffRole = staffRole,
        attendanceDate = attendanceDate,
        statusId = statusId,
        statusCode = statusCode,
        statusName = statusName,
        isPaidLeave = isPaidLeave,
        checkInTime = checkInTime,
        checkOutTime = checkOutTime,
        remarks = remarks
    )
    
    private fun LocalOrganizationRemarkEntity.toRemark() = Remark(
        id = id,
        parentOrgId = parentOrganizationId,
        organizationId = organizationId,
        activeSessionId = activeSessionId,
        content = content,
        category = category,
        priority = priority,
        creatorUserId = creatorUserId,
        creatorWorkspaceRoleId = creatorRoleName,
        visibilityType = visibilityType,
        visibilityAudience = emptyList(),
        isPinned = isPinned,
        pinExpiresAt = pinExpiresAt,
        expiresAt = expiresAt,
        isActive = isActive,
        isDeleted = isDeleted,
        createdAt = "",
        updatedAt = "",
        createdBy = creatorUserName,
        updatedBy = null,
        syncStatus = if (syncState == "SYNCED") "Synced" else "Offline_Pending"
    )
    
    private fun LocalOrganizationRemarkEntity.toRemarkTarget() = RemarkTarget(
        id = targetId,
        parentOrgId = parentOrganizationId,
        organizationId = organizationId,
        activeSessionId = activeSessionId,
        remarkId = id,
        targetType = targetType,
        targetStudentId = targetStudentId,
        targetGuardianId = targetGuardianId,
        targetStaffId = targetStaffId,
        targetUserId = targetUserId,
        isActive = isActive,
        isDeleted = isDeleted,
        createdAt = "",
        updatedAt = "",
        createdBy = creatorUserName,
        updatedBy = null,
        syncStatus = if (syncState == "SYNCED") "Synced" else "Offline_Pending"
    )

    private suspend fun fetchAndUpdateStudentClassName(studentId: String): LocalStudentEntity? {
        return try {
            // Stage 1: Basic Student details (from organization_students)
            val remoteStudent = remoteDataSource.fetchStudentById(studentId) ?: return null

            // Stage 2: Class enrollment details (from organization_student_enrollments)
            val enrollments = remoteDataSource.fetchStudentEnrollments(listOf(studentId))
            val enrollment = enrollments.firstOrNull()

            // Resolve Class name
            val className = enrollment?.class_id?.let { classId ->
                val orgClass = try {
                    SupabaseClient.client.from("organization_classes")
                        .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("class_id")) {
                            filter { eq("id", classId) }
                        }.decodeSingleOrNull<OrgClassTempDto>()
                } catch (e: Exception) { null }

                if (orgClass != null) {
                    try {
                        SupabaseClient.client.from("global_classes")
                            .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("name")) {
                                filter { eq("id", orgClass.class_id) }
                            }.decodeSingleOrNull<GlobalClassTempDto>()?.name
                    } catch (e: Exception) { null }
                } else null
            }

            // Resolve Section name
            val sectionName = enrollment?.section_id?.let { secId ->
                try {
                    SupabaseClient.client.from("organization_sections")
                        .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("name")) {
                            filter { eq("id", secId) }
                        }.decodeSingleOrNull<SectionTempDto>()?.name
                } catch (e: Exception) { null }
            }

            // Stage 3: Additional Details (from organization_student_additional_details)
            val detailsList = remoteDataSource.fetchStudentAdditionalDetails(listOf(studentId))
            val details = detailsList.firstOrNull()

            // Stage 4: Guardian Details (from organization_guardians)
            val guardianId = remoteStudent.guardian_id
            val guardian = if (guardianId.isNotEmpty()) {
                remoteDataSource.fetchGuardiansByIds(listOf(guardianId)).firstOrNull()
            } else null

            // Resolve Guardian Relationship Type name
            val relationshipName = guardian?.relationship_type_id?.let { relId ->
                try {
                    SupabaseClient.client.from("global_relationship_types")
                        .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("name")) {
                            filter { eq("id", relId) }
                        }.decodeSingleOrNull<GlobalNameTempDto>()?.name
                } catch (e: Exception) { null }
            }

            // Resolve Category name
            val categoryName = remoteStudent.category_id?.let { catId ->
                try {
                    SupabaseClient.client.from("global_student_categories")
                        .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("name")) {
                            filter { eq("id", catId) }
                        }.decodeSingleOrNull<GlobalNameTempDto>()?.name
                } catch (e: Exception) { null }
            }

            // Resolve Blood Group name
            val bloodGroupName = remoteStudent.blood_group_id?.let { bgId ->
                try {
                    SupabaseClient.client.from("global_blood_groups")
                        .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("name")) {
                            filter { eq("id", bgId) }
                        }.decodeSingleOrNull<GlobalNameTempDto>()?.name
                } catch (e: Exception) { null }
            }

            // Resolve Student Status name
            val studentStatusName = remoteStudent.student_status_id?.let { statusId ->
                try {
                    SupabaseClient.client.from("global_student_status")
                        .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("name")) {
                            filter { eq("id", statusId) }
                        }.decodeSingleOrNull<GlobalNameTempDto>()?.name
                } catch (e: Exception) { null }
            }

            // Resolve Mother Tongue name
            val motherTongueName = details?.mother_tongue_id?.let { mtId ->
                try {
                    SupabaseClient.client.from("global_languages")
                        .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("name")) {
                            filter { eq("id", mtId) }
                        }.decodeSingleOrNull<GlobalNameTempDto>()?.name
                } catch (e: Exception) { null }
            }

            // Resolve Address Area name
            val addressAreaName = remoteStudent.address_area_id?.let { areaId ->
                try {
                    SupabaseClient.client.from("global_areas")
                        .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("name")) {
                            filter { eq("id", areaId) }
                        }.decodeSingleOrNull<GlobalNameTempDto>()?.name
                } catch (e: Exception) { null }
            }

            // Resolve Permanent Address Area name
            val permanentAreaName = details?.permanent_address_area_id?.let { areaId ->
                try {
                    SupabaseClient.client.from("global_areas")
                        .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("name")) {
                            filter { eq("id", areaId) }
                        }.decodeSingleOrNull<GlobalNameTempDto>()?.name
                } catch (e: Exception) { null }
            }

            // Stage 5: QR, ID Cards and Home Location
            val qr = remoteDataSource.fetchStudentQrIdentities(listOf(studentId)).firstOrNull()
            val idCard = remoteDataSource.fetchStudentIdCards(listOf(studentId)).firstOrNull()
            val location = remoteDataSource.fetchStudentHomeLocations(listOf(studentId)).firstOrNull()

            // Stage 6: Assembly & Save into Room Database
            val student = dao.getStudentById(studentId)
            if (student != null) {
                val updated = student.copy(
                    classId = enrollment?.class_id,
                    className = className,
                    sectionId = enrollment?.section_id,
                    sectionName = sectionName,
                    rollNumber = enrollment?.roll_number,
                    
                    // Basic Student Fields
                    gender = remoteStudent.gender ?: student.gender,
                    dateOfBirth = remoteStudent.date_of_birth,
                    enrollmentNumber = remoteStudent.enrollment_number ?: student.enrollmentNumber,
                    imageUrl = remoteStudent.image_url ?: student.imageUrl,
                    guardianImageUrl = remoteStudent.guardian_image_url ?: student.guardianImageUrl,
                    isActive = remoteStudent.is_active,
                    isDeleted = remoteStudent.is_deleted,
                    guardianId = remoteStudent.guardian_id,
                    categoryId = remoteStudent.category_id ?: student.categoryId,
                    bloodGroupId = remoteStudent.blood_group_id ?: student.bloodGroupId,
                    studentStatusId = remoteStudent.student_status_id ?: student.studentStatusId,
                    addressAreaId = remoteStudent.address_area_id ?: student.addressAreaId,
                    addressDetails = remoteStudent.address_details ?: student.addressDetails,

                    // Resolved Names
                    guardianName = guardian?.name ?: student.guardianName,
                    guardianMobile = guardian?.mobile_number ?: student.guardianMobile,
                    guardianRelationshipName = relationshipName ?: student.guardianRelationshipName,
                    categoryName = categoryName ?: student.categoryName,
                    bloodGroupName = bloodGroupName ?: student.bloodGroupName,
                    studentStatusName = studentStatusName ?: student.studentStatusName,
                    addressAreaName = addressAreaName ?: student.addressAreaName,

                    // Additional Details
                    motherTongueId = details?.mother_tongue_id ?: student.motherTongueId,
                    motherTongueName = motherTongueName ?: student.motherTongueName,
                    religion = details?.religion ?: student.religion,
                    nationality = details?.nationality ?: student.nationality,
                    identificationMark = details?.identification_mark ?: student.identificationMark,
                    isSingleGirlChild = details?.is_single_girl_child ?: student.isSingleGirlChild,
                    casteCertificateNumber = details?.caste_certificate_number ?: student.casteCertificateNumber,
                    fatherName = details?.father_name ?: student.fatherName,
                    fatherMobile = details?.father_mobile ?: student.fatherMobile,
                    fatherEmail = details?.father_email ?: student.fatherEmail,
                    fatherQualification = details?.father_qualification ?: student.fatherQualification,
                    fatherOccupation = details?.father_occupation ?: student.fatherOccupation,
                    parentsAadhaarFather = details?.parents_aadhaar_father ?: student.parentsAadhaarFather,
                    motherName = details?.mother_name ?: student.motherName,
                    motherMobile = details?.mother_mobile ?: student.motherMobile,
                    motherEmail = details?.mother_email ?: student.motherEmail,
                    motherQualification = details?.mother_qualification ?: student.motherQualification,
                    motherOccupation = details?.mother_occupation ?: student.motherOccupation,
                    parentsAadhaarMother = details?.parents_aadhaar_mother ?: student.parentsAadhaarMother,
                    familyAnnualIncome = details?.family_annual_income ?: student.familyAnnualIncome,
                    permanentAddressDetails = details?.permanent_address_details ?: student.permanentAddressDetails,
                    permanentAddressArea = details?.permanent_address_area ?: student.permanentAddressArea,
                    permanentAddressAreaId = details?.permanent_address_area_id ?: student.permanentAddressAreaId,
                    permanentAreaName = permanentAreaName ?: student.permanentAreaName,
                    bankAccountNumber = details?.bank_account_number ?: student.bankAccountNumber,
                    bankName = details?.bank_name ?: student.bankName,
                    bankBranch = details?.bank_branch ?: student.bankBranch,
                    bankIfsc = details?.bank_ifsc ?: student.bankIfsc,
                    bankAccountHolder = details?.bank_account_holder ?: student.bankAccountHolder,
                    studentAadhar = details?.student_aadhar ?: student.studentAadhar,

                    // QR details
                    qrIdentityId = qr?.id ?: student.qrIdentityId,
                    qrTokenHash = qr?.qr_token_hash ?: student.qrTokenHash,
                    qrStatus = qr?.status ?: student.qrStatus,
                    qrExpiryDate = qr?.expiry_date ?: student.qrExpiryDate,

                    // ID Card details
                    idCardId = idCard?.id ?: student.idCardId,
                    cardNumber = idCard?.card_number ?: student.cardNumber,
                    idCardStatus = idCard?.status ?: student.idCardStatus,
                    idCardReissueReason = idCard?.reason_for_reissue ?: student.idCardReissueReason,

                    // Home Location coordinates
                    homeLatitude = location?.latitude ?: student.homeLatitude,
                    homeLongitude = location?.longitude ?: student.homeLongitude,

                    lastSyncedAt = System.currentTimeMillis(),
                    syncState = "SYNCED"
                )
                dao.insertStudents(listOf(updated))
                updated
            } else null
        } catch (e: Exception) {
            android.util.Log.e("StudentSync", "Failed to sequentially sync student details for studentId: $studentId", e)
            null
        }
    }
}

@kotlinx.serialization.Serializable
private data class EnrollmentTempDto(
    val class_id: String,
    val section_id: String? = null,
    val roll_number: Int? = null
)

@kotlinx.serialization.Serializable
private data class OrgClassTempDto(val class_id: String)

@kotlinx.serialization.Serializable
private data class GlobalClassTempDto(val name: String)

@kotlinx.serialization.Serializable
private data class SectionTempDto(val name: String)

@kotlinx.serialization.Serializable
private data class FeeHeadNameDto(val name: String)

@kotlinx.serialization.Serializable
private data class GlobalNameTempDto(val name: String)

@kotlinx.serialization.Serializable
private data class OrgClassWithIdDto(val id: String, val class_id: String, val custom_class_name: String?)

@kotlinx.serialization.Serializable
private data class SectionWithIdDto(val id: String, val name: String)

@kotlinx.serialization.Serializable
private data class GlobalLookupDto(val id: String, val name: String)
