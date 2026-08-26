package com.vidyasetuai.feature_profile.data.repository

import android.content.Context
import android.util.Log
import com.vidyasetuai.core.auth.SessionManager
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_profile.data.local.ProfileAvatarCacheManager
import com.vidyasetuai.feature_profile.data.local.ProfileDao
import com.vidyasetuai.feature_profile.data.local.ProfileInspirationEntity
import com.vidyasetuai.feature_profile.data.local.ProfileVerificationEntity
import com.vidyasetuai.feature_profile.data.local.UserProfileEntity
import com.vidyasetuai.feature_profile.data.remote.ProfileRemoteDataSource
import com.vidyasetuai.feature_profile.domain.model.ContributorVerification
import com.vidyasetuai.feature_profile.domain.model.ProfileInspiration
import com.vidyasetuai.feature_profile.domain.model.UserProfile
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.io.File

/**
 * Enterprise Profile Repository Implementation.
 * 0ms reactive Room DB Flow reads + Direct safe remote writes with instant local upsert.
 */
class ProfileRepositoryImpl(
    private val context: Context,
    private val profileDao: ProfileDao,
    private val remoteDataSource: ProfileRemoteDataSource,
    private val avatarCacheManager: ProfileAvatarCacheManager
) : ProfileRepository {

    private val sessionManager = SessionManager(context)
    private val backgroundScope = CoroutineScope(Dispatchers.IO)

    override fun getCurrentUserId(): String? {
        return sessionManager.getUserId() ?: SupabaseClient.client.auth.currentUserOrNull()?.id
    }

    override fun getMyProfileFlow(): Flow<UserProfile?> {
        return profileDao.getMyProfileFlow().map { it?.toDomain() }
    }

    override fun getProfileByIdFlow(userId: String): Flow<UserProfile?> {
        return profileDao.getProfileByIdFlow(userId).map { it?.toDomain() }
    }

    override fun getInspirationsFlow(userId: String, type: String): Flow<List<ProfileInspiration>> {
        return profileDao.getInspirationsFlow(userId, type).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getVerificationFlow(userId: String): Flow<ContributorVerification?> {
        return profileDao.getVerificationFlow(userId).map { it?.toDomain() }
    }

    override suspend fun syncProfileData(targetUserId: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val activeUserId = getCurrentUserId() ?: return@withContext Result.failure(Exception("Unauthorized"))
            val queryTargetId = targetUserId ?: activeUserId
            val isMe = (queryTargetId == activeUserId)

            val remoteResult = remoteDataSource.fetchProfilePayload(queryTargetId)
            if (remoteResult.isFailure) {
                return@withContext Result.failure(remoteResult.exceptionOrNull() ?: Exception("Sync failed"))
            }

            val payload = remoteResult.getOrNull() ?: return@withContext Result.failure(Exception("Empty payload"))
            val profileObj = payload["profile"] as? JsonObject

            if (profileObj != null) {
                val pUserId = profileObj["user_id"]?.jsonPrimitive?.contentOrNull ?: queryTargetId
                val existingLocal = profileDao.getProfileById(pUserId)

                val entity = UserProfileEntity(
                    userId = pUserId,
                    username = profileObj["username"]?.jsonPrimitive?.contentOrNull ?: "user",
                    firstName = profileObj["first_name"]?.jsonPrimitive?.contentOrNull,
                    lastName = profileObj["last_name"]?.jsonPrimitive?.contentOrNull,
                    fullName = profileObj["full_name"]?.jsonPrimitive?.contentOrNull ?: "Member",
                    profilePictureUrl = profileObj["profile_picture_url"]?.jsonPrimitive?.contentOrNull,
                    profilePictureLocalPath = existingLocal?.profilePictureLocalPath,
                    coverPhotoUrl = profileObj["cover_photo_url"]?.jsonPrimitive?.contentOrNull,
                    coverPhotoLocalPath = existingLocal?.coverPhotoLocalPath,
                    gender = profileObj["gender"]?.jsonPrimitive?.contentOrNull,
                    dateOfBirth = profileObj["date_of_birth"]?.jsonPrimitive?.contentOrNull,
                    bio = profileObj["bio"]?.jsonPrimitive?.contentOrNull,
                    preferredLanguage = profileObj["preferred_language"]?.jsonPrimitive?.contentOrNull,
                    isPrivate = profileObj["is_private"]?.jsonPrimitive?.booleanOrNull ?: false,
                    isVerified = profileObj["is_verified"]?.jsonPrimitive?.booleanOrNull ?: false,
                    totalInspiringCount = profileObj["total_inspiring_count"]?.jsonPrimitive?.intOrNull ?: 0,
                    totalInspiredCount = profileObj["total_inspired_count"]?.jsonPrimitive?.intOrNull ?: 0,
                    totalCaseStudiesCount = profileObj["total_case_studies_count"]?.jsonPrimitive?.intOrNull ?: 0,
                    isMe = isMe,
                    lastSyncedAt = payload["synced_at"]?.jsonPrimitive?.contentOrNull ?: System.currentTimeMillis().toString()
                )

                profileDao.upsertProfile(entity)

                backgroundScope.launch {
                    entity.profilePictureUrl?.let { avatarCacheManager.cacheAvatar(pUserId, it) }
                    entity.coverPhotoUrl?.let { avatarCacheManager.cacheCoverPhoto(pUserId, it) }
                }
            } else if (isMe) {
                // Fallback default profile for logged-in user if remote is empty
                val existingLocal = profileDao.getMyProfile()
                if (existingLocal == null) {
                    val fallbackName = sessionManager.getUserEmail()?.substringBefore("@") ?: "Member"
                    val defaultEntity = UserProfileEntity(
                        userId = activeUserId,
                        username = fallbackName.lowercase(),
                        fullName = fallbackName,
                        isMe = true,
                        lastSyncedAt = System.currentTimeMillis().toString()
                    )
                    profileDao.upsertProfile(defaultEntity)
                }
            }

            // Sync Following List
            val followingArray = payload["following_list"] as? JsonArray
            if (followingArray != null) {
                val followingEntities = followingArray.mapNotNull { element ->
                    val obj = element as? JsonObject ?: return@mapNotNull null
                    val targetId = obj["target_user_id"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                    ProfileInspirationEntity(
                        id = obj["id"]?.jsonPrimitive?.contentOrNull ?: "insp_${queryTargetId}_$targetId",
                        userId = queryTargetId,
                        targetUserId = targetId,
                        peerName = obj["peer_name"]?.jsonPrimitive?.contentOrNull ?: "Member",
                        peerUsername = obj["peer_username"]?.jsonPrimitive?.contentOrNull ?: "",
                        peerAvatarUrl = obj["peer_avatar_url"]?.jsonPrimitive?.contentOrNull,
                        peerAvatarLocalPath = null,
                        peerBio = obj["peer_bio"]?.jsonPrimitive?.contentOrNull,
                        relationType = "FOLLOWING",
                        isMutual = obj["is_mutual"]?.jsonPrimitive?.booleanOrNull ?: false,
                        isVerified = obj["is_verified"]?.jsonPrimitive?.booleanOrNull ?: false,
                        updatedAt = obj["updated_at"]?.jsonPrimitive?.contentOrNull ?: ""
                    )
                }
                profileDao.upsertInspirations(followingEntities)
            }

            // Sync Followers List
            val followersArray = payload["followers_list"] as? JsonArray
            if (followersArray != null) {
                val followersEntities = followersArray.mapNotNull { element ->
                    val obj = element as? JsonObject ?: return@mapNotNull null
                    val targetId = obj["target_user_id"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                    ProfileInspirationEntity(
                        id = obj["id"]?.jsonPrimitive?.contentOrNull ?: "foll_${queryTargetId}_$targetId",
                        userId = queryTargetId,
                        targetUserId = targetId,
                        peerName = obj["peer_name"]?.jsonPrimitive?.contentOrNull ?: "Member",
                        peerUsername = obj["peer_username"]?.jsonPrimitive?.contentOrNull ?: "",
                        peerAvatarUrl = obj["peer_avatar_url"]?.jsonPrimitive?.contentOrNull,
                        peerAvatarLocalPath = null,
                        peerBio = obj["peer_bio"]?.jsonPrimitive?.contentOrNull,
                        relationType = "FOLLOWER",
                        isMutual = obj["is_mutual"]?.jsonPrimitive?.booleanOrNull ?: false,
                        isVerified = obj["is_verified"]?.jsonPrimitive?.booleanOrNull ?: false,
                        updatedAt = obj["updated_at"]?.jsonPrimitive?.contentOrNull ?: ""
                    )
                }
                profileDao.upsertInspirations(followersEntities)
            }

            // Sync Verification
            val verificationObj = payload["verification"] as? JsonObject
            if (verificationObj != null) {
                val verId = verificationObj["id"]?.jsonPrimitive?.contentOrNull
                if (verId != null) {
                    val verEntity = ProfileVerificationEntity(
                        id = verId,
                        userId = queryTargetId,
                        contributorType = verificationObj["contributor_type"]?.jsonPrimitive?.contentOrNull ?: "GENERAL",
                        status = verificationObj["status"]?.jsonPrimitive?.contentOrNull ?: "PENDING",
                        applicantNote = verificationObj["applicant_note"]?.jsonPrimitive?.contentOrNull,
                        rejectionReason = verificationObj["rejection_reason"]?.jsonPrimitive?.contentOrNull,
                        createdAt = verificationObj["created_at"]?.jsonPrimitive?.contentOrNull ?: ""
                    )
                    profileDao.upsertVerification(verEntity)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProfileRepo", "Sync profile data failed", e)
            Result.failure(e)
        }
    }

    override suspend fun checkUsernameAvailability(username: String): Result<Boolean> = withContext(Dispatchers.IO) {
        remoteDataSource.checkUsernameAvailability(username)
    }

    override suspend fun updateProfile(
        username: String?,
        firstName: String?,
        lastName: String?,
        bio: String?,
        gender: String?,
        dateOfBirth: String?,
        preferredLanguage: String?,
        isPrivate: Boolean?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val activeUserId = getCurrentUserId() ?: return@withContext Result.failure(Exception("Unauthorized"))

            val remoteResult = remoteDataSource.updateProfile(
                username = username,
                firstName = firstName,
                lastName = lastName,
                bio = bio,
                gender = gender,
                dateOfBirth = dateOfBirth,
                preferredLanguage = preferredLanguage,
                profilePictureUrl = null,
                coverPhotoUrl = null,
                isPrivate = isPrivate
            )

            if (remoteResult.isFailure) {
                return@withContext Result.failure(remoteResult.exceptionOrNull() ?: Exception("Update failed"))
            }

            val existing = profileDao.getMyProfile()
            if (existing != null) {
                val updated = existing.copy(
                    username = username ?: existing.username,
                    firstName = firstName ?: existing.firstName,
                    lastName = lastName ?: existing.lastName,
                    fullName = listOfNotNull(firstName ?: existing.firstName, lastName ?: existing.lastName).joinToString(" ").ifEmpty { existing.fullName },
                    bio = bio ?: existing.bio,
                    gender = gender ?: existing.gender,
                    dateOfBirth = dateOfBirth ?: existing.dateOfBirth,
                    preferredLanguage = preferredLanguage ?: existing.preferredLanguage,
                    isPrivate = isPrivate ?: existing.isPrivate
                )
                profileDao.upsertProfile(updated)
            } else {
                syncProfileData(activeUserId)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProfileRepo", "Update profile failed", e)
            Result.failure(e)
        }
    }

    override suspend fun uploadAvatar(imageFile: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            val activeUserId = getCurrentUserId() ?: return@withContext Result.failure(Exception("Unauthorized"))

            // 1. Instant 0ms Local Room DB Update
            val existing = profileDao.getMyProfile()
            if (existing != null) {
                profileDao.upsertProfile(existing.copy(
                    profilePictureLocalPath = imageFile.absolutePath
                ))
            } else {
                val fallbackName = sessionManager.getUserEmail()?.substringBefore("@") ?: "Member"
                profileDao.upsertProfile(UserProfileEntity(
                    userId = activeUserId,
                    username = fallbackName.lowercase(),
                    fullName = fallbackName,
                    profilePictureLocalPath = imageFile.absolutePath,
                    isMe = true,
                    lastSyncedAt = System.currentTimeMillis().toString()
                ))
            }

            // 2. Upload to Supabase Storage "media" Bucket
            val uploadResult = remoteDataSource.uploadProfileImage("media", imageFile, activeUserId)
            
            if (uploadResult.isFailure) {
                return@withContext Result.failure(uploadResult.exceptionOrNull() ?: Exception("Upload avatar failed"))
            }

            val publicUrl = uploadResult.getOrNull() ?: return@withContext Result.failure(Exception("No URL returned"))

            // 3. Update Remote Profile Table
            remoteDataSource.updateProfile(
                username = null,
                firstName = null, lastName = null, bio = null, gender = null,
                dateOfBirth = null, preferredLanguage = null,
                profilePictureUrl = publicUrl, coverPhotoUrl = null, isPrivate = null
            )

            // 4. Save Public URL in Room DB and Cache Manager
            val updated = profileDao.getMyProfile()
            if (updated != null) {
                profileDao.upsertProfile(updated.copy(
                    profilePictureUrl = publicUrl,
                    profilePictureLocalPath = imageFile.absolutePath
                ))
            }
            avatarCacheManager.cacheAvatar(activeUserId, publicUrl)

            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e("ProfileRepo", "Upload avatar failed", e)
            Result.failure(e)
        }
    }

    override suspend fun uploadCoverPhoto(imageFile: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            val activeUserId = getCurrentUserId() ?: return@withContext Result.failure(Exception("Unauthorized"))

            // 1. Instant 0ms Local Room DB Update
            val existing = profileDao.getMyProfile()
            if (existing != null) {
                profileDao.upsertProfile(existing.copy(
                    coverPhotoLocalPath = imageFile.absolutePath
                ))
            } else {
                val fallbackName = sessionManager.getUserEmail()?.substringBefore("@") ?: "Member"
                profileDao.upsertProfile(UserProfileEntity(
                    userId = activeUserId,
                    username = fallbackName.lowercase(),
                    fullName = fallbackName,
                    coverPhotoLocalPath = imageFile.absolutePath,
                    isMe = true,
                    lastSyncedAt = System.currentTimeMillis().toString()
                ))
            }

            // 2. Upload to Supabase Storage "media" Bucket
            val uploadResult = remoteDataSource.uploadProfileImage("media", imageFile, activeUserId)

            if (uploadResult.isFailure) {
                return@withContext Result.failure(uploadResult.exceptionOrNull() ?: Exception("Upload cover failed"))
            }

            val publicUrl = uploadResult.getOrNull() ?: return@withContext Result.failure(Exception("No URL returned"))

            // 3. Update Remote Profile Table
            remoteDataSource.updateProfile(
                username = null,
                firstName = null, lastName = null, bio = null, gender = null,
                dateOfBirth = null, preferredLanguage = null,
                profilePictureUrl = null, coverPhotoUrl = publicUrl, isPrivate = null
            )

            // 4. Save Public URL in Room DB and Cache Manager
            val updated = profileDao.getMyProfile()
            if (updated != null) {
                profileDao.upsertProfile(updated.copy(
                    coverPhotoUrl = publicUrl,
                    coverPhotoLocalPath = imageFile.absolutePath
                ))
            }
            avatarCacheManager.cacheCoverPhoto(activeUserId, publicUrl)

            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e("ProfileRepo", "Upload cover failed", e)
            Result.failure(e)
        }
    }

    override suspend fun clearLocalData() {
        withContext(Dispatchers.IO) {
            try {
                profileDao.clearAllProfiles()
                profileDao.clearAllInspirations()
                profileDao.clearAllVerifications()
                avatarCacheManager.clearMediaCache()
                Log.d("ProfileRepo", "Local profile data wiped successfully.")
            } catch (e: Exception) {
                Log.e("ProfileRepo", "Failed to clear local profile data", e)
            }
        }
    }
}