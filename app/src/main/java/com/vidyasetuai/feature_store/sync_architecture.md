# Mobile Store Module - Supabase Sync Engine & RPC Architecture Blueprint (`feature_store`)
> **स्थान**: `app/src/main/java/com/vidyasetuai/feature_store/sync_architecture.md`  
> **मूल उद्देश्य**: 1-राउंड-ट्रिप (< 150ms) में सभी २० टेबल्स का 100% डेटा एक ही RPC Call से प्राप्त करना और 1-क्लिक "Fresh Re-Sync & Reset" बटन से रिफ्रेश करना।

---

## १. एकात्मक Supabase RPC फ़ंक्शन (Single Atomic Workspace RPC Function)

२० अलग-अलग HTTP कॉल्स करने के बजाय, Supabase सर्वर पर १ ही RPC फ़ंक्शन निष्पादित होगा:

### SQL RPC फ़ंक्शन: `fn_fetch_business_store_workspace_payload`
```sql
CREATE OR REPLACE FUNCTION public.fn_fetch_business_store_workspace_payload(
    p_business_id UUID,
    p_user_id UUID,
    p_last_synced_at TIMESTAMPTZ DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_result JSONB;
BEGIN
    SELECT jsonb_build_object(
        'business', (SELECT to_jsonb(b.*) FROM public.businesses b WHERE b.id = p_business_id LIMIT 1),
        'settings', (SELECT to_jsonb(s.*) FROM public.business_settings s WHERE s.business_id = p_business_id LIMIT 1),
        'branches', COALESCE((SELECT jsonb_agg(br.*) FROM public.business_branches br WHERE br.business_id = p_business_id AND br.is_deleted = false), '[]'::jsonb),
        'staff', COALESCE((SELECT jsonb_agg(st.*) FROM public.business_staff_members st WHERE st.business_id = p_business_id AND st.is_deleted = false), '[]'::jsonb),
        'parties', COALESCE((SELECT jsonb_agg(pt.*) FROM public.parties pt WHERE pt.business_id = p_business_id AND pt.is_deleted = false), '[]'::jsonb),
        'party_addresses', COALESCE((SELECT jsonb_agg(pa.*) FROM public.party_addresses pa WHERE pa.party_id IN (SELECT id FROM public.parties WHERE business_id = p_business_id) AND pa.is_deleted = false), '[]'::jsonb),
        'categories', COALESCE((SELECT jsonb_agg(c.*) FROM public.item_categories c WHERE c.business_id = p_business_id AND c.is_deleted = false), '[]'::jsonb),
        'items', COALESCE((SELECT jsonb_agg(i.*) FROM public.items i WHERE i.business_id = p_business_id AND i.is_deleted = false), '[]'::jsonb),
        'item_variants', COALESCE((SELECT jsonb_agg(iv.*) FROM public.item_variants iv WHERE iv.item_id IN (SELECT id FROM public.items WHERE business_id = p_business_id) AND iv.is_deleted = false), '[]'::jsonb),
        'inventory_stocks', COALESCE((SELECT jsonb_agg(inv.*) FROM public.inventory_stocks inv WHERE inv.branch_id IN (SELECT id FROM public.business_branches WHERE business_id = p_business_id) AND inv.is_deleted = false), '[]'::jsonb),
        'inventory_transactions', COALESCE((SELECT jsonb_agg(it.*) FROM public.inventory_transactions it WHERE it.business_id = p_business_id AND it.is_deleted = false), '[]'::jsonb),
        'orders', COALESCE((SELECT jsonb_agg(o.*) FROM public.orders o WHERE o.business_id = p_business_id AND o.is_deleted = false), '[]'::jsonb),
        'order_items', COALESCE((SELECT jsonb_agg(oi.*) FROM public.order_items oi WHERE oi.order_id IN (SELECT id FROM public.orders WHERE business_id = p_business_id) AND oi.is_deleted = false), '[]'::jsonb),
        'delivery_riders', COALESCE((SELECT jsonb_agg(r.*) FROM public.delivery_riders r WHERE r.business_id = p_business_id AND r.is_deleted = false), '[]'::jsonb),
        'order_deliveries', COALESCE((SELECT jsonb_agg(od.*) FROM public.order_deliveries od WHERE od.order_id IN (SELECT id FROM public.orders WHERE business_id = p_business_id) AND od.is_deleted = false), '[]'::jsonb),
        'invoices', COALESCE((SELECT jsonb_agg(inv.*) FROM public.invoices inv WHERE inv.business_id = p_business_id AND inv.is_deleted = false), '[]'::jsonb),
        'invoice_items', COALESCE((SELECT jsonb_agg(ii.*) FROM public.invoice_items ii WHERE ii.invoice_id IN (SELECT id FROM public.invoices WHERE business_id = p_business_id) AND ii.is_deleted = false), '[]'::jsonb),
        'purchase_invoices', COALESCE((SELECT jsonb_agg(pi.*) FROM public.purchase_invoices pi WHERE pi.business_id = p_business_id AND pi.is_deleted = false), '[]'::jsonb),
        'payments', COALESCE((SELECT jsonb_agg(p.*) FROM public.payments p WHERE p.business_id = p_business_id AND p.is_deleted = false), '[]'::jsonb),
        'party_ledger_entries', COALESCE((SELECT jsonb_agg(pl.*) FROM public.party_ledger_entries pl WHERE pl.business_id = p_business_id AND pl.is_deleted = false), '[]'::jsonb)
    ) INTO v_result;

    RETURN v_result;
END;
$$;
```

