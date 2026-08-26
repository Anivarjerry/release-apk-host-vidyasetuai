package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessBranchDao {
    @Query("SELECT * FROM business_branches WHERE business_id = :businessId AND is_deleted = 0")
    fun getBranchesFlow(businessId: String): Flow<List<BusinessBranchEntity>>

    @Query("SELECT * FROM business_branches WHERE id = :branchId AND is_deleted = 0 LIMIT 1")
    fun getBranchById(branchId: String): Flow<BusinessBranchEntity?>

    @Query("UPDATE business_branches SET is_main_branch = (id = :mainBranchId) WHERE business_id = :businessId")
    suspend fun setMainBranch(businessId: String, mainBranchId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(branch: BusinessBranchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(branches: List<BusinessBranchEntity>)

    @Query("DELETE FROM business_branches WHERE business_id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
