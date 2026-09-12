package echo.music.iad1tya.notune.privacy

import com.music.echo.notune.privacy.ai.AIPrivacyPolicy
import com.music.echo.notune.privacy.ai.AIRequestSanitizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AIPrivacyPolicyTest {

    private lateinit var privacyPolicy: AIPrivacyPolicy
    private lateinit var sanitizer: AIRequestSanitizer

    @Before
    fun setUp() {
        privacyPolicy = AIPrivacyPolicy()
        sanitizer = AIRequestSanitizer()
    }

    @Test
    fun testTransparencyPayloadGeneration_minimizesSensitiveData() {
        val payload = privacyPolicy.getTransparencyPayload(
            userPrompt = "Suggest songs similar to Blinding Lights",
            currentTrackTitle = "Blinding Lights",
            currentArtistName = "The Weeknd",
            isCloudAi = true,
            isPrivateAiChat = true
        )

        assertTrue(payload.sharedFields.any { it.contains("Blinding Lights") })
        assertTrue(payload.excludedFields.contains("Full listening history"))
        assertTrue(payload.excludedFields.contains("Account credentials & passwords"))
        assertTrue(payload.excludedFields.contains("Private messages & Couple dedications"))
    }

    @Test
    fun testSanitizerStripsSecretsAndAuthTokens() {
        val rawPrompt = "Find song with Bearer eyJhbGciOiJIUzI1NiJ9.test and password=secret123"
        val sanitized = sanitizer.sanitizePrompt(rawPrompt)

        assertFalse(sanitized.contains("eyJhbGciOiJIUzI1NiJ9"))
        assertFalse(sanitized.contains("password=secret123"))
        assertTrue(sanitized.contains("[REDACTED_SECRET]"))
    }
}
