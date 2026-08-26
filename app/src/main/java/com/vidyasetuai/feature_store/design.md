# Mobile Store Module - Design & UI/UX Blueprint (`feature_store`)
> **स्थान**: `app/src/main/java/com/vidyasetuai/feature_store/design.md`  
> **मॉड्यूल**: `feature_store` (🏪 Store & POS Commerce Hub)  
> **डिज़ाइन दर्शन**: Apple Minimalist Flat HIG (High-Density, Slender B2B Cards, Dynamic Light/OLED Pitch Dark, 120FPS)

---

## १. डिज़ाइन सिद्धांत एवं ब्रांड वैल्यू मानक (Design Principles & Brand Identity)

1. **अल्ट्रा-मिनिमल एवं स्लीक कार्ड्स (0% Bulky / Bloated Boxes):**
   - बड़े, थुलथुले (Chunky) और गैर-ज़रूरी जगह घेरने वाले कार्ड्स को पूरी तरह समाप्त किया गया है।
   - **कार्ड संरचना:** स्लिम 12-14dp कॉर्नर रेडियस, 1dp हेयरलाइन बॉर्डर (`outline.copy(alpha = 0.08f)`), 0% भारी शैडोज़।
   - **उच्च सूचना घनत्व (High Information Density):** दुकानदार को एक ही स्क्रीन पर बिना लंबा स्क्रॉल किए बिक्री, बिल, स्टॉक और पेंडिंग ऑर्डर्स एक नज़र में दिखेंगे।

2. **डायनेमिक लाइट व ट्रू OLED पिच-ब्लैक डार्क मोड:**
   - **Light Mode (`#FAFAFA` / `#FFFFFF`):** सफ़ेद कैनवास, हल्के स्लेट कार्ड्स और क्रिस्प ब्लैक टाइपोग्राफी।
   - **Dark Mode (`#000000` / `#0D0D0D`):** ट्रू OLED पिच ब्लैक बैकग्राउंड, सॉलिड डार्क ग्रे कार्ड्स (`#161616`), और डीप कॉन्ट्रास्ट।

3. **कलर पैलेट व ६०-३०-१० नियम (Restrained Palette):**
   - **६०% कैनवास:** `#FAFAFA` (Light) ⟷ `#000000` (Dark)
   - **३०% सरफेस कार्ड्स:** `#FFFFFF` (Light) ⟷ `#121212` (Dark)
   - **१०% ब्रांड एमराल्ड ग्रीन (`#10B981`):** केवल एक्टिव स्टेट्स, मुख्य ऐक्शन बटन (CTA), और लाइव स्टेटस पिल्स (`LIVE 🟢`) के लिए।
   - **सपोर्टिंग स्टेटस कलर्स:**
     - 🟡 Amber (`#F59E0B`): `PREPARING` / कम स्टॉक चेतावनी
     - 🔵 Blue (`#3B82F6`): `NEW` ऑर्डर / टोकन
     - 🟣 Purple (`#8B5CF6`): `OUT_FOR_DELIVERY`
     - 🔴 Red (`#EF4444`): `CANCELLED` / एक्सपायर / आउट ऑफ स्टॉक

4. **टाइपोग्राफी पदानुक्रम (Apple San Francisco Style Typography):**
   - **Hero Metric:** `22-26sp Bold` (उदा. `₹48,250.00`)
   - **Section Headers:** `13-14sp SemiBold` (`Letter Spacing +0.3sp`)
   - **Body / Labels:** `11-12sp Medium`
   - **Micro Badges / Chips:** `10sp Bold Uppercase` (उदा. `LIVE`, `3 PCS`, `GST 5%`)

---

## २. स्टोर ओनर व कैशियर डैशबोर्ड लेआउट (Owner & POS Dashboard Architecture)

### 📌 A. टॉप हेडर (Top Bar & Sticky Branch Selector):
- **Store Identity:** स्टोर का नाम (`Sharma Sweets & Fast Food`) + स्लिम लाइव स्टेटस।
- **Branch Context Pill:** `📍 All Outlets (समस्त शाखाएं) ▾` या `📍 Main Branch ▾` (टैप करने पर Apple-style Branch Switcher Bottom Sheet खुलती है)।

