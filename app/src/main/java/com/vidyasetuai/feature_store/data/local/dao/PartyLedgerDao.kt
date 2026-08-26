package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.PartyLedgerEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartyLedgerDao {
    @Query("SELECT * FROM party_ledger_entries WHERE party_id = :partyId AND is_deleted = 0 ORDER BY created_at DESC")
    fun getPartyPassbookFlow(partyId: String): Flow<List<PartyLedgerEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: PartyLedgerEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entries: List<PartyLedgerEntryEntity>)

    @Query("DELETE FROM party_ledger_entries WHERE business_id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
