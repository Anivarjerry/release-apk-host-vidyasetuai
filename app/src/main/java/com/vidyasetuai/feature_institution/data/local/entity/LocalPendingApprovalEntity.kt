package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_institution.domain.model.PendingApproval

@Entity(tableName = "local_pending_approvals")
data class LocalPendingApprovalEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val role: String,
    val targetName: String,
    val personName: String,
    val tableName: String
) {
    fun toDomain(): PendingApproval = PendingApproval(
        id = id,
        role = role,
        targetName = targetName,
        personName = personName,
        tableName = tableName
    )

    companion object {
        fun fromDomain(domain: PendingApproval, userId: String): LocalPendingApprovalEntity = LocalPendingApprovalEntity(
            id = domain.id,
            userId = userId,
            role = domain.role,
            targetName = domain.targetName,
            personName = domain.personName,
            tableName = domain.tableName
        )
    }
}
