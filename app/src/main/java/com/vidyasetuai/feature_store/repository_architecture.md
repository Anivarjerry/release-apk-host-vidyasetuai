# Mobile Store Module - Repository Layer Architecture Blueprint (`feature_store`)
> **स्थान**: `app/src/main/java/com/vidyasetuai/feature_store/repository_architecture.md`  
> **मॉड्यूल नाम**: `feature_store` (🏪 Store)  
> **भूमिका**: Single Source of Truth (SSOT) - रूम लोकल DB और Supabase नेटवर्क सिंक का एकीकरण।

---

## १. रिपॉजिटरी परत का मूल सिद्धांत (Core Repository Principles)

1. **सिंगल सोर्स ऑफ ट्रुथ (Single Source of Truth - SSOT):**
   - व्यू-मॉडल (ViewModel) या यूज़-केस (UseCase) कभी भी सीधे नेटवर्क API को कॉल नहीं करेगा। वे केवल `StoreRepository` से डेटा माँगेंगे।
2. **ऑफ़लाइन-फर्स्ट राइट लॉजिक (Offline-First Write):**
   - जब भी मर्चेंट नया बिल काटेगा या प्रोडक्ट जोड़ेगा, रिपॉजिटरी पहले इसे तुरंत **Room DB में दर्ज करेगी** (status: `PENDING_INSERT`) और फिर बैकग्राउंड सिंक थ्रेड को कॉल करेगी।
3. **मल्टी-टेनेंट सुरक्षा लॉजिक (Tenant Security Scope):**
   - रिपॉजिटरी हर एक कॉल में स्वचालित रूप से `activeBusinessId` और `activeBranchId` अटैच करेगी ताकि गलती से भी किसी दूसरी दुकान का डेटा लीक न हो।
4. **कॉन्फ़्लिक्ट समाधान नियम (Conflict Resolution):**
   - यदि ऑफ़लाइन और ऑनलाइन डेटा में टकराव होता है, तो उच्च `sync_version` या नवीनतम `updated_at` टाइमस्टैम्प वाला डेटा मान्य होगा।

---

## २. रिपॉजिटरी परत का आर्किटेक्चर चित्र (Architecture Flow)

```
[ Domain UseCases / ViewModels ]
              │
              ▼
[ StoreRepository Interface ]
              │
              ▼
[ StoreRepositoryImpl (Concrete Logic) ]
        ├──► Local Operation ──► [ 20 Room DB DAOs ] (0ms Immediate UI)
        └──► Async Sync Job   ──► [ Supabase Network Client ] (Background Retry)
```

---

## ३. ३ मुख्य रिपॉजिटरी इंटरफेसेस (The 3 Core Repositories)

### A. `StoreCatalogRepository` (कैटलॉग, प्रोडक्ट्स व स्टॉक)
```kotlin
interface StoreCatalogRepository {
    fun getCategoriesFlow(businessId: String): Flow<List<ItemCategoryEntity>>
    fun getStoreItemsFlow(businessId: String): Flow<List<ItemEntity>>
    fun getItemsByCategoryFlow(businessId: String, categoryId: String): Flow<List<ItemEntity>>
    suspend fun findItemByBarcode(businessId: String, barcode: String): ItemEntity?
    fun getStockFlow(itemId: String, branchId: String): Flow<InventoryStockEntity?>
    fun getLowStockAlertsFlow(branchId: String): Flow<List<InventoryStockEntity>>
    suspend fun saveItem(item: ItemEntity, stock: InventoryStockEntity?): Boolean
    suspend fun updateStock(itemId: String, branchId: String, deltaQuantity: Double, notes: String?): Boolean
}
```

### B. `SalesOrderRepository` (POS बिलिंग, KDS ऑर्डर्स, इनवॉइस व ई-वे बिल)
```kotlin
interface SalesOrderRepository {
    fun getLiveKdsOrdersFlow(businessId: String, branchId: String): Flow<List<OrderEntity>>
    fun getBranchOrdersFlow(businessId: String, branchId: String): Flow<List<OrderEntity>>
    fun getInvoicesFlow(businessId: String, branchId: String): Flow<List<InvoiceEntity>>
    suspend fun createPosCheckoutOrder(order: OrderEntity, items: List<OrderItemEntity>): Boolean
    suspend fun createTaxInvoice(invoice: InvoiceEntity, items: List<InvoiceItemEntity>): Boolean
    suspend fun updateOrderStatus(orderId: String, newStatus: String): Boolean
    suspend fun assignRiderToOrder(orderId: String, riderId: String): Boolean
}
```

### C. `KhataRepository` (ग्राहक व सप्लायर बही-खाता लेजर)
```kotlin
interface KhataRepository {
    fun getPartiesFlow(businessId: String, partyType: String): Flow<List<PartyEntity>>
    fun getPartyAddressesFlow(partyId: String): Flow<List<PartyAddressEntity>>
    fun getPartyPassbookFlow(partyId: String): Flow<List<PartyLedgerEntryEntity>>
    suspend fun addKhataEntry(entry: PartyLedgerEntryEntity): Boolean
    suspend fun saveParty(party: PartyEntity, address: PartyAddressEntity?): Boolean
}
```
