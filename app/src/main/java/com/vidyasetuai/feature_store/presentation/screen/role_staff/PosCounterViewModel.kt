package com.vidyasetuai.feature_store.presentation.screen.role_staff

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessEntity
import com.vidyasetuai.feature_store.data.local.entity.ItemCategoryEntity
import com.vidyasetuai.feature_store.data.local.entity.ItemEntity
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import com.vidyasetuai.feature_store.data.remote.dto.PosCartItemDto
import com.vidyasetuai.feature_store.data.remote.dto.PosCustomerPartyDto
import com.vidyasetuai.feature_store.data.remote.dto.PosInvoiceResponseDto
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PosCartLine(
    val item: ItemEntity,
    val quantity: Double = 1.0,
    val discount: Double = 0.0
) {
    val subtotal: Double get() = quantity * item.salePrice
    val taxable: Double get() = if (item.taxRate > 0) subtotal / (1.0 + (item.taxRate / 100.0)) else subtotal
    val taxAmount: Double get() = subtotal - taxable
    val total: Double get() = subtotal - discount
}

data class PosCounterUiState(
    val activeBusiness: BusinessEntity? = null,
    val branches: List<BusinessBranchEntity> = emptyList(),
    val activeBranchId: String? = null,
    val activeBranchName: String = "Main Branch",
    val categories: List<ItemCategoryEntity> = emptyList(),
    val selectedCategoryId: String = "ALL",
    val items: List<ItemEntity> = emptyList(),
    val searchQuery: String = "",
    val cart: Map<String, PosCartLine> = emptyMap(), // itemId -> PosCartLine
    val customerParties: List<PosCustomerPartyDto> = emptyList(),
    val selectedParty: PosCustomerPartyDto? = null,
    val customerName: String = "Counter Customer",
    val customerPhone: String = "9999999999",
    val paymentMethod: String = "CASH_ON_COUNTER", // "CASH_ON_COUNTER", "UPI_QR", "CREDIT_KHATA"
    val discountAmount: Double = 0.0,
    val deliveryCharge: Double = 0.0,
    val packingCharge: Double = 0.0,
    val orderNotes: String = "",
    val tenderedCash: Double = 0.0,
    val sendToKitchen: Boolean = false,
    val upiId: String = "shop@upi",
    val isCheckingOut: Boolean = false,
    val checkoutSuccessInvoice: PosInvoiceResponseDto? = null,
    val errorMessage: String? = null,
    val stockMap: Map<String, Double> = emptyMap(), // itemId -> available stock
    val lowStockThresholdMap: Map<String, Double> = emptyMap(), // itemId -> low_stock_threshold
    val allowNegativeStock: Boolean = true
) {
    val cartItemsList: List<PosCartLine> get() = cart.values.toList()
    val totalItemsCount: Int get() = cart.values.sumOf { it.quantity.toInt() }
    val itemsSubtotal: Double get() = cart.values.sumOf { it.subtotal }
    val totalTaxAmount: Double get() = cart.values.sumOf { it.taxAmount }
    val grandTotal: Double get() = (itemsSubtotal + deliveryCharge + packingCharge - discountAmount).coerceAtLeast(0.0)
    val rawChangeDue: Double get() = tenderedCash - grandTotal
    val changeDue: Double get() = (tenderedCash - grandTotal).coerceAtLeast(0.0)
    val isCashShort: Boolean get() = tenderedCash > 0.0 && rawChangeDue < 0.0
    val cashShortageAmount: Double get() = (grandTotal - tenderedCash).coerceAtLeast(0.0)
}

