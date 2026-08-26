package com.vidyasetuai.feature_store.domain.repository

import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessSettingsEntity
import com.vidyasetuai.feature_store.domain.model.StoreSettingsOverviewUiModel
import kotlinx.coroutines.flow.Flow

interface StoreSettingsRepository {
    /**
     * Reactively streams consolidated store settings combining Room tables on Dispatchers.Default.
     */
    fun getSettingsOverviewFlow(businessId: String): Flow<StoreSettingsOverviewUiModel>

    /**
     * Updates store identity & profile on Supabase and local Room DB.
     */
    suspend fun updateBusinessProfile(business: BusinessEntity): Result<BusinessEntity>

    /**
     * Saves or updates primary store outlet branch & address.
     */
    suspend fun saveMainBranch(branch: BusinessBranchEntity): Result<BusinessBranchEntity>

    /**
     * Saves billing, invoice prefix, thermal printer & extra fee settings.
     */
    suspend fun saveBillingSettings(settings: BusinessSettingsEntity): Result<BusinessSettingsEntity>

    /**
     * Updates bank account details and dynamic UPI ID for receipt QR codes.
     */
    suspend fun saveBankAndUpi(
        businessId: String,
        upiId: String?,
        bankName: String?,
        accountNo: String?,
        ifsc: String?,
        branch: String?
    ): Result<Unit>

    /**
     * Saves Indian GST and E-Way Bill NIC credentials.
     */
    suspend fun saveGstEwayConfig(
        businessId: String,
        gstin: String?,
        apiUsername: String?,
        apiPassword: String?,
        gspProvider: String,
        enableAutoEway: Boolean
    ): Result<Unit>
}
