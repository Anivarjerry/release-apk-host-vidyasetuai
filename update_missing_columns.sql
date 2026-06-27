-- ====================================================================
-- SQL Script: Add Missing Columns for Offline-First Synchronization
-- Target: Supabase (PostgreSQL)
-- ====================================================================

-- 1. organization_sections (सेक्शन मास्टर - Add updated_at for incremental sync)
ALTER TABLE public.organization_sections 
ADD COLUMN IF NOT EXISTS updated_at timestamp with time zone NOT NULL DEFAULT now();

-- Auto-update trigger for organization_sections.updated_at
CREATE OR REPLACE FUNCTION public.handle_update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS set_timestamp_organization_sections ON public.organization_sections;
CREATE TRIGGER set_timestamp_organization_sections
    BEFORE UPDATE ON public.organization_sections
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_update_timestamp();


-- 2. organization_profiles (संस्थान प्रोफाइल - Add is_active & is_deleted)
ALTER TABLE public.organization_profiles 
ADD COLUMN IF NOT EXISTS is_active boolean NOT NULL DEFAULT true,
ADD COLUMN IF NOT EXISTS is_deleted boolean NOT NULL DEFAULT false;


-- 3. user_profiles (यूजर प्रोफाइल - Add is_active & is_deleted)
ALTER TABLE public.user_profiles 
ADD COLUMN IF NOT EXISTS is_active boolean NOT NULL DEFAULT true,
ADD COLUMN IF NOT EXISTS is_deleted boolean NOT NULL DEFAULT false;


-- 4. global_staff_roles (स्टाफ रोल - Add is_deleted)
ALTER TABLE public.global_staff_roles 
ADD COLUMN IF NOT EXISTS is_deleted boolean NOT NULL DEFAULT false;


-- 5. global_classes (सिस्टम क्लासेज - Add is_deleted)
ALTER TABLE public.global_classes 
ADD COLUMN IF NOT EXISTS is_deleted boolean NOT NULL DEFAULT false;


-- 6. user_journey_task_progress (जर्नी टास्क प्रोग्रेस - Add is_active)
ALTER TABLE public.user_journey_task_progress 
ADD COLUMN IF NOT EXISTS is_active boolean NOT NULL DEFAULT true;


-- 7. user_journey_mcq_progress (जर्नी MCQ प्रोग्रेस - Add is_active)
ALTER TABLE public.user_journey_mcq_progress 
ADD COLUMN IF NOT EXISTS is_active boolean NOT NULL DEFAULT true;
