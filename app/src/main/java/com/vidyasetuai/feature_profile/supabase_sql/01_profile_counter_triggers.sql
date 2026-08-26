-- ============================================================================
-- 01_PROFILE_COUNTER_TRIGGERS.SQL
-- Automatic Realtime Synchronization between campus_connections and user_profiles counts
-- ============================================================================

-- Function to atomically update following/followers counts in user_profiles
CREATE OR REPLACE FUNCTION public.fn_sync_profile_connection_counts()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    -- 1. INSERTION (New Follow)
    IF (TG_OP = 'INSERT') THEN
        IF (NEW.status = 'FOLLOWING' AND NEW.is_deleted = false) THEN
            -- user_id is following someone -> increase total_inspiring_count (Following)
            UPDATE public.user_profiles
            SET total_inspiring_count = total_inspiring_count + 1,
                updated_at = now()
            WHERE user_id = NEW.user_id;

            -- target_user_id gained a follower -> increase total_inspired_count (Followers)
            UPDATE public.user_profiles
            SET total_inspired_count = total_inspired_count + 1,
                updated_at = now()
            WHERE user_id = NEW.target_user_id;
        END IF;

    -- 2. UPDATE (Unfollow / Soft Delete / Block / Restore)
    ELSIF (TG_OP = 'UPDATE') THEN
        -- Case A: Became Deleted or Status changed from FOLLOWING to BLOCKED/UNFOLLOWED
        IF ((OLD.status = 'FOLLOWING' AND OLD.is_deleted = false) AND
            (NEW.status != 'FOLLOWING' OR NEW.is_deleted = true)) THEN

            UPDATE public.user_profiles
            SET total_inspiring_count = GREATEST(0, total_inspiring_count - 1),
                updated_at = now()
            WHERE user_id = OLD.user_id;

            UPDATE public.user_profiles
            SET total_inspired_count = GREATEST(0, total_inspired_count - 1),
                updated_at = now()
            WHERE user_id = OLD.target_user_id;

        -- Case B: Restored from deleted / unblocked back to FOLLOWING
        ELSIF ((OLD.status != 'FOLLOWING' OR OLD.is_deleted = true) AND
               (NEW.status = 'FOLLOWING' AND NEW.is_deleted = false)) THEN

            UPDATE public.user_profiles
            SET total_inspiring_count = total_inspiring_count + 1,
                updated_at = now()
            WHERE user_id = NEW.user_id;

            UPDATE public.user_profiles
            SET total_inspired_count = total_inspired_count + 1,
                updated_at = now()
            WHERE user_id = NEW.target_user_id;
        END IF;

    -- 3. HARD DELETE
    ELSIF (TG_OP = 'DELETE') THEN
        IF (OLD.status = 'FOLLOWING' AND OLD.is_deleted = false) THEN
            UPDATE public.user_profiles
            SET total_inspiring_count = GREATEST(0, total_inspiring_count - 1),
                updated_at = now()
            WHERE user_id = OLD.user_id;

            UPDATE public.user_profiles
            SET total_inspired_count = GREATEST(0, total_inspired_count - 1),
                updated_at = now()
            WHERE user_id = OLD.target_user_id;
        END IF;
    END IF;

    RETURN COALESCE(NEW, OLD);
END;
$$;

-- Attach trigger to campus_connections
DROP TRIGGER IF EXISTS trg_sync_profile_connection_counts ON public.campus_connections;
CREATE TRIGGER trg_sync_profile_connection_counts
AFTER INSERT OR UPDATE OR DELETE ON public.campus_connections
FOR EACH ROW
EXECUTE FUNCTION public.fn_sync_profile_connection_counts();

-- ============================================================================
-- 3. One-Time Historical Backfill / Counter Reconciliation Query
-- Run this once to accurately populate total_inspiring_count and total_inspired_count
-- from existing campus_connections records into user_profiles table.
-- ============================================================================

UPDATE public.user_profiles p
SET 
    total_inspiring_count = COALESCE((
        SELECT COUNT(*)::integer 
        FROM public.campus_connections c 
        WHERE c.user_id = p.user_id 
          AND c.status = 'FOLLOWING' 
          AND c.is_deleted = false
    ), 0),
    total_inspired_count = COALESCE((
        SELECT COUNT(*)::integer 
        FROM public.campus_connections c 
        WHERE c.target_user_id = p.user_id 
          AND c.status = 'FOLLOWING' 
          AND c.is_deleted = false
    ), 0),
    updated_at = now();

