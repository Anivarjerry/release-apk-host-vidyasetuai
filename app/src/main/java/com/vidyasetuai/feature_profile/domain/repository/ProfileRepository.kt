package com.vidyasetuai.feature_profile.domain.repository

import com.vidyasetuai.feature_profile.domain.model.ContributorVerification
import com.vidyasetuai.feature_profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfileFlow(userId: String): Flow<UserProfile?>
    suspend fun syncProfile(userId: String): Result<Unit>
    suspend fun updateProfile(profile: UserProfile): Result<Unit>
    suspend fun checkUsernameUnique(username: String, currentUserId: String): Result<Boolean>
    
    fun getVerificationFlow(userId: String): Flow<ContributorVerification?>
    suspend fun syncVerification(userId: String): Result<Unit>
    suspend fun applyForVerification(userId: String, applicantNote: String): Result<Unit>

    suspend fun getProfileById(userId: String): Result<UserProfile>
    suspend fun toggleUserInspiration(inspiredUserId: String, inspiringUserId: String): Result<Boolean>
    suspend fun getInspiredCount(userId: String): Result<Int>
    suspend fun getInspiringCount(userId: String): Result<Int>
    suspend fun isInspiredBy(inspiredUserId: String, inspiringUserId: String): Result<Boolean>
    suspend fun getInspiredUsers(userId: String): Result<List<UserProfile>>
    suspend fun getInspiringUsers(userId: String): Result<List<UserProfile>>
}