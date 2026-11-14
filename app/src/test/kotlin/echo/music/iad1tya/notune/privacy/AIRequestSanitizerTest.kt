package echo.music.iad1tya.notune.privacy

import com.music.echo.notune.privacy.ai.AIRequestSanitizer
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AIRequestSanitizerTest {
    private val sanitizer = AIRequestSanitizer()

    @Test
    fun redactsEmailPhoneIpAndReportsSensitivePayload() {
        val sanitized = sanitizer.sanitizePrompt(
            "Contact user@example.com or call 555-123-4567 from 192.168.1.10"
        )

        assertTrue(sanitized.contains("[REDACTED_EMAIL]"))
        assertTrue(sanitized.contains("[REDACTED_PHONE]"))
        assertTrue(sanitized.contains("[REDACTED_IP]"))
        assertTrue(sanitizer.containsProhibitedPayload(sanitized))
        assertFalse(sanitized.contains("user@example.com"))
        assertFalse(sanitized.contains("555-123-4567"))
        assertFalse(sanitized.contains("192.168.1.10"))
    }

    @Test
    fun redactsEntirePemPrivateKeyBlock() {
        val sanitized = sanitizer.sanitizePrompt(
            "key=-----BEGIN PRIVATE KEY-----\nTOP_SECRET_MATERIAL\n-----END PRIVATE KEY-----"
        )

        assertTrue(sanitized.contains("[REDACTED_SECRET]"))
        assertFalse(sanitized.contains("TOP_SECRET_MATERIAL"))
    }

    @Test
    fun ordinaryPromptIsNotMarkedAsSensitive() {
        val sanitized = sanitizer.sanitizePrompt("Recommend Tamil indie songs")

        assertFalse(sanitizer.containsProhibitedPayload(sanitized))
    }
}
