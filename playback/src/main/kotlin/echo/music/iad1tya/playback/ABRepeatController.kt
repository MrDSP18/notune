package echo.music.iad1tya.playback

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class ABRepeatState(
    val pointA: Long? = null,
    val pointB: Long? = null,
    val isEnabled: Boolean = false
) {
    val isLoopReady: Boolean
        get() = isEnabled && pointA != null && pointB != null && pointB > pointA
}

@Singleton
class ABRepeatController @Inject constructor() {

    private val _state = MutableStateFlow(ABRepeatState())
    val state: StateFlow<ABRepeatState> = _state.asStateFlow()

    fun setPointA(positionMs: Long) {
        val current = _state.value
        val clampedA = positionMs.coerceAtLeast(0L)
        val validB = if (current.pointB != null && current.pointB <= clampedA) null else current.pointB
        _state.value = current.copy(
            pointA = clampedA,
            pointB = validB,
            isEnabled = validB != null
        )
    }

    fun setPointB(positionMs: Long) {
        val current = _state.value
        val clampedB = positionMs.coerceAtLeast(0L)
        if (current.pointA != null && clampedB > current.pointA) {
            _state.value = current.copy(
                pointB = clampedB,
                isEnabled = true
            )
        }
    }

    fun toggle() {
        val current = _state.value
        if (current.pointA != null && current.pointB != null && current.pointB > current.pointA) {
            _state.value = current.copy(isEnabled = !current.isEnabled)
        }
    }

    fun clear() {
        _state.value = ABRepeatState()
    }

    /**
     * Checks if current playback position has reached Point B.
     * If so, triggers seekTo back to Point A.
     */
    fun checkAndLoop(currentPositionMs: Long, seekTo: (Long) -> Unit): Boolean {
        val current = _state.value
        if (current.isLoopReady) {
            val a = current.pointA!!
            val b = current.pointB!!
            if (currentPositionMs >= b) {
                seekTo(a)
                return true
            }
        }
        return false
    }
}
