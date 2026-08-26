-- =============================================================================
-- Function: fn_fetch_public_marketplace_stores
-- Description: Paginated hyper-local public store search & marketplace query (<100ms).
--              Accepts optional search query, category filter, limit (10-15), and offset.
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_fetch_public_marketplace_stores(
    p_limit INT DEFAULT 10,
    p_offset INT DEFAULT 0,
    p_search_query TEXT DEFAULT NULL,
    p_category_id UUID DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_result JSONB;
BEGIN
    SELECT COALESCE(jsonb_agg(store_data), '[]'::jsonb)
    INTO v_result
    FROM (
        SELECT 
            b.id AS business_id,
            b.legal_name,
            b.trade_name,
            b.business_type,
            b.logo_url,
            b.phone,
            b.email,
            (
                SELECT to_jsonb(br.*)
                FROM business_branches br
                WHERE br.business_id = b.id AND br.is_deleted = false
                LIMIT 1
            ) AS primary_branch,
            (
                SELECT COUNT(*)
                FROM items i
                WHERE i.business_id = b.id AND i.is_active = true AND i.is_deleted = false
            ) AS total_items_count
        FROM businesses b
        WHERE b.is_deleted = false
          AND b.is_active = true
          AND (
              p_search_query IS NULL 
              OR p_search_query = '' 
              OR b.trade_name ILIKE '%' || p_search_query || '%'
              OR b.legal_name ILIKE '%' || p_search_query || '%'
              OR EXISTS (
                  SELECT 1 FROM items it 
                  WHERE it.business_id = b.id 
                    AND it.name ILIKE '%' || p_search_query || '%'
                    AND it.is_deleted = false
              )
          )
        ORDER BY b.created_at DESC
        LIMIT p_limit
        OFFSET p_offset
    ) store_data;

    RETURN v_result;
END;
$$;
