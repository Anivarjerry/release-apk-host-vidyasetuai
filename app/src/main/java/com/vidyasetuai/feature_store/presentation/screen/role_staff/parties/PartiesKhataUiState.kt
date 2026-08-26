package com.vidyasetuai.feature_store.presentation.screen.role_staff.parties

import com.vidyasetuai.feature_store.data.local.entity.PartyEntity
import com.vidyasetuai.feature_store.data.local.entity.PartyLedgerEntryEntity

enum class PartyTypeFilter {
    ALL, CUSTOMER, SUPPLIER
}

data class PartiesKhataUiState(
    val businessId: String = "",
    val parties: List<PartyEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedType: PartyTypeFilter = PartyTypeFilter.ALL,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // BottomSheet & Modal States
    val isAddEditOpen: Boolean = false,
    val editingParty: PartyEntity? = null,

    val isPaymentModalOpen: Boolean = false,
    val paymentTargetParty: PartyEntity? = null,

    val isPassbookOpen: Boolean = false,
    val selectedPartyForPassbook: PartyEntity? = null,
    val ledgerEntries: List<PartyLedgerEntryEntity> = emptyList(),
    val isLedgerLoading: Boolean = false
) {
    val filteredParties: List<PartyEntity>
        get() {
            return parties.filter { party ->
                val matchesType = when (selectedType) {
                    PartyTypeFilter.ALL -> true
                    PartyTypeFilter.CUSTOMER -> party.partyType == "CUSTOMER"
                    PartyTypeFilter.SUPPLIER -> party.partyType == "SUPPLIER"
                }
                val q = searchQuery.trim().lowercase()
                val matchesQuery = q.isEmpty() ||
                        party.name.lowercase().contains(q) ||
                        party.phone.contains(q) ||
                        (party.gstin?.lowercase()?.contains(q) == true) ||
                        (party.email?.lowercase()?.contains(q) == true)

                matchesType && matchesQuery
            }
        }

    val totalReceivable: Double
        get() = parties.filter { it.currentBalance > 0 }.sumOf { it.currentBalance }

    val totalPayable: Double
        get() = parties.filter { it.currentBalance < 0 }.sumOf { Math.abs(it.currentBalance) }

    val customerCount: Int
        get() = parties.count { it.partyType == "CUSTOMER" }

    val supplierCount: Int
        get() = parties.count { it.partyType == "SUPPLIER" }
}
