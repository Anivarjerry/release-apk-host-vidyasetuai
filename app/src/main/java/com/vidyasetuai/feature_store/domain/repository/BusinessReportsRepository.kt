package com.vidyasetuai.feature_store.domain.repository

import com.vidyasetuai.feature_store.domain.model.BusinessReportsPayloadUiModel
import kotlinx.coroutines.flow.Flow

/**
 * Domain Repository Interface for Business Reports & P&L Module.
 */
interface BusinessReportsRepository {

    /**
     * 0ms Reactive Room Flow returning aggregated business reports based on date range.
     */
    fun getLocalReportsFlow(
        businessId: String,
        startDate: String,
        endDate: String
    ): Flow<BusinessReportsPayloadUiModel>

    /**
     * Fetches audited business financial reports from Supabase RPC fn_get_business_financial_reports.
     */
    suspend fun fetchRemoteReports(
        businessId: String,
        startDate: String,
        endDate: String
    ): Result<BusinessReportsPayloadUiModel>
}
