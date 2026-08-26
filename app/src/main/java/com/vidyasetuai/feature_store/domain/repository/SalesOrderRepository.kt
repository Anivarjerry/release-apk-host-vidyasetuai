package com.vidyasetuai.feature_store.domain.repository

import com.vidyasetuai.feature_store.data.local.entity.*
import com.vidyasetuai.feature_store.domain.model.InvoiceWithDetailsUiModel
import kotlinx.coroutines.flow.Flow

interface SalesOrderRepository {
    fun getLiveKdsOrdersFlow(businessId: String, branchId: String): Flow<List<OrderEntity>>
    fun getBranchOrdersFlow(businessId: String, branchId: String): Flow<List<OrderEntity>>
    fun getInvoicesFlow(businessId: String, branchId: String): Flow<List<InvoiceEntity>>
    fun getInvoicesWithDetailsFlow(businessId: String): Flow<List<InvoiceWithDetailsUiModel>>

    suspend fun createPosCheckoutOrder(order: OrderEntity, items: List<OrderItemEntity>): Boolean
    suspend fun createTaxInvoice(invoice: InvoiceEntity, items: List<InvoiceItemEntity>): Boolean
    suspend fun updateOrderStatus(orderId: String, newStatus: String): Boolean
    suspend fun assignRiderToOrder(orderId: String, riderId: String): Boolean

    suspend fun cancelInvoice(invoiceId: String): Result<Boolean>
    suspend fun updateEwayBill(
        invoiceId: String,
        ewayBillNo: String?,
        vehicleNo: String?,
        transportName: String?,
        distanceKm: Int,
        ewayStatus: String
    ): Result<Boolean>

    suspend fun syncInvoices(businessId: String): Result<Unit>
}
