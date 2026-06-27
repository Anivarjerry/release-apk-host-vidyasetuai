# VidyaSetu AI — Case Study Feature: Complete Database Schema
# Version: V2 (Scalable from Day 1)
# Last Updated: 2026-06-11
# Author: Architecture Design Session

# ─────────────────────────────────────────────────────────────────
# IMPORTANT NOTES
# ─────────────────────────────────────────────────────────────────
# 1. user_profiles.is_verified को TOUCH नहीं करना — वह अलग system है
# 2. Verification के लिए अलग table: content_contributor_verifications
# 3. तीन तरह के Contributor: User, Organization, ParentOrganization
# 4. Naming Convention: Existing pattern follow किया गया है
#    - Global/Shared tables: global_* prefix नहीं, content_* prefix
#    - Feature tables: case_study_* prefix
# 5. यह schema existing tables को छुए बिना नई tables add करता है
# ─────────────────────────────────────────────────────────────────


-- WARNING: This schema is for context only and is not meant to be run directly.
-- Table order and constraints may not be valid for execution.
-- Always create tables in dependency order in Supabase Dashboard.


-- ═══════════════════════════════════════════════════════════════
-- TABLE 1: content_contributor_verifications
-- ═══════════════════════════════════════════════════════════════
-- Purpose:
--   यह table decide करती है कि कौन Case Study upload कर सकता है।
--   तीन entities apply कर सकती हैं: User, Organization, ParentOrganization
--   App इसी table से पढ़कर verification status check करेगी।
--   user_profiles.is_verified को बिल्कुल touch नहीं किया जाएगा।
--
-- Contributor Types:
--   'user'                → public.users का कोई individual user
--   'organization'        → public.organizations का कोई institution
--   'parent_organization' → public.organization_parents का कोई group

CREATE TABLE public.content_contributor_verifications (
  id uuid NOT NULL DEFAULT gen_random_uuid(),

  -- Contributor Identity (केवल एक null होगा, दो null होंगे)
  contributor_type text NOT NULL
    CHECK (contributor_type IN ('user', 'organization', 'parent_organization')),
  user_id uuid,                    -- contributor_type = 'user' होने पर
  organization_id uuid,            -- contributor_type = 'organization' होने पर
  parent_organization_id uuid,     -- contributor_type = 'parent_organization' होने पर

  -- Verification Status
  status text NOT NULL DEFAULT 'pending'
    CHECK (status IN ('pending', 'approved', 'rejected', 'suspended')),

  -- Apply करते समय User द्वारा भरा गया (optional motivation)
  applicant_note text,

  -- Review Information (V1: manually by team via Supabase Dashboard)
  -- (V2: Super Admin App से होगा — same columns, same table)
  reviewed_by uuid,                -- NULL in V1 (team manually approves)
  reviewed_at timestamp with time zone,
  rejection_reason text,           -- rejected होने पर कारण
  suspension_reason text,          -- suspended होने पर कारण

  -- Timestamps
  applied_at timestamp with time zone NOT NULL DEFAULT now(),
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),

  -- Constraints
  CONSTRAINT content_contributor_verifications_pkey PRIMARY KEY (id),

  -- एक entity एक ही active verification request रख सकती है
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

  -- Logic Constraint: contributor_type और corresponding ID match होना चाहिए
  CONSTRAINT ccv_type_id_consistency CHECK (
    (contributor_type = 'user' AND user_id IS NOT NULL
      AND organization_id IS NULL AND parent_organization_id IS NULL)
    OR
    (contributor_type = 'organization' AND organization_id IS NOT NULL
      AND user_id IS NULL AND parent_organization_id IS NULL)
    OR
    (contributor_type = 'parent_organization' AND parent_organization_id IS NOT NULL
      AND user_id IS NULL AND organization_id IS NULL)
  )
);

-- Indexes
CREATE INDEX idx_ccv_user_id ON public.content_contributor_verifications(user_id);
CREATE INDEX idx_ccv_organization_id ON public.content_contributor_verifications(organization_id);
CREATE INDEX idx_ccv_parent_organization_id ON public.content_contributor_verifications(parent_organization_id);
CREATE INDEX idx_ccv_status ON public.content_contributor_verifications(status);


