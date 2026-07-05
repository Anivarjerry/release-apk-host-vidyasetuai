-- ============================================================================
-- PROJECT: VidyaSetu AI
-- SUPABASE POSTGRESQL PRODUCTION-READY SCHEMA
-- ============================================================================

-- ==========================================
-- 1. EXTENSIONS
-- ==========================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_cron" WITH SCHEMA pg_catalog;

-- ==========================================
-- 2. STORAGE BUCKET CREATION
-- ==========================================
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
    'media', 
    'media', 
    true, 
    10485760, -- 10 MB in bytes
    ARRAY['image/jpeg', 'image/png', 'image/webp']
)
ON CONFLICT (id) DO NOTHING;

-- ==========================================
-- 3. STORAGE POLICIES
-- ==========================================
CREATE POLICY "Public Read" ON storage.objects
    FOR SELECT 
    USING (bucket_id = 'media');

CREATE POLICY "Authenticated Upload" ON storage.objects
    FOR INSERT 
    WITH CHECK (
        bucket_id = 'media' 
        AND auth.role() = 'authenticated'
        AND (
            name LIKE 'quicks/%' OR 
            name LIKE 'experiences/%' OR 
            name LIKE 'case-studies/%' OR 
            name LIKE 'profiles/%'
        )
    );

CREATE POLICY "Owner Update" ON storage.objects
    FOR UPDATE 
    USING (bucket_id = 'media' AND auth.uid() = owner)
    WITH CHECK (bucket_id = 'media' AND auth.uid() = owner);

CREATE POLICY "Owner Delete" ON storage.objects
    FOR DELETE 
    USING (bucket_id = 'media' AND auth.uid() = owner);

-- ==========================================
-- 4. UTILITY & AUDIT FUNCTIONS / TRIGGERS
-- ==========================================
CREATE FUNCTION public.set_audit_fields()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION public.get_current_user_id()
RETURNS UUID AS $$
BEGIN
    RETURN (SELECT id FROM public.users WHERE auth_id = auth.uid());
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ==========================================
-- 5. MODULE 1: QUICKS
-- ==========================================

-- Table: quicks
CREATE TABLE public.quicks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    cover_image_url TEXT,
    views_count INTEGER DEFAULT 0 NOT NULL,
    helpful_count INTEGER DEFAULT 0 NOT NULL,
    status TEXT DEFAULT 'draft' NOT NULL CONSTRAINT quick_status_check CHECK (status IN ('published', 'draft', 'archived')),
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    expires_at TIMESTAMPTZ DEFAULT (now() + interval '24 hours') NOT NULL
);

