package com.music.echo.notune.privacy.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Keystore-backed secure storage manager for NØTUNE.
 * Isolates sensitive credentials (OAuth tokens, refresh tokens, session secrets, room keys)
 * into Android KeyStore with AES-256-GCM encryption.
 */
@Singleton
class SecureStorageManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val securityHardening: SecurityHardening
) {
    private val keyAlias = "notune_master_keystore_key"
    private val keyStoreType = "AndroidKeyStore"
    private val prefsName = "notune_secure_prefs"

    private val sharedPreferences by lazy {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    init {
        ensureMasterKeyExists()
    }

    @Synchronized
    private fun ensureMasterKeyExists() {
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null)

        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                keyStoreType
            )
            val builder = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                setKeySize(256)
            }
            keyGenerator.init(builder.build())
            keyGenerator.generateKey()
        }
    }

    private fun getMasterKey(): SecretKey {
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null)
        val entry = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
            ?: throw IllegalStateException("No SecretKey entry found for alias: $keyAlias")
        return entry.secretKey
    }

    /**
     * Stores a secret securely in KeyStore-encrypted SharedPreferences with AAD context binding.
     */
    fun storeSecret(key: String, secretValue: String) {
        if (secretValue.isEmpty()) {
            sharedPreferences.edit().remove(key).apply()
            return
        }
        val secretKey = getMasterKey()
        val payload = securityHardening.encryptAesGcmWithAad(
            plaintext = secretValue.toByteArray(Charsets.UTF_8),
            key = secretKey,
            aadContext = "NOTUNE_SECURE_STORAGE:$key"
        )

        val ivBase64 = Base64.encodeToString(payload.iv, Base64.NO_WRAP)
        val ciphertextBase64 = Base64.encodeToString(payload.ciphertext, Base64.NO_WRAP)
        val serializedPayload = "$ivBase64:$ciphertextBase64"

        sharedPreferences.edit().putString(key, serializedPayload).apply()
    }

    /**
     * Retrieves a secret, validating AAD context. Returns null if missing or corrupted.
     */
    fun getSecret(key: String): String? {
        val serializedPayload = sharedPreferences.getString(key, null) ?: return null
        val parts = serializedPayload.split(":")
        if (parts.size != 2) return null

        return try {
            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val ciphertext = Base64.decode(parts[1], Base64.NO_WRAP)
            val payload = GcmEncryptedPayload(
                iv = iv,
                ciphertext = ciphertext,
                aadContext = "NOTUNE_SECURE_STORAGE:$key"
            )
            val secretKey = getMasterKey()
            val plaintext = securityHardening.decryptAesGcmWithAad(payload, secretKey)
            String(plaintext, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Clears a specific stored secret.
     */
    fun removeSecret(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    /**
     * Clears all stored secure credentials.
     */
    fun clearAllSecrets() {
        sharedPreferences.edit().clear().apply()
    }
}
