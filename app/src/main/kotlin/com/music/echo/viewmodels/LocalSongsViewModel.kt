package echo.music.iad1tya.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.localmedia.LocalSongScanSummary
import echo.music.iad1tya.models.LocalDuplicateGroup
import echo.music.iad1tya.models.LocalFolderNode
import echo.music.iad1tya.models.LocalSmartPlaylistType
import echo.music.iad1tya.repository.LocalMediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalSongsViewModel @Inject constructor(
    private val repository: LocalMediaRepository,
) : ViewModel() {

    private val _scanState = MutableStateFlow(LocalSongsScanState())
    val scanState = _scanState.asStateFlow()

    private val _folders = MutableStateFlow<List<LocalFolderNode>>(emptyList())
    val folders = _folders.asStateFlow()

    private val _recentlyAdded = MutableStateFlow<List<Song>>(emptyList())
    val recentlyAdded = _recentlyAdded.asStateFlow()

    private val _mostPlayed = MutableStateFlow<List<Song>>(emptyList())
    val mostPlayed = _mostPlayed.asStateFlow()

    private val _favorites = MutableStateFlow<List<Song>>(emptyList())
    val favorites = _favorites.asStateFlow()

    private val _recommendations = MutableStateFlow<List<Song>>(emptyList())
    val recommendations = _recommendations.asStateFlow()

    private val _duplicates = MutableStateFlow<List<LocalDuplicateGroup>>(emptyList())
    val duplicates = _duplicates.asStateFlow()

    init {
        scanDevice()
    }

    val songs = repository.getSongs().map { localSongs ->
        localSongs.map { local ->
            val entity = echo.music.iad1tya.db.entities.SongEntity(
                id = local.id,
                title = local.title,
                duration = local.durationSeconds,
                thumbnailUrl = local.thumbnailUrl,
                albumName = local.albumTitle,
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
                    _scanState.value = LocalSongsScanState(isScanning = false, errorMessage = null)
                    refreshLocalInsights()
                }
                .onFailure { error ->
                    _scanState.value = _scanState.value.copy(isScanning = false, errorMessage = error.message)
                }
        }
    }

    fun scanSafFolder(treeUri: Uri) {
        viewModelScope.launch {
            _scanState.value = _scanState.value.copy(isScanning = true, errorMessage = null)
            repository.scanSafFolder(treeUri)
                .onSuccess { addedCount ->
                    _scanState.value = LocalSongsScanState(isScanning = false, errorMessage = null)
                    refreshLocalInsights()
                }
                .onFailure { error ->
                    _scanState.value = _scanState.value.copy(isScanning = false, errorMessage = error.message)
                }
        }
    }

    fun refreshLocalInsights() {
        viewModelScope.launch {
            _folders.value = repository.getFolders()
            _recentlyAdded.value = repository.getSmartPlaylist(LocalSmartPlaylistType.RECENTLY_ADDED)
            _mostPlayed.value = repository.getSmartPlaylist(LocalSmartPlaylistType.MOST_PLAYED)
            _favorites.value = repository.getSmartPlaylist(LocalSmartPlaylistType.FAVORITES)
            _recommendations.value = repository.getRecommendations()
            _duplicates.value = repository.getDuplicates()
        }
    }

    fun updateMetadata(songId: String, title: String, artist: String, album: String?) {
        viewModelScope.launch {
            repository.updateSongMetadata(songId, title, artist, album)
            refreshLocalInsights()
        }
    }
}

data class LocalSongsScanState(
    val isScanning: Boolean = false,
    val lastSummary: LocalSongScanSummary? = null,
    val errorMessage: String? = null,
)
