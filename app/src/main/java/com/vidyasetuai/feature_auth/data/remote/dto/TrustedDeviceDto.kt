package com.vidyasetuai.feature_auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrustedDeviceDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("device_id") val deviceId: String,
    @SerialName("device_name") val deviceName: String? = null,
    @SerialName("public_key_pem") val publicKeyPem: String,
    @SerialName("is_trusted") val isTrusted: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("last_used_at") val lastUsedAt: String? = null
)

@Serializable
data class ChallengeRequestDto(
    @SerialName("p_device_id") val deviceId: String,
    @SerialName("p_user_email") val userEmail: String
)

@Serializable
data class ChallengeResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("nonce") val nonce: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("message") val message: String? = null
)

@Serializable
data class VerifySignatureRequestDto(
    @SerialName("p_user_email") val userEmail: String,
    @SerialName("p_device_id") val deviceId: String,
    @SerialName("p_nonce") val nonce: String,
    @SerialName("p_signature") val signature: String
)

@Serializable
data class VerifySignatureResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("auth_id") val authId: String? = null,
    @SerialName("user_email") val userEmail: String? = null,
    @SerialName("otp_code") val otpCode: String? = null,
    @SerialName("message") val message: String? = null
)


@Serializable
data class TrustedAccountInfo(
    val email: String,
    val userId: String,
    val password: String = "",
    val refreshToken: String = "",
    val isTrusted: Boolean = true,
    val lastLoggedInAt: Long = System.currentTimeMillis()
)
