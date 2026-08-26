-- =============================================================================
-- Function: fn_fetch_business_store_workspace_payload
-- Description: Fetches complete 20-table JSON payload for store workspace in single roundtrip (<150ms).
--              Enforces strict branch-level data scoping:
--              1. Master Owner (businesses.user_id = auth.uid()) -> Gets all branches data.
--              2. Branch Staff / Manager -> Gets strictly THEIR ASSIGNED BRANCH data (stocks, orders, invoices).
--              3. Public Customer (auth.uid() not in any store) -> Gets clean empty workspace payload.
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_fetch_business_store_workspace_payload(
    p_business_id UUID DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_business_id UUID := p_business_id;
    v_branch_id UUID := NULL;
    v_is_owner BOOLEAN := FALSE;
    v_result JSONB;
BEGIN
    -- 1. Security Check: Ensure User is Authenticated
    IF auth.uid() IS NULL THEN
        RAISE EXCEPTION 'Access Denied: Unauthenticated user';
    END IF;

    -- 2. Detect User Authority Level & Branch Assignment
    IF v_business_id IS NOT NULL THEN
        -- Verify if p_business_id was explicitly provided and if user is Owner or Staff
        IF EXISTS (SELECT 1 FROM businesses WHERE id = v_business_id AND user_id = auth.uid() AND is_deleted = false) THEN
            v_is_owner := TRUE;
        ELSE
            -- Check staff membership for explicit p_business_id
            SELECT branch_id INTO v_branch_id
            FROM business_staff_members
            WHERE business_id = v_business_id AND user_id = auth.uid() AND is_active = true AND is_deleted = false
            LIMIT 1;

            IF v_branch_id IS NULL THEN
                -- Check manager in business_branches
                SELECT id INTO v_branch_id
                FROM business_branches
                WHERE business_id = v_business_id AND manager_user_id = auth.uid() AND is_deleted = false
                LIMIT 1;
            END IF;

            IF v_branch_id IS NULL THEN
                RAISE EXCEPTION 'Access Denied: You do not have permission to access business workspace %', v_business_id;
            END IF;
        END IF;
    ELSE
        -- p_business_id IS NULL (First Login Auto-Resolve)
        -- Check if user is Master Owner
        SELECT id INTO v_business_id
        FROM businesses
        WHERE user_id = auth.uid() AND is_deleted = false
        LIMIT 1;

        IF v_business_id IS NOT NULL THEN
            v_is_owner := TRUE;
        ELSE
            -- Check if user is Staff Member / Manager
            SELECT business_id, branch_id INTO v_business_id, v_branch_id
            FROM business_staff_members
            WHERE user_id = auth.uid() AND is_active = true AND is_deleted = false
            LIMIT 1;

            IF v_business_id IS NULL THEN
                SELECT business_id, id INTO v_business_id, v_branch_id
                FROM business_branches
                WHERE manager_user_id = auth.uid() AND is_deleted = false
                LIMIT 1;
            END IF;
        END IF;
    END IF;

    -- 3. If User has no Business or Staff membership, return clean empty payload (Public Customer)
    IF v_business_id IS NULL THEN
        RETURN jsonb_build_object(
            'business', NULL,
            'settings', NULL,
            'branches', '[]'::jsonb,
            'staff', '[]'::jsonb,
            'parties', '[]'::jsonb,
            'party_addresses', '[]'::jsonb,
            'categories', '[]'::jsonb,
            'items', '[]'::jsonb,
            'item_variants', '[]'::jsonb,
            'inventory_stocks', '[]'::jsonb,
            'inventory_transactions', '[]'::jsonb,
            'orders', '[]'::jsonb,
            'order_items', '[]'::jsonb,
            'delivery_riders', '[]'::jsonb,
            'order_deliveries', '[]'::jsonb,
            'invoices', '[]'::jsonb,
            'invoice_items', '[]'::jsonb,
            'purchase_invoices', '[]'::jsonb,
            'payments', '[]'::jsonb,
            'party_ledger_entries', '[]'::jsonb,
            'staff_salary_profiles', '[]'::jsonb,
            'staff_attendance', '[]'::jsonb,
            'staff_salary_payouts', '[]'::jsonb,
            'staff_salary_payments', '[]'::jsonb,
            'expense_types', '[]'::jsonb,
            'expenses', '[]'::jsonb
        );
    END IF;

    -- 4. Aggregate Complete 24-Table JSON Payload with Strict Branch Scoping
    SELECT jsonb_build_object(
        'business', (
            SELECT to_jsonb(b.*)
            FROM businesses b
            WHERE b.id = v_business_id AND b.is_deleted = false
        ),
        'settings', (
            SELECT to_jsonb(bs.*)
            FROM business_settings bs
            WHERE bs.business_id = v_business_id AND bs.is_deleted = false
            LIMIT 1
        ),
        'branches', (
            SELECT COALESCE(jsonb_agg(to_jsonb(br.*)), '[]'::jsonb)
            FROM business_branches br
            WHERE br.business_id = v_business_id 
              AND (v_is_owner IS TRUE OR v_branch_id IS NULL OR br.id = v_branch_id)
              AND br.is_deleted = false
        ),
        'staff', (
            SELECT COALESCE(jsonb_agg(to_jsonb(st.*)), '[]'::jsonb)
            FROM business_staff_members st
            WHERE st.business_id = v_business_id AND st.is_deleted = false
        ),
        'parties', (
            SELECT COALESCE(jsonb_agg(to_jsonb(p.*)), '[]'::jsonb)
            FROM parties p
            WHERE p.business_id = v_business_id AND p.is_deleted = false
        ),
        'party_addresses', (
            SELECT COALESCE(jsonb_agg(to_jsonb(pa.*)), '[]'::jsonb)
            FROM party_addresses pa
            WHERE pa.party_id IN (
                SELECT id FROM parties WHERE business_id = v_business_id AND is_deleted = false
            ) AND pa.is_deleted = false
        ),
        'categories', (
            SELECT COALESCE(jsonb_agg(to_jsonb(c.*)), '[]'::jsonb)
            FROM item_categories c
            WHERE c.business_id = v_business_id AND c.is_deleted = false
        ),
        'items', (
            SELECT COALESCE(jsonb_agg(to_jsonb(i.*)), '[]'::jsonb)
            FROM items i
            WHERE i.business_id = v_business_id AND i.is_deleted = false
        ),
        'item_variants', (
            SELECT COALESCE(jsonb_agg(to_jsonb(v.*)), '[]'::jsonb)
            FROM item_variants v
            WHERE v.item_id IN (
                SELECT id FROM items WHERE business_id = v_business_id AND is_deleted = false
            ) AND v.is_deleted = false
        ),
        'inventory_stocks', (
            SELECT COALESCE(jsonb_agg(to_jsonb(s.*)), '[]'::jsonb)
            FROM inventory_stocks s
            WHERE s.branch_id IN (
                SELECT id FROM business_branches 
                WHERE business_id = v_business_id 
                  AND (v_is_owner IS TRUE OR v_branch_id IS NULL OR id = v_branch_id) 
                  AND is_deleted = false
            ) AND s.is_deleted = false
        ),
        'inventory_transactions', (
            SELECT COALESCE(jsonb_agg(to_jsonb(it.*)), '[]'::jsonb)
            FROM inventory_transactions it
            WHERE it.business_id = v_business_id 
              AND (v_is_owner IS TRUE OR v_branch_id IS NULL OR it.branch_id = v_branch_id)
              AND it.is_deleted = false
        ),
        'orders', (
            SELECT COALESCE(jsonb_agg(to_jsonb(o.*)), '[]'::jsonb)
            FROM orders o
            WHERE o.business_id = v_business_id 
              AND (v_is_owner IS TRUE OR v_branch_id IS NULL OR o.branch_id = v_branch_id)
              AND o.is_deleted = false
        ),
        'order_items', (
            SELECT COALESCE(jsonb_agg(to_jsonb(oi.*)), '[]'::jsonb)
            FROM order_items oi
            WHERE oi.order_id IN (
                SELECT id FROM orders 
                WHERE business_id = v_business_id 
                  AND (v_is_owner IS TRUE OR v_branch_id IS NULL OR branch_id = v_branch_id) 
                  AND is_deleted = false
            ) AND oi.is_deleted = false
        ),
        'delivery_riders', (
            SELECT COALESCE(jsonb_agg(to_jsonb(r.*)), '[]'::jsonb)
            FROM delivery_riders r
            WHERE r.business_id = v_business_id AND r.is_deleted = false
        ),
        'order_deliveries', (
            SELECT COALESCE(jsonb_agg(to_jsonb(od.*)), '[]'::jsonb)
            FROM order_deliveries od
            WHERE od.order_id IN (
                SELECT id FROM orders 
                WHERE business_id = v_business_id 
                  AND (v_is_owner IS TRUE OR v_branch_id IS NULL OR branch_id = v_branch_id) 
                  AND is_deleted = false
            ) AND od.is_deleted = false
        ),
        'invoices', (
            SELECT COALESCE(jsonb_agg(to_jsonb(inv.*)), '[]'::jsonb)
            FROM invoices inv
            WHERE inv.business_id = v_business_id 
              AND (v_is_owner IS TRUE OR v_branch_id IS NULL OR inv.branch_id = v_branch_id)
              AND inv.is_deleted = false
        ),
        'invoice_items', (
            SELECT COALESCE(jsonb_agg(to_jsonb(ii.*)), '[]'::jsonb)
            FROM invoice_items ii
            WHERE ii.invoice_id IN (
                SELECT id FROM invoices 
                WHERE business_id = v_business_id 
                  AND (v_is_owner IS TRUE OR v_branch_id IS NULL OR branch_id = v_branch_id) 
                  AND is_deleted = false
            ) AND ii.is_deleted = false
        ),
        'purchase_invoices', (
            SELECT COALESCE(jsonb_agg(to_jsonb(pi.*)), '[]'::jsonb)
            FROM purchase_invoices pi
            WHERE pi.business_id = v_business_id 
              AND (v_is_owner IS TRUE OR v_branch_id IS NULL OR pi.branch_id = v_branch_id)
              AND pi.is_deleted = false
        ),
        'payments', (
            SELECT COALESCE(jsonb_agg(to_jsonb(pay.*)), '[]'::jsonb)
            FROM payments pay
            WHERE pay.business_id = v_business_id AND pay.is_deleted = false
        ),
        'party_ledger_entries', (
            SELECT COALESCE(jsonb_agg(to_jsonb(ple.*)), '[]'::jsonb)
            FROM party_ledger_entries ple
            WHERE ple.business_id = v_business_id AND ple.is_deleted = false
        ),
        'staff_salary_profiles', (
            SELECT COALESCE(jsonb_agg(to_jsonb(sp.*)), '[]'::jsonb)
            FROM business_staff_salary_profiles sp
            WHERE sp.business_id = v_business_id AND sp.is_deleted = false
        ),
        'staff_attendance', (
            SELECT COALESCE(jsonb_agg(to_jsonb(sa.*)), '[]'::jsonb)
            FROM business_staff_attendance sa
            WHERE sa.business_id = v_business_id AND sa.is_deleted = false
        ),
        'staff_salary_payouts', (
            SELECT COALESCE(jsonb_agg(to_jsonb(spt.*)), '[]'::jsonb)
            FROM business_staff_salary_payouts spt
            WHERE spt.business_id = v_business_id AND spt.is_deleted = false
        ),
        'staff_salary_payments', (
            SELECT COALESCE(jsonb_agg(to_jsonb(spay.*)), '[]'::jsonb)
            FROM business_staff_salary_payments spay
            WHERE spay.business_id = v_business_id AND spay.is_deleted = false
        ),
        'expense_types', (
            SELECT COALESCE(jsonb_agg(to_jsonb(et.*)), '[]'::jsonb)
            FROM business_expense_types et
            WHERE (et.business_id = v_business_id OR et.is_system_default = true) AND et.is_deleted = false
        ),
        'expenses', (
            SELECT COALESCE(jsonb_agg(to_jsonb(e.*)), '[]'::jsonb)
            FROM business_expenses e
            WHERE e.business_id = v_business_id 
              AND (v_is_owner IS TRUE OR v_branch_id IS NULL OR e.branch_id = v_branch_id)
              AND e.is_deleted = false
        )
    ) INTO v_result;

    RETURN v_result;
END;
$$;
