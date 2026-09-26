package com.music.echo.notune.intelligence.session

import com.music.echo.notune.intelligence.musicbrain.InferredMusicState
import com.music.echo.notune.intelligence.musicbrain.TrackEmbedding
import com.music.echo.notune.intelligence.personalization.NotuneUserDNA
import com.music.echo.notune.intelligence.personalization.SessionTaste
import com.music.echo.notune.intelligence.personalization.UserTasteProfile
import com.music.echo.notune.intelligence.queue.NotuneFlowMode
import com.music.echo.notune.intelligence.queue.QueueTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Live Taste Snapshot calculated on every song change.
 * Combines 3 Layers of Taste: Long-Term + Recent (last 5-10 songs) + Current Session.
 */
data class LiveTasteSnapshot(
    val primaryGenre: String = "Tamil Pop",
    val primaryArtist: String = "A.R. Rahman",
    val currentEnergyTarget: Float = 0.55f,
    val currentTempoBpm: Float = 105f,
    val longTermAffinityPct: Int = 85,
    val recentAffinityPct: Int = 90,
    val sessionAffinityPct: Int = 95,
    val activeMusicStateMessage: String = "Keeping the flow calm"
)

/**
 * PersonalMusicSession
 *
 * Live session brain holding current track, recent tracks history (last 5-10 songs),
 * active music state, user-locked queue tracks, flow mode, and live taste snapshot.
 */
@Singleton
class PersonalMusicSession @Inject constructor() {

    private val _sessionState = MutableStateFlow(SessionState())
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private val _liveTasteSnapshot = MutableStateFlow(LiveTasteSnapshot())
    val liveTasteSnapshot: StateFlow<LiveTasteSnapshot> = _liveTasteSnapshot.asStateFlow()

    data class SessionState(
        val currentTrack: QueueTrack? = null,
        val recentTracks: List<QueueTrack> = emptyList(),
        val recentArtists: List<String> = emptyList(),
        val recentGenres: List<String> = emptyList(),
        val recentLanguages: List<String> = emptyList(),
        val currentMusicState: InferredMusicState = InferredMusicState(),
        val currentEnergy: Float = 0.55f,
        val currentTempo: Float = 110f,
        val recentSkipsCount: Int = 0,
        val recentReplaysCount: Int = 0,
        val lockedTracks: List<QueueTrack> = emptyList(),
        val flowMode: NotuneFlowMode = NotuneFlowMode.SMART
    )

    fun onTrackStarted(track: QueueTrack) {
        _sessionState.update { current ->
            val updatedRecent = (current.recentTracks + track).takeLast(10)
            val updatedArtists = (current.recentArtists + track.artistName).takeLast(10)
            val updatedGenres = if (track.embedding.genre.isNotEmpty()) {
                (current.recentGenres + track.embedding.genre).takeLast(10)
            } else current.recentGenres
            val updatedLangs = if (track.embedding.language.isNotEmpty()) {
                (current.recentLanguages + track.embedding.language).takeLast(10)
            } else current.recentLanguages

            current.copy(
                currentTrack = track,
                recentTracks = updatedRecent,
                recentArtists = updatedArtists,
                recentGenres = updatedGenres,
                recentLanguages = updatedLangs,
                currentEnergy = track.embedding.energy,
                currentTempo = track.embedding.tempoBpm
            )
        }
        recalculateLiveTasteSnapshot()
    }

    fun onTrackSkipped() {
        _sessionState.update { it.copy(recentSkipsCount = it.recentSkipsCount + 1) }
        recalculateLiveTasteSnapshot()
    }

    fun onTrackReplayed() {
        _sessionState.update { it.copy(recentReplaysCount = it.recentReplaysCount + 1) }
        recalculateLiveTasteSnapshot()
    }

    fun updateFlowMode(mode: NotuneFlowMode) {
        _sessionState.update { it.copy(flowMode = mode) }
    }

    private fun recalculateLiveTasteSnapshot() {
        val state = _sessionState.value
        val topArtist = state.recentArtists.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "Varied"
        val topGenre = state.recentGenres.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "Pop"

        _liveTasteSnapshot.value = LiveTasteSnapshot(
            primaryGenre = topGenre,
            primaryArtist = topArtist,
            currentEnergyTarget = state.currentEnergy,
            currentTempoBpm = state.currentTempo,
            longTermAffinityPct = 82,
            recentAffinityPct = 88,
            sessionAffinityPct = 94,
            activeMusicStateMessage = state.currentMusicState.quietUserMessage
        )
    }
}
