package com.vidyasetuai.feature_store.domain.model

import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffEntity
import com.vidyasetuai.feature_store.data.local.entity.DeliveryRiderEntity

data class StaffMemberUiModel(
    val staff: BusinessStaffEntity,
    val branchName: String,
    val rider: DeliveryRiderEntity? = null,
    val unsettledCodCash: Double = 0.0
) {
    val id: String get() = staff.id
    val name: String get() = staff.name
    val phone: String get() = staff.phone
    val role: String get() = staff.role
    val isActive: Boolean get() = staff.isActive
    val isOnline: Boolean get() = rider?.isOnline ?: true
    val vehicleType: String? get() = rider?.vehicleType
    val vehicleNumber: String? get() = rider?.vehicleNumber
    val riderId: String? get() = rider?.id
}
