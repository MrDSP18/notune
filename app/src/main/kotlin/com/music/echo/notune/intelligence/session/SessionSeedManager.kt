package com.music.echo.notune.intelligence.session

import com.music.echo.notune.intelligence.queue.NotuneFlowMode
import echo.music.iad1tya.notune.provider.UnifiedTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

enum class SessionSeedSourceType {
    TRACK,
    ALBUM,
    PLAYLIST,
    ARTIST,
    LOCAL_TRACK,
    ROOM_QUEUE,
    SEARCH_RESULT
}

data class SessionSeed(
    val sourceType: SessionSeedSourceType = SessionSeedSourceType.TRACK,
    val seedId: String = "",
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val genres: List<String> = emptyList(),
    val language: String = "",
    val energy: Float = 0.55f,
    val tempo: Float = 110f,
    val valence: Float = 0.5f,
    val acousticness: Float = 0.3f,
    val danceability: Float = 0.6f,
    val familiarity: Float = 0.7f,
    val provider: String = "YouTube",
    val timestamp: Long = System.currentTimeMillis(),
    val authoredTracks: List<UnifiedTrack> = emptyList()
)

data class SessionProfile(
    val seed: SessionSeed = SessionSeed(),
    val currentEnergyTarget: Float = 0.55f,
    val currentTempoTarget: Float = 110f,
    val targetLanguage: String = "",
    val artistAffinityName: String = "",
    val genreAffinity: String = "",
    val discoveryRatio: Float = 0.25f,
    val familiarityRatio: Float = 0.75f,
    val flowMode: NotuneFlowMode = NotuneFlowMode.SMART,
    val isAuthoredContentActive: Boolean = false,
    val authoredTracksRemaining: Int = 0
)

@Singleton
class SessionSeedManager @Inject constructor() {

    private val _currentSeed = MutableStateFlow(SessionSeed())
    val currentSeed: StateFlow<SessionSeed> = _currentSeed.asStateFlow()

    private val _sessionProfile = MutableStateFlow(SessionProfile())
    val sessionProfile: StateFlow<SessionProfile> = _sessionProfile.asStateFlow()

    fun createTrackSeed(track: UnifiedTrack, sourceType: SessionSeedSourceType = SessionSeedSourceType.TRACK): SessionSeed {
        val seed = SessionSeed(
            sourceType = sourceType,
            seedId = track.id,
            title = track.title,
            artist = track.artist,
            album = track.album ?: "",
            genres = emptyList(),
            language = "",
            energy = 0.60f,
            tempo = 112f,
            provider = track.providerName,
            timestamp = System.currentTimeMillis()
        )
        setSeed(seed)
        return seed
    }

    fun createCollectionSeed(
        collectionId: String,
        title: String,
        sourceType: SessionSeedSourceType,
        tracks: List<UnifiedTrack>
    ): SessionSeed {
        val firstTrack = tracks.firstOrNull()
        val seed = SessionSeed(
            sourceType = sourceType,
            seedId = collectionId,
            title = title,
            artist = firstTrack?.artist ?: "",
            album = if (sourceType == SessionSeedSourceType.ALBUM) title else "",
            genres = emptyList(),
            language = "",
            energy = 0.55f,
            tempo = 108f,
            provider = firstTrack?.providerName ?: "YouTube",
            timestamp = System.currentTimeMillis(),
            authoredTracks = tracks
        )
        setSeed(seed)
        return seed
    }

    fun setSeed(seed: SessionSeed) {
        _currentSeed.value = seed
        _sessionProfile.update {
            SessionProfile(
                seed = seed,
                currentEnergyTarget = seed.energy,
                currentTempoTarget = seed.tempo,
                targetLanguage = seed.language,
                artistAffinityName = seed.artist,
                genreAffinity = seed.genres.firstOrNull().orEmpty(),
                discoveryRatio = if (seed.sourceType == SessionSeedSourceType.ARTIST) 0.35f else 0.25f,
                familiarityRatio = if (seed.sourceType == SessionSeedSourceType.ARTIST) 0.65f else 0.75f,
                isAuthoredContentActive = seed.authoredTracks.isNotEmpty(),
                authoredTracksRemaining = seed.authoredTracks.size
            )
        }
    }

    fun updateSessionProfile(transform: (SessionProfile) -> SessionProfile) {
        _sessionProfile.update(transform)
    }

    fun updateAuthoredProgress(remaining: Int) {
        _sessionProfile.update {
            it.copy(
                authoredTracksRemaining = remaining,
                isAuthoredContentActive = remaining > 0
            )
        }
    }
}
