package com.vidyasetuai.feature_profile.data.repository

import com.vidyasetuai.feature_profile.domain.model.ContributorVerification
import com.vidyasetuai.feature_profile.domain.model.ProfileInspiration
import com.vidyasetuai.feature_profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Enterprise Repository Interface for Profile Module.
 * Standardized 0ms reactive reads from Room DB + direct remote writes with instant local upsert.
 */
interface ProfileRepository {

    fun getCurrentUserId(): String?
    fun getMyProfileFlow(): Flow<UserProfile?>
    fun getProfileByIdFlow(userId: String): Flow<UserProfile?>
    fun getInspirationsFlow(userId: String, type: String): Flow<List<ProfileInspiration>>
    fun getVerificationFlow(userId: String): Flow<ContributorVerification?>

    suspend fun syncProfileData(targetUserId: String? = null): Result<Unit>

    suspend fun checkUsernameAvailability(username: String): Result<Boolean>

    suspend fun updateProfile(
        username: String?,
        firstName: String?,
        lastName: String?,
        bio: String?,
        gender: String?,
        dateOfBirth: String?,
        preferredLanguage: String?,
        isPrivate: Boolean?
    ): Result<Unit>

    suspend fun uploadAvatar(imageFile: File): Result<String>
    suspend fun uploadCoverPhoto(imageFile: File): Result<String>

    suspend fun clearLocalData()
}
