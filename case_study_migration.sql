-- ================================================================
-- VidyaSetu AI — Case Study Feature Migration
-- Version: 1.0.0
-- Run this in Supabase SQL Editor (in order, top to bottom)
-- ================================================================


-- ================================================================
-- STEP 1: content_contributor_verifications
-- (यह पहले बनेगी — case_studies इसपर depend नहीं, लेकिन logic पहले)
-- ================================================================

CREATE TABLE IF NOT EXISTS public.content_contributor_verifications (
  id uuid NOT NULL DEFAULT gen_random_uuid(),

  contributor_type text NOT NULL
    CHECK (contributor_type IN ('user', 'organization', 'parent_organization')),

  user_id uuid,
  organization_id uuid,
  parent_organization_id uuid,

  status text NOT NULL DEFAULT 'pending'
    CHECK (status IN ('pending', 'approved', 'rejected', 'suspended')),

  applicant_note text,

  reviewed_by uuid,
  reviewed_at timestamp with time zone,
  rejection_reason text,
  suspension_reason text,

  applied_at timestamp with time zone NOT NULL DEFAULT now(),
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),

  CONSTRAINT content_contributor_verifications_pkey PRIMARY KEY (id),

  CONSTRAINT ccv_unique_user UNIQUE (user_id),
  CONSTRAINT ccv_unique_organization UNIQUE (organization_id),
  CONSTRAINT ccv_unique_parent_organization UNIQUE (parent_organization_id),

  CONSTRAINT ccv_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES public.users(id),
  CONSTRAINT ccv_organization_id_fkey
    FOREIGN KEY (organization_id) REFERENCES public.organizations(id),
  CONSTRAINT ccv_parent_organization_id_fkey
    FOREIGN KEY (parent_organization_id) REFERENCES public.organization_parents(id),
  CONSTRAINT ccv_reviewed_by_fkey
    FOREIGN KEY (reviewed_by) REFERENCES public.users(id),

  CONSTRAINT ccv_type_id_consistency CHECK (
    (contributor_type = 'user'
      AND user_id IS NOT NULL
      AND organization_id IS NULL
      AND parent_organization_id IS NULL)
    OR
    (contributor_type = 'organization'
      AND organization_id IS NOT NULL
      AND user_id IS NULL
      AND parent_organization_id IS NULL)
    OR
    (contributor_type = 'parent_organization'
      AND parent_organization_id IS NOT NULL
      AND user_id IS NULL
      AND organization_id IS NULL)
  )
);

CREATE INDEX IF NOT EXISTS idx_ccv_user_id
  ON public.content_contributor_verifications(user_id);

CREATE INDEX IF NOT EXISTS idx_ccv_organization_id
  ON public.content_contributor_verifications(organization_id);

CREATE INDEX IF NOT EXISTS idx_ccv_parent_organization_id
  ON public.content_contributor_verifications(parent_organization_id);

CREATE INDEX IF NOT EXISTS idx_ccv_status
  ON public.content_contributor_verifications(status);


-- ================================================================
-- STEP 2: case_studies
-- ================================================================

CREATE TABLE IF NOT EXISTS public.case_studies (
  id uuid NOT NULL DEFAULT gen_random_uuid(),

  -- Preview Data (Home Screen)
  title text NOT NULL,
  slug text NOT NULL,
  cover_image_url text NOT NULL,
  short_description text NOT NULL,
  language text NOT NULL DEFAULT 'hindi'
    CHECK (language IN ('hindi', 'english', 'bilingual')),
  tags text[] NOT NULL DEFAULT '{}',
  read_time_minutes integer,

  -- Full Content (Detail Screen only)
  schema_version integer NOT NULL DEFAULT 1,
  content_blocks jsonb NOT NULL DEFAULT '{"schema_version": 1, "blocks": []}',

  -- Media
  additional_image_urls text[] NOT NULL DEFAULT '{}',

  -- Author
  author_type text NOT NULL DEFAULT 'platform'
    CHECK (author_type IN ('platform', 'user', 'organization', 'parent_organization')),
  author_user_id uuid,
  author_organization_id uuid,
  author_parent_organization_id uuid,

  -- Publishing
  status text NOT NULL DEFAULT 'draft'
    CHECK (status IN ('draft', 'under_review', 'published', 'archived', 'rejected')),
  visibility text NOT NULL DEFAULT 'public'
    CHECK (visibility IN ('public', 'institution_only')),
  published_at timestamp with time zone,
  scheduled_publish_at timestamp with time zone,

  -- Moderation
  moderated_by uuid,
  moderated_at timestamp with time zone,
  moderation_note text,

  -- Metrics
  view_count integer NOT NULL DEFAULT 0,

  -- Audit
  is_deleted boolean NOT NULL DEFAULT false,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),

  CONSTRAINT case_studies_pkey PRIMARY KEY (id),
  CONSTRAINT case_studies_slug_unique UNIQUE (slug),

  CONSTRAINT cs_author_user_id_fkey
    FOREIGN KEY (author_user_id) REFERENCES public.users(id),
  CONSTRAINT cs_author_organization_id_fkey
    FOREIGN KEY (author_organization_id) REFERENCES public.organizations(id),
  CONSTRAINT cs_author_parent_organization_id_fkey
    FOREIGN KEY (author_parent_organization_id) REFERENCES public.organization_parents(id),
  CONSTRAINT cs_moderated_by_fkey
    FOREIGN KEY (moderated_by) REFERENCES public.users(id),

  CONSTRAINT cs_author_type_id_consistency CHECK (
    (author_type = 'platform'
      AND author_user_id IS NULL
      AND author_organization_id IS NULL
      AND author_parent_organization_id IS NULL)
    OR
    (author_type = 'user'
      AND author_user_id IS NOT NULL
      AND author_organization_id IS NULL
      AND author_parent_organization_id IS NULL)
    OR
    (author_type = 'organization'
      AND author_organization_id IS NOT NULL
      AND author_user_id IS NULL
      AND author_parent_organization_id IS NULL)
    OR
    (author_type = 'parent_organization'
      AND author_parent_organization_id IS NOT NULL
      AND author_user_id IS NULL
      AND author_organization_id IS NULL)
  ),

  CONSTRAINT cs_max_additional_images CHECK (
    array_length(additional_image_urls, 1) IS NULL
    OR array_length(additional_image_urls, 1) <= 4
  )
);

