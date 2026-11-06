package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.models.MediaMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlowEngine @Inject constructor(
    private val candidateGenerator: FlowCandidateGenerator,
    private val candidateFilter: FlowCandidateFilter,
    private val candidateScorer: FlowCandidateScorer,
    private val queueOptimizer: FlowQueueOptimizer,
    private val feedbackProcessor: FlowFeedbackProcessor,
    private val languageSessionTracker: LanguageSessionTracker
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val generationIdCounter = AtomicLong(1L)
    private val generationMutex = Mutex()

    private val _flowState = MutableStateFlow(FlowState())
    val flowState: StateFlow<FlowState> = _flowState.asStateFlow()

    private val _flowQueue = MutableStateFlow<List<FlowQueueItem>>(emptyList())
    val flowQueue: StateFlow<List<FlowQueueItem>> = _flowQueue.asStateFlow()

    fun updateConfig(
        enabled: Boolean = _flowState.value.enabled,
        mode: FlowMode = _flowState.value.mode,
        contextMode: FlowContextMode = _flowState.value.contextMode,
        discoveryRatio: Float = _flowState.value.discoveryRatio
    ) {
        _flowState.value = _flowState.value.copy(
            enabled = enabled,
            mode = mode,
            contextMode = contextMode,
            discoveryRatio = discoveryRatio
        )
    }

    fun onTrackTransition(
        currentTrack: MediaMetadata?,
        upcomingQueueTrackIds: List<String>,
        onQueueRefillReady: (List<FlowQueueItem>) -> Unit
    ) {
        if (!_flowState.value.enabled) return
        val currentId = currentTrack?.id ?: return

        _flowState.value = _flowState.value.copy(currentTrackId = currentId)

        scope.launch {
            if (queueOptimizer.shouldRefillQueue(upcomingQueueTrackIds.size)) {
                generateAndOptimizeQueue(currentTrack, upcomingQueueTrackIds, onQueueRefillReady)
            }
        }
    }

    fun onUserSkip(
        currentTrack: MediaMetadata?,
        playedDurationMs: Long,
        totalDurationMs: Long,
        upcomingQueueTrackIds: List<String>,
        onQueueRefillReady: (List<FlowQueueItem>) -> Unit
    ) {
        currentTrack?.let {
            feedbackProcessor.recordPlaybackFeedback(it, playedDurationMs, totalDurationMs)
        }
        if (_flowState.value.enabled && currentTrack != null) {
            scope.launch {
                generateAndOptimizeQueue(currentTrack, upcomingQueueTrackIds, onQueueRefillReady)
            }
        }
    }

    fun onSearchPerformed(query: String) {
        feedbackProcessor.recordSearchQuery(query)
    }

    fun onMoreLikeThis(
        currentTrack: MediaMetadata?,
        upcomingQueueTrackIds: List<String>,
        onQueueRefillReady: (List<FlowQueueItem>) -> Unit
    ) {
        currentTrack?.let { feedbackProcessor.recordMoreLikeThis(it) }
        if (_flowState.value.enabled && currentTrack != null) {
            scope.launch {
                generateAndOptimizeQueue(currentTrack, upcomingQueueTrackIds, onQueueRefillReady)
            }
        }
    }

    fun onLessLikeThis(
        currentTrack: MediaMetadata?,
        upcomingQueueTrackIds: List<String>,
        onQueueRefillReady: (List<FlowQueueItem>) -> Unit
    ) {
        currentTrack?.let { feedbackProcessor.recordLessLikeThis(it) }
        if (_flowState.value.enabled && currentTrack != null) {
            scope.launch {
                generateAndOptimizeQueue(currentTrack, upcomingQueueTrackIds, onQueueRefillReady)
            }
        }
    }

    suspend fun generateAndOptimizeQueue(
        currentTrack: MediaMetadata?,
        upcomingQueueTrackIds: List<String>,
        onQueueRefillReady: (List<FlowQueueItem>) -> Unit
    ) = withContext(Dispatchers.Default) {
        generationMutex.withLock {
            val genId = generationIdCounter.incrementAndGet()
            _flowState.value = _flowState.value.copy(
                generationState = GenerationState.GENERATING,
                generationId = genId
            )

            try {
                val currentState = _flowState.value
                val sessionProfile = languageSessionTracker.computeSessionProfile(feedbackProcessor.getRecentLanguages())

                val rawCandidates = candidateGenerator.generateCandidates(
                    currentTrack = currentTrack,
                    mode = currentState.mode,
                    contextMode = currentState.contextMode
                )

                val filteredCandidates = candidateFilter.filterCandidates(
                    candidates = rawCandidates,
                    currentTrack = currentTrack,
                    activeQueueTrackIds = upcomingQueueTrackIds,
                    recentlySkippedIds = feedbackProcessor.getRecentlySkippedTrackIds().keys.toList(),
                    recommendationHistory = feedbackProcessor.getRecommendationHistory()
                )

                val scoredCandidates = candidateScorer.scoreCandidates(
                    candidates = filteredCandidates,
                    currentTrack = currentTrack,
                    mode = currentState.mode,
                    contextMode = currentState.contextMode,
                    discoveryRatio = currentState.discoveryRatio,
                    sessionProfile = sessionProfile,
                    topArtistIds = feedbackProcessor.getBoostedArtistIds(),
                    favoriteTrackIds = feedbackProcessor.getFavoriteTrackIds(),
                    recentlySkippedTrackIds = feedbackProcessor.getRecentlySkippedTrackIds(),
                    recommendationHistory = feedbackProcessor.getRecommendationHistory()
                )

                val diversifiedScores = candidateFilter.diversifyCandidates(scoredCandidates)

                val newQueue = queueOptimizer.buildOptimizedQueue(
                    existingItems = _flowQueue.value,
                    rankedCandidates = diversifiedScores,
                    generationId = genId
                )

                // Record recommendation history for newly recommended tracks
                for (item in newQueue) {
                    feedbackProcessor.recordRecommendation(item.mediaMetadata)
                }

                // Stale result protection
                if (_flowState.value.generationId == genId) {
                    _flowQueue.value = newQueue
                    _flowState.value = _flowState.value.copy(
                        generationState = GenerationState.READY,
                        lastGenerationTime = System.currentTimeMillis()
                    )
                    withContext(Dispatchers.Main) {
                        onQueueRefillReady(newQueue)
                    }
                }
            } catch (e: Exception) {
                _flowState.value = _flowState.value.copy(generationState = GenerationState.FAILED)
            }
        }
    }
}
