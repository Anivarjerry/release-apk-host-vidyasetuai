package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.InvoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices WHERE business_id = :businessId AND is_deleted = 0 ORDER BY created_at DESC")
    fun getInvoicesByBusinessFlow(businessId: String): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE is_deleted = 0 ORDER BY created_at DESC")
    fun getAllInvoicesFlow(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE business_id = :businessId AND branch_id = :branchId AND is_deleted = 0 ORDER BY created_at DESC")
    fun getInvoicesFlow(businessId: String, branchId: String): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE id = :invoiceId AND is_deleted = 0 LIMIT 1")
    fun getInvoiceById(invoiceId: String): Flow<InvoiceEntity?>

    @Query("UPDATE invoices SET payment_status = :status, updated_at = :updatedAt WHERE id = :invoiceId")
    suspend fun updatePaymentStatus(invoiceId: String, status: String, updatedAt: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(invoice: InvoiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(invoices: List<InvoiceEntity>)

    @Query("DELETE FROM invoices WHERE business_id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
