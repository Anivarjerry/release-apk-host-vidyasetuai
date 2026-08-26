-- ============================================================================
-- VIDYASETU CAMPUS - 02: TRIGGERS (Automated Mutual Follow Synchronization)
-- ============================================================================

-- Function: Automatically maintains is_mutual flag on both directional rows
CREATE OR REPLACE FUNCTION public.fn_sync_mutual_connection_state()
RETURNS TRIGGER AS $$
DECLARE
    v_reciprocal_exists BOOLEAN := false;
BEGIN
    -- Prevent infinite recursion loop in PostgreSQL
    IF pg_trigger_depth() > 1 THEN
        RETURN NEW;
    END IF;

    -- On INSERT or UPDATE
    IF TG_OP IN ('INSERT', 'UPDATE') THEN
        -- Check if target user already has an active reciprocal connection to this user
        SELECT EXISTS (
            SELECT 1 FROM public.campus_connections 
            WHERE user_id = NEW.target_user_id 
              AND target_user_id = NEW.user_id 
              AND status = 'FOLLOWING'
              AND is_deleted = false
        ) INTO v_reciprocal_exists;

        IF (NEW.status = 'FOLLOWING' AND NEW.is_deleted = false AND v_reciprocal_exists) THEN
            NEW.is_mutual := true;
            -- Update the reverse row
            UPDATE public.campus_connections 
            SET is_mutual = true, updated_at = NOW()
            WHERE user_id = NEW.target_user_id 
              AND target_user_id = NEW.user_id 
              AND is_mutual = false;
        ELSE
            NEW.is_mutual := false;
            -- Update the reverse row
            UPDATE public.campus_connections 
            SET is_mutual = false, updated_at = NOW()
            WHERE user_id = NEW.target_user_id 
              AND target_user_id = NEW.user_id 
              AND is_mutual = true;
        END IF;

        RETURN NEW;
    END IF;

    -- On DELETE
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


-- Attach Trigger to campus_connections table
DROP TRIGGER IF EXISTS trg_sync_campus_mutual ON public.campus_connections;

CREATE TRIGGER trg_sync_campus_mutual
BEFORE INSERT OR UPDATE ON public.campus_connections
FOR EACH ROW EXECUTE FUNCTION public.fn_sync_mutual_connection_state();
