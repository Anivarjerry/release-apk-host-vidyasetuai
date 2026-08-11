package com.vidyasetuai.feature_auth.data.repository

import android.os.Build
import android.util.Log
import com.vidyasetuai.core.auth.SessionManager
import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.core.security.CryptoManager
import com.vidyasetuai.feature_auth.data.remote.dto.ChallengeRequestDto
import com.vidyasetuai.feature_auth.data.remote.dto.ChallengeResponseDto
import com.vidyasetuai.feature_auth.data.remote.dto.TrustedDeviceDto
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@kotlinx.serialization.Serializable
private data class TrustedUserMappingDto(val id: String, val email: String = "")

class TrustedDeviceRepository(
    private val sessionManager: SessionManager
) {
    private companion object {
        private const val TAG = "VidyaSetu_TrustedRepo"
    }

    /**
     * Registers or updates the Public Key for this device in Supabase database.
     */
    suspend fun registerDevicePublicKey(
        userId: String,
        userEmail: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            var effectiveUserId = userId.ifEmpty { sessionManager.getUserId() ?: "" }
            var effectiveEmail = userEmail.ifEmpty { sessionManager.getUserEmail() ?: "" }

            if (effectiveUserId.isEmpty()) {
                val currentSession = SupabaseClient.client.auth.currentSessionOrNull()
                val authId = currentSession?.user?.id
                if (authId != null) {
                    val userResponse = SupabaseClient.client.from("users")
                        .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("id, email")) {
                            filter { eq("auth_id", authId) }
                        }.decodeSingleOrNull<TrustedUserMappingDto>()
                    if (userResponse != null) {
                        effectiveUserId = userResponse.id
                        effectiveEmail = userResponse.email
                        sessionManager.saveSession(
                            userId = effectiveUserId,
                            email = effectiveEmail,
                            accessToken = currentSession.accessToken,
                            refreshToken = currentSession.refreshToken ?: ""
                        )
                    }
                }
            }

            if (effectiveUserId.isEmpty()) {
                return@withContext Result.failure(Exception("Active User ID not found. Please log in again."))
            }

            val deviceId = sessionManager.getDeviceId()
            val publicKeyPem = CryptoManager.getPublicKeyPem(effectiveUserId)
                ?: return@withContext Result.failure(Exception("Failed to generate Android Hardware Public Key"))

            val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"

            val dto = TrustedDeviceDto(
                userId = effectiveUserId,
                deviceId = deviceId,
                deviceName = deviceName,
                publicKeyPem = publicKeyPem,
                isTrusted = true
            )

            SupabaseClient.client.from("user_trusted_devices").upsert(dto) {
                onConflict = "user_id,device_id"
            }

            val currentRefreshToken = sessionManager.getRefreshToken() ?: ""
            sessionManager.saveTrustedAccount(email = effectiveEmail, userId = effectiveUserId, refreshToken = currentRefreshToken)
            Log.d(TAG, "Device public key registered successfully in Supabase for user: $effectiveUserId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register device public key", e)
            Result.failure(e)
        }
    }

    /**
     * Calls Supabase RPC `request_trusted_device_challenge` to fetch a cryptographic challenge nonce.
     */
    suspend fun requestChallenge(
        userEmail: String
    ): Result<ChallengeResponseDto> = withContext(Dispatchers.IO) {
        try {
            val deviceId = sessionManager.getDeviceId()
            val requestDto = ChallengeRequestDto(deviceId = deviceId, userEmail = userEmail)

            val response = SupabaseClient.client.postgrest.rpc(
                function = "request_trusted_device_challenge",
                parameters = requestDto
            ).decodeAs<ChallengeResponseDto>()

            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ?: "Device is not trusted"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "RPC request_trusted_device_challenge failed", e)
            Result.failure(e)
        }
    }

    /**
     * Calls Supabase RPC `verify_trusted_device_signature` to verify cryptographic signature.
     */
    suspend fun verifySignature(
        userEmail: String,
        nonce: String,
        signature: String
    ): Result<com.vidyasetuai.feature_auth.data.remote.dto.VerifySignatureResponseDto> = withContext(Dispatchers.IO) {
        try {
            val deviceId = sessionManager.getDeviceId()
            val requestDto = com.vidyasetuai.feature_auth.data.remote.dto.VerifySignatureRequestDto(
                userEmail = userEmail,
                deviceId = deviceId,
                nonce = nonce,
                signature = signature
            )

            val response = SupabaseClient.client.postgrest.rpc(
                function = "verify_trusted_device_signature",
                parameters = requestDto
            ).decodeAs<com.vidyasetuai.feature_auth.data.remote.dto.VerifySignatureResponseDto>()

            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ?: "Biometric signature verification failed"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "RPC verify_trusted_device_signature failed", e)
            Result.failure(e)
        }
    }

    /**
     * Authenticates a trusted device using hardware RSA challenge signature when password is empty.
     * 1. Requests a challenge nonce from Supabase RPC request_trusted_device_challenge.
     * 2. Signs the challenge nonce using the device's hardware Private Key.
     * 3. Verifies the signature via RPC verify_trusted_device_signature.
     * 4. Returns the single-use recovery OTP code.
     */
    suspend fun authenticateWithHardwareSignature(
        userEmail: String,
        userId: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 1. Request challenge nonce
            var challengeRes = requestChallenge(userEmail).getOrNull()
            if (challengeRes == null || !challengeRes.success) {
                // Device might not be registered yet -> auto-register and retry
                registerDevicePublicKey(userId, userEmail)
                challengeRes = requestChallenge(userEmail).getOrElse { throw it }
            }

            val nonce = challengeRes.nonce ?: throw Exception("Challenge nonce missing")

            // 2. Sign challenge nonce using device hardware key
            var signatureInstance = CryptoManager.getSignatureInstance(userId)
            if (signatureInstance == null) {
                registerDevicePublicKey(userId, userEmail)
                signatureInstance = CryptoManager.getSignatureInstance(userId)
                    ?: return@withContext Result.failure(Exception("Hardware security key failed to initialize"))
            }
            val signature = CryptoManager.signChallenge(signatureInstance, nonce)

            // 3. Verify signature via RPC
            var verifyRes = verifySignature(userEmail, nonce, signature).getOrNull()
            if (verifyRes == null || !verifyRes.success) {
                // Re-register public key and retry signature once if key mismatched
                registerDevicePublicKey(userId, userEmail)
                val newChallenge = requestChallenge(userEmail).getOrElse { throw it }
                val newNonce = newChallenge.nonce ?: throw Exception("Challenge nonce missing")
                val newSigInstance = CryptoManager.getSignatureInstance(userId)
                    ?: return@withContext Result.failure(Exception("Hardware security key re-initialization failed"))
                val newSig = CryptoManager.signChallenge(newSigInstance, newNonce)
                verifyRes = verifySignature(userEmail, newNonce, newSig).getOrElse { throw it }
            }

            val otpCode = verifyRes.otpCode ?: throw Exception("OTP code missing in verification response")
            Result.success(otpCode)
        } catch (e: Exception) {
            Log.e(TAG, "Hardware signature authentication failed: ${e.message}", e)
            Result.failure(e)
        }
    }


    /**
     * Revokes device trust by removing public key from Supabase & clearing hardware key on phone.
     */
    suspend fun revokeDeviceTrust(
        userId: String,
        userEmail: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val deviceId = sessionManager.getDeviceId()

            SupabaseClient.client.from("user_trusted_devices").delete {
                filter {
                    eq("user_id", userId)
                    eq("device_id", deviceId)
                }
            }

            CryptoManager.deleteKeyPair(userId)
            sessionManager.removeTrustedAccount(userEmail)
            Log.d(TAG, "Successfully revoked device trust for user $userId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to revoke device trust", e)
            Result.failure(e)
        }
    }

    /**
     * Gets list of registered trusted devices for the logged in user.
     */
    suspend fun getTrustedDevicesForUser(userId: String): Result<List<TrustedDeviceDto>> = withContext(Dispatchers.IO) {
        try {
            val devices = SupabaseClient.client.from("user_trusted_devices")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }.decodeList<TrustedDeviceDto>()
            Result.success(devices)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
