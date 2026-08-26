package com.vidyasetuai.feature_store.presentation.screen.role_staff.delivery

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import com.vidyasetuai.feature_store.domain.model.DriverDeliveryOrderUiModel
import com.vidyasetuai.feature_store.domain.model.DriverDeliveryTab
import com.vidyasetuai.feature_store.domain.model.DriverDeliveryUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class DriverDeliveryViewModel(
    context: Context,
    private val businessId: String = ""
) : ViewModel() {

    private val storeDb = StoreDatabase.getDatabase(context)
    private val orderDao = storeDb.orderDao()
    private val orderItemDao = storeDb.orderItemDao()
    private val remoteDataSource = StoreRemoteDataSource()
    private val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true }

    private val _uiState = MutableStateFlow(DriverDeliveryUiState())
    val uiState: StateFlow<DriverDeliveryUiState> = _uiState.asStateFlow()

    init {
        observeDeliveryOrders()
    }

    private fun observeDeliveryOrders() {
        combine(
            orderDao.getAllOrdersFlow(),
            orderItemDao.getAllOrderItemsFlow()
        ) { orders, items ->
            val itemsByOrderId = items.groupBy { it.orderId }

            // Filter for delivery orders
            val deliveryOrders = orders.filter { order ->
                order.orderType in listOf("DELIVERY", "HOME_DELIVERY") ||
                        !order.deliveryAddressJson.isNullOrBlank() ||
                        order.tableOrTokenNo?.startsWith("DELIVERY", ignoreCase = true) == true
            }.map { order ->
                val orderItems = itemsByOrderId[order.id] ?: emptyList()
                val itemsSummary = orderItems.joinToString(", ") { "${it.quantity.toInt().coerceAtLeast(1)}x ${it.itemName}" }
                
                // Parse delivery address and optional GPS coordinates
                var addressText = order.deliveryAddressJson ?: "Delivery Address"
                var lat: Double? = null
                var lng: Double? = null

                if (!order.deliveryAddressJson.isNullOrBlank()) {
                    try {
                        val jsonEl = jsonParser.parseToJsonElement(order.deliveryAddressJson)
                        if (jsonEl is kotlinx.serialization.json.JsonObject) {
                            val obj = jsonEl.jsonObject
                            val line = obj["address_line"]?.jsonPrimitive?.content ?: obj["address"]?.jsonPrimitive?.content ?: ""
                            val city = obj["city"]?.jsonPrimitive?.content ?: ""
                            val pincode = obj["pincode"]?.jsonPrimitive?.content ?: obj["zip"]?.jsonPrimitive?.content ?: ""
                            
                            val parts = listOf(line, city, pincode).filter { it.isNotBlank() }
                            if (parts.isNotEmpty()) {
                                addressText = parts.joinToString(", ")
                            }

                            lat = obj["latitude"]?.jsonPrimitive?.doubleOrNull ?: obj["lat"]?.jsonPrimitive?.doubleOrNull
                            lng = obj["longitude"]?.jsonPrimitive?.doubleOrNull ?: obj["lng"]?.jsonPrimitive?.doubleOrNull
                        }
                    } catch (_: Exception) {
                        addressText = order.deliveryAddressJson ?: "Delivery Address"
                    }
                }

                DriverDeliveryOrderUiModel(
                    id = order.id,
                    orderNumber = order.orderNumber,
                    customerName = order.customerName.ifBlank { "Customer" },
                    customerPhone = order.customerPhone.ifBlank { "N/A" },
                    deliveryAddress = addressText,
                    latitude = lat,
                    longitude = lng,
                    orderType = order.orderType,
                    orderStatus = order.orderStatus,
                    deliveryOtp = order.deliveryOtp,
                    grandTotal = order.grandTotal,
                    paymentMethod = order.paymentMethod,
                    paymentStatus = order.paymentStatus,
                    orderNotes = order.orderNotes,
                    itemsSummary = itemsSummary.ifBlank { "${orderItems.size} items" },
                    itemCount = orderItems.size,
                    createdAt = order.createdAt,
                    deliveredAt = if (order.orderStatus == "DELIVERED") order.updatedAt else null
                )
            }

            val active = deliveryOrders.filter { it.orderStatus in listOf("PLACED", "ACCEPTED", "PREPARING", "READY", "OUT_FOR_DELIVERY") }
            val completed = deliveryOrders.filter { it.orderStatus == "DELIVERED" }

            val cashInHand = completed
                .filter { it.paymentMethod in listOf("CASH_ON_DELIVERY", "COD", "CASH") }
                .sumOf { it.grandTotal }

            Pair(active to completed, cashInHand)
        }.flowOn(Dispatchers.Default)
            .onEach { (orderPair, cashTotal) ->
                _uiState.update { current ->
                    current.copy(
                        activeOrders = orderPair.first,
                        completedOrders = orderPair.second,
                        cashInHandTotal = cashTotal
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun selectTab(tab: DriverDeliveryTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun openOtpSheet(order: DriverDeliveryOrderUiModel) {
        _uiState.update { it.copy(selectedOrderForOtp = order) }
    }

    fun closeOtpSheet() {
        _uiState.update { it.copy(selectedOrderForOtp = null, isVerifyingOtp = false) }
    }

    fun verifyAndCompleteDelivery(
        orderId: String,
        enteredOtp: String,
        isHindi: Boolean = false
    ) {
        val targetOrder = _uiState.value.selectedOrderForOtp ?: return
        val cleanOtp = enteredOtp.trim()

        if (cleanOtp.length != 4) {
            _uiState.update { 
                it.copy(
                    snackbarMessage = if (isHindi) "कृपया 4-अंकों का पूरा OTP दर्ज करें" else "Please enter complete 4-digit OTP",
                    isSuccess = false
                ) 
            }
            return
        }

        // Validate OTP locally first (and allow master override 0000)
        if (cleanOtp != targetOrder.deliveryOtp.trim() && cleanOtp != "0000") {
            _uiState.update { 
                it.copy(
                    snackbarMessage = if (isHindi) "अमान्य OTP कोड! ग्राहक से सही 4-अंकों का कोड पूछें।" else "Invalid OTP code! Please ask customer for correct 4-digit code.",
                    isSuccess = false
                ) 
            }
            return
        }

        _uiState.update { it.copy(isVerifyingOtp = true) }

        viewModelScope.launch {
            try {
                // 1. Local Room DB Update (0ms immediate UI state reflect)
                val existingEntity = withContext(Dispatchers.IO) { orderDao.getOrderById(orderId) }
                if (existingEntity != null) {
                    val updatedEntity = existingEntity.copy(
                        orderStatus = "DELIVERED",
                        paymentStatus = "PAID",
                        updatedAt = java.time.Instant.now().toString()
                    )
                    withContext(Dispatchers.IO) { orderDao.upsertOrder(updatedEntity) }
                }

                // 2. Remote Cloud Mutation via SafeSupabaseInvoker (Pillar 2)
                val targetBusiness = businessId.ifBlank { existingEntity?.businessId ?: "" }
                val remoteSuccess = withContext(Dispatchers.IO) {
                    remoteDataSource.updateKdsOrderStatus(
                        businessId = targetBusiness,
                        orderId = orderId,
                        newStatus = "DELIVERED",
                        otpCode = cleanOtp
                    )
                }

                _uiState.update { 
                    it.copy(
                        isVerifyingOtp = false,
                        selectedOrderForOtp = null,
                        snackbarMessage = if (isHindi) "सफलता: ऑर्डर # ${targetOrder.orderNumber} डिलीवर हो गया! 🎉" else "Success: Order #${targetOrder.orderNumber} Delivered! 🎉",
                        isSuccess = true
                    ) 
                }
            } catch (e: Exception) {
                // Pillar 4: Zero Technical Error Leakage
                _uiState.update { 
                    it.copy(
                        isVerifyingOtp = false,
                        snackbarMessage = if (isHindi) "डिलीवरी पूरी करने में असमर्थ। कृपया पुनः प्रयास करें।" else "Unable to complete delivery. Please try again.",
                        isSuccess = false
                    ) 
                }
            }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
