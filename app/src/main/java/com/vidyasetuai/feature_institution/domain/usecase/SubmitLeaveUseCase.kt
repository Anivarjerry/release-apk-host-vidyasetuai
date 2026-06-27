package com.vidyasetuai.feature_institution.domain.usecase

import com.vidyasetuai.feature_institution.domain.repository.InstitutionRepository

class SubmitLeaveUseCase(private val repository: InstitutionRepository) {
    suspend operator fun invoke(
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
    ): Result<Unit> {
        return repository.submitLeave(
            parentOrgId = parentOrgId,
            orgId = orgId,
            sessionId = sessionId,
            applicantType = applicantType,
            staffId = staffId,
            studentId = studentId,
            leaveType = leaveType,
            startDate = startDate,
            endDate = endDate,
            isHalfDay = isHalfDay,
            halfDayPeriod = halfDayPeriod,
            reason = reason,
            createdBy = createdBy
        )
    }
}
