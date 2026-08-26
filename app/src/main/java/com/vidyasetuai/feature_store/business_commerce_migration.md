# Database Schema Reference - Business Commerce Migration
> **स्थान**: `app/src/main/java/com/vidyasetuai/feature_store/business_commerce_migration.md`  
> **फाइल प्रकार**: Markdown Reference (Gradle Build System इसे APK में 100% इग्नोर करेगा)

```sql
-- =========================================================================
-- VidyaSetu AI: Complete Business Management, POS & Commerce System Migration
-- =========================================================================

-- 1.1 Businesses Table
CREATE TABLE IF NOT EXISTS public.businesses (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    business_type text NOT NULL,
    business_tier text NOT NULL DEFAULT 'MICRO',
    legal_name text NOT NULL,
    trade_name text NOT NULL,
    slug text NOT NULL UNIQUE,
    owner_name text NOT NULL,
    phone text NOT NULL,
    email text NULL,
    gstin text NULL,
    is_composition boolean NOT NULL DEFAULT false,
    pan_number text NULL,
    currency text NOT NULL DEFAULT 'INR',
    state_code text NOT NULL DEFAULT '08',
    bank_name text NULL,
    bank_account_no text NULL,
    bank_ifsc text NULL,
    bank_branch text NULL,
    upi_id text NULL,
    logo_url text NULL,
    banner_url text NULL,
    description text NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT businesses_pkey PRIMARY KEY (id)
);

-- 1.2 Business Settings
CREATE TABLE IF NOT EXISTS public.business_settings (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    business_id uuid NOT NULL REFERENCES public.businesses(id) ON DELETE CASCADE,
    enable_gst_billing boolean NOT NULL DEFAULT false,
    enable_kds boolean NOT NULL DEFAULT false,
    enable_delivery_tracking boolean NOT NULL DEFAULT false,
    enable_inventory_tracking boolean NOT NULL DEFAULT true,
    enable_customer_khata boolean NOT NULL DEFAULT true,
    allow_online_orders boolean NOT NULL DEFAULT true,
    invoice_prefix text NOT NULL DEFAULT 'INV',
    thermal_printer_size text NOT NULL DEFAULT '3_INCH',
    delivery_charge_default numeric(10,2) NOT NULL DEFAULT 0.00,
    packing_charge_default numeric(10,2) NOT NULL DEFAULT 0.00,
    free_delivery_above numeric(10,2) NULL,
    opening_time text NULL,
    closing_time text NULL,
    is_store_open boolean NOT NULL DEFAULT true,
    gst_api_username text NULL,
    gst_api_password text NULL,
    gsp_provider_name text NOT NULL DEFAULT 'CLEARTAX',
    enable_auto_eway_bill boolean NOT NULL DEFAULT false,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT business_settings_pkey PRIMARY KEY (id),
    CONSTRAINT business_settings_business_id_key UNIQUE (business_id)
);

-- 1.3 Business Branches
CREATE TABLE IF NOT EXISTS public.business_branches (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    business_id uuid NOT NULL REFERENCES public.businesses(id) ON DELETE CASCADE,
    branch_name text NOT NULL,
    address_line text NOT NULL,
    city text NOT NULL,
    state text NOT NULL,
    pincode text NOT NULL,
    lat numeric(10,7) NULL,
    lng numeric(10,7) NULL,
    is_main_branch boolean NOT NULL DEFAULT true,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT business_branches_pkey PRIMARY KEY (id)
);

-- 1.4 Business Staff Members
CREATE TABLE IF NOT EXISTS public.business_staff_members (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    business_id uuid NOT NULL REFERENCES public.businesses(id) ON DELETE CASCADE,
    branch_id uuid NULL REFERENCES public.business_branches(id) ON DELETE SET NULL,
    user_id uuid NULL,
    name text NOT NULL,
    phone text NOT NULL,
    email text NULL,
    role text NOT NULL DEFAULT 'CASHIER',
    permissions jsonb NOT NULL DEFAULT '{}'::jsonb,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT business_staff_members_pkey PRIMARY KEY (id)
);

-- 2.1 Parties (Customers & Suppliers)
CREATE TABLE IF NOT EXISTS public.parties (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    business_id uuid NOT NULL REFERENCES public.businesses(id) ON DELETE CASCADE,
    user_id uuid NULL,
    party_type text NOT NULL,
    name text NOT NULL,
    phone text NOT NULL,
    email text NULL,
    gstin text NULL,
    pan_number text NULL,
    state_code text NULL,
    credit_limit numeric(12,2) NOT NULL DEFAULT 0.00,
    current_balance numeric(12,2) NOT NULL DEFAULT 0.00,
    opening_balance numeric(12,2) NOT NULL DEFAULT 0.00,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT parties_pkey PRIMARY KEY (id)
);

-- 2.2 Party Addresses
CREATE TABLE IF NOT EXISTS public.party_addresses (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    party_id uuid NOT NULL REFERENCES public.parties(id) ON DELETE CASCADE,
    user_id uuid NULL,
    address_type text NOT NULL DEFAULT 'HOME',
    full_address text NOT NULL,
    landmark text NULL,
    city text NOT NULL,
    state text NOT NULL,
    pincode text NOT NULL,
    lat numeric(10,7) NULL,
    lng numeric(10,7) NULL,
    is_default boolean NOT NULL DEFAULT true,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT party_addresses_pkey PRIMARY KEY (id)
);

-- 3.1 Item Categories
CREATE TABLE IF NOT EXISTS public.item_categories (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    business_id uuid NOT NULL REFERENCES public.businesses(id) ON DELETE CASCADE,
    name text NOT NULL,
    parent_category_id uuid NULL REFERENCES public.item_categories(id) ON DELETE SET NULL,
    icon_url text NULL,
    display_order integer NOT NULL DEFAULT 0,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT item_categories_pkey PRIMARY KEY (id)
);

-- 3.2 Items (Products & Menu)
CREATE TABLE IF NOT EXISTS public.items (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    business_id uuid NOT NULL REFERENCES public.businesses(id) ON DELETE CASCADE,
    category_id uuid NULL REFERENCES public.item_categories(id) ON DELETE SET NULL,
    item_type text NOT NULL DEFAULT 'PRODUCT',
    name text NOT NULL,
    description text NULL,
    sku text NULL,
    barcode text NULL,
    hsn_sac_code text NULL,
    tax_rate numeric(5,2) NOT NULL DEFAULT 0.00,
    is_tax_inclusive boolean NOT NULL DEFAULT true,
    purchase_price numeric(12,2) NOT NULL DEFAULT 0.00,
    sale_price numeric(12,2) NOT NULL,
    mrp numeric(12,2) NULL,
    unit text NOT NULL DEFAULT 'PCS',
    food_type text NULL DEFAULT 'NONE',
    is_available_online boolean NOT NULL DEFAULT true,
    image_url text NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT items_pkey PRIMARY KEY (id)
);

-- 3.3 Item Variants
CREATE TABLE IF NOT EXISTS public.item_variants (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    item_id uuid NOT NULL REFERENCES public.items(id) ON DELETE CASCADE,
    variant_name text NOT NULL,
    sale_price numeric(12,2) NOT NULL,
    purchase_price numeric(12,2) NOT NULL DEFAULT 0.00,
    barcode text NULL,
    sku text NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT item_variants_pkey PRIMARY KEY (id)
);

-- 3.4 Inventory Stocks
CREATE TABLE IF NOT EXISTS public.inventory_stocks (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    item_id uuid NOT NULL REFERENCES public.items(id) ON DELETE CASCADE,
    variant_id uuid NULL REFERENCES public.item_variants(id) ON DELETE CASCADE,
    branch_id uuid NOT NULL REFERENCES public.business_branches(id) ON DELETE CASCADE,
    current_stock numeric(12,3) NOT NULL DEFAULT 0.000,
    low_stock_threshold numeric(12,3) NOT NULL DEFAULT 5.000,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT inventory_stocks_pkey PRIMARY KEY (id)
);

-- 4.1 Orders Table
CREATE TABLE IF NOT EXISTS public.orders (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    business_id uuid NOT NULL REFERENCES public.businesses(id) ON DELETE CASCADE,
    branch_id uuid NOT NULL REFERENCES public.business_branches(id) ON DELETE CASCADE,
    order_number text NOT NULL,
    party_id uuid NULL REFERENCES public.parties(id) ON DELETE SET NULL,
    user_id uuid NULL,
    customer_name text NOT NULL,
    customer_phone text NOT NULL,
    order_type text NOT NULL DEFAULT 'COUNTER_POS',
    table_or_token_no text NULL,
    order_status text NOT NULL DEFAULT 'PLACED',
    delivery_otp text NOT NULL,
    sub_total numeric(12,2) NOT NULL DEFAULT 0.00,
    tax_total numeric(12,2) NOT NULL DEFAULT 0.00,
    delivery_charge numeric(10,2) NOT NULL DEFAULT 0.00,
    packing_charge numeric(10,2) NOT NULL DEFAULT 0.00,
    discount_amount numeric(10,2) NOT NULL DEFAULT 0.00,
    grand_total numeric(12,2) NOT NULL DEFAULT 0.00,
    payment_status text NOT NULL DEFAULT 'UNPAID',
    payment_method text NOT NULL DEFAULT 'CASH_ON_DELIVERY',
    delivery_address_json jsonb NULL,
    order_notes text NULL,
    cancelled_reason text NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT orders_pkey PRIMARY KEY (id)
);

-- 4.2 Order Items
CREATE TABLE IF NOT EXISTS public.order_items (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    order_id uuid NOT NULL REFERENCES public.orders(id) ON DELETE CASCADE,
    item_id uuid NULL REFERENCES public.items(id) ON DELETE SET NULL,
    variant_id uuid NULL REFERENCES public.item_variants(id) ON DELETE SET NULL,
    item_name text NOT NULL,
    variant_name text NULL,
    quantity numeric(12,3) NOT NULL,
    unit_price numeric(12,2) NOT NULL,
    tax_rate numeric(5,2) NOT NULL DEFAULT 0.00,
    tax_amount numeric(10,2) NOT NULL DEFAULT 0.00,
    discount_amount numeric(10,2) NOT NULL DEFAULT 0.00,
    total_price numeric(12,2) NOT NULL,
    item_notes text NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT order_items_pkey PRIMARY KEY (id)
);

-- 5.1 Delivery Riders
CREATE TABLE IF NOT EXISTS public.delivery_riders (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    business_id uuid NOT NULL REFERENCES public.businesses(id) ON DELETE CASCADE,
    user_id uuid NOT NULL,
    name text NOT NULL,
    phone text NOT NULL,
    vehicle_number text NULL,
    vehicle_type text NOT NULL DEFAULT 'BIKE',
    is_online boolean NOT NULL DEFAULT false,
    current_lat numeric(10,7) NULL,
    current_lng numeric(10,7) NULL,
    heading numeric(5,2) NULL,
    speed numeric(5,2) NULL,
    last_ping_at timestamp with time zone NULL,
    active_order_id uuid NULL REFERENCES public.orders(id) ON DELETE SET NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT delivery_riders_pkey PRIMARY KEY (id)
);

-- 6.1 Invoices
CREATE TABLE IF NOT EXISTS public.invoices (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    business_id uuid NOT NULL REFERENCES public.businesses(id) ON DELETE CASCADE,
    branch_id uuid NOT NULL REFERENCES public.business_branches(id) ON DELETE CASCADE,
    order_id uuid NULL REFERENCES public.orders(id) ON DELETE SET NULL,
    party_id uuid NULL REFERENCES public.parties(id) ON DELETE SET NULL,
    user_id uuid NULL,
    invoice_type text NOT NULL DEFAULT 'TAX_INVOICE',
    invoice_number text NOT NULL,
    invoice_date date NOT NULL DEFAULT CURRENT_DATE,
    due_date date NULL,
    place_of_supply text NOT NULL DEFAULT '08',
    is_interstate boolean NOT NULL DEFAULT false,
    eway_bill_number text NULL,
    eway_bill_date timestamp with time zone NULL,
    eway_bill_valid_until timestamp with time zone NULL,
    vehicle_number text NULL,
    transport_name text NULL,
    transporter_id text NULL,
    distance_km integer DEFAULT 0,
    eway_status text NOT NULL DEFAULT 'NOT_GENERATED',
    taxable_amount numeric(12,2) NOT NULL DEFAULT 0.00,
    cgst_amount numeric(12,2) NOT NULL DEFAULT 0.00,
    sgst_amount numeric(12,2) NOT NULL DEFAULT 0.00,
    igst_amount numeric(12,2) NOT NULL DEFAULT 0.00,
    cess_amount numeric(12,2) NOT NULL DEFAULT 0.00,
    delivery_charge numeric(10,2) NOT NULL DEFAULT 0.00,
    packing_charge numeric(10,2) NOT NULL DEFAULT 0.00,
    discount_total numeric(12,2) NOT NULL DEFAULT 0.00,
    round_off numeric(5,2) NOT NULL DEFAULT 0.00,
    grand_total numeric(12,2) NOT NULL DEFAULT 0.00,
    paid_amount numeric(12,2) NOT NULL DEFAULT 0.00,
    due_amount numeric(12,2) NOT NULL DEFAULT 0.00,
    payment_status text NOT NULL DEFAULT 'UNPAID',
    notes text NULL,
    terms_conditions text NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT invoices_pkey PRIMARY KEY (id)
);

-- 6.2 Invoice Items
CREATE TABLE IF NOT EXISTS public.invoice_items (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    invoice_id uuid NOT NULL REFERENCES public.invoices(id) ON DELETE CASCADE,
    item_id uuid NULL REFERENCES public.items(id) ON DELETE SET NULL,
    item_name text NOT NULL,
    hsn_sac_code text NULL,
    quantity numeric(12,3) NOT NULL,
    unit text NOT NULL DEFAULT 'PCS',
    unit_rate numeric(12,2) NOT NULL,
    discount_amount numeric(10,2) NOT NULL DEFAULT 0.00,
    taxable_value numeric(12,2) NOT NULL,
    gst_rate numeric(5,2) NOT NULL DEFAULT 0.00,
    cgst_rate numeric(5,2) NOT NULL DEFAULT 0.00,
    cgst_amount numeric(10,2) NOT NULL DEFAULT 0.00,
    sgst_rate numeric(5,2) NOT NULL DEFAULT 0.00,
    sgst_amount numeric(10,2) NOT NULL DEFAULT 0.00,
    igst_rate numeric(5,2) NOT NULL DEFAULT 0.00,
    igst_amount numeric(10,2) NOT NULL DEFAULT 0.00,
    total_amount numeric(12,2) NOT NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT invoice_items_pkey PRIMARY KEY (id)
);

-- 7.1 Payments
CREATE TABLE IF NOT EXISTS public.payments (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    business_id uuid NOT NULL REFERENCES public.businesses(id) ON DELETE CASCADE,
    order_id uuid NULL REFERENCES public.orders(id) ON DELETE SET NULL,
    invoice_id uuid NULL REFERENCES public.invoices(id) ON DELETE SET NULL,
    party_id uuid NULL REFERENCES public.parties(id) ON DELETE SET NULL,
    user_id uuid NULL,
    amount numeric(12,2) NOT NULL,
    payment_mode text NOT NULL,
    payment_status text NOT NULL DEFAULT 'PENDING',
    collected_by_type text NOT NULL DEFAULT 'CASHIER',
    collected_by_user_id uuid NULL,
    gateway_provider text NULL,
    gateway_order_id text NULL,
    gateway_payment_id text NULL,
    gateway_signature text NULL,
    gateway_response_json jsonb NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT payments_pkey PRIMARY KEY (id)
);

-- 8.1 Party Ledger Entries
CREATE TABLE IF NOT EXISTS public.party_ledger_entries (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    business_id uuid NOT NULL REFERENCES public.businesses(id) ON DELETE CASCADE,
    party_id uuid NOT NULL REFERENCES public.parties(id) ON DELETE CASCADE,
    user_id uuid NULL,
    entry_type text NOT NULL,
    amount numeric(12,2) NOT NULL,
    balance_after numeric(12,2) NOT NULL,
    reference_type text NOT NULL,
    reference_id uuid NULL,
    description text NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by uuid NULL,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by uuid NULL,
    sync_version bigint NOT NULL DEFAULT 1,
    CONSTRAINT party_ledger_entries_pkey PRIMARY KEY (id)
);
```
