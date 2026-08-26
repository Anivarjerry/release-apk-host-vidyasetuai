# 🚀 VidyaSetu Commerce (`feature_store`) - Comprehensive Architecture & Implementation Tracking

**Last Updated:** August 17, 2026  
**Status:** Production-Ready Enterprise Core Architecture & Multi-Role Commerce Engine  
**Parity:** 100% 1:1 Parity with Next.js Web App (`vidyasetu-ai/src/app/(business)/*`)

---

## 📌 1. Architecture Overview & High-Level Design

The **VidyaSetu Commerce Module (`feature_store`)** is a hyper-local, offline-first commerce platform designed for multi-branch retail merchants and public customers.

```
+-----------------------------------------------------------------------------------+
|                                JETPACK COMPOSE UI                                 |
|   +-------------------+  +---------------------+  +---------------------------+   |
|   |  POS Counter UI   |  | Products Catalog UI |  | Inventory & Stock Control |   |
|   +-------------------+  +---------------------+  +---------------------------+   |
|   +-------------------+  +---------------------+  +---------------------------+   |
|   | Sales Invoices UI |  |  Parties & Khata UI |  |  Staff & Delivery Fleet   |   |
|   +-------------------+  +---------------------+  +---------------------------+   |
+-----------------------------------------+-----------------------------------------+
                                          |
                                 VIEWMODEL & UI STATE
                     (Flow Collection, State Mutex, Validation)
                                          |
                                          v
+-----------------------------------------------------------------------------------+
|                                 REPOSITORY LAYER                                  |
|  +---------------------------+  +------------------------+  +------------------+  |
|  | StoreCatalogRepository    |  | SalesOrderRepository   |  | KhataRepository  |  |
|  +---------------------------+  +------------------------+  +------------------+  |
|  | StaffFleetRepository      |  | StoreWorkspaceSync     |                     |  |
|  +---------------------------+  +------------------------+                     |  |
+--------------------+------------------------------------+-------------------------+
                     |                                    |
                     v (0ms Reactive Flow Reads)          v (Direct Supabase Mutations)
+-----------------------------------+   +-------------------------------------------+
|          LOCAL ROOM DB            |   |       SAFE SUPABASE REMOTE INVOKER        |
|  (20 SQLite Tables + Room DAOs)   |   |  (SafeSupabaseInvoker + Token Refresher)  |
+-----------------------------------+   +-------------------------------------------+
```

---

## 🗄️ 2. Database Schema (20 Room Entities & DAOs)

All entities implement `@Serializable` with `@SerialName` annotations and use `FlexibleJsonAsStringSerializer` for crash-free JSONB mapping:

| Entity Name | Room Table Name | Primary Purpose / Schema Highlights |
|---|---|---|
| **`BusinessEntity`** | `businesses` | Master merchant profile, trade name, GSTIN, currency, tax mode. |
| **`BusinessSettingsEntity`** | `business_settings` | Tax calculation settings, invoice numbering prefixes, thermal printer setup. |
| **`BusinessBranchEntity`** | `business_branches` | Outlets and warehouses with GPS latitude/longitude, address, and primary counter flags. |
| **`BusinessStaffEntity`** | `business_staff_members` | Store employees, assigned `branch_id`, role (`ADMIN`, `CASHIER`, `RIDER`, `CHEF`, `WAITER`, `MANAGER`). |
| **`PartyEntity`** | `parties` | Customers and suppliers directory with credit limits, current balance, GSTIN, PAN. |
| **`PartyAddressEntity`** | `party_addresses` | Multiple shipping/billing addresses for customers/suppliers. |
| **`PartyLedgerEntryEntity`** | `party_ledger_entries` | Chronological debit/credit transaction passbook ledger with balance snapshots. |
| **`ItemCategoryEntity`** | `item_categories` | Catalog category taxonomy with color codes, icons, and hierarchy. |
| **`ItemEntity`** | `items` | Master product catalog, SKU, barcode, selling price, purchase price, GST rate, HSN code, food type. |
| **`ItemVariantEntity`** | `item_variants` | SKU variant dimensions (sizes, colors, pack sizes) and custom pricing. |
| **`InventoryStockEntity`** | `inventory_stocks` | Per-branch inventory stock levels, min alert levels, and batch allocations. |
| **`InventoryTransactionEntity`**| `inventory_transactions`| Stock movement audit trail (`IN`, `OUT`, `ADJUST`, `AUDIT`, `RETURN`). |
| **`InvoiceEntity`** | `invoices` | Official GST B2B/B2C sales invoices, total amount, taxes, balance due, payment status. |
| **`InvoiceItemEntity`** | `invoice_items` | Invoice line items with unit rates, discounts, CGST, SGST, IGST, cess. |
| **`PurchaseInvoiceEntity`** | `purchase_invoices` | Supplier purchase bills, input tax credit (ITC) records. |
| **`PaymentEntity`** | `payments` | Customer & supplier payment receipts, modes (`CASH`, `UPI`, `CARD`, `BANK`, `CHEQUE`). |
| **`OrderEntity`** | `orders` | Hyper-local marketplace customer orders, order number, delivery status, delivery OTP. |
| **`OrderItemEntity`** | `order_items` | Marketplace customer order line items. |
| **`DeliveryRiderEntity`** | `delivery_riders` | Delivery fleet riders, vehicle details, live online/offline duty status, GPS tracking. |
| **`OrderDeliveryEntity`** | `order_deliveries` | Order delivery tracking, assigned rider, COD cash collected in hand, handover flag. |

