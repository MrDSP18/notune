package com.music.echo.notune.gesture

/**
 * Rich set of domain actions invokable by [GestureEngine].
 */
sealed class GestureAction {
    object None : GestureAction()

    // Playback
    object NextTrack : GestureAction()
    object PreviousTrack : GestureAction()
    object TogglePlayPause : GestureAction()
    data class SeekBy(val deltaMs: Long) : GestureAction()
    object VolumeUp : GestureAction()
    object VolumeDown : GestureAction()
    data class ChangeSpeed(val delta: Float) : GestureAction()

    // Layout
    object MinimizePlayer : GestureAction()
    object ExpandPlayer : GestureAction()
    object CompactPlayer : GestureAction()
    object FullPlayer : GestureAction()

    // Favorites / Library
    object ToggleFavorite : GestureAction()
    object FavoriteWithAnimation : GestureAction()
    object OpenTrackActions : GestureAction()

    // Queue Actions
    object ToggleQueue : GestureAction()
    object PlayNext : GestureAction()
    object RemoveFromQueue : GestureAction()
    object MoveToQueueTop : GestureAction()
    object MoveToQueueBottom : GestureAction()
    object ShuffleQueue : GestureAction()

    // AI & Intelligence
    object OpenAi : GestureAction()
    object ActivateAiDj : GestureAction()
    object AiDiscovery : GestureAction()
    object AiFlow : GestureAction()
    object AiFindGems : GestureAction()
    object AiExplain : GestureAction()
    data class AskNotune(val query: String) : GestureAction()

    // Lyrics
    object ToggleLyrics : GestureAction()
    object LyricsKaraoke : GestureAction()
    object LyricsTranslate : GestureAction()
    data class LyricsSeekToLine(val lineNumber: Int) : GestureAction()

    // EQ Adjustments
    data class AdjustBass(val deltaDb: Float) : GestureAction()
    data class AdjustTreble(val deltaDb: Float) : GestureAction()
    data class AdjustEqIntensity(val deltaRatio: Float) : GestureAction()

    // Sleep Timer & Utility
    object SleepTimer : GestureAction()
    object OpenSettings : GestureAction()
    object OpenSearch : GestureAction()

    // Social & Rooms
    object RoomVoteUp : GestureAction()
    object RoomVoteDown : GestureAction()
    object ToggleRoomSync : GestureAction()

    // Couple Mode
    object CoupleSendSong : GestureAction()
    object CoupleAddSharedPlaylist : GestureAction()

    // Party Mode
    object PartyBoost : GestureAction()

    // Macros & Custom Tool Execution
    data class ExecuteMacro(val macroId: String) : GestureAction()
    data class ExecuteAiTool(val toolId: String, val params: Map<String, Any> = emptyMap()) : GestureAction()
}