class PosCounterViewModel(
    private val context: Context,
    private val remoteDataSource: StoreRemoteDataSource = StoreRemoteDataSource()
) : ViewModel() {

    private val storeDb = StoreDatabase.getDatabase(context)
    private val _uiState = MutableStateFlow(PosCounterUiState())
    val uiState: StateFlow<PosCounterUiState> = _uiState.asStateFlow()

    private var stockJob: kotlinx.coroutines.Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // 1. Observe Active Business
            storeDb.businessDao().getAnyActiveBusinessFlow().collectLatest { business ->
                _uiState.update { it.copy(activeBusiness = business) }
                if (business != null) {
                    loadBranches(business.id)
                    loadCategoriesAndItems(business.id)
                    loadSettings(business.id)
                    fetchRemoteParties(business.id)
                }
            }
        }
    }

    private fun loadSettings(businessId: String) {
        viewModelScope.launch {
            storeDb.businessSettingsDao().getSettingsFlow(businessId).collectLatest { settings ->
                _uiState.update { it.copy(allowNegativeStock = settings?.allowNegativeStock ?: true) }
            }
        }
    }

    private fun loadBranches(businessId: String) {
        viewModelScope.launch {
            storeDb.businessBranchDao().getBranchesFlow(businessId).collectLatest { branchList ->
                val mainBranch = branchList.firstOrNull { it.isMainBranch } ?: branchList.firstOrNull()
                val activeId = _uiState.value.activeBranchId ?: mainBranch?.id
                _uiState.update { current ->
                    current.copy(
                        branches = branchList,
                        activeBranchId = activeId,
                        activeBranchName = branchList.firstOrNull { it.id == activeId }?.branchName ?: "Main Branch"
                    )
                }
                if (!activeId.isNullOrBlank()) {
                    observeBranchStocks(activeId)
                }
            }
        }
    }

    private fun observeBranchStocks(branchId: String) {
        stockJob?.cancel()
        stockJob = viewModelScope.launch {
            storeDb.inventoryStockDao().getStocksForBranchFlow(branchId).collectLatest { stocks ->
                val sMap = stocks.associate { it.itemId to it.currentStock }
                val tMap = stocks.associate { it.itemId to it.lowStockThreshold }
                _uiState.update {
                    it.copy(
                        stockMap = sMap,
                        lowStockThresholdMap = tMap
                    )
                }
            }
        }
    }

    private fun loadCategoriesAndItems(businessId: String) {
        viewModelScope.launch {
            // Observe Categories
            storeDb.itemCategoryDao().getCategoriesFlow(businessId).collectLatest { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }

        viewModelScope.launch {
            // Observe Items
            storeDb.itemDao().getStoreItemsFlow(businessId).collectLatest { itemList ->
                _uiState.update { it.copy(items = itemList) }
            }
        }
    }

    private fun fetchRemoteParties(businessId: String) {
        viewModelScope.launch {
            val parties = remoteDataSource.fetchCustomerParties(businessId)
            _uiState.update { it.copy(customerParties = parties) }
        }
    }

    fun selectBranch(branchId: String) {
        val branch = _uiState.value.branches.firstOrNull { it.id == branchId }
        _uiState.update {
            it.copy(
                activeBranchId = branchId,
                activeBranchName = branch?.branchName ?: "Main Branch"
            )
        }
        observeBranchStocks(branchId)
    }

    fun selectCategory(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun handleBarcodeScan(barcode: String) {
        val cleanBarcode = barcode.trim()
        val matchedItem = _uiState.value.items.firstOrNull {
            it.barcode.equals(cleanBarcode, ignoreCase = true) || it.sku.equals(cleanBarcode, ignoreCase = true)
        }
        if (matchedItem != null) {
            addToCart(matchedItem)
        } else {
            _uiState.update { it.copy(errorMessage = "Barcode '$cleanBarcode' not found in catalog.") }
        }
    }

    fun addToCart(item: ItemEntity) {
        val current = _uiState.value
        val allowNegative = current.allowNegativeStock
        val isPhysicalProduct = item.itemType == "PRODUCT"

        if (!allowNegative && isPhysicalProduct) {
            val available = current.stockMap[item.id] ?: 0.0
            val inCart = current.cart[item.id]?.quantity ?: 0.0
            if (inCart + 1.0 > available) {
                _uiState.update {
                    it.copy(errorMessage = if (available <= 0) "⚠️ '${item.name}' आउट ऑफ स्टॉक है (स्टॉक: 0)" else "⚠️ '${item.name}' के केवल $available उपलब्ध हैं")
                }
                return
            }
        }

        _uiState.update { curr ->
            val updatedMap = curr.cart.toMutableMap()
            val existing = updatedMap[item.id]
            if (existing != null) {
                updatedMap[item.id] = existing.copy(quantity = existing.quantity + 1.0)
            } else {
                updatedMap[item.id] = PosCartLine(item = item, quantity = 1.0)
            }
            curr.copy(cart = updatedMap, errorMessage = null)
        }
    }

    fun decreaseQuantity(item: ItemEntity) {
        _uiState.update { current ->
            val updatedMap = current.cart.toMutableMap()
            val existing = updatedMap[item.id]
            if (existing != null) {
                if (existing.quantity > 1.0) {
                    updatedMap[item.id] = existing.copy(quantity = existing.quantity - 1.0)
                } else {
                    updatedMap.remove(item.id)
                }
            }
            current.copy(cart = updatedMap)
        }
    }

    fun setQuantity(item: ItemEntity, quantity: Double) {
        _uiState.update { current ->
            val updatedMap = current.cart.toMutableMap()
            if (quantity > 0.0) {
                val existing = updatedMap[item.id] ?: PosCartLine(item = item)
                updatedMap[item.id] = existing.copy(quantity = quantity)
            } else {
                updatedMap.remove(item.id)
            }
            current.copy(cart = updatedMap)
        }
    }

    fun removeFromCart(item: ItemEntity) {
        _uiState.update { current ->
            val updatedMap = current.cart.toMutableMap()
            updatedMap.remove(item.id)
            current.copy(cart = updatedMap)
        }
    }

    fun clearCart() {
        _uiState.update {
            it.copy(
                cart = emptyMap(),
                discountAmount = 0.0,
                deliveryCharge = 0.0,
                packingCharge = 0.0,
                orderNotes = "",
                tenderedCash = 0.0
            )
        }
    }

    fun selectParty(party: PosCustomerPartyDto?) {
        _uiState.update {
            it.copy(
                selectedParty = party,
                customerName = party?.name ?: "Counter Customer",
                customerPhone = party?.phone ?: "9999999999"
            )
        }
    }

    fun setCustomerDetails(name: String, phone: String) {
        _uiState.update { it.copy(customerName = name, customerPhone = phone) }
    }

    fun setPaymentMethod(method: String) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun setDiscount(amount: Double) {
        _uiState.update { it.copy(discountAmount = amount.coerceAtLeast(0.0)) }
    }

    fun setPackingCharge(amount: Double) {
        _uiState.update { it.copy(packingCharge = amount.coerceAtLeast(0.0)) }
    }

    fun setDeliveryCharge(amount: Double) {
        _uiState.update { it.copy(deliveryCharge = amount.coerceAtLeast(0.0)) }
    }

    fun setNotes(notes: String) {
        _uiState.update { it.copy(orderNotes = notes) }
    }

    fun setTenderedCash(amount: Double) {
        _uiState.update { it.copy(tenderedCash = amount.coerceAtLeast(0.0)) }
    }

    fun setSendToKitchen(enabled: Boolean) {
        _uiState.update { it.copy(sendToKitchen = enabled) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun dismissSuccessInvoice() {
        _uiState.update { it.copy(checkoutSuccessInvoice = null) }
    }

    fun executeCheckout(onSuccess: (PosInvoiceResponseDto) -> Unit = {}) {
        val state = _uiState.value
        val businessId = state.activeBusiness?.id ?: return
        val branchId = state.activeBranchId ?: businessId

        if (state.cart.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Cart is empty. Please add items before billing.") }
            return
        }

        if (state.paymentMethod == "CREDIT_KHATA" && state.selectedParty == null && state.customerName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please select or enter a Customer Party for Credit Khata.") }
            return
        }

        if (state.paymentMethod == "CASH_ON_COUNTER" && state.tenderedCash > 0.0 && state.isCashShort) {
            _uiState.update { it.copy(errorMessage = "Cash received (₹${String.format(java.util.Locale.US, "%.2f", state.tenderedCash)}) is less than total amount (₹${String.format(java.util.Locale.US, "%.2f", state.grandTotal)}). Please collect ₹${String.format(java.util.Locale.US, "%.2f", state.cashShortageAmount)} more.") }
            return
        }

        val cartDtos = state.cart.values.map { line ->
            PosCartItemDto(
                itemId = line.item.id,
                itemName = line.item.name,
                barcode = line.item.barcode ?: "",
                hsnSacCode = line.item.hsnSacCode ?: "",
                unit = line.item.unit,
                quantity = line.quantity,
                salePrice = line.item.salePrice,
                taxRate = line.item.taxRate,
                discount = line.discount
            )
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingOut = true, errorMessage = null) }
            try {
                val effectiveCustomerName = if (state.paymentMethod == "CREDIT_KHATA") {
                    state.selectedParty?.name ?: state.customerName.ifBlank { "Khata Customer" }
                } else {
                    "Counter Customer"
                }
                val effectiveCustomerPhone = if (state.paymentMethod == "CREDIT_KHATA") {
                    state.selectedParty?.phone ?: state.customerPhone.ifBlank { "9999999999" }
                } else {
                    "9999999999"
                }

                val response = remoteDataSource.createPosInvoiceTransaction(
                    businessId = businessId,
                    branchId = branchId,
                    partyId = if (state.paymentMethod == "CREDIT_KHATA") state.selectedParty?.id else null,
                    customerName = effectiveCustomerName,
                    customerPhone = effectiveCustomerPhone,
                    paymentMethod = state.paymentMethod,
                    cartItems = cartDtos,
                    discountAmount = state.discountAmount,
                    deliveryCharge = state.deliveryCharge,
                    packingCharge = state.packingCharge,
                    notes = state.orderNotes.ifBlank { null },
                    orderStatus = if (state.sendToKitchen) "NEW" else "DELIVERED"
                )

                if (response.success) {
                    _uiState.update {
                        it.copy(
                            isCheckingOut = false,
                            checkoutSuccessInvoice = response,
                            cart = emptyMap(),
                            discountAmount = 0.0,
                            deliveryCharge = 0.0,
                            packingCharge = 0.0,
                            orderNotes = "",
                            tenderedCash = 0.0
                        )
                    }
                    onSuccess(response)
                } else {
                    _uiState.update {
                        it.copy(
                            isCheckingOut = false,
                            errorMessage = response.error ?: "Failed to complete POS billing transaction."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("PosCounter", "Checkout execution exception: ${e.message}", e)
                val cleanError = when {
                    e is io.github.jan.supabase.exceptions.UnauthorizedRestException ||
                            e.message?.contains("JWT expired", ignoreCase = true) == true ||
                            e.message?.contains("401", ignoreCase = true) == true ->
                        "Session expired. Please log in again to continue."
                    e.message?.contains("Unable to resolve host", ignoreCase = true) == true ||
                            e.message?.contains("ConnectException", ignoreCase = true) == true ->
                        "No internet connection. Please check your network and try again."
                    else -> "Unable to complete POS billing transaction. Please try again."
                }
                _uiState.update {
                    it.copy(
                        isCheckingOut = false,
                        errorMessage = cleanError
                    )
                }
            }
        }
    }
}