---

## 📱 3. Presentation Modules & 1:1 Web Parity Matrix

### Module 1: 🛒 POS Counter Billing (`PosCounterBillingScreen.kt`)
- **Web Reference:** `/pos`
- **Architecture:** Room DB reactive flow reads + instant barcode lookup.
- **Key Features:**
  - Barcode scanner integration.
  - Multi-category item grid + quick cart stepper.
  - Split & multi-mode settlement (`CASH`, `UPI QR`, `KHATA`, `CARD`).
  - Thermal / PDF receipt invoice generation.

### Module 2: 📦 Products & Menu Catalog (`ProductsCatalogScreen.kt`)
- **Web Reference:** `/business-products`
- **Architecture:** 3-way Room combine (`items`, `categories`, `stocks`) + `StoreCatalogRepositoryImpl`.
- **Key Features:**
  - Category manager dialog with color picker.
  - `AddEditProductBottomSheet`: Title, SKU, Barcode, Selling Price, Purchase Price, GST Slab, HSN/SAC, Food Type, Stock Alert threshold.
  - Search by title, barcode, or SKU with 0ms response.

### Module 3: 📊 Inventory & Stock Control (`InventoryStockControlScreen.kt`)
- **Web Reference:** `/business-inventory`
- **Architecture:** 4-way reactive combine (`items`, `stocks`, `transactions`, `categories`).
- **Key Features:**
  - 4-Pill KPI Metric Cards: Total Stock Valuation (₹), Low Stock Alerts, Out of Stock, Healthy Stock.
  - `StockAdjustmentBottomSheet`: Stock adjustments with reason codes (`PURCHASE_RECEIPT`, `DAMAGED_EXPIRED`, `AUDIT_CORRECTION`, `THEFT_LOSS`, `CUSTOMER_RETURN`).
  - `StockTimelineBottomSheet`: Chronological stock audit ledger.

### Module 4: 🧾 GST Sales Invoices & Bills (`SalesInvoicesBillsScreen.kt`)
- **Web Reference:** `/business-sales`
- **Architecture:** 3-way reactive combine (`invoices`, `parties`, `invoice_items`) via in-memory `groupBy` (no main thread blocking).
- **Key Features:**
  - 4 KPI Pills: Total Sales, Total Paid, Total Due, Total Invoices.
  - `InvoiceDetailsBottomSheet`: Line items breakdown, GST slabs, payment receipts, 1-tap WhatsApp PDF share.
  - `EwayBillBottomSheet`: E-Way bill generation modal (Transporter ID, Vehicle No, Mode, Distance KM).

### Module 5: 👥 Parties & Customer Khata (`PartiesKhataScreen.kt`)
- **Web Reference:** `/business-parties`
- **Architecture:** Room DB Flow + Supabase RPC `fn_record_party_payment_transaction`.
- **Key Features:**
  - 4-Pill KPI Summary Cards: Total Receivable (₹), Total Payable (₹), Customers, Suppliers.
  - `AddEditPartyBottomSheet`: Customer/Supplier toggle, Name, Phone, Email, GSTIN, PAN, Credit Limit, Opening Balance.
  - `RecordPaymentBottomSheet`: Payment In / Out settlement with quick amount chips (`₹500`, `₹1000`, `₹2000`, `₹5000`, `Full Balance`).
  - `PartyPassbookBottomSheet`: Chronological passbook ledger with WhatsApp statement reminder.

