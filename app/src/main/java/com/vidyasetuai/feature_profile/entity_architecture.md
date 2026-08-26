# Feature Profile - Room Database & Entity Architecture

## 1. Database Specifications
- **Database File:** `profile_database.db`
- **Class:** `ProfileDatabase.kt`
- **Isolation:** Standalone SQLite database with zero cross-module lock contention.
- **Read Rule:** 100% reactive reads from local Room DB `Flow<T>` on `Dispatchers.IO` (0ms UI latency).
- **Write Rule:** Direct writes to Supabase via `SafeSupabaseInvoker`, followed by immediate local upsert on success.

---

## 2. Room Database Entities (3 Core Tables)

### 📊 Table 1: `user_profile_cache` (`UserProfileEntity`)
Stores the authenticated user's own profile as well as cached public profiles visited by the user.

```kotlin
@Entity(tableName = "user_profile_cache")
data class UserProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "first_name")
    val firstName: String?,

    @ColumnInfo(name = "last_name")
    val lastName: String?,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "profile_picture_url")
    val profilePictureUrl: String?,

    @ColumnInfo(name = "profile_picture_local_path")
    val profilePictureLocalPath: String?, // 0ms offline image rendering

    @ColumnInfo(name = "cover_photo_url")
    val coverPhotoUrl: String?,

    @ColumnInfo(name = "cover_photo_local_path")
    val coverPhotoLocalPath: String?, // 0ms offline cover rendering

    @ColumnInfo(name = "gender")
    val gender: String?,

    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String?,

    @ColumnInfo(name = "bio")
    val bio: String?,

    @ColumnInfo(name = "preferred_language")
    val preferredLanguage: String?,

    @ColumnInfo(name = "is_private")
    val isPrivate: Boolean,

    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean,

    @ColumnInfo(name = "total_inspiring_count")
    val totalInspiringCount: Int, // Following count

    @ColumnInfo(name = "total_inspired_count")
    val totalInspiredCount: Int, // Followers count

    @ColumnInfo(name = "total_case_studies_count")
    val totalCaseStudiesCount: Int,

    @ColumnInfo(name = "is_me")
    val isMe: Boolean, // true = logged in user, false = cached public peer

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: String
)
```

---

### 👥 Table 2: `profile_inspirations` (`ProfileInspirationEntity`)
Caches the list of users that the user is following ("Inspiring") and users who follow the user ("Inspired") for 0ms offline browsing.

```kotlin
@Entity(tableName = "profile_inspirations")
data class ProfileInspirationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // Connection ID / conv ID

    @ColumnInfo(name = "user_id")
    val userId: String, // Parent user owning the list

    @ColumnInfo(name = "target_user_id")
    val targetUserId: String, // Peer user ID

    @ColumnInfo(name = "peer_name")
    val peerName: String,

    @ColumnInfo(name = "peer_username")
    val peerUsername: String,

    @ColumnInfo(name = "peer_avatar_url")
    val peerAvatarUrl: String?,

    @ColumnInfo(name = "peer_avatar_local_path")
    val peerAvatarLocalPath: String?,

    @ColumnInfo(name = "peer_bio")
    val peerBio: String?,

    @ColumnInfo(name = "relation_type")
    val relationType: String, // "FOLLOWING" or "FOLLOWER"

    @ColumnInfo(name = "is_mutual")
    val isMutual: Boolean,

    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
```

---

### 🏅 Table 3: `profile_verifications` (`ProfileVerificationEntity`)
Tracks contributor verification applications and badge statuses (Educator, Creator, Campus Ambassador).

```kotlin
@Entity(tableName = "profile_verifications")
data class ProfileVerificationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "contributor_type")
    val contributorType: String, // e.g. "TEACHER", "CREATOR", "STUDENT_LEADER"

    @ColumnInfo(name = "status")
    val status: String, // "PENDING", "APPROVED", "REJECTED"

    @ColumnInfo(name = "applicant_note")
    val applicantNote: String?,

    @ColumnInfo(name = "rejection_reason")
    val rejectionReason: String?,

    @ColumnInfo(name = "created_at")
    val createdAt: String
)
```

---

## 3. Data Access Object (DAO Specifications: `ProfileDao.kt`)

