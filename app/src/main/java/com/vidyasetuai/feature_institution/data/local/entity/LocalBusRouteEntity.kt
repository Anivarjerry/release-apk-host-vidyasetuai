package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_bus_routes")
data class LocalBusRouteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_bus_routes.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "bus_id")
    val busId: String,
    
    @ColumnInfo(name = "stop_name")
    val stopName: String,
    
    @ColumnInfo(name = "stop_order")
    val stopOrder: Int,
    
    @ColumnInfo(name = "latitude")
    val latitude: Double?,
    
    @ColumnInfo(name = "longitude")
    val longitude: Double?,
    
    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean
)