### 📊 B. स्लिम माइक्रो-मीट्रिक ग्रिड (Compact 4-Pill KPI Row):
बड़े बॉक्सेस के बजाय 4 पतले स्लीक मीट्रिक चिप्स:
1. 💵 **आज की बिक्री (Sales):** `₹18,450`
2. 🧾 **कुल बिल (Bills):** `34`
3. ⏳ **लाइव ऑर्डर्स (Active KDS):** `3`
4. ⚠️ **लो-स्टॉक अलर्ट:** `2 Items`

### ⚡ C. 1-टैप फास्ट एक्शन बार (Quick Action Row):
- ⚡ `+ New POS Bill` (तुरंत बिलिंग काउंटर)
- ➕ `+ Add Product` (नया आइटम जोड़ना)
- 📥 `+ Payment In` (खाता वसूली दर्ज करना)
- 🚚 `+ KDS Orders` (रसोई स्क्रीन)

### 🏪 D. मुख्य 6-मॉड्यूल ग्रिड (Compact 2x3 Grid):
1. 🛒 **POS Counter (बिलिंग काउंटर):** बारकोड, टच ग्रिड, थर्मल प्रिंट
2. 📦 **Products & Stock (उत्पाद व इन्वेंटरी):** कैटलॉग, वेरिएंट्स, स्टॉक एडजस्टमेंट
3. 🚚 **Live Orders & KDS (रसोई व डिलीवरी):** टोकन, KOT, राइडर डिस्पैच
4. 📖 **Customer Khata (ग्राहक खाता):** उधारी बही-खाता, लेज़र, WhatsApp रिमाइंडर
5. 🧾 **Sales Invoices (बिक्री इनवॉइस):** GST बिल इतिहास, रिटर्न
6. 📊 **Business Reports (P&L):** दैनिक बिक्री, टैक्स व लाभ-हानि

---

## ३. POS बिलिंग काउंटर स्क्रीन आर्किटेक्चर (`PosCounterBillingScreen.kt`)

### 🛒 १. यूज़र फ्लो व कंपोनेंट्स:
- **Left/Top Area:**
  - **Category Tabs Pill Bar:** `All Items`, `Fast Food`, `Dairy`, `Beverages` आदि।
  - **Search & Barcode Scanner:** F2 / 1-टैप कैमरा बारकोड स्कैनर।
  - **Product Tile Grid:** 1:1 कॉम्पैक्ट फोटो, आइटम नाम, दाम, और 1-टैप `[+ ADD]` / `[- 1 +]` स्टीपर।
- **Live Slide-Over Cart Drawer (or Bottom Capsule):**
  - चयनित सामानों की लिस्ट, मात्रा, छूट, और GST टैक्स ब्रेकअप।
  - ग्राहक चयन: वॉक-इन काउंटर ग्राहक या `parties` से खाता ग्राहक।
- **1-Tap Checkout Modal:**
  - पेमेंट मोड: 💵 कैश (Tendered Change Calculator), 📱 UPI QR कोड, 📖 उधारी खाता (`CREDIT_KHATA`)।
  - **Atomic Transaction Execution:** `fn_create_pos_invoice_transaction` कॉल ➔ इनवॉइस + ऑर्डर + स्टॉक डिडक्शन + लेज़र अपडेट।
  - **KDS Kitchen Ticket Linkage:** बिल कटते ही KDS स्क्रीन पर 0ms में नया KOT टिकट चला जाता है।
  - **Thermal Print:** ब्लूटूथ 2-इंच/3-इंच ESC/POS रसीद या WhatsApp डिजिटल बिल।



bahi ak bat btao jo fcm notification wala hai kya vo [AGENTS.md](file;file:///d%3A/VidyaSetu%20AI/AGENTS.md) isme jo rules likhe hai unke anushra hi hai kya yani notification app level par haii ya alg alg or banni bhi chahiye to kis trike se degine karna chahiey notification ko keval btao no changes whiout my permisson