package com.vidyasetuai.feature_store.presentation.screen.role_owner.reports

import com.vidyasetuai.feature_store.domain.model.*

/**
 * UI State for Business Reports & P&L Screen.
 */
data class BusinessReportsUiState(
    val selectedDatePreset: DatePreset = DatePreset.THIS_MONTH,
    val startDate: String = "",
    val endDate: String = "",
    val activeTab: ReportTab = ReportTab.DAYBOOK,
    val daybook: DaybookUiModel = DaybookUiModel(),
    val profitLoss: ProfitLossUiModel = ProfitLossUiModel(),
    val gstr1: Gstr1UiModel = Gstr1UiModel(),
    val gstr3b: Gstr3bUiModel = Gstr3bUiModel(),
    val isCustomDatePickerOpen: Boolean = false,
    val isLoading: Boolean = false,
    val isSyncingRemote: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
