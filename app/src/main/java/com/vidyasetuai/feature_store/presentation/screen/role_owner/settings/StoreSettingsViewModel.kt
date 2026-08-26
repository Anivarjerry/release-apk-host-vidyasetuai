package com.vidyasetuai.feature_store.presentation.screen.role_owner.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessSettingsEntity
import com.vidyasetuai.feature_store.data.repository.StoreSettingsRepositoryImpl
import com.vidyasetuai.feature_store.domain.repository.StoreSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StoreSettingsViewModel(
    context: Context,
    private val repository: StoreSettingsRepository = StoreSettingsRepositoryImpl(context)
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreSettingsUiState())
    val uiState: StateFlow<StoreSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettingsOverview()
    }

    private fun loadSettingsOverview() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getSettingsOverviewFlow("").collect { overview ->
                _uiState.update {
                    it.copy(
                        overview = overview,
                        isLoading = false
                    )
                }
            }
        }
    }

    // --- Modal / Sheet Toggles ---
    fun openBranchSheet() = _uiState.update { it.copy(isBranchSheetOpen = true) }
    fun closeBranchSheet() = _uiState.update { it.copy(isBranchSheetOpen = false) }

    fun openBillingSheet() = _uiState.update { it.copy(isBillingSheetOpen = true) }
    fun closeBillingSheet() = _uiState.update { it.copy(isBillingSheetOpen = false) }

    fun openBankUpiSheet() = _uiState.update { it.copy(isBankUpiSheetOpen = true) }
    fun closeBankUpiSheet() = _uiState.update { it.copy(isBankUpiSheetOpen = false) }

    fun openFeatureSwitchesSheet() = _uiState.update { it.copy(isFeatureSwitchesSheetOpen = true) }
    fun closeFeatureSwitchesSheet() = _uiState.update { it.copy(isFeatureSwitchesSheetOpen = false) }

    fun openGstEwaySheet() = _uiState.update { it.copy(isGstEwaySheetOpen = true) }
    fun closeGstEwaySheet() = _uiState.update { it.copy(isGstEwaySheetOpen = false) }

    fun openQrStandeeModal() = _uiState.update { it.copy(isQrStandeeModalOpen = true) }
    fun closeQrStandeeModal() = _uiState.update { it.copy(isQrStandeeModalOpen = false) }

    fun clearMessages() = _uiState.update { it.copy(successMessage = null, errorMessage = null) }

    // --- Save Actions (Safe Remote Invoker + Room DB) ---

    fun saveMainBranch(
        branchName: String,
        addressLine: String,
        city: String,
        state: String,
        pincode: String,
        lat: Double?,
        lng: Double?
    ) {
        val bizId = _uiState.value.overview.business?.id ?: return
        val currentMain = _uiState.value.overview.mainBranch

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val nowIso = java.time.Instant.now().toString()
            val branchToSave = currentMain?.copy(
                branchName = branchName,
                addressLine = addressLine,
                city = city,
                state = state,
                pincode = pincode,
                lat = lat,
                lng = lng,
                isMainBranch = true,
                updatedAt = nowIso
            ) ?: BusinessBranchEntity(
                id = java.util.UUID.randomUUID().toString(),
                businessId = bizId,
                branchName = branchName,
                addressLine = addressLine,
                city = city,
                state = state,
                pincode = pincode,
                lat = lat,
                lng = lng,
                isMainBranch = true,
                createdAt = nowIso,
                updatedAt = nowIso
            )

            val result = repository.saveMainBranch(branchToSave)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isBranchSheetOpen = false,
                        successMessage = "दुकान का पता सफलतापूर्वक अपडेट हुआ! (Address saved successfully)"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "शाखा पता सुरक्षित नहीं हो सका: ${err.message}"
                    )
                }
            }
        }
    }

    fun saveBillingSettings(
        invoicePrefix: String,
        thermalPrinterSize: String,
        enableGstBilling: Boolean,
        packingCharge: Double,
        deliveryCharge: Double,
        freeDeliveryAbove: Double?
    ) {
        val bizId = _uiState.value.overview.business?.id ?: return
        val currentSettings = _uiState.value.overview.settings

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val nowIso = java.time.Instant.now().toString()
            val settingsToSave = currentSettings?.copy(
                invoicePrefix = invoicePrefix,
                thermalPrinterSize = thermalPrinterSize,
                enableGstBilling = enableGstBilling,
                packingChargeDefault = packingCharge,
                deliveryChargeDefault = deliveryCharge,
                freeDeliveryAbove = freeDeliveryAbove,
                updatedAt = nowIso
            ) ?: BusinessSettingsEntity(
                id = java.util.UUID.randomUUID().toString(),
                businessId = bizId,
                invoicePrefix = invoicePrefix,
                thermalPrinterSize = thermalPrinterSize,
                enableGstBilling = enableGstBilling,
                packingChargeDefault = packingCharge,
                deliveryChargeDefault = deliveryCharge,
                freeDeliveryAbove = freeDeliveryAbove,
                createdAt = nowIso,
                updatedAt = nowIso
            )

            val result = repository.saveBillingSettings(settingsToSave)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isBillingSheetOpen = false,
                        successMessage = "बिलिंग व प्रिंटर सेटिंग्स सफलतापूर्वक अपडेट हुईं! (Billing settings saved)"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "बिलिंग सेटिंग्स सुरक्षित नहीं हो सकी: ${err.message}"
                    )
                }
            }
        }
    }

    fun saveBankAndUpi(
        upiId: String?,
        bankName: String?,
        accountNo: String?,
        ifsc: String?,
        branch: String?
    ) {
        val biz = _uiState.value.overview.business ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val updatedBiz = biz.copy(
                upiId = upiId,
                bankName = bankName,
                bankAccountNo = accountNo,
                bankIfsc = ifsc,
                bankBranch = branch
            )

            val result = repository.updateBusinessProfile(updatedBiz)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isBankUpiSheetOpen = false,
                        successMessage = "बैंक व UPI QR सेटिंग्स सुरक्षित हुईं! (Bank & UPI QR updated)"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "बैंक सेटिंग्स सुरक्षित नहीं हो सकी: ${err.message}"
                    )
                }
            }
        }
    }

    fun saveFeatureSwitches(
        enableKds: Boolean,
        enableDeliveryTracking: Boolean,
        enableInventoryTracking: Boolean,
        enableCustomerKhata: Boolean,
        allowOnlineOrders: Boolean
    ) {
        val bizId = _uiState.value.overview.business?.id ?: return
        val currentSettings = _uiState.value.overview.settings

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val nowIso = java.time.Instant.now().toString()
            val settingsToSave = currentSettings?.copy(
                enableKds = enableKds,
                enableDeliveryTracking = enableDeliveryTracking,
                enableInventoryTracking = enableInventoryTracking,
                enableCustomerKhata = enableCustomerKhata,
                allowOnlineOrders = allowOnlineOrders,
                updatedAt = nowIso
            ) ?: BusinessSettingsEntity(
                id = java.util.UUID.randomUUID().toString(),
                businessId = bizId,
                enableKds = enableKds,
                enableDeliveryTracking = enableDeliveryTracking,
                enableInventoryTracking = enableInventoryTracking,
                enableCustomerKhata = enableCustomerKhata,
                allowOnlineOrders = allowOnlineOrders,
                createdAt = nowIso,
                updatedAt = nowIso
            )

            val result = repository.saveBillingSettings(settingsToSave)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isFeatureSwitchesSheetOpen = false,
                        successMessage = "सिस्टम फीचर्स सफलतापूर्वक अपडेट हुए! (Features updated)"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "फीचर्स अपडेट नहीं हो सके: ${err.message}"
                    )
                }
            }
        }
    }

    fun saveGstEwayConfig(
        gstin: String?,
        apiUsername: String?,
        apiPassword: String?,
        gspProvider: String,
        enableAutoEway: Boolean
    ) {
        val biz = _uiState.value.overview.business ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            if (!gstin.isNullOrBlank()) {
                val updatedBiz = biz.copy(gstin = gstin)
                repository.updateBusinessProfile(updatedBiz)
            }

            val currentSettings = _uiState.value.overview.settings
            val nowIso = java.time.Instant.now().toString()
            val settingsToSave = currentSettings?.copy(
                gstApiUsername = apiUsername,
                gstApiPassword = apiPassword,
                gspProviderName = gspProvider,
                enableAutoEwayBill = enableAutoEway,
                updatedAt = nowIso
            ) ?: BusinessSettingsEntity(
                id = java.util.UUID.randomUUID().toString(),
                businessId = biz.id,
                gstApiUsername = apiUsername,
                gstApiPassword = apiPassword,
                gspProviderName = gspProvider,
                enableAutoEwayBill = enableAutoEway,
                createdAt = nowIso,
                updatedAt = nowIso
            )

            val result = repository.saveBillingSettings(settingsToSave)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isGstEwaySheetOpen = false,
                        successMessage = "GSTIN व E-Way Bill NIC सेटिंग्स सुरक्षित हुईं! (GST & E-Way configured)"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "GST सेटिंग्स सुरक्षित नहीं हो सकी: ${err.message}"
                    )
                }
            }
        }
    }
}