-- ═══════════════════════════════════════════════════════════════
-- TABLE 2: case_studies
-- ═══════════════════════════════════════════════════════════════
-- Purpose:
--   मुख्य Case Study table। Home Screen पर Preview columns load होंगे।
--   Full content_blocks केवल Detail Screen पर fetch होगा।
--
-- Author Types (कौन upload कर सकता है):
--   'platform'            → VidyaSetu Team (आप, manually via Supabase)
--   'user'                → Verified Individual User
--   'organization'        → Verified Organization
--   'parent_organization' → Verified Parent Organization
--
-- Image Rules:
--   - cover_image_url: 1 main cover image (1:1 ratio), अलग field में
--   - additional_image_urls: max 4 extra images (total 5 with cover), 1:1 ratio
--   - Videos: केवल external links allowed, direct upload नहीं
--
-- Content Rules:
--   - schema_version: JSON parser version, शुरू में 1
--   - content_blocks: Block-based JSON, Detail Screen पर load होगा
--   - Preview fetch में content_blocks को SELECT से exclude करें

CREATE TABLE public.case_studies (
  id uuid NOT NULL DEFAULT gen_random_uuid(),

  -- ── PREVIEW DATA (Home Screen पर load होगा) ──────────────────
  title text NOT NULL,
  slug text NOT NULL UNIQUE,          -- URL-friendly, "ai-in-education" जैसा
  cover_image_url text NOT NULL,      -- 1:1 Cover Image (Supabase Storage URL)
  short_description text NOT NULL,    -- Max 300 chars, Preview card के लिए
  language text NOT NULL DEFAULT 'hindi'
    CHECK (language IN ('hindi', 'english', 'bilingual')),
  tags text[] NOT NULL DEFAULT '{}',  -- ["AI", "Education", "Technology"]
  read_time_minutes integer,          -- Estimated read time (UX के लिए)

  -- ── FULL CONTENT (Detail Screen पर load होगा) ────────────────
  schema_version integer NOT NULL DEFAULT 1,
  content_blocks jsonb NOT NULL DEFAULT '{"schema_version": 1, "blocks": []}',

  -- ── MEDIA ─────────────────────────────────────────────────────
  -- Cover Image ऊपर cover_image_url में है
  -- यहाँ अतिरिक्त images (max 4, सभी 1:1 ratio)
  additional_image_urls text[] NOT NULL DEFAULT '{}',
  -- Video links content_blocks के अंदर video_link type blocks में रहेंगे

  -- ── AUTHOR INFORMATION ────────────────────────────────────────
  author_type text NOT NULL DEFAULT 'platform'
    CHECK (author_type IN ('platform', 'user', 'organization', 'parent_organization')),
  author_user_id uuid,                -- author_type = 'user' होने पर
  author_organization_id uuid,        -- author_type = 'organization' होने पर
  author_parent_organization_id uuid, -- author_type = 'parent_organization' होने पर

  -- ── PUBLISHING CONTROL ────────────────────────────────────────
  status text NOT NULL DEFAULT 'draft'
    CHECK (status IN ('draft', 'under_review', 'published', 'archived', 'rejected')),
  visibility text NOT NULL DEFAULT 'public'
    CHECK (visibility IN ('public', 'institution_only')),
  published_at timestamp with time zone,   -- कब publish हुई
  scheduled_publish_at timestamp with time zone, -- Scheduled publishing (future use)

  -- ── MODERATION (V1: team manually, V2: Super Admin App) ───────
  -- User/Org द्वारा submit की गई case study review के बाद publish होगी
  moderated_by uuid,                  -- NULL = platform post (no review needed)
  moderated_at timestamp with time zone,
  moderation_note text,               -- Reject/suspend होने पर reason

  -- ── ENGAGEMENT METRICS (Simple counters, not real-time) ───────
  -- Real counts case_study_reactions और case_study_bookmarks से आएंगे
  -- ये counters approximate/cached हैं, performance के लिए
  view_count integer NOT NULL DEFAULT 0,

  -- ── AUDIT ─────────────────────────────────────────────────────
  is_deleted boolean NOT NULL DEFAULT false,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),

  -- ── CONSTRAINTS ───────────────────────────────────────────────
  CONSTRAINT case_studies_pkey PRIMARY KEY (id),

  CONSTRAINT cs_author_user_id_fkey
    FOREIGN KEY (author_user_id) REFERENCES public.users(id),
  CONSTRAINT cs_author_organization_id_fkey
    FOREIGN KEY (author_organization_id) REFERENCES public.organizations(id),
  CONSTRAINT cs_author_parent_organization_id_fkey
    FOREIGN KEY (author_parent_organization_id) REFERENCES public.organization_parents(id),
  CONSTRAINT cs_moderated_by_fkey
    FOREIGN KEY (moderated_by) REFERENCES public.users(id),

  -- author_type और corresponding ID consistency
  CONSTRAINT cs_author_type_id_consistency CHECK (
    (author_type = 'platform'
      AND author_user_id IS NULL
      AND author_organization_id IS NULL
      AND author_parent_organization_id IS NULL)
    OR
    (author_type = 'user' AND author_user_id IS NOT NULL
      AND author_organization_id IS NULL
      AND author_parent_organization_id IS NULL)
    OR
    (author_type = 'organization' AND author_organization_id IS NOT NULL
      AND author_user_id IS NULL
      AND author_parent_organization_id IS NULL)
    OR
    (author_type = 'parent_organization' AND author_parent_organization_id IS NOT NULL
      AND author_user_id IS NULL
      AND author_organization_id IS NULL)
  ),

  -- Image count constraint (additional_image_urls max 4)
  CONSTRAINT cs_max_additional_images CHECK (
    array_length(additional_image_urls, 1) IS NULL
    OR array_length(additional_image_urls, 1) <= 4
  )
);

