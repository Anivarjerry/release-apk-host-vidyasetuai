package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartyDao {
    @Query("SELECT * FROM parties WHERE business_id = :businessId AND is_deleted = 0 ORDER BY name ASC")
    fun getAllPartiesFlow(businessId: String): Flow<List<PartyEntity>>

    @Query("SELECT * FROM parties WHERE is_deleted = 0 ORDER BY name ASC")
    fun getAllPartiesFlow(): Flow<List<PartyEntity>>

    @Query("SELECT * FROM parties WHERE is_deleted = 0 ORDER BY name ASC")
    fun observeAllParties(): Flow<List<PartyEntity>>

    @Query("SELECT * FROM parties WHERE business_id = :businessId AND party_type = :partyType AND is_deleted = 0 ORDER BY name ASC")
    fun getPartiesFlow(businessId: String, partyType: String): Flow<List<PartyEntity>>

    @Query("SELECT * FROM parties WHERE id = :partyId AND is_deleted = 0 LIMIT 1")
    fun getPartyByIdFlow(partyId: String): Flow<PartyEntity?>

    @Query("UPDATE parties SET current_balance = :newBalance, updated_at = :updatedAt WHERE id = :partyId")
    suspend fun updatePartyBalance(partyId: String, newBalance: Double, updatedAt: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(party: PartyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(parties: List<PartyEntity>)

    @Query("DELETE FROM parties WHERE business_id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
