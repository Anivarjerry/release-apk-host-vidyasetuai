package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.PartyAddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartyAddressDao {
    @Query("SELECT * FROM party_addresses WHERE party_id = :partyId AND is_deleted = 0")
    fun getPartyAddressesFlow(partyId: String): Flow<List<PartyAddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(address: PartyAddressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(addresses: List<PartyAddressEntity>)

    @Query("DELETE FROM party_addresses WHERE party_id IN (SELECT id FROM parties WHERE business_id = :businessId)")
    suspend fun purgeByBusiness(businessId: String)
}
