-- ============================================================================
-- SQL RPC Function: fetch_home_feed_payload_mobile
-- Purpose: Ultra-fast single database call to fetch initial Quicks, Experiences,
--          and Case Studies for the mobile Home Feed in a single JSON payload.
-- ============================================================================

CREATE OR REPLACE FUNCTION fetch_home_feed_payload_mobile(
    p_user_id UUID,
    p_limit INT DEFAULT 10
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_quicks JSONB;
    v_experiences JSONB;
    v_case_studies JSONB;
    v_result JSONB;
BEGIN
    -- 1. Fetch top active Quicks (Limited to p_limit)
    SELECT COALESCE(jsonb_agg(q_data), '[]'::jsonb) INTO v_quicks
    FROM (
        SELECT 
            q.id,
            q.author_user_id AS "authorUserId",
            q.title,
            q.description,
            q.cover_image_url AS "coverImageUrl",
            q.views_count AS "viewsCount",
            q.helpful_count AS "helpfulCount",
            q.status,
            q.created_at AS "createdAt",
            q.expires_at AS "expiresAt",
            COALESCE(op.organization_name, 'VidyaSetu User') AS "authorName",
            COALESCE(op.slug, 'user') AS "authorUsername",
            op.profile_pic_url AS "authorProfilePicUrl",
            COALESCE(op.is_verified, false) AS "isAuthorVerified",
            EXISTS (
                SELECT 1 FROM quick_reactions qr 
                WHERE qr.quick_id = q.id AND qr.user_id = p_user_id
            ) AS "isHelpful"
        FROM quicks q
        LEFT JOIN organization_profiles op ON op.user_id = q.author_user_id
        WHERE q.status = 'active' OR q.status IS NULL
        ORDER BY q.created_at DESC
        LIMIT p_limit
    ) q_data;

    -- 2. Fetch top Experiences (Limited to p_limit)
    SELECT COALESCE(jsonb_agg(e_data), '[]'::jsonb) INTO v_experiences
    FROM (
        SELECT 
            e.id,
            e.title,
            e.cover_image_url AS "coverImageUrl",
            e.description,
            e.author_user_id AS "authorUserId",
            COALESCE(e.inspired_count, 0) AS "inspiredCount",
            EXISTS (
                SELECT 1 FROM experience_inspirations ei 
                WHERE ei.experience_id = e.id AND ei.user_id = p_user_id
            ) AS "isInspired",
            e.created_at AS "createdAt",
            COALESCE(op.organization_name, 'Scholar') AS "authorName",
            COALESCE(op.slug, 'user') AS "authorUsername",
            op.profile_pic_url AS "authorProfilePicUrl",
            COALESCE(op.is_verified, false) AS "isAuthorVerified"
        FROM experiences e
        LEFT JOIN organization_profiles op ON op.user_id = e.author_user_id
        ORDER BY e.created_at DESC
        LIMIT p_limit
    ) e_data;

    -- 3. Fetch top Case Studies (Limited to p_limit)
    SELECT COALESCE(jsonb_agg(cs_data), '[]'::jsonb) INTO v_case_studies
    FROM (
        SELECT 
            cs.id,
            cs.title,
            cs.slug,
            cs.cover_image_url AS "coverImageUrl",
            COALESCE(cs.short_description, '') AS "shortDescription",
            COALESCE(cs.language, 'en') AS "language",
            COALESCE(cs.tags, '{}'::text[]) AS "tags",
            cs.read_time_minutes AS "readTimeMinutes",
            COALESCE(cs.author_type, 'organization') AS "authorType",
            cs.author_user_id AS "authorUserId",
            COALESCE(op.organization_name, 'VidyaSetu AI') AS "authorName",
            COALESCE(op.slug, 'vidyasetu') AS "authorUsername",
            op.profile_pic_url AS "authorProfilePicUrl",
            COALESCE(op.is_verified, false) AS "isAuthorVerified",
            COALESCE(cs.view_count, 0) AS "viewCount",
            cs.published_at AS "publishedAt",
            cs.created_at AS "createdAt",
            cs.updated_at AS "updatedAt",
            EXISTS (
                SELECT 1 FROM case_study_reactions csr 
                WHERE csr.case_study_id = cs.id AND csr.user_id = p_user_id
            ) AS "isReacted",
            COALESCE(cs.reaction_count, 0) AS "reactionCount",
            EXISTS (
                SELECT 1 FROM case_study_bookmarks csb 
                WHERE csb.case_study_id = cs.id AND csb.user_id = p_user_id
            ) AS "isBookmarked",
            COALESCE(cs.bookmark_count, 0) AS "bookmarkCount"
        FROM case_studies cs
        LEFT JOIN organization_profiles op ON op.user_id = cs.author_user_id
        ORDER BY cs.created_at DESC
        LIMIT p_limit
    ) cs_data;

    -- Construct JSON output object containing all three feeds
    v_result := jsonb_build_object(
        'quicks', v_quicks,
        'experiences', v_experiences,
        'case_studies', v_case_studies
    );

    RETURN v_result;
END;
$$;
