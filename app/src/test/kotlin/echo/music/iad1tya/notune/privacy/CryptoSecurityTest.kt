package echo.music.iad1tya.notune.privacy

import com.music.echo.notune.privacy.security.GcmEncryptedPayload
import com.music.echo.notune.privacy.security.SecurityHardening
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import javax.crypto.KeyGenerator

class CryptoSecurityTest {

    private lateinit var security: SecurityHardening

    @Before
    fun setUp() {
        security = SecurityHardening()
    }

    @Test
    fun testHkdfExtractAndExpand_returnsRequestedLength() {
        val ikm = "InputKeyingMaterial".toByteArray(Charsets.UTF_8)
        val salt = "SaltValue1234567".toByteArray(Charsets.UTF_8)
        val info = "ApplicationInfoContext".toByteArray(Charsets.UTF_8)

        val derivedKey = security.hkdfExtractAndExpand(salt, ikm, info, 32)
        assertNotNull(derivedKey)
        assertEquals(32, derivedKey.size)
    }

    @Test
    fun testAesGcmEncryptionWithAad_encryptAndDecryptSucceeds() {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val secretKey = keyGen.generateKey()

        val plaintext = "Sensitive Couple Secret Message".toByteArray(Charsets.UTF_8)
        val aadContext = "room101:deviceA:epoch1:msg"

        val payload = security.encryptAesGcmWithAad(plaintext, secretKey, aadContext)
        val decrypted = security.decryptAesGcmWithAad(payload, secretKey)

        assertEquals("Sensitive Couple Secret Message", String(decrypted, Charsets.UTF_8))
    }

    @Test(expected = Exception::class)
    fun testAesGcmEncryptionWithAad_tamperedAadFailsDecryption() {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val secretKey = keyGen.generateKey()

        val plaintext = "Sensitive Message".toByteArray(Charsets.UTF_8)
        val payload = security.encryptAesGcmWithAad(plaintext, secretKey, "room101:deviceA:epoch1:msg")

        val tamperedPayload = GcmEncryptedPayload(
            iv = payload.iv,
            ciphertext = payload.ciphertext,
            aadContext = "room101:deviceB:epoch1:msg" // Tampered AAD context
        )

        security.decryptAesGcmWithAad(tamperedPayload, secretKey)
    }

    @Test
    fun testReplayProtection_rejectsDuplicateMessages() {
        val roomId = "room_abc"
        val deviceId = "device_123"
        val sequence = 1001L

        val firstTry = security.verifyAndMarkMessageUnique(roomId, deviceId, sequence)
        val secondTry = security.verifyAndMarkMessageUnique(roomId, deviceId, sequence)

        assertTrue(firstTry)
        assertFalse(secondTry)
    }
}
