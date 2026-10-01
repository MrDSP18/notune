package com.music.echo.notune.intelligence.queue

import com.music.echo.notune.intelligence.musicbrain.TrackEmbedding
import echo.music.iad1tya.notune.provider.UnifiedTrack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdaptiveQueueOrchestrator @Inject constructor(
    private val adaptiveQueueEngine: AdaptiveQueueEngine
) {

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

    private fun toQueueTrack(track: UnifiedTrack, isLocked: Boolean): QueueTrack {
        return QueueTrack(
            id = track.id,
            title = track.title,
            artistName = track.artist,
            isLockedByUser = isLocked,
            embedding = TrackEmbedding(
                trackId = track.id,
                title = track.title,
                artistName = track.artist
            )
        )
    }
}
