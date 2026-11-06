package com.music.echo.repository

import echo.music.iad1tya.models.PlaybackState
import echo.music.iad1tya.repository.PlaybackRepository
import echo.music.iad1tya.playback.PlayerConnectionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.models.TechnicalTelemetry

/**
 * Thread-safe implementation of [PlaybackRepository].
 *
 * All player state is consumed from thread-safe [StateFlow]s exposed by [PlayerConnection]
 * that are exclusively updated by [Player.Listener] callbacks on the main thread.
 *
 * NO direct access to `connection.player.*` is permitted here — this repository runs its
 * [StateFlow] pipeline on [ApplicationScope] which is backed by [Dispatchers.Default].
 */
@Singleton
class PlaybackRepositoryImpl @Inject constructor(
    private val connectionManager: PlayerConnectionManager,
    @echo.music.iad1tya.di.ApplicationScope private val scope: CoroutineScope
) : PlaybackRepository {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override val playbackState: StateFlow<PlaybackState> = connectionManager.playerConnection
        .flatMapLatest { connection ->
            if (connection == null) return@flatMapLatest flowOf(PlaybackState())

            // All sources below are StateFlows updated exclusively on the main thread.
            // The combine operator itself runs on whichever thread emits, but because
            // all of these flows are backed by MutableStateFlow (thread-safe), reading
            // their values inside this block is safe on any dispatcher.
            combine(
                connection.mediaMetadata,
                connection.isPlaying,
                connection.position,
                connection.duration,
                connection.shuffleModeEnabled,
                connection.repeatMode,
                connection.technicalTelemetry,
                connection.audioFormat,
                connection.aiDjCommentary,
                // Thread-safe snapshot flows — populated by Player.Listener on main thread.
                connection.audioSessionId,
                connection.bufferedPosition,
                connection.playerVolume,
                connection.playerQueue,
                connection.currentMediaItemIndex
            ) { array ->
                val metadata      = array[0] as? MediaMetadata
                val isPlaying     = array[1] as Boolean
                val pos           = array[2] as Long
                val dur           = array[3] as Long
                val shuffle       = array[4] as Boolean
                val repeat        = array[5] as Int
                val telemetry     = array[6] as TechnicalTelemetry
                val format        = array[7] as? androidx.media3.common.Format
                val aiCommentary  = array[8] as? String
                val audioSessId   = array[9] as? Int
                val buffered      = array[10] as Long
                val volume        = array[11] as Float
                @Suppress("UNCHECKED_CAST")
                val queue         = array[12] as List<MediaMetadata>
                val queueIndex    = array[13] as Int

                val updatedTelemetry = if (format != null) {
                    telemetry.copy(
                        bitrate = if (format.bitrate != androidx.media3.common.Format.NO_VALUE) format.bitrate else null,
                        sampleRate = if (format.sampleRate != androidx.media3.common.Format.NO_VALUE) format.sampleRate else null,
                        codec = format.sampleMimeType?.substringAfter("audio/")?.uppercase(),
                        mimeType = format.sampleMimeType,
                        channelCount = if (format.channelCount != androidx.media3.common.Format.NO_VALUE) format.channelCount else null,
                        audioSessionId = audioSessId
                    )
                } else {
                    telemetry.copy(audioSessionId = audioSessId)
                }

                PlaybackState(
                    currentSong = metadata,
                    isPlaying = isPlaying,
                    position = pos,
                    duration = dur,
                    bufferedPosition = buffered,
                    queue = queue,
                    queueIndex = queueIndex,
                    shuffleModeEnabled = shuffle,
                    repeatMode = repeat,
                    volume = volume,
                    isFavorite = metadata?.liked ?: false,
                    lyricsAvailable = metadata?.id != null,
                    aiDjCommentary = aiCommentary,
                    telemetry = updatedTelemetry
                )
            }
        }.stateIn(scope, SharingStarted.WhileSubscribed(5000), PlaybackState())

    // Control methods — PlayerConnection.runOnMain ensures these dispatch to the main thread.
    override fun play()                      { connectionManager.playerConnection.value?.play() }
    override fun pause()                     { connectionManager.playerConnection.value?.pause() }
    override fun next()                      { connectionManager.playerConnection.value?.seekToNext() }
    override fun previous()                  { connectionManager.playerConnection.value?.seekToPrevious() }
    override fun seekTo(position: Long)      { connectionManager.playerConnection.value?.seekTo(position) }
    override fun setVolume(volume: Float)    { connectionManager.playerConnection.value?.setVolume(volume) }
    override fun setShuffleMode(enabled: Boolean) { connectionManager.playerConnection.value?.setShuffleModeEnabled(enabled) }
    override fun setRepeatMode(mode: Int)    { connectionManager.playerConnection.value?.setRepeatMode(mode) }
}
