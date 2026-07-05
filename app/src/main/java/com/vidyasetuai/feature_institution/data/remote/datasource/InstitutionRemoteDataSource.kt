package com.vidyasetuai.feature_institution.data.remote.datasource

import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_institution.data.remote.dto.*
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.*
import kotlinx.serialization.encodeToString

class InstitutionRemoteDataSource {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    suspend fun fetchGuardianWorkspaces(userId: String): List<WorkspaceLinkDto> {
        return SupabaseClient.client.from("organization_guardian_user_links")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("user_id", userId)
                }
            }.decodeList()
    }

    suspend fun fetchStaffWorkspaces(userId: String): List<WorkspaceLinkDto> {
        return SupabaseClient.client.from("organization_parent_staff_user_links")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("user_id", userId)
                }
            }.decodeList()
    }

    suspend fun fetchStudentWorkspaces(userId: String): List<WorkspaceLinkDto> {
        return SupabaseClient.client.from("organization_student_user_links")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("user_id", userId)
                }
            }.decodeList()
    }

    suspend fun fetchChildOrgSetup(orgId: String): ChildOrgSetupDto? {
        if (orgId.isEmpty()) return null
        
        try {
            // A. Get parent org ID & Profile setup completion status
            val orgInfo = try {
                SupabaseClient.client.from("organizations")
                    .select(columns = Columns.raw("parent_organization_id")) {
                        filter { eq("id", orgId) }
                    }.decodeSingleOrNull<OrgParentIdDto>()
            } catch (e: Exception) {
                android.util.Log.e("OfflineSync", "Error fetching org info", e)
                null
            }
            val parentOrgId = orgInfo?.parent_organization_id ?: ""

            val profileSetup = try {
                SupabaseClient.client.from("organization_profiles")
                    .select(columns = Columns.raw("is_setup_complete")) {
                        filter { eq("organization_id", orgId) }
                    }.decodeSingleOrNull<OrgProfileSetupDto>()
            } catch (e: Exception) {
                android.util.Log.e("OfflineSync", "Error fetching profile setup complete status", e)
                null
            }
            val isSetupComplete = profileSetup?.is_setup_complete ?: false

            // B. Fetch active session
            var sessionId = "fa000000-0000-0000-0000-000000000001"
            var sessionName = "2025-26"
            try {
                val activeSession = SupabaseClient.client.from("global_sessions")
                    .select(columns = Columns.raw("id, name")) {
                        filter {
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeSingleOrNull<GlobalSessionMapDto>()
                if (activeSession != null) {
                    sessionId = activeSession.id
                    sessionName = activeSession.name
                }
            } catch (e: Exception) {
                android.util.Log.e("OfflineSync", "Error fetching active session", e)
            }

            // C. Fetch boards list & global board names
            val boardsJson = try {
                val orgBoards = SupabaseClient.client.from("organization_boards")
                    .select(columns = Columns.raw("board_id")) {
                        filter {
                            eq("organization_id", orgId)
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeList<OrgBoardMapDto>()
                val globalBoards = if (orgBoards.isNotEmpty()) {
                    val boardIds = orgBoards.map { it.board_id }.distinct()
                    SupabaseClient.client.from("global_boards")
                        .select(columns = Columns.raw("id, name")) {
                            filter { isIn("id", boardIds) }
                        }.decodeList<GlobalIdNameDto>()
                } else emptyList()
                Json.encodeToString(globalBoards)
            } catch (e: Exception) {
                android.util.Log.e("OfflineSync", "Error fetching boards", e)
                "[]"
            }

            // D. Fetch mediums list & global medium names
            val mediumsJson = try {
                val orgMediums = SupabaseClient.client.from("organization_mediums")
                    .select(columns = Columns.raw("medium_id")) {
                        filter {
                            eq("organization_id", orgId)
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeList<OrgMediumMapDto>()
                val globalMediums = if (orgMediums.isNotEmpty()) {
                    val mediumIds = orgMediums.map { it.medium_id }.distinct()
                    SupabaseClient.client.from("global_mediums")
                        .select(columns = Columns.raw("id, name")) {
                            filter { isIn("id", mediumIds) }
                        }.decodeList<GlobalIdNameDto>()
                } else emptyList()
                Json.encodeToString(globalMediums)
            } catch (e: Exception) {
                android.util.Log.e("OfflineSync", "Error fetching mediums", e)
                "[]"
            }

            // E. Fetch languages list & global language names
            val languagesJson = try {
                val orgLanguages = SupabaseClient.client.from("organization_languages")
                    .select(columns = Columns.raw("language_id")) {
                        filter {
                            eq("organization_id", orgId)
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeList<OrgLanguageMapDto>()
                val globalLanguages = if (orgLanguages.isNotEmpty()) {
                    val languageIds = orgLanguages.map { it.language_id }.distinct()
                    SupabaseClient.client.from("global_languages")
                        .select(columns = Columns.raw("id, name")) {
                            filter { isIn("id", languageIds) }
                        }.decodeList<GlobalIdNameDto>()
                } else emptyList()
                Json.encodeToString(globalLanguages)
            } catch (e: Exception) {
                android.util.Log.e("OfflineSync", "Error fetching languages", e)
                "[]"
            }

            // F. Fetch periods list
            val periodsJson = try {
                val periods = SupabaseClient.client.from("organization_periods")
                    .select(columns = Columns.raw("id, name, start_time, end_time, is_break")) {
                        filter {
                            eq("organization_id", orgId)
                            eq("is_active", true)
                        }
                    }.decodeList<OrgPeriodDto>()
                Json.encodeToString(periods)
            } catch (e: Exception) {
                android.util.Log.e("OfflineSync", "Error fetching periods", e)
                "[]"
            }

            // G. Fetch fees structure list
            val feesStructureJson = try {
                val feeAssignments = SupabaseClient.client.from("organization_fee_assignments")
                    .select(columns = Columns.raw("id, organization_class_id, fee_head_id, amount")) {
                        filter {
                            eq("organization_id", orgId)
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeList<OrgFeeAssignmentDto>()
                Json.encodeToString(feeAssignments)
            } catch (e: Exception) {
                android.util.Log.e("OfflineSync", "Error fetching fee assignments", e)
                "[]"
            }

            // H. Construct class_structure_json dynamically
            var classStructureJson = "[]"
            try {
                // Fetch active organization classes
                val orgClasses = SupabaseClient.client.from("organization_classes")
                    .select(columns = Columns.raw("id, class_id")) {
                        filter {
                            eq("organization_id", orgId)
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeList<OrgClassMapDto>()

                if (orgClasses.isNotEmpty()) {
                    // Fetch global class names
                    val globalClassIds = orgClasses.map { it.class_id }.distinct()
                    val globalClasses = SupabaseClient.client.from("global_classes")
                        .select(columns = Columns.raw("id, name")) {
                            filter { isIn("id", globalClassIds) }
                        }.decodeList<GlobalClassMapDto>()
                    val globalClassNames = globalClasses.associate { it.id to it.name }

                    // Fetch active organization sections
                    val orgClassIds = orgClasses.map { it.id }
                    val orgSections = SupabaseClient.client.from("organization_sections")
                        .select(columns = Columns.raw("id, organization_class_id, name, room_number, max_capacity")) {
                            filter {
                                isIn("organization_class_id", orgClassIds)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<OrgSectionQueryDto>()

                    // Fetch staff profiles for name mapping
                    val staffProfiles = if (parentOrgId.isNotEmpty()) {
                        SupabaseClient.client.from("organization_parent_staff")
                            .select(columns = Columns.raw("id, name")) {
                                filter {
                                    eq("parent_organization_id", parentOrgId)
                                    eq("is_active", true)
                                    eq("is_deleted", false)
                                }
                            }.decodeList<StaffIdNameDto>()
                    } else emptyList()
                    val staffNames = staffProfiles.associate { it.id to it.name }

                    // Fetch session class & session subjects mapping
                    val sessionClasses = SupabaseClient.client.from("organization_session_classes")
                        .select(columns = Columns.raw("id, organization_class_id")) {
                            filter {
                                eq("organization_id", orgId)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<OrgSessionClassMapDto>()

                    val oscIds = sessionClasses.map { it.id }
                    
                    // Fetch session sections for class teacher mapping
                    val sessionSections = if (oscIds.isNotEmpty()) {
                        SupabaseClient.client.from("organization_session_sections")
                            .select(columns = Columns.raw("organization_session_class_id, organization_section_id, class_teacher_id")) {
                                filter {
                                    isIn("organization_session_class_id", oscIds)
                                    eq("is_active", true)
                                    eq("is_deleted", false)
                                }
                            }.decodeList<OrgSessionSectionMapDto>()
                    } else emptyList()
                    val sectionTeacherMap = sessionSections.associate { it.organization_section_id to (it.class_teacher_id ?: "") }

                    val sessionSubjects = if (oscIds.isNotEmpty()) {
                        SupabaseClient.client.from("organization_session_subjects")
                            .select(columns = Columns.raw("organization_session_class_id, subject_id, subject_teacher_id, is_elective")) {
                                filter {
                                    isIn("organization_session_class_id", oscIds)
                                    eq("is_active", true)
                                    eq("is_deleted", false)
                                }
                            }.decodeList<OrgSessionSubjectMapDto>()
                    } else emptyList()

                    val globalSubjectIds = sessionSubjects.map { it.subject_id }.distinct()
                    val globalSubjects = if (globalSubjectIds.isNotEmpty()) {
                        SupabaseClient.client.from("global_subjects")
                            .select(columns = Columns.raw("id, name, code")) {
                                filter { isIn("id", globalSubjectIds) }
                            }.decodeList<GlobalSubjectMapDto>()
                    } else emptyList()
                    val subjectNames = globalSubjects.associate { it.id to it.name }
                    val subjectCodes = globalSubjects.associate { it.id to (it.code ?: "") }

                    // Group mappings
                    val sectionsByOrgClassId = orgSections.groupBy { it.organization_class_id }
                    val sessionClassToOrgClassMap = sessionClasses.associate { it.id to it.organization_class_id }
                    val subjectsByOrgClassId = sessionSubjects.groupBy { sessionClassToOrgClassMap[it.organization_session_class_id] ?: "" }

                    // Build class structure JSON
                    val classStructureList = orgClasses.map { oc ->
                        val className = globalClassNames[oc.class_id] ?: "Class"
                        
                        val sectionsList = (sectionsByOrgClassId[oc.id] ?: emptyList()).map { s ->
                            val classTeacherId = sectionTeacherMap[s.id] ?: ""
                            buildJsonObject {
                                put("section_id", s.id)
                                put("section_name", s.name)
                                put("max_capacity", s.max_capacity ?: 40)
                                put("class_teacher_id", classTeacherId)
                                put("class_teacher_name", staffNames[classTeacherId] ?: "")
                            }
                        }

                        val subjectsList = (subjectsByOrgClassId[oc.id] ?: emptyList()).map { sub ->
                            buildJsonObject {
                                put("subject_id", sub.subject_id)
                                put("subject_name", subjectNames[sub.subject_id] ?: "")
                                put("subject_code", subjectCodes[sub.subject_id] ?: "")
                                put("subject_teacher_id", sub.subject_teacher_id ?: "")
                                put("subject_teacher_name", staffNames[sub.subject_teacher_id] ?: "")
                                put("is_elective", sub.is_elective)
                            }
                        }

                        buildJsonObject {
                            put("class_id", oc.class_id)
                            put("class_name", className)
                            put("sections", JsonArray(sectionsList))
                            put("subjects", JsonArray(subjectsList))
                        }
                    }
                    classStructureJson = JsonArray(classStructureList).toString()
                }
            } catch (e: Exception) {
                android.util.Log.e("OfflineSync", "Error building class structure JSON dynamically", e)
            }

            return ChildOrgSetupDto(
                organization_id = orgId,
                session_id = sessionId,
                session_name = sessionName,
                boards_json = boardsJson,
                mediums_json = mediumsJson,
                languages_json = languagesJson,
                class_structure_json = classStructureJson,
                periods_json = periodsJson,
                fees_structure_json = feesStructureJson,
                is_setup_complete = isSetupComplete
            )
        } catch (e: Exception) {
            android.util.Log.e("OfflineSync", "Fatal error constructing setup", e)
            return null
        }
    }

    suspend fun fetchParentBuses(parentOrgId: String): List<ParentBusDto> {
        return SupabaseClient.client.from("organization_parent_buses")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("parent_organization_id", parentOrgId)
                }
            }.decodeList()
    }

    suspend fun fetchBusRoutes(parentOrgId: String): List<BusRouteDto> {
        return SupabaseClient.client.from("organization_bus_routes")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("parent_organization_id", parentOrgId)
                    eq("is_active", true)
                    eq("is_deleted", false)
                }
            }.decodeList()
    }

    suspend fun fetchStudentDeltaUpdates(
        orgId: String,
        userRole: String,
        userId: String,
        lastSyncedAt: String?
    ): List<StudentSyncDto> {
        return try {
            val responseText = SupabaseClient.client.postgrest.rpc(
                "get_student_delta_updates",
                buildJsonObject {
                    if (orgId.isEmpty()) {
                        put("p_organization_id", JsonNull)
                    } else {
                        put("p_organization_id", orgId)
                    }
                    put("p_user_role", userRole)
                    put("p_user_id", userId)
                    if (lastSyncedAt != null) {
                        put("p_last_synced_at", lastSyncedAt)
                    }
                }
            ).data
            
            android.util.Log.d("OfflineSync", "Raw response from get_student_delta_updates: $responseText")

            val jsonElement = json.parseToJsonElement(responseText)
            val studentsArray = when (jsonElement) {
                is JsonArray -> {
                    val firstRow = jsonElement.firstOrNull() as? JsonObject
                    if (firstRow != null && firstRow.containsKey("students_json")) {
                        firstRow["students_json"] ?: JsonNull
                    } else {
                        jsonElement
                    }
                }
                is JsonObject -> {
                    if (jsonElement.containsKey("students_json")) {
                        jsonElement["students_json"] ?: JsonNull
                    } else {
                        jsonElement
                    }
                }
                else -> JsonNull
            }

            if (studentsArray is JsonArray) {
                json.decodeFromJsonElement<List<StudentSyncDto>>(studentsArray)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("OfflineSync", "Error fetching student delta updates", e)
            emptyList()
        }
    }

    suspend fun fetchEntireWorkspacePayload(
        parentOrgId: String,
        childOrgId: String?,
        userRole: String,
        userId: String,
        lastSyncedAt: String?
    ): WorkspaceSyncPayloadDto {
        return try {
            val responseText = SupabaseClient.client.postgrest.rpc(
                "fetch_entire_workspace_payload_for_mobile",
                buildJsonObject {
                    put("p_parent_organization_id", parentOrgId)
                    if (childOrgId.isNullOrEmpty()) {
                        put("p_child_organization_id", JsonNull)
                    } else {
                        put("p_child_organization_id", childOrgId)
                    }
                    put("p_user_role", userRole)
                    put("p_user_id", userId)
                    if (lastSyncedAt != null) {
                        put("p_last_synced_at", lastSyncedAt)
                    }
                }
            ).data
            
            android.util.Log.d("OfflineSync", "Raw response from fetch_entire_workspace_payload_for_mobile length: ${responseText.length}")

            json.decodeFromString<WorkspaceSyncPayloadDto>(responseText)
        } catch (e: Exception) {
            android.util.Log.e("OfflineSync", "Error fetching entire workspace payload", e)
            WorkspaceSyncPayloadDto()
        }
    }

    suspend fun fetchStudents(orgId: String): List<StudentDto> {
        return SupabaseClient.client.from("organization_students")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("organization_id", orgId)
                }
            }.decodeList()
    }

    suspend fun fetchLinkedStudents(guardianId: String): List<StudentDto> {
        return SupabaseClient.client.from("organization_students")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("guardian_id", guardianId)
                }
            }.decodeList()
    }

    suspend fun fetchStudentById(studentId: String): StudentDto? {
        return try {
            SupabaseClient.client.from("organization_students")
                .select(columns = Columns.raw("*")) {
                    filter {
                        eq("id", studentId)
                    }
                }.decodeSingleOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchStudentEnrollments(studentIds: List<String>): List<StudentEnrollmentDto> {
        return try {
            SupabaseClient.client.from("organization_student_enrollments")
                .select(columns = Columns.raw("*")) {
                    filter {
                        isIn("student_id", studentIds)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchStudentAdditionalDetails(studentIds: List<String>): List<StudentAdditionalDetailsDto> {
        return try {
            SupabaseClient.client.from("organization_student_additional_details")
                .select(columns = Columns.raw("*")) {
                    filter {
                        isIn("student_id", studentIds)
                    }
                }.decodeList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchGuardiansByIds(guardianIds: List<String>): List<GuardianDto> {
        return try {
            SupabaseClient.client.from("organization_guardians")
                .select(columns = Columns.raw("*")) {
                    filter {
                        isIn("id", guardianIds)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchStudentQrIdentities(studentIds: List<String>): List<StudentQrIdentityDto> {
        return try {
            SupabaseClient.client.from("organization_student_qr_identities")
                .select(columns = Columns.raw("*")) {
                    filter {
                        isIn("student_id", studentIds)
                        eq("status", "Active")
                        eq("is_active", true)
                    }
                }.decodeList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchStudentIdCards(studentIds: List<String>): List<StudentIdCardDto> {
        return try {
            SupabaseClient.client.from("organization_student_id_cards")
                .select(columns = Columns.raw("*")) {
                    filter {
                        isIn("student_id", studentIds)
                        eq("status", "Active")
                        eq("is_active", true)
                    }
                }.decodeList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchStudentHomeLocations(studentIds: List<String>): List<StudentHomeLocationDto> {
        return try {
            SupabaseClient.client.from("organization_student_home_locations")
                .select(columns = Columns.raw("*")) {
                    filter {
                        isIn("student_id", studentIds)
                    }
                }.decodeList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchStudentAdditionalFees(studentId: String): List<StudentAdditionalFeeDto> {
        return SupabaseClient.client.from("organization_student_additional_fees")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("student_id", studentId)
                }
            }.decodeList()
    }

    suspend fun fetchStudentFeePayments(studentIds: List<String>): List<StudentFeePaymentDto> {
        return SupabaseClient.client.from("organization_student_fee_payments")
            .select(columns = Columns.raw("*")) {
                filter {
                    isIn("student_id", studentIds)
                }
            }.decodeList()
    }

    suspend fun fetchParentExpenses(parentOrgId: String): List<ParentExpenseDto> {
        return SupabaseClient.client.from("organization_parent_expenses")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("parent_organization_id", parentOrgId)
                }
            }.decodeList()
    }

    suspend fun fetchStudentAttendance(studentIds: List<String>): List<StudentAttendanceDto> {
        return SupabaseClient.client.from("organization_student_attendance")
            .select(columns = Columns.raw("*")) {
                filter {
                    isIn("student_id", studentIds)
                }
            }.decodeList()
    }

    suspend fun fetchParentBusTrips(driverId: String): List<ParentBusTripDto> {
        return SupabaseClient.client.from("organization_parent_bus_trips")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("driver_id", driverId)
                }
            }.decodeList()
    }

    suspend fun fetchBusTripAttendanceLogs(tripId: String): List<ParentBusTripAttendanceLogDto> {
        return SupabaseClient.client.from("organization_parent_bus_trip_attendance_logs")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("trip_id", tripId)
                }
            }.decodeList()
    }

    suspend fun fetchLeaves(parentOrgId: String): List<OrganizationLeaveDto> {
        return SupabaseClient.client.from("organization_leaves")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("parent_organization_id", parentOrgId)
                }
            }.decodeList()
    }

    suspend fun fetchRemarks(parentOrgId: String): List<OrganizationRemarkDto> {
        return SupabaseClient.client.from("organization_remarks")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("parent_organization_id", parentOrgId)
                }
            }.decodeList()
    }

    suspend fun fetchCalendarEvents(parentOrgId: String): List<CalendarEventDto> {
        return SupabaseClient.client.from("organization_calendar_events")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("parent_organization_id", parentOrgId)
                }
            }.decodeList()
    }

    suspend fun fetchStaffAttendance(parentOrgId: String, date: String): List<ParentStaffAttendanceDto> {
        return SupabaseClient.client.from("organization_parent_staff_attendance")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("parent_organization_id", parentOrgId)
                    eq("attendance_date", date)
                }
            }.decodeList()
    }

    suspend fun fetchStaffProfiles(parentOrgId: String): List<ParentStaffDto> {
        return SupabaseClient.client.from("organization_parent_staff")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("parent_organization_id", parentOrgId)
                }
            }.decodeList()
    }

    suspend fun fetchExams(orgId: String, sessionId: String): List<OrganizationExamDto> {
        return SupabaseClient.client.from("organization_exams")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("organization_id", orgId)
                    eq("active_session_id", sessionId)
                }
            }.decodeList()
    }

    suspend fun fetchExamSubjectSettings(orgId: String, sessionId: String): List<ExamSubjectSettingDto> {
        return SupabaseClient.client.from("organization_exam_subject_settings")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("organization_id", orgId)
                    eq("active_session_id", sessionId)
                }
            }.decodeList()
    }

    suspend fun fetchStudentExamMarks(examId: String, classId: String, subjectId: String): List<StudentExamMarkDto> {
        return SupabaseClient.client.from("organization_student_exam_marks")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("exam_id", examId)
                    eq("class_id", classId)
                    eq("subject_id", subjectId)
                }
            }.decodeList()
    }

    suspend fun fetchStudentAdditionalFeesBulk(studentIds: List<String>): List<StudentAdditionalFeeDto> {
        if (studentIds.isEmpty()) return emptyList()
        return try {
            SupabaseClient.client.from("organization_student_additional_fees")
                .select(columns = Columns.raw("*")) {
                    filter {
                        isIn("student_id", studentIds)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchStudentExamMarksBulk(studentIds: List<String>): List<StudentExamMarkDto> {
        if (studentIds.isEmpty()) return emptyList()
        return try {
            SupabaseClient.client.from("organization_student_exam_marks")
                .select(columns = Columns.raw("*")) {
                    filter {
                        isIn("student_id", studentIds)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchStudentBusAssignments(studentIds: List<String>): List<StudentBusAssignmentDto> {
        if (studentIds.isEmpty()) return emptyList()
        return SupabaseClient.client.from("organization_student_bus_assignments")
            .select(columns = Columns.raw("*")) {
                filter {
                    isIn("student_id", studentIds)
                    eq("is_active", true)
                    eq("is_deleted", false)
                }
            }.decodeList()
    }

    suspend fun fetchStudentBusAssignmentsByBusId(busId: String): List<StudentBusAssignmentDto> {
        if (busId.isEmpty()) return emptyList()
        return SupabaseClient.client.from("organization_student_bus_assignments")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("bus_id", busId)
                    eq("is_active", true)
                    eq("is_deleted", false)
                }
            }.decodeList()
    }

    // ── Upsert / Sync Pushes ──────────────────────────────────────────────────

    suspend fun upsertBusAttendanceLogs(logs: List<ParentBusTripAttendanceLogDto>) {
        android.util.Log.d("SupabaseSync", "upsertBusAttendanceLogs calling: $logs")
        try {
            val response = SupabaseClient.client.from("organization_parent_bus_trip_attendance_logs").upsert(logs)
            android.util.Log.d("SupabaseSync", "upsertBusAttendanceLogs SUCCESS: $response")
        } catch (e: Exception) {
            android.util.Log.e("SupabaseSync", "upsertBusAttendanceLogs FAILURE: error=${e.message}", e)
            throw e
        }
    }

    suspend fun upsertRemarks(remarks: List<OrganizationRemarkDto>) {
        val remarksToInsert = remarks.map {
            OrganizationRemarkInsertDto(
                id = it.id,
                parent_organization_id = it.parent_organization_id,
                organization_id = it.organization_id?.takeIf { it.isNotBlank() },
                active_session_id = it.active_session_id,
                content = it.content,
                category = it.category,
                priority = it.priority,
                creator_user_id = it.creator_user_id,
                creator_workspace_role_id = it.creator_workspace_role_id,
                visibility_type = it.visibility_type,
                visibility_audience = it.visibility_audience,
                is_pinned = it.is_pinned,
                pin_expires_at = it.pin_expires_at?.takeIf { it.isNotBlank() },
                expires_at = it.expires_at?.takeIf { it.isNotBlank() },
                is_active = it.is_active,
                is_deleted = it.is_deleted
            )
        }
        val targetsToInsert = remarks.map {
            OrganizationRemarkTargetInsertDto(
                id = it.target_id.ifEmpty { java.util.UUID.randomUUID().toString() },
                parent_organization_id = it.parent_organization_id,
                organization_id = it.organization_id?.takeIf { it.isNotBlank() },
                active_session_id = it.active_session_id,
                remark_id = it.id,
                target_type = it.target_type,
                target_student_id = it.target_student_id?.takeIf { it.isNotBlank() },
                target_guardian_id = it.target_guardian_id?.takeIf { it.isNotBlank() },
                target_staff_id = it.target_staff_id?.takeIf { it.isNotBlank() },
                target_user_id = it.target_user_id?.takeIf { it.isNotBlank() },
                is_active = it.is_active,
                is_deleted = it.is_deleted
            )
        }
        SupabaseClient.client.from("organization_remarks").upsert(remarksToInsert)
        SupabaseClient.client.from("organization_remark_targets").upsert(targetsToInsert)
    }

    suspend fun upsertStudentAttendance(attendance: List<StudentAttendanceDto>) {
        SupabaseClient.client.from("organization_student_attendance").upsert(attendance)
    }

    suspend fun upsertStudentExamMarks(marks: List<StudentExamMarkDto>) {
        SupabaseClient.client.from("organization_student_exam_marks").upsert(marks)
    }

    suspend fun upsertLeaves(leaves: List<OrganizationLeaveDto>) {
        SupabaseClient.client.from("organization_leaves").upsert(leaves)
    }

    suspend fun upsertBusTrip(trip: ParentBusTripDto) {
        android.util.Log.d("SupabaseSync", "upsertBusTrip calling: $trip")
        try {
            val response = SupabaseClient.client.from("organization_parent_bus_trips").upsert(trip)
            android.util.Log.d("SupabaseSync", "upsertBusTrip SUCCESS: $response")
        } catch (e: Exception) {
            android.util.Log.e("SupabaseSync", "upsertBusTrip FAILURE: error=${e.message}", e)
            throw e
        }
    }

    suspend fun fetchGlobalStaffRoles(): List<GlobalStaffRoleDto> {
        return SupabaseClient.client.from("global_staff_roles")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("is_active", true)
                    eq("is_deleted", false)
                }
            }.decodeList()
    }

    suspend fun upsertBusLiveLocation(dto: BusLiveLocationDto) {
        SupabaseClient.client.from("organization_parent_bus_live_locations").upsert(dto)
    }

    suspend fun fetchBusLiveLocation(busId: String): BusLiveLocationDto? {
        return SupabaseClient.client.from("organization_parent_bus_live_locations")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("bus_id", busId)
                }
            }.decodeSingleOrNull<BusLiveLocationDto>()
    }
}