package com.vidyasetuai.feature_profile.data.repository

import com.vidyasetuai.feature_profile.data.local.datasource.ProfileLocalDataSource
import com.vidyasetuai.feature_profile.data.mapper.toDomain
import com.vidyasetuai.feature_profile.data.mapper.toEntity
import com.vidyasetuai.feature_profile.data.mapper.toDto
import com.vidyasetuai.feature_profile.data.remote.datasource.ProfileRemoteDataSource
import com.vidyasetuai.feature_profile.domain.model.ContributorVerification
import com.vidyasetuai.feature_profile.domain.model.UserProfile
import com.vidyasetuai.feature_profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepositoryImpl(
    private val localDataSource: ProfileLocalDataSource,
    private val remoteDataSource: ProfileRemoteDataSource
) : ProfileRepository {

    override fun getProfileFlow(userId: String): Flow<UserProfile?> {
        return localDataSource.getProfileFlow(userId).map { it?.toDomain() }
    }

    override suspend fun syncProfile(userId: String): Result<Unit> {
        return runCatching {
            val remoteProfileDto = remoteDataSource.getProfile(userId)
            localDataSource.saveProfile(remoteProfileDto.toEntity())
        }.onFailure { e ->
            android.util.Log.e("VidyaSetu_ProfileRepo", "Error syncing profile for user: $userId", e)
        }
    }

    override suspend fun updateProfile(profile: UserProfile): Result<Unit> {
        return runCatching {
            // First save locally to keep UI fast
            localDataSource.saveProfile(profile.toEntity())
            // Sync to Supabase
            remoteDataSource.updateProfile(profile.toDto())
        }.onFailure { e ->
            android.util.Log.e("VidyaSetu_ProfileRepo", "Error updating profile for user: ${profile.userId}", e)
        }
    }

    override suspend fun checkUsernameUnique(username: String, currentUserId: String): Result<Boolean> {
        return runCatching {
            remoteDataSource.checkUsernameUnique(username, currentUserId)
        }.onFailure { e ->
            android.util.Log.e("VidyaSetu_ProfileRepo", "Error checking username uniqueness for: $username", e)
        }
    }

    override fun getVerificationFlow(userId: String): Flow<ContributorVerification?> {
        return localDataSource.getVerificationFlow(userId).map { it?.toDomain() }
    }

    override suspend fun syncVerification(userId: String): Result<Unit> {
        return runCatching {
            val remoteVerificationDto = remoteDataSource.getVerification(userId)
            if (remoteVerificationDto != null) {
                localDataSource.saveVerification(remoteVerificationDto.toEntity())
            }
        }.onFailure { e ->
            android.util.Log.e("VidyaSetu_ProfileRepo", "Error syncing verification for user: $userId", e)
        }
    }

    override suspend fun applyForVerification(userId: String, applicantNote: String): Result<Unit> {
        return runCatching {
            remoteDataSource.applyForVerification(userId, applicantNote)
            // Immediately sync back verification status from Supabase to show as Pending
            syncVerification(userId).getOrThrow()
        }.onFailure { e ->
            android.util.Log.e("VidyaSetu_ProfileRepo", "Error applying for verification for user: $userId", e)
        }
    }

    override suspend fun getProfileById(userId: String): Result<UserProfile> {
        return runCatching {
            val remoteProfileDto = remoteDataSource.getProfile(userId)
            localDataSource.saveProfile(remoteProfileDto.toEntity())
            remoteProfileDto.toDomain()
        }.onFailure { e ->
            android.util.Log.e("VidyaSetu_ProfileRepo", "Error getting profile for user: $userId", e)
        }
    }

    override suspend fun toggleUserInspiration(inspiredUserId: String, inspiringUserId: String): Result<Boolean> {
        return runCatching {
            remoteDataSource.toggleUserInspiration(inspiredUserId, inspiringUserId)
        }.onFailure { e ->
            android.util.Log.e("VidyaSetu_ProfileRepo", "Error toggling user inspiration connection", e)
        }
    }

    override suspend fun getInspiredCount(userId: String): Result<Int> {
        return runCatching {
            remoteDataSource.getInspiredCount(userId)
        }.onFailure { e ->
            android.util.Log.e("VidyaSetu_ProfileRepo", "Error getting inspired count for user: $userId", e)
        }
    }

    override suspend fun getInspiringCount(userId: String): Result<Int> {
        return runCatching {
            remoteDataSource.getInspiringCount(userId)
        }.onFailure { e ->
            android.util.Log.e("VidyaSetu_ProfileRepo", "Error getting inspiring count for user: $userId", e)
        }
    }

    override suspend fun isInspiredBy(inspiredUserId: String, inspiringUserId: String): Result<Boolean> {
        return runCatching {
            remoteDataSource.isInspiredBy(inspiredUserId, inspiringUserId)
        }.onFailure { e ->
            android.util.Log.e("VidyaSetu_ProfileRepo", "Error checking isInspiredBy", e)
        }
    }

    override suspend fun getInspiredUsers(userId: String): Result<List<UserProfile>> {
        return runCatching {
            remoteDataSource.getInspiredUsers(userId).map { it.toDomain() }
        }.onFailure { e ->
            android.util.Log.e("VidyaSetu_ProfileRepo", "Error getting inspired users for: $userId", e)
        }
    }

    override suspend fun getInspiringUsers(userId: String): Result<List<UserProfile>> {
        return runCatching {
            remoteDataSource.getInspiringUsers(userId).map { it.toDomain() }
        }.onFailure { e ->
            android.util.Log.e("VidyaSetu_ProfileRepo", "Error getting inspiring users for: $userId", e)
        }
    }
}