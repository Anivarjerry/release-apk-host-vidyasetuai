package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffAttendanceEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffSalaryPaymentEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffSalaryPayoutEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffSalaryProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessStaffSalaryDao {

    // =========================================================================
    // 1. SALARY PROFILES (Base Pay, Bank Account & UPI ID)
    // =========================================================================

    @Query("SELECT * FROM business_staff_salary_profiles WHERE business_id = :businessId AND is_deleted = 0")
    fun getSalaryProfilesFlow(businessId: String): Flow<List<BusinessStaffSalaryProfileEntity>>

    @Query("SELECT * FROM business_staff_salary_profiles WHERE staff_id = :staffId AND is_deleted = 0 LIMIT 1")
    fun getProfileForStaffFlow(staffId: String): Flow<BusinessStaffSalaryProfileEntity?>

    @Query("SELECT * FROM business_staff_salary_profiles WHERE staff_id = :staffId AND is_deleted = 0 LIMIT 1")
    suspend fun getProfileForStaff(staffId: String): BusinessStaffSalaryProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: BusinessStaffSalaryProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfiles(profiles: List<BusinessStaffSalaryProfileEntity>)

    // =========================================================================
    // 2. ATTENDANCE (Daily & Monthly Register)
    // =========================================================================

    @Query("""
        SELECT * FROM business_staff_attendance 
        WHERE business_id = :businessId 
          AND staff_id = :staffId 
          AND is_deleted = 0 
          AND attendance_date >= :startDate 
          AND attendance_date <= :endDate 
        ORDER BY attendance_date ASC
    """)
    fun getAttendanceForMonthFlow(
        businessId: String,
        staffId: String,
        startDate: String,
        endDate: String
    ): Flow<List<BusinessStaffAttendanceEntity>>

    @Query("""
        SELECT * FROM business_staff_attendance 
        WHERE business_id = :businessId 
          AND staff_id = :staffId 
          AND is_deleted = 0 
          AND attendance_date >= :startDate 
          AND attendance_date <= :endDate 
        ORDER BY attendance_date ASC
    """)
    suspend fun getAttendanceForMonth(
        businessId: String,
        staffId: String,
        startDate: String,
        endDate: String
    ): List<BusinessStaffAttendanceEntity>

    @Query("""
        SELECT * FROM business_staff_attendance 
        WHERE business_id = :businessId 
          AND attendance_date = :date 
          AND is_deleted = 0
    """)
    fun getDailyAttendanceFlow(businessId: String, date: String): Flow<List<BusinessStaffAttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAttendance(attendance: BusinessStaffAttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllAttendance(attendanceList: List<BusinessStaffAttendanceEntity>)

    @Query("UPDATE business_staff_attendance SET is_deleted = 1, updated_at = :updatedAt WHERE id = :id")
    suspend fun deleteAttendance(id: String, updatedAt: String)

    // =========================================================================
    // 3. SALARY PAYOUTS (Monthly Slips & Locking)
    // =========================================================================

    @Query("""
        SELECT * FROM business_staff_salary_payouts 
        WHERE business_id = :businessId 
          AND payout_month = :month 
          AND payout_year = :year 
          AND is_deleted = 0
    """)
    fun getPayoutsForMonthFlow(
        businessId: String,
        month: Int,
        year: Int
    ): Flow<List<BusinessStaffSalaryPayoutEntity>>

    @Query("""
        SELECT * FROM business_staff_salary_payouts 
        WHERE staff_id = :staffId 
          AND payout_month = :month 
          AND payout_year = :year 
          AND is_deleted = 0 
        LIMIT 1
    """)
    fun getPayoutForStaffMonthFlow(
        staffId: String,
        month: Int,
        year: Int
    ): Flow<BusinessStaffSalaryPayoutEntity?>

    @Query("""
        SELECT * FROM business_staff_salary_payouts 
        WHERE staff_id = :staffId 
          AND is_deleted = 0 
        ORDER BY payout_year DESC, payout_month DESC
    """)
    fun getAllPayoutsForStaffFlow(staffId: String): Flow<List<BusinessStaffSalaryPayoutEntity>>

    @Query("""
        SELECT * FROM business_staff_salary_payouts 
        WHERE staff_id = :staffId 
          AND is_deleted = 0 
        ORDER BY payout_year DESC, payout_month DESC
    """)
    suspend fun getAllPayoutsForStaff(staffId: String): List<BusinessStaffSalaryPayoutEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPayout(payout: BusinessStaffSalaryPayoutEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllPayouts(payouts: List<BusinessStaffSalaryPayoutEntity>)

    // =========================================================================
    // 4. SALARY PAYMENTS & ADVANCES
    // =========================================================================

    @Query("""
        SELECT * FROM business_staff_salary_payments 
        WHERE staff_id = :staffId 
          AND is_deleted = 0 
        ORDER BY payment_date DESC, created_at DESC
    """)
    fun getPaymentsForStaffFlow(staffId: String): Flow<List<BusinessStaffSalaryPaymentEntity>>

    @Query("""
        SELECT * FROM business_staff_salary_payments 
        WHERE staff_id = :staffId 
          AND is_deleted = 0 
        ORDER BY payment_date DESC, created_at DESC
    """)
    suspend fun getPaymentsForStaff(staffId: String): List<BusinessStaffSalaryPaymentEntity>

    @Query("""
        SELECT * FROM business_staff_salary_payments 
        WHERE staff_id = :staffId 
          AND payment_type = 'ADVANCE' 
          AND is_advance_settled = 0 
          AND is_deleted = 0
    """)
    fun getUnsettledAdvancesForStaffFlow(staffId: String): Flow<List<BusinessStaffSalaryPaymentEntity>>

    @Query("""
        SELECT * FROM business_staff_salary_payments 
        WHERE staff_id = :staffId 
          AND payment_type = 'ADVANCE' 
          AND is_advance_settled = 0 
          AND is_deleted = 0
    """)
    suspend fun getUnsettledAdvancesForStaff(staffId: String): List<BusinessStaffSalaryPaymentEntity>

    @Query("""
        SELECT * FROM business_staff_salary_payments 
        WHERE business_id = :businessId 
          AND is_deleted = 0 
        ORDER BY payment_date DESC
    """)
    fun getAllPaymentsForBusinessFlow(businessId: String): Flow<List<BusinessStaffSalaryPaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPayment(payment: BusinessStaffSalaryPaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllPayments(payments: List<BusinessStaffSalaryPaymentEntity>)

    // =========================================================================
    // 5. TRANSACTION PURGE (Full Reset Sync)
    // =========================================================================

    @Query("DELETE FROM business_staff_salary_profiles WHERE business_id = :businessId")
    suspend fun purgeProfilesByBusiness(businessId: String)

    @Query("DELETE FROM business_staff_attendance WHERE business_id = :businessId")
    suspend fun purgeAttendanceByBusiness(businessId: String)

    @Query("DELETE FROM business_staff_salary_payouts WHERE business_id = :businessId")
    suspend fun purgePayoutsByBusiness(businessId: String)

    @Query("DELETE FROM business_staff_salary_payments WHERE business_id = :businessId")
    suspend fun purgePaymentsByBusiness(businessId: String)
}
