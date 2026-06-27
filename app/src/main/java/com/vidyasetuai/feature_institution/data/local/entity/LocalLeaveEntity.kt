package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_institution.domain.model.Leave

@Entity(tableName = "local_leaves")
data class LocalLeaveEntity(
    @PrimaryKey val id: String,
    val cachedForUserId: String,
    val parentOrgId: String,
    val organizationId: String?,
    val applicantType: String,
    val staffId: String?,
    val studentId: String?,
    val leaveType: String,
    val startDate: String,
    val endDate: String,
    val isHalfDay: Boolean,
    val halfDayPeriod: String?,
    val reason: String?,
    val status: String
) {
    fun toDomain(): Leave = Leave(
        id = id,
        parentOrgId = parentOrgId,
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

    companion object {
        fun fromDomain(domain: Leave, userId: String): LocalLeaveEntity = LocalLeaveEntity(
            id = domain.id,
            cachedForUserId = userId,
            parentOrgId = domain.parentOrgId,
            organizationId = domain.organizationId,
            applicantType = domain.applicantType,
            staffId = domain.staffId,
            studentId = domain.studentId,
            leaveType = domain.leaveType,
            startDate = domain.startDate,
            endDate = domain.endDate,
            isHalfDay = domain.isHalfDay,
            halfDayPeriod = domain.halfDayPeriod,
            reason = domain.reason,
            status = domain.status
        )
    }
}
