package com.vidyasetuai.feature_institution.domain.model

data class OrganizationExam(
    val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val examTypeId: String,
    val examTypeName: String?,
    val examTypeCode: String?,
    val name: String,
    val startDate: String?,
    val endDate: String?
)

data class ExamSubjectSetting(
    val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val examId: String,
    val classId: String,
    val subjectId: String,
    val className: String?,
    val subjectName: String?,
    val maxMarks: Double?,
    val minimumPassingMarks: Double?,
    val gradingSystem: String?
)

data class StudentExamMark(
    val id: String,
    val organizationId: String,
    val activeSessionId: String,
    val examId: String,
    val classId: String,
    val subjectId: String,
    val studentId: String,
    val obtainedMarks: Double?,
    val isAbsent: Boolean,
    val isMedicalLeave: Boolean,
    val teacherRemarks: String?,
    val studentName: String?,
    val rollNumber: Int?,
    val subjectName: String?,
    val examName: String?,
    val maxMarks: Double?,
    val minimumPassingMarks: Double?
)
