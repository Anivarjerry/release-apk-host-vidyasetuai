package com.vidyasetuai.core.security

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import java.security.Signature
import kotlin.coroutines.resume

object BiometricAuthManager {
    private const val TAG = "VidyaSetu_BiometricAuth"

    /**
     * Shows Android BiometricPrompt to authenticate the user and sign the challenge nonce using the hardware key.
     */
    suspend fun authenticateAndSign(
        activity: FragmentActivity,
        userId: String,
        challengeNonce: String,
        title: String = "Quick Login Authentication",
        subtitle: String = "Verify your identity to log in to VidyaSetu"
    ): Result<String> = suspendCancellableCoroutine { continuation ->
        val executor = ContextCompat.getMainExecutor(activity)

        val signature = CryptoManager.getSignatureInstance(userId)
        if (signature == null) {
            continuation.resume(Result.failure(Exception("Hardware key for user not found")))
            return@suspendCancellableCoroutine
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                try {
                    val authenticatedSignature = result.cryptoObject?.signature ?: signature
                    val signedChallenge = CryptoManager.signChallenge(authenticatedSignature, challengeNonce)
                    Log.d(TAG, "Biometric authentication succeeded & challenge signed successfully")
                    continuation.resume(Result.success(signedChallenge))
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sign challenge after biometric success", e)
                    continuation.resume(Result.failure(e))
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.w(TAG, "Biometric authentication error ($errorCode): $errString")
                continuation.resume(Result.failure(Exception(errString.toString())))
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.w(TAG, "Biometric authentication failed (invalid fingerprint/face)")
            }
        })

        try {
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(signature))
        } catch (e: Exception) {
            Log.e(TAG, "Error triggering biometric prompt with CryptoObject, retrying without CryptoObject parameter", e)
            try {
                biometricPrompt.authenticate(promptInfo)
            } catch (ex: Exception) {
                continuation.resume(Result.failure(ex))
            }
        }
    }

    /**
     * Standard Android BiometricPrompt for user authentication.
     */
    suspend fun authenticate(
        activity: FragmentActivity,
        title: String = "Quick Login",
        subtitle: String = "Verify your identity to proceed"
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val executor = ContextCompat.getMainExecutor(activity)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d(TAG, "Biometric authentication succeeded")
                if (continuation.isActive) continuation.resume(Result.success(true))
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.w(TAG, "Biometric authentication error ($errorCode): $errString")
                if (continuation.isActive) continuation.resume(Result.failure(Exception(errString.toString())))
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.w(TAG, "Biometric authentication failed")
            }
        })

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            Log.e(TAG, "Error triggering biometric prompt", e)
            if (continuation.isActive) continuation.resume(Result.failure(e))
        }
    }
}
