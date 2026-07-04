package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_calendar_events")
data class LocalCalendarEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_calendar_events.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "parent_organization_name")
    val parentOrganizationName: String?, // resolved from organization_parents.name
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "organization_name")
    val organizationName: String?, // resolved from organizations.name
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "name")
    val name: String, // event name like "Diwali Break" or "PTM 1"
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "start_date")
    val startDate: String, // YYYY-MM-DD
    
    @ColumnInfo(name = "end_date")
    val endDate: String, // YYYY-MM-DD
    
    @ColumnInfo(name = "event_type")
    val eventType: String, // Holiday, PTM, Exam, Event, Academic
    
    @ColumnInfo(name = "is_school_closed")
    val isSchoolClosed: Boolean,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_INSERT, PENDING_UPDATE
)