-- Indexes (Preview fetch fast होगा)
CREATE INDEX idx_cs_status_published ON public.case_studies(status, published_at DESC)
  WHERE status = 'published' AND is_deleted = false;
CREATE INDEX idx_cs_author_user ON public.case_studies(author_user_id);
CREATE INDEX idx_cs_author_org ON public.case_studies(author_organization_id);
CREATE INDEX idx_cs_author_parent_org ON public.case_studies(author_parent_organization_id);
CREATE INDEX idx_cs_tags ON public.case_studies USING gin(tags);
CREATE INDEX idx_cs_language ON public.case_studies(language);
CREATE INDEX idx_cs_slug ON public.case_studies(slug);


-- ═══════════════════════════════════════════════════════════════
-- TABLE 3: case_study_reactions
-- ═══════════════════════════════════════════════════════════════
-- Purpose:
--   User किसी Case Study पर एक Reaction दे सकता है।
--   एक User + एक Case Study = एक Reaction (Unique Constraint)
--   Reaction बदली और हटाई जा सकती है।
--
-- Reaction Types (अभी केवल एक, भविष्य में और जुड़ सकते हैं):
--   'helpful'   → "यह उपयोगी लगी" (Default, V1 में यही होगा)
--   Future: 'insightful', 'inspiring' — Table structure नहीं बदलेगी

CREATE TABLE public.case_study_reactions (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  case_study_id uuid NOT NULL,
  user_id uuid NOT NULL,
  reaction_type text NOT NULL DEFAULT 'helpful'
    CHECK (reaction_type IN ('helpful', 'insightful', 'inspiring')),
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),

  CONSTRAINT case_study_reactions_pkey PRIMARY KEY (id),

  -- एक User एक Case Study पर केवल एक Reaction
  CONSTRAINT csr_unique_user_case_study UNIQUE (case_study_id, user_id),

  CONSTRAINT csr_case_study_id_fkey
    FOREIGN KEY (case_study_id) REFERENCES public.case_studies(id) ON DELETE CASCADE,
  CONSTRAINT csr_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE INDEX idx_csr_case_study_id ON public.case_study_reactions(case_study_id);
CREATE INDEX idx_csr_user_id ON public.case_study_reactions(user_id);


-- ═══════════════════════════════════════════════════════════════
-- TABLE 4: case_study_bookmarks
-- ═══════════════════════════════════════════════════════════════
-- Purpose:
--   User किसी Case Study को Save/Bookmark कर सकता है।
--   भविष्य में Vidya Journey Feature से connect हो सकता है।
--   एक User एक Case Study को एक बार ही bookmark कर सकता है।

CREATE TABLE public.case_study_bookmarks (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  case_study_id uuid NOT NULL,
  user_id uuid NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),

  CONSTRAINT case_study_bookmarks_pkey PRIMARY KEY (id),

  -- एक User एक Case Study को एक बार bookmark करे
  CONSTRAINT csb_unique_user_case_study UNIQUE (case_study_id, user_id),

  CONSTRAINT csb_case_study_id_fkey
    FOREIGN KEY (case_study_id) REFERENCES public.case_studies(id) ON DELETE CASCADE,
  CONSTRAINT csb_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE INDEX idx_csb_case_study_id ON public.case_study_bookmarks(case_study_id);
