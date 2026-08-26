package com.vidyasetuai.feature_store.data.sync

import android.content.Context
import androidx.room.withTransaction
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import com.vidyasetuai.feature_store.data.remote.dto.WorkspacePayloadDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoreWorkspaceSyncEngine(
    private val storeDatabase: StoreDatabase,
    private val remoteDataSource: StoreRemoteDataSource = StoreRemoteDataSource()
) {
    constructor(context: Context) : this(StoreDatabase.getDatabase(context))

    /**
     * Fetches complete 20-table JSON payload from Supabase RPC for [businessId] (or auto-resolves if null)
     * and performs atomic `@Transaction` purge & bulk-insert into local `store_database.db`.
     */
    suspend fun triggerFullNetworkSync(businessId: String? = null): Boolean {
        return withContext(Dispatchers.IO) {
            val payload = remoteDataSource.fetchWorkspacePayload(businessId)
            if (payload != null) {
                val resolvedBusinessId = businessId ?: payload.business?.id ?: ""
                processFreshWorkspaceResetPayload(resolvedBusinessId, payload)
            } else {
                false
            }
        }
    }

    /**
     * Executes atomic `@Transaction` purge of existing local data for [businessId]
     * and performs bulk insert of fresh 20-table payload [payload].
     */
    suspend fun processFreshWorkspaceResetPayload(businessId: String, payload: WorkspacePayloadDto): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val effectiveBusinessId = businessId.ifBlank { payload.business?.id ?: "" }
                storeDatabase.withTransaction {
                    if (effectiveBusinessId.isNotBlank()) {
                        // 1. Safe Transaction Purge of Old Business Records
                        storeDatabase.partyLedgerDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.paymentDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.purchaseInvoiceDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.invoiceItemDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.invoiceDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.orderDeliveryDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.deliveryRiderDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.orderItemDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.orderDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.inventoryTransactionDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.inventoryStockDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.itemVariantDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.itemDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.itemCategoryDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.partyAddressDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.partyDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.businessStaffDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.businessStaffSalaryDao().purgePaymentsByBusiness(effectiveBusinessId)
                        storeDatabase.businessStaffSalaryDao().purgePayoutsByBusiness(effectiveBusinessId)
                        storeDatabase.businessStaffSalaryDao().purgeAttendanceByBusiness(effectiveBusinessId)
                        storeDatabase.businessStaffSalaryDao().purgeProfilesByBusiness(effectiveBusinessId)
                        storeDatabase.businessExpenseDao().clearExpenses(effectiveBusinessId)
                        storeDatabase.businessBranchDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.businessSettingsDao().purgeByBusiness(effectiveBusinessId)
                        storeDatabase.businessDao().purgeByBusiness(effectiveBusinessId)
                    }

                    // 2. Atomic Bulk Insertion of 24-Table Fresh RPC Payload
                    payload.business?.let { storeDatabase.businessDao().upsert(it) }
                    payload.settings?.let { storeDatabase.businessSettingsDao().upsert(it) }

                    if (payload.branches.isNotEmpty()) storeDatabase.businessBranchDao().upsertAll(payload.branches)
                    if (payload.staff.isNotEmpty()) storeDatabase.businessStaffDao().upsertAll(payload.staff)
                    if (payload.parties.isNotEmpty()) storeDatabase.partyDao().upsertAll(payload.parties)
                    if (payload.partyAddresses.isNotEmpty()) storeDatabase.partyAddressDao().upsertAll(payload.partyAddresses)
                    if (payload.categories.isNotEmpty()) storeDatabase.itemCategoryDao().upsertAll(payload.categories)
                    if (payload.items.isNotEmpty()) storeDatabase.itemDao().upsertAll(payload.items)
                    if (payload.itemVariants.isNotEmpty()) storeDatabase.itemVariantDao().upsertAll(payload.itemVariants)
                    if (payload.inventoryStocks.isNotEmpty()) storeDatabase.inventoryStockDao().upsertAll(payload.inventoryStocks)
                    if (payload.inventoryTransactions.isNotEmpty()) storeDatabase.inventoryTransactionDao().upsertAll(payload.inventoryTransactions)
                    if (payload.orders.isNotEmpty()) storeDatabase.orderDao().upsertAll(payload.orders)
                    if (payload.orderItems.isNotEmpty()) storeDatabase.orderItemDao().upsertAll(payload.orderItems)
                    if (payload.deliveryRiders.isNotEmpty()) storeDatabase.deliveryRiderDao().upsertAll(payload.deliveryRiders)
                    if (payload.orderDeliveries.isNotEmpty()) storeDatabase.orderDeliveryDao().upsertAll(payload.orderDeliveries)
                    if (payload.invoices.isNotEmpty()) storeDatabase.invoiceDao().upsertAll(payload.invoices)
                    if (payload.invoiceItems.isNotEmpty()) storeDatabase.invoiceItemDao().upsertAll(payload.invoiceItems)
                    if (payload.purchaseInvoices.isNotEmpty()) storeDatabase.purchaseInvoiceDao().upsertAll(payload.purchaseInvoices)
                    if (payload.payments.isNotEmpty()) storeDatabase.paymentDao().upsertAll(payload.payments)
                    if (payload.partyLedgerEntries.isNotEmpty()) storeDatabase.partyLedgerDao().upsertAll(payload.partyLedgerEntries)
                    if (payload.staffSalaryProfiles.isNotEmpty()) storeDatabase.businessStaffSalaryDao().upsertProfiles(payload.staffSalaryProfiles)
                    if (payload.staffAttendance.isNotEmpty()) storeDatabase.businessStaffSalaryDao().upsertAllAttendance(payload.staffAttendance)
                    if (payload.staffSalaryPayouts.isNotEmpty()) storeDatabase.businessStaffSalaryDao().upsertAllPayouts(payload.staffSalaryPayouts)
                    if (payload.staffSalaryPayments.isNotEmpty()) storeDatabase.businessStaffSalaryDao().upsertAllPayments(payload.staffSalaryPayments)
                    if (payload.expenseTypes.isNotEmpty()) storeDatabase.businessExpenseDao().upsertExpenseTypes(payload.expenseTypes)
                    if (payload.expenses.isNotEmpty()) storeDatabase.businessExpenseDao().upsertExpenses(payload.expenses)
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    /**
     * Completely purges all 24 local Store Database tables in an ACID transaction.
     * Useful during user logout, business switching, or hard cache resets.
     */
    suspend fun purgeAllStoreData(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                storeDatabase.withTransaction {
                    storeDatabase.clearAllTables()
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
