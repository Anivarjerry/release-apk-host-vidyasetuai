package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessStaffDao {
    @Query("SELECT * FROM business_staff_members WHERE business_id = :businessId AND is_deleted = 0")
    fun getStaffFlow(businessId: String): Flow<List<BusinessStaffEntity>>

    @Query("SELECT * FROM business_staff_members WHERE id = :staffId AND is_deleted = 0 LIMIT 1")
    fun getStaffById(staffId: String): Flow<BusinessStaffEntity?>

    @Query("UPDATE business_staff_members SET is_deleted = 1, updated_at = :updatedAt WHERE id = :staffId")
    suspend fun deleteStaffById(staffId: String, updatedAt: String)

    @Query("UPDATE business_staff_members SET is_active = :isActive, updated_at = :updatedAt WHERE id = :staffId")
    suspend fun updateStaffActiveStatus(staffId: String, isActive: Boolean, updatedAt: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(staff: BusinessStaffEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(staffList: List<BusinessStaffEntity>)

    @Query("DELETE FROM business_staff_members WHERE business_id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
