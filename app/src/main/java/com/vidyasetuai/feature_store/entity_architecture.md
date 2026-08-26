# Mobile Store Module - Exhaustive Entity & Clean Architecture Blueprint (`feature_store`)
> **स्थान**: `app/src/main/java/com/vidyasetuai/feature_store/entity_architecture.md`  
> **मॉड्यूल नाम**: `feature_store` (🏪 Store)  
> **गारंटी**: Supabase PostgreSQL के सभी २० टेबल्स और हर एक कॉलम (100% Columns) का 1:1 सटीक एंड्रॉइड मैपिंग।

---

## १. सर्वसमावेशी २०/२० Android Room DB एंटिटीज एवं संपूर्ण कॉलम्स सूची

### 1. `BusinessEntity` (`@Entity(tableName = "businesses")`)
- `id: String` (PK UUID)
- `user_id: String`
- `business_type: String`
- `business_tier: String`
- `legal_name: String`
- `trade_name: String`
- `slug: String`
- `owner_name: String`
- `phone: String`
- `email: String?`
- `gstin: String?`
- `is_composition: Boolean`
- `pan_number: String?`
- `currency: String`
- `state_code: String`
- `bank_name: String?`
- `bank_account_no: String?`
- `bank_ifsc: String?`
- `bank_branch: String?`
- `upi_id: String?`
- `logo_url: String?`
- `banner_url: String?`
- `description: String?`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 2. `BusinessSettingsEntity` (`@Entity(tableName = "business_settings")`)
- `id: String` (PK UUID)
- `business_id: String`
- `enable_gst_billing: Boolean`
- `enable_kds: Boolean`
- `enable_delivery_tracking: Boolean`
- `enable_inventory_tracking: Boolean`
- `enable_customer_khata: Boolean`
- `allow_online_orders: Boolean`
- `invoice_prefix: String`
- `thermal_printer_size: String`
- `delivery_charge_default: Double`
- `packing_charge_default: Double`
- `free_delivery_above: Double?`
- `opening_time: String?`
- `closing_time: String?`
- `is_store_open: Boolean`
- `gst_api_username: String?`
- `gst_api_password: String?`
- `gsp_provider_name: String`
- `enable_auto_eway_bill: Boolean`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 3. `BusinessBranchEntity` (`@Entity(tableName = "business_branches")`)
- `id: String` (PK UUID)
- `business_id: String`
- `branch_name: String`
- `address_line: String`
- `city: String`
- `state: String`
- `pincode: String`
- `lat: Double?`
- `lng: Double?`
- `is_main_branch: Boolean`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 4. `BusinessStaffEntity` (`@Entity(tableName = "business_staff_members")`)
- `id: String` (PK UUID)
- `business_id: String`
- `branch_id: String?`
- `user_id: String?`
- `name: String`
- `phone: String`
- `email: String?`
- `role: String`
- `permissions_json: String`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 5. `PartyEntity` (`@Entity(tableName = "parties")`)
- `id: String` (PK UUID)
- `business_id: String`
- `user_id: String?`
- `party_type: String`
- `name: String`
- `phone: String`
- `email: String?`
- `gstin: String?`
- `pan_number: String?`
- `state_code: String?`
- `credit_limit: Double`
- `current_balance: Double`
- `opening_balance: Double`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 6. `PartyAddressEntity` (`@Entity(tableName = "party_addresses")`)
- `id: String` (PK UUID)
- `party_id: String`
- `user_id: String?`
- `address_type: String`
- `full_address: String`
- `landmark: String?`
- `city: String`
- `state: String`
- `pincode: String`
- `lat: Double?`
- `lng: Double?`
- `is_default: Boolean`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 7. `ItemCategoryEntity` (`@Entity(tableName = "item_categories")`)
- `id: String` (PK UUID)
- `business_id: String`
- `name: String`
- `parent_category_id: String?`
- `icon_url: String?`
- `display_order: Int`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 8. `ItemEntity` (`@Entity(tableName = "items")`)
- `id: String` (PK UUID)
- `business_id: String`
- `category_id: String?`
- `item_type: String`
- `name: String`
- `description: String?`
- `sku: String?`
- `barcode: String?`
- `hsn_sac_code: String?`
- `tax_rate: Double`
- `is_tax_inclusive: Boolean`
- `purchase_price: Double`
- `sale_price: Double`
- `mrp: Double?`
- `unit: String`
- `food_type: String?`
- `is_available_online: Boolean`
- `image_url: String?`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 9. `ItemVariantEntity` (`@Entity(tableName = "item_variants")`)
- `id: String` (PK UUID)
- `item_id: String`
- `variant_name: String`
- `sale_price: Double`
- `purchase_price: Double`
- `barcode: String?`
- `sku: String?`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 10. `InventoryStockEntity` (`@Entity(tableName = "inventory_stocks")`)
- `id: String` (PK UUID)
- `item_id: String`
- `variant_id: String?`
- `branch_id: String`
- `current_stock: Double` (Supports 3 decimal grams)
- `low_stock_threshold: Double`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 11. `InventoryTransactionEntity` (`@Entity(tableName = "inventory_transactions")`)
- `id: String` (PK UUID)
- `business_id: String`
- `branch_id: String`
- `item_id: String`
- `variant_id: String?`
- `txn_type: String`
- `quantity: Double`
- `balance_after: Double`
- `reference_id: String?`
- `notes: String?`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 12. `OrderEntity` (`@Entity(tableName = "orders")`)
- `id: String` (PK UUID)
- `business_id: String`
- `branch_id: String`
- `order_number: String`
- `party_id: String?`
- `user_id: String?`
- `customer_name: String`
- `customer_phone: String`
- `order_type: String`
- `table_or_token_no: String?`
- `order_status: String`
- `delivery_otp: String`
- `sub_total: Double`
- `tax_total: Double`
- `delivery_charge: Double`
- `packing_charge: Double`
- `discount_amount: Double`
- `grand_total: Double`
- `payment_status: String`
- `payment_method: String`
- `delivery_address_json: String?`
- `order_notes: String?`
- `cancelled_reason: String?`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 13. `OrderItemEntity` (`@Entity(tableName = "order_items")`)
- `id: String` (PK UUID)
- `order_id: String`
- `item_id: String?`
- `variant_id: String?`
- `item_name: String`
- `variant_name: String?`
- `quantity: Double`
- `unit_price: Double`
- `tax_rate: Double`
- `tax_amount: Double`
- `discount_amount: Double`
- `total_price: Double`
- `item_notes: String?`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 14. `DeliveryRiderEntity` (`@Entity(tableName = "delivery_riders")`)
- `id: String` (PK UUID)
- `business_id: String`
- `user_id: String`
- `name: String`
- `phone: String`
- `vehicle_number: String?`
- `vehicle_type: String`
- `is_online: Boolean`
- `current_lat: Double?`
- `current_lng: Double?`
- `heading: Double?`
- `speed: Double?`
- `last_ping_at: String?`
- `active_order_id: String?`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 15. `OrderDeliveryEntity` (`@Entity(tableName = "order_deliveries")`)
- `id: String` (PK UUID)
- `order_id: String`
- `rider_id: String`
- `delivery_status: String`
- `assigned_at: String`
- `picked_up_at: String?`
- `delivered_at: String?`
- `otp_entered: String?`
- `cash_collected_by_rider: Double`
- `cash_submitted_to_shop: Boolean`
- `delivery_notes: String?`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 16. `InvoiceEntity` (`@Entity(tableName = "invoices")`)
- `id: String` (PK UUID)
- `business_id: String`
- `branch_id: String`
- `order_id: String?`
- `party_id: String?`
- `user_id: String?`
- `invoice_type: String`
- `invoice_number: String`
- `invoice_date: String`
- `due_date: String?`
- `place_of_supply: String`
- `is_interstate: Boolean`
- `eway_bill_number: String?`
- `eway_bill_date: String?`
- `eway_bill_valid_until: String?`
- `vehicle_number: String?`
- `transport_name: String?`
- `transporter_id: String?`
- `distance_km: Int`
- `eway_status: String`
- `taxable_amount: Double`
- `cgst_amount: Double`
- `sgst_amount: Double`
- `igst_amount: Double`
- `cess_amount: Double`
- `delivery_charge: Double`
- `packing_charge: Double`
- `discount_total: Double`
- `round_off: Double`
- `grand_total: Double`
- `paid_amount: Double`
- `due_amount: Double`
- `payment_status: String`
- `notes: String?`
- `terms_conditions: String?`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 17. `InvoiceItemEntity` (`@Entity(tableName = "invoice_items")`)
- `id: String` (PK UUID)
- `invoice_id: String`
- `item_id: String?`
- `item_name: String`
- `hsn_sac_code: String?`
- `quantity: Double`
- `unit: String`
- `unit_rate: Double`
- `discount_amount: Double`
- `taxable_value: Double`
- `gst_rate: Double`
- `cgst_rate: Double`
- `cgst_amount: Double`
- `sgst_rate: Double`
- `sgst_amount: Double`
- `igst_rate: Double`
- `igst_amount: Double`
- `total_amount: Double`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 18. `PurchaseInvoiceEntity` (`@Entity(tableName = "purchase_invoices")`)
- `id: String` (PK UUID)
- `business_id: String`
- `branch_id: String`
- `supplier_id: String`
- `supplier_invoice_number: String`
- `invoice_date: String`
- `taxable_amount: Double`
- `cgst_amount: Double`
- `sgst_amount: Double`
- `igst_amount: Double`
- `grand_total: Double`
- `paid_amount: Double`
- `due_amount: Double`
- `payment_status: String`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 19. `PaymentEntity` (`@Entity(tableName = "payments")`)
- `id: String` (PK UUID)
- `business_id: String`
- `order_id: String?`
- `invoice_id: String?`
- `party_id: String?`
- `user_id: String?`
- `amount: Double`
- `payment_mode: String`
- `payment_status: String`
- `collected_by_type: String`
- `collected_by_user_id: String?`
- `gateway_provider: String?`
- `gateway_order_id: String?`
- `gateway_payment_id: String?`
- `gateway_signature: String?`
- `gateway_response_json: String?`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

