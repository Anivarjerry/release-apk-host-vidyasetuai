package com.vidyasetuai.feature_store.presentation.screen.role_staff.catalog

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.entity.ItemEntity
import com.vidyasetuai.feature_store.data.repository.StoreCatalogRepositoryImpl
import com.vidyasetuai.feature_store.domain.model.ItemWithStockUiModel
import com.vidyasetuai.feature_store.domain.repository.StoreCatalogRepository
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class ProductsCatalogViewModel(
    private val context: Context,
    private val repository: StoreCatalogRepository = StoreCatalogRepositoryImpl(context),
    private val remoteDataSource: StoreRemoteDataSource = StoreRemoteDataSource()
) : ViewModel() {

    private val storeDb = StoreDatabase.getDatabase(context)

    private val _uiState = MutableStateFlow(ProductsCatalogUiState())
    val uiState: StateFlow<ProductsCatalogUiState> = _uiState.asStateFlow()

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
                    observeCatalogData(businessId)
                }
            }
        }
    }

    private fun observeBranch(businessId: String) {
        viewModelScope.launch {
            storeDb.businessBranchDao().getBranchesFlow(businessId).collect { branchList ->
                val mainBranch = branchList.firstOrNull { it.isMainBranch } ?: branchList.firstOrNull()
                _uiState.update { it.copy(branchId = mainBranch?.id, branches = branchList) }
            }
        }

        viewModelScope.launch {
            storeDb.inventoryStockDao().getAllStocksFlow().collect { allStocks ->
                val sMap = allStocks.associate { "${it.branchId}_${it.itemId}" to it.currentStock }
                val activeBranchStocks = allStocks
                    .filter { it.branchId == (_uiState.value.branchId ?: "") }
                    .associate { it.itemId to it.currentStock }
                _uiState.update { it.copy(branchStockMap = activeBranchStocks) }
            }
        }
    }

    private fun observeCatalogData(businessId: String) {
        viewModelScope.launch {
            repository.getCategoriesFlow(businessId).collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }

        viewModelScope.launch {
            repository.getItemsWithStockFlow(businessId).collect { itemsWithStock ->
                _uiState.update { it.copy(items = itemsWithStock, isLoading = false) }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectCategory(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun selectFoodType(foodType: String) {
        _uiState.update { it.copy(selectedFoodType = foodType) }
    }

    fun openAddProduct() {
        _uiState.update { it.copy(isAddEditOpen = true, editingItem = null) }
    }

    fun openEditProduct(itemWithStock: ItemWithStockUiModel) {
        _uiState.update { it.copy(isAddEditOpen = true, editingItem = itemWithStock) }
    }

    fun closeAddEdit() {
        _uiState.update { it.copy(isAddEditOpen = false, editingItem = null) }
    }

    fun openCategoryModal() {
        _uiState.update { it.copy(isCategoryModalOpen = true) }
    }

    fun closeCategoryModal() {
        _uiState.update { it.copy(isCategoryModalOpen = false) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun generateSku(name: String): String {
        val clean = name.trim().replace(Regex("[^a-zA-Z0-9]"), "").uppercase()
        val prefix = if (clean.length >= 3) clean.take(3) else (clean + "XXX").take(3)
        val count = (_uiState.value.items.count { it.item.sku?.startsWith(prefix) == true } + 1)
        return "$prefix-${"%02d".format(count)}"
    }

    fun saveProduct(
        name: String,
        categoryId: String?,
        itemType: String = "PRODUCT",
        description: String? = null,
        sku: String? = null,
        barcode: String? = null,
        hsnSacCode: String? = null,
        salePrice: Double,
        mrp: Double? = null,
        purchasePrice: Double = 0.0,
        taxRate: Double = 5.0,
        isTaxInclusive: Boolean = true,
        unit: String = "PCS",
        foodType: String = "VEG",
        isAvailableOnline: Boolean = true,
        imageUrl: String? = null,
        extraImages: String? = "[]",
        initialStock: Double = 0.0,
        lowStockThreshold: Double = 5.0
    ) {
        val businessId = _uiState.value.businessId
        if (businessId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Business workspace not found.") }
            return
        }

        if (name.trim().isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter product name.") }
            return
        }

        // Duplicate Barcode Collision Check (Local Room Cache)
        val trimmedBarcode = barcode?.trim()?.ifBlank { null }
        val editing = _uiState.value.editingItem
        if (trimmedBarcode != null) {
            val duplicate = _uiState.value.items.find { 
                it.item.barcode.equals(trimmedBarcode, ignoreCase = true) && it.item.id != editing?.item?.id 
            }
            if (duplicate != null) {
                _uiState.update { it.copy(errorMessage = "Barcode '$trimmedBarcode' is already assigned to '${duplicate.item.name}'") }
                return
            }
        }

        val nowIso = Instant.now().toString()
        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            if (editing == null) {
                // 1. Create New Product / Service
                val newItem = ItemEntity(
                    id = UUID.randomUUID().toString(),
                    businessId = businessId,
                    categoryId = categoryId?.ifBlank { null },
                    itemType = itemType,
                    name = name.trim(),
                    description = description?.trim()?.ifBlank { null },
                    sku = sku?.trim()?.ifBlank { null },
                    barcode = trimmedBarcode,
                    hsnSacCode = hsnSacCode?.trim()?.ifBlank { null },
                    taxRate = taxRate,
                    isTaxInclusive = isTaxInclusive,
                    purchasePrice = purchasePrice,
                    salePrice = salePrice,
                    mrp = mrp,
                    unit = unit,
                    foodType = foodType,
                    isAvailableOnline = isAvailableOnline,
                    imageUrl = imageUrl?.trim()?.ifBlank { null },
                    extraImages = extraImages ?: "[]",
                    isActive = true,
                    isDeleted = false,
                    createdAt = nowIso,
                    updatedAt = nowIso
                )

                val effectiveStock = if (itemType == "PRODUCT") initialStock else 0.0
                val result = repository.createItemWithStock(
                    businessId = businessId,
                    branchId = _uiState.value.branchId,
                    item = newItem,
                    initialStock = effectiveStock,
                    lowStockThreshold = lowStockThreshold
                )

                result.fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                isAddEditOpen = false,
                                successMessage = if (itemType == "PRODUCT") "Product saved successfully!" else "Service saved successfully!"
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                errorMessage = error.message ?: "Failed to save item."
                            )
                        }
                    }
                )
            } else {
                // 2. Update Existing Product / Service
                val updatedItem = editing.item.copy(
                    name = name.trim(),
                    categoryId = categoryId?.ifBlank { null },
                    itemType = itemType,
                    description = description?.trim()?.ifBlank { null },
                    sku = sku?.trim()?.ifBlank { null },
                    barcode = trimmedBarcode,
                    hsnSacCode = hsnSacCode?.trim()?.ifBlank { null },
                    taxRate = taxRate,
                    isTaxInclusive = isTaxInclusive,
                    purchasePrice = purchasePrice,
                    salePrice = salePrice,
                    mrp = mrp,
                    unit = unit,
                    foodType = foodType,
                    isAvailableOnline = isAvailableOnline,
                    imageUrl = imageUrl?.trim()?.ifBlank { null },
                    extraImages = extraImages ?: "[]",
                    updatedAt = nowIso
                )

                val result = repository.updateItem(updatedItem)
                result.fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                isAddEditOpen = false,
                                editingItem = null,
                                successMessage = "Updated successfully!"
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                errorMessage = error.message ?: "Failed to update item."
                            )
                        }
                    }
                )
            }
        }
    }

    fun deleteProduct(itemId: String) {
        viewModelScope.launch {
            val result = repository.deleteItem(itemId)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(successMessage = "Product deleted successfully!") }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Failed to delete product.") }
                }
            )
        }
    }

    fun createCategory(name: String) {
        val businessId = _uiState.value.businessId
        if (name.trim().isBlank() || businessId.isBlank()) return

        // Duplicate Name Check
        val alreadyExists = _uiState.value.categories.any { it.name.trim().equals(name.trim(), ignoreCase = true) }
        if (alreadyExists) {
            _uiState.update { it.copy(errorMessage = "Category '$name' already exists.") }
            return
        }

        viewModelScope.launch {
            val result = repository.createCategory(businessId, name.trim())
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(successMessage = "Category created successfully!") }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Failed to create category.") }
                }
            )
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            val result = repository.deleteCategory(categoryId)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(successMessage = "Category deleted.") }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Failed to delete category.") }
                }
            )
        }
    }

    // =========================================================================
    // 🚚 MULTI-BRANCH STOCK TRANSFER
    // =========================================================================

    fun openStockTransfer() {
        _uiState.update { it.copy(isStockTransferOpen = true, errorMessage = null) }
    }

    fun closeStockTransfer() {
        _uiState.update { it.copy(isStockTransferOpen = false) }
    }

    fun executeStockTransfer(
        fromBranchId: String,
        toBranchId: String,
        itemId: String,
        quantity: Double,
        notes: String?
    ) {
        val businessId = _uiState.value.businessId
        if (businessId.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isTransferringStock = true) }

            // 1. Validation: Check available source stock
            val sourceStock = storeDb.inventoryStockDao().getStockDirect(itemId, fromBranchId)
            val currentSrcQty = sourceStock?.currentStock ?: 0.0
            if (currentSrcQty < quantity) {
                _uiState.update {
                    it.copy(
                        isTransferringStock = false,
                        errorMessage = "स्रोत शाखा पर केवल $currentSrcQty उपलब्ध है।"
                    )
                }
                return@launch
            }

            // 2. Call Remote Supabase RPC first
            val ok = remoteDataSource.transferBranchStock(
                businessId = businessId,
                fromBranchId = fromBranchId,
                toBranchId = toBranchId,
                itemId = itemId,
                quantity = quantity,
                notes = notes
            )

            if (ok) {
                // 3. Update Source Stock in Room DB on success
                if (sourceStock != null) {
                    storeDb.inventoryStockDao().upsert(sourceStock.copy(currentStock = currentSrcQty - quantity))
                }

                // 4. Update Destination Stock in Room DB on success
                val destStock = storeDb.inventoryStockDao().getStockDirect(itemId, toBranchId)
                if (destStock != null) {
                    storeDb.inventoryStockDao().upsert(destStock.copy(currentStock = destStock.currentStock + quantity))
                } else {
                    val nowIso = java.time.Instant.now().toString()
                    storeDb.inventoryStockDao().upsert(
                        com.vidyasetuai.feature_store.data.local.entity.InventoryStockEntity(
                            id = java.util.UUID.randomUUID().toString(),
                            itemId = itemId,
                            branchId = toBranchId,
                            currentStock = quantity,
                            lowStockThreshold = 5.0,
                            isActive = true,
                            isDeleted = false,
                            createdAt = nowIso,
                            updatedAt = nowIso
                        )
                    )
                }

                _uiState.update {
                    it.copy(
                        isTransferringStock = false,
                        isStockTransferOpen = false,
                        successMessage = "$quantity ट्रांसफर सफलतापूर्वक संपन्न हुआ!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isTransferringStock = false,
                        errorMessage = "स्टॉक ट्रांसफर क्लाउड पर असफल रहा। कृपया इंटरनेट कनेक्शन जांचें।"
                    )
                }
            }
        }
    }
}
