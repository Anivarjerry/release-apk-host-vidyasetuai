package com.vidyasetuai.feature_store.domain.model

import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessSettingsEntity

/**
 * Domain model representing the consolidated store & billing settings.
 */
data class StoreSettingsOverviewUiModel(
    val business: BusinessEntity? = null,
    val mainBranch: BusinessBranchEntity? = null,
    val allBranches: List<BusinessBranchEntity> = emptyList(),
    val settings: BusinessSettingsEntity? = null
)
