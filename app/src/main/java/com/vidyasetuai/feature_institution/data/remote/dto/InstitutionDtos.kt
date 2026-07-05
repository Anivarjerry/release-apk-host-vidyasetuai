package com.vidyasetuai.feature_institution.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorkspaceLinkDto(
    val id: String,
    val user_id: String,
    val is_approved: Boolean = false,
    val approved_by: String? = null,
    val approved_at: String? = null,
    val role: String = "",
    val parent_organization_id: String = "",
    val child_organization_id: String? = null,
    val organization_id: String? = null,
    val staff_id: String? = null,
    val student_id: String? = null,
    val guardian_id: String? = null
)

@Serializable
data class ChildOrgSetupDto(
    val organization_id: String,
    val session_id: String,
    val session_name: String,
    val boards_json: String,
    val mediums_json: String,
    val languages_json: String,
    val class_structure_json: String,
    val periods_json: String,
    val fees_structure_json: String,
    val is_setup_complete: Boolean
)

@Serializable
data class ParentBusDto(
    val id: String,
    val parent_organization_id: String,
    val active_session_id: String,
    val bus_number: String,
    val bus_name: String?,
    val route_name: String?,
    val max_capacity: Int?,
    val is_active: Boolean,
    val is_deleted: Boolean,
    val insurance_expiry_date: String?,
    val insurance_image_url: String?,
    val fitness_expiry_date: String?,
    val fitness_image_url: String?,
    val pollution_expiry_date: String?,
    val pollution_image_url: String?,
    val driver_id: String? = null,
    val driver_name: String? = null,
    val driver_mobile: String? = null,
    val conductor_id: String? = null,
    val conductor_name: String? = null,
    val conductor_mobile: String? = null
)

@Serializable
data class BusRouteDto(
    val id: String,
    val parent_organization_id: String,
    val active_session_id: String,
    val bus_id: String,
    val stop_name: String,
    val stop_order: Int,
    val latitude: Double?,
    val longitude: Double?,
    val scheduled_time: String? = null,
    val is_active: Boolean,
    val is_deleted: Boolean
)

@Serializable
data class StudentDto(
    val id: String,
    val organization_id: String,
    val active_session_id: String,
    val name: String,
    val gender: String? = null,
    val sr_number: String,
    val admission_date: String,
    val date_of_birth: String,
    val enrollment_number: String? = null,
    val image_url: String? = null,
    val guardian_image_url: String? = null,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val guardian_id: String,
    val category_id: String? = null,
    val blood_group_id: String? = null,
    val student_status_id: String? = null,
    val address_area_id: String? = null,
    val address_details: String? = null,
    val mother_tongue_id: String? = null,
    val religion: String? = null,
    val nationality: String = "Indian",
    val identification_mark: String? = null,
    val is_single_girl_child: Boolean = false,
    val caste_certificate_number: String? = null,
    val father_name: String? = null,
    val father_mobile: String? = null,
    val father_email: String? = null,
    val father_qualification: String? = null,
    val father_occupation: String? = null,
    val parents_aadhaar_father: String? = null,
    val mother_name: String? = null,
    val mother_mobile: String? = null,
    val mother_email: String? = null,
    val mother_qualification: String? = null,
    val mother_occupation: String? = null,
    val parents_aadhaar_mother: String? = null,
    val family_annual_income: Double? = null,
    val permanent_address_details: String? = null,
    val permanent_address_area: String? = null,
    val permanent_address_area_id: String? = null,
    val bank_account_number: String? = null,
    val bank_name: String? = null,
    val bank_branch: String? = null,
    val bank_ifsc: String? = null,
    val bank_account_holder: String? = null,
    val student_aadhar: String? = null
)

@Serializable
data class StudentEnrollmentDto(
    val id: String,
    val organization_id: String,
    val active_session_id: String,
    val student_id: String,
    val class_id: String,
    val section_id: String? = null,
    val roll_number: Int? = null,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false
)

