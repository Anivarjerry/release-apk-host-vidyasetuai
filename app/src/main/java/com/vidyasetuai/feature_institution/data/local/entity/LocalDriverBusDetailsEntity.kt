package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_institution.domain.model.DriverBusDetails

@Entity(tableName = "local_driver_bus_details")
data class LocalDriverBusDetailsEntity(
    @PrimaryKey val workspaceId: String,
    val busId: String,
    val busNumber: String,
    val routeName: String?,
    val staffId: String,
    val parentOrgId: String,
    val activeSessionId: String
) {
    fun toDomain(): DriverBusDetails = DriverBusDetails(
        busId = busId,
        busNumber = busNumber,
        routeName = routeName,
        staffId = staffId,
        parentOrgId = parentOrgId,
        activeSessionId = activeSessionId
    )

    companion object {
        fun fromDomain(domain: DriverBusDetails, workspaceId: String): LocalDriverBusDetailsEntity = LocalDriverBusDetailsEntity(
            workspaceId = workspaceId,
            busId = domain.busId,
            busNumber = domain.busNumber,
            routeName = domain.routeName,
            staffId = domain.staffId,
            parentOrgId = domain.parentOrgId,
            activeSessionId = domain.activeSessionId
        )
    }
}
