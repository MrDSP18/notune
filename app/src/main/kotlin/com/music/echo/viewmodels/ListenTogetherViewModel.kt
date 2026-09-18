package echo.music.iad1tya.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.music.iad1tya.models.RoomType
import echo.music.iad1tya.models.Room
import echo.music.iad1tya.repository.RoomRepository
import echo.music.iad1tya.listentogether.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListenTogetherViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {

    val connectionState = roomRepository.connectionState
    val currentRoom = roomRepository.currentRoom
    val syncState = roomRepository.syncState
    
    val roomState: StateFlow<Room?> = roomRepository.currentRoom
    val role: StateFlow<RoomRole> = MutableStateFlow(RoomRole.NONE)
    val pendingJoinRequests: StateFlow<List<JoinRequestPayload>> = MutableStateFlow(emptyList())
    val logs: StateFlow<List<LogEntry>> = MutableStateFlow(emptyList())
    val blockedUsernames: StateFlow<Set<String>> = MutableStateFlow(emptySet())
    val events: SharedFlow<ListenTogetherEvent> = MutableSharedFlow<ListenTogetherEvent>().asSharedFlow()

    fun createRoom(name: String) {
        viewModelScope.launch {
            roomRepository.createRoom(name, RoomType.PUBLIC)
        }
    }

    fun joinRoom(roomCode: String) {
        viewModelScope.launch {
            roomRepository.joinRoom(roomCode)
        }
    }
    
    fun joinRoom(roomCode: String, username: String) {
        viewModelScope.launch {
            roomRepository.joinRoom(roomCode)
        }
    }

    fun leaveRoom() {
        viewModelScope.launch {
            roomRepository.leaveRoom()
        }
    }

    fun play() {
        viewModelScope.launch { roomRepository.play() }
    }

    fun pause() {
        viewModelScope.launch { roomRepository.pause() }
    }

    fun seekTo(positionMs: Long) {
        viewModelScope.launch { roomRepository.seekTo(positionMs) }
    }

    fun next() {
        viewModelScope.launch { roomRepository.next() }
    }

    fun previous() {
        viewModelScope.launch { roomRepository.previous() }
    }

    fun sendChatMessage(message: String) {
        viewModelScope.launch { roomRepository.sendChatMessage(message) }
    }
    
    fun clearLogs() {}
    fun unblockUser(username: String) {}
}
