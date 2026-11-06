package echo.music.iad1tya.repository

import echo.music.iad1tya.models.PlaybackState
import kotlinx.coroutines.flow.StateFlow

interface PlaybackRepository {
    val playbackState: StateFlow<PlaybackState>
    
    fun play()
    fun pause()
    fun next()
    fun previous()
    fun seekTo(position: Long)
    fun setVolume(volume: Float)
    fun setShuffleMode(enabled: Boolean)
    fun setRepeatMode(mode: Int)
}
