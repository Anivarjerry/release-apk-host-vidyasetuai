package com.vidyasetuai.feature_store.data.local.dao

import androidx.room.*
import com.vidyasetuai.feature_store.data.local.entity.PurchaseInvoiceEntity
import com.vidyasetuai.feature_store.data.local.entity.PurchaseInvoiceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseInvoiceDao {
    @Query("SELECT * FROM purchase_invoices WHERE business_id = :businessId AND branch_id = :branchId AND is_deleted = 0 ORDER BY invoice_date DESC, created_at DESC")
    fun getPurchaseInvoicesFlow(businessId: String, branchId: String): Flow<List<PurchaseInvoiceEntity>>

    @Query("SELECT * FROM purchase_invoices WHERE business_id = :businessId AND is_deleted = 0 ORDER BY invoice_date DESC, created_at DESC")
    fun observePurchaseInvoices(businessId: String): Flow<List<PurchaseInvoiceEntity>>

    @Query("SELECT * FROM purchase_invoices WHERE is_deleted = 0 ORDER BY created_at DESC")
    fun getAllPurchaseInvoicesFlow(): Flow<List<PurchaseInvoiceEntity>>

    @Query("SELECT * FROM purchase_invoice_items WHERE purchase_id = :purchaseId AND is_deleted = 0")
    fun observePurchaseItems(purchaseId: String): Flow<List<PurchaseInvoiceItemEntity>>

    @Query("SELECT * FROM purchase_invoice_items WHERE purchase_id = :purchaseId AND is_deleted = 0")
    suspend fun getPurchaseItems(purchaseId: String): List<PurchaseInvoiceItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(purchaseInvoice: PurchaseInvoiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(purchaseInvoices: List<PurchaseInvoiceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPurchaseInvoiceItems(items: List<PurchaseInvoiceItemEntity>)

    @Query("DELETE FROM purchase_invoices WHERE business_id = :businessId")
    suspend fun purgeByBusiness(businessId: String)
}
