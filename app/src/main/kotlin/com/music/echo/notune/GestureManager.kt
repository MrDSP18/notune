
package echo.music.iad1tya.notune

import android.content.Context
import android.media.AudioManager
import androidx.datastore.preferences.core.edit
import echo.music.iad1tya.constants.GestureDoubleTapCenterKey
import echo.music.iad1tya.constants.GestureDoubleTapLeftKey
import echo.music.iad1tya.constants.GestureDoubleTapRightKey
import echo.music.iad1tya.constants.GestureLongPressKey
import echo.music.iad1tya.constants.GestureSwipeDownKey
import echo.music.iad1tya.constants.GestureSwipeLeftKey
import echo.music.iad1tya.constants.GestureSwipeRightKey
import echo.music.iad1tya.constants.GestureSwipeUpKey
import echo.music.iad1tya.constants.GestureTwoFingerTapKey
import echo.music.iad1tya.playback.PlayerConnection
import echo.music.iad1tya.utils.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/** All configurable gesture actions. */
enum class GestureAction {
    NONE,
    NEXT,
    PREVIOUS,
    PLAY_PAUSE,
    SEEK_FORWARD,
    SEEK_BACKWARD,
    VOLUME_UP,
    VOLUME_DOWN,
    TOGGLE_LYRICS,
    TOGGLE_QUEUE,
    LIKE_SONG,
    OPEN_SETTINGS,
    SEARCH,
    SHOW_DETAILS,
    ADD_TO_PLAYLIST,
    SLEEP_TIMER,
    AI_EXPLAIN,
    REPEAT_MODE,
    SHUFFLE_MODE,
    ROOMS_LISTEN_TOGETHER,
    TRANSLATE_LYRICS,
}


/** Gesture slot → action mapping. All defaults match Nothing Phone-inspired UX. */
data class GestureConfig(
    val swipeLeft: GestureAction       = GestureAction.NEXT,
    val swipeRight: GestureAction      = GestureAction.PREVIOUS,
    val swipeUp: GestureAction         = GestureAction.TOGGLE_QUEUE,
    val swipeDown: GestureAction       = GestureAction.NONE,
    val doubleTapCenter: GestureAction = GestureAction.PLAY_PAUSE,
    val doubleTapLeft: GestureAction   = GestureAction.SEEK_BACKWARD,
    val doubleTapRight: GestureAction  = GestureAction.SEEK_FORWARD,
    val longPress: GestureAction       = GestureAction.LIKE_SONG,
    val twoFingerTap: GestureAction    = GestureAction.TOGGLE_LYRICS,
)

/**
 * Repository that persists [GestureConfig] in DataStore and executes
 * [GestureAction]s against the live [PlayerConnection].
 */
@Singleton
class GestureConfigRepository @Inject constructor(
    private val context: Context,
) {
    /** Observe the current gesture config as a Flow. */
    val configFlow: Flow<GestureConfig> = context.dataStore.data.map { prefs ->
        fun action(key: androidx.datastore.preferences.core.Preferences.Key<String>, default: GestureAction): GestureAction =
            prefs[key]?.let { runCatching { GestureAction.valueOf(it) }.getOrNull() } ?: default

        GestureConfig(
            swipeLeft       = action(GestureSwipeLeftKey,       GestureAction.NEXT),
            swipeRight      = action(GestureSwipeRightKey,      GestureAction.PREVIOUS),
            swipeUp         = action(GestureSwipeUpKey,         GestureAction.TOGGLE_QUEUE),
            swipeDown       = action(GestureSwipeDownKey,       GestureAction.NONE),
            doubleTapCenter = action(GestureDoubleTapCenterKey, GestureAction.PLAY_PAUSE),
            doubleTapLeft   = action(GestureDoubleTapLeftKey,   GestureAction.SEEK_BACKWARD),
            doubleTapRight  = action(GestureDoubleTapRightKey,  GestureAction.SEEK_FORWARD),
            longPress       = action(GestureLongPressKey,       GestureAction.LIKE_SONG),
            twoFingerTap    = action(GestureTwoFingerTapKey,    GestureAction.TOGGLE_LYRICS),
        )
    }

    /** Persist a changed gesture mapping. */
    suspend fun setGesture(
        key: androidx.datastore.preferences.core.Preferences.Key<String>,
        action: GestureAction,
    ) {
        context.dataStore.edit { prefs -> prefs[key] = action.name }
    }
}