```kotlin
@Dao
interface ProfileDao {
    // --- Profile Cache Queries ---
    @Query("SELECT * FROM user_profile_cache WHERE is_me = 1 LIMIT 1")
    fun getMyProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile_cache WHERE user_id = :userId LIMIT 1")
    fun getProfileByIdFlow(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile_cache WHERE user_id = :userId LIMIT 1")
    suspend fun getProfileById(userId: String): UserProfileEntity?

    @Upsert
    suspend fun upsertProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile_cache SET profile_picture_local_path = :localPath WHERE user_id = :userId")
    suspend fun updateAvatarLocalPath(userId: String, localPath: String)

    @Query("UPDATE user_profile_cache SET cover_photo_local_path = :localPath WHERE user_id = :userId")
    suspend fun updateCoverLocalPath(userId: String, localPath: String)

    // --- Inspirations Queries ---
    @Query("SELECT * FROM profile_inspirations WHERE user_id = :userId AND relation_type = :type ORDER BY peer_name ASC")
    fun getInspirationsFlow(userId: String, type: String): Flow<List<ProfileInspirationEntity>>

    @Upsert
    suspend fun upsertInspirations(list: List<ProfileInspirationEntity>)

    @Query("DELETE FROM profile_inspirations WHERE user_id = :userId")
    suspend fun clearInspirationsForUser(userId: String)

    // --- Verifications Queries ---
    @Query("SELECT * FROM profile_verifications WHERE user_id = :userId LIMIT 1")
    fun getVerificationFlow(userId: String): Flow<ProfileVerificationEntity?>

    @Upsert
    suspend fun upsertVerification(verification: ProfileVerificationEntity)

    // --- Wipe Database (Logout) ---
    @Query("DELETE FROM user_profile_cache")
    suspend fun clearAllProfiles()

    @Query("DELETE FROM profile_inspirations")
    suspend fun clearAllInspirations()

    @Query("DELETE FROM profile_verifications")
    suspend fun clearAllVerifications()
}
```

---

## 4. Supabase SQL Functions & Triggers Specifications

### 🚀 RPC 1: `fn_fetch_profile_v2_payload(p_target_user_id UUID DEFAULT NULL)`
- **Purpose:** Single-flight payload sync returning complete profile, accurate follower/following counts, recent inspirations list, and verification badge status in 1 network flight.
- **SQL Location:** `feature_profile/supabase_sql/02_profile_v2_rpcs.sql`
- **Output JSON Payload:**
  ```json
  {
    "success": true,
    "is_me": true,
    "profile": {
      "user_id": "21513c76-f57b-45bd-9714-9a944abedd0e",
      "username": "harsh_sharma",
      "first_name": "Harsh",
      "last_name": "Sharma",
      "full_name": "Harsh Sharma",
      "profile_picture_url": "https://.../avatar.jpg",
      "cover_photo_url": "https://.../cover.jpg",
      "gender": "male",
      "date_of_birth": "2002-05-14",
      "bio": "Building VidyaSetu AI",
      "preferred_language": "hi",
      "is_private": false,
      "is_verified": true,
      "total_inspiring_count": 128,
      "total_inspired_count": 450,
      "total_case_studies_count": 12,
      "updated_at": "2026-08-24T00:30:00Z"
    },
    "following_list": [ ... ],
    "followers_list": [ ... ],
    "verification": { ... },
    "synced_at": "2026-08-24T01:05:00Z"
  }
  ```

### ✍️ RPC 2: `fn_update_user_profile_v2(...)`
- **Purpose:** Atomic profile update (Name, Bio, Gender, DOB, Language, Avatar/Cover URLs, Privacy) with server-side validation.
- **SQL Location:** `feature_profile/supabase_sql/02_profile_v2_rpcs.sql`

### ⚡ Trigger: `trg_sync_profile_connection_counts`
- **Purpose:** Automatic realtime synchronization of `total_inspiring_count` (Following) and `total_inspired_count` (Followers) on `public.campus_connections` events (Insert, Unfollow, Block, Restore).
- **SQL Location:** `feature_profile/supabase_sql/01_profile_counter_triggers.sql`
