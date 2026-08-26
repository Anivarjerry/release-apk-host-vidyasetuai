-- =============================================================================
-- Function: fn_create_public_customer_order
-- Description: Atomic Public Customer Order placement with 4-digit Delivery OTP generation.
--              Inserts rows into orders, order_items, and order_deliveries in single transaction.
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_create_public_customer_order(
    p_business_id UUID,
    p_branch_id UUID,
    p_items_json JSONB,
    p_delivery_address_json JSONB,
    p_total_amount NUMERIC,
    p_payment_mode TEXT DEFAULT 'CASH'
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_order_id UUID := gen_random_uuid();
    v_order_number TEXT;
    v_delivery_otp TEXT;
    v_item JSONB;
    v_result JSONB;
BEGIN
    -- 1. Security Check: Ensure User is Authenticated
    IF auth.uid() IS NULL THEN
        RAISE EXCEPTION 'Access Denied: Unauthenticated user';
    END IF;

    -- 2. Generate Unique Order Number & Random 4-Digit OTP (e.g. 4892)
    v_order_number := 'ORD-' || UPPER(SUBSTRING(gen_random_uuid()::text FROM 1 FOR 8));
    v_delivery_otp := (FLOOR(RANDOM() * 8999 + 1000))::text;

    -- 3. Insert Order Row into orders Table
    INSERT INTO orders (
        id,
        business_id,
        branch_id,
        order_number,
        customer_user_id,
        status,
        payment_status,
        payment_mode,
        total_amount,
        delivery_address_json,
        delivery_otp,
        created_at,
        updated_at,
        is_deleted
    ) VALUES (
        v_order_id,
        p_business_id,
        p_branch_id,
        v_order_number,
        auth.uid(),
        'PENDING',
        'UNPAID',
        p_payment_mode,
        p_total_amount,
        p_delivery_address_json,
        v_delivery_otp,
        NOW(),
        NOW(),
        false
    );

    -- 4. Bulk Insert Items into order_items Table
    FOR v_item IN SELECT * FROM jsonb_array_elements(p_items_json)
    LOOP
        INSERT INTO order_items (
            id,
            order_id,
            item_id,
            variant_id,
            item_name,
            quantity,
            unit_price,
            total_price,
            created_at,
            is_deleted
        ) VALUES (
            gen_random_uuid(),
            v_order_id,
            (v_item->>'item_id')::UUID,
            (v_item->>'variant_id')::UUID,
            COALESCE(v_item->>'item_name', 'Item'),
            COALESCE((v_item->>'quantity')::NUMERIC, 1),
            COALESCE((v_item->>'unit_price')::NUMERIC, 0),
            COALESCE((v_item->>'total_price')::NUMERIC, 0),
            NOW(),
            false
        );
    END LOOP;

    -- 5. Insert Delivery Tracking Row into order_deliveries Table
    INSERT INTO order_deliveries (
        id,
        order_id,
        status,
        delivery_address_json,
        delivery_otp,
        created_at,
        is_deleted
    ) VALUES (
        gen_random_uuid(),
        v_order_id,
        'ASSIGNING',
        p_delivery_address_json,
        v_delivery_otp,
        NOW(),
        false
    );

    -- 6. Return Success Payload with Order Details & Delivery OTP
    v_result := jsonb_build_object(
        'success', true,
        'order_id', v_order_id,
        'order_number', v_order_number,
        'delivery_otp', v_delivery_otp,
        'status', 'PENDING',
        'total_amount', p_total_amount,
        'created_at', NOW()
    );

    RETURN v_result;
END;
$$;
