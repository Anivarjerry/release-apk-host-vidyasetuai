package com.vidyasetuai.feature_institution.domain.model

data class PendingApproval(
    val id: String,
    val role: String,
    val targetName: String,
    val personName: String,
    val tableName: String
)
