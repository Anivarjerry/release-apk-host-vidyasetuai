package com.vidyasetuai.feature_store.domain.repository

import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffEntity
import com.vidyasetuai.feature_store.data.local.entity.DeliveryRiderEntity
import com.vidyasetuai.feature_store.domain.model.StaffMemberUiModel
import kotlinx.coroutines.flow.Flow

interface StaffFleetRepository {
    fun getStaffWithFleetFlow(businessId: String): Flow<List<StaffMemberUiModel>>
    fun getBranchesFlow(businessId: String): Flow<List<BusinessBranchEntity>>
    suspend fun saveBranch(branch: BusinessBranchEntity): Result<BusinessBranchEntity>
    suspend fun saveStaffMember(
        staff: BusinessStaffEntity,
        vehicleType: String?,
        vehicleNumber: String?
    ): Result<BusinessStaffEntity>
    suspend fun deleteStaffMember(staffId: String): Result<Boolean>
    suspend fun toggleRiderDuty(businessId: String, riderId: String, isOnline: Boolean): Result<Boolean>
    suspend fun settleRiderCash(businessId: String, riderId: String): Result<Boolean>
    suspend fun searchUserByUsername(username: String): Result<com.vidyasetuai.feature_store.data.remote.dto.StaffLinkedUserDto?>
    suspend fun syncAllStaffData(businessId: String)
}
