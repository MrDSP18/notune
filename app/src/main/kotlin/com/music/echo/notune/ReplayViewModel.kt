
package echo.music.iad1tya.notune

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ReplayViewModel @Inject constructor(
    private val database: MusicDatabase
) : ViewModel() {
    private val _topSongs = MutableStateFlow<List<Song>>(emptyList())
    val topSongs: StateFlow<List<Song>> = _topSongs

    init {
        loadReplay()
    }

    private fun loadReplay() {
        viewModelScope.launch {
            // Fetch top songs for the current month
            val now = LocalDateTime.now()
            val startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0)
            // Note: DB queries in core might not have monthly filtering yet, 
            // so we'll use topSongs overall for now but label it as Replay
            _topSongs.value = database.topSongs(20).first()
        }
    }
}
