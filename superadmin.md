# VidyaSetu AI — Super Admin System: Architecture & Schema Reference
# Version: Future Reference Document (अभी implement नहीं होगा)
# Last Updated: 2026-06-11
# Purpose: Super Admin App का भविष्य का Blueprint

# ─────────────────────────────────────────────────────────────────
# OVERVIEW
# ─────────────────────────────────────────────────────────────────
# Super Admin एक SEPARATE APPLICATION होगा (अलग Android App या Web App)
# जो केवल VidyaSetu AI Team के authorized members use करेंगे।
# यह App VidyaSetu User App से completely अलग होगी।
# इसे Public Play Store पर publish नहीं किया जाएगा।
#
# अभी (V1): Team Supabase Dashboard से manually सब manage करती है।
# भविष्य (V2): Super Admin App से यही काम screen-based होगा।
# ─────────────────────────────────────────────────────────────────


# ═══════════════════════════════════════════════════════════════
# SUPER ADMIN APP — क्या-क्या होगा
# ═══════════════════════════════════════════════════════════════

## 1. Authentication & Access Control
   - Super Admin App का अपना Login होगा (separate auth system)
   - केवल team members को access होगा
   - Role-based: SuperAdmin, ContentModerator, SupportAgent
   - Two-Factor Authentication अनिवार्य होगा
   - Session timeout strict होगा
   - Activity logs सभी actions के लिए

## 2. Contributor Verification Management
   - Pending verification requests की list
   - User profile का full view (complete profile check के लिए)
   - Approve / Reject / Suspend with reason
   - Email notification trigger on status change
   - Bulk actions (multiple approve/reject)
   - Filter by: contributor_type, date, status

## 3. Case Study Moderation
   - 'under_review' status वाली case studies की queue
   - Full case study preview (exact जैसा User App में दिखेगा)
   - Approve (status → published)
   - Reject with reason (status → rejected)
   - Edit before publish (content blocks edit करना)
   - Archive published case study
   - Bulk moderation

## 4. Platform Case Study Management (आपकी टीम की posts)
   - नई Case Study create करना (Rich editor)
   - Draft save करना
   - Schedule publish करना
   - Edit published case study
   - Archive / Delete

## 5. User Management
   - User search (by email, name, username)
   - User profile view
   - Account suspend / activate
   - Verification status view

## 6. Organization Management
   - Organizations list
   - Organization verification status
   - Organization suspend / activate

## 7. Analytics Dashboard
   - Total case studies (by status, by author_type)
   - Pending verifications count
   - Total reactions, bookmarks
   - Most viewed case studies
   - Recent activity log

## 8. App Version Management
   - app_versions table से data (already in schema)
   - New version publish
   - Force update toggle
   - Changelog manage

## 9. Global Data Management
   - global_boards, global_subjects, global_classes manage
   - global_sessions manage
   - global_languages, global_blood_groups etc.

## 10. Support & Communication
    - Verification emails track
    - User queries view
    - Announcement push (FCM notifications)


# ═══════════════════════════════════════════════════════════════
# SUPER ADMIN DATABASE TABLES (भविष्य में बनेंगी)
# ═══════════════════════════════════════════════════════════════
# Note: अभी Supabase Dashboard से सब होगा।
# नीचे केवल reference के लिए — अभी create नहीं करनी।

## admin_users
   - id, auth_id, email, role, is_active, last_login
   - role: 'super_admin', 'content_moderator', 'support_agent'

## admin_activity_logs
   - id, admin_user_id, action_type, target_table, target_id
   - old_value (JSONB), new_value (JSONB), ip_address, created_at
   - यह सभी admin actions को audit करेगा

## admin_sessions
   - id, admin_user_id, session_token, expires_at, created_at


# ═══════════════════════════════════════════════════════════════
# SUPER ADMIN APP — TECHNOLOGY STACK (Suggestion)
# ═══════════════════════════════════════════════════════════════

## Option A: Web App (Recommended)
   - Next.js + TypeScript
   - Supabase JS Client
   - Deployed on Vercel (private, team-only URL)
   - No Play Store required
   - Easy to update (no app release cycle)
   - Desktop-first design (team PC पर use होगा)

## Option B: Android App (Same Tech Stack as User App)
   - Kotlin + Jetpack Compose
   - Internal distribution (Firebase App Distribution)
   - Play Store publish नहीं
   - Same Supabase backend

## Recommendation: Web App — क्योंकि
   - Team PC/Laptop पर comfortable
   - Release cycle नहीं
   - Easy content editing (keyboard + mouse)
   - No Play Store review issues


# ═══════════════════════════════════════════════════════════════
# V1 से V2 SUPER ADMIN MIGRATION
# ═══════════════════════════════════════════════════════════════

## V1 (अभी — Supabase Dashboard):
   - content_contributor_verifications में manually status update
   - case_studies में manually insert/update
   - No audit trail (Supabase built-in logs only)

## V2 (Super Admin App):
   - Same tables, same columns — कुछ नहीं बदलेगा
   - केवल App layer add होगी जो same Supabase tables से interact करेगी
   - admin_activity_logs add होगी (new table)
   - Email automation improve होगी


# ═══════════════════════════════════════════════════════════════
# EMAIL AUTOMATION REFERENCE (V1)
# ═══════════════════════════════════════════════════════════════

## Verification Apply होने पर (V1):
   - Supabase Edge Function trigger होगा
   - Auto email → support@vidyasetuai.com
   - Subject: "New Contributor Verification Request"
   - Body:
     - Contributor Type: User/Organization/Parent Organization
     - Name / Email / ID
     - Applied At
     - Supabase Dashboard link to review

## Verification Status Update होने पर (V1: Manual trigger via Dashboard):
   - Team manually User को email करे (template ready रखें)
   - Approved: "बधाई! आप अब Case Study upload कर सकते हैं"
   - Rejected: "आपकी request reject कर दी गई। कारण: [reason]"

## V2 में:
   - Super Admin App से button click पर auto email trigger
   - Same Edge Function, just called from Admin App


# ═══════════════════════════════════════════════════════════════
# RULES & GUIDELINES FOR SUPER ADMIN
# ═══════════════════════════════════════════════════════════════

1. Super Admin credentials कभी share नहीं होंगी
2. सभी actions audit-logged होंगे (V2 में)
3. Case Study reject करते समय reason देना अनिवार्य होगा
4. Verification approve करने से पहले profile completeness check करें
5. Suspension केवल policy violation पर, explanation required
6. Platform case studies के लिए कम से कम 1 reviewer होगा
7. Emergency actions (mass delete, suspend) के लिए 2FA confirm
