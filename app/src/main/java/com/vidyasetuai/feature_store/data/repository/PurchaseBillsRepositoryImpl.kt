package com.vidyasetuai.feature_store.data.repository

import android.content.Context
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.entity.ItemEntity
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity
import com.vidyasetuai.feature_store.data.local.entity.PaymentEntity
import com.vidyasetuai.feature_store.data.local.entity.PurchaseInvoiceEntity
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import com.vidyasetuai.feature_store.domain.model.PurchaseBillUiModel
import com.vidyasetuai.feature_store.domain.model.PurchaseItemUiModel
import com.vidyasetuai.feature_store.domain.model.RecordPurchaseBillPayload
import com.vidyasetuai.feature_store.domain.repository.PurchaseBillsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.time.Instant
import java.util.UUID

class PurchaseBillsRepositoryImpl(
    context: Context,
    private val database: StoreDatabase = StoreDatabase.getDatabase(context),
    private val remoteDataSource: StoreRemoteDataSource = StoreRemoteDataSource()
) : PurchaseBillsRepository {

    private val purchaseInvoiceDao = database.purchaseInvoiceDao()
    private val partyDao = database.partyDao()
    private val itemDao = database.itemDao()
    private val inventoryStockDao = database.inventoryStockDao()
    private val paymentDao = database.paymentDao()

    override fun getPurchaseBillsFlow(businessId: String): Flow<List<PurchaseBillUiModel>> {
        val purchasesFlow = purchaseInvoiceDao.getAllPurchaseInvoicesFlow()
        val partiesFlow = partyDao.getAllPartiesFlow(businessId)

        return combine(purchasesFlow, partiesFlow) { purchases, parties ->
            val partyMap = parties.associateBy { it.id }

            purchases
                .filter { it.isDeleted == false && (businessId.isBlank() || it.businessId == businessId) }
                .map { entity ->
                    val supplier = partyMap[entity.supplierId]
                    PurchaseBillUiModel(
                        id = entity.id,
                        businessId = entity.businessId,
                        branchId = entity.branchId,
                        supplierId = entity.supplierId,
                        supplierName = supplier?.name ?: "Supplier",
                        supplierPhone = supplier?.phone,
                        supplierInvoiceNumber = entity.supplierInvoiceNumber,
                        invoiceDate = entity.invoiceDate,
                        taxableAmount = entity.taxableAmount,
                        cgstAmount = entity.cgstAmount,
                        sgstAmount = entity.sgstAmount,
                        igstAmount = entity.igstAmount,
                        grandTotal = entity.grandTotal,
                        paidAmount = entity.paidAmount,
                        dueAmount = entity.dueAmount,
                        paymentStatus = entity.paymentStatus,
                        notes = null,
                        createdAt = entity.createdAt
                    )
                }
        }.flowOn(Dispatchers.Default)
    }

    override fun getSuppliersFlow(businessId: String): Flow<List<PartyEntity>> {
        return partyDao.getPartiesFlow(businessId, "SUPPLIER")
    }

    override fun getCatalogItemsFlow(businessId: String): Flow<List<ItemEntity>> {
        return itemDao.getAllItemsFlow()
    }

    override suspend fun recordPurchaseBill(
        businessId: String,
        branchId: String,
        payload: RecordPurchaseBillPayload
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val purchaseId = UUID.randomUUID().toString()
            val nowStr = Instant.now().toString()
            val targetBranch = branchId.ifBlank { businessId }

            val dueAmount = maxOf(0.0, payload.grandTotal - payload.paidAmount)
            val paymentStatus = when {
                dueAmount <= 0 -> "PAID"
                payload.paidAmount > 0 -> "PARTIAL"
                else -> "UNPAID"
            }

            // 1. Remote Sync: Execute Atomic Supabase RPC (Auto-Stock In + Khata) first
            val itemsJsonArray = buildJsonArray {
                payload.purchaseItems.forEach { item ->
                    addJsonObject {
                        if (!item.itemId.isNullOrBlank()) put("item_id", item.itemId)
                        put("item_name", item.itemName)
                        if (!item.hsnSacCode.isNullOrBlank()) put("hsn_code", item.hsnSacCode)
                        put("quantity", item.quantity)
                        put("unit", item.unit)
                        put("purchase_rate", item.unitRate)
                        put("tax_rate", item.taxRate)
                        put("taxable_value", item.taxableValue)
                        put("cgst_amount", item.cgstAmount)
                        put("sgst_amount", item.sgstAmount)
                        put("total_amount", item.totalAmount)
                    }
                }
            }

            remoteDataSource.createPurchaseBillTransaction(
                businessId = businessId,
                branchId = targetBranch,
                supplierId = payload.supplierId,
                supplierBillNo = payload.supplierInvoiceNumber,
                billDate = payload.billDate,
                purchaseItems = itemsJsonArray,
                taxableAmount = payload.taxableAmount,
                cgstAmount = payload.cgstAmount,
                sgstAmount = payload.sgstAmount,
                igstAmount = payload.igstAmount,
                grandTotal = payload.grandTotal,
                paidAmount = payload.paidAmount,
                paymentMode = payload.paymentMode,
                notes = payload.notes
            )

            // 2. Local Room DB: Insert Purchase Invoice on remote success
            val localEntity = PurchaseInvoiceEntity(
                id = purchaseId,
                businessId = businessId,
                branchId = targetBranch,
                supplierId = payload.supplierId,
                supplierInvoiceNumber = payload.supplierInvoiceNumber,
                invoiceDate = payload.billDate,
                taxableAmount = payload.taxableAmount,
                cgstAmount = payload.cgstAmount,
                sgstAmount = payload.sgstAmount,
                igstAmount = payload.igstAmount,
                grandTotal = payload.grandTotal,
                paidAmount = payload.paidAmount,
                dueAmount = dueAmount,
                paymentStatus = paymentStatus,
                isActive = true,
                isDeleted = false,
                createdAt = nowStr,
                updatedAt = nowStr
            )
            purchaseInvoiceDao.upsert(localEntity)

            // 2B. Local Room DB: Insert Purchase Invoice Line Items
            val localItems = payload.purchaseItems.map { item ->
                com.vidyasetuai.feature_store.data.local.entity.PurchaseInvoiceItemEntity(
                    id = UUID.randomUUID().toString(),
                    purchaseId = purchaseId,
                    itemId = item.itemId,
                    itemName = item.itemName,
                    hsnSacCode = item.hsnSacCode,
                    quantity = item.quantity,
                    unit = item.unit,
                    unitRate = item.unitRate,
                    taxRate = item.taxRate,
                    taxableValue = item.taxableValue,
                    cgstAmount = item.cgstAmount,
                    sgstAmount = item.sgstAmount,
                    totalAmount = item.totalAmount,
                    isActive = true,
                    isDeleted = false,
                    createdAt = nowStr,
                    updatedAt = nowStr
                )
            }
            purchaseInvoiceDao.upsertPurchaseInvoiceItems(localItems)

            // 3. Local Room DB: If cash/upi paid, record in payments table
            if (payload.paidAmount > 0) {
                val paymentEntity = PaymentEntity(
                    id = UUID.randomUUID().toString(),
                    businessId = businessId,
                    partyId = payload.supplierId,
                    amount = payload.paidAmount,
                    paymentMode = payload.paymentMode,
                    paymentStatus = "SUCCESS",
                    collectedByType = "CASHIER",
                    isActive = true,
                    isDeleted = false,
                    createdAt = nowStr,
                    updatedAt = nowStr
                )
                paymentDao.upsert(paymentEntity)
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
