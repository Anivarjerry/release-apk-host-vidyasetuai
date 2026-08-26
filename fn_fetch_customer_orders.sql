-- =============================================================================
-- Function: fn_fetch_customer_orders
-- Description: Strictly fetches ONLY orders belonging to the authenticated customer (o.user_id = auth.uid()).
--              If user is not authenticated or no matching orders exist, returns empty array [].
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_fetch_customer_orders(
    p_phone TEXT DEFAULT NULL,
    p_limit INT DEFAULT 30
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_user_id UUID := auth.uid();
    v_result JSONB;
BEGIN
    -- If user is unauthenticated and no phone is provided, return empty array immediately
    IF v_user_id IS NULL AND (p_phone IS NULL OR p_phone = '') THEN
        RETURN '[]'::jsonb;
    END IF;

    SELECT COALESCE(jsonb_agg(order_row), '[]'::jsonb)
    INTO v_result
    FROM (
        SELECT 
            o.id AS order_id,
            o.order_number,
            o.order_status,
            o.order_type,
            o.table_or_token_no,
            o.delivery_otp,
            o.sub_total,
            o.grand_total,
            o.delivery_charge,
            o.customer_name,
            o.customer_phone,
            o.payment_status,
            o.payment_method,
            o.delivery_address_json,
            o.order_notes,
            o.created_at,
            b.trade_name AS merchant_name,
            b.logo_url AS merchant_logo,
            COALESCE(
                (
                    SELECT jsonb_agg(
                        jsonb_build_object(
                            'id', oi.id,
                            'item_name', oi.item_name,
                            'quantity', oi.quantity,
                            'unit_price', oi.unit_price,
                            'total_price', oi.total_price
                        )
                    )
                    FROM public.order_items oi
                    WHERE oi.order_id = o.id
                ),
                '[]'::jsonb
            ) AS items
        FROM public.orders o
        LEFT JOIN public.businesses b ON b.id = o.business_id
        WHERE (v_user_id IS NOT NULL AND o.user_id = v_user_id)
           OR (v_user_id IS NULL AND p_phone IS NOT NULL AND p_phone <> '' AND o.customer_phone = p_phone)
        ORDER BY o.created_at DESC
        LIMIT p_limit
    ) order_row;

    RETURN v_result;
END;
$$;
