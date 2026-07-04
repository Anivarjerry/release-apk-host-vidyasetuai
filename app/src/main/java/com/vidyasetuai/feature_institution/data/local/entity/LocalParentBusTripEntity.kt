package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_parent_bus_trips")
data class LocalParentBusTripEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_parent_bus_trips.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "bus_id")
    val busId: String,
    
    @ColumnInfo(name = "bus_number")
    val busNumber: String?,
    
    @ColumnInfo(name = "bus_name")
    val busName: String?,
    
    @ColumnInfo(name = "driver_id")
    val driverId: String,
    
    @ColumnInfo(name = "driver_name")
    val driverName: String?,
    
    @ColumnInfo(name = "driver_phone")
    val driverPhone: String?,
    
    @ColumnInfo(name = "trip_type")
    val tripType: String, // Morning_Pickup, Evening_Drop, Special
    
    @ColumnInfo(name = "status")
    val status: String, // Scheduled, Ongoing, Completed, Cancelled
    
    @ColumnInfo(name = "start_time")
    val startTime: String?,
    
    @ColumnInfo(name = "end_time")
    val endTime: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_UPDATE
)
