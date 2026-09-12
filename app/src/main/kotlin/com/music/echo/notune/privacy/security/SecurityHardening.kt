package com.music.echo.notune.privacy.security

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityHardening @Inject constructor() {

    private val random = SecureRandom()
    private val seenMessageCache = mutableSetOf<String>()

    /**
     * Genuine RFC 5869 HKDF Extract and Expand implementation using HMAC-SHA256.
     */
    fun hkdfExtractAndExpand(
        salt: ByteArray?,
        ikm: ByteArray,
        info: ByteArray,
        outLength: Int
    ): ByteArray {
        val actualSalt = salt ?: ByteArray(32) { 0 }
        
        // HKDF-Extract: PRK = HMAC-Hash(salt, IKM)
        val macExtract = Mac.getInstance("HmacSHA256")
        macExtract.init(SecretKeySpec(actualSalt, "HmacSHA256"))
        val prk = macExtract.doFinal(ikm)

        // HKDF-Expand: OKM = T(1) | T(2) | T(3) | ... | T(N)
        val hashLen = 32
        val n = (outLength + hashLen - 1) / hashLen
        val okm = ByteArray(outLength)
        var t = ByteArray(0)
        var offset = 0

        val macExpand = Mac.getInstance("HmacSHA256")
        macExpand.init(SecretKeySpec(prk, "HmacSHA256"))

        for (i in 1..n) {
            macExpand.reset()
            macExpand.update(t)
            macExpand.update(info)
            macExpand.update(i.toByte())
            t = macExpand.doFinal()

            val bytesToCopy = minOf(hashLen, outLength - offset)
            System.arraycopy(t, 0, okm, offset, bytesToCopy)
            offset += bytesToCopy
        }

        return okm
    }

    /**
     * Encrypts plaintext using AES-256-GCM and binds context parameters to Additional Authenticated Data (AAD).
     */
    fun encryptAesGcmWithAad(
        plaintext: ByteArray,
        key: SecretKey,
        aadContext: String
    ): GcmEncryptedPayload {
        val iv = ByteArray(12)
        random.nextBytes(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        cipher.updateAAD(aadContext.toByteArray(Charsets.UTF_8))

        val ciphertext = cipher.doFinal(plaintext)
        return GcmEncryptedPayload(iv = iv, ciphertext = ciphertext, aadContext = aadContext)
    }

    /**
     * Decrypts AES-256-GCM ciphertext while validating AAD context. Throws SecurityException if AAD or key mismatch.
     */
    fun decryptAesGcmWithAad(
        payload: GcmEncryptedPayload,
        key: SecretKey
    ): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, payload.iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        cipher.updateAAD(payload.aadContext.toByteArray(Charsets.UTF_8))

        return cipher.doFinal(payload.ciphertext)
    }

    /**
     * Protocol-level replay protection using roomId:senderDeviceId:sequenceNumber.
     * Returns true if message is unique (NOT a replay), or false if it was already processed.
     */
    @Synchronized
    fun verifyAndMarkMessageUnique(roomId: String, senderDeviceId: String, sequenceNumber: Long): Boolean {
        val key = "$roomId:$senderDeviceId:$sequenceNumber"
        if (seenMessageCache.contains(key)) {
            return false // Duplicate/Replay detected
        }
        if (seenMessageCache.size > 5000) {
            seenMessageCache.clear()
        }
        seenMessageCache.add(key)
        return true
    }
}

data class GcmEncryptedPayload(
    val iv: ByteArray,
    val ciphertext: ByteArray,
    val aadContext: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as GcmEncryptedPayload
        if (!iv.contentEquals(other.iv)) return false
        if (!ciphertext.contentEquals(other.ciphertext)) return false
        if (aadContext != other.aadContext) return false
        return true
    }

    override fun hashCode(): Int {
        var result = iv.contentHashCode()
        result = 31 * result + ciphertext.contentHashCode()
        result = 31 * result + aadContext.hashCode()
        return result
    }
}
