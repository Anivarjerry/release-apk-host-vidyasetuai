package com.vidyasetuai.feature_store.data.remote.datasource

import android.util.Log
import com.vidyasetuai.core.network.SafeSupabaseInvoker
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_store.data.local.entity.InvoiceEntity
import com.vidyasetuai.feature_store.data.remote.dto.PublicProductDto
import com.vidyasetuai.feature_store.data.remote.dto.WorkspacePayloadDto
import com.vidyasetuai.feature_store.data.remote.dto.StaffLinkedUserDto
import com.vidyasetuai.feature_store.data.local.entity.PartyAddressEntity
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity
import com.vidyasetuai.feature_store.data.local.entity.PartyLedgerEntryEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessSettingsEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffSalaryProfileEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffAttendanceEntity
import com.vidyasetuai.feature_store.data.local.entity.DeliveryRiderEntity
import com.vidyasetuai.feature_store.data.local.entity.OrderDeliveryEntity
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class RecordPartyPaymentResponseDto(
    @SerialName("success") val success: Boolean = false,
    @SerialName("payment_id") val paymentId: String? = null,
    @SerialName("ledger_id") val ledgerId: String? = null,
    @SerialName("old_balance") val oldBalance: Double = 0.0,
    @SerialName("new_balance") val newBalance: Double = 0.0,
    @SerialName("party_name") val partyName: String? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class PlaceOrderResponseDto(
    @SerialName("success") val success: Boolean = false,
    @SerialName("order_id") val orderId: String? = null,
    @SerialName("order_number") val orderNumber: String? = null,
    @SerialName("delivery_otp") val deliveryOtp: String? = null,
    @SerialName("order_type") val orderType: String? = null,
    @SerialName("table_or_token_no") val tableOrTokenNo: String? = null,
    @SerialName("grand_total") val grandTotal: Double = 0.0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("error") val error: String? = null
)

class StoreRemoteDataSource {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    /**
     * Fetches complete 20-table JSON payload from Supabase RPC `fn_fetch_business_store_workspace_payload`.
     */
    suspend fun fetchWorkspacePayload(businessId: String? = null): WorkspacePayloadDto? {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val params = buildJsonObject {
                if (!businessId.isNullOrBlank()) {
                    put("p_business_id", businessId)
                }
            }
            val responseText = SupabaseClient.client.postgrest.rpc(
                "fn_fetch_business_store_workspace_payload",
                params
            ).data

            Log.d("StoreSync", "Raw RPC payload response length: ${responseText.length}")
            json.decodeFromString<WorkspacePayloadDto>(responseText)
        }
        return result.getOrNull()
    }

    /**
     * Fetches real paginated public marketplace products from Supabase RPC `fn_fetch_public_marketplace_products`.
     */
    suspend fun fetchPublicMarketplaceProducts(
        limit: Int = 15,
        offset: Int = 0,
        query: String? = null,
        categoryId: String? = null
    ): List<PublicProductDto> {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val params = buildJsonObject {
                put("p_limit", limit)
                put("p_offset", offset)
                if (!query.isNullOrBlank()) {
                    put("p_search_query", query)
                }
                if (!categoryId.isNullOrBlank()) {
                    put("p_category_id", categoryId)
                }
            }

            val responseText = SupabaseClient.client.postgrest.rpc(
                "fn_fetch_public_marketplace_products",
                params
            ).data

            Log.d("StoreMarketplace", "Raw products RPC response: $responseText")
            json.decodeFromString<List<PublicProductDto>>(responseText)
        }
        return result.getOrDefault(emptyList())
    }

    /**
     * Places real public customer order via battle-tested Web Store RPC `fn_place_public_customer_order`.
     * Calculates verified totals, generates dynamic 4-digit OTP and #ORD-XXXX order number directly from Postgres.
     */
    suspend fun placePublicCustomerOrder(
        businessSlug: String,
        customerName: String,
        customerPhone: String,
        orderType: String,
        tableOrTokenNo: String?,
        deliveryAddressJson: JsonObject?,
        cartItems: JsonArray,
        orderNotes: String?
    ): PlaceOrderResponseDto {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val params = buildJsonObject {
                put("p_business_slug", businessSlug)
                put("p_customer_name", customerName)
                put("p_customer_phone", customerPhone)
                put("p_order_type", orderType)
                if (!tableOrTokenNo.isNullOrBlank()) {
                    put("p_table_or_token_no", tableOrTokenNo)
                }
                if (deliveryAddressJson != null) {
                    put("p_delivery_address_json", deliveryAddressJson)
                }
                put("p_cart_items", cartItems)
                if (!orderNotes.isNullOrBlank()) {
                    put("p_order_notes", orderNotes)
                }
            }

            Log.d("StoreOrder", "Calling fn_place_public_customer_order params: $params")
            val responseText = SupabaseClient.client.postgrest.rpc(
                "fn_place_public_customer_order",
                params
            ).data

            Log.d("StoreOrder", "Order RPC response: $responseText")
            json.decodeFromString<PlaceOrderResponseDto>(responseText)
        }
        return result.getOrElse { error ->
            Log.e("StoreOrder", "Error placing customer order RPC: ${error.message}", error)
            PlaceOrderResponseDto(
                success = false,
                error = error.message ?: "Failed to place order. Please try again."
            )
        }
    }

    /**
     * Fetches real live and past customer orders with delivery OTP and items from Supabase RPC `fn_fetch_customer_orders`.
     */
    suspend fun fetchCustomerOrders(phone: String? = null): List<com.vidyasetuai.feature_store.data.remote.dto.CustomerOrderDto> {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val params = buildJsonObject {
                if (!phone.isNullOrBlank()) {
                    put("p_phone", phone)
                }
            }

            val responseText = SupabaseClient.client.postgrest.rpc(
                "fn_fetch_customer_orders",
                params
            ).data

            Log.d("StoreOrdersList", "Customer orders RPC response: $responseText")
            json.decodeFromString<List<com.vidyasetuai.feature_store.data.remote.dto.CustomerOrderDto>>(responseText)
        }
        return result.getOrDefault(emptyList())
    }

    /**
     * Executes atomic POS Billing invoice transaction via Supabase RPC `fn_create_pos_invoice_transaction`.
     */
    suspend fun createPosInvoiceTransaction(
        businessId: String,
        branchId: String,
        partyId: String?,
        customerName: String,
        customerPhone: String,
        paymentMethod: String,
        cartItems: List<com.vidyasetuai.feature_store.data.remote.dto.PosCartItemDto>,
        discountAmount: Double,
        deliveryCharge: Double,
        packingCharge: Double,
        notes: String?,
        orderStatus: String = "DELIVERED"
    ): com.vidyasetuai.feature_store.data.remote.dto.PosInvoiceResponseDto {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val cartItemsJson = json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(com.vidyasetuai.feature_store.data.remote.dto.PosCartItemDto.serializer()),
                cartItems
            )
            val cartJsonArray = json.decodeFromString<JsonArray>(cartItemsJson)

            val params = buildJsonObject {
                put("p_business_id", businessId)
                put("p_branch_id", branchId)
                if (!partyId.isNullOrBlank()) {
                    put("p_party_id", partyId)
                }
                put("p_customer_name", customerName)
                put("p_customer_phone", customerPhone)
                put("p_payment_method", paymentMethod)
                put("p_cart_items", cartJsonArray)
                put("p_discount_amount", discountAmount)
                put("p_delivery_charge", deliveryCharge)
                put("p_packing_charge", packingCharge)
                if (!notes.isNullOrBlank()) {
                    put("p_notes", notes)
                }
                put("p_order_status", orderStatus)
            }

            Log.d("StorePOS", "Calling fn_create_pos_invoice_transaction params: $params")
            val responseText = SupabaseClient.client.postgrest.rpc(
                "fn_create_pos_invoice_transaction",
                params
            ).data

            Log.d("StorePOS", "POS RPC response: $responseText")
            json.decodeFromString<com.vidyasetuai.feature_store.data.remote.dto.PosInvoiceResponseDto>(responseText)
        }

        return result.getOrElse { error ->
            Log.e("StorePOS", "Error executing POS checkout RPC: ${error.message}", error)
            com.vidyasetuai.feature_store.data.remote.dto.PosInvoiceResponseDto(
                success = false,
                error = error.message ?: "Unable to complete billing transaction. Please try again."
            )
        }
    }

    /**
     * Fetches active customer parties from Supabase `parties` table for POS auto-complete.
     */
    suspend fun fetchCustomerParties(businessId: String): List<com.vidyasetuai.feature_store.data.remote.dto.PosCustomerPartyDto> {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val response = SupabaseClient.client.postgrest.from("parties")
                .select {
                    filter {
                        eq("business_id", businessId)
                        eq("is_active", true)
                    }
                }
            val responseText = response.data
            json.decodeFromString<List<com.vidyasetuai.feature_store.data.remote.dto.PosCustomerPartyDto>>(responseText)
        }
        return result.getOrDefault(emptyList())
    }

    /**
     * Creates a new item in Supabase `items` table and inserts initial stock into `inventory_stocks`.
     */
    suspend fun createItemWithStock(
        item: com.vidyasetuai.feature_store.data.local.entity.ItemEntity,
        branchId: String?,
        initialStock: Double = 0.0,
        lowStockThreshold: Double = 5.0
    ): com.vidyasetuai.feature_store.data.local.entity.ItemEntity? {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val response = SupabaseClient.client.postgrest.from("items")
                .insert(item) {
                    select()
                }
            val insertedList = json.decodeFromString<List<com.vidyasetuai.feature_store.data.local.entity.ItemEntity>>(response.data)
            val insertedItem = insertedList.firstOrNull() ?: item

            if (insertedItem.itemType == "PRODUCT" && !branchId.isNullOrBlank()) {
                val stockEntity = com.vidyasetuai.feature_store.data.local.entity.InventoryStockEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    itemId = insertedItem.id,
                    branchId = branchId,
                    currentStock = initialStock,
                    lowStockThreshold = lowStockThreshold,
                    isActive = true,
                    isDeleted = false,
                    createdAt = insertedItem.createdAt,
                    updatedAt = insertedItem.updatedAt
                )
                SupabaseClient.client.postgrest.from("inventory_stocks")
                    .insert(stockEntity)
            }
            insertedItem
        }
        return result.getOrNull()
    }

    /**
     * Updates an existing item in Supabase `items` table.
     */
    suspend fun updateItem(item: com.vidyasetuai.feature_store.data.local.entity.ItemEntity): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            SupabaseClient.client.postgrest.from("items")
                .update(item) {
                    filter {
                        eq("id", item.id)
                    }
                }
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Soft-deletes an item in Supabase `items` table.
     */
    suspend fun deleteItem(itemId: String): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val updatePayload = buildJsonObject {
                put("is_deleted", true)
                put("is_active", false)
            }
            SupabaseClient.client.postgrest.from("items")
                .update(updatePayload) {
                    filter {
                        eq("id", itemId)
                    }
                }
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Creates a new item category in Supabase `item_categories` table.
     */
    suspend fun createCategory(category: com.vidyasetuai.feature_store.data.local.entity.ItemCategoryEntity): com.vidyasetuai.feature_store.data.local.entity.ItemCategoryEntity? {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val response = SupabaseClient.client.postgrest.from("item_categories")
                .insert(category) {
                    select()
                }
            val insertedList = json.decodeFromString<List<com.vidyasetuai.feature_store.data.local.entity.ItemCategoryEntity>>(response.data)
            insertedList.firstOrNull() ?: category
        }
        return result.getOrNull()
    }

    /**
     * Soft-deletes a category in Supabase `item_categories` table.
     */
    suspend fun deleteCategory(categoryId: String): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val updatePayload = buildJsonObject {
                put("is_deleted", true)
                put("is_active", false)
            }
            SupabaseClient.client.postgrest.from("item_categories")
                .update(updatePayload) {
                    filter {
                        eq("id", categoryId)
                    }
                }
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Executes atomic RPC `fn_adjust_inventory_stock` in Supabase.
     */
    suspend fun adjustStock(
        businessId: String,
        branchId: String?,
        itemId: String,
        adjustedStock: Double,
        reason: String,
        notes: String?
    ): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val params = buildJsonObject {
                put("p_business_id", businessId)
                if (!branchId.isNullOrBlank()) {
                    put("p_branch_id", branchId)
                }
                put("p_item_id", itemId)
                put("p_adjusted_stock", adjustedStock)
                put("p_reason", reason)
                if (!notes.isNullOrBlank()) {
                    put("p_notes", notes)
                }
            }
            SupabaseClient.client.postgrest.rpc("fn_adjust_inventory_stock", params)
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Fetches historical inventory transactions for an item.
     */
    suspend fun fetchItemStockTimeline(itemId: String): List<com.vidyasetuai.feature_store.data.local.entity.InventoryTransactionEntity> {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val response = SupabaseClient.client.postgrest.from("inventory_transactions")
                .select {
                    filter {
                        eq("item_id", itemId)
                        eq("is_deleted", false)
                    }
                }
            json.decodeFromString<List<com.vidyasetuai.feature_store.data.local.entity.InventoryTransactionEntity>>(response.data)
        }
        return result.getOrDefault(emptyList())
    }

    /**
     * Fetches recent sales invoices from Supabase.
     */
    suspend fun fetchInvoices(businessId: String): List<InvoiceEntity> {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val response = SupabaseClient.client.postgrest.from("invoices")
                .select {
                    filter {
                        eq("business_id", businessId)
                        eq("is_deleted", false)
                    }
                }
            json.decodeFromString<List<InvoiceEntity>>(response.data)
        }
        return result.getOrDefault(emptyList())
    }

    /**
     * Cancels an invoice in Supabase (sets payment_status = "CANCELLED").
     */
    suspend fun cancelInvoice(invoiceId: String): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val updatePayload = buildJsonObject {
                put("payment_status", "CANCELLED")
                put("updated_at", java.time.Instant.now().toString())
            }
            SupabaseClient.client.postgrest.from("invoices")
                .update(updatePayload) {
                    filter {
                        eq("id", invoiceId)
                    }
                }
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Updates E-Way Bill details for an invoice in Supabase.
     */
    suspend fun updateEwayBill(
        invoiceId: String,
        ewayBillNo: String?,
        vehicleNo: String?,
        transportName: String?,
        distanceKm: Int,
        ewayStatus: String
    ): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val nowIso = java.time.Instant.now().toString()
            val updatePayload = buildJsonObject {
                put("eway_bill_number", ewayBillNo)
                put("vehicle_number", vehicleNo)
                put("transport_name", transportName)
                put("distance_km", distanceKm)
                put("eway_status", ewayStatus)
                put("eway_bill_date", nowIso)
                put("updated_at", nowIso)
            }
            SupabaseClient.client.postgrest.from("invoices")
                .update(updatePayload) {
                    filter {
                        eq("id", invoiceId)
                    }
                }
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Fetches active parties for a business from Supabase.
     */
    suspend fun fetchParties(businessId: String): List<PartyEntity> {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val responseText = SupabaseClient.client.postgrest.from("parties")
                .select {
                    filter {
                        eq("business_id", businessId)
                        eq("is_active", true)
                        eq("is_deleted", false)
                    }
                }.data
            json.decodeFromString<List<PartyEntity>>(responseText)
        }
        return result.getOrDefault(emptyList())
    }

    /**
     * Fetches passbook ledger entries for a party from Supabase.
     */
    suspend fun fetchPartyLedger(partyId: String): List<PartyLedgerEntryEntity> {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val responseText = SupabaseClient.client.postgrest.from("party_ledger_entries")
                .select {
                    filter {
                        eq("party_id", partyId)
                        eq("is_deleted", false)
                    }
                }.data
            json.decodeFromString<List<PartyLedgerEntryEntity>>(responseText)
        }
        return result.getOrDefault(emptyList())
    }

    /**
     * Inserts or updates a party in Supabase.
     */
    suspend fun saveParty(party: PartyEntity, address: PartyAddressEntity?): PartyEntity? {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val responseText = SupabaseClient.client.postgrest.from("parties")
                .upsert(party) {
                    select()
                }.data
            val savedParty = json.decodeFromString<PartyEntity>(responseText)

            if (address != null) {
                SupabaseClient.client.postgrest.from("party_addresses")
                    .upsert(address.copy(partyId = savedParty.id))
            }
            savedParty
        }
        return result.getOrNull()
    }

    /**
     * Calls Supabase RPC `fn_record_party_payment_transaction` to record live settlement.
     */
    suspend fun recordPartyPayment(
        businessId: String,
        partyId: String,
        txnType: String,
        amount: Double,
        paymentMode: String,
        refNo: String?,
        notes: String?
    ): RecordPartyPaymentResponseDto? {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val params = buildJsonObject {
                put("p_business_id", businessId)
                put("p_party_id", partyId)
                put("p_txn_type", txnType)
                put("p_amount", amount)
                put("p_payment_mode", paymentMode)
                put("p_reference_number", refNo)
                put("p_notes", notes)
            }
            val responseText = SupabaseClient.client.postgrest.rpc(
                "fn_record_party_payment_transaction",
                params
            ).data
            Log.d("StoreRemote", "recordPartyPayment response: $responseText")
            json.decodeFromString<RecordPartyPaymentResponseDto>(responseText)
        }
        return result.getOrNull()
    }

    /**
     * Fetches branches for a business from Supabase.
     */
    suspend fun fetchBranches(businessId: String): List<BusinessBranchEntity> {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val responseText = SupabaseClient.client.postgrest.from("business_branches")
                .select {
                    filter {
                        eq("business_id", businessId)
                        eq("is_deleted", false)
                    }
                }.data
            json.decodeFromString<List<BusinessBranchEntity>>(responseText)
        }
        return result.getOrDefault(emptyList())
    }

    /**
     * Saves or updates a branch in Supabase.
     */
    suspend fun saveBranch(branch: BusinessBranchEntity): BusinessBranchEntity? {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val responseText = SupabaseClient.client.postgrest.from("business_branches")
                .upsert(branch) {
                    select()
                }.data
            json.decodeFromString<BusinessBranchEntity>(responseText)
        }
        return result.getOrNull()
    }

    /**
     * Fetches staff members for a business from Supabase.
     */
    suspend fun fetchStaffMembers(businessId: String): List<BusinessStaffEntity> {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val responseText = SupabaseClient.client.postgrest.from("business_staff_members")
                .select {
                    filter {
                        eq("business_id", businessId)
                        eq("is_deleted", false)
                    }
                }.data
            json.decodeFromString<List<BusinessStaffEntity>>(responseText)
        }
        return result.getOrDefault(emptyList())
    }

    /**
     * Saves or updates a staff member in Supabase.
     */
    suspend fun saveStaffMember(staff: BusinessStaffEntity): BusinessStaffEntity? {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val responseText = SupabaseClient.client.postgrest.from("business_staff_members")
                .upsert(staff) {
                    select()
                }.data
            json.decodeFromString<BusinessStaffEntity>(responseText)
        }
        return result.getOrNull()
    }

    /**
     * Soft deletes a staff member in Supabase.
     */
    suspend fun deleteStaffMember(staffId: String): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val nowIso = java.time.Instant.now().toString()
            val payload = buildJsonObject {
                put("is_deleted", true)
                put("is_active", false)
                put("updated_at", nowIso)
            }
            SupabaseClient.client.postgrest.from("business_staff_members")
                .update(payload) {
                    filter {
                        eq("id", staffId)
                    }
                }
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Fetches delivery riders for a business from Supabase.
     */
    suspend fun fetchRiders(businessId: String): List<DeliveryRiderEntity> {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val responseText = SupabaseClient.client.postgrest.from("delivery_riders")
                .select {
                    filter {
                        eq("business_id", businessId)
                        eq("is_deleted", false)
                    }
                }.data
            json.decodeFromString<List<DeliveryRiderEntity>>(responseText)
        }
        return result.getOrDefault(emptyList())
    }

    /**
     * Saves or updates a delivery rider in Supabase.
     */
    suspend fun saveDeliveryRider(rider: DeliveryRiderEntity): DeliveryRiderEntity? {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val responseText = SupabaseClient.client.postgrest.from("delivery_riders")
                .upsert(rider) {
                    select()
                }.data
            json.decodeFromString<DeliveryRiderEntity>(responseText)
        }
        return result.getOrNull()
    }

    /**
     * Toggles a delivery rider's online/offline duty status.
     */
    suspend fun toggleRiderDutyStatus(businessId: String, riderId: String, isOnline: Boolean): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val nowIso = java.time.Instant.now().toString()
            val payload = buildJsonObject {
                put("is_online", isOnline)
                put("updated_at", nowIso)
            }
            SupabaseClient.client.postgrest.from("delivery_riders")
                .update(payload) {
                    filter {
                        eq("id", riderId)
                        eq("business_id", businessId)
                    }
                }
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Fetches unsettled deliveries for calculating rider COD cash in hand.
     */
    suspend fun fetchUnsettledDeliveries(businessId: String): List<OrderDeliveryEntity> {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val responseText = SupabaseClient.client.postgrest.from("order_deliveries")
                .select {
                    filter {
                        eq("delivery_status", "DELIVERED")
                        eq("cash_submitted_to_shop", false)
                        eq("is_deleted", false)
                    }
                }.data
            json.decodeFromString<List<OrderDeliveryEntity>>(responseText)
        }
        return result.getOrDefault(emptyList())
    }

    /**
     * Settles rider COD cash in Supabase.
     */
    suspend fun settleRiderCash(businessId: String, riderId: String): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val nowIso = java.time.Instant.now().toString()
            val payload = buildJsonObject {
                put("cash_submitted_to_shop", true)
                put("updated_at", nowIso)
            }
            SupabaseClient.client.postgrest.from("order_deliveries")
                .update(payload) {
                    filter {
                        eq("rider_id", riderId)
                        eq("delivery_status", "DELIVERED")
                        eq("cash_submitted_to_shop", false)
                    }
                }
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Updates business profile in Supabase.
     */
    suspend fun updateBusiness(business: BusinessEntity): BusinessEntity? {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val responseText = SupabaseClient.client.postgrest.from("businesses")
                .upsert(business) {
                    select()
                }.data
            json.decodeFromString<BusinessEntity>(responseText)
        }
        return result.getOrNull()
    }

    /**
     * Saves or updates business settings in Supabase.
     */
    suspend fun saveBusinessSettings(settings: BusinessSettingsEntity): BusinessSettingsEntity? {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val responseText = SupabaseClient.client.postgrest.from("business_settings")
                .upsert(settings) {
                    select()
                }.data
            json.decodeFromString<BusinessSettingsEntity>(responseText)
        }
        return result.getOrNull()
    }

    /**
     * Updates bank and UPI details in businesses table.
     */
    suspend fun updateBankAndUpi(
        businessId: String,
        upiId: String?,
        bankName: String?,
        accountNo: String?,
        ifsc: String?,
        branch: String?
    ): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                if (upiId != null) put("upi_id", upiId)
                if (bankName != null) put("bank_name", bankName)
                if (accountNo != null) put("bank_account_no", accountNo)
                if (ifsc != null) put("bank_ifsc", ifsc)
                if (branch != null) put("bank_branch", branch)
            }
            SupabaseClient.client.postgrest.from("businesses")
                .update(payload) {
                    filter {
                        eq("id", businessId)
                    }
                }
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Updates GST and E-Way Bill settings in Supabase.
     */
    suspend fun updateGstEwaySettings(
        businessId: String,
        gstin: String?,
        username: String?,
        password: String?,
        gspProvider: String,
        autoEway: Boolean
    ): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            if (!gstin.isNullOrBlank()) {
                SupabaseClient.client.postgrest.from("businesses")
                    .update(buildJsonObject { put("gstin", gstin) }) {
                        filter { eq("id", businessId) }
                    }
            }
            val settingsPayload = buildJsonObject {
                if (username != null) put("gst_api_username", username)
                if (password != null) put("gst_api_password", password)
                put("gsp_provider_name", gspProvider)
                put("enable_auto_eway_bill", autoEway)
            }
            SupabaseClient.client.postgrest.from("business_settings")
                .update(settingsPayload) {
                    filter { eq("business_id", businessId) }
                }
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Updates order status in Supabase via fn_update_order_kds_status RPC.
     */
    suspend fun updateKdsOrderStatus(
        businessId: String,
        orderId: String,
        newStatus: String,
        riderId: String? = null,
        otpCode: String? = null
    ): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                put("p_business_id", businessId)
                put("p_order_id", orderId)
                put("p_new_status", newStatus)
                if (!riderId.isNullOrBlank()) put("p_rider_id", riderId)
                if (!otpCode.isNullOrBlank()) put("p_otp_code", otpCode)
            }
            SupabaseClient.client.postgrest.rpc("fn_update_order_kds_status", payload)
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Fetches audited business financial reports from Supabase RPC fn_get_business_financial_reports.
     */
    suspend fun getBusinessFinancialReports(
        businessId: String,
        startDate: String,
        endDate: String
    ): JsonObject? {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                put("p_business_id", businessId)
                put("p_start_date", startDate)
                put("p_end_date", endDate)
            }
            SupabaseClient.client.postgrest.rpc("fn_get_business_financial_reports", payload).decodeAs<JsonObject>()
        }
        return result.getOrNull()
    }

    /**
     * Records a supplier purchase bill atomically via fn_create_purchase_bill_transaction RPC.
     */
    suspend fun createPurchaseBillTransaction(
        businessId: String,
        branchId: String,
        supplierId: String,
        supplierBillNo: String,
        billDate: String,
        purchaseItems: JsonArray,
        taxableAmount: Double,
        cgstAmount: Double,
        sgstAmount: Double,
        igstAmount: Double,
        grandTotal: Double,
        paidAmount: Double,
        paymentMode: String,
        notes: String? = null
    ): Boolean {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                put("p_business_id", businessId)
                put("p_branch_id", branchId)
                put("p_supplier_id", supplierId)
                put("p_supplier_bill_no", supplierBillNo)
                put("p_bill_date", billDate)
                put("p_purchase_items", purchaseItems)
                put("p_taxable_amount", taxableAmount)
                put("p_cgst_amount", cgstAmount)
                put("p_sgst_amount", sgstAmount)
                put("p_igst_amount", igstAmount)
                put("p_grand_total", grandTotal)
                put("p_paid_amount", paidAmount)
                put("p_payment_mode", paymentMode)
                if (!notes.isNullOrBlank()) put("p_notes", notes)
            }
            SupabaseClient.client.postgrest.rpc("fn_create_purchase_bill_transaction", payload)
            true
        }
        return result.getOrDefault(false)
    }

    /**
     * Searches public.user_profiles by @username for linking staff members with actual app accounts.
     */
    suspend fun searchUserProfileByUsername(username: String): StaffLinkedUserDto? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val cleanUsername = username.trim().removePrefix("@").lowercase()
        if (cleanUsername.length < 2) return@withContext null

        val result = SafeSupabaseInvoker.safeSupabaseCall {
            SupabaseClient.client.postgrest["user_profiles"]
                .select {
                    filter {
                        eq("username", cleanUsername)
                    }
                    limit(1)
                }
                .decodeSingleOrNull<StaffLinkedUserDto>()
        }
        result.getOrNull()
    }

    /**
     * Upserts staff salary profile into public.business_staff_salary_profiles.
     */
    suspend fun saveStaffSalaryProfile(profile: BusinessStaffSalaryProfileEntity): Boolean = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                put("id", profile.id)
                put("business_id", profile.businessId)
                if (!profile.branchId.isNullOrBlank()) put("branch_id", profile.branchId)
                put("staff_id", profile.staffId)
                put("salary_type", profile.salaryType)
                put("base_salary", profile.baseSalary)
                put("working_days_per_month", profile.workingDaysPerMonth)
                if (!profile.bankName.isNullOrBlank()) put("bank_name", profile.bankName)
                if (!profile.accountNumber.isNullOrBlank()) put("account_number", profile.accountNumber)
                if (!profile.ifscCode.isNullOrBlank()) put("ifsc_code", profile.ifscCode)
                if (!profile.accountHolderName.isNullOrBlank()) put("account_holder_name", profile.accountHolderName)
                if (!profile.upiId.isNullOrBlank()) put("upi_id", profile.upiId)
                put("is_active", profile.isActive)
                put("is_deleted", profile.isDeleted)
                put("created_at", profile.createdAt)
                put("updated_at", profile.updatedAt)
                put("sync_version", profile.syncVersion)
            }
            SupabaseClient.client.postgrest["business_staff_salary_profiles"]
                .upsert(payload)
            true
        }
        result.getOrDefault(false)
    }

    /**
     * Records or updates single staff attendance in public.business_staff_attendance.
     */
    suspend fun recordStaffAttendance(attendance: BusinessStaffAttendanceEntity): Boolean = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                put("id", attendance.id)
                put("business_id", attendance.businessId)
                if (!attendance.branchId.isNullOrBlank()) put("branch_id", attendance.branchId)
                put("staff_id", attendance.staffId)
                put("attendance_date", attendance.attendanceDate)
                put("status", attendance.status)
                if (!attendance.checkInTime.isNullOrBlank()) put("check_in_time", attendance.checkInTime)
                if (!attendance.checkOutTime.isNullOrBlank()) put("check_out_time", attendance.checkOutTime)
                if (!attendance.remarks.isNullOrBlank()) put("remarks", attendance.remarks)
                put("is_active", attendance.isActive)
                put("is_deleted", attendance.isDeleted)
                put("created_at", attendance.createdAt)
                put("updated_at", attendance.updatedAt)
                put("sync_version", attendance.syncVersion)
            }
            SupabaseClient.client.postgrest["business_staff_attendance"]
                .upsert(payload)
            true
        }
        result.getOrDefault(false)
    }

    /**
     * Records staff salary payment or advance disbursement via fn_record_staff_salary_payment RPC.
     */
    suspend fun recordStaffSalaryPayment(
        businessId: String,
        staffId: String,
        payoutId: String? = null,
        paymentType: String = "SALARY_PAYMENT",
        amountPaid: Double,
        paymentMode: String = "CASH",
        transactionRef: String? = null,
        remarks: String? = null
    ): Boolean = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                put("p_business_id", businessId)
                put("p_staff_id", staffId)
                if (!payoutId.isNullOrBlank()) put("p_payout_id", payoutId)
                put("p_payment_type", paymentType)
                put("p_amount_paid", amountPaid)
                put("p_payment_mode", paymentMode)
                if (!transactionRef.isNullOrBlank()) put("p_transaction_ref", transactionRef)
                if (!remarks.isNullOrBlank()) put("p_remarks", remarks)
            }
            SupabaseClient.client.postgrest.rpc("fn_record_staff_salary_payment", payload)
            true
        }
        result.getOrDefault(false)
    }

    /**
     * Calculates & generates locked/unlocked monthly payroll slips via fn_generate_monthly_staff_payroll RPC.
     */
    suspend fun generateMonthlyPayroll(
        businessId: String,
        month: Int,
        year: Int,
        lockMonth: Boolean = false
    ): Boolean = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                put("p_business_id", businessId)
                put("p_month", month)
                put("p_year", year)
                put("p_lock_month", lockMonth)
            }
            SupabaseClient.client.postgrest.rpc("fn_generate_monthly_staff_payroll", payload)
            true
        }
        result.getOrDefault(false)
    }

    /**
     * Executes multi-branch stock transfer via fn_transfer_branch_stock RPC.
     */
    suspend fun transferBranchStock(
        businessId: String,
        fromBranchId: String,
        toBranchId: String,
        itemId: String,
        quantity: Double,
        notes: String? = null
    ): Boolean = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                put("p_business_id", businessId)
                put("p_from_branch_id", fromBranchId)
                put("p_to_branch_id", toBranchId)
                put("p_item_id", itemId)
                put("p_quantity", quantity)
                if (!notes.isNullOrBlank()) put("p_notes", notes)
            }
            SupabaseClient.client.postgrest.rpc("fn_transfer_branch_stock", payload)
            true
        }
        result.getOrDefault(false)
    }

    /**
     * Records a new store expense via atomic fn_record_business_expense RPC.
     */
    suspend fun recordExpense(
        businessId: String,
        branchId: String?,
        expenseTypeId: String,
        title: String,
        amount: Double,
        paymentMode: String,
        expenseDate: String,
        referenceType: String = "NONE",
        referenceId: String? = null,
        paidTo: String? = null,
        notes: String? = null
    ): Result<String> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                put("p_business_id", businessId)
                if (!branchId.isNullOrBlank()) put("p_branch_id", branchId)
                put("p_expense_type_id", expenseTypeId)
                put("p_title", title)
                put("p_amount", amount)
                put("p_payment_mode", paymentMode)
                put("p_expense_date", expenseDate)
                put("p_reference_type", referenceType)
                if (!referenceId.isNullOrBlank()) put("p_reference_id", referenceId)
                if (!paidTo.isNullOrBlank()) put("p_paid_to", paidTo)
                if (!notes.isNullOrBlank()) put("p_notes", notes)
            }
            val response = SupabaseClient.client.postgrest.rpc("fn_record_business_expense", payload).data
            val jsonObject = json.parseToJsonElement(response).jsonObject
            val success = jsonObject["success"]?.jsonPrimitive?.booleanOrNull ?: false
            if (!success) {
                val errorMsg = jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: "Failed to record expense"
                throw Exception(errorMsg)
            }
            jsonObject["voucher_number"]?.jsonPrimitive?.contentOrNull ?: "EXP-VOUCHER"
        }
    }

    /**
     * Soft deletes an expense record on Supabase.
     */
    suspend fun deleteExpense(expenseId: String): Boolean = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val result = SafeSupabaseInvoker.safeSupabaseCall {
            val payload = buildJsonObject {
                put("is_deleted", true)
                put("updated_at", java.time.Instant.now().toString())
            }
            SupabaseClient.client.postgrest["business_expenses"].update(payload) {
                filter {
                    eq("id", expenseId)
                }
            }
            true
        }
        result.getOrDefault(false)
    }
}

