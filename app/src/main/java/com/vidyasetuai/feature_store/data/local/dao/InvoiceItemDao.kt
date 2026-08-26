package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceItemDao {
    @Query("SELECT * FROM invoice_items WHERE is_deleted = 0")
    fun getAllInvoiceItemsFlow(): Flow<List<InvoiceItemEntity>>

    @Query("SELECT * FROM invoice_items WHERE invoice_id = :invoiceId AND is_deleted = 0")
    fun getInvoiceItemsFlow(invoiceId: String): Flow<List<InvoiceItemEntity>>

    @Query("SELECT * FROM invoice_items WHERE invoice_id = :invoiceId AND is_deleted = 0")
    fun getInvoiceItemsList(invoiceId: String): List<InvoiceItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(invoiceItem: InvoiceItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(invoiceItems: List<InvoiceItemEntity>)

    @Query("DELETE FROM invoice_items WHERE invoice_id IN (SELECT id FROM invoices WHERE business_id = :businessId)")
    suspend fun purgeByBusiness(businessId: String)
}
