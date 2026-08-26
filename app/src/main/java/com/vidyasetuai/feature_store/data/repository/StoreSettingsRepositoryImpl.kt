package com.vidyasetuai.feature_store.data.repository

import android.content.Context
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessSettingsEntity
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import com.vidyasetuai.feature_store.domain.model.StoreSettingsOverviewUiModel
import com.vidyasetuai.feature_store.domain.repository.StoreSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID

class StoreSettingsRepositoryImpl(
    context: Context,
    private val database: StoreDatabase = StoreDatabase.getDatabase(context),
    private val remoteDataSource: StoreRemoteDataSource = StoreRemoteDataSource()
) : StoreSettingsRepository {

    private val businessDao = database.businessDao()
    private val settingsDao = database.businessSettingsDao()
    private val branchDao = database.businessBranchDao()

    override fun getSettingsOverviewFlow(businessId: String): Flow<StoreSettingsOverviewUiModel> {
        val bizFlow = if (businessId.isBlank()) {
            businessDao.getAnyActiveBusinessFlow()
        } else {
            businessDao.getBusinessFlow(businessId)
        }

        val settingsFlow = settingsDao.getSettingsFlow(businessId)
        val branchesFlow = branchDao.getBranchesFlow(businessId)

        return combine(bizFlow, settingsFlow, branchesFlow) { biz, settings, branches ->
            val mainBranch = branches.firstOrNull { it.isMainBranch } ?: branches.firstOrNull()
            StoreSettingsOverviewUiModel(
                business = biz,
                mainBranch = mainBranch,
                allBranches = branches,
                settings = settings
            )
        }.flowOn(Dispatchers.Default)
    }

    override suspend fun updateBusinessProfile(business: BusinessEntity): Result<BusinessEntity> = withContext(Dispatchers.IO) {
        try {
            val remoteBiz = remoteDataSource.updateBusiness(business)
            val effective = remoteBiz ?: business
            businessDao.upsert(effective)
            Result.success(effective)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveMainBranch(branch: BusinessBranchEntity): Result<BusinessBranchEntity> = withContext(Dispatchers.IO) {
        try {
            val nowIso = Instant.now().toString()
            val branchToSave = if (branch.id.isBlank()) {
                branch.copy(id = UUID.randomUUID().toString(), createdAt = nowIso, updatedAt = nowIso)
            } else {
                branch.copy(updatedAt = nowIso)
            }

            val remoteBranch = remoteDataSource.saveBranch(branchToSave)
            val effective = remoteBranch ?: branchToSave
            branchDao.upsert(effective)
            Result.success(effective)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveBillingSettings(settings: BusinessSettingsEntity): Result<BusinessSettingsEntity> = withContext(Dispatchers.IO) {
        try {
            val nowIso = Instant.now().toString()
            val settingsToSave = if (settings.id.isBlank()) {
                settings.copy(id = UUID.randomUUID().toString(), createdAt = nowIso, updatedAt = nowIso)
            } else {
                settings.copy(updatedAt = nowIso)
            }

            val remoteSettings = remoteDataSource.saveBusinessSettings(settingsToSave)
            val effective = remoteSettings ?: settingsToSave
            settingsDao.upsert(effective)
            Result.success(effective)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveBankAndUpi(
        businessId: String,
        upiId: String?,
        bankName: String?,
        accountNo: String?,
        ifsc: String?,
        branch: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.updateBankAndUpi(businessId, upiId, bankName, accountNo, ifsc, branch)
            // Update local Room database
            val existing = businessDao.getBusinessFlow(businessId)
            // Retrieve current and update fields
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveGstEwayConfig(
        businessId: String,
        gstin: String?,
        apiUsername: String?,
        apiPassword: String?,
        gspProvider: String,
        enableAutoEway: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.updateGstEwaySettings(businessId, gstin, apiUsername, apiPassword, gspProvider, enableAutoEway)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
