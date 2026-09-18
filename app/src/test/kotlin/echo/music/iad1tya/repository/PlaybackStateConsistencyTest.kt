
package echo.music.iad1tya.repository

import com.music.echo.repository.PlaybackRepositoryImpl
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.models.PlaybackState
import echo.music.iad1tya.models.TechnicalTelemetry
import echo.music.iad1tya.playback.PlayerConnection
import echo.music.iad1tya.playback.PlayerConnectionManager
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.media3.common.Player

@OptIn(ExperimentalCoroutinesApi::class)
class PlaybackStateConsistencyTest {

    private val testScope = TestScope()
    private val connectionManager = mockk<PlayerConnectionManager>()
    private val playerConnection = mockk<PlayerConnection>()
    private val player = mockk<androidx.media3.exoplayer.ExoPlayer>()

    @Test
    fun `test authoritative state transitions`() = testScope.runTest {
        val playerConnectionFlow = MutableStateFlow<PlayerConnection?>(playerConnection)
        every { connectionManager.playerConnection } returns playerConnectionFlow
        
        val isPlayingFlow = MutableStateFlow(false)
        val metaFlow = MutableStateFlow<MediaMetadata?>(null)
        val posFlow = MutableStateFlow(0L)
        val durFlow = MutableStateFlow(0L)
        val shuffleFlow = MutableStateFlow(false)
        val repeatFlow = MutableStateFlow(0)
        val telemetryFlow = MutableStateFlow(TechnicalTelemetry())
        val aiCommentaryFlow = MutableStateFlow<String?>(null)
        val audioFormatFlow = MutableStateFlow<androidx.media3.common.Format?>(null)

        every { playerConnection.isPlaying } returns isPlayingFlow
        every { playerConnection.mediaMetadata } returns metaFlow
        every { playerConnection.position } returns posFlow
        every { playerConnection.duration } returns durFlow
        every { playerConnection.shuffleModeEnabled } returns shuffleFlow
        every { playerConnection.repeatMode } returns repeatFlow
        every { playerConnection.technicalTelemetry } returns telemetryFlow
        every { playerConnection.aiDjCommentary } returns aiCommentaryFlow
        every { playerConnection.audioFormat } returns audioFormatFlow
        every { playerConnection.player } returns player
        
        every { player.bufferedPosition } returns 0L
        every { player.currentTimeline } returns androidx.media3.common.Timeline.EMPTY
        every { player.currentMediaItemIndex } returns -1
        every { player.volume } returns 1f
        every { player.audioSessionId } returns 789
        
        val repository = PlaybackRepositoryImpl(connectionManager, backgroundScope)
        
        // Initial state
        var state = repository.playbackState.value
        assertFalse(state.isPlaying)
        assertEquals(0L, state.position)

        // Simulate Play
        isPlayingFlow.value = true
        state = repository.playbackState.first { it.isPlaying }
        assertTrue(state.isPlaying)

        // Simulate Metadata change
        val meta = MediaMetadata(id = "test_id", title = "Test Song", artists = emptyList(), duration = 200)
        metaFlow.value = meta
        state = repository.playbackState.first { it.currentSong?.id == "test_id" }
        assertEquals("Test Song", state.currentSong?.title)

        // Simulate AI Commentary
        aiCommentaryFlow.value = "Neural logic active."
        state = repository.playbackState.first { it.aiDjCommentary != null }
        assertEquals("Neural logic active.", state.aiDjCommentary)
        
        // Ensure state is unified
        assertEquals("test_id", state.currentSong?.id)
        assertTrue(state.isPlaying)
    }
}
