package echo.music.iad1tya.notune.ai.adaptive

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Integration bridge connecting real ExoPlayer / Media3 playback events
 * to NØ AI Adaptive Queue Manager & Scoring Engine.
 */
@Singleton
class AdaptivePlaybackBridge @Inject constructor(
    val recommendationEngine: AdaptiveRecommendationEngine
) {
    private val bridgeScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _currentSessionId = MutableStateFlow(UUID.randomUUID().toString())
    val currentSessionId: StateFlow<String> = _currentSessionId.asStateFlow()

    private val _currentUserIntent = MutableStateFlow(UserIntent())
    val currentUserIntent: StateFlow<UserIntent> = _currentUserIntent.asStateFlow()

    private val _isRoomModeActive = MutableStateFlow(false)
    val isRoomModeActive: StateFlow<Boolean> = _isRoomModeActive.asStateFlow()

    private var lastEmittedEventKey: String? = null

    // Track played history in session
    private val sessionPlayedHistory = mutableListOf<AdaptiveTrackContext>()

    /**
     * Set active user intent (e.g. user selected an explicit Album, Playlist, Artist, or Song).
     */
    fun setUserIntent(intent: UserIntent) {
        _currentUserIntent.value = intent
    }

    /**
     * Enable/disable Listen Together room authority guard.
     */
    fun setRoomModeActive(active: Boolean) {
        _isRoomModeActive.value = active
    }

    /**
     * Start a new listening session ID.
     */
    fun startNewSession(sessionId: String = UUID.randomUUID().toString()) {
        _currentSessionId.value = sessionId
        sessionPlayedHistory.clear()
        recommendationEngine.queueManager.resetSession()
    }

    /**
     * Emit a playback feedback event safely on a background thread.
     * Prevents duplicate event emissions and updates session history.
     */
    fun onPlaybackEvent(
        eventType: FeedbackEventType,
        track: AdaptiveTrackContext,
        positionMs: Long = 0L,
        durationMs: Long = 0L,
        context: String? = null
    ) {
        val eventKey = "${eventType.name}_${track.trackId}_${positionMs / 1000}"
        if (eventKey == lastEmittedEventKey && eventType != FeedbackEventType.PLAY) {
            return // Prevent duplicate event emission
        }
        lastEmittedEventKey = eventKey

        val completionPercent = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

        val event = FeedbackEvent(
            eventType = eventType,
            trackId = track.trackId,
            sessionId = _currentSessionId.value,
            positionMs = positionMs,
            completionPercent = completionPercent,
            context = context ?: _currentUserIntent.value.targetName
        )

        if (eventType == FeedbackEventType.PLAY || eventType == FeedbackEventType.COMPLETE) {
            synchronized(sessionPlayedHistory) {
                if (sessionPlayedHistory.none { it.trackId == track.trackId }) {
                    sessionPlayedHistory.add(track)
                }
            }
        }

        // Record feedback safely in background
        bridgeScope.launch {
            recommendationEngine.queueManager.recordFeedback(event)
        }
    }

    /**
     * Asynchronously recalculate and adapt the upcoming queue on background thread.
     * Guaranteed never to execute on the main/UI thread.
     * Preserves currently playing track and room sync authority.
     */
    suspend fun adaptQueueAsync(
        currentTrack: AdaptiveTrackContext?,
        remainingQueue: List<AdaptiveTrackContext>,
        candidatePool: List<AdaptiveTrackContext>
    ): List<AdaptiveTrackContext> = withContext(Dispatchers.Default) {
        // Room authority guard: Do not let single client mutate shared room queue independently
        if (_isRoomModeActive.value) {
            return@withContext remainingQueue
        }

        val request = RecommendationRequest(
            sessionId = _currentSessionId.value,
            currentTrack = currentTrack,
            currentQueue = remainingQueue,
            history = sessionPlayedHistory.toList(),
            skippedTracks = emptyList(), // Handled internally by queueManager
            replayedTracks = emptyList(),
            userIntent = _currentUserIntent.value
        )

        recommendationEngine.adaptActiveQueue(
            currentTrack = currentTrack,
            remainingQueue = remainingQueue,
            candidatePool = candidatePool,
            history = sessionPlayedHistory.toList()
        )
    }
}
