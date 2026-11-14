
package echo.music.iad1tya.notune

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.AskNoTuneHistoryKey
import echo.music.iad1tya.notune.ai.*
import echo.music.iad1tya.notune.ai.engine.NoCoreOrchestrator
import echo.music.iad1tya.notune.ai.tools.AiToolManager
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import com.music.innertube.models.SongItem

@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

fun ChatMessage.toAiChatMessage() = AiChatMessage(role, content, timestamp)
fun AiChatMessage.toChatMessage() = ChatMessage(role, content, timestamp)

sealed class AskNoTuneEvent {
    data class PlaySongs(val songs: List<SongItem>) : AskNoTuneEvent()
}

@HiltViewModel
class AskNoTuneViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val orchestrator: NoCoreOrchestrator,
    private val aiToolManager: AiToolManager
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _events = MutableSharedFlow<AskNoTuneEvent>()
    val events: SharedFlow<AskNoTuneEvent> = _events.asSharedFlow()

    init {
        loadHistory()
        if (_messages.value.isEmpty()) {
            _messages.value = listOf(ChatMessage("assistant", "NØTUNE KERNEL ONLINE. NEURAL MATRIX STABILIZED. READY FOR COMMANDS."))
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val historyJson = context.dataStore.get(AskNoTuneHistoryKey, "[]")
            try {
                val list = Json.decodeFromString<List<ChatMessage>>(historyJson)
                if (list.isNotEmpty()) _messages.value = list
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    private fun saveHistory() {
        viewModelScope.launch {
            val historyJson = Json.encodeToString(_messages.value.takeLast(50))
            context.dataStore.edit { it[AskNoTuneHistoryKey] = historyJson }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        val userMessage = ChatMessage("user", content)
        _messages.value = _messages.value + userMessage
        
        _isTyping.value = true
        
        viewModelScope.launch {
            try {
                val result = orchestrator.processUserRequest(
                    prompt = content,
                    history = _messages.value.map { it.toAiChatMessage() },
                    toolExecutor = { toolCall -> aiToolManager.executeTool(toolCall) }
                )

                result.onSuccess { response ->
                    _messages.value = _messages.value + ChatMessage("assistant", response.text)
                }.onFailure { error ->
                    _messages.value = _messages.value + ChatMessage(
                        "assistant",
                        "I couldn't complete that action: ${error.message ?: "AI execution failed"}"
                    )
                }
            } catch (error: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    "assistant",
                    "I couldn't complete that action: ${error.message ?: "Unexpected error"}"
                )
            } finally {
                _isTyping.value = false
                saveHistory()
            }
        }
    }
    
    fun clearHistory() {
        _messages.value = listOf(ChatMessage("assistant", "HISTORY CLEARED. I AM READY FOR NEW REQUESTS."))
        saveHistory()
    }
}