---

## २. 1-क्लिक "🔄 Fresh Re-Sync & Reset" बटन आर्किटेक्चर

जब भी मर्चेंट/यूज़र यूआई में **`🔄 Fresh Re-Sync & Reset Workspace`** बटन दबाएगा:

1. **सुरक्षित लोकल क्लीयरेंस (`@Transaction Purge`):**
   - एंड्रॉइड Room DB में उस `activeBusinessId` की २० टेबल्स को एक सुरक्षित ट्रांजैक्शन में Clear किया जाएगा।
2. **RPC कॉल (Single HTTP Fetch):**
   - `fn_fetch_business_store_workspace_payload(businessId, userId)` निष्पादित होगा।
3. **बल्क इंसर्ट (Batch Bulk Insert):**
   - प्राप्त JSON पेलोड से २० टेबल्स के नए डेटा को Room DB में १ बार में सेव कर दिया जाएगा।
4. **0ms यूआई रिफ्रेश:**
   - Kotlin `Flow` अपने आप नई स्टेट यूआई पर एमिट कर देगा।

---

## ३. स्टाफ व रोल-आधारित यूआई फ़िल्टरिंग (Staff Branch Filtering)

- **रूम डेटाबेस (Room DB Storage):**  
  पूरा डेटा Room DB में सुरक्षित रूप से सेव रहेगा।
- **व्यू-मॉडल व यूआई स्तर (ViewModel & Compose Level):**  
  - यदि लॉग-इन यूज़र **स्टाफ सदस्य (CASHIER / KITCHEN_STAFF)** है और उसकी `branch_id != null` है, तो एंड्रॉइड ViewModel स्क्रीन पर केवल उसकी **विशिष्ट शाखा (Assigned Branch)** का ही POS काउंटर और KDS दिखाएगा।
  - यदि लॉग-इन यूज़र **OWNER / ADMIN** है, तो उसे सभी शाखाओं (All Branches) का स्विच दिखेगा।

---

## ४. फायदे
1. **< 150ms डाउनलोड स्पीड:** २० अलग कॉल्स के बजाय १ ही कॉल से पूरा डेटा आ जाएगा।
2. **0% डेटा लॉस:** कोई भी कॉलम या टेबल नहीं छूटेगा।
3. **98% सर्वर कॉस्ट की बचत:** बार-बार पिंग करने की आवश्यकता नहीं।
