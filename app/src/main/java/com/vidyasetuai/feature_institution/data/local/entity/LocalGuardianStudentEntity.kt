package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_institution.domain.model.InstitutionStudent

@Entity(tableName = "local_guardian_students")
data class LocalGuardianStudentEntity(
    @PrimaryKey val id: String,
    val guardianLinkId: String,
    val name: String,
    val classId: String?,
    val className: String?,
    val totalFee: Double,
    val paidFee: Double,
    val pendingFee: Double
) {
    fun toDomain(): InstitutionStudent = InstitutionStudent(
        id = id,
        name = name,
        classId = classId,
        className = className,
        totalFee = totalFee,
        paidFee = paidFee,
        pendingFee = pendingFee
    )

    companion object {
        fun fromDomain(domain: InstitutionStudent, guardianLinkId: String): LocalGuardianStudentEntity = LocalGuardianStudentEntity(
            id = domain.id,
            guardianLinkId = guardianLinkId,
            name = domain.name,
            classId = domain.classId,
            className = domain.className,
            totalFee = domain.totalFee,
            paidFee = domain.paidFee,
            pendingFee = domain.pendingFee
        )
    }
}
