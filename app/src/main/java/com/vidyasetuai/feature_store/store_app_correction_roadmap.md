# 📱 VidyaSetu Commerce Mobile App (`feature_store`) - Comprehensive Production Roadmap

**दस्तावेज़ का उद्देश्य (Document Purpose):**  
वेब ऐप्लिकेशन (`vidyasetu-ai`) में किए गए नवीनतम 0ms WatermelonDB (v4) आर्किटेक्चर, खरीद बिल (Purchase Invoices), खर्चे (Expenses), ग्राहक खाता (Parties & Khata), गल्ला (Daybook), मुख्य डैशबोर्ड (Business Command Center), और सुरक्षित लोकल डेट पार्सिंग के 1:1 समानता के आधार पर Android मोबाइल ऐप (`feature_store`) का संपूर्ण तकनीकी ब्लूप्रिंट व सुधार रोडमैप।

---

## 📌 1. मुख्य सारांश (Executive Summary of Architecture Upgrades)

वेब ऐप्लिकेशन और Supabase बैकएंड में किए गए सभी नवीनतम सुधारों को मोबाइल ऐप के Room Database व Jetpack Compose आर्किटेक्चर में 100% संरेखित (Align) किया गया है:

1. **⚡ 100% 0ms Offline-First Read Architecture:** सभी मुख्य ऑपरेशनल स्क्रीन सीधे स्थानीय Room DB `Flow<List<T>>` से 0ms में लोड होंगी। नेटवर्क कॉल केवल बैकग्राउंड में 2-वे सिंक के रूप में चलेगी।
2. **📦 खरीद बिल (Purchase Invoices) मॉड्यूल एकीकरण:** `purchase_invoices` और `purchase_invoice_items` (फॉरेन की: `purchase_id`) का स्थानीय Room DB कैश और ऑटो-स्टॉक इन (+Qty) ट्रांज़ैक्शन।
3. **👥 ग्राहक खाता व लेजर (Parties & Khata):** `parties` और `party_ledger_entries` का 0ms स्थानीय रेंडर और 1-टैप WhatsApp स्टेटमेंट जनरेटर।
4. **💵 खर्चे व पेट्टी कैश (Expenses & Petty Cash):** `business_expenses` और `business_expense_types` का वाउचर-आधारित स्थानीय प्रबंधन।
5. **📊 मुख्य डैशबोर्ड (Business Command Center):** 4 मुख्य KPI कार्ड्स, 1-पंक्ति क्विक एक्शन बार, 7-दिवसीय बिक्री ट्रेंड बार चार्ट (स्थानीय IST तारीख के अनुसार), और कम स्टॉक / उधारी अलर्ट्स।
6. **🪙 गल्ला (Daybook) व वित्तीय रिपोर्ट्स:** Supabase RPC `fn_get_business_financial_reports` आधारित 100% सटीक वित्तीय गणना (दुकान खर्चे व नकद वेतन कटौती सहित)।
7. **🕒 टाइमज़ोन व लोकल डेट सुरक्षा (IST Local Date Invariant):** UTC मिडनाइट शिफ्ट से बचने के लिए टाइमस्टैम्प्स को हमेशा लोकल कैलेंडर डेट (`YYYY-MM-DD`) में बदला जाए, जिससे 17 और 18 तारीख के बिल अलग-अलग सही दिन पर दिखें।

---

## 🏛️ 2. सभी 8 मॉड्यूल्स की 0ms ऑफलाइन स्थिति (All 8 Modules Matrix)

| # | मॉड्यूल (Module) | Room DB Entity / Tables | स्थानीय लोड समय | बैकग्राउंड सिंक |
|:---:|---|---|:---:|:---:|
| **1** | 📦 **Products & Menu** | `ItemEntity`, `ItemCategoryEntity`, `ItemVariantEntity` | ⚡ **0ms** | 🔄 2-Way Sync |
| **2** | 📊 **Inventory & Stock** | `InventoryStockEntity`, `InventoryTransactionEntity` | ⚡ **0ms** | 🔄 2-Way Sync |
| **3** | 👥 **Parties & Customer Khata** | `PartyEntity`, `PartyLedgerEntryEntity` | ⚡ **0ms** | 🔄 2-Way Sync |
| **4** | 💵 **Expenses & Petty Cash** | `BusinessExpenseEntity`, `BusinessExpenseTypeEntity` | ⚡ **0ms** | 🔄 2-Way Sync |
| **5** | 🧾 **GST Sales Invoices** | `InvoiceEntity`, `InvoiceItemEntity` | ⚡ **0ms** | 🔄 2-Way Sync |
| **6** | 🛒 **Purchase Bills (ITC)** | `PurchaseInvoiceEntity`, `PurchaseInvoiceItemEntity` | ⚡ **0ms** | 🔄 2-Way Sync |
| **7** | 🛵 **Staff & Fleet** | `StaffMemberEntity`, `BusinessBranchEntity`, `StaffSalaryProfileEntity` | ⚡ **0ms** | 🔄 2-Way Sync |
| **8** | ⚙️ **Store Settings** | `BusinessSettingsEntity`, `BusinessEntity` | ⚡ **0ms** | 🔄 2-Way Sync |

