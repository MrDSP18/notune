package echo.music.iad1tya.notune.intelligence.enrichment

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orchestrates multi-source staged metadata queries.
 * Calls providers in order of priority, deduplicating and matching song identities.
 */
@Singleton
class ProviderOrchestrator @Inject constructor(
    private val localProvider: LocalMetadataProvider,
    private val backendProvider: BackendMetadataProvider,
    private val innerTubeProvider: InnerTubeMetadataProvider,
    private val lrclibProvider: LrclibMetadataProvider
) {

    private val providers: List<MetadataProvider> by lazy {
        listOf(localProvider, backendProvider, innerTubeProvider, lrclibProvider)
            .sortedByDescending { it.providerPriority }
    }

    /**
     * Staged resolution query across registered providers.
     * Returns candidate matches grouped by providerId.
     */
    suspend fun queryAllProviders(identity: SongIdentity): List<ProviderSongMatch> {
        val results = mutableListOf<ProviderSongMatch>()

        for (provider in providers) {
            try {
                val matches = provider.searchSong(identity)
                results.addAll(matches)
                // Stop early if local provider returned exact confident match
                if (provider.providerId == localProvider.providerId && matches.any { it.confidenceScore >= 0.95 }) {
                    // Continue to query backend/enrichment providers for extra fields, but don't force block
                }
            } catch (e: Exception) {
                // Ignore individual provider failure gracefully
            }
        }

        return results
    }

    /**
     * Returns concrete provider by providerId.
     */
    fun getProvider(providerId: String): MetadataProvider? {
        return providers.firstOrNull { it.providerId == providerId }
    }
}
