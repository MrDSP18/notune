package com.music.echo.notune.intelligence.queue

import com.music.echo.notune.intelligence.musicbrain.TrackEmbedding
import com.music.echo.notune.intelligence.session.SessionSeedManager
import com.music.echo.notune.intelligence.session.SessionSeedSourceType
import echo.music.iad1tya.notune.provider.UnifiedTrack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdaptiveQueueOrchestrator @Inject constructor(
    private val adaptiveQueueEngine: AdaptiveQueueEngine,
    private val sessionSeedManager: SessionSeedManager
) {

    fun startSessionWithTrack(track: UnifiedTrack, sourceType: SessionSeedSourceType = SessionSeedSourceType.TRACK) {
        val seed = sessionSeedManager.createTrackSeed(track, sourceType)
        val currentQueueTrack = toQueueTrack(track, isLocked = false)
        adaptiveQueueEngine.setQueue(currentQueueTrack, emptyList())
    }

    fun startSessionWithCollection(
        collectionId: String,
        title: String,
        sourceType: SessionSeedSourceType,
        tracks: List<UnifiedTrack>
    ) {
        if (tracks.isEmpty()) return
        sessionSeedManager.createCollectionSeed(collectionId, title, sourceType, tracks)
        val firstTrack = toQueueTrack(tracks.first(), isLocked = false)
        val upcomingTracks = tracks.drop(1).map { toQueueTrack(it, isLocked = false) }
        adaptiveQueueEngine.setQueue(firstTrack, upcomingTracks)
    }

    fun updateQueueFromTracks(
        currentTrack: UnifiedTrack?,
        upcomingTracks: List<UnifiedTrack>,
        lockUpcoming: Boolean = false
    ) {
        val currentQueueTrack = currentTrack?.let { toQueueTrack(it, isLocked = false) }
        val upcomingQueueTracks = upcomingTracks.map { toQueueTrack(it, isLocked = lockUpcoming) }
        adaptiveQueueEngine.setQueue(currentQueueTrack, upcomingQueueTracks)
    }

    fun addTrackToNext(track: UnifiedTrack) {
        val queueTrack = toQueueTrack(track, isLocked = true)
        val state = adaptiveQueueEngine.queueState.value
        val newUpcoming = listOf(queueTrack) + state.upcomingQueue
        adaptiveQueueEngine.setQueue(state.currentlyPlaying, newUpcoming)
    }

    fun removeTracksByArtist(artistName: String) {
        val state = adaptiveQueueEngine.queueState.value
        val filtered = state.upcomingQueue.filterNot { it.artistName.contains(artistName, ignoreCase = true) }
        adaptiveQueueEngine.setQueue(state.currentlyPlaying, filtered)
    }

    fun clearUpcoming() {
        val state = adaptiveQueueEngine.queueState.value
        adaptiveQueueEngine.setQueue(state.currentlyPlaying, emptyList())
    }

    fun reportFeedback(track: UnifiedTrack, signal: FeedbackSignalType, progressPct: Float = 1.0f) {
        val queueTrack = toQueueTrack(track, isLocked = false)
        adaptiveQueueEngine.onFeedback(queueTrack, signal, progressPct)
    }

    private fun toQueueTrack(track: UnifiedTrack, isLocked: Boolean): QueueTrack {
        return QueueTrack(
            id = track.id,
            title = track.title,
            artistName = track.artist,
            isLockedByUser = isLocked,
            embedding = TrackEmbedding(
                trackId = track.id,
                title = track.title,
                artistName = track.artist,
                genre = "",
                language = ""
            )
        )
    }
}
