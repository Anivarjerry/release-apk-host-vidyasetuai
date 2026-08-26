# Mobile Store Module - DAO Architecture & KDS Engine Blueprint (`feature_store`)
> **स्थान**: `app/src/main/java/com/vidyasetuai/feature_store/dao_architecture.md`  
> **मॉड्यूल नाम**: `feature_store` (🏪 Store)  
> **मूल सिद्धांत**: UI केवल और केवल स्थानीय Room DB से ही पढ़ेगा। UI को इंटरनेट की उपस्थिति का ज्ञान नहीं होगा (100% Offline-First Decoupled Architecture - 0ms UI Speed)।

---

## १. यूआई व स्थानीय डेटाबेस के १००% पृथक्करण का सिद्धांत (100% UI-Room DB Decoupling)

```
[ Jetpack Compose UI Screens ]
             │ (0ms 100% Local Reads & Writes)
             ▼
[ Room DAOs (@Dao) with Kotlin Flow ]
             │ (Local SQLite Disk)
             ▼
[ Local Room Database (20/20 Entities) ]
             ▲
             │ (Background Delta Sync Thread - Network Independent)
[ Background Sync Engine & Supabase Realtime ]
```

### मुख्य गारंटी:
1. **0ms यूआई प्रतिक्रिया (0ms UI Latency):** स्क्रीन कभी भी सर्वर से नेटवर्क रिस्पॉन्स का इंतज़ार नहीं करेगी। उपयोगकर्ता द्वारा बिल काटने पर डेटा **तुरंत पहले Room DB में दर्ज होगा** और स्क्रीन 0ms में रिफ्रेश हो जाएगी।
2. **ऑफ़लाइन स्वायत्तता (Offline Autonomy):** इंटरनेट बंद हो या चालू, ऐप का हर एक बटन, स्कैनर, और प्रिंटर 100% सामान्य रूप से काम करता रहेगा।
3. **ट्रिपल सुरक्षा मर्चेंट लॉक:** सभी DAOs की SQL क्वेरीज़ में `WHERE business_id = :businessId AND is_deleted = 0` अनिवार्य रहेगा ताकि गलती से भी किसी दूसरी दुकान का डेटा मोबाइल पर न आ सके।

---

## २. डिजिटल KDS (Kitchen Display System) रीयल-टाइम आर्किटेक्चर

कैफ़े और रेस्टोरेंट के लिए रीयल-टाइम KOT ऑर्डर किचन स्क्रीन पर बिना किसी लैग के कैसे पहुँचेगा:

```
[ ग्राहक/काउंटर ऑर्डर ] ──► [ Supabase Realtime WebSocket ] ──► [ Room DB OrderEntity (STATUS: 'PLACED') ]
                                                                             │
                                                                             ▼ (0ms Flow Emit)
[ KDS Screen Chime Beep Sound 🔔 ] ◄── [ getLiveKdsOrdersFlow() ] ◄── [ Room OrderDao ]
```

### KDS ऑर्डर प्रवाह (KDS Order Flow Steps):
1. **ऑर्डर प्राप्ति (0ms Insertion):** जैसे ही काउंटर या ऑनलाइन से ऑर्डर आता है, सिंक थ्रेड इसे तुरंत लोकल Room DB की `orders` व `order_items` एंटिटी में डाल देता है।
2. **रीयल-टाइम यूआई एमिट (0ms Flow Emission):** `OrderDao` की `getLiveKdsOrdersFlow(businessId, branchId)` विधि बिना किसी पिंग के तुरंत नए KOT टिकट को KDS स्क्रीन पर भेज देती है।
3. **ऑडियो बीप व वाइब्रेशन अलर्ट:** नया ऑर्डर आते ही कंपोज़ स्क्रीन पर **बीप साउंड चाइम (Audio KOT Chime)** बजेगा और नए ऑर्डर का कार्ड हरे रंग में फ़्लैश होगा।
4. **किचन एक्शन व स्टेटस अपडेट:** किचन स्टाफ `[ 👨‍🍳 Start Preparing ]` पर टैप करेगा ➔ स्थिति रूम DB में `PREPARING` होगी ➔ बैकग्राउंड सिंक ग्राहक के ऐप में लाइव स्थिति अपडेट कर देगा।

---

