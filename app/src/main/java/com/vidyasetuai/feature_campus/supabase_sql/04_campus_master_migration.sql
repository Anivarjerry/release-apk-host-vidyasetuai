-- ============================================================================
-- VIDYASETU CAMPUS - MASTER PRODUCTION MIGRATION SCRIPT
-- RUN THIS COMPLETE SCRIPT ONCE IN SUPABASE SQL EDITOR
-- ============================================================================

BEGIN;

-- 1. TABLE: campus_connections
CREATE TABLE IF NOT EXISTS public.campus_connections (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    target_user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    status TEXT NOT NULL DEFAULT 'FOLLOWING', -- 'FOLLOWING', 'BLOCKED'
    is_mutual BOOLEAN NOT NULL DEFAULT false, -- Trigger maintained
    peer_name TEXT,
    peer_username TEXT,
    peer_avatar_url TEXT,
    peer_bio TEXT,
    sync_version BIGINT NOT NULL DEFAULT 1,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT uq_campus_connection UNIQUE (user_id, target_user_id)
);

CREATE INDEX IF NOT EXISTS idx_campus_conn_user ON public.campus_connections(user_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_campus_conn_target ON public.campus_connections(target_user_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_campus_conn_mutual ON public.campus_connections(user_id, is_mutual) WHERE is_deleted = false AND is_mutual = true;
CREATE INDEX IF NOT EXISTS idx_campus_conn_updated ON public.campus_connections(user_id, updated_at DESC);

ALTER TABLE public.campus_connections ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Users can manage own campus connections" ON public.campus_connections;
CREATE POLICY "Users can manage own campus connections"
    ON public.campus_connections
    FOR ALL
    USING (
        user_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid())
    )
    WITH CHECK (
        user_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid())
    );

DROP POLICY IF EXISTS "Users can view incoming connections" ON public.campus_connections;
CREATE POLICY "Users can view incoming connections"
    ON public.campus_connections
    FOR SELECT
    USING (
        target_user_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid())
    );


-- 2. TABLE: campus_messages
CREATE TABLE IF NOT EXISTS public.campus_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id TEXT NOT NULL,
    sender_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    recipient_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    encrypted_payload TEXT NOT NULL,
    media_url TEXT,
    media_type TEXT NOT NULL DEFAULT 'TEXT',
    status TEXT NOT NULL DEFAULT 'SENT', -- 'SENT', 'DELIVERED', 'READ'
    is_saved BOOLEAN NOT NULL DEFAULT false,
    expires_at TIMESTAMPTZ DEFAULT (now() + interval '24 hours'),
    sync_version BIGINT NOT NULL DEFAULT 1,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT now(),
    delivered_at TIMESTAMPTZ,
    read_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_campus_msg_conv ON public.campus_messages(conversation_id, created_at DESC) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_campus_msg_unread ON public.campus_messages(recipient_id, status) WHERE is_deleted = false AND status != 'READ';
CREATE INDEX IF NOT EXISTS idx_campus_msg_expires ON public.campus_messages(expires_at) WHERE is_deleted = false AND is_saved = false;
CREATE INDEX IF NOT EXISTS idx_campus_msg_recipient_sync ON public.campus_messages(recipient_id, created_at DESC);

ALTER TABLE public.campus_messages ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Users can view sent or received messages" ON public.campus_messages;
CREATE POLICY "Users can view sent or received messages"
    ON public.campus_messages
    FOR SELECT
    USING (
        sender_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid()) OR
        recipient_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid())
    );

DROP POLICY IF EXISTS "Users can insert own sent messages" ON public.campus_messages;
CREATE POLICY "Users can insert own sent messages"
    ON public.campus_messages
    FOR INSERT
    WITH CHECK (
        sender_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid())
    );

DROP POLICY IF EXISTS "Participants can update message delivery status" ON public.campus_messages;
CREATE POLICY "Participants can update message delivery status"
    ON public.campus_messages
    FOR UPDATE
    USING (
        sender_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid()) OR
        recipient_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid())
    )
    WITH CHECK (
        sender_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid()) OR
        recipient_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid())
    );


-- 3. TRIGGER: Mutual Follow Synchronization
CREATE OR REPLACE FUNCTION public.fn_sync_mutual_connection_state()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP IN ('INSERT', 'UPDATE') THEN
        IF (NEW.status = 'FOLLOWING' AND NEW.is_deleted = false) AND EXISTS (
            SELECT 1 FROM public.campus_connections 
            WHERE user_id = NEW.target_user_id 
              AND target_user_id = NEW.user_id 
              AND status = 'FOLLOWING'
              AND is_deleted = false
        ) THEN
            NEW.is_mutual := true;
            UPDATE public.campus_connections 
            SET is_mutual = true, updated_at = NOW()
            WHERE user_id = NEW.target_user_id 
              AND target_user_id = NEW.user_id 
              AND is_mutual = false;
        ELSE
            NEW.is_mutual := false;
            UPDATE public.campus_connections 
            SET is_mutual = false, updated_at = NOW()
            WHERE user_id = NEW.target_user_id 
              AND target_user_id = NEW.user_id 
              AND is_mutual = true;
        END IF;
        RETURN NEW;
    END IF;

    IF TG_OP = 'DELETE' THEN
        UPDATE public.campus_connections 
        SET is_mutual = false, updated_at = NOW()
        WHERE user_id = OLD.target_user_id 
          AND target_user_id = OLD.user_id 
          AND is_mutual = true;
        RETURN OLD;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS trg_sync_campus_mutual ON public.campus_connections;
CREATE TRIGGER trg_sync_campus_mutual
BEFORE INSERT OR UPDATE ON public.campus_connections
FOR EACH ROW EXECUTE FUNCTION public.fn_sync_mutual_connection_state();


-- 4. RPCs: fn_send_campus_message, fn_mark_campus_chat_read, fn_fetch_campus_workspace_payload, fn_purge_expired_campus_messages
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

COMMIT;
