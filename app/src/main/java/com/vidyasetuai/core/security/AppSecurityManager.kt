package com.vidyasetuai.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest

class AppSecurityManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "vidyasetu_secure_security_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_PIN_LENGTH = "pin_length"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_AUTO_LOCK_TIMEOUT = "auto_lock_timeout" // In seconds (0 = Immediately, 30, 60, 300)
        private const val KEY_LAST_BACKGROUND_TIME = "last_background_time"
        private const val SALT = "VidyaSetu_Secure_Salt_2026"
    }

    fun isAppLockEnabled(): Boolean = prefs.getBoolean(KEY_APP_LOCK_ENABLED, false)

    fun setAppLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_APP_LOCK_ENABLED, enabled).apply()
    }

    fun isBiometricEnabled(): Boolean = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun getPinLength(): Int = prefs.getInt(KEY_PIN_LENGTH, 4)

    fun hasPin(): Boolean = !prefs.getString(KEY_PIN_HASH, null).isNullOrEmpty()

    fun setPin(pin: String) {
        val hashed = hashPin(pin)
        prefs.edit()
            .putString(KEY_PIN_HASH, hashed)
            .putInt(KEY_PIN_LENGTH, pin.length)
            .apply()
    }

    fun verifyPin(inputPin: String): Boolean {
        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        return storedHash == hashPin(inputPin)
    }

    fun getAutoLockTimeoutSeconds(): Long = prefs.getLong(KEY_AUTO_LOCK_TIMEOUT, 0L)

    fun setAutoLockTimeoutSeconds(seconds: Long) {
        prefs.edit().putLong(KEY_AUTO_LOCK_TIMEOUT, seconds).apply()
    }

    fun recordBackgroundTimestamp() {
        prefs.edit().putLong(KEY_LAST_BACKGROUND_TIME, System.currentTimeMillis()).apply()
    }

    fun shouldTriggerLock(): Boolean {
        if (!isAppLockEnabled()) return false
        val lastTime = prefs.getLong(KEY_LAST_BACKGROUND_TIME, 0L)
        if (lastTime == 0L) return true

        val timeoutMillis = getAutoLockTimeoutSeconds() * 1000L
        val elapsedTime = System.currentTimeMillis() - lastTime
        return elapsedTime >= timeoutMillis
    }

    fun isBiometricHardwareAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    private fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest((SALT + pin).toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