### 20. `PartyLedgerEntryEntity` (`@Entity(tableName = "party_ledger_entries")`)
- `id: String` (PK UUID)
- `business_id: String`
- `party_id: String`
- `user_id: String?`
- `entry_type: String`
- `amount: Double`
- `balance_after: Double`
- `reference_type: String`
- `reference_id: String?`
- `description: String?`
- `is_active: Boolean`
- `is_deleted: Boolean`
- `created_at: String`
- `created_by: String?`
- `updated_at: String`
- `updated_by: String?`
- `sync_version: Long`
- `sync_status: String`

---

## २. एंड्रॉइड क्लीन आर्किटेक्चर के ५ चरण (Layer-by-Layer Architecture)

```
[ १. एंटिटीज (@Entity) ]
         │ (स्थानीय डेटाबेस टेबल्स)
         ▼
[ २. DAOs (@Dao Data Access Objects) ]
         │ (SQL क्वेरीज़, Insert/Update, Kotlin Flow Observables)
         ▼
[ ३. रिपॉजिटरीज़ (Repository Interface & Implementation) ]
         │ (Room DB + Supabase Network Sync का एकीकरण)
         ▼
[ ४. डोमेन यूज़ केसेस (Domain Use Cases) ]
         │ (बिज़नेस लॉजिक: SettlePosOrderUseCase, PrintBarcodeSheetUseCase)
         ▼
[ ५. व्यू-मॉडल व जेटपैक कंपोज़ UI (ViewModels & UI Screens) ]
           (StoreTabScreen.kt, PosCounterScreen.kt, KdsScreen.kt)
```
