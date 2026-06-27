package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_institution.domain.model.StudentAttendance

// Supabase table: organization_student_attendance
@Entity(tableName = "local_student_attendance")
data class LocalStudentAttendanceEntity(
    @PrimaryKey val id: String,
    val organizationId: String = "",
    val activeSessionId: String = "",
    val studentId: String,
    val attendanceDate: String,
    val status: String,
    val remarks: String?,
    val markedByStaffId: String? = null,
    val isActive: Boolean = true,
    val isDeleted: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    fun toDomain(): StudentAttendance = StudentAttendance(
        id = id,
        studentId = studentId,
        attendanceDate = attendanceDate,
        status = status,
        remarks = remarks
    )

    companion object {
        fun fromDomain(domain: StudentAttendance): LocalStudentAttendanceEntity =
            LocalStudentAttendanceEntity(
                id = domain.id,
                studentId = domain.studentId,
                attendanceDate = domain.attendanceDate,
                status = domain.status,
                remarks = domain.remarks
            )
    }
}