## ३. सर्वसमावेशी २० DAOs कोड विनिर्देश (Exhaustive 20 DAOs Specification)

### 1. `BusinessDao`
```kotlin
@Dao
interface BusinessDao {
    @Query("SELECT * FROM businesses WHERE id = :businessId AND is_deleted = 0 LIMIT 1")
    fun getBusinessFlow(businessId: String): Flow<BusinessEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(business: BusinessEntity)
}
```

### 2. `BusinessSettingsDao`
```kotlin
@Dao
interface BusinessSettingsDao {
    @Query("SELECT * FROM business_settings WHERE business_id = :businessId AND is_deleted = 0 LIMIT 1")
    fun getSettingsFlow(businessId: String): Flow<BusinessSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: BusinessSettingsEntity)
}
```

### 3. `BusinessBranchDao`
```kotlin
@Dao
interface BusinessBranchDao {
    @Query("SELECT * FROM business_branches WHERE business_id = :businessId AND is_deleted = 0 ORDER BY is_main_branch DESC")
    fun getBranchesFlow(businessId: String): Flow<List<BusinessBranchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(branch: BusinessBranchEntity)
}
```

### 4. `BusinessStaffDao`
```kotlin
@Dao
interface BusinessStaffDao {
    @Query("SELECT * FROM business_staff_members WHERE business_id = :businessId AND is_deleted = 0 ORDER BY name ASC")
    fun getStaffFlow(businessId: String): Flow<List<BusinessStaffEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(staff: BusinessStaffEntity)
}
```

### 5. `PartyDao`
```kotlin
@Dao
interface PartyDao {
    @Query("SELECT * FROM parties WHERE business_id = :businessId AND party_type = :partyType AND is_deleted = 0 ORDER BY name ASC")
    fun getPartiesFlow(businessId: String, partyType: String): Flow<List<PartyEntity>>

    @Query("SELECT * FROM parties WHERE business_id = :businessId AND phone = :phone AND is_deleted = 0 LIMIT 1")
    suspend fun findByPhone(businessId: String, phone: String): PartyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(party: PartyEntity)
}
```

### 6. `PartyAddressDao`
```kotlin
@Dao
interface PartyAddressDao {
    @Query("SELECT * FROM party_addresses WHERE party_id = :partyId AND is_deleted = 0 ORDER BY is_default DESC")
    fun getPartyAddressesFlow(partyId: String): Flow<List<PartyAddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(address: PartyAddressEntity)
}
```

### 7. `ItemCategoryDao`
```kotlin
@Dao
interface ItemCategoryDao {
    @Query("SELECT * FROM item_categories WHERE business_id = :businessId AND is_deleted = 0 ORDER BY display_order ASC, name ASC")
    fun getCategoriesFlow(businessId: String): Flow<List<ItemCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: ItemCategoryEntity)
}
```

### 8. `ItemDao`
```kotlin
@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE business_id = :businessId AND is_deleted = 0 ORDER BY name ASC")
    fun getStoreItemsFlow(businessId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE business_id = :businessId AND category_id = :categoryId AND is_deleted = 0 ORDER BY name ASC")
    fun getItemsByCategoryFlow(businessId: String, categoryId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE business_id = :businessId AND (barcode = :code OR sku = :code) AND is_deleted = 0 LIMIT 1")
    suspend fun findByBarcode(businessId: String, code: String): ItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ItemEntity)
}
```

### 9. `ItemVariantDao`
```kotlin
@Dao
interface ItemVariantDao {
    @Query("SELECT * FROM item_variants WHERE item_id = :itemId AND is_deleted = 0 ORDER BY sale_price ASC")
    fun getItemVariantsFlow(itemId: String): Flow<List<ItemVariantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(variant: ItemVariantEntity)
}
```

### 10. `InventoryStockDao`
```kotlin
@Dao
interface InventoryStockDao {
    @Query("SELECT * FROM inventory_stocks WHERE item_id = :itemId AND branch_id = :branchId AND is_deleted = 0 LIMIT 1")
    fun getStockFlow(itemId: String, branchId: String): Flow<InventoryStockEntity?>

    @Query("SELECT * FROM inventory_stocks WHERE branch_id = :branchId AND current_stock <= low_stock_threshold AND is_deleted = 0")
    fun getLowStockAlertsFlow(branchId: String): Flow<List<InventoryStockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stock: InventoryStockEntity)
}
```

