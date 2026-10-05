package com.music.echo.notune.intelligence.recommendation

import com.music.echo.notune.intelligence.memory.UserMemoryEngine
import com.music.echo.notune.intelligence.musicbrain.MusicIdentityEngine
import echo.music.iad1tya.notune.provider.ProviderRegistry
import echo.music.iad1tya.notune.provider.UnifiedTrack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CandidateEngine @Inject constructor(
    private val providerRegistry: ProviderRegistry,
    private val musicIdentityEngine: MusicIdentityEngine,
    private val userMemoryEngine: UserMemoryEngine
) {

    suspend fun generateCandidatePool(
        seedQuery: String? = null,
        targetArtist: String? = null,
        targetGenre: String? = null,
        targetLanguage: String? = null,
        excludeArtists: List<String> = emptyList()
    ): List<UnifiedTrack> {
        val pool = mutableListOf<UnifiedTrack>()

        // 1. Search seed query or target artist across all providers
        val primaryQuery = seedQuery ?: targetArtist ?: targetGenre ?: targetLanguage ?: "top Tamil hits"
        val catalogResults = providerRegistry.searchUnifiedCatalog(primaryQuery)
        pool.addAll(catalogResults)

        // 2. Fetch language or genre candidates if specified
        if (targetLanguage != null && targetLanguage != primaryQuery) {
            pool.addAll(providerRegistry.searchUnifiedCatalog("$targetLanguage songs"))
        }

        // 3. Deduplicate candidates using MusicIdentityEngine
        val deduplicated = musicIdentityEngine.deduplicateTracks(pool)

        // 4. Availability & Exclusion filtering
        return deduplicated.filter { track ->
            val isExcludedArtist = excludeArtists.any { ex -> track.artist.contains(ex, ignoreCase = true) }
            val isPlayable = track.rights.isStreamable
            !isExcludedArtist && isPlayable
        }
    }
}
