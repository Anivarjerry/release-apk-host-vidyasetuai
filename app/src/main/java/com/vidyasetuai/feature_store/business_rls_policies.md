# Database Security Reference - Business RLS Policies
> **स्थान**: `app/src/main/java/com/vidyasetuai/feature_store/business_rls_policies.md`  
> **फाइल प्रकार**: Markdown Reference (Gradle Build System इसे APK में 100% इग्नोर करेगा)

```sql
-- =========================================================================
-- VidyaSetu AI: Multi-Tenant Business Row Level Security (RLS) Policies
-- =========================================================================

-- Enable RLS on all Business Commerce tables
ALTER TABLE public.businesses ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.business_settings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.business_branches ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.business_staff_members ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.parties ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.party_addresses ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.item_categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.item_variants ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.inventory_stocks ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.inventory_transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.order_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.delivery_riders ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.order_deliveries ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.invoices ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.invoice_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.purchase_invoices ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.payments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.party_ledger_entries ENABLE ROW LEVEL SECURITY;

-- 1. Owner & Staff Access Policies
CREATE POLICY "Authenticated users access businesses" ON public.businesses
    FOR ALL TO authenticated USING (true) WITH CHECK (true);

CREATE POLICY "Authenticated users access business_settings" ON public.business_settings
    FOR ALL TO authenticated USING (true) WITH CHECK (true);

CREATE POLICY "Authenticated users access items" ON public.items
    FOR ALL TO authenticated USING (true) WITH CHECK (true);

CREATE POLICY "Authenticated users access orders" ON public.orders
    FOR ALL TO authenticated USING (true) WITH CHECK (true);

CREATE POLICY "Authenticated users access delivery_riders" ON public.delivery_riders
    FOR ALL TO authenticated USING (true) WITH CHECK (true);

-- 2. Public Anonymous Customer Read Access for Storefront
CREATE POLICY "Public read active businesses" ON public.businesses
    FOR SELECT TO anon USING (is_active = true AND is_deleted = false);

CREATE POLICY "Public read store items" ON public.items
    FOR SELECT TO anon USING (is_active = true AND is_deleted = false AND is_available_online = true);

CREATE POLICY "Public read store categories" ON public.item_categories
    FOR SELECT TO anon USING (is_active = true AND is_deleted = false);

-- 3. Storage Bucket Media Security for Product Images
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
    'business-media',
    'business-media',
    true,
    5242880,
    ARRAY['image/jpeg', 'image/png', 'image/webp', 'image/gif']
)
ON CONFLICT (id) DO UPDATE SET
    public = true,
    file_size_limit = 5242880,
    allowed_mime_types = ARRAY['image/jpeg', 'image/png', 'image/webp', 'image/gif'];
```
