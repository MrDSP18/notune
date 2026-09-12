
package echo.music.iad1tya.notune

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.AskNoTuneHistoryKey
import echo.music.iad1tya.notune.ai.*
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

sealed class AskNoTuneEvent {
    data class PlaySongs(val songs: List<SongItem>) : AskNoTuneEvent()
}

@HiltViewModel
class AskNoTuneViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val aiEngine: AiEngine,
    private val toolManager: AiToolManager
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _events = MutableSharedFlow<AskNoTuneEvent>()
    val events: SharedFlow<AskNoTuneEvent> = _events.asSharedFlow()

    private val systemInstruction = """
        You are NØTUNE, a futuristic and intelligent music assistant for an Android music player.
        You have access to music tools. Use them to help the user.
        If a user asks to play something, use search_songs first if you don't have IDs, then use play_song or create_playlist.
        Be concise, futuristic, and helpful. 
        Your identity is NØTUNE.
    """.trimIndent()

    init {
        loadHistory()
        if (_messages.value.isEmpty()) {
            _messages.value = listOf(ChatMessage("assistant", "HELLO. I AM NØTUNE. HOW CAN I HELP YOUR MUSICAL JOURNEY TODAY?"))
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
            val isExperimental = context.dataStore.get(echo.music.iad1tya.constants.ExperimentalAiFeaturesKey, false)
            val tools = if (isExperimental) aiEngine.getMusicTools() else null

            val result = aiEngine.generateResponse(
                prompt = content,
                tools = tools,
                systemInstruction = systemInstruction
            )
            
            result.onSuccess { response ->
                if (response.toolCalls != null) {
                    var toolResultSummary = ""
                    for (toolCall in response.toolCalls) {
                        val toolResult = toolManager.executeTool(toolCall)
                        toolResultSummary += "\n[Tool: ${toolCall.functionName}] $toolResult"
                    }
                    
                    val finalContent = if (response.text.isBlank()) toolResultSummary.trim() else response.text
                    _messages.value = _messages.value + ChatMessage("assistant", finalContent)
                } else {
                    _messages.value = _messages.value + ChatMessage("assistant", response.text)
                }
            }.onFailure {
                _messages.value = _messages.value + ChatMessage("assistant", "I encountered a communication error with my core intelligence. Please check your API configuration.")
            }
            
            _isTyping.value = false
            saveHistory()
        }
    }
    
    fun clearHistory() {
        _messages.value = listOf(ChatMessage("assistant", "HISTORY CLEARED. I AM READY FOR NEW REQUESTS."))
        saveHistory()
    }
}
