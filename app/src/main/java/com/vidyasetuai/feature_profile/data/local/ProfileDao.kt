package com.vidyasetuai.feature_profile.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Enterprise Room DAO for 0ms reactive profile reads and instant local upserts.
 */
@Dao
interface ProfileDao {

    @Query("SELECT * FROM user_profile_cache WHERE is_me = 1 LIMIT 1")
    fun getMyProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile_cache WHERE is_me = 1 LIMIT 1")
    suspend fun getMyProfile(): UserProfileEntity?

    @Query("SELECT * FROM user_profile_cache WHERE user_id = :userId LIMIT 1")
    fun getProfileByIdFlow(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile_cache WHERE user_id = :userId LIMIT 1")
    suspend fun getProfileById(userId: String): UserProfileEntity?

    @Upsert
    suspend fun upsertProfile(profile: UserProfileEntity)

    @Upsert
    suspend fun upsertProfiles(profiles: List<UserProfileEntity>)

    @Query("UPDATE user_profile_cache SET profile_picture_local_path = :localPath WHERE user_id = :userId")
    suspend fun updateAvatarLocalPath(userId: String, localPath: String)

    @Query("UPDATE user_profile_cache SET cover_photo_local_path = :localPath WHERE user_id = :userId")
    suspend fun updateCoverLocalPath(userId: String, localPath: String)

    @Query("SELECT * FROM profile_inspirations WHERE user_id = :userId AND relation_type = :type ORDER BY peer_name ASC")
    fun getInspirationsFlow(userId: String, type: String): Flow<List<ProfileInspirationEntity>>

    @Upsert
    suspend fun upsertInspirations(list: List<ProfileInspirationEntity>)

    @Query("UPDATE profile_inspirations SET peer_avatar_local_path = :localPath WHERE target_user_id = :targetUserId")
    suspend fun updateInspirationAvatarLocalPath(targetUserId: String, localPath: String)

    @Query("DELETE FROM profile_inspirations WHERE user_id = :userId")
    suspend fun clearInspirationsForUser(userId: String)

    @Query("SELECT * FROM profile_verifications WHERE user_id = :userId LIMIT 1")
    fun getVerificationFlow(userId: String): Flow<ProfileVerificationEntity?>

    @Upsert
    suspend fun upsertVerification(verification: ProfileVerificationEntity)

    @Query("DELETE FROM user_profile_cache")
    suspend fun clearAllProfiles()

    @Query("DELETE FROM profile_inspirations")
    suspend fun clearAllInspirations()

    @Query("DELETE FROM profile_verifications")
    suspend fun clearAllVerifications()
}