CREATE INDEX IF NOT EXISTS idx_cs_status_published
  ON public.case_studies(status, published_at DESC)
  WHERE status = 'published' AND is_deleted = false;

CREATE INDEX IF NOT EXISTS idx_cs_author_user
  ON public.case_studies(author_user_id);

CREATE INDEX IF NOT EXISTS idx_cs_author_org
  ON public.case_studies(author_organization_id);

CREATE INDEX IF NOT EXISTS idx_cs_author_parent_org
  ON public.case_studies(author_parent_organization_id);

CREATE INDEX IF NOT EXISTS idx_cs_tags
  ON public.case_studies USING gin(tags);

CREATE INDEX IF NOT EXISTS idx_cs_language
  ON public.case_studies(language);

CREATE INDEX IF NOT EXISTS idx_cs_slug
  ON public.case_studies(slug);

CREATE INDEX IF NOT EXISTS idx_cs_is_deleted
  ON public.case_studies(is_deleted);


-- ================================================================
-- STEP 3: case_study_reactions
-- ================================================================

CREATE TABLE IF NOT EXISTS public.case_study_reactions (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  case_study_id uuid NOT NULL,
  user_id uuid NOT NULL,
  reaction_type text NOT NULL DEFAULT 'helpful'
    CHECK (reaction_type IN ('helpful', 'insightful', 'inspiring')),
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),

  CONSTRAINT case_study_reactions_pkey PRIMARY KEY (id),
  CONSTRAINT csr_unique_user_case_study UNIQUE (case_study_id, user_id),

  CONSTRAINT csr_case_study_id_fkey
    FOREIGN KEY (case_study_id) REFERENCES public.case_studies(id) ON DELETE CASCADE,
  CONSTRAINT csr_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_csr_case_study_id
  ON public.case_study_reactions(case_study_id);

CREATE INDEX IF NOT EXISTS idx_csr_user_id
  ON public.case_study_reactions(user_id);


-- ================================================================
-- STEP 4: case_study_bookmarks
-- ================================================================

CREATE TABLE IF NOT EXISTS public.case_study_bookmarks (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  case_study_id uuid NOT NULL,
  user_id uuid NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),

  CONSTRAINT case_study_bookmarks_pkey PRIMARY KEY (id),
  CONSTRAINT csb_unique_user_case_study UNIQUE (case_study_id, user_id),

  CONSTRAINT csb_case_study_id_fkey
    FOREIGN KEY (case_study_id) REFERENCES public.case_studies(id) ON DELETE CASCADE,
  CONSTRAINT csb_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_csb_case_study_id
  ON public.case_study_bookmarks(case_study_id);

CREATE INDEX IF NOT EXISTS idx_csb_user_id
  ON public.case_study_bookmarks(user_id);


-- ================================================================
-- STEP 5: updated_at Auto-Update Trigger
-- (हर table पर — updated_at automatically set होगा)
-- ================================================================

-- Trigger Function (एक बार बनाएं, सभी tables पर use होगी)
CREATE OR REPLACE FUNCTION public.handle_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- content_contributor_verifications
CREATE TRIGGER trg_ccv_updated_at
  BEFORE UPDATE ON public.content_contributor_verifications
  FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- case_studies
