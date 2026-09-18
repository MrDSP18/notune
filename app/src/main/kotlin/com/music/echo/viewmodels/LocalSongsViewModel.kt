

package echo.music.iad1tya.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import echo.music.iad1tya.localmedia.LocalSongScanConfig
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.localmedia.LocalSongScanSummary
import echo.music.iad1tya.localmedia.LocalSongScanner
import echo.music.iad1tya.utils.reportException
import javax.inject.Inject

@HiltViewModel
class LocalSongsViewModel @Inject constructor(
    private val repository: echo.music.iad1tya.repository.LocalMediaRepository,
) : ViewModel() {
    private val _scanState = MutableStateFlow(LocalSongsScanState())
    val scanState = _scanState.asStateFlow()

    val songs = repository.getSongs().map { localSongs ->
        localSongs.map { local ->
            // Try to find in DB if it's already there (to get proper Song entity with artists etc)
            // For now, we construct a Song entity from local data
            val entity = echo.music.iad1tya.db.entities.SongEntity(
                id = local.id,
                title = local.title,
                duration = local.durationSeconds,
                thumbnailUrl = local.thumbnailUrl,
                albumId = local.albumTitle, // Rough mapping
                isLocal = true
            )
            echo.music.iad1tya.db.entities.Song(
                song = entity,
                artists = local.artists.map { echo.music.iad1tya.db.entities.ArtistEntity(id = "LOCAL_$it", name = it) },
                album = null
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList(),
    )

    fun scanDevice() {
        if (_scanState.value.isScanning) return
        viewModelScope.launch {
            _scanState.value = _scanState.value.copy(isScanning = true, errorMessage = null)
            repository.startScan()
                .onSuccess {
                    _scanState.value = LocalSongsScanState(
                        isScanning = false,
                        errorMessage = null,
                    )
                }
                .onFailure { error ->
                    _scanState.value = _scanState.value.copy(
                        isScanning = false,
                        errorMessage = error.message,
                    )
                }
        }
    }
}

data class LocalSongsScanState(
    val isScanning: Boolean = false,
    val lastSummary: LocalSongScanSummary? = null,
    val errorMessage: String? = null,
)