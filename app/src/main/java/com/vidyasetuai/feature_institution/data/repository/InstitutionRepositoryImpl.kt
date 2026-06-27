package com.vidyasetuai.feature_institution.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_institution.domain.model.ConnectionState
import com.vidyasetuai.feature_institution.domain.model.Workspace
import com.vidyasetuai.feature_institution.domain.model.Leave
import com.vidyasetuai.feature_institution.domain.model.FeePayment
import com.vidyasetuai.feature_institution.domain.repository.InstitutionRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import com.vidyasetuai.feature_institution.data.local.dao.InstitutionDao
import com.vidyasetuai.feature_institution.data.local.entity.*
import com.vidyasetuai.feature_institution.domain.model.StudentSearchResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
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
import com.vidyasetuai.feature_institution.domain.model.StaffSalaryPayment
import com.vidyasetuai.feature_institution.domain.model.ContentFeedItem
import com.vidyasetuai.feature_institution.domain.model.StudentHomeLocation
import kotlinx.coroutines.launch
import com.vidyasetuai.feature_institution.domain.model.ParentBusTrip
import com.vidyasetuai.feature_institution.domain.model.ParentBusTripAttendanceLog
import com.vidyasetuai.feature_institution.domain.model.Remark
import com.vidyasetuai.feature_institution.domain.model.RemarkTarget
import com.vidyasetuai.feature_institution.domain.model.GlobalStaffRole


