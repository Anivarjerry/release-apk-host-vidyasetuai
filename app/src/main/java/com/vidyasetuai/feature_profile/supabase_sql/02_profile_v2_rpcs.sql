-- ============================================================================
-- 02_PROFILE_V2_RPCS.SQL
-- Enterprise Single-Flight RPCs for Profile Module
-- (Non-breaking: Does NOT alter or delete any legacy functions)
-- ============================================================================

-- 1. Single-Flight Master Payload Fetcher for Profile Tab
CREATE OR REPLACE FUNCTION public.fn_fetch_profile_v2_payload(
    p_target_user_id UUID DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_auth_id UUID;
    v_active_user_id UUID;
    v_target_user_id UUID;
    v_profile_record RECORD;
    v_following_count INT := 0;
    v_followers_count INT := 0;
    v_case_studies_count INT := 0;
    v_following_json JSONB := '[]'::jsonb;
    v_followers_json JSONB := '[]'::jsonb;
    v_verification_json JSONB := NULL;
BEGIN
    -- Identify the caller user
    v_caller_auth_id := auth.uid();
    IF v_caller_auth_id IS NULL THEN
        RAISE EXCEPTION 'Unauthorized call to fn_fetch_profile_v2_payload';
    END IF;

    SELECT id INTO v_active_user_id 
    FROM public.users 
    WHERE auth_id = v_caller_auth_id OR id = v_caller_auth_id 
    LIMIT 1;

    IF v_active_user_id IS NULL THEN
        v_active_user_id := v_caller_auth_id;
    END IF;

    -- If target_user_id is not provided, fetch for current active user
    IF p_target_user_id IS NULL THEN
        v_target_user_id := v_active_user_id;
    ELSE
        v_target_user_id := p_target_user_id;
    END IF;

    -- 1. Fetch User Profile Row
    SELECT * INTO v_profile_record
    FROM public.user_profiles
    WHERE user_id = v_target_user_id;

    IF v_profile_record IS NULL THEN
        RETURN jsonb_build_object(
            'success', false,
            'error', 'Profile not found'
        );
    END IF;

    -- 2. Fetch Accurate Counts
    SELECT COUNT(*) INTO v_following_count
    FROM public.campus_connections
    WHERE user_id = v_target_user_id 
      AND status = 'FOLLOWING' 
      AND is_deleted = false;

    SELECT COUNT(*) INTO v_followers_count
    FROM public.campus_connections
    WHERE target_user_id = v_target_user_id 
      AND status = 'FOLLOWING' 
      AND is_deleted = false;

    -- Case studies count (if table exists)
    BEGIN
        SELECT COUNT(*) INTO v_case_studies_count
        FROM public.case_studies
        WHERE author_id = v_target_user_id 
          AND is_deleted = false;
    EXCEPTION WHEN OTHERS THEN
        v_case_studies_count := 0;
    END;

    -- 3. Fetch Following List (First 100 for 0ms offline browsing)
    SELECT COALESCE(jsonb_agg(
        jsonb_build_object(
            'id', c.id,
            'user_id', c.user_id,
            'target_user_id', c.target_user_id,
            'peer_name', COALESCE(p.full_name, p.username, 'Member'),
            'peer_username', COALESCE(p.username, ''),
            'peer_avatar_url', p.profile_picture_url,
            'peer_bio', p.bio,
            'relation_type', 'FOLLOWING',
            'is_mutual', c.is_mutual,
            'is_verified', COALESCE(p.is_verified, false),
            'updated_at', c.updated_at
        )
    ), '[]'::jsonb)
    INTO v_following_json
    FROM public.campus_connections c
    LEFT JOIN public.user_profiles p ON p.user_id = c.target_user_id
    WHERE c.user_id = v_target_user_id 
      AND c.status = 'FOLLOWING' 
      AND c.is_deleted = false;

    -- 4. Fetch Followers List
    SELECT COALESCE(jsonb_agg(
        jsonb_build_object(
            'id', c.id,
            'user_id', c.target_user_id,
            'target_user_id', c.user_id,
            'peer_name', COALESCE(p.full_name, p.username, 'Member'),
            'peer_username', COALESCE(p.username, ''),
            'peer_avatar_url', p.profile_picture_url,
            'peer_bio', p.bio,
            'relation_type', 'FOLLOWER',
            'is_mutual', c.is_mutual,
            'is_verified', COALESCE(p.is_verified, false),
            'updated_at', c.updated_at
        )
    ), '[]'::jsonb)
    INTO v_followers_json
    FROM public.campus_connections c
    LEFT JOIN public.user_profiles p ON p.user_id = c.user_id
    WHERE c.target_user_id = v_target_user_id 
      AND c.status = 'FOLLOWING' 
      AND c.is_deleted = false;

    -- 5. Fetch Verification Status
    BEGIN
        SELECT to_jsonb(v) INTO v_verification_json
        FROM public.contributor_verifications v
        WHERE v.user_id = v_target_user_id
        ORDER BY v.created_at DESC
        LIMIT 1;
    EXCEPTION WHEN OTHERS THEN
        v_verification_json := NULL;
    END;

    -- Construct the Master Single-Flight Response
    RETURN jsonb_build_object(
        'success', true,
        'is_me', (v_target_user_id = v_active_user_id),
        'profile', jsonb_build_object(
            'user_id', v_profile_record.user_id,
            'username', v_profile_record.username,
            'first_name', v_profile_record.first_name,
            'last_name', v_profile_record.last_name,
            'full_name', v_profile_record.full_name,
            'profile_picture_url', v_profile_record.profile_picture_url,
            'cover_photo_url', v_profile_record.cover_photo_url,
            'gender', v_profile_record.gender,
            'date_of_birth', v_profile_record.date_of_birth,
            'bio', v_profile_record.bio,
            'preferred_language', v_profile_record.preferred_language,
            'is_private', v_profile_record.is_private,
            'is_verified', v_profile_record.is_verified,
            'total_inspiring_count', v_following_count,
            'total_inspired_count', v_followers_count,
            'total_case_studies_count', v_case_studies_count,
            'updated_at', v_profile_record.updated_at
        ),
        'following_list', v_following_json,
        'followers_list', v_followers_json,
        'verification', v_verification_json,
        'synced_at', now()
    );
END;
$$;


-- 2. Atomic Profile Update RPC with Validation
CREATE OR REPLACE FUNCTION public.fn_update_user_profile_v2(
    p_username TEXT DEFAULT NULL,
    p_first_name TEXT DEFAULT NULL,
    p_last_name TEXT DEFAULT NULL,
    p_bio TEXT DEFAULT NULL,
    p_gender public.gender_type DEFAULT NULL,
    p_date_of_birth DATE DEFAULT NULL,
    p_preferred_language TEXT DEFAULT NULL,
    p_profile_picture_url TEXT DEFAULT NULL,
    p_cover_photo_url TEXT DEFAULT NULL,
    p_is_private BOOLEAN DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_auth_id UUID;
    v_user_id UUID;
    v_clean_username TEXT;
    v_updated_record RECORD;
BEGIN
    v_caller_auth_id := auth.uid();
    IF v_caller_auth_id IS NULL THEN
        RAISE EXCEPTION 'Unauthorized';
    END IF;

    SELECT id INTO v_user_id 
    FROM public.users 
    WHERE auth_id = v_caller_auth_id OR id = v_caller_auth_id 
    LIMIT 1;

    IF v_user_id IS NULL THEN
        v_user_id := v_caller_auth_id;
    END IF;

    -- Validate and clean username if provided
    IF p_username IS NOT NULL AND TRIM(p_username) <> '' THEN
        v_clean_username := LOWER(TRIM(p_username));
        IF LENGTH(v_clean_username) < 3 THEN
            RETURN jsonb_build_object(
                'success', false,
                'error', 'Username must be at least 3 characters'
            );
        END IF;

        IF EXISTS (
            SELECT 1 FROM public.user_profiles 
            WHERE LOWER(username) = v_clean_username AND user_id <> v_user_id
        ) THEN
            RETURN jsonb_build_object(
                'success', false,
                'error', 'Username is already taken'
            );
        END IF;
    END IF;

    UPDATE public.user_profiles
    SET 
        username = COALESCE(v_clean_username, username),
        first_name = COALESCE(p_first_name, first_name),
        last_name = COALESCE(p_last_name, last_name),
        bio = COALESCE(p_bio, bio),
        gender = COALESCE(p_gender, gender),
        date_of_birth = COALESCE(p_date_of_birth, date_of_birth),
        preferred_language = COALESCE(p_preferred_language, preferred_language),
        profile_picture_url = COALESCE(p_profile_picture_url, profile_picture_url),
        cover_photo_url = COALESCE(p_cover_photo_url, cover_photo_url),
        is_private = COALESCE(p_is_private, is_private),
        updated_at = now()
    WHERE user_id = v_user_id
    RETURNING * INTO v_updated_record;

    RETURN jsonb_build_object(
        'success', true,
        'profile', to_jsonb(v_updated_record)
    );
END;
$$;


-- 3. Live Debounced Username Availability Checker RPC
CREATE OR REPLACE FUNCTION public.fn_check_username_available(
    p_username TEXT
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_caller_auth_id UUID;
    v_user_id UUID;
    v_clean_username TEXT;
    v_is_taken BOOLEAN;
BEGIN
    v_caller_auth_id := auth.uid();
    IF v_caller_auth_id IS NOT NULL THEN
        SELECT id INTO v_user_id 
        FROM public.users 
        WHERE auth_id = v_caller_auth_id OR id = v_caller_auth_id 
        LIMIT 1;
        IF v_user_id IS NULL THEN v_user_id := v_caller_auth_id; END IF;
    END IF;

    v_clean_username := LOWER(TRIM(p_username));

    IF v_clean_username = '' OR LENGTH(v_clean_username) < 3 THEN
        RETURN jsonb_build_object(
            'available', false,
            'error', 'Username must be at least 3 characters'
        );
    END IF;

    -- Check if taken by someone else
    SELECT EXISTS (
        SELECT 1 FROM public.user_profiles 
        WHERE LOWER(username) = v_clean_username 
          AND (v_user_id IS NULL OR user_id <> v_user_id)
    ) INTO v_is_taken;

    RETURN jsonb_build_object(
        'available', NOT v_is_taken
    );
END;
$$;

