package echo.music.iad1tya.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.music.iad1tya.models.PlaybackState
import echo.music.iad1tya.repository.PlaybackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val playbackRepository: PlaybackRepository
) : ViewModel() {
    val playbackState: StateFlow<PlaybackState> = playbackRepository.playbackState
}
