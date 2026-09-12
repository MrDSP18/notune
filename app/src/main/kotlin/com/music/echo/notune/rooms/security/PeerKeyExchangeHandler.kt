package echo.music.iad1tya.notune.rooms.security

import java.util.Base64
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * P0-4: Application-level Replay Protection Filter.
 * Tracks processed message IDs and enforces monotonically increasing sequence numbers per session.
 */
class ReplayProtectionFilter {
    private val processedMessageIds = ConcurrentHashMap.newKeySet<String>()
    private val lastSequenceNumbers = ConcurrentHashMap<String, Long>()

    fun isMessageReplayed(roomId: String, messageId: String, sequenceNumber: Long): Boolean {
        if (!processedMessageIds.add(messageId)) {
            return true // Duplicate message ID
        }

        val lastSeq = lastSequenceNumbers[roomId] ?: 0L
        if (sequenceNumber <= lastSeq && lastSeq > 0) {
            return true // Replayed sequence counter
        }

        lastSequenceNumbers[roomId] = sequenceNumber
        return false
    }

    fun resetSession(roomId: String) {
        lastSequenceNumbers.remove(roomId)
    }
}

@Singleton
class PeerKeyExchangeHandler @Inject constructor() {

    private val replayFilter = ReplayProtectionFilter()

    companion object {
        private const val ALGORITHM_EC = "EC"
        private const val CURVE_NAME = "secp256r1"
        private const val KEY_AGREEMENT_ECDH = "ECDH"
        private const val SIGNATURE_ALGORITHM = "SHA256withECDSA"
        private const val HMAC_ALGORITHM = "HmacSHA256"
        private const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
        private const val IV_SIZE_BYTES = 12
        private const val AES_KEY_SIZE_BYTES = 32
    }

    fun getReplayFilter(): ReplayProtectionFilter = replayFilter

