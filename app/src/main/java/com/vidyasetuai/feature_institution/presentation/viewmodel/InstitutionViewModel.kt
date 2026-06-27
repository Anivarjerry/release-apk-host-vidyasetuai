package com.vidyasetuai.feature_institution.presentation.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_institution.domain.model.Workspace
import com.vidyasetuai.feature_institution.domain.repository.InstitutionRepository
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.domain.model.DriverBusDetails
import com.vidyasetuai.feature_institution.data.local.service.LocationTrackingService
import com.vidyasetuai.feature_institution.domain.model.ChildOrg
import com.vidyasetuai.feature_institution.domain.model.StudentHomeLocation
import com.vidyasetuai.feature_institution.domain.model.OrgClass
import com.vidyasetuai.feature_institution.domain.model.OrgSection
import com.vidyasetuai.feature_institution.domain.model.StudentAttendanceInfo
import com.vidyasetuai.feature_institution.domain.model.AssignedSection
import com.vidyasetuai.feature_institution.domain.model.ContentFeedItem
import com.vidyasetuai.feature_institution.domain.model.ParentBusTrip
import com.vidyasetuai.feature_institution.domain.model.ParentBusTripAttendanceLog
import com.vidyasetuai.feature_institution.domain.model.Remark
import com.vidyasetuai.feature_institution.domain.model.RemarkTarget
import com.vidyasetuai.feature_institution.domain.model.GlobalStaffRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InstitutionViewModel(
    val repository: InstitutionRepository,
    private val appContext: Context
) : ViewModel() {

    private val _uiState = mutableStateOf(InstitutionUiState())
    val uiState: State<InstitutionUiState> = _uiState

    private var tripTimerJob: Job? = null
    private var currentUserId: String = ""

    private val prefs: SharedPreferences by lazy {
        appContext.getSharedPreferences("institution_state", Context.MODE_PRIVATE)
    }

    /** Save active subscreen for the current workspace to SharedPreferences */
    private fun saveActiveSubScreen(workspaceId: String?, subScreen: String?) {
        if (workspaceId.isNullOrEmpty()) return
        prefs.edit().apply {
            if (subScreen != null) {
                putString("active_sub_screen_$workspaceId", subScreen)
            } else {
                remove("active_sub_screen_$workspaceId")
            }
            apply()
        }
    }

    /** Restore active subscreen for a workspace from SharedPreferences */
    private fun restoreActiveSubScreen(workspaceId: String?): String? {
        if (workspaceId.isNullOrEmpty()) return null
        return prefs.getString("active_sub_screen_$workspaceId", null)
    }

    init {
        viewModelScope.launch {
            LocationTrackingService.isTracking.collect { tracking ->
                _uiState.value = _uiState.value.copy(isTripActive = tracking)
                if (tracking) {
                    startUiTimer()
                } else {
                    stopUiTimer()
                }
            }
        }
    }

    private fun startUiTimer() {
        tripTimerJob?.cancel()
        tripTimerJob = viewModelScope.launch {
            while (true) {
                val startTime = LocationTrackingService.trackingStartTime.value
                if (startTime != null) {
                    val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
                    _uiState.value = _uiState.value.copy(tripDurationSeconds = maxOf(0L, elapsedSeconds))
                }
                delay(1000)
            }
        }
    }

    private fun stopUiTimer() {
        tripTimerJob?.cancel()
        tripTimerJob = null
        _uiState.value = _uiState.value.copy(tripDurationSeconds = 0L)
    }

    fun onEvent(event: InstitutionEvent) {
        when (event) {
            is InstitutionEvent.LoadWorkspaces -> {
                loadWorkspaces(event.userId, event.navTarget)
            }
            is InstitutionEvent.SwitchWorkspace -> {
                switchWorkspace(event.workspaceId)
            }
            is InstitutionEvent.SubmitLeaveRequest -> {
                submitLeave(event)
            }
            is InstitutionEvent.LoadLeaves -> {
                loadLeaves(event.userId, event.role)
            }
            is InstitutionEvent.LoadFeePayments -> {
                loadFeePayments(event.studentIds)
            }
            is InstitutionEvent.LoadAttendance -> {
                loadAttendance(event.studentIds)
            }
            is InstitutionEvent.LoadStudentBuses -> {
                loadStudentBuses(event.studentIds)
            }
            is InstitutionEvent.LoadBusLiveLocation -> {
                loadBusLiveLocation(event.busId)
            }
            is InstitutionEvent.ToggleDriverTrip -> {
                toggleDriverTrip(event.busId, event.parentOrgId, event.sessionId)
            }
            is InstitutionEvent.SelectChildOrg -> {
                _uiState.value = _uiState.value.copy(
                    selectedChildOrg = event.orgId,
                    selectedClass = null,
                    selectedSection = null,
                    activeClasses = emptyList(),
                    activeSections = emptyList(),
                    studentsForAttendance = emptyList(),
                    attendanceSubmittedSuccess = false
                )
                loadClasses(event.orgId)
            }
            is InstitutionEvent.SelectClass -> {
                _uiState.value = _uiState.value.copy(
                    selectedClass = event.classId,
                    selectedSection = null,
                    activeSections = emptyList(),
                    studentsForAttendance = emptyList(),
                    attendanceSubmittedSuccess = false
                )
                loadSections(event.classId)
            }
            is InstitutionEvent.SelectSection -> {
                _uiState.value = _uiState.value.copy(
                    selectedSection = event.sectionId,
                    attendanceSubmittedSuccess = false,
                    studentsForAttendance = emptyList(),
                    isAttendanceAlreadyMarked = false
                )
            }
            is InstitutionEvent.LoadPendingApprovals -> {
                loadPendingApprovals(event.userId)
            }
            is InstitutionEvent.ApproveSpecificConnection -> {
                approveSpecificConnection(event.linkId, event.tableName, event.userId)
            }
            is InstitutionEvent.LoadAttendanceDropdowns -> {
                loadAttendanceDropdowns(event.parentOrgId)
            }
            is InstitutionEvent.LoadStudentsForAttendance -> {
                loadStudentsForAttendance(event.orgId, event.classId, event.sectionId, event.date)
            }
            is InstitutionEvent.UpdateStudentAttendanceStatus -> {
                val updated = _uiState.value.studentsForAttendance.map {
                    if (it.studentId == event.studentId) it.copy(status = event.status) else it
                }
                _uiState.value = _uiState.value.copy(studentsForAttendance = updated)
            }
            is InstitutionEvent.SubmitStudentsAttendance -> {
                submitStudentsAttendance(event.orgId, event.date, event.staffUserId, event.parentOrgId)
            }
            is InstitutionEvent.LoadClassTeacherAssignment -> {
                loadClassTeacherAssignment(event.userId, event.parentOrgId, event.autoLoadIfMarked)
            }
            is InstitutionEvent.ChangeAttendanceDate -> {
                _uiState.value = _uiState.value.copy(
                    attendanceDate = event.date,
                    attendanceSubmittedSuccess = false,
                    studentsForAttendance = emptyList(),
                    isAttendanceAlreadyMarked = false
                )
            }
            is InstitutionEvent.LoadSalaryDetails -> {
                loadSalaryDetails(event.userId, event.parentOrgId)
            }
            is InstitutionEvent.SetAttendanceEditEnabled -> {
                _uiState.value = _uiState.value.copy(isAttendanceEditEnabled = event.enabled)
            }
            is InstitutionEvent.ClearAttendanceFilters -> {
                _uiState.value = _uiState.value.copy(
                    selectedChildOrg = null,
                    selectedClass = null,
                    selectedSection = null,
                    studentsForAttendance = emptyList(),
                    isAttendanceAlreadyMarked = false,
                    isAttendanceEditEnabled = false,
                    attendanceSubmittedSuccess = false,
                    activeClasses = emptyList(),
                    activeSections = emptyList()
                )
            }
            is InstitutionEvent.LoadContentFeed -> {
                loadContentFeed()
            }
            is InstitutionEvent.LoadActiveTrip -> {
                loadActiveTrip(event.driverId)
            }
            is InstitutionEvent.StartBusTrip -> {
                startBusTrip(event.parentOrgId, event.sessionId, event.busId, event.driverId, event.tripType)
            }
            is InstitutionEvent.EndBusTrip -> {
                endBusTrip(event.tripId)
            }
            is InstitutionEvent.ScanStudentQrCode -> {
                scanStudentQrCode(event.qrToken, event.latitude, event.longitude, event.staffId)
            }
            is InstitutionEvent.LoadTripAttendanceLogs -> {
                loadTripAttendanceLogs(event.tripId)
            }
            is InstitutionEvent.SyncOfflineLogs -> {
                syncOfflineLogs()
            }
            is InstitutionEvent.LoadDriverTrips -> {
                loadDriverTrips(event.driverId)
            }
            is InstitutionEvent.LoadAssignedStudents -> {
                loadAssignedStudents(event.busId)
            }
            is InstitutionEvent.UpdateSearchQuery -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }
            is InstitutionEvent.ToggleCameraScanner -> {
                _uiState.value = _uiState.value.copy(isCameraScannerOpen = event.open)
            }
            is InstitutionEvent.MarkStudentBoarded -> {
                markStudentBoarded(event.studentId, event.latitude, event.longitude, event.staffId)
            }
            is InstitutionEvent.LoadRemarks -> {
                loadRemarks(event.sessionId, event.parentOrgId)
            }
            is InstitutionEvent.SubmitRemark -> {
                submitRemark(event)
            }
            is InstitutionEvent.DeleteRemark -> {
                deleteRemark(event.remarkId)
            }
            is InstitutionEvent.SyncRemarks -> {
                syncRemarks()
            }
            is InstitutionEvent.ChangeActiveSubScreen -> {
                val workspaceId = _uiState.value.activeWorkspace?.id
                _uiState.value = _uiState.value.copy(activeSubScreen = event.subScreen)
                saveActiveSubScreen(workspaceId, event.subScreen)
            }
            is InstitutionEvent.LoadStudentProfileDetails -> {
                loadStudentProfileDetails(event.studentId)
            }
            is InstitutionEvent.SaveStudentHomeCoordinates -> {
                saveStudentHomeCoordinates(
                    event.organizationId,
                    event.studentId,
                    event.latitude,
                    event.longitude,
                    event.userId
                )
            }
        }
    }

    private fun loadWorkspaces(userId: String, navTarget: String? = null) {
        currentUserId = userId
        // If workspaces are already loaded (e.g. coming back from tab switch), do a silent refresh
        val alreadyLoaded = _uiState.value.workspaces.isNotEmpty()
        if (!alreadyLoaded) {
            _uiState.value = _uiState.value.copy(isLoading = true, userId = userId)
        } else {
            _uiState.value = _uiState.value.copy(userId = userId)
        }
        viewModelScope.launch {
            repository.getActiveSessionDetails().onSuccess { pair ->
                val sessId = pair.first
                val sessName = pair.second
                var startYr = 2026
                var endYr = 2027
                try {
                    val parts = sessName.split("-")
                    if (parts.size >= 2) {
                        startYr = parts[0].trim().toInt()
                        endYr = parts[1].trim().toInt()
                    } else {
                        startYr = sessName.trim().toInt()
                        endYr = startYr + 1
                    }
                } catch (e: Exception) {
                    // default fallback
                }
                _uiState.value = _uiState.value.copy(
                    activeSessionId = sessId,
                    activeSessionName = sessName,
                    sessionStartDate = String.format("%04d-04-01", startYr),
                    sessionEndDate = String.format("%04d-03-31", endYr)
                )
            }

            repository.getWorkspaces(userId).fold(
                onSuccess = { list ->
                    val active = if (navTarget == "driver_trip") {
                        list.find { it.role == "Driver" } ?: list.find { it.isActive } ?: list.firstOrNull()
                    } else {
                        list.find { it.isActive } ?: list.firstOrNull()
                    }
                    // Restore saved activeSubScreen from SharedPreferences
                    val restoredSubScreen = if (navTarget == "driver_trip" && active?.role == "Driver") {
                        "driver_student_attendance"
                    } else if (!alreadyLoaded) {
                        restoreActiveSubScreen(active?.id)
                    } else {
                        _uiState.value.activeSubScreen
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        workspaces = list,
                        activeWorkspace = active,
                        activeSubScreen = restoredSubScreen
                    )
                    if (active != null) {
                        if (navTarget == "driver_trip" && active.role == "Driver") {
                            repository.setActiveWorkspace(active.id)
                        }
                        loadWorkspaceData(userId, active, silent = alreadyLoaded)
                        triggerWorkspaceSync(userId, active)
                    }
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            )
            // Always load pending approvals alongside workspaces
            loadPendingApprovals(userId)
        }
    }

    private fun loadPendingApprovals(userId: String) {
        if (userId.isEmpty()) return
        viewModelScope.launch {
            repository.getPendingApprovals(userId).onSuccess { list ->
                _uiState.value = _uiState.value.copy(pendingApprovals = list)
            }
        }
    }

    private fun approveSpecificConnection(linkId: String, tableName: String, userId: String) {
        viewModelScope.launch {
            repository.approveSpecificConnection(linkId, tableName).fold(
                onSuccess = {
                    // Refresh both workspaces and pending approvals
                    loadWorkspaces(userId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
            )
        }
    }

    private fun loadWorkspaceData(userId: String, workspace: Workspace, silent: Boolean = false) {
        viewModelScope.launch {
            loadLeaves(userId, workspace.role)
            loadContentFeed(silent = silent)

            // Load remarks & global staff roles
            val sessionPair = repository.getActiveSessionDetails().getOrNull()
            val sessionId = sessionPair?.first ?: ""
            _uiState.value = _uiState.value.copy(
                activeSessionId = sessionId,
                activeSessionName = sessionPair?.second ?: ""
            )
            loadRemarks(sessionId, workspace.parentOrgId)
            loadGlobalStaffRoles()
            
            // Load role-specific data
            if (workspace.role == "Guardian") {
                loadGuardianStudents(workspace.id)
            } else if (workspace.role == "Student") {
                viewModelScope.launch {
                    repository.getStudentLinkByUserId(userId).onSuccess { link ->
                        val studentId = link?.studentId ?: ""
                        if (studentId.isNotEmpty()) {
                            loadFeePayments(listOf(studentId))
                            loadStudentProfileDetails(studentId)
                        } else {
                            loadFeePayments(listOf("student_mock_id_1"))
                        }
                    }
                }
            } else {
                // For all staff roles (Teacher, Driver, Admin, Gatekeeper, etc.)
                repository.getStaffLeaveQuotaAndRemaining(userId, workspace.parentOrgId).onSuccess { pair ->
                    _uiState.value = _uiState.value.copy(
                        leaveQuota = pair.first,
                        remainingLeaves = pair.second
                    )
                }
                loadSalaryDetails(userId, workspace.parentOrgId)

                val isAdminRole = workspace.role in listOf("Admin", "System Administrator", "School Administrator", "Org Admin", "Principal", "Director", "Owner")
                if (isAdminRole) {
                    loadAttendanceDropdowns(workspace.parentOrgId, silent = silent)
                    loadAdminFinanceStats(workspace.parentOrgId)
                }

                if (workspace.role == "Driver") {
                    repository.getDriverBusDetails(workspace.id).onSuccess { details ->
                        _uiState.value = _uiState.value.copy(driverBusDetails = details)
                        if (details != null && details.busId.isNotEmpty()) {
                            loadAssignedStudents(details.busId)
                        }
                    }
                }
                
                // Load offline students list for staff/admin search directories
                loadOfflineStudents()
                loadOfflineStaff(workspace.parentOrgId)
            }
        }
    }

    private fun loadGuardianStudents(guardianLinkId: String) {
        viewModelScope.launch {
            repository.getGuardianStudents(guardianLinkId).fold(
                onSuccess = { list ->
                    _uiState.value = _uiState.value.copy(
                        guardianStudents = list
                    )
                    if (list.isNotEmpty()) {
                        val studentIds = list.map { it.id }
                        loadFeePayments(studentIds)
                        loadAttendance(studentIds)
                        loadStudentBuses(studentIds)
                    }
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
            )
        }
    }

    private fun loadOfflineStudents() {
        viewModelScope.launch {
            repository.searchStudentsOffline("", null, null).onSuccess { list ->
                val entities = list.map {
                    com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity(
                        id = it.id,
                        name = it.name,
                        srNumber = it.sr_number,
                        rollNumber = it.roll_number,
                        className = it.class_name,
                        sectionName = it.section_name,
                        guardianName = it.guardian_name,
                        guardianMobile = it.guardian_mobile,
                        imageUrl = it.image_url
                    )
                }
                _uiState.value = _uiState.value.copy(
                    offlineStudents = entities
                )
            }
        }
    }

    private fun loadOfflineStaff(parentOrgId: String) {
        viewModelScope.launch {
            repository.getOfflineStaff(parentOrgId).onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    offlineStaff = list
                )
            }
        }
    }

    private fun switchWorkspace(workspaceId: String) {
        val current = _uiState.value.workspaces.find { it.id == workspaceId } ?: return
        // Restore saved activeSubScreen for the new workspace
        val restoredSubScreen = restoreActiveSubScreen(workspaceId)
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            activeWorkspace = current,
            activeSubScreen = restoredSubScreen,
            guardianStudents = emptyList(),
            assignedStudents = emptyList(),
            studentsForAttendance = emptyList(),
            leaves = emptyList(),
            feePayments = emptyList(),
            studentAttendance = emptyList(),
            offlineStudents = emptyList()
        )
        viewModelScope.launch {
            repository.setActiveWorkspace(workspaceId)
            // Simulate skeleton loader transition
            delay(400)
            _uiState.value = _uiState.value.copy(isLoading = false)
            // Load specific data
            val userId = currentUserId.ifEmpty { "user_mock_id_1" }
            loadWorkspaceData(userId, current)
            triggerWorkspaceSync(userId, current)
        }
    }

    private fun submitLeave(event: InstitutionEvent.SubmitLeaveRequest) {
        viewModelScope.launch {
            repository.submitLeave(
                event.parentOrgId, event.orgId, event.sessionId, event.applicantType,
                event.staffId, event.studentId, event.leaveType, event.startDate, event.endDate,
                event.isHalfDay, event.halfDayPeriod, event.reason, event.createdBy
            ).fold(
                onSuccess = {
                    loadLeaves(event.createdBy, _uiState.value.activeWorkspace?.role ?: "")
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
            )
        }
    }

    private fun loadLeaves(userId: String, role: String) {
        viewModelScope.launch {
            repository.getLeaves(userId, role).onSuccess { list ->
                _uiState.value = _uiState.value.copy(leaves = list)
            }
        }
    }

    private fun loadAttendance(studentIds: List<String>) {
        viewModelScope.launch {
            repository.getStudentAttendance(studentIds).onSuccess { list ->
                _uiState.value = _uiState.value.copy(studentAttendance = list)
            }
        }
    }

    private fun loadStudentBuses(studentIds: List<String>) {
        viewModelScope.launch {
            repository.getStudentBusAssignments(studentIds).onSuccess { list ->
                _uiState.value = _uiState.value.copy(studentBuses = list)
            }
        }
    }

    private fun loadBusLiveLocation(busId: String) {
        viewModelScope.launch {
            repository.getBusLiveLocation(busId).onSuccess { location ->
                _uiState.value = _uiState.value.copy(activeBusLocation = location)
            }
        }
    }

    private fun loadFeePayments(studentIds: List<String>) {
        viewModelScope.launch {
            repository.getFeePayments(studentIds).onSuccess { list ->
                _uiState.value = _uiState.value.copy(feePayments = list)
            }
        }
    }

    private fun toggleDriverTrip(busId: String, parentOrgId: String, sessionId: String?) {
        val isTripActive = !_uiState.value.isTripActive
        _uiState.value = _uiState.value.copy(isTripActive = isTripActive)

        if (isTripActive) {
            _uiState.value = _uiState.value.copy(tripDurationSeconds = 0L)
            tripTimerJob = viewModelScope.launch {
                var seconds = 0L
                while (true) {
                    delay(1000)
                    seconds++
                    _uiState.value = _uiState.value.copy(tripDurationSeconds = seconds)
                    
                    // Periodically send coordinates to Supabase
                    repository.updateBusLiveLocation(
                        busId = busId,
                        parentOrgId = parentOrgId,
                        latitude = 26.9124 + (seconds * 0.0001), 
                        longitude = 75.7873 + (seconds * 0.0001),
                        speed = 35.0,
                        sessionId = sessionId
                    )
                }
            }
        } else {
            tripTimerJob?.cancel()
            tripTimerJob = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        tripTimerJob?.cancel()
    }

    private fun loadAttendanceDropdowns(parentOrgId: String, silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                _uiState.value = _uiState.value.copy(isLoading = true, attendanceSubmittedSuccess = false)
            }
            repository.getOrganizations(parentOrgId).fold(
                onSuccess = { list ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        childOrganizations = list
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            )
        }
    }

    private fun loadClasses(orgId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getClasses(orgId).fold(
                onSuccess = { list ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        activeClasses = list
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            )
        }
    }

    private fun loadSections(classId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getSections(classId).fold(
                onSuccess = { list ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        activeSections = list
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            )
        }
    }

    private fun loadStudentsForAttendance(orgId: String, classId: String, sectionId: String, date: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isAttendanceEditEnabled = false)
            val isMarked = repository.checkIfAttendanceMarked(orgId, classId, sectionId, date).getOrDefault(false)

            repository.getStudentsForAttendance(orgId, classId, sectionId, date).fold(
                onSuccess = { list ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        studentsForAttendance = list,
                        isAttendanceAlreadyMarked = isMarked
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message,
                        isAttendanceAlreadyMarked = false
                    )
                }
            )
        }
    }

    private fun submitStudentsAttendance(orgId: String, date: String, staffUserId: String, parentOrgId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val list = _uiState.value.studentsForAttendance
            repository.submitStudentAttendance(orgId, date, list, staffUserId, parentOrgId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        attendanceSubmittedSuccess = true,
                        isAttendanceAlreadyMarked = true,
                        isAttendanceEditEnabled = false
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            )
        }
    }

    private fun loadClassTeacherAssignment(userId: String, parentOrgId: String, autoLoadIfMarked: Boolean = false) {
        viewModelScope.launch {
            repository.getClassTeacherAssignment(userId, parentOrgId).fold(
                onSuccess = { assignment ->
                    _uiState.value = _uiState.value.copy(
                        assignedClassTeacherSection = assignment
                    )
                    // If an assignment exists, trigger loading child organizations and pre-populate selections
                    if (assignment != null) {
                        _uiState.value = _uiState.value.copy(
                            selectedChildOrg = assignment.orgId,
                            selectedClass = assignment.classId,
                            selectedSection = assignment.sectionId
                        )
                        loadClasses(assignment.orgId)
                        loadSections(assignment.classId)
                        
                        if (autoLoadIfMarked) {
                            val isMarked = repository.checkIfAttendanceMarked(
                                assignment.orgId,
                                assignment.classId,
                                assignment.sectionId,
                                _uiState.value.attendanceDate
                            ).getOrDefault(false)
                            
                            if (isMarked) {
                                loadStudentsForAttendance(
                                    assignment.orgId,
                                    assignment.classId,
                                    assignment.sectionId,
                                    _uiState.value.attendanceDate
                                )
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    isAttendanceAlreadyMarked = false,
                                    studentsForAttendance = emptyList()
                                )
                            }
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isAttendanceAlreadyMarked = false,
                                studentsForAttendance = emptyList()
                            )
                        }
                    }
                },
                onFailure = { e ->
                    Log.e("VidyaSetu_Auth", "Error loading class teacher assignment", e)
                }
            )
        }
    }

    private fun loadSalaryDetails(userId: String, parentOrgId: String) {
        viewModelScope.launch {
            repository.getStaffSalaryDetails(userId, parentOrgId).fold(
                onSuccess = { details ->
                    _uiState.value = _uiState.value.copy(
                        monthlySalary = details.monthlySalary,
                        totalSalaryPaid = details.totalPaid,
                        salaryPayments = details.payments
                    )
                },
                onFailure = { e ->
                    Log.e("VidyaSetu_Auth", "Error loading salary details", e)
                }
            )
        }
    }

    private fun loadContentFeed(silent: Boolean = false) {
        val workspace = _uiState.value.activeWorkspace ?: return
        val sessionId = _uiState.value.activeSessionId.ifEmpty { "fa000000-0000-0000-0000-000000000001" }
        if (!silent) {
            _uiState.value = _uiState.value.copy(isLoading = true)
        }
        viewModelScope.launch {
            repository.getContentFeed(workspace, sessionId).fold(
                onSuccess = { list ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        contentFeedItems = list
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            )
        }
    }

    private fun loadAdminFinanceStats(parentOrgId: String) {
        viewModelScope.launch {
            repository.getAdminFeeStats(parentOrgId).fold(
                onSuccess = { pair ->
                    _uiState.value = _uiState.value.copy(
                        adminTotalCollected = pair.first,
                        adminTotalPending = pair.second
                    )
                },
                onFailure = { e ->
                    Log.e("VidyaSetu_Auth", "Error loading admin finance stats", e)
                }
            )
        }
    }

    private fun triggerWorkspaceSync(userId: String, workspace: Workspace) {
        val sessionId = _uiState.value.activeSessionId.ifEmpty { "fa000000-0000-0000-0000-000000000001" }
        viewModelScope.launch {
            repository.syncWorkspaceData(userId, workspace, sessionId).fold(
                onSuccess = {
                    loadWorkspaceData(userId, workspace, silent = true)
                    if (workspace.role != "Guardian" && workspace.role != "Student") {
                        loadOfflineStudents()
                    }
                },
                onFailure = { e ->
                    Log.e("OfflineSync", "Background sync failed", e)
                }
            )
        }
    }

    private fun loadActiveTrip(driverId: String) {
        viewModelScope.launch {
            repository.getActiveBusTrip(driverId).onSuccess { trip ->
                _uiState.value = _uiState.value.copy(activeBusTrip = trip)
                if (trip != null) {
                    loadTripAttendanceLogs(trip.id)
                    val busId = _uiState.value.driverBusDetails?.busId ?: ""
                    if (busId.isNotEmpty()) {
                        loadAssignedStudents(busId)
                    }
                }
            }
        }
    }

    private fun startBusTrip(parentOrgId: String, sessionId: String, busId: String, driverId: String, tripType: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            repository.startBusTrip(parentOrgId, sessionId, busId, driverId, tripType).fold(
                onSuccess = { trip ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        activeBusTrip = trip,
                        busTripAttendanceLogs = emptyList()
                    )
                    loadAssignedStudents(busId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            )
        }
    }

    private fun endBusTrip(tripId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            repository.endBusTrip(tripId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        activeBusTrip = null,
                        busTripAttendanceLogs = emptyList()
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            )
        }
    }

    private fun loadTripAttendanceLogs(tripId: String) {
        viewModelScope.launch {
            repository.getBusTripAttendanceLogsWithStudentInfo(tripId).fold(
                onSuccess = { list ->
                    _uiState.value = _uiState.value.copy(busTripAttendanceLogs = list)
                },
                onFailure = { e ->
                    Log.e("DriverAttendance", "Error loading trip logs", e)
                }
            )
        }
    }

    private fun scanStudentQrCode(qrToken: String, latitude: Double?, longitude: Double?, staffId: String) {
        val trip = _uiState.value.activeBusTrip ?: return
        viewModelScope.launch {
            val hash = try {
                val digest = java.security.MessageDigest.getInstance("SHA-256")
                val hashBytes = digest.digest(qrToken.trim().toByteArray(Charsets.UTF_8))
                hashBytes.joinToString("") { "%02x".format(it) }
            } catch (e: Exception) {
                qrToken.trim()
            }
            
            repository.getStudentByQrHash(hash).fold(
                onSuccess = { student ->
                    if (student != null) {
                        val alreadyScanned = _uiState.value.busTripAttendanceLogs.any { it.studentId == student.id }
                        if (alreadyScanned) {
                            _uiState.value = _uiState.value.copy(errorMessage = "Student already boarded/scanned")
                            return@fold
                        }
                        
                        val logId = java.util.UUID.randomUUID().toString()
                        val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", java.util.Locale.US).format(java.util.Date())
                        val log = ParentBusTripAttendanceLog(
                            id = logId,
                            parentOrganizationId = trip.parentOrganizationId,
                            organizationId = _uiState.value.activeWorkspace?.childOrgId ?: trip.parentOrganizationId,
                            activeSessionId = trip.activeSessionId,
                            tripId = trip.id,
                            studentId = student.id,
                            status = "Boarded",
                            scanLatitude = latitude,
                            scanLongitude = longitude,
                            scannedAt = now,
                            scannedByStaffId = staffId,
                            syncStatus = "Offline_Pending",
                            isActive = true,
                            isDeleted = false,
                            createdAt = now,
                            updatedAt = now,
                            createdBy = staffId,
                            updatedBy = staffId
                        )
                        
                        repository.submitBusAttendanceLog(log).fold(
                            onSuccess = { submittedLog ->
                                loadTripAttendanceLogs(trip.id)
                                _uiState.value = _uiState.value.copy(isCameraScannerOpen = false)
                            },
                            onFailure = { e ->
                                _uiState.value = _uiState.value.copy(errorMessage = e.message)
                            }
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(errorMessage = "Invalid QR Code / Student not found")
                    }
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
            )
        }
    }

    private fun syncOfflineLogs() {
        _uiState.value = _uiState.value.copy(isSyncingLogs = true, syncSuccessCount = 0)
        viewModelScope.launch {
            repository.syncOfflineAttendanceLogs().fold(
                onSuccess = { count ->
                    _uiState.value = _uiState.value.copy(
                        isSyncingLogs = false,
                        syncSuccessCount = count
                    )
                    val active = _uiState.value.activeBusTrip
                    if (active != null) {
                        loadTripAttendanceLogs(active.id)
                    }
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isSyncingLogs = false,
                        errorMessage = e.message
                    )
                }
            )
        }
    }

    private fun loadDriverTrips(driverId: String) {
        viewModelScope.launch {
            repository.getParentBusTripsForDriver(driverId).onSuccess { list ->
                _uiState.value = _uiState.value.copy(driverBusTrips = list)
            }
        }
    }

    private fun loadAssignedStudents(busId: String) {
        viewModelScope.launch {
            repository.getStudentsAssignedToBus(busId).fold(
                onSuccess = { list ->
                    _uiState.value = _uiState.value.copy(assignedStudents = list)
                },
                onFailure = { e ->
                    Log.e("DriverAttendance", "Error loading assigned students", e)
                }
            )
        }
    }

    private fun markStudentBoarded(studentId: String, latitude: Double?, longitude: Double?, staffId: String) {
        val trip = _uiState.value.activeBusTrip ?: return
        viewModelScope.launch {
            val alreadyScanned = _uiState.value.busTripAttendanceLogs.any { it.studentId == studentId }
            if (alreadyScanned) {
                _uiState.value = _uiState.value.copy(errorMessage = "Student already boarded")
                return@launch
            }
            
            val logId = java.util.UUID.randomUUID().toString()
            val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", java.util.Locale.US).format(java.util.Date())
            val log = ParentBusTripAttendanceLog(
                id = logId,
                parentOrganizationId = trip.parentOrganizationId,
                organizationId = _uiState.value.activeWorkspace?.childOrgId ?: trip.parentOrganizationId,
                activeSessionId = trip.activeSessionId,
                tripId = trip.id,
                studentId = studentId,
                status = "Boarded",
                scanLatitude = latitude,
                scanLongitude = longitude,
                scannedAt = now,
                scannedByStaffId = staffId,
                syncStatus = "Offline_Pending",
                isActive = true,
                isDeleted = false,
                createdAt = now,
                updatedAt = now,
                createdBy = staffId,
                updatedBy = staffId
            )
            
            repository.submitBusAttendanceLog(log).fold(
                onSuccess = { submittedLog ->
                    loadTripAttendanceLogs(trip.id)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
            )
        }
    }

    private fun loadRemarks(sessionId: String, parentOrgId: String) {
        viewModelScope.launch {
            val remarksResult = repository.getRemarks(sessionId)
            val allRemarks = remarksResult.getOrNull() ?: emptyList()
            val remarksList = allRemarks.filter { it.parentOrgId == parentOrgId }
            
            val targetsMap = mutableMapOf<String, List<RemarkTarget>>()
            for (remark in remarksList) {
                val targetsResult = repository.getRemarkTargets(remark.id)
                val targetsList = targetsResult.getOrNull() ?: emptyList()
                targetsMap[remark.id] = targetsList
            }
            
            val latestAlert = remarksList
                .filter { it.priority in listOf("Critical", "High") }
                .maxByOrNull { it.createdAt } ?: remarksList.maxByOrNull { it.createdAt }

            _uiState.value = _uiState.value.copy(
                remarks = remarksList,
                remarkTargetsMap = targetsMap,
                latestRemarkAlert = latestAlert
            )
        }
    }

    private fun loadGlobalStaffRoles() {
        viewModelScope.launch {
            repository.getGlobalStaffRoles().onSuccess { roles ->
                _uiState.value = _uiState.value.copy(globalStaffRoles = roles)
            }
        }
    }

    private fun submitRemark(event: InstitutionEvent.SubmitRemark) {
        viewModelScope.launch {
            val workspace = _uiState.value.activeWorkspace ?: return@launch
            val sessionId = _uiState.value.activeSessionId.ifEmpty { "session_mock_id_1" }
            val nowStr = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", java.util.Locale.US).format(java.util.Date())
            val remarkId = java.util.UUID.randomUUID().toString()

            // workspace.role mein role ka naam hota hai (e.g. "System Administrator")
            // Supabase ko UUID chahiye, isliye globalStaffRoles se match karke UUID nikalte hain
            val roleUuid = _uiState.value.globalStaffRoles
                .firstOrNull { it.name.equals(workspace.role, ignoreCase = true) }?.id

            var resolvedOrgId = workspace.childOrgId
            if (event.targetType == "Student" && event.targetStudentId != null) {
                val studentResult = repository.getLocalStudentById(event.targetStudentId)
                val student = studentResult.getOrNull()
                if (student != null) {
                    resolvedOrgId = student.organizationId.takeIf { it.isNotEmpty() }
                }
            } else if (event.targetType == "Self" || event.targetType == "Staff") {
                resolvedOrgId = null
            }

            val remark = Remark(
                id = remarkId,
                parentOrgId = workspace.parentOrgId,
                organizationId = resolvedOrgId,
                activeSessionId = sessionId,
                content = event.content,
                category = event.category,
                priority = event.priority,
                creatorUserId = currentUserId.ifEmpty { workspace.id },
                creatorWorkspaceRoleId = roleUuid,
                visibilityType = event.visibilityType,
                visibilityAudience = event.visibilityAudience,
                isPinned = false,
                pinExpiresAt = null,
                expiresAt = null,
                isActive = true,
                isDeleted = false,
                createdAt = nowStr,
                updatedAt = nowStr,
                createdBy = currentUserId.ifEmpty { workspace.id },
                updatedBy = currentUserId.ifEmpty { workspace.id },
                syncStatus = "Offline_Pending"
            )

            val targets = mutableListOf<RemarkTarget>()
            if (event.targetType == "Self") {
                targets.add(
                    RemarkTarget(
                        id = java.util.UUID.randomUUID().toString(),
                        parentOrgId = workspace.parentOrgId,
                        organizationId = resolvedOrgId,
                        activeSessionId = sessionId,
                        remarkId = remarkId,
                        targetType = "Self",
                        targetStudentId = null,
                        targetGuardianId = null,
                        targetStaffId = null,
                        targetUserId = currentUserId.ifEmpty { workspace.id },
                        isActive = true,
                        isDeleted = false,
                        createdAt = nowStr,
                        updatedAt = nowStr,
                        createdBy = currentUserId.ifEmpty { workspace.id },
                        updatedBy = currentUserId.ifEmpty { workspace.id },
                        syncStatus = "Offline_Pending"
                    )
                )
            } else {
                targets.add(
                    RemarkTarget(
                        id = java.util.UUID.randomUUID().toString(),
                        parentOrgId = workspace.parentOrgId,
                        organizationId = resolvedOrgId,
                        activeSessionId = sessionId,
                        remarkId = remarkId,
                        targetType = event.targetType,
                        targetStudentId = event.targetStudentId,
                        targetGuardianId = event.targetGuardianId,
                        targetStaffId = event.targetStaffId,
                        targetUserId = null,
                        isActive = true,
                        isDeleted = false,
                        createdAt = nowStr,
                        updatedAt = nowStr,
                        createdBy = currentUserId.ifEmpty { workspace.id },
                        updatedBy = currentUserId.ifEmpty { workspace.id },
                        syncStatus = "Offline_Pending"
                    )
                )
            }

            repository.addRemark(remark, targets).fold(
                onSuccess = {
                    loadRemarks(sessionId, workspace.parentOrgId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
            )
        }
    }

    private fun deleteRemark(remarkId: String) {
        viewModelScope.launch {
            val workspace = _uiState.value.activeWorkspace ?: return@launch
            val sessionId = _uiState.value.activeSessionId
            repository.softDeleteRemark(remarkId).fold(
                onSuccess = {
                    loadRemarks(sessionId, workspace.parentOrgId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
            )
        }
    }

    private fun syncRemarks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncingLogs = true)
            val workspace = _uiState.value.activeWorkspace
            val sessionId = _uiState.value.activeSessionId
            
            if (workspace != null) {
                // First sync pending offline remarks to server
                repository.syncRemarksOffline()
                
                // Then fetch latest remarks from server
                repository.fetchRemarksFromServer(sessionId, workspace.parentOrgId).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(isSyncingLogs = false)
                        loadRemarks(sessionId, workspace.parentOrgId)
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            isSyncingLogs = false,
                            errorMessage = "Remarks sync failed: ${e.message}"
                        )
                        loadRemarks(sessionId, workspace.parentOrgId)
                    }
                )
            } else {
                _uiState.value = _uiState.value.copy(isSyncingLogs = false)
            }
        }
    }
    private fun loadStudentProfileDetails(studentId: String) {
        _uiState.value = _uiState.value.copy(
            selectedStudentDetail = null,
            selectedStudentBusAssignment = null,
            selectedStudentHomeLocation = null
        )
        viewModelScope.launch {
            repository.getLocalStudentById(studentId).onSuccess { student ->
                _uiState.value = _uiState.value.copy(selectedStudentDetail = student)
            }

            repository.getStudentBusAssignments(listOf(studentId)).onSuccess { list ->
                _uiState.value = _uiState.value.copy(selectedStudentBusAssignment = list.firstOrNull())
            }

            repository.getStudentHomeLocation(studentId).fold(
                onSuccess = { loc ->
                    _uiState.value = _uiState.value.copy(selectedStudentHomeLocation = loc)
                },
                onFailure = { e ->
                    Log.e("VidyaSetu_HomeLocation", "Error loading home location", e)
                    _uiState.value = _uiState.value.copy(selectedStudentHomeLocation = null)
                }
            )
        }
    }

    private fun saveStudentHomeCoordinates(
        organizationId: String,
        studentId: String,
        latitude: Double,
        longitude: Double,
        userId: String
    ) {
        _uiState.value = _uiState.value.copy(isSavingHomeLocation = true)
        viewModelScope.launch {
            repository.saveStudentHomeLocation(organizationId, studentId, latitude, longitude, userId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isSavingHomeLocation = false)
                    loadStudentProfileDetails(studentId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isSavingHomeLocation = false,
                        errorMessage = e.message
                    )
                }
            )
        }
    }
}