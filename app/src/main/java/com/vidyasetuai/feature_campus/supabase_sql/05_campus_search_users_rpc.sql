-- ============================================================================
-- VIDYASETU CAMPUS - 05: RPC TO SEARCH CAMPUS USERS WITH CONNECTION STATUS
-- ============================================================================

CREATE OR REPLACE FUNCTION public.fn_search_campus_users(
    p_query TEXT,
    p_limit INT DEFAULT 20
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_auth_id UUID := auth.uid();
    v_current_user_id UUID;
    v_search_pattern TEXT;
    v_results JSONB;
BEGIN
    IF v_auth_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'UNAUTHORIZED');
    END IF;

    -- Resolve public.users.id from auth_id
    SELECT id INTO v_current_user_id 
    FROM public.users 
    WHERE auth_id = v_auth_id OR id = v_auth_id 
    LIMIT 1;

    IF v_current_user_id IS NULL THEN
        v_current_user_id := v_auth_id;
    END IF;

    v_search_pattern := '%' || trim(p_query) || '%';

    IF length(trim(p_query)) < 2 THEN
        RETURN jsonb_build_object('success', true, 'users', '[]'::jsonb);
    END IF;

    SELECT COALESCE(jsonb_agg(
        jsonb_build_object(
            'id', p.user_id,
            'username', COALESCE(p.username, ''),
            'full_name', COALESCE(p.full_name, CONCAT_WS(' ', p.first_name, p.last_name), 'Campus Member'),
            'avatar_url', COALESCE(p.profile_picture_url, ''),
            'bio', COALESCE(p.bio, ''),
            'is_verified', COALESCE(p.is_verified, false),
            'connection_status', CASE
                WHEN (c_out.id IS NOT NULL AND c_out.status = 'FOLLOWING' AND c_out.is_deleted = false)
                 AND (c_in.id IS NOT NULL AND c_in.status = 'FOLLOWING' AND c_in.is_deleted = false) THEN 'MUTUAL'
                WHEN (c_out.id IS NOT NULL AND c_out.status = 'FOLLOWING' AND c_out.is_deleted = false) THEN 'INSPIRED'
                WHEN (c_in.id IS NOT NULL AND c_in.status = 'FOLLOWING' AND c_in.is_deleted = false) THEN 'INSPIRES_YOU'
                ELSE 'NONE'
            END

        )
    ), '[]'::jsonb)
    INTO v_results
    FROM public.user_profiles p
    LEFT JOIN public.campus_connections c_out 
        ON c_out.user_id = v_current_user_id 
        AND c_out.target_user_id = p.user_id 
        AND c_out.is_deleted = false
    LEFT JOIN public.campus_connections c_in 
        ON c_in.user_id = p.user_id 
        AND c_in.target_user_id = v_current_user_id 
        AND c_in.is_deleted = false
    WHERE p.user_id != v_current_user_id
      AND (
          p.username ILIKE v_search_pattern
          OR p.full_name ILIKE v_search_pattern
          OR p.first_name ILIKE v_search_pattern
          OR p.last_name ILIKE v_search_pattern
      )
    LIMIT p_limit;

    RETURN jsonb_build_object(
        'success', true,
        'users', v_results
    );
END;
$$;


-- ============================================================================
-- 2. RPC: fn_toggle_campus_inspire (Atomic Guaranteed Conflict-Free Inspire / Uninspire)
-- ============================================================================
CREATE OR REPLACE FUNCTION public.fn_toggle_campus_inspire(
    p_target_user_id UUID,
    p_action TEXT DEFAULT 'TOGGLE' -- 'INSPIRE', 'UNINSPIRE', 'TOGGLE'
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_auth_id UUID := auth.uid();
    v_current_user_id UUID;
    v_existing RECORD;
    v_new_status TEXT;
    v_is_mutual BOOLEAN := false;
BEGIN
    IF v_auth_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'UNAUTHORIZED');
    END IF;

    SELECT id INTO v_current_user_id 
    FROM public.users 
    WHERE auth_id = v_auth_id OR id = v_auth_id 
    LIMIT 1;

    IF v_current_user_id IS NULL THEN
        v_current_user_id := v_auth_id;
    END IF;

    IF v_current_user_id = p_target_user_id THEN
        RETURN jsonb_build_object('success', false, 'error', 'CANNOT_INSPIRE_SELF');
    END IF;

    SELECT * INTO v_existing 
    FROM public.campus_connections
    WHERE user_id = v_current_user_id AND target_user_id = p_target_user_id
    LIMIT 1;

    IF v_existing.id IS NULL THEN
        -- Insert new following row
        INSERT INTO public.campus_connections (user_id, target_user_id, status, is_deleted, updated_at)
        VALUES (v_current_user_id, p_target_user_id, 'FOLLOWING', false, now());
        v_new_status := 'INSPIRED';
    ELSE
        -- Update existing row
        IF v_existing.is_deleted = true OR p_action = 'INSPIRE' THEN
            UPDATE public.campus_connections
            SET is_deleted = false, status = 'FOLLOWING', updated_at = now()
            WHERE id = v_existing.id;
            v_new_status := 'INSPIRED';
        ELSE
            UPDATE public.campus_connections
            SET is_deleted = true, is_mutual = false, updated_at = now()
            WHERE id = v_existing.id;
            v_new_status := 'NONE';
        END IF;
    END IF;

    -- Check if reciprocal follow exists
    SELECT EXISTS (
        SELECT 1 FROM public.campus_connections
        WHERE user_id = p_target_user_id AND target_user_id = v_current_user_id AND is_deleted = false AND status = 'FOLLOWING'
    ) INTO v_is_mutual;

    IF v_new_status = 'INSPIRED' AND v_is_mutual THEN
        v_new_status := 'MUTUAL';
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'connection_status', v_new_status
    );
END;
$$;

