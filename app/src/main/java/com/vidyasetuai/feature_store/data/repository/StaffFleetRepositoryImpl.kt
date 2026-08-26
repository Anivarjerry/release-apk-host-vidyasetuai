package com.vidyasetuai.feature_store.data.repository

import android.content.Context
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.dao.BusinessBranchDao
import com.vidyasetuai.feature_store.data.local.dao.BusinessStaffDao
import com.vidyasetuai.feature_store.data.local.dao.DeliveryRiderDao
import com.vidyasetuai.feature_store.data.local.dao.OrderDeliveryDao
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffEntity
import com.vidyasetuai.feature_store.data.local.entity.DeliveryRiderEntity
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import com.vidyasetuai.feature_store.domain.model.StaffMemberUiModel
import com.vidyasetuai.feature_store.domain.repository.StaffFleetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID

class StaffFleetRepositoryImpl(
    private val businessStaffDao: BusinessStaffDao,
    private val businessBranchDao: BusinessBranchDao,
    private val deliveryRiderDao: DeliveryRiderDao,
    private val orderDeliveryDao: OrderDeliveryDao,
    private val remoteDataSource: StoreRemoteDataSource = StoreRemoteDataSource()
) : StaffFleetRepository {

    constructor(context: Context) : this(
        StoreDatabase.getDatabase(context).businessStaffDao(),
        StoreDatabase.getDatabase(context).businessBranchDao(),
        StoreDatabase.getDatabase(context).deliveryRiderDao(),
        StoreDatabase.getDatabase(context).orderDeliveryDao()
    )

    override fun getStaffWithFleetFlow(businessId: String): Flow<List<StaffMemberUiModel>> {
        return combine(
            businessStaffDao.getStaffFlow(businessId),
            businessBranchDao.getBranchesFlow(businessId),
            deliveryRiderDao.getAllRidersFlow(),
            orderDeliveryDao.getUnsettledDeliveriesFlow()
        ) { staffList, branches, riders, unsettledDeliveries ->
            val branchMap = branches.associateBy { it.id }
            val riderByPhoneMap = riders.associateBy { it.phone }
            val cashByRiderIdMap = unsettledDeliveries
                .groupBy { it.riderId }
                .mapValues { (_, delivs) -> delivs.sumOf { it.cashCollectedByRider } }

            staffList.map { staff ->
                val branch = branchMap[staff.branchId]
                val rider = riderByPhoneMap[staff.phone]
                val codCash = if (rider != null) cashByRiderIdMap[rider.id] ?: 0.0 else 0.0

                StaffMemberUiModel(
                    staff = staff,
                    branchName = branch?.branchName ?: "Main Counter",
                    rider = rider,
                    unsettledCodCash = codCash
                )
            }
        }.flowOn(Dispatchers.Default)
    }

    override fun getBranchesFlow(businessId: String): Flow<List<BusinessBranchEntity>> {
        return businessBranchDao.getBranchesFlow(businessId).flowOn(Dispatchers.IO)
    }

    override suspend fun saveBranch(branch: BusinessBranchEntity): Result<BusinessBranchEntity> =
        withContext(Dispatchers.IO) {
            try {
                // 1. Remote Save to Supabase
                val remoteSaved = remoteDataSource.saveBranch(branch)
                val effective = remoteSaved ?: branch

                // 2. Local Room DB Upsert on remote success
                businessBranchDao.upsert(effective)
                if (effective.isMainBranch) {
                    businessBranchDao.setMainBranch(effective.businessId, effective.id)
                }

                Result.success(effective)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }

    override suspend fun saveStaffMember(
        staff: BusinessStaffEntity,
        vehicleType: String?,
        vehicleNumber: String?
    ): Result<BusinessStaffEntity> = withContext(Dispatchers.IO) {
        try {
            // 1. Save Staff to Supabase
            val remoteStaff = remoteDataSource.saveStaffMember(staff)
            val effectiveStaff = remoteStaff ?: staff
            businessStaffDao.upsert(effectiveStaff)

            // 2. If Delivery Rider, upsert DeliveryRiderEntity
            if (staff.role == "DELIVERY_RIDER") {
                val nowIso = Instant.now().toString()
                val riderEntity = DeliveryRiderEntity(
                    id = UUID.randomUUID().toString(),
                    businessId = staff.businessId,
                    userId = staff.userId ?: staff.id,
                    name = staff.name,
                    phone = staff.phone,
                    vehicleType = vehicleType ?: "BIKE",
                    vehicleNumber = vehicleNumber ?: "",
                    isOnline = true,
                    isActive = true,
                    isDeleted = false,
                    createdAt = nowIso,
                    updatedAt = nowIso
                )
                remoteDataSource.saveDeliveryRider(riderEntity)
                deliveryRiderDao.upsert(riderEntity)
            }

            Result.success(effectiveStaff)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun deleteStaffMember(staffId: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val nowIso = Instant.now().toString()
                remoteDataSource.deleteStaffMember(staffId)
                businessStaffDao.deleteStaffById(staffId, nowIso)
                Result.success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }

    override suspend fun toggleRiderDuty(
        businessId: String,
        riderId: String,
        isOnline: Boolean
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.toggleRiderDutyStatus(businessId, riderId, isOnline)
            val nowIso = Instant.now().toString()
            deliveryRiderDao.updateRiderDutyStatus(riderId, isOnline, nowIso)
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun settleRiderCash(
        businessId: String,
        riderId: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.settleRiderCash(businessId, riderId)
            val nowIso = Instant.now().toString()
            orderDeliveryDao.settleRiderDeliveries(riderId, nowIso)
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun syncAllStaffData(businessId: String) = withContext(Dispatchers.IO) {
        try {
            val branches = remoteDataSource.fetchBranches(businessId)
            if (branches.isNotEmpty()) businessBranchDao.upsertAll(branches)

            val staff = remoteDataSource.fetchStaffMembers(businessId)
            if (staff.isNotEmpty()) businessStaffDao.upsertAll(staff)

            val riders = remoteDataSource.fetchRiders(businessId)
            if (riders.isNotEmpty()) deliveryRiderDao.upsertAll(riders)

            val unsettled = remoteDataSource.fetchUnsettledDeliveries(businessId)
            if (unsettled.isNotEmpty()) orderDeliveryDao.upsertAll(unsettled)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun searchUserByUsername(
        username: String
    ): Result<com.vidyasetuai.feature_store.data.remote.dto.StaffLinkedUserDto?> = withContext(Dispatchers.IO) {
        try {
            val user = remoteDataSource.searchUserProfileByUsername(username)
            Result.success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