---

## 📱 3. मुख्य डैशबोर्ड (Business Command Center) ब्लूप्रिंट

### UI स्क्रीन: `BranchDashboardScreen.kt`

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ 1. शीर्ष बैनर: स्टोर नाम, टियर बैज, [Open POS Counter] बटन                │
├─────────────────────────────────────────────────────────────────────────────┤
│ 2. चार मुख्य KPI कार्ड्स:                                                   │
│    [Today's Sales ₹]   [Customer Udhar ₹]   [Low Stock Qty]   [Month Invoices]│
├─────────────────────────────────────────────────────────────────────────────┤
│ 3. 1-लाइन क्विक एक्शन बार:                                                  │
│    [+ POS Sale]   [+ Record Expense]   [+ Customer Khata]   [+ Purchase Bill]│
├──────────────────────────────────────┬──────────────────────────────────────┤
│ 4A. 7-Day Sales Trend Bar Chart      │ 4B. Actionable Insights Box:         │
│     (दैनिक बिक्री का विज़ुअल बार ग्राफ)│     • ⚠️ Low Stock Warnings (+Reorder)│
│                                      │     • 📲 Top Khata Dues (1-Tap WA)   │
└──────────────────────────────────────┴──────────────────────────────────────┘
```

#### ViewModel स्टेट डेटा मॉडल (`BranchDashboardViewModel.kt`):
```kotlin
data class DashboardUiState(
    val todaySales: Double = 0.0,
    val todayOrdersCount: Int = 0,
    val totalCustomerUdhar: Double = 0.0,
    val activeDebtorsCount: Int = 0,
    val lowStockCount: Int = 0,
    val monthInvoicesCount: Int = 0,
    val salesTrend: List<DaySalesTrend> = emptyList(),
    val lowStockItems: List<LowStockItemDto> = emptyList(),
    val topDebtors: List<TopDebtorDto> = emptyList(),
    val isLoading: Boolean = false
)

data class DaySalesTrend(
    val date: String,      // 'YYYY-MM-DD'
    val dayLabel: String,  // 'Mon', 'Tue', 'Today'
    val amount: Double,
    val ordersCount: Int
)

data class TopDebtorDto(
    val id: String,
    val name: String,
    val phone: String,
    val currentBalance: Double
)
```

---

## 🛒 4. खरीद बिल मॉड्यूल (Purchase Invoices Module) Room DB ब्लूप्रिंट

### 1. Room Entities

#### `PurchaseInvoiceEntity.kt`:
```kotlin
@Entity(
    tableName = "purchase_invoices",
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["branch_id"]),
        Index(value = ["supplier_id"]),
        Index(value = ["supplier_invoice_number"]),
        Index(value = ["invoice_date"])
    ]
)
@Serializable
data class PurchaseInvoiceEntity(
    @PrimaryKey
    @SerialName("id") val id: String = UUID.randomUUID().toString(),
    @SerialName("business_id") val businessId: String,
    @SerialName("branch_id") val branchId: String? = null,
    @SerialName("supplier_id") val supplierId: String,
    @SerialName("supplier_invoice_number") val supplierInvoiceNumber: String,
    @SerialName("invoice_date") val invoiceDate: String, // 'YYYY-MM-DD'
    @SerialName("taxable_amount") val taxableAmount: Double = 0.0,
    @SerialName("cgst_amount") val cgstAmount: Double = 0.0,
    @SerialName("sgst_amount") val sgstAmount: Double = 0.0,
    @SerialName("igst_amount") val igstAmount: Double = 0.0,
    @SerialName("grand_total") val grandTotal: Double = 0.0,
    @SerialName("paid_amount") val paidAmount: Double = 0.0,
    @SerialName("due_amount") val dueAmount: Double = 0.0,
    @SerialName("payment_status") val paymentStatus: String = "PAID", // 'PAID', 'UNPAID', 'PARTIAL'
    @SerialName("payment_mode") val paymentMode: String? = "CASH",
    @SerialName("notes") val notes: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_deleted") val isDeleted: Boolean = false,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("sync_version") val syncVersion: Long = 1L
)
```

#### `PurchaseInvoiceItemEntity.kt` (महत्वपूर्ण: `purchase_id` फॉरेन की):
```kotlin
@Entity(
    tableName = "purchase_invoice_items",
    indices = [
        Index(value = ["purchase_id"]),
        Index(value = ["item_id"])
    ]
)
@Serializable
data class PurchaseInvoiceItemEntity(
    @PrimaryKey
    @SerialName("id") val id: String = UUID.randomUUID().toString(),
    @SerialName("purchase_id") val purchaseId: String, // 🔴 Match PostgreSQL purchase_id
    @SerialName("item_id") val itemId: String? = null,
    @SerialName("item_name") val itemName: String,
    @SerialName("hsn_sac_code") val hsnSacCode: String? = null,
    @SerialName("quantity") val quantity: Double = 1.0,
    @SerialName("unit") val unit: String = "PCS",
    @SerialName("unit_rate") val unitRate: Double = 0.0,
    @SerialName("tax_rate") val taxRate: Double = 0.0,
    @SerialName("taxable_value") val taxableValue: Double = 0.0,
    @SerialName("cgst_amount") val cgstAmount: Double = 0.0,
    @SerialName("sgst_amount") val sgstAmount: Double = 0.0,
    @SerialName("total_amount") val totalAmount: Double = 0.0,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_deleted") val isDeleted: Boolean = false,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("sync_version") val syncVersion: Long = 1L
)
```

### 2. Room DAO (`PurchaseInvoiceDao.kt`):
```kotlin
@Dao
interface PurchaseInvoiceDao {
    @Query("SELECT * FROM purchase_invoices WHERE business_id = :businessId AND is_deleted = 0 ORDER BY invoice_date DESC, created_at DESC")
    fun observePurchaseInvoices(businessId: String): Flow<List<PurchaseInvoiceEntity>>

