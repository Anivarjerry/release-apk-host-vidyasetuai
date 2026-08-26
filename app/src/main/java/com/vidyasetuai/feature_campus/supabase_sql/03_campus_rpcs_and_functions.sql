-- ============================================================================
-- VIDYASETU CAMPUS - 03: RPCS & STORED FUNCTIONS
-- ============================================================================

-- 1. RPC: fn_send_campus_message (Atomic Secure Message Sender with Block Check)
CREATE OR REPLACE FUNCTION public.fn_send_campus_message(
    p_conversation_id TEXT,
    p_recipient_id UUID,
    p_encrypted_payload TEXT,
    p_media_url TEXT DEFAULT NULL,
    p_media_type TEXT DEFAULT 'TEXT'
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_auth_id UUID := auth.uid();
    v_sender_id UUID;
    v_new_msg RECORD;
    v_is_blocked BOOLEAN := false;
BEGIN
    IF v_auth_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'UNAUTHORIZED');
    END IF;

    -- Resolve public.users.id from auth_id
    SELECT id INTO v_sender_id 
    FROM public.users 
    WHERE auth_id = v_auth_id OR id = v_auth_id 
    LIMIT 1;

    IF v_sender_id IS NULL THEN
        v_sender_id := v_auth_id;
    END IF;

    -- Check if recipient has blocked sender
    SELECT EXISTS (
        SELECT 1 FROM public.campus_connections
        WHERE user_id = p_recipient_id 
          AND target_user_id = v_sender_id 
          AND status = 'BLOCKED'
          AND is_deleted = false
    ) INTO v_is_blocked;

    IF v_is_blocked THEN
        RETURN jsonb_build_object(
            'success', false, 
            'error', 'BLOCKED_BY_RECIPIENT', 
            'message', 'Unable to deliver message'
        );
    END IF;

    -- Insert message
    INSERT INTO public.campus_messages (
        conversation_id,
        sender_id,
        recipient_id,
        encrypted_payload,
        media_url,
        media_type,
        status,
        expires_at,
        created_at
    ) VALUES (
        p_conversation_id,
        v_sender_id,
        p_recipient_id,
        p_encrypted_payload,
        p_media_url,
        p_media_type,
        'SENT',
        now() + interval '24 hours',
        now()
    )
    RETURNING * INTO v_new_msg;

    RETURN jsonb_build_object(
        'success', true,
        'message_id', v_new_msg.id,
        'conversation_id', v_new_msg.conversation_id,
        'status', v_new_msg.status,
        'created_at', v_new_msg.created_at,
        'expires_at', v_new_msg.expires_at
    );
END;
$$;


-- 2. RPC: fn_mark_campus_chat_read (Instant Read Receipts & Delivery Updates)
CREATE OR REPLACE FUNCTION public.fn_mark_campus_chat_read(
    p_conversation_id TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_auth_id UUID := auth.uid();
    v_user_id UUID;
    v_updated_count INT;
BEGIN
    IF v_auth_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'UNAUTHORIZED');
    END IF;

    -- Resolve public.users.id from auth_id
    SELECT id INTO v_user_id 
    FROM public.users 
    WHERE auth_id = v_auth_id OR id = v_auth_id 
    LIMIT 1;

    IF v_user_id IS NULL THEN
        v_user_id := v_auth_id;
    END IF;

    UPDATE public.campus_messages
    SET status = 'READ',
        read_at = now()
    WHERE conversation_id = p_conversation_id
      AND recipient_id = v_user_id
      AND status != 'READ';

    GET DIAGNOSTICS v_updated_count = ROW_COUNT;

    RETURN jsonb_build_object(
        'success', true,
        'updated_count', v_updated_count
    );
END;
$$;


-- 3. RPC: fn_fetch_campus_workspace_payload (1-Roundtrip 0ms Offline Sync)
CREATE OR REPLACE FUNCTION public.fn_fetch_campus_workspace_payload()
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_auth_id UUID := auth.uid();
    v_user_id UUID;
    v_connections JSONB;
    v_messages JSONB;
    v_incoming_requests JSONB;