    /**
     * Generates an EC Curve P-256 KeyPair for ephemeral or identity key exchange.
     */
    fun generateKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_EC)
        val ecSpec = ECGenParameterSpec(CURVE_NAME)
        keyPairGenerator.initialize(ecSpec, SecureRandom())
        return keyPairGenerator.generateKeyPair()
    }

    fun encodePublicKey(publicKey: PublicKey): String {
        return Base64.getEncoder().encodeToString(publicKey.encoded)
    }

    fun decodePublicKey(base64PublicKey: String): PublicKey {
        val keyBytes = Base64.getDecoder().decode(base64PublicKey)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(ALGORITHM_EC)
        return keyFactory.generatePublic(keySpec)
    }

    /**
     * P0-1: Digital signature signing of public key using persistent Identity Private Key to prevent MITM key substitution.
     */
    fun signPublicKey(publicKeyToSign: PublicKey, identityPrivateKey: PrivateKey): String {
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initSign(identityPrivateKey)
        signature.update(publicKeyToSign.encoded)
        return Base64.getEncoder().encodeToString(signature.sign())
    }

    /**
     * P0-1: Verify that an incoming public key was signed by the peer's authentic Identity Public Key.
     */
    fun verifyPublicKeySignature(
        publicKeyToVerify: PublicKey,
        signatureBase64: String,
        peerIdentityPublicKey: PublicKey
    ): Boolean {
        return try {
            val signatureBytes = Base64.getDecoder().decode(signatureBase64)
            val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
            signature.initVerify(peerIdentityPublicKey)
            signature.update(publicKeyToVerify.encoded)
            signature.verify(signatureBytes)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * P0-2: RFC 5869 HKDF-SHA256 Extract + Expand algorithm.
     */
    fun hkdfExtract(salt: ByteArray?, ikm: ByteArray): ByteArray {
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        val actualSalt = salt ?: ByteArray(32) // If salt is null, use 32 zero bytes
        mac.init(SecretKeySpec(actualSalt, HMAC_ALGORITHM))
        return mac.doFinal(ikm)
    }

    fun hkdfExpand(prk: ByteArray, info: ByteArray, length: Int): ByteArray {
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(SecretKeySpec(prk, HMAC_ALGORITHM))

        val result = ByteArray(length)
        var t = ByteArray(0)
        var offset = 0
        var i = 1

        while (offset < length) {
            mac.reset()
            mac.update(t)
            mac.update(info)
            mac.update(i.toByte())
            t = mac.doFinal()

            val bytesToCopy = minOf(t.size, length - offset)
            System.arraycopy(t, 0, result, offset, bytesToCopy)
            offset += bytesToCopy
            i++
        }

        return result
    }

    /**
     * P0-1 & P0-2: Authenticated ECDH Shared Secret derivation with RFC 5869 HKDF key expansion and context separation.
     */
    fun deriveSharedSecret(
        privateKey: PrivateKey,
        remotePublicKey: PublicKey,
        salt: ByteArray? = null,
        infoContext: String = "NOTUNE_ROOM_KEY_DERIVATION_v1"
    ): SecretKey {
        val keyAgreement = KeyAgreement.getInstance(KEY_AGREEMENT_ECDH)
        keyAgreement.init(privateKey)
        keyAgreement.doPhase(remotePublicKey, true)
        val ikm = keyAgreement.generateSecret()

        val prk = hkdfExtract(salt, ikm)
        val infoBytes = infoContext.toByteArray(Charsets.UTF_8)
        val okm = hkdfExpand(prk, infoBytes, AES_KEY_SIZE_BYTES)

        return SecretKeySpec(okm, "AES")
    }

    /**
     * P0-3 & P0-11: Encrypts plaintext using AES-256-GCM with 12-byte randomized IV.
     */
    fun encryptWithDerivedKey(plainText: String, secretKey: SecretKey, aad: ByteArray? = null): String {
        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        val iv = ByteArray(IV_SIZE_BYTES)
        SecureRandom().nextBytes(iv)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

        if (aad != null) {
            cipher.updateAAD(aad)
        }

        val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = ByteArray(iv.size + cipherText.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)

        return Base64.getEncoder().encodeToString(combined)
    }

    /**
     * P0-3 & P0-11: Decrypts AES-256-GCM ciphertext payload with tag verification.
     */
    fun decryptWithDerivedKey(
        cipherTextBase64: String,
        secretKey: SecretKey,
        aad: ByteArray? = null,
        roomId: String? = null,
        messageId: String? = null,
        sequenceNumber: Long? = null
    ): String {
        if (roomId != null && messageId != null && sequenceNumber != null) {
            if (replayFilter.isMessageReplayed(roomId, messageId, sequenceNumber)) {
                throw SecurityException("Message replay detected! Replayed messageId: $messageId, sequenceNumber: $sequenceNumber")
            }
        }

        val combined = Base64.getDecoder().decode(cipherTextBase64)
        if (combined.size <= IV_SIZE_BYTES) {
            throw IllegalArgumentException("Ciphertext payload too short")
        }

        val iv = ByteArray(IV_SIZE_BYTES)
        val cipherText = ByteArray(combined.size - IV_SIZE_BYTES)
        System.arraycopy(combined, 0, iv, 0, IV_SIZE_BYTES)
        System.arraycopy(combined, IV_SIZE_BYTES, cipherText, 0, cipherText.size)

        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        if (aad != null) {
            cipher.updateAAD(aad)
        }

        return String(cipher.doFinal(cipherText), Charsets.UTF_8)
    }

    /**
     * P0-3: RFC 5869 HKDF-based key rotation for member departure/revocation events.
     */
    fun rotateKey(oldSecretKey: SecretKey, rotationContext: String = "NOTUNE_ROOM_KEY_ROTATION_v1"): SecretKey {
        val salt = ByteArray(32)
        SecureRandom().nextBytes(salt)
        val prk = hkdfExtract(salt, oldSecretKey.encoded)
        val okm = hkdfExpand(prk, rotationContext.toByteArray(Charsets.UTF_8), AES_KEY_SIZE_BYTES)
        return SecretKeySpec(okm, "AES")
    }
}


