package com.vidyasetuai.feature_store.data.repository

import android.content.Context
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.dao.*
import com.vidyasetuai.feature_store.data.local.entity.*
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import com.vidyasetuai.feature_store.domain.repository.KhataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID

class KhataRepositoryImpl(
    private val partyDao: PartyDao,
    private val partyAddressDao: PartyAddressDao,
    private val partyLedgerDao: PartyLedgerDao,
    private val remoteDataSource: StoreRemoteDataSource = StoreRemoteDataSource()
) : KhataRepository {

    constructor(context: Context) : this(
        StoreDatabase.getDatabase(context).partyDao(),
        StoreDatabase.getDatabase(context).partyAddressDao(),
        StoreDatabase.getDatabase(context).partyLedgerDao()
    )

    override fun getAllPartiesFlow(businessId: String): Flow<List<PartyEntity>> {
        return partyDao.getAllPartiesFlow(businessId).flowOn(Dispatchers.IO)
    }

    override fun getPartiesFlow(businessId: String, partyType: String): Flow<List<PartyEntity>> {
        return partyDao.getPartiesFlow(businessId, partyType).flowOn(Dispatchers.IO)
    }

    override fun getPartyByIdFlow(partyId: String): Flow<PartyEntity?> {
        return partyDao.getPartyByIdFlow(partyId).flowOn(Dispatchers.IO)
    }

    override fun getPartyAddressesFlow(partyId: String): Flow<List<PartyAddressEntity>> {
        return partyAddressDao.getPartyAddressesFlow(partyId).flowOn(Dispatchers.IO)
    }

    override fun getPartyPassbookFlow(partyId: String): Flow<List<PartyLedgerEntryEntity>> {
        return partyLedgerDao.getPartyPassbookFlow(partyId).flowOn(Dispatchers.IO)
    }

    override suspend fun saveParty(party: PartyEntity, address: PartyAddressEntity?): Result<PartyEntity> =
        withContext(Dispatchers.IO) {
            try {
                // 1. Remote Save to Supabase
                val remoteSaved = remoteDataSource.saveParty(party, address)
                val effectiveParty = remoteSaved ?: party

                // 2. Local Room DB Upsert
                partyDao.upsert(effectiveParty)
                address?.let { partyAddressDao.upsert(it.copy(partyId = effectiveParty.id)) }

                // 3. If Opening Balance is non-zero, insert opening ledger entry if not existing
                if (effectiveParty.openingBalance != 0.0) {
                    val openingEntry = PartyLedgerEntryEntity(
                        id = UUID.randomUUID().toString(),
                        businessId = effectiveParty.businessId,
                        partyId = effectiveParty.id,
                        entryType = if (effectiveParty.openingBalance > 0) "DEBIT" else "CREDIT",
                        amount = Math.abs(effectiveParty.openingBalance),
                        balanceAfter = effectiveParty.openingBalance,
                        referenceType = "OPENING_BALANCE",
                        description = "Opening Balance Settlement",
                        createdAt = Instant.now().toString(),
                        updatedAt = Instant.now().toString()
                    )
                    partyLedgerDao.upsert(openingEntry)
                }

                Result.success(effectiveParty)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }

    override suspend fun recordPayment(
        businessId: String,
        partyId: String,
        txnType: String,
        amount: Double,
        paymentMode: String,
        refNo: String?,
        notes: String?
    ): Result<Double> = withContext(Dispatchers.IO) {
        try {
            // 1. Call Atomic Supabase RPC fn_record_party_payment_transaction
            val response = remoteDataSource.recordPartyPayment(
                businessId = businessId,
                partyId = partyId,
                txnType = txnType,
                amount = amount,
                paymentMode = paymentMode,
                refNo = refNo,
                notes = notes
            )

            if (response != null && response.success) {
                val newBal = response.newBalance
                val nowIso = Instant.now().toString()

                // 2. Insert into Local Room Passbook Ledger
                val ledgerEntry = PartyLedgerEntryEntity(
                    id = response.ledgerId ?: UUID.randomUUID().toString(),
                    businessId = businessId,
                    partyId = partyId,
                    entryType = if (txnType == "PAYMENT_IN") "CREDIT" else "DEBIT",
                    amount = amount,
                    balanceAfter = newBal,
                    referenceType = "PAYMENT",
                    referenceId = response.paymentId,
                    description = "Payment ${if (txnType == "PAYMENT_IN") "Received" else "Paid"} ($paymentMode)" + (notes?.let { " - $it" } ?: ""),
                    createdAt = nowIso,
                    updatedAt = nowIso
                )
                partyLedgerDao.upsert(ledgerEntry)

                // 3. Update Party Balance locally in Room DB
                partyDao.updatePartyBalance(partyId, newBal, nowIso)

                Result.success(newBal)
            } else {
                Result.failure(Exception(response?.error ?: "Failed to record payment transaction."))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun syncParties(businessId: String) = withContext(Dispatchers.IO) {
        try {
            val remoteParties = remoteDataSource.fetchParties(businessId)
            if (remoteParties.isNotEmpty()) {
                partyDao.upsertAll(remoteParties)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun syncPartyLedger(partyId: String) = withContext(Dispatchers.IO) {
        try {
            val remoteEntries = remoteDataSource.fetchPartyLedger(partyId)
            if (remoteEntries.isNotEmpty()) {
                partyLedgerDao.upsertAll(remoteEntries)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
