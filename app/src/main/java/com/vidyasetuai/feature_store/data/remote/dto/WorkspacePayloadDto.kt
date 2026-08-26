package com.vidyasetuai.feature_store.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.vidyasetuai.feature_store.data.local.entity.*

@Serializable
data class StaffLinkedUserDto(
    @SerialName("id") val id: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null
)

@Serializable
data class WorkspacePayloadDto(
    @SerialName("business")
    val business: BusinessEntity? = null,

    @SerialName("settings")
    val settings: BusinessSettingsEntity? = null,

    @SerialName("branches")
    val branches: List<BusinessBranchEntity> = emptyList(),

    @SerialName("staff")
    val staff: List<BusinessStaffEntity> = emptyList(),

    @SerialName("parties")
    val parties: List<PartyEntity> = emptyList(),

    @SerialName("party_addresses")
    val partyAddresses: List<PartyAddressEntity> = emptyList(),

    @SerialName("categories")
    val categories: List<ItemCategoryEntity> = emptyList(),

    @SerialName("items")
    val items: List<ItemEntity> = emptyList(),

    @SerialName("item_variants")
    val itemVariants: List<ItemVariantEntity> = emptyList(),

    @SerialName("inventory_stocks")
    val inventoryStocks: List<InventoryStockEntity> = emptyList(),

    @SerialName("inventory_transactions")
    val inventoryTransactions: List<InventoryTransactionEntity> = emptyList(),

    @SerialName("orders")
    val orders: List<OrderEntity> = emptyList(),

    @SerialName("order_items")
    val orderItems: List<OrderItemEntity> = emptyList(),

    @SerialName("delivery_riders")
    val deliveryRiders: List<DeliveryRiderEntity> = emptyList(),

    @SerialName("order_deliveries")
    val orderDeliveries: List<OrderDeliveryEntity> = emptyList(),

    @SerialName("invoices")
    val invoices: List<InvoiceEntity> = emptyList(),

    @SerialName("invoice_items")
    val invoiceItems: List<InvoiceItemEntity> = emptyList(),

    @SerialName("purchase_invoices")
    val purchaseInvoices: List<PurchaseInvoiceEntity> = emptyList(),

    @SerialName("payments")
    val payments: List<PaymentEntity> = emptyList(),

    @SerialName("party_ledger_entries")
    val partyLedgerEntries: List<PartyLedgerEntryEntity> = emptyList(),

    @SerialName("staff_salary_profiles")
    val staffSalaryProfiles: List<BusinessStaffSalaryProfileEntity> = emptyList(),

    @SerialName("staff_attendance")
    val staffAttendance: List<BusinessStaffAttendanceEntity> = emptyList(),

    @SerialName("staff_salary_payouts")
    val staffSalaryPayouts: List<BusinessStaffSalaryPayoutEntity> = emptyList(),

    @SerialName("staff_salary_payments")
    val staffSalaryPayments: List<BusinessStaffSalaryPaymentEntity> = emptyList(),

    @SerialName("expense_types")
    val expenseTypes: List<BusinessExpenseTypeEntity> = emptyList(),

    @SerialName("expenses")
    val expenses: List<BusinessExpenseEntity> = emptyList()
)
