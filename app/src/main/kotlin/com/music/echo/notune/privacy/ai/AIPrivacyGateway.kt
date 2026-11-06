package com.music.echo.notune.privacy.ai

import echo.music.iad1tya.notune.ai.AiProviderType
import javax.inject.Inject
import javax.inject.Singleton

enum class AiPrivacyDecision {
    ALLOW_LOCAL,
    ALLOW_CLOUD,
    DENY,
}

data class AiPrivacyRequest(
    val prompt: String,
    val provider: AiProviderType,
    val privateSession: Boolean,
    val aiPersonalizationEnabled: Boolean,
)

data class AiPrivacyResult(
    val decision: AiPrivacyDecision,
    val prompt: String,
    val reason: String? = null,
)

@Singleton
class AIPrivacyGateway @Inject constructor(
    private val sanitizer: AIRequestSanitizer,
) {
    fun evaluate(request: AiPrivacyRequest): AiPrivacyResult {
        if (request.privateSession) {
            return AiPrivacyResult(AiPrivacyDecision.DENY, "", "AI is disabled during Private Session")
        }

        val sanitizedPrompt = sanitizer.sanitizePrompt(request.prompt)
        if (request.provider == AiProviderType.OLLAMA || request.provider == AiProviderType.LOCAL) {
            return AiPrivacyResult(AiPrivacyDecision.ALLOW_LOCAL, sanitizedPrompt)
        }

        if (!request.aiPersonalizationEnabled) {
            return AiPrivacyResult(AiPrivacyDecision.DENY, "", "Cloud AI personalization is disabled")
        }

        return AiPrivacyResult(AiPrivacyDecision.ALLOW_CLOUD, sanitizedPrompt)
    }
}