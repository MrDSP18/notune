package echo.music.iad1tya.notune.ai.providers

import com.music.echo.notune.personalization.repository.TasteProfileRepository
import echo.music.iad1tya.notune.ai.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class NoTuneBasicAiProvider @Inject constructor(
    private val tasteProfileRepository: TasteProfileRepository
) : AiProvider {

    override val type: AiProviderType = AiProviderType.LOCAL

    override suspend fun isConfigured(): Boolean = true // Always available zero-config offline

    override suspend fun getModels(): List<AiModel> = listOf(
        AiModel(
            id = "notune-basic-intelligence",
            name = "NØTUNE Basic AI (Offline)",
            capabilities = listOf(ModelCapability.TEXT_GENERATION, ModelCapability.TOOL_USE),
            isFree = true
        )
    )

    override suspend fun generateResponse(
        prompt: String,
        modelId: String?,
        tools: List<AiTool>?,
        systemInstruction: String?
    ): Result<AiResponse> {
        val lowerPrompt = prompt.lowercase()
        val tasteProfile = tasteProfileRepository.tasteProfile.first()

        val responseText = when {
            "similar" in lowerPrompt || "like this" in lowerPrompt -> {
                "NØTUNE Basic AI: Analyzing track metadata and matching with your top genres (${tasteProfile.favoriteGenres.take(2).joinToString()}) to adjust your queue."
            }
            "calm" in lowerPrompt || "relax" in lowerPrompt || "chill" in lowerPrompt -> {
                "NØTUNE Basic AI: Switching NØTUNE FLOW to Low Energy / Calm mode."
            }
            "energetic" in lowerPrompt || "workout" in lowerPrompt || "party" in lowerPrompt -> {
                "NØTUNE Basic AI: Elevating queue energy to High for an upbeat session."
            }
            "tamil" in lowerPrompt || "hindi" in lowerPrompt || "telugu" in lowerPrompt -> {
                "NØTUNE Basic AI: Prioritizing your preferred music languages (${tasteProfile.musicLanguages.joinToString()})."
            }
            else -> {
                "NØTUNE Basic AI is active offline. Your playback and FLOW recommendations are optimized based on your Music DNA."
            }
        }

        return Result.success(
            AiResponse(
                text = responseText,
                modelId = "notune-basic-intelligence"
            )
        )
    }
}
