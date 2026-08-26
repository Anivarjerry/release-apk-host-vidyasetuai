package com.vidyasetuai.feature_store.presentation.screen.role_staff.inventory

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.repository.StoreCatalogRepositoryImpl
import com.vidyasetuai.feature_store.domain.model.ItemWithStockUiModel
import com.vidyasetuai.feature_store.domain.repository.StoreCatalogRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InventoryStockViewModel(
    context: Context,
    private val repository: StoreCatalogRepository = StoreCatalogRepositoryImpl(context)
) : ViewModel() {

    private val storeDb = StoreDatabase.getDatabase(context)

    private val _uiState = MutableStateFlow(InventoryStockUiState())
    val uiState: StateFlow<InventoryStockUiState> = _uiState.asStateFlow()

    private var itemsJob: Job? = null
    private var timelineJob: Job? = null

    init {
        observeActiveBusiness()
    }

    private fun observeActiveBusiness() {
        viewModelScope.launch {
            storeDb.businessDao().getAnyActiveBusinessFlow().collect { activeBusiness ->
                val businessId = activeBusiness?.id ?: ""
                _uiState.update { it.copy(businessId = businessId) }

                if (businessId.isNotEmpty()) {
                    observeBranch(businessId)
                    observeInventoryFlows(businessId)
                }
            }
        }
    }

    private fun observeBranch(businessId: String) {
        viewModelScope.launch {
            storeDb.businessBranchDao().getBranchesFlow(businessId).collect { branches ->
                val branchId = branches.firstOrNull()?.id
                _uiState.update { it.copy(branchId = branchId) }
            }
        }
    }

    private fun observeInventoryFlows(businessId: String) {
        itemsJob?.cancel()
        itemsJob = viewModelScope.launch {
            // 1. Reactive Flow of Items with Current Physical Stock
            repository.getItemsWithStockFlow(businessId).collect { itemsList ->
                _uiState.update { it.copy(items = itemsList, isLoading = false) }
            }
        }

        viewModelScope.launch {
            // 2. Reactive Categories Flow
            repository.getCategoriesFlow(businessId).collect { catList ->
                _uiState.update { it.copy(categories = catList) }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSelectedCategoryId(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun setStockStatusFilter(filter: StockStatusFilter) {
        _uiState.update { it.copy(stockStatusFilter = filter) }
    }

    fun openAdjustmentModal(item: ItemWithStockUiModel) {
        _uiState.update {
            it.copy(
                isAdjustmentModalOpen = true,
                selectedAdjustmentItem = item
            )
        }
    }

    fun closeAdjustmentModal() {
        _uiState.update {
            it.copy(
                isAdjustmentModalOpen = false,
                selectedAdjustmentItem = null
            )
        }
    }

    fun submitStockAdjustment(
        itemId: String,
        adjustedStock: Double,
        reason: String,
        notes: String?
    ) {
        val businessId = _uiState.value.businessId
        val branchId = _uiState.value.branchId

        if (businessId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Business workspace not found.") }
            return
        }

        val targetItem = _uiState.value.selectedAdjustmentItem
        _uiState.update { it.copy(isSubmittingAdjustment = true) }

        viewModelScope.launch {
            val result = repository.adjustStock(
                businessId = businessId,
                branchId = branchId,
                itemId = itemId,
                adjustedStock = adjustedStock,
                reason = reason,
                notes = notes
            )

            result.fold(
                onSuccess = {
                    val unit = targetItem?.item?.unit ?: "Units"
                    val name = targetItem?.item?.name ?: "Product"
                    _uiState.update {
                        it.copy(
                            isSubmittingAdjustment = false,
                            isAdjustmentModalOpen = false,
                            selectedAdjustmentItem = null,
                            successMessage = "Stock for '$name' updated to $adjustedStock $unit"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSubmittingAdjustment = false,
                            errorMessage = error.message ?: "Failed to adjust stock."
                        )
                    }
                }
            )
        }
    }

    fun openTimeline(item: ItemWithStockUiModel) {
        _uiState.update {
            it.copy(
                isTimelineModalOpen = true,
                selectedTimelineItem = item,
                isTimelineLoading = true
            )
        }

        timelineJob?.cancel()
        timelineJob = viewModelScope.launch {
            // 1. Observe local Room DB flow
            repository.getItemTimelineFlow(item.item.id).collect { history ->
                _uiState.update {
                    it.copy(
                        itemTimelineHistory = history,
                        isTimelineLoading = false
                    )
                }
            }
        }

        // 2. Fetch fresh timeline from Supabase in background
        viewModelScope.launch {
            repository.syncItemTimeline(item.item.id)
        }
    }

    fun closeTimeline() {
        timelineJob?.cancel()
        _uiState.update {
            it.copy(
                isTimelineModalOpen = false,
                selectedTimelineItem = null,
                itemTimelineHistory = emptyList(),
                isTimelineLoading = false
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
