package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_institution.domain.model.ChildOrg
import com.vidyasetuai.feature_institution.domain.model.OrgClass
import com.vidyasetuai.feature_institution.domain.model.OrgSection

// Supabase table: organization_classes (full schema)
@Entity(tableName = "local_org_classes")
data class LocalOrgClassEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val classId: String,            // FK to global_classes
    val customClassName: String? = null,
    val nextPromotionClassId: String? = null,
    val isActive: Boolean = true,
    val isDeleted: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = "",
    val createdBy: String? = null,
    val updatedBy: String? = null
) {
    fun toDomain(): OrgClass = OrgClass(id = id, name = customClassName ?: classId)
}

// Supabase table: organization_sections (full schema)
@Entity(tableName = "local_org_sections")
data class LocalOrgSectionEntity(
    @PrimaryKey val id: String,
    val organizationId: String,
    val classId: String,            // FK to organization_classes
    val name: String = "",
    val isActive: Boolean = true,
    val isDeleted: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = "",
    val createdBy: String? = null,
    val updatedBy: String? = null
) {
    fun toDomain(): OrgSection = OrgSection(id = id, name = name)
}

// Child organizations (custom — not a direct Supabase table)
@Entity(tableName = "local_child_orgs")
data class LocalChildOrgEntity(
    @PrimaryKey val id: String,
    val parentOrgId: String,
    val name: String
) {
    fun toDomain(): ChildOrg = ChildOrg(id = id, name = name)
}

