
package echo.music.iad1tya.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.ai.AiRecommendationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiRecommendationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val helper: AiRecommendationHelper
) : ViewModel() {
    private val _log = MutableStateFlow("Initializing...")
    val log = _log.asStateFlow()

    private val _isGenerating = MutableStateFlow(true)
    val isGenerating = _isGenerating.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isComplete = MutableStateFlow(false)
    val isComplete = _isComplete.asStateFlow()

    fun startGeneration() {
        viewModelScope.launch {
            try {
                helper.generateRecommendations(
                    context = context,
                    onLog = { msg ->
                        _log.value = msg
                    }
                )
                _isComplete.value = true
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isGenerating.value = false
            }
        }
    }
}
