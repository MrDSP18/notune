package com.music.echo.notune.intelligence.search

import com.music.echo.notune.intelligence.intent.StructuredUserIntent
import com.music.echo.notune.intelligence.musicbrain.MusicIdentityEngine
import echo.music.iad1tya.notune.provider.ProviderRegistry
import echo.music.iad1tya.notune.provider.UnifiedTrack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogSearchResolver @Inject constructor(
    private val providerRegistry: ProviderRegistry,
    private val musicIdentityEngine: MusicIdentityEngine
) {

    suspend fun resolveConstrainedSearch(intent: StructuredUserIntent): List<UnifiedTrack> {
        val queryBuilder = StringBuilder(intent.rawQuery)
        intent.targetLanguage?.let { queryBuilder.append(" ").append(it) }
        intent.constraints.targetEra?.let { queryBuilder.append(" ").append(it) }

        val rawResults = providerRegistry.searchUnifiedCatalog(queryBuilder.toString())
        val deduplicated = musicIdentityEngine.deduplicateTracks(rawResults)

        return deduplicated.filter { track ->
            val passesArtistExclusion = intent.constraints.excludeArtists.none { ex ->
                track.artist.contains(ex, ignoreCase = true)
            }
            passesArtistExclusion
        }
    }
}
