
package echo.music.iad1tya.listentogether

import android.content.Context
import echo.music.iad1tya.playback.PlayerConnection
import echo.music.iad1tya.playback.MusicService
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import echo.music.iad1tya.utils.dataStore
import kotlinx.coroutines.Dispatchers
import android.util.Log

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy

@OptIn(ExperimentalCoroutinesApi::class)
class ListenTogetherSyncTest {

    private val context = mockk<Context>(relaxed = true)
    private val client = mockk<ListenTogetherClient>(relaxed = true)
    private val playerConnection = mockk<PlayerConnection>(relaxed = true)
    private val player = mockk<ExoPlayer>(relaxed = true)
    private val musicService = mockk<MusicService>(relaxed = true)
    private val dataStore = mockk<DataStore<Preferences>>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    
    private val roleFlow = MutableStateFlow(RoomRole.NONE)
    private val roomStateFlow = MutableStateFlow<RoomState?>(null)
    private val eventsFlow = MutableStateFlow<ListenTogetherEvent>(ListenTogetherEvent.Disconnected)
    private val isInRoomFlow = MutableStateFlow(false)
    private val isHostFlow = MutableStateFlow(false)
    
    private lateinit var manager: ListenTogetherManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.isLoggable(any(), any()) } returns true

        mockkStatic("echo.music.iad1tya.utils.DataStoreKt")
        every { context.dataStore } returns dataStore
        every { dataStore.data } returns flowOf(mockk(relaxed = true))

        // Setup initial client state
        roleFlow.value = RoomRole.NONE
        roomStateFlow.value = null
        eventsFlow.value = ListenTogetherEvent.Disconnected
        isInRoomFlow.value = false
        isHostFlow.value = false
        
        every { client.events } returns eventsFlow
        every { client.roomState } returns roomStateFlow
        every { client.role } returns roleFlow
        every { client.isInRoom } answers { isInRoomFlow.value }
        every { client.isHost } answers { isHostFlow.value }
        every { client.userId } returns MutableStateFlow("user123")
        every { client.connectionState } returns MutableStateFlow(ConnectionState.DISCONNECTED)
        every { client.pendingJoinRequests } returns MutableStateFlow(emptyList())
        every { client.bufferingUsers } returns MutableStateFlow(emptyList())
        every { client.logs } returns MutableStateFlow(emptyList())
        every { client.blockedUsernames } returns MutableStateFlow(emptySet())
        every { client.pendingSuggestions } returns MutableStateFlow(emptyList())
        every { client.rtt } returns MutableStateFlow(0L)

        every { playerConnection.isMuted } returns MutableStateFlow(false)
        every { playerConnection.service } returns musicService
        every { musicService.playerVolume } returns MutableStateFlow(1f)
        every { playerConnection.player } returns player
        
        manager = ListenTogetherManager(client, context)
        manager.setPlayerConnection(playerConnection)
    }

    @After
    fun tearDown() {
        manager.release()
        Dispatchers.resetMain()
        unmockkStatic("echo.music.iad1tya.utils.DataStoreKt")
        unmockkStatic(Log::class)
    }

    @Test
    fun `test host sends play action to server`() = runTest {
        try {
            roleFlow.value = RoomRole.HOST
            isInRoomFlow.value = true
            isHostFlow.value = true
        
            manager.initialize()
            advanceTimeBy(500)
        
            val listenerSlot = slot<Player.Listener>()
            verify { player.addListener(capture(listenerSlot)) }
        
            every { player.currentPosition } returns 5000L
            every { player.playWhenReady } returns true
        
            listenerSlot.captured.onPlayWhenReadyChanged(true, Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST)
            advanceTimeBy(500)
        
            coVerify { client.sendPlaybackAction(PlaybackActions.PLAY, position = 5000L) }
        } finally {
            manager.release()
        }
    }

    @Test
    fun `test guest playback restriction`() = runTest {
        try {
            roleFlow.value = RoomRole.GUEST
            isInRoomFlow.value = true
            isHostFlow.value = false
            roomStateFlow.value = RoomState(
                roomCode = "ABCDEF",
                hostId = "host123",
                users = emptyList(),
                isPlaying = false,
                position = 0L,
                lastUpdate = System.currentTimeMillis(),
                allowParticipantControl = false
            )
        
            manager.initialize()
            advanceTimeBy(500)
        
            val blockCallbacks = mutableListOf<() -> Boolean>()
            verify(atLeast = 1) { playerConnection.shouldBlockPlaybackChanges = capture(blockCallbacks) }
        
            assertTrue(blockCallbacks.last().invoke())
        } finally {
            manager.release()
        }
    }
}
