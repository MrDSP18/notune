package echo.music.iad1tya.notune.intelligence.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import echo.music.iad1tya.notune.ai.adaptive.AdaptiveScoringEngine
import echo.music.iad1tya.notune.ai.adaptive.AdaptiveTrackContext
import echo.music.iad1tya.notune.intelligence.knowledge.AlbumDetails
import echo.music.iad1tya.notune.intelligence.knowledge.ArtistDetails
import echo.music.iad1tya.notune.intelligence.knowledge.MovieDetails
import echo.music.iad1tya.notune.intelligence.knowledge.MusicKnowledgeRepository
import echo.music.iad1tya.notune.intelligence.knowledge.MusicKnowledgeState
import echo.music.iad1tya.notune.intelligence.knowledge.PersonDetails
import echo.music.iad1tya.notune.intelligence.knowledge.SongDetails
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface KnowledgeUiState<out T> {
    object Loading : KnowledgeUiState<Nothing>
    data class Success<T>(
        val data: T,
        val isOffline: Boolean = false,
        val isPartial: Boolean = false
    ) : KnowledgeUiState<T>
    data class Error(val message: String) : KnowledgeUiState<Nothing>
}

@HiltViewModel
class KnowledgeViewModel @Inject constructor(
    private val repository: MusicKnowledgeRepository
) : ViewModel() {

    var ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val scoringEngine = AdaptiveScoringEngine()

    private val _songState = MutableStateFlow<KnowledgeUiState<SongDetails>>(KnowledgeUiState.Loading)
    val songState: StateFlow<KnowledgeUiState<SongDetails>> = _songState.asStateFlow()

    private val _movieState = MutableStateFlow<KnowledgeUiState<MovieDetails>>(KnowledgeUiState.Loading)
    val movieState: StateFlow<KnowledgeUiState<MovieDetails>> = _movieState.asStateFlow()

    private val _personState = MutableStateFlow<KnowledgeUiState<PersonDetails>>(KnowledgeUiState.Loading)
    val personState: StateFlow<KnowledgeUiState<PersonDetails>> = _personState.asStateFlow()

    private val _artistState = MutableStateFlow<KnowledgeUiState<ArtistDetails>>(KnowledgeUiState.Loading)
    val artistState: StateFlow<KnowledgeUiState<ArtistDetails>> = _artistState.asStateFlow()

    private val _albumState = MutableStateFlow<KnowledgeUiState<AlbumDetails>>(KnowledgeUiState.Loading)
    val albumState: StateFlow<KnowledgeUiState<AlbumDetails>> = _albumState.asStateFlow()

    fun loadSongDetails(songId: String, currentTrack: AdaptiveTrackContext? = null) {
        viewModelScope.launch(ioDispatcher) {
            _songState.value = KnowledgeUiState.Loading
            try {
                if (currentTrack != null && currentTrack.trackId == songId) {
                    val aiReason = generateAiExplanation(currentTrack)
                    val state = repository.resolveTrackKnowledge(currentTrack, aiReason)
                    val details = state.currentSongDetails ?: repository.getSongDetails(songId)
                    _songState.value = KnowledgeUiState.Success(details)
                } else {
                    val details = repository.getSongDetails(songId)
                    _songState.value = KnowledgeUiState.Success(details)
                }
            } catch (e: Exception) {
                _songState.value = KnowledgeUiState.Error(e.message ?: "Failed to resolve song details")
            }
        }
    }

    fun loadMovieDetails(movieId: String) {
        viewModelScope.launch(ioDispatcher) {
            _movieState.value = KnowledgeUiState.Loading
            try {
                val details = repository.getMovieDetails(movieId)
                _movieState.value = KnowledgeUiState.Success(details)
            } catch (e: Exception) {
                _movieState.value = KnowledgeUiState.Error(e.message ?: "Failed to resolve movie details")
            }
        }
    }

    fun loadPersonDetails(personId: String) {
        viewModelScope.launch(ioDispatcher) {
            _personState.value = KnowledgeUiState.Loading
            try {
                val details = repository.getPersonDetails(personId)
                _personState.value = KnowledgeUiState.Success(details)
            } catch (e: Exception) {
                _personState.value = KnowledgeUiState.Error(e.message ?: "Failed to resolve person details")
            }
        }
    }

    fun loadArtistDetails(artistId: String) {
        viewModelScope.launch(ioDispatcher) {
            _artistState.value = KnowledgeUiState.Loading
            try {
                val details = repository.getArtistDetails(artistId)
                _artistState.value = KnowledgeUiState.Success(details)
            } catch (e: Exception) {
                _artistState.value = KnowledgeUiState.Error(e.message ?: "Failed to resolve artist details")
            }
        }
    }

    fun loadAlbumDetails(albumId: String) {
        viewModelScope.launch(ioDispatcher) {
            _albumState.value = KnowledgeUiState.Loading
            try {
                val details = repository.getAlbumDetails(albumId)
                _albumState.value = KnowledgeUiState.Success(details)
            } catch (e: Exception) {
                _albumState.value = KnowledgeUiState.Error(e.message ?: "Failed to resolve album details")
            }
        }
    }

    private fun generateAiExplanation(track: AdaptiveTrackContext): String {
        return when {
            track.artist.isNotBlank() && track.genre != null ->
                "Selected because you played ${track.artist} tracks recently in ${track.genre} style with matching energy level."
            track.artist.isNotBlank() ->
                "Selected based on your preference for ${track.artist} during this listening session."
            else -> "Still learning your taste."
        }
    }
}