/** Stateless executor — turns a [GestureAction] into a side effect on the player/system. */
object GestureManager {

    /**
     * Executes a [GestureAction] against the live player.
     *
     * @param action   The action to perform.
     * @param player   Live [PlayerConnection] for playback control.
     * @param scope    Coroutine scope for async operations.
     * @param onToggleLyrics   Lambda to show/hide lyrics panel (owned by the caller UI).
     * @param onToggleQueue    Lambda to show/hide queue panel.
     * @param onOpenSearch     Lambda to open search screen.
     * @param onOpenSettings   Lambda to open settings screen.
     * @param onShowDetails    Lambda to open track detail sheet.
     * @param onAddToPlaylist  Lambda to open "add to playlist" sheet.
     * @param onOpenSleepTimer Lambda to open sleep timer dialog.
     * @param onAiExplain      Lambda to trigger AI "explain this song".
     */
    fun executeAction(
        action: GestureAction,
        player: PlayerConnection,
        scope: CoroutineScope,
        onToggleLyrics: (() -> Unit)? = null,
        onToggleQueue: (() -> Unit)? = null,
        onOpenSearch: (() -> Unit)? = null,
        onOpenSettings: (() -> Unit)? = null,
        onShowDetails: (() -> Unit)? = null,
        onAddToPlaylist: (() -> Unit)? = null,
        onOpenSleepTimer: (() -> Unit)? = null,
        onAiExplain: (() -> Unit)? = null,
    ) {
        when (action) {
            GestureAction.NONE            -> Unit

            GestureAction.NEXT            -> player.seekToNext()
            GestureAction.PREVIOUS        -> player.seekToPrevious()
            GestureAction.PLAY_PAUSE      -> player.togglePlayPause()

            GestureAction.SEEK_FORWARD    -> {
                val pos = player.position.value
                player.seekTo((pos + 10_000L).coerceAtMost(player.duration.value.coerceAtLeast(0L)))
            }
            GestureAction.SEEK_BACKWARD   -> {
                val pos = player.position.value
                player.seekTo((pos - 10_000L).coerceAtLeast(0L))
            }

            GestureAction.VOLUME_UP       -> {
                val audio = player.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
            }
            GestureAction.VOLUME_DOWN     -> {
                val audio = player.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
            }

            GestureAction.LIKE_SONG       -> scope.launch { player.toggleLike() }

            GestureAction.TOGGLE_LYRICS   -> onToggleLyrics?.invoke()
            GestureAction.TOGGLE_QUEUE    -> onToggleQueue?.invoke()
            GestureAction.OPEN_SETTINGS   -> onOpenSettings?.invoke()
            GestureAction.SEARCH          -> onOpenSearch?.invoke()
            GestureAction.SHOW_DETAILS    -> onShowDetails?.invoke()
            GestureAction.ADD_TO_PLAYLIST -> onAddToPlaylist?.invoke()
            GestureAction.SLEEP_TIMER     -> onOpenSleepTimer?.invoke()
            GestureAction.AI_EXPLAIN      -> onAiExplain?.invoke()

            GestureAction.REPEAT_MODE     -> {
                val next = when (player.repeatMode.value) {
                    androidx.media3.common.Player.REPEAT_MODE_OFF  -> androidx.media3.common.Player.REPEAT_MODE_ALL
                    androidx.media3.common.Player.REPEAT_MODE_ALL  -> androidx.media3.common.Player.REPEAT_MODE_ONE
                    else                                            -> androidx.media3.common.Player.REPEAT_MODE_OFF
                }
                player.setRepeatMode(next)
            }
            GestureAction.SHUFFLE_MODE    -> {
                player.setShuffleModeEnabled(!player.shuffleModeEnabled.value)
            }
            GestureAction.ROOMS_LISTEN_TOGETHER -> onToggleQueue?.invoke()
            GestureAction.TRANSLATE_LYRICS -> onToggleLyrics?.invoke()
        }
    }
}

