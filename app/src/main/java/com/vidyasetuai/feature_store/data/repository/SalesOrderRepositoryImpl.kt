package com.vidyasetuai.feature_store.data.repository

import android.content.Context
import android.util.Log
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.dao.*
import com.vidyasetuai.feature_store.data.local.entity.*
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import com.vidyasetuai.feature_store.domain.model.InvoiceWithDetailsUiModel
import com.vidyasetuai.feature_store.domain.repository.SalesOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class SalesOrderRepositoryImpl(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val invoiceDao: InvoiceDao,
    private val invoiceItemDao: InvoiceItemDao,
    private val orderDeliveryDao: OrderDeliveryDao,
    private val partyDao: PartyDao,
    private val remoteDataSource: StoreRemoteDataSource = StoreRemoteDataSource()
) : SalesOrderRepository {

    constructor(context: Context) : this(
        StoreDatabase.getDatabase(context).orderDao(),
        StoreDatabase.getDatabase(context).orderItemDao(),
        StoreDatabase.getDatabase(context).invoiceDao(),
        StoreDatabase.getDatabase(context).invoiceItemDao(),
        StoreDatabase.getDatabase(context).orderDeliveryDao(),
        StoreDatabase.getDatabase(context).partyDao()
    )

    override fun getLiveKdsOrdersFlow(businessId: String, branchId: String): Flow<List<OrderEntity>> {
        return orderDao.getLiveKdsOrdersFlow(businessId, branchId)
    }

    override fun getBranchOrdersFlow(businessId: String, branchId: String): Flow<List<OrderEntity>> {
        return orderDao.getBranchOrdersFlow(businessId, branchId)
    }

    override fun getInvoicesFlow(businessId: String, branchId: String): Flow<List<InvoiceEntity>> {
        return invoiceDao.getInvoicesFlow(businessId, branchId)
    }

    override fun getInvoicesWithDetailsFlow(businessId: String): Flow<List<InvoiceWithDetailsUiModel>> {
        return combine(
            invoiceDao.getInvoicesByBusinessFlow(businessId),
            partyDao.getAllPartiesFlow(businessId),
            invoiceItemDao.getAllInvoiceItemsFlow()
        ) { invoices, parties, allItems ->
            val partyMap = parties.associateBy { it.id }
            val itemsGroupedByInvoice = allItems.groupBy { it.invoiceId }

            invoices.map { invoice ->
                val party = invoice.partyId?.let { partyMap[it] }
                val items = itemsGroupedByInvoice[invoice.id] ?: emptyList()

                InvoiceWithDetailsUiModel(
                    invoice = invoice,
                    items = items,
                    customerName = party?.name,
                    customerPhone = party?.phone,
                    partyGstin = party?.gstin,
                    orderNumber = null
                )
            }
        }.flowOn(Dispatchers.Default)
    }

    override suspend fun createPosCheckoutOrder(order: OrderEntity, items: List<OrderItemEntity>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                orderDao.upsertOrder(order)
                items.forEach { orderItemDao.upsert(it) }
                true
            } catch (e: Exception) {
                Log.e("SalesOrderRepo", "Error creating POS order: ${e.message}", e)
                false
            }
        }
    }

    override suspend fun createTaxInvoice(invoice: InvoiceEntity, items: List<InvoiceItemEntity>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                invoiceDao.upsert(invoice)
                items.forEach { invoiceItemDao.upsert(it) }
                true
            } catch (e: Exception) {
                Log.e("SalesOrderRepo", "Error creating Tax Invoice: ${e.message}", e)
                false
            }
        }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                true
            } catch (e: Exception) {
                Log.e("SalesOrderRepo", "Error updating order status: ${e.message}", e)
                false
            }
        }
    }

    override suspend fun assignRiderToOrder(orderId: String, riderId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                true
            } catch (e: Exception) {
                Log.e("SalesOrderRepo", "Error assigning rider: ${e.message}", e)
                false
            }
        }
    }

    override suspend fun cancelInvoice(invoiceId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val nowIso = java.time.Instant.now().toString()
            // 1. Safe Supabase remote mutation
            remoteDataSource.cancelInvoice(invoiceId)
            // 2. Local Room DB update
            invoiceDao.updatePaymentStatus(invoiceId, "CANCELLED", nowIso)
            Result.success(true)
        } catch (e: Exception) {
            Log.e("SalesOrderRepo", "Error cancelling invoice: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun updateEwayBill(
        invoiceId: String,
        ewayBillNo: String?,
        vehicleNo: String?,
        transportName: String?,
        distanceKm: Int,
        ewayStatus: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val nowIso = java.time.Instant.now().toString()
            // 1. Safe Supabase remote mutation
            remoteDataSource.updateEwayBill(invoiceId, ewayBillNo, vehicleNo, transportName, distanceKm, ewayStatus)
            // 2. Local Room DB update via entity upsert
            val existingInvoice = invoiceDao.getInvoiceById(invoiceId).firstOrNull()
            if (existingInvoice != null) {
                val updatedInvoice = existingInvoice.copy(
                    ewayBillNumber = ewayBillNo,
                    vehicleNumber = vehicleNo,
                    transportName = transportName,
                    distanceKm = distanceKm,
                    ewayStatus = ewayStatus,
                    ewayBillDate = nowIso,
                    updatedAt = nowIso
                )
                invoiceDao.upsert(updatedInvoice)
            }
            Result.success(true)
        } catch (e: Exception) {
            Log.e("SalesOrderRepo", "Error updating E-Way Bill: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun syncInvoices(businessId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val remoteInvoices = remoteDataSource.fetchInvoices(businessId)
            if (remoteInvoices.isNotEmpty()) {
                invoiceDao.upsertAll(remoteInvoices)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SalesOrderRepo", "Error syncing invoices: ${e.message}", e)
            Result.failure(e)
        }
    }
}
