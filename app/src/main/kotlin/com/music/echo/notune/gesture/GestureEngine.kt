package com.music.echo.notune.gesture

import echo.music.iad1tya.notune.ai.tools.AiToolRegistry
import echo.music.iad1tya.playback.PlayerConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Universal gesture control bus and execution engine.
 * Maps context-aware touch, multi-finger, drawn shape, and motion gestures
 * to NØTUNE domain tools and Media3 playback capabilities.
 */
@Singleton
class GestureEngine @Inject constructor(
    private val toolRegistry: AiToolRegistry,
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _currentContext = MutableStateFlow(GestureContext.PLAYER)
    val currentContext: StateFlow<GestureContext> = _currentContext.asStateFlow()

    private val _settings = MutableStateFlow(GestureSettings())
    val settings: StateFlow<GestureSettings> = _settings.asStateFlow()

    private val _lastActionTrace = MutableStateFlow<GestureAction?>(null)
    val lastActionTrace: StateFlow<GestureAction?> = _lastActionTrace.asStateFlow()

    private var lastGestureTimestampMs = 0L

    /**
     * Updates the current active gesture context (e.g. switching from PLAYER to LYRICS).
     */
    fun setContext(context: GestureContext) {
        _currentContext.value = context
    }

    /**
     * Updates gesture engine settings (sensitivities, feature toggles).
     */
    fun updateSettings(newSettings: GestureSettings) {
        _settings.value = newSettings
    }

    /**
     * Processes an incoming raw gesture event and resolves it against the current context.
     */
    fun processGesture(
        event: GestureEvent,
        player: PlayerConnection? = null,
        onToggleLyrics: (() -> Unit)? = null,
        onToggleQueue: (() -> Unit)? = null,
        onOpenSearch: (() -> Unit)? = null,
        onOpenSettings: (() -> Unit)? = null,
        onOpenAi: (() -> Unit)? = null,
        onShowDetails: (() -> Unit)? = null,
        onAddToPlaylist: (() -> Unit)? = null,
        onOpenSleepTimer: (() -> Unit)? = null,
        onAiExplain: (() -> Unit)? = null,
    ): GestureAction {
        val currentSettings = _settings.value
        if (!currentSettings.isGestureSystemEnabled) return GestureAction.None

        val now = System.currentTimeMillis()
        if (now - lastGestureTimestampMs < currentSettings.gestureCooldownMs) {
            return GestureAction.None
        }
        lastGestureTimestampMs = now

        // Check feature toggles
        if (event is GestureEvent.ShakeDevice && !currentSettings.isShakeToShuffleEnabled) {
            return GestureAction.None
        }
        if (event is GestureEvent.DrawnShape && !currentSettings.isDrawnShapesEnabled) {
            return GestureAction.None
        }
        if ((event is GestureEvent.EqPinch || event is GestureEvent.EqHorizontal || event is GestureEvent.EqVertical) && !currentSettings.isGestureEqEnabled) {
            return GestureAction.None
        }

        // Resolve action based on current context
        val action = GestureMapping.resolveAction(event, _currentContext.value)
        _lastActionTrace.value = action

        // Execute action against domain tools and player
        executeActionInternal(
            action = action,
            player = player,
            onToggleLyrics = onToggleLyrics,
            onToggleQueue = onToggleQueue,
            onOpenSearch = onOpenSearch,
            onOpenSettings = onOpenSettings,
            onOpenAi = onOpenAi,
            onShowDetails = onShowDetails,
            onAddToPlaylist = onAddToPlaylist,
            onOpenSleepTimer = onOpenSleepTimer,
            onAiExplain = onAiExplain
        )

        return action
    }

    /**
     * Helper to classify a stroke path of 2D points and immediately process the resulting drawn shape gesture.
     */
    fun processStrokePoints(
        points: List<GesturePoint>,
        player: PlayerConnection? = null,
        onOpenAi: (() -> Unit)? = null,
        onToggleLyrics: (() -> Unit)? = null,
        onToggleQueue: (() -> Unit)? = null,
    ): GestureAction {
        val shapeEvent = GestureClassifier.classifyStrokePath(points, _settings.value.strokeConfidenceThreshold)
            ?: return GestureAction.None
        return processGesture(
            event = shapeEvent,
            player = player,
            onOpenAi = onOpenAi,
            onToggleLyrics = onToggleLyrics,
            onToggleQueue = onToggleQueue
        )
    }

    private fun executeActionInternal(
        action: GestureAction,
        player: PlayerConnection?,
        onToggleLyrics: (() -> Unit)?,
        onToggleQueue: (() -> Unit)?,
        onOpenSearch: (() -> Unit)?,
        onOpenSettings: (() -> Unit)?,
        onOpenAi: (() -> Unit)?,
        onShowDetails: (() -> Unit)?,
        onAddToPlaylist: (() -> Unit)?,
        onOpenSleepTimer: (() -> Unit)?,
        onAiExplain: (() -> Unit)?,
    ) {
        when (action) {
            is GestureAction.None -> Unit

            is GestureAction.NextTrack -> player?.seekToNext()
            is GestureAction.PreviousTrack -> player?.seekToPrevious()
            is GestureAction.TogglePlayPause -> player?.togglePlayPause()

            is GestureAction.SeekBy -> {
                player?.let { p ->
                    val pos = p.position.value
                    p.seekTo((pos + action.deltaMs).coerceAtLeast(0L))
                }
            }

            is GestureAction.VolumeUp -> {
                scope.launch {
                    toolRegistry.executeTool("set_volume", mapOf("volume" to 1.0f))
                }
            }
            is GestureAction.VolumeDown -> {
                scope.launch {
                    toolRegistry.executeTool("set_volume", mapOf("volume" to 0.5f))
                }
            }

            is GestureAction.ToggleFavorite -> scope.launch { player?.toggleLike() }
            is GestureAction.FavoriteWithAnimation -> scope.launch { player?.toggleLike() }

            is GestureAction.ToggleQueue -> onToggleQueue?.invoke()
            is GestureAction.ToggleLyrics -> onToggleLyrics?.invoke()
            is GestureAction.OpenSettings -> onOpenSettings?.invoke()
            is GestureAction.OpenSearch -> onOpenSearch?.invoke()
            is GestureAction.OpenTrackActions -> onShowDetails?.invoke()
            is GestureAction.SleepTimer -> onOpenSleepTimer?.invoke()
            is GestureAction.AiExplain -> onAiExplain?.invoke()
            is GestureAction.OpenAi -> onOpenAi?.invoke()

            is GestureAction.ActivateAiDj -> onOpenAi?.invoke()
            is GestureAction.AiDiscovery -> onOpenAi?.invoke()
            is GestureAction.AiFlow -> onOpenAi?.invoke()
            is GestureAction.AiFindGems -> onOpenAi?.invoke()
            is GestureAction.AskNotune -> onOpenAi?.invoke()

            is GestureAction.ShuffleQueue -> scope.launch {
                toolRegistry.executeTool("shuffle_queue", emptyMap())
            }

            is GestureAction.ExecuteMacro -> {
                val macro = GestureMacroEngine.getMacro(action.macroId)
                macro?.actions?.forEach { subAction ->
                    executeActionInternal(
                        action = subAction,
                        player = player,
                        onToggleLyrics = onToggleLyrics,
                        onToggleQueue = onToggleQueue,
                        onOpenSearch = onOpenSearch,
                        onOpenSettings = onOpenSettings,
                        onOpenAi = onOpenAi,
                        onShowDetails = onShowDetails,
                        onAddToPlaylist = onAddToPlaylist,
                        onOpenSleepTimer = onOpenSleepTimer,
                        onAiExplain = onAiExplain
                    )
                }
            }

            is GestureAction.ExecuteAiTool -> scope.launch {
                toolRegistry.executeTool(action.toolId, action.params)
            }

            else -> Unit
        }
    }
}
