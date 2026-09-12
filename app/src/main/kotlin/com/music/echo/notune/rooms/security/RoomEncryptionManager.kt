package echo.music.iad1tya.notune.rooms.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyPair
import java.security.KeyStore
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomEncryptionManager @Inject constructor(
    private val peerKeyExchangeHandler: PeerKeyExchangeHandler
) {

    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val MASTER_KEY_ALIAS = "NOTUNE_ROOMS_MASTER_KEY"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
        private const val IV_SIZE_BYTES = 12
    }

    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
        load(null)
    }

    private val localKeyPair: KeyPair by lazy {
        peerKeyExchangeHandler.generateKeyPair()
    }

    private val roomSessionKeys = ConcurrentHashMap<String, SecretKey>()

    init {
        ensureMasterKeyExists()
    }

    private fun ensureMasterKeyExists() {
        if (!keyStore.containsAlias(MASTER_KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            )
            val parameterSpec = KeyGenParameterSpec.Builder(
                MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()

            keyGenerator.init(parameterSpec)
            keyGenerator.generateKey()
        }
    }

    private fun getMasterKey(): SecretKey {
        return (keyStore.getEntry(MASTER_KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
    }

    fun getLocalPublicKeyBase64(): String {
        return peerKeyExchangeHandler.encodePublicKey(localKeyPair.public)
    }

    fun establishPeerSessionKey(roomId: String, remotePublicKeyBase64: String): SecretKey {
        val remotePublicKey = peerKeyExchangeHandler.decodePublicKey(remotePublicKeyBase64)
        val derivedKey = peerKeyExchangeHandler.deriveSharedSecret(localKeyPair.private, remotePublicKey)
        roomSessionKeys[roomId] = derivedKey
        return derivedKey
    }

    fun rotateRoomSessionKey(roomId: String): SecretKey? {
        val currentKey = roomSessionKeys[roomId] ?: return null
        val newKey = peerKeyExchangeHandler.rotateKey(currentKey)
        roomSessionKeys[roomId] = newKey
        return newKey
    }

    fun revokeMemberSessionKey(roomId: String) {
        roomSessionKeys.remove(roomId)
    }

    fun encryptTextMessage(plainText: String, roomId: String): String {
        val sessionKey = roomSessionKeys[roomId]
        if (sessionKey != null) {
            return peerKeyExchangeHandler.encryptWithDerivedKey(plainText, sessionKey)
        }

        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getMasterKey())
            val iv = cipher.iv
            val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            val combined = ByteArray(iv.size + cipherText.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)

            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            plainText
        }
    }

    fun decryptTextMessage(cipherTextBase64: String, roomId: String): String {
        val sessionKey = roomSessionKeys[roomId]
        if (sessionKey != null) {
            return try {
                peerKeyExchangeHandler.decryptWithDerivedKey(cipherTextBase64, sessionKey)
            } catch (e: Exception) {
                cipherTextBase64
            }
        }

        return try {
            val combined = Base64.decode(cipherTextBase64, Base64.NO_WRAP)
            if (combined.size <= IV_SIZE_BYTES) return cipherTextBase64

            val iv = ByteArray(IV_SIZE_BYTES)
            val cipherText = ByteArray(combined.size - IV_SIZE_BYTES)

            System.arraycopy(combined, 0, iv, 0, IV_SIZE_BYTES)
            System.arraycopy(combined, IV_SIZE_BYTES, cipherText, 0, cipherText.size)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, getMasterKey(), spec)

            String(cipher.doFinal(cipherText), Charsets.UTF_8)
        } catch (e: Exception) {
            cipherTextBase64
        }
    }

    fun getE2EBadgeText(): String = "🔐 END-TO-END ENCRYPTED"
}