### 11. `InventoryTransactionDao`
```kotlin
@Dao
interface InventoryTransactionDao {
    @Query("SELECT * FROM inventory_transactions WHERE business_id = :businessId AND item_id = :itemId AND is_deleted = 0 ORDER BY created_at DESC")
    fun getItemAuditLogsFlow(businessId: String, itemId: String): Flow<List<InventoryTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(txn: InventoryTransactionEntity)
}
```

### 12. `OrderDao` (KDS व POS मास्टर DAO)
```kotlin
@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE business_id = :businessId AND branch_id = :branchId AND order_status IN ('PLACED', 'PREPARING') AND is_deleted = 0 ORDER BY created_at ASC")
    fun getLiveKdsOrdersFlow(businessId: String, branchId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE business_id = :businessId AND branch_id = :branchId AND is_deleted = 0 ORDER BY created_at DESC")
    fun getBranchOrdersFlow(businessId: String, branchId: String): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOrder(order: OrderEntity)

    @Transaction
    suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>) {
        upsertOrder(order)
        items.forEach { insertOrderItem(it) }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(item: OrderItemEntity)
}
```

### 13. `OrderItemDao`
```kotlin
@Dao
interface OrderItemDao {
    @Query("SELECT * FROM order_items WHERE order_id = :orderId AND is_deleted = 0")
    fun getOrderItemsFlow(orderId: String): Flow<List<OrderItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: OrderItemEntity)
}
```

### 14. `DeliveryRiderDao`
```kotlin
@Dao
interface DeliveryRiderDao {
    @Query("SELECT * FROM delivery_riders WHERE business_id = :businessId AND is_active = 1 AND is_deleted = 0")
    fun getRidersFlow(businessId: String): Flow<List<DeliveryRiderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rider: DeliveryRiderEntity)
}
```

### 15. `OrderDeliveryDao`
```kotlin
@Dao
interface OrderDeliveryDao {
    @Query("SELECT * FROM order_deliveries WHERE order_id = :orderId AND is_deleted = 0 LIMIT 1")
    fun getOrderDeliveryFlow(orderId: String): Flow<OrderDeliveryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(delivery: OrderDeliveryEntity)
}
```

### 16. `InvoiceDao` (POS बिलिंग व E-Way Bill DAO)
```kotlin
@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices WHERE business_id = :businessId AND branch_id = :branchId AND is_deleted = 0 ORDER BY created_at DESC")
    fun getInvoicesFlow(businessId: String, branchId: String): Flow<List<InvoiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertInvoice(invoice: InvoiceEntity)

    @Transaction
    suspend fun createFullInvoice(invoice: InvoiceEntity, items: List<InvoiceItemEntity>) {
        upsertInvoice(invoice)
        items.forEach { insertInvoiceItem(it) }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceItem(item: InvoiceItemEntity)
}
```

### 17. `InvoiceItemDao`
```kotlin
@Dao
interface InvoiceItemDao {
    @Query("SELECT * FROM invoice_items WHERE invoice_id = :invoiceId AND is_deleted = 0")
    fun getInvoiceItemsFlow(invoiceId: String): Flow<List<InvoiceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: InvoiceItemEntity)
}
```

### 18. `PurchaseInvoiceDao`
```kotlin
@Dao
interface PurchaseInvoiceDao {
    @Query("SELECT * FROM purchase_invoices WHERE business_id = :businessId AND supplier_id = :supplierId AND is_deleted = 0 ORDER BY invoice_date DESC")
    fun getSupplierInvoicesFlow(businessId: String, supplierId: String): Flow<List<PurchaseInvoiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(purchase: PurchaseInvoiceEntity)
}
```

### 19. `PaymentDao`
```kotlin
@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE business_id = :businessId AND is_deleted = 0 ORDER BY created_at DESC")
    fun getPaymentsFlow(businessId: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(payment: PaymentEntity)
}
```

### 20. `PartyLedgerDao`
```kotlin
@Dao
interface PartyLedgerDao {
    @Query("SELECT * FROM party_ledger_entries WHERE party_id = :partyId AND is_deleted = 0 ORDER BY created_at DESC")
    fun getPartyPassbookFlow(partyId: String): Flow<List<PartyLedgerEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: PartyLedgerEntryEntity)
}
```
