package com.vidyasetuai.feature_store.presentation.screen.role_staff.parties

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity
import com.vidyasetuai.feature_store.data.repository.KhataRepositoryImpl
import com.vidyasetuai.feature_store.domain.repository.KhataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class PartiesKhataViewModel(
    private val context: Context,
    private val repository: KhataRepository = KhataRepositoryImpl(context)
) : ViewModel() {

    private val storeDb = StoreDatabase.getDatabase(context)

    private val _uiState = MutableStateFlow(PartiesKhataUiState())
    val uiState: StateFlow<PartiesKhataUiState> = _uiState.asStateFlow()

    private var partiesJob: Job? = null
    private var ledgerJob: Job? = null

    init {
        observeActiveBusiness()
    }

    private fun observeActiveBusiness() {
        viewModelScope.launch(Dispatchers.IO) {
            storeDb.businessDao().getAnyActiveBusinessFlow().collect { activeBusiness ->
                val businessId = activeBusiness?.id ?: ""
                _uiState.update { it.copy(businessId = businessId) }

                if (businessId.isNotEmpty()) {
                    observeParties(businessId)
                    repository.syncParties(businessId)
                }
            }
        }
    }

    private fun observeParties(businessId: String) {
        partiesJob?.cancel()
        partiesJob = viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            repository.getAllPartiesFlow(businessId).collect { partiesList ->
                _uiState.update {
                    it.copy(
                        parties = partiesList,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSelectedType(type: PartyTypeFilter) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun openAddParty() {
        _uiState.update {
            it.copy(
                isAddEditOpen = true,
                editingParty = null,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun openEditParty(party: PartyEntity) {
        _uiState.update {
            it.copy(
                isAddEditOpen = true,
                editingParty = party,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun closeAddEdit() {
        _uiState.update {
            it.copy(
                isAddEditOpen = false,
                editingParty = null,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun openRecordPayment(party: PartyEntity) {
        _uiState.update {
            it.copy(
                isPaymentModalOpen = true,
                paymentTargetParty = party,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun closeRecordPayment() {
        _uiState.update {
            it.copy(
                isPaymentModalOpen = false,
                paymentTargetParty = null,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun openPassbook(party: PartyEntity) {
        _uiState.update {
            it.copy(
                isPassbookOpen = true,
                selectedPartyForPassbook = party,
                isLedgerLoading = true
            )
        }
        observeLedger(party.id)
    }

    private fun observeLedger(partyId: String) {
        ledgerJob?.cancel()
        ledgerJob = viewModelScope.launch(Dispatchers.IO) {
            // Background sync fresh remote ledger history
            repository.syncPartyLedger(partyId)

            repository.getPartyPassbookFlow(partyId).collect { entries ->
                _uiState.update {
                    it.copy(
                        ledgerEntries = entries,
                        isLedgerLoading = false
                    )
                }
            }
        }
    }

    fun closePassbook() {
        _uiState.update {
            it.copy(
                isPassbookOpen = false,
                selectedPartyForPassbook = null,
                ledgerEntries = emptyList()
            )
        }
    }

    fun saveParty(
        partyType: String,
        name: String,
        phone: String,
        email: String?,
        gstin: String?,
        panNumber: String?,
        creditLimit: Double,
        openingBalance: Double,
        isReceivable: Boolean
    ) {
        val businessId = _uiState.value.businessId
        if (businessId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Business workspace not active.") }
            return
        }

        if (name.isBlank() || phone.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Party name and 10-digit phone are required.") }
            return
        }

        val editing = _uiState.value.editingParty
        val nowIso = Instant.now().toString()
        val calculatedOpening = if (isReceivable) openingBalance else -openingBalance

        val partyEntity = PartyEntity(
            id = editing?.id ?: UUID.randomUUID().toString(),
            businessId = businessId,
            userId = editing?.userId,
            partyType = partyType,
            name = name.trim(),
            phone = phone.trim(),
            email = email?.trim()?.ifBlank { null },
            gstin = gstin?.trim()?.ifBlank { null },
            panNumber = panNumber?.trim()?.ifBlank { null },
            stateCode = editing?.stateCode ?: "08",
            creditLimit = creditLimit,
            currentBalance = if (editing != null) editing.currentBalance else calculatedOpening,
            openingBalance = calculatedOpening,
            isActive = true,
            isDeleted = false,
            createdAt = editing?.createdAt ?: nowIso,
            updatedAt = nowIso
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = repository.saveParty(partyEntity, null)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isAddEditOpen = false,
                        editingParty = null,
                        successMessage = "Party saved successfully!"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = err.message ?: "Failed to save party."
                    )
                }
            }
        }
    }

    fun recordPayment(
        partyId: String,
        txnType: String,
        amount: Double,
        paymentMode: String,
        refNo: String?,
        notes: String?
    ) {
        val businessId = _uiState.value.businessId
        if (businessId.isBlank() || partyId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Invalid business or party.") }
            return
        }

        if (amount <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid amount greater than ₹0.") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = repository.recordPayment(
                businessId = businessId,
                partyId = partyId,
                txnType = txnType,
                amount = amount,
                paymentMode = paymentMode,
                refNo = refNo?.trim()?.ifBlank { null },
                notes = notes?.trim()?.ifBlank { null }
            )

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isPaymentModalOpen = false,
                        paymentTargetParty = null,
                        successMessage = "Payment recorded successfully!"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = err.message ?: "Failed to record payment."
                    )
                }
            }
        }
    }

    fun getWhatsAppStatementUrl(party: PartyEntity, storeName: String): String {
        val cleanPhone = party.phone.replace("[^0-9]".toRegex(), "")
        val formattedPhone = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone

        val bal = party.currentBalance
        val statusMsg = when {
            bal > 0 -> "आपका कुल बकाया (Pending Amount): ₹${String.format("%.2f", bal)}"
            bal < 0 -> "आपका एडवांस जमा (Advance Balance): ₹${String.format("%.2f", Math.abs(bal))}"
            else -> "आपका खाता चुकता है (No Due Balance: ₹0.00)"
        }

        val message = "नमस्कार ${party.name} जी,\n\n*$storeName* बही-खाता विवरण:\n$statusMsg\n\nधन्यवाद!"
        val encodedMsg = java.net.URLEncoder.encode(message, "UTF-8")
        return "https://wa.me/$formattedPhone?text=$encodedMsg"
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
