

package echo.music.iad1tya.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.models.filterExplicit
import com.music.innertube.models.filterVideoSongs
import com.music.innertube.models.filterYoutubeShorts
import com.music.innertube.pages.BrowseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.HideExplicitKey
import echo.music.iad1tya.constants.HideVideoSongsKey
import echo.music.iad1tya.constants.HideYoutubeShortsKey
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.reportException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YouTubeBrowseViewModel
@Inject
constructor(
    @ApplicationContext val context: Context,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val browseId = savedStateHandle.get<String>("browseId")!!
    private val params = savedStateHandle.get<String>("params")

    val result = MutableStateFlow<BrowseResult?>(null)

    init {
        viewModelScope.launch {
            val prefs = context.dataStore.data.first()
            val hideExplicit = prefs[HideExplicitKey] ?: false
            val hideVideoSongs = prefs[HideVideoSongsKey] ?: false
            val hideYoutubeShorts = prefs[HideYoutubeShortsKey] ?: false
            YouTube
                .browse(browseId, params)
                .onSuccess {
                    result.value = it
                        .filterExplicit(hideExplicit)
                        .filterVideoSongs(hideVideoSongs)
                        .filterYoutubeShorts(hideYoutubeShorts)
                }.onFailure {
                    reportException(it)
                }
        }
    }
}
