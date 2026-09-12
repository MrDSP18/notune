
package echo.music.iad1tya.notune

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

/** Time window options for the Replay screen. */
enum class ReplayPeriod(val label: String) {
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month"),
    THIS_YEAR("This Year"),
    ALL_TIME("All Time"),
}

@HiltViewModel
class ReplayViewModel @Inject constructor(
    private val database: MusicDatabase
) : ViewModel() {

    private val _period = MutableStateFlow(ReplayPeriod.THIS_MONTH)
    val period: StateFlow<ReplayPeriod> = _period

    private val _topSongs = MutableStateFlow<List<Song>>(emptyList())
    val topSongs: StateFlow<List<Song>> = _topSongs

    private val _totalPlayTimeMs = MutableStateFlow(0L)
    val totalPlayTimeMs: StateFlow<Long> = _totalPlayTimeMs

    private val _uniqueTrackCount = MutableStateFlow(0)
    val uniqueTrackCount: StateFlow<Int> = _uniqueTrackCount

    init {
        _period.onEach { loadReplay(it) }.launchIn(viewModelScope)
    }

    fun setPeriod(newPeriod: ReplayPeriod) {
        _period.value = newPeriod
    }

    private fun loadReplay(period: ReplayPeriod) {
        val now = System.currentTimeMillis()
        val fromMs = when (period) {
            ReplayPeriod.THIS_WEEK  -> LocalDateTime.now().minusWeeks(1)
            ReplayPeriod.THIS_MONTH -> LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
            ReplayPeriod.LAST_MONTH -> LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
            ReplayPeriod.THIS_YEAR  -> LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0)
            ReplayPeriod.ALL_TIME   -> LocalDateTime.of(2000, 1, 1, 0, 0)
        }.toInstant(ZoneOffset.UTC).toEpochMilli()

        val toMs = when (period) {
            ReplayPeriod.LAST_MONTH -> LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
                .toInstant(ZoneOffset.UTC).toEpochMilli()
            else -> now
        }

        // Real time-filtered top songs using the event table
        combine(
            database.mostPlayedSongs(fromMs, 20, 0, toMs),
            database.getTotalPlayTimeInRange(fromMs, toMs),
            database.getUniqueSongCountInRange(fromMs, toMs),
        ) { songs, totalTime, uniqueCount ->
            _topSongs.value = songs
            _totalPlayTimeMs.value = totalTime ?: 0L
            _uniqueTrackCount.value = uniqueCount
        }.launchIn(viewModelScope)
    }
}