    @Query("SELECT * FROM purchase_invoice_items WHERE purchase_id = :purchaseId AND is_deleted = 0")
    fun observePurchaseItems(purchaseId: String): Flow<List<PurchaseInvoiceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPurchaseInvoices(invoices: List<PurchaseInvoiceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPurchaseInvoiceItems(items: List<PurchaseInvoiceItemEntity>)
}
```

---

## 💵 5. खर्चे व पेट्टी कैश (Expenses Module) Room DB ब्लूप्रिंट

### Room Entities (`BusinessExpenseEntity.kt` & `BusinessExpenseTypeEntity.kt`):
```kotlin
@Entity(
    tableName = "business_expenses",
    indices = [
        Index(value = ["business_id"]),
        Index(value = ["branch_id"]),
        Index(value = ["expense_type_id"]),
        Index(value = ["expense_date"])
    ]
)
@Serializable
data class BusinessExpenseEntity(
    @PrimaryKey
    @SerialName("id") val id: String = UUID.randomUUID().toString(),
    @SerialName("business_id") val businessId: String,
    @SerialName("branch_id") val branchId: String? = null,
    @SerialName("expense_type_id") val expenseTypeId: String,
    @SerialName("voucher_number") val voucherNumber: String,
    @SerialName("title") val title: String,
    @SerialName("amount") val amount: Double,
    @SerialName("payment_mode") val paymentMode: String = "CASH", // 'CASH', 'UPI', 'BANK_TRANSFER', 'CHEQUE'
    @SerialName("expense_date") val expenseDate: String, // 'YYYY-MM-DD'
    @SerialName("reference_type") val referenceType: String = "NONE",
    @SerialName("reference_id") val referenceId: String? = null,
    @SerialName("paid_to") val paidTo: String? = null,
    @SerialName("notes") val notes: String? = null,
    @SerialName("created_by") val createdBy: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_deleted") val isDeleted: Boolean = false,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("sync_version") val syncVersion: Long = 1L
)

@Entity(
    tableName = "business_expense_types",
    indices = [Index(value = ["business_id"])]
)
@Serializable
data class BusinessExpenseTypeEntity(
    @PrimaryKey
    @SerialName("id") val id: String = UUID.randomUUID().toString(),
    @SerialName("business_id") val businessId: String? = null,
    @SerialName("name") val name: String,
    @SerialName("name_hi") val nameHi: String? = null,
    @SerialName("code") val code: String,
    @SerialName("reference_type") val referenceType: String = "NONE",
    @SerialName("icon") val icon: String = "Receipt",
    @SerialName("color") val color: String = "#10B981",
    @SerialName("is_system_default") val isSystemDefault: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_deleted") val isDeleted: Boolean = false,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("sync_version") val syncVersion: Long = 1L
)
```

---

## 🧮 6. गल्ला (Daybook) व वित्तीय रिपोर्टिंग फॉर्मूला (Financial Formulas)

### A. दुकान के गल्ले में शुद्ध नकद (Net Cash in Hand / Drawer):
$$\text{Net Cash Drawer} = (\text{POS Cash Sales} + \text{Khata Cash Recovery}) - (\text{Supplier Cash Paid} + \text{Store Cash Expenses} + \text{Staff Cash Salary/Advance})$$

### B. शुद्ध व्यापारिक लाभ (Net Operating Profit):
$$\text{Net Operating Profit} = (\text{Gross Revenue} - \text{COGS} - \text{Discounts Given}) - \text{Total Store Expenses}$$

### RPC फ़ंक्शन: `fn_get_business_financial_reports`
```json
{
  "daybook": {
    "cash_sales": 15110.00,
    "upi_sales": 0.00,
    "khata_sales": 0.00,
    "payments_received_cash": 1340.00,
    "payments_received_upi": 0.00,
    "supplier_paid_cash": 3445.00,
    "expenses_paid_cash": 50.00,
    "salary_paid_cash": 1000.00,
    "net_cash_drawer": 11955.00
  },
  "profit_loss": {
    "total_revenue": 15110.00,
    "cogs_total": 8500.00,
    "discount_total": 0.00,
    "expenses_total": 1050.00,
    "net_profit": 5560.00,
    "profit_margin_pct": 36.80
  }
}
```

---

## 🔒 7. आर्किटेक्चरल एवं क्वालिटी चेकलिस्ट (COMPLETED PRODUCTION STATUS)

| # | चेकलिस्ट आइटम | स्थिति | कार्यान्वयन विवरण |
|:---:|---|:---:|:---|
| **1** | **0ms Offline-First Reads** | ✅ **पूर्ण** | सभी 8 मॉड्यूल्स सीधे Room DB `Flow<List<T>>` पर `Dispatchers.IO` से लोड होते हैं। |
| **2** | **Single-Flight Safe Invoker** | ✅ **पूर्ण** | सभी सर्वर म्यूटेशन (`SafeSupabaseInvoker.safeSupabaseCall`) Mutex गेटकीपर से 401 ऑटो-रिफ्रेश के साथ सुरक्षित हैं। |
| **3** | **Zero UI Choke & 60 FPS** | ✅ **पूर्ण** | सभी लिस्ट्स में `LazyColumn` के साथ यूनिक स्टेबल की (`key = { it.id }`) लागू है। |
| **4** | **Layered BackHandler Navigation** | ✅ **पूर्ण** | सभी बॉटम शीट्स, डायलॉग्स और सब-स्क्रीन्स में लेयर्ड `BackHandler` लागू है। |
| **5** | **Zero Technical Error Leakage** | ✅ **पूर्ण** | UI पर रॉ एरर डंप या JWT नहीं दिखते, केवल विनम्र संदेश दिखते हैं। |
| **6** | **Bilingual Localization** | ✅ **पूर्ण** | हिंदी और अंग्रेज़ी दोनों भाषाओं में पूर्ण 1:1 समर्थन। |
| **7** | **IST Local Date Formatting** | ✅ **पूर्ण** | बिल और ट्रेंड ग्राफ भारतीय समय (IST) के अनुसार 100% सही दिन पर लोड होते हैं। |
| **8** | **Modular Navigation & Drawer** | ✅ **पूर्ण** | Store Owner, Store Staff, और Customer के लिए अलग-अलग डैशबोर्ड और ड्रावर अनुमतियाँ पूरी तरह सक्रिय हैं। |
| **9** | **7-Day Trend Chart & Baseline** | ✅ **पूर्ण** | फिक्स्ड 80dp बेसलाइन कंटेनर, ट्रैक बैकग्राउंड, और कॉम्पैक्ट करंसी (`₹11.6k`) लागू। |
| **10** | **1-Roundtrip Main Sync (26 Tables)** | ✅ **पूर्ण** | `fn_fetch_business_store_workspace_payload` और `StoreWorkspaceSyncEngine` में 1 ही कॉल में खर्चे और 15+ कैटेगरीज ACID एटॉमिक ट्रांज़ैक्शन में सिंक। |
| **11** | **1:1 Dynamic Web Parity for Expenses** | ✅ **पूर्ण** | सर्च योग्य कैटेगरी ड्रॉपडाउन, मल्टी-ब्रांच सपोर्ट, और `reference_type` रूटिंग (`STAFF`, `BRANCH`, `SUPPLIER`, `ITEM`) 100% डायनामिक। |
| **12** | **Zero Hardcoded Mock Data** | ✅ **पूर्ण** | कोड से डमी लिस्ट्स पूरी तरह हटाकर 100% शुद्ध डायनामिक Supabase डेटाबेस इंटीग्रेशन। |

---

**दस्तावेज़ की स्थिति:** 🟢 **सभी 8 मॉड्यूल्स + मुख्य डैशबोर्ड + ड्रावर + 26-टेबल मेन सिंक + 100% डायनामिक खर्चे पूर्ण और सत्यापित।**