### Module 6: 🛵 Staff Members, Delivery Fleet & Branch Outlets (`StaffFleetScreen.kt`)
- **Web Reference:** `/business-staff`
- **Architecture:** 4-way combine (`staff`, `branches`, `riders`, `deliveries`) on `Dispatchers.Default`.
- **Key Features:**
  - 4-Pill KPI Cards: Total Staff, On-Duty Riders (`X / Y`), Store Outlets, Cash with Riders (₹ Pending Handover).
  - `BranchManagementBottomSheet`: Configured branches list with Primary Counter badge, full address, GPS Auto-Detect with native Geocoder reverse-address auto-fill.
  - `AddEditStaffBottomSheet`: Staff roles (`ADMIN`, `MANAGER`, `CASHIER`, `KITCHEN_STAFF`, `WAITER`, `DELIVERY_RIDER`, `ACCOUNTANT`) with Rider vehicle details.
  - Live Rider On-Duty / Off-Duty toggle switch (`🟢 ON-DUTY` vs `⚪ OFF-DUTY`).
  - `RiderCashSettlementBottomSheet`: 1-tap COD cash handover to shop cash drawer.

### Module 7: ⚙️ Store & Billing Settings (`StoreSettingsScreen.kt`)
- **Web Reference:** `/business-settings`
- **Architecture:** 4-way reactive combine (`businesses`, `business_settings`, `business_branches`) on `Dispatchers.Default` + `SafeSupabaseInvoker` mutations.
- **Key Features:**
  - Public Customer Web Store & Digital Menu Top Banner (`/store/{slug}`) with `StoreQrStandeeBottomSheet` (printable QR standee with store branding and scan-to-pay).
  - `EditBranchAddressBottomSheet`: Primary branch address, city, state, pincode, and Android Native Geocoder GPS auto-detect.
  - `EditBillingPrinterBottomSheet`: Bill prefix (`INV-24/`), thermal printer width (`2_INCH`, `3_INCH`, `A4`), GST billing toggle, packing/delivery fees.
  - `EditBankUpiBottomSheet`: Merchant UPI ID (VPA) for dynamic receipt payment QR, Bank account number, IFSC code, and branch.
  - `FeatureSwitchesBottomSheet`: Toggles for KDS, Delivery tracking, Multi-branch inventory, Customer Khata, and Online store orders.
  - `GstEwayBillConfigBottomSheet`: GSTIN, NIC API Username/Password, GSP provider (`CLEARTAX`, `MASTERS_INDIA`, `EWAY_NIC`), and Auto E-Way Bill toggle.
  - Standard Drawer List Integration: "Settings & Tax Config" relocated from sticky footer to standard scrollable services list in `StoreDrawerContent.kt`.

### Module 8: 🍳 Kitchen Display System (KDS) (`KitchenKdsScreen.kt`)
- **Web Reference:** `/kds` (`KitchenDisplayKDS.tsx`)
- **Architecture:** 2-way reactive combine (`orders`, `order_items`) on `Dispatchers.Default` + Supabase RPC `fn_update_order_kds_status`.
- **Key Features:**
  - 0ms Reactive Room Flow combining active orders and order items without blocking UI.
  - Filter Tabs: `ALL ACTIVE KOT`, `NEW 🔴`, `PREPARING 🟡`, `READY 🟢`, `SERVED TODAY ⚪`.
  - Visual color-coded KOT ticket cards with elapsed time timers (`4m ago`, urgency highlight >15m).
  - Item checklist with `[2x]` quantity pill badges and Chef special cooking notes.
  - Interactive status transitions: `START PREPARING` (Amber), `MARK FOOD READY` (Emerald), `MARK SERVED` (Dark Slate).
  - Background direct Supabase RPC update (`fn_update_order_kds_status`) via `SafeSupabaseInvoker`.
  - Android `ToneGenerator` audio chime alert toggle (`🔔 Audio Alerts ON` / `🔕 Muted`).

### Module 9: 📊 Financial Business Reports & P&L Module (`BusinessReportsScreen.kt`)
- **Web Reference:** `/business-reports` (`page.tsx`)
- **Architecture:** 3-way local Room aggregation (`invoices`, `invoice_items`, `items`) on `Dispatchers.Default` + Supabase RPC `fn_get_business_financial_reports`.
- **Key Features:**
  - 0ms Offline-First reactive computation of all Daybook, P&L, GSTR-1, and GSTR-3B metrics.
  - 4 Dedicated Financial Views: Daily Daybook, Profit & Loss (P&L with margin %), GSTR-1 (Outward taxes & HSN table), and GSTR-3B (Net GST payable).
  - 1-Tap HSN CSV file generation & Android native share intent.
  - Date Presets: `TODAY`, `THIS_WEEK`, `THIS_MONTH`, and `CUSTOM` date picker bottom sheet.
  - Cloud Audited Sync via `SafeSupabaseInvoker`.

