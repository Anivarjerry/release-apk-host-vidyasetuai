package com.vidyasetuai.feature_store.domain.repository

import com.vidyasetuai.feature_store.data.local.entity.*
import kotlinx.coroutines.flow.Flow

interface KhataRepository {
    fun getAllPartiesFlow(businessId: String): Flow<List<PartyEntity>>
    fun getPartiesFlow(businessId: String, partyType: String): Flow<List<PartyEntity>>
    fun getPartyByIdFlow(partyId: String): Flow<PartyEntity?>
    fun getPartyAddressesFlow(partyId: String): Flow<List<PartyAddressEntity>>
    fun getPartyPassbookFlow(partyId: String): Flow<List<PartyLedgerEntryEntity>>
    suspend fun saveParty(party: PartyEntity, address: PartyAddressEntity?): Result<PartyEntity>
    suspend fun recordPayment(
        businessId: String,
        partyId: String,
        txnType: String,
        amount: Double,
        paymentMode: String,
        refNo: String?,
        notes: String?
    ): Result<Double>
    suspend fun syncParties(businessId: String)
    suspend fun syncPartyLedger(partyId: String)
}
