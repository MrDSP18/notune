package echo.music.iad1tya.notune.ai.brain

import echo.music.iad1tya.notune.ai.models.CloudModelProvider
import echo.music.iad1tya.notune.ai.models.ModelCapability
import echo.music.iad1tya.notune.ai.models.NotuneModelProvider
import echo.music.iad1tya.notune.ai.providers.NoTuneBasicAiProvider
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class ModelRouter @Inject constructor(
    private val localAiProvider: NoTuneBasicAiProvider
) {

    private val localProvider = object : NotuneModelProvider {
        override val providerId: String = "notune-local-music-model"
        override val capabilities: Set<ModelCapability> = setOf(
            ModelCapability.FAST_LOCAL,
            ModelCapability.MUSIC_RANKING,
            ModelCapability.OFFLINE_ONLY
        )

        override suspend fun isAvailable(): Boolean = localAiProvider.isConfigured()

        override suspend fun generateText(
            prompt: String,
            systemInstruction: String?
        ): Result<String> = localAiProvider.generateResponse(
            prompt = prompt,
            systemInstruction = systemInstruction
        ).map { it.text }
    }

    private val providers = mutableListOf<NotuneModelProvider>(localProvider)

    fun registerCloudProvider(provider: CloudModelProvider) {
        providers.removeAll { it.providerId == provider.providerId }
        providers.add(provider)
    }

    suspend fun routeModel(capability: ModelCapability): NotuneModelProvider {
        val available = providers.filter { it.isAvailable() }
        val selected = available.firstOrNull { capability in it.capabilities }
            ?: available.firstOrNull()
            ?: throw IllegalStateException("No available NØTUNE model provider for $capability")

        Timber.d("ModelRouter: Routed to ${selected.providerId} for $capability")
        return selected
    }
}