CREATE TRIGGER trg_cs_updated_at
  BEFORE UPDATE ON public.case_studies
  FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- case_study_reactions
CREATE TRIGGER trg_csr_updated_at
  BEFORE UPDATE ON public.case_study_reactions
  FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();


-- ================================================================
-- STEP 6: Row Level Security (RLS) Enable करें
-- ================================================================

ALTER TABLE public.content_contributor_verifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.case_studies ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.case_study_reactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.case_study_bookmarks ENABLE ROW LEVEL SECURITY;


-- ================================================================
-- STEP 7: RLS Policies
-- ================================================================

-- ── content_contributor_verifications ──────────────────────────

-- User अपना record देख सकता है
CREATE POLICY "ccv_select_own" ON public.content_contributor_verifications
  FOR SELECT
  USING (
    user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid())
  );

-- User अपने लिए apply कर सकता है
CREATE POLICY "ccv_insert_own" ON public.content_contributor_verifications
  FOR INSERT
  WITH CHECK (
    user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid())
    AND contributor_type = 'user'
  );

-- UPDATE: केवल Supabase Service Role (आप Dashboard से करेंगे)
-- (कोई policy नहीं = anon/authenticated update नहीं कर सकते)


-- ── case_studies ───────────────────────────────────────────────

-- सभी published case studies सभी authenticated users देख सकते हैं
CREATE POLICY "cs_select_published" ON public.case_studies
  FOR SELECT
  USING (
    status = 'published'
    AND is_deleted = false
  );

-- Author अपनी draft/under_review case study देख सकता है
CREATE POLICY "cs_select_own_drafts" ON public.case_studies
  FOR SELECT
  USING (
    author_user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid())
  );

-- Verified user अपनी case study submit कर सकता है
CREATE POLICY "cs_insert_verified_user" ON public.case_studies
  FOR INSERT
  WITH CHECK (
    author_type = 'user'
    AND author_user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid())
    AND EXISTS (
      SELECT 1 FROM public.content_contributor_verifications
      WHERE user_id = author_user_id
      AND status = 'approved'
    )
  );

-- Author अपनी case study edit कर सकता है (केवल draft/rejected state में)
CREATE POLICY "cs_update_own" ON public.case_studies
  FOR UPDATE
  USING (
    author_user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid())
    AND status IN ('draft', 'rejected')
  );


-- ── case_study_reactions ────────────────────────────────────────

-- Authenticated users reactions देख सकते हैं
CREATE POLICY "csr_select_all" ON public.case_study_reactions
  FOR SELECT
  USING (auth.role() = 'authenticated');

-- Authenticated user reaction दे सकता है
CREATE POLICY "csr_insert_own" ON public.case_study_reactions
  FOR INSERT
  WITH CHECK (
    user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid())
  );

-- User अपनी reaction बदल सकता है
CREATE POLICY "csr_update_own" ON public.case_study_reactions
  FOR UPDATE
  USING (
    user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid())
  );

-- User अपनी reaction हटा सकता है
CREATE POLICY "csr_delete_own" ON public.case_study_reactions
  FOR DELETE
  USING (
    user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid())
  );


-- ── case_study_bookmarks ────────────────────────────────────────

-- User केवल अपने bookmarks देख सकता है
CREATE POLICY "csb_select_own" ON public.case_study_bookmarks
  FOR SELECT
  USING (
    user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid())
  );

-- User bookmark कर सकता है
CREATE POLICY "csb_insert_own" ON public.case_study_bookmarks
  FOR INSERT
  WITH CHECK (
    user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid())
  );

-- User bookmark हटा सकता है
CREATE POLICY "csb_delete_own" ON public.case_study_bookmarks
  FOR DELETE
  USING (
    user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid())
  );


-- ================================================================
-- STEP 8: Helpful Views (Optional but useful)
-- ================================================================

-- Published case studies का preview (content_blocks exclude)
-- Android app Home Screen इसे use करेगी
CREATE OR REPLACE VIEW public.case_studies_preview AS
  SELECT
    id,
    title,
    slug,
    cover_image_url,
    short_description,
    language,
    tags,
    read_time_minutes,
    author_type,
    author_user_id,
    author_organization_id,
    author_parent_organization_id,
    view_count,
    published_at,
    created_at,
    updated_at
  FROM public.case_studies
  WHERE status = 'published'
    AND is_deleted = false
  ORDER BY published_at DESC;


-- ================================================================
-- MIGRATION COMPLETE
-- Tables created:
--   ✓ content_contributor_verifications
--   ✓ case_studies
--   ✓ case_study_reactions
--   ✓ case_study_bookmarks
-- Views created:
--   ✓ case_studies_preview
-- Triggers created:
--   ✓ updated_at auto-triggers on all tables
-- RLS Policies:
--   ✓ All tables secured
-- ================================================================
