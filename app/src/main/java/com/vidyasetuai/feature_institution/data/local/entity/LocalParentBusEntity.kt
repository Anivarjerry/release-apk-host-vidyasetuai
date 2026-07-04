package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_parent_buses")
data class LocalParentBusEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_parent_buses.id
    
    @ColumnInfo(name = "parent_organization_id")
    val parentOrganizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "bus_number")
    val busNumber: String,
    
    @ColumnInfo(name = "bus_name")
    val busName: String?,
    
    @ColumnInfo(name = "route_name")
    val routeName: String?,
    
    @ColumnInfo(name = "max_capacity")
    val maxCapacity: Int?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Insurance Details & Cache
    @ColumnInfo(name = "insurance_expiry_date")
    val insuranceExpiryDate: String?,
    @ColumnInfo(name = "insurance_image_url")
    val insuranceImageUrl: String?,
    @ColumnInfo(name = "insurance_image_local_path")
    val insuranceImageLocalPath: String?,

    // Fitness Details & Cache
    @ColumnInfo(name = "fitness_expiry_date")
    val fitnessExpiryDate: String?,
    @ColumnInfo(name = "fitness_image_url")
    val fitnessImageUrl: String?,
    @ColumnInfo(name = "fitness_image_local_path")
    val fitnessImageLocalPath: String?,

    // Pollution Details & Cache
    @ColumnInfo(name = "pollution_expiry_date")
    val pollutionExpiryDate: String?,
    @ColumnInfo(name = "pollution_image_url")
    val pollutionImageUrl: String?,
    @ColumnInfo(name = "pollution_image_local_path")
    val pollutionImageLocalPath: String?,

    // Resolved Driver Details
    @ColumnInfo(name = "driver_id")
    val driverId: String?,
    @ColumnInfo(name = "driver_name")
    val driverName: String?,
    @ColumnInfo(name = "driver_mobile")
    val driverMobile: String?,

    // Resolved Conductor Details
    @ColumnInfo(name = "conductor_id")
    val conductorId: String?,
    @ColumnInfo(name = "conductor_name")
    val conductorName: String?,
    @ColumnInfo(name = "conductor_mobile")
    val conductorMobile: String?,

    // Last Location Tracking Cache
    @ColumnInfo(name = "last_latitude")
    val lastLatitude: Double?,
    @ColumnInfo(name = "last_longitude")
    val lastLongitude: Double?,
    @ColumnInfo(name = "last_location_updated_at")
    val lastLocationUpdatedAt: Long?,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String
)
