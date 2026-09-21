

package echo.music.iad1tya.playback

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import echo.music.iad1tya.utils.PlaybackLogManager
import echo.music.iad1tya.utils.PlaybackLogLevel
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.extensions.currentMetadata
import echo.music.iad1tya.extensions.getCurrentQueueIndex
import echo.music.iad1tya.extensions.getQueueWindows
import echo.music.iad1tya.extensions.metadata
import echo.music.iad1tya.extensions.togglePlayPause
import echo.music.iad1tya.playback.MusicService.MusicBinder
import echo.music.iad1tya.playback.queues.Queue
import echo.music.iad1tya.utils.reportException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import echo.music.iad1tya.constants.SponsorBlockEnabledKey
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.data.SponsorBlockRepository
import echo.music.iad1tya.models.SponsorBlockSegment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Job

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerConnection(
    val context: Context,
    binder: MusicBinder,
    val database: MusicDatabase,
    val scope: CoroutineScope,
) : Player.Listener {
    private companion object {
        private const val TAG = "PlayerConnection"
        private const val PLAYER_INIT_TIMEOUT_MS = 5000L 
    }

    val service = binder.service
    private val playerReadinessFlow = service.isPlayerReady
    
    
    private fun getPlayerSafe(): ExoPlayer {
        return try {
            if (!playerReadinessFlow.value) {
                Timber.tag(TAG).w("Player accessed before service initialization complete; returning best-effort reference")
            }
            service.player
        } catch (e: UninitializedPropertyAccessException) {
            Timber.tag(TAG).e(e, "Fatal: player property accessed but not initialized")
            throw IllegalStateException("MusicService.player not initialized; possible race condition in service startup", e)
        }
    }

    
    val player: ExoPlayer
        get() = getPlayerSafe()

    
    private val isPlayerInitialized = MutableStateFlow(service.isPlayerReady.value)

    val playbackState: MutableStateFlow<Int>
    private val playWhenReady: MutableStateFlow<Boolean>
    val isPlaying: kotlinx.coroutines.flow.StateFlow<Boolean>
    
    init {
        Timber.tag(TAG).d("PlayerConnection init: playerReady=${playerReadinessFlow.value}")
        
        
        val initialState = try {
            val initialPlayer = getPlayerSafe()
            Triple(initialPlayer.playbackState, initialPlayer.playWhenReady, 
                   initialPlayer.playWhenReady && initialPlayer.playbackState != STATE_ENDED)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error during PlayerConnection initialization, using defaults")
            Triple(Player.STATE_IDLE, false, false)
        }
        
        playbackState = MutableStateFlow(initialState.first)
        playWhenReady = MutableStateFlow(initialState.second)
        isPlaying = combine(playbackState, playWhenReady) { state, ready ->
            ready && state != STATE_ENDED
        }.stateIn(
            scope,
            SharingStarted.Lazily,
            initialState.third
        )
        
        
        scope.launch {
            playerReadinessFlow.collect { ready ->
                isPlayerInitialized.value = ready
                if (ready) {
                    Timber.tag(TAG).d("Service player initialization detected by PlayerConnection")
                }
            }
        }
        
        Timber.tag(TAG).d("PlayerConnection state flows initialized successfully")
    }
    
    
    val isEffectivelyPlaying = combine(
        isPlaying,
        service.castConnectionHandler?.isCasting ?: MutableStateFlow(false),
        service.castConnectionHandler?.castIsPlaying ?: MutableStateFlow(false)
    ) { localPlaying, isCasting, castPlaying ->
        if (isCasting) castPlaying else localPlaying
    }.stateIn(
        scope,
        SharingStarted.Lazily,
        player.playbackState != STATE_ENDED && player.playWhenReady
    )
    
    val mediaMetadata = MutableStateFlow(player.currentMetadata)
    val currentSong =
        mediaMetadata.flatMapLatest {
            database.song(it?.id)
        }
    val currentLyrics = mediaMetadata.flatMapLatest { mediaMetadata ->
        database.lyrics(mediaMetadata?.id)
    }
    val currentFormat =
        mediaMetadata.flatMapLatest { mediaMetadata ->
            database.format(mediaMetadata?.id)
        }

    val queueTitle = MutableStateFlow<String?>(null)
    val queueWindows = MutableStateFlow<List<Timeline.Window>>(emptyList())
    val currentMediaItemIndex = MutableStateFlow(-1)
    val currentWindowIndex = MutableStateFlow(-1)

    val shuffleModeEnabled = MutableStateFlow(false)
    val repeatMode = MutableStateFlow(REPEAT_MODE_OFF)

    val canSkipPrevious = MutableStateFlow(true)
    val canSkipNext = MutableStateFlow(true)

    val error = MutableStateFlow<PlaybackException?>(null)
    val isMuted = service.isMuted

    val position = kotlinx.coroutines.flow.flow {
        while (true) {
            if (isPlayerInitialized.value) {
                val currentPos = withContext(Dispatchers.Main.immediate) { player.currentPosition }
                emit(currentPos)
            }
            kotlinx.coroutines.delay(if (isPlaying.value) 1000L else 5000L)
        }
    }.stateIn(scope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), 0L)

    val duration = kotlinx.coroutines.flow.flow {
        while (true) {
            if (isPlayerInitialized.value) {
                val currentDur = withContext(Dispatchers.Main.immediate) { player.duration.coerceAtLeast(0L) }
                emit(currentDur)
            }
            kotlinx.coroutines.delay(if (isPlaying.value) 2000L else 10000L)
        }
    }.stateIn(scope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), 0L)

    val waitingForNetworkConnection = service.waitingForNetworkConnection
    val isCrossfading: kotlinx.coroutines.flow.StateFlow<Boolean> = service.isCrossfading
    val isAutomixing: kotlinx.coroutines.flow.StateFlow<Boolean> = service.isAutomixing
    val automixDebugInfo: kotlinx.coroutines.flow.StateFlow<MusicService.AutomixDebugInfo?> = service.automixDebugInfo
    val aiDjCommentary: kotlinx.coroutines.flow.StateFlow<String?> = service.aiDjCommentary
    val technicalTelemetry: kotlinx.coroutines.flow.StateFlow<echo.music.iad1tya.models.TechnicalTelemetry> = service.technicalTelemetry

    val audioFormat = MutableStateFlow<androidx.media3.common.Format?>(null)

    // Thread-safe snapshots updated via Player.Listener (always on the main thread).
    // Consume these instead of accessing connection.player.* directly from background threads.
    val audioSessionId = MutableStateFlow<Int?>(null)
    val bufferedPosition = MutableStateFlow(0L)
    val playerVolume = MutableStateFlow(1f)
    val playerQueue = MutableStateFlow<List<echo.music.iad1tya.models.MediaMetadata>>(emptyList())

    var shouldBlockPlaybackChanges: (() -> Boolean)? = null
    
    
    @Volatile
    var allowInternalSync: Boolean = false

    var onSkipPrevious: (() -> Unit)? = null
    var onSkipNext: (() -> Unit)? = null

    private var attachedPlayer: Player? = null
    
    private val sponsorBlockRepository = SponsorBlockRepository()
    private var sponsorBlockJob: Job? = null
    private val sponsorBlockSegments = MutableStateFlow<List<SponsorBlockSegment>>(emptyList())

    init {
        try {
            
            scope.launch(Dispatchers.Main.immediate) {
                service.playerFlow.collect { newPlayer ->
                    if (newPlayer != null && newPlayer != attachedPlayer) {
                        updateAttachedPlayer(newPlayer)
                    }
                }
            }
            
            
            if (attachedPlayer == null && service.isPlayerReady.value) {
                 updateAttachedPlayer(player)
            }

            Timber.tag(TAG).d("PlayerConnection flow observer registered")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to initialize PlayerConnection listener or state")
            
            throw e
        }
    }

    private fun updateAttachedPlayer(newPlayer: Player) {
        attachedPlayer?.removeListener(this)
        attachedPlayer = newPlayer
        newPlayer.addListener(this)
        
        playbackState.value = newPlayer.playbackState
        playWhenReady.value = newPlayer.playWhenReady
        mediaMetadata.value = newPlayer.currentMetadata
        queueTitle.value = service.queueTitle
        queueWindows.value = newPlayer.getQueueWindows()
        currentWindowIndex.value = newPlayer.getCurrentQueueIndex()
        currentMediaItemIndex.value = newPlayer.currentMediaItemIndex
        shuffleModeEnabled.value = newPlayer.shuffleModeEnabled
        repeatMode.value = newPlayer.repeatMode

        val audioTrack = newPlayer.currentTracks.groups.firstOrNull {
            it.type == androidx.media3.common.C.TRACK_TYPE_AUDIO && it.isSelected
        }
        audioFormat.value = audioTrack?.getTrackFormat(0)

        // Snapshot thread-safe properties (called on main thread from service playerFlow)
        syncPlayerSnapshot(newPlayer)

        Timber.tag(TAG).d("Attached to new player instance: $newPlayer")
        
        startSponsorBlockPolling()
    }

    /**
     * Refresh the thread-safe snapshot flows from the player.
     * MUST be called only on the main thread (from Player.Listener callbacks or service init).
     */
    private fun syncPlayerSnapshot(p: Player) {
        val sessionId = try {
            (p as? ExoPlayer)?.audioSessionId
                ?.takeIf { it != androidx.media3.common.C.AUDIO_SESSION_ID_UNSET }
        } catch (_: Throwable) { null }
        audioSessionId.value = sessionId

        bufferedPosition.value = try { p.bufferedPosition } catch (_: Throwable) { 0L }
        playerVolume.value = try { p.volume } catch (_: Throwable) { 1f }
        playerQueue.value = try {
            val list = mutableListOf<echo.music.iad1tya.models.MediaMetadata>()
            val timeline = p.currentTimeline
            for (i in 0 until timeline.windowCount) {
                timeline.getWindow(i, androidx.media3.common.Timeline.Window())
                    .mediaItem.metadata?.let { list.add(it) }
            }
            list
        } catch (_: Throwable) { emptyList() }
    }

    fun playQueue(queue: Queue) {
        if (!playerReadinessFlow.value) {
            Timber.tag(TAG).w("playQueue called before player ready; delegating to service")
        }
        try {
            service.playQueue(queue)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in playQueue")
            throw e
        }
    }

    fun startRadioSeamlessly() {
        
        if (shouldBlockPlaybackChanges?.invoke() == true) {
            Timber.tag("PlayerConnection").d("startRadioSeamlessly blocked - Listen Together guest")
            return
        }
        if (!playerReadinessFlow.value) {
            Timber.tag(TAG).w("startRadioSeamlessly called before player ready; delegating to service")
        }
        try {
            service.startRadioSeamlessly()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in startRadioSeamlessly")
            throw e
        }
    }

    fun playNext(item: MediaItem) = playNext(listOf(item))

    fun playNext(items: List<MediaItem>) {
        
        if (!allowInternalSync && shouldBlockPlaybackChanges?.invoke() == true) {
            Timber.tag("PlayerConnection").d("playNext blocked - Listen Together guest")
            return
        }
        try {
            service.playNext(items)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in playNext")
            throw e
        }
    }

    fun addToQueue(item: MediaItem) = addToQueue(listOf(item))

    fun addToQueue(items: List<MediaItem>) {
        
        if (!allowInternalSync && shouldBlockPlaybackChanges?.invoke() == true) {
            Timber.tag("PlayerConnection").d("addToQueue blocked - Listen Together guest")
            return
        }
        try {
            service.addToQueue(items)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in addToQueue")
            throw e
        }
    }

    fun toggleLike() {
        try {
            service.toggleLike()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in toggleLike")
        }
    }

    fun toggleMute() {
        service.toggleMute()
    }

    fun setMuted(muted: Boolean) {
        service.setMuted(muted)
    }

    fun toggleLibrary() {
        try {
            service.toggleLibrary()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in toggleLibrary")
        }
    }

    fun setVolume(volume: Float) {
        runOnMain {
            try { player.volume = volume } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error in setVolume")
            }
        }
    }

    fun setShuffleModeEnabled(enabled: Boolean) {
        runOnMain {
            try { player.shuffleModeEnabled = enabled } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error in setShuffleModeEnabled")
            }
        }
    }

    fun setRepeatMode(mode: Int) {
        runOnMain {
            try { player.repeatMode = mode } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error in setRepeatMode")
            }
        }
    }

    
    private inline fun <T> runOnMain(crossinline block: () -> T): T {
        return if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper() || android.os.Looper.getMainLooper() == null) {
            block()
        } else {
            try {
                kotlinx.coroutines.runBlocking(Dispatchers.Main.immediate) {
                    block()
                }
            } catch (e: Throwable) {
                block()
            }
        }
    }

    fun togglePlayPause() {
        runOnMain {
            try {
                val castHandler = service.castConnectionHandler
                if (castHandler?.isCasting?.value == true) {
                    if (castHandler.castIsPlaying.value) {
                        castHandler.pause()
                    } else {
                        castHandler.play()
                    }
                } else {
                    player.togglePlayPause()
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error in togglePlayPause")
            }
        }
    }
    
    fun play() {
        runOnMain {
            try {
                val castHandler = service.castConnectionHandler
                if (castHandler?.isCasting?.value == true) {
                    castHandler.play()
                } else {
                    if (player.playbackState == Player.STATE_IDLE) {
                        player.prepare()
                    }
                    player.playWhenReady = true
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error in play")
            }
        }
    }
    
    fun pause() {
        runOnMain {
            try {
                val castHandler = service.castConnectionHandler
                if (castHandler?.isCasting?.value == true) {
                    castHandler.pause()
                } else {
                    player.playWhenReady = false
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error in pause")
            }
        }
    }

    fun seekTo(position: Long) {
        runOnMain {
            try {
                val castHandler = service.castConnectionHandler
                if (castHandler?.isCasting?.value == true) {
                    castHandler.seekTo(position)
                } else {
                    player.seekTo(position)
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error in seekTo")
            }
        }
    }

    fun seekToNext() {
        runOnMain {
            try {
                val castHandler = service.castConnectionHandler
                if (castHandler?.isCasting?.value == true) {
                    castHandler.skipToNext()
                    return@runOnMain
                }
                player.seekToNext()
                if (player.playbackState == Player.STATE_IDLE || player.playbackState == Player.STATE_ENDED) {
                    player.prepare()
                }
                player.playWhenReady = true
                onSkipNext?.invoke()
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error in seekToNext")
            }
        }
    }

    var onRestartSong: (() -> Unit)? = null

    fun seekToPrevious() {
        runOnMain {
            try {
                val castHandler = service.castConnectionHandler
                if (castHandler?.isCasting?.value == true) {
                    castHandler.skipToPrevious()
                    return@runOnMain
                }

                if (player.currentPosition > 3000 || !player.hasPreviousMediaItem()) {
                    player.seekTo(0)
                    if (player.playbackState == Player.STATE_IDLE || player.playbackState == Player.STATE_ENDED) {
                        player.prepare()
                    }
                    player.playWhenReady = true
                    onRestartSong?.invoke()
                } else {
                    player.seekToPreviousMediaItem()
                    if (player.playbackState == Player.STATE_IDLE || player.playbackState == Player.STATE_ENDED) {
                        player.prepare()
                    }
                    player.playWhenReady = true
                    onSkipPrevious?.invoke()
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error in seekToPrevious")
            }
        }
    }

    override fun onPlaybackStateChanged(state: Int) {
        playbackState.value = state
        error.value = player.playerError
        syncPlayerSnapshot(player)
    }

    override fun onPlayWhenReadyChanged(
        newPlayWhenReady: Boolean,
        reason: Int,
    ) {
        playWhenReady.value = newPlayWhenReady
        syncPlayerSnapshot(player)
    }

    override fun onMediaItemTransition(
        mediaItem: MediaItem?,
        reason: Int,
    ) {
        mediaMetadata.value = mediaItem?.metadata
        currentMediaItemIndex.value = player.currentMediaItemIndex
        currentWindowIndex.value = player.getCurrentQueueIndex()
        updateCanSkipPreviousAndNext()
        syncPlayerSnapshot(player)
    }

    override fun onTimelineChanged(
        timeline: Timeline,
        reason: Int,
    ) {
        mediaMetadata.value = player.currentMediaItem?.metadata
        queueWindows.value = player.getQueueWindows()
        queueTitle.value = service.queueTitle
        currentMediaItemIndex.value = player.currentMediaItemIndex
        currentWindowIndex.value = player.getCurrentQueueIndex()
        updateCanSkipPreviousAndNext()
        syncPlayerSnapshot(player)
    }

    override fun onShuffleModeEnabledChanged(enabled: Boolean) {
        shuffleModeEnabled.value = enabled
        queueWindows.value = player.getQueueWindows()
        currentWindowIndex.value = player.getCurrentQueueIndex()
        updateCanSkipPreviousAndNext()
        syncPlayerSnapshot(player)
    }

    override fun onRepeatModeChanged(mode: Int) {
        repeatMode.value = mode
        updateCanSkipPreviousAndNext()
    }

    override fun onTracksChanged(tracks: androidx.media3.common.Tracks) {
        val audioTrack = tracks.groups.firstOrNull { it.type == androidx.media3.common.C.TRACK_TYPE_AUDIO && it.isSelected }
        audioFormat.value = audioTrack?.getTrackFormat(0)
    }

    override fun onPlayerErrorChanged(playbackError: PlaybackException?) {
        if (playbackError != null) {
            reportException(playbackError)
            PlaybackLogManager.log(PlaybackLogLevel.ERROR, "Player Error", playbackError.message)
        }
        error.value = playbackError
    }

    private fun updateCanSkipPreviousAndNext() {
        if (!player.currentTimeline.isEmpty && player.currentMediaItemIndex != androidx.media3.common.C.INDEX_UNSET && player.currentMediaItemIndex >= 0 && player.currentMediaItemIndex < player.currentTimeline.windowCount) {
            val window =
                player.currentTimeline.getWindow(player.currentMediaItemIndex, Timeline.Window())
            canSkipPrevious.value = player.isCommandAvailable(COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM) ||
                    !window.isLive ||
                    player.isCommandAvailable(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
            canSkipNext.value = window.isLive &&
                    window.isDynamic ||
                    player.isCommandAvailable(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
        } else {
            canSkipPrevious.value = false
            canSkipNext.value = false
        }
    }

    private fun startSponsorBlockPolling() {
        sponsorBlockJob?.cancel()
        sponsorBlockJob = scope.launch(Dispatchers.Main.immediate) {
            val sponsorBlockEnabledFlow = context.dataStore.data.map { (try { it[SponsorBlockEnabledKey] } catch(e: Exception) { null }) ?: false }

            launch(Dispatchers.IO) {
                combine(mediaMetadata, sponsorBlockEnabledFlow) { metadata, enabled ->
                    Pair(metadata, enabled)
                }.collect { (metadata, enabled) ->
                    if (enabled && metadata != null) {
                        val videoId = metadata.id
                        sponsorBlockSegments.value = sponsorBlockRepository.getSkipSegments(videoId)
                    } else {
                        sponsorBlockSegments.value = emptyList()
                    }
                }
            }

            launch(Dispatchers.Main.immediate) {
                while (true) {
                    if (player.isPlaying) {
                        val currentSegments = sponsorBlockSegments.value
                        if (currentSegments.isNotEmpty()) {
                            val currentPosSec = player.currentPosition / 1000f
                            for (segment in currentSegments) {
                                if (currentPosSec >= segment.segment[0] && currentPosSec < segment.segment[1]) {
                                    Timber.d("SponsorBlock: skipping segment ${segment.category} from ${segment.segment[0]} to ${segment.segment[1]}")
                                    player.seekTo((segment.segment[1] * 1000).toLong())
                                    break
                                }
                            }
                        }
                    }
                    delay(500)
                }
            }
        }
    }

    fun dispose() {
        try {
            sponsorBlockJob?.cancel()
            attachedPlayer?.removeListener(this)
            attachedPlayer = null
            Timber.tag(TAG).d("PlayerConnection disposed successfully")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error during PlayerConnection disposal")
        }
    }
}