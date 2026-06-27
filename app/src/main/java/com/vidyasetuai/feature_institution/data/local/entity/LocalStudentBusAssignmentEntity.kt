package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ============================================================
// SESSION, CONTENT, TIMETABLE, REMARK ENTITIES
// ============================================================

// Supabase table: organization_session_classes
@Entity(tableName = "local_session_classes")
data class LocalSessionClassEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val organizationClassId: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_session_sections
@Entity(tableName = "local_session_sections")
data class LocalSessionSectionEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val organizationClassId: String,
    val organizationSectionId: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_session_subjects
@Entity(tableName = "local_session_subjects")
data class LocalSessionSubjectEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val organizationClassId: String,
    val subjectId: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_content_assignments
@Entity(tableName = "local_content_assignments")
data class LocalContentAssignmentEntity(
    @PrimaryKey val id: String,
    val contentId: String,
    val targetOrganizationId: String,
    val assignedAt: String
)

// Supabase table: organization_timetable_schedules
@Entity(tableName = "local_timetable_schedules")
data class LocalTimetableScheduleEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val organizationSectionId: String,
    val periodId: String,
    val subjectId: String?,
    val staffId: String?,
    val dayOfWeek: Int,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

// Supabase table: organization_remark_attachments
@Entity(tableName = "local_remark_attachments")
data class LocalRemarkAttachmentEntity(
    @PrimaryKey val id: String,
    val remarkId: String,
    val fileUrl: String,
    val fileType: String?,
    val localFilePath: String?,    // Downloaded attachment local path
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String
)

// Supabase table: organization_remark_history
@Entity(tableName = "local_remark_history")
data class LocalRemarkHistoryEntity(
    @PrimaryKey val id: String,
    val remarkId: String,
    val changedBy: String,
    val changeType: String,
    val oldContent: String?,
    val newContent: String?,
    val createdAt: String
)

// Supabase table: organization_student_bus_assignments (full schema replacement)
@Entity(tableName = "local_student_bus_assignments")
data class LocalStudentBusAssignmentEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String = "",
    val organizationId: String = "",
    val activeSessionId: String = "",
    val studentId: String,
    val busId: String,
    val pickupStop: String? = null,
    val isActive: Boolean = true,
    val isDeleted: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = "",
    // Denormalized for display
    val studentName: String? = null,
    val busNumber: String? = null,
    val busName: String? = null,
    val routeName: String? = null
) {
    fun toDomain(): com.vidyasetuai.feature_institution.domain.model.StudentBusAssignment =
        com.vidyasetuai.feature_institution.domain.model.StudentBusAssignment(
            studentId = studentId,
            studentName = studentName ?: "",
            busId = busId,
            busNumber = busNumber ?: "",
            busName = busName,
            routeName = routeName,
            pickupStop = pickupStop
        )

    companion object {
        fun fromDomain(domain: com.vidyasetuai.feature_institution.domain.model.StudentBusAssignment): LocalStudentBusAssignmentEntity =
            LocalStudentBusAssignmentEntity(
                id = domain.studentId + "_" + domain.busId,
                studentId = domain.studentId,
                busId = domain.busId,
                pickupStop = domain.pickupStop,
                studentName = domain.studentName,
                busNumber = domain.busNumber,
                busName = domain.busName,
                routeName = domain.routeName
            )
    }
}
