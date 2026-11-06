package echo.music.iad1tya.notune.ai.brain

import echo.music.iad1tya.notune.ai.models.CloudModelProvider
import echo.music.iad1tya.notune.ai.models.ModelCapability
import echo.music.iad1tya.notune.ai.models.NotuneModelProvider
import echo.music.iad1tya.notune.ai.models.OnDeviceModelProvider
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class ModelRouter @Inject constructor() {

    private val providers = mutableListOf<NotuneModelProvider>()

    init {
        providers.add(OnDeviceModelProvider())
    }

    fun registerCloudProvider(provider: CloudModelProvider) {
        providers.add(provider)
    }

    suspend fun routeModel(capability: ModelCapability): NotuneModelProvider {
        val available = providers.filter { it.isAvailable() }
        val matching = available.find { it.capabilities.contains(capability) }
        val selected = matching ?: available.firstOrNull() ?: OnDeviceModelProvider()
        Timber.d("ModelRouter: Routed to provider ${selected.providerId} for capability $capability")
        return selected
    }
}
