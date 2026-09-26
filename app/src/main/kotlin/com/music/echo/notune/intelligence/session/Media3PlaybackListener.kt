package com.music.echo.notune.intelligence.session

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.music.echo.notune.intelligence.NotuneIntelligenceEngine
import com.music.echo.notune.intelligence.feedback.UserEvent
import com.music.echo.notune.intelligence.musicbrain.TrackEmbedding
import com.music.echo.notune.intelligence.queue.QueueTrack
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * Media3PlaybackListener
 *
 * Bridges real ExoPlayer / Media3 listener events directly into NotuneIntelligenceEngine
 * and PersonalMusicSession.
 *
 * Captures:
 * - MediaItem transitions
 * - IsPlaying state changes (Play / Pause / Resume)
 * - Playback completion & Early skips
 * - Seek discontinuities
 */
@Singleton
class Media3PlaybackListener @Inject constructor(
    private val intelligenceEngine: NotuneIntelligenceEngine,
    private val personalMusicSession: PersonalMusicSession
) : Player.Listener {

    private var currentMediaId: String? = null
    private var trackStartTimestampMs: Long = System.currentTimeMillis()
    private var lastReportedPositionMs: Long = 0L

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        val id = mediaItem?.mediaId ?: return
        val title = mediaItem.mediaMetadata.title?.toString() ?: "Unknown Track"
        val artist = mediaItem.mediaMetadata.artist?.toString() ?: "Unknown Artist"

        if (currentMediaId != null && currentMediaId != id) {
            val durationSec = (System.currentTimeMillis() - trackStartTimestampMs) / 1000f
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                // Track completed normally
                intelligenceEngine.recordFeedback(
                    UserEvent.Completed(trackId = currentMediaId!!, title = title, artistName = artist)
                )
            } else if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) {
                // Skip event
                intelligenceEngine.recordFeedback(
                    UserEvent.Skip(trackId = currentMediaId!!, title = title, artistName = artist, playedDurationSec = durationSec)
                )
                personalMusicSession.onTrackSkipped()
            }
        }

        currentMediaId = id
        trackStartTimestampMs = System.currentTimeMillis()

        val track = QueueTrack(
            id = id,
            title = title,
            artistName = artist,
            embedding = TrackEmbedding(trackId = id, title = title, artistName = artist)
        )
        personalMusicSession.onTrackStarted(track)

        intelligenceEngine.recordFeedback(
            UserEvent.Play(trackId = id, title = title, artistName = artist)
        )
        Timber.d("Intelligence Engine recorded real playback transition for $title by $artist")
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        val id = currentMediaId ?: return
        if (isPlaying) {
            intelligenceEngine.recordFeedback(UserEvent.Resume(trackId = id))
        } else {
            intelligenceEngine.recordFeedback(UserEvent.Pause(trackId = id, positionMs = lastReportedPositionMs))
        }
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        lastReportedPositionMs = newPosition.positionMs
    }
}