@Serializable
data class StudentAdditionalDetailsDto(
    val student_id: String,
    val mother_tongue_id: String? = null,
    val religion: String? = null,
    val nationality: String = "Indian",
    val identification_mark: String? = null,
    val is_single_girl_child: Boolean = false,
    val caste_certificate_number: String? = null,
    val father_name: String? = null,
    val father_mobile: String? = null,
    val father_email: String? = null,
    val father_qualification: String? = null,
    val father_occupation: String? = null,
    val parents_aadhaar_father: String? = null,
    val mother_name: String? = null,
    val mother_mobile: String? = null,
    val mother_email: String? = null,
    val mother_qualification: String? = null,
    val mother_occupation: String? = null,
    val parents_aadhaar_mother: String? = null,
    val family_annual_income: Double? = null,
    val permanent_address_details: String? = null,
    val permanent_address_area: String? = null,
    val permanent_address_area_id: String? = null,
    val bank_account_number: String? = null,
    val bank_name: String? = null,
    val bank_branch: String? = null,
    val bank_ifsc: String? = null,
    val bank_account_holder: String? = null,
    val student_aadhar: String? = null,
    val previous_school_name: String? = null,
    val previous_class: String? = null,
    val previous_board: String? = null,
    val tc_number: String? = null,
    val tc_date: String? = null,
    val previous_marks: Double? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val medical_conditions: String? = null,
    val regular_medications: String? = null,
    val emergency_contact_name: String? = null,
    val emergency_contact_phone: String? = null
)

@Serializable
data class GuardianDto(
    val id: String,
    val name: String,
    val mobile_number: String,
    val relationship_type_id: String? = null
)

@Serializable
data class StudentQrIdentityDto(
    val id: String,
    val student_id: String,
    val qr_token_hash: String,
    val status: String,
    val expiry_date: String? = null,
    val is_active: Boolean = true
)

@Serializable
data class StudentIdCardDto(
    val id: String,
    val student_id: String,
    val card_number: String,
    val status: String,
    val reason_for_reissue: String? = null,
    val is_active: Boolean = true
)

