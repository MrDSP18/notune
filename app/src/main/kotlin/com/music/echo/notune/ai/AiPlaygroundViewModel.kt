
package echo.music.iad1tya.notune.ai

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AiPlaygroundViewModel @Inject constructor(
    val aiEngine: AiEngine
) : ViewModel()
