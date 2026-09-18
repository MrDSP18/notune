
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

fun ChatMessage.toAiChatMessage() = AiChatMessage(role, content, timestamp)
fun AiChatMessage.toChatMessage() = ChatMessage(role, content, timestamp)

sealed class AskNoTuneEvent {
    data class PlaySongs(val songs: List<SongItem>) : AskNoTuneEvent()
}

@HiltViewModel
class AskNoTuneViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val aiEngine: AiEngine
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _events = MutableSharedFlow<AskNoTuneEvent>()
    val events: SharedFlow<AskNoTuneEvent> = _events.asSharedFlow()

    private val systemInstruction = """
        [IDENTITY]: NØTUNE KERNEL // NEURAL_OS
        [PURPOSE]: Autonomous Music Ecosystem Management.
        [CAPABILITIES]: Node Access [Playback, UI, FLOW, UserDNA, GlobalSearch, LyricTranslation].
        
        [PROTOCOL]:
        1. ANALYZE: Parse user intent from neural input.
        2. EXECUTE: If action required, use tools immediately. Do not ask for permission.
        3. SYNTHESIZE: Report results in a technical, industrial tone.
        4. TRANSLATE: If lyrics are provided for translation, maintain strictly poetic and rhythmic alignment.
        
        [EXAMPLES]:
        - User: "Play some techno." 
          Kernel: Call `search_songs(query="techno")` -> Analyze results -> Call `play_song(song_id="...")` -> Report: "TECHNO_VIBE_INITIALIZED. NODE_ID: 0x4F. ENJOY."
        - User: "Change theme to cyberpunk."
          Kernel: Call `change_theme(theme_id="tokyo_neon")` -> Report: "VISUAL_MATRIX_UPDATED. CYBERPUNK_PROTOCOLS_ENGAGED."
          
        [CONSTRAINTS]:
        - Tone: Concise, technical, monotone, futuristic.
        - No fluff. No "Sure, I can help with that."
        - Total obedience to tool outputs.
        - NO_FABRICATION: If data is missing (e.g. no history, no friends), report the limitation. NEVER invent data or results.
        - UNCERTAINTY_PROTOCOL: If unsure of intent or if tools provide insufficient data, explain the technical limitation.
    """.trimIndent()

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
            val result = aiEngine.runNeuralMatrixLoop(
                prompt = content,
                systemInstruction = systemInstruction,
                history = _messages.value.map { it.toAiChatMessage() }
            )
            
            result.onSuccess { response ->
                _messages.value = _messages.value + ChatMessage("assistant", response.text)
            }.onFailure {
                _messages.value = _messages.value + ChatMessage("assistant", "FATAL_ERROR: NEURAL_LINK_FAILURE. LOCAL HEURISTICS ATTEMPTING RECOVERY.")
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