@Serializable
data class StudentHomeLocationDto(
    val student_id: String,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class StudentAdditionalFeeDto(
    val id: String,
    val organization_id: String,
    val active_session_id: String,
    val student_id: String,
    val global_fee_head_id: String,
    val global_fee_head_name: String? = null,
    val global_fee_head_code: String? = null,
    val amount: Double,
    val is_active: Boolean,
    val is_deleted: Boolean
)

@Serializable
data class StudentFeePaymentDto(
    val id: String,
    val organization_id: String,
    val active_session_id: String,
    val student_id: String,
    val receipt_number: String,
    val payment_mode: String,
    val payment_date: String,
    val amount_paid: Double,
    val discount_amount: Double,
    val fine_amount: Double,
    val discount_reason: String?,
    val cash_received_by_user_id: String?,
    val cash_received_by_user_name: String? = null,
    val cheque_number: String?,
    val online_transaction_id: String?,
    val remarks: String?,
    val status: String,
    val is_active: Boolean,
    val is_deleted: Boolean
)

@Serializable
data class StudentBusAssignmentDto(
    val id: String,
    val student_id: String,
    val bus_id: String,
    val pickup_stop: String? = null,
    val is_active: Boolean,
    val is_deleted: Boolean
)

@Serializable
data class ParentExpenseDto(
    val id: String,
    val parent_organization_id: String,
    val active_session_id: String,
    val expense_type_id: String,
    val payment_method: String,
    val amount: Double,
    val receipt_uuid: String?,
    val admin_note: String?,
    val expense_date: String,
    val reference_id: String?,
    val reference_type: String?,
    val vendor_name: String?,
    val bill_number: String?,
    val is_active: Boolean,
    val created_by: String?
)

@Serializable
data class StudentAttendanceDto(
    val id: String,
    val organization_id: String,
    val active_session_id: String,
    val student_id: String,
    val student_name: String? = null,
    val roll_number: Int? = null,
    val class_id: String? = null,
    val class_name: String? = null,
    val section_id: String? = null,
    val section_name: String? = null,
    val attendance_date: String,
    val status: String,
    val remarks: String?,
    val marked_by_staff_id: String?,
    val marked_by_staff_name: String? = null,
    val is_active: Boolean,
    val is_deleted: Boolean
)

@Serializable
data class ParentBusTripDto(
    val id: String,
    val parent_organization_id: String,
    val active_session_id: String,
    val bus_id: String,
    val bus_number: String? = null,
    val bus_name: String? = null,
    val driver_id: String,
    val driver_name: String? = null,
    val driver_phone: String? = null,
    val trip_type: String,
    val status: String,
    val start_time: String?,
    val end_time: String?,
    val is_active: Boolean,
    val is_deleted: Boolean
)

@Serializable
data class ParentBusTripAttendanceLogDto(
    val id: String,
    val parent_organization_id: String,
    val organization_id: String,
    val active_session_id: String,
    val trip_id: String,
    val student_id: String,
    val student_name: String? = null,
    val roll_number: Int? = null,
    val class_name: String? = null,
    val section_name: String? = null,
    val status: String,
    val scan_latitude: Double?,
    val scan_longitude: Double?,
    val scanned_at: String,
    val scanned_by_staff_id: String,
    val scanned_by_staff_name: String? = null,
    val is_active: Boolean,
    val is_deleted: Boolean
)

@Serializable
data class OrganizationLeaveDto(
    val id: String,
    val parent_organization_id: String,
    val organization_id: String?,
    val active_session_id: String,
    val applicant_type: String,
    val staff_id: String?,
    val student_id: String?,
    val leave_type: String,
    val start_date: String,
    val end_date: String,
    val is_half_day: Boolean,
    val half_day_period: String?,
    val reason: String?,
    val status: String,
    val action_remarks: String?,
    val action_by: String?,
    val action_at: String?,
    val is_active: Boolean,
    val is_deleted: Boolean,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
data class OrganizationRemarkDto(
    val id: String,
    val parent_organization_id: String,
    val organization_id: String?,
    val active_session_id: String,
    val content: String,
    val category: String,
    val priority: String,
    val creator_user_id: String,
    @kotlinx.serialization.SerialName("creator_workspace_role_id")
    val creator_workspace_role_id: String?,
    val visibility_type: String,
    @kotlinx.serialization.SerialName("visibility_audience")
    val visibility_audience: List<String>,
    val is_pinned: Boolean,
    val pin_expires_at: String?,
    val expires_at: String?,
    val target_id: String,
    val target_type: String,
    val target_student_id: String?,
    val target_guardian_id: String?,
    val target_staff_id: String?,
    val target_user_id: String?,
    val is_active: Boolean,
    val is_deleted: Boolean,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
data class OrganizationRemarkInsertDto(
    val id: String,
    val parent_organization_id: String,
    val organization_id: String?,
    val active_session_id: String,
    val content: String,
    val category: String,
    val priority: String,
    val creator_user_id: String,
    @kotlinx.serialization.SerialName("creator_workspace_role_id")
    val creator_workspace_role_id: String?,
    val visibility_type: String,
    @kotlinx.serialization.SerialName("visibility_audience")
    val visibility_audience: List<String>,
    val is_pinned: Boolean,
    val pin_expires_at: String?,
    val expires_at: String?,
    val is_active: Boolean,
    val is_deleted: Boolean,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
data class OrganizationRemarkTargetInsertDto(
    val id: String,
    val parent_organization_id: String,
    val organization_id: String?,
    val active_session_id: String,
    val remark_id: String,
    val target_type: String,
    val target_student_id: String?,
    val target_guardian_id: String?,
    val target_staff_id: String?,
    val target_user_id: String?,
    val is_active: Boolean,
    val is_deleted: Boolean,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
data class OrgProfileSessionDto(
    val active_session_id: String?
)

@Serializable
data class OrgParentProfileSessionDto(
    val active_session_id: String?
)

@Serializable
data class OrgProfileSessionMapDto(
    val organization_id: String,
    val active_session_id: String?
)

@Serializable
data class OrgParentProfileSessionMapDto(
    val parent_organization_id: String,
    val active_session_id: String?
)

@Serializable
data class CalendarEventDto(
    val id: String,
    val parent_organization_id: String,
    val organization_id: String,
    val active_session_id: String,
    val name: String,
    val description: String?,
    val start_date: String,
    val end_date: String,
    val event_type: String,
    val is_school_closed: Boolean,
    val is_active: Boolean,
    val is_deleted: Boolean
)

@Serializable
data class ParentStaffAttendanceDto(
    val id: String,
    val parent_organization_id: String,
    val active_session_id: String,
    val staff_id: String,
    val attendance_date: String,
    val status_id: String,
    val is_paid_leave: Boolean,
    val check_in_time: String?,
    val check_out_time: String?,
    val remarks: String?,
    val is_active: Boolean,
    val is_deleted: Boolean
)

@Serializable
data class ParentStaffDto(
    val id: String,
    val parent_organization_id: String,
    val active_session_id: String,
    val name: String,
    val mobile_number: String,
    val email: String?,
    val gender: String?,
    val date_of_joining: String,
    val date_of_birth: String?,
    val pan_number: String?,
    val aadhaar_number: String?,
    val license_number: String?,
    val license_expiry_date: String?,
    val is_active: Boolean,
    val is_deleted: Boolean,
    val role_id: String?,
    val subject_id: String?,
    val address_area_id: String?
)

@Serializable
data class OrganizationExamDto(
    val id: String,
    val organization_id: String,
    val active_session_id: String,
    val exam_type_id: String,
    val name: String,
    val start_date: String?,
    val end_date: String?,
    val is_active: Boolean,
    val is_deleted: Boolean
)

@Serializable
data class ExamSubjectSettingDto(
    val id: String,
    val organization_id: String,
    val active_session_id: String,
    val exam_id: String,
    val class_id: String,
    val subject_id: String,
    val class_name: String? = null,
    val subject_name: String? = null,
    val max_marks: Double? = null,
    val minimum_passing_marks: Double? = null,
    val grading_system: kotlinx.serialization.json.JsonElement? = null,
    val is_deleted: Boolean = false
)

@Serializable
data class StudentExamMarkDto(
    val id: String,
    val organization_id: String,
    val active_session_id: String,
    val exam_id: String,
    val class_id: String,
    val subject_id: String,
    val student_id: String,
    val obtained_marks: Double?,
    val is_absent: Boolean,
    val is_medical_leave: Boolean,
    val teacher_remarks: String?,
    val is_active: Boolean,
    val is_deleted: Boolean
)

@Serializable
data class RoleNameDto(
    val id: String = "",
    val name: String = ""
)

@Serializable
data class StaffRoleIdDto(
    val id: String = "",
    val role_id: String? = null,
    val name: String? = null
)

@Serializable
data class OrgNameDto(
    val id: String = "",
    val name: String = "",
    val parent_organization_id: String? = null
)

@Serializable
data class ParentOrgNameDto(
    val id: String = "",
    val name: String = ""
)

@Serializable
data class OrgClassMapDto(
    val id: String,
    val class_id: String
)

@Serializable
data class GlobalClassMapDto(
    val id: String,
    val name: String
)

@Serializable
data class OrgSectionMapDto(
    val id: String,
    val organization_class_id: String,
    val name: String
)

@Serializable
data class GlobalSessionMapDto(
    val id: String,
    val name: String
)

@Serializable
data class OrgBoardMapDto(
    val board_id: String
)

@Serializable
data class OrgMediumMapDto(
    val medium_id: String
)

@Serializable
data class OrgLanguageMapDto(
    val language_id: String
)

@Serializable
data class GlobalIdNameDto(
    val id: String,
    val name: String
)

@Serializable
data class OrgPeriodDto(
    val id: String,
    val name: String,
    val start_time: String,
    val end_time: String,
    val is_break: Boolean
)

@Serializable
data class OrgFeeAssignmentDto(
    val id: String,
    val organization_class_id: String,
    val fee_head_id: String,
    val amount: Double
)

@Serializable
data class OrgProfileSetupDto(
    val is_setup_complete: Boolean
)

@Serializable
data class OrgParentIdDto(
    val parent_organization_id: String? = null
)

@Serializable
data class StaffIdNameDto(
    val id: String,
    val name: String
)

@Serializable
data class OrgSessionClassMapDto(
    val id: String,
    val organization_class_id: String
)

@Serializable
data class OrgSessionSubjectMapDto(
    val organization_session_class_id: String,
    val subject_id: String,
    val subject_teacher_id: String? = null,
    val is_elective: Boolean = false
)

@Serializable
data class GlobalSubjectMapDto(
    val id: String,
    val name: String,
    val code: String? = null
)

@Serializable
data class OrgSectionQueryDto(
    val id: String,
    val organization_class_id: String,
    val name: String,
    val room_number: String? = null,
    val max_capacity: Int? = null,
    val class_teacher_id: String? = null
)

@Serializable
data class OrgIdOnlyDto(
    val id: String = ""
)

@Serializable
data class OrgSessionSectionMapDto(
    val organization_session_class_id: String,
    val organization_section_id: String,
    val class_teacher_id: String? = null
)

@Serializable
data class StudentSyncDto(
    val id: String,
    val organization_id: String,
    val active_session_id: String,
    val name: String,
    val gender: String? = null,
    val sr_number: String,
    val admission_date: String,
    val date_of_birth: String,
    val enrollment_number: String? = null,
    val image_url: String? = null,
    val is_active: Boolean,
    val is_deleted: Boolean,
    
    val guardian_id: String,
    val guardian_name: String? = null,
    val guardian_mobile: String? = null,
    val guardian_relationship_name: String? = null,
    
    val category_id: String? = null,
    val category_name: String? = null,
    val blood_group_id: String? = null,
    val blood_group_name: String? = null,
    val student_status_id: String? = null,
    val student_status_name: String? = null,
    val address_area_id: String? = null,
    val address_area_name: String? = null,
    val address_details: String? = null,
    
    val class_id: String? = null,
    val class_name: String? = null,
    val section_id: String? = null,
    val section_name: String? = null,
    val roll_number: Int? = null,
    
    val qr_identity_id: String? = null,
    val qr_token_hash: String? = null,
    val qr_status: String? = null,
    val qr_expiry_date: String? = null,
    
    val id_card_id: String? = null,
    val card_number: String? = null,
    val id_card_status: String? = null,
    val id_card_reissue_reason: String? = null,
    
    val home_latitude: Double? = null,
    val home_longitude: Double? = null,
    
    val mother_tongue_id: String? = null,
    val mother_tongue_name: String? = null,
    val religion: String? = null,
    val nationality: String = "Indian",
    val identification_mark: String? = null,
    val is_single_girl_child: Boolean = false,
    val caste_certificate_number: String? = null,
    
    val father_name: String? = null,
    val father_mobile: String? = null,
    val father_email: String? = null,
    val father_qualification: String? = null,
    val father_occupation: String? = null,
    val parents_aadhaar_father: String? = null,
    val mother_name: String? = null,
    val mother_mobile: String? = null,
    val mother_email: String? = null,
    val mother_qualification: String? = null,
    val mother_occupation: String? = null,
    val parents_aadhaar_mother: String? = null,
    val family_annual_income: Double? = null,
    
    val permanent_address_details: String? = null,
    val permanent_address_area: String? = null,
    val permanent_address_area_id: String? = null,
    val permanent_area_name: String? = null,
    val previous_school_name: String? = null,
    val previous_class: String? = null,
    val previous_board: String? = null,
    val tc_number: String? = null,
    val tc_date: String? = null,
    val previous_marks: Double? = null,
    
    val height: Double? = null,
    val weight: Double? = null,
    val medical_conditions: String? = null,
    val regular_medications: String? = null,
    val emergency_contact_name: String? = null,
    val emergency_contact_phone: String? = null,
    
    val bank_account_number: String? = null,
    val bank_name: String? = null,
    val bank_branch: String? = null,
    val bank_ifsc: String? = null,
    val bank_account_holder: String? = null,
    val student_aadhar: String? = null
)

@Serializable
data class SimpleChildOrgSetupDto(
    val organization_id: String,
    val organization_name: String? = null,
    val email: String? = null,
    val mobile_number: String? = null,
    val alternate_mobile_number: String? = null,
    val address_line1: String? = null,
    val address_line2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val session_id: String,
    val session_name: String,
    val boards_json: String = "[]",
    val mediums_json: String = "[]",
    val languages_json: String = "[]",
    val class_structure_json: String = "[]",
    val periods_json: String = "[]",
    val fees_structure_json: String = "[]",
    val is_setup_complete: Boolean
)

@Serializable
data class WorkspaceSyncPayloadDto(
    val setups: List<SimpleChildOrgSetupDto> = emptyList(),
    val students: List<StudentSyncDto> = emptyList(),
    val student_additional_fees: List<StudentAdditionalFeeDto> = emptyList(),
    val student_fee_payments: List<StudentFeePaymentDto> = emptyList(),
    val student_attendance: List<StudentAttendanceDto> = emptyList(),
    val buses: List<ParentBusDto> = emptyList(),
    val bus_routes: List<BusRouteDto> = emptyList(),
    val staff_members: List<ParentStaffDto> = emptyList(),
    val expenses: List<ParentExpenseDto> = emptyList(),
    val exams: List<OrganizationExamDto> = emptyList(),
    val bus_trips: List<ParentBusTripDto> = emptyList(),
    val bus_trip_attendance_logs: List<ParentBusTripAttendanceLogDto> = emptyList(),
    val calendar_events: List<CalendarEventDto> = emptyList(),
    val exam_subject_settings: List<ExamSubjectSettingDto> = emptyList(),
    val remarks: List<OrganizationRemarkDto> = emptyList()
)

@Serializable
data class GlobalStaffRoleDto(
    val id: String,
    val name: String,
    val code: String,
    val description: String? = null,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false
)

@Serializable
data class BusLiveLocationDto(
    val bus_id: String,
    val parent_organization_id: String,
    val active_session_id: String? = null,
    val latitude: Double,
    val longitude: Double,
    val speed: Double,
    val updated_at: String
)
