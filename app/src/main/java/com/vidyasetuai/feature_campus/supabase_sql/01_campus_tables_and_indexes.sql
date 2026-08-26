-- ============================================================================
-- VIDYASETU CAMPUS - 01: TABLES, INDEXES & RLS POLICIES
-- ============================================================================

-- 1. TABLE: campus_connections (Followers, Following, Mutual Friends & Offline Cache)
CREATE TABLE IF NOT EXISTS public.campus_connections (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    target_user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    status TEXT NOT NULL DEFAULT 'FOLLOWING', -- 'FOLLOWING', 'BLOCKED'
    is_mutual BOOLEAN NOT NULL DEFAULT false, -- Computed automatically by trigger
    peer_name TEXT,                          -- Cached friend name for 0ms offline display
    peer_username TEXT,                      -- Cached friend @username
    peer_avatar_url TEXT,                    -- Remote avatar URL
    peer_bio TEXT,                           -- Status / Bio
    sync_version BIGINT NOT NULL DEFAULT 1,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT uq_campus_connection UNIQUE (user_id, target_user_id)
);

-- Performance Indexes for 0ms Connection Reads
CREATE INDEX IF NOT EXISTS idx_campus_conn_user ON public.campus_connections(user_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_campus_conn_target ON public.campus_connections(target_user_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_campus_conn_mutual ON public.campus_connections(user_id, is_mutual) WHERE is_deleted = false AND is_mutual = true;
CREATE INDEX IF NOT EXISTS idx_campus_conn_updated ON public.campus_connections(user_id, updated_at DESC);

-- Enable RLS
ALTER TABLE public.campus_connections ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can manage own campus connections"
    ON public.campus_connections
    FOR ALL
    USING (
        user_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid())
    )
    WITH CHECK (
        user_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid())
    );

CREATE POLICY "Users can view incoming connections"
    ON public.campus_connections
    FOR SELECT
    USING (
        target_user_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid())
    );


-- 2. TABLE: campus_messages (E2EE Messages, Media, Delivery Ticks & 24h Vanish)
CREATE TABLE IF NOT EXISTS public.campus_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id TEXT NOT NULL,           -- Sorted hash: 'conv_minUUID_maxUUID'
    sender_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    recipient_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    encrypted_payload TEXT NOT NULL,         -- AES-256 encrypted payload (Zero Plaintext on Server)
    media_url TEXT,                          -- Remote Storage Media link
    media_type TEXT NOT NULL DEFAULT 'TEXT', -- 'TEXT', 'IMAGE', 'VOICE', 'FILE'
    status TEXT NOT NULL DEFAULT 'SENT',     -- 'SENT', 'DELIVERED', 'READ'
    is_saved BOOLEAN NOT NULL DEFAULT false, -- Starred/Saved messages do not expire
    expires_at TIMESTAMPTZ DEFAULT (now() + interval '24 hours'),
    sync_version BIGINT NOT NULL DEFAULT 1,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT now(),
    delivered_at TIMESTAMPTZ,
    read_at TIMESTAMPTZ
);

-- Performance Indexes for 0ms Chat Queries & Unread Counters
CREATE INDEX IF NOT EXISTS idx_campus_msg_conv ON public.campus_messages(conversation_id, created_at DESC) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_campus_msg_unread ON public.campus_messages(recipient_id, status) WHERE is_deleted = false AND status != 'READ';
CREATE INDEX IF NOT EXISTS idx_campus_msg_expires ON public.campus_messages(expires_at) WHERE is_deleted = false AND is_saved = false;
CREATE INDEX IF NOT EXISTS idx_campus_msg_recipient_sync ON public.campus_messages(recipient_id, created_at DESC);

-- Enable RLS
ALTER TABLE public.campus_messages ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view sent or received messages"
    ON public.campus_messages
    FOR SELECT
    USING (
        sender_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid()) OR
        recipient_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid())
    );

CREATE POLICY "Users can insert own sent messages"
    ON public.campus_messages
    FOR INSERT
    WITH CHECK (
        sender_id IN (SELECT id FROM public.users WHERE auth_id = auth.uid() OR id = auth.uid())
    );

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
