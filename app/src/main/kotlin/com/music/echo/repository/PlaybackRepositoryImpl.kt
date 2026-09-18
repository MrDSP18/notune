package com.music.echo.repository

import echo.music.iad1tya.models.PlaybackState
import echo.music.iad1tya.repository.PlaybackRepository
import echo.music.iad1tya.playback.PlayerConnectionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import echo.music.iad1tya.extensions.metadata
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.models.TechnicalTelemetry

@Singleton
class PlaybackRepositoryImpl @Inject constructor(
    private val connectionManager: PlayerConnectionManager,
    @echo.music.iad1tya.di.ApplicationScope private val scope: CoroutineScope
) : PlaybackRepository {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override val playbackState: StateFlow<PlaybackState> = connectionManager.playerConnection
        .flatMapLatest { connection ->
            if (connection == null) flowOf(PlaybackState())
            else combine(
                connection.mediaMetadata,
                connection.isPlaying,
                connection.position,
                connection.duration,
                connection.shuffleModeEnabled,
                connection.repeatMode,
                connection.technicalTelemetry,
                connection.audioFormat,
                connection.aiDjCommentary
            ) { array ->
                val metadata = array[0] as? MediaMetadata
                val isPlaying = array[1] as Boolean
                val pos = array[2] as Long
                val dur = array[3] as Long
                val shuffle = array[4] as Boolean
                val repeat = array[5] as Int
                val telemetry = array[6] as TechnicalTelemetry
                val format = array[7] as? androidx.media3.common.Format
                val aiCommentary = array[8] as? String

                val updatedTelemetry = if (format != null) {
                    telemetry.copy(
                        bitrate = if (format.bitrate != androidx.media3.common.Format.NO_VALUE) format.bitrate else null,
                        sampleRate = if (format.sampleRate != androidx.media3.common.Format.NO_VALUE) format.sampleRate else null,
                        codec = format.sampleMimeType?.substringAfter("audio/")?.uppercase(),
                        mimeType = format.sampleMimeType,
                        channelCount = if (format.channelCount != androidx.media3.common.Format.NO_VALUE) format.channelCount else null,
                        audioSessionId = connection.player.audioSessionId.takeIf { it != androidx.media3.common.C.AUDIO_SESSION_ID_UNSET }
                    )
                } else {
                    telemetry.copy(
                         audioSessionId = connection.player.audioSessionId.takeIf { it != androidx.media3.common.C.AUDIO_SESSION_ID_UNSET }
                    )
                }

                PlaybackState(
                    currentSong = metadata,
                    isPlaying = isPlaying,
                    position = pos,
                    duration = dur,
                    bufferedPosition = connection.player.bufferedPosition,
                    queue = connection.player.currentTimeline.let { timeline ->
                        val list = mutableListOf<MediaMetadata>()
                        for (i in 0 until timeline.windowCount) {
                            timeline.getWindow(i, androidx.media3.common.Timeline.Window()).mediaItem.metadata?.let { list.add(it) }
                        }
                        list
                    },
                    queueIndex = connection.player.currentMediaItemIndex,
                    shuffleModeEnabled = shuffle,
                    repeatMode = repeat,
                    volume = connection.player.volume,
                    isFavorite = metadata?.liked ?: false,
                    lyricsAvailable = metadata?.id != null, // simplified for now
                    aiDjCommentary = aiCommentary,
                    telemetry = updatedTelemetry
                )
            }
        }.stateIn(scope, SharingStarted.WhileSubscribed(5000), PlaybackState())

    override fun play() { connectionManager.playerConnection.value?.play() }
    override fun pause() { connectionManager.playerConnection.value?.pause() }
    override fun next() { connectionManager.playerConnection.value?.seekToNext() }
    override fun previous() { connectionManager.playerConnection.value?.seekToPrevious() }
    override fun seekTo(position: Long) { connectionManager.playerConnection.value?.seekTo(position) }
    override fun setVolume(volume: Float) {
        connectionManager.playerConnection.value?.player?.volume = volume
    }
    override fun setShuffleMode(enabled: Boolean) {
        connectionManager.playerConnection.value?.player?.shuffleModeEnabled = enabled
    }
    override fun setRepeatMode(mode: Int) {
        connectionManager.playerConnection.value?.player?.repeatMode = mode
    }
}
