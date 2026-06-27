package com.vidyasetuai.feature_institution.domain.model

data class InstitutionStudent(
    val id: String,
    val name: String,
    val classId: String?,
    val className: String?,
    val totalFee: Double,
    val paidFee: Double,
    val pendingFee: Double
)
