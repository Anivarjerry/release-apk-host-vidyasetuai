package com.vidyasetuai.feature_store.presentation.screen.role_owner.purchases

import com.vidyasetuai.feature_store.data.local.entity.ItemEntity
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity
import com.vidyasetuai.feature_store.domain.model.PurchaseBillUiModel
import com.vidyasetuai.feature_store.domain.model.PurchaseFilterStatus
import com.vidyasetuai.feature_store.domain.model.PurchaseSummaryUiModel

/**
 * UI State for Supplier Purchase Bills Screen.
 */
data class PurchaseBillsUiState(
    val purchases: List<PurchaseBillUiModel> = emptyList(),
    val filteredPurchases: List<PurchaseBillUiModel> = emptyList(),
    val searchQuery: String = "",
    val selectedStatusFilter: PurchaseFilterStatus = PurchaseFilterStatus.ALL,
    val summary: PurchaseSummaryUiModel = PurchaseSummaryUiModel(),
    val suppliers: List<PartyEntity> = emptyList(),
    val catalogItems: List<ItemEntity> = emptyList(),
    val isAddDrawerOpen: Boolean = false,
    val selectedPurchaseForDetail: PurchaseBillUiModel? = null,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