CREATE INDEX idx_csb_user_id ON public.case_study_bookmarks(user_id);


-- ═══════════════════════════════════════════════════════════════
-- CONTENT BLOCKS JSON STRUCTURE (Reference)
-- ═══════════════════════════════════════════════════════════════
-- यह case_studies.content_blocks column का expected JSON format है।
-- App इसी format को parse करके UI render करेगी।
-- schema_version से App को पता चलेगा कि कौन सा parser use करना है।
--
-- Block Types:
--   title          → Main heading (एक ही होगा, top पर)
--   section        → Section heading (h2 equivalent)
--   sub_section    → Sub-section heading (h3 equivalent)
--   paragraph      → Normal text paragraph (Hindi/English/Bilingual)
--   image          → Single image with optional caption (Supabase Storage URL)
--   video_link     → External video link (YouTube/Vimeo etc.)
--   quote          → Highlighted quote with optional author
--   checklist      → List of points with checkmarks
--   resource_link  → External resource URL with label
--   table          → Simple data table (headers + rows)
--   tip            → Highlighted tip box (green)
--   warning        → Highlighted warning box (orange/red)
--
-- Example Structure:
-- {
--   "schema_version": 1,
--   "blocks": [
--     {
--       "type": "section",
--       "text": "परिचय (Introduction)"
--     },
--     {
--       "type": "paragraph",
--       "text": "यह Case Study AI के शिक्षा में उपयोग के बारे में है।"
--     },
--     {
--       "type": "image",
--       "url": "https://your-supabase.storage.url/case-study-images/abc.jpg",
--       "caption": "AI-powered classroom (optional)"
--     },
--     {
--       "type": "video_link",
--       "url": "https://youtube.com/watch?v=xyz",
--       "title": "AI in Education - Overview",
--       "thumbnail_url": "https://img.youtube.com/vi/xyz/hqdefault.jpg"
--     },
--     {
--       "type": "resource_link",
--       "url": "https://example.com/report.pdf",
--       "label": "NCERT AI Report 2024"
--     },
--     {
--       "type": "tip",
--       "text": "यह जानकारी विशेष रूप से Class 10-12 के छात्रों के लिए उपयोगी है।"
--     },
--     {
--       "type": "quote",
--       "text": "Education is the most powerful weapon which you can use to change the world.",
--       "author": "Nelson Mandela"
--     },
--     {
--       "type": "checklist",
--       "items": [
--         "AI tools का उपयोग सीखें",
--         "Online courses explore करें",
--         "Projects में AI apply करें"
--       ]
--     }
--   ]
-- }


-- ═══════════════════════════════════════════════════════════════
-- APP LOGIC REFERENCE: Verification Check Flow
-- ═══════════════════════════════════════════════════════════════
--
-- जब कोई User Case Study upload करना चाहे:
--
-- Step 1: App check करे content_contributor_verifications table में
--         WHERE user_id = current_user_id
--
-- Step 2: Row नहीं मिली (कभी apply नहीं किया)
--         → Screen: "Verified Contributor बनें" button दिखाएं
--         → Rules show करें:
--           ✓ पहले Profile Complete करें
--           ✓ Apply करें
--           ✓ Review में 1 महीना लग सकता है
--           ✓ Contact: support@vidyasetuai.com
--
-- Step 3: Row मिली, status = 'pending'
--         → "आपकी verification request review में है" message दिखाएं
--         → Contact option दिखाएं
--
-- Step 4: Row मिली, status = 'approved'
--         → Upload Case Study button दिखाएं
--         → User upload कर सकता है
--
-- Step 5: Row मिली, status = 'rejected'
--         → Rejection reason दिखाएं
--         → "फिर से Apply करें" option (नई row नहीं, existing update होगी)
--
-- Step 6: Row मिली, status = 'suspended'
--         → "आपका contributor access suspend कर दिया गया है" message
--         → Suspension reason दिखाएं
--         → Contact option दिखाएं
--
-- Organization के लिए same flow, organization_id से check करें।
-- Parent Organization के लिए same flow, parent_organization_id से check।


