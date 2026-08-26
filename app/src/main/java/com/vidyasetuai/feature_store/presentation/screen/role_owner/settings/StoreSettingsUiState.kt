package com.vidyasetuai.feature_store.presentation.screen.role_owner.settings

import com.vidyasetuai.feature_store.domain.model.StoreSettingsOverviewUiModel

/**
 * UI State for Store & Billing Settings Screen.
 */
data class StoreSettingsUiState(
    val overview: StoreSettingsOverviewUiModel = StoreSettingsOverviewUiModel(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,

    // Bottom Sheet Visibility States
    val isBranchSheetOpen: Boolean = false,
    val isBillingSheetOpen: Boolean = false,
    val isBankUpiSheetOpen: Boolean = false,
    val isFeatureSwitchesSheetOpen: Boolean = false,
    val isGstEwaySheetOpen: Boolean = false,
    val isQrStandeeModalOpen: Boolean = false
)
