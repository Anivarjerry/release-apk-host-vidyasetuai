package com.vidyasetuai.feature_store.domain.repository

import com.vidyasetuai.feature_store.data.local.entity.ItemEntity
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity
import com.vidyasetuai.feature_store.domain.model.PurchaseBillUiModel
import com.vidyasetuai.feature_store.domain.model.RecordPurchaseBillPayload
import kotlinx.coroutines.flow.Flow

/**
 * Domain Repository Interface for Supplier Purchase Bills & Auto-Stock In.
 */
interface PurchaseBillsRepository {

    /**
     * 0ms Reactive Room Flow returning all purchase bills with supplier party information.
     */
    fun getPurchaseBillsFlow(businessId: String): Flow<List<PurchaseBillUiModel>>

    /**
     * Flow of active suppliers for the purchase creation dropdown.
     */
    fun getSuppliersFlow(businessId: String): Flow<List<PartyEntity>>

    /**
     * Flow of active store catalog items for quick line item selection.
     */
    fun getCatalogItemsFlow(businessId: String): Flow<List<ItemEntity>>

    /**
     * Records a new purchase bill, updates Room DB and executes atomic RPC on Supabase.
     */
    suspend fun recordPurchaseBill(
        businessId: String,
        branchId: String,
        payload: RecordPurchaseBillPayload
    ): Result<Boolean>
}