-- Table: quick_views
CREATE TABLE public.quick_views (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quick_id UUID NOT NULL REFERENCES public.quicks(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    CONSTRAINT quick_views_unique UNIQUE (quick_id, user_id)
);

-- Table: quick_helpfuls
CREATE TABLE public.quick_helpfuls (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quick_id UUID NOT NULL REFERENCES public.quicks(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    CONSTRAINT quick_helpfuls_unique UNIQUE (quick_id, user_id)
);

-- Counters: Quick Views increment trigger
CREATE FUNCTION public.increment_quick_views()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE public.quicks
    SET views_count = views_count + 1
    WHERE id = NEW.quick_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_increment_quick_views
    AFTER INSERT ON public.quick_views
    FOR EACH ROW
    EXECUTE FUNCTION public.increment_quick_views();

-- Counters: Quick Helpfuls update trigger
CREATE FUNCTION public.update_quick_helpful_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE public.quicks
        SET helpful_count = helpful_count + 1
        WHERE id = NEW.quick_id;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE public.quicks
        SET helpful_count = greatest(0, helpful_count - 1)
        WHERE id = OLD.quick_id;
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_update_quick_helpful_count
    AFTER INSERT OR DELETE ON public.quick_helpfuls
    FOR EACH ROW
    EXECUTE FUNCTION public.update_quick_helpful_count();

-- Auto-delete logic: delete expired quicks function
CREATE FUNCTION public.delete_expired_quicks()
RETURNS void AS $$
BEGIN
    DELETE FROM public.quicks
    WHERE expires_at <= now();
END;
$$ LANGUAGE plpgsql;

-- Schedule cron job: delete expired quicks every 10 minutes
SELECT cron.schedule(
    'delete-expired-quicks-job',
    '*/10 * * * *',
    'SELECT public.delete_expired_quicks();'
);

-- ==========================================
-- 6. MODULE 2: EXPERIENCES
-- ==========================================

-- Table: experiences
CREATE TABLE public.experiences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    cover_image_url TEXT,
    description TEXT NOT NULL,
    views_count INTEGER DEFAULT 0 NOT NULL,
    inspired_count INTEGER DEFAULT 0 NOT NULL,
    status TEXT DEFAULT 'draft' NOT NULL CONSTRAINT experience_status_check CHECK (status IN ('published', 'draft', 'archived')),
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT now() NOT NULL
);

-- Table: experience_views
CREATE TABLE public.experience_views (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    experience_id UUID NOT NULL REFERENCES public.experiences(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    CONSTRAINT experience_views_unique UNIQUE (experience_id, user_id)
);

-- Table: experience_inspirations
CREATE TABLE public.experience_inspirations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    experience_id UUID NOT NULL REFERENCES public.experiences(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    CONSTRAINT experience_inspirations_unique UNIQUE (experience_id, user_id)
);

-- Counters: Experience Views increment trigger
CREATE FUNCTION public.increment_experience_views()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE public.experiences
    SET views_count = views_count + 1
    WHERE id = NEW.experience_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_increment_experience_views
    AFTER INSERT ON public.experience_views
    FOR EACH ROW
    EXECUTE FUNCTION public.increment_experience_views();

-- Counters: Experience Inspirations update trigger
CREATE FUNCTION public.update_experience_inspired_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE public.experiences
        SET inspired_count = inspired_count + 1
        WHERE id = NEW.experience_id;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE public.experiences
        SET inspired_count = greatest(0, inspired_count - 1)
        WHERE id = OLD.experience_id;
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_update_experience_inspired_count
    AFTER INSERT OR DELETE ON public.experience_inspirations
    FOR EACH ROW
    EXECUTE FUNCTION public.update_experience_inspired_count();

-- Audit trigger for experiences
CREATE TRIGGER tr_set_audit_fields_experiences
    BEFORE UPDATE ON public.experiences
    FOR EACH ROW
    EXECUTE FUNCTION public.set_audit_fields();

-- ==========================================
-- 7. MODULE 3: CASE STUDIES
-- ==========================================

-- Table: case_studies
CREATE TABLE public.case_studies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    short_description TEXT NOT NULL,
    cover_image_url TEXT,
    content JSONB NOT NULL CONSTRAINT case_study_content_check CHECK (
        jsonb_typeof(content) = 'object'
        AND content ? 'version'
        AND jsonb_typeof(content -> 'version') = 'number'
        AND content ? 'sections'
        AND jsonb_typeof(content -> 'sections') = 'array'
    ),
    views_count INTEGER DEFAULT 0 NOT NULL,
    inspired_count INTEGER DEFAULT 0 NOT NULL,
    status TEXT DEFAULT 'draft' NOT NULL CONSTRAINT case_study_status_check CHECK (status IN ('published', 'draft', 'archived')),
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT now() NOT NULL
);

-- Table: case_study_views
CREATE TABLE public.case_study_views (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_study_id UUID NOT NULL REFERENCES public.case_studies(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    CONSTRAINT case_study_views_unique UNIQUE (case_study_id, user_id)
);

-- Table: case_study_inspirations
CREATE TABLE public.case_study_inspirations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_study_id UUID NOT NULL REFERENCES public.case_studies(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    CONSTRAINT case_study_inspirations_unique UNIQUE (case_study_id, user_id)
);

-- Counters: Case Study Views increment trigger
CREATE FUNCTION public.increment_case_study_views()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE public.case_studies
    SET views_count = views_count + 1
    WHERE id = NEW.case_study_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_increment_case_study_views
    AFTER INSERT ON public.case_study_views
    FOR EACH ROW
    EXECUTE FUNCTION public.increment_case_study_views();

-- Counters: Case Study Inspirations update trigger
CREATE FUNCTION public.update_case_study_inspired_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE public.case_studies
        SET inspired_count = inspired_count + 1
        WHERE id = NEW.case_study_id;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE public.case_studies
        SET inspired_count = greatest(0, inspired_count - 1)
        WHERE id = OLD.case_study_id;
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_update_case_study_inspired_count
    AFTER INSERT OR DELETE ON public.case_study_inspirations
    FOR EACH ROW
    EXECUTE FUNCTION public.update_case_study_inspired_count();

-- Audit trigger for case studies
CREATE TRIGGER tr_set_audit_fields_case_studies
    BEFORE UPDATE ON public.case_studies
    FOR EACH ROW
    EXECUTE FUNCTION public.set_audit_fields();

-- ==========================================
-- 8. INDEXES (Foreign Key Optimizations)
-- ==========================================

-- Quicks Indexes
CREATE INDEX idx_quicks_author_user_id ON public.quicks(author_user_id);
CREATE INDEX idx_quicks_expires_at ON public.quicks(expires_at);
CREATE INDEX idx_quick_views_quick_id ON public.quick_views(quick_id);
CREATE INDEX idx_quick_views_user_id ON public.quick_views(user_id);
CREATE INDEX idx_quick_helpfuls_quick_id ON public.quick_helpfuls(quick_id);
CREATE INDEX idx_quick_helpfuls_user_id ON public.quick_helpfuls(user_id);

-- Experiences Indexes
CREATE INDEX idx_experiences_author_user_id ON public.experiences(author_user_id);
CREATE INDEX idx_experience_views_experience_id ON public.experience_views(experience_id);
CREATE INDEX idx_experience_views_user_id ON public.experience_views(user_id);
CREATE INDEX idx_experience_inspirations_experience_id ON public.experience_inspirations(experience_id);
CREATE INDEX idx_experience_inspirations_user_id ON public.experience_inspirations(user_id);

-- Case Studies Indexes
CREATE INDEX idx_case_studies_author_user_id ON public.case_studies(author_user_id);
CREATE INDEX idx_case_study_views_case_study_id ON public.case_study_views(case_study_id);
CREATE INDEX idx_case_study_views_user_id ON public.case_study_views(user_id);
CREATE INDEX idx_case_study_inspirations_case_study_id ON public.case_study_inspirations(case_study_id);
CREATE INDEX idx_case_study_inspirations_user_id ON public.case_study_inspirations(user_id);

-- JSONB GIN Performance Optimization
CREATE INDEX idx_case_studies_content_gin ON public.case_studies USING gin (content);

-- ==========================================
-- 9. ROW LEVEL SECURITY (RLS) ENABLEMENT
-- ==========================================

-- Enable RLS on all tables
ALTER TABLE public.quicks ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.quick_views ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.quick_helpfuls ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.experiences ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.experience_views ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.experience_inspirations ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.case_studies ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.case_study_views ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.case_study_inspirations ENABLE ROW LEVEL SECURITY;

-- ==========================================
-- 10. RLS POLICIES
-- ==========================================

-- --- MODULE 1: QUICKS POLICIES ---

-- Quicks
CREATE POLICY "Allow public select for published quicks or owner" ON public.quicks
    FOR SELECT 
    USING (status = 'published' OR public.get_current_user_id() = author_user_id);

CREATE POLICY "Allow auth users to insert quicks" ON public.quicks
    FOR INSERT 
    WITH CHECK (auth.role() = 'authenticated' AND public.get_current_user_id() = author_user_id);

CREATE POLICY "Allow owner to update quicks" ON public.quicks
    FOR UPDATE 
    USING (public.get_current_user_id() = author_user_id)
    WITH CHECK (public.get_current_user_id() = author_user_id);

CREATE POLICY "Allow owner to delete quicks" ON public.quicks
    FOR DELETE 
    USING (public.get_current_user_id() = author_user_id);

-- Quick Views
CREATE POLICY "Allow read access to quick views" ON public.quick_views
    FOR SELECT 
    USING (true);

CREATE POLICY "Allow authenticated users to insert quick views" ON public.quick_views
    FOR INSERT 
    WITH CHECK (auth.role() = 'authenticated' AND public.get_current_user_id() = user_id);

CREATE POLICY "Allow user to delete their quick views" ON public.quick_views
    FOR DELETE 
    USING (public.get_current_user_id() = user_id);

-- Quick Helpfuls
CREATE POLICY "Allow read access to quick helpfuls" ON public.quick_helpfuls
    FOR SELECT 
    USING (true);

CREATE POLICY "Allow authenticated users to insert quick helpfuls" ON public.quick_helpfuls
    FOR INSERT 
    WITH CHECK (auth.role() = 'authenticated' AND public.get_current_user_id() = user_id);

CREATE POLICY "Allow user to delete their quick helpfuls" ON public.quick_helpfuls
    FOR DELETE 
    USING (public.get_current_user_id() = user_id);


-- --- MODULE 2: EXPERIENCES POLICIES ---

-- Experiences
CREATE POLICY "Allow public select for published experiences or owner" ON public.experiences
    FOR SELECT 
    USING (status = 'published' OR public.get_current_user_id() = author_user_id);

CREATE POLICY "Allow auth users to insert experiences" ON public.experiences
    FOR INSERT 
    WITH CHECK (auth.role() = 'authenticated' AND public.get_current_user_id() = author_user_id);

CREATE POLICY "Allow owner to update experiences" ON public.experiences
    FOR UPDATE 
    USING (public.get_current_user_id() = author_user_id)
    WITH CHECK (public.get_current_user_id() = author_user_id);

CREATE POLICY "Allow owner to delete experiences" ON public.experiences
    FOR DELETE 
    USING (public.get_current_user_id() = author_user_id);

-- Experience Views
CREATE POLICY "Allow read access to experience views" ON public.experience_views
    FOR SELECT 
    USING (true);

CREATE POLICY "Allow authenticated users to insert experience views" ON public.experience_views
    FOR INSERT 
    WITH CHECK (auth.role() = 'authenticated' AND public.get_current_user_id() = user_id);

CREATE POLICY "Allow user to delete their experience views" ON public.experience_views
    FOR DELETE 
    USING (public.get_current_user_id() = user_id);

-- Experience Inspirations
CREATE POLICY "Allow read access to experience inspirations" ON public.experience_inspirations
    FOR SELECT 
    USING (true);

CREATE POLICY "Allow authenticated users to insert experience inspirations" ON public.experience_inspirations
    FOR INSERT 
    WITH CHECK (auth.role() = 'authenticated' AND public.get_current_user_id() = user_id);

CREATE POLICY "Allow user to delete their experience inspirations" ON public.experience_inspirations
    FOR DELETE 
    USING (public.get_current_user_id() = user_id);


-- --- MODULE 3: CASE STUDIES POLICIES ---

-- Case Studies
CREATE POLICY "Allow public select for published case studies or owner" ON public.case_studies
    FOR SELECT 
    USING (status = 'published' OR public.get_current_user_id() = author_user_id);

CREATE POLICY "Allow auth users to insert case studies" ON public.case_studies
    FOR INSERT 
    WITH CHECK (auth.role() = 'authenticated' AND public.get_current_user_id() = author_user_id);

CREATE POLICY "Allow owner to update case studies" ON public.case_studies
    FOR UPDATE 
    USING (public.get_current_user_id() = author_user_id)
    WITH CHECK (public.get_current_user_id() = author_user_id);

CREATE POLICY "Allow owner to delete case studies" ON public.case_studies
    FOR DELETE 
    USING (public.get_current_user_id() = author_user_id);

-- Case Study Views
CREATE POLICY "Allow read access to case study views" ON public.case_study_views
    FOR SELECT 
    USING (true);

CREATE POLICY "Allow authenticated users to insert case study views" ON public.case_study_views
    FOR INSERT 
    WITH CHECK (auth.role() = 'authenticated' AND public.get_current_user_id() = user_id);

-- Case Study Views Delete
CREATE POLICY "Allow user to delete their case study views" ON public.case_study_views
    FOR DELETE 
    USING (public.get_current_user_id() = user_id);

-- Case Study Inspirations
CREATE POLICY "Allow read access to case study inspirations" ON public.case_study_inspirations
    FOR SELECT 
    USING (true);

CREATE POLICY "Allow authenticated users to insert case study inspirations" ON public.case_study_inspirations
    FOR INSERT 
    WITH CHECK (auth.role() = 'authenticated' AND public.get_current_user_id() = user_id);

CREATE POLICY "Allow user to delete their case study inspirations" ON public.case_study_inspirations
    FOR DELETE 
    USING (public.get_current_user_id() = user_id);
