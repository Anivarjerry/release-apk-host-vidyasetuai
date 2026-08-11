package com.vidyasetuai.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Production-Level Cryptographic Manager for VidyaSetu AI.
 * Handles both:
 * 1. Hardware Biometric & Trusted Device RSA/EC Signing (KeyPair generation, challenge signing, PEM export).
 * 2. End-to-End Encryption (E2EE) for Private Campus (ECDH key agreement & AES-256-GCM symmetric encryption).
 */
object CryptoManager {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val DEFAULT_E2EE_ALIAS = "vidyasetu_e2ee_keypair"
    private const val AES_GCM_TAG_LENGTH = 128

    init {
        ensureKeyPairExists()
    }

    // ── 1. Biometric & Trusted Device Hardware Key Signing Methods ───────────────────────

    /**
     * Generates or retrieves an EC KeyPair for a given alias (e.g. userId) in Android KeyStore.
     */
    fun generateOrGetKeyPair(alias: String = "trusted_device_key"): KeyPair? {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            if (!keyStore.containsAlias(alias)) {
                val keyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC,
                    ANDROID_KEYSTORE
                )
                val parameterSpec = KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                )
                    .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setUserAuthenticationRequired(false)
                    .build()

                keyPairGenerator.initialize(parameterSpec)
                keyPairGenerator.generateKeyPair()
            }

            val entry = keyStore.getEntry(alias, null) as? KeyStore.PrivateKeyEntry
            if (entry != null) {
                KeyPair(entry.certificate.publicKey, entry.privateKey)
            } else null
        } catch (e: Exception) {
            android.util.Log.e("CryptoManager", "Error generating or getting KeyPair for alias $alias", e)
            null
        }
    }

    /**
     * Returns PEM formatted public key string for trusted device registration.
     */
    fun getPublicKeyPem(alias: String = "trusted_device_key"): String? {
        return try {
            val keyPair = generateOrGetKeyPair(alias) ?: return null
            val encoded = Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP)
            "-----BEGIN PUBLIC KEY-----\n$encoded\n-----END PUBLIC KEY-----"
        } catch (e: Exception) {
            android.util.Log.e("CryptoManager", "Error getting PEM public key for alias $alias", e)
            null
        }
    }

    /**
     * Initializes a java.security.Signature instance with private key for BiometricPrompt or challenge signing.
     */
    fun getSignatureInstance(alias: String = "trusted_device_key"): Signature? {
        return try {
            val keyPair = generateOrGetKeyPair(alias) ?: return null
            val signature = Signature.getInstance("SHA256withECDSA")
            signature.initSign(keyPair.private)
            signature
        } catch (e: Exception) {
            android.util.Log.e("CryptoManager", "Error getting Signature instance for alias $alias", e)
            null
        }
    }

    /**
     * Signs a challenge nonce using the initialized Signature object or generates signature.
     */
    fun signChallenge(signature: Signature, challenge: String): String {
        return try {
            signature.update(challenge.toByteArray(Charsets.UTF_8))
            val signatureBytes = signature.sign()
            Base64.encodeToString(signatureBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            android.util.Log.e("CryptoManager", "Error signing challenge", e)
            ""
        }
    }

    /**
     * Deletes KeyPair entry from Android KeyStore for device revocation.
     */
    fun deleteKeyPair(alias: String = "trusted_device_key") {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            if (keyStore.containsAlias(alias)) {
                keyStore.deleteEntry(alias)
            }
        } catch (e: Exception) {
            android.util.Log.e("CryptoManager", "Error deleting KeyPair for alias $alias", e)
        }
    }

    // ── 2. End-to-End Encryption (E2EE) Private Campus Messaging Methods ─────────────────

    /**
     * Ensures an EC KeyPair exists inside hardware-backed Android KeyStore for E2EE chats.
     */
    fun ensureKeyPairExists() {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            if (!keyStore.containsAlias(DEFAULT_E2EE_ALIAS)) {
                val keyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC,
                    ANDROID_KEYSTORE
                )
                val parameterSpec = KeyGenParameterSpec.Builder(
                    DEFAULT_E2EE_ALIAS,
                    KeyProperties.PURPOSE_AGREE_KEY or KeyProperties.PURPOSE_SIGN
                )
                    .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setUserAuthenticationRequired(false)
                    .build()

                keyPairGenerator.initialize(parameterSpec)
                keyPairGenerator.generateKeyPair()
            }
        } catch (e: Exception) {
            android.util.Log.e("CryptoManager", "Error ensuring EC KeyPair in KeyStore", e)
        }
    }

    /**
     * Returns Base64 encoded string of device's Public Key for E2EE.
     */
    fun getMyPublicKeyBase64(): String? {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            val certificate = keyStore.getCertificate(DEFAULT_E2EE_ALIAS) ?: return null
            Base64.encodeToString(certificate.publicKey.encoded, Base64.NO_WRAP)
        } catch (e: Exception) {
            android.util.Log.e("CryptoManager", "Failed to retrieve public key", e)
            null
        }
    }

    /**
     * Gets E2EE PrivateKey reference from KeyStore.
     */
    private fun getMyPrivateKey(): PrivateKey? {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            val entry = keyStore.getEntry(DEFAULT_E2EE_ALIAS, null) as? KeyStore.PrivateKeyEntry
            entry?.privateKey
        } catch (e: Exception) {
            android.util.Log.e("CryptoManager", "Failed to retrieve private key", e)
            null
        }
    }

    /**
     * Decodes Base64 X509 PublicKey.
     */
    fun decodePublicKey(base64PublicKey: String): PublicKey? {
        return try {
            val cleanKey = base64PublicKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\n", "")
                .trim()
            val decodedBytes = Base64.decode(cleanKey, Base64.NO_WRAP)
            val keySpec = X509EncodedKeySpec(decodedBytes)
            val keyFactory = KeyFactory.getInstance("EC")
            keyFactory.generatePublic(keySpec)
        } catch (e: Exception) {
            android.util.Log.e("CryptoManager", "Failed to decode public key Base64", e)
            null
        }
    }

    /**
     * Derives an AES-256 SecretKey using ECDH between local Private Key and peer's Public Key.
     */
    private fun deriveSharedSecretKey(peerPublicKey: PublicKey): SecretKey? {
        val myPrivateKey = getMyPrivateKey() ?: return null
        return try {
            val keyAgreement = KeyAgreement.getInstance("ECDH")
            keyAgreement.init(myPrivateKey)
            keyAgreement.doPhase(peerPublicKey, true)
            val secret = keyAgreement.generateSecret()
            
            val messageDigest = java.security.MessageDigest.getInstance("SHA-256")
            val aesKeyBytes = messageDigest.digest(secret)
            SecretKeySpec(aesKeyBytes, "AES")
        } catch (e: Exception) {
            android.util.Log.e("CryptoManager", "Failed to derive ECDH shared secret key", e)
            null
        }
    }

    /**
     * Encrypts plaintext message text for a peer recipient.
     */
    fun encryptMessage(plainText: String, peerPublicKeyBase64: String?): String {
        if (peerPublicKeyBase64.isNullOrEmpty() || plainText.isEmpty()) {
            return plainText
        }
        val peerPublicKey = decodePublicKey(peerPublicKeyBase64) ?: return plainText
        val sharedKey = deriveSharedSecretKey(peerPublicKey) ?: return plainText

        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, sharedKey)
            val iv = cipher.iv
            val cipherTextBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
            val cipherTextBase64 = Base64.encodeToString(cipherTextBytes, Base64.NO_WRAP)
            "ENC:$ivBase64:$cipherTextBase64"
        } catch (e: Exception) {
            android.util.Log.e("CryptoManager", "Error encrypting message", e)
            plainText
        }
    }

    /**
     * Decrypts encrypted message ciphertext payload using peer's Public Key.
     */
    fun decryptMessage(encryptedPayload: String, peerPublicKeyBase64: String?): String {
        if (!encryptedPayload.startsWith("ENC:") || peerPublicKeyBase64.isNullOrEmpty()) {
            return encryptedPayload
        }
        val parts = encryptedPayload.split(":")
        if (parts.size != 3) return encryptedPayload

        val ivBase64 = parts[1]
        val cipherTextBase64 = parts[2]

        val peerPublicKey = decodePublicKey(peerPublicKeyBase64) ?: return encryptedPayload
        val sharedKey = deriveSharedSecretKey(peerPublicKey) ?: return encryptedPayload

        return try {
            val iv = Base64.decode(ivBase64, Base64.NO_WRAP)
            val cipherTextBytes = Base64.decode(cipherTextBase64, Base64.NO_WRAP)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = GCMParameterSpec(AES_GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, sharedKey, gcmSpec)

            val plainTextBytes = cipher.doFinal(cipherTextBytes)
            String(plainTextBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            android.util.Log.e("CryptoManager", "Error decrypting message", e)
            encryptedPayload
        }
    }
}