@Serializable
private data class StudentQrIdentityDto(
    val id: String,
    val organization_id: String,
    val active_session_id: String,
    val student_id: String,
    val qr_token_hash: String,
    val version: Int = 1,
    val status: String = "Active",
    val expiry_date: String? = null,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String = "",
    val updated_at: String = "",
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
private data class StudentIdCardDto(
    val id: String,
    val organization_id: String,
    val active_session_id: String,
    val student_id: String,
    val qr_identity_id: String,
    val card_number: String,
    val status: String = "Pending_Print",
    val reason_for_reissue: String? = null,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String = "",
    val updated_at: String = "",
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
private data class ParentBusTripDto(
    val id: String,
    val parent_organization_id: String,
    val active_session_id: String,
    val bus_id: String,
    val driver_id: String,
    val trip_type: String,
    val status: String = "Scheduled",
    val start_time: String? = null,
    val end_time: String? = null,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String = "",
    val updated_at: String = "",
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
private data class ParentBusTripAttendanceLogDto(
    val id: String,
    val parent_organization_id: String,
    val organization_id: String,
    val active_session_id: String,
    val trip_id: String,
    val student_id: String,
    val status: String = "Boarded",
    val scan_latitude: Double? = null,
    val scan_longitude: Double? = null,
    val scanned_at: String = "",
    val scanned_by_staff_id: String,
    val sync_status: String = "Synced",
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String = "",
    val updated_at: String = "",
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
private data class GuardianLinkDto(
    val id: String = "",
    val organization_id: String = "",
    val guardian_id: String = "",
    val is_approved: Boolean? = false,
    val is_active: Boolean? = true,
    val is_deleted: Boolean? = false
)

@Serializable
private data class StaffLinkDto(
    val id: String = "",
    val parent_organization_id: String = "",
    val staff_id: String = "",
    val is_approved: Boolean? = false,
    val is_active: Boolean? = true,
    val is_deleted: Boolean? = false
)

@Serializable
private data class StaffLinkDetailsDto(
    val staff_id: String,
    val parent_organization_id: String,
    val active_session_id: String
)

@Serializable
private data class StaffBusLinkDto(
    val bus_id: String? = null
)

@Serializable
private data class StudentLinkDto(
    val id: String = "",
    val organization_id: String = "",
    val student_id: String = "",
    val is_approved: Boolean? = false,
    val is_active: Boolean? = true,
    val is_deleted: Boolean? = false
)

@Serializable
private data class OrgDetailsDto(
    val id: String = "",
    val name: String = "",
    val parent_organization_id: String? = null
)

@Serializable
private data class ParentOrgDetailsDto(
    val id: String = "",
    val name: String = ""
)

@Serializable
private data class OrganizationProfileDto(
    val logo_url: String? = null,
    val website_url: String? = null,
    val email: String? = null,
    val mobile_number: String? = null,
    val address_line1: String? = null,
    val address_line2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null
)

@Serializable
private data class GuardianDto(
    val name: String? = null
)

@Serializable
private data class StudentDto(
    val name: String? = null
)

@Serializable
private data class StaffDto(
    val name: String? = null,
    val email: String? = null,
    val mobile_number: String? = null,
    val role_id: String? = null
)

@Serializable
private data class GlobalStaffRoleDto(
    val name: String = ""
)

@Serializable
private data class LeaveDto(
    val id: String,
    val parent_organization_id: String,
    val organization_id: String? = null,
    val applicant_type: String,
    val staff_id: String? = null,
    val student_id: String? = null,
    val leave_type: String,
    val start_date: String,
    val end_date: String,
    val is_half_day: Boolean,
    val half_day_period: String? = null,
    val reason: String? = null,
    val status: String
)

@Serializable
private data class LeaveQuotaDto(
    val total_leaves: Double
)

@Serializable
private data class FeePaymentDto(
    val id: String,
    val organization_id: String,
    val student_id: String,
    val receipt_number: String,
    val payment_mode: String,
    val payment_date: String,
    val amount_paid: Double,
    val discount_amount: Double,
    val fine_amount: Double,
    val discount_reason: String? = null,
    val remarks: String? = null
)

@Serializable
private data class GuardianLinkDetailsDto(
    val guardian_id: String? = null,
    val organization_id: String? = null,
    val active_session_id: String? = null
)

@Serializable
private data class GlobalSessionDto(
    val id: String? = null,
    val name: String? = null
)

@Serializable
private data class StaffDetailsForLeaveDto(
    val id: String? = null,
    val parent_organization_id: String? = null,
    val active_session_id: String? = null
)

@Serializable
private data class StudentDetailsForLeaveDto(
    val id: String? = null,
    val organization_id: String? = null,
    val active_session_id: String? = null
)

@Serializable
private data class LeaveInsertDto(
    val parent_organization_id: String,
    val organization_id: String? = null,
    val active_session_id: String,
    val applicant_type: String,
    val staff_id: String? = null,
    val student_id: String? = null,
    val leave_type: String,
    val start_date: String,
    val end_date: String,
    val is_half_day: Boolean,
    val half_day_period: String? = null,
    val reason: String? = null,
    val status: String,
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
private data class StudentDetailsDto(
    val id: String? = null,
    val name: String? = null,
    val active_session_id: String? = null
)

@Serializable
private data class EnrollmentDetailsDto(
    val student_id: String? = null,
    val class_id: String? = null
)

@Serializable
private data class OrgClassDetailsDto(
    val id: String? = null,
    val class_id: String? = null,
    val custom_class_name: String? = null
)

@Serializable
private data class GlobalClassDetailsDto(
    val id: String? = null,
    val name: String? = null
)

@Serializable
private data class FeeAssignmentDto(
    val organization_class_id: String? = null,
    val amount: Double? = 0.0
)

@Serializable
private data class StudentAdditionalFeeDto(
    val student_id: String? = null,
    val amount: Double? = 0.0
)

@Serializable
private data class StudentFeePaymentDto(
    val student_id: String? = null,
    val amount_paid: Double? = 0.0
)

@Serializable
private data class StudentAttendanceDto(
    val id: String,
    val student_id: String,
    val attendance_date: String,
    val status: String,
    val remarks: String? = null
)

@Serializable
private data class StudentBusAssignmentDto(
    val id: String,
    val student_id: String,
    val bus_id: String,
    val pickup_stop: String? = null
)

@Serializable
private data class BusDetailsDto(
    val id: String,
    val bus_number: String,
    val bus_name: String? = null,
    val route_name: String? = null
)

@Serializable
private data class BusLiveLocationDto(
    val bus_id: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Double,
    val updated_at: String
)

@Serializable
private data class BusLiveLocationUpdateDto(
    val bus_id: String,
    val parent_organization_id: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Double,
    val updated_at: String,
    val active_session_id: String? = null
)

@Serializable
private data class StaffSalaryDto(
    val monthly_salary: Double
)

@Serializable
private data class StaffSalaryPaymentDto(
    val id: String,
    val payment_date: String,
    val amount_paid: Double,
    val payment_mode: String,
    val online_transaction_id: String? = null,
    val online_payment_app: String? = null,
    val remarks: String? = null
)

@Serializable
private data class ExistingStudentAttendanceDto(
    val student_id: String,
    val status: String
)


@Serializable
private data class ChildOrgDto(
    val id: String,
    val name: String = "" // optional: sync mein kabhi kabhi sirf id aata hai
)

@Serializable
private data class GlobalClassNameDto(
    val name: String
)

@Serializable
private data class OrgClassJoinDto(
    val id: String,
    val global_classes: GlobalClassNameDto? = null
)

@Serializable
private data class OrgSectionDto(
    val id: String,
    val name: String
)

@Serializable
private data class StudentDetailsDtoForAttendance(
    val name: String,
    val sr_number: String
)

@Serializable
private data class StudentEnrollmentJoinDto(
    val student_id: String,
    val organization_students: StudentDetailsDtoForAttendance? = null
)

@Serializable
private data class AttendanceStudentLeaveDto(
    val student_id: String? = null,
    val status: String
)

@Serializable
private data class AttendanceInsertDto(
    val organization_id: String,
    val active_session_id: String,
    val student_id: String,
    val attendance_date: String,
    val status: String,
    val remarks: String? = null,
    val marked_by_staff_id: String? = null,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false
)

@Serializable
private data class OrgUserLinkDto(
    val id: String,
    val organization_id: String? = null
)

@Serializable
private data class ClassTeacherSectDto(
    val organization_section_id: String,
    val organization_session_class_id: String
)

@Serializable
private data class ClassTeacherClassDto(
    val organization_id: String,
    val organization_class_id: String
)

@Serializable
private data class ContentFeedOrgNameDto(
    val name: String = ""
)

@Serializable
private data class ContentFeedParentOrgNameDto(
    val name: String = ""
)

@Serializable
private data class ContentFeedItemDto(
    val id: String,
    val content_type: String,
    val publisher_type: String,
    val parent_organization_id: String? = null,
    val organization_id: String? = null,
    val session_id: String,
    val title: String? = null,
    val description: String? = null,
    val image_url: String? = null,
    val target_scope: String,
    val target_roles: List<String> = emptyList(),
    val status: String,
    val is_active: Boolean,
    val is_deleted: Boolean,
    val created_at: String,
    val organizations: ContentFeedOrgNameDto? = null,
    val organization_parents: ContentFeedParentOrgNameDto? = null
)

@Serializable
private data class ContentAssignmentDto(
    val content_id: String
)

@Serializable
private data class StudentImageVectorDto(
    val id: String,
    val organization_id: String,
    val student_id: String,
    val person_type: String,
    val face_vector: String = "",
    val image_url: String = "",
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String = "",
    val updated_at: String = ""
)

@Serializable
private data class StudentBusAssignmentCacheDto(
    val studentId: String,
    val studentName: String,
    val busId: String,
    val busNumber: String,
    val busName: String? = null,
    val routeName: String? = null,
    val pickupStop: String? = null
)

@Serializable
private data class StaffSalaryPaymentCacheDto(
    val id: String,
    val paymentDate: String,
    val amountPaid: Double,
    val paymentMode: String,
    val transactionId: String? = null,
    val paymentApp: String? = null,
    val remarks: String? = null
)

@Serializable
private data class StaffSalaryDetailsCacheDto(
    val monthlySalary: Double,
    val totalPaid: Double,
    val payments: List<StaffSalaryPaymentCacheDto>
)

@Serializable
private data class StaffLeaveQuotaCacheDto(
    val totalQuota: Double,
    val remaining: Double
)

class InstitutionRepositoryImpl(
    private val institutionDao: InstitutionDao
) : InstitutionRepository {

    private val jsonSerializer = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }

    private suspend inline fun <reified T> cacheAndReturn(
        key: String,
        forceRefresh: Boolean = false,
        crossinline remoteFetcher: suspend () -> T
    ): Result<T> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val cached = try {
                institutionDao.getCache(key)
            } catch (e: Exception) {
                null
            }
            if (cached != null) {
                try {
                    val data = jsonSerializer.decodeFromString<T>(cached.responseJson)
                    return@withContext Result.success(data)
                } catch (ex: Exception) {
                    Log.e("OfflineCache", "Failed to decode cache for key: $key", ex)
                }
            }
        }

        try {
            val data = remoteFetcher()
            try {
                val jsonStr = jsonSerializer.encodeToString(data)
                institutionDao.insertCache(OfflineCacheEntity(key, jsonStr, System.currentTimeMillis()))
            } catch (ex: Exception) {
                Log.e("OfflineCache", "Failed to write cache for key: $key", ex)
            }
            Result.success(data)
        } catch (e: Exception) {
            Log.e("OfflineCache", "Failed to fetch remote for key: $key. Falling back to local cache.", e)
            if (!forceRefresh) {
                Result.failure(e)
            } else {
                val cached = try {
                    institutionDao.getCache(key)
                } catch (ex: Exception) {
                    null
                }
                if (cached != null) {
                    try {
                        val data = jsonSerializer.decodeFromString<T>(cached.responseJson)
                        Result.success(data)
                    } catch (ex: Exception) {
                        Result.failure(e)
                    }
                } else {
                    Result.failure(e)
                }
            }
        }
    }

    override suspend fun checkConnectionStatus(userId: String): Result<ConnectionState> = withContext(Dispatchers.IO) {
        try {
            Log.d("VidyaSetu_Auth", "Checking connection status for userId: $userId")

            // 1. Check organization_guardian_user_links
            val guardianLinks = SupabaseClient.client.from("organization_guardian_user_links")
                .select(columns = Columns.raw("id, organization_id, guardian_id, is_approved, is_active, is_deleted")) {
                    filter {
                        eq("user_id", userId)
                        eq("is_deleted", false)
                        eq("is_active", true)
                    }
                }.decodeList<GuardianLinkDto>()

            // 2. Check organization_parent_staff_user_links
            val staffLinks = SupabaseClient.client.from("organization_parent_staff_user_links")
                .select(columns = Columns.raw("id, parent_organization_id, staff_id, is_approved, is_active, is_deleted")) {
                    filter {
                        eq("user_id", userId)
                        eq("is_deleted", false)
                        eq("is_active", true)
                    }
                }.decodeList<StaffLinkDto>()

            // 3. Check organization_student_user_links
            val studentLinks = SupabaseClient.client.from("organization_student_user_links")
                .select(columns = Columns.raw("id, organization_id, student_id, is_approved, is_active, is_deleted")) {
                    filter {
                        eq("user_id", userId)
                        eq("is_deleted", false)
                        eq("is_active", true)
                    }
                }.decodeList<StudentLinkDto>()

            Log.d("VidyaSetu_Auth", "checkConnectionStatus: guardianLinks=${guardianLinks.size}, staffLinks=${staffLinks.size}, studentLinks=${studentLinks.size}")

            val anyApproved = guardianLinks.any { it.is_approved == true } ||
                    staffLinks.any { it.is_approved == true } ||
                    studentLinks.any { it.is_approved == true }

            if (anyApproved) {
                Log.d("VidyaSetu_Auth", "checkConnectionStatus: found approved connections")
                return@withContext Result.success(ConnectionState.CONNECTED)
            }

            // Check for pending links and load details
            val pendingGuardian = guardianLinks.firstOrNull { it.is_approved != true }
            if (pendingGuardian != null) {
                Log.d("VidyaSetu_Auth", "checkConnectionStatus: found pending guardian link: ${pendingGuardian.id}")
                val orgId = pendingGuardian.organization_id
                val guardianId = pendingGuardian.guardian_id
                
                val org = SupabaseClient.client.from("organizations")
                    .select(columns = Columns.raw("name")) {
                        filter { eq("id", orgId) }
                    }.decodeList<OrgDetailsDto>().firstOrNull()
                
                val profile = SupabaseClient.client.from("organization_profiles")
                    .select(columns = Columns.raw("logo_url, website_url, email, mobile_number, address_line1, address_line2, city, state, pincode")) {
                        filter { eq("organization_id", orgId) }
                    }.decodeList<OrganizationProfileDto>().firstOrNull()

                val guardian = SupabaseClient.client.from("organization_guardians")
                    .select(columns = Columns.raw("name")) {
                        filter { eq("id", guardianId) }
                    }.decodeList<GuardianDto>().firstOrNull()

                return@withContext Result.success(
                    ConnectionState.PENDING(
                        role = "Guardian",
                        targetName = org?.name ?: "Institution",
                        personName = guardian?.name ?: "Guardian",
                        logoUrl = profile?.logo_url,
                        websiteUrl = profile?.website_url,
                        email = profile?.email,
                        mobileNumber = profile?.mobile_number,
                        address = buildAddressString(profile)
                    )
                )
            }

            val pendingStudent = studentLinks.firstOrNull { it.is_approved != true }
            if (pendingStudent != null) {
                Log.d("VidyaSetu_Auth", "checkConnectionStatus: found pending student link: ${pendingStudent.id}")
                val orgId = pendingStudent.organization_id
                val studentId = pendingStudent.student_id
                
                val org = SupabaseClient.client.from("organizations")
                    .select(columns = Columns.raw("name")) {
                        filter { eq("id", orgId) }
                    }.decodeList<OrgDetailsDto>().firstOrNull()
                
                val profile = SupabaseClient.client.from("organization_profiles")
                    .select(columns = Columns.raw("logo_url, website_url, email, mobile_number, address_line1, address_line2, city, state, pincode")) {
                        filter { eq("organization_id", orgId) }
                    }.decodeList<OrganizationProfileDto>().firstOrNull()

                val student = SupabaseClient.client.from("organization_students")
                    .select(columns = Columns.raw("name")) {
                        filter { eq("id", studentId) }
                    }.decodeList<StudentDto>().firstOrNull()

                return@withContext Result.success(
                    ConnectionState.PENDING(
                        role = "Student",
                        targetName = org?.name ?: "Institution",
                        personName = student?.name ?: "Student",
                        logoUrl = profile?.logo_url,
                        websiteUrl = profile?.website_url,
                        email = profile?.email,
                        mobileNumber = profile?.mobile_number,
                        address = buildAddressString(profile)
                    )
                )
            }
            val pendingStaff = staffLinks.firstOrNull { it.is_approved != true }
            if (pendingStaff != null) {
                Log.d("VidyaSetu_Auth", "checkConnectionStatus: found pending staff link: ${pendingStaff.id}")
                val parentOrgId = pendingStaff.parent_organization_id
                val staffId = pendingStaff.staff_id

                val parentOrg = SupabaseClient.client.from("organization_parents")
                    .select(columns = Columns.raw("name")) {
                        filter { eq("id", parentOrgId) }
                    }.decodeList<ParentOrgDetailsDto>().firstOrNull()

                val staff = SupabaseClient.client.from("organization_parent_staff")
                    .select(columns = Columns.raw("name, email, mobile_number, role_id")) {
                        filter { eq("id", staffId) }
                    }.decodeList<StaffDto>().firstOrNull()

                val roleName = if (staff?.role_id != null) {
                    SupabaseClient.client.from("global_staff_roles")
                        .select(columns = Columns.raw("name")) {
                            filter { eq("id", staff.role_id) }
                        }.decodeList<GlobalStaffRoleDto>().firstOrNull()?.name ?: "Staff"
                } else "Staff"

                return@withContext Result.success(
                    ConnectionState.PENDING(
                        role = roleName,
                        targetName = parentOrg?.name ?: "Parent Institution",
                        personName = staff?.name ?: "Staff Member",
                        logoUrl = null,
                        websiteUrl = null,
                        email = staff?.email,
                        mobileNumber = staff?.mobile_number,
                        address = null
                    )
                )
            }

            Log.d("VidyaSetu_Auth", "checkConnectionStatus: no approved or pending connections found")
            Result.success(ConnectionState.NOT_CONNECTED)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error checking connection status", e)
            Result.failure(e)
        }
    }

    private fun buildAddressString(profile: OrganizationProfileDto?): String? {
        if (profile == null) return null
        val parts = listOfNotNull(
            profile.address_line1?.takeIf { it.isNotBlank() },
            profile.address_line2?.takeIf { it.isNotBlank() },
            profile.city?.takeIf { it.isNotBlank() },
            profile.state?.takeIf { it.isNotBlank() },
            profile.pincode?.takeIf { it.isNotBlank() }
        )
        return if (parts.isEmpty()) null else parts.joinToString(", ")
    }

    override suspend fun approveConnection(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("VidyaSetu_Auth", "Approving connection request for userId: $userId")

            SupabaseClient.client.from("organization_guardian_user_links")
                .update(mapOf("is_approved" to true)) {
                    filter {
                        eq("user_id", userId)
                        eq("is_approved", false)
                    }
                }

            SupabaseClient.client.from("organization_parent_staff_user_links")
                .update(mapOf("is_approved" to true)) {
                    filter {
                        eq("user_id", userId)
                        eq("is_approved", false)
                    }
                }

            SupabaseClient.client.from("organization_student_user_links")
                .update(mapOf("is_approved" to true)) {
                    filter {
                        eq("user_id", userId)
                        eq("is_approved", false)
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error approving connection request", e)
            Result.failure(e)
        }
    }

    override suspend fun getPendingApprovals(userId: String, forceRefresh: Boolean): Result<List<PendingApproval>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val local = try {
                institutionDao.getPendingApprovals(userId).map { it.toDomain() }
            } catch (e: Exception) {
                emptyList()
            }
            if (local.isNotEmpty()) {
                return@withContext Result.success(local)
            }
        }

        try {
            Log.d("VidyaSetu_Auth", "Fetching pending approvals from remote for userId: $userId")
            val pendingList = mutableListOf<PendingApproval>()

            // 1. organization_guardian_user_links
            val guardianLinks = SupabaseClient.client.from("organization_guardian_user_links")
                .select(columns = Columns.raw("id, organization_id, guardian_id, is_approved")) {
                    filter {
                        eq("user_id", userId)
                        eq("is_deleted", false)
                        eq("is_active", true)
                    }
                }.decodeList<GuardianLinkDto>().filter { it.is_approved != true }

            for (link in guardianLinks) {
                val org = SupabaseClient.client.from("organizations")
                    .select(columns = Columns.raw("name")) {
                        filter { eq("id", link.organization_id) }
                    }.decodeList<OrgDetailsDto>().firstOrNull()

                val guardian = SupabaseClient.client.from("organization_guardians")
                    .select(columns = Columns.raw("name")) {
                        filter { eq("id", link.guardian_id) }
                    }.decodeList<GuardianDto>().firstOrNull()

                pendingList.add(
                    PendingApproval(
                        id = link.id,
                        role = "Guardian",
                        targetName = org?.name ?: "Institution",
                        personName = guardian?.name ?: "Guardian",
                        tableName = "organization_guardian_user_links"
                    )
                )
            }

            // 2. organization_parent_staff_user_links
            val staffLinks = SupabaseClient.client.from("organization_parent_staff_user_links")
                .select(columns = Columns.raw("id, parent_organization_id, staff_id, is_approved")) {
                    filter {
                        eq("user_id", userId)
                        eq("is_deleted", false)
                        eq("is_active", true)
                    }
                }.decodeList<StaffLinkDto>().filter { it.is_approved != true }
            for (link in staffLinks) {
                val parentOrg = SupabaseClient.client.from("organization_parents")
                    .select(columns = Columns.raw("name")) {
                        filter { eq("id", link.parent_organization_id) }
                    }.decodeList<ParentOrgDetailsDto>().firstOrNull()

                val staff = SupabaseClient.client.from("organization_parent_staff")
                    .select(columns = Columns.raw("name, role_id")) {
                        filter { eq("id", link.staff_id) }
                    }.decodeList<StaffDto>().firstOrNull()

                val roleName = if (staff?.role_id != null) {
                    SupabaseClient.client.from("global_staff_roles")
                        .select(columns = Columns.raw("name")) {
                            filter { eq("id", staff.role_id) }
                        }.decodeList<GlobalStaffRoleDto>().firstOrNull()?.name ?: "Staff"
                } else "Staff"

                pendingList.add(
                    PendingApproval(
                        id = link.id,
                        role = roleName,
                        targetName = parentOrg?.name ?: "Parent Institution",
                        personName = staff?.name ?: "Staff Member",
                        tableName = "organization_parent_staff_user_links"
                    )
                )
            }

            // 3. organization_student_user_links
            val studentLinks = SupabaseClient.client.from("organization_student_user_links")
                .select(columns = Columns.raw("id, organization_id, student_id, is_approved")) {
                    filter {
                        eq("user_id", userId)
                        eq("is_deleted", false)
                        eq("is_active", true)
                    }
                }.decodeList<StudentLinkDto>().filter { it.is_approved != true }

            for (link in studentLinks) {
                val org = SupabaseClient.client.from("organizations")
                    .select(columns = Columns.raw("name")) {
                        filter { eq("id", link.organization_id) }
                    }.decodeList<OrgDetailsDto>().firstOrNull()

                val student = SupabaseClient.client.from("organization_students")
                    .select(columns = Columns.raw("name")) {
                        filter { eq("id", link.student_id) }
                    }.decodeList<StudentDto>().firstOrNull()

                pendingList.add(
                    PendingApproval(
                        id = link.id,
                        role = "Student",
                        targetName = org?.name ?: "Institution",
                        personName = student?.name ?: "Student",
                        tableName = "organization_student_user_links"
                    )
                )
            }

            // Save to database
            try {
                institutionDao.deletePendingApprovals(userId)
                institutionDao.insertPendingApprovals(pendingList.map { LocalPendingApprovalEntity.fromDomain(it, userId) })
            } catch (e: Exception) {
                Log.e("VidyaSetu_Auth", "Error writing pending approvals to local DB", e)
            }

            Result.success(pendingList)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error fetching pending approvals from remote", e)
            val local = try {
                institutionDao.getPendingApprovals(userId).map { it.toDomain() }
            } catch (ex: Exception) {
                emptyList()
            }
            if (local.isNotEmpty()) {
                Result.success(local)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun approveSpecificConnection(linkId: String, tableName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("VidyaSetu_Auth", "Approving connection link $linkId in table $tableName")
            SupabaseClient.client.from(tableName)
                .update(mapOf("is_approved" to true)) {
                    filter {
                        eq("id", linkId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error approving connection $linkId in table $tableName", e)
            Result.failure(e)
        }
    }

    override suspend fun getWorkspaces(userId: String): Result<List<Workspace>> = withContext(Dispatchers.IO) {
        val localList = try {
            institutionDao.getWorkspaces().map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }

        if (localList.isNotEmpty()) {
            // Start background update of workspaces so it updates in DB asynchronously
            kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                try {
                    fetchAndSyncRemoteWorkspaces(userId, localList)
                } catch (e: Exception) {
                    Log.e("VidyaSetu_Auth", "Background workspace sync failed", e)
                }
            }
            return@withContext Result.success(localList)
        }

        // If local is empty, we must fetch from remote synchronously (initial login/first load scenario)
        try {
            val finalList = fetchAndSyncRemoteWorkspaces(userId, emptyList())
            Result.success(finalList)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error getting workspaces from remote", e)
            Result.failure(e)
        }
    }

    private suspend fun fetchAndSyncRemoteWorkspaces(userId: String, localList: List<Workspace>): List<Workspace> {
        val list = mutableListOf<Workspace>()

        // 1. Fetch Guardian Workspaces
        val guardianLinks = SupabaseClient.client.from("organization_guardian_user_links")
            .select(columns = Columns.raw("id, organization_id, guardian_id")) {
                filter {
                    eq("user_id", userId)
                    eq("is_approved", true)
                    eq("is_active", true)
                    eq("is_deleted", false)
                }
            }.decodeList<GuardianLinkDto>()

        for (link in guardianLinks) {
            val org = SupabaseClient.client.from("organizations")
                .select(columns = Columns.raw("id, name, parent_organization_id")) {
                    filter { eq("id", link.organization_id) }
                }.decodeList<OrgDetailsDto>().firstOrNull()

            val parentName = if (org?.parent_organization_id != null) {
                SupabaseClient.client.from("organization_parents")
                    .select(columns = Columns.raw("name")) {
                        filter { eq("id", org.parent_organization_id) }
                    }.decodeList<ParentOrgDetailsDto>().firstOrNull()?.name ?: "Parent Org"
            } else "Parent Org"

            list.add(
                Workspace(
                    id = link.id,
                    parentOrgId = org?.parent_organization_id ?: "",
                    parentOrgName = parentName,
                    childOrgId = link.organization_id,
                    childOrgName = org?.name ?: "School",
                    role = "Guardian",
                    isActive = false
                )
            )
        }

        // 2. Fetch Student Workspaces
        val studentLinks = SupabaseClient.client.from("organization_student_user_links")
            .select(columns = Columns.raw("id, organization_id, student_id")) {
                filter {
                    eq("user_id", userId)
                    eq("is_approved", true)
                    eq("is_active", true)
                    eq("is_deleted", false)
                }
            }.decodeList<StudentLinkDto>()

        for (link in studentLinks) {
            val org = SupabaseClient.client.from("organizations")
                .select(columns = Columns.raw("id, name, parent_organization_id")) {
                    filter { eq("id", link.organization_id) }
                }.decodeList<OrgDetailsDto>().firstOrNull()

            val parentName = if (org?.parent_organization_id != null) {
                SupabaseClient.client.from("organization_parents")
                    .select(columns = Columns.raw("name")) {
                        filter { eq("id", org.parent_organization_id) }
                    }.decodeList<ParentOrgDetailsDto>().firstOrNull()?.name ?: "Parent Org"
            } else "Parent Org"

            list.add(
                Workspace(
                    id = link.id,
                    parentOrgId = org?.parent_organization_id ?: "",
                    parentOrgName = parentName,
                    childOrgId = link.organization_id,
                    childOrgName = org?.name ?: "School",
                    role = "Student",
                    isActive = false
                )
            )
        }

        // 3. Fetch Staff Workspaces
        val staffLinks = SupabaseClient.client.from("organization_parent_staff_user_links")
            .select(columns = Columns.raw("id, parent_organization_id, staff_id")) {
                filter {
                    eq("user_id", userId)
                    eq("is_approved", true)
                    eq("is_active", true)
                    eq("is_deleted", false)
                }
            }.decodeList<StaffLinkDto>()
        for (link in staffLinks) {
            val parentOrg = SupabaseClient.client.from("organization_parents")
                .select(columns = Columns.raw("id, name")) {
                    filter { eq("id", link.parent_organization_id) }
                }.decodeList<ParentOrgDetailsDto>().firstOrNull()

            val staff = SupabaseClient.client.from("organization_parent_staff")
                .select(columns = Columns.raw("role_id")) {
                    filter { eq("id", link.staff_id) }
                }.decodeList<StaffDto>().firstOrNull()

            val roleName = if (staff?.role_id != null) {
                SupabaseClient.client.from("global_staff_roles")
                    .select(columns = Columns.raw("name")) {
                        filter { eq("id", staff.role_id) }
                    }.decodeList<GlobalStaffRoleDto>().firstOrNull()?.name ?: "Teacher"
            } else {
                "Teacher"
            }

            list.add(
                Workspace(
                    id = link.id,
                    parentOrgId = link.parent_organization_id,
                    parentOrgName = parentOrg?.name ?: "Parent Org",
                    childOrgId = null,
                    childOrgName = null,
                    role = roleName,
                    isActive = false
                )
            )
        }

        // Merge local isActive state
        val mergedList = list.map { remote ->
            val local = localList.find { it.id == remote.id }
            if (local != null) {
                remote.copy(isActive = local.isActive)
            } else {
                remote
            }
        }

        // If no workspace is active, set first one active
        val finalList = if (mergedList.isNotEmpty() && mergedList.none { it.isActive }) {
            mergedList.mapIndexed { idx, ws -> if (idx == 0) ws.copy(isActive = true) else ws }
        } else {
            mergedList
        }

        // Save to database
        try {
            institutionDao.clearAll()
            institutionDao.insertWorkspaces(finalList.map { WorkspaceEntity.fromDomain(it) })
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error writing workspaces to local DB", e)
        }

        return finalList
    }

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
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            var resolvedStaffId = if (applicantType == "staff") staffId else null
            var resolvedStudentId = if (applicantType == "student") studentId else null
            var resolvedParentOrgId = parentOrgId.ifBlank { null }
            var resolvedOrgId = orgId?.ifBlank { null }
            var resolvedSessionId = sessionId.ifBlank { null }

            // 1. Resolve staff details if applicant type is staff
            if (applicantType == "staff") {
                if (resolvedStaffId == null || resolvedStaffId == "staff_mock_id_1" || resolvedStaffId.isBlank()) {
                    val staffLink = SupabaseClient.client.from("organization_parent_staff_user_links")
                        .select(columns = Columns.raw("id, parent_organization_id, staff_id")) {
                            filter {
                                eq("user_id", createdBy)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<StaffLinkDto>().firstOrNull()
                    resolvedStaffId = staffLink?.staff_id
                }

                if (resolvedStaffId != null) {
                    val staff = SupabaseClient.client.from("organization_parent_staff")
                        .select(columns = Columns.raw("parent_organization_id, active_session_id")) {
                            filter {
                                eq("id", resolvedStaffId)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeSingleOrNull<StaffDetailsForLeaveDto>()
                    if (staff != null) {
                        if (resolvedParentOrgId.isNullOrBlank()) resolvedParentOrgId = staff.parent_organization_id
                        if (resolvedSessionId.isNullOrBlank()) resolvedSessionId = staff.active_session_id
                    }
                }
            }

            // 2. Resolve student details if applicant type is student
            if (applicantType == "student") {
                if (resolvedStudentId == null || resolvedStudentId == "student_mock_id_1" || resolvedStudentId.isBlank()) {
                    val studentLink = SupabaseClient.client.from("organization_student_user_links")
                        .select(columns = Columns.raw("id, organization_id, student_id")) {
                            filter {
                                eq("user_id", createdBy)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<StudentLinkDto>().firstOrNull()
                    resolvedStudentId = studentLink?.student_id
                }

                if (resolvedStudentId != null) {
                    val student = SupabaseClient.client.from("organization_students")
                        .select(columns = Columns.raw("organization_id, active_session_id")) {
                            filter {
                                eq("id", resolvedStudentId)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeSingleOrNull<StudentDetailsForLeaveDto>()
                    if (student != null) {
                        if (resolvedOrgId.isNullOrBlank()) resolvedOrgId = student.organization_id
                        if (resolvedSessionId.isNullOrBlank()) resolvedSessionId = student.active_session_id
                    }
                }
                
                // If parent organization ID is not resolved yet, fetch from organizations
                if (resolvedParentOrgId.isNullOrBlank() && !resolvedOrgId.isNullOrBlank()) {
                    val org = SupabaseClient.client.from("organizations")
                        .select(columns = Columns.raw("parent_organization_id")) {
                            filter { eq("id", resolvedOrgId!!) }
                        }.decodeList<OrgDetailsDto>().firstOrNull()
                    resolvedParentOrgId = org?.parent_organization_id
                }
            }

            // 3. Fallback for Session ID if still mock/blank
            if (resolvedSessionId.isNullOrBlank() || resolvedSessionId == "session_mock_id_1") {
                val activeSession = SupabaseClient.client.from("global_sessions")
                    .select(columns = Columns.raw("id")) {
                        filter {
                            eq("is_active", true)
                        }
                    }.decodeList<GlobalSessionDto>().firstOrNull()
                resolvedSessionId = activeSession?.id ?: "fa000000-0000-0000-0000-000000000001"
            }

            // Fallback for Parent Org ID if still blank (fetch any organization's parent or default)
            if (resolvedParentOrgId.isNullOrBlank()) {
                val fallbackOrg = SupabaseClient.client.from("organizations")
                    .select(columns = Columns.raw("parent_organization_id"))
                    .decodeList<OrgDetailsDto>()
                    .firstOrNull { !it.parent_organization_id.isNullOrBlank() }
                resolvedParentOrgId = fallbackOrg?.parent_organization_id ?: "fa000000-0000-0000-0000-000000000001"
            }

            val leaveDto = LeaveInsertDto(
                parent_organization_id = resolvedParentOrgId ?: "fa000000-0000-0000-0000-000000000001",
                organization_id = resolvedOrgId,
                active_session_id = resolvedSessionId ?: "fa000000-0000-0000-0000-000000000001",
                applicant_type = applicantType,
                staff_id = resolvedStaffId,
                student_id = resolvedStudentId,
                leave_type = leaveType,
                start_date = startDate,
                end_date = endDate,
                is_half_day = isHalfDay,
                half_day_period = if (isHalfDay) halfDayPeriod else null,
                reason = reason,
                status = "Pending",
                created_by = createdBy,
                updated_by = createdBy
            )

            SupabaseClient.client.from("organization_leaves").insert(leaveDto)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error submitting leave", e)
            Result.failure(e)
        }
    }

    override suspend fun getLeaveQuotas(staffId: String): Result<Double> = withContext(Dispatchers.IO) {
        try {
            val quotas = SupabaseClient.client.from("organization_parent_staff_leave_quotas")
                .select(columns = Columns.raw("total_leaves")) {
                    filter {
                        eq("staff_id", staffId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<LeaveQuotaDto>().firstOrNull()
            Result.success(quotas?.total_leaves ?: 12.0)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error getting leave quotas", e)
            Result.failure(e)
        }
    }

    override suspend fun getStaffLeaveQuotaAndRemaining(
        userId: String,
        parentOrgId: String,
        forceRefresh: Boolean
    ): Result<Pair<Double, Double>> = withContext(Dispatchers.IO) {
        val cacheKey = "staff_leave_quota_${userId}_$parentOrgId"
        cacheAndReturn(cacheKey, forceRefresh) {
            // 1. Resolve staff_id from organization_parent_staff_user_links
            val staffLink = SupabaseClient.client.from("organization_parent_staff_user_links")
                .select(columns = Columns.raw("id, parent_organization_id, staff_id")) {
                    filter {
                        eq("user_id", userId)
                        eq("parent_organization_id", parentOrgId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<StaffLinkDto>().firstOrNull()

            val staffId = staffLink?.staff_id
                ?: throw Exception("Staff assignment not found")

            // 2. Fetch leave quota
            val quotas = SupabaseClient.client.from("organization_parent_staff_leave_quotas")
                .select(columns = Columns.raw("total_leaves")) {
                    filter {
                        eq("staff_id", staffId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<LeaveQuotaDto>().firstOrNull()
            val totalQuota = quotas?.total_leaves ?: 12.0

            // 3. Fetch approved leaves for this staff member
            val approvedLeaves = SupabaseClient.client.from("organization_leaves")
                .select(columns = Columns.raw("id, parent_organization_id, organization_id, applicant_type, staff_id, student_id, leave_type, start_date, end_date, is_half_day, half_day_period, reason, status")) {
                    filter {
                        eq("staff_id", staffId)
                        eq("status", "Approved")
                        eq("is_deleted", false)
                        eq("is_active", true)
                    }
                }.decodeList<LeaveDto>()

            // 4. Calculate total leaves taken
            var takenLeaves = 0.0
            for (leave in approvedLeaves) {
                if (leave.is_half_day) {
                    takenLeaves += 0.5
                } else {
                    takenLeaves += getDaysBetween(leave.start_date, leave.end_date)
                }
            }

            val remaining = maxOf(0.0, totalQuota - takenLeaves)
            StaffLeaveQuotaCacheDto(totalQuota, remaining)
        }.map { cacheDto ->
            Pair(cacheDto.totalQuota, cacheDto.remaining)
        }
    }

    private fun getDaysBetween(start: String, end: String): Double {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val startDate = sdf.parse(start)
            val endDate = sdf.parse(end)
            if (startDate != null && endDate != null) {
                val diff = endDate.time - startDate.time
                (diff / (24 * 60 * 60 * 1000)).toDouble() + 1.0
            } else {
                1.0
            }
        } catch (e: Exception) {
            1.0
        }
    }

    override suspend fun getLeaves(userId: String, role: String, forceRefresh: Boolean): Result<List<Leave>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val local = try {
                institutionDao.getLeavesForUser(userId).map { it.toDomain() }
            } catch (e: Exception) {
                emptyList()
            }
            if (local.isNotEmpty()) {
                return@withContext Result.success(local)
            }
        }

        try {
            val remoteLeaves = when (role) {
                "Student" -> {
                    val studentLink = SupabaseClient.client.from("organization_student_user_links")
                        .select(columns = Columns.raw("id, organization_id, student_id")) {
                            filter {
                                eq("user_id", userId)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<StudentLinkDto>().firstOrNull()
                    val studentId = studentLink?.student_id
                    if (studentId != null) {
                        SupabaseClient.client.from("organization_leaves")
                            .select(columns = Columns.raw("id, parent_organization_id, organization_id, applicant_type, staff_id, student_id, leave_type, start_date, end_date, is_half_day, half_day_period, reason, status")) {
                                filter {
                                    eq("student_id", studentId)
                                    eq("is_deleted", false)
                                    eq("is_active", true)
                                }
                            }.decodeList<LeaveDto>()
                    } else {
                        emptyList()
                    }
                }
                "Guardian" -> {
                    val guardianLinks = SupabaseClient.client.from("organization_guardian_user_links")
                        .select(columns = Columns.raw("id, organization_id, guardian_id")) {
                            filter {
                                eq("user_id", userId)
                                eq("is_approved", true)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<GuardianLinkDto>()
                    
                    val studentIds = mutableListOf<String>()
                    for (link in guardianLinks) {
                        val students = SupabaseClient.client.from("organization_students")
                            .select(columns = Columns.raw("id")) {
                                filter {
                                    eq("guardian_id", link.guardian_id)
                                    eq("organization_id", link.organization_id)
                                    eq("is_active", true)
                                    eq("is_deleted", false)
                                }
                            }.decodeList<StudentDetailsDto>()
                        studentIds.addAll(students.mapNotNull { it.id })
                    }

                    if (studentIds.isNotEmpty()) {
                        SupabaseClient.client.from("organization_leaves")
                            .select(columns = Columns.raw("id, parent_organization_id, organization_id, applicant_type, staff_id, student_id, leave_type, start_date, end_date, is_half_day, half_day_period, reason, status")) {
                                filter {
                                    isIn("student_id", studentIds)
                                    eq("is_deleted", false)
                                    eq("is_active", true)
                                }
                            }.decodeList<LeaveDto>()
                    } else {
                        emptyList()
                    }
                }
                "Teacher", "Driver", "Admin" -> {
                    val staffLink = SupabaseClient.client.from("organization_parent_staff_user_links")
                        .select(columns = Columns.raw("id, parent_organization_id, staff_id")) {
                            filter {
                                eq("user_id", userId)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<StaffLinkDto>().firstOrNull()
                    val staffId = staffLink?.staff_id
                    if (staffId != null) {
                        SupabaseClient.client.from("organization_leaves")
                            .select(columns = Columns.raw("id, parent_organization_id, organization_id, applicant_type, staff_id, student_id, leave_type, start_date, end_date, is_half_day, half_day_period, reason, status")) {
                                filter {
                                    eq("staff_id", staffId)
                                    eq("is_deleted", false)
                                    eq("is_active", true)
                                }
                            }.decodeList<LeaveDto>()
                    } else {
                        SupabaseClient.client.from("organization_leaves")
                            .select(columns = Columns.raw("id, parent_organization_id, organization_id, applicant_type, staff_id, student_id, leave_type, start_date, end_date, is_half_day, half_day_period, reason, status")) {
                                filter {
                                    eq("created_by", userId)
                                    eq("is_deleted", false)
                                    eq("is_active", true)
                                }
                            }.decodeList<LeaveDto>()
                    }
                }
                else -> {
                    SupabaseClient.client.from("organization_leaves")
                        .select(columns = Columns.raw("id, parent_organization_id, organization_id, applicant_type, staff_id, student_id, leave_type, start_date, end_date, is_half_day, half_day_period, reason, status")) {
                            filter {
                                eq("created_by", userId)
                                eq("is_deleted", false)
                                eq("is_active", true)
                            }
                        }.decodeList<LeaveDto>()
                }
            }

            val mapped = remoteLeaves.map { dto ->
                Leave(
                    id = dto.id,
                    parentOrgId = dto.parent_organization_id,
                    organizationId = dto.organization_id,
                    applicantType = dto.applicant_type,
                    staffId = dto.staff_id,
                    studentId = dto.student_id,
                    leaveType = dto.leave_type,
                    startDate = dto.start_date,
                    endDate = dto.end_date,
                    isHalfDay = dto.is_half_day,
                    halfDayPeriod = dto.half_day_period,
                    reason = dto.reason,
                    status = dto.status
                )
            }

            try {
                institutionDao.deleteLeavesForUser(userId)
                institutionDao.insertLeaves(mapped.map { LocalLeaveEntity.fromDomain(it, userId) })
            } catch (ex: Exception) {
                Log.e("VidyaSetu_Auth", "Error writing leaves to Room DB", ex)
            }

            Result.success(mapped)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error getting leaves from remote", e)
            val local = try {
                institutionDao.getLeavesForUser(userId).map { it.toDomain() }
            } catch (ex: Exception) {
                emptyList()
            }
            if (local.isNotEmpty()) {
                Result.success(local)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getFeePayments(studentIds: List<String>, forceRefresh: Boolean): Result<List<FeePayment>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val local = try {
                institutionDao.getFeePaymentsForStudents(studentIds).map { it.toDomain() }
            } catch (e: Exception) {
                emptyList()
            }
            if (local.isNotEmpty()) {
                return@withContext Result.success(local)
            }
        }

        try {
            val dtoList = if (studentIds.isEmpty()) emptyList()
            else {
                SupabaseClient.client.from("organization_student_fee_payments")
                    .select(columns = Columns.raw("id, organization_id, student_id, receipt_number, payment_mode, payment_date, amount_paid, discount_amount, fine_amount, discount_reason, remarks")) {
                        filter {
                            isIn("student_id", studentIds)
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeList<FeePaymentDto>()
            }

            val mapped = dtoList.map { dto ->
                FeePayment(
                    id = dto.id,
                    studentId = dto.student_id,
                    receiptNumber = dto.receipt_number,
                    paymentMode = dto.payment_mode,
                    paymentDate = dto.payment_date,
                    amountPaid = dto.amount_paid,
                    discountAmount = dto.discount_amount,
                    fineAmount = dto.fine_amount,
                    discountReason = dto.discount_reason,
                    remarks = dto.remarks
                )
            }

            try {
                institutionDao.deleteFeePaymentsForStudents(studentIds)
                institutionDao.insertFeePayments(mapped.map { LocalFeePaymentEntity.fromDomain(it) })
            } catch (ex: Exception) {
                Log.e("VidyaSetu_Auth", "Error writing fee payments to Room DB", ex)
            }

            Result.success(mapped)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error getting fee payments from remote", e)
            val local = try {
                institutionDao.getFeePaymentsForStudents(studentIds).map { it.toDomain() }
            } catch (ex: Exception) {
                emptyList()
            }
            if (local.isNotEmpty()) {
                Result.success(local)
            } else {
                Result.failure(e)
            }
        }
    }

    @Serializable
    private data class AdminFeeStatsDto(
        val total_collected: Double = 0.0,
        val total_expected: Double = 0.0,
        val total_pending: Double = 0.0
    )

    override suspend fun getAdminFeeStats(parentOrgId: String, forceRefresh: Boolean): Result<Pair<Double, Double>> = withContext(Dispatchers.IO) {
        val cacheKey = "admin_fee_stats_$parentOrgId"
        cacheAndReturn(cacheKey, forceRefresh) {
            SupabaseClient.client.postgrest.rpc(
                "get_admin_fee_stats",
                buildJsonObject {
                    put("p_parent_org_id", parentOrgId)
                }
            ).decodeAs<AdminFeeStatsDto>()
        }.map { stats ->
            Pair(stats.total_collected, stats.total_pending)
        }
    }

    override suspend fun updateBusLiveLocation(
        busId: String,
        parentOrgId: String,
        latitude: Double,
        longitude: Double,
        speed: Double,
        sessionId: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updateDto = BusLiveLocationUpdateDto(
                bus_id = busId,
                parent_organization_id = parentOrgId,
                latitude = latitude,
                longitude = longitude,
                speed = speed,
                updated_at = nowAsString(),
                active_session_id = sessionId
            )

            // Upsert (Insert on conflict update)
            SupabaseClient.client.from("organization_parent_bus_live_locations").upsert(updateDto)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error updating bus live location", e)
            Result.failure(e)
        }
    }

    override suspend fun setActiveWorkspace(workspaceId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            institutionDao.clearActiveWorkspaces()
            institutionDao.setActiveWorkspace(workspaceId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error setting active workspace locally", e)
            Result.failure(e)
        }
    }

    override suspend fun getGuardianStudents(guardianLinkId: String, forceRefresh: Boolean): Result<List<InstitutionStudent>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val local = try {
                institutionDao.getGuardianStudents(guardianLinkId).map { it.toDomain() }
            } catch (e: Exception) {
                emptyList()
            }
            if (local.isNotEmpty()) {
                return@withContext Result.success(local)
            }
        }

        try {
            Log.d("VidyaSetu_Auth", "Fetching students for guardianLinkId: $guardianLinkId")

            // 1. Fetch Guardian Link
            val link = SupabaseClient.client.from("organization_guardian_user_links")
                .select(columns = Columns.raw("guardian_id, organization_id, active_session_id")) {
                    filter {
                        eq("id", guardianLinkId)
                    }
                }.decodeSingleOrNull<GuardianLinkDetailsDto>()

            if (link == null || link.guardian_id == null || link.organization_id == null) {
                Log.d("VidyaSetu_Auth", "Guardian link not found or missing fields")
                return@withContext Result.success(emptyList())
            }

            // 2. Fetch Students
            val students = SupabaseClient.client.from("organization_students")
                .select(columns = Columns.raw("id, name, active_session_id")) {
                    filter {
                        eq("guardian_id", link.guardian_id)
                        eq("organization_id", link.organization_id)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<StudentDetailsDto>()

            if (students.isEmpty()) {
                Log.d("VidyaSetu_Auth", "No students linked to guardian_id ${link.guardian_id}")
                return@withContext Result.success(emptyList())
            }

            val studentIds = students.mapNotNull { it.id }
            if (studentIds.isEmpty()) {
                return@withContext Result.success(emptyList())
            }

            // 3. Fetch Enrollments
            val enrollments = SupabaseClient.client.from("organization_student_enrollments")
                .select(columns = Columns.raw("student_id, class_id")) {
                    filter {
                        isIn("student_id", studentIds)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<EnrollmentDetailsDto>()

            // 4. Fetch Organization Classes & Global Classes for Name Mapping
            val orgClasses = SupabaseClient.client.from("organization_classes")
                .select(columns = Columns.raw("id, class_id, custom_class_name")) {
                    filter {
                        eq("organization_id", link.organization_id)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<OrgClassDetailsDto>()

            val globalClasses = SupabaseClient.client.from("global_classes")
                .select(columns = Columns.raw("id, name")) {
                    filter {
                        eq("is_active", true)
                    }
                }.decodeList<GlobalClassDetailsDto>()

            val classNamesMap = orgClasses.associate { orgClass ->
                val name = orgClass.custom_class_name?.takeIf { it.isNotBlank() }
                    ?: globalClasses.find { it.id == orgClass.class_id }?.name
                    ?: "Class"
                orgClass.id to name
            }

            // 5. Fetch Fee Assignments (Base class fees)
            val feeAssignments = SupabaseClient.client.from("organization_fee_assignments")
                .select(columns = Columns.raw("organization_class_id, amount")) {
                    filter {
                        eq("organization_id", link.organization_id)
                        eq("session_id", link.active_session_id ?: "")
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<FeeAssignmentDto>()

            val classFeeMap = feeAssignments.groupBy { it.organization_class_id }
                .mapValues { entry -> entry.value.sumOf { it.amount ?: 0.0 } }

            // 6. Fetch Student Additional Fees
            val additionalFees = SupabaseClient.client.from("organization_student_additional_fees")
                .select(columns = Columns.raw("student_id, amount")) {
                    filter {
                        isIn("student_id", studentIds)
                        eq("organization_id", link.organization_id)
                        eq("active_session_id", link.active_session_id ?: "")
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<StudentAdditionalFeeDto>()

            val additionalFeeMap = additionalFees.groupBy { it.student_id }
                .mapValues { entry -> entry.value.sumOf { it.amount ?: 0.0 } }

            // 7. Fetch Student Fee Payments (Paid)
            val payments = SupabaseClient.client.from("organization_student_fee_payments")
                .select(columns = Columns.raw("student_id, amount_paid")) {
                    filter {
                        isIn("student_id", studentIds)
                        eq("organization_id", link.organization_id)
                        eq("active_session_id", link.active_session_id ?: "")
                        eq("status", "Completed")
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<StudentFeePaymentDto>()

            val paidFeeMap = payments.groupBy { it.student_id }
                .mapValues { entry -> entry.value.sumOf { it.amount_paid ?: 0.0 } }

            // 8. Map to Domain model
            val list = students.mapNotNull { student ->
                val sId = student.id ?: return@mapNotNull null
                val enrollment = enrollments.find { it.student_id == sId }
                val classId = enrollment?.class_id
                val className = classId?.let { classNamesMap[it] }

                val baseFee = classId?.let { classFeeMap[it] } ?: 0.0
                val addFee = additionalFeeMap[sId] ?: 0.0
                val totalFee = baseFee + addFee
                val paidFee = paidFeeMap[sId] ?: 0.0
                val pendingFee = totalFee - paidFee

                InstitutionStudent(
                    id = sId,
                    name = student.name ?: "Student",
                    classId = classId,
                    className = className,
                    totalFee = totalFee,
                    paidFee = paidFee,
                    pendingFee = pendingFee
                )
            }

            try {
                institutionDao.deleteGuardianStudents(guardianLinkId)
                institutionDao.insertGuardianStudents(list.map { LocalGuardianStudentEntity.fromDomain(it, guardianLinkId) })
            } catch (e: Exception) {
                Log.e("OfflineCache", "Error caching guardian students", e)
            }

            Result.success(list)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error fetching guardian students & fees", e)
            Result.failure(e)
        }
    }

    private fun nowAsString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        return sdf.format(java.util.Date())
    }

    override suspend fun getStudentAttendance(studentIds: List<String>, forceRefresh: Boolean): Result<List<StudentAttendance>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val local = try {
                institutionDao.getStudentAttendanceForStudents(studentIds).map { it.toDomain() }
            } catch (e: Exception) {
                emptyList()
            }
            if (local.isNotEmpty()) {
                return@withContext Result.success(local)
            }
        }

        try {
            if (studentIds.isEmpty()) return@withContext Result.success(emptyList())
            val dtoList = SupabaseClient.client.from("organization_student_attendance")
                .select(columns = Columns.raw("id, student_id, attendance_date, status, remarks")) {
                    filter {
                        isIn("student_id", studentIds)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<StudentAttendanceDto>()

            val domainList = dtoList.map { dto ->
                StudentAttendance(
                    id = dto.id,
                    studentId = dto.student_id,
                    attendanceDate = dto.attendance_date,
                    status = dto.status,
                    remarks = dto.remarks
                )
            }

            try {
                institutionDao.deleteStudentAttendanceForStudents(studentIds)
                institutionDao.insertStudentAttendance(domainList.map { LocalStudentAttendanceEntity.fromDomain(it) })
            } catch (e: Exception) {
                Log.e("OfflineCache", "Error caching student attendance", e)
            }

            Result.success(domainList)
        } catch (e: Exception) {
            Log.e("OfflineCache", "Error fetching student attendance", e)
            Result.failure(e)
        }
    }

    override suspend fun getStudentBusAssignments(studentIds: List<String>, forceRefresh: Boolean): Result<List<StudentBusAssignment>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val local = try {
                institutionDao.getStudentBusAssignmentsForStudents(studentIds).map { it.toDomain() }
            } catch (e: Exception) {
                emptyList()
            }
            if (local.isNotEmpty()) {
                return@withContext Result.success(local)
            }
        }

        try {
            if (studentIds.isEmpty()) return@withContext Result.success(emptyList())
            val assignments = SupabaseClient.client.from("organization_student_bus_assignments")
                .select(columns = Columns.raw("id, student_id, bus_id, pickup_stop")) {
                    filter {
                        isIn("student_id", studentIds)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<StudentBusAssignmentDto>()
            
            val domainList = if (assignments.isEmpty()) emptyList()
            else {
                val busIds = assignments.map { it.bus_id }.distinct()
                val buses = SupabaseClient.client.from("organization_parent_buses")
                    .select(columns = Columns.raw("id, bus_number, bus_name, route_name")) {
                        filter {
                            isIn("id", busIds)
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeList<BusDetailsDto>()

                val students = SupabaseClient.client.from("organization_students")
                    .select(columns = Columns.raw("id, name")) {
                        filter {
                            isIn("id", studentIds)
                        }
                    }.decodeList<StudentDetailsDto>()
                
                assignments.mapNotNull { assign ->
                    val bus = buses.find { it.id == assign.bus_id } ?: return@mapNotNull null
                    val studentName = students.find { it.id == assign.student_id }?.name ?: "Student"
                    StudentBusAssignment(
                        studentId = assign.student_id,
                        studentName = studentName,
                        busId = assign.bus_id,
                        busNumber = bus.bus_number,
                        busName = bus.bus_name,
                        routeName = bus.route_name,
                        pickupStop = assign.pickup_stop
                    )
                }
            }

            try {
                institutionDao.deleteStudentBusAssignmentsForStudents(studentIds)
                institutionDao.insertStudentBusAssignments(domainList.map { LocalStudentBusAssignmentEntity.fromDomain(it) })
            } catch (e: Exception) {
                Log.e("OfflineCache", "Error caching student bus assignments", e)
            }

            Result.success(domainList)
        } catch (e: Exception) {
            Log.e("OfflineCache", "Error fetching student bus assignments", e)
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getBusLiveLocation(busId: String): Result<BusLiveLocation?> = withContext(Dispatchers.IO) {
        try {
            val dto = SupabaseClient.client.from("organization_parent_bus_live_locations")
                .select(columns = Columns.raw("bus_id, latitude, longitude, speed, updated_at")) {
                    filter {
                        eq("bus_id", busId)
                    }
                }.decodeSingleOrNull<BusLiveLocationDto>()
            
            if (dto == null) return@withContext Result.success(null)
            
            val isLive = try {
                val cleanStr = dto.updated_at.replace(" ", "T")
                val updatedTime = try {
                    java.time.Instant.parse(cleanStr)
                } catch (e: Exception) {
                    try {
                        java.time.OffsetDateTime.parse(cleanStr).toInstant()
                    } catch (e2: Exception) {
                        java.time.LocalDateTime.parse(cleanStr.substringBefore("+").substringBefore("Z")).toInstant(java.time.ZoneOffset.UTC)
                    }
                }
                val diff = java.time.Duration.between(updatedTime, java.time.Instant.now()).toMinutes()
                diff < 5
            } catch (e: Exception) {
                Log.e("VidyaSetu_Auth", "Error parsing updated_at: ${dto.updated_at}", e)
                false
            }

            Result.success(
                BusLiveLocation(
                    busId = dto.bus_id,
                    latitude = dto.latitude,
                    longitude = dto.longitude,
                    speed = dto.speed,
                    updatedAt = dto.updated_at,
                    isLive = isLive
                )
            )
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error getting bus live location", e)
            Result.failure(e)
        }
    }

    override suspend fun getDriverBusDetails(workspaceId: String, forceRefresh: Boolean): Result<DriverBusDetails?> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val local = try {
                institutionDao.getDriverBusDetails(workspaceId)?.toDomain()
            } catch (e: Exception) {
                null
            }
            if (local != null) {
                return@withContext Result.success(local)
            }
        }

        try {
            // 1. Fetch staff link details
            val link = SupabaseClient.client.from("organization_parent_staff_user_links")
                .select(columns = Columns.raw("staff_id, parent_organization_id, active_session_id")) {
                    filter { eq("id", workspaceId) }
                }.decodeSingleOrNull<StaffLinkDetailsDto>()
                
            if (link == null) return@withContext Result.success(null)
            
            // 2. Fetch staff record to get bus_id
            val staff = SupabaseClient.client.from("organization_parent_staff")
                .select(columns = Columns.raw("bus_id")) {
                    filter { eq("id", link.staff_id) }
                }.decodeSingleOrNull<StaffBusLinkDto>()
                
            val details = if (staff == null || staff.bus_id == null) {
                DriverBusDetails(
                    busId = "",
                    busNumber = "",
                    routeName = null,
                    staffId = link.staff_id,
                    parentOrgId = link.parent_organization_id,
                    activeSessionId = link.active_session_id
                )
            } else {
                // 3. Fetch bus details
                val bus = SupabaseClient.client.from("organization_parent_buses")
                    .select(columns = Columns.raw("id, bus_number, route_name")) {
                        filter { eq("id", staff.bus_id) }
                    }.decodeSingleOrNull<BusDetailsDto>()
                    
                if (bus == null) null
                else {
                    DriverBusDetails(
                        busId = bus.id,
                        busNumber = bus.bus_number,
                        routeName = bus.route_name,
                        staffId = link.staff_id,
                        parentOrgId = link.parent_organization_id,
                        activeSessionId = link.active_session_id
                    )
                }
            }

            if (details != null) {
                try {
                    institutionDao.insertDriverBusDetails(LocalDriverBusDetailsEntity.fromDomain(details, workspaceId))
                } catch (e: Exception) {
                    Log.e("OfflineCache", "Error caching driver bus details", e)
                }
            }

            Result.success(details)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error getting driver bus details", e)
            Result.failure(e)
        }
    }

    override suspend fun getOrganizations(parentOrgId: String, forceRefresh: Boolean): Result<List<ChildOrg>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val local = try {
                institutionDao.getChildOrgs(parentOrgId).map { it.toDomain() }
            } catch (e: Exception) {
                emptyList()
            }
            if (local.isNotEmpty()) {
                return@withContext Result.success(local)
            }
        }

        try {
            val dtoList = SupabaseClient.client.from("organizations")
                .select(columns = Columns.raw("id, name")) {
                    filter {
                        eq("parent_organization_id", parentOrgId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<ChildOrgDto>()

            val domainList = dtoList.map { ChildOrg(it.id, it.name) }

            try {
                institutionDao.insertChildOrgs(domainList.map { LocalChildOrgEntity(it.id, parentOrgId, it.name) })
            } catch (e: Exception) {
                Log.e("OfflineCache", "Error caching child orgs", e)
            }

            Result.success(domainList)
        } catch (e: Exception) {
            Log.e("OfflineCache", "Error fetching organizations", e)
            Result.failure(e)
        }
    }

    override suspend fun getClasses(orgId: String, forceRefresh: Boolean): Result<List<OrgClass>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val local = try {
                institutionDao.getOrgClasses(orgId).map { it.toDomain() }
            } catch (e: Exception) {
                emptyList()
            }
            if (local.isNotEmpty()) {
                return@withContext Result.success(local)
            }
        }

        try {
            val dtoList = SupabaseClient.client.from("organization_classes")
                .select(columns = Columns.raw("id, global_classes(name)")) {
                    filter {
                        eq("organization_id", orgId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<OrgClassJoinDto>()

            val domainList = dtoList.map { OrgClass(it.id, it.global_classes?.name ?: "Class") }

            try {
                institutionDao.insertOrgClasses(domainList.map { LocalOrgClassEntity(it.id, orgId, it.name) })
            } catch (e: Exception) {
                Log.e("OfflineCache", "Error caching org classes", e)
            }

            Result.success(domainList)
        } catch (e: Exception) {
            Log.e("OfflineCache", "Error fetching classes", e)
            Result.failure(e)
        }
    }

    override suspend fun getSections(classId: String, forceRefresh: Boolean): Result<List<OrgSection>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val local = try {
                institutionDao.getOrgSections(classId).map { it.toDomain() }
            } catch (e: Exception) {
                emptyList()
            }
            if (local.isNotEmpty()) {
                return@withContext Result.success(local)
            }
        }

        try {
            val dtoList = SupabaseClient.client.from("organization_sections")
                .select(columns = Columns.raw("id, name")) {
                    filter {
                        eq("organization_class_id", classId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<OrgSectionDto>()

            val domainList = dtoList.map { OrgSection(it.id, it.name) }

            try {
                institutionDao.insertOrgSections(domainList.map { LocalOrgSectionEntity(it.id, classId, it.name) })
            } catch (e: Exception) {
                Log.e("OfflineCache", "Error caching org sections", e)
            }

            Result.success(domainList)
        } catch (e: Exception) {
            Log.e("OfflineCache", "Error fetching sections", e)
            Result.failure(e)
        }
    }

    override suspend fun getStudentsForAttendance(
        orgId: String,
        classId: String,
        sectionId: String,
        date: String
    ): Result<List<StudentAttendanceInfo>> = withContext(Dispatchers.IO) {
        try {
            // 1. Fetch enrolled students
            val enrollments = SupabaseClient.client.from("organization_student_enrollments")
                .select(columns = Columns.raw("student_id, organization_students(name, sr_number)")) {
                    filter {
                        eq("organization_id", orgId)
                        eq("class_id", classId)
                        eq("section_id", sectionId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<StudentEnrollmentJoinDto>()

            val studentIds = enrollments.map { it.student_id }
            if (studentIds.isEmpty()) {
                return@withContext Result.success(emptyList())
            }

            // 2. Fetch leaves for these students covering the selected date
            val leaves = SupabaseClient.client.from("organization_leaves")
                .select(columns = Columns.raw("student_id, status")) {
                    filter {
                        eq("organization_id", orgId)
                        eq("applicant_type", "student")
                        eq("is_deleted", false)
                        eq("is_active", true)
                        lte("start_date", date)
                        gte("end_date", date)
                    }
                }.decodeList<AttendanceStudentLeaveDto>()

            // 3. Fetch existing marked attendance records for these students on this date
            val existingAttendance = try {
                SupabaseClient.client.from("organization_student_attendance")
                    .select(columns = Columns.raw("student_id, status")) {
                        filter {
                            eq("organization_id", orgId)
                            eq("attendance_date", date)
                            isIn("student_id", studentIds)
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeList<ExistingStudentAttendanceDto>()
            } catch (e: Exception) {
                Log.e("VidyaSetu_Auth", "Error checking existing attendance", e)
                emptyList<ExistingStudentAttendanceDto>()
            }

            // 4. Map to domain model
            val list = enrollments.map { e ->
                val studId = e.student_id
                val name = e.organization_students?.name ?: "Unknown"
                val srNumber = e.organization_students?.sr_number ?: ""
                
                val studLeaves = leaves.filter { it.student_id == studId }
                val approved = studLeaves.any { it.status == "Approved" }
                val pending = studLeaves.any { it.status == "Pending" }
                
                val existingRecord = existingAttendance.find { it.student_id == studId }
                val defaultStatus = existingRecord?.status ?: if (approved) "On Leave" else "Present"
                
                StudentAttendanceInfo(
                    studentId = studId,
                    name = name,
                    srNumber = srNumber,
                    status = defaultStatus,
                    isLeaveApproved = approved,
                    isLeavePending = pending
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error getting students for attendance", e)
            Result.failure(e)
        }
    }

    override suspend fun submitStudentAttendance(
        orgId: String,
        date: String,
        attendanceList: List<StudentAttendanceInfo>,
        staffUserId: String,
        parentOrgId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Resolve staff link for this teacher
            val staffLink = SupabaseClient.client.from("organization_parent_staff_user_links")
                .select(columns = Columns.raw("id, parent_organization_id, staff_id")) {
                    filter {
                        eq("user_id", staffUserId)
                        eq("parent_organization_id", parentOrgId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<StaffLinkDto>().firstOrNull()
            val staffId = staffLink?.staff_id

            // 2. Resolve active session ID from organization_parent_staff
            var sessionId: String? = null
            if (staffId != null) {
                val staff = SupabaseClient.client.from("organization_parent_staff")
                    .select(columns = Columns.raw("active_session_id")) {
                        filter {
                            eq("id", staffId)
                        }
                    }.decodeSingleOrNull<StaffDetailsForLeaveDto>()
                sessionId = staff?.active_session_id
            }

            val resolvedSessionId = sessionId ?: run {
                val activeSession = SupabaseClient.client.from("global_sessions")
                    .select(columns = Columns.raw("id")) {
                        filter { eq("is_active", true) }
                    }.decodeList<GlobalSessionDto>().firstOrNull()
                activeSession?.id ?: "fa000000-0000-0000-0000-000000000001"
            }

            val studentIds = attendanceList.map { it.studentId }

            // 3. Delete existing attendance records for these students on this date to prevent duplicates
            SupabaseClient.client.from("organization_student_attendance")
                .delete {
                    filter {
                        isIn("student_id", studentIds)
                        eq("attendance_date", date)
                    }
                }

            // 4. Batch insert new records
            val insertList = attendanceList.map { item ->
                AttendanceInsertDto(
                    organization_id = orgId,
                    active_session_id = resolvedSessionId,
                    student_id = item.studentId,
                    attendance_date = date,
                    status = item.status,
                    marked_by_staff_id = staffId,
                    is_active = true,
                    is_deleted = false
                )
            }

            SupabaseClient.client.from("organization_student_attendance").insert(insertList)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error submitting student attendance", e)
            Result.failure(e)
        }
    }

    override suspend fun getClassTeacherAssignment(
        userId: String,
        parentOrgId: String
    ): Result<AssignedSection?> = withContext(Dispatchers.IO) {
        try {
            // 1. Resolve organization_users link(s) for the teacher's user ID
            val orgUsers = SupabaseClient.client.from("organization_users")
                .select(columns = Columns.raw("id, organization_id")) {
                    filter {
                        eq("user_id", userId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<OrgUserLinkDto>()

            if (orgUsers.isEmpty()) {
                return@withContext Result.success(null)
            }

            val orgUserIds = orgUsers.map { it.id }

            // 2. Query organization_session_sections to see if teacher is assigned to any section
            val section = SupabaseClient.client.from("organization_session_sections")
                .select(columns = Columns.raw("organization_section_id, organization_session_class_id")) {
                    filter {
                        isIn("class_teacher_id", orgUserIds)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<ClassTeacherSectDto>().firstOrNull()

            if (section == null) {
                return@withContext Result.success(null)
            }

            // 3. Resolve parent class and child org ID
            val sessionClass = SupabaseClient.client.from("organization_session_classes")
                .select(columns = Columns.raw("organization_id, organization_class_id")) {
                    filter {
                        eq("id", section.organization_session_class_id)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeSingleOrNull<ClassTeacherClassDto>()

            if (sessionClass == null) {
                return@withContext Result.success(null)
            }

            Result.success(
                AssignedSection(
                    orgId = sessionClass.organization_id,
                    classId = sessionClass.organization_class_id,
                    sectionId = section.organization_section_id
                )
            )
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error getting class teacher assignment", e)
            Result.failure(e)
        }
    }

    override suspend fun getStaffSalaryDetails(
        userId: String,
        parentOrgId: String,
        forceRefresh: Boolean
    ): Result<StaffSalaryDetails> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val localDetails = try {
                institutionDao.getStaffSalaryDetails(userId, parentOrgId)
            } catch (e: Exception) {
                null
            }
            val localPayments = try {
                institutionDao.getStaffSalaryPayments(userId, parentOrgId)
            } catch (e: Exception) {
                emptyList()
            }

            if (localDetails != null) {
                val domainPayments = localPayments.map { it.toDomain() }.sortedByDescending { it.paymentDate }
                return@withContext Result.success(
                    StaffSalaryDetails(
                        monthlySalary = localDetails.monthlySalary,
                        totalPaid = localDetails.totalPaid,
                        payments = domainPayments
                    )
                )
            }
        }

        try {
            // 1. Resolve staffId from links
            val staffLink = SupabaseClient.client.from("organization_parent_staff_user_links")
                .select(columns = Columns.raw("id, parent_organization_id, staff_id")) {
                    filter {
                        eq("user_id", userId)
                        eq("parent_organization_id", parentOrgId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<StaffLinkDto>().firstOrNull()
            val staffId = staffLink?.staff_id
                ?: throw Exception("Staff assignment not found")

            // 2. Fetch monthly salary
            val salary = try {
                SupabaseClient.client.from("organization_parent_staff_salaries")
                    .select(columns = Columns.raw("monthly_salary")) {
                        filter {
                            eq("staff_id", staffId)
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeList<StaffSalaryDto>().firstOrNull()?.monthly_salary ?: 0.0
            } catch (e: Exception) {
                Log.e("VidyaSetu_Auth", "Error fetching staff salary", e)
                0.0
            }

            // 3. Fetch salary payments
            val payments = try {
                SupabaseClient.client.from("organization_parent_staff_salary_payments")
                    .select(columns = Columns.raw("id, payment_date, amount_paid, payment_mode, online_transaction_id, online_payment_app, remarks")) {
                        filter {
                            eq("staff_id", staffId)
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeList<StaffSalaryPaymentDto>()
            } catch (e: Exception) {
                Log.e("VidyaSetu_Auth", "Error fetching staff salary payments", e)
                emptyList<StaffSalaryPaymentDto>()
            }

            val domainPayments = payments.map {
                StaffSalaryPayment(
                    id = it.id,
                    paymentDate = it.payment_date,
                    amountPaid = it.amount_paid,
                    paymentMode = it.payment_mode,
                    transactionId = it.online_transaction_id,
                    paymentApp = it.online_payment_app,
                    remarks = it.remarks
                )
            }.sortedByDescending { it.paymentDate }

            val totalPaid = domainPayments.sumOf { it.amountPaid }

            try {
                institutionDao.insertStaffSalaryDetails(
                    LocalStaffSalaryDetailsEntity(userId, parentOrgId, salary, totalPaid)
                )
                institutionDao.deleteStaffSalaryPayments(userId, parentOrgId)
                institutionDao.insertStaffSalaryPayments(
                    domainPayments.map { LocalStaffSalaryPaymentEntity.fromDomain(it, userId, parentOrgId) }
                )
            } catch (e: Exception) {
                Log.e("OfflineCache", "Error caching staff salary details", e)
            }

            Result.success(
                StaffSalaryDetails(
                    monthlySalary = salary,
                    totalPaid = totalPaid,
                    payments = domainPayments
                )
            )
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error getting staff salary details", e)
            Result.failure(e)
        }
    }

    override suspend fun getActiveSessionDetails(forceRefresh: Boolean): Result<Pair<String, String>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val local = try {
                institutionDao.getActiveSession()
            } catch (e: Exception) {
                null
            }
            if (local != null) {
                return@withContext Result.success(Pair(local.id, local.name))
            }
        }

        try {
            val activeSession = SupabaseClient.client.from("global_sessions")
                .select(columns = Columns.raw("id, name")) {
                    filter {
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<GlobalSessionDto>().firstOrNull()

            val sessionId = activeSession?.id ?: "fa000000-0000-0000-0000-000000000001"
            val sessionName = activeSession?.name ?: "2026-2027"

            try {
                institutionDao.deleteActiveSession()
                institutionDao.insertActiveSession(LocalActiveSessionEntity(sessionId, sessionName))
            } catch (e: Exception) {
                Log.e("OfflineCache", "Error caching active session details", e)
            }

            Result.success(Pair(sessionId, sessionName))
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error getting active session details", e)
            Result.failure(e)
        }
    }

    override suspend fun checkIfAttendanceMarked(
        orgId: String,
        classId: String,
        sectionId: String,
        date: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Find student IDs in this class/section
            val enrollments = SupabaseClient.client.from("organization_student_enrollments")
                .select(columns = Columns.raw("student_id")) {
                    filter {
                        eq("organization_id", orgId)
                        eq("class_id", classId)
                        eq("section_id", sectionId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<EnrollmentDetailsDto>()

            val studentIds = enrollments.mapNotNull { it.student_id }
            if (studentIds.isEmpty()) {
                return@withContext Result.success(false)
            }

            val count = SupabaseClient.client.from("organization_student_attendance")
                .select(columns = Columns.raw("id")) {
                    filter {
                        eq("organization_id", orgId)
                        eq("attendance_date", date)
                        isIn("student_id", studentIds)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<ExistingAttendanceCountDto>().size

            Result.success(count > 0)
        } catch (e: Exception) {
            Log.e("VidyaSetu_Auth", "Error checking if attendance marked", e)
            Result.success(false)
        }
    }

    override suspend fun getContentFeed(
        workspace: Workspace,
        sessionId: String,
        forceRefresh: Boolean
    ): Result<List<ContentFeedItem>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val local = try {
                institutionDao.getContentFeedItems(workspace.id, sessionId).map { it.toDomain() }
            } catch (e: Exception) {
                emptyList()
            }
            if (local.isNotEmpty()) {
                return@withContext Result.success(local.sortedByDescending { it.createdAt })
            }
        }

        try {
            val childOrgId = workspace.childOrgId
            val parentOrgId = workspace.parentOrgId
            val userRole = workspace.role.lowercase()

            // 1. Fetch assigned content IDs for this child organization (if childOrgId is present)
            val assignedContentIds = if (!childOrgId.isNullOrEmpty()) {
                try {
                    SupabaseClient.client.from("organization_content_assignments")
                        .select(columns = Columns.raw("content_id")) {
                            filter {
                                eq("target_organization_id", childOrgId)
                            }
                        }.decodeList<ContentAssignmentDto>().map { it.content_id }
                } catch (e: Exception) {
                    Log.e("VidyaSetu_Auth", "Error fetching content assignments", e)
                    emptyList()
                }
            } else {
                emptyList()
            }

            // 2. Fetch contents
            val rawContents = SupabaseClient.client.from("organization_contents")
                .select(columns = Columns.raw("id, content_type, publisher_type, parent_organization_id, organization_id, session_id, title, description, image_url, target_scope, target_roles, status, is_active, is_deleted, created_at, organizations(name), organization_parents(name)")) {
                    filter {
                        eq("session_id", sessionId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                        eq("status", "published")
                    }
                }.decodeList<ContentFeedItemDto>()

            // 3. Filter in memory by publisher targeting
            val filteredContents = rawContents.filter { item ->
                val isPublisherMatch = when {
                    // Published directly by child org
                    !childOrgId.isNullOrEmpty() && item.organization_id == childOrgId -> true
                    
                    // Published by parent org and targeting all children
                    item.parent_organization_id == parentOrgId && item.target_scope == "all_children" -> true
                    
                    // Published by parent org and explicitly assigned to this child org
                    item.parent_organization_id == parentOrgId && item.target_scope == "selected_children" && assignedContentIds.contains(item.id) -> true
                    
                    // If no childOrgId (e.g. parent level workspace) and parent matches
                    childOrgId.isNullOrEmpty() && item.parent_organization_id == parentOrgId -> true
                    
                    else -> false
                }
                
                if (!isPublisherMatch) return@filter false

                // 4. Role checking
                val roleMatch = when {
                    userRole == "admin" -> true
                    item.target_roles.contains("all") -> true
                    userRole == "student" && item.target_roles.contains("student") -> true
                    userRole == "guardian" && item.target_roles.contains("parents") -> true
                    userRole == "teacher" && item.target_roles.contains("teacher") -> true
                    else -> false
                }
                
                if (!roleMatch) return@filter false

                // 5. Date checking for notices (notice items older than today's date are hidden)
                if (item.content_type == "notice") {
                    try {
                        val dateStr = item.created_at.substringBefore("T").substringBefore(" ")
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                        val itemDate = sdf.parse(dateStr)
                        
                        val cal = java.util.Calendar.getInstance()
                        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                        cal.set(java.util.Calendar.MINUTE, 0)
                        cal.set(java.util.Calendar.SECOND, 0)
                        cal.set(java.util.Calendar.MILLISECOND, 0)
                        val today = cal.time
                        
                        itemDate != null && !itemDate.before(today)
                    } catch (e: Exception) {
                        Log.e("VidyaSetu_Auth", "Error parsing notice date: ${item.created_at}", e)
                        true
                    }
                } else {
                    true
                }
            }

            val domainList = filteredContents.map { item ->
                val publisherName = when (item.publisher_type) {
                    "parent" -> item.organization_parents?.name ?: "Parent Organization"
                    else -> item.organizations?.name ?: "School Branch"
                }
                
                ContentFeedItem(
                    id = item.id,
                    contentType = item.content_type,
                    publisherType = item.publisher_type,
                    publisherName = publisherName,
                    title = item.title,
                    description = item.description,
                    imageUrl = item.image_url,
                    createdAt = item.created_at
                )
            }.sortedByDescending { it.createdAt }

            try {
                institutionDao.deleteContentFeedItems(workspace.id, sessionId)
                institutionDao.insertContentFeedItems(domainList.map { LocalContentFeedItemEntity.fromDomain(it, workspace.id, sessionId) })
            } catch (e: Exception) {
                Log.e("OfflineCache", "Error caching content feed", e)
            }

            Result.success(domainList)
        } catch (e: Exception) {
            Log.e("OfflineCache", "Error fetching content feed", e)
            Result.failure(e)
        }
    }

    override suspend fun syncWorkspaceData(userId: String, workspace: Workspace, sessionId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("OfflineSync", "Starting sync for workspace: ${workspace.id}, role: ${workspace.role}")
            
            val isAdmin = workspace.role in listOf(
                "Admin", "System Administrator", "School Administrator", "Org Admin", 
                "Principal", "Director", "Owner", "Accountant", "Librarian", 
                "Office Clerk", "Clerk", "Security Guard", "Security", "Teacher"
            )
            var isTeacherClassTeacher = false
            
            val studentIds = when {
                workspace.role == "Guardian" -> {
                    getGuardianStudents(workspace.id, forceRefresh = true).getOrNull()?.map { it.id } ?: emptyList()
                }
                workspace.role == "Student" -> {
                    val studentLink = SupabaseClient.client.from("organization_student_user_links")
                        .select(columns = Columns.raw("student_id")) {
                            filter {
                                eq("user_id", userId)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<StudentLinkDto>().firstOrNull()
                    listOfNotNull(studentLink?.student_id)
                }
                workspace.role == "Teacher" -> {
                    // Resolve teacher staff assignment
                    val staffLink = SupabaseClient.client.from("organization_parent_staff_user_links")
                        .select(columns = Columns.raw("id, parent_organization_id, staff_id")) {
                            filter {
                                eq("user_id", userId)
                                eq("parent_organization_id", workspace.parentOrgId)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<StaffLinkDto>().firstOrNull()
                    
                    val staffId = staffLink?.staff_id
                    if (staffId != null) {
                        // 1. Resolve organization_users link(s) for the teacher's user ID to get their orgUserIds
                        val orgUsers = SupabaseClient.client.from("organization_users")
                            .select(columns = Columns.raw("id")) {
                                filter {
                                    eq("user_id", userId)
                                    eq("is_active", true)
                                    eq("is_deleted", false)
                                }
                            }.decodeList<OrgUserLinkDto>()
                        val orgUserIds = orgUsers.map { it.id }

                        // 2. Query organization_session_sections to see if teacher is assigned to any section
                        val teacherSections = if (orgUserIds.isNotEmpty()) {
                            SupabaseClient.client.from("organization_session_sections")
                                .select(columns = Columns.raw("organization_section_id")) {
                                    filter {
                                        isIn("class_teacher_id", orgUserIds)
                                        eq("is_active", true)
                                        eq("is_deleted", false)
                                    }
                                }.decodeList<kotlinx.serialization.json.JsonObject>()
                        } else emptyList()

                        val taughtSectionIds = teacherSections.mapNotNull { it["organization_section_id"]?.toString()?.replace("\"", "") }
                        if (taughtSectionIds.isNotEmpty()) {
                            val enrollments = SupabaseClient.client.from("organization_student_enrollments")
                                .select(columns = Columns.raw("student_id")) {
                                    filter {
                                        isIn("section_id", taughtSectionIds)
                                        eq("is_active", true)
                                        eq("is_deleted", false)
                                    }
                                }.decodeList<EnrollmentDetailsDto>()
                            val list = enrollments.mapNotNull { it.student_id }
                            if (list.isNotEmpty()) {
                                isTeacherClassTeacher = true
                                list
                            } else emptyList()
                        } else emptyList()
                    } else emptyList()
                }
                else -> emptyList() // Admin uses orgIds scope
            }
            
            val isAdminForSync = if (workspace.role == "Teacher") {
                !isTeacherClassTeacher
            } else {
                isAdmin
            }
            
            val isDriver = workspace.role == "Driver"
            val orgIds = if (isAdminForSync || isDriver) {
                if (!workspace.childOrgId.isNullOrEmpty()) {
                    listOf(workspace.childOrgId)
                } else {
                    val childOrgs = SupabaseClient.client.from("organizations")
                        .select(columns = Columns.raw("id, name")) {
                            filter {
                                eq("parent_organization_id", workspace.parentOrgId)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<ChildOrgDto>()
                    childOrgs.map { it.id } + workspace.parentOrgId
                }
            } else emptyList()
            
            // Query students matching the scope
            val studentsList = when {
                isAdminForSync || isDriver -> {
                    SupabaseClient.client.from("organization_students")
                        .select(columns = Columns.raw("id, organization_id, name, sr_number, guardian_id, image_url")) {
                            filter {
                                isIn("organization_id", orgIds)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<kotlinx.serialization.json.JsonObject>()
                }
                studentIds.isNotEmpty() -> {
                    SupabaseClient.client.from("organization_students")
                        .select(columns = Columns.raw("id, organization_id, name, sr_number, guardian_id, image_url")) {
                            filter {
                                isIn("id", studentIds)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<kotlinx.serialization.json.JsonObject>()
                }
                else -> emptyList()
            }
            
            // Always clear previous workspace students before inserting new ones
            institutionDao.clearStudents()
            
            if (studentsList.isNotEmpty()) {
                val enrollments = SupabaseClient.client.from("organization_student_enrollments")
                    .select(columns = Columns.raw("student_id, class_id, section_id, roll_number")) {
                        filter {
                            if (isAdminForSync) {
                                isIn("organization_id", orgIds)
                            } else {
                                isIn("student_id", studentIds)
                            }
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeList<kotlinx.serialization.json.JsonObject>()
                    
                val orgClasses = SupabaseClient.client.from("organization_classes")
                    .select(columns = Columns.raw("id, class_id, custom_class_name")) {
                        filter {
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeList<kotlinx.serialization.json.JsonObject>()
                    
                val globalClasses = SupabaseClient.client.from("global_classes")
                    .select(columns = Columns.raw("id, name")) {
                        filter {
                            eq("is_active", true)
                        }
                    }.decodeList<kotlinx.serialization.json.JsonObject>()
                    
                val sections = SupabaseClient.client.from("organization_sections")
                    .select(columns = Columns.raw("id, name")) {
                        filter {
                            eq("is_active", true)
                            eq("is_deleted", false)
                        }
                    }.decodeList<kotlinx.serialization.json.JsonObject>()
                    
                val guardianIds = studentsList.mapNotNull { it["guardian_id"]?.toString()?.replace("\"", "") }.distinct()
                val guardians = if (guardianIds.isNotEmpty()) {
                    SupabaseClient.client.from("organization_guardians")
                        .select(columns = Columns.raw("id, name, mobile_number")) {
                            filter {
                                isIn("id", guardianIds)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<kotlinx.serialization.json.JsonObject>()
                } else emptyList()
                
                val localStudents = studentsList.map { studentJson ->
                    val id = studentJson["id"]?.toString()?.replace("\"", "") ?: ""
                    val organizationId = studentJson["organization_id"]?.toString()?.replace("\"", "") ?: ""
                    val name = studentJson["name"]?.toString()?.replace("\"", "") ?: ""
                    val srNumber = studentJson["sr_number"]?.toString()?.replace("\"", "")
                    val imageUrl = studentJson["image_url"]?.toString()?.replace("\"", "")

                    val enrollment = enrollments.find { it["student_id"]?.toString()?.replace("\"", "") == id }
                    val classId = enrollment?.get("class_id")?.toString()?.replace("\"", "")
                    val sectionId = enrollment?.get("section_id")?.toString()?.replace("\"", "")
                    val rollNumber = enrollment?.get("roll_number")?.toString()?.toIntOrNull()

                    val orgClass = orgClasses.find { it["id"]?.toString()?.replace("\"", "") == classId }
                    val customClassName = orgClass?.get("custom_class_name")?.toString()?.replace("\"", "")
                    val globalClassId = orgClass?.get("class_id")?.toString()?.replace("\"", "")
                    val globalClassName = globalClasses.find { it["id"]?.toString()?.replace("\"", "") == globalClassId }?.get("name")?.toString()?.replace("\"", "")
                    val className = customClassName ?: globalClassName

                    val sectionName = sections.find { it["id"]?.toString()?.replace("\"", "") == sectionId }?.get("name")?.toString()?.replace("\"", "")

                    val guardianId = studentJson["guardian_id"]?.toString()?.replace("\"", "")
                    val guardian = guardians.find { it["id"]?.toString()?.replace("\"", "") == guardianId }
                    val guardianName = guardian?.get("name")?.toString()?.replace("\"", "")
                    val guardianMobile = guardian?.get("mobile_number")?.toString()?.replace("\"", "")

                    LocalStudentEntity(
                        id = id,
                        organizationId = organizationId,
                        name = name,
                        srNumber = srNumber,
                        rollNumber = rollNumber,
                        className = className,
                        sectionName = sectionName,
                        guardianName = guardianName,
                        guardianMobile = guardianMobile,
                        imageUrl = imageUrl
                    )
                }
                
                institutionDao.insertStudents(localStudents)
                Log.d("OfflineSync", "Synced ${localStudents.size} students offline.")
            } else {
                Log.d("OfflineSync", "No students found for role: ${workspace.role}, cleared local cache.")
            }

            // 1b. Sync Student Image Vectors (for offline face recognition)
            // Admin aur Driver dono ke liye orgIds se vectors sync hote hain
            // Guardian/Student ke liye studentIds se sync hota hai
            try {
                val vectorOrgIds = if (isAdminForSync || isDriver) {
                    if (!workspace.childOrgId.isNullOrEmpty()) {
                        listOf(workspace.childOrgId, workspace.parentOrgId)
                    } else {
                        orgIds
                    }
                } else emptyList()

                val vectorsList = when {
                    isAdminForSync || isDriver -> {
                        // Admin aur Driver: apni org ke sabhi students ke vectors
                        Log.d("OfflineSync", "Fetching image vectors for orgIds: $vectorOrgIds (role: ${workspace.role})")
                        SupabaseClient.client.from("organization_student_image_vectors")
                            .select(columns = Columns.raw("id, organization_id, student_id, person_type, face_vector, image_url, is_active, is_deleted, created_at, updated_at")) {
                                filter {
                                    isIn("organization_id", vectorOrgIds)
                                    eq("is_active", true)
                                    eq("is_deleted", false)
                                }
                            }.decodeList<StudentImageVectorDto>()
                    }
                    studentIds.isNotEmpty() -> {
                        // Guardian/Student: sirf unke students ke vectors
                        SupabaseClient.client.from("organization_student_image_vectors")
                            .select(columns = Columns.raw("id, organization_id, student_id, person_type, face_vector, image_url, is_active, is_deleted, created_at, updated_at")) {
                                filter {
                                    isIn("student_id", studentIds)
                                    eq("is_active", true)
                                    eq("is_deleted", false)
                                }
                            }.decodeList<StudentImageVectorDto>()
                    }
                    else -> emptyList()
                }

                if (vectorsList.isNotEmpty()) {
                    val localVectors = vectorsList.map {
                        LocalStudentImageVectorEntity(
                            id = it.id,
                            organizationId = it.organization_id,
                            studentId = it.student_id,
                            personType = it.person_type,
                            faceVector = parseFaceVectorString(it.face_vector),
                            imageUrl = it.image_url,
                            isActive = it.is_active,
                            isDeleted = it.is_deleted,
                            createdAt = it.created_at,
                            updatedAt = it.updated_at
                        )
                    }
                    // Pehle purana data saaf karo, phir naya insert karo
                    if (isAdminForSync || isDriver) {
                        institutionDao.deleteStudentImageVectorsForOrgs(vectorOrgIds)
                    } else if (studentIds.isNotEmpty()) {
                        for (sId in studentIds) {
                            institutionDao.deleteStudentImageVectorsByStudent(sId)
                        }
                    }
                    institutionDao.insertStudentImageVectors(localVectors)
                    Log.d("OfflineSync", "Synced ${localVectors.size} student image vectors for role: ${workspace.role}")
                } else {
                    Log.d("OfflineSync", "No image vectors found in Supabase for role: ${workspace.role} — table might be empty on server")
                }
            } catch (e: Exception) {
                Log.e("OfflineSync", "Failed to sync student image vectors", e)
            }

            // 1c. Sync Student QR Identities and ID Cards (for offline lookup by driver)
            try {
                if (isDriver || isAdminForSync) {
                    val qrIdentities = SupabaseClient.client.from("organization_student_qr_identities")
                        .select(columns = Columns.raw("id, organization_id, active_session_id, student_id, qr_token_hash, version, status, expiry_date, is_active, is_deleted, created_at, updated_at, created_by, updated_by")) {
                            filter {
                                isIn("organization_id", orgIds)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<StudentQrIdentityDto>()
                    
                    val localQr = qrIdentities.map {
                        LocalStudentQrIdentityEntity(
                            id = it.id,
                            organizationId = it.organization_id,
                            activeSessionId = it.active_session_id,
                            studentId = it.student_id,
                            qrTokenHash = it.qr_token_hash,
                            version = it.version,
                            status = it.status,
                            expiryDate = it.expiry_date,
                            isActive = it.is_active,
                            isDeleted = it.is_deleted,
                            createdAt = it.created_at,
                            updatedAt = it.updated_at,
                            createdBy = it.created_by,
                            updatedBy = it.updated_by
                        )
                    }
                    institutionDao.insertStudentQrIdentities(localQr)
                    Log.d("OfflineSync", "Synced ${localQr.size} student QR identities offline.")

                    val idCards = SupabaseClient.client.from("organization_student_id_cards")
                        .select(columns = Columns.raw("id, organization_id, active_session_id, student_id, qr_identity_id, card_number, status, reason_for_reissue, is_active, is_deleted, created_at, updated_at, created_by, updated_by")) {
                            filter {
                                isIn("organization_id", orgIds)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<StudentIdCardDto>()

                    val localCards = idCards.map {
                        LocalStudentIdCardEntity(
                            id = it.id,
                            organizationId = it.organization_id,
                            activeSessionId = it.active_session_id,
                            studentId = it.student_id,
                            qrIdentityId = it.qr_identity_id,
                            cardNumber = it.card_number,
                            status = it.status,
                            reasonForReissue = it.reason_for_reissue,
                            isActive = it.is_active,
                            isDeleted = it.is_deleted,
                            createdAt = it.created_at,
                            updatedAt = it.updated_at,
                            createdBy = it.created_by,
                            updatedBy = it.updated_by
                        )
                    }
                    institutionDao.insertStudentIdCards(localCards)
                    Log.d("OfflineSync", "Synced ${localCards.size} student ID cards offline.")
                }
            } catch (e: Exception) {
                Log.e("OfflineSync", "Failed to sync QR identities and ID cards", e)
            }

            // 1d. Sync Bus Trips and Attendance Logs for the driver
            try {
                if (isDriver) {
                    val staffLink = SupabaseClient.client.from("organization_parent_staff_user_links")
                        .select(columns = Columns.raw("staff_id")) {
                            filter {
                                eq("user_id", userId)
                                eq("parent_organization_id", workspace.parentOrgId)
                                eq("is_active", true)
                                eq("is_deleted", false)
                            }
                        }.decodeList<StaffLinkDto>().firstOrNull()

                    val staffId = staffLink?.staff_id
                    if (staffId != null) {
                        // Sync Student Bus Assignments for driver's bus
                        try {
                            val staffRecord = SupabaseClient.client.from("organization_parent_staff")
                                .select(columns = Columns.raw("bus_id")) {
                                    filter {
                                        eq("id", staffId)
                                        eq("is_active", true)
                                        eq("is_deleted", false)
                                    }
                                }.decodeSingleOrNull<StaffBusLinkDto>()
                            val busId = staffRecord?.bus_id
                            
                            if (busId != null) {
                                val assignments = SupabaseClient.client.from("organization_student_bus_assignments")
                                    .select(columns = Columns.raw("id, student_id, bus_id, pickup_stop")) {
                                        filter {
                                            eq("bus_id", busId)
                                            eq("is_active", true)
                                            eq("is_deleted", false)
                                        }
                                    }.decodeList<StudentBusAssignmentDto>()
                                
                                if (assignments.isNotEmpty()) {
                                    val studentIdsToFetch = assignments.map { it.student_id }.distinct()
                                    
                                    // 1. Fetch full student details from organization_students
                                    val students = SupabaseClient.client.from("organization_students")
                                        .select(columns = Columns.raw("id, organization_id, name, sr_number, guardian_id, image_url")) {
                                            filter {
                                                isIn("id", studentIdsToFetch)
                                            }
                                        }.decodeList<kotlinx.serialization.json.JsonObject>()
                                    
                                    // 2. Fetch enrollments
                                    val enrollments = SupabaseClient.client.from("organization_student_enrollments")
                                        .select(columns = Columns.raw("student_id, class_id, section_id, roll_number")) {
                                            filter {
                                                isIn("student_id", studentIdsToFetch)
                                                eq("is_active", true)
                                                eq("is_deleted", false)
                                            }
                                        }.decodeList<kotlinx.serialization.json.JsonObject>()
                                    
                                    val classIds = enrollments.mapNotNull { it["class_id"]?.toString()?.replace("\"", "") }.distinct()
                                    val sectionIds = enrollments.mapNotNull { it["section_id"]?.toString()?.replace("\"", "") }.distinct()
                                    val guardianIds = students.mapNotNull { it["guardian_id"]?.toString()?.replace("\"", "") }.distinct()
                                    
                                    // 3. Fetch org classes
                                    val orgClasses = if (classIds.isNotEmpty()) {
                                        SupabaseClient.client.from("organization_classes")
                                            .select(columns = Columns.raw("id, class_id")) {
                                                filter {
                                                    isIn("id", classIds)
                                                    eq("is_active", true)
                                                    eq("is_deleted", false)
                                                }
                                            }.decodeList<kotlinx.serialization.json.JsonObject>()
                                    } else emptyList()

                                    // 4. Fetch global classes
                                    val globalClassIds = orgClasses.mapNotNull { it["class_id"]?.toString()?.replace("\"", "") }.distinct()
                                    val globalClasses = if (globalClassIds.isNotEmpty()) {
                                        SupabaseClient.client.from("global_classes")
                                            .select(columns = Columns.raw("id, name")) {
                                                filter {
                                                    isIn("id", globalClassIds)
                                                    eq("is_active", true)
                                                    eq("is_deleted", false)
                                                }
                                            }.decodeList<kotlinx.serialization.json.JsonObject>()
                                    } else emptyList()
                                    
                                    // 5. Fetch org sections
                                    val orgSections = if (sectionIds.isNotEmpty()) {
                                        SupabaseClient.client.from("organization_sections")
                                            .select(columns = Columns.raw("id, name")) {
                                                filter {
                                                    isIn("id", sectionIds)
                                                    eq("is_active", true)
                                                    eq("is_deleted", false)
                                                }
                                            }.decodeList<kotlinx.serialization.json.JsonObject>()
                                    } else emptyList()
                                    
                                    // 6. Fetch guardians
                                    val guardians = if (guardianIds.isNotEmpty()) {
                                        SupabaseClient.client.from("organization_guardians")
                                            .select(columns = Columns.raw("id, name, mobile_number")) {
                                                filter {
                                                    isIn("id", guardianIds)
                                                    eq("is_active", true)
                                                    eq("is_deleted", false)
                                                }
                                            }.decodeList<kotlinx.serialization.json.JsonObject>()
                                    } else emptyList()
                                    
                                    // 7. Map to LocalStudentEntity and insert
                                    val localStudents = students.map { studentJson ->
                                        val id = studentJson["id"]?.toString()?.replace("\"", "") ?: ""
                                        val name = studentJson["name"]?.toString()?.replace("\"", "") ?: ""
                                        val srNumber = studentJson["sr_number"]?.toString()?.replace("\"", "")
                                        val imageUrl = studentJson["image_url"]?.toString()?.replace("\"", "")
                                        
                                        val enrollment = enrollments.find { it["student_id"]?.toString()?.replace("\"", "") == id }
                                        val classId = enrollment?.get("class_id")?.toString()?.replace("\"", "")
                                        val sectionId = enrollment?.get("section_id")?.toString()?.replace("\"", "")
                                        val rollNumber = enrollment?.get("roll_number")?.toString()?.replace("\"", "")?.toIntOrNull()
                                        
                                        val orgClass = orgClasses.find { it["id"]?.toString()?.replace("\"", "") == classId }
                                        val gClassId = orgClass?.get("class_id")?.toString()?.replace("\"", "")
                                        val globalClass = globalClasses.find { it["id"]?.toString()?.replace("\"", "") == gClassId }
                                        val className = globalClass?.get("name")?.toString()?.replace("\"", "")
                                        
                                        val section = orgSections.find { it["id"]?.toString()?.replace("\"", "") == sectionId }
                                        val sectionName = section?.get("name")?.toString()?.replace("\"", "")
                                        
                                        val guardianId = studentJson["guardian_id"]?.toString()?.replace("\"", "")
                                        val guardian = guardians.find { it["id"]?.toString()?.replace("\"", "") == guardianId }
                                        val guardianName = guardian?.get("name")?.toString()?.replace("\"", "")
                                        val guardianMobile = guardian?.get("mobile_number")?.toString()?.replace("\"", "")
                                        
                                        val organizationIdDriver = studentJson["organization_id"]?.toString()?.replace("\"", "") ?: ""
                                        LocalStudentEntity(
                                            id = id,
                                            organizationId = organizationIdDriver,
                                            name = name,
                                            srNumber = srNumber,
                                            rollNumber = rollNumber,
                                            className = className,
                                            sectionName = sectionName,
                                            guardianName = guardianName,
                                            guardianMobile = guardianMobile,
                                            imageUrl = imageUrl
                                        )
                                    }
                                    institutionDao.insertStudents(localStudents)
                                    
                                    val buses = SupabaseClient.client.from("organization_parent_buses")
                                        .select(columns = Columns.raw("id, bus_number, bus_name, route_name")) {
                                            filter {
                                                isIn("id", listOf(busId))
                                                eq("is_active", true)
                                                eq("is_deleted", false)
                                            }
                                        }.decodeList<BusDetailsDto>()
                                    
                                    val domainList = assignments.mapNotNull { assign ->
                                        val studentName = students.find { it["id"]?.toString()?.replace("\"", "") == assign.student_id }?.get("name")?.toString()?.replace("\"", "") ?: "Student"
                                        val bus = buses.find { it.id == assign.bus_id }
                                        StudentBusAssignment(
                                            studentId = assign.student_id,
                                            studentName = studentName,
                                            busId = assign.bus_id,
                                            busNumber = bus?.bus_number ?: "",
                                            busName = bus?.bus_name,
                                            routeName = bus?.route_name,
                                            pickupStop = assign.pickup_stop
                                        )
                                    }
                                    
                                    institutionDao.deleteStudentBusAssignmentsForStudents(studentIdsToFetch)
                                    institutionDao.insertStudentBusAssignments(domainList.map { LocalStudentBusAssignmentEntity.fromDomain(it) })
                                    Log.d("OfflineSync", "Synced ${domainList.size} student bus assignments offline for driver.")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("OfflineSync", "Failed to sync student bus assignments for driver", e)
                        }

                        val trips = SupabaseClient.client.from("organization_parent_bus_trips")
                            .select(columns = Columns.raw("id, parent_organization_id, active_session_id, bus_id, driver_id, trip_type, status, start_time, end_time, is_active, is_deleted, created_at, updated_at, created_by, updated_by")) {
                                filter {
                                    eq("driver_id", staffId)
                                    eq("is_deleted", false)
                                }
                            }.decodeList<ParentBusTripDto>()

                        val localTrips = trips.map {
                            LocalParentBusTripEntity(
                                id = it.id,
                                parentOrganizationId = it.parent_organization_id,
                                activeSessionId = it.active_session_id,
                                busId = it.bus_id,
                                driverId = it.driver_id,
                                tripType = it.trip_type,
                                status = it.status,
                                startTime = it.start_time,
                                endTime = it.end_time,
                                isActive = it.is_active,
                                isDeleted = it.is_deleted,
                                createdAt = it.created_at,
                                updatedAt = it.updated_at,
                                createdBy = it.created_by,
                                updatedBy = it.updated_by
                            )
                        }
                        institutionDao.insertParentBusTrips(localTrips)
                        Log.d("OfflineSync", "Synced ${localTrips.size} bus trips offline.")

                        val tripIds = trips.map { it.id }
                        if (tripIds.isNotEmpty()) {
                            val logs = SupabaseClient.client.from("organization_parent_bus_trip_attendance_logs")
                                .select(columns = Columns.raw("id, parent_organization_id, organization_id, active_session_id, trip_id, student_id, status, scan_latitude, scan_longitude, scanned_at, scanned_by_staff_id, sync_status, is_active, is_deleted, created_at, updated_at, created_by, updated_by")) {
                                    filter {
                                        isIn("trip_id", tripIds)
                                        eq("is_deleted", false)
                                    }
                                }.decodeList<ParentBusTripAttendanceLogDto>()

                            val localLogs = logs.map {
                                LocalParentBusTripAttendanceLogEntity(
                                    id = it.id,
                                    parentOrganizationId = it.parent_organization_id,
                                    organizationId = it.organization_id,
                                    activeSessionId = it.active_session_id,
                                    tripId = it.trip_id,
                                    studentId = it.student_id,
                                    status = it.status,
                                    scanLatitude = it.scan_latitude,
                                    scanLongitude = it.scan_longitude,
                                    scannedAt = it.scanned_at,
                                    scannedByStaffId = it.scanned_by_staff_id,
                                    syncStatus = "Synced",
                                    isActive = it.is_active,
                                    isDeleted = it.is_deleted,
                                    createdAt = it.created_at,
                                    updatedAt = it.updated_at,
                                    createdBy = it.created_by,
                                    updatedBy = it.updated_by
                                )
                            }
                            institutionDao.insertParentBusTripAttendanceLogs(localLogs)
                            Log.d("OfflineSync", "Synced ${localLogs.size} bus trip attendance logs offline.")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("OfflineSync", "Failed to sync bus trips and logs", e)
            }
            
            // 2. Proactively trigger all key-value cached endpoints so they cache their data!
            try {
                getLeaves(userId, workspace.role, forceRefresh = true)
            } catch (e: Exception) {
                Log.e("OfflineSync", "Failed to cache leaves", e)
            }
            
            try {
                getContentFeed(workspace, sessionId, forceRefresh = true)
            } catch (e: Exception) {
                Log.e("OfflineSync", "Failed to cache content feed", e)
            }
            
            if (workspace.role == "Guardian") {
                try {
                    getGuardianStudents(workspace.id, forceRefresh = true)
                } catch (e: Exception) {
                    Log.e("OfflineSync", "Failed to cache guardian students", e)
                }
                if (studentIds.isNotEmpty()) {
                    try {
                        getFeePayments(studentIds, forceRefresh = true)
                    } catch (e: Exception) {
                        Log.e("OfflineSync", "Failed to cache fee payments", e)
                    }
                    try {
                        getStudentAttendance(studentIds, forceRefresh = true)
                    } catch (e: Exception) {
                        Log.e("OfflineSync", "Failed to cache student attendance", e)
                    }
                    try {
                        getStudentBusAssignments(studentIds, forceRefresh = true)
                    } catch (e: Exception) {
                        Log.e("OfflineSync", "Failed to cache student bus assignments", e)
                    }
                }
            } else {
                try {
                    getStaffSalaryDetails(userId, workspace.parentOrgId, forceRefresh = true)
                } catch (e: Exception) {
                    Log.e("OfflineSync", "Failed to cache salary details", e)
                }
                try {
                    getStaffLeaveQuotaAndRemaining(userId, workspace.parentOrgId, forceRefresh = true)
                } catch (e: Exception) {
                    Log.e("OfflineSync", "Failed to cache staff leave quota", e)
                }
                
                if (isAdmin) {
                    try {
                        val childOrgs = getOrganizations(workspace.parentOrgId, forceRefresh = true).getOrNull() ?: emptyList()
                        for (org in childOrgs) {
                            val classes = getClasses(org.id, forceRefresh = true).getOrNull() ?: emptyList()
                            for (clazz in classes) {
                                getSections(clazz.id, forceRefresh = true)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("OfflineSync", "Failed to cache organizations/classes/sections", e)
                    }
                    try {
                        getAdminFeeStats(workspace.parentOrgId, forceRefresh = true)
                    } catch (e: Exception) {
                        Log.e("OfflineSync", "Failed to cache admin fee stats", e)
                    }
                }
            }
            
            // 3. Sync offline remarks & fetch from server
            try {
                syncRemarksOffline()
            } catch (e: Exception) {
                Log.e("OfflineSync", "Failed to sync offline remarks", e)
            }
            try {
                fetchRemarksFromServer(sessionId, workspace.parentOrgId)
            } catch (e: Exception) {
                Log.e("OfflineSync", "Failed to fetch remarks from server", e)
            }
            try {
                loadGlobalStaffRolesFromServer()
            } catch (e: Exception) {
                Log.e("OfflineSync", "Failed to load global staff roles", e)
            }

            Log.d("OfflineSync", "Sync completed successfully.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("OfflineSync", "Sync failed for workspace: ${workspace.id}", e)
            Result.failure(e)
        }
    }

    private fun LocalParentBusTripEntity.toDomain(): ParentBusTrip = ParentBusTrip(
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
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdBy = createdBy,
        updatedBy = updatedBy
    )

    private fun ParentBusTrip.toEntity(): LocalParentBusTripEntity = LocalParentBusTripEntity(
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
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdBy = createdBy,
        updatedBy = updatedBy
    )

    private fun LocalParentBusTripAttendanceLogEntity.toDomain(): ParentBusTripAttendanceLog = ParentBusTripAttendanceLog(
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
        syncStatus = syncStatus,
        isActive = isActive,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdBy = createdBy,
        updatedBy = updatedBy
    )

    private fun ParentBusTripAttendanceLog.toEntity(): LocalParentBusTripAttendanceLogEntity = LocalParentBusTripAttendanceLogEntity(
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
        syncStatus = syncStatus,
        isActive = isActive,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdBy = createdBy,
        updatedBy = updatedBy
    )

    override suspend fun getActiveBusTrip(driverId: String): Result<ParentBusTrip?> = withContext(Dispatchers.IO) {
        try {
            val local = institutionDao.getActiveBusTripForDriver(driverId, "Ongoing")
            if (local != null) {
                return@withContext Result.success(local.toDomain())
            }
            val remoteList = SupabaseClient.client.from("organization_parent_bus_trips")
                .select(columns = Columns.raw("id, parent_organization_id, active_session_id, bus_id, driver_id, trip_type, status, start_time, end_time, is_active, is_deleted, created_at, updated_at, created_by, updated_by")) {
                    filter {
                        eq("driver_id", driverId)
                        eq("status", "Ongoing")
                        eq("is_deleted", false)
                    }
                }.decodeList<ParentBusTripDto>()
            val remote = remoteList.firstOrNull()
            if (remote != null) {
                val entity = LocalParentBusTripEntity(
                    id = remote.id,
                    parentOrganizationId = remote.parent_organization_id,
                    activeSessionId = remote.active_session_id,
                    busId = remote.bus_id,
                    driverId = remote.driver_id,
                    tripType = remote.trip_type,
                    status = remote.status,
                    startTime = remote.start_time,
                    endTime = remote.end_time,
                    isActive = remote.is_active,
                    isDeleted = remote.is_deleted,
                    createdAt = remote.created_at,
                    updatedAt = remote.updated_at,
                    createdBy = remote.created_by,
                    updatedBy = remote.updated_by
                )
                institutionDao.insertParentBusTrips(listOf(entity))
                return@withContext Result.success(entity.toDomain())
            }
            Result.success(null)
        } catch (e: Exception) {
            Log.e("DriverAttendance", "Error getting active bus trip", e)
            Result.failure(e)
        }
    }

    override suspend fun getStudentByQrHash(hash: String): Result<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity?> = withContext(Dispatchers.IO) {
        try {
            val qr = institutionDao.getQrIdentityByHash(hash)
            if (qr == null) {
                return@withContext Result.success(null)
            }
            val students = institutionDao.getStudentsByIds(listOf(qr.studentId))
            Result.success(students.firstOrNull())
        } catch (e: Exception) {
            Log.e("DriverAttendance", "Error getting student by QR hash", e)
            Result.failure(e)
        }
    }

    override suspend fun getParentBusTripsForDriver(driverId: String): Result<List<ParentBusTrip>> = withContext(Dispatchers.IO) {
        try {
            val local = institutionDao.getParentBusTripsForDriver(driverId).map { it.toDomain() }
            Result.success(local)
        } catch (e: Exception) {
            Log.e("DriverAttendance", "Error getting driver trips", e)
            Result.failure(e)
        }
    }

    override suspend fun startBusTrip(
        parentOrgId: String,
        sessionId: String,
        busId: String,
        driverId: String,
        tripType: String
    ): Result<ParentBusTrip> = withContext(Dispatchers.IO) {
        try {
            val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", java.util.Locale.US).format(java.util.Date())
            val tripId = java.util.UUID.randomUUID().toString()
            val trip = ParentBusTrip(
                id = tripId,
                parentOrganizationId = parentOrgId,
                activeSessionId = sessionId,
                busId = busId,
                driverId = driverId,
                tripType = tripType,
                status = "Ongoing",
                startTime = now,
                endTime = null,
                isActive = true,
                isDeleted = false,
                createdAt = now,
                updatedAt = now,
                createdBy = driverId,
                updatedBy = driverId
            )
            
            institutionDao.insertParentBusTrips(listOf(trip.toEntity()))
            
            try {
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
                    is_deleted = trip.isDeleted,
                    created_at = trip.createdAt,
                    updated_at = trip.updatedAt,
                    created_by = trip.createdBy,
                    updated_by = trip.updatedBy
                )
                SupabaseClient.client.from("organization_parent_bus_trips").insert(dto)
                Log.d("DriverAttendance", "Trip inserted in Supabase successfully")
            } catch (ne: Exception) {
                Log.w("DriverAttendance", "Could not write trip to Supabase immediately (offline). Saved locally.", ne)
            }
            
            Result.success(trip)
        } catch (e: Exception) {
            Log.e("DriverAttendance", "Error starting bus trip", e)
            Result.failure(e)
        }
    }

    override suspend fun endBusTrip(tripId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", java.util.Locale.US).format(java.util.Date())
            
            try {
                SupabaseClient.client.from("organization_parent_bus_trips")
                    .update(mapOf("status" to "Completed", "end_time" to now, "updated_at" to now)) {
                        filter { eq("id", tripId) }
                    }
                Log.d("DriverAttendance", "Trip ended in Supabase successfully")
            } catch (ne: Exception) {
                Log.w("DriverAttendance", "Could not end trip in Supabase (offline). Saved locally.", ne)
            }

            institutionDao.updateParentBusTripStatus(tripId, "Completed", now, now)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DriverAttendance", "Error ending bus trip", e)
            Result.failure(e)
        }
    }

    override suspend fun submitBusAttendanceLog(log: ParentBusTripAttendanceLog): Result<ParentBusTripAttendanceLog> = withContext(Dispatchers.IO) {
        try {
            val qr = institutionDao.getQrIdentityByStudent(log.studentId)
            val correctOrgId = qr?.organizationId ?: log.organizationId
            val correctedLog = log.copy(organizationId = correctOrgId)
            val entity = correctedLog.toEntity()
            institutionDao.insertParentBusTripAttendanceLogs(listOf(entity))
            
            try {
                val dto = ParentBusTripAttendanceLogDto(
                    id = correctedLog.id,
                    parent_organization_id = correctedLog.parentOrganizationId,
                    organization_id = correctedLog.organizationId,
                    active_session_id = correctedLog.activeSessionId,
                    trip_id = correctedLog.tripId,
                    student_id = correctedLog.studentId,
                    status = correctedLog.status,
                    scan_latitude = correctedLog.scanLatitude,
                    scan_longitude = correctedLog.scanLongitude,
                    scanned_at = correctedLog.scannedAt,
                    scanned_by_staff_id = correctedLog.scannedByStaffId,
                    sync_status = "Synced",
                    is_active = correctedLog.isActive,
                    is_deleted = correctedLog.isDeleted,
                    created_at = correctedLog.createdAt,
                    updated_at = correctedLog.updatedAt,
                    created_by = correctedLog.createdBy,
                    updated_by = correctedLog.updatedBy
                )
                SupabaseClient.client.from("organization_parent_bus_trip_attendance_logs").insert(dto)
                
                val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", java.util.Locale.US).format(java.util.Date())
                institutionDao.updateAttendanceLogSyncStatus(correctedLog.id, "Synced", now)
                Log.d("DriverAttendance", "Attendance log inserted in Supabase successfully")
                return@withContext Result.success(correctedLog.copy(syncStatus = "Synced", updatedAt = now))
            } catch (ne: Exception) {
                Log.w("DriverAttendance", "Could not write attendance log to Supabase immediately (offline). Saved locally.", ne)
                return@withContext Result.success(correctedLog)
            }
        } catch (e: Exception) {
            Log.e("DriverAttendance", "Error submitting attendance log", e)
            Result.failure(e)
        }
    }

    override suspend fun getBusTripAttendanceLogs(tripId: String, forceRefresh: Boolean): Result<List<ParentBusTripAttendanceLog>> = withContext(Dispatchers.IO) {
        try {
            if (!forceRefresh) {
                val local = institutionDao.getAttendanceLogsForTrip(tripId).map { it.toDomain() }
                if (local.isNotEmpty()) {
                    return@withContext Result.success(local)
                }
            }
            val remoteLogs = SupabaseClient.client.from("organization_parent_bus_trip_attendance_logs")
                .select(columns = Columns.raw("id, parent_organization_id, organization_id, active_session_id, trip_id, student_id, status, scan_latitude, scan_longitude, scanned_at, scanned_by_staff_id, sync_status, is_active, is_deleted, created_at, updated_at, created_by, updated_by")) {
                    filter {
                        eq("trip_id", tripId)
                        eq("is_deleted", false)
                    }
                }.decodeList<ParentBusTripAttendanceLogDto>()
            val localLogs = remoteLogs.map {
                LocalParentBusTripAttendanceLogEntity(
                    id = it.id,
                    parentOrganizationId = it.parent_organization_id,
                    organizationId = it.organization_id,
                    activeSessionId = it.active_session_id,
                    tripId = it.trip_id,
                    studentId = it.student_id,
                    status = it.status,
                    scanLatitude = it.scan_latitude,
                    scanLongitude = it.scan_longitude,
                    scannedAt = it.scanned_at,
                    scannedByStaffId = it.scanned_by_staff_id,
                    syncStatus = "Synced",
                    isActive = it.is_active,
                    isDeleted = it.is_deleted,
                    createdAt = it.created_at,
                    updatedAt = it.updated_at,
                    createdBy = it.created_by,
                    updatedBy = it.updated_by
                )
            }
            institutionDao.insertParentBusTripAttendanceLogs(localLogs)
            Result.success(localLogs.map { it.toDomain() })
        } catch (e: Exception) {
            Log.e("DriverAttendance", "Error getting attendance logs", e)
            Result.failure(e)
        }
    }

    override suspend fun getBusTripAttendanceLogsWithStudentInfo(tripId: String): Result<List<com.vidyasetuai.feature_institution.data.local.entity.LocalParentBusTripAttendanceLogWithStudentInfo>> = withContext(Dispatchers.IO) {
        try {
            val logs = institutionDao.getAttendanceLogsForTripWithStudentInfo(tripId)
            Result.success(logs)
        } catch (e: Exception) {
            Log.e("DriverAttendance", "Error getting logs with student info", e)
            Result.failure(e)
        }
    }

    override suspend fun syncOfflineAttendanceLogs(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val pending = institutionDao.getPendingSyncAttendanceLogs()
            if (pending.isEmpty()) return@withContext Result.success(0)
            
            var syncCount = 0
            for (log in pending) {
                try {
                    val trip = institutionDao.getParentBusTripById(log.tripId)
                    if (trip != null) {
                        val tripDto = ParentBusTripDto(
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
                            is_deleted = trip.isDeleted,
                            created_at = trip.createdAt,
                            updated_at = trip.updatedAt,
                            created_by = trip.createdBy,
                            updated_by = trip.updatedBy
                        )
                        try {
                            SupabaseClient.client.from("organization_parent_bus_trips").insert(tripDto)
                        } catch (e: Exception) {
                            // Already exists or duplicate
                        }
                    }
                    
                    val qr = institutionDao.getQrIdentityByStudent(log.studentId)
                    val correctOrgId = qr?.organizationId ?: log.organizationId
                    
                    if (correctOrgId != log.organizationId) {
                        val correctedLocalLog = log.copy(organizationId = correctOrgId)
                        institutionDao.insertParentBusTripAttendanceLogs(listOf(correctedLocalLog))
                    }
                    
                    val dto = ParentBusTripAttendanceLogDto(
                        id = log.id,
                        parent_organization_id = log.parentOrganizationId,
                        organization_id = correctOrgId,
                        active_session_id = log.activeSessionId,
                        trip_id = log.tripId,
                        student_id = log.studentId,
                        status = log.status,
                        scan_latitude = log.scanLatitude,
                        scan_longitude = log.scanLongitude,
                        scanned_at = log.scannedAt,
                        scanned_by_staff_id = log.scannedByStaffId,
                        sync_status = "Synced",
                        is_active = log.isActive,
                        is_deleted = log.isDeleted,
                        created_at = log.createdAt,
                        updated_at = log.updatedAt,
                        created_by = log.createdBy,
                        updated_by = log.updatedBy
                    )
                    SupabaseClient.client.from("organization_parent_bus_trip_attendance_logs").insert(dto)
                    
                    val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", java.util.Locale.US).format(java.util.Date())
                    institutionDao.updateAttendanceLogSyncStatus(log.id, "Synced", now)
                    syncCount++
                } catch (e: Exception) {
                    val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", java.util.Locale.US).format(java.util.Date())
                    val errMsg = e.message ?: ""
                    // FK constraint violations are irrecoverable — soft-delete to stop infinite retry loop
                    if (errMsg.contains("foreign key", ignoreCase = true) ||
                        errMsg.contains("violates", ignoreCase = true) ||
                        errMsg.contains("fk_log_org", ignoreCase = true)) {
                        Log.e("OfflineSync", "Permanently failed log ${log.id} due to FK violation — soft-deleting to stop retry loop", e)
                        institutionDao.markAttendanceLogAsBadAndDelete(log.id, now)
                    } else {
                        Log.e("OfflineSync", "Failed to sync single log ${log.id} (Fix with AI)", e)
                    }
                }
            }
            Result.success(syncCount)
        } catch (e: Exception) {
            Log.e("OfflineSync", "Error in syncOfflineAttendanceLogs", e)
            Result.failure(e)
        }
    }

    override suspend fun searchStudentsOffline(
        query: String,
        classFilterName: String?,
        sectionFilterName: String?
    ): Result<List<StudentSearchResult>> = withContext(Dispatchers.IO) {
        try {
            val dbQuery = "%$query%"
            val entities = institutionDao.searchStudentsWithFilters(
                query = dbQuery,
                className = classFilterName,
                sectionName = sectionFilterName
            )
            val results = entities.map { entity ->
                StudentSearchResult(
                    id = entity.id,
                    name = entity.name,
                    sr_number = entity.srNumber,
                    roll_number = entity.rollNumber,
                    class_name = entity.className,
                    section_name = entity.sectionName,
                    guardian_name = entity.guardianName,
                    guardian_mobile = entity.guardianMobile,
                    image_url = entity.imageUrl
                )
            }
            Result.success(results)
        } catch (e: Exception) {
            Log.e("OfflineSearch", "Error searching students offline", e)
            Result.failure(e)
        }
    }

    override suspend fun getStudentsAssignedToBus(busId: String): Result<List<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity>> = withContext(Dispatchers.IO) {
        try {
            val list = institutionDao.getStudentsAssignedToBus(busId)
            Result.success(list)
        } catch (e: Exception) {
            Log.e("DriverAttendance", "Error getting students assigned to bus", e)
            Result.failure(e)
        }
    }

    override suspend fun getRemarks(sessionId: String): Result<List<Remark>> = withContext(Dispatchers.IO) {
        try {
            val list = institutionDao.getRemarksForActiveSession(sessionId).map { it.toDomain() }
            Result.success(list)
        } catch (e: Exception) {
            Log.e("Remarks", "Error fetching local remarks", e)
            Result.failure(e)
        }
    }

    override suspend fun getRemarkTargets(remarkId: String): Result<List<RemarkTarget>> = withContext(Dispatchers.IO) {
        try {
            val list = institutionDao.getRemarkTargetsForRemark(remarkId).map { it.toDomain() }
            Result.success(list)
        } catch (e: Exception) {
            Log.e("Remarks", "Error fetching local remark targets", e)
            Result.failure(e)
        }
    }

    override suspend fun addRemark(remark: Remark, targets: List<RemarkTarget>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Save locally first
            val localRemark = LocalRemarkEntity.fromDomain(remark.copy(syncStatus = "Offline_Pending"))
            val localTargets = targets.map { LocalRemarkTargetEntity.fromDomain(it.copy(syncStatus = "Offline_Pending")) }
            institutionDao.insertRemarks(listOf(localRemark))
            institutionDao.insertRemarkTargets(localTargets)

            // Try to sync with server
            try {
                val remarkDto = RemarkDto(
                    id = remark.id,
                    parent_organization_id = remark.parentOrgId,
                    organization_id = remark.organizationId,
                    active_session_id = remark.activeSessionId,
                    content = remark.content,
                    category = remark.category,
                    priority = remark.priority,
                    creator_user_id = remark.creatorUserId,
                    creator_workspace_role_id = remark.creatorWorkspaceRoleId,
                    visibility_type = remark.visibilityType,
                    visibility_audience = remark.visibilityAudience,
                    is_pinned = remark.isPinned,
                    pin_expires_at = remark.pinExpiresAt,
                    expires_at = remark.expiresAt,
                    is_active = remark.isActive,
                    is_deleted = remark.isDeleted,
                    created_at = remark.createdAt,
                    updated_at = remark.updatedAt,
                    created_by = remark.createdBy,
                    updated_by = remark.updatedBy
                )
                SupabaseClient.client.from("organization_remarks").insert(remarkDto)
                institutionDao.updateRemarkSyncStatus(remark.id, "Synced", remark.updatedAt)

                for (target in targets) {
                    val targetDto = RemarkTargetDto(
                        id = target.id,
                        parent_organization_id = target.parentOrgId,
                        organization_id = target.organizationId,
                        active_session_id = target.activeSessionId,
                        remark_id = target.remarkId,
                        target_type = target.targetType,
                        target_student_id = target.targetStudentId,
                        target_guardian_id = target.targetGuardianId,
                        target_staff_id = target.targetStaffId,
                        target_user_id = target.targetUserId,
                        is_active = target.isActive,
                        is_deleted = target.isDeleted,
                        created_at = target.createdAt,
                        updated_at = target.updatedAt,
                        created_by = target.createdBy,
                        updated_by = target.updatedBy
                    )
                    SupabaseClient.client.from("organization_remark_targets").insert(targetDto)
                    institutionDao.updateRemarkTargetSyncStatus(target.id, "Synced", target.updatedAt)
                }
            } catch (e: Exception) {
                Log.e("Remarks", "Failed to sync added remark immediately, will sync later", e)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Remarks", "Error adding remark", e)
            Result.failure(e)
        }
    }

    override suspend fun softDeleteRemark(remarkId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val nowStr = java.time.format.DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.now())
            institutionDao.softDeleteRemark(remarkId, nowStr)
            institutionDao.softDeleteRemarkTargets(remarkId, nowStr)

            try {
                SupabaseClient.client.from("organization_remarks").update(buildJsonObject {
                    put("is_deleted", true)
                    put("updated_at", nowStr)
                }) {
                    filter {
                        eq("id", remarkId)
                    }
                }
                institutionDao.updateRemarkSyncStatus(remarkId, "Synced", nowStr)

                SupabaseClient.client.from("organization_remark_targets").update(buildJsonObject {
                    put("is_deleted", true)
                    put("updated_at", nowStr)
                }) {
                    filter {
                        eq("remark_id", remarkId)
                    }
                }
            } catch (e: Exception) {
                Log.e("Remarks", "Failed to sync soft delete immediately, will sync later", e)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Remarks", "Error deleting remark", e)
            Result.failure(e)
        }
    }

    override suspend fun getGlobalStaffRoles(): Result<List<GlobalStaffRole>> = withContext(Dispatchers.IO) {
        try {
            val list = institutionDao.getGlobalStaffRoles().map { it.toDomain() }
            Result.success(list)
        } catch (e: Exception) {
            Log.e("Remarks", "Error fetching local global staff roles", e)
            Result.failure(e)
        }
    }

    override suspend fun loadGlobalStaffRolesFromServer(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val list = SupabaseClient.client.from("global_staff_roles")
                .select(columns = Columns.raw("id, name, code, description, is_active, is_deleted")) {
                    filter {
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.decodeList<GlobalStaffRoleServerDto>()

            val localEntities = list.map {
                LocalGlobalStaffRoleEntity(
                    id = it.id,
                    name = it.name,
                    code = it.code,
                    description = it.description,
                    isActive = it.is_active,
                    isDeleted = it.is_deleted
                )
            }
            institutionDao.insertGlobalStaffRoles(localEntities)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Remarks", "Error loading global staff roles from server", e)
            Result.failure(e)
        }
    }

    override suspend fun syncRemarksOffline(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val pendingRemarks = institutionDao.getPendingSyncRemarks()
            for (remarkEntity in pendingRemarks) {
                val remark = remarkEntity.toDomain()
                val remarkDto = RemarkDto(
                    id = remark.id,
                    parent_organization_id = remark.parentOrgId,
                    organization_id = remark.organizationId,
                    active_session_id = remark.activeSessionId,
                    content = remark.content,
                    category = remark.category,
                    priority = remark.priority,
                    creator_user_id = remark.creatorUserId,
                    creator_workspace_role_id = remark.creatorWorkspaceRoleId,
                    visibility_type = remark.visibilityType,
                    visibility_audience = remark.visibilityAudience,
                    is_pinned = remark.isPinned,
                    pin_expires_at = remark.pinExpiresAt,
                    expires_at = remark.expiresAt,
                    is_active = remark.isActive,
                    is_deleted = remark.isDeleted,
                    created_at = remark.createdAt,
                    updated_at = remark.updatedAt,
                    created_by = remark.createdBy,
                    updated_by = remark.updatedBy
                )
                SupabaseClient.client.from("organization_remarks").upsert(remarkDto)
                institutionDao.updateRemarkSyncStatus(remark.id, "Synced", remark.updatedAt)
            }

            val pendingTargets = institutionDao.getPendingSyncRemarkTargets()
            for (targetEntity in pendingTargets) {
                val target = targetEntity.toDomain()
                val targetDto = RemarkTargetDto(
                    id = target.id,
                    parent_organization_id = target.parentOrgId,
                    organization_id = target.organizationId,
                    active_session_id = target.activeSessionId,
                    remark_id = target.remarkId,
                    target_type = target.targetType,
                    target_student_id = target.targetStudentId,
                    target_guardian_id = target.targetGuardianId,
                    target_staff_id = target.targetStaffId,
                    target_user_id = target.targetUserId,
                    is_active = target.isActive,
                    is_deleted = target.isDeleted,
                    created_at = target.createdAt,
                    updated_at = target.updatedAt,
                    created_by = target.createdBy,
                    updated_by = target.updatedBy
                )
                SupabaseClient.client.from("organization_remark_targets").upsert(targetDto)
                institutionDao.updateRemarkTargetSyncStatus(target.id, "Synced", target.updatedAt)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Remarks", "Error syncing pending remarks offline", e)
            Result.failure(e)
        }
    }

    override suspend fun fetchRemarksFromServer(sessionId: String, parentOrgId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val remarks = SupabaseClient.client.from("organization_remarks")
                .select(columns = Columns.raw("id, parent_organization_id, organization_id, active_session_id, content, category, priority, creator_user_id, creator_workspace_role_id, visibility_type, visibility_audience, is_pinned, pin_expires_at, expires_at, is_active, is_deleted, created_at, updated_at, created_by, updated_by")) {
                    filter {
                        eq("active_session_id", sessionId)
                        eq("parent_organization_id", parentOrgId)
                        eq("is_deleted", false)
                        eq("is_active", true)
                    }
                }.decodeList<RemarkDto>()

            val remarkEntities = remarks.map {
                LocalRemarkEntity(
                    id = it.id,
                    parentOrgId = it.parent_organization_id,
                    organizationId = it.organization_id,
                    activeSessionId = it.active_session_id,
                    content = it.content,
                    category = it.category,
                    priority = it.priority,
                    creatorUserId = it.creator_user_id,
                    creatorWorkspaceRoleId = it.creator_workspace_role_id,
                    visibilityType = it.visibility_type,
                    visibilityAudience = it.visibility_audience,
                    isPinned = it.is_pinned,
                    pinExpiresAt = it.pin_expires_at,
                    expiresAt = it.expires_at,
                    isActive = it.is_active,
                    isDeleted = it.is_deleted,
                    createdAt = it.created_at,
                    updatedAt = it.updated_at,
                    createdBy = it.created_by,
                    updatedBy = it.updated_by,
                    syncStatus = "Synced"
                )
            }
            institutionDao.insertRemarks(remarkEntities)

            if (remarks.isNotEmpty()) {
                val remarkIds = remarks.map { it.id }
                val targets = SupabaseClient.client.from("organization_remark_targets")
                    .select(columns = Columns.raw("id, parent_organization_id, organization_id, active_session_id, remark_id, target_type, target_student_id, target_guardian_id, target_staff_id, target_user_id, is_active, is_deleted, created_at, updated_at, created_by, updated_by")) {
                        filter {
                            isIn("remark_id", remarkIds)
                            eq("is_deleted", false)
                            eq("is_active", true)
                        }
                    }.decodeList<RemarkTargetDto>()

                val targetEntities = targets.map {
                    LocalRemarkTargetEntity(
                        id = it.id,
                        parentOrgId = it.parent_organization_id,
                        organizationId = it.organization_id,
                        activeSessionId = it.active_session_id,
                        remarkId = it.remark_id,
                        targetType = it.target_type,
                        targetStudentId = it.target_student_id,
                        targetGuardianId = it.target_guardian_id,
                        targetStaffId = it.target_staff_id,
                        targetUserId = it.target_user_id,
                        isActive = it.is_active,
                        isDeleted = it.is_deleted,
                        createdAt = it.created_at,
                        updatedAt = it.updated_at,
                        createdBy = it.created_by,
                        updatedBy = it.updated_by,
                        syncStatus = "Synced"
                    )
                }
                institutionDao.insertRemarkTargets(targetEntities)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Remarks", "Error fetching remarks from server", e)
            Result.failure(e)
        }
    }

    override suspend fun getLocalStudentById(studentId: String): Result<LocalStudentEntity?> = withContext(Dispatchers.IO) {
        try {
            Result.success(institutionDao.getStudentsByIds(listOf(studentId)).firstOrNull())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOfflineStaff(parentOrgId: String): Result<List<com.vidyasetuai.feature_institution.data.local.entity.LocalParentStaffEntity>> = withContext(Dispatchers.IO) {
        try {
            Result.success(institutionDao.getParentStaff(parentOrgId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStudentHomeLocation(studentId: String): Result<StudentHomeLocation?> = withContext(Dispatchers.IO) {
        try {
            val dto = SupabaseClient.client.from("organization_student_home_locations")
                .select {
                    filter {
                        eq("student_id", studentId)
                    }
                }.decodeSingleOrNull<StudentHomeLocationDto>()
            val model = dto?.let {
                StudentHomeLocation(
                    id = it.id,
                    organizationId = it.organization_id,
                    studentId = it.student_id,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    isNotificationSent = it.is_notification_sent
                    // createdBy and updatedBy not mapped — filled by set_audit_fields() DB trigger
                )
            }
            Result.success(model)
        } catch (e: Exception) {
            Log.e("VidyaSetu_HomeLocation", "Error fetching home location", e)
            Result.failure(e)
        }
    }

    override suspend fun saveStudentHomeLocation(
        organizationId: String,
        studentId: String,
        latitude: Double,
        longitude: Double,
        userId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Note: created_by and updated_by are auto filed by the set_audit_fields() trigger
            val dto = StudentHomeLocationDto(
                organization_id = organizationId,
                student_id = studentId,
                latitude = latitude,
                longitude = longitude,
                is_notification_sent = false
            )
            SupabaseClient.client.from("organization_student_home_locations").upsert(dto) {
                onConflict = "student_id"
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("VidyaSetu_HomeLocation", "Error saving home location", e)
            Result.failure(e)
        }
    }

    override suspend fun getStudentLinkByUserId(userId: String): Result<com.vidyasetuai.feature_institution.data.local.entity.LocalStudentUserLinkEntity?> = withContext(Dispatchers.IO) {
        try {
            val link = institutionDao.getStudentLinkByUserId(userId)
            Result.success(link)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Serializable
private data class RemarkDto(
    val id: String,
    val parent_organization_id: String,
    val organization_id: String?,
    val active_session_id: String,
    val content: String,
    val category: String,
    val priority: String,
    val creator_user_id: String,
    val creator_workspace_role_id: String? = null,
    val visibility_type: String = "Public",
    val visibility_audience: List<String> = emptyList(),
    val is_pinned: Boolean = false,
    val pin_expires_at: String? = null,
    val expires_at: String? = null,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String = "",
    val updated_at: String = "",
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
private data class RemarkTargetDto(
    val id: String,
    val parent_organization_id: String,
    val organization_id: String?,
    val active_session_id: String,
    val remark_id: String,
    val target_type: String,
    val target_student_id: String? = null,
    val target_guardian_id: String? = null,
    val target_staff_id: String? = null,
    val target_user_id: String? = null,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false,
    val created_at: String = "",
    val updated_at: String = "",
    val created_by: String? = null,
    val updated_by: String? = null
)

@Serializable
private data class GlobalStaffRoleServerDto(
    val id: String,
    val name: String,
    val code: String,
    val description: String? = null,
    val is_active: Boolean = true,
    val is_deleted: Boolean = false
)

@Serializable
private data class ExistingAttendanceCountDto(
    val id: String
)

private fun parseFaceVectorString(vectorStr: String): List<Float> {
    val clean = vectorStr.trim().removePrefix("[").removeSuffix("]")
    if (clean.isBlank()) return emptyList()
    return clean.split(",").mapNotNull { it.trim().toFloatOrNull() }
}

@Serializable
private data class StudentHomeLocationDto(
    val id: String? = null,
    val organization_id: String,
    val student_id: String,
    val latitude: Double,
    val longitude: Double,
    val is_notification_sent: Boolean = false
    // created_by and updated_by intentionally omitted — filled by set_audit_fields() DB trigger
)