BEGIN
    IF v_auth_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'UNAUTHORIZED');
    END IF;

    -- Resolve public.users.id from auth_id
    SELECT id INTO v_user_id 
    FROM public.users 
    WHERE auth_id = v_auth_id OR id = v_auth_id 
    LIMIT 1;

    IF v_user_id IS NULL THEN
        v_user_id := v_auth_id;
    END IF;

    -- 1. Fetch user connections (following + mutual)
    SELECT COALESCE(jsonb_agg(
        jsonb_build_object(
            'id', c.id,
            'user_id', c.user_id,
            'target_user_id', c.target_user_id,
            'status', c.status,
            'is_mutual', c.is_mutual,
            'peer_name', COALESCE(c.peer_name, p.full_name, CONCAT_WS(' ', p.first_name, p.last_name), 'Campus Friend'),
            'peer_username', COALESCE(c.peer_username, p.username, ''),
            'peer_avatar_url', COALESCE(c.peer_avatar_url, p.profile_picture_url, ''),
            'peer_bio', COALESCE(c.peer_bio, p.bio, ''),
            'sync_version', c.sync_version,
            'is_deleted', c.is_deleted,
            'updated_at', c.updated_at
        )
    ), '[]'::jsonb)
    INTO v_connections
    FROM public.campus_connections c
    LEFT JOIN public.user_profiles p ON p.user_id = c.target_user_id
    WHERE c.user_id = v_user_id AND c.is_deleted = false;

    -- 2. Fetch incoming requests (users who follow me where is_mutual is false)
    SELECT COALESCE(jsonb_agg(
        jsonb_build_object(
            'id', c.id,
            'user_id', c.user_id,
            'target_user_id', c.target_user_id,
            'status', c.status,
            'is_mutual', c.is_mutual,
            'peer_name', COALESCE(p.full_name, CONCAT_WS(' ', p.first_name, p.last_name), 'Campus User'),
            'peer_username', COALESCE(p.username, ''),
            'peer_avatar_url', COALESCE(p.profile_picture_url, ''),
            'peer_bio', COALESCE(p.bio, ''),
            'created_at', c.created_at
        )
    ), '[]'::jsonb)
    INTO v_incoming_requests
    FROM public.campus_connections c
    LEFT JOIN public.user_profiles p ON p.user_id = c.user_id
    WHERE c.target_user_id = v_user_id 
      AND c.status = 'FOLLOWING' 
      AND c.is_mutual = false 
      AND c.is_deleted = false;

    -- 3. Fetch active messages (last 24h OR saved/starred)
    SELECT COALESCE(jsonb_agg(
        jsonb_build_object(
            'id', m.id,
            'conversation_id', m.conversation_id,
            'sender_id', m.sender_id,
            'recipient_id', m.recipient_id,
            'encrypted_payload', m.encrypted_payload,
            'media_url', m.media_url,
            'media_type', m.media_type,
            'status', m.status,
            'is_saved', m.is_saved,
            'expires_at', m.expires_at,
            'sync_version', m.sync_version,
            'is_deleted', m.is_deleted,
            'created_at', m.created_at,
            'delivered_at', m.delivered_at,
            'read_at', m.read_at
        ) ORDER BY m.created_at ASC
    ), '[]'::jsonb)
    INTO v_messages
    FROM public.campus_messages m
    WHERE (m.sender_id = v_user_id OR m.recipient_id = v_user_id)
      AND m.is_deleted = false
      AND (m.is_saved = true OR m.expires_at > now());

    RETURN jsonb_build_object(
        'success', true,
        'connections', v_connections,
        'incoming_requests', v_incoming_requests,
        'messages', v_messages,
        'server_timestamp', now()
    );
END;
$$;


-- 4. RPC: fn_purge_expired_campus_messages (24-Hour Auto-Vanish Janitor)
CREATE OR REPLACE FUNCTION public.fn_purge_expired_campus_messages()
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_purged_count INT;
BEGIN
    DELETE FROM public.campus_messages
    WHERE is_saved = false 
      AND expires_at <= now();

    GET DIAGNOSTICS v_purged_count = ROW_COUNT;

    RETURN jsonb_build_object(
        'success', true,
        'purged_count', v_purged_count,
        'purged_at', now()
    );
END;
$$;


-- 5. RPC: fn_search_campus_users (Debounced User Search with Bidirectional Connection Status)
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
                WHEN (c_out.is_mutual = true OR c_in.is_mutual = true) THEN 'MUTUAL'
                WHEN c_out.id IS NOT NULL AND c_out.status = 'FOLLOWING' AND c_out.is_deleted = false THEN 'INSPIRED'
                WHEN c_in.id IS NOT NULL AND c_in.status = 'FOLLOWING' AND c_in.is_deleted = false THEN 'INSPIRES_YOU'
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