### Module 10: 🛒 Supplier Purchase Bills & Auto-Stock In (`PurchaseBillsScreen.kt`)
- **Web Reference:** `/business-purchases` (`page.tsx`)
- **Architecture:** Local Room DB (`purchase_invoices`, `parties`, `items`) on `Dispatchers.Default` + Supabase RPC `fn_create_purchase_bill_transaction`.
- **Key Features:**
  - 0ms Offline-First reactive list and summary metrics (Total Spend, Paid, Due, Suppliers Count).
  - Search & Status Filter Chips (`ALL`, `PAID 🟢`, `PARTIAL 🟡`, `UNPAID 🔴`).
  - Interactive `AddPurchaseBillBottomSheet` with item picker, unit purchase rate, GST slabs (`0%`, `5%`, `12%`, `18%`, `28%`), payment settlement mode, and auto-stock inward.
  - Full modal bill breakdown in `PurchaseBillDetailBottomSheet`.
  - Layered `BackHandler` (Pillar 5) and virtualized 60 FPS `LazyColumn`.

### Module 11: 👔 Staff Workspace Operational Dashboard (`StaffDashboardScreen.kt`)
- **Architecture:** Role-driven dynamic landing dashboard for `STORE_STAFF` (`MANAGER`, `CASHIER`, `KITCHEN_STAFF`, `DELIVERY_RIDER`, `INVENTORY_CLERK`).
- **Key Features:**
  - 0ms reactive Room DB profile resolution and branch location badge (`📍 Main Branch`).
  - Clean operational UI with **zero technical DB re-sync clutter**.
  - Dynamic 2-Column Action Cards Grid tailored to active staff role.
  - Priority hero card for Delivery Riders (`Active Delivery Orders & OTP`) and Kitchen Chefs (`Live Kitchen KDS Screen`).
  - Layered `BackHandler` (Pillar 5) returning smoothly from all sub-screens.

### Module 12: 🛵 Driver Delivery Orders & Free Google Maps Navigation (`DriverDeliveryOrdersScreen.kt`)
- **Web Reference:** `/business-orders` & Delivery Dispatch
- **Architecture:** Local Room DB (`orders`, `order_items`) reactive combine on `Dispatchers.Default` + Supabase RPC `fn_update_order_kds_status`.
- **Key Features:**
  - 0ms Offline-First reactive delivery list, customer address, and phone number with 1-tap call dialer.
  - **100% Free Turn-by-Turn GPS Voice Navigation:** Android Native Google Maps Intent (`google.navigation:q=$lat,$lng&mode=d` with address text fallback) with **Zero API Key / Billing Cost**.
  - **Atomic 4-Digit Delivery OTP Handover:** Verification sheet with COD cash collection alert and immediate status transition to `'DELIVERED'`.
  - **Rider Cash In Hand Tracker:** Computes accumulated cash from completed COD deliveries to deposit at the store counter.
  - Layered `BackHandler` (Pillar 5) and virtualized 60 FPS `LazyColumn`.

---

## 🛡️ 4. Enterprise Production Invariants Verified

| Pillar / Invariant | Status | Implementation Mechanism |
|---|---|---|
| **Pillar 1: Single-Flight Auth Engine** | ✅ **Verified** | Centralized Mutex gatekeeper preventing token race conditions on background sync. |
| **Pillar 2: Resilient Remote Invoker** | ✅ **Verified** | All Supabase RPCs and PostgREST calls route through `SafeSupabaseInvoker.safeSupabaseCall`. |
| **Pillar 3: Zero Data Loss** | ✅ **Verified** | Local UI states preserved across network reconnects; 1-tap retry enabled. |
| **Pillar 4: Zero Technical Error Leakage** | ✅ **Verified** | All errors sanitized; polite and actionable user messages displayed via Snackbars. |
| **Pillar 5: Layered Back Navigation** | ✅ **Verified** | Every screen and bottom sheet implements `BackHandler` to dismiss sheets before screen exit. |
| **Rule 7: Virtualized Lazy Layouts** | ✅ **Verified** | Every list uses `LazyColumn(key = { it.id })` with stable unique keys, ensuring buttery 60 FPS scrolling. |

---

## 🎯 5. Next Steps Checklist
- [x] POS Counter Billing Module
- [x] Products & Menu Catalog Module
- [x] Inventory & Stock Control Module
- [x] GST Sales Invoices & E-Way Bill Module
- [x] Parties & Customer Khata Module
- [x] Staff Members & Branch Fleet Module
- [x] Store Settings & Tax Configuration Screen (`StoreSettingsScreen.kt`)
- [x] Kitchen Display System (KDS) Screen (`KitchenKdsScreen.kt`)
- [x] Financial Business Reports & P&L Module (`BusinessReportsScreen.kt`)
- [x] Supplier Purchase Bills & Auto-Stock In Module (`PurchaseBillsScreen.kt`)
- [x] Staff Workspace Operational Dashboard (`StaffDashboardScreen.kt`)
- [x] Driver Delivery Orders & Free Google Maps Navigation Module (`DriverDeliveryOrdersScreen.kt`)



