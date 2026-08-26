package com.vidyasetuai.feature_store.presentation.screen.role_staff.kds

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_store.data.repository.KitchenKdsRepositoryImpl
import com.vidyasetuai.feature_store.domain.model.KdsFilterTab
import com.vidyasetuai.feature_store.domain.model.KitchenKdsUiModel
import com.vidyasetuai.feature_store.domain.repository.KitchenKdsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class KitchenKdsViewModel(
    context: Context,
    private val repository: KitchenKdsRepository = KitchenKdsRepositoryImpl(context)
) : ViewModel() {

    private val _uiState = MutableStateFlow(KitchenKdsUiState())
    val uiState: StateFlow<KitchenKdsUiState> = _uiState.asStateFlow()

    private var previousActiveCount = 0

    init {
        observeActiveKdsTickets()
        observeServedTickets()
        observeDeliveryRiders()
        startLiveTimerTicker()
    }

    private fun observeActiveKdsTickets() {
        viewModelScope.launch {
            repository.getActiveKdsTicketsFlow("").collect { tickets ->
                // Trigger audio chime if new order arrived and sound is enabled
                if (tickets.size > previousActiveCount && _uiState.value.isSoundEnabled && previousActiveCount > 0) {
                    playNewOrderChime()
                }
                previousActiveCount = tickets.size

                _uiState.update {
                    it.copy(
                        activeTickets = tickets,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observeServedTickets() {
        viewModelScope.launch {
            repository.getServedTicketsFlow("").collect { tickets ->
                _uiState.update {
                    it.copy(servedTickets = tickets)
                }
            }
        }
    }

    private fun observeDeliveryRiders() {
        viewModelScope.launch {
            repository.getDeliveryRidersFlow("").collect { riders ->
                _uiState.update {
                    it.copy(availableRiders = riders)
                }
            }
        }
    }

    /**
     * Periodic 30-second live ticker to increment elapsed minutes in memory.
     */
    private fun startLiveTimerTicker() {
        viewModelScope.launch {
            while (isActive) {
                delay(30_000)
                _uiState.update { state ->
                    val nowMillis = System.currentTimeMillis()
                    val updatedActive = state.activeTickets.map { ticket ->
                        val diff = parseAndComputeMinutes(ticket.order.createdAt, nowMillis)
                        ticket.copy(elapsedMinutes = diff, isUrgent = diff >= 15 && ticket.isNew)
                    }
                    state.copy(activeTickets = updatedActive)
                }
            }
        }
    }

    private fun parseAndComputeMinutes(createdAtStr: String?, nowMillis: Long): Int {
        if (createdAtStr.isNullOrBlank()) return 0
        return try {
            val epoch = java.time.Instant.parse(createdAtStr).toEpochMilli()
            ((nowMillis - epoch) / (60 * 1000)).toInt().coerceAtLeast(0)
        } catch (_: Exception) {
            0
        }
    }

    private fun playNewOrderChime() {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 250)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setFilterTab(tab: KdsFilterTab) {
        _uiState.update { it.copy(activeFilterTab = tab) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleSound() {
        _uiState.update { it.copy(isSoundEnabled = !it.isSoundEnabled) }
    }

    fun openAssignRiderModal(ticket: KitchenKdsUiModel) {
        _uiState.update { it.copy(selectedTicketForRiderAssignment = ticket) }
    }

    fun closeAssignRiderModal() {
        _uiState.update { it.copy(selectedTicketForRiderAssignment = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    // --- Status Actions ---

    fun startPreparing(orderId: String, businessId: String = "") {
        viewModelScope.launch {
            val result = repository.updateOrderStatus(businessId, orderId, "PREPARING")
            result.onSuccess {
                _uiState.update {
                    it.copy(successMessage = "ऑर्डर पकाना शुरू हुआ! (Started Preparing)")
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(errorMessage = "स्टेटस अपडेट नहीं हुआ: ${err.message}")
                }
            }
        }
    }

    fun markReady(orderId: String, businessId: String = "") {
        viewModelScope.launch {
            val result = repository.updateOrderStatus(businessId, orderId, "READY")
            result.onSuccess {
                _uiState.update {
                    it.copy(successMessage = "खाना तैयार है! पिकअप के लिए उपलब्ध (Marked Ready)")
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(errorMessage = "स्टेटस अपडेट नहीं हुआ: ${err.message}")
                }
            }
        }
    }

    fun markServed(orderId: String, businessId: String = "") {
        viewModelScope.launch {
            val result = repository.updateOrderStatus(businessId, orderId, "DELIVERED")
            result.onSuccess {
                _uiState.update {
                    it.copy(successMessage = "ऑर्डर काउंटर पर सर्व हो गया! (Marked Served)")
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(errorMessage = "स्टेटस अपडेट नहीं हुआ: ${err.message}")
                }
            }
        }
    }

    fun dispatchOrDeliverOrder(
        orderId: String,
        newStatus: String,
        riderId: String?,
        otpCode: String?,
        businessId: String = ""
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val result = repository.updateOrderStatus(
                businessId = businessId,
                orderId = orderId,
                newStatus = newStatus,
                riderId = riderId,
                otpCode = otpCode
            )
            result.onSuccess {
                val msg = if (newStatus == "OUT_FOR_DELIVERY") {
                    "ऑर्डर राइडर को डिस्पैच हो गया! (Dispatched to Rider 🛵)"
                } else {
                    "ऑर्डर ग्राहक को सफलतापूर्वक डिलीवर हो गया! (Delivered 🟢)"
                }
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        selectedTicketForRiderAssignment = null,
                        successMessage = msg
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = "डिस्पैच / डिलीवरी में समस्या: ${err.message}"
                    )
                }
            }
        }
    }
}
