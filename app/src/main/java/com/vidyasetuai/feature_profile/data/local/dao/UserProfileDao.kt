package com.vidyasetuai.feature_profile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vidyasetuai.feature_profile.data.local.entity.ContributorVerificationEntity
import com.vidyasetuai.feature_profile.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profiles WHERE user_id = :userId")
    fun getProfileFlow(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE user_id = :userId")
    suspend fun getProfile(userId: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM contributor_verifications WHERE user_id = :userId")
    fun getVerificationFlow(userId: String): Flow<ContributorVerificationEntity?>

    @Query("SELECT * FROM contributor_verifications WHERE user_id = :userId")
    suspend fun getVerification(userId: String): ContributorVerificationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerification(verification: ContributorVerificationEntity)

    @Query("DELETE FROM user_profiles")
    suspend fun clearProfiles()

    @Query("DELETE FROM contributor_verifications")
    suspend fun clearVerifications()
}
