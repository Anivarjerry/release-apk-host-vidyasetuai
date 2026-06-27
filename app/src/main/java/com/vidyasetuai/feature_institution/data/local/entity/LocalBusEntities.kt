package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Supabase table: organization_parent_buses (full schema — replaces partial LocalDriverBusDetailsEntity)
// NOTE: LocalDriverBusDetailsEntity table name kept for backward compatibility via migration
@Entity(tableName = "local_parent_buses_full")
data class LocalParentBusFullEntity(
    @PrimaryKey val id: String,
    val parentOrganizationId: String,
    val activeSessionId: String,
    val busNumber: String,
    val busName: String?,
    val routeName: String?,
    val capacity: Int?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String?,
    val updatedBy: String?
)
