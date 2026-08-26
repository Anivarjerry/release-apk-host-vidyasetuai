-- =============================================================================
-- Function: fn_fetch_public_marketplace_products
-- Description: Paginated hyper-local public product marketplace query (<100ms).
--              Accepts optional search query, category filter, limit (10-15), and offset.
--              Includes merchant branch city, latitude (br.lat), longitude (br.lng),
--              and merchant business_slug (b.slug).
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_fetch_public_marketplace_products(
    p_limit INT DEFAULT 15,
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
    SELECT COALESCE(jsonb_agg(product_data), '[]'::jsonb)
    INTO v_result
    FROM (
        SELECT 
            i.id AS item_id,
            i.business_id,
            b.slug AS business_slug,
            i.category_id,
            i.name AS item_name,
            i.description,
            i.unit,
            i.sale_price AS selling_price,
            i.mrp,
            i.image_url,
            b.trade_name AS merchant_name,
            b.logo_url AS merchant_logo,
            br.id AS branch_id,
            br.branch_name,
            br.city,
            br.lat,
            br.lng,
            c.name AS category_name
        FROM items i
        JOIN businesses b ON b.id = i.business_id AND b.is_active = true AND b.is_deleted = false
        LEFT JOIN business_branches br ON br.business_id = b.id AND br.is_deleted = false AND br.is_main_branch = true
        LEFT JOIN item_categories c ON c.id = i.category_id AND c.is_deleted = false
        WHERE i.is_deleted = false
          AND i.is_active = true
          AND (
              p_search_query IS NULL 
              OR p_search_query = '' 
              OR i.name ILIKE '%' || p_search_query || '%'
              OR i.description ILIKE '%' || p_search_query || '%'
              OR b.trade_name ILIKE '%' || p_search_query || '%'
              OR (c.name IS NOT NULL AND c.name ILIKE '%' || p_search_query || '%')
          )
          AND (
              p_category_id IS NULL 
              OR i.category_id = p_category_id
          )
        ORDER BY i.created_at DESC
        LIMIT p_limit
        OFFSET p_offset
    ) product_data;

    RETURN v_result;
END;
$$;
