package echo.music.iad1tya.notune.privacy

import com.music.echo.notune.privacy.ai.AIPrivacyGateway
import com.music.echo.notune.privacy.ai.AiPrivacyDecision
import com.music.echo.notune.privacy.ai.AiPrivacyRequest
import echo.music.iad1tya.notune.ai.AiProviderType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AIPrivacyGatewayTest {
    private lateinit var gateway: AIPrivacyGateway

    @Before
    fun setUp() {
        gateway = AIPrivacyGateway(com.music.echo.notune.privacy.ai.AIRequestSanitizer())
    }

    @Test
    fun privateSession_deniesAllProviders() {
        val result = gateway.evaluate(
            AiPrivacyRequest("recommend a song", AiProviderType.OLLAMA, true, true),
        )

        assertEquals(AiPrivacyDecision.DENY, result.decision)
    }

    @Test
    fun disabledPersonalization_deniesCloudButAllowsLocal() {
        val cloud = gateway.evaluate(
            AiPrivacyRequest("recommend a song", AiProviderType.GEMINI, false, false),
        )
        val local = gateway.evaluate(
            AiPrivacyRequest("recommend a song", AiProviderType.OLLAMA, false, false),
        )

        assertEquals(AiPrivacyDecision.DENY, cloud.decision)
        assertEquals(AiPrivacyDecision.ALLOW_LOCAL, local.decision)
    }

    @Test
    fun cloudPrompt_isSanitizedBeforeAllowing() {
        val result = gateway.evaluate(
            AiPrivacyRequest("find Bearer abc.def", AiProviderType.GROQ, false, true),
        )

        assertEquals(AiPrivacyDecision.ALLOW_CLOUD, result.decision)
        assertTrue(result.prompt.contains("[REDACTED_SECRET]"))
        assertTrue(!result.prompt.contains("Bearer abc.def"))
    }
}