package com.vidyasetuai.feature_profile.data.local.datasource

import com.vidyasetuai.feature_profile.data.local.dao.UserProfileDao
import com.vidyasetuai.feature_profile.data.local.entity.ContributorVerificationEntity
import com.vidyasetuai.feature_profile.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

class ProfileLocalDataSource(private val dao: UserProfileDao) {

    fun getProfileFlow(userId: String): Flow<UserProfileEntity?> {
        return dao.getProfileFlow(userId)
    }

    suspend fun getProfile(userId: String): UserProfileEntity? {
        return dao.getProfile(userId)
    }

    suspend fun saveProfile(profile: UserProfileEntity) {
        dao.insertProfile(profile)
    }

    fun getVerificationFlow(userId: String): Flow<ContributorVerificationEntity?> {
        return dao.getVerificationFlow(userId)
    }

    suspend fun saveVerification(verification: ContributorVerificationEntity) {
        dao.insertVerification(verification)
    }

    suspend fun getVerification(userId: String): ContributorVerificationEntity? {
        return dao.getVerification(userId)
    }
}