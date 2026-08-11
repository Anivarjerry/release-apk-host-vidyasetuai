package com.vidyasetuai.core.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.UUID

class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "vidyasetu_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_TRUSTED_ACCOUNTS_JSON = "trusted_accounts_json"
    }

    /**
     * Get or generate a unique persistent device ID.
     */
    fun getDeviceId(): String {
        var deviceId = sharedPreferences.getString(KEY_DEVICE_ID, null)
        if (deviceId.isNullOrEmpty()) {
            deviceId = UUID.randomUUID().toString()
            sharedPreferences.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }
        return deviceId
    }

    fun saveSession(userId: String, email: String, accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    fun updateTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    fun getAccessToken(): String? = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)

    fun getUserId(): String? = sharedPreferences.getString(KEY_USER_ID, null)

    fun getUserEmail(): String? = sharedPreferences.getString(KEY_USER_EMAIL, null)

    fun saveFcmToken(token: String) {
        sharedPreferences.edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    fun getFcmToken(): String? = sharedPreferences.getString(KEY_FCM_TOKEN, null)

    fun hasActiveSession(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }

    /**
     * Save/Update a trusted account in local EncryptedSharedPreferences.
     */
    fun saveTrustedAccount(email: String, userId: String, password: String = "", refreshToken: String = "") {
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()
        val currentAccounts = getTrustedAccounts().toMutableList()
        val existingAccount = currentAccounts.find { it.email.equals(cleanEmail, ignoreCase = true) }
        val finalPassword = cleanPassword.ifEmpty { existingAccount?.password ?: "" }
        val existingToken = refreshToken.ifEmpty { getRefreshToken() ?: "" }

        android.util.Log.d("VidyaSetu_Session", "saveTrustedAccount: email='$cleanEmail', userId='$userId', passLength=${finalPassword.length}")

        currentAccounts.removeAll { it.email.equals(cleanEmail, ignoreCase = true) }
        currentAccounts.add(
            com.vidyasetuai.feature_auth.data.remote.dto.TrustedAccountInfo(
                email = cleanEmail,
                userId = userId,
                password = finalPassword,
                refreshToken = existingToken
            )
        )

        val json = kotlinx.serialization.json.Json.encodeToString(
            kotlinx.serialization.builtins.ListSerializer(com.vidyasetuai.feature_auth.data.remote.dto.TrustedAccountInfo.serializer()),
            currentAccounts
        )
        sharedPreferences.edit().putString(KEY_TRUSTED_ACCOUNTS_JSON, json).apply()
    }

    /**
     * Get all trusted accounts registered on this device.
     */
    fun getTrustedAccounts(): List<com.vidyasetuai.feature_auth.data.remote.dto.TrustedAccountInfo> {
        val json = sharedPreferences.getString(KEY_TRUSTED_ACCOUNTS_JSON, null) ?: return emptyList()
        return try {
            kotlinx.serialization.json.Json.decodeFromString(
                kotlinx.serialization.builtins.ListSerializer(com.vidyasetuai.feature_auth.data.remote.dto.TrustedAccountInfo.serializer()),
                json
            )
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Remove a trusted account from this device locally.
     */
    fun removeTrustedAccount(email: String) {
        val currentAccounts = getTrustedAccounts().toMutableList()
        currentAccounts.removeAll { it.email.equals(email, ignoreCase = true) }
        val json = kotlinx.serialization.json.Json.encodeToString(
            kotlinx.serialization.builtins.ListSerializer(com.vidyasetuai.feature_auth.data.remote.dto.TrustedAccountInfo.serializer()),
            currentAccounts
        )
        sharedPreferences.edit().putString(KEY_TRUSTED_ACCOUNTS_JSON, json).apply()
    }

    fun clearSession() {
        sharedPreferences.edit().apply {
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            apply()
        }
    }
}