-- ═══════════════════════════════════════════════════════════════
-- APP LOGIC REFERENCE: Case Study Upload Validation
-- ═══════════════════════════════════════════════════════════════
--
-- User Upload के लिए Validation (App में):
-- 1. contributor verified है? (content_contributor_verifications.status = 'approved')
-- 2. Title: Required, max 200 chars
-- 3. Cover Image: Required, 1:1 ratio, max 5MB
-- 4. Short Description: Required, max 300 chars
-- 5. Additional Images: max 4 (total 5 with cover), सभी 1:1 ratio
-- 6. Content Blocks: कम से कम 1 paragraph block required
-- 7. Videos: केवल valid URLs allowed (YouTube/Vimeo links)
-- 8. Submit होने पर status = 'under_review' set होगा
-- 9. Team review के बाद status = 'published' या 'rejected'


-- ═══════════════════════════════════════════════════════════════
-- SUPABASE STORAGE BUCKET REFERENCE
-- ═══════════════════════════════════════════════════════════════
--
-- Bucket Name: case-study-images
-- Folder Structure:
--   case-study-images/
--   ├── covers/           → Cover images (1:1)
--   │   └── {case_study_id}/cover.jpg
--   └── content/          → Additional content images (1:1)
--       └── {case_study_id}/{image_index}.jpg
--
-- Access:
--   - Public read (published case studies)
--   - Authenticated write (verified contributors only)
--   - RLS Policy based on content_contributor_verifications.status


-- ═══════════════════════════════════════════════════════════════
-- RLS POLICIES REFERENCE (Supabase Row Level Security)
-- ═══════════════════════════════════════════════════════════════
--
-- case_studies:
--   READ:   status = 'published' AND is_deleted = false → सभी देख सकते हैं
--   INSERT: author_type = 'platform' → Service Role Key से (आपकी team)
--           author_type = 'user' → verified user (ccv.status = 'approved')
--           author_type = 'organization' → verified org
--           author_type = 'parent_organization' → verified parent org
--   UPDATE: author_user_id = auth.uid() (अपनी खुद की case study edit)
--           OR platform team (Service Role)
--   DELETE: Soft delete (is_deleted = true), केवल platform team
--
-- case_study_reactions:
--   READ:   सभी authenticated users
--   INSERT: Authenticated users, एक बार
--   UPDATE: अपनी reaction बदल सकते हैं (reaction_type)
--   DELETE: अपनी reaction हटा सकते हैं
--
-- case_study_bookmarks:
--   READ:   केवल अपने bookmarks (user_id = auth.uid())
--   INSERT: Authenticated users
--   DELETE: अपना bookmark हटा सकते हैं
--
-- content_contributor_verifications:
--   READ:   अपना खुद का record (user_id = auth.uid())
--           Platform team: सभी records (Service Role)
--   INSERT: Authenticated users (अपने लिए)
--   UPDATE: केवल Platform team (Supabase Dashboard / V2: Super Admin)


-- ═══════════════════════════════════════════════════════════════
-- MIGRATION SAFETY NOTES
-- ═══════════════════════════════════════════════════════════════
--
-- 1. user_profiles table: कोई बदलाव नहीं — is_verified column untouched
-- 2. users table: कोई बदलाव नहीं
-- 3. organizations table: कोई बदलाव नहीं
-- 4. organization_parents table: कोई बदलाव नहीं
-- 5. सभी नई tables independent हैं — existing data affect नहीं होगा
-- 6. App Force Update से नई tables activate होंगी
-- 7. पुराना App Version नई tables को simply ignore करेगा (no crash)


-- ═══════════════════════════════════════════════════════════════
-- V1 → V2 MIGRATION PATH
-- ═══════════════════════════════════════════════════════════════
--
-- V1 (अभी):
--   - Team manually Supabase Dashboard से case studies insert करे
--   - author_type = 'platform' set करे
--   - author_user_id, author_organization_id, author_parent_organization_id = NULL
--
-- V2 (User Upload):
--   - content_contributor_verifications में approved record होने पर
--   - App के अंदर Upload Screen से submit करे
--   - author_type = 'user' / 'organization' / 'parent_organization'
--   - status = 'under_review' पर submit होगी
--   - Team (V1: Dashboard, V2: Super Admin App) approve करे
--   - Database tables में कोई migration नहीं — same tables
--
-- V2 के लिए केवल Android App में नई screens चाहिए:
--   - CaseStudyUploadScreen
--   - ContributorVerificationScreen
--   - MyContributionsScreen
