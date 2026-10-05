package echo.music.iad1tya.notune.intelligence.enrichment

import echo.music.iad1tya.notune.ai.adaptive.AdaptiveTrackContext
import echo.music.iad1tya.notune.intelligence.knowledge.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resolution stages during progressive metadata enrichment.
 */
enum class EnrichmentStage {
    LOCAL_READY,
    IDENTIFYING,
    PARTIAL,
    ENRICHING,
    COMPLETE,
    STALE_REFRESH
}

/**
 * Encapsulates current progressive state of song enrichment.
 */
data class ProgressiveEnrichmentState(
    val stage: EnrichmentStage,
    val songDetails: SongDetails,
    val identityMatch: SongIdentityMatch? = null,
    val confidence: MatchConfidence = MatchConfidence.UNKNOWN,
    val enrichedFields: List<String> = emptyList(),
    val isComplete: Boolean = false
)

/**
 * Central Metadata Resolver.
 * Executes multi-source resolution: Normalization -> Cache check -> Staged Provider Query ->
 * Entity Resolution & Confidence validation -> Field Merging -> Cache -> Progressive Emission.
 */
@Singleton
class MusicMetadataResolver @Inject constructor(
    private val normalizer: SongIdentityNormalizer,
    private val orchestrator: ProviderOrchestrator,
    private val confidenceEngine: ConfidenceEngine,
    private val fieldMerger: FieldMerger,
    private val cacheManager: MetadataCacheManager
) {

    /**
     * Resolves deep music metadata progressively as a Flow.
     * Never blocks playback. Emits LOCAL_READY instantly, followed by ENRICHING and COMPLETE states.
     */
    fun resolveProgressively(track: AdaptiveTrackContext): Flow<ProgressiveEnrichmentState> = flow {
        // Step 1: Normalize identity
        val identity = normalizer.normalize(
            rawTitle = track.title,
            rawArtist = track.artist,
            rawAlbum = track.album,
            durationMs = track.durationMs
        )

        // Step 2: Immediate Local/Cache resolution
        val cachedSong = cacheManager.getSong(track.trackId)
        val initialSong = cachedSong ?: SongDetails(
            songId = track.trackId,
            title = identity.cleanTitle.ifBlank { track.title.ifBlank { "Information unavailable" } },
            originalTitle = identity.rawInput.takeIf { it != identity.cleanTitle },
            artists = listOf(PersonDetails(personId = "artist_${track.artist.hashCode()}", name = track.artist.ifBlank { "Unknown Artist" }, source = MetadataSource.LOCAL)),
            credits = SongCredits(singers = listOf(PersonDetails(personId = "artist_${track.artist.hashCode()}", name = track.artist.ifBlank { "Unknown Artist" }, source = MetadataSource.LOCAL))),
            albumTitle = identity.cleanAlbum?.takeIf { it.isNotBlank() },
            movieId = identity.cleanMovie?.let { "movie_${it.hashCode()}" },
            movieTitle = identity.cleanMovie,
            language = track.language,
            durationMs = track.durationMs,
            artwork = MediaArtwork(role = ArtworkRole.SONG, source = MetadataSource.LOCAL),
            source = MetadataSource.LOCAL
        )

        emit(
            ProgressiveEnrichmentState(
                stage = EnrichmentStage.LOCAL_READY,
                songDetails = initialSong,
                confidence = MatchConfidence.HIGH,
                enrichedFields = listOf("title", "artist", "album"),
                isComplete = false
            )
        )

        if (cachedSong != null) {
            emit(
                ProgressiveEnrichmentState(
                    stage = EnrichmentStage.COMPLETE,
                    songDetails = cachedSong,
                    confidence = MatchConfidence.EXACT,
                    enrichedFields = listOf("all_cached"),
                    isComplete = true
                )
            )
            return@flow
        }

        // Step 3: Identify & query providers
        emit(
            ProgressiveEnrichmentState(
                stage = EnrichmentStage.IDENTIFYING,
                songDetails = initialSong,
                confidence = MatchConfidence.MEDIUM,
                enrichedFields = listOf("title", "artist", "album"),
                isComplete = false
            )
        )

        val matches = orchestrator.queryAllProviders(identity)
        if (matches.isEmpty()) {
            emit(
                ProgressiveEnrichmentState(
                    stage = EnrichmentStage.COMPLETE,
                    songDetails = initialSong,
                    confidence = MatchConfidence.LOW,
                    enrichedFields = listOf("local_only"),
                    isComplete = true
                )
            )
            return@flow
        }

        // Step 4: Validate confidence & perform field-level merge
        var mergedSong = initialSong

        for (match in matches) {
            val candidateMatch = confidenceEngine.evaluateMatch(identity, match.songDetails, match.providerId)
            if (confidenceEngine.isMatchValidForAttachment(candidateMatch)) {
                mergedSong = fieldMerger.mergeSongDetails(mergedSong, match.songDetails)
            }
        }

        // Step 5: Cache merged song details
        cacheManager.putSong(mergedSong)

        // Step 6: Emit final enriched state
        emit(
            ProgressiveEnrichmentState(
                stage = EnrichmentStage.COMPLETE,
                songDetails = mergedSong,
                confidence = MatchConfidence.HIGH,
                enrichedFields = listOf("title", "artist", "album", "credits", "movie", "artwork", "lyrics"),
                isComplete = true
            )
        )
    }
}
