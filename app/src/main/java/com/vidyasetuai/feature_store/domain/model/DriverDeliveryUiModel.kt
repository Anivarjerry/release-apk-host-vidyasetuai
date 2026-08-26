package com.vidyasetuai.feature_store.domain.model

enum class DriverDeliveryTab {
    ACTIVE,
    DELIVERED
}

data class DriverDeliveryOrderUiModel(
    val id: String,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val orderType: String = "DELIVERY",
    val orderStatus: String = "PLACED",
    val deliveryOtp: String,
    val grandTotal: Double = 0.0,
    val paymentMethod: String = "CASH_ON_DELIVERY",
    val paymentStatus: String = "UNPAID",
    val orderNotes: String? = null,
    val itemsSummary: String = "",
    val itemCount: Int = 0,
    val createdAt: String,
    val deliveredAt: String? = null
)

data class DriverDeliveryUiState(
    val activeOrders: List<DriverDeliveryOrderUiModel> = emptyList(),
    val completedOrders: List<DriverDeliveryOrderUiModel> = emptyList(),
    val selectedTab: DriverDeliveryTab = DriverDeliveryTab.ACTIVE,
    val selectedOrderForOtp: DriverDeliveryOrderUiModel? = null,
    val isVerifyingOtp: Boolean = false,
    val cashInHandTotal: Double = 0.0,
    val searchQuery: String = "",
    val snackbarMessage: String? = null,
    val isSuccess: Boolean = true
)
