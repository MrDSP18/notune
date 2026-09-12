package echo.music.iad1tya.notune

import echo.music.iad1tya.notune.rooms.security.PeerKeyExchangeHandler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import javax.crypto.AEADBadTagException

class E2EKeyExchangeTest {

    private val keyExchangeHandler = PeerKeyExchangeHandler()

    @Test
    fun testECDHKeyExchange_userA_and_userB_deriveSameSecret() {
        val userAKeyPair = keyExchangeHandler.generateKeyPair()
        val userBKeyPair = keyExchangeHandler.generateKeyPair()

        val secretA = keyExchangeHandler.deriveSharedSecret(userAKeyPair.private, userBKeyPair.public)
        val secretB = keyExchangeHandler.deriveSharedSecret(userBKeyPair.private, userAKeyPair.public)

        assertNotNull(secretA)
        assertNotNull(secretB)
        assertEquals(secretA, secretB)
    }

    @Test
    fun testPeerEncryptionDecryption_userAEncrypts_userBDecrypts() {
        val userAKeyPair = keyExchangeHandler.generateKeyPair()
        val userBKeyPair = keyExchangeHandler.generateKeyPair()

        val secretA = keyExchangeHandler.deriveSharedSecret(userAKeyPair.private, userBKeyPair.public)
        val secretB = keyExchangeHandler.deriveSharedSecret(userBKeyPair.private, userAKeyPair.public)

        val originalMessage = "Secret NØTUNE Couple Message ❤️"
        val cipherText = keyExchangeHandler.encryptWithDerivedKey(originalMessage, secretA)
        val decryptedMessage = keyExchangeHandler.decryptWithDerivedKey(cipherText, secretB)

        assertEquals(originalMessage, decryptedMessage)
    }

    @Test
    fun testKeySignature_validSignature_passesVerification() {
        val identityKeyPair = keyExchangeHandler.generateKeyPair()
        val ephemeralKeyPair = keyExchangeHandler.generateKeyPair()

        val signature = keyExchangeHandler.signPublicKey(ephemeralKeyPair.public, identityKeyPair.private)
        val isValid = keyExchangeHandler.verifyPublicKeySignature(ephemeralKeyPair.public, signature, identityKeyPair.public)

        assertTrue(isValid)
    }

    @Test
    fun testKeySignature_tamperedKey_failsVerification() {
        val identityKeyPair = keyExchangeHandler.generateKeyPair()
        val ephemeralKeyPair = keyExchangeHandler.generateKeyPair()
        val attackerKeyPair = keyExchangeHandler.generateKeyPair()

        val signature = keyExchangeHandler.signPublicKey(ephemeralKeyPair.public, identityKeyPair.private)
        // Attacker attempts MITM key substitution using their own public key
        val isValid = keyExchangeHandler.verifyPublicKeySignature(attackerKeyPair.public, signature, identityKeyPair.public)

        assertFalse(isValid)
    }

    @Test
    fun testHkdfExtractAndExpand_sameInputs_returnsSameKey() {
        val ikm = "InputKeyMaterialBytes123456789".toByteArray()
        val salt = "SaltBytes12345".toByteArray()
        val info = "ContextInfo123".toByteArray()

        val prk1 = keyExchangeHandler.hkdfExtract(salt, ikm)
        val okm1 = keyExchangeHandler.hkdfExpand(prk1, info, 32)

        val prk2 = keyExchangeHandler.hkdfExtract(salt, ikm)
        val okm2 = keyExchangeHandler.hkdfExpand(prk2, info, 32)

        assertTrue(okm1.contentEquals(okm2))
    }

    @Test
    fun testHkdfExtractAndExpand_differentContextInfo_returnsDifferentKeys() {
        val ikm = "InputKeyMaterialBytes123456789".toByteArray()
        val salt = "SaltBytes12345".toByteArray()

        val prk = keyExchangeHandler.hkdfExtract(salt, ikm)
        val okmContextA = keyExchangeHandler.hkdfExpand(prk, "ContextA".toByteArray(), 32)
        val okmContextB = keyExchangeHandler.hkdfExpand(prk, "ContextB".toByteArray(), 32)

        assertFalse(okmContextA.contentEquals(okmContextB))
    }

    @Test(expected = AEADBadTagException::class)
    fun testEncryptionWithAAD_tamperedAAD_throwsAEADException() {
        val userAKeyPair = keyExchangeHandler.generateKeyPair()
        val userBKeyPair = keyExchangeHandler.generateKeyPair()
        val secretA = keyExchangeHandler.deriveSharedSecret(userAKeyPair.private, userBKeyPair.public)

        val aadOriginal = "RoomID_12345".toByteArray()
        val aadTampered = "RoomID_99999".toByteArray()

        val cipherText = keyExchangeHandler.encryptWithDerivedKey("Payload", secretA, aadOriginal)
        keyExchangeHandler.decryptWithDerivedKey(cipherText, secretA, aadTampered)
    }

    @Test(expected = SecurityException::class)
    fun testReplayProtection_replayedMessageId_throwsSecurityException() {
        val userAKeyPair = keyExchangeHandler.generateKeyPair()
        val userBKeyPair = keyExchangeHandler.generateKeyPair()
        val secret = keyExchangeHandler.deriveSharedSecret(userAKeyPair.private, userBKeyPair.public)

        val cipherText = keyExchangeHandler.encryptWithDerivedKey("Payload", secret)

        // First decryption succeeds
        keyExchangeHandler.decryptWithDerivedKey(cipherText, secret, null, "room_1", "msg_100", 1L)

        // Replay of same message ID throws SecurityException
        keyExchangeHandler.decryptWithDerivedKey(cipherText, secret, null, "room_1", "msg_100", 1L)
    }

    @Test
    fun testKeyRotation_derivedNewKey_differsFromOldKey() {
        val userAKeyPair = keyExchangeHandler.generateKeyPair()
        val userBKeyPair = keyExchangeHandler.generateKeyPair()
        val secretA = keyExchangeHandler.deriveSharedSecret(userAKeyPair.private, userBKeyPair.public)

        val rotatedSecret = keyExchangeHandler.rotateKey(secretA)

        assertNotNull(rotatedSecret)
        assertNotEquals(secretA, rotatedSecret)
    }
}


