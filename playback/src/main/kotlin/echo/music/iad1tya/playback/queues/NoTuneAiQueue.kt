
package echo.music.iad1tya.playback.queues

import androidx.media3.common.MediaItem
import com.music.innertube.models.SongItem
import echo.music.iad1tya.notune.ai.AiEngine
import echo.music.iad1tya.utils.toMediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class NoTuneAiQueue(
    private val initialSongs: List<MediaItem>,
    private val aiEngine: AiEngine
) : Queue {
    private val _items = MutableStateFlow(initialSongs)
    override val items: StateFlow<List<MediaItem>> = _items.asStateFlow()
    
    override val title: String = "NØTUNE AI RADIO"

    override fun preload(index: Int) {
        // Logic to fetch more songs from AI when reaching the end
        if (index >= _items.value.size - 2) {
             // We can't easily launch a coroutine here without a scope
             // Usually the MusicService handles preloading
        }
    }
